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

import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.idoc.jco.JCoIDocServer;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.Environment;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerFactory;
import org.ballerinalang.jvm.values.HandleValue;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.jvm.values.ObjectValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.module.sap.utils.BallerinaSapException;
import org.wso2.ei.module.sap.utils.CustomDestinationDataProvider;
import org.wso2.ei.module.sap.utils.SapConstants;
import org.wso2.ei.module.sap.utils.SapUtils;

import java.util.Properties;

import static org.wso2.ei.module.sap.utils.SapConstants.CONSUMER_SERVER_CONNECTOR_NAME;
import static org.wso2.ei.module.sap.utils.SapConstants.CONSUMER_SERVER_STRUCT_NAME;
import static org.wso2.ei.module.sap.utils.SapConstants.CONSUMER_TRANSPORT_NAME;
import static org.wso2.ei.module.sap.utils.SapConstants.DESTINATION_CONFIG;
import static org.wso2.ei.module.sap.utils.SapConstants.SERVER_CONFIG;

public class Init {

    private static Logger log = LoggerFactory.getLogger("ballerina");

    public static ObjectValue consumerInit(ObjectValue consumer) throws BallerinaSapException {

        CustomDestinationDataProvider destinationDataProvider;
        CustomServerDataProvider serverDataProvider;
//        MapValue serviceEndpoint = (MapValue)consumer;
        MapValue serverConfig = consumer.getMapValue(SERVER_CONFIG);
        MapValue destinationConfig = consumer.getMapValue(DESTINATION_CONFIG);
        Properties serverProperties = SapUtils.getServerProperties(serverConfig);
        Properties destinationProperties = SapUtils.getDestinationProperties(destinationConfig);
        destinationDataProvider = new CustomDestinationDataProvider(destinationProperties);
        String transportName = serverConfig.getStringValue(CONSUMER_TRANSPORT_NAME);
        String serverName = serverConfig.getStringValue(CONSUMER_SERVER_STRUCT_NAME);
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
                consumer.addNativeData(CONSUMER_SERVER_CONNECTOR_NAME, jcoIDocServer);
                consumer.addNativeData(CONSUMER_SERVER_CONNECTOR_NAME, jcoIDocServer);
            } catch (JCoException e) {
                throw new BallerinaSapException("Could not get the IDoc server '" + serverName + "': " + e.toString());
            }
        } else {
            try {
                JCoServer jcoServer = JCoServerFactory.getServer(serverName);
                consumer.addNativeData(CONSUMER_SERVER_CONNECTOR_NAME, jcoServer);
                serverConfig.addNativeData(CONSUMER_SERVER_CONNECTOR_NAME, jcoServer);
            } catch (JCoException e) {
                throw new BallerinaSapException("Could not get the JCo server " + serverName + ": " + e.toString());
            }
        }
        consumer.addNativeData(CONSUMER_TRANSPORT_NAME, transportName);
        return consumer;
    }
}
