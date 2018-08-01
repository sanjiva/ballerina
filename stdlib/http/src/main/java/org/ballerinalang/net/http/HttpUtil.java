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

package org.ballerinalang.net.http;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import org.apache.commons.lang3.StringUtils;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BLangVMErrors;
import org.ballerinalang.connector.api.AnnAttrValue;
import org.ballerinalang.connector.api.Annotation;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.connector.api.ConnectorUtils;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.connector.api.Service;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.connector.api.Value;
import org.ballerinalang.mime.util.EntityBodyChannel;
import org.ballerinalang.mime.util.EntityBodyHandler;
import org.ballerinalang.mime.util.EntityWrapper;
import org.ballerinalang.mime.util.HeaderUtil;
import org.ballerinalang.mime.util.MimeUtil;
import org.ballerinalang.mime.util.MultipartDecoder;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.net.http.caching.RequestCacheControlStruct;
import org.ballerinalang.net.http.caching.ResponseCacheControlStruct;
import org.ballerinalang.net.http.session.Session;
import org.ballerinalang.services.ErrorHandlerUtils;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.ballerinalang.util.observability.ObservabilityUtils;
import org.ballerinalang.util.observability.ObserverContext;
import org.ballerinalang.util.tracer.TraceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.config.ChunkConfig;
import org.wso2.transport.http.netty.config.ForwardedExtensionConfig;
import org.wso2.transport.http.netty.config.KeepAliveConfig;
import org.wso2.transport.http.netty.config.ListenerConfiguration;
import org.wso2.transport.http.netty.config.Parameter;
import org.wso2.transport.http.netty.config.SslConfiguration;
import org.wso2.transport.http.netty.contract.HttpResponseFuture;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;
import org.wso2.transport.http.netty.message.Http2PushPromise;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;
import org.wso2.transport.http.netty.message.HttpMessageDataStreamer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpHeaderNames.CACHE_CONTROL;
import static org.ballerinalang.mime.util.EntityBodyHandler.checkEntityBodyAvailability;
import static org.ballerinalang.mime.util.MimeConstants.BOUNDARY;
import static org.ballerinalang.mime.util.MimeConstants.ENTITY;
import static org.ballerinalang.mime.util.MimeConstants.ENTITY_BYTE_CHANNEL;
import static org.ballerinalang.mime.util.MimeConstants.ENTITY_HEADERS;
import static org.ballerinalang.mime.util.MimeConstants.IS_BODY_BYTE_CHANNEL_ALREADY_SET;
import static org.ballerinalang.mime.util.MimeConstants.MEDIA_TYPE;
import static org.ballerinalang.mime.util.MimeConstants.MULTIPART_AS_PRIMARY_TYPE;
import static org.ballerinalang.mime.util.MimeConstants.NO_CONTENT_LENGTH_FOUND;
import static org.ballerinalang.mime.util.MimeConstants.OCTET_STREAM;
import static org.ballerinalang.mime.util.MimeConstants.ONE_BYTE;
import static org.ballerinalang.mime.util.MimeConstants.PROTOCOL_PACKAGE_MIME;
import static org.ballerinalang.mime.util.MimeConstants.REQUEST_ENTITY_FIELD;
import static org.ballerinalang.mime.util.MimeConstants.RESPONSE_ENTITY_FIELD;
import static org.ballerinalang.net.http.HttpConstants.ALWAYS;
import static org.ballerinalang.net.http.HttpConstants.ANN_CONFIG_ATTR_CHUNKING;
import static org.ballerinalang.net.http.HttpConstants.ANN_CONFIG_ATTR_COMPRESSION;
import static org.ballerinalang.net.http.HttpConstants.ENTITY_INDEX;
import static org.ballerinalang.net.http.HttpConstants.HTTP_MESSAGE_INDEX;
import static org.ballerinalang.net.http.HttpConstants.HTTP_STATUS_CODE;
import static org.ballerinalang.net.http.HttpConstants.NEVER;
import static org.ballerinalang.net.http.HttpConstants.PROTOCOL_PACKAGE_HTTP;
import static org.ballerinalang.net.http.HttpConstants.REQUEST;
import static org.ballerinalang.net.http.HttpConstants.REQUEST_CACHE_CONTROL;
import static org.ballerinalang.net.http.HttpConstants.REQUEST_CACHE_CONTROL_FIELD;
import static org.ballerinalang.net.http.HttpConstants.RESOLVED_REQUESTED_URI;
import static org.ballerinalang.net.http.HttpConstants.RESOLVED_REQUESTED_URI_FIELD;
import static org.ballerinalang.net.http.HttpConstants.RESPONSE_CACHE_CONTROL;
import static org.ballerinalang.net.http.HttpConstants.RESPONSE_CACHE_CONTROL_FIELD;
import static org.ballerinalang.net.http.HttpConstants.RESPONSE_REASON_PHRASE_FIELD;
import static org.ballerinalang.net.http.HttpConstants.RESPONSE_STATUS_CODE_FIELD;
import static org.ballerinalang.net.http.HttpConstants.TRANSPORT_MESSAGE;
import static org.ballerinalang.util.observability.ObservabilityConstants.PROPERTY_HTTP_HOST;
import static org.ballerinalang.util.observability.ObservabilityConstants.PROPERTY_HTTP_PORT;
import static org.ballerinalang.util.observability.ObservabilityConstants.TAG_KEY_HTTP_METHOD;
import static org.ballerinalang.util.observability.ObservabilityConstants.TAG_KEY_HTTP_STATUS_CODE;
import static org.ballerinalang.util.observability.ObservabilityConstants.TAG_KEY_HTTP_URL;
import static org.ballerinalang.util.observability.ObservabilityConstants.TAG_KEY_PEER_ADDRESS;
import static org.wso2.transport.http.netty.common.Constants.ENCODING_GZIP;
import static org.wso2.transport.http.netty.common.Constants.HTTP_TRANSFER_ENCODING_IDENTITY;

/**
 * Utility class providing utility methods.
 */
public class HttpUtil {

    public static final boolean TRUE = true;
    public static final boolean FALSE = false;

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static final String METHOD_ACCESSED = "isMethodAccessed";
    private static final String IO_EXCEPTION_OCCURED = "I/O exception occurred";
    private static final String CHUNKING_CONFIG = "chunking_config";

    public static BValue[] getProperty(Context context, boolean isRequest) {
        BMap<String, BValue> httpMessageStruct = (BMap<String, BValue>) context.getRefArgument(0);
        HttpCarbonMessage httpCarbonMessage = HttpUtil
                .getCarbonMsg(httpMessageStruct, HttpUtil.createHttpCarbonMessage(isRequest));
        String propertyName = context.getStringArgument(0);

        Object propertyValue = httpCarbonMessage.getProperty(propertyName);

        if (propertyValue == null) {
            return new BValue[0];
        }

        if (propertyValue instanceof String) {
            return new BValue[] { new BString((String) propertyValue) };
        } else {
            throw new BallerinaException("Property value is of unknown type : " + propertyValue.getClass().getName());
        }
    }

    public static void setProperty(Context context, boolean isRequest) {
        BMap<String, BValue> httpMessageStruct = (BMap<String, BValue>) context.getRefArgument(0);
        String propertyName = context.getStringArgument(0);
        String propertyValue = context.getStringArgument(1);

        if (propertyName != null && propertyValue != null) {
            HttpCarbonMessage httpCarbonMessage = HttpUtil
                    .getCarbonMsg(httpMessageStruct, HttpUtil.createHttpCarbonMessage(isRequest));
            httpCarbonMessage.setProperty(propertyName, propertyValue);
        }
    }

