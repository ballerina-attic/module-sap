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

import com.sap.conn.idoc.jco.JCoIDocServer;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.sap.utils.SapConstants;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.util.concurrent.TimeUnit;

import static org.ballerinalang.sap.utils.SapConstants.CONSUMER_SERVER_CONNECTOR_NAME;
import static org.ballerinalang.sap.utils.SapConstants.CONSUMER_SERVER_STRUCT_NAME;
import static org.ballerinalang.sap.utils.SapConstants.CONSUMER_TRANSPORT_NAME;
import static org.ballerinalang.sap.utils.SapConstants.SAP_BAPI_PROTOCOL_NAME;
import static org.ballerinalang.sap.utils.SapConstants.SAP_IDOC_PROTOCOL_NAME;
import static org.ballerinalang.sap.utils.SapConstants.serverStopTimeout;


/**
 * Stop the server connector.
 */
@BallerinaFunction(
    orgName = SapConstants.ORG_NAME,
    packageName = SapConstants.FULL_PACKAGE_NAME,
    functionName = "stop",
    receiver = @Receiver(
            type = TypeKind.OBJECT,
            structType = SapConstants.CONSUMER_STRUCT_NAME,
            structPackage = SapConstants.SAP_NATIVE_PACKAGE
    ),
    isPublic = true
)
public class Stop extends BlockingNativeCallableUnit {

    private static final Log log = LogFactory.getLog(Stop.class);

    @Override
    public void execute(Context context) {
        Struct consumerStruct = BLangConnectorSPIUtil.getConnectorEndpointStruct(context);
        String transportName = (String) consumerStruct.getNativeData(CONSUMER_TRANSPORT_NAME);
        String serverName = (String) consumerStruct.getNativeData(CONSUMER_SERVER_STRUCT_NAME);
        if (transportName.equalsIgnoreCase(SAP_IDOC_PROTOCOL_NAME)) {
            //Get the running IDoc server
            JCoIDocServer jcoIDocServer = (JCoIDocServer) consumerStruct.getNativeData(CONSUMER_SERVER_CONNECTOR_NAME);
            if (log.isDebugEnabled()) {
                log.debug("Stopping the JCo endpoint : " + serverName);
            }
            jcoIDocServer.stop();
            jcoIDocServer.release();
            if (waitForServerStop(jcoIDocServer)) {
                log.warn("JCo server : " + serverName + " is taking an unusually long time to stop.");
            } else {
                log.info("JCo server : " + serverName + " stopped");
            }
        } else if (transportName.equalsIgnoreCase(SAP_BAPI_PROTOCOL_NAME)) {
            //Get the running JCo server
            JCoServer jcoServer = (JCoServer) consumerStruct.getNativeData(CONSUMER_SERVER_CONNECTOR_NAME);
            if (log.isDebugEnabled()) {
                log.debug("Stopping the JCo endpoint : " + serverName);
            }
            jcoServer.stop();
            jcoServer.release();
            if (waitForServerStop(jcoServer)) {
                log.warn("JCo server : " + serverName + " is taking an unusually long time to stop.");
            } else {
                log.info("JCo server : " + serverName + " stopped");
            }
        } else {
            throw new BallerinaException("Protocol name: " + transportName + " is not supported.");
        }
        context.setReturnValues();
    }

    /**
     * Block until the server state is stopped or until the maximum timeout time is reached.
     *
     * @param jcoServer The jcoServer The JCO server, which will wait to be stopped
     * @return true if the server is stopped before the timeout time is exceeded.
     */
    private boolean waitForServerStop(JCoServer jcoServer) {
        long timeStamp = System.currentTimeMillis();
        while (jcoServer.getState() != JCoServerState.STOPPED
                && timeStamp + serverStopTimeout > System.currentTimeMillis()) {
            if (log.isDebugEnabled()) {
                log.debug("Waiting for server to stop...");
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                //Continue the loop.
            }
        }
        return jcoServer.getState() != JCoServerState.STOPPED;
    }
}
