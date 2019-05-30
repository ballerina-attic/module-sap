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
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.Environment;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerFactory;
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
import static org.ballerinalang.sap.utils.SapConstants.CONSUMER_SERVER_STRUCT_NAME;
import static org.ballerinalang.sap.utils.SapConstants.CONSUMER_TRANSPORT_NAME;
import static org.ballerinalang.sap.utils.SapConstants.DESTINATION_CONFIG;
import static org.ballerinalang.sap.utils.SapConstants.SERVER_CONFIG;

/**
 * This is used to configure the properties of the destination and server, and create the RFC connection.
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

    @Override
    public void execute(Context context) {
        CustomDestinationDataProvider destinationDataProvider;
        CustomServerDataProvider serverDataProvider;
        Struct serviceEndpoint = BLangConnectorSPIUtil.getConnectorEndpointStruct(context);
        Struct serverConfig = serviceEndpoint.getStructField(SERVER_CONFIG);
        Struct destinationConfig = serviceEndpoint.getStructField(DESTINATION_CONFIG);
        Properties serverProperties = SapUtils.getServerProperties(serverConfig);
        Properties destinationProperties = SapUtils.getDestinationProperties(destinationConfig);
        destinationDataProvider = new CustomDestinationDataProvider(destinationProperties);
        String transportName = serverConfig.getStringField(CONSUMER_TRANSPORT_NAME);
        String serverName = serverConfig.getStringField(CONSUMER_SERVER_STRUCT_NAME);
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
                serviceEndpoint.addNativeData(CONSUMER_SERVER_CONNECTOR_NAME, jcoIDocServer);
                serverConfig.addNativeData(CONSUMER_SERVER_CONNECTOR_NAME, jcoIDocServer);
            } catch (JCoException e) {
                throw new BallerinaException("Could not get the IDoc server " + serverName + ": " + e.toString());
            }
        } else {
            try {
                JCoServer jcoServer = JCoServerFactory.getServer(serverName);
                serviceEndpoint.addNativeData(CONSUMER_SERVER_CONNECTOR_NAME, jcoServer);
                serverConfig.addNativeData(CONSUMER_SERVER_CONNECTOR_NAME, jcoServer);
            } catch (JCoException e) {
                throw new BallerinaException("Could not get the Jco server " + serverName + ": " + e.toString());
            }
        }
        serviceEndpoint.addNativeData(CONSUMER_TRANSPORT_NAME, transportName);
    }
}