    /**
     * Set new entity to in/out request/response struct.
     *
     * @param context           ballerina context.
     * @param httpMessageStruct request/response struct.
     */
    public static BMap<String, BValue> createNewEntity(Context context, BMap<String, BValue> httpMessageStruct) {
        BMap<String, BValue> entity = ConnectorUtils.createAndGetStruct(context, PROTOCOL_PACKAGE_MIME, ENTITY);
        HttpCarbonMessage httpCarbonMessage = HttpUtil.getCarbonMsg(httpMessageStruct,
                HttpUtil.createHttpCarbonMessage(isRequestStruct(httpMessageStruct)));
        entity.addNativeData(ENTITY_HEADERS, httpCarbonMessage.getHeaders());
        entity.addNativeData(ENTITY_BYTE_CHANNEL, null);
        httpMessageStruct.put(isRequestStruct(httpMessageStruct) ? REQUEST_ENTITY_FIELD : RESPONSE_ENTITY_FIELD
                , entity);
        httpMessageStruct.addNativeData(IS_BODY_BYTE_CHANNEL_ALREADY_SET, false);
        return entity;
    }

    /**
     * Set the given entity to request or response message.
     *
     * @param context   Ballerina context
     * @param isRequest boolean representing whether the message is a request or a response
     */
    public static void setEntity(Context context, boolean isRequest) {
        BMap<String, BValue> httpMessageStruct = (BMap<String, BValue>) context.getRefArgument(HTTP_MESSAGE_INDEX);

        HttpCarbonMessage httpCarbonMessage = HttpUtil.getCarbonMsg(httpMessageStruct,
                HttpUtil.createHttpCarbonMessage(isRequest));
        BMap<String, BValue> entity = (BMap<String, BValue>) context.getRefArgument(ENTITY_INDEX);
        String contentType = MimeUtil.getContentTypeWithParameters(entity);
        if (checkEntityBodyAvailability(entity)) {
            httpCarbonMessage.waitAndReleaseAllEntities();
            if (contentType == null) {
                contentType = OCTET_STREAM;
            }
            HeaderUtil.setHeaderToEntity(entity, HttpHeaderNames.CONTENT_TYPE.toString(), contentType);
        }
        httpMessageStruct.put(isRequest ? REQUEST_ENTITY_FIELD : RESPONSE_ENTITY_FIELD, entity);
        httpMessageStruct.addNativeData(IS_BODY_BYTE_CHANNEL_ALREADY_SET, checkEntityBodyAvailability(entity));
    }

    /**
     * Get the entity from request or response.
     *
     * @param context            Ballerina context
     * @param isRequest          boolean representing whether the message is a request or a response
     * @param entityBodyRequired boolean representing whether the entity body is required
     * @return Entity of the request or response
     */
    public static BValue[] getEntity(Context context, boolean isRequest, boolean entityBodyRequired) {
        try {
            BMap<String, BValue> httpMessageStruct = (BMap<String, BValue>) context.getRefArgument(HTTP_MESSAGE_INDEX);
            BMap<String, BValue> entity = (BMap<String, BValue>) httpMessageStruct
                    .get(isRequest ? REQUEST_ENTITY_FIELD : RESPONSE_ENTITY_FIELD);
            boolean byteChannelAlreadySet = false;

            if (httpMessageStruct.getNativeData(IS_BODY_BYTE_CHANNEL_ALREADY_SET) != null) {
                byteChannelAlreadySet = (Boolean) httpMessageStruct.getNativeData(IS_BODY_BYTE_CHANNEL_ALREADY_SET);
            }
            if (entityBodyRequired && !byteChannelAlreadySet) {
                populateEntityBody(context, httpMessageStruct, entity, isRequest);
            }
            
            // Entity cannot be null, since it is not a nullable field in http:Request or http:Response
            if (entity.isEmpty()) {
                entity = createNewEntity(context, httpMessageStruct);
            }
            return new BValue[]{entity};
        } catch (Throwable throwable) {
            return new BValue[]{MimeUtil.createError(context,
                    "Error occurred during entity construction: " + throwable.getMessage())};
        }
    }

    /**
     * Populate entity with the relevant body content.
     *
     * @param context           Represent ballerina context
     * @param httpMessageStruct Represent ballerina request/response
     * @param entity            Represent an entity
     * @param isRequest         boolean representing whether the message is a request or a response
     */
    public static void populateEntityBody(Context context, BMap<String, BValue> httpMessageStruct,
                                          BMap<String, BValue> entity, boolean isRequest) {
        HttpCarbonMessage httpCarbonMessage = HttpUtil
                .getCarbonMsg(httpMessageStruct, HttpUtil.createHttpCarbonMessage(isRequest));
        HttpMessageDataStreamer httpMessageDataStreamer = new HttpMessageDataStreamer(httpCarbonMessage);
        String contentType = httpCarbonMessage.getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
        if (MimeUtil.isNotNullAndEmpty(contentType) && contentType.startsWith(MULTIPART_AS_PRIMARY_TYPE)
                && context != null) {
            MultipartDecoder.parseBody(context, entity, contentType, httpMessageDataStreamer.getInputStream());
        } else {
            long contentLength = NO_CONTENT_LENGTH_FOUND;
            String lengthStr = httpCarbonMessage.getHeader(HttpHeaderNames.CONTENT_LENGTH.toString());
            try {
                contentLength = lengthStr != null ? Long.parseLong(lengthStr) : contentLength;
                if (contentLength == NO_CONTENT_LENGTH_FOUND) {
                    //Read one byte to make sure the incoming stream has data
                    contentLength = httpCarbonMessage.countMessageLengthTill(ONE_BYTE);
                }
            } catch (NumberFormatException e) {
                throw new BallerinaException("Invalid content length");
            }
            if (contentLength > 0) {
                entity.addNativeData(ENTITY_BYTE_CHANNEL, new EntityWrapper(
                        new EntityBodyChannel(httpMessageDataStreamer.getInputStream())));
            }
        }
        httpMessageStruct.put(isRequest ? REQUEST_ENTITY_FIELD : RESPONSE_ENTITY_FIELD, entity);
        httpMessageStruct.addNativeData(IS_BODY_BYTE_CHANNEL_ALREADY_SET, true);
    }

    public static BMap<String, BValue> extractEntity(BMap<String, BValue> httpMessageStruct) {
        Object isEntityBodyAvailable = httpMessageStruct.getNativeData(IS_BODY_BYTE_CHANNEL_ALREADY_SET);
        if (isEntityBodyAvailable == null || !((Boolean) isEntityBodyAvailable)) {
            return null;
        }
        return (BMap<String, BValue>) httpMessageStruct
                .get(isRequestStruct(httpMessageStruct) ? REQUEST_ENTITY_FIELD : RESPONSE_ENTITY_FIELD);
    }

    public static void closeMessageOutputStream(OutputStream messageOutputStream) {
        try {
            if (messageOutputStream != null) {
                messageOutputStream.close();
            }
        } catch (IOException e) {
            log.error("Couldn't close message output stream", e);
        }
    }

    public static void prepareOutboundResponse(Context context, HttpCarbonMessage inboundRequestMsg,
                                               HttpCarbonMessage outboundResponseMsg,
                                               BMap<String, BValue> outboundResponseStruct) {

        HttpUtil.checkEntityAvailability(context, outboundResponseStruct);

        HttpUtil.addHTTPSessionAndCorsHeaders(context, inboundRequestMsg, outboundResponseMsg);
        HttpUtil.enrichOutboundMessage(outboundResponseMsg, outboundResponseStruct);
        HttpUtil.setCompressionHeaders(context, inboundRequestMsg, outboundResponseMsg);
        HttpUtil.setChunkingHeader(context, outboundResponseMsg);
    }

    public static BMap<String, BValue> createSessionStruct(Context context, Session session) {
        BMap<String, BValue> sessionStruct = ConnectorUtils
                .createAndGetStruct(context, HttpConstants.PROTOCOL_PACKAGE_HTTP, HttpConstants.SESSION);
        //Add session to the struct as a native data
        sessionStruct.addNativeData(HttpConstants.HTTP_SESSION, session);
        return sessionStruct;
    }

    public static String getSessionID(String cookieHeader) {
        return Arrays.stream(cookieHeader.split(";"))
                .filter(cookie -> cookie.trim().startsWith(HttpConstants.SESSION_ID))
                .findFirst().get().trim().substring(HttpConstants.SESSION_ID.length());
    }

    public static void addHTTPSessionAndCorsHeaders(Context context, HttpCarbonMessage requestMsg,
                                                    HttpCarbonMessage responseMsg) {
        //TODO Remove once service session LC is introduced
//        Session session = (Session) requestMsg.getProperty(HttpConstants.HTTP_SESSION);
//        if (session != null) {
//            boolean isSecureRequest = false;
//            AnnAttachmentInfo configAnn = context.getServiceInfo().getAnnotationAttachmentInfo(
//                    HttpConstants.PROTOCOL_PACKAGE_HTTP, HttpConstants.ANN_NAME_CONFIG);
//            if (configAnn != null) {
//                AnnAttributeValue httpsPortAttrVal = configAnn
//                        .getAttributeValue(HttpConstants.ANN_CONFIG_ATTR_HTTPS_PORT);
//                if (httpsPortAttrVal != null) {
//                    Integer listenerPort = (Integer) requestMsg.getProperty(HttpConstants.LISTENER_PORT);
//                    if (listenerPort != null && httpsPortAttrVal.getIntValue() == listenerPort) {
//                        isSecureRequest = true;
//                    }
//                }
//            }
//            session.generateSessionHeader(responseMsg, isSecureRequest);
//        }
        //Process CORS if exists.
        if (requestMsg.getHeader(HttpHeaderNames.ORIGIN.toString()) != null) {
            CorsHeaderGenerator.process(requestMsg, responseMsg, true);
        }
    }

    public static HttpResponseFuture sendOutboundResponse(HttpCarbonMessage requestMsg,
                                                          HttpCarbonMessage responseMsg) {
        HttpResponseFuture responseFuture;
        try {
            responseFuture = requestMsg.respond(responseMsg);
        } catch (org.wso2.transport.http.netty.contract.ServerConnectorException e) {
            throw new BallerinaConnectorException("Error occurred during response", e);
        }
        return responseFuture;
    }

    /**
     * Sends an HTTP/2 Server Push message back to the client.
     *
     * @param requestMsg   the request message associated to the server push response
     * @param pushResponse the server push message
     * @param pushPromise  the push promise associated with the server push
     * @return the future to get notifications of the operation asynchronously
     */
    public static HttpResponseFuture pushResponse(HttpCarbonMessage requestMsg, HttpCarbonMessage pushResponse,
                                                  Http2PushPromise pushPromise) {
        HttpResponseFuture responseFuture;
        try {
            responseFuture = requestMsg.pushResponse(pushResponse, pushPromise);
        } catch (org.wso2.transport.http.netty.contract.ServerConnectorException e) {
            throw new BallerinaConnectorException("Error occurred while sending a server push message", e);
        }
        return responseFuture;
    }

    /**
     * Sends an HTTP/2 Push Promise message back to the client.
     *
     * @param requestMsg  the request message associated to the push promise
     * @param pushPromise the push promise message
     * @return the future to get notifications of the operation asynchronously
     */
    public static HttpResponseFuture pushPromise(HttpCarbonMessage requestMsg, Http2PushPromise pushPromise) {
        HttpResponseFuture responseFuture;
        try {
            responseFuture = requestMsg.pushPromise(pushPromise);
        } catch (org.wso2.transport.http.netty.contract.ServerConnectorException e) {
            throw new BallerinaConnectorException("Error occurred during response", e);
        }
        return responseFuture;
    }

    public static void handleFailure(HttpCarbonMessage requestMessage, BallerinaConnectorException ex) {
        String errorMsg = ex.getMessage();
        int statusCode = getStatusCode(requestMessage, errorMsg);
        sendOutboundResponse(requestMessage, createErrorMessage(errorMsg, statusCode));
    }

    public static void handleFailure(HttpCarbonMessage requestMessage, BMap<String, BValue> error) {
        String errorMsg = error.get(BLangVMErrors.ERROR_MESSAGE_FIELD).stringValue();
        int statusCode = getStatusCode(requestMessage, errorMsg);
        ErrorHandlerUtils.printError("error: " + BLangVMErrors.getPrintableStackTrace(error));
        sendOutboundResponse(requestMessage, createErrorMessage(errorMsg, statusCode));
    }

    private static int getStatusCode(HttpCarbonMessage requestMessage, String errorMsg) {
        Object carbonStatusCode = requestMessage.getProperty(HttpConstants.HTTP_STATUS_CODE);
        if (carbonStatusCode == null) {
            //log only the internal server errors
            log.error(errorMsg);
            return HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
        }
        return Integer.parseInt(carbonStatusCode.toString());
    }

    public static HttpCarbonMessage createErrorMessage(String payload, int statusCode) {
        HttpCarbonMessage response = HttpUtil.createHttpCarbonMessage(false);
        response.waitAndReleaseAllEntities();
        if (payload != null) {
            payload = lowerCaseTheFirstLetter(payload);
            response.addHttpContent(new DefaultLastHttpContent(Unpooled.wrappedBuffer(payload.getBytes())));
        } else {
            response.addHttpContent(new DefaultLastHttpContent());
        }
        setHttpStatusCodes(statusCode, response);

        return response;
    }

    private static String lowerCaseTheFirstLetter(String payload) {
        if (!payload.isEmpty()) {
            char[] characters = payload.toCharArray();
            characters[0] = Character.toLowerCase(characters[0]);
            payload = new String(characters);
        }
        return payload;
    }

    private static void setHttpStatusCodes(int statusCode, HttpCarbonMessage response) {
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.set(HttpHeaderNames.CONTENT_TYPE, org.wso2.transport.http.netty.common.Constants.TEXT_PLAIN);

        response.setProperty(org.wso2.transport.http.netty.common.Constants.HTTP_STATUS_CODE, statusCode);
    }

    /**
     * Get error struct.
     *
     * @param context Represent ballerina context
     * @param errMsg  Error message
     * @return Error struct
     */
    public static BMap<String, BValue> getError(Context context, String errMsg) {
        return BLangVMErrors.createError(context, errMsg);
    }

    /**
     * Get error struct from throwable.
     *
     * @param context   Represent ballerina context
     * @param throwable Throwable representing the error.
     * @return Error struct
     */
    public static BMap<String, BValue> getError(Context context, Throwable throwable) {
        if (throwable.getMessage() == null) {
            return BLangVMErrors.createError(context, IO_EXCEPTION_OCCURED);
        } else {
            return BLangVMErrors.createError(context, throwable.getMessage());
        }
    }

    public static HttpCarbonMessage getCarbonMsg(BMap<String, BValue> struct, HttpCarbonMessage defaultMsg) {
        HttpCarbonMessage httpCarbonMessage = (HttpCarbonMessage) struct.getNativeData(TRANSPORT_MESSAGE);
        if (httpCarbonMessage != null) {
            return httpCarbonMessage;
        }
        addCarbonMsg(struct, defaultMsg);
        return defaultMsg;
    }

    /**
     * Gets the {@code Http2PushPromise} represented by the PushPromise struct.
     *
     * @param pushPromiseStruct  the push promise struct
     * @param defaultPushPromise the Http2PushPromise to use if the struct does not have native data of a push promise
     * @return the {@code Http2PushPromise} represented by the PushPromise struct
     */
    public static Http2PushPromise getPushPromise(BMap<String, BValue> pushPromiseStruct,
                                                  Http2PushPromise defaultPushPromise) {
        Http2PushPromise pushPromise =
                (Http2PushPromise) pushPromiseStruct.getNativeData(HttpConstants.TRANSPORT_PUSH_PROMISE);
        if (pushPromise != null) {
            return pushPromise;
        }
        pushPromiseStruct.addNativeData(HttpConstants.TRANSPORT_PUSH_PROMISE, defaultPushPromise);
        return defaultPushPromise;
    }

