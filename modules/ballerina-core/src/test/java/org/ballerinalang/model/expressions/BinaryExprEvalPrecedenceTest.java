/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.model.expressions;

import org.ballerinalang.core.utils.BTestUtils;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.exceptions.BLangRuntimeException;
import org.ballerinalang.util.program.BLangFunctions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This will test binary expression precedence.
 */
public class BinaryExprEvalPrecedenceTest {

    private ProgramFile programFile;

    @BeforeClass
    public void setup() {
        programFile = BTestUtils.getProgramFile("lang/expressions/binary-expr-precedence.bal");
    }

    @Test(description = "Test binary OR expression with " +
            "left most expr evaluated to true expression.")
    public void testBinaryOrExprWithLeftMostExprTrue() {
        boolean one = true;
        boolean two = false;
        boolean three = false;

        boolean expectedResult = true;

        BValue[] args = {new BBoolean(one), new BBoolean(two), new BBoolean(three)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "binaryOrExprWithLeftMostSubExprTrue", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BBoolean.class);
        boolean actualResult = ((BBoolean) returns[0]).booleanValue();

        Assert.assertEquals(actualResult, expectedResult);
    }

    //TODO this expected NullPointerException should be properly handled at ballerina layer when
    //TODO accessing non existing JSON elements.
    @Test(description = "Test binary OR expression with " +
            "left most expr evaluated to false expression.", expectedExceptions = {BLangRuntimeException.class})
    public void testBinaryOrExprWithLeftMostExprFalseNegativeCase() {
        boolean one = false;
        boolean two = false;
        boolean three = false;
        BValue[] args = {new BBoolean(one), new BBoolean(two), new BBoolean(three)};
        BLangFunctions.invokeNew(programFile, "binaryOrExprWithLeftMostSubExprTrue", args);
    }

    @Test(description = "Test binary AND expression with " +
            "left most expr evaluated to false expression.")
    public void testBinaryAndExprWithLeftMostExprTrue() {
        boolean one = false;
        boolean two = false;
        boolean three = false;

        boolean expectedResult = false;

        BValue[] args = {new BBoolean(one), new BBoolean(two), new BBoolean(three)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "binaryANDExprWithLeftMostSubExprFalse", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BBoolean.class);
        boolean actualResult = ((BBoolean) returns[0]).booleanValue();

        Assert.assertEquals(actualResult, expectedResult);
    }

    //TODO this expected NullPointerException should be properly handled at ballerina layer when
    //TODO accessing non existing JSON elements.
    @Test(description = "Test binary AND expression with " +
            "left most expr evaluated to true expression.", expectedExceptions = {BLangRuntimeException.class})
    public void testBinaryAndExprWithLeftMostExprFalseNegativeCase() {
        boolean one = true;
        boolean two = false;
        boolean three = false;
        BValue[] args = {new BBoolean(one), new BBoolean(two), new BBoolean(three)};
        BLangFunctions.invokeNew(programFile, "binaryANDExprWithLeftMostSubExprFalse", args);
    }

    @Test(description = "Test multi binary expression with OR sub expressions inside If condition.")
    public void testMultiBinaryORExpr() {
        boolean one = false;
        boolean two = false;
        boolean three = true;

        int expectedResult = 101;

        BValue[] args = {new BBoolean(one), new BBoolean(two), new BBoolean(three)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "multiBinaryORExpr", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        int actualResult = (int) ((BInteger) returns[0]).intValue();

        Assert.assertEquals(actualResult, expectedResult);
    }

    @Test(description = "Test multi binary expression with OR sub expressions inside If condition.")
    public void testMultiBinaryORExprNegative() {
        boolean one = false;
        boolean two = false;
        boolean three = false;

        int expectedResult = 201;

        BValue[] args = {new BBoolean(one), new BBoolean(two), new BBoolean(three)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "multiBinaryORExpr", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        int actualResult = (int) ((BInteger) returns[0]).intValue();

        Assert.assertEquals(actualResult, expectedResult);
    }

    @Test(description = "Test multi binary expression with OR sub expressions inside If condition.")
    public void testMultiBinaryANDExpr() {
        boolean one = true;
        boolean two = true;
        boolean three = true;

        int expectedResult = 101;

        BValue[] args = {new BBoolean(one), new BBoolean(two), new BBoolean(three)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "multiBinaryANDExpr", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        int actualResult = (int) ((BInteger) returns[0]).intValue();

        Assert.assertEquals(actualResult, expectedResult);
    }

    @Test(description = "Test multi binary expression with OR sub expressions inside If condition.")
    public void testMultiBinaryANDExprNegative() {
        boolean one = true;
        boolean two = false;
        boolean three = false;

        int expectedResult = 201;

        BValue[] args = {new BBoolean(one), new BBoolean(two), new BBoolean(three)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "multiBinaryANDExpr", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        int actualResult = (int) ((BInteger) returns[0]).intValue();

        Assert.assertEquals(actualResult, expectedResult);
    }

}
