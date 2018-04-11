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
package org.ballerinalang.test.object;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases for object with attached functions invocations as function pointers in ballerina.
 */
public class ObjectAttachedFunctionPointerTest {

    private CompileResult compileResult;

    @BeforeClass
    public void setup() {
        compileResult = BCompileUtil.compile("test-src/object/object-attached-function-pointers.bal");
    }

    @Test(description = "Test basic function pointer test")
    public void testBasicAttachedFunctionPointerTest() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test1");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 60);
    }

    @Test(description = "Test object attached function pointer with lambda")
    public void testAttachedFunctionPointerWithLambdaTest() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test2");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 60);
    }

    @Test(description = "Test basic externally attached function pointer")
    public void testBasicExternalAttachedFunctionPointerTest() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test3");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 53);
    }

    @Test(description = "Test externally attached function pointer with lambda")
    public void testExternalAttachedFunctionPointerWithLambdaTest() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test4");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 60);
    }

    @Test(description = "Test multi level lambda with attached function")
    public void testMultiLevelLambdaWithinAttachedFunction() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test5");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 65);
    }
}