    /**
     * Populates the push promise struct from native {@code Http2PushPromise}.
     *
     * @param pushPromiseStruct the push promise struct
     * @param pushPromise the native Http2PushPromise
     */
    public static void populatePushPromiseStruct(BMap<String, BValue> pushPromiseStruct,
                                                 Http2PushPromise pushPromise) {
        pushPromiseStruct.addNativeData(HttpConstants.TRANSPORT_PUSH_PROMISE, pushPromise);
        pushPromiseStruct.put(HttpConstants.PUSH_PROMISE_PATH_FIELD, new BString(pushPromise.getPath()));
        pushPromiseStruct.put(HttpConstants.PUSH_PROMISE_METHOD_FIELD, new BString(pushPromise.getMethod()));
    }

    /**
     * Creates native {@code Http2PushPromise} from PushPromise struct.
     *
     * @param struct the PushPromise struct
     * @return the populated the native {@code Http2PushPromise}
     */
    public static Http2PushPromise createHttpPushPromise(BMap<String, BValue> struct) {
        String method = struct.get(HttpConstants.PUSH_PROMISE_METHOD_FIELD).stringValue();
        if (method == null || method.isEmpty()) {
            method = HttpConstants.HTTP_METHOD_GET;
        }

        String path = struct.get(HttpConstants.PUSH_PROMISE_PATH_FIELD).stringValue();
        if (path == null || path.isEmpty()) {
            path = HttpConstants.DEFAULT_BASE_PATH;
        }
        return new Http2PushPromise(method, path);
    }

    public static void addCarbonMsg(BMap<String, BValue> struct, HttpCarbonMessage httpCarbonMessage) {
        struct.addNativeData(TRANSPORT_MESSAGE, httpCarbonMessage);
    }

    public static void populateInboundRequest(BMap<String, BValue> inboundRequestStruct, BMap<String, BValue> entity,
                                              BMap<String, BValue> mediaType, HttpCarbonMessage inboundRequestMsg,
                                              ProgramFile programFile) {
        inboundRequestStruct.addNativeData(TRANSPORT_MESSAGE, inboundRequestMsg);
        inboundRequestStruct.addNativeData(REQUEST, true);

        enrichWithInboundRequestInfo(inboundRequestStruct, inboundRequestMsg);
        enrichWithInboundRequestHeaders(inboundRequestStruct, inboundRequestMsg);

        populateEntity(entity, mediaType, inboundRequestMsg);
        inboundRequestStruct.put(REQUEST_ENTITY_FIELD, entity);
        inboundRequestStruct.addNativeData(IS_BODY_BYTE_CHANNEL_ALREADY_SET, false);

        String cacheControlHeader = inboundRequestMsg.getHeader(CACHE_CONTROL.toString());
        if (cacheControlHeader != null) {
            BMap<String, BValue> cacheControlStruct =
                    BLangConnectorSPIUtil.createBStruct(programFile, PROTOCOL_PACKAGE_HTTP, REQUEST_CACHE_CONTROL);
            RequestCacheControlStruct requestCacheControl = new RequestCacheControlStruct(cacheControlStruct);
            requestCacheControl.populateStruct(cacheControlHeader);
            inboundRequestStruct.put(REQUEST_CACHE_CONTROL_FIELD, requestCacheControl.getStruct());
        }
    }

    private static void enrichWithInboundRequestHeaders(BMap<String, BValue> inboundRequestStruct,
                                                        HttpCarbonMessage inboundRequestMsg) {
        if (inboundRequestMsg.getHeader(HttpHeaderNames.USER_AGENT.toString()) != null) {
            String agent = inboundRequestMsg.getHeader(HttpHeaderNames.USER_AGENT.toString());
            inboundRequestStruct.put(HttpConstants.REQUEST_USER_AGENT_FIELD, new BString(agent));
            inboundRequestMsg.removeHeader(HttpHeaderNames.USER_AGENT.toString());
        }
    }

    private static void enrichWithInboundRequestInfo(BMap<String, BValue> inboundRequestStruct,
                                                     HttpCarbonMessage inboundRequestMsg) {
        inboundRequestStruct.put(HttpConstants.REQUEST_RAW_PATH_FIELD,
                new BString((String) inboundRequestMsg.getProperty(HttpConstants.REQUEST_URL)));
        inboundRequestStruct.put(HttpConstants.REQUEST_METHOD_FIELD,
                new BString((String) inboundRequestMsg.getProperty(HttpConstants.HTTP_METHOD)));
        inboundRequestStruct.put(HttpConstants.REQUEST_VERSION_FIELD,
                new BString((String) inboundRequestMsg.getProperty(HttpConstants.HTTP_VERSION)));
        Map<String, String> resourceArgValues =
                (Map<String, String>) inboundRequestMsg.getProperty(HttpConstants.RESOURCE_ARGS);
        if (resourceArgValues != null) {
            inboundRequestStruct.put(HttpConstants.REQUEST_EXTRA_PATH_INFO_FIELD,
                    new BString(resourceArgValues.get(HttpConstants.EXTRA_PATH_INFO)));
        }
    }

    /**
     * Populate connection information.
     *
     * @param connection Represent the connection struct
     * @param inboundMsg Represent carbon message.
     */
    public static void enrichConnectionInfo(BMap<String, BValue> connection, HttpCarbonMessage inboundMsg,
                                            Struct config) {
        connection.addNativeData(HttpConstants.TRANSPORT_MESSAGE, inboundMsg);
        connection.put(HttpConstants.HTTP_CONNECTOR_CONFIG_FIELD, (BMap<String, BValue>) config.getVMValue());
    }

