/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.net.http;

import io.netty.handler.codec.http.HttpHeaders;
import org.ballerinalang.compiler.CompilerPhase;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.mime.util.Constants;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.net.http.HttpConstants;
import org.ballerinalang.net.http.HttpUtil;
import org.ballerinalang.net.http.caching.RequestCacheControlStruct;
import org.ballerinalang.net.http.caching.ResponseCacheControlStruct;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ballerinalang.compiler.FileSystemProjectDirectory;
import org.wso2.transport.http.netty.message.HTTPCarbonMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.ballerinalang.mime.util.Constants.MESSAGE_ENTITY;
import static org.ballerinalang.net.http.HttpConstants.HTTP_STATUS_CODE;
import static org.ballerinalang.net.http.HttpConstants.PROTOCOL_PACKAGE_HTTP;
import static org.ballerinalang.net.http.HttpConstants.REQUEST_CACHE_CONTROL;
import static org.ballerinalang.net.http.HttpConstants.REQUEST_CACHE_CONTROL_INDEX;
import static org.ballerinalang.net.http.HttpConstants.RESPONSE;
import static org.ballerinalang.net.http.HttpConstants.RESPONSE_CACHE_CONTROL;

/**
 * Test cases for the HTTP caching client.
 */
public class HttpCachingClientTest {

    private static final String AGE = "Age";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String DATE = "Date";
    private static final String ETAG = "Etag";
    private static final String EXPIRES = "Expires";
    private static final String LAST_MODIFIED = "Last-Modified";
    private static final String WARNING = "Warning";
    
    private CompileResult compileResult;
    
    @BeforeClass
    public void setup() throws IOException {
        String httpModuleSourcePath = HttpConstants.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        Path sourceRoot = Paths.get(httpModuleSourcePath, "META-INF", "ballerina");
        if (Files.notExists(Paths.get(sourceRoot.toString(), ".ballerina"))) {
            Files.createDirectory(Paths.get(sourceRoot.toString(), ".ballerina"));
        }
        compileResult = BCompileUtil.compile(sourceRoot.toString(), "http", CompilerPhase.CODE_GEN,
                                             new FileSystemProjectDirectory(sourceRoot));
    }

    @Test(description = "Tests whether the Age header is parsed correctly, according to the specification",
          enabled = false)
    public void testGetResponseAge() {
        BStruct cachedResponse = BCompileUtil.createAndGetStruct(compileResult.getProgFile(),
                                                                 PROTOCOL_PACKAGE_HTTP,
                                                                 RESPONSE);

        HTTPCarbonMessage inResponseMsg = HttpUtil.createHttpCarbonMessage(false);
        inResponseMsg.setHeader(AGE, "10");
        inResponseMsg.setProperty(HTTP_STATUS_CODE, 200);

        initInboundResponse(cachedResponse, inResponseMsg);

        BValue[] inputArgs = new BValue[]{cachedResponse};
        BValue[] returns = BRunUtil.invoke(compileResult, "getResponseAge", inputArgs);
        Assert.assertEquals(returns[0].stringValue(), "10");

        inResponseMsg.setHeader(AGE, "abc");
        returns = BRunUtil.invoke(compileResult, "getResponseAge", inputArgs);
        Assert.assertEquals(returns[0].stringValue(), "0");

        inResponseMsg.removeHeader(AGE);
        returns = BRunUtil.invoke(compileResult, "getResponseAge", inputArgs);
        Assert.assertEquals(returns[0].stringValue(), "0");
    }

