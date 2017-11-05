/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package org.ballerinalang.test.services;

import org.ballerinalang.launcher.util.BServiceUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BJSON;
import org.ballerinalang.net.http.Constants;
import org.ballerinalang.runtime.message.StringDataSource;
import org.ballerinalang.test.services.testutils.HTTPTestRequest;
import org.ballerinalang.test.services.testutils.MessageUtils;
import org.ballerinalang.test.services.testutils.Services;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.transport.http.netty.message.HTTPCarbonMessage;

import java.nio.ByteBuffer;

/**
 * Service/Resource dispatchers test class.
 */
public class ServiceTest {

    CompileResult compileResult;

    @BeforeClass
    public void setup() {
        compileResult = BServiceUtil.setupProgramFile(this, "test-src/services/echoService.bal");
    }

    @Test
    public void testServiceDispatching() {
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/message", "GET");
        HTTPCarbonMessage response = Services.invokeNew(cMsg);
        Assert.assertNotNull(response);
        // TODO: Improve with more assets
    }
    
    @Test
    public void testServiceDispatchingWithWorker() {
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/message_worker", "GET");
        HTTPCarbonMessage response = Services.invokeNew(cMsg);
        Assert.assertNotNull(response);
    }

    @Test(description = "Test for service availability check")
    public void testServiceAvailabilityCheck() {
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage("/foo/message", "GET");
        HTTPCarbonMessage invoke = Services.invokeNew(cMsg);
        Assert.assertEquals(invoke.getMessageDataSource().getMessageAsString(),
                "no matching service found for path : /foo/message");
    }

    @Test(description = "Test for resource availability check")
    public void testResourceAvailabilityCheck() {
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage("/echo/bar", "GET");
        HTTPCarbonMessage invoke = Services.invokeNew(cMsg);
        Assert.assertEquals(invoke.getMessageDataSource().getMessageAsString(),
                "no matching resource found for path : /echo/bar , method : GET");
    }

    @Test
    public void testSetString() {
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/setString", "POST");
        cMsg.waitAndReleaseAllEntities();
        cMsg.addMessageBody(ByteBuffer.wrap("hello".getBytes()));
        cMsg.setEndOfMsgAdded(true);
        HTTPCarbonMessage response = Services.invokeNew(cMsg);

        Assert.assertNotNull(response);
    }

    @Test(dependsOnMethods = "testSetString")
    public void testGetString() {
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/getString", "GET");
        HTTPCarbonMessage response = Services.invokeNew(cMsg);
        Assert.assertNotNull(response);

        StringDataSource stringDataSource = (StringDataSource) response.getMessageDataSource();
        Assert.assertNotNull(stringDataSource);
        Assert.assertEquals(stringDataSource.getValue(), "hello");
    }

    @Test(description = "Test accessing service level variable in resource")
    public void testGetServiceLevelString() {
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/getServiceLevelString", "GET");
        HTTPCarbonMessage response = Services.invokeNew(cMsg);
        Assert.assertNotNull(response);

        StringDataSource stringDataSource = (StringDataSource) response.getMessageDataSource();
        Assert.assertNotNull(stringDataSource);
        Assert.assertEquals(stringDataSource.getValue(), "sample value");
    }

    @Test(description = "Test using constant as annotation attribute value")
    public void testConstantValueAsAnnAttributeVal() {
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/constantPath", "GET");
        HTTPCarbonMessage response = Services.invokeNew(cMsg);
        Assert.assertNotNull(response);

        StringDataSource stringDataSource = (StringDataSource) response.getMessageDataSource();
        Assert.assertNotNull(stringDataSource);
        Assert.assertEquals(stringDataSource.getValue(), "constant path test");
    }

    @Test(description = "Test getString after setting string")
    public void testGetStringAfterSetString() {
        HTTPCarbonMessage setStringCMsg = MessageUtils.generateHTTPMessage("/echo/setString", "POST");
        String stringPayload = "hello";
        setStringCMsg.waitAndReleaseAllEntities();
        setStringCMsg.addMessageBody(ByteBuffer.wrap(stringPayload.getBytes()));
        setStringCMsg.setEndOfMsgAdded(true);
        Services.invokeNew(setStringCMsg);

        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/getString", "GET");
        HTTPCarbonMessage response = Services.invokeNew(cMsg);
        Assert.assertNotNull(response);

        StringDataSource stringDataSource = (StringDataSource) response.getMessageDataSource();
        Assert.assertNotNull(stringDataSource);
        Assert.assertEquals(stringDataSource.getValue(), stringPayload);
    }

    @Test(description = "Test remove headers native function")
    public void testRemoveHeadersNativeFunction() {
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/removeHeaders", "GET");
        cMsg.setHeader("header1", "wso2");
        cMsg.setHeader("header2", "ballerina");
        cMsg.setHeader("header3", "hello");
        HTTPCarbonMessage response = Services.invokeNew(cMsg);
        Assert.assertNotNull(response);

        Assert.assertNull(response.getHeader("header1"));
        Assert.assertNull(response.getHeader("header2"));
        Assert.assertNull(response.getHeader("header3"));
    }

    @Test(description = "Test GetFormParams Native Function")
    public void testGetFormParamsNativeFunction() {
        String path = "/echo/getFormParams";
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage(path, "POST", "firstName=WSO2&team=BalDance");
        cMsg.setHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_FORM);
        HTTPCarbonMessage response = Services.invokeNew(cMsg);

        Assert.assertNotNull(response, "Response message not found");
        BJSON bJson = ((BJSON) response.getMessageDataSource());
        Assert.assertEquals(bJson.value().get("Name").asText(), "WSO2"
                , "Name variable not set properly.");
        Assert.assertEquals(bJson.value().get("Team").asText(), "BalDance"
                , "Team variable not set properly.");
    }

    @Test(description = "Test GetFormParams with undefined key")
    public void testGetFormParamsForUndefinedKey() {
        String path = "/echo/getFormParams";
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage(path, "POST", "firstName=WSO2&company=BalDance");
        cMsg.setHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_FORM);
        HTTPCarbonMessage response = Services.invokeNew(cMsg);

        Assert.assertNotNull(response, "Response message not found");
        BJSON bJson = ((BJSON) response.getMessageDataSource());
        Assert.assertEquals(bJson.value().get("Team").asText(), ""
                , "Team variable not set properly.");
    }

    @Test(description = "Test GetFormParams empty payloads")
    public void testGetFormParamsEmptyPayload() {
        String path = "/echo/getFormParams";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "POST", "");
        cMsg.setHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_FORM);
        HTTPCarbonMessage response = Services.invokeNew(cMsg);

        Assert.assertNotNull(response, "Response message not found");
        StringDataSource stringDataSource = (StringDataSource) response.getMessageDataSource();
        Assert.assertNotNull(stringDataSource);
        Assert.assertTrue(stringDataSource.getValue().contains("empty message payload"));
    }

    @Test(description = "Test GetFormParams with unsupported media type")
    public void testGetFormParamsWithUnsupportedMediaType() {
        String path = "/echo/getFormParams";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "POST", "firstName=WSO2&company=BalDance");
        HTTPCarbonMessage response = Services.invokeNew(cMsg);

        Assert.assertNotNull(response, "Response message not found");
        StringDataSource stringDataSource = (StringDataSource) response.getMessageDataSource();
        Assert.assertNotNull(stringDataSource);
        Assert.assertTrue(stringDataSource.getValue().contains("unsupported media type"));
    }

    @AfterClass
    public void tearDown() {
        BServiceUtil.cleanup(compileResult);
    }

    //TODO: add more test cases

}
