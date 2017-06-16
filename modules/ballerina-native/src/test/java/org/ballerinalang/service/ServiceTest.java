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

package org.ballerinalang.service;

import org.ballerinalang.runtime.message.StringDataSource;
import org.ballerinalang.services.dispatchers.DispatcherRegistry;
import org.ballerinalang.services.dispatchers.http.HTTPResourceDispatcher;
import org.ballerinalang.testutils.EnvironmentInitializer;
import org.ballerinalang.testutils.MessageUtils;
import org.ballerinalang.testutils.Services;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.messaging.CarbonMessage;

import java.nio.ByteBuffer;

/**
 * Service/Resource dispatchers test class.
 */
public class ServiceTest {

    ProgramFile programFile;

    @BeforeClass
    public void setup() {
        programFile = EnvironmentInitializer.setupProgramFile("lang/service/echoService.bal");
    }

    @Test
    public void testServiceDispatching() {
        CarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/message", "GET");
        CarbonMessage response = Services.invoke(cMsg);
        Assert.assertNotNull(response);
        // TODO: Improve with more assets
    }

    @Test(description = "Test for protocol availability check", expectedExceptions = {BallerinaException.class},
            expectedExceptionsMessageRegExp = ".*protocol not defined.*")
    public void testProtocolAvailabilityCheck() {
        CarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/message", "GET");
        cMsg.removeProperty(org.wso2.carbon.messaging.Constants.PROTOCOL);
        Services.invoke(cMsg);
    }

    @Test(description = "Test for service dispatcher availability check",
            expectedExceptions = {BallerinaException.class},
            expectedExceptionsMessageRegExp = ".*no service dispatcher available .*")
    public void testServiceDispatcherAvailabilityCheck() {
        CarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/message", "GET");
        cMsg.setProperty(org.wso2.carbon.messaging.Constants.PROTOCOL, "FOO");   // setting incorrect protocol
        Services.invoke(cMsg);
    }

    @Test(description = "Test for service availability check")
    public void testServiceAvailabilityCheck() {
        CarbonMessage cMsg = MessageUtils.generateHTTPMessage("/foo/message", "GET");
        CarbonMessage invoke = Services.invoke(cMsg);
        Assert.assertEquals(invoke.getMessageDataSource().getMessageAsString(),
                "no service found to handle incoming request received to : /foo/message");
    }

    @Test(description = "Test for resource dispatcher availability check")
    public void testResourceDispatcherAvailabilityCheck() {
        CarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/message", "GET");
        DispatcherRegistry.getInstance().unregisterResourceDispatcher("http"); // Remove http resource dispatcher
        try {
            CarbonMessage invoke = Services.invoke(cMsg);
            Assert.assertEquals(invoke.getMessageDataSource().getMessageAsString(),
                    "no resource dispatcher available to handle protocol: http");

        } finally {
            DispatcherRegistry.getInstance().registerResourceDispatcher(new HTTPResourceDispatcher()); // Add back
        }
    }

    @Test(description = "Test for resource availability check")
    public void testResourceAvailabilityCheck() {
        CarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/bar", "GET");
        CarbonMessage invoke = Services.invoke(cMsg);
        Assert.assertEquals(invoke.getMessageDataSource().getMessageAsString(),
                "no matching resource found for path : /bar , method : GET");
    }

    @Test
    public void testSetString() {
        CarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/setString", "POST");
        cMsg.addMessageBody(ByteBuffer.wrap("hello".getBytes()));
        cMsg.setEndOfMsgAdded(true);
        CarbonMessage response = Services.invoke(cMsg);

        Assert.assertNotNull(response);
    }

    @Test(dependsOnMethods = "testSetString")
    public void testGetString() {
        CarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/getString", "GET");
        CarbonMessage response = Services.invoke(cMsg);
        Assert.assertNotNull(response);

        StringDataSource stringDataSource = (StringDataSource) response.getMessageDataSource();
        Assert.assertNotNull(stringDataSource);
        Assert.assertEquals(stringDataSource.getValue(), "hello");
    }

    @Test
    public void testGetStringAfterSetString() {
        CarbonMessage setStringCMsg = MessageUtils.generateHTTPMessage("/echo/setString", "POST");
        String stringPayload = "hello";
        setStringCMsg.addMessageBody(ByteBuffer.wrap(stringPayload.getBytes()));
        setStringCMsg.setEndOfMsgAdded(true);
        Services.invoke(setStringCMsg);

        CarbonMessage cMsg = MessageUtils.generateHTTPMessage("/echo/getString", "GET");
        CarbonMessage response = Services.invoke(cMsg);
        Assert.assertNotNull(response);

        StringDataSource stringDataSource = (StringDataSource) response.getMessageDataSource();
        Assert.assertNotNull(stringDataSource);
        Assert.assertEquals(stringDataSource.getValue(), stringPayload);
    }


    @AfterClass
    public void tearDown() {
        EnvironmentInitializer.cleanup(programFile);
    }

    //TODO: add more test cases

}
