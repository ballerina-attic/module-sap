import ballerina/config;
import ballerina/io;
import ballerina/test;

ProducerConfig producerConfigs = {
    destinationName: config:getAsString("DESTINATION_NAME"),
    sapclient: config:getAsString("SAP_CLIENT"),
    username: config:getAsString("USERNAME"),
    password: config:getAsString("PASSWORD"),
    ashost: config:getAsString("ASHOST"),
    sysnr: config:getAsString("SYSNR"),
    language: config:getAsString("LANGUAGE")
};

Producer sapProducer = new (producerConfigs);

# Test tRFC BAPI outbound request.
@test:Config {}
function testBapiSend() {
    xml bapi = xml `<BAPI_DOCUMENT_GETLIST></BAPI_DOCUMENT_GETLIST>`;
    io:println("Testing Outbound BAPI requests.");
    var result = sapProducer->sendBapi(bapi, true);
    if (result is error) {
        test:assertTrue(false, msg = "Failed!");
    } else {
        test:assertTrue(true, msg = "Passed!");
    }
}

# Test outbound IDOC message.
@test:Config {}
function testIdocSend() {
    int idocVersion = 3;
    xml idoc = xml `<_-DSD_-ROUTEACCOUNT_CORDER002>
                             <IDOC BEGIN="1">
                                <EDI_DC40 SEGMENT="1">
                                   <TABNAM>EDI_DC40</TABNAM>
                                   <MANDT>405</MANDT>
                                   <DOCREL>700</DOCREL>
                                   <STATUS>30</STATUS>
                                   <DIRECT>1</DIRECT>
                                   <OUTMOD>2</OUTMOD>
                                   <IDOCTYP>/DSD/ROUTEACCOUNT_CORDER002</IDOCTYP>
                                   <MESTYP>/DSD/ROUTEACCOUNT_CORDER0</MESTYP>
                                   <STDMES>/DSD/R</STDMES>
                                   <SNDPOR>SAPCCR</SNDPOR>
                                   <SNDPRT>LS</SNDPRT>
                                   <SNDPRN>WSO2ESB</SNDPRN>
                                   <RCVPOR>SAP_GW_IDO</RCVPOR>
                                   <RCVPRT>LS</RCVPRT>
                                   <RCVPRN>WSO2ESB</RCVPRN>
                                   <CREDAT>20160816</CREDAT>
                                   <CRETIM>132507</CRETIM>
                                </EDI_DC40>
                                <_-DSD_-E1BPRAGENERALHD SEGMENT="1">
                                   <TOUR_ID>2</TOUR_ID>
                                   <MISSION_ID>2</MISSION_ID>
                                </_-DSD_-E1BPRAGENERALHD>
                             </IDOC>
                          </_-DSD_-ROUTEACCOUNT_CORDER002>`;
    io:println("Testing Outbound IDOC messages.");
    var result = sapProducer->sendIdoc(idoc, idocVersion);
    if (result is error) {
        io:println(result);
        test:assertTrue(false, msg = "Failed!");
    } else {
        test:assertTrue(true, msg = "Passed!");
    }
}
