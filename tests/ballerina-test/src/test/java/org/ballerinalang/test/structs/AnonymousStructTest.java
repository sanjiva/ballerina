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
package org.ballerinalang.test.structs;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases for anonymous structs.
 *
 * @since 0.94
 */
@Test(groups = {"broken"})
public class AnonymousStructTest {

    private CompileResult compileResult;

    @BeforeClass
    public void setup() {
        compileResult = BCompileUtil.compile("test-src/structs/anon-struct.bal");
    }

    @Test(description = "Test Anonymous struct in a function parameter declaration")
    public void testAnonStructAsFuncParam() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAnonStructAsFuncParam");

        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 24);
    }

    @Test(description = "Test Anonymous struct in a local variable declaration")
    public void testAnonStructAsLocalVar() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAnonStructAsLocalVar");

        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 11);
    }

    @Test(description = "Test Anonymous struct in a package variable declaration")
    public void testAnonStructAsPkgVar() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAnonStructAsPkgVar");

        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "sameera:jayasoma:100");
    }

    @Test(description = "Test Anonymous struct in a struct field")
    public void testAnonStructAsStructField() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAnonStructAsStructField");

        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "JAN:12 Gemba St APT 134:CA:sam");
    }
}
