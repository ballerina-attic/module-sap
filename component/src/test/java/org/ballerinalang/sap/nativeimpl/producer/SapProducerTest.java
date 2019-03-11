/*
 *   Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.ballerinalang.sap.nativeimpl.producer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.Test;


public class SapProducerTest {
    CompileResult compileResult;
    private static Log log = LogFactory.getLog(SapProducerTest.class);

    @Test(description = "Test for send the IDoc to SAP instance")
    public void testSapIDocProducer() {

        compileResult = BCompileUtil.compile("producer/sap_idoc_producer.bal");
        BValue[] returnBValue = BRunUtil.invoke(compileResult, "funcSapIDocProducer");
        Assert.assertTrue(returnBValue[0] instanceof BString);
    }

    @Test(description = "Test for send the IDoc to SAP instance")
    public void funcSapBAPIProducer() {

        compileResult = BCompileUtil.compile("producer/sap_bapi_producer.bal");
        BValue[] returnBValue = BRunUtil.invoke(compileResult, "funcSapBAPIProducer");
        Assert.assertTrue(returnBValue[0] instanceof BString);
    }
}
