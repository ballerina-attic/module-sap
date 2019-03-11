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
 * specif ic language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.sap.nativeimpl.consumer;

import com.sap.conn.idoc.IDocDocumentList;
import com.sap.conn.idoc.IDocXMLProcessor;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.idoc.jco.JCoIDocHandler;
import com.sap.conn.idoc.jco.JCoIDocHandlerFactory;
import com.sap.conn.idoc.jco.JCoIDocServerContext;
import com.sap.conn.jco.server.JCoServerContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.connector.api.Executor;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.model.values.BError;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.sap.utils.SapUtils;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.ballerinalang.sap.utils.SapConstants.CONSUMER_STRUCT_NAME;
import static org.ballerinalang.sap.utils.SapConstants.RESOURCE_ON_MESSAGE;
import static org.ballerinalang.sap.utils.SapConstants.SAP_MESSAGE_OBJECT;
import static org.ballerinalang.sap.utils.SapConstants.SAP_NATIVE_PACKAGE;

/**
 * This class handles IDoc calls returned from the SAP gateway.
 * <p>
 * This class encapsulates each IDoc request received.
 * handling  and converting them to String
 * </p>
 */
public class IDocHandlerFactory implements JCoIDocHandlerFactory {

    private static Log log = LogFactory.getLog(IDocHandlerFactory.class);
    private Map<String, IDocHandlerBase> handlersMap;
    private Map<String, Resource> sapService;

    public IDocHandlerFactory(Map<String, Resource> sapService) {

        handlersMap = new HashMap<String, IDocHandlerBase>();
        this.sapService = sapService;
        log.info("......IDocHandlerFactory is created.");
    }

    public boolean registerHadler(String idocType, IDocHandlerBase handler) {

        handlersMap.put(idocType, handler);
        log.info("For the IDoc type=" + idocType + " the handler " + handler.getClass() + " is registered");
        return true;
    }

    @Override
    public JCoIDocHandler getIDocHandler(JCoIDocServerContext serverCtx) {

        return new JCoIDocHandlerImplementation(sapService);
    }

    class JCoIDocHandlerImplementation implements JCoIDocHandler {

        private ResponseCallback callback;
        private IDocXMLProcessor xmlProcessor;
        private Map<String, Resource> sapService;

        public JCoIDocHandlerImplementation(Map<String, Resource> sapService) {

            this.sapService = sapService;
            callback = new ResponseCallback();
            this.xmlProcessor = JCoIDoc.getIDocFactory().getIDocXMLProcessor();
        }

        @Override
        public void handleRequest(JCoServerContext serverCtx, IDocDocumentList idocList) {

            CustomServerTIDHandler tidh = (CustomServerTIDHandler) serverCtx.getServer().getTIDHandler();
            if (log.isDebugEnabled()) {
                log.debug("New IDoc received");
            }
            try {
                Map<String, Object> properties = new HashMap<>();
                Resource onMessageResource = sapService.get(RESOURCE_ON_MESSAGE);
                ProgramFile programFile = onMessageResource.getResourceInfo().getPackageInfo().getProgramFile();
                BMap<String, BValue> consumerStruct = BLangConnectorSPIUtil.createBStruct(programFile,
                        SAP_NATIVE_PACKAGE, CONSUMER_STRUCT_NAME);
                log.info("idocList type: " + idocList.getIDocType());
                Executor.submit(onMessageResource, callback, properties, null,
                        getSignatureParameters(idocList, consumerStruct));
            } catch (BallerinaConnectorException e) {
                SapUtils.invokeOnError(sapService, callback, e.getMessage());
            }
        }

        private BMap<String, BValue> getSignatureParameters(IDocDocumentList idocList,
                                                            BMap<String, BValue> consumerStruct) {

            IDocXMLProcessor xmlProcessor = JCoIDoc.getIDocFactory().getIDocXMLProcessor();
            OutputStreamWriter osw = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
                xmlProcessor.render(idocList, osw);
            } catch (IOException e) {
                SapUtils.invokeOnError(sapService, callback, e.getMessage());
            } finally {
                try {
                    if (osw != null) {
                        osw.close();
                    }
                } catch (IOException e) {
                    throw new BallerinaException("Error while closing the stream :" + e.getMessage());
                }
            }
            consumerStruct.addNativeData(SAP_MESSAGE_OBJECT, baos.toString());
            return consumerStruct;
        }
    }

    private static class ResponseCallback implements CallableUnitCallback {

        @Override
        public void notifySuccess() {
            // do nothing
        }

        @Override
        public void notifyFailure(BError error) {
            // do nothing
        }
    }
}
