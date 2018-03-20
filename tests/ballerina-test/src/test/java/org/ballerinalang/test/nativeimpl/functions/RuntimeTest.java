/*
*   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.ballerinalang.test.nativeimpl.functions;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BIntArray;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Properties;

/**
 * Test class for runtime package.
 */
public class RuntimeTest {

    private CompileResult compileResult;
    private CompileResult errorResult;

    @BeforeClass
    public void setup() {
        compileResult = BCompileUtil.compile("test-src/nativeimpl/functions/runtime-test.bal");
        errorResult = BCompileUtil.compile("test-src/nativeimpl/functions/runtime-error.bal");
    }

    @Test
    public void testSleepCurrentThread() {
        long startTime = System.currentTimeMillis();
        BRunUtil.invoke(compileResult, "testSleepCurrentThread");
        long endTime = System.currentTimeMillis();
        Assert.assertTrue((endTime - startTime) >= 1000);
    }
    
    @Test
    public void testConcurrentSleepCurrentThread() {
        BIntArray result = (BIntArray) BRunUtil.invoke(compileResult, "testConcurrentSleep")[0];
        Assert.assertTrue(checkWithErrorMargin(result.get(0), 1000, 500));
        Assert.assertTrue(checkWithErrorMargin(result.get(1), 1000, 500));
        Assert.assertTrue(checkWithErrorMargin(result.get(2), 2000, 500));
        Assert.assertTrue(checkWithErrorMargin(result.get(3), 2000, 500));
        Assert.assertTrue(checkWithErrorMargin(result.get(4), 1000, 500));
    }
    
    private boolean checkWithErrorMargin(long actual, long expected, long error) {
        return actual <= expected + error && actual >= expected - error; 
    }

    @Test
    public void testSetProperty() {
        String key = "BALLERINA";
        String value = "ROCKS";
        BValue[] args = new BValue[2];
        args[0] = new BString(key);
        args[1] = new BString(value);
        BRunUtil.invoke(compileResult, "testSetProperty", args);
        String actualValue = System.getProperty(key);
        Assert.assertEquals(actualValue, value);
    }

    @Test
    public void testGetProperty() {
        String key = "BALLERINA";
        String expectedValue = "ROCKS";
        System.setProperty(key, expectedValue);

        BValue[] args = new BValue[1];
        args[0] = new BString(key);
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetProperty", args);
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), expectedValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetProperties() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetProperties");
        Assert.assertTrue(returns[0] instanceof BMap);
        BMap<String, BString> actualProperties = (BMap<String, BString>) returns[0];
        Properties expectedProperties = System.getProperties();
        Assert.assertEquals(actualProperties.size(), expectedProperties.size());

        actualProperties.keySet().forEach(key -> {
            String property = expectedProperties.getProperty(key);
            Assert.assertNotNull(property);
        });
    }

    @Test
    public void testGetCurrentDirectory() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetCurrentDirectory");
        Assert.assertTrue(returns[0] instanceof BString);
        String expectedValue = System.getProperty("user.dir");
        Assert.assertEquals(returns[0].stringValue(), expectedValue);
    }

    @Test
    public void testGetFileEncoding() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetFileEncoding");
        Assert.assertTrue(returns[0] instanceof BString);
        String expectedValue = System.getProperty("file.encoding");
        Assert.assertEquals(returns[0].stringValue(), expectedValue);
    }

    @Test
    public void testGetCallStack() {
        BValue[] returns = BRunUtil.invoke(errorResult, "testGetCallStack");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "[{callableName:\"getCallStack\", " +
                "packageName:\"ballerina.runtime\", fileName:\"<native>\", lineNumber:0}," +
                " {callableName:\"level2Function\", packageName:\".\", fileName:\"runtime-error.bal\","
                + " lineNumber:12}," +
                " {callableName:\"level1Function\", packageName:\".\", fileName:\"runtime-error.bal\","
                + " lineNumber:8}," +
                " {callableName:\"testGetCallStack\", packageName:\".\", fileName:\"runtime-error.bal\","
                + " lineNumber:4}]");
    }

    @Test
    public void testErrorStackFrame() {
        BValue[] returns = BRunUtil.invoke(errorResult, "testErrorStackFrame");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "{callableName:\"testErrorStackFrame\", packageName:\".\","
                + " fileName:\"runtime-error.bal\", lineNumber:17}");
    }
}
