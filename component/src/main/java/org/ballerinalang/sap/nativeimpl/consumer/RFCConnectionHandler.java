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
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.model.values.BError;
import org.ballerinalang.sap.utils.SapUtils;

import java.io.PrintStream;
import java.util.Map;

/**
 * This class handles the BAPI calls that are returned from the SAP gateway.
 * <p>
 * This class encapsulates each BAPI/RFC request received.
 * by handling  and converting them to Strings.
 * </p>
 */
public class RFCConnectionHandler implements JCoServerFunctionHandler {

    private static final Log log = LogFactory.getLog(RFCConnectionHandler.class);
    private ResponseCallback callback;
    private Map<String, Resource> sapResource;
    private Context context;
    private static final PrintStream console = System.out;

    RFCConnectionHandler(Map<String, Resource> sapService, Context context) {
        this.sapResource = sapService;
        callback = new ResponseCallback();
        this.context = context;
        log.info("......JCo server function handler is created.");
    }

    /**
     * Handle BAPI requests coming through the SAP gateway.
     * @param jCoServerContext The jCoServerContext JCO Server environment configuration
     * @param jCoFunction The jCoFunction BAPI/RFC function which is being called
     */
    public void handleRequest(JCoServerContext jCoServerContext, JCoFunction jCoFunction) {
        if (log.isDebugEnabled()) {
            log.debug("New BAPI function call received");
        }
        console.println("New BAPI function call received");
        jCoFunction.getExportParameterList().setValue("ECHOTEXT", jCoFunction.getImportParameterList()
                .getString("REQUTEXT"));
        String output = jCoFunction.getImportParameterList().getString("REQUTEXT");
        SapUtils.invokeOnBapiMessage(output, sapResource, callback, context);
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
