## Package overview

Ballerina SAP Client Endpoint is used to connect Ballerina with SAP. With this SAO Client Endpoint, Ballerina can act as SAP Consumers and SAP Producers.
It has full IDoc and experimental BAPI support. It uses the SAP JCO library as the underlying framework to communicate with SAP. 

## Steps to Configure

 * Install the Java JDK 1.8
 * Download the sapidoc3.jar and sapjco3.jar middleware libraries from the SAP support portal and copy those libraries 
   to the <BRE_HOME>/bre/lib/ directory.
 * Download the native SAP JCo library and copy it to the system path. You need to select the system path applicable 
   to your operating system as described below.
    
    <table class="tg">
      <tr>
        <td class="tg-yw4l">Linux 32-bit</td>
        <td class="tg-yw4l">Copy the Linux native SAP jcolibrary libsapjco3.so to <JDK_HOME>/jre/lib/i386/server.</td>
      </tr>
      <tr>
        <td class="tg-yw4l">Linux 64-bit</td>
        <td class="tg-yw4l">Copy the Linux native SAP jcolibrary libsapjco3.so to <JDK_HOME>/jre/lib/amd64.</td>
      </tr>
      <tr>
        <td class="tg-yw4l">Windows</td>
        <td class="tg-yw4l">Copy the Windows native SAP jcolibrary sapjco3.dll to <WINDOWS_HOME>/system32.
        </td>
      </tr>
    </table>
    
 * Extract wso2-sap-<version>.zip and  Run the install.sh script to install the module.
  
   You can uninstall the module by running uninstall.sh.

Building From the Source
==================================
If you want to build Ballerina sap endpoint from the source code:

1. Get a clone or download the source from this repository:
    [https://github.com/wso2-ballerina/module-sap](https://github.com/wso2-ballerina/module-sap)
2. Create lib folder into the module-sap 
3. Download the sapidoc3.jar and sapjco3.jar middleware libraries from the SAP support portal and copy those 
   libraries to the module-sap/lib
4. Run the following Maven command from the ballerina directory:
    mvn clean install
5. Extract the distribution created at `/component/target/wso2-sap-<version>.zip`. Run the install.{sh/bat} script to install the module.

You can uninstall the module by running uninstall.{sh/bat}.    
    
   
## Samples

### Sample SAP client endpoint 

```ballerina

sap:ProducerConfig producerConfigs = {
    destinationName:"<The SAP gateway name>",
    sapClient:"<SAP client, for example, 001>",
    userName:"<The user logon>",
    password:"<The logon password>",
    asHost:"<The R/3 application server>",
    sysnr:"<SAP system number, for example, 01>",
    language:"<The logon language>"
};

sap:Producer sapProducer = new(producerConfigs);
```

#### Sample SAP client operations

Following example demonstrates a way to publish a bapi to sap.

```ballerina
string qname = "_-DSD_-ROUTEACCOUNT_CORDER002";
int idocVersion = The version of IDoc;
xml idoc = xml `<IDoc Data>`; 
NOTE: Here, If Any tag in the IDoc start with "_-", then you have to intialise that tag and use it in the IDoc. 
      eg: <{{qname}}>...</{{qname}}>  

public function main() {
    var result = sapProducer-> sendIdocMessage(idocVersion, idoc);
    if (result is error) {
        io:println("Error: ", result.reason());
    } else {
        io:println("Response: ", result);
    }
}
````

Following example demonstrates a way to publish a bapi to sap.

```ballerina
xml baPiData = xml `<BAPI Data>`;
public function main() {
    var result = sapProducer-> sendBapiMessage(bapiData , false, false);
    if (result is error) {
        io:println("Error: ", result.reason());
    } else {
        io:println("Response: ", result);
    }
}
```

### Sample SAP listener endpoint

```ballerina

listener sap:Listener consumerEP = new ({
    transportName:"<The protocol name[idoc/bapi]>",
    serverName:"<Name of the server configuration>",
    gwHost:"<Gateway host on which the server should be registered>",
    progId:"<The program ID with which the registration is done>",
    repositoryDestination:"<Name of the .dest file>",
    gwServ:"<Gateway service>",
    unicode:"<Determines whether or not you connect in unicodemode>"}, {
    sapClient:"<SAP client, for example, 001>",
    userName:"<The user logon>",
    password:"<The logon password>",
    asHost:"<The R/3 application server>",
    sysnr:"<SAP system number, for example, 01>",
    language:"<The logon language>"
    }
);
```

##Sample service for the SAP listener endpoint

```ballerina
service SapConsumerTest on consumerEP {
    resource function onMessage(string idocList) {
        io:println("The message received from SAP instance : " + idocList);
    }

    resource function onError(error err) {
        io:println("Error from Connector: " + err.reason());
    }
}
```
