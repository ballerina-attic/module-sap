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

package org.wso2.ei.module.sap.consumer;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import org.ballerinalang.jvm.BRuntime;
import org.ballerinalang.jvm.types.AttachedFunction;
import org.ballerinalang.jvm.values.ErrorValue;
import org.ballerinalang.jvm.values.ObjectValue;
import org.ballerinalang.jvm.values.connector.CallableUnitCallback;
import org.ballerinalang.jvm.values.connector.Executor;
import org.ballerinalang.model.values.BError;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.module.sap.utils.SapUtils;

import java.util.Map;

import static org.wso2.ei.module.sap.utils.SapConstants.RESOURCE_ON_MESSAGE;

/**
 * This class handles the BAPI calls that are returned from the SAP gateway.
 * <p>
 * This class encapsulates each BAPI/RFC request received.
 * by handling  and converting them to Strings.
 * </p>
 */
public class BapiHandlerFactory implements JCoServerFunctionHandler {

    private static Logger log = LoggerFactory.getLogger("ballerina");

    private ResponseCallback callback;
    private Map<String, AttachedFunction> sapResource;
    private final BRuntime runtime;
//    private Context context;

    BapiHandlerFactory(Map<String, AttachedFunction> sapService) {

        this.sapResource = sapService;
        callback = new ResponseCallback();
//        this.context = context;
        runtime = BRuntime.getCurrentRuntime();
        log.info("......JCo server function handler is created.");
    }

    /**
     * Handle BAPI requests coming through the SAP gateway.
     *
     * @param jCoServerContext The jCoServerContext JCO Server environment configuration
     * @param jCoFunction      The jCoFunction BAPI/RFC function which is being called
     */
    public void handleRequest(JCoServerContext jCoServerContext, JCoFunction jCoFunction) {

        if (log.isDebugEnabled()) {
            log.debug("New BAPI function call received");
        }
        jCoFunction.getExportParameterList().setValue("ECHOTEXT", jCoFunction.getImportParameterList()
                .getString("REQUTEXT"));
        String output = jCoFunction.getImportParameterList().getString("REQUTEXT");
        log.info("handleRequest1");
//        SapUtils.invokeOnBapiMessage(output, sapResource, callback);
        try {
            log.info("invokeOnBapiMessage");
            AttachedFunction onMessageResource = sapResource.get(RESOURCE_ON_MESSAGE);
            String serviceName = onMessageResource.getName();
            runtime.invokeMethodAsync((ObjectValue) onMessageResource, serviceName, callback);
//            Executor.submit(null, (ObjectValue) onMessageResource, serviceName, callback, null,
//                    null, output, true);
        } catch (BallerinaException e) {
            SapUtils.invokeOnError(sapResource, callback, e.getMessage());
        }
    }

    private static class ResponseCallback implements CallableUnitCallback {

        @Override
        public void notifySuccess() {
            // do nothing
        }

        @Override
        public void notifyFailure(ErrorValue error) {
            // do nothing
        }
    }
}
