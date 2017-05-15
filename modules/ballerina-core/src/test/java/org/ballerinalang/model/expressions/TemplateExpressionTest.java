/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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

import org.ballerinalang.bre.SymScope;
import org.ballerinalang.core.utils.BTestUtils;
import org.ballerinalang.model.BLangProgram;
import org.ballerinalang.model.SymbolName;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BJSON;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.BXML;
import org.ballerinalang.natives.BuiltInNativeConstructLoader;
import org.ballerinalang.runtime.internal.GlobalScopeHolder;
import org.ballerinalang.util.exceptions.SemanticException;
import org.ballerinalang.util.program.BLangFunctions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test class to validate the backtick based inline xml and json definitions.
 */
public class TemplateExpressionTest {
    private BLangProgram bLangProgram;
    
    @BeforeClass
    public void setup() {
        // Add Native functions.
        SymScope symScope = GlobalScopeHolder.getInstance().getScope();
        if (symScope.lookup(new SymbolName("ballerina.model.system:print_string")) == null) {
            BuiltInNativeConstructLoader.loadConstructs();
        }
        bLangProgram = BTestUtils.parseBalFile("lang/expressions/template-expr.bal");
    }

    @Test(description = "Test XML backtick expression definition")
    public void testBacktickXMLExpr() {
        BValue[] args = { new BString("WSO2")};
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "backtickXMLTest", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BXML.class);
        String expected = "<name>John</name>";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test JSON backtick expression definition")
    public void testJSONInit() {
        BValue[] args = { new BString("WSO2")};
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testJSONInit", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BJSON.class);
        String expected = "{\"name\":\"John\"}";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test JSON backtick expression with string variable reference")
    public void testStringVariableAccessInJSONInit() {
        BValue[] args = { new BString("WSO2")};
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testStringVariableAccessInJSONInit", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BJSON.class);
        String expected = "{\"name\":\"WSO2\"}";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test XML backtick expression with variable reference")
    public void testBacktickXMLVariableAccessExpr() {
        BValue[] args = { new BString("WSO2")};
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "backtickVariableAccessXML", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BXML.class);
        String expected = "<name>WSO2</name>";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test JSON backtick expression with integer variable reference")
    public void testIntegerVariableAccessInJSONInit() {
        BValue[] args = { new BInteger(11)};
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testIntegerVariableAccessInJSONInit", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BJSON.class);
        String expected = "{\"age\":11}";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test JSON backtick expression with embedding full JSON")
    public void testEnrichFullJSON() {
        BValue[] args = { new BInteger(11)};
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testEnrichFullJSON", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BJSON.class);
        String expected =  "{\"name\":\"John\"}";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test JSON backtick expression with multiple variables embedding full JSON")
    public void testMultipleVariablesInJSONInit() {
        BValue[] args = { new BString("Chanaka"), new BString("Fernando")};
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testMultipleVariablesInJSONInit", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BJSON.class);
        String expected =  "{\"name\":{\"first_name\":\"Chanaka\",\"last_name\":\"Fernando\"}}";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test JSON backtick expression with int and string arrays variable reference")
    public void testArrayVariableAccessInJSONInit() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testArrayVariableAccessInJSONInit");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BJSON.class);
        String expected = "{\"strIndex0\":\"value0\",\"intIndex2\":2,\"strIndex2\":\"value2\"}";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test XML backtick expression with int and string arrays variable reference")
    public void testBacktickXMLArrayVariableAccess() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "backtickXMLArrayVariableAccess");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BXML.class);
        String expected = "<root><stringIndex1>value1</stringIndex1><intIndex1>1</intIndex1></root>";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test JSON backtick expression with map variable reference")
    public void testMapVariableAccessInJSONInit() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testMapVariableAccessInJSONInit");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BJSON.class);
        String expected = "{\"val1\":\"value0\",\"val2\":1}";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test XML backtick expression with map variable reference")
    public void testBacktickXMLMapVariableAccess() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "backtickXMLMapVariableAccess");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BXML.class);
        String expected = "<root><stringIndex0>value0</stringIndex0><intIndex1>1</intIndex1></root>";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test(description = "Test JSON backtick expression with boolean and integers as string values")
    public void testBooleanIntegerValuesAsStringsInJSONInit() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testBooleanIntegerValuesAsStringsInJSONInit");
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BJSON.class);
        String expected = "{\"intStrIndex0\":\"0\",\"intStrIndex1\":\"1\","
                + "\"boolStrIndex0\":\"true\",\"boolStrIndex1\":\"false\"}";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }
    
    @Test(description = "Test backtick expression for JSON type",
            expectedExceptions = {SemanticException.class },
            expectedExceptionsMessageRegExp = "json-backtick-expr.bal:2: incompatible types: expected xml")
    public void testBacktickJSON() {
        bLangProgram = BTestUtils.parseBalFile("lang/expressions/json-backtick-expr.bal");
    }
}
