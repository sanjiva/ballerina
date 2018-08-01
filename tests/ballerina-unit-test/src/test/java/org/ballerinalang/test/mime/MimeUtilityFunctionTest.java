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

package org.ballerinalang.test.mime;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.mime.util.EntityBodyHandler;
import org.ballerinalang.mime.util.MimeUtil;
import org.ballerinalang.model.util.JsonParser;
import org.ballerinalang.model.util.StringUtils;
import org.ballerinalang.model.util.XMLUtils;
import org.ballerinalang.model.values.BByteArray;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.BXML;
import org.ballerinalang.stdlib.io.channels.base.Channel;
import org.ballerinalang.stdlib.io.utils.Base64ByteChannel;
import org.ballerinalang.stdlib.io.utils.Base64Wrapper;
import org.ballerinalang.stdlib.io.utils.IOConstants;
import org.ballerinalang.test.utils.ByteArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import static org.ballerinalang.mime.util.MimeConstants.CONTENT_DISPOSITION_FILENAME_FIELD;
import static org.ballerinalang.mime.util.MimeConstants.CONTENT_DISPOSITION_NAME_FIELD;
import static org.ballerinalang.mime.util.MimeConstants.CONTENT_DISPOSITION_STRUCT;
import static org.ballerinalang.mime.util.MimeConstants.DISPOSITION_FIELD;
import static org.ballerinalang.mime.util.MimeConstants.MEDIA_TYPE;
import static org.ballerinalang.mime.util.MimeConstants.PARAMETER_MAP_FIELD;
import static org.ballerinalang.mime.util.MimeConstants.PRIMARY_TYPE_FIELD;
import static org.ballerinalang.mime.util.MimeConstants.PROTOCOL_PACKAGE_MIME;
import static org.ballerinalang.mime.util.MimeConstants.SUBTYPE_FIELD;
import static org.ballerinalang.mime.util.MimeConstants.SUFFIX_FIELD;

/**
 * Unit tests for MIME package utilities.
 *
 * @since 0.963.0
 */
public class MimeUtilityFunctionTest {
    private static final Logger log = LoggerFactory.getLogger(MimeUtilityFunctionTest.class);

    private CompileResult compileResult;
    private final String protocolPackageMime = PROTOCOL_PACKAGE_MIME;
    private final String mediaTypeStruct = MEDIA_TYPE;
    private final String contentDispositionStruct = CONTENT_DISPOSITION_STRUCT;
    private static final String ERROR_MESSAGE_FIELD = "message";

    @BeforeClass
    public void setup() {
        String sourceFilePath = "test-src/mime/mime-test.bal";
        compileResult = BCompileUtil.compile(sourceFilePath);
    }

    @Test(description = "Test 'getMediaType' function in ballerina/mime package")
    public void testGetMediaType() {
        String contentType = "multipart/form-data; boundary=032a1ab685934650abbe059cb45d6ff3";
        BValue[] args = {new BString(contentType)};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetMediaType", args);
        Assert.assertEquals(returns.length, 1);
        BMap<String, BValue> mediaType = (BMap<String, BValue>) returns[0];
        Assert.assertEquals(mediaType.get(PRIMARY_TYPE_FIELD).stringValue(), "multipart");
        Assert.assertEquals(mediaType.get(SUBTYPE_FIELD).stringValue(), "form-data");
        Assert.assertEquals(mediaType.get(SUFFIX_FIELD).stringValue(), "");
        BMap map = (BMap) mediaType.get(PARAMETER_MAP_FIELD);
        Assert.assertEquals(map.get("boundary").stringValue(), "032a1ab685934650abbe059cb45d6ff3");
    }

    @Test(description = "Test 'getBaseType' function in ballerina/mime package")
    public void testGetBaseTypeOnMediaType() {
        BMap<String, BValue> mediaType = BCompileUtil
                .createAndGetStruct(compileResult.getProgFile(), protocolPackageMime, mediaTypeStruct);
        mediaType.put(PRIMARY_TYPE_FIELD, new BString("application"));
        mediaType.put(SUBTYPE_FIELD, new BString("test+xml"));
        BValue[] args = {mediaType};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetBaseTypeOnMediaType", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "application/test+xml");
    }

