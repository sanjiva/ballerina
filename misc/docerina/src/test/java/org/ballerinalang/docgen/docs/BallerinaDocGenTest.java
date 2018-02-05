/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.docgen.docs;

import org.ballerinalang.docgen.docs.utils.BallerinaDocGenTestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;

import java.io.IOException;
import java.util.Map;

/**
 *  Ballerina doc generation tests.
 */
public class BallerinaDocGenTest {

    private String testResourceRoot;

    @BeforeClass()
    public void setup() {
        testResourceRoot = BallerinaDocGenTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    @Test(description = "Test Single Bal file")
    public void testSingleBalFile() {
        try {
            Map<String, BLangPackage> docsMap = BallerinaDocGenerator
                    .generatePackageDocsFromBallerina(testResourceRoot + "balFiles", "helloWorld.bal");

            Assert.assertNotNull(docsMap);
            Assert.assertEquals(docsMap.size(), 1);
        } catch (IOException e) {
            Assert.fail();
        } finally {
            BallerinaDocGenTestUtils.cleanUp();
        }
    }

    @Test(description = "Test a folder with Bal files")
    public void testFolderWithBalFile() {
        try {
            String path = testResourceRoot + "balFiles/balFolder";
            Map<String, BLangPackage> docsMap = BallerinaDocGenerator.generatePackageDocsFromBallerina(path, path);

            Assert.assertNotNull(docsMap);
            // this folder has 3 bal files. 2 bal files out of those are in same package.
            Assert.assertEquals(docsMap.size(), 2);
            // assert package names
            Assert.assertEquals(docsMap.containsKey("a.b"), true);
            Assert.assertEquals(docsMap.containsKey("a.b.c"), true);
        } catch (IOException e) {
            Assert.fail();
        } finally {
            BallerinaDocGenTestUtils.cleanUp();
        }
    }
}
