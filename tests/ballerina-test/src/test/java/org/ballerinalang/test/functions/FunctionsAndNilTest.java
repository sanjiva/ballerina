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
package org.ballerinalang.test.functions;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class contains tests that are related to functions and nil type ().
 */
public class FunctionsAndNilTest {

    private CompileResult result;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/functions/functions-with-nil/functions_with_nil_basics.bal");
    }

    @Test(description = "Test functions that returns nil type")
    public void testFuncReturnNilImplicit() {
        BValue[] returns = BRunUtil.invoke(result, "funcReturnNilImplicit");
        Assert.assertEquals(returns.length, 0);
    }

    @Test(description = "Test functions that returns nil type")
    public void funcReturnNilExplicit() {
        BValue[] returns = BRunUtil.invoke(result, "funcReturnNilExplicit");
        Assert.assertEquals(returns.length, 0);
    }

    @Test(description = "Test functions that returns nil type")
    public void funcReturnNilOrError() {
        BValue[] params = new BValue[1];
        params[0] = new BInteger(10);
        BValue[] returns = BRunUtil.invoke(result, "funcReturnNilOrError", params);
        Assert.assertEquals(returns.length, 1);
        Assert.assertNull(returns[0]);

        params[0] = new BInteger(30);
        returns = BRunUtil.invoke(result, "funcReturnNilOrError", params);
        Assert.assertEquals(returns.length, 1);
        Assert.assertNotNull(returns[0]);
        Assert.assertEquals(returns[0].getClass(), BStruct.class);
        BStruct errorValue = (BStruct) returns[0];
        Assert.assertEquals("dummy error message", errorValue.getStringField(0));
    }

    @Test(description = "Test functions that returns nil type")
    public void funcReturnOptionallyError() {
        BValue[] params = new BValue[1];
        params[0] = new BInteger(10);
        BValue[] returns = BRunUtil.invoke(result, "funcReturnOptionallyError", params);
        Assert.assertEquals(returns.length, 1);
        Assert.assertNull(returns[0]);

        params[0] = new BInteger(30);
        returns = BRunUtil.invoke(result, "funcReturnOptionallyError", params);
        Assert.assertEquals(returns.length, 1);
        Assert.assertNotNull(returns[0]);
        Assert.assertEquals(returns[0].getClass(), BStruct.class);
        BStruct errorValue = (BStruct) returns[0];
        Assert.assertEquals("dummy error message", errorValue.getStringField(0));
    }

    @Test(description = "Test functions that returns nil type")
    public void testNilReturnAssignment() {
        BValue[] returns = BRunUtil.invoke(result, "testNilReturnAssignment");
        Assert.assertEquals(returns.length, 0);
    }
}
