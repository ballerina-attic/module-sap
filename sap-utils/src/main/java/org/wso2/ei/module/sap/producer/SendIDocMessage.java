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

import com.sap.conn.idoc.IDocDocumentList;
import com.sap.conn.idoc.IDocFactory;
import com.sap.conn.idoc.IDocParseException;
import com.sap.conn.idoc.IDocRepository;
import com.sap.conn.idoc.IDocXMLProcessor;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.sap.utils.SapConstants;

import static org.ballerinalang.sap.utils.SapConstants.FULL_PACKAGE_NAME;
import static org.ballerinalang.sap.utils.SapConstants.NATIVE_PRODUCER;
import static org.ballerinalang.sap.utils.SapConstants.ORG_NAME;
import static org.ballerinalang.sap.utils.SapConstants.PRODUCER_STRUCT_NAME;
import static org.ballerinalang.sap.utils.SapConstants.SAP_NATIVE_PACKAGE;
import static org.ballerinalang.sap.utils.SapUtils.createError;

/**
 * {@code Send} Send the IDoc message to the  SAP instance.
 */
@BallerinaFunction(
    orgName = ORG_NAME,
    packageName = FULL_PACKAGE_NAME,
    functionName = "sendIdocMessage",
    receiver = @Receiver(type = TypeKind.OBJECT, structType = PRODUCER_STRUCT_NAME,
            structPackage = SAP_NATIVE_PACKAGE)
)
public class SendIDocMessage extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        String idocVersion = String.valueOf(context.getIntArgument(0));
        String content = String.valueOf(context.getRefArgument(1));
        BMap<String, BValue> producerConnector = (BMap<String, BValue>) context.getRefArgument(0);
        BMap producerMap = (BMap) producerConnector.get("producerHolder");
        BMap<String, BValue> producerStruct = (BMap<String, BValue>) producerMap.get(new BString(NATIVE_PRODUCER));
        JCoDestination destination = (JCoDestination) producerStruct.getNativeData(NATIVE_PRODUCER);
        char setIdocVersion;
        switch (idocVersion) {
            case SapConstants.SAP_IDOC_VERSION_2:
                setIdocVersion = IDocFactory.IDOC_VERSION_2;
                break;
            case SapConstants.SAP_IDOC_VERSION_3:
                setIdocVersion = IDocFactory.IDOC_VERSION_3;
                break;
            default:
                setIdocVersion = IDocFactory.IDOC_VERSION_DEFAULT;
                break;
        }
        try {
            IDocFactory iDocFactory = JCoIDoc.getIDocFactory();
            IDocXMLProcessor processor = iDocFactory.getIDocXMLProcessor();
            IDocRepository iDocRepository = JCoIDoc.getIDocRepository(destination);

            IDocDocumentList iDocList = processor.parse(iDocRepository, content);
            String tid = destination.createTID();
            JCoIDoc.send(iDocList, setIdocVersion, destination, tid);
            destination.confirmTID(tid);
            context.setReturnValues(new BString(tid));
        } catch (JCoException e) {
            context.setReturnValues(createError(context, "Failed to connect the SAP gateway : "
                    + e.toString()));
            createError(context, "Failed to connect the SAP gateway : "
                    + e.toString());
        } catch (IDocParseException e) {
            context.setReturnValues(createError(context, "Error occurred when converting the data in " +
                    "as string [" + content + " ]  " + "to IDocList." + e.toString()));
            createError(context, "Error occurred when converting the data in " +
                    "as string [" + content + " ]  " + "to IDocList." + e.toString());
        }
    }
}
