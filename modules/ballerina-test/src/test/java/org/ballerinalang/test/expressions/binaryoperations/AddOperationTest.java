/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.test.expressions.binaryoperations;

import org.ballerinalang.launcher.util.BAssertUtil;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Class to test functionality of add operation.
 */
public class AddOperationTest {

    CompileResult result;
    CompileResult resultNegative;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/expressions/binaryoperations/add-operation.bal");
        resultNegative = BCompileUtil.compile("test-src/expressions/binaryoperations/add-operation-negative.bal");
    }

    @Test(description = "Test two int add expression")
    public void testIntAddExpr() {
        BValue[] args = { new BInteger(100), new BInteger(200)};

        BValue[] returns = BRunUtil.invoke(result, "intAdd", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        long actual = ((BInteger) returns[0]).intValue();
        long expected = 300;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test two float add expression")
    public void testFloatAddExpr() {
        BValue[] args = { new BFloat(100.0f), new BFloat(200.0f)};

        BValue[] returns = BRunUtil.invoke(result, "floatAdd", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BFloat.class);
        double actual = ((BFloat) returns[0]).floatValue();
        double expected = 300.0f;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test two string add expression")
    public void testStringAddExpr() {
        BValue[] args = { new BString("WSO2"), new BString(" Inc.")};
        BValue[] returns = BRunUtil.invoke(result, "stringAdd", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BString.class);

        String actual = returns[0].stringValue();
        String expected = "WSO2 Inc.";
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test adding negative values")
    public void testNegativeValues() {
        int a = -10;
        int b = -20;

        long expectedResult = a + b;

        BValue[] args = {new BInteger(a), new BInteger(b)};

        BValue[] returns = BRunUtil.invoke(result, "intAdd", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        long actualResult = ((BInteger) returns[0]).intValue();
        Assert.assertEquals(actualResult, expectedResult);
    }

    @Test(description = "Test string and int add expression")
    public void testStringAndIntAddExpr() {
        String a = "test";
        int b = 10;

        String expectedResult = a + b;

        BValue[] args = {new BString(a), new BInteger(b)};

        BValue[] returns = BRunUtil.invoke(result, "stringAndIntAdd", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BString.class);
        String actualResult = ((BString) returns[0]).stringValue();
        Assert.assertEquals(actualResult, expectedResult);
    }

    @Test(description = "Test int float add expression")
    public void testIntFloatAddExpr() {
        int a = 10;
        float b = 1.5f;
        BValue[] args = { new BInteger(a), new BFloat(b)};

        BValue[] returns = BRunUtil.invoke(result, "intFloatAdd", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BFloat.class);
        double actual = ((BFloat) returns[0]).floatValue();
        double expected = a + b;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test float int add expression")
    public void testFloatIntAddExpr() {
        int a = 10;
        float b = 1.5f;
        BValue[] args = { new BFloat(b), new BInteger(a)};

        BValue[] returns = BRunUtil.invoke(result, "floatIntAdd", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BFloat.class);
        double actual = ((BFloat) returns[0]).floatValue();
        double expected = a + b;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test binary statement with errors")
    public void testSubtractStmtNegativeCases() {
        Assert.assertEquals(resultNegative.getErrorCount(), 2);
        BAssertUtil.validateError(resultNegative, 0, "operator '+' not defined for 'json' and 'json'", 8, 10);
        BAssertUtil.validateError(resultNegative, 1, "incompatible types: expected 'int', found 'string'", 14, 9);
    }
}
