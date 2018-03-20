/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.test.statements.safeassign;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.exceptions.BLangRuntimeException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class contain safe assignment operator '=?' related test scenarios.
 */
public class SafeAssignmentOperatorTest {

    private CompileResult result;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile(
                "test-src/statements/safeassign/safe_assign_basics.bal");
    }

    @Test(description = "Test basics of safe assignment statement")
    public void testSafeAssignmentBasics1() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeAssignmentBasics1", new BValue[]{});
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BBoolean.class);
        Assert.assertEquals(((BBoolean) returns[0]).booleanValue(), true, "Invalid boolean value returned.");
    }

    @Test(description = "Test basics of safe assignment statement")
    public void testSafeAssignmentBasics2() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeAssignmentBasics2", new BValue[]{});
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BStruct.class);
        BStruct errorStruct = (BStruct) returns[0];
        Assert.assertEquals(errorStruct.getStringField(0),
                "file not found error: /home/sameera/bar.txt", "Invalid error message value returned.");
    }

    @Test(description = "Test basics of safe assignment statement", expectedExceptions = BLangRuntimeException.class,
            expectedExceptionsMessageRegExp = ".*error: error, message: file not found error: /home/sameera/bar.txt.*")
    public void testSafeAssignmentBasics3() {
        BRunUtil.invoke(result, "testSafeAssignmentBasics3", new BValue[]{});
    }

    @Test(description = "Test basics of safe assignment statement", expectedExceptions = BLangRuntimeException.class,
            expectedExceptionsMessageRegExp = ".*error: error, message: file not found error: /home/sameera/bar.txt.*")
    public void testSafeAssignmentBasics4() {
        BRunUtil.invoke(result, "testSafeAssignmentBasics4", new BValue[]{});
    }

    @Test(description = "Test basics of safe assignment statement")
    public void testSafeAssignOpInAssignmentStatement1() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeAssignOpInAssignmentStatement1", new BValue[]{});
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BBoolean.class);
        Assert.assertEquals(((BBoolean) returns[0]).booleanValue(), true, "Invalid boolean value returned.");
    }

    @Test(description = "Test basics of safe assignment statement")
    public void testSafeAssignOpInAssignmentStatement2() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeAssignOpInAssignmentStatement2", new BValue[]{});
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BStruct.class);
        BStruct errorStruct = (BStruct) returns[0];
        Assert.assertEquals(errorStruct.getStringField(0),
                "file not found error: /home/sameera/foo.txt", "Invalid error message value returned.");
    }

    @Test(description = "Test basics of safe assignment statement")
    public void testSafeAssignOpInAssignmentStatement3() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeAssignOpInAssignmentStatement3", new BValue[]{});
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BBoolean.class);
        Assert.assertEquals(((BBoolean) returns[0]).booleanValue(), true, "Invalid boolean value returned.");

    }

    @Test(description = "Test basics of safe assignment statement")
    public void testSafeAssignOpInAssignmentStatement4() {
        BValue[] returns = BRunUtil.invoke(result, "testSafeAssignOpInAssignmentStatement4", new BValue[]{});
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BBoolean.class);
        Assert.assertEquals(((BBoolean) returns[0]).booleanValue(), true, "Invalid boolean value returned.");

    }

    @Test(description = "Test basics of safe assignment statement", expectedExceptions = BLangRuntimeException.class,
            expectedExceptionsMessageRegExp = ".*error: error, message: file not found error: /home/sameera/bar.txt.*")
    public void testSafeAssignOpInAssignmentStatement5() {
        BRunUtil.invoke(result, "testSafeAssignOpInAssignmentStatement5", new BValue[]{});
    }
}

