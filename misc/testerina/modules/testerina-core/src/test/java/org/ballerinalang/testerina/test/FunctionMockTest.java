/*
*   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.testerina.test;

import org.ballerinalang.testerina.core.BTestRunner;
import org.ballerinalang.testerina.core.TesterinaRegistry;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Test cases for ballerina.test package.
 */
public class FunctionMockTest {

    private String sourceRoot = "src/test/resources/";

    @BeforeClass
    public void setup() {
    }

    private void cleanup() {
        TesterinaRegistry.getInstance().setProgramFiles(new ArrayList<>());
        TesterinaRegistry.getInstance().setTestSuites(new HashMap<>());
    }

    @Test
    public void testBefore2() {
        cleanup();
        BTestRunner runner = new BTestRunner();
        runner.runTest(sourceRoot, new Path[]{Paths.get("functionmocktest.pkg"), Paths.get("functionmocktest2.pkg")},
                new ArrayList<>());
        Assert.assertEquals(runner.getTesterinaReport().getTestSummary("functionmocktest.pkg", "passed"), 1);
        Assert.assertEquals(runner.getTesterinaReport().getTestSummary("functionmocktest2.pkg", "passed"), 1);
    }
}