    @Test(description = "Tests whether the Date header is parsed correctly to an integer value", enabled = false)
    public void testGetDateValue() {
        String expectedDate = "Thu, 01 Mar 2018 15:36:34 GMT";
        BStruct inResponse = BCompileUtil.createAndGetStruct(compileResult.getProgFile(),
                                                             PROTOCOL_PACKAGE_HTTP,
                                                             RESPONSE);

        HTTPCarbonMessage inResponseMsg = HttpUtil.createHttpCarbonMessage(false);
        inResponseMsg.setHeader(DATE, expectedDate);
        inResponseMsg.setProperty(HTTP_STATUS_CODE, 200);

        initInboundResponse(inResponse, inResponseMsg);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
        long date = ZonedDateTime.parse(expectedDate, formatter).toInstant().toEpochMilli();

        BValue[] inputArg = {inResponse};
        BValue[] returns = BRunUtil.invoke(compileResult, "getDateValue", inputArg);

        Assert.assertEquals(((BInteger) returns[0]).intValue(), date);
    }

    @Test(description = "Tests whether the 2xx warnings are retained correctly as per the specification",
          enabled = false)
    public void testRetain2xxWarnings() {
        final String warning214 = "Warning: 214 - \"Transformation Applied\"";
        final String warning299 = "Warning: 299 - \"Miscellaneous Persistent Warning\"";
        BStruct inResponse = BCompileUtil.createAndGetStruct(compileResult.getProgFile(),
                                                             PROTOCOL_PACKAGE_HTTP,
                                                             RESPONSE);

        HTTPCarbonMessage inResponseMsg = HttpUtil.createHttpCarbonMessage(false);
        HttpHeaders httpHeaders = inResponseMsg.getHeaders();
        httpHeaders.add(WARNING, "Warning: 110 - \"Response is Stale\"");
        httpHeaders.add(WARNING, "Warning: 111 - \"Revalidation Failed\"");
        httpHeaders.add(WARNING, "Warning: 112 - \"Disconnected Operation\"");
        httpHeaders.add(WARNING, "Warning: 113 - \"Heuristic Expiration\"");
        httpHeaders.add(WARNING, warning214);
        httpHeaders.add(WARNING, warning299);
        inResponseMsg.setProperty(HTTP_STATUS_CODE, 200);

        initInboundResponse(inResponse, inResponseMsg);
        BValue[] inputArg = {inResponse};
        BRunUtil.invoke(compileResult, "retain2xxWarnings", inputArg);

        List<String> warningHeaders = inResponseMsg.getHeaders().getAll(WARNING);

        Assert.assertEquals(warningHeaders.size(), 2);
        Assert.assertEquals(warningHeaders.get(0), warning214);
        Assert.assertEquals(warningHeaders.get(1), warning299);
    }

    @Test(description = "Tests whether the headers of a cached response are correctly updated by a validation response",
          enabled = false)
    public void testReplaceHeaders() {
        final String cachedDateHeader = "Thu, 01 Mar 2018 15:36:34 GMT";
        final String cachedExpiresHeader = "Thu, 01 Mar 2018 15:46:34 GMT";
        final String validationDateHeader = "Thu, 01 Mar 2018 15:38:20 GMT";
        final String validationExpiresHeader = "Thu, 01 Mar 2018 15:48:20 GMT";
        final String validationTestHeader = "ballerina-replace-headers";
        final String cacheControlHeader = "no-cache=\"Set-Cookie\",max-age=120";
        final String etagHeader = "1sps79e:q0efehi8";

        BStruct cachedResponse = BCompileUtil.createAndGetStruct(compileResult.getProgFile(),
                                                                 PROTOCOL_PACKAGE_HTTP,
                                                                 RESPONSE);
        HTTPCarbonMessage cachedResponseMsg = HttpUtil.createHttpCarbonMessage(false);
        cachedResponseMsg.setProperty(HTTP_STATUS_CODE, 200);
        cachedResponseMsg.setHeader(DATE, cachedDateHeader);
        cachedResponseMsg.setHeader(CACHE_CONTROL, cacheControlHeader);
        cachedResponseMsg.setHeader(EXPIRES, cachedExpiresHeader);
        cachedResponseMsg.setHeader(ETAG, etagHeader);
        initInboundResponse(cachedResponse, cachedResponseMsg);

        BStruct validationResponse = BCompileUtil.createAndGetStruct(compileResult.getProgFile(),
                                                                     PROTOCOL_PACKAGE_HTTP,
                                                                     RESPONSE);
        HTTPCarbonMessage validationResponseMsg = HttpUtil.createHttpCarbonMessage(false);
        validationResponseMsg.setProperty(HTTP_STATUS_CODE, 304);
        validationResponseMsg.setHeader(DATE, validationDateHeader);
        validationResponseMsg.setHeader(CACHE_CONTROL, cacheControlHeader);
        validationResponseMsg.setHeader(EXPIRES, validationExpiresHeader);
        validationResponseMsg.setHeader(ETAG, etagHeader);
        validationResponseMsg.setHeader("test-name", validationTestHeader);
        initInboundResponse(validationResponse, validationResponseMsg);

        BValue[] inputArg = {cachedResponse, validationResponse};
        BRunUtil.invoke(compileResult, "replaceHeaders", inputArg);

        HttpHeaders updatedCachedRespHeaders = cachedResponseMsg.getHeaders();

        Assert.assertEquals(updatedCachedRespHeaders.size(), 5);
        Assert.assertEquals(updatedCachedRespHeaders.get(DATE), validationDateHeader);
        Assert.assertEquals(updatedCachedRespHeaders.get(CACHE_CONTROL), cacheControlHeader);
        Assert.assertEquals(updatedCachedRespHeaders.get(EXPIRES), validationExpiresHeader);
        Assert.assertEquals(updatedCachedRespHeaders.get(ETAG), etagHeader);
        Assert.assertEquals(updatedCachedRespHeaders.get("test-name"), validationTestHeader);
    }

