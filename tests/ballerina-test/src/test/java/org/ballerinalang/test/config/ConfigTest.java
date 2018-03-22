/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.test.config;

import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.BServiceUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.test.services.testutils.HTTPTestRequest;
import org.ballerinalang.test.services.testutils.MessageUtils;
import org.ballerinalang.test.services.testutils.Services;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.transport.http.netty.message.HTTPCarbonMessage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Test Native functions in ballerina.config.
 */
public class ConfigTest {

    private static final ConfigRegistry registry = ConfigRegistry.getInstance();
    private static final String BALLERINA_CONF = "ballerina.conf";
    private CompileResult compileResult;
    private String resourceRoot;
    private Path sourceRoot;
    private Path ballerinaConfPath;
    private String customConfigFilePath;

    @BeforeClass
    public void setup() throws IOException {
        resourceRoot = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        sourceRoot = Paths.get(resourceRoot, "test-src", "config");
        ballerinaConfPath = Paths.get(resourceRoot, "datafiles", "config", "default", BALLERINA_CONF);
        customConfigFilePath = Paths.get(resourceRoot, "datafiles", "config", BALLERINA_CONF).toString();

        compileResult = BCompileUtil.compile(sourceRoot.resolve("config.bal").toString());
    }

    @Test(description = "test global method with runtime and custom config file properties")
    public void testGetAsStringWithRuntime() throws IOException {
        BString key = new BString("ballerina.http.host");
        BValue[] inputArg = {key};

        registry.initRegistry(getRuntimeProperties(), customConfigFilePath, ballerinaConfPath);

        BValue[] returnVals = BRunUtil.invoke(compileResult, "testGetAsString", inputArg);

        Assert.assertFalse(returnVals == null || returnVals.length == 0 || returnVals[0] == null,
                "Invalid Return Values.");
        Assert.assertTrue(returnVals[0] instanceof BString);
        Assert.assertEquals(returnVals[0].stringValue(), "10.100.1.201");
    }

    @Test(description = "test global method with default config file properties")
    public void testGetAsStringWithDefaultConfigFile() throws IOException {
        BString key = new BString("ballerina.http.host");
        BValue[] inputArg = {key};

        registry.initRegistry(new HashMap<>(), null, ballerinaConfPath);

        BValue[] returnVals = BRunUtil.invoke(compileResult, "testGetAsString", inputArg);

        Assert.assertFalse(returnVals == null || returnVals.length == 0 || returnVals[0] == null,
                "Invalid Return Values.");
        Assert.assertTrue(returnVals[0] instanceof BString);
        Assert.assertEquals(returnVals[0].stringValue(), "10.100.1.205");
    }

    @Test(description = "test global method with runtime, custom config and default file properties")
    public void testGetAsStringWithAllProperties() throws IOException {
        BString key = new BString("ballerina.http.host");
        BValue[] inputArg = {key};

        registry.initRegistry(getRuntimeProperties(), customConfigFilePath, ballerinaConfPath);

        BValue[] returnVals = BRunUtil.invoke(compileResult, "testGetAsString", inputArg);

        Assert.assertFalse(returnVals == null || returnVals.length == 0 || returnVals[0] == null,
                "Invalid Return Values.");
        Assert.assertTrue(returnVals[0] instanceof BString);
        Assert.assertEquals(returnVals[0].stringValue(), "10.100.1.201");
    }

    @Test(description = "test get global method with unavailable config")
    public void testGetAsStringNegative() throws IOException {
        BString key = new BString("ballerina.wso2");
        BValue[] inputArg = {key};

        registry.initRegistry(new HashMap<>(), null, ballerinaConfPath);

        BValue[] returnVals = BRunUtil.invoke(compileResult, "testGetAsString", inputArg);

        Assert.assertFalse(returnVals == null || returnVals.length == 0, "Invalid Return Values.");
        Assert.assertTrue(returnVals[0] instanceof BString || returnVals[0] == null);
//        Assert.assertNull(returnVals[0].stringValue());
    }

    @Test(description = "test instance method with runtime and custom config file properties")
    public void testGetInstanceValuesWithRuntime() throws IOException {
        BString key = new BString("http1.ballerina.http.port");
        BValue[] inputArg = {key};

        registry.initRegistry(getRuntimeProperties(), customConfigFilePath, ballerinaConfPath);

        BValue[] returnVals = BRunUtil.invoke(compileResult, "testGetAsString", inputArg);

        Assert.assertFalse(returnVals == null || returnVals.length == 0 || returnVals[0] == null,
                "Invalid Return Values.");
        Assert.assertTrue(returnVals[0] instanceof BString);
        Assert.assertEquals(returnVals[0].stringValue(), "8082");
    }

    @Test(description = "test instance method with default config file properties")
    public void testGetInstanceValuesWithDefaultConfigFile() throws IOException {
        BString key = new BString("http1.ballerina.http.port");
        BValue[] inputArg = {key};

        registry.initRegistry(new HashMap<>(), null, ballerinaConfPath);

        BValue[] returnVals = BRunUtil.invoke(compileResult, "testGetAsString", inputArg);

        Assert.assertFalse(returnVals == null || returnVals.length == 0 || returnVals[0] == null,
                "Invalid Return Values.");
        Assert.assertTrue(returnVals[0] instanceof BString);
        Assert.assertEquals(returnVals[0].stringValue(), "8085");
    }

    @Test(description = "test instance method with runtime, custom and default config file properties")
    public void testGetInstanceValuesWithAllProperties() throws IOException {
        BString key = new BString("http1.ballerina.http.port");
        BValue[] inputArg = {key};

        registry.initRegistry(getRuntimeProperties(), customConfigFilePath, ballerinaConfPath);

        BValue[] returnVals = BRunUtil.invoke(compileResult, "testGetAsString", inputArg);

        Assert.assertFalse(returnVals == null || returnVals.length == 0 || returnVals[0] == null,
                "Invalid Return Values.");
        Assert.assertTrue(returnVals[0] instanceof BString);
        Assert.assertEquals(returnVals[0].stringValue(), "8082");
    }

    @Test(description = "test get Instance method with unavailable config")
    public void testGetInstanceValuesNegative() throws IOException {
        BString key = new BString("http1.ballerina.wso2");
        BValue[] inputArg = {key};

        registry.initRegistry(new HashMap<>(), null, ballerinaConfPath);

        BValue[] returnVals = BRunUtil.invoke(compileResult, "testGetAsString", inputArg);

        Assert.assertFalse(returnVals == null || returnVals.length == 0, "Invalid Return Values.");
        Assert.assertTrue(returnVals[0] instanceof BString || returnVals[0] == null);
    }

    @Test(description = "Test config entries with trailing whitespaces")
    public void testEntriesWithTrailingWhitespace() throws IOException {
        BString key = new BString("http3.ballerina.http.port");
        BValue[] inputArg = {key};

        registry.initRegistry(new HashMap<>(), null, ballerinaConfPath);

        BValue[] returnVals = BRunUtil.invoke(compileResult, "testGetAsString", inputArg);

        Assert.assertFalse(returnVals == null || returnVals.length == 0 || returnVals[0] == null,
                           "Invalid Return Values.");
        Assert.assertTrue(returnVals[0] instanceof BString);
        Assert.assertEquals(returnVals[0].stringValue(), "7070");
    }

    @Test(description = "Test setting configs from Ballerina source code")
    public void testSetConfig() throws IOException {
        BString key = new BString("ballerina.http.host");
        BString value = new BString("ballerinalang.org");
        BValue[] inputArgs = {key, value};

        registry.initRegistry(getRuntimeProperties(), customConfigFilePath, ballerinaConfPath);

        BRunUtil.invoke(compileResult, "testSetConfig", inputArgs);

        // The config we set in Ballerina code should overwrite the configs set from other sources
        Assert.assertEquals(registry.getConfiguration(key.stringValue()), value.stringValue());
    }

    @Test(description = "Test contains() method")
    public void testContains() throws IOException {
        BString key = new BString("ballerina.http.host");

        registry.initRegistry(null, null, ballerinaConfPath);

        BValue[] inputArg = {key};
        BValue[] returnVals = BRunUtil.invoke(compileResult, "testContains", inputArg);

        Assert.assertTrue(((BBoolean) returnVals[0]).booleanValue());
    }

    @Test(description = "Test for configuring a service", enabled = false)
    public void testConfiguringAService() throws IOException {
        registry.initRegistry(null, Paths.get(resourceRoot, "datafiles", "config", "service-config.conf").toString(),
                              null);

        String serviceFile = Paths.get(resourceRoot, "test-src", "config", "service_configuration.bal").toString();
        CompileResult configuredService = BServiceUtil.setupProgramFile(this, serviceFile);

        HTTPTestRequest requestMsg = MessageUtils.generateHTTPMessage("/hello", "GET");
        HTTPCarbonMessage responseMsg = Services.invokeNew(configuredService, "backendEP", requestMsg);

        Assert.assertNotNull(responseMsg);
    }

    private Map<String, String> getRuntimeProperties() {
        Map<String, String> runtimeConfigs = new HashMap<>();
        runtimeConfigs.put("ballerina.http.host", "10.100.1.201");
        runtimeConfigs.put("http1.ballerina.http.port", "8082");
        return runtimeConfigs;
    }
}
