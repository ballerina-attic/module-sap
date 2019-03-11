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

package org.ballerinalang.sap.nativeimpl.consumer;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Executor;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.model.values.BError;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.sap.utils.SapUtils;
import org.ballerinalang.util.codegen.ProgramFile;

import java.util.HashMap;
import java.util.Map;

import static org.ballerinalang.sap.utils.SapConstants.CONSUMER_STRUCT_NAME;
import static org.ballerinalang.sap.utils.SapConstants.RESOURCE_ON_MESSAGE;
import static org.ballerinalang.sap.utils.SapConstants.SAP_MESSAGE_OBJECT;
import static org.ballerinalang.sap.utils.SapConstants.SAP_NATIVE_PACKAGE;

/**
 * This class handles BAPI calls returned from the SAP gateway.
 * <p>
 * This class encapsulates each bapi/rfc request received.
 * handling  and converting them to String
 * </p>
 */
public class RFCConnectionHandler implements JCoServerFunctionHandler {

    private static final Log log = LogFactory.getLog(RFCConnectionHandler.class);
    private ResponseCallback callback;
    private Map<String, Resource> sapService;

    public RFCConnectionHandler(Map<String, Resource> sapService) {

        this.sapService = sapService;
        callback = new ResponseCallback();
    }

    /**
     * handle bapi requests coming through SAP gateway
     * @param jCoServerContext JCO Server environment configuration
     * @param jCoFunction bAPI/rfc function being called
     */
    @Override
    public void handleRequest(JCoServerContext jCoServerContext, JCoFunction jCoFunction) {

        if (log.isDebugEnabled()) {
            log.debug("New BAPI function call received");
        }
        Map<String, Object> properties = new HashMap<>();
        Resource onMessageResource = sapService.get(RESOURCE_ON_MESSAGE);
        ProgramFile programFile = onMessageResource.getResourceInfo().getPackageInfo().getProgramFile();
        BMap<String, BValue> consumerStruct = BLangConnectorSPIUtil.createBStruct(programFile,
                SAP_NATIVE_PACKAGE, CONSUMER_STRUCT_NAME);
        Executor.submit(onMessageResource, callback, properties, null,
                getSignatureParameters(jCoFunction, consumerStruct));
    }

    private BMap<String, BValue> getSignatureParameters(JCoFunction jCoFunction,
                                                        BMap<String, BValue> consumerStruct) {

        try {
            log.debug("Starting a new BAPI process from the incoming request");
            jCoFunction.getExportParameterList().setValue("ECHOTEXT",
                    jCoFunction.getImportParameterList().getString("REQUTEXT"));
            log.info("Request text: " + jCoFunction.getImportParameterList().getString("REQUTEXT"));
            consumerStruct.addNativeData(SAP_MESSAGE_OBJECT, jCoFunction.getImportParameterList()
                    .getString("REQUTEXT"));
        } catch (Throwable e) {
            SapUtils.invokeOnError(sapService, callback, e.getMessage());
        }
        return consumerStruct;
    }

    private static class ResponseCallback implements CallableUnitCallback {

        @Override
        public void notifySuccess() {
            // do nothing
        }

        @Override
        public void notifyFailure(BError error) {
            // do nothing
        }
    }
}