    @Test(description = "Test 'testToStringOnMediaType' function in ballerina/mime package")
    public void testToStringOnMediaType() {
        BMap<String, BValue> mediaType = BCompileUtil
                .createAndGetStruct(compileResult.getProgFile(), protocolPackageMime, mediaTypeStruct);
        mediaType.put(PRIMARY_TYPE_FIELD, new BString("application"));
        mediaType.put(SUBTYPE_FIELD, new BString("test+xml"));
        BMap map = new BMap();
        map.put("charset", new BString("utf-8"));
        mediaType.put(PARAMETER_MAP_FIELD, map);
        BValue[] args = {mediaType};
        BValue[] returns = BRunUtil.invoke(compileResult, "testToStringOnMediaType", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "application/test+xml; charset=utf-8");
    }

    @Test(description = "Test 'getContentDispositionObject' function in ballerina/mime package")
    public void testGetContentDispositionObject() {
        String contentType = "form-data; name=\"filepart\"; filename=\"file-01.txt\"";
        BValue[] args = {new BString(contentType)};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetContentDispositionObject", args);
        Assert.assertEquals(returns.length, 1);
        BMap<String, BValue> contentDisposition = (BMap<String, BValue>) returns[0];
        Assert.assertEquals(contentDisposition.get(CONTENT_DISPOSITION_FILENAME_FIELD).stringValue(),
                "file-01.txt");
        Assert.assertEquals(contentDisposition.get(CONTENT_DISPOSITION_NAME_FIELD).stringValue(),
                "filepart");
        Assert.assertEquals(contentDisposition.get(DISPOSITION_FIELD).stringValue(),
                "form-data");
        BMap map = (BMap) contentDisposition.get(PARAMETER_MAP_FIELD);
        Assert.assertEquals(map.size(), 0);
    }

    @Test
    public void testToStringOnContentDisposition() {
        BMap<String, BValue> contentDisposition = BCompileUtil
                .createAndGetStruct(compileResult.getProgFile(), protocolPackageMime, contentDispositionStruct);
        contentDisposition.put(CONTENT_DISPOSITION_FILENAME_FIELD, new BString("file-01.txt"));
        contentDisposition.put(DISPOSITION_FIELD, new BString("form-data"));
        contentDisposition.put(CONTENT_DISPOSITION_NAME_FIELD, new BString("test"));
        BValue[] args = {contentDisposition};
        BValue[] returns = BRunUtil.invoke(compileResult, "testToStringOnContentDisposition", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "form-data;name=\"test\";filename=\"file-01.txt\"");
    }

    @Test
    public void testMimeBase64EncodeString() {
        String expectedValue = "SGVsbG8gQmFsbGVyaW5h";
        BValue[] args = new BValue[]{new BString("Hello Ballerina")};
        BValue[] returnValues = BRunUtil.invoke(compileResult, "testMimeBase64EncodeString", args);
        Assert.assertFalse(returnValues == null || returnValues.length == 0 || returnValues[0] == null,
                "Invalid return value");
        Assert.assertEquals(returnValues[0].stringValue(), expectedValue);
    }

    @Test
    public void testMimeBase64EncodeBlob() {
        String expectedValue = "SGVsbG8gQmFsbGVyaW5h";
        BValue[] args = new BValue[]{new BByteArray("Hello Ballerina".getBytes())};
        BValue[] returnValues = BRunUtil.invoke(compileResult, "testMimeBase64EncodeBlob", args);
        Assert.assertFalse(returnValues == null || returnValues.length == 0 || returnValues[0] == null,
                "Invalid return value");
        ByteArrayUtils.assertJBytesWithBBytes(((BByteArray) returnValues[0]).getBytes(), expectedValue.getBytes());
    }

