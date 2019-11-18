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

import com.sap.conn.idoc.jco.JCoIDocServer;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerState;

import java.util.concurrent.TimeUnit;

import org.ballerinalang.jvm.values.HandleValue;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.jvm.values.ObjectValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.module.sap.utils.BallerinaSapException;
import org.wso2.ei.module.sap.utils.SapConstants;

/**
 * Stop the connector service.
 */
public class Stop {

    private static Logger log = LoggerFactory.getLogger(SapConstants.BALLERINA);

    /**
     * Stop a running JCo or IDoc Service.
     *
     * @param consumer Consumer object through which the connection is created.
     * @throws BallerinaSapException Throws a BallerinaSapException.
     */
    public static HandleValue stop(ObjectValue consumer) throws BallerinaSapException {

        MapValue consumerStruct = (MapValue) consumer;
        String transportName = (String) consumerStruct.getNativeData(SapConstants.CONSUMER_TRANSPORT_NAME);
        String serverName = (String) consumerStruct.getNativeData(SapConstants.CONSUMER_SERVER_STRUCT_NAME);
        if (transportName.equalsIgnoreCase(SapConstants.SAP_IDOC_PROTOCOL_NAME)) {
            //Get the running IDoc service
            JCoIDocServer jcoIDocServer = (JCoIDocServer) consumerStruct.getNativeData(SapConstants.CONSUMER_SERVER_CONNECTOR_NAME);
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
        } else if (transportName.equalsIgnoreCase(SapConstants.SAP_BAPI_PROTOCOL_NAME)) {
            //Get the running JCo service
            JCoServer jcoServer = (JCoServer) consumerStruct.getNativeData(SapConstants.CONSUMER_SERVER_CONNECTOR_NAME);
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
            throw new BallerinaSapException("Protocol name: " + transportName + " is not supported.");
        }
        return new HandleValue("consumer");
    }

    /**
     * Block until the server state is stopped or until the maximum timeout time is reached.
     *
     * @param jcoServer The jcoServer The JCO server, which will wait to be stopped.
     * @return True if the server is stopped before the timeout time is exceeded.
     */
    private static boolean waitForServerStop(JCoServer jcoServer) {

        long timeStamp = System.currentTimeMillis();
        while (jcoServer.getState() != JCoServerState.STOPPED
                && timeStamp + SapConstants.serverStopTimeout > System.currentTimeMillis()) {
            if (log.isDebugEnabled()) {
                log.debug("Waiting for the server to stop.");
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
