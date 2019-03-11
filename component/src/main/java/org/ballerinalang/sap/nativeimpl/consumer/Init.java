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

import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.idoc.jco.JCoIDocServer;
import com.sap.conn.jco.ext.Environment;
import com.sap.conn.jco.server.JCoServer;
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
import org.ballerinalang.sap.utils.SapUtils;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.util.Properties;

import static org.ballerinalang.sap.utils.SapConstants.CONSUMER_SERVER_CONNECTOR_NAME;
import static org.ballerinalang.sap.utils.SapConstants.CONSUMER_TRANSPORT_NAME;

/**
 * This is used to configure properties for destination and server, and made the RFC connection.
 */
@BallerinaFunction(
    orgName = SapConstants.ORG_NAME,
    packageName = SapConstants.FULL_PACKAGE_NAME,
    functionName = "init",
    receiver = @Receiver(
            type = TypeKind.OBJECT,
            structType = SapConstants.CONSUMER_STRUCT_NAME,
            structPackage = SapConstants.SAP_NATIVE_PACKAGE
    ),
    isPublic = true
)
public class Init extends BlockingNativeCallableUnit {
    private static final Log log = LogFactory.getLog(Init.class);
    private static CustomDestinationDataProvider destinationDataProvider;
    private static CustomServerDataProvider serverDataProvider;
    @Override
    public void execute(Context context) {

        Struct serviceEndpoint = BLangConnectorSPIUtil.getConnectorEndpointStruct(context);
        Struct serverConfig = serviceEndpoint.getStructField("serverConfig");
        Struct destinationConfig = serviceEndpoint.getStructField("destinationConfig");
        Properties serverProperties = SapUtils.getServerProperties(serverConfig);
        Properties destinationProperties = SapUtils.getDestinationProperties(destinationConfig);
        destinationDataProvider = new CustomDestinationDataProvider(destinationProperties);
        String transportName = serverConfig.getStringField("transportName");
        String serverName = serverConfig.getStringField("serverName");
        serverDataProvider = new CustomServerDataProvider(serverProperties);
        if (!Environment.isDestinationDataProviderRegistered()) {
            Environment.registerDestinationDataProvider(destinationDataProvider);
        }
        if (!Environment.isServerDataProviderRegistered()) {
            Environment.registerServerDataProvider(serverDataProvider);
        }
        if (transportName.equalsIgnoreCase(SapConstants.SAP_IDOC_PROTOCOL_NAME)) {
            try {
                JCoIDocServer jcoIDocServer = JCoIDoc.getServer(serverName);
                jcoIDocServer.setTIDHandler(new CustomServerTIDHandler());
                jcoIDocServer.addServerErrorListener((server, connectionId, serverCtx, error) -> {
                    log.info("Error occured on " + server.getProgramID() + " connection " + connectionId
                            + error.toString());
                });
                jcoIDocServer.addServerExceptionListener((server, connectionId, serverCtx, error) -> {
                    log.info("Exception occured on " + server.getProgramID() + " connection " + connectionId
                            + error.toString());
                });
                jcoIDocServer.addServerStateChangedListener((server, oldState, newState) ->
                        log.info("Server state changed from " + oldState.toString() + " to " + newState.toString()
                                + " on server with program id " + server.getProgramID()));

                serviceEndpoint.addNativeData(CONSUMER_SERVER_CONNECTOR_NAME, jcoIDocServer);
                serverConfig.addNativeData(CONSUMER_SERVER_CONNECTOR_NAME, jcoIDocServer);
            } catch (Exception e) {
                throw new BallerinaException("Could not start the IDoc server " + serverName + " , because of "
                        + e.toString());
            }
        } else {
            try {
                JCoServer jcoServer = JCoIDoc.getServer(serverName);
                jcoServer.setTIDHandler(new CustomServerTIDHandler());
                jcoServer.addServerErrorListener((server, connectionId, serverCtx, error) -> {
                    log.info("Error occured on " + server.getProgramID() + " connection " + connectionId
                            + error.toString());
                });
                jcoServer.addServerExceptionListener((server, connectionId, serverCtx, error) -> {
                    log.info("Exception occured on " + server.getProgramID() + " connection " + connectionId
                            + error.toString());
                });
                jcoServer.addServerStateChangedListener((server, oldState, newState) ->
                        log.info("Server state changed from " + oldState.toString() + " to " + newState.toString()
                                + " on server with program id " + server.getProgramID()));

                serviceEndpoint.addNativeData(CONSUMER_SERVER_CONNECTOR_NAME, jcoServer);
                serverConfig.addNativeData(CONSUMER_SERVER_CONNECTOR_NAME, jcoServer);
            } catch (Exception e) {
                throw new BallerinaException("Could not start the IDoc server " + serverName + " , because of "
                        + e.toString());
            }
        }
        serviceEndpoint.addNativeData(CONSUMER_TRANSPORT_NAME, transportName);
    }
}
