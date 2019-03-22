# module-sap

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

## Building from the Source

Follow the steps below to build the Ballerina SAP endpoint from the source code:

1. Get a clone or download [the source](https://github.com/wso2-ballerina/module-sap).
2. Create a lib folder in the module-sap directory.
3. Download the sapidoc3.jar and sapjco3.jar middleware libraries from the SAP support portal and copy those 
   libraries to the module-sap/lib directory.
4. Navigate to the folder `module-sap` directory and execute the following Maven command:
    
        mvn clean install
    
    > If you want to run the test cases, please refer the [Running Tests](#running-tests). 
    
5. Extract the `/component/target/wso2-sap-<version>.zip` distribution. 
6. Execute either of the install.{sh/bat} scripts to install the module.

You can uninstall the module by executing either of the uninstall.{sh/bat} scripts.    
 
## Running Tests

* Obtain the following SAP credential and update these value in the `module-sap/component/src/test/resources/producer/sap_bapi_producer.bal` and `module-sap/component/src/test/resources/producer/sap_idoc_producer.bal`.
    ````
        destinationName:"The name of the SAP gateway",
        logonClient:"The SAP client ID (usually a number) used to connect to the SAP system",
        userName:"Username of an authorized SAP user",
        password:"Password credential of an authorized SAP user",
        asHost:"The SAP endpoint",
        sysnr:"System number used to connect to the SAP system",
        language:"The language to use for the SAP connection. For example, en for English"
   ````
* If you want to update the sample IDoc content, update it in the `module-sap/component/src/test/resources/producer/sap_idoc_producer.bal`.
    ````
      xml idoc = xml `<IDoc/>`
    ````
* If you want to update the sample BAPI content, update it in the `module-sap/component/src/test/resources/producer/sap_bapi_producer.bal`.
    ````
      xml bapiData = xml `<BAPI/>`;
    ```` 
* Navigate to the folder `module-sap` directory and execute the following Maven command:

        mvn clean install -DskipTests=false
    
## Samples

### SAP Producer

The following example demonstrates how to publish an IDoc to SAP.

```ballerina
import wso2/sap;
import ballerina/io;

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

int idocVersion = 3;
xml idoc = xml `<_-DSD_-ROUTEACCOUNT_CORDER002>
                    <IDOC BEGIN="1">
                        <EDI_DC40 SEGMENT="1">
                        </EDI_DC40>
                    </IDOC>
                 </_-DSD_-ROUTEACCOUNT_CORDER002>`; 

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
xml bapi = xml `<BAPI_DOCUMENT_GETLIST></BAPI_DOCUMENT_GETLIST>`;
public function main() {
    var result = sapProducer->sendBapiMessage(bapi , false, false);
    if (result is error) {
        io:println("Error: ", result.reason());
    } else {
        io:println("Response: ", result);
    }
}
```

>Supported BAPI structure
       
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

### SAP Consumer

```ballerina
import wso2/sap;
import ballerina/io;

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
});
service SapConsumerTest on consumerEP {
    // The `resource` registered to receive server messages
    resource function onMessage(string idoc) {
        io:println("The message received from SAP instance : " + idoc);
    }

    // The `resource` registered to receive server error messages
    resource function onError(error err) {
        io:println("Error from Connector: " + err.reason());
    }
}
```