    @Test
    public void testMimeBase64EncodeByteChannel() {
        String expectedValue = "SGVsbG8gQmFsbGVyaW5h";
        BMap<String, BValue> byteChannelStruct = Util.getByteChannelStruct(compileResult);
        InputStream inputStream = new ByteArrayInputStream("Hello Ballerina".getBytes());
        Base64ByteChannel base64ByteChannel = new Base64ByteChannel(inputStream);
        byteChannelStruct.addNativeData(IOConstants.BYTE_CHANNEL_NAME, new Base64Wrapper(base64ByteChannel));
        BValue[] args = new BValue[]{byteChannelStruct};
        BValue[] returnValues = BRunUtil.invoke(compileResult, "testMimeBase64EncodeByteChannel", args);
        Assert.assertFalse(returnValues == null || returnValues.length == 0 || returnValues[0] == null,
                "Invalid return value");
        BMap<String, BValue> decodedByteChannel = (BMap<String, BValue>) returnValues[0];
        Channel byteChannel = (Channel) decodedByteChannel.getNativeData(IOConstants.BYTE_CHANNEL_NAME);
        Assert.assertEquals(StringUtils.getStringFromInputStream(byteChannel.getInputStream()),
                expectedValue);
    }

    @Test
    public void testMimeBase64DecodeString() {
        String expectedValue = "Hello Ballerina";
        BValue[] args = new BValue[]{new BString("SGVsbG8gQmFsbGVyaW5h")};
        BValue[] returnValues = BRunUtil.invoke(compileResult, "testMimeBase64DecodeString", args);
        Assert.assertFalse(returnValues == null || returnValues.length == 0 || returnValues[0] == null,
                "Invalid return value");
        Assert.assertEquals(returnValues[0].stringValue(), expectedValue);
        Assert.assertFalse(returnValues[0] == null, "Invalid return value");
        Assert.assertEquals(returnValues[0].stringValue(), expectedValue);
    }

    @Test
    public void testMimeBase64DecodeBlob() {
        String expectedValue = "Hello Ballerina";
        BValue[] args = new BValue[]{new BByteArray("SGVsbG8gQmFsbGVyaW5h".getBytes())};
        BValue[] returnValues = BRunUtil.invoke(compileResult, "testMimeBase64DecodeBlob", args);
        Assert.assertFalse(returnValues == null || returnValues.length == 0 || returnValues[0] == null,
                "Invalid return value");
        ByteArrayUtils.assertJBytesWithBBytes(((BByteArray) returnValues[0]).getBytes(), expectedValue.getBytes());
    }

    @Test
    public void testMimeBase64DecodeByteChannel() {
        String expectedValue = "Hello Ballerina!";
        BMap<String, BValue> byteChannelStruct = Util.getByteChannelStruct(compileResult);
        byte[] encodedByteArray = Base64.getMimeEncoder().encode(expectedValue.getBytes());
        InputStream encodedStream = new ByteArrayInputStream(encodedByteArray);
        Base64ByteChannel base64ByteChannel = new Base64ByteChannel(encodedStream);
        byteChannelStruct.addNativeData(IOConstants.BYTE_CHANNEL_NAME, new Base64Wrapper(base64ByteChannel));
        BValue[] args = new BValue[]{byteChannelStruct};
        BValue[] returnValues = BRunUtil.invoke(compileResult, "testMimeBase64DecodeByteChannel", args);
        Assert.assertFalse(returnValues == null || returnValues.length == 0 || returnValues[0] == null,
                "Invalid return value");
        BMap<String, BValue> decodedByteChannel = (BMap<String, BValue>) returnValues[0];
        Channel byteChannel = (Channel) decodedByteChannel.getNativeData(IOConstants.BYTE_CHANNEL_NAME);
        Assert.assertEquals(StringUtils.getStringFromInputStream(byteChannel.getInputStream()),
                expectedValue);
    }

