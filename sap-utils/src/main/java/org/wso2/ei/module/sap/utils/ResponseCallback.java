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

package org.ballerinalang.sap.utils;

import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.model.values.BError;

/**
 *  This represents a callback to report back a success or a
 *  failure state back to the originator.
 */
public class ResponseCallback implements CallableUnitCallback {

    /**
     * This should be called when you want to notify that your operation
     * is done successfully.
     */
    public void notifySuccess() {
        // Skip this
    }

    /**
     * This should be called to notify the listener that your operation
     * failed with a specific error.
     *
     * @param error the error to be reported when the operation failed
     */
    public void notifyFailure(BError error) {
        // Skip this
    }
}
