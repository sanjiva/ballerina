/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
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
package org.ballerinalang.test.statements.nextstatement;

import org.ballerinalang.launcher.util.BAssertUtil;
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
 * This contains methods to test next statement in a while loop.
 *
 * @since 0.89
 */
public class NextStmtTest {

    private CompileResult positiveCompileResult;
    private CompileResult negativeCompileResult;

    @BeforeClass
    public void setup() {
        positiveCompileResult = BCompileUtil.compile("test-src/statements/nextstatement/next-stmt.bal");
        negativeCompileResult = BCompileUtil.compile("test-src/statements/nextstatement/next-stmt-negative.bal");
    }

    @Test(description = "Test next statement in a while loop.")
    public void testNextStmtConditionTrue() {
        BValue[] args = {new BInteger(15), new BInteger(5)};
        BValue[] returns = BRunUtil.invoke(positiveCompileResult, "calculateExp1", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        long actual = ((BInteger) returns[0]).intValue();
        long expected = 9;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test next statement in a while loop, where next not in execution path ")
    public void testNextStmtConditionFalse() {
        BValue[] args = {new BInteger(25), new BInteger(15)};
        BValue[] returns = BRunUtil.invoke(positiveCompileResult, "calculateExp1", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        long actual = ((BInteger) returns[0]).intValue();
        long expected = 10;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test next statement in a nested while loop.")
    public void testNextStmtInNestedWhileConditionTrue() {
        BValue[] args = {new BInteger(15), new BInteger(5)};
        BValue[] returns = BRunUtil.invoke(positiveCompileResult, "nestedNextStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        long actual = ((BInteger) returns[0]).intValue();
        long expected = 666;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test next statement in a nested while loop.")
    public void testNextStmtInNestedWhileConditionFalse() {
        BValue[] args = {new BInteger(25), new BInteger(15)};
        BValue[] returns = BRunUtil.invoke(positiveCompileResult, "nestedNextStmt", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        long actual = ((BInteger) returns[0]).intValue();
        long expected = 2486;
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testFinallyWithWhile() {
        BValue[] args = {new BString("next")};
        BValue[] returns = BRunUtil.invoke(positiveCompileResult, "testFinallyWithWhile", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "start->while0->try0->finally0->whileEnd0->" +
                "while1->try1->finally1->whileEnd1->" +
                "while2->try2->next->finally2->" +
                "while3->try3->finally3->whileEnd3->" +
                "while4->try4->finally4->whileEnd4->end");
    }

    @Test
    public void testFinallyWithForeach() {
        BValue[] args = {new BString("next")};
        BValue[] returns = BRunUtil.invoke(positiveCompileResult, "testFinallyWithForeach", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "start->foreach0->try0->finally0->foreachEnd0->" +
                "foreach1->try1->finally1->foreachEnd1->" +
                "foreach2->try2->finally2->foreachEnd2->" +
                "foreach3->try3->next->finally3->" +
                "foreach4->try4->finally4->foreachEnd4->" +
                "foreach5->try5->finally5->foreachEnd5->end");
    }

    @Test(description = "Check invalid next statement location.")
    public void testNegative() {
        Assert.assertEquals(negativeCompileResult.getErrorCount(), 2);
        BAssertUtil.validateError(negativeCompileResult, 0, "next cannot be used outside of a loop", 15, 5);
        BAssertUtil.validateError(negativeCompileResult, 1, "unreachable code", 31, 13);
    }
}
