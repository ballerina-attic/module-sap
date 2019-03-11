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

# Represents a SAP service listener .
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

# The server properities configuration for Sap.
#
# + transportName - The protocol name
# + serverName - Name of the server configuration.This needs to be the same name provided in the SAP configuration.
# + gwhost - Gateway host on which the server should be registered
# + progId - The program ID with which the registration is done
# + gwServ - Gateway service, i.e. the port on which a registration can be done
# + connectionCount - The number of connections that should be registered at the gateway
# + trace - Enable/disable RFC trace (1 or 0)
# + repositoryDestination - Name of the .dest file. For example, if the .dest file is SAPSYS01.dest ,
#                            set this to SAPSYS01 .
# + unicode - Determines whether or not you connect in unicodemode (true, false)
# + sncMyName - SNC name of your server. Overrides the default SNC name. Typically something like
#   p:CN=JCoServer, O=ACompany, C=EN
# + sncqop - SNC level of security, 1 to 9
# + sncLib - Path to library which provides SNC service.
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

# The desination properities configuration for Sap.
#
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
# + tpName - The program ID of external server program
# + tpHost - The host of external server program
# + hostType - Type of remote host (3=R/3, E=External)
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
    Value trace = 1;
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
