/*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.ballerinalang.test.streaming;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This contains methods to test filter behaviour of Ballerina Streaming.
 *
 * @since 0.965.0
 */
public class FilterTest {

    private CompileResult result;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/streaming/filter-streaming-test.bal");
    }

    @Test(description = "Test filter streaming query")
    public void testFilterQuery() {
        BValue[] outputEmployeeEvents = BRunUtil.invoke(result, "testFilterQuery");

        Assert.assertNotNull(outputEmployeeEvents);

        Assert.assertEquals(outputEmployeeEvents.length, 2, "Expected events are not received");

        BStruct employee0 = (BStruct) outputEmployeeEvents[0];
        BStruct employee1 = (BStruct) outputEmployeeEvents[1];

        Assert.assertEquals(employee0.getIntField(0), 33);
        Assert.assertEquals(employee1.getIntField(0), 45);
    }

}
