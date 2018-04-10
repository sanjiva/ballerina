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
package org.ballerinalang.test.expressions.fieldaccess;

import org.ballerinalang.launcher.util.BAssertUtil;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases for safe navigation.
 * 
 * @since 0.970.0
 */
public class SafeNavigationTest {

    CompileResult result;
    CompileResult negativeResult;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/expressions/fieldaccess/safe_navigation.bal");
        negativeResult = BCompileUtil.compile("test-src/expressions/fieldaccess/safe_navigation_negative.bal");
    }

    @Test
    public void testNegativeCases() {
        Assert.assertEquals(negativeResult.getErrorCount(), 12);
        BAssertUtil.validateError(negativeResult, 0, "incompatible types: expected 'string?', found 'string|error'",
                25, 19);
        BAssertUtil.validateError(negativeResult, 1,
                "invalid operation: type 'Info|error' does not support field access", 34, 25);
        BAssertUtil.validateError(negativeResult, 2,
                "incompatible types: expected 'string|error?', found 'other|error'", 34, 25);
        BAssertUtil.validateError(negativeResult, 3, "incompatible types: expected 'string', found 'string?'", 40, 16);
        BAssertUtil.validateError(negativeResult, 4, "incompatible types: expected 'string[]', found 'string[]?'", 41,
                21);
        BAssertUtil.validateError(negativeResult, 5,
                "safe navigation cannot be used in the target expression of an assignment", 46, 5);
        BAssertUtil.validateError(negativeResult, 6,
                "safe navigation cannot be used in the target expression of an assignment", 46, 5);
        BAssertUtil.validateError(negativeResult, 7,
                "safe navigation cannot be used in the target expression of an assignment", 46, 5);
        BAssertUtil.validateError(negativeResult, 8,
                "invalid operation: type 'Person[]|error' does not support indexing", 51, 12);
        BAssertUtil.validateError(negativeResult, 9, "safe navigation operator not required for type 'error?'", 56,
                12);
        BAssertUtil.validateError(negativeResult, 10, "incompatible types: expected 'string', found 'other|error?'",
                56, 12);
        BAssertUtil.validateError(negativeResult, 11, "safe navigation operator not required for type 'error'", 61,
                12);

    }

    @Test
    public void testNonErrorPath() {
        BValue[] returns = BRunUtil.invoke(result, "testNonErrorPath");
        Assert.assertEquals(returns[0].stringValue(), "Colombo");
    }

    @Test
    public void testNotNilPath() {
        BValue[] returns = BRunUtil.invoke(result, "testNotNilPath");
        Assert.assertEquals(returns[0].stringValue(), "Colombo");
    }

    @Test
    public void testErrorInMiddle() {
        BValue[] returns = BRunUtil.invoke(result, "testErrorInMiddle");
        Assert.assertTrue(returns[0] instanceof BStruct);
        Assert.assertEquals(returns[0].stringValue(), "{message:\"custom error\", cause:null}");
    }

    @Test
    public void testErrorInFirstVar() {
        BValue[] returns = BRunUtil.invoke(result, "testErrorInFirstVar");
        Assert.assertTrue(returns[0] instanceof BStruct);
        Assert.assertEquals(returns[0].stringValue(), "{message:\"custom error\", cause:null}");
    }

    @Test
    public void testNilInMiddle() {
        BValue[] returns = BRunUtil.invoke(result, "testNilInMiddle");
        Assert.assertEquals(returns[0], null);
        Assert.assertEquals(returns[1], null);
    }

    @Test
    public void testNilInFirstVar() {
        BValue[] returns = BRunUtil.invoke(result, "testNilInFirstVar");
        Assert.assertEquals(returns[0], null);
        Assert.assertEquals(returns[1], null);
    }

    @Test
    public void testSafeNavigatingNilJSON_1() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigatingNilJSON_1");
        Assert.assertEquals(returns[0], null);
    }

    @Test
    public void testSafeNavigatingNilJSON_2() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigatingNilJSON_2");
        Assert.assertEquals(returns[0], null);
    }

    @Test
    public void testSafeNavigatingNilJSON_3() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigatingNilJSON_3");
        Assert.assertEquals(returns[0], null);
    }

    @Test
    public void testSafeNavigatingNilJSON_4() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigatingNilJSON_4");
        Assert.assertEquals(returns[0], null);
    }

    @Test
    public void testSafeNavigatingJSONWithNilInMiddle_1() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigatingJSONWithNilInMiddle_1");
        Assert.assertEquals(returns[0], null);
    }

    @Test
    public void testSafeNavigatingJSONWithNilInMiddle_2() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigatingJSONWithNilInMiddle_2");
        Assert.assertEquals(returns[0], null);
    }

    @Test
    public void testSafeNavigatingNilMap() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigatingNilMap");
        Assert.assertEquals(returns[0], null);
    }

    @Test
    public void testSafeNavigatingWithFuncInovc_1() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigatingWithFuncInovc_1");
        Assert.assertEquals(returns[0], null);
    }

    @Test
    public void testSafeNavigationOnObject_1() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigationOnObject_1");
        Assert.assertEquals(returns[0].stringValue(), "John");
    }

    @Test
    public void testSafeNavigationOnObject_2() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigationOnObject_2");
        Assert.assertEquals(returns[0].stringValue(), "null name");
    }

    @Test
    public void testSafeNavigationOnObject_3() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigationOnObject_3");
        Assert.assertEquals(returns[0].stringValue(), "John");
    }

    @Test
    public void testSafeNavigateArray_1() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigateArray_1");
        Assert.assertEquals(returns[0], null);
    }

    @Test
    public void testSafeNavigateArray_2() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigateArray_2");
        Assert.assertEquals(returns[0], null);
    }

    @Test
    public void testNullLiftingOnError() {
        BValue[] returns = BRunUtil.invoke(result, "testNullLiftingOnError");
        Assert.assertEquals(returns[0].stringValue(), "");
    }

    @Test
    public void testSafeNavigateOnErrorOrNull() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeNavigateOnErrorOrNull");
        Assert.assertEquals(returns[0], null);
    }
}
