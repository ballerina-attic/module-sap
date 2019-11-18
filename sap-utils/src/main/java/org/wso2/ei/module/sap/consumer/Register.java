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

package org.wso2.ei.module.sap.consumer;

import java.util.HashMap;
import java.util.Map;

import org.ballerinalang.jvm.BRuntime;
import org.ballerinalang.jvm.types.AttachedFunction;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.jvm.values.ObjectValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.module.sap.utils.BallerinaSapException;
import org.wso2.ei.module.sap.utils.SapConstants;

/**
 * Registers a listener to the SAP Service.
 */
public class Register {

    private static Logger log = LoggerFactory.getLogger(SapConstants.BALLERINA);

    /**
     * Registers a listener to the SAP Service by adding resources to a MapValue.
     *
     * @param consumer The consumer object through which the connection is created.
     */
    public static ObjectValue register(ObjectValue consumer, ObjectValue service) throws BallerinaSapException {

        BRuntime runtime = BRuntime.getCurrentRuntime();
        Map<String, AttachedFunction> sapResources = getResourceMap(consumer, service);
        MapValue listenerEndpoint = (MapValue) consumer;
        //Check the resources already add or not to the Struct.
        if (consumer.getNativeData(SapConstants.SAP_RESOURCE) == null) {
            consumer.addNativeData(SapConstants.SAP_RESOURCE, sapResources);
            return consumer;
        } else {
            throw new BallerinaSapException("Stop the running listener.");
        }
    }

    /**
     * Validate the resources and set those into the map.
     *
     * @param consumer The consumer object through which the connection is created.
     * @return Returns the resource map.
     */
    private static Map<String, AttachedFunction> getResourceMap(ObjectValue consumer, ObjectValue service1) throws BallerinaSapException {
        Map<String, AttachedFunction> registry = new HashMap<>(2);
        return registry;
    }
}
