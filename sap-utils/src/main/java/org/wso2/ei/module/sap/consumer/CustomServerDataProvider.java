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

import com.sap.conn.jco.ext.ServerDataEventListener;
import com.sap.conn.jco.ext.ServerDataProvider;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.util.Properties;

/**
 * The custom destination data provider implements the DestinationDataProvider and
 *  provides an implementation for at least the getDestinationProperties(String) function.
 */
public class CustomServerDataProvider implements ServerDataProvider {

    private Properties serverProp;

    CustomServerDataProvider(Properties properties) {
        serverProp = properties;
    }

    /**
     * Return the properties of the server
     * @param serverName Name of the server
     * @return Properties of the server
     */
    @Override
    public Properties getServerProperties(String serverName) {
        if (serverProp != null) {
            return serverProp;
        }
        throw new BallerinaException("Properties for Server " + serverName + " is not available");
    }

    /**
     * An implementation supporting events has to retain the eventListener instance provided
     * by the JCo runtime. This listener instance shall be used to notify the JCo runtime
     * about all changes in destination configurations.
     * @param dataEventListener The sever data event listener
     */
    @Override
    public void setServerDataEventListener(ServerDataEventListener dataEventListener) {
    }

    @Override
    public boolean supportsEvents() {
        return true;
    }
}
