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

package org.wso2.ei.module.sap.producer;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.jvm.values.ObjectValue;
import org.ballerinalang.jvm.values.XMLValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.module.sap.utils.BallerinaSapException;
import org.wso2.ei.module.sap.utils.SapConstants;

import javax.xml.stream.XMLStreamException;

import static org.wso2.ei.module.sap.producer.RFCMetaDataParser.processMetaDataDocument;
import static org.wso2.ei.module.sap.utils.SapConstants.NATIVE_PRODUCER;

public class SendBAPIMessage {

    private static Logger log = LoggerFactory.getLogger(SapConstants.BALLERINA);

    public static String sendBapiMessage(ObjectValue producer, XMLValue content1, Boolean transaction)
            throws BallerinaSapException {

        log.info("Sending the BAPI request.");
        MapValue producerStruct = (MapValue) producer.getNativeData(NATIVE_PRODUCER);
        String content = String.valueOf(content1);
        boolean doTransaction = transaction;
        String functionName = null;
        JCoDestination destination = (JCoDestination) producerStruct.getNativeData(NATIVE_PRODUCER);
        String response;
        try {
            OMElement omData = AXIOMUtil.stringToOM(content);
            functionName = omData.getQName().toString();
            if (doTransaction) {
                JCoContext.begin(destination);
                // Get the BAPI/RFC function from the SAP repository
                JCoFunction function = TransactionHandler.getRFCfunction(destination, functionName);
                // Process the BAPI function
                processMetaDataDocument(omData, function);
                // Evaluate the BAPI/RFC function in a remote R/* system
                response = TransactionHandler.evaluateRFCfunction(function, destination);
                JCoFunction commitFunction = TransactionHandler.getRFCfunction(destination,
                        SapConstants.BAPI_COMMIT_NAME);
                TransactionHandler.evaluateRFCfunction(commitFunction, destination);
                log.info("Committed the transaction for function: " + functionName);
                return response;
            } else {
                // Get the BAPI/RFC function from the SAP repository
                JCoFunction function = TransactionHandler.getRFCfunction(destination, functionName);
                // Process the BAPI function
                processMetaDataDocument(omData, function);
                // Evaluate the BAPI/RFC function in a remote R/* system
                response = TransactionHandler.evaluateRFCfunction(function, destination);
                log.info("BAPI request is sent.");
                return response;
            }
        } catch (XMLStreamException e) {
            throw new BallerinaSapException("Error occurred when converting the XML data" +
                    " in as string to XML: ", e);
        } catch (Exception e) {
            if (doTransaction) {
                JCoFunction rollbackFunction = TransactionHandler.getRFCfunction(destination,
                        SapConstants.ROLLBACK_BAPI_NAME);
                response = TransactionHandler.evaluateRFCfunction(rollbackFunction, destination);
                log.info("Rollback transaction for function: " + functionName);
                return response;
            } else {
                return "Error occurred when sending the BAPI request: " + e;
            }
        } finally {
            if (doTransaction) {
                try {
                    JCoContext.end(destination);
                } catch (JCoException e) {
                    throw new BallerinaSapException("Error occurred when end the " +
                            "Jco context.", e);
                }
                log.info("End the transaction for function: " + functionName);
            }
        }
    }
}
