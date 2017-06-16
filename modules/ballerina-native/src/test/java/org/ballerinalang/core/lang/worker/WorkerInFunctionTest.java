/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.core.lang.worker;

import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMessage;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.nativeimpl.util.BTestUtils;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.program.BLangFunctions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases for usages of worker in functions.
 */
public class WorkerInFunctionTest {
    private ProgramFile bProgramFile;

    @BeforeClass
    public void setup() {
        bProgramFile = BTestUtils.getProgramFile("samples/worker-in-function-test.bal");
    }

    //@Test(description = "Test worker in function")
    public void testWorkerInFunction() {
        BValue[] args = {new BMessage()};
        BValue[] returns = BLangFunctions.invokeNew(bProgramFile, "testworker", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertTrue(returns[0] instanceof BMessage);
        final String expected = "{\"name\":\"WSO2\"}";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test simple worker in function")
    public void testSimpleWorkerInFunction() {
        BValue[] args = {new BMessage()};
        BValue[] returns = BLangFunctions.invokeNew(bProgramFile, "testSimpleWorker", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertTrue(returns[0] instanceof BMessage);
        final String expected = "{\"name\":\"chanaka\"}";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test worker accessing parameters passed into function")
    public void testWorkerAccessingFunctionParameters() {
        bProgramFile = BTestUtils.getProgramFile("samples/worker-accessing-function-params.bal");
        BValue[] args = {new BInteger(100)};
        BValue[] returns = BLangFunctions.invokeNew(bProgramFile, "testFunctionArgumentAccessFromWorker", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertTrue(returns[0] instanceof BInteger);
        final String expected = "1100";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }
}
