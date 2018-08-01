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
package org.ballerinalang.test.types.map;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BRefType;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.BXMLItem;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Test class for ballerina map.
 */
public class BMapValueTest {

    private CompileResult programFile;

    @Test
    public void testStandardJavaMap() {
        // Standard Map
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("Chanaka", 1);
        map.put("Udaya", 2);
        map.put("Chanaka", 1);
        assertEquals((int) map.get("Chanaka"), 1);

        for (int i = 0; i < 100; i++) {
            map.put(String.valueOf(i), i);
        }
        assertEquals(map.size(), 102);
        assertEquals((int) map.get("51"), 51);

        map.remove("Chanaka");
        assertEquals(map.size(), 101);

        map.remove("WSO2");
        assertEquals(map.size(), 101);
    }

    @Test
    public void testBMap() {

        BMap<BString, BInteger> map = new BMap<>();
        map.put(new BString("Chanaka"), new BInteger(1));
        map.put(new BString("Udaya"), new BInteger(2));
        map.put(new BString("Chanaka"), new BInteger(1));
        assertEquals(map.get(new BString("Chanaka")), new BInteger(1));
        for (int i = 0; i < 100; i++) {
            map.put(new BString(String.valueOf(i)), new BInteger(i));
        }
        assertEquals(map.size(), 102);
        assertEquals(map.get(new BString("51")), new BInteger(51));

        map.remove(new BString("Chanaka"));
        assertEquals(map.size(), 101);

        map.remove(new BString("Chanaka"));
        assertEquals(map.size(), 101);
    }

    @Test
    public void testBMapClear() {
        BMap<BString, BInteger> map = new BMap<>();
        map.put(new BString("IS"), new BInteger(0));
        map.put(new BString("ESB"), new BInteger(1));
        map.put(new BString("APIM"), new BInteger(2));
        assertEquals(map.size(), 3);
        map.clear();
        assertEquals(map.size(), 0);
    }

    @Test
    public void testBMapHasKey() {
        BMap<BString, BInteger> map = new BMap<>();
        map.put(new BString("IS"), new BInteger(0));
        map.put(new BString("ESB"), new BInteger(1));
        map.put(new BString("APIM"), new BInteger(2));
        assertEquals(map.hasKey(new BString("IS")), true);
        assertEquals(map.hasKey(new BString("ESB")), true);
        assertEquals(map.hasKey(new BString("APIM")), true);
    }

    @Test
    void testGrammar() {
        programFile = BCompileUtil.compile("test-src/types/map/map-value.bal");
    }

    @Test(dependsOnMethods = "testGrammar")
    public void testMapWithAny() {
        BValue[] returnVals = BRunUtil.invoke(programFile, "testMapWithAny", new BValue[0]);
        Assert.assertNotNull(returnVals, "Return values can't be null.");
        Assert.assertEquals(returnVals.length, 1, "Return value size din't match.");
        Assert.assertNotNull(returnVals[0], "Return value can't be null.");
        Assert.assertEquals(returnVals[0].stringValue(), "Lion", "Return value din't match. Expected Lion");
    }

    @Test(dependsOnMethods = "testGrammar")
    public void testMapWithMap() {
        BValue[] returnVals = BRunUtil.invoke(programFile, "testMapWithMap", new BValue[0]);
        Assert.assertNotNull(returnVals, "Return values can't be null.");
        Assert.assertEquals(returnVals.length, 1, "Return value size din't match.");
        Assert.assertNotNull(returnVals[0], "Return value can't be null.");
        Assert.assertEquals(returnVals[0].stringValue(), "item1", "Return value din't match.");
    }

    @Test(dependsOnMethods = "testGrammar")
    public void testMapWithAnyValue() {
        BValue[] returnVals = BRunUtil.invoke(programFile, "testMapWithAnyValue", new BValue[0]);
        Assert.assertNotNull(returnVals, "Return values can't be null.");
        Assert.assertEquals(returnVals.length, 1, "Return value size din't match.");
        Assert.assertNotNull(returnVals[0], "Return value can't be null.");
        Assert.assertEquals(((BInteger) returnVals[0]).intValue(), 5, "Return value din't match.");
    }

    @Test(dependsOnMethods = "testGrammar")
    public void testMapWithAnyDifferentValue() {
        BValue[] returnVals = BRunUtil.invoke(programFile, "testMapWithAnyDifferentValue", new BValue[0]);
        Assert.assertNotNull(returnVals, "Return values can't be null.");
        Assert.assertEquals(returnVals.length, 1, "Return value size din't match.");
        Assert.assertNotNull(returnVals[0], "Return value can't be null.");
        Assert.assertEquals(returnVals[0].stringValue(), "aString", "Return value din't match.");
    }

