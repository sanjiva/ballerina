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
package org.ballerinalang.model.structs;

import org.ballerinalang.core.utils.BTestUtils;
import org.ballerinalang.model.BLangProgram;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.ballerinalang.util.exceptions.SemanticException;
import org.ballerinalang.util.program.BLangFunctions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases for user defined struct types in ballerina.
 */
public class StructAccessWithIndexTest {

    private BLangProgram bLangProgram;
    
    @BeforeClass
    public void setup() {
        bLangProgram = BTestUtils.parseBalFile("lang/structs/struct-with-indexed-access.bal");
    }
    
    @Test(description = "Test Basic struct operations")
    public void testBasicStruct() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testCreateStruct");

        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "Jack");

        Assert.assertTrue(returns[1] instanceof BMap);
        BMap<BString, ?> adrsMap = ((BMap) returns[1]);
        Assert.assertEquals(adrsMap.get(new BString("country")), new BString("USA"));
        Assert.assertEquals(adrsMap.get(new BString("state")), new BString("CA"));

        Assert.assertTrue(returns[2] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[2]).intValue(), 25);
    }

    @Test(description = "Test using expressions as index for struct arrays")
    public void testExpressionAsIndex() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testExpressionAsIndex");
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "Jane");
    }
    
    @Test(description = "Test using structs inside structs")
    public void testStructOfStructs() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testStructOfStruct");

        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "USA");
    }
    
    @Test(description = "Test returning fields of a struct")
    public void testReturnStructAttributes() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testReturnStructAttributes");

        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "emily");
    }
    
    @Test(description = "Test using struct expression as a index in another struct expression")
    public void testStructExpressionAsIndex() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testStructExpressionAsIndex");
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "emily");
    }
    
    @Test(description = "Test default value of a struct field")
    public void testDefaultValue() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testDefaultVal");
        
        // Check default value of a field where the default value is set
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "default first name");
        
        // Check the default value of a field where the default value is not set
        Assert.assertTrue(returns[1] instanceof BString);
        Assert.assertEquals(returns[1].stringValue(), "");
        
        Assert.assertTrue(returns[2] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[2]).intValue(), 999);
    }
    
    @Test(description = "Test default value of a nested struct field")
    public void testNestedFieldDefaultValue() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testNestedFieldDefaultVal");
        
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "default first name");
        
        Assert.assertTrue(returns[1] instanceof BString);
        Assert.assertEquals(returns[1].stringValue(), "Smith");
        
        Assert.assertTrue(returns[2] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[2]).intValue(), 999);
    }
    
    /*
     *  Negative tests
     */
    
    @Test(description = "Test accessing an field of a noninitialized struct",
            expectedExceptions = {BallerinaException.class},
            expectedExceptionsMessageRegExp = "field 'employees\\[0\\]' is null")
    public void testGetNonInitField() {
        BLangFunctions.invoke(bLangProgram, "testGetNonInitAttribute");
    }
    
    @Test(description = "Test accessing an arrays field of a noninitialized struct",
            expectedExceptions = {BallerinaException.class},
            expectedExceptionsMessageRegExp = "field 'employees' is null")
    public void testGetNonInitArrayField() {
        BLangFunctions.invoke(bLangProgram, "testGetNonInitArrayAttribute");
    }
    
    @Test(description = "Test accessing the field of a noninitialized struct",
            expectedExceptions = {BallerinaException.class},
            expectedExceptionsMessageRegExp = "field 'dpt' is null")
    public void testGetNonInitLastField() {
        BLangFunctions.invoke(bLangProgram, "testGetNonInitLastAttribute");
    }
    
    @Test(description = "Test setting an field of a noninitialized child struct",
            expectedExceptions = {BallerinaException.class},
            expectedExceptionsMessageRegExp = "field 'family' is null")
    public void testSetNonInitField() {
        BLangFunctions.invoke(bLangProgram, "testSetFieldOfNonInitChildStruct");
    }
    
    @Test(description = "Test setting the field of a noninitialized root struct",
            expectedExceptions = {BallerinaException.class},
            expectedExceptionsMessageRegExp = "field 'dpt' is null")
    public void testSetNonInitLastField() {
        BLangFunctions.invoke(bLangProgram, "testSetFieldOfNonInitStruct");
    }

    @Test(description = "Test accessing an undeclared struct",
            expectedExceptions = {SemanticException.class},
            expectedExceptionsMessageRegExp = "undeclared-struct-access-with-index.bal:4: undefined symbol 'dpt1'")
    public void testUndeclaredStructAccess() {
        BTestUtils.parseBalFile("lang/structs/undeclared-struct-access-with-index.bal");
    }
    
    @Test(description = "Test accessing an undeclared field of a struct",
            expectedExceptions = {SemanticException.class},
            expectedExceptionsMessageRegExp = "undeclared-attribute-access-as-index.bal:5: unknown field 'id' in" +
                " struct 'Department'")
    public void testUndeclaredFieldAccess() {
        BTestUtils.parseBalFile("lang/structs/undeclared-attribute-access-as-index.bal");
    }
    
    @Test(description = "Test accesing a struct with a dynamic index",
            expectedExceptions = {SemanticException.class},
            expectedExceptionsMessageRegExp = "struct-access-with-dynamic-index.bal:15: only static keys are " +
            "supported for accessing struct fields")
    public void testExpressionAsStructIndex() {
        BTestUtils.parseBalFile("lang/structs/struct-access-with-dynamic-index.bal");
    }
    
}
