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

import ballerina/io;
import ballerina/ 'lang\.object as lang;
import ballerinax/java;

# Represents a SAP service Listener.
#
# + serverConfig - SAP endpoint parameters when the external SAP endpoint is configured as a server.
# + destinationConfig - SAP endpoint parameters when the external SAP endpoint is configured as a client.
public type Listener object {
    *lang:Listener;

    public ConsumerServerConfig serverConfig = {};
    public ConsumerDestinationConfig destinationConfig = {};
    handle sapConsumer = java:createNull();

    public function __init(ConsumerServerConfig servConfig, ConsumerDestinationConfig destConfig) {
        self.serverConfig = servConfig;
        self.destinationConfig = destConfig;
        handle | error initResult = initListener(self);
        if (initResult is handle) {
            self.sapConsumer = initResult;
        } else {
            io:println("Error occured while initializing the listener!" + initResult.toString());
        }
    }

    public function __start() returns error? {
        handle | error startResult = startListener(self.sapConsumer);
        if (startResult is handle) {
            self.sapConsumer = startResult;
        } else {
            io:println("Error occured while starting the listener!" + startResult.toString());
            return startResult;
        }
    }

    public function __stop() returns error? {
        handle | error stopResult = stopListener(self.sapConsumer);
        if (stopResult is handle) {
            self.sapConsumer = stopResult;
        } else {
            io:println("Error occured while stopping the listener!" + stopResult.toString());
            return stopResult;
        }
    }

    public function __attach(service s, string? name = ()) returns error? {
        handle | error attachResult = attachListener(s, name);
        if (attachResult is handle) {
            self.sapConsumer = attachResult;
        } else {
            io:println("Error occured while registering the listener!" + attachResult.toString());
            return attachResult;
        }
    }

    public function __gracefulStop() returns error? {
        handle | error stopResult = stopListener(self.sapConsumer);
        if (stopResult is handle) {
            self.sapConsumer = stopResult;
        } else {
            io:println("Error occured while stopping the listener!" + stopResult.toString());
            return stopResult;
        }
    }

    public function __immediateStop() returns error? {

    }

    public function __detach(service s) returns error? {

    }
};

function initListener(Listener consumer) returns handle | error = @java:Method {
    name: "consumerInit",
    class: "org.wso2.ei.module.sap.consumer.Init"
} external;

function startListener(handle consumer) returns handle | error = @java:Method {
    name: "start",
    class: "org.wso2.ei.module.sap.consumer.Start"
} external;

function stopListener(handle consumer) returns handle | error = @java:Method {
    name: "stop",
    class: "org.wso2.ei.module.sap.consumer.Stop"
} external;

function attachListener(service s, any name) returns handle | error = @java:Method {
    name: "register",
    class: "org.wso2.ei.module.sap.consumer.Register"
} external;

# The server properties configuration of SAP.
#
# + transportName - Name of the protocol
# + serverName - Name of the server configuration. This needs to be the same name that is provided in the SAP configuration.
# + gwhost - Gateway host on which the server should be registered
# + progid - The program ID with which the registration is done
# + gwserv - Gateway service( i.e., the port on which a registration can be done)
# + connectioncount - The number of connections that should be registered with the gateway. default: 1
# + trace - Enable/disable the RFC trace (1 or 0 [default])
# + repositorydestination - Name of the repository.
# + unicode - Determines whether you connect in Unicode mode or not (true, false)
# + sncmyname - SNC name of your server. This overrides the default SNC name. Typically, something like
#   p:CN=JCoServer, O=ACompany, C=EN
# + sncqop - SNC level of security, 1 to 9
# + snclib - Path to the library, which provides the SNC service.
public type ConsumerServerConfig record {
    Transport transportName = "idoc";
    string serverName = "";
    string gwserv = "";
    string progid = "";
    string repositorydestination = "";
    string gwhost = "";
    int connectioncount = 1;
    Value unicode = 0;
    string? sncmyname = "";
    Value trace = 1;
    SncQOP sncqop = 8;
    string? snclib = "";
};

#The destination properties configuration of SAP.
#
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
# + msserv - SAP message server port to use instead of the default SAP message server system ID
# + gwserv - Gateway service
# + r3name - System ID of the SAP system
# + servergroup - The group of application servers
# + usesapgui - Start a SAP GUI and associate with the connection. (0 - do not start [default], 1 start GUI,
#   2 start GUI and hide if not used)
# + codepage  - Initial login codepage in SAP notation
# + getsso2  - Whether to get an SSO ticket after logging in or not (1 or 0 [default])
# + sncpartnername - SNC partner. E.g., p:CN=R3, O=XYZ-INC, C=EN
# + sncmode - Whether the Secure Network Connection (SNC) mode in on (1) or off (0 [default])
# + sncqop - SNC level of security (1-9, default:8)
# + sncmyname - SNC name. Overrides default SNC partner
# + snclib - Path to the library which provides the SNC service
#   Valid values are true (yes, default) and false (no)
# + tpname - The program ID of the external server program
# + tphost - The host of the external server program
# + hosttype - Type of the remote host (3=R/3, E=External)
# + dest - The R/2 destination
public type ConsumerDestinationConfig record {|
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
