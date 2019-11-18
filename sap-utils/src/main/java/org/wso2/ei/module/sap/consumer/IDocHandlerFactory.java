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

package org.wso2.ei.module.sap.consumer;

import com.sap.conn.idoc.IDocDocumentList;
import com.sap.conn.idoc.IDocXMLProcessor;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.idoc.jco.JCoIDocHandler;
import com.sap.conn.idoc.jco.JCoIDocHandlerFactory;
import com.sap.conn.idoc.jco.JCoIDocServerContext;
import com.sap.conn.jco.server.JCoServerContext;
import org.ballerinalang.jvm.XMLFactory;
import org.ballerinalang.jvm.types.AttachedFunction;
import org.ballerinalang.jvm.values.XMLValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.module.sap.utils.ResponseCallback;
import org.wso2.ei.module.sap.utils.SapConstants;
import org.wso2.ei.module.sap.utils.SapUtils;

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

    private static Logger log = LoggerFactory.getLogger(SapConstants.BALLERINA);
    private Map<String, AttachedFunction> sapResource;

    IDocHandlerFactory(Map<String, AttachedFunction> sapService) {

        this.sapResource = sapService;
    }

    public JCoIDocHandler getIDocHandler(JCoIDocServerContext serverCtx) {

        return new JCoIDocHandlerImplementation(sapResource);
    }

    class JCoIDocHandlerImplementation implements JCoIDocHandler {

        private ResponseCallback callback;
        private Map<String, AttachedFunction> sapService;

        JCoIDocHandlerImplementation(Map<String, AttachedFunction> sapService) {

            log.info("IDocDocumentList type: JCoIDocHandlerImplementation ");
            this.sapService = sapService;
            callback = new ResponseCallback();
        }

        public void handleRequest(JCoServerContext serverCtx, IDocDocumentList idocList) {

            if (log.isDebugEnabled()) {
                log.debug("New IDoc received");
            }
            log.info("IDocDocumentList type: " + idocList.getIDocType());
            IDocXMLProcessor xmlProcessor = JCoIDoc.getIDocFactory().getIDocXMLProcessor();
            String xmlString = xmlProcessor.render(idocList);
            StringReader reader = new StringReader(xmlString);
            XMLValue xml = XMLFactory.parse(reader);
            SapUtils.invokeOnIdocMessage(xml, sapService, callback);
        }
    }
}