    /**
     * Populate serviceEndpoint information.
     *
     * @param serviceEndpoint Represent the serviceEndpoint struct
     * @param inboundMsg Represent carbon message.
     * @param httpResource Represent Http Resource.
     */
    public static void enrichServiceEndpointInfo(BMap<String, BValue> serviceEndpoint, HttpCarbonMessage inboundMsg,
                                                 HttpResource httpResource, Struct config) {
        BMap<String, BValue> remote = BLangConnectorSPIUtil.createBStruct(
                httpResource.getBalResource().getResourceInfo().getServiceInfo().getPackageInfo().getProgramFile(),
                PROTOCOL_PACKAGE_HTTP, HttpConstants.REMOTE);
        BMap<String, BValue> local = BLangConnectorSPIUtil.createBStruct(
                httpResource.getBalResource().getResourceInfo().getServiceInfo().getPackageInfo().getProgramFile(),
                PROTOCOL_PACKAGE_HTTP, HttpConstants.LOCAL);

        Object remoteSocketAddress = inboundMsg.getProperty(HttpConstants.REMOTE_ADDRESS);
        if (remoteSocketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteSocketAddress;
            String remoteHost = inetSocketAddress.getHostName();
            long remotePort = inetSocketAddress.getPort();
            remote.put(HttpConstants.REMOTE_HOST_FIELD, new BString(remoteHost));
            remote.put(HttpConstants.REMOTE_PORT_FIELD, new BInteger(remotePort));
        }
        serviceEndpoint.put(HttpConstants.REMOTE_STRUCT_FIELD, remote);

        Object localSocketAddress = inboundMsg.getProperty(HttpConstants.LOCAL_ADDRESS);
        if (localSocketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) localSocketAddress;
            String localHost = inetSocketAddress.getHostName();
            long localPort = inetSocketAddress.getPort();
            local.put(HttpConstants.LOCAL_HOST_FIELD, new BString(localHost));
            local.put(HttpConstants.LOCAL_PORT_FIELD, new BInteger(localPort));
        }
        serviceEndpoint.put(HttpConstants.LOCAL_STRUCT_INDEX, local);
        serviceEndpoint.put(HttpConstants.SERVICE_ENDPOINT_PROTOCOL_FIELD,
                new BString((String) inboundMsg.getProperty(HttpConstants.PROTOCOL)));
        serviceEndpoint.put(HttpConstants.SERVICE_ENDPOINT_CONFIG_FIELD, (BMap<String, BValue>) config.getVMValue());
    }

    /**
     * Populate inbound response with headers and entity.
     * @param inboundResponse  Ballerina struct to represent response
     * @param entity    Entity of the response
     * @param mediaType Content type of the response
     * @param programFile  Cache control struct which holds the cache control directives related to the
*                              response
     * @param inboundResponseMsg      Represent carbon message.
     */
    public static void populateInboundResponse(BMap<String, BValue> inboundResponse, BMap<String, BValue> entity,
                                               BMap<String, BValue> mediaType, ProgramFile programFile,
                                               HttpCarbonMessage inboundResponseMsg) {
        inboundResponse.addNativeData(TRANSPORT_MESSAGE, inboundResponseMsg);
        int statusCode = (Integer) inboundResponseMsg.getProperty(HTTP_STATUS_CODE);
        inboundResponse.put(RESPONSE_STATUS_CODE_FIELD, new BInteger(statusCode));
        inboundResponse.put(RESPONSE_REASON_PHRASE_FIELD,
                new BString(HttpResponseStatus.valueOf(statusCode).reasonPhrase()));

        if (inboundResponseMsg.getHeader(HttpHeaderNames.SERVER.toString()) != null) {
            inboundResponse.put(HttpConstants.RESPONSE_SERVER_FIELD,
                    new BString(inboundResponseMsg.getHeader(HttpHeaderNames.SERVER.toString())));
            inboundResponseMsg.removeHeader(HttpHeaderNames.SERVER.toString());
        }

        if (inboundResponseMsg.getProperty(RESOLVED_REQUESTED_URI) != null) {
            inboundResponse.put(RESOLVED_REQUESTED_URI_FIELD,
                    new BString(inboundResponseMsg.getProperty(RESOLVED_REQUESTED_URI).toString()));
        }

        String cacheControlHeader = inboundResponseMsg.getHeader(CACHE_CONTROL.toString());
        if (cacheControlHeader != null) {
            ResponseCacheControlStruct responseCacheControl
                    = new ResponseCacheControlStruct(
                            programFile.getPackageInfo(PROTOCOL_PACKAGE_HTTP).getStructInfo(RESPONSE_CACHE_CONTROL));
            responseCacheControl.populateStruct(cacheControlHeader);
            inboundResponse.put(RESPONSE_CACHE_CONTROL_FIELD, responseCacheControl.getStruct());
        }

        populateEntity(entity, mediaType, inboundResponseMsg);
        inboundResponse.put(RESPONSE_ENTITY_FIELD, entity);
        inboundResponse.addNativeData(IS_BODY_BYTE_CHANNEL_ALREADY_SET, false);
    }

    /**
     * Populate entity with headers, content-type and content-length.
     *
     * @param entity    Represent an entity struct
     * @param mediaType mediaType struct that needs to be set to the entity
     * @param cMsg      Represent a carbon message
     */
    private static void populateEntity(BMap<String, BValue> entity, BMap<String, BValue> mediaType,
                                       HttpCarbonMessage cMsg) {
        String contentType = cMsg.getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
        MimeUtil.setContentType(mediaType, entity, contentType);
        long contentLength = -1;
        String lengthStr = cMsg.getHeader(HttpHeaderNames.CONTENT_LENGTH.toString());
        try {
            contentLength = lengthStr != null ? Long.parseLong(lengthStr) : contentLength;
            MimeUtil.setContentLength(entity, contentLength);
        } catch (NumberFormatException e) {
            throw new BallerinaException("Invalid content length");
        }
        entity.addNativeData(ENTITY_HEADERS, cMsg.getHeaders());
    }

    /**
     * Set headers and properties of request/response struct to the outbound transport message.
     *
     * @param outboundMsg    transport Http carbon message.
     * @param outboundStruct req/resp struct.
     */
    public static void enrichOutboundMessage(HttpCarbonMessage outboundMsg, BMap<String, BValue> outboundStruct) {
        setHeadersToTransportMessage(outboundMsg, outboundStruct);
        setPropertiesToTransportMessage(outboundMsg, outboundStruct);
    }

    @SuppressWarnings("unchecked")
    private static void setHeadersToTransportMessage(HttpCarbonMessage outboundMsg, BMap<String, BValue> struct) {
        BMap<String, BValue> entityStruct = (BMap<String, BValue>) struct
                .get(isRequestStruct(struct) ? REQUEST_ENTITY_FIELD : RESPONSE_ENTITY_FIELD);
        HttpHeaders transportHeaders = outboundMsg.getHeaders();
        if (isRequestStruct(struct) || isResponseStruct(struct)) {
            addRemovedPropertiesBackToHeadersMap(struct, transportHeaders);
            // Since now the InRequest & OutRequest are merged to a single Request and InResponse & OutResponse
            // are merged to a single Response, without returning need to populate all headers from the struct
            // to the HttpCarbonMessage.
            // TODO: refactor this logic properly.
            // return;
        }
        HttpHeaders httpHeaders = (HttpHeaders) entityStruct.getNativeData(ENTITY_HEADERS);
        if (httpHeaders != transportHeaders) {
            //This is done only when the entity map and transport message do not refer to the same header map
            if (httpHeaders != null) {
                transportHeaders.add(httpHeaders);
            }
            //Once the headers are synced, set the entity headers to transport message headers so that they
            //both refer the same header map for future operations
            entityStruct.addNativeData(ENTITY_HEADERS, outboundMsg.getHeaders());
        }
    }

    public static boolean isRequestStruct(BMap<String, BValue> struct) {
        return struct.getType().getName().equals(REQUEST);
    }

    private static boolean isResponseStruct(BMap<String, BValue> struct) {
        return struct.getType().getName().equals(HttpConstants.RESPONSE);
    }

    private static void addRemovedPropertiesBackToHeadersMap(BMap<String, BValue> struct,
                                                             HttpHeaders transportHeaders) {
        if (isRequestStruct(struct)) {
            BValue userAgent = struct.get(HttpConstants.REQUEST_USER_AGENT_FIELD);
            if (userAgent != null && !userAgent.stringValue().isEmpty()) {
                transportHeaders.set(HttpHeaderNames.USER_AGENT.toString(), userAgent.stringValue());
            }
        } else {
            BValue server = struct.get(HttpConstants.RESPONSE_SERVER_FIELD);
            if (server != null && !server.stringValue().isEmpty()) {
                transportHeaders.set(HttpHeaderNames.SERVER.toString(), server.stringValue());
            }
        }
    }

    private static void setPropertiesToTransportMessage(HttpCarbonMessage outboundResponseMsg,
                                                        BMap<String, BValue> struct) {
        if (isResponseStruct(struct)) {
            long statusCode = ((BInteger) struct.get(RESPONSE_STATUS_CODE_FIELD)).intValue();
            if (statusCode != 0) {
                outboundResponseMsg.setProperty(HttpConstants.HTTP_STATUS_CODE, getIntValue(statusCode));
            }
            BValue respPhrase = struct.get(RESPONSE_REASON_PHRASE_FIELD);
            if (respPhrase != null && !respPhrase.stringValue().isEmpty()) {
                outboundResponseMsg.setProperty(HttpConstants.HTTP_REASON_PHRASE, respPhrase.stringValue());
            }
        }
    }

    /**
     * Check the existence of entity. Set new entity of not present.
     *
     * @param context ballerina context.
     * @param struct  request/response struct.
     */
    public static void checkEntityAvailability(Context context, BMap<String, BValue> struct) {
        BMap<String, BValue> entity = (BMap<String, BValue>) struct
                .get(isRequestStruct(struct) ? REQUEST_ENTITY_FIELD : RESPONSE_ENTITY_FIELD);
        if (entity == null) {
            createNewEntity(context, struct);
        }
    }

    /**
     * Check the existence of the message entity data source.
     *
     * @param struct  request/response struct.
     * @return true if the message entity data source is available else false.
     */
    public static boolean isEntityDataSourceAvailable(BMap<String, BValue> struct) {
        BMap<String, BValue> entityStruct = (BMap<String, BValue>) struct
                .get(isRequestStruct(struct) ? REQUEST_ENTITY_FIELD : RESPONSE_ENTITY_FIELD);
        return (entityStruct != null && EntityBodyHandler.getMessageDataSource(entityStruct) != null);
    }

    private static void setCompressionHeaders(Context context, HttpCarbonMessage requestMsg, HttpCarbonMessage
            outboundResponseMsg) {
        Service serviceInstance = BLangConnectorSPIUtil.getService(context.getProgramFile(),
                context.getServiceInfo().getType());
        Annotation configAnnot = getServiceConfigAnnotation(serviceInstance, PROTOCOL_PACKAGE_HTTP);
        if (configAnnot == null) {
            return;
        }
        String contentEncoding = outboundResponseMsg.getHeaders().get(HttpHeaderNames.CONTENT_ENCODING);
        if (contentEncoding != null) {
            return;
        }
        String compressionValue = configAnnot.getValue().getRefField(ANN_CONFIG_ATTR_COMPRESSION).getStringValue();
        String acceptEncodingValue = requestMsg.getHeaders().get(HttpHeaderNames.ACCEPT_ENCODING);
        if (ALWAYS.equalsIgnoreCase(compressionValue)) {
            if (acceptEncodingValue == null || HTTP_TRANSFER_ENCODING_IDENTITY.equals(acceptEncodingValue)) {
                outboundResponseMsg.getHeaders().set(HttpHeaderNames.CONTENT_ENCODING, ENCODING_GZIP);
            }
        } else if (NEVER.equalsIgnoreCase(compressionValue)) {
            outboundResponseMsg.getHeaders().set(HttpHeaderNames.CONTENT_ENCODING,
                    HTTP_TRANSFER_ENCODING_IDENTITY);
        }
    }

    public static String getListenerInterface(String host, int port) {
        host = host != null ? host : "0.0.0.0";
        return host + ":" + port;
    }

    public static ChunkConfig getChunkConfig(String chunkConfig) {
        switch (chunkConfig) {
            case HttpConstants.AUTO:
                return ChunkConfig.AUTO;
            case HttpConstants.ALWAYS:
                return ChunkConfig.ALWAYS;
            case NEVER:
                return ChunkConfig.NEVER;
            default:
                throw new BallerinaConnectorException(
                        "Invalid configuration found for Transfer-Encoding: " + chunkConfig);
        }
    }

    public static KeepAliveConfig getKeepAliveConfig(String keepAliveConfig) {
        switch (keepAliveConfig) {
            case HttpConstants.AUTO:
                return KeepAliveConfig.AUTO;
            case HttpConstants.ALWAYS:
                return KeepAliveConfig.ALWAYS;
            case NEVER:
                return KeepAliveConfig.NEVER;
            default:
                throw new BallerinaConnectorException(
                        "Invalid configuration found for Keep-Alive: " + keepAliveConfig);
        }
    }

    public static ForwardedExtensionConfig getForwardedExtensionConfig(String forwarded) {
        ForwardedExtensionConfig forwardedConfig;
        if (HttpConstants.FORWARDED_ENABLE.equalsIgnoreCase(forwarded)) {
            forwardedConfig = ForwardedExtensionConfig.ENABLE;
        } else if (HttpConstants.FORWARDED_TRANSITION.equalsIgnoreCase(forwarded)) {
            forwardedConfig = ForwardedExtensionConfig.TRANSITION;
        } else if (HttpConstants.FORWARDED_DISABLE.equalsIgnoreCase(forwarded)) {
            forwardedConfig = ForwardedExtensionConfig.DISABLE;
        } else {
            throw new BallerinaConnectorException("Invalid configuration found for Forwarded : " + forwarded);
        }
        return forwardedConfig;
    }

    private static void extractHttpsConfig(Annotation configInfo, Set<ListenerConfiguration> listenerConfSet) {
        // Retrieve secure port from either http of ws configuration annotation.
        AnnAttrValue httpsPortAttrVal;
        if (configInfo.getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_HTTPS_PORT) == null) {
            httpsPortAttrVal =
                    configInfo.getAnnAttrValue(WebSocketConstants.ANN_CONFIG_ATTR_WSS_PORT);
        } else {
            httpsPortAttrVal = configInfo.getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_HTTPS_PORT);
        }

        AnnAttrValue keyStoreFileAttrVal = configInfo.getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_KEY_STORE_FILE);
        AnnAttrValue keyStorePasswordAttrVal = configInfo.getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_KEY_STORE_PASS);
        AnnAttrValue certPasswordAttrVal = configInfo.getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_CERT_PASS);
        AnnAttrValue trustStoreFileAttrVal = configInfo.getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_TRUST_STORE_FILE);
        AnnAttrValue trustStorePasswordAttrVal = configInfo.getAnnAttrValue(
                HttpConstants.ANN_CONFIG_ATTR_TRUST_STORE_PASS);
        AnnAttrValue sslVerifyClientAttrVal = configInfo.getAnnAttrValue(
                HttpConstants.ANN_CONFIG_ATTR_SSL_VERIFY_CLIENT);
        AnnAttrValue sslEnabledProtocolsAttrVal = configInfo
                .getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_SSL_ENABLED_PROTOCOLS);
        AnnAttrValue ciphersAttrVal = configInfo.getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_CIPHERS);
        AnnAttrValue sslProtocolAttrVal = configInfo.getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_SSL_PROTOCOL);
        AnnAttrValue hostAttrVal = configInfo.getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_HOST);
        AnnAttrValue certificateValidationEnabledAttrValue = configInfo
                .getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_VALIDATE_CERT_ENABLED);
        AnnAttrValue cacheSizeAttrValue = configInfo.getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_CACHE_SIZE);
        AnnAttrValue cacheValidityPeriodAttrValue = configInfo
                .getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_CACHE_VALIDITY_PERIOD);

        ListenerConfiguration listenerConfiguration = new ListenerConfiguration();
        if (httpsPortAttrVal != null && httpsPortAttrVal.getIntValue() > 0) {
            listenerConfiguration.setPort(Math.toIntExact(httpsPortAttrVal.getIntValue()));
            listenerConfiguration.setScheme(HttpConstants.PROTOCOL_HTTPS);

            if (hostAttrVal != null && hostAttrVal.getStringValue() != null) {
                listenerConfiguration.setHost(hostAttrVal.getStringValue());
            } else {
                listenerConfiguration.setHost(HttpConstants.HTTP_DEFAULT_HOST);
            }

            if (keyStoreFileAttrVal == null || keyStoreFileAttrVal.getStringValue() == null) {
                //TODO get from language pack, and add location
                throw new BallerinaConnectorException("Keystore location must be provided for secure connection");
            }
            if (keyStorePasswordAttrVal == null || keyStorePasswordAttrVal.getStringValue() == null) {
                //TODO get from language pack, and add location
                throw new BallerinaConnectorException("Keystore password value must be provided for secure connection");
            }
            if (certPasswordAttrVal == null || certPasswordAttrVal.getStringValue() == null) {
                //TODO get from language pack, and add location
                throw new BallerinaConnectorException(
                        "Certificate password value must be provided for secure connection");
            }
            if ((trustStoreFileAttrVal == null || trustStoreFileAttrVal.getStringValue() == null)
                    && sslVerifyClientAttrVal != null) {
                //TODO get from language pack, and add location
                throw new BallerinaException("Truststore location must be provided to enable Mutual SSL");
            }
            if ((trustStorePasswordAttrVal == null || trustStorePasswordAttrVal.getStringValue() == null)
                    && sslVerifyClientAttrVal != null) {
                //TODO get from language pack, and add location
                throw new BallerinaException("Truststore password value must be provided to enable Mutual SSL");
            }

            listenerConfiguration.setTLSStoreType(HttpConstants.PKCS_STORE_TYPE);
            listenerConfiguration.setKeyStoreFile(keyStoreFileAttrVal.getStringValue());
            listenerConfiguration.setKeyStorePass(keyStorePasswordAttrVal.getStringValue());
            listenerConfiguration.setCertPass(certPasswordAttrVal.getStringValue());

            if (sslVerifyClientAttrVal != null) {
                listenerConfiguration.setVerifyClient(sslVerifyClientAttrVal.getStringValue());
            }
            if (trustStoreFileAttrVal != null) {
                listenerConfiguration.setTrustStoreFile(trustStoreFileAttrVal.getStringValue());
            }
            if (trustStorePasswordAttrVal != null) {
                listenerConfiguration.setTrustStorePass(trustStorePasswordAttrVal.getStringValue());
            }
            if (certificateValidationEnabledAttrValue != null && certificateValidationEnabledAttrValue
                    .getBooleanValue()) {
                listenerConfiguration.setValidateCertEnabled(certificateValidationEnabledAttrValue.getBooleanValue());
                if (cacheSizeAttrValue != null) {
                    listenerConfiguration.setCacheSize((int) cacheSizeAttrValue.getIntValue());
                }
                if (cacheValidityPeriodAttrValue != null) {
                    listenerConfiguration.setCacheValidityPeriod((int) cacheValidityPeriodAttrValue.getIntValue());
                }
            }
            List<Parameter> serverParams = new ArrayList<>();
            Parameter serverCiphers;
            if (sslEnabledProtocolsAttrVal != null && sslEnabledProtocolsAttrVal.getStringValue() != null) {
                serverCiphers = new Parameter(HttpConstants.ANN_CONFIG_ATTR_SSL_ENABLED_PROTOCOLS,
                        sslEnabledProtocolsAttrVal.getStringValue());
                serverParams.add(serverCiphers);
            }

            if (ciphersAttrVal != null && ciphersAttrVal.getStringValue() != null) {
                serverCiphers = new Parameter(HttpConstants.ANN_CONFIG_ATTR_CIPHERS, ciphersAttrVal.getStringValue());
                serverParams.add(serverCiphers);
            }

            if (!serverParams.isEmpty()) {
                listenerConfiguration.setParameters(serverParams);
            }

            if (sslProtocolAttrVal != null) {
                listenerConfiguration.setSSLProtocol(sslProtocolAttrVal.getStringValue());
            }

            listenerConfiguration
                    .setId(getListenerInterface(listenerConfiguration.getHost(), listenerConfiguration.getPort()));
            listenerConfSet.add(listenerConfiguration);
        }
    }

    public static HttpCarbonMessage createHttpCarbonMessage(boolean isRequest) {
        HttpCarbonMessage httpCarbonMessage;
        if (isRequest) {
            httpCarbonMessage = new HttpCarbonMessage(
                    new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, ""));
        } else {
            httpCarbonMessage = new HttpCarbonMessage(
                    new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
        }
        httpCarbonMessage.completeMessage();
        return httpCarbonMessage;
    }

    public static void checkFunctionValidity(BMap<String, BValue> connectionStruct, HttpCarbonMessage reqMsg) {
        serverConnectionStructCheck(reqMsg);
        methodInvocationCheck(connectionStruct, reqMsg);
    }

    private static void methodInvocationCheck(BMap<String, BValue> bStruct, HttpCarbonMessage reqMsg) {
        if (bStruct.getNativeData(METHOD_ACCESSED) != null || reqMsg == null) {
            throw new IllegalStateException("illegal function invocation");
        }

        if (!is100ContinueRequest(reqMsg)) {
            bStruct.addNativeData(METHOD_ACCESSED, true);
        }
    }

    public static void serverConnectionStructCheck(HttpCarbonMessage reqMsg) {
        if (reqMsg == null) {
            throw new BallerinaException("operation not allowed:invalid Connection variable");
        }
    }

    private static boolean is100ContinueRequest(HttpCarbonMessage reqMsg) {
        return HttpConstants.HEADER_VAL_100_CONTINUE.equalsIgnoreCase(
                reqMsg.getHeader(HttpHeaderNames.EXPECT.toString()));
    }

    public static Annotation getServiceConfigAnnotation(Service service, String pkgPath) {
        List<Annotation> annotationList = service
                .getAnnotationList(pkgPath, HttpConstants.ANN_NAME_HTTP_SERVICE_CONFIG);

        if (annotationList == null) {
            return null;
        }

        if (annotationList.size() > 1) {
            throw new BallerinaException(
                    "multiple service configuration annotations found in service: " + service.getName());
        }

        return annotationList.isEmpty() ? null : annotationList.get(0);
    }

    public static Annotation getServiceConfigStruct(Service service, String pkgPath) {
        List<Annotation> annotationList = service.getAnnotationList(pkgPath, HttpConstants.ANN_NAME_CONFIG);

        if (annotationList == null) {
            return null;
        }

        if (annotationList.size() > 1) {
            throw new BallerinaException(
                    "multiple service configuration annotations found in service: " + service.getName());
        }

        return annotationList.isEmpty() ? null : annotationList.get(0);
    }

