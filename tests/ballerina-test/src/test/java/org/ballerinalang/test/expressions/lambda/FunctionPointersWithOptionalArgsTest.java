/*
*   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.test.expressions.lambda;

import org.ballerinalang.launcher.util.BAssertUtil;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases for Function pointers and lambda, with optional paramaters
 *
 * @since 0.965
 */
public class FunctionPointersWithOptionalArgsTest {

    CompileResult result;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/expressions/lambda/function-pointers-with-optional-args.bal");
    }

    @Test
    public void testFunctionPointerAssignmentWithRestParams() {
        BValue[] returns = BRunUtil.invoke(result, "testFunctionPointerAssignmentWithRestParams");
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 3);
        Assert.assertNotNull(returns[0]);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 1);

        Assert.assertNotNull(returns[1]);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 2);

        Assert.assertNotNull(returns[2]);
        Assert.assertEquals(returns[2].stringValue(), "[3, 4]");
    }

    @Test
    public void testFunctionPointersWithRestArgs() {
        CompileResult result =
                BCompileUtil.compile("test-src/expressions/lambda/function-pointers-with-rest-args-negative.bal");
        BAssertUtil.validateError(result, 0, "invalid token '...'", 6, 28);
    }

    @Test
    public void testFunctionPointersWithNamedArgs() {
        CompileResult result =
                BCompileUtil.compile("test-src/expressions/lambda/function-pointers-with-named-args-negative.bal");
        BAssertUtil.validateError(result, 0, "invalid token 'c'", 6, 29);
    }

    @Test
    public void testInvokingFuncPointerWithRestArgs() {
        CompileResult result =
                BCompileUtil.compile("test-src/expressions/lambda/function-pointers-with-optional-args-negative.bal");
        BAssertUtil.validateError(result, 0, "too many arguments in call to 'func()'", 7, 12);
    }

    @Test
    public void testFunctionPointerAssignmentWithNamedParams() {
        BValue[] returns = BRunUtil.invoke(result, "testFunctionPointerAssignmentWithNamedParams");
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 3);
        Assert.assertNotNull(returns[0]);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 1);

        Assert.assertNotNull(returns[1]);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 2);

        Assert.assertNotNull(returns[2]);
        Assert.assertEquals(returns[2].stringValue(), "Alex");
    }
}
