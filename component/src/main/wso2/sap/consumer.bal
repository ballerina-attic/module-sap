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

# Represents a SAP service Listener.
public type Listener object {
    *AbstractListener;

    private ConsumerServerConfig serverConfig = {};
    private ConsumerDestinationConfig destinationConfig = {};

    public function __init(ConsumerServerConfig servConfig, ConsumerDestinationConfig destConfig) {
        self.serverConfig = servConfig;
        self.destinationConfig = destConfig;
        var initResult = self.init(servConfig, destConfig);
        if (initResult is error) {
            panic initResult;
        }
    }

    public function __start() returns error? {
        return self.start();
    }

    public function __stop() returns error? {
        return self.stop();
    }

    public function __attach(service s, map<any> annotationData) returns error? {
        return self.register(s, annotationData);
    }

    extern function register(service serviceType, map<any> annotationData) returns error?;

    extern function start() returns error?;

    extern function stop() returns error?;

    extern function init(ConsumerServerConfig servConfig, ConsumerDestinationConfig destConfig) returns error?;
};

# The server properties configuration of SAP.
#
# + transportName - Name of the protocol
# + serverName - Name of the server configuration. This needs to be the same name that is provided in the SAP configuration.
# + gwhost - Gateway host on which the server should be registered
# + progId - The program ID with which the registration is done
# + gwServ - Gateway service( i.e., the port on which a registration can be done)
# + connectionCount - The number of connections that should be registered with the gateway. default: 1
# + trace - Enable/disable the RFC trace (1 or 0 [default])
# + repositoryDestination - Name of the .dest file. For example, if the .dest file is SAPSYS01.dest,
#                            set this to SAPSYS01.
# + unicode - Determines whether you connect in Unicode mode or not (true, false)
# + sncMyName - SNC name of your server. This overrides the default SNC name. Typically, something like
#   p:CN=JCoServer, O=ACompany, C=EN
# + sncqop - SNC level of security, 1 to 9
# + sncLib - Path to the library, which provides the SNC service.
public type ConsumerServerConfig record {
    Transport transportName = "idoc";
    string serverName = "";
    string gwServ = "";
    string progId = "";
    string repositoryDestination = "";
    string gwhost = "";
    int connectionCount = 1;
    Value unicode = 0;
    string? sncMyName = "";
    Value trace = 1;
    SncQOP sncqop = 8;
    string? sncLib = "";
};

#The destination properties configuration of SAP.
#
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
# + msServ - SAP message server port to use instead of the default SAP message server system ID
# + gwServ - Gateway service
# + r3Name - System ID of the SAP system
# + serverGroup - The group of application servers
# + useSapGui - Start a SAP GUI and associate with the connection. (0 - do not start [default], 1 start GUI,
#   2 start GUI and hide if not used)
# + codePage  - Initial login codepage in SAP notation
# + getsso2  - Whether to get an SSO ticket after logging in or not (1 or 0 [default])
# + sncPartnerName - SNC partner. E.g., p:CN=R3, O=XYZ-INC, C=EN
# + sncMode - Whether the Secure Network Connection (SNC) mode in on (1) or off (0 [default])
# + sncqop - SNC level of security (1-9, default:8)
# + sncmyName - SNC name. Overrides default SNC partner
# + sncLib - Path to the library which provides the SNC service
#   Valid values are true (yes, default) and false (no)
# + tpName - The program ID of the external server program
# + tpHost - The host of the external server program
# + hostType - Type of the remote host (3=R/3, E=External)
# + dest - The R/2 destination
public type ConsumerDestinationConfig record {
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
