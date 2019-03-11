/*
 * Copyright (c) 2019, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http:www.apache.orglicensesLICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.sap.nativeimpl.producer;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.sap.utils.SapConstants;
import org.wso2.carbon.kernel.utils.StringUtils;

import javax.xml.stream.XMLStreamException;

import static org.ballerinalang.sap.nativeimpl.producer.RFCMetaDataParser.processMetaDataDocument;
import static org.ballerinalang.sap.utils.SapConstants.FULL_PACKAGE_NAME;
import static org.ballerinalang.sap.utils.SapConstants.NATIVE_PRODUCER;
import static org.ballerinalang.sap.utils.SapConstants.ORG_NAME;
import static org.ballerinalang.sap.utils.SapConstants.PRODUCER_STRUCT_NAME;
import static org.ballerinalang.sap.utils.SapConstants.SAP_NATIVE_PACKAGE;
import static org.ballerinalang.sap.utils.SapUtils.createError;

/**
 * {@code Send} send the BAPI message to the  SAP Instance.
 */
@BallerinaFunction(
    orgName = ORG_NAME,
    packageName = FULL_PACKAGE_NAME,
    functionName = "sendBapiMessage",
    receiver = @Receiver(type = TypeKind.OBJECT, structType = PRODUCER_STRUCT_NAME,
            structPackage = SAP_NATIVE_PACKAGE)
)
public class SendBAPIMessage extends BlockingNativeCallableUnit {
    private static Log log = LogFactory.getLog(SendBAPIMessage.class);

    @Override
    public void execute(Context context) {
        BMap<String, BValue> producerConnector = (BMap<String, BValue>) context.getRefArgument(0);
        BMap producerMap = (BMap) producerConnector.get("producerHolder");
        BMap<String, BValue> producerStruct = (BMap<String, BValue>) producerMap.get(new BString(NATIVE_PRODUCER));
        String content = String.valueOf(context.getRefArgument(1));
        boolean doLogon = context.getBooleanArgument(0);
        boolean doTransaction = context.getBooleanArgument(1);
        String productManufacturer = String.valueOf(context.getRefArgument(2));
        String productName = String.valueOf(context.getRefArgument(3));
        String ccmsInterface = String.valueOf(context.getRefArgument(4));
        String cccmsInterfaceVersion = String.valueOf(context.getRefArgument(4));

        JCoDestination destination = (JCoDestination) producerStruct.getNativeData(NATIVE_PRODUCER);
        String response = null;
        try {
            OMElement omData = AXIOMUtil.stringToOM(content);
            String functionName = omData.getQName().toString();
            if (doLogon) {
                if (!(StringUtils.isNullOrEmpty(productManufacturer) || StringUtils.isNullOrEmpty(productName) ||
                        StringUtils.isNullOrEmpty(ccmsInterface))) {
                    log.error("The logon's parameter/s has/have null value.");
                }
                log.info("Begin Transaction");
                JCoContext.begin(destination);
                TransactionHandler.logon(destination, productManufacturer, productName, ccmsInterface,
                        cccmsInterfaceVersion, context);
            }
            if (doTransaction) {
                log.info("Begin transaction.");
                JCoContext.begin(destination);
                JCoFunction function = TransactionHandler.getRFCfunction(destination, functionName, context);
                processMetaDataDocument(omData, function);
                response = TransactionHandler.evaluateRFCfunction(function, destination, context);
                JCoFunction commitFunction = TransactionHandler.getRFCfunction(destination,
                        SapConstants.BAPI_COMMIT_NAME, context);
                TransactionHandler.evaluateRFCfunction(commitFunction, destination, context);
                context.setReturnValues(new BString(response));
                log.info("Commit transaction.");
            } else {
                JCoFunction function = TransactionHandler.getRFCfunction(destination, functionName, context);
                RFCMetaDataParser.processMetaDataDocument(omData, function);
                response = TransactionHandler.evaluateRFCfunction(function, destination, context);
                context.setReturnValues(new BString(response));
            }
        } catch (XMLStreamException e) {
            context.setReturnValues(createError(context, "Error occurred when converting the XML data" +
                    " in as string to XML: " + e.toString()));
        } catch (Exception e) {
            if (doTransaction) {
                JCoFunction rollbackFunction = TransactionHandler.getRFCfunction(destination,
                        SapConstants.ROLLBACK_BAPI_NAME, context);
                TransactionHandler.evaluateRFCfunction(rollbackFunction, destination, context);
                log.info("Rollback transaction.");
            }
        } finally {
            if (doTransaction || doLogon) {
                try {
                    JCoContext.end(destination);
                } catch (JCoException e) {
                    context.setReturnValues(createError(context, "Error occurred when end the " +
                            "Jco context."));
                }
                log.info("End transaction.");
            }
        }
    }
}
