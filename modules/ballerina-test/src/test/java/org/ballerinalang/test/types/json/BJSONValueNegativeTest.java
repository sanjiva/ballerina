/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
package org.ballerinalang.test.types.json;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.util.exceptions.BLangRuntimeException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test class for ballerina map.
 */
public class BJSONValueNegativeTest {

    private CompileResult compileResult;

    @BeforeClass
    public void setup() {
        compileResult = BCompileUtil.compile("test-src/types/jsontype/json-value-negative.bal");
    }

    @Test(expectedExceptions = {BLangRuntimeException.class},
          expectedExceptionsMessageRegExp = "error: NullReferenceException.*")
    public void testGetFromNull() {
        BRunUtil.invoke(compileResult, "testGetFromNull");
    }

    @Test(expectedExceptions = {BLangRuntimeException.class},
          expectedExceptionsMessageRegExp = "error: NullReferenceException.*")
    public void testAddToNull() {
        BRunUtil.invoke(compileResult, "testAddToNull");
    }
}
