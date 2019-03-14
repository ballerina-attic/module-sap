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

package org.ballerinalang.sap.nativeimpl.producer;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ballerinalang.sap.utils.SapConstants;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.util.Iterator;

import static org.ballerinalang.sap.utils.SapConstants.FIELD;
import static org.ballerinalang.sap.utils.SapConstants.IMPORT;
import static org.ballerinalang.sap.utils.SapConstants.STRUCTURE;
import static org.ballerinalang.sap.utils.SapConstants.TABLE;
import static org.ballerinalang.sap.utils.SapConstants.TABLES;

/** This contains the required methods that can be used to parse a
 * meta data description of a BAPI/RFC call.
 * Thus, the BNF grammar of the metadata would look like below:
 *  BAPI Function   -> import | tables | both
 *  import    -> structure | field | both
 *  structure -> 1 or more fields
 *  tables    -> 1 or more table
 *  table     -> row
 *  row       -> 1 or more fields
 *  field     -> name and value
 *
 */
public class RFCMetaDataParser {

    private static Log log = LogFactory.getLog(RFCMetaDataParser.class);

    /**
     * Process the document.
     * @param document The document node
     * @param function The RFC function to be executed
     */
    static void processMetaDataDocument(OMElement document, JCoFunction function) {
        Iterator itr = document.getChildElements();
        while (itr.hasNext()) {
            OMElement childElement = (OMElement) itr.next();
            processElement(childElement, function);
        }
    }

    /**
     * Process every element in the document.
     * @param element The OMElement
     * @param function The RFC function to be executed.
     */
    private static void processElement(OMElement element, JCoFunction function) {
        String qname = element.getQName().toString();
        if (qname != null) {
            if (qname.equals(IMPORT)) {
                processImport(element, function);
            } else if (qname.equals(TABLES)) {
                processTables(element, function);
            } else {
                log.warn("Unknown meta data type tag :" + qname + " detected. " +
                        "This meta data element will be discarded!");
            }
        }
    }

    /**
     * Process the import function.
     * @param element The OMElement
     * @param function The RFC function to be executed
     */
    private static void processImport(OMElement element, JCoFunction function) {
        Iterator itr = element.getChildElements();
        while (itr.hasNext()) {
            OMElement childElement = (OMElement) itr.next();
            String qname = childElement.getQName().toString();
            String name = childElement.getAttributeValue(SapConstants.NAME_Q);
            switch (qname) {
                case STRUCTURE:
                    processStructure(childElement, function, name);
                    break;
                case FIELD:
                    processField(childElement, function, name);
                    break;
                default:
                    log.warn("Unknown meta data type tag :" + qname + " detected. " +
                            "This meta data element will be discarded!");
            }
        }
    }

    /**
     * Process the structure under the import function.
     * @param element The OMElement
     * @param function The RFC function to be executed
     * @param strcutName Name of the structure
     */
    private static void processStructure(OMElement element, JCoFunction function, String strcutName) {
        if (strcutName == null) {
            throw new BallerinaException("A structure should have a name!");
        }
        JCoStructure jcoStrcture = function.getImportParameterList().getStructure(strcutName);
        if (jcoStrcture != null) {
            Iterator itr = element.getChildElements();
            boolean isRecordFound = false;
            while (itr.hasNext()) {
                OMElement childElement = (OMElement) itr.next();
                String qname = childElement.getQName().toString();
                if (qname.equals(FIELD)) {
                    String fieldName = childElement.getAttributeValue(SapConstants.NAME_Q);
                    String fieldValue = childElement.getText();
                    for (JCoField field : jcoStrcture) {
                        if (fieldName != null && fieldName.equals(field.getName())) {
                            isRecordFound = true;
                            field.setValue(fieldValue);
                            break;
                        }
                    }
                    if (!isRecordFound) {
                        throw new BallerinaException("Invalid configuration! The field : " + fieldName + "" +
                                " did not find the the strcture : " + strcutName);
                    }
                } else {
                    log.warn("Invalid meta data type element found : " + qname + " .This meta data " +
                            "type will be ignored");
                }
            }
        } else {
            log.error("Didn't find the specified structure : " + strcutName + " on the RFC" +
                    " repository. This structure will be ignored");
        }
    }

    /**
     * Process the field.
     * @param element The OMElement
     * @param function The RFC function to execute
     * @param fieldName The field name
     */
    private static void processField(OMElement element, JCoFunction function, String fieldName) {
        if (fieldName == null) {
            throw new BallerinaException("A field should have a name!");
        }
        String fieldValue = element.getText();
        if (fieldValue != null) {
            function.getImportParameterList().setValue(fieldName, fieldValue);
        }
    }

    /**
     * Process the tables.
     * @param element The OMElement
     * @param function The RFC function to be execute
     */
    private static void processTables(OMElement element, JCoFunction function) {
        Iterator itr = element.getChildElements();
        while (itr.hasNext()) {
            OMElement childElement = (OMElement) itr.next();
            String qname = childElement.getQName().toString();
            String tableName = childElement.getAttributeValue(SapConstants.NAME_Q);
            if (qname.equals(TABLE)) {
                processTable(childElement, function, tableName);
            } else {
                log.warn("Invalid meta data type element found : " + qname + " .This meta data " +
                            "type will be ignored");
            }
        }
    }

    /**
     * Process the table
     * @param element The OMElement
     * @param function The RFC function to execute
     */
    private static void processTable(OMElement element, JCoFunction function, String tableName) {
        JCoTable inputTable = function.getTableParameterList().getTable(tableName);
        if (inputTable == null) {
            throw new BallerinaException("Input table :" + tableName + " does not exist");
        }
        Iterator itr = element.getChildElements();
        while (itr.hasNext()) {
            OMElement childElement = (OMElement) itr.next();
            String qname = childElement.getQName().toString();
            String id = childElement.getAttributeValue(SapConstants.ID_Q);
            if (qname.equals("row")) {
                processRow(childElement, inputTable, id);
            } else {
                log.warn("Invalid meta data type element found : " + qname + " .This meta data " +
                            "type will be ignored");
            }
        }
    }

    /**
     * Process the table.
     * @param element The OMElement
     * @param table The RFC function to be executed
     * @param id The ID of the row
     */
    private static void processRow(OMElement element, JCoTable table, String id) {
        int rowId;
        try {
            rowId = Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            log.warn("Row ID should be a integer, found " + id + ". Skipping row", ex);
            return;
        }
        if (table.getNumRows() <= rowId) {
            //which mean this is a new row
            table.appendRow();
        } else {
            table.setRow(rowId);
        }

        Iterator itr = element.getChildElements();
        while (itr.hasNext()) {
            OMElement childElement = (OMElement) itr.next();
            String qname = childElement.getQName().toString();
            if (qname != null && qname.equals(FIELD)) {
                processField(childElement, table);
            } else {
                log.warn("Invalid meta data type element found : " + qname + " .This meta data " +
                        "type will be ignored");
            }
        }
    }

    /**
     * Process the field.
     * @param element The OMElement
     * @param table The RFC function to be executed
     */
    private static void processField(OMElement element, JCoTable table) {
        String fieldName = element.getAttributeValue(SapConstants.NAME_Q);
        String fieldValue = element.getText();
        if (fieldName == null) {
            throw new BallerinaException("A field should have a name!");
        }
        if (fieldValue != null) {
            table.setValue(fieldName, fieldValue);
        }
    }
}
