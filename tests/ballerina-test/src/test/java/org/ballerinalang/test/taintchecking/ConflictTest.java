/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.taintchecking;

import org.ballerinalang.launcher.util.BAssertUtil;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test conflicting conditions such as recursions and cyclic invocations of functions, that will be addressed by the
 * the conflict resolution mechanism of taint analyzer.
 *
 * @since 0.965.0
 */
public class ConflictTest {

    // Test recursions.

    @Test (enabled = false)
    public void testRecursion() {
        CompileResult result = BCompileUtil.compile("test-src/taintchecking/conflicts/recursion.bal");
        Assert.assertTrue(result.getDiagnostics().length == 1);
        BAssertUtil.validateWarning(result, 0, "taint checking for 'f1' partially done based on return annotations",
                1, 1);
    }

    @Test (enabled = false)
    public void testRecursionNegative() {
        CompileResult result = BCompileUtil.compile("test-src/taintchecking/conflicts/recursion-negative.bal");
        Assert.assertTrue(result.getDiagnostics().length == 1);
        BAssertUtil.validateError(result, 0,
                "taint checking for 'f1' could not complete due to recursion with 'f1', add @tainted or " +
                "@untainted to returns", 3, 12);
    }

    // Test cyclic function invocations.

    @Test (enabled = false)
    public void testCyclicCall() {
        CompileResult result = BCompileUtil.compile("test-src/taintchecking/conflicts/cyclic-call.bal");
        Assert.assertTrue(result.getDiagnostics().length == 1);
        BAssertUtil.validateWarning(result, 0, "taint checking for 'f1' partially done based on return annotations",
                1, 1);
    }

    @Test (enabled = false)
    public void testCyclicCallNegative() {
        CompileResult result = BCompileUtil.compile("test-src/taintchecking/conflicts/cyclic-call-negative.bal");
        Assert.assertTrue(result.getDiagnostics().length == 3);
        BAssertUtil.validateWarning(result, 0,
                "taint checking for 'f3' could not complete due to recursion with 'f1', add @tainted or " +
                "@untainted to returns", 10, 12);
        BAssertUtil.validateError(result, 1,
                "taint checking for 'f1' could not complete due to recursion with 'f2', add @tainted or " +
                "@untainted to returns", 2, 12);
        BAssertUtil.validateError(result, 2,
                "taint checking for 'f2' could not complete due to recursion with 'f3', add @tainted or " +
                "@untainted to returns", 6, 12);
    }
}
