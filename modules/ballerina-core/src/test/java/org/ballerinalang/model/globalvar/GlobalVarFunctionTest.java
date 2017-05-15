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

package org.ballerinalang.model.globalvar;

import org.ballerinalang.core.utils.BTestUtils;
import org.ballerinalang.model.BLangProgram;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BJSON;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.program.BLangFunctions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Global variable function test.
 */
public class GlobalVarFunctionTest {

    @BeforeClass
    public void setup() {

    }

    @Test(description = "Test Defining global variables")
    public void testDefiningGlobalVar() {
        BLangProgram bLangProgram = BTestUtils.parseBalFile("lang/globalvar/global-var-function.bal");
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "getGlobalVars");
        Assert.assertEquals(returns.length, 4);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        Assert.assertSame(returns[1].getClass(), BString.class);
        Assert.assertSame(returns[2].getClass(), BFloat.class);
        Assert.assertSame(returns[3].getClass(), BInteger.class);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 800);
        Assert.assertEquals(((BString) returns[1]).stringValue(), "value");
        Assert.assertEquals(((BFloat) returns[2]).floatValue(), 99.34323);
        Assert.assertEquals(((BInteger) returns[3]).intValue(), 88343);
    }

    @Test(description = "Test access global variable within function")
    public void testAccessGlobalVarWithinFunctions() {
        BLangProgram bLangProgram = BTestUtils.parseBalFile("lang/globalvar/global-var-function.bal");
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "accessGlobalVar");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 89143);
    }

    @Test(description = "Test change global var within functions")
    public void testChangeGlobalVarWithinFunction() {
        BLangProgram bLangProgram = BTestUtils.parseBalFile("lang/globalvar/global-var-function.bal");

        BValue[] args = {new BInteger(88)};
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "changeGlobalVar", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BFloat.class);

        Assert.assertEquals(((BFloat) returns[0]).floatValue(), 165.0);


        BLangProgram bLangProgram1 = BTestUtils.parseBalFile("lang/globalvar/global-var-function.bal");

        BValue[] returnsChanged = BLangFunctions.invoke(bLangProgram1, "getGlobalFloatVar");

        Assert.assertEquals(returnsChanged.length, 1);
        Assert.assertSame(returnsChanged[0].getClass(), BFloat.class);

        Assert.assertEquals(((BFloat) returnsChanged[0]).floatValue(), 80.0);
    }

    @Test(description = "Test assigning global variable to another global variable")
    public void testAssignGlobalVarToAnotherGlobalVar() {
        BLangProgram bLangProgram = BTestUtils.parseBalFile("lang/globalvar/global-var-function.bal");

        BValue[] returns = BLangFunctions.invoke(bLangProgram, "getGlobalVarFloat1");

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BFloat.class);

        Assert.assertEquals(((BFloat) returns[0]).floatValue(), 99.34323);
    }

    @Test(description = "Test assigning global var within a function")
    public void testInitializingGlobalVarWithinFunction() {
        BLangProgram bLangProgram = BTestUtils.parseBalFile("lang/globalvar/global-var-function.bal");

        BValue[] returns = BLangFunctions.invoke(bLangProgram, "initializeGlobalVarSeparately");

        Assert.assertEquals(returns.length, 2);
        Assert.assertSame(returns[0].getClass(), BJSON.class);
        Assert.assertSame(returns[1].getClass(), BFloat.class);

        Assert.assertEquals(((BJSON) returns[0]).stringValue(), "{\"name\":\"James\",\"age\":30}");
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 3432.3423);
    }

}
