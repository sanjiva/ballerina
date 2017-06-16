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
package org.ballerinalang.core.lang.worker;

import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.nativeimpl.util.BTestUtils;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.program.BLangFunctions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test case for fork join.
 */
public class ForkJoinReturnAnyTest {
    private ProgramFile bProgramFile;

    @BeforeClass
    public void setup() {
        bProgramFile = BTestUtils.getProgramFile("samples/fork-join-return-any.bal");
    }


//    @Test(description = "Test Fork Join With workers returning any type")
//    public void testForkJoinReturnAnyType() {
//        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testForkJoinReturnAnyType");
//        Assert.assertEquals(returns.length, 2);
//        Assert.assertTrue(returns[0] instanceof BInteger);
//        Assert.assertTrue(returns[1] instanceof BString);
//        //Assert.assertEquals(((BMap) returns[0]).size(), 4);
//    }

    @Test(description = "Test Fork Join With workers returning any type")
    public void testForkJoinReturnAnyType() {
        BValue[] returns = BLangFunctions.invokeNew(bProgramFile, "testForkJoinReturnAnyType");
        Assert.assertEquals(returns.length, 2);
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertTrue(returns[1] instanceof BString);
        //Assert.assertEquals(((BMap) returns[0]).size(), 4);
    }
}
