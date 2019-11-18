import ballerina/config;
import ballerina/io;

listener Listener idocConsumer = new (
    {
        transportName: <Transport>config:getAsString("TRANSPORT_NAME"),
        serverName: config:getAsString("SERVER_NAME"),
        gwhost: config:getAsString("GWHOST"),
        progid: config:getAsString("PROGRAM_ID"),
        repositorydestination: config:getAsString("REPOSITORY_DESTINATION"),
        gwserv: config:getAsString("GWSERVER"),
        unicode: <Value>config:getAsInt("UNICODE")
    },
    {
        sapclient: config:getAsString("SAP_CLIENT"),
        username: config:getAsString("USERNAME"),
        password: config:getAsString("PASSWORD"),
        ashost: config:getAsString("ASHOST"),
        sysnr: config:getAsString("SYSNR"),
        language: config:getAsString("LANGUAGE")
    }
);

service SapConsumerTest on idocConsumer {
    // The `resource` registered to receive server messages
    resource function onMessage(string idoc) {
        io:println("The message received from SAP instance: " + idoc);
    }

    // The `resource` registered to receive server error messages
    resource function onError(error err) {
        io:println("Error from Connector: " + err.reason());
    }
}