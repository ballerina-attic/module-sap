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

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.util.Properties;

/**
 * The custom destination data provider implements the DestinationDataProvider and
 * provides an implementation for at least the getDestinationProperties(String) function.
 */
public class CustomDestinationDataProvider implements DestinationDataProvider {

    private Properties connectionProp;

    public CustomDestinationDataProvider(Properties properties) {
        connectionProp = properties;
    }

    /**
     * Return the properties of the destination.
     * @param destName Name of the destination
     * @return Properties of the destination
     */
    @Override
    public Properties getDestinationProperties(String destName) {
        if (connectionProp != null && connectionProp.isEmpty()) {
                throw new BallerinaException("Destination configuration is incorrect:"
                        + DataProviderException.Reason.INVALID_CONFIGURATION);
        } else {
            return connectionProp;
        }
    }

    /**
     * An implementation supporting events has to retain the eventListener instance provided
     * by the JCo runtime. This listener instance shall be used to notify the JCo runtime
     * about all changes in destination configurations.
     * @param dataEventListener The destination data event listener
     */
    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener dataEventListener) {
    }

    @Override
    public boolean supportsEvents() {
        return true;
    }
}
