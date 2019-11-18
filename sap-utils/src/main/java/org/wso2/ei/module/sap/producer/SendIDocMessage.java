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

package org.wso2.ei.module.sap.producer;

import com.sap.conn.idoc.IDocDocumentList;
import com.sap.conn.idoc.IDocFactory;
import com.sap.conn.idoc.IDocParseException;
import com.sap.conn.idoc.IDocRepository;
import com.sap.conn.idoc.IDocXMLProcessor;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.jvm.values.ObjectValue;
import org.ballerinalang.jvm.values.XMLValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.module.sap.utils.BallerinaSapException;
import org.wso2.ei.module.sap.utils.SapConstants;

import static org.wso2.ei.module.sap.utils.SapConstants.NATIVE_PRODUCER;

public class SendIDocMessage {

    private static Logger log = LoggerFactory.getLogger(SapConstants.BALLERINA);

    public static String sendIdocMessage(ObjectValue producer, int idocVersion1, XMLValue content1)
            throws BallerinaSapException {

        String idocVersion = String.valueOf(idocVersion1);
        String content = String.valueOf(content1);
        MapValue producerMap = (MapValue) producer.get("producerConfig");
        JCoDestination destination = (JCoDestination) producerMap.getNativeData(NATIVE_PRODUCER);
        char setIdocVersion;
        switch (idocVersion) {
            case SapConstants.SAP_IDOC_VERSION_2:
                setIdocVersion = IDocFactory.IDOC_VERSION_2;
                break;
            case SapConstants.SAP_IDOC_VERSION_3:
                setIdocVersion = IDocFactory.IDOC_VERSION_3;
                break;
            default:
                setIdocVersion = IDocFactory.IDOC_VERSION_DEFAULT;
                break;
        }
        try {
            IDocFactory iDocFactory = JCoIDoc.getIDocFactory();
            IDocXMLProcessor processor = iDocFactory.getIDocXMLProcessor();
            IDocRepository iDocRepository = JCoIDoc.getIDocRepository(destination);
            IDocDocumentList iDocList = processor.parse(iDocRepository, content);
            String tid = destination.createTID();
            log.info("TID for outbound IDOC message: " + tid);
            JCoIDoc.send(iDocList, setIdocVersion, destination, tid);
            destination.confirmTID(tid);
            return tid;
        } catch (JCoException e) {
            throw new BallerinaSapException("Failed to connect the SAP gateway : ", e);
        } catch (IDocParseException e) {
            throw new BallerinaSapException("Error occurred when converting the data in " +
                    "as string [" + content + " ]  " + "to IDocList.", e);
        }
    }
}
