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
package org.ballerinalang.test.closures;

import org.ballerinalang.launcher.util.BAssertUtil;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test cases for closure related negative scenarios in ballerina.
 */
public class ClosureNegativeTest {

    @Test(description = "Test private field access")
    public void testPrivateFieldAccess() {
        CompileResult compileResult = BCompileUtil.compile("test-src/closures/closure-negative.bal");
        Assert.assertEquals(compileResult.getErrorCount(), 14);
        int index = 0;
        BAssertUtil.validateError(compileResult, index++, "undefined symbol 'functionR'", 6, 56);
        BAssertUtil.validateError(compileResult, index++, "undefined symbol 'methodInt3'", 17, 44);
        BAssertUtil.validateError(compileResult, index++, "cannot assign a value to function argument 'a'", 29, 9);
        BAssertUtil.validateError(compileResult, index++, "cannot assign a value to function argument 'fOut'", 34, 17);
        BAssertUtil.validateError(compileResult, index++, "redeclared symbol 'a'", 50, 9);
        BAssertUtil.validateError(compileResult, index++, "cannot assign a value to function argument 'a'", 56, 13);
        BAssertUtil.validateError(compileResult, index++, "redeclared symbol 'a'", 64, 17);
        BAssertUtil.validateError(compileResult, index++, "undefined symbol 'l'", 81, 58);
        BAssertUtil.validateError(compileResult, index++, "undefined symbol 'm'", 81, 62);
        BAssertUtil.validateError(compileResult, index++, "undefined symbol 'n'", 81, 66);
        BAssertUtil.validateError(compileResult, index++, "undefined symbol 'm'", 84, 40);
        BAssertUtil.validateError(compileResult, index++, "undefined symbol 'n'", 84, 44);
        BAssertUtil.validateError(compileResult, index++, "undefined symbol 'n'", 87, 36);
        BAssertUtil.validateError(compileResult, index++, "undefined symbol 'm'", 96, 24);
    }
}
