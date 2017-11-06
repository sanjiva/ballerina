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
package org.ballerinalang.test.services.nativeimpl.response;

import org.ballerinalang.launcher.util.BAssertUtil;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.BServiceUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BJSON;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.net.http.Constants;
import org.ballerinalang.net.http.HttpUtil;
import org.ballerinalang.runtime.message.BallerinaMessageDataSource;
import org.ballerinalang.runtime.message.StringDataSource;
import org.ballerinalang.test.services.testutils.MessageUtils;
import org.ballerinalang.test.services.testutils.Services;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.transport.http.netty.message.HTTPCarbonMessage;
import org.wso2.carbon.transport.http.netty.message.HttpMessageDataStreamer;

/**
 * Test cases for ballerina.net.http.response negative native functions.
 */
public class ResponseNativeFunctionNegativeTest {

    private CompileResult result, resultNegative, serviceResult;
    private final String responseStruct = Constants.RESPONSE;
    private final String protocolPackageHttp = Constants.PROTOCOL_PACKAGE_HTTP;
    private String filePath = "test-src/statements/services/nativeimpl/response/responseNativeFunctionNegative.bal";
    private String filePathNeg = "test-src/statements/services/nativeimpl/response/responseCompileNegative.bal";

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile(filePath);
        resultNegative = BCompileUtil.compile(filePathNeg);
        serviceResult = BServiceUtil.setupProgramFile(this, filePath);
    }

    @Test(description = "Test when the content length header is not set")
    public void testGetContentLength() {
        BStruct request = BCompileUtil.createAndGetStruct(result.getProgFile(), protocolPackageHttp, responseStruct);
        HTTPCarbonMessage cMsg = HttpUtil.createHttpCarbonMessage(true);
        HttpUtil.addCarbonMsg(request, cMsg);

        BValue[] inputArg = {request};
        String error = null;
        try {
            BRunUtil.invoke(result, "testGetContentLength", inputArg);
        } catch (Throwable e) {
            error = e.getMessage();
        }
        Assert.assertEquals(error.substring(23, 45), "invalid content length");
    }

    @Test
    public void testGetHeader() {
        BStruct request = BCompileUtil.createAndGetStruct(result.getProgFile(), protocolPackageHttp, responseStruct);
        HTTPCarbonMessage cMsg = HttpUtil.createHttpCarbonMessage(true);
        HttpUtil.addCarbonMsg(request, cMsg);

        BString key = new BString(Constants.CONTENT_TYPE);
        BValue[] inputArg = {request, key};
        BValue[] returnVals = BRunUtil.invoke(result, "testGetHeader", inputArg);
        Assert.assertEquals(((BString) returnVals[0]).value(), "");
        Assert.assertFalse(((BBoolean) returnVals[1]).booleanValue());
    }

    @Test(description = "Test method without json payload")
    public void testGetJsonPayloadWithoutPayload() {
        BStruct request = BCompileUtil.createAndGetStruct(result.getProgFile(), protocolPackageHttp, responseStruct);
        HTTPCarbonMessage cMsg = HttpUtil.createHttpCarbonMessage(true);
        HttpUtil.addCarbonMsg(request, cMsg);

        BValue[] inputArg = {request};
        String error = null;
        try {
            BRunUtil.invoke(result, "testGetJsonPayload", inputArg);
        } catch (Throwable e) {
            error = e.getMessage();
        }
        Assert.assertEquals(error.substring(23, 133), "error while retrieving json payload from message: " +
                "failed to create json: No content to map due to end-of-input");
    }

    @Test(description = "Test method with string payload")
    public void testGetJsonPayloadWithStringPayload() {
        BStruct request = BCompileUtil.createAndGetStruct(result.getProgFile(), protocolPackageHttp, responseStruct);
        HTTPCarbonMessage cMsg = HttpUtil.createHttpCarbonMessage(true);
        String payload = "ballerina";
        BallerinaMessageDataSource dataSource = new StringDataSource(payload);
        dataSource.setOutputStream(new HttpMessageDataStreamer(cMsg).getOutputStream());
        cMsg.setMessageDataSource(dataSource);
        cMsg.setAlreadyRead(true);
        HttpUtil.addCarbonMsg(request, cMsg);

        BValue[] inputArg = {request};
        String error = null;
        try {
            BRunUtil.invoke(result, "testGetJsonPayload", inputArg);
        } catch (Throwable e) {
            error = e.getMessage();
        }
        Assert.assertEquals(error.substring(23, 118), "error while retrieving json payload from message" +
                ": Unrecognized token 'ballerina': was expecting");
    }

    @Test
    public void testGetProperty() {
        BStruct request = BCompileUtil.createAndGetStruct(result.getProgFile(), protocolPackageHttp, responseStruct);
        HTTPCarbonMessage cMsg = HttpUtil.createHttpCarbonMessage(true);
        HttpUtil.addCarbonMsg(request, cMsg);

        BString propertyName = new BString("wso2");
        BValue[] inputArg = {request, propertyName};
        BValue[] returnVals = BRunUtil.invoke(result, "testGetProperty", inputArg);
        Assert.assertFalse(returnVals == null || returnVals.length == 0 || returnVals[0] == null,
                "Invalid Return Values.");
        Assert.assertEquals(((BString) returnVals[0]).value(), "");
    }

    @Test(description = "Test getStringPayload method without a paylaod")
    public void testGetStringPayloadNegative() {
        BStruct request = BCompileUtil.createAndGetStruct(result.getProgFile(), protocolPackageHttp, responseStruct);
        HTTPCarbonMessage cMsg = HttpUtil.createHttpCarbonMessage(true);
        HttpUtil.addCarbonMsg(request, cMsg);

        BValue[] inputArg = {request};
        BValue[] returnVals = BRunUtil.invoke(result, "testGetStringPayload", inputArg);
        Assert.assertFalse(returnVals == null || returnVals.length == 0 || returnVals[0] == null,
                "Invalid Return Values.");
        Assert.assertEquals(returnVals[0].stringValue(), "");
    }

    @Test(description = "Test getStringPayload method with JSON payload")
    public void testGetStringPayloadMethodWithJsonPayload() {
        BStruct request = BCompileUtil.createAndGetStruct(result.getProgFile(), protocolPackageHttp, responseStruct);
        HTTPCarbonMessage cMsg = HttpUtil.createHttpCarbonMessage(true);
        String payload = "{\"code\":\"123\"}";
        BallerinaMessageDataSource dataSource = new BJSON(payload);
        dataSource.setOutputStream(new HttpMessageDataStreamer(cMsg).getOutputStream());
        cMsg.setMessageDataSource(dataSource);
        cMsg.setAlreadyRead(true);
        HttpUtil.addCarbonMsg(request, cMsg);

        BValue[] inputArg = {request};
        BValue[] returnVals = BRunUtil.invoke(result, "testGetStringPayload", inputArg);
        Assert.assertFalse(returnVals == null || returnVals.length == 0 || returnVals[0] == null,
                "Invalid Return Values.");
        Assert.assertEquals(returnVals[0].stringValue(), payload);
    }

    @Test
    public void testGetXmlPayloadNegative() {
        BStruct request = BCompileUtil.createAndGetStruct(result.getProgFile(), protocolPackageHttp, responseStruct);
        HTTPCarbonMessage cMsg = HttpUtil.createHttpCarbonMessage(true);
        HttpUtil.addCarbonMsg(request, cMsg);

        BValue[] inputArg = {request};
        String error = null;
        try {
            BRunUtil.invoke(result, "testGetXmlPayload", inputArg);
        } catch (Throwable e) {
            error = e.getMessage();
        }
        Assert.assertTrue(error.contains("error: error, message: error while retrieving XML payload from message: " +
                "Unexpected EOF in prolog"));
    }

    @Test
    public void testRemoveHeaderNegative() {
        BStruct request = BCompileUtil.createAndGetStruct(result.getProgFile(), protocolPackageHttp, responseStruct);
        HTTPCarbonMessage cMsg = HttpUtil.createHttpCarbonMessage(true);
        String expect = "Expect";
        cMsg.setHeader(expect, "100-continue");
        HttpUtil.addCarbonMsg(request, cMsg);

        BString key = new BString("Range");
        BValue[] inputArg = {request, key};
        BValue[] returnVals = BRunUtil.invoke(result, "testRemoveHeader", inputArg);
        Assert.assertFalse(returnVals == null || returnVals.length == 0 || returnVals[0] == null,
                "Invalid Return Values.");
        Assert.assertTrue(returnVals[0] instanceof BStruct);
        HTTPCarbonMessage response = HttpUtil.getCarbonMsg((BStruct) returnVals[0], null);
        Assert.assertNotNull(response.getHeader(expect));
    }

    @Test
    public void testRemoveAllHeadersNegative() {
        BStruct request = BCompileUtil.createAndGetStruct(result.getProgFile(), protocolPackageHttp, responseStruct);
        HTTPCarbonMessage cMsg = HttpUtil.createHttpCarbonMessage(true);
        HttpUtil.addCarbonMsg(request, cMsg);

        BValue[] inputArg = {request};
        BValue[] returnVals = BRunUtil.invoke(result, "testRemoveAllHeaders", inputArg);
        Assert.assertFalse(returnVals == null || returnVals.length == 0 || returnVals[0] == null,
                "Invalid Return Values.");
        Assert.assertTrue(returnVals[0] instanceof BStruct);
        HTTPCarbonMessage response = HttpUtil.getCarbonMsg((BStruct) returnVals[0], null);
        Assert.assertNull(response.getHeader("Expect"));
    }

    @Test
    public void testSend() {
        String path = "/hello/11";
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage(path, Constants.HTTP_METHOD_GET);
        HTTPCarbonMessage response = Services.invokeNew(cMsg);

        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(response.getProperty(Constants.HTTP_STATUS_CODE), 500);
        Assert.assertTrue(response.getMessageDataSource().getMessageAsString().contains("operation not allowed"));
    }

    @Test
    public void testForward() {
        String path = "/hello/12";
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage(path, Constants.HTTP_METHOD_GET);
        HTTPCarbonMessage response = Services.invokeNew(cMsg);

        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(response.getProperty(Constants.HTTP_STATUS_CODE), 500);
        Assert.assertTrue(response.getMessageDataSource().getMessageAsString().contains("operation not allowed"));
    }

    @Test
    public void testForwardWithNullParameter() {
        String path = "/hello/13";
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage(path, Constants.HTTP_METHOD_GET);
        HTTPCarbonMessage response = Services.invokeNew(cMsg);

        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(response.getProperty(Constants.HTTP_STATUS_CODE), 500);
        Assert.assertTrue(response.getMessageDataSource().getMessageAsString().contains("argument 1 is null"));
    }

    @Test
    public void testForwardWithEmptyResponse() {
        String path = "/hello/14";
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage(path, Constants.HTTP_METHOD_GET);
        HTTPCarbonMessage response = Services.invokeNew(cMsg);

        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(response.getProperty(Constants.HTTP_STATUS_CODE), 500);
        Assert.assertTrue(response.getMessageDataSource().getMessageAsString()
                .contains("failed to forward: empty response parameter"));
    }

    @Test(description = "test declaration of two response method. Error is shown in the console", enabled = false)
    public void testRedeclarationOfTwoResponseMethods() {
        String path = "/hello/15";
        HTTPCarbonMessage cMsg = MessageUtils.generateHTTPMessage(path, Constants.HTTP_METHOD_GET);
        HTTPCarbonMessage response = Services.invokeNew(cMsg);

        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(response.getMessageDataSource().getMessageAsString(), "wso2");
    }

    @Test
    public void testGetBinaryPayloadMethod() {
        //TODO Test this with multipart support, not needed for now
    }

    @Test
    public void testCompilationErrorTestCases() {
        Assert.assertEquals(resultNegative.getErrorCount(), 3);
        //testResponseSetStatusCodeWithString
        BAssertUtil.validateError(resultNegative, 0, "incompatible types: expected 'int', found 'string'", 4, 23);
        //testResponseGetContentLengthWithString
        BAssertUtil.validateError(resultNegative, 1, "incompatible types: expected 'int', found 'string'", 9, 26);
        //testResponseGetMethod
        BAssertUtil.validateError(resultNegative, 2,
                "undefined function 'getMethod' in struct 'ballerina.net.http:Response'", 14, 21);
    }

    @AfterClass
    public void tearDown() {
        BServiceUtil.cleanup(serviceResult);
    }

}