    @Test(dependsOnMethods = "testGrammar")
    public void testMapWithBinaryExpression() {
        BValue[] returnVals = BRunUtil.invoke(programFile, "testMapWithBinaryExpression", new BValue[0]);
        Assert.assertNotNull(returnVals, "Return values can't be null.");
        Assert.assertEquals(returnVals.length, 1, "Return value size din't match.");
        Assert.assertNotNull(returnVals[0], "Return value can't be null.");
        Assert.assertEquals(((BInteger) returnVals[0]).intValue(), 3, "Return value din't match.");
    }

    @Test(dependsOnMethods = "testGrammar")
    public void testMapWithFunctionInvocations() {
        BValue[] returnVals = BRunUtil.invoke(programFile, "testMapWithFunctionInvocations", new BValue[0]);
        Assert.assertNotNull(returnVals, "Return values can't be null.");
        Assert.assertEquals(returnVals.length, 1, "Return value size din't match.");
        Assert.assertNotNull(returnVals[0], "Return value can't be null.");
        Assert.assertEquals(returnVals[0].stringValue(), "item1", "Return value din't match.");
    }

    @Test(dependsOnMethods = "testGrammar")
    public void testMapWithAnyFunctionInvocations() {
        BValue[] returnVals = BRunUtil.invoke(programFile, "testMapWithAnyFunctionInvocations", new BValue[0]);
        Assert.assertNotNull(returnVals, "Return values can't be null.");
        Assert.assertEquals(returnVals.length, 1, "Return value size din't match.");
        Assert.assertNotNull(returnVals[0], "Return value can't be null.");
        Assert.assertEquals(returnVals[0].stringValue(), "item2", "Return value din't match.");
    }
    @Test(description = "Testing map value access in variableDefStmt")
    void testInvalidGrammar1() {
        CompileResult compileResult = BCompileUtil.compile("test-src/types/map/map-value-validator-1-negative.bal");
        Assert.assertTrue(compileResult.getDiagnostics().length == 1);
        Assert.assertEquals(compileResult.getDiagnostics()[0].getMessage(),
                            "incompatible types: expected 'string', found 'any'");
    }

    @Test(description = "Testing map value access in assignStmt")
    void testInvalidGrammar2() {
        CompileResult compileResult = BCompileUtil.compile("test-src/types/map/map-value-validator-2-negative.bal");
        Assert.assertTrue(compileResult.getDiagnostics().length == 1);
        Assert.assertEquals(compileResult.getDiagnostics()[0].getMessage(),
                            "incompatible types: expected 'string', found 'any'");
    }

    @Test(description = "Testing map value access in binary expression")
    void testInvalidGrammar3() {
        CompileResult compileResult = BCompileUtil.compile("test-src/types/map/map-value-validator-3-negative.bal");
        Assert.assertTrue(compileResult.getDiagnostics().length == 1);
        Assert.assertEquals(compileResult.getDiagnostics()[0].getMessage(),
                            "operator '+' not defined for 'any' and 'int'");
    }
    
    @Test(description = "Testing convert map values to string")
    public void testBMapToString() {
        BMap<String, BRefType> map = new BMap<>();
        map.put(new String("key1"), new BInteger(1));
        map.put(new String("key2"), new BString("foo"));
        map.put(new String("key3"), new BXMLItem("<bar>hello</bar>"));
        
        Assert.assertEquals(map.stringValue(), "{\"key1\":1, \"key2\":\"foo\", \"key3\":<bar>hello</bar>}");
    }

    @Test(dependsOnMethods = "testGrammar")
    public void testMapOrder() {
        BValue[] returnVals = BRunUtil.invoke(programFile, "testMapOrder", new BValue[0]);
        BMap<String, BRefType<?>> m = (BMap) returnVals[0];
        String[] keys = m.keys();
        int counter = 0;
        String[] values = { "Element 1", "Element 2", "Element 3" };
        for (String key : keys) {
            Assert.assertEquals(m.get(key).stringValue(), values[counter]);
            counter++;
        }
        String mapString = m.stringValue();
        Assert.assertEquals(mapString, "{\"key1\":\"Element 1\", \"key2\":\"Element 2\", \"key3\":\"Element 3\"}");
    }

    @Test
    public  void testBMapOrder() {
        BMap<String, BRefType> map = new BMap<>();
        map.put(new String("Entry1"), new BString("foo"));
        map.put(new String("Entry2"), new BString("bar"));
        map.put(new String("Entry3"), new BString("foobar"));

        String[] keys = map.keys();
        Assert.assertEquals(map.get(keys[0]).stringValue(), "foo");
        Assert.assertEquals(map.get(keys[1]).stringValue(), "bar");
        Assert.assertEquals(map.get(keys[2]).stringValue(), "foobar");

    }

    @Test(dependsOnMethods = "testGrammar")
    public void testMapSynchronization() {
        BValue[] returnVals = BRunUtil.invoke(programFile, "testMapSynchronization", new BValue[0]);
        Assert.assertEquals(((BInteger) returnVals[0]).intValue(), 2000);
    }
}
