/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.model.values;

import org.ballerinalang.core.utils.BTestUtils;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.exceptions.BLangRuntimeException;
import org.ballerinalang.util.program.BLangFunctions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class contains methods to test the arrays implementation in Ballerina.
 *
 * @since 0.8.0
 */
public class BArrayValueTest {
    private static final double DELTA = 0.01;
    private ProgramFile programFile;

    @BeforeClass
    public void setup() {
        programFile = BTestUtils.getProgramFile("lang/values/array-value.bal");
    }

    @Test(description = "Test lazy arrays creation", expectedExceptions = {BLangRuntimeException.class},
            expectedExceptionsMessageRegExp = ".*array index out of range: index: 0, size: 0.*")
    public void testLazyArrayCreation() {
        BLangFunctions.invokeNew(programFile, "lazyInitThrowArrayIndexOutOfBound", new BValue[0]);
    }

    @Test(description = "Test lazy arrays initializer. Size should be zero")
    public void lazyInitSizeZero() {
        BValue[] args = {};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "lazyInitSizeZero", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BStringArray.class);

        BStringArray arrayValue = (BStringArray) returns[0];
        Assert.assertEquals(arrayValue.size(), 0);
    }

    @Test(description = "Test add value operation on int arrays")
    public void addValueToIntegerArray() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "addValueToIntArray");

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BIntArray.class);

        BIntArray arrayValue = (BIntArray) returns[0];
        Assert.assertEquals(arrayValue.size(), 200, "Invalid arrays size.");
        Assert.assertEquals(arrayValue.get(0), (-10), "Invalid value returned.");
        Assert.assertEquals(arrayValue.get(15), 20, "Invalid value returned.");
        Assert.assertEquals(arrayValue.get(99), 2147483647, "Invalid value returned.");
        Assert.assertEquals(arrayValue.get(100), -4, "Invalid value returned.");
        Assert.assertEquals(arrayValue.get(115), -2147483647, "Invalid value returned.");
        Assert.assertEquals(arrayValue.get(199), 6, "Invalid value returned.");
    }

    @Test(description = "Test add value operation on float arrays")
    public void addValueToFloatArray() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "addValueToFloatArray");

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BFloatArray.class);

        BFloatArray arrayValue = (BFloatArray) returns[0];
        Assert.assertEquals(arrayValue.size(), 200, "Invalid arrays size.");

        Assert.assertEquals(arrayValue.get(0), new Double(-10.0), DELTA, "Invalid value returned.");
        Assert.assertEquals(arrayValue.get(15), new Double(2.5), DELTA, "Invalid value returned.");
        Assert.assertEquals(arrayValue.get(99), new Double(2147483647.1), DELTA,
                "Invalid value returned.");
        Assert.assertEquals(arrayValue.get(100), new Double(4.3), DELTA, "Invalid value returned.");
        Assert.assertEquals(arrayValue.get(115), new Double(-2147483647.7), DELTA,
                "Invalid value returned.");
        Assert.assertEquals(arrayValue.get(199), new Double(6.9), DELTA, "Invalid value returned.");
    }

    @Test(description = "test default value of an element in an integer array")
    public void testDefaultValueOfIntArrayElement() {
        BValue[] args = {};
        BValue[] returns = BLangFunctions.invokeNew(programFile,
                "testDefaultValueOfIntArrayElement", args);

        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertTrue(returns[1] instanceof BInteger);
        Assert.assertTrue(returns[2] instanceof BInteger);

        Assert.assertEquals(((BInteger) returns[0]).intValue(), 0);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 0);
        Assert.assertEquals(((BInteger) returns[2]).intValue(), 45);
    }

    @Test(description = "test default value of an element in an json array")
    public void testDefaultValueOfJsonArrayElement() {
        BValue[] args = {};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testDefaultValueOfJsonArrayElement", args);

        Assert.assertNull(returns[0]);
        Assert.assertNull(returns[1]);
        Assert.assertTrue(returns[2] instanceof BJSON);
        Assert.assertEquals(returns[2].stringValue(), "{\"name\":\"supun\"}");
    }
}
