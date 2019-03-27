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

# Represent a SAP producer endpoint.
#
# + producerHolder - List of available producers.
# + producerConfig - Used to store configurations related to a SAP connection.
public type Producer client object {

    ProducerConfig producerConfig;

    public function __init(ProducerConfig config) {
        self.producerConfig = config;
        var initResult = self.init(config);
        if (initResult is error) {
            panic initResult;
        }
    }

    # Initialize the producer endpoint.
    #
    # + config - Configurations related to the endpoint.
    # + return - Return an error if initialization failed.
    function init(ProducerConfig config) returns error? = external;

    public map<any> producerHolder = {};

    # Pushes data into the SAP instance by sending IDocs over RFC.
    #
    # + idocVersion - The version of the IDoc
    # + content - The message that used for transmission
    # + return - Returns an error if sending the IDoc fails.
    public remote function sendIdocMessage(int idocVersion, xml content) returns string|error = external;

    # Pushes data into the SAP instance by sending IDocs over RFC.
    #
    # + content - The message that is used for transmission
    # + doTransaction - Whether the BAPI_TRANSACTION_COMMIT is called after the LUW is completed successfully
    # + doLogon - Whether you log in to one of the SAP CCMS system-administration interfaces to retrieve
    #   the SAP function module
    # + productManufacturer - The manufacturer of the product to whose CCMS system administration interface you want to
    #   log in to a CCMS system administration interface. If you log in to one of the SAP CCMS system-administration
    #   interfaces, it is mandatory
    # + productName - The name of the product to whose CCMS system-administration interface you want to log in to
    #   If you log in to one of the SAP CCMS system-administration interfaces, it is mandatory.
    # + interface - The identification code of the interface that is to be used (such as XBR (DB backup),
    #   XBP (Background processing), XMB (Basic functions), XOM (Output management), and XDB (DB administration))
    #   If you log in to one of the SAP CCMS system-administration interfaces, it is mandatory.
    # + CCMSInterfaceVersion - The version of the CCMS system-administration interface that expects
    #   the external product from the R/3 System
    # + return - Returns an error if sending the BAPI fails.
    public remote function sendBapiMessage(xml content, boolean doTransaction, boolean doLogon,
    string? productManufacturer = "", string? productName = "", string? interface = "",
    string? CCMSInterfaceVersion = "") returns string|error = external;
};

# The Client endpoint configuration of SAP.
#
# + destinationName - Name of the SAP gateway
# + ^"client" - SAP Client
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
    string ^"client" = "";
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