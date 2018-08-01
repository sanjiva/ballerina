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
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.ballerinalang.test.balo.functions;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BIntArray;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.test.balo.BaloCreator;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Test function signatures and calling with optional and named params.
 * 
 * @since 0.975.0
 */
public class FunctionSignatureInBaloTest {

    CompileResult result;

    @BeforeClass
    public void setup() throws IOException {
        BaloCreator.createAndSetupBalo("test-src/balo/test_projects/test_project", "testorg", "foo");
        result = BCompileUtil.compile("test-src/balo/test_balo/functions/test_different_function_signatures.bal");
    }

    @Test
    public void testInvokeFunctionInOrder1() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFunctionInOrder1");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 20.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "Alex");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 30);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Bob");

        Assert.assertTrue(returns[5] instanceof BIntArray);
        Assert.assertEquals(returns[5].stringValue(), "[40, 50, 60]");
    }

    @Test
    public void testInvokeFunctionInOrder2() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFunctionInOrder2");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 20.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "Alex");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 30);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Bob");

        Assert.assertTrue(returns[5] instanceof BIntArray);
        Assert.assertEquals(returns[5].stringValue(), "[40, 50, 60]");
    }

    @Test
    public void testInvokeFunctionInMixOrder1() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFunctionInMixOrder1");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 20.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "Alex");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 30);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Bob");

        Assert.assertTrue(returns[5] instanceof BIntArray);
        Assert.assertEquals(returns[5].stringValue(), "[40, 50, 60]");
    }

    @Test
    public void testInvokeFunctionInMixOrder2() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFunctionInMixOrder2");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 20.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "Alex");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 30);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Bob");

        Assert.assertTrue(returns[5] instanceof BIntArray);
        Assert.assertEquals(returns[5].stringValue(), "[40, 50, 60]");
    }

    @Test
    public void testInvokeFunctionWithoutRestArgs() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFunctionWithoutRestArgs");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 20.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "Alex");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 30);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Bob");

        Assert.assertTrue(returns[5] instanceof BIntArray);
        Assert.assertEquals(returns[5].stringValue(), "[]");
    }

    @Test
    public void testInvokeFunctionWithoutSomeNamedArgs() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFunctionWithoutSomeNamedArgs");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 20.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "Alex");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 5);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Doe");

        Assert.assertTrue(returns[5] instanceof BIntArray);
        Assert.assertEquals(returns[5].stringValue(), "[]");
    }

    @Test
    public void testInvokeFunctionWithRequiredArgsOnly() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFunctionWithRequiredArgsOnly");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 20.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "John");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 5);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Doe");

        Assert.assertTrue(returns[5] instanceof BIntArray);
        Assert.assertEquals(returns[5].stringValue(), "[]");
    }

    @Test
    public void testInvokeFunctionWithRequiredAndRestArgs() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFunctionWithRequiredAndRestArgs");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 20.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "John");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 5);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Doe");

        Assert.assertTrue(returns[5] instanceof BIntArray);
        Assert.assertEquals(returns[5].stringValue(), "[40, 50, 60]");
    }

    @Test
    public void testInvokeFuncWithoutRestParams() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFuncWithoutRestParams");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 20.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "John");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 30);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Bob");
    }

    @Test
    public void testInvokeFuncWithOnlyNamedParams1() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFuncWithOnlyNamedParams1");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 20.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "Alex");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 30);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Bob");
    }

    @Test
    public void testInvokeFuncWithOnlyNamedParams2() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFuncWithOnlyNamedParams2");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 5);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 6.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "Alex");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 30);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Bob");
    }

    @Test
    public void testInvokeFuncWithOnlyNamedParams3() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFuncWithOnlyNamedParams3");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 5);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 6.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "John");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 7);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Doe");
    }

    @Test
    public void testInvokeFuncWithOnlyRestParam1() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFuncWithOnlyRestParam1");
        Assert.assertTrue(returns[0] instanceof BIntArray);
        Assert.assertEquals(returns[0].stringValue(), "[]");
    }

    @Test
    public void testInvokeFuncWithOnlyRestParam2() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFuncWithOnlyRestParam2");
        Assert.assertTrue(returns[0] instanceof BIntArray);
        Assert.assertEquals(returns[0].stringValue(), "[10, 20, 30]");
    }

    @Test
    public void testInvokeFuncWithOnlyRestParam3() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFuncWithOnlyRestParam3");
        Assert.assertTrue(returns[0] instanceof BIntArray);
        Assert.assertEquals(returns[0].stringValue(), "[10, 20, 30]");
    }

    @Test
    public void testInvokeFuncWithAnyRestParam1() {
        BValue[] returns = BRunUtil.invoke(result, "testInvokeFuncWithAnyRestParam1");
        Assert.assertTrue(returns[0] instanceof BIntArray);
        Assert.assertEquals(returns[0].stringValue(), "[10, 20, 30]");
    }

    @Test
    public void funcInvocAsRestArgs() {
        BValue[] returns = BRunUtil.invoke(result, "funcInvocAsRestArgs");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);

        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 20.0);

        Assert.assertTrue(returns[2] instanceof BString);
        Assert.assertEquals(returns[2].stringValue(), "Alex");

        Assert.assertTrue(returns[3] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 30);

        Assert.assertTrue(returns[4] instanceof BString);
        Assert.assertEquals(returns[4].stringValue(), "Bob");

        Assert.assertTrue(returns[5] instanceof BIntArray);
        Assert.assertEquals(returns[5].stringValue(), "[1, 2, 3, 4]");
    }

    @Test
    public void testFuncWithUnionTypedDefaultParam() {
        BValue[] returns = BRunUtil.invoke(result, "testFuncWithUnionTypedDefaultParam");
        Assert.assertEquals(returns[0].stringValue(), "John");
    }

    @Test
    public void testFuncWithNilDefaultParamExpr() {
        BValue[] returns = BRunUtil.invoke(result, "testFuncWithNilDefaultParamExpr");
        Assert.assertNull(returns[0]);
        Assert.assertNull(returns[1]);
    }

    @Test
    public void testAttachedFunction() {
        BValue[] returns = BRunUtil.invoke(result, "testAttachedFunction");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 100);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 110);
    }

    @Test(description = "Test object function with defaultableParam")
    public void defaultValueForObjectFunctionParam() {
        BValue[] returns = BRunUtil.invoke(result, "testDefaultableParamInnerFunc");

        Assert.assertEquals(returns.length, 2);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        Assert.assertSame(returns[1].getClass(), BString.class);

        Assert.assertEquals(((BInteger) returns[0]).intValue(), 60);
        Assert.assertEquals(returns[1].stringValue(), "hello world");
    }

    @Test(description = "Test object outer function with defaultable param")
    public void defaultValueForObjectOuterFunctionParam() {
        BValue[] returns = BRunUtil.invoke(result, "testDefaultableParamOuterFunc");

        Assert.assertEquals(returns.length, 2);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        Assert.assertSame(returns[1].getClass(), BString.class);

        Assert.assertEquals(((BInteger) returns[0]).intValue(), 50);
        Assert.assertEquals(returns[1].stringValue(), "hello world");
    }

    @AfterClass
    public void tearDown() {
        BaloCreator.clearPackageFromRepository("testorg", "foo");
    }
}
