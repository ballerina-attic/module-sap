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
import com.sap.conn.jco.server.DefaultServerHandlerFactory.FunctionHandlerFactory;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import org.ballerinalang.jvm.types.AttachedFunction;
import org.ballerinalang.jvm.values.HandleValue;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.jvm.values.ObjectValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.module.sap.utils.BallerinaSapException;
import org.wso2.ei.module.sap.utils.SapConstants;

import java.io.PrintStream;
import java.util.Map;

import static org.wso2.ei.module.sap.utils.SapConstants.CONSUMER_SERVER_CONNECTOR_NAME;
import static org.wso2.ei.module.sap.utils.SapConstants.CONSUMER_TRANSPORT_NAME;
import static org.wso2.ei.module.sap.utils.SapConstants.SAP_RESOURCE;

public class Start {

    private static Logger log = LoggerFactory.getLogger("ballerina");
    private static final PrintStream console = System.out;

    public static HandleValue start(ObjectValue consumerStruct) throws BallerinaSapException {

//        MapValue consumerStruct = (MapValue) consumer;
        Map<String, AttachedFunction> sapService = (Map<String, AttachedFunction>)
                consumerStruct.getNativeData(SAP_RESOURCE);
        String transportName = (String) consumerStruct.getNativeData(CONSUMER_TRANSPORT_NAME);
        StateChangedListener stateListener = new StateChangedListener();
        ThrowableListener listener = new ThrowableListener(sapService);
        if (transportName.equalsIgnoreCase(SapConstants.SAP_BAPI_PROTOCOL_NAME)) {
            try {
                FunctionHandlerFactory factory = new FunctionHandlerFactory();
                JCoServerFunctionHandler rfcConnectionHandler = new BapiHandlerFactory(sapService);
                factory.registerGenericHandler(rfcConnectionHandler);
                JCoServer jcoServer = (JCoServer) consumerStruct.getNativeData(CONSUMER_SERVER_CONNECTOR_NAME);
                //Configure the Jco Server
                jcoServer.setCallHandlerFactory(factory);
                jcoServer.setTIDHandler(new CustomServerTIDHandler());
                jcoServer.addServerErrorListener(listener);
                jcoServer.addServerExceptionListener(listener);
                jcoServer.addServerStateChangedListener(stateListener);
                // Start the JCo server
                jcoServer.start();
                console.println("JCo Server started");
            } catch (Exception e) {
                throw new BallerinaSapException("Error while starting the JCo Server: ", e);
            }

        } else {
            try {
                JCoIDocServer jcoIDocServer = (JCoIDocServer)
                        consumerStruct.getNativeData(CONSUMER_SERVER_CONNECTOR_NAME);
                IDocHandlerFactory iDocHandlerFactory = new IDocHandlerFactory(sapService);
                //Configure the IDoc Server
                jcoIDocServer.setIDocHandlerFactory(iDocHandlerFactory);
                jcoIDocServer.setTIDHandler(new CustomServerTIDHandler());
                jcoIDocServer.addServerErrorListener(listener);
                jcoIDocServer.addServerExceptionListener(listener);
                jcoIDocServer.addServerStateChangedListener(stateListener);
                // Start the IDoc server
                jcoIDocServer.start();
                console.println("IDoc Server started");
            } catch (Exception e) {
                throw new BallerinaSapException("Error while starting the IDoc Server: ", e);
            }

        }
        return new HandleValue("consumer");
    }
}
