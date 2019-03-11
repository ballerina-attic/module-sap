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

# Represent a Sap producer endpoint.
#
# + producerHolder - List of producers available.
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
    # + return - Error if initialization failed, none otherwise.
    extern function init(ProducerConfig config) returns error?;

    public map<any> producerHolder = {};

    # Pushes data into the SAP instance by sending IDocs over RFC.
    #
    # + idocVersion - The version of the idoc
    # + content - The message that used for transmission
    # + return - Returns error if send action fails to send IDoc, none otherwise.
    public remote extern function sendIdocMessage(int idocVersion, xml content) returns string|error;

    # Pushes data into the SAP instance by sending IDocs over RFC.
    #
    # + content - The message that used for transmission
    # + doTransaction - Do you call the BAPI_TRANSACTION_COMMIT after the LUW are completed successfully
    # + doLogon - Do you log onto one of the SAP CCMS system administration interfaces to retrieve
    #   the SAP function module
    # + productManufacturer - The manufacturer of the product that wants to log onto
    #   a CCMS system administration interface.If you log onto one of the SAP CCMS system administration interfaces,
    #   it is mandatory
    # + productName - The name of the product that is to log onto a CCMS system administration interface
    #   If you log onto one of the SAP CCMS system administration interfaces,
    #   it is mandatory.
    # + interface - The identification code of the interface that is to be used (such as XBR (DB backup),
    #   XBP (Background processing),XMB (Basic functions),XOM (Output management),XDB (DB administration))
    #   If you log onto one of the SAP CCMS system administration interfaces,
    #   it is mandatory.
    # + CCMSInterfaceVersion - The version of the CCMS system administration interface that expects
    #   the external product from the R/3 System
    # + return - Returns error if send action fails to execute bapi, none otherwise.
    public remote extern function sendBapiMessage(xml content, boolean doTransaction, boolean doLogon,
    string? productManufacturer = "", string? productName = "", string? interface = "",
    string? CCMSInterfaceVersion = "") returns string|error;
};

# The Client endpoint configuration for Sap.
#
# + destinationName - The SAP gateway name
# + sapClient - SAP Client
# + userName - Logon user
# + password - Logon password
# + language - Logon language
# + sysnr - SAP system number
# + aliasUser - Logon user alias
# + mySapsso2 - Use the specified SAP cookie version 2 as the logon ticket
# + x509Cert - X509-certificate as logon ticket
# + extiddata - External identification user logon data
# + extidtype - Type of the external identification user logon data
# + sapRouter - SAP router string to use for a system protected by a firewall
# + gwHost - Gateway host
# + asHost - SAP application server
# + msHost - SAP message server
# + msServ - SAP message server port to use instead of the default sapms sysid
# + gwServ - Gateway service
# + r3Name - System ID of the SAP system
# + serverGroup - The group of application servers
# + trace - Enable or disable RFC trace (true or false)
# + useSapGui - Start a SAP GUI and associate with the connection. (0 - do not start [default], 1 start GUI,
#   2 start GUI and hide if not used)
# + codePage  - Initial logon codepage in SAP notatio
# + getsso2  - Get/don't get an SSO ticket after logon (true or false)
# + sncPartnerName - SNC partner, e.g. p:CN=R3, O=XYZ-INC, C=EN
# + sncMode - Secure network connection (SNC) mode, false (off) or true (on)
# + sncqop - SNC level of security (1-9)
# + sncmyName - SNC name. Overrides default SNC partner
# + sncLib - Path to library which provides SNC service
#   Valid values are true (yes, default) and false (no)
# + tpName - The program ID of external server program
# + tpHost - The host of external server program
# + hostType - Type of remote host (3=R/3, E=External)
# + dest - The R/2 destination
public type ProducerConfig record {
    string destinationName = "";
    string sapClient = "";
    string userName = "";
    string password = "";
    string language = "";
    string asHost = "";
    string? mySapsso2 = "";
    string? aliasUser = "";
    string? sysnr = "";
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
    boolean trace = true;
    int useSapGui = 0;
    string? codePage = "";
    boolean getsso2 = false;
    string? sncPartnerName = "";
    boolean sncMode = false;
    string? sncqop = "";
    string? sncmyName = "";
    string? sncLib = "";
    string? tpName = "";
    string? tpHost = "";
    string? hostType = "";
    string? dest = "";
};
