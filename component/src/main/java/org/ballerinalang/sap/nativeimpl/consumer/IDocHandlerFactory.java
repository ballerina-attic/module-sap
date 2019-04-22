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
import org.ballerinalang.bre.Context;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.model.util.XMLUtils;
import org.ballerinalang.model.values.BXML;
import org.ballerinalang.sap.utils.ResponseCallback;
import org.ballerinalang.sap.utils.SapUtils;

import java.io.PrintStream;
import java.io.StringReader;
import java.util.Map;

/**
 * This class handles IDoc calls that are returned from the SAP gateway.
 * <p>
 * This class encapsulates each IDoc request received
 * by handling  and converting them to Strings.
 * </p>
 */
public class IDocHandlerFactory implements JCoIDocHandlerFactory {

    private static Log log = LogFactory.getLog(IDocHandlerFactory.class);
    private Map<String, Resource> sapResource;
    Context context;
    private static final PrintStream console = System.out;

    IDocHandlerFactory(Map<String, Resource> sapService, Context context) {
        this.sapResource = sapService;
        this.context = context;
        log.info("......IDocHandlerFactory is created.");
    }

    public JCoIDocHandler getIDocHandler(JCoIDocServerContext serverCtx) {
        return new JCoIDocHandlerImplementation(sapResource);
    }

    class JCoIDocHandlerImplementation implements JCoIDocHandler {

        private ResponseCallback callback;
        private Map<String, Resource> sapService;

        JCoIDocHandlerImplementation(Map<String, Resource> sapService) {
            log.info("IDocDocumentList type: JCoIDocHandlerImplementation ");
            this.sapService = sapService;
            callback = new ResponseCallback();
        }

        public void handleRequest(JCoServerContext serverCtx, IDocDocumentList idocList) {
            console.println("New IDoc received");
            IDocXMLProcessor xmlProcessor = JCoIDoc.getIDocFactory().getIDocXMLProcessor();
            String xmlString = xmlProcessor.render(idocList);
            StringReader reader = new StringReader(xmlString);
            BXML xml = XMLUtils.parse(reader);
            if (log.isDebugEnabled()) {
                log.info("xmlString : " + xmlProcessor.render(idocList));
                log.info("IDocDocumentList type: " + xmlString);
            }
            SapUtils.invokeOnIdocMessage(xml, sapService, callback, context);
        }
    }
}
