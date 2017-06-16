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
package org.ballerinalang.model.any;

import org.ballerinalang.core.utils.BTestUtils;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BJSON;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.program.BLangFunctions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class contains methods to test the any type implementation success scenarios.
 *
 * @since 0.85
 */
public class BAnyTypeSuccessScenariosTest {
    private ProgramFile programFile;

    @BeforeClass
    public void setup() {
        programFile = BTestUtils.getProgramFile("lang/any/any-type-success.bal");
    }

    @Test(description = "Test any type as a return value with actual json returning")
    public void testAnyReturnWithJson() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "jsonReturnTest");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BJSON.class);
        BJSON json = (BJSON) returns[0];
        Assert.assertEquals(json.stringValue(), "{\"PropertyName\":\"Value\"}", "Invalid json value returned.");
    }

//TODO fix below scenario - basically need to rewrite the tree in method visit(ReturnStmt returnStmt) in
// SemanticAnalyser
//    @Test(description = "Test any type as a parameter for function and explicit casting")
//    public void testAnyAsParameterForFunction() {
//        BValue[] returns = BLangFunctions.invokeNew(programFile, "anyMethodParameter");
//        Assert.assertEquals(returns.length, 1);
//        Assert.assertSame(returns[0].getClass(), BInteger.class);
//        BInteger intReturn = (BInteger) returns[0];
//        Assert.assertEquals(intReturn.intValue(), 9, "Invalid integer value returned.");
//    }

    @Test(description = "Test any type as a struct parameter with boolean value")
    public void testAnyAsStructParam() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "anyInStructTest");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BBoolean.class);
        BBoolean bBoolean = (BBoolean) returns[0];
        Assert.assertEquals(bBoolean.booleanValue(), true, "Invalid boolean value returned.");
    }

    @Test(description = "Test float value in any type get casted to int in two steps")
    public void testFloatInAnyCastToInt() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "successfulIntCasting");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        BInteger intVal = (BInteger) returns[0];
        Assert.assertEquals(intVal.intValue(), 5, "Invalid int value returned.");
    }

    @Test(description = "Test any to any explicit cast")
    public void testAnyToAnyCast() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "anyToAnyExplicitCasting");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BJSON.class);
        BJSON json = (BJSON) returns[0];
        Assert.assertEquals(json.stringValue(), "{\"PropertyName\":\"Value\"}", "Invalid json value returned.");
    }

    @Test(description = "Test Multiple returns with any")
    public void testMultipleReturnWithAny() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "multipleReturnWithAny");
        Assert.assertEquals(returns.length, 2);
        Assert.assertSame(returns[0].getClass(), BJSON.class);
        Assert.assertSame(returns[1].getClass(), BInteger.class);
        BJSON json = (BJSON) returns[0];
        BInteger intVal = (BInteger) returns[1];
        Assert.assertEquals(json.stringValue(), "{\"PropertyName\":\"Value\"}", "Invalid json value returned.");
        Assert.assertEquals(intVal.intValue(), 7, "Invalid int value returned.");
    }

    @Test(description = "Test multiple params with any")
    public void testMultipleParamWithAny() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "multipleParamWithAny");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        BInteger intVal = (BInteger) returns[0];
        Assert.assertEquals(intVal.intValue(), 5, "Invalid int value returned.");
    }

    @Test(description = "Test variable init with any")
    public void variableDefTest() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "variableDefTest");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        BInteger intVal = (BInteger) returns[0];
        Assert.assertEquals(intVal.intValue(), 5, "Invalid int value returned.");
    }

    @Test(description = "Test any variable assignment with float")
    public void assignmentTest() {
        BValue[] returns = BLangFunctions.invokeNew(programFile, "assignmentTest");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BFloat.class);
        BFloat floatVal = (BFloat) returns[0];
        Assert.assertEquals(floatVal.floatValue(), 44.3d, "Invalid float value returned.");
    }

}