    @Test(description = "Set json data to entity and get the content back from entity as json")
    public void testGetAndSetJson() {
        BValue jsonContent = JsonParser.parse("{'code':'123'}");
        BValue[] args = {jsonContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetAndGetJson", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BMap) returns[0]).get("code").stringValue(), "123");
    }

    @Test(description = "Test whether the json content can be retrieved properly when it is called multiple times")
    public void testGetJsonMoreThanOnce() {
        BValue jsonContent = JsonParser.parse("{'code':'123'}");
        BValue[] args = {jsonContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetJsonMultipleTimes", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(),
                "{\"concatContent\":[{\"code\":\"123\"}, {\"code\":\"123\"}, {\"code\":\"123\"}]}");
    }

    @Test(description = "Set xml data to entity and get the content back from entity as xml")
    public void testGetAndSetXml() {
        BXML xmlContent = XMLUtils.parse("<name>ballerina</name>");
        BValue[] args = {xmlContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetAndGetXml", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BXML) returns[0]).getTextValue().stringValue(), "ballerina");
    }

    @Test(description = "Test whether the xml content can be retrieved properly when it is called multiple times")
    public void testGetXmlMoreThanOnce() {
        BXML xmlContent = XMLUtils.parse("<name>ballerina</name>");
        BValue[] args = {xmlContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetXmlMultipleTimes", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BXML<Object>) returns[0]).stringValue(),
                "<name>ballerina</name><name>ballerina</name><name>ballerina</name>");
    }

    @Test(description = "Set text data to entity and get the content back from entity as text")
    public void testGetAndSetText() {
        BString textContent = new BString("Hello Ballerina !");
        BValue[] args = {textContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetAndGetText", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "Hello Ballerina !");
    }

    @Test(description = "Test whether the text content can be retrieved properly when it is called multiple times")
    public void testGetTextMoreThanOnce() {
        BString textContent = new BString("Hello Ballerina !");
        BValue[] args = {textContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetTextMultipleTimes", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(),
                "Hello Ballerina !Hello Ballerina !Hello Ballerina !");
    }

    @Test(description = "Set byte array data to entity and get the content back from entity as a byte array")
    public void testGetAndSetByteArray() {
        String content = "ballerina";
        BByteArray byteContent = new BByteArray(content.getBytes());
        BValue[] args = {byteContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetAndGetByteArray", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(new String(((BByteArray) returns[0]).getBytes()), content);
    }

    @Test(description = "Test whether the byte array content can be " +
            "retrieved properly when it is called multiple times")
    public void testGetByteArrayMoreThanOnce() {
        String content = "ballerina";
        BByteArray byteContent = new BByteArray(content.getBytes());
        BValue[] args = {byteContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetByteArrayMultipleTimes", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(),
                "ballerinaballerinaballerina");
    }

    @Test(description = "Set file as entity body and get the content back as a byte array")
    public void testSetFileAsEntityBody() {
        try {
            File file = File.createTempFile("testFile", ".tmp");
            file.deleteOnExit();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("Hello Ballerina!");
            bufferedWriter.close();
            BValue[] args = {new BString(file.getAbsolutePath())};
            BValue[] returns = BRunUtil.invoke(compileResult, "testSetFileAsEntityBody", args);
            Assert.assertEquals(returns.length, 1);
            Assert.assertEquals(new String(((BByteArray) returns[0]).getBytes()), "Hello Ballerina!",
                    "Entity body is not properly set");
        } catch (IOException e) {
            log.error("Error occurred in testSetFileAsEntityBody", e.getMessage());
        }
    }

    @Test(description = "Set byte channel as entity body and get the content back as a byte array")
    public void testSetByteChannel() {
        try {
            File file = File.createTempFile("testFile", ".tmp");
            file.deleteOnExit();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("Hello Ballerina!");
            bufferedWriter.close();
            BMap<String, BValue> byteChannelStruct = Util.getByteChannelStruct(compileResult);
            byteChannelStruct.addNativeData(IOConstants.BYTE_CHANNEL_NAME, EntityBodyHandler.getByteChannelForTempFile
                    (file.getAbsolutePath()));
            BValue[] args = {byteChannelStruct};
            BValue[] returns = BRunUtil.invoke(compileResult, "testSetByteChannel", args);
            Assert.assertEquals(returns.length, 1);
            Assert.assertEquals(new String(((BByteArray) returns[0]).getBytes()), "Hello Ballerina!",
                    "Entity body is not properly set");
        } catch (IOException e) {
            log.error("Error occurred in testSetByteChannel", e.getMessage());
        }
    }

    @Test(description = "Set byte channel as entity body and get that channel back")
    public void testGetByteChannel() {
        try {
            File file = File.createTempFile("testFile", ".tmp");
            file.deleteOnExit();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("Hello Ballerina!");
            bufferedWriter.close();
            BMap<String, BValue> byteChannelStruct = Util.getByteChannelStruct(compileResult);
            byteChannelStruct.addNativeData(IOConstants.BYTE_CHANNEL_NAME, EntityBodyHandler.getByteChannelForTempFile
                    (file.getAbsolutePath()));
            BValue[] args = {byteChannelStruct};
            BValue[] returns = BRunUtil.invoke(compileResult, "testGetByteChannel", args);
            Assert.assertEquals(returns.length, 1);
            BMap<String, BValue> returnByteChannelStruct = (BMap<String, BValue>) returns[0];
            Channel byteChannel = (Channel) returnByteChannelStruct.getNativeData(IOConstants.BYTE_CHANNEL_NAME);
            Assert.assertEquals(StringUtils.getStringFromInputStream(byteChannel.getInputStream()),
                    "Hello Ballerina!");
        } catch (IOException e) {
            log.error("Error occurred in testSetByteChannel", e.getMessage());
        }
    }

    @Test(description = "Set entity body as a byte channel get the content back as a string")
    public void testSetEntityBodyMultipleTimes() {
        try {
            File file = File.createTempFile("testFile", ".tmp");
            file.deleteOnExit();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("File Content");
            bufferedWriter.close();
            BMap<String, BValue> byteChannelStruct = Util.getByteChannelStruct(compileResult);
            byteChannelStruct.addNativeData(IOConstants.BYTE_CHANNEL_NAME,
                    EntityBodyHandler.getByteChannelForTempFile(file.getAbsolutePath()));
            BValue[] args = {byteChannelStruct, new BString("Hello Ballerina!")};
            BValue[] returns = BRunUtil.invoke(compileResult, "testSetEntityBodyMultipleTimes", args);
            Assert.assertEquals(returns.length, 1);
            Assert.assertEquals(returns[0].stringValue(), "File Content");
        } catch (IOException e) {
            log.error("Error occurred in testSetByteChannel", e.getMessage());
        }
    }

    @Test(description = "Once the temp file channel is closed, check whether the temp file gets deleted")
    public void testTempFileDeletion() {
        File file;
        try {
            file = File.createTempFile("testFile", ".tmp");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("File Content");
            bufferedWriter.close();
            Channel tempFileIOChannel = EntityBodyHandler.getByteChannelForTempFile(file.getAbsolutePath());
            Assert.assertTrue(file.exists());
            InputStream inputStream = tempFileIOChannel.getInputStream();
            Assert.assertNotNull(inputStream);
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            MimeUtil.writeInputToOutputStream(inputStream, result);
            Assert.assertEquals(result.toString("UTF-8"), "File Content");
            tempFileIOChannel.close();
            Assert.assertFalse(file.exists());
        } catch (IOException e) {
            log.error("Error occurred in testTempFileDeletion", e.getMessage());
        }
    }

    @Test(description = "An EntityError should be returned in case the byte channel is null")
    public void testGetByteChannelForNull() {
        BMap<String, BValue> byteChannelStruct = Util.getByteChannelStruct(compileResult);
        byteChannelStruct.addNativeData(IOConstants.BYTE_CHANNEL_NAME, null);
        BValue[] args = {byteChannelStruct};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetByteChannel", args);
        Assert.assertEquals(returns.length, 1);
        BMap<String, BValue> errorStruct = (BMap<String, BValue>) returns[0];
        Assert.assertEquals(errorStruct.get(ERROR_MESSAGE_FIELD).stringValue(),
                "Byte channel is not available as payload");
    }

    @Test(description = "An EntityError should be returned from 'getByteChannel()', in case the payload " +
            "is in data source form")
    public void testByteChannelWhenPayloadInDataSource() {
        BValue jsonContent = JsonParser.parse("{'code':'123'}");
        BValue[] args = {jsonContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetJsonAndGetByteChannel", args);
        Assert.assertEquals(returns.length, 1);
        BMap<String, BValue> errorStruct = (BMap<String, BValue>) returns[0];
        Assert.assertEquals(errorStruct.get(ERROR_MESSAGE_FIELD).stringValue(),
                "Byte channel is not available but payload can be obtain either" +
                        " as xml, json, string or byte[] type");
    }

    @Test(description = "Once the byte channel is consumed by the user, check whether the content retrieved " +
            "as a text data source is empty")
    public void testGetTextDataSource() {
        try {
            File file = File.createTempFile("testFile", ".tmp");
            file.deleteOnExit();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("{'code':'123'}");
            bufferedWriter.close();
            BMap<String, BValue> byteChannelStruct = Util.getByteChannelStruct(compileResult);
            byteChannelStruct.addNativeData(IOConstants.BYTE_CHANNEL_NAME, EntityBodyHandler.getByteChannelForTempFile
                    (file.getAbsolutePath()));
            BValue[] args = {byteChannelStruct};
            BValue[] returns = BRunUtil.invoke(compileResult, "testGetTextDataSource", args);
            Assert.assertEquals(returns.length, 1);
            Assert.assertEquals(returns[0].stringValue(), "");
        } catch (IOException e) {
            log.error("Error occurred in testTempFileDeletion", e.getMessage());
        }
    }

    @Test(description = "Once the byte channel is consumed by the user, check whether the content retrieved " +
            "as a json data source return an error")
    public void testGetJsonDataSource() {
        try {
            File file = File.createTempFile("testFile", ".tmp");
            file.deleteOnExit();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("Hello Ballerina!");
            bufferedWriter.close();
            BMap<String, BValue> byteChannelStruct = Util.getByteChannelStruct(compileResult);
            byteChannelStruct.addNativeData(IOConstants.BYTE_CHANNEL_NAME, EntityBodyHandler.getByteChannelForTempFile
                    (file.getAbsolutePath()));
            BValue[] args = {byteChannelStruct};
            BValue[] returns = BRunUtil.invoke(compileResult, "testGetJsonDataSource", args);
            Assert.assertEquals(returns.length, 1);
            Assert.assertNotNull(returns[0]);
            Assert.assertTrue(returns[0].stringValue().contains("Error occurred while extracting json " +
                    "data from entity: empty JSON document"));
        } catch (IOException e) {
            log.error("Error occurred in testTempFileDeletion", e.getMessage());
        }
    }

    @Test(description = "Test whether the Content-Disposition header value can be built from ContentDisposition " +
            "object values.")
    public void testContentDispositionForFormData() {
        BMap<String, BValue> bodyPart = Util.getEntityStruct(compileResult);
        BMap<String, BValue> contentDispositionStruct = Util.getContentDispositionStruct(compileResult);
        MimeUtil.setContentDisposition(contentDispositionStruct, bodyPart,
                "form-data; name=\"filepart\"; filename=\"file-01.txt\"");
        String contentDispositionValue = MimeUtil.getContentDisposition(bodyPart);
        Assert.assertEquals(contentDispositionValue, "form-data;name=\"filepart\";filename=\"file-01.txt\"");
    }

    @Test
    public void testFileNameWithoutQuotes() {
        BMap<String, BValue> bodyPart = Util.getEntityStruct(compileResult);
        BMap<String, BValue> contentDispositionStruct = Util.getContentDispositionStruct(compileResult);
        MimeUtil.setContentDisposition(contentDispositionStruct, bodyPart,
                "form-data; name=filepart; filename=file-01.txt");
        String contentDispositionValue = MimeUtil.getContentDisposition(bodyPart);
        Assert.assertEquals(contentDispositionValue, "form-data;name=\"filepart\";filename=\"file-01.txt\"");
    }

    @Test
    public void testContentDispositionWithoutParams() {
        BMap<String, BValue> bodyPart = Util.getEntityStruct(compileResult);
        BMap<String, BValue> contentDispositionStruct = Util.getContentDispositionStruct(compileResult);
        MimeUtil.setContentDisposition(contentDispositionStruct, bodyPart,
                "form-data");
        String contentDispositionValue = MimeUtil.getContentDisposition(bodyPart);
        Assert.assertEquals(contentDispositionValue, "form-data");
    }

    @Test
    public void testGetXmlWithSuffix() {
        BXML xmlContent = XMLUtils.parse("<name>ballerina</name>");
        BValue[] args = {xmlContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetXmlWithSuffix", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BXML) returns[0]).getTextValue().stringValue(), "ballerina");
    }

    @Test(description = "Get xml content from entity that has a non compatible xml content-type")
    public void testGetXmlWithNonCompatibleMediaType() {
        BXML xmlContent = XMLUtils.parse("<name>ballerina</name>");
        BValue[] args = {xmlContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetXmlWithNonCompatibleMediaType", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BMap<String, BValue>) returns[0]).get(ERROR_MESSAGE_FIELD).stringValue(),
                "Entity body is not xml compatible since the received content-type is : " +
                        "application/3gpdash-qoe-report");
    }

    @Test
    public void testGetJsonWithSuffix() {
        BValue jsonContent = JsonParser.parse("{'code':'123'}");
        BValue[] args = {jsonContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetJsonWithSuffix", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "{\"code\":\"123\"}");
    }

    @Test
    public void testGetJsonWithNonCompatibleMediaType() {
        BValue jsonContent = JsonParser.parse("{'code':'123'}");
        BValue[] args = {jsonContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetJsonWithNonCompatibleMediaType", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BMap<String, BValue>) returns[0]).get(ERROR_MESSAGE_FIELD).stringValue(),
                "Entity body is not json compatible since the received content-type is : " +
                        "application/whoispp-query");
    }

    @Test
    public void testGetTextContentWithNonCompatibleMediaType() {
        BString textContent = new BString("Hello Ballerina !");
        BValue[] args = {textContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testGetTextWithNonCompatibleMediaType", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BMap<String, BValue>) returns[0]).get(ERROR_MESSAGE_FIELD).stringValue(),
                "Entity body is not text compatible since the received content-type is : " +
                        "model/vnd.parasolid.transmit");
    }

    @Test
    public void testSetBodyAndGetText() {
        BString textContent = new BString("Hello Ballerina !");
        BValue[] args = {textContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetBodyAndGetText", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), textContent.stringValue());
    }

    @Test
    public void testSetBodyAndGetXml() {
        BXML xmlContent = XMLUtils.parse("<name>ballerina</name>");
        BValue[] args = {xmlContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetBodyAndGetXml", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "<name>ballerina</name>");
    }

    @Test
    public void testSetBodyAndGetJson() {
        BValue jsonContent = JsonParser.parse("{'code':'123'}");
        BValue[] args = {jsonContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetBodyAndGetJson", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(((BMap) returns[0]).get("code").stringValue(), "123");
    }

    @Test
    public void testSetBodyAndGetByteArray() {
        String content = "ballerina";
        BByteArray byteContent = new BByteArray(content.getBytes());
        BValue[] args = {byteContent};
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetBodyAndGetByteArray", args);
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(new String(((BByteArray) returns[0]).getBytes()), content);
    }

    @Test
    public void testSetBodyAndGetByteChannel() {
        try {
            File file = File.createTempFile("testFile", ".tmp");
            file.deleteOnExit();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("Hello Ballerina!");
            bufferedWriter.close();
            BMap<String, BValue> byteChannelStruct = Util.getByteChannelStruct(compileResult);
            byteChannelStruct.addNativeData(IOConstants.BYTE_CHANNEL_NAME, EntityBodyHandler.getByteChannelForTempFile
                    (file.getAbsolutePath()));
            BValue[] args = {byteChannelStruct};
            BValue[] returns = BRunUtil.invoke(compileResult, "testSetBodyAndGetByteChannel", args);
            Assert.assertEquals(returns.length, 1);
            BMap<String, BValue> returnByteChannelStruct = (BMap<String, BValue>) returns[0];
            Channel byteChannel = (Channel) returnByteChannelStruct.getNativeData(IOConstants.BYTE_CHANNEL_NAME);
            Assert.assertEquals(StringUtils.getStringFromInputStream(byteChannel.getInputStream()),
                    "Hello Ballerina!");
        } catch (IOException e) {
            log.error("Error occurred in testSetByteChannel", e.getMessage());
        }
    }

    @Test
    public void testSetMediaTypeToEntity() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetMediaTypeToEntity");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "application/my-custom-type+json");
    }

    @Test
    public void testSetMediaTypeAndGetValueAsHeader() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetMediaTypeAndGetValueAsHeader");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "application/my-custom-type+json");
    }

    @Test
    public void testSetHeaderAndGetMediaType() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetHeaderAndGetMediaType");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "text/plain; charset=UTF-8");
    }

    @Test
    public void testSetContentDispositionToEntity() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetContentDispositionToEntity");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "inline;name=\"test\";filename=\"test_file.xml\"");
    }

    @Test
    public void testSetContentDispositionAndGetValueAsHeader() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetContentDispositionAndGetValueAsHeader");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "inline;name=\"test\";filename=\"test_file.xml\"");
    }

    @Test
    public void testSetHeaderAndGetContentDisposition() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetHeaderAndGetContentDisposition");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "inline;name=\"test\";filename=\"test_file.xml\"");
    }

    @Test
    public void testSetContentLengthToEntity() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetContentLengthToEntity");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "45555");
    }

