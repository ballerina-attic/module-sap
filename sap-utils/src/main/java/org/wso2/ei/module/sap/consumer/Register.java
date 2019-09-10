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

package org.ballerinalang.sap.nativeimpl.consumer;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.connector.api.Service;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.sap.utils.SapConstants;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.util.HashMap;
import java.util.Map;

import static org.ballerinalang.sap.utils.SapConstants.RESOURCE_ON_ERROR;
import static org.ballerinalang.sap.utils.SapConstants.RESOURCE_ON_MESSAGE;
import static org.ballerinalang.sap.utils.SapConstants.SAP_RESOURCE;

/**
 * This is used to register a Listener to the SAP service.
 */
@BallerinaFunction(
    orgName = SapConstants.ORG_NAME,
    packageName = SapConstants.FULL_PACKAGE_NAME,
    functionName = "register",
    receiver = @Receiver(
            type = TypeKind.OBJECT,
            structType = SapConstants.CONSUMER_STRUCT_NAME,
            structPackage = SapConstants.SAP_NATIVE_PACKAGE
    )
)
public class Register extends BlockingNativeCallableUnit {

    /**
     * Add the resources to the Struct
     * @param context Current context instance
     */
    @Override
    public void execute(Context context) {
        Map<String, Resource> sapResources = getResourceMap(context);
        Struct listenerEndpoint = BLangConnectorSPIUtil.getConnectorEndpointStruct(context);
        //Check the resources already add or not to the Struct.
        if (listenerEndpoint.getNativeData(SAP_RESOURCE) == null) {
            listenerEndpoint.addNativeData(SAP_RESOURCE, sapResources);
            context.setReturnValues();
        } else {
            throw new BallerinaException("Stop the running listener.");
        }
    }

    /**
     * Validate the resources and set those into the map. .
     * @param context The context
     * @return return the resource map
     */
    private Map<String, Resource> getResourceMap(Context context) {
        Service service = BLangConnectorSPIUtil.getServiceRegistered(context);
        Map<String, Resource> registry = new HashMap<>(2);
        for (Resource resource : service.getResources()) {
            switch (resource.getName()) {
                case RESOURCE_ON_ERROR:
                    registry.put(RESOURCE_ON_ERROR, resource);
                    break;
                case RESOURCE_ON_MESSAGE:
                    registry.put(RESOURCE_ON_MESSAGE, resource);
                    break;
                default:
                    throw new BallerinaException("One of these name [" + RESOURCE_ON_ERROR + ", "
                            + RESOURCE_ON_MESSAGE + "] should be used to register the resource in the SAP service");
            }
        }
        return registry;
    }
}
