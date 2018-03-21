/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.ballerinalang.test.nativeimpl.functions.regex;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStringArray;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test Native functions in ballerina regular expression support.
 */
public class RegexTest {

    private static final String s1 = "WSO2 Inc.";
    private CompileResult result;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/regex/regex-test.bal");
    }

    @Test(description = "Test for executing on matches regex method")
    public void testMatches() {
        BValue[] args = {new BString(s1), new BString("WSO2.*")};
        BValue[] returns = BRunUtil.invoke(result, "matches", args);
        Assert.assertTrue(returns[0] instanceof BBoolean);
        Assert.assertEquals(((BBoolean) returns[0]).booleanValue(), true);
    }

    @Test(description = "Test for executing on matches regex method -  negative case")
    public void testMatchesNegative() {
        BValue[] args = {new BString("Virtusa"), new BString("WSO2.*")};
        BValue[] returns = BRunUtil.invoke(result, "matches", args);
        Assert.assertTrue(returns[0] instanceof BBoolean);
        Assert.assertEquals(((BBoolean) returns[0]).booleanValue(), false);
    }

    @Test(description = "Test for executing on find all regex method")
    public void testFindAll() {
        BValue[] args = {new BString("This is a sentence."), new BString("[a-zA-Z]*is")};
        BValue[] returns = BRunUtil.invoke(result, "findAll", args);
        Assert.assertTrue(returns[0] instanceof BStringArray);
        BStringArray bStringArray = (BStringArray) returns[0];
        Assert.assertEquals(bStringArray.get(0), "This");
        Assert.assertEquals(bStringArray.get(1), "is");
    }

    @Test(description = "Test for executing on replace all regex method")
    public void testReplaceAllRgx() {
        BValue[] args = {new BString("abc is not abc as abc anymore"), new BString("[a-zA-Z]*bc"),
                new BString("xyz")};
        BValue[] returns = BRunUtil.invoke(result, "replaceAllRgx", args);
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "xyz is not xyz as xyz anymore");
    }

    @Test(description = "Test for executing on replace first regex method")
    public void testReplaceFirstRgx() {
        BValue[] args = {new BString("abc is not abc as abc anymore"), new BString("[a-zA-Z]*bc"),
                new BString("xyz")};
        BValue[] returns = BRunUtil.invoke(result, "replaceFirstRgx", args);
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "xyz is not abc as abc anymore");
    }

    @Test(description = "Test for executing on invalid regex pattern")
    public void testInvalidPattern() {
        BValue[] args = {new BString("[")};
        BValue[] returns = BRunUtil.invoke(result, "invalidPattern", args);
        Assert.assertNotNull(returns[0]);
        Assert.assertTrue(returns[0] instanceof BStruct);
        Assert.assertEquals(((BStruct) returns[0]).getStringField(0), "Unclosed character class near index 0\n" +
                "[\n" +
                "^");
    }

    @Test(description = "Test for executing regex functions on non-initialized pattern")
    public void testInvalidPatternWithMatch() {
        BValue[] args = {new BString(s1), new BString("[")};
        BValue[] returns = BRunUtil.invoke(result, "matches", args);
        Assert.assertNotNull(returns[0]);
        Assert.assertTrue(returns[0] instanceof BStruct);
        Assert.assertEquals(((BStruct) returns[0]).getStringField(0), "Unclosed character class near index 0\n" +
                "[\n" +
                "^");
    }

    @Test(description = "Test for executing multiple regex functions on same pattern")
    public void testMultipleReplaceFirst() {
        BValue[] args = {new BString("abc is not abc as abc anymore"), new BString("[a-zA-Z]*bc"),
                new BString("xyz")};
        BValue[] returns = BRunUtil.invoke(result, "multipleReplaceFirst", args);
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "xyz is not xyz as abc anymore");
    }

}
