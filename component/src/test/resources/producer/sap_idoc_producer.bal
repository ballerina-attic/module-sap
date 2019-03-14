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

import wso2/sap;

sap:ProducerConfig producerConfigs = {
    destinationName:"The name of the SAP gateway",
    logonClient:"The SAP client ID (usually a number) used to connect to the SAP system",
    userName:"Username of an authorized SAP user",
    password:"Password credential of an authorized SAP user",
    asHost:"The SAP endpoint",
    sysnr:"System number used to connect to the SAP system",
    language:"The language to use for the SAP connection. For example, en for English"
};

int idocVersion = 3;
xml idoc = xml `<_-DSD_-ROUTEACCOUNT_CORDER002>
                    <IDOC BEGIN="1">
                        <EDI_DC40 SEGMENT="1">
                        </EDI_DC40>
                    </IDOC>
                 </_-DSD_-ROUTEACCOUNT_CORDER002>`;
function funcSapIDocProducer() returns string|error {
    sap:Producer sapProducer = new(producerConfigs);
    return sapProducer->sendIdocMessage(idocVersion, idoc);
}
