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

package org.ballerinalang.test.taintchecking.connectors;

import org.ballerinalang.launcher.util.BAssertUtil;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test IO related natives for taint checking operations.
 */
public class IOTest {

    @Test
    public void testCharacterIO() {
        CompileResult result = BCompileUtil
                .compile("test-src/taintchecking/connectors/character-io.bal");
        Assert.assertTrue(result.getDiagnostics().length == 0);
    }

    @Test
    public void testCharacterIONegative() {
        CompileResult result = BCompileUtil
                .compile("test-src/taintchecking/connectors/character-io-negative.bal");
        Assert.assertTrue(result.getDiagnostics().length == 4);
        BAssertUtil.validateError(result, 0, "tainted value passed to sensitive parameter 'path'", 8, 43);
        BAssertUtil.validateError(result, 1, "tainted value passed to sensitive parameter 'accessMode'", 8, 53);
        BAssertUtil.validateError(result, 2, "tainted value passed to sensitive parameter 'numberOfChars'", 20, 40);
        BAssertUtil.validateError(result, 3, "tainted value passed to sensitive parameter 'sensitiveValue'", 22, 18);
    }

    @Test
    public void testFileIONegative() {
        CompileResult result = BCompileUtil
                .compile("test-src/taintchecking/connectors/file-read-negative.bal");
        Assert.assertTrue(result.getDiagnostics().length == 4);
        BAssertUtil.validateError(result, 0, "tainted value passed to sensitive parameter 'accessMode'", 6, 17);
        BAssertUtil.validateError(result, 1, "tainted value passed to sensitive parameter 'accessMode'", 8, 50);
        BAssertUtil.validateError(result, 2, "tainted value passed to sensitive parameter 'nBytes'", 14, 34);
        BAssertUtil.validateError(result, 3, "tainted value passed to sensitive parameter 'sensitiveValue'", 16, 18);
    }
}
