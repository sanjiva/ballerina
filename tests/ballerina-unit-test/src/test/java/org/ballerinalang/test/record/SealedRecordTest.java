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
package org.ballerinalang.test.record;

import org.ballerinalang.launcher.util.BAssertUtil;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases for user defined sealed record types in Ballerina.
 */
public class SealedRecordTest {

    private CompileResult compileResult;

    @BeforeClass
    public void setup() {
        compileResult = BCompileUtil.compile("test-src/record/sealed_record.bal");
    }

    @Test(description = "Test using expressions as index for record arrays")
    public void testExpressionAsIndex() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testExpressionAsIndex");
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "Jane");
    }

    @Test(description = "Test using records inside records")
    public void testStructOfStructs() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testStructOfStruct");

        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "USA");
    }

    @Test(description = "Test returning fields of a record")
    public void testReturnStructAttributes() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testReturnStructAttributes");

        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "emily");
    }

    @Test(description = "Test using record expression as a index in another record expression")
    public void testStructExpressionAsIndex() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testStructExpressionAsIndex");
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "emily");
    }

    @Test(description = "Test default value of a record field")
    public void testDefaultValue() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testDefaultVal");

        // Check default value of a field where the default value is set
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "default first name");

        // Check the default value of a field where the default value is not set
        Assert.assertTrue(returns[1] instanceof BString);
        Assert.assertEquals(returns[1].stringValue(), "");

        Assert.assertTrue(returns[2] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[2]).intValue(), 999);
    }

    @Test(description = "Test default value of a nested record field")
    public void testNestedFieldDefaultValue() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testNestedFieldDefaultVal");

        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "default first name");

        Assert.assertTrue(returns[1] instanceof BString);
        Assert.assertEquals(returns[1].stringValue(), "Smith");

        Assert.assertTrue(returns[2] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[2]).intValue(), 999);
    }

    @Test(description = "Test default value of a nested record field")
    public void testNestedStructInit() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testNestedStructInit");

        Assert.assertTrue(returns[0] instanceof BMap);
        BMap<String, BValue> person = ((BMap<String, BValue>) returns[0]);
        Assert.assertEquals(person.get("name").stringValue(), "aaa");
        Assert.assertEquals(((BInteger) person.get("age")).intValue(), 25);

        Assert.assertTrue(person.get("parent") instanceof BMap);
        BMap<String, BValue> parent = ((BMap<String, BValue>) person.get("parent"));
        Assert.assertEquals(parent.get("name").stringValue(), "bbb");
        Assert.assertEquals(((BInteger) parent.get("age")).intValue(), 50);
    }

    @Test(description = "Test negative default values in record")
    public void testNegativeDefaultValue() {
        BValue[] returns = BRunUtil.invoke(compileResult, "getStructNegativeValues");
        Assert.assertEquals(returns.length, 4);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        Assert.assertSame(returns[1].getClass(), BInteger.class);
        Assert.assertSame(returns[2].getClass(), BFloat.class);
        Assert.assertSame(returns[3].getClass(), BFloat.class);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), -9);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), -8);
        Assert.assertEquals(((BFloat) returns[2]).floatValue(), -88.234);
        Assert.assertEquals(((BFloat) returns[3]).floatValue(), -24.99);
    }

    @Test(description = "Test negative default values in record")
    public void testStructToString() {
        BValue[] returns = BRunUtil.invoke(compileResult, "getStruct");
        Assert.assertEquals(returns[0].stringValue(), "{name:\"aaa\", lname:\"\", adrs:{}, age:25, " +
                "family:{spouse:\"\", noOfChildren:0, children:[]}, parent:{name:\"bbb\", lname:\"ccc\", " +
                "adrs:{}, age:50, family:{spouse:\"\", noOfChildren:0, children:[]}, parent:null}}");
    }

    @Test
    public void testStructLiteral() {
        CompileResult compileResult = BCompileUtil.compile("test-src/record/sealed_record_literals.bal");
        BValue[] returns = BRunUtil.invoke(compileResult, "testStructLiteral1");
        Assert.assertEquals(returns[0].stringValue(), "{dptName:\"\", employees:[], manager:" +
                "{name:\"default first name\", lname:\"\", adrs:{}, age:999, child:null}}");

        returns = BRunUtil.invoke(compileResult, "testStructLiteral2");
        Assert.assertEquals(returns[0].stringValue(),
                "{name:\"default first name\", lname:\"\", adrs:{}, age:999, child:null}");
    }

    @Test
    public void testStructLiteralInitFunc() {
        CompileResult result = BCompileUtil.compile("test-src/record/nested_sealed_record_inline_init.bal");
        BValue[] returns = BRunUtil.invoke(result, "testCreateStruct");
        Assert.assertEquals(returns[0].stringValue(),
                "{name:\"default first name\", fname:\"\", lname:\"Doe\", adrs:{}, age:999, " +
                        "family:{spouse:\"Jane\", noOfChildren:0, children:[\"Alex\", \"Bob\"]}}");
    }

    @Test (description = "Negative test to test attaching functions to record literal")
    public void testStructLiteralAttachedFunc() {
        CompileResult result = BCompileUtil.compile(
                "test-src/record/sealed_record_literal_with_attached_functions.bal");
        Assert.assertEquals(result.getErrorCount(), 2);
        BAssertUtil.validateError(result, 0, "cannot attach function 'getName' to record type 'Person'", 8, 1);
        BAssertUtil.validateError(result, 1, "undefined symbol 'self'", 9, 12);
    }

    @Test(description = "Test for records defined using the 'record' keyword")
    public void testRecordDefinedWithKeyword() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testStructWithRecordKeyword");

        Assert.assertEquals(returns[0].stringValue(), "{name:\"John\", lname:\"Doe\", address:{\"country\":\"USA\", " +
                "\"state\":\"CA\"}, age:25, family:{spouse:\"\", noOfChildren:0, children:[]}, parent:null, " +
                "designation:\"Software Engineer\"}");
    }

    @Test(description = "Test function pointer as a record field")
    public void testFuncPtrAsAField() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testFuncPtrAsRecordField");
        Assert.assertEquals(returns[0].stringValue(), "Doe, John");
    }

    @Test(description = "Test white space between the type name and ellipsis in rest descriptor")
    public void testRestDescriptorSyntax() {
        CompileResult result = BCompileUtil.compile("test-src/record/sealed_record_negative.bal");

        BAssertUtil.validateError(result, 0, "invalid record rest descriptor", 5, 7);
        BAssertUtil.validateError(result, 1, "invalid record rest descriptor", 12, 9);
        BAssertUtil.validateError(result, 2, "invalid record rest descriptor", 20, 5);
    }
}
