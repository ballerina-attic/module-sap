/*
 * Copyright (c) 2019, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http:www.apache.orglicensesLICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.sap.nativeimpl.consumer;

import com.sap.conn.idoc.jco.JCoIDocServer;
import com.sap.conn.jco.server.DefaultServerHandlerFactory;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.sap.utils.SapConstants;

import java.util.Map;

import static org.ballerinalang.sap.utils.SapConstants.CONSUMER_SERVER_CONNECTOR_NAME;
import static org.ballerinalang.sap.utils.SapConstants.CONSUMER_TRANSPORT_NAME;
import static org.ballerinalang.sap.utils.SapConstants.SAP_SERVICE;

/**
 * Star the server connector.
 */
@BallerinaFunction(
    orgName = SapConstants.ORG_NAME,
    packageName = SapConstants.FULL_PACKAGE_NAME,
    functionName = "start",
    receiver = @Receiver(
            type = TypeKind.OBJECT,
            structType = SapConstants.CONSUMER_STRUCT_NAME,
            structPackage = SapConstants.SAP_NATIVE_PACKAGE
    ),
    isPublic = true
)
public class Start extends BlockingNativeCallableUnit {
    private static Log log = LogFactory.getLog(Start.class);

    @Override
    public void execute(Context context) {

        Struct consumerStruct = BLangConnectorSPIUtil.getConnectorEndpointStruct(context);
        Map<String, Resource> sapService = (Map<String, Resource>) consumerStruct.getNativeData(SAP_SERVICE);
        String transportName = (String) consumerStruct.getNativeData(CONSUMER_TRANSPORT_NAME);

        if (transportName.equalsIgnoreCase(SapConstants.SAP_BAPI_PROTOCOL_NAME)) {
            DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory
                    .FunctionHandlerFactory();
            JCoServerFunctionHandler rfcConnectionHandler = new RFCConnectionHandler(sapService);
            factory.registerGenericHandler(rfcConnectionHandler);
            JCoServer jcoServer = (JCoServer) consumerStruct.getNativeData(CONSUMER_SERVER_CONNECTOR_NAME);
            jcoServer.setCallHandlerFactory(factory);
            log.info("JCo Server start...........");
            jcoServer.start();
            context.setReturnValues();
        } else {
            JCoIDocServer jcoIDocServer = (JCoIDocServer) consumerStruct.getNativeData(CONSUMER_SERVER_CONNECTOR_NAME);
            IDocHandlerFactory iDocHandlerFactory = new IDocHandlerFactory(sapService);
            jcoIDocServer.setIDocHandlerFactory(iDocHandlerFactory);
            log.info("IDoc Server start...........");
            jcoIDocServer.start();
            context.setReturnValues();
        }
    }
}
