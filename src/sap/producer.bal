// Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerinax/java;

# Represent a SAP producer endpoint.
#
# + producerConfig - Used to store configurations related to a SAP connection.
# + producerHolder - List of available producers.
public type Producer client object {

    public ProducerConfig producerConfig = {};
    public map<any> producerHolder = {};
    handle sapProducer = java:createNull();

    public function __init(ProducerConfig config) {
        self.producerConfig = config;
        self.initProducer();
    }

    function initProducer() {
        //map<anydata>|error config = map<anydata>.constructFrom(self.producerConfig);
        handle | error value = init(self);
        if (value is handle) {
            self.sapProducer = value;
        }
    }

    # Pushes data into the SAP instance by sending BAPI over RFC.
    #
    # + content - The message that is used for transmission
    # + doTransaction - Whether the BAPI_TRANSACTION_COMMIT is called after the LUW is completed successfully
    # + return - Returns the response or error.
    public remote function sendBapi(xml content, boolean doTransaction) returns handle | error {
        handle sapProducer = self.sapProducer;
        handle | error value = sendBapiMessage(sapProducer, content, doTransaction);
        return value;
    }

    # Pushes data into the SAP instance by sending IDocs over RFC.
    #
    # + content - The message that is used for transmission
    # + idocVersion - IDOC Version.
    # + return - Returns the response or error.
    public remote function sendIdoc(xml content, int idocVersion) returns handle | error {
        handle sapProducer = self.sapProducer;
        handle | error value = sendIdocMessage(sapProducer, idocVersion, content);
        return value;
    }
};

function init(Producer producer) returns handle | error = @java:Method {
    name: "producerInit",
    class: "org.wso2.ei.module.sap.producer.InitClient"
} external;


function sendBapiMessage(handle producer, xml content, boolean doTransaction) returns handle | error = @java:Method {
    class: "org.wso2.ei.module.sap.producer.SendBAPIMessage"
} external;


function sendIdocMessage(handle producer, int idocVersion, xml content) returns handle | error = @java:Method {
    class: "org.wso2.ei.module.sap.producer.SendIDocMessage"
} external;

# The Client endpoint configuration of SAP.
#
# + destinationName - Name of the SAP gateway
# + sapclient - SAP Client
# + username - Username to log in to SAP
# + password - Password to log in to SAP
# + language - Language preferred by the user who is logged in
# + sysnr - SAP system number
# + aliasuser - Alias of the user who is logged in.
# + mysapsso2 - The SAP cookie version 2 of the login ticket that should be used
# + x509Cert - The X509-certificate of the login ticket
# + extiddata - External identification login data of the user
# + extidtype - Type of the external identification login data of the user
# + saprouter - SAP router String to use for a system that is protected by a firewall
# + gwhost - Gateway host
# + ashost - SAP application server
# + mshost - SAP message server
# + msserv - SAP message server port to use instead of the default sapms sysid
# + gwserv - Gateway service
# + r3name - System ID of the SAP system
# + servergroup - The group of application servers
# + trace - Whether to enable or disable RFC trace (1 or 0 [default])
# + usesapgui - Start a SAP GUI and associate with the connection. (0 - do not start [default], 1 start GUI,
#   2 start GUI and hide if not used)
# + codepage  - Initial login codepage in SAP notation
# + getsso2  - Whether or not to get an SSO ticket after logging in (1 or 0 [default])
# + sncpartnername - SNC partner, e.g. p:CN=R3, O=XYZ-INC, C=EN
# + sncmode - Whether the Secure Network Connection (SNC) mode is on (1) or off (0) [default]
# + sncqop - SNC level of security (1-9, default:8)
# + sncmyname - SNC name. This overrides the default SNC partner
# + snclib - Path to the library which provides the SNC service Valid values are true (yes, default) and false (no)
# + tpname - The program ID of the external server program
# + tphost - The host of the external server program
# + hosttype - Type of the remote host (3=R/3, E=External)
# + dest - The R/2 destination
public type ProducerConfig record {|
    string destinationName = "";
    string sapclient = "";
    string username = "";
    string password = "";
    string language = "";
    string ashost = "";
    string sysnr = "";
    string? mysapsso2 = "";
    string? aliasuser = "";
    string? extiddata = "";
    string? extidtype = "";
    string? saprouter = "";
    string? x509Cert = "";
    string? gwhost = "";
    string? mshost = "";
    string? msserv = "";
    string? gwserv = "";
    string? r3name = "";
    string? servergroup = "";
    Value trace = 0;
    Value usesapgui = 0;
    string? codepage = "";
    Value getsso2 = 0;
    string? sncpartnername = "";
    Value sncmode = 0;
    SncQOP sncqop = 8;
    string? sncmyname = "";
    string? snclib = "";
    string? tpname = "";
    string? tphost = "";
    string? hosttype = "";
    string? dest = "";
|};
