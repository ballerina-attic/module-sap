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
 * specif ic language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.sap.nativeimpl.consumer;

import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerState;
import com.sap.conn.jco.server.JCoServerStateChangedListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.PrintStream;

import static org.ballerinalang.sap.utils.SapConstants.SAP_SERVER_STATE;

/**
 * The state change listener provides details for connections managed by a server instance
 * are available via JCoServerMonitor
 */
public class StateChangedListener implements JCoServerStateChangedListener {

    private static Log log = LogFactory.getLog(Start.class);
    private static final PrintStream console = System.out;

    @Override
    public void serverStateChangeOccurred(JCoServer jCoServer, JCoServerState oldState, JCoServerState newState) {

        console.println(SAP_SERVER_STATE + "Server state changed from " + oldState.toString() + " to "
                + newState.toString() + " on server with program id " + jCoServer.getProgramID());
    }
}
