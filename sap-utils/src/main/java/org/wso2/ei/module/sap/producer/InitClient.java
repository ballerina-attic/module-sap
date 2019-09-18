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

package org.wso2.ei.module.sap.producer;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.jvm.values.ObjectValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.module.sap.utils.BallerinaSapException;
import org.wso2.ei.module.sap.utils.CustomDestinationDataProvider;
import org.wso2.ei.module.sap.utils.SapConstants;

import java.util.Properties;

import static org.wso2.ei.module.sap.utils.SapConstants.NATIVE_PRODUCER;

/**
 * Initialize a producer instance for the connector.
 */
public class InitClient {

    private static Logger log = LoggerFactory.getLogger("ballerina");

    /**
     * Initialize the producer instance for the connector.
     *
     * @param producer Producer object that is to be initialized.
     * @return Returns the initialized producer object.
     * @throws BallerinaSapException Throws a BallerinaSapException.
     */
    public static ObjectValue producerInit(ObjectValue producer) throws BallerinaSapException {

        log.debug("Initializing the producer client.");
        MapValue producerConf = (MapValue) producer.get("producerConfig");
        CustomDestinationDataProvider destinationDataProvider;
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT,
                String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_CLIENT)));
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,
                String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_USER)));
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD,
                String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_PASSWD)));
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,
                String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_LANG)));
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST,
                String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_ASHOST)));
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,
                String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SYSNR)));
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties
                .JCO_EXTID_DATA))) != null && !String.valueOf(producerConf.get(SapConstants.Clientproperties
                .JCO_EXTID_DATA)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_EXTID_DATA,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_EXTID_DATA)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties
                .JCO_EXTID_TYPE))) != null && !String.valueOf(producerConf.get(SapConstants.Clientproperties
                .JCO_EXTID_TYPE)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_EXTID_TYPE,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_EXTID_TYPE)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties
                .JCO_SAPROUTER))) != null && !String.valueOf(producerConf.get(SapConstants.Clientproperties
                .JCO_SAPROUTER)).equals("")) {
            connectProperties.setProperty(SapConstants.Clientproperties.JCO_SAPROUTER,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SAPROUTER)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties
                .JCO_GWHOST))) != null && !String.valueOf(producerConf.get(SapConstants.Clientproperties
                .JCO_GWHOST)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_GWHOST,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_GWHOST)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties
                .JCO_MSHOST))) != null && !String.valueOf(producerConf.get(SapConstants.Clientproperties
                .JCO_MSHOST)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_MSHOST,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_MSHOST)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_ALIAS_USER))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_ALIAS_USER)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_ALIAS_USER,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_ALIAS_USER)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_MSSERV))) != null &&
                !String.valueOf(producerConf.get(SapConstants.Clientproperties
                        .JCO_MSSERV)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_MSSERV,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_MSSERV)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_GWSERV))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_GWSERV)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_GWSERV,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_GWSERV)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_R3NAME))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_R3NAME)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_R3NAME)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_GROUP))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_GROUP)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_GROUP,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_GROUP)));
        }
        connectProperties.setProperty(DestinationDataProvider.JCO_TRACE,
                String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_TRACE)));
        connectProperties.setProperty(DestinationDataProvider.JCO_USE_SAPGUI,
                String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_USE_SAPGUI)));
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_CODEPAGE))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_CODEPAGE)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_CODEPAGE,
                    String.valueOf(Integer.parseInt(String.valueOf(producerConf.get(SapConstants.
                            Clientproperties.JCO_CODEPAGE)))));
        }
        connectProperties.setProperty(DestinationDataProvider.JCO_GETSSO2,
                String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_GETSSO2)));
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_PARTNERNAME))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_PARTNERNAME)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_SNC_PARTNERNAME,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_PARTNERNAME)));
        }
        connectProperties.setProperty(DestinationDataProvider.JCO_SNC_MODE,
                String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_MODE)));
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_QOP))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_QOP)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_SNC_QOP,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_QOP)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_MYNAME))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_MYNAME)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_SNC_MYNAME,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_MYNAME)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_LIBRARY))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_LIBRARY)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_SNC_LIBRARY,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_SNC_LIBRARY)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_TPNAME))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_TPNAME)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_TPNAME,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_TPNAME)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_TPHOST))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_TPHOST)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_TPHOST,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_TPHOST)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_TYPE))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_TYPE)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_TYPE,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_TYPE)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_DEST))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_DEST)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_DEST,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_DEST)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_MYSAPSSO2))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_MYSAPSSO2)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_MYSAPSSO2,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_MYSAPSSO2)));
        }
        if ((String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_X509CERT))) != null
                && !String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_X509CERT)).equals("")) {
            connectProperties.setProperty(DestinationDataProvider.JCO_X509CERT,
                    String.valueOf(producerConf.get(SapConstants.Clientproperties.JCO_X509CERT)));
        }
        JCoDestination destination = null;
        String destinationName = String.valueOf(producerConf.get(SapConstants.DESTINATION_NAME));
        destinationDataProvider = new CustomDestinationDataProvider(connectProperties);
        if (!Environment.isDestinationDataProviderRegistered()) {
            Environment.registerDestinationDataProvider(destinationDataProvider);
        }
        try {
            destination = JCoDestinationManager.getDestination(destinationName);
        } catch (JCoException e) {
            throw new BallerinaSapException("Destination named '" + destinationName
                    + "' is not registered with JCo.", e);
        }

        producer.addNativeData(NATIVE_PRODUCER, destination);
        MapValue<String, Object> producerStruct = (MapValue<String, Object>) producerConf;
        producerStruct.addNativeData(NATIVE_PRODUCER, destination);
        producer.addNativeData(new String(NATIVE_PRODUCER), producerStruct);
        return (ObjectValue) producer;
    }
}