    @Test
    public void testSetContentLengthAndGetValueAsHeader() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetContentLengthAndGetValueAsHeader");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "45555");
    }

    @Test
    public void testSetContentIdToEntity() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetContentIdToEntity");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "test-id");
    }

    @Test
    public void testSetContentIdAndGetValueAsHeader() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testSetContentIdAndGetValueAsHeader");
        Assert.assertEquals(returns.length, 1);
        Assert.assertEquals(returns[0].stringValue(), "test-id");
    }

    @Test
    public void testGetAnyStreamAsString() {
        try {
            File file = File.createTempFile("testFile", ".tmp");
            file.deleteOnExit();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("{'code':'123'}");
            bufferedWriter.close();
            BMap<String, BValue> byteChannelStruct = Util.getByteChannelStruct(compileResult);
            byteChannelStruct.addNativeData(IOConstants.BYTE_CHANNEL_NAME, EntityBodyHandler.getByteChannelForTempFile
                    (file.getAbsolutePath()));
            BValue[] args = {byteChannelStruct, new BString("application/json")};
            BValue[] returns = BRunUtil.invoke(compileResult, "testGetAnyStreamAsString", args);
            Assert.assertEquals(returns.length, 1);
            Assert.assertEquals(returns[0].stringValue(), "{'code':'123'}");
        } catch (IOException e) {
            log.error("Error occurred in testTempFileDeletion", e.getMessage());
        }
    }
}