    @Test(description = "Test for the isAStrongValidator() function which determines whether a given ETag is strong",
          enabled = false)
    public void testIsAStrongValidator() {
        // Not testing for null arguments since in code, this function is always called after a null check
        BValue[] returns = BRunUtil.invoke(compileResult, "isAStrongValidator", new BValue[]{new BString("abc")});
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        returns = BRunUtil.invoke(compileResult, "isAStrongValidator", new BValue[]{new BString("12345")});
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        returns = BRunUtil.invoke(compileResult, "isAStrongValidator", new BValue[]{new BString("W/abc")});
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());

        returns = BRunUtil.invoke(compileResult, "isAStrongValidator", new BValue[]{new BString("w/abc")});
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        returns = BRunUtil.invoke(compileResult, "isAStrongValidator", new BValue[]{new BString("")});
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test for the hasAWeakValidator() function which determines whether a 304 response contains a" +
            " weak validator", enabled = false)
    public void testHasAWeakValidator() {
        final String lastModifiedHeader = "Thu, 01 Mar 2018 15:36:34 GMT";
        final String etagHeader = "1sps79e:q0efehi8";

        BStruct validationResponse = BCompileUtil.createAndGetStruct(compileResult.getProgFile(),
                                                                     PROTOCOL_PACKAGE_HTTP,
                                                                     RESPONSE);
        HTTPCarbonMessage validationResponseMsg = HttpUtil.createHttpCarbonMessage(false);
        validationResponseMsg.setProperty(HTTP_STATUS_CODE, 200);
        validationResponseMsg.setHeader(LAST_MODIFIED, lastModifiedHeader);
        validationResponseMsg.setHeader(ETAG, etagHeader);
        initInboundResponse(validationResponse, validationResponseMsg);

        // Not testing with a null validation response since this function is not called without a null check

        BValue[] inputArg = {validationResponse, new BString(etagHeader)};
        BValue[] returns = BRunUtil.invoke(compileResult, "hasAWeakValidator", inputArg);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        inputArg[1] = new BString(null);
        returns = BRunUtil.invoke(compileResult, "hasAWeakValidator", inputArg);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        validationResponseMsg.removeHeader(LAST_MODIFIED);
        initInboundResponse(validationResponse, validationResponseMsg);
        inputArg[1] = new BString(etagHeader);
        returns = BRunUtil.invoke(compileResult, "hasAWeakValidator", inputArg);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());

        inputArg[1] = new BString("W/" + etagHeader); // passing a weak validator
        returns = BRunUtil.invoke(compileResult, "hasAWeakValidator", inputArg);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test for the isStaleResponseAccepted() function which determines whether stale responses are" +
            " acceptable", enabled = false)
    public void testIsStaleResponseAccepted() {
        boolean isSharedCache = false;
        RequestCacheControlStruct requestCacheControl
                = new RequestCacheControlStruct(compileResult.getProgFile()
                                                        .getPackageInfo(PROTOCOL_PACKAGE_HTTP)
                                                        .getStructInfo(REQUEST_CACHE_CONTROL));
        requestCacheControl.setMaxStale(Long.MAX_VALUE);

        BStruct cachedResponse = BCompileUtil.createAndGetStruct(compileResult.getProgFile(),
                                                                 PROTOCOL_PACKAGE_HTTP,
                                                                 RESPONSE);
        ResponseCacheControlStruct responseCacheControl
                = new ResponseCacheControlStruct(compileResult.getProgFile()
                                                         .getPackageInfo(PROTOCOL_PACKAGE_HTTP)
                                                         .getStructInfo(RESPONSE_CACHE_CONTROL));

        HTTPCarbonMessage cachedResponseMsg = HttpUtil.createHttpCarbonMessage(false);
        cachedResponseMsg.setProperty(HTTP_STATUS_CODE, 200);
        cachedResponseMsg.setHeader(AGE, "10");
        cachedResponseMsg.setHeader(CACHE_CONTROL, responseCacheControl.buildCacheControlDirectives());
        initInboundResponse(cachedResponse, cachedResponseMsg);

        BValue[] inputArgs = {requestCacheControl.getStruct(), cachedResponse, new BBoolean(isSharedCache)};
        BValue[] returns = BRunUtil.invoke(compileResult, "isStaleResponseAccepted", inputArgs);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        requestCacheControl.setMaxStale(-1);
        returns = BRunUtil.invoke(compileResult, "isStaleResponseAccepted", inputArgs);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());

        requestCacheControl.setMaxStale(7);
        responseCacheControl.setMaxAge(5);
        returns = BRunUtil.invoke(compileResult, "isStaleResponseAccepted", inputArgs);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        requestCacheControl.setMaxStale(7);
        responseCacheControl.setMaxAge(3);
        returns = BRunUtil.invoke(compileResult, "isStaleResponseAccepted", inputArgs);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test for the isServingStaleProhibited() function which determines whether serving stale " +
            "responses is prohibited.", enabled = false)
    public void testIsServingStaleProhibited() {
        RequestCacheControlStruct requestCacheControl
                = new RequestCacheControlStruct(compileResult.getProgFile()
                                                        .getPackageInfo(PROTOCOL_PACKAGE_HTTP)
                                                        .getStructInfo(REQUEST_CACHE_CONTROL));

        ResponseCacheControlStruct responseCacheControl
                = new ResponseCacheControlStruct(compileResult.getProgFile()
                                                         .getPackageInfo(PROTOCOL_PACKAGE_HTTP)
                                                         .getStructInfo(RESPONSE_CACHE_CONTROL));

        BValue[] inputArgs = {requestCacheControl.getStruct(), responseCacheControl.getStruct()};
        BValue[] returns;

        // No prohibitive directives set
        returns = BRunUtil.invoke(compileResult, "isServingStaleProhibited", inputArgs);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());

        // Setting no-store in request
        requestCacheControl.setNoStore(true);
        returns = BRunUtil.invoke(compileResult, "isServingStaleProhibited", inputArgs);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        // Setting no-cache in request
        requestCacheControl.setNoStore(false).setNoCache(true);
        returns = BRunUtil.invoke(compileResult, "isServingStaleProhibited", inputArgs);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        // Setting must-revalidate in response
        requestCacheControl.setNoCache(false);
        responseCacheControl.setMustRevalidate(true);
        returns = BRunUtil.invoke(compileResult, "isServingStaleProhibited", inputArgs);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        // Setting must-revalidate in response
        responseCacheControl.setMustRevalidate(false).setSMaxAge(10);
        returns = BRunUtil.invoke(compileResult, "isServingStaleProhibited", inputArgs);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        // Setting must-revalidate in response
        responseCacheControl.setSMaxAge(-1).setProxyRevalidate(true);
        returns = BRunUtil.invoke(compileResult, "isServingStaleProhibited", inputArgs);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    // To ensure freshness calculation adheres to https://tools.ietf.org/html/rfc7234#section-4.2.1
    @Test(description = "Test for the determination of freshness lifetime of a cached response", enabled = false)
    public void testGetFreshnessLifetime() {
        // First, set all potential fields that can be used for freshness calculation so that the test can determine
        // if the correct precedence order is followed.
        final String dateHeader = "Thu, 01 Mar 2018 15:36:34 GMT";
        final String expiresHeader = "Thu, 01 Mar 2018 15:46:34 GMT"; // 600 second difference

        BStruct cachedResponse = BCompileUtil.createAndGetStruct(compileResult.getProgFile(),
                                                                 PROTOCOL_PACKAGE_HTTP,
                                                                 RESPONSE);
        ResponseCacheControlStruct responseCacheControl
                = new ResponseCacheControlStruct(compileResult.getProgFile()
                                                         .getPackageInfo(PROTOCOL_PACKAGE_HTTP)
                                                         .getStructInfo(RESPONSE_CACHE_CONTROL));
        responseCacheControl.setSMaxAge(20).setMaxAge(15);

        HTTPCarbonMessage cachedResponseMsg = HttpUtil.createHttpCarbonMessage(false);
        cachedResponseMsg.setProperty(HTTP_STATUS_CODE, 200);
        cachedResponseMsg.setHeader(DATE, dateHeader);
        cachedResponseMsg.setHeader(EXPIRES, expiresHeader);
        cachedResponseMsg.setHeader(CACHE_CONTROL, responseCacheControl.buildCacheControlDirectives());
        initInboundResponse(cachedResponse, cachedResponseMsg);

        // Parameters: cached response and whether the cache is a shared cache
        BValue[] inputArgs = {cachedResponse, new BBoolean(true)};
        BValue[] returns;

        // If the cache is shared and the s-maxage directive is present, that's the freshness lifetime
        returns = BRunUtil.invoke(compileResult, "getFreshnessLifetime", inputArgs);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 20);

        // If the cache is not shared, s-maxage cannot be used
        inputArgs[1] = new BBoolean(false);
        returns = BRunUtil.invoke(compileResult, "getFreshnessLifetime", inputArgs);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 15);

        // If the cache is shared, but s-maxage is not set
        inputArgs[1] = new BBoolean(true);
        responseCacheControl.setSMaxAge(-1);
        returns = BRunUtil.invoke(compileResult, "getFreshnessLifetime", inputArgs);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 15);

        // When both max-age and s-maxage are not applicable
        inputArgs[1] = new BBoolean(false);
        responseCacheControl.setMaxAge(-1);
        returns = BRunUtil.invoke(compileResult, "getFreshnessLifetime", inputArgs);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 600);

        // When there are multiple values for the Date header
        HttpHeaders responseHeaders = cachedResponseMsg.getHeaders();
        responseHeaders.add(DATE, "Thu, 01 Mar 2018 15:37:34 GMT");
        responseHeaders.add(DATE, "Thu, 01 Mar 2018 15:56:34 GMT");
        returns = BRunUtil.invoke(compileResult, "getFreshnessLifetime", inputArgs);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 0);

        // When there are multiple values for the Expires header
        responseHeaders.set(DATE, dateHeader);
        responseHeaders.add(EXPIRES, "Thu, 01 Mar 2018 15:37:34 GMT");
        responseHeaders.add(EXPIRES, "Thu, 01 Mar 2018 15:56:34 GMT");
        returns = BRunUtil.invoke(compileResult, "getFreshnessLifetime", inputArgs);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 0);
    }

    @Test(description = "Test for the isFreshResponse() function which determines whether a cached response is still " +
            "fresh.", enabled = false)
    public void testIsFreshResponse() {
        final String dateHeader = "Thu, 01 Mar 2018 15:36:34 GMT";
        final String expiresHeader = "Thu, 01 Mar 2018 15:46:34 GMT"; // 600 second difference

        BStruct cachedResponse = BCompileUtil.createAndGetStruct(compileResult.getProgFile(),
                                                                 PROTOCOL_PACKAGE_HTTP,
                                                                 RESPONSE);
        ResponseCacheControlStruct responseCacheControl
                = new ResponseCacheControlStruct(compileResult.getProgFile()
                                                         .getPackageInfo(PROTOCOL_PACKAGE_HTTP)
                                                         .getStructInfo(RESPONSE_CACHE_CONTROL));
        responseCacheControl.setMaxAge(300).setSMaxAge(300);

        HTTPCarbonMessage cachedResponseMsg = HttpUtil.createHttpCarbonMessage(false);
        cachedResponseMsg.setProperty(HTTP_STATUS_CODE, 200);
        cachedResponseMsg.setHeader(AGE, String.valueOf(200));
        cachedResponseMsg.setHeader(DATE, dateHeader);
        cachedResponseMsg.setHeader(EXPIRES, expiresHeader);
        cachedResponseMsg.setHeader(CACHE_CONTROL, responseCacheControl.buildCacheControlDirectives());
        initInboundResponse(cachedResponse, cachedResponseMsg);

        HttpHeaders responseHeaders = cachedResponseMsg.getHeaders();

        BValue[] inputArgs = {cachedResponse, new BBoolean(true)};
        BValue[] returns;

        // When all 3 parameters for freshness life time is set
        returns = BRunUtil.invoke(compileResult, "isFreshResponse", inputArgs);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        responseHeaders.set(AGE, "350");
        returns = BRunUtil.invoke(compileResult, "isFreshResponse", inputArgs);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());

        // When only max-age and Expires header are there
        responseCacheControl.setSMaxAge(-1);
        inputArgs[1] = new BBoolean(false);
        returns = BRunUtil.invoke(compileResult, "isFreshResponse", inputArgs);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());

        responseHeaders.set(AGE, "200");
        returns = BRunUtil.invoke(compileResult, "isFreshResponse", inputArgs);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        // When only Expires header is there
        responseCacheControl.setMaxAge(-1);
        returns = BRunUtil.invoke(compileResult, "isFreshResponse", inputArgs);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());

        responseHeaders.set(AGE, "650");
        returns = BRunUtil.invoke(compileResult, "isFreshResponse", inputArgs);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());

        // When there are none of the above 3
        responseHeaders.remove(EXPIRES);
        returns = BRunUtil.invoke(compileResult, "isFreshResponse", inputArgs);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    private void initInboundResponse(BStruct inResponse, HTTPCarbonMessage inResponseMsg) {
        HttpUtil.addCarbonMsg(inResponse, inResponseMsg);
        BStruct entity = BCompileUtil.createAndGetStruct(compileResult.getProgFile(), Constants.PROTOCOL_PACKAGE_MIME,
                                                         Constants.ENTITY);
        BStruct mediaType = BCompileUtil.createAndGetStruct(compileResult.getProgFile(),
                                                            Constants.PROTOCOL_PACKAGE_MIME, Constants.MEDIA_TYPE);
        HttpUtil.populateInboundResponse(inResponse, entity, mediaType, compileResult.getProgFile(), inResponseMsg);
    }

    private void initOutboundRequest(BStruct outRequest, RequestCacheControlStruct cacheControl) {
        BStruct entity = BCompileUtil.createAndGetStruct(compileResult.getProgFile(), Constants.PROTOCOL_PACKAGE_MIME,
                                                         Constants.ENTITY);
        outRequest.addNativeData(MESSAGE_ENTITY, entity);

        outRequest.setRefField(REQUEST_CACHE_CONTROL_INDEX, cacheControl.getStruct());
    }
}
