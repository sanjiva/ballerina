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
package org.ballerinalang.test.expressions.invocations;


import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * package init function invocation test.
 *
 * @since 0.8.0
 */
public class PackageInitInvocationTest {

    CompileResult result;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile(this, "test-src", "expressions.invocations.pkg.c");
    }

    @Test
    public void invokeFunctionWithParams() {
        BValue[] values = BRunUtil.invoke(result, "testInitInvocation");
        Assert.assertEquals(values.length, 1);
        Assert.assertTrue(values[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) values[0]).intValue(), 19);
    }
}
