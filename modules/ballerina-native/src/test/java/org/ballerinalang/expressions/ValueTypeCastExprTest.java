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
package org.ballerinalang.expressions;

import org.ballerinalang.model.values.BBlob;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BFloatArray;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.nativeimpl.util.BTestUtils;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.program.BLangFunctions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test Cases for type casting.
 */
public class ValueTypeCastExprTest {
    private static final double DELTA = 0.01;
    private ProgramFile programFile;

    @BeforeClass
    public void setup() {
        programFile = BTestUtils.getProgramFile("lang/expressions/type/cast/value-type-casting.bal");
    }

    @Test
    public void testIntToFloat() {
        BValue[] args = {new BInteger(55555555)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "intToFloat", args);
        Assert.assertTrue(returns[0] instanceof BFloat);
        double expected = 5.5555555E7;
        Assert.assertEquals(((BFloat) returns[0]).floatValue(), expected, DELTA);
    }

    @Test
    public void testIntToString() {
        BValue[] args = {new BInteger(111)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "intToString", args);
        Assert.assertTrue(returns[0] instanceof BString);
        final String expected = "111";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test
    public void testIntToBoolean() {
        BValue[] args = {new BInteger(1)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "intToBoolean", args);
        Assert.assertTrue(returns[0] instanceof BBoolean);
        final boolean expected = true;
        Assert.assertEquals(((BBoolean) returns[0]).booleanValue(), expected);
    }

    @Test
    public void testIntToAny() {
        BValue[] args = {new BInteger(1)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "intToAny", args);
        Assert.assertTrue(returns[0] instanceof BInteger);
    }

    @Test
    public void testFloatToInt() {
        BValue[] args = {new BFloat(222222.44444f)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "floatToInt", args);
        Assert.assertTrue(returns[0] instanceof BInteger);
        final String expected = "222222";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test
    public void testFloatToString() {
        BValue[] args = {new BFloat(111.333f)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "floatToString", args);
        Assert.assertTrue(returns[0] instanceof BString);
        final String expected = "111.333";
        Assert.assertEquals(returns[0].stringValue().substring(0, 7), expected);
    }

    @Test
    public void testFloatToBoolean() {
        BValue[] args = {new BFloat(1.0f)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "floatToBoolean", args);
        Assert.assertTrue(returns[0] instanceof BBoolean);
        final boolean expected = true;
        Assert.assertEquals(((BBoolean) returns[0]).booleanValue(), expected);
    }

    @Test
    public void testFloatToAny() {
        BValue[] args = {new BFloat(111.333f)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "floatToAny", args);
        Assert.assertTrue(returns[0] instanceof BFloat);
    }

    @Test
    public void testStringToInt() {
        BValue[] args = {new BString("100")};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "stringToInt", args);
        Assert.assertTrue(returns[0] instanceof BInteger);
        final long expected = 100;
        Assert.assertEquals(((BInteger) returns[0]).intValue(), expected);
    }

    @Test
    public void testStringToFloat() {
        BValue[] args = {new BString("2222.333f")};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "stringToFloat", args);
        Assert.assertTrue(returns[0] instanceof BFloat);
        double expected = 2222.333;
        Assert.assertEquals(((BFloat) returns[0]).floatValue(), expected, DELTA);
    }

    @Test
    public void testStringToBoolean() {
        BValue[] args = {new BString("trUe")};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "stringToBoolean", args);
        Assert.assertTrue(returns[0] instanceof BBoolean);
        boolean expected = true;
        Assert.assertEquals(((BBoolean) returns[0]).booleanValue(), expected);
    }

    @Test
    public void testStringToAny() {
        BValue[] args = {new BString("adfs sadfasd")};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "stringToAny", args);
        Assert.assertTrue(returns[0] instanceof BString);
    }

    @Test
    public void testBooleanToInt() {
        BValue[] args = {new BBoolean(true)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "booleanToInt", args);
        Assert.assertTrue(returns[0] instanceof BInteger);
        final int expected = 1;
        Assert.assertEquals(((BInteger) returns[0]).intValue(), expected);
    }

    @Test
    public void testBooleanToFloat() {
        BValue[] args = {new BBoolean(true)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "booleanToFloat", args);
        Assert.assertTrue(returns[0] instanceof BFloat);
        final double expected = 1.0;
        Assert.assertEquals(((BFloat) returns[0]).floatValue(), expected);
    }

    @Test
    public void testBooleanToString() {
        BValue[] args = {new BBoolean(true)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "booleanToString", args);
        Assert.assertTrue(returns[0] instanceof BString);
        final String expected = "true";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test
    public void testBooleanToAny() {
        BValue[] args = {new BBoolean(true)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "booleanToAny", args);
        Assert.assertTrue(returns[0] instanceof BBoolean);
    }

    @Test
    public void testBooleanAppendToString() {
        BValue[] args = {new BBoolean(true)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "booleanappendtostring", args);
        Assert.assertTrue(returns[0] instanceof BString);
        final String expected = "true-append-true";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test
    public void testBlobToAny() {
        BValue[] args = {new BBlob("string".getBytes())};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "blobToAny", args);
        Assert.assertTrue(returns[0] instanceof BBlob);
    }

    @Test
    public void testIntArrayToLongArray() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "intarrtofloatarr");
        Assert.assertTrue(returns[0] instanceof BFloatArray);
        BFloatArray result = (BFloatArray) returns[0];
        Assert.assertEquals(result.get(0), 999.0, DELTA);
        Assert.assertEquals(result.get(1), 95.0, DELTA);
        Assert.assertEquals(result.get(2), 889.0, DELTA);
    }

    @Test
    public void testAnyToInt() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "anyToInt");
        Assert.assertTrue(returns[0] instanceof BInteger);
        final int expected = 5;
        Assert.assertEquals(((BInteger) returns[0]).intValue(), expected);
    }

    @Test
    public void testAnyToFloat() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "anyToFloat");
        Assert.assertTrue(returns[0] instanceof BFloat);
        final double expected = 5.0;
        Assert.assertEquals(((BFloat) returns[0]).intValue(), expected, DELTA);
    }

    @Test
    public void testAnyToString() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "anyToString");
        Assert.assertTrue(returns[0] instanceof BString);
        final String expected = "test";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test
    public void testAnyToBoolean() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "anyToBoolean");
        Assert.assertTrue(returns[0] instanceof BBoolean);
        final boolean expected = false;
        Assert.assertEquals(((BBoolean) returns[0]).booleanValue(), expected);
    }

    @Test
    public void testAnyToBlob() {
        byte[] data = "string".getBytes();
        BValue[] args = {new BBlob(data)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "anyToBlob", args);
        Assert.assertTrue(returns[0] instanceof BBlob);
        Assert.assertEquals(((BBlob) returns[0]).blobValue(), data);
    }
}
