/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.test.worker;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Basic worker related tests.
 */
public class BasicWorkerTest {

    private CompileResult result;
    
    @BeforeClass
    public void setup() {
        this.result = BCompileUtil.compile("test-src/workers/basic-worker-actions.bal");
        Assert.assertEquals(result.getErrorCount(), 0);
    }
    
    @Test
    public void workerDeclTest() {
        BRunUtil.invoke(result, "workerDeclTest", new BValue[0]);
    }
    
    @Test
    public void simpleWorkerMessagePassingTest() {
        BRunUtil.invoke(result, "simpleWorkerMessagePassingTest", new BValue[0]);
    }
    
    @Test
    public void basicForkJoinTest() {
        BValue[] vals = BRunUtil.invoke(result, "basicForkJoinTest", new BValue[0]);
        Assert.assertEquals(vals.length, 1);
        Assert.assertEquals(((BInteger) vals[0]).intValue(), 10);
    }
    
    @Test
    public void forkJoinWithMessageParsingTest() {
        BValue[] vals = BRunUtil.invoke(result, "forkJoinWithMessageParsingTest", new BValue[0]);
        Assert.assertEquals(vals.length, 1);
        Assert.assertEquals(((BInteger) vals[0]).intValue(), 5);
    }
    
    @Test
    public void forkJoinWithSingleForkMessages() {
        BValue[] vals = BRunUtil.invoke(result, "forkJoinWithSingleForkMessages", new BValue[0]);
        Assert.assertEquals(vals.length, 1);
        Assert.assertEquals(((BInteger) vals[0]).intValue(), 5);
    }
    
    @Test
    public void forkJoinWithMultipleForkMessages() {
        BValue[] vals = BRunUtil.invoke(result, "forkJoinWithMultipleForkMessages", new BValue[0]);
        Assert.assertEquals(vals.length, 1);
        Assert.assertEquals(((BInteger) vals[0]).intValue(), 5);
    }
    
    @Test
    public void forkJoinWithSomeJoin() {
        BValue[] vals = BRunUtil.invoke(result, "forkJoinWithSomeJoin", new BValue[0]);
        Assert.assertEquals(vals.length, 1);
        BInteger val = (BInteger) vals[0];
        Assert.assertEquals(val.intValue(), 15);
    }
    
    @Test
    public void workerReturnTest() {
        BValue[] vals = BRunUtil.invoke(result, "workerReturnTest", new BValue[0]);
        Assert.assertEquals(vals.length, 1);
        BInteger ret = (BInteger) vals[0];
        Assert.assertEquals(ret.intValue(), 51);
    }
    
}
