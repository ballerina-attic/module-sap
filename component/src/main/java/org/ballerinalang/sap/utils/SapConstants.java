/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.sap.utils;

import javax.xml.namespace.QName;

/**
 * Constants related to SAP.
 */
public class SapConstants {

    private SapConstants() {
    }

    public static final String PATH_SEPARATOR = "/";
    public static final String BLOCK_SEPARATOR = ":";

    public static final String ORG_NAME = "wso2";
    public static final String PACKAGE_NAME = "sap";
    public static final String VERSION = "0.0.0";
    public static final String FULL_PACKAGE_NAME = PACKAGE_NAME + BLOCK_SEPARATOR + VERSION;
    public static final String SAP_NATIVE_PACKAGE = ORG_NAME + PATH_SEPARATOR + FULL_PACKAGE_NAME;

    public static final String RESOURCE_ON_MESSAGE = "onMessage";
    public static final String RESOURCE_ON_ERROR = "onError";
    public static final String SAP_RESOURCE = "sapResource";

    public static final String CONSUMER_STRUCT_NAME = "Listener";
    public static final String CONSUMER_TRANSPORT_NAME = "transportName";
    public static final String CONSUMER_SERVER_CONNECTOR_NAME = "serverConnector";
    public static final String CONSUMER_SERVER_STRUCT_NAME = "serverName";
    public static final String DESTINATION_CONFIG = "destinationConfig";
    public static final String SERVER_CONFIG = "serverConfig";
    public static final String SAP_MESSAGE_OBJECT = "sap_message_object";

    public static final String PRODUCER_STRUCT_NAME = "Producer";
    public static final String DESTINATION_NAME = "destinationName";
    public static final String NATIVE_PRODUCER = "SapProducer";

    public static final String SAP_IDOC_PROTOCOL_NAME = "idoc";
    public static final String SAP_BAPI_PROTOCOL_NAME = "bapi";

    public static final String SAP_IDOC_VERSION_2 = "2";
    public static final String SAP_IDOC_VERSION_3 = "3";
    public static final String SAP_ERROR_CODE = "error";

    /**
     * Name of the RFC configuration.
     */
    public static final QName NAME_Q = new QName("name");

    /**
     *ID of the RFC configuration.
     */
    public static final QName ID_Q = new QName("id");

    public static final String ROLLBACK_BAPI_NAME = "BAPI_TRANSACTION_ROLLBACK";
    public static final String BAPI_COMMIT_NAME = "BAPI_TRANSACTION_COMMIT";

    public static final String BAPI_XMI_LOGON = "BAPI_XMI_LOGON";
    public static final String EXTCOMPANY = "EXTCOMPANY";
    public static final String EXTPRODUCT = "EXTPRODUCT";
    public static final String INTERFACE = "INTERFACE";
    public static final String XMIVERSION = "VERSION";
    public static final String RETURN = "RETURN";
    public static final String MESSAGE = "MESSAGE";
    public static final String TYPE = "TYPE";
    public static final String S = "S";
    public static final String W = "W";
    public static final String TABLE = "table";
    public static final String FIELD = "field";
    public static final String STRUCTURE = "structure";
    public static final String IMPORT = "import";
    public static final String TABLES = "tables";

    /**
     * Max timeout to allow the server to stop.
     */
    public static int serverStopTimeout = 30000;

    /**
     * Server configuration constants.
     */
    public static class Serverproperties {
        public static final String JCO_GWHOST = "gwHost";
        public static final String JCO_PROGID = "progId";
        public static final String JCO_REPOSITORY_DEST = "repositoryDestination";
        public static final String JCO_GWSERV = "gwServ";
        public static final String JCO_CONNECTION_COUNT = "connectionCount";

        public static final String JCO_TRACE = "trace";
        public static final String JCO_SNC_MYNAME = "sncMyName";
        public static final String JCO_SNC_QOP = "sncqop";
        public static final String JCO_SNC_LIBRARY = "sncLib";
        public static final String JCO_UNICODE = "unicode";
        public static final String JCO_SERVER_UNICODE = "jco.server.unicode";
    }

    /**
     * Client configuration constants.
     */
    public static class Clientproperties {

        public static final String JCO_MSHOST = "msHost";
        public static final String JCO_R3NAME = "r3Name";
        public static final String JCO_CLIENT = "sapClient";
        public static final String JCO_USER = "userName";
        public static final String JCO_PASSWD = "password";
        public static final String JCO_GROUP = "serverGroup";
        public static final String JCO_LANG = "language";

        public static final String JCO_ALIAS_USER = "aliasUser";
        public static final String JCO_SYSNR = "sysnr";
        public static final String JCO_ASHOST = "asHost";
        public static final String JCO_GWHOST = "gwHost";
        public static final String JCO_GWSERV = "gwServ";
        public static final String JCO_TPNAME = "tpName";
        public static final String JCO_TPHOST = "tpHost";
        public static final String JCO_TYPE = "hostType";
        public static final String JCO_CODEPAGE = "codePage";
        public static final String JCO_USE_SAPGUI = "useSapGui";
        public static final String JCO_MYSAPSSO2 = "mySapsso2";
        public static final String JCO_SNC_PARTNERNAME = "sncPartnerName";
        public static final String JCO_SNC_MODE = "sncMode";
        public static final String JCO_SNC_QOP = "sncqop";
        public static final String JCO_SNC_MYNAME = "sncmyName";
        public static final String JCO_SNC_LIBRARY = "sncLib";
        public static final String JCO_DEST = "dest";
        public static final String JCO_EXTID_DATA = "extiddata";
        public static final String JCO_EXTID_TYPE = "extidtype";
        public static final String JCO_X509CERT = "x509Cert";
        public static final String JCO_MSSERV = "msServ";
        public static final String JCO_TRACE = "trace";
        public static final String JCO_GETSSO2 = "getsso2";
        public static final String JCO_SAPROUTER = "sapRouter";
    }
}
