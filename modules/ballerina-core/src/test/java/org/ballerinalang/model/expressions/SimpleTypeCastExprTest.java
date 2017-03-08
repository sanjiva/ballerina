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
package org.ballerinalang.model.expressions;

import org.ballerinalang.core.utils.BTestUtils;
import org.ballerinalang.model.BLangProgram;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BLong;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.program.BLangFunctions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test Cases for simple type casting.
 */
public class SimpleTypeCastExprTest {

    private BLangProgram bLangProgram;

    @BeforeClass
    public void setup() {
        bLangProgram = BTestUtils.parseBalFile("lang/expressions/simple-type-cast.bal");
    }

    @Test
    public void testIntToLong() {
        BValue[] args = {new BInteger(55555555)};
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "inttolong", args);
        Assert.assertTrue(returns[0] instanceof BLong);
        final String expected = "55555555";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test
    public void testBooleanToIntImplicit() {
        testBooleanToIntCast(true, 1, "booleanToIntImplicit");
        testBooleanToIntCast(false, 0, "booleanToIntImplicit");
    }

    @Test
    public void testBooleanToFloatImplicit() {
        testBooleanToFloatCast(true, 1.0f, "booleanToFloatImplicit");
        testBooleanToFloatCast(false, 0.0f, "booleanToFloatImplicit");
    }

    @Test
    public void testBooleanToIntExplicit() {
        testBooleanToIntCast(true, 1, "booleanToIntExplicit");
        testBooleanToIntCast(false, 0, "booleanToIntExplicit");
    }

    @Test
    public void testBooleanToFloatExplicit() {
        testBooleanToFloatCast(true, 1.0f, "booleanToFloatExplicit");
        testBooleanToFloatCast(false, 0.0f, "booleanToFloatExplicit");
    }

    @Test
    public void testIntToBooleanExplicit() {
        testIntToBooleanCast(1, true);
        testIntToBooleanCast(-1, true);
        testIntToBooleanCast(0, false);
    }

    @Test
    public void testFloatToBooleanExplicit() {
        testFloatToBooleanCast(1.0f, true);
        testFloatToBooleanCast(0.1f, true);
        testFloatToBooleanCast(-1.0f, true);
        testFloatToBooleanCast(0.0f, false);
        testFloatToBooleanCast(0, false);
    }

    private void testBooleanToIntCast(Boolean input, int excepted, String functionName) {
        BValue[] args = { new BBoolean(input) };
        BValue[] returns = BLangFunctions.invoke(bLangProgram, functionName, args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].getClass(), BInteger.class);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), excepted);
    }

    private void testBooleanToFloatCast(Boolean input, float excepted, String functionName) {
        BValue[] args = { new BBoolean(input) };
        BValue[] returns = BLangFunctions.invoke(bLangProgram, functionName, args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].getClass(), BFloat.class);
        Assert.assertEquals(((BFloat) returns[0]).floatValue(), excepted);
    }

    private void testIntToBooleanCast(int input, boolean excepted) {
        BValue[] args = { new BInteger(input) };
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "intToBooleanExplicit", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].getClass(), BBoolean.class);
        Assert.assertEquals(((BBoolean) returns[0]).booleanValue(), excepted);
    }

    private void testFloatToBooleanCast(float input, boolean excepted) {
        BValue[] args = { new BFloat(input) };
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "floatToBooleanExplicit", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].getClass(), BBoolean.class);
        Assert.assertEquals(((BBoolean) returns[0]).booleanValue(), excepted);
    }

}
