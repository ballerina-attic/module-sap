## Package overview

The Ballerina SAP Client Endpoint is used to connect Ballerina with SAP. Ballerina SAP Client acts as a SAP producer while the Ballerina SAP Listener acts as a SAP Consumer.
It has full IDoc and experimental BAPI support. It uses the SAP JCO library as the underlying framework to communicate with SAP. 

## Steps to Configure

 * Install Java JDK 1.8.
 * Download the sapidoc3.jar and sapjco3.jar middleware libraries from the SAP support portal and copy those libraries 
   to the <BRE_HOME>/bre/lib/ directory.
 * Download the native SAP JCo library and copy it to the system path. You need to select the system path that is applicable 
   to your Operating System as described below.
    
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
    
 * Extract the wso2-sap-<version>.zip file and execute the install.sh script to install the module.
  
   You can uninstall the module by executing the uninstall.sh script.   
       
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

The following example demonstrates how to publish an IDoc to SAP.

```ballerina
string qname = "_-DSD_-ROUTEACCOUNT_CORDER002";
int idocVersion = >The version of IDoc>;
xml idoc = xml `<IDoc Data>`; 

public function main() {
    var result = sapProducer->sendIdocMessage(idocVersion, idoc);
    if (result is error) {
        io:println("Error: ", result.reason());
    } else {
        io:println("Response: ", result);
    }
}
````
      
The following example demonstrates how to publish a BAPI to SAP.

```ballerina
xml baPiData = xml `<BAPI Data>`;
public function main() {
    var result = sapProducer->sendBapiMessage(bapiData , false, false);
    if (result is error) {
        io:println("Error: ", result.reason());
    } else {
        io:println("Response: ", result);
    }
}
```

>Supported BAPI structure.
       
```xml
   <BAPI_FUNCTION_NAME>
       <import>
            <structure name="NAME_OF_THE_STRUCTUER">
                <field name="NAME_OF_THE_FIELD">"VALUE_OF_THE_FIELD"</field>
                <!--Can create multiple fields-->
            </structure>
            <field name="NAME_OF_THE_FIELD">"VALUE_OF_THE_FIELD"</field>
       </import>
       <tables>
            <table name="NAME_OF_THE_TABLE">
                <row id="NAME_OF_THE_ID">
                    <field name="NAME_OF_THE_FIELD">"VALUE_OF_THE_FIELD"</field>
                    <!--Can create multiple fields-->
                </row>
            </table>
            <!--Can create multiple table-->
       </tables>
   </BAPI_FUNCTION_NAME>
```
 
### Sample SAP listener endpoint

```ballerina

listener sap:Listener consumerEP = new ({
    transportName:"<The protocol name[idoc/bapi]>",
    serverName:"<Name of the server configuration>",
    gwHost:"<Gateway host on which the server should be registered>",
    progId:"<The program ID with which the registration is done>",
    repositoryDestination:"<Name of the repository>",
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
    resource function onMessage(string idoc) {
        io:println("The message received from SAP instance : " + idoc);
    }

    resource function onError(error err) {
        io:println("Error from Connector: " + err.reason());
    }
}
```