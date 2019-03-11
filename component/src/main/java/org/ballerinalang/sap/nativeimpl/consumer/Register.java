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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import static org.ballerinalang.sap.utils.SapConstants.SAP_SERVICE;

/**
 * This is used to register a listener to the SAP service.
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
    private static final Log log = LogFactory.getLog(Register.class);

    @Override
    public void execute(Context context) {

        Map<String, Resource> sapService = getResourceMap(context);
        Struct listenerEndpoint = BLangConnectorSPIUtil.getConnectorEndpointStruct(context);
        listenerEndpoint.addNativeData(SAP_SERVICE, sapService);
        context.setReturnValues();
    }

    private Map<String, Resource> getResourceMap(Context context) {

        Service service = BLangConnectorSPIUtil.getServiceRegistered(context);
        return getResourceRegistry(service);
    }

    private Map<String, Resource> getResourceRegistry(Service service) {

        Map<String, Resource> registry = new HashMap<>(2);
        int resourceCount = 0;
        for (Resource resource : service.getResources()) {
            switch (resource.getName()) {
                case RESOURCE_ON_ERROR:
                    resourceCount++;
                    registry.put(RESOURCE_ON_ERROR, resource);
                    break;
                case RESOURCE_ON_MESSAGE:
                    resourceCount++;
                    registry.put(RESOURCE_ON_MESSAGE, resource);
                    break;
                default:
                    throw new BallerinaException("");
            }
            if (resourceCount == 2) {
                break;
            }
        }
        return registry;
    }
}