//    protected static void populateKeepAliveAndCompressionStatus(HttpService service, Annotation annotation) {
//        if (annotation == null) {
//            return;
//        }
//        AnnAttrValue keepAliveAttrVal = annotation.getAnnAttrValue(HttpConstants.ANN_CONFIG_ATTR_KEEP_ALIVE);
//        if (keepAliveAttrVal != null) {
//            service.setKeepAlive(keepAliveAttrVal.getBooleanValue());
//        }
//
//        AnnAttrValue compressionEnabled = annotation.getAnnAttrValue(
//                HttpConstants.ANN_CONFIG_ATTR_COMPRESSION_ENABLED);
//        if (compressionEnabled != null) {
//            service.setCompressionEnabled(compressionEnabled.getBooleanValue());
//        }
//    }

    public static Annotation getResourceConfigAnnotation(Resource resource, String pkgPath) {
        List<Annotation> annotationList = resource.getAnnotationList(pkgPath, HttpConstants.ANN_NAME_RESOURCE_CONFIG);

        if (annotationList == null) {
            return null;
        }

        if (annotationList.size() > 1) {
            throw new BallerinaException(
                    "multiple resource configuration annotations found in resource: " +
                            resource.getServiceName() + "." + resource.getName());
        }

        return annotationList.isEmpty() ? null : annotationList.get(0);
    }

    private static int getIntValue(long val) {
        int intVal = (int) val;

        if (intVal != val) {
            throw new IllegalArgumentException("invalid argument: " + val);
        }

        return intVal;
    }

    public static String getContentTypeFromTransportMessage(HttpCarbonMessage transportMessage) {
        return transportMessage.getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
    }

    /**
     * If the given Content-Type header value doesn't have a boundary parameter value, get a new boundary string and
     * append it to Content-Type and set it to transport message.
     *
     * @param transportMessage Represent transport message
     * @param contentType      Represent the Content-Type header value
     * @return The boundary string that was extracted from header or the newly generated one
     */
    public static String addBoundaryIfNotExist(HttpCarbonMessage transportMessage, String contentType) {
        String boundaryString;
        BString boundaryValue = HeaderUtil.extractBoundaryParameter(contentType);
        boundaryString = boundaryValue != null ? boundaryValue.toString() :
                HttpUtil.addBoundaryParameter(transportMessage, contentType);
        return boundaryString;
    }

    /**
     * Generate a new boundary string and append it Content-Type and set that to transport message.
     *
     * @param transportMessage Represent transport message
     * @param contentType      Represent the Content-Type header value
     * @return The newly generated boundary string
     */
    private static String addBoundaryParameter(HttpCarbonMessage transportMessage, String contentType) {
        String boundaryString = null;
        if (contentType != null && contentType.startsWith(MULTIPART_AS_PRIMARY_TYPE)) {
            boundaryString = MimeUtil.getNewMultipartDelimiter();
            transportMessage.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), contentType + "; " + BOUNDARY + "=" +
                    boundaryString);
        }
        return boundaryString;
    }

    public static HttpWsConnectorFactory createHttpWsConnectionFactory() {
        return new DefaultHttpWsConnectorFactory();
    }

    public static void checkAndObserveHttpRequest(Context context, HttpCarbonMessage message) {
        Optional<ObserverContext> observerContext = ObservabilityUtils.getParentContext(context);
        observerContext.ifPresent(ctx -> {
            HttpUtil.injectHeaders(message, ObservabilityUtils.getContextProperties(ctx, TraceConstants.TRACE_HEADER));
            ctx.addTag(TAG_KEY_HTTP_METHOD, String.valueOf(message.getProperty(HttpConstants.HTTP_METHOD)));
            ctx.addTag(TAG_KEY_HTTP_URL, String.valueOf(message.getProperty(HttpConstants.TO)));
            ctx.addTag(TAG_KEY_PEER_ADDRESS,
                    message.getProperty(PROPERTY_HTTP_HOST) + ":" + message.getProperty(PROPERTY_HTTP_PORT));
            // Add HTTP Status Code tag. The HTTP status code will be set using the response message.
            // Sometimes the HTTP status code will not be set due to errors etc. Therefore, it's very important to set
            // some value to HTTP Status Code to make sure that tags will not change depending on various
            // circumstances.
            // HTTP Status code must be a number.
            ctx.addTag(TAG_KEY_HTTP_STATUS_CODE, Integer.toString(0));
        });
    }

    public static void injectHeaders(HttpCarbonMessage msg, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach((key, value) -> msg.setHeader(key, String.valueOf(value)));
        }
    }

    private static void setChunkingHeader(Context context, HttpCarbonMessage
            outboundResponseMsg) {
        Service serviceInstance = BLangConnectorSPIUtil.getService(context.getProgramFile(),
                context.getServiceInfo().getType());
        Annotation configAnnot = getServiceConfigAnnotation(serviceInstance, PROTOCOL_PACKAGE_HTTP);
        if (configAnnot == null) {
            return;
        }
        String transferValue = configAnnot.getValue().getRefField(ANN_CONFIG_ATTR_CHUNKING).getStringValue();
        if (transferValue != null) {
            outboundResponseMsg.setProperty(CHUNKING_CONFIG, getChunkConfig(transferValue));
        }
    }

    /**
     * Creates InResponse using the native {@code HttpCarbonMessage}.
     *
     * @param context           ballerina context
     * @param httpCarbonMessage the HttpCarbonMessage
     * @return the Response struct
     */
    public static BMap<String, BValue> createResponseStruct(Context context, HttpCarbonMessage httpCarbonMessage) {
        BMap<String, BValue> responseStruct = BLangConnectorSPIUtil.createBStruct(context,
                HttpConstants.PROTOCOL_PACKAGE_HTTP, HttpConstants.RESPONSE);
        BMap<String, BValue> entity =
                BLangConnectorSPIUtil.createBStruct(context, PROTOCOL_PACKAGE_MIME, HttpConstants.ENTITY);
        BMap<String, BValue> mediaType =
                BLangConnectorSPIUtil.createBStruct(context, PROTOCOL_PACKAGE_MIME, MEDIA_TYPE);

        HttpUtil.populateInboundResponse(responseStruct, entity, mediaType, context.getProgramFile(),
                httpCarbonMessage);
        return responseStruct;
    }

    public static void populateSSLConfiguration(SslConfiguration sslConfiguration, Struct secureSocket) {
        Struct trustStore = secureSocket.getStructField(HttpConstants.ENDPOINT_CONFIG_TRUST_STORE);
        Struct keyStore = secureSocket.getStructField(HttpConstants.ENDPOINT_CONFIG_KEY_STORE);
        Struct protocols = secureSocket.getStructField(HttpConstants.ENDPOINT_CONFIG_PROTOCOLS);
        Struct validateCert = secureSocket.getStructField(HttpConstants.ENDPOINT_CONFIG_VALIDATE_CERT);
        List<Parameter> clientParams = new ArrayList<>();
        if (trustStore != null) {
            String trustStoreFile = trustStore.getStringField(HttpConstants.FILE_PATH);
            if (StringUtils.isNotBlank(trustStoreFile)) {
                sslConfiguration.setTrustStoreFile(trustStoreFile);
            }
            String trustStorePassword = trustStore.getStringField(HttpConstants.PASSWORD);
            if (StringUtils.isNotBlank(trustStorePassword)) {
                sslConfiguration.setTrustStorePass(trustStorePassword);
            }
        }
        if (keyStore != null) {
            String keyStoreFile = keyStore.getStringField(HttpConstants.FILE_PATH);
            if (StringUtils.isNotBlank(keyStoreFile)) {
                sslConfiguration.setKeyStoreFile(keyStoreFile);
            }
            String keyStorePassword = keyStore.getStringField(HttpConstants.PASSWORD);
            if (StringUtils.isNotBlank(keyStorePassword)) {
                sslConfiguration.setKeyStorePassword(keyStorePassword);
            }
        }
        if (protocols != null) {
            List<Value> sslEnabledProtocolsValueList = Arrays
                    .asList(protocols.getArrayField(HttpConstants.ENABLED_PROTOCOLS));
            if (sslEnabledProtocolsValueList.size() > 0) {
                String sslEnabledProtocols = sslEnabledProtocolsValueList.stream().map(Value::getStringValue)
                        .collect(Collectors.joining(",", "", ""));
                Parameter clientProtocols = new Parameter(HttpConstants.SSL_ENABLED_PROTOCOLS,
                        sslEnabledProtocols);
                clientParams.add(clientProtocols);
            }
            String sslProtocol = protocols.getStringField(HttpConstants.PROTOCOL_VERSION);
            if (StringUtils.isNotBlank(sslProtocol)) {
                sslConfiguration.setSSLProtocol(sslProtocol);
            }
        }

        if (validateCert != null) {
            boolean validateCertEnabled = validateCert.getBooleanField(HttpConstants.ENABLE);
            int cacheSize = (int) validateCert.getIntField(HttpConstants.SSL_CONFIG_CACHE_SIZE);
            int cacheValidityPeriod = (int) validateCert
                    .getIntField(HttpConstants.SSL_CONFIG_CACHE_VALIDITY_PERIOD);
            sslConfiguration.setValidateCertEnabled(validateCertEnabled);
            if (cacheValidityPeriod != 0) {
                sslConfiguration.setCacheValidityPeriod(cacheValidityPeriod);
            }
            if (cacheSize != 0) {
                sslConfiguration.setCacheSize(cacheSize);
            }
        }
        boolean hostNameVerificationEnabled = secureSocket
                .getBooleanField(HttpConstants.SSL_CONFIG_HOST_NAME_VERIFICATION_ENABLED);
        boolean ocspStaplingEnabled = secureSocket.getBooleanField(HttpConstants.ENDPOINT_CONFIG_OCSP_STAPLING);
        sslConfiguration.setOcspStaplingEnabled(ocspStaplingEnabled);
        sslConfiguration.setHostNameVerificationEnabled(hostNameVerificationEnabled);

        List<Value> ciphersValueList = Arrays
                .asList(secureSocket.getArrayField(HttpConstants.SSL_CONFIG_CIPHERS));
        if (ciphersValueList.size() > 0) {
            String ciphers = ciphersValueList.stream().map(Value::getStringValue)
                    .collect(Collectors.joining(",", "", ""));
            Parameter clientCiphers = new Parameter(HttpConstants.CIPHERS, ciphers);
            clientParams.add(clientCiphers);
        }
        String enableSessionCreation = String.valueOf(secureSocket
                .getBooleanField(HttpConstants.SSL_CONFIG_ENABLE_SESSION_CREATION));
        Parameter clientEnableSessionCreation = new Parameter(HttpConstants.SSL_CONFIG_ENABLE_SESSION_CREATION,
                enableSessionCreation);
        clientParams.add(clientEnableSessionCreation);
        if (!clientParams.isEmpty()) {
            sslConfiguration.setParameters(clientParams);
        }
    }

    public static void setDefaultTrustStore(SslConfiguration sslConfiguration) {
        sslConfiguration.setTrustStoreFile(String.valueOf(
                Paths.get(System.getProperty("ballerina.home"), "bre", "security", "ballerinaTruststore.p12")));
        sslConfiguration.setTrustStorePass("ballerina");
    }
}
