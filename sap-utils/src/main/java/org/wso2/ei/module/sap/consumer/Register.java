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
import org.ballerinalang.jvm.values.HandleValue;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.jvm.values.ObjectValue;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.module.sap.utils.BallerinaSapException;

import javax.xml.bind.Marshaller;

import static org.wso2.ei.module.sap.utils.SapConstants.RESOURCE_ON_ERROR;
import static org.wso2.ei.module.sap.utils.SapConstants.RESOURCE_ON_MESSAGE;
import static org.wso2.ei.module.sap.utils.SapConstants.SAP_RESOURCE;

/**
 * Registers a listener to the SAP Service.
 */
public class Register {

    private static Logger log = LoggerFactory.getLogger("ballerina");

    /**
     * Registers a listener to the SAP Service by adding resources to a MapValue.
     *
     * @param consumer The consumer object through which the connection is created.
     */
    public static ObjectValue register(ObjectValue consumer, ObjectValue service) throws BallerinaSapException {

        BRuntime runtime = BRuntime.getCurrentRuntime();
//        MapValue consumer = (MapValue) consumer1;
        Map<String, AttachedFunction> sapResources = getResourceMap(consumer, service);
        MapValue listenerEndpoint = (MapValue) consumer;
        //Check the resources already add or not to the Struct.
        if (consumer.getNativeData(SAP_RESOURCE) == null) {
            consumer.addNativeData(SAP_RESOURCE, sapResources);
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

//        Service service = (Service) service1;
        Map<String, AttachedFunction> registry = new HashMap<>(2);
//        for (AttachedFunction resource : (AttachedFunction[]) service.getResources()) {
//            switch (resource.getName()) {
////                case RESOURCE_ON_ERROR:
////                    log.info("Register6");
////                    registry.put(RESOURCE_ON_ERROR, resource);
////                    break;
//                case RESOURCE_ON_MESSAGE:
//                    log.info("Register7");
//                    registry.put(RESOURCE_ON_MESSAGE, resource);
//                    break;
//                default:
//                    log.info("Register8");
//                    throw new BallerinaSapException("One of these name [" + RESOURCE_ON_ERROR + ", "
//                            + RESOURCE_ON_MESSAGE + "] should be used to register the resource in the SAP service");
//            }
//        }
        return registry;
    }
}
