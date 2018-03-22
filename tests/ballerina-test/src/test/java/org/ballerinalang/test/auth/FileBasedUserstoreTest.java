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

package org.ballerinalang.test.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBasedUserstoreTest {
    private static final Log log = LogFactory.getLog(FileBasedUserstoreTest.class);
    private static final String BALLERINA_CONF = "ballerina.conf";
    private CompileResult compileResult;
    private String resourceRoot;

    @BeforeClass
    public void setup() throws Exception {
        resourceRoot = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        Path sourceRoot = Paths.get(resourceRoot, "test-src", "auth");
        Path ballerinaConfPath = Paths
                .get(resourceRoot, "datafiles", "config", "auth", "basicauth", "userstore", BALLERINA_CONF);

        compileResult = BCompileUtil.compile(sourceRoot.resolve("file-based-userstore.bal").toString());

        // load configs
        ConfigRegistry registry = ConfigRegistry.getInstance();
        registry.initRegistry(null, ballerinaConfPath.toString(), null);
    }

    @Test(description = "Test case for creating file based userstore")
    public void testCreateFileBasedUserstore() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testCreateFileBasedUserstore");
        Assert.assertTrue(returns != null);
        Assert.assertTrue(returns[0] instanceof BStruct);
    }

    @Test(description = "Test case for authenticating non-existing user")
    public void testAuthenticationOfNonExistingUser() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationOfNonExistingUser");
        Assert.assertTrue(returns != null);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for authenticating with invalid password")
    public void testAuthenticationOfNonExistingPassword() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthenticationOfNonExistingPassword");
        Assert.assertTrue(returns != null);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for successful authentication")
    public void testAuthentication() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testAuthentication");
        Assert.assertTrue(returns != null);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for reading groups of non-existing user")
    public void testReadGroupsOfNonExistingUser() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testReadGroupsOfNonExistingUser");
        Assert.assertTrue(returns != null);
        Assert.assertEquals(returns[0].stringValue(), "");
    }

    @Test(description = "Test case for reading groups of a user")
    public void testReadGroupsOfUser() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testReadGroupsOfUser");
        Assert.assertTrue(returns != null);
        BString groups = (BString) returns[0];
        log.info("groups: " + groups);
        Assert.assertNotNull(groups);
        Assert.assertEquals(groups.stringValue(), "xyz");
    }
}
