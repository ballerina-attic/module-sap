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

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ballerinalang.bre.Context;
import org.ballerinalang.sap.utils.SapConstants;

import static org.ballerinalang.sap.utils.SapUtils.createError;

/**
 * <code> TransactionHandler </code> provides the Transport-Sender implementation for SAP endpoints.
 */
public class TransactionHandler {

    private static Log log = LogFactory.getLog(TransactionHandler.class);

    /**
     * Returns the BAPI/RFC function from the SAP repository.
     * @param destination SAP JCO destination
     * @param rfcName the name of the RFC
     * @return The BAPI/RFC function
     */
    public static JCoFunction getRFCfunction(JCoDestination destination, String rfcName, Context context) {
        JCoFunction function = null;
        try {
            function = destination.getRepository().getFunction(rfcName);
        } catch (JCoException e) {
            context.setReturnValues(createError(context, "Unable to connect with the SAP system. RFC "
                    + "function:" + function + e.toString()));
        }
        return function;
    }

    /**
     * Evaluate the BAPI/RFC function in a remote R/* system.
     * @param function The BAPI/RFC function
     * @param destination The JCo destination
     * @return the result of the function execution
     */
    public static String evaluateRFCfunction(JCoFunction function, JCoDestination destination, Context context) {
        try {
            function.execute(destination);
        } catch (JCoException e) {
            context.setReturnValues(createError(context, "Cloud not execute the RFC function: " + function
                    + e.toString()));
            createError(context, "Cloud not execute the RFC function: " + function + e.toString());
        }
        JCoStructure returnStructure = function.getExportParameterList().getStructure(SapConstants.RETURN);
        if (!(returnStructure.getString(SapConstants.TYPE).equals("") ||
                returnStructure.getString(SapConstants.TYPE).equals(SapConstants.S) ||
                returnStructure.getString(SapConstants.TYPE).equals(SapConstants.W))) {
            context.setReturnValues(createError(context, returnStructure.getString(SapConstants.MESSAGE)));
        }
        return function.toString();
    }

    /**
     * Log in to one of the SAP CCMS system-administration interfaces to retrieve the SAP function module.
     * @param destination The JCo destination
     * @param productManufacturer The manufacturer of the product to whose CCMS system-
     *  - administration interface that is being logged in
     * @param productName The name of the product to whose CCMS system-administration interface that is being logged in
     * @param ccmsInterface The identification code of the interface
     * @param cccmsInterfaceVersion The version of the CCMS system-administration interface that expects
     *                              the external product from the R/3 System
     * @param context The context
     */
    public static void logon(JCoDestination destination, String productManufacturer, String productName,
                             String ccmsInterface, String cccmsInterfaceVersion, Context context) {
        JCoFunction logonFunction = getRFCfunction(destination, SapConstants.BAPI_XMI_LOGON, context);
        logonFunction.getImportParameterList().setValue(SapConstants.EXTCOMPANY, productManufacturer);
        logonFunction.getImportParameterList().setValue(SapConstants.EXTPRODUCT, productName);
        logonFunction.getImportParameterList().setValue(SapConstants.INTERFACE, ccmsInterface);
        logonFunction.getImportParameterList().setValue(SapConstants.XMIVERSION, cccmsInterfaceVersion);
        String logonResponse = evaluateRFCfunction(logonFunction, destination, context);
        if (log.isDebugEnabled()) {
            log.debug("BAPI XMI Logon response: " + logonResponse);
        }
    }
}
