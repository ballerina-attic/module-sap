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

import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerTIDHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Hashtable;
import java.util.Map;

/**
 * <code>CustomServerTIDHandler </code> provides a default implementation of the SAP transaction ID handling
 *  mechanism.
 */
public class CustomServerTIDHandler implements JCoServerTIDHandler {

    private static Log log = LogFactory.getLog(CustomServerTIDHandler.class);

    private enum TIDState {

        CREATED, EXECUTED, COMMITTED, ROLLED_BACK, CONFIRMED;
    }

    Map<String, TIDState> availableTIDs = new Hashtable<String, TIDState>();

    @Override
    public boolean checkTID(JCoServerContext serverCtx, String tid) {

        // This example uses a Hash table to store status information. But usually
        // you would use a database. If the DB is down, throw a RuntimeException at
        // this point. JCo will then abort the R/3 backend will try again later.
        log.info("TID Handler: checkTID for " + tid);
        TIDState state = availableTIDs.get(tid);
        if (state == null) {
            availableTIDs.put(tid, TIDState.CREATED);
            return true;
        }

        if (state == TIDState.CREATED || state == TIDState.ROLLED_BACK) {
            return true;
        }
        return false;
        // "true" means that JCo will now execute the transaction, "false" means
        // that we have already executed this transaction previously, so JCo will
        // skip the handleRequest() step and will immediately return an OK code to R/3.
    }

    @Override
    public void commit(JCoServerContext serverCtx, String tid) {

        log.info("TID Handler: commit for " + tid);
        // react on commit e.g. commit on the database
        // if  necessary throw a RuntimeException, if  the commit was not
        // possible
        availableTIDs.put(tid, TIDState.COMMITTED);
    }

    @Override
    public void confirmTID(JCoServerContext arg0, String tid) {

        log.info("TID Handler: confirmTID for " + tid);
        try {
            // clean up the resources
        } finally {
            availableTIDs.remove(tid);
        }
    }

    /**
     *  React rollback on the database
     * @param serverCtx Jco server context
     * @param tid The transaction ID
     */
    @Override
    public void rollback(JCoServerContext serverCtx, String tid) {

        log.info("TID Handler: rollback for " + tid);
        availableTIDs.put(tid, TIDState.ROLLED_BACK);
    }

    /**
     *
     * @param serverCtx Jco server context
     */
    public void execute(JCoServerContext serverCtx) {

        String tid = serverCtx.getTID();
        if (tid != null) {
            log.info("TID Handler: execute for " + tid);
            availableTIDs.put(tid, TIDState.EXECUTED);
        }
    }
}
