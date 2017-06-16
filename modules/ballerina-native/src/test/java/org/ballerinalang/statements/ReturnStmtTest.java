/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.statements;

import org.ballerinalang.model.values.BFloat;
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
 * Test class for return statement.
 */
public class ReturnStmtTest {
    private static final double DELTA = 0.01;
    private ProgramFile programFile;

    @BeforeClass
    public void setup() {
        programFile = BTestUtils.getProgramFile("lang/statements/returnstmt/return-stmt-positive.bal");
    }

    @Test(description = "Test void return")
    public void testReturn() {
        BValue[] args = {};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testReturn", args);

        Assert.assertEquals(returns.length, 0);
    }

    @Test(description = "Test void return")
    public void testReturnOneVarDcl() {
        BValue[] args = {};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testReturnOneVarDcl", args);

        Assert.assertEquals(returns.length, 0);
    }

    @Test(description = "Test one return value")
    public void testReturnOneReturnArg() {
        BValue[] args = {};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testReturnOneReturnArg", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(5, ((BInteger) returns[0]).intValue());
    }

    @Test(description = "Test one parameter but void return")
    public void testReturnOneParam() {
        BValue[] args = {new BInteger(10)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testReturnOneParam", args);

        Assert.assertEquals(returns.length, 0);
    }

    @Test(description = "Test one parameter and one return value")
    public void testReturnOneParamOneReturnArg() {
        BValue[] args = {new BInteger(10)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testReturnOneParamOneReturnArg", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(10, ((BInteger) returns[0]).intValue());
    }

    @Test(description = "Test one parameter and one return value")
    public void testReturnOneParamOneVarDclOneReturnArg() {
        BValue[] args = {new BInteger(10)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testReturnOneParamOneVarDclOneReturnArg", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(20, ((BInteger) returns[0]).intValue());
    }

    @Test(description = "Test two return values")
    public void testReturnTwoVarDclsTwoReturnArgs() {
        BValue[] args = {};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testReturnNoParamTwoVarDclsTwoReturnArgs", args);

        Assert.assertEquals(returns.length, 2);
        Assert.assertEquals(10, ((BInteger) returns[0]).intValue());
        Assert.assertEquals("john", ((BString) returns[1]).stringValue());
    }

    @Test(description = "Test three return values")
    public void testReturnThreeVarDclsThreeReturnArgs() {
        BValue[] args = {new BInteger(10), new BInteger(30)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testReturnThreeVarDclsThreeReturnArgs", args);

        Assert.assertEquals(returns.length, 3);
        Assert.assertEquals(50, ((BInteger) returns[0]).intValue());
        Assert.assertEquals("john", ((BString) returns[1]).stringValue());
        Assert.assertEquals(1.0f, ((BFloat) returns[2]).floatValue(), DELTA);
    }

    @Test(description = "Test one parameter and one return value")
    public void testSplitString() {
        BValue[] args = {};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testSplitString", args);

        Assert.assertEquals(returns.length, 3);
        Assert.assertEquals("section1", ((BString) returns[0]).stringValue());
        Assert.assertEquals("section2", ((BString) returns[1]).stringValue());
        Assert.assertEquals("section3", ((BString) returns[2]).stringValue());
    }

    @Test(description = "Test one parameter and one return value")
    public void testToUpperUtil() {
        BValue[] args = {new BString("section")};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testToUpperUtil", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals("SECTION", ((BString) returns[0]).stringValue());
    }

    @Test(description = "Test one parameter and one return value")
    public void testToUpperUtilDouble() {
        BValue[] args = {new BString("name1"), new BString("name2")};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testToUpperUtilDouble", args);

        Assert.assertEquals(returns.length, 2);
        Assert.assertEquals("NAME1", ((BString) returns[0]).stringValue());
        Assert.assertEquals("NAME2", ((BString) returns[1]).stringValue());
    }

    @Test(description = "Test one parameter and one return value")
    public void testToUpper() {
        BValue[] args = {new BString("section")};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testToUpper", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals("SECTION", ((BString) returns[0]).stringValue());
    }

    @Test(description = "Test one parameter and one return value")
    public void testToUpper1() {
        BValue[] args = {new BString("name1"), new BString("name2")};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testToUpper1", args);

        Assert.assertEquals(returns.length, 2);
        Assert.assertEquals("SECTION", ((BString) returns[0]).stringValue());
        Assert.assertEquals("name2", ((BString) returns[1]).stringValue());
    }

    @Test(description = "Test one parameter and one return value")
    public void testToUpper2() {
        BValue[] args = {new BString("name1"), new BString("name2")};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testToUpper2", args);

        Assert.assertEquals(returns.length, 2);
        Assert.assertEquals("name1", ((BString) returns[0]).stringValue());
        Assert.assertEquals("SECTION", ((BString) returns[1]).stringValue());
    }

    @Test(description = "Test one parameter and one return value")
    public void testToUpper3() {
        BValue[] args = {new BString("name1"), new BString("name2")};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testToUpper3", args);

        Assert.assertEquals(returns.length, 2);
        Assert.assertEquals("SECTION", ((BString) returns[0]).stringValue());
        Assert.assertEquals("SECTION", ((BString) returns[1]).stringValue());
    }

    @Test(description = "Test one parameter and one return value")
    public void testToUpper4() {
        BValue[] args = {new BString("name1"), new BString("name2")};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testToUpper4", args);

        Assert.assertEquals(returns.length, 2);
        Assert.assertEquals("NAME1", ((BString) returns[0]).stringValue());
        Assert.assertEquals("NAME2", ((BString) returns[1]).stringValue());
    }

    @Test(description = "Test return with three return arguments")
    public void testReturnWithThreeArguments() {
        BValue[] args = {};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "testReturnWithThreeArguments", args);

        Assert.assertEquals(returns.length, 3);
        Assert.assertEquals(10, ((BInteger) returns[0]).intValue());
        Assert.assertEquals("foo", ((BString) returns[1]).stringValue());
        Assert.assertEquals(4, ((BInteger) returns[2]).intValue());
    }

    public static void main(String[] args) {
        ReturnStmtTest test = new ReturnStmtTest();
        test.setup();
        test.testReturnWithThreeArguments();
    }
}
