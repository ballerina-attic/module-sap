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
    destinationName: "<The SAP gateway name>",
    ^"client: "<SAP client, for example, 001>",
    username: "<The user logon>",
    password: "<The logon password>",
    ashost: "<The R/3 application server>",
    sysnr: "<SAP system number, for example, 01>",
    language: "<The logon language>"
};

xml bapi = xml `<BAPI_DOCUMENT_GETLIST></BAPI_DOCUMENT_GETLIST>`;
function funcSapBAPIProducer() returns string|error {
    sap:Producer sapProducer = new(producerConfigs);
    return sapProducer->sendBapiMessage(bapi, false, false);
}
