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

    public ProducerConfig producerConfig;

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
    extern function init(ProducerConfig config) returns error?;

    public map<any> producerHolder = {};

    # Pushes data into the SAP instance by sending IDocs over RFC.
    #
    # + idocVersion - The version of the IDoc
    # + content - The message that used for transmission
    # + return - Returns an error if sending the IDoc fails.
    public remote extern function sendIdocMessage(int idocVersion, xml content) returns string|error;

    # Pushes data into the SAP instance by sending IDocs over RFC.
    #
    # + content - The message that is used for transmission
    # + doTransaction - Whether the BAPI_TRANSACTION_COMMIT is called after the LUW is completed successfully
    # + doLogon - Whether you log in to one of the SAP CCMS system-administration interfaces to retrieve
    #   the SAP function module
    # + productManufacturer - The manufacturer of the product to whose CCMS system administration interface you want to log in to
    #   a CCMS system administration interface. If you log in to one of the SAP CCMS system-administration interfaces,
    #   it is mandatory
    # + productName - The name of the product to whose CCMS system-administration interface you want to log in to
    #   If you log in to one of the SAP CCMS system-administration interfaces,
    #   it is mandatory.
    # + interface - The identification code of the interface that is to be used (such as XBR (DB backup),
    #   XBP (Background processing), XMB (Basic functions), XOM (Output management), and XDB (DB administration))
    #   If you log in to one of the SAP CCMS system-administration interfaces,
    #   it is mandatory.
    # + CCMSInterfaceVersion - The version of the CCMS system-administration interface that expects
    #   the external product from the R/3 System
    # + return - Returns an error if sending the BAPI fails.
    public remote extern function sendBapiMessage(xml content, boolean doTransaction, boolean doLogon,
    string? productManufacturer = "", string? productName = "", string? interface = "",
    string? CCMSInterfaceVersion = "") returns string|error;
};

# The Client endpoint configuration of SAP.
#
# + destinationName - Name of the SAP gateway
# + sapClient - SAP Client
# + userName - Username to log in to SAP
# + password - Password to log in to SAP
# + language - Language preferred by the user who is logged in
# + sysnr - SAP system number
# + aliasUser - Alias of the user who is logged in.
# + mySapsso2 - The SAP cookie version 2 of the login ticket that should be used
# + x509Cert - The X509-certificate of the login ticket
# + extiddata - External identification login data of the user
# + extidtype - Type of the external identification login data of the user
# + sapRouter - SAP router String to use for a system that is protected by a firewall
# + gwHost - Gateway host
# + asHost - SAP application server
# + msHost - SAP message server
# + msServ - SAP message server port to use instead of the default sapms sysid
# + gwServ - Gateway service
# + r3Name - System ID of the SAP system
# + serverGroup - The group of application servers
# + trace - Whether to enable or disable RFC trace (1 or 0 [default])
# + useSapGui - Start a SAP GUI and associate with the connection. (0 - do not start [default], 1 start GUI,
#   2 start GUI and hide if not used)
# + codePage  - Initial login codepage in SAP notation
# + getsso2  - Whether or not to get an SSO ticket after logging in (1 or 0 [default])
# + sncPartnerName - SNC partner, e.g. p:CN=R3, O=XYZ-INC, C=EN
# + sncMode - Whether the Secure Network Connection (SNC) mode is on (1) or off (0) [default]
# + sncqop - SNC level of security (1-9, default:8)
# + sncmyName - SNC name. This overrides the default SNC partner
# + sncLib - Path to the library which provides the SNC service
#   Valid values are true (yes, default) and false (no)
# + tpName - The program ID of the external server program
# + tpHost - The host of the external server program
# + hostType - Type of the remote host (3=R/3, E=External)
# + dest - The R/2 destination
public type ProducerConfig record {
    string destinationName = "";
    string sapClient = "";
    string userName = "";
    string password = "";
    string language = "";
    string asHost = "";
    string sysnr = "";
    string? mySapsso2 = "";
    string? aliasUser = "";
    string? extiddata = "";
    string? extidtype = "";
    string? sapRouter = "";
    string? x509Cert = "";
    string? gwHost = "";
    string? msHost = "";
    string? msServ = "";
    string? gwServ = "";
    string? r3Name = "";
    string? serverGroup = "";
    Value trace = 0;
    Value useSapGui = 0;
    string? codePage = "";
    Value getsso2 = 0;
    string? sncPartnerName = "";
    Value sncMode = 0;
    SncQOP sncqop = 8;
    string? sncmyName = "";
    string? sncLib = "";
    string? tpName = "";
    string? tpHost = "";
    string? hostType = "";
    string? dest = "";
};