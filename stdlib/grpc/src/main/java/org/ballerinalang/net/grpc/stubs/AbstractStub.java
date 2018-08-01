/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.net.grpc.stubs;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.net.grpc.ClientCall;
import org.ballerinalang.net.grpc.GrpcConstants;
import org.ballerinalang.net.grpc.Message;
import org.ballerinalang.net.grpc.MessageUtils;
import org.ballerinalang.net.grpc.OutboundMessage;
import org.ballerinalang.net.grpc.Status;
import org.ballerinalang.net.grpc.exception.ClientRuntimeException;
import org.ballerinalang.net.http.HttpConstants;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.wso2.transport.http.netty.common.Constants;
import org.wso2.transport.http.netty.contract.HttpClientConnector;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ballerinalang.net.grpc.GrpcConstants.AUTHORITY;
import static org.ballerinalang.net.grpc.GrpcConstants.SCHEME_HEADER;
import static org.ballerinalang.runtime.Constants.BALLERINA_VERSION;

/**
 * Abstract class for stub implementations.
 * <p>
 * Referenced from grpc-java implementation.
 * <p>
 *
 * @since 0.980.0
 */
public abstract class AbstractStub {

    private static final Logger logger = Logger.getLogger(AbstractStub.class.getName());
    private final HttpClientConnector connector;
    private final Struct endpointConfig;
    private static final String CACHE_BALLERINA_VERSION;

    static {
        CACHE_BALLERINA_VERSION = System.getProperty(BALLERINA_VERSION);
    }

    /**
     * Constructor for use by subclasses.
     *
     * @param connector the client connector which use to communicate.
     */
    AbstractStub(HttpClientConnector connector, Struct endpointConfig) {
        this.connector = connector;
        this.endpointConfig = endpointConfig;
    }

    /**
     * The underlying connector of the stub.
     */
    public final HttpClientConnector getConnector() {
        return connector;
    }

    /**
     * Returns remote endpoint config.
     */
    private Struct getEndpointConfig() {
        return endpointConfig;
    }

    OutboundMessage createOutboundRequest(HttpHeaders httpHeaders) {
        try {
            HttpCarbonMessage carbonMessage = MessageUtils.createHttpCarbonMessage(true);
            String urlString = getEndpointConfig().getStringField(GrpcConstants.CLIENT_ENDPOINT_URL);
            URL url = new URL(urlString);
            int port = getOutboundReqPort(url);
            String host = url.getHost();
            carbonMessage.setHeader(SCHEME_HEADER, url.getProtocol());
            carbonMessage.setHeader(AUTHORITY, urlString);
            setOutboundReqProperties(carbonMessage, url, port, host);
            setOutboundReqHeaders(carbonMessage, port, host);
            if (httpHeaders != null) {
                for (Map.Entry<String, String> headerEntry : httpHeaders.entries()) {
                    carbonMessage.setHeader(headerEntry.getKey(), headerEntry.getValue());
                }
            }
            return new OutboundMessage(carbonMessage);
        } catch (MalformedURLException e) {
            throw new BallerinaException("Malformed url specified. " + e.getMessage());
        } catch (Exception t) {
            throw new BallerinaException("Failed to prepare request. " + t.getMessage());
        }
    }

    private int getOutboundReqPort(URL url) {
        int port = 80;
        if (url.getPort() != -1) {
            port = url.getPort();
        } else if (url.getProtocol().equalsIgnoreCase(HttpConstants.PROTOCOL_HTTPS)) {
            port = 443;
        }
        return port;
    }

    private void setOutboundReqHeaders(HttpCarbonMessage outboundRequest, int port, String host) {
        HttpHeaders headers = outboundRequest.getHeaders();
        setHostHeader(host, port, headers);
        setOutboundUserAgent(headers);
        removeConnectionHeader(headers);
    }

    private void setOutboundReqProperties(HttpCarbonMessage outboundRequest, URL url, int port, String host) {
        outboundRequest.setProperty(Constants.HTTP_HOST, host);
        outboundRequest.setProperty(Constants.HTTP_PORT, port);
        String outboundReqPath = url.getPath();
        outboundRequest.setProperty(HttpConstants.TO, outboundReqPath);
        outboundRequest.setProperty(HttpConstants.PROTOCOL, url.getProtocol());
    }

    private void setHostHeader(String host, int port, HttpHeaders headers) {
        if (port == 80 || port == 443) {
            headers.set(HttpHeaderNames.HOST, host);
        } else {
            headers.set(HttpHeaderNames.HOST, host + ":" + port);
        }
    }

    private void removeConnectionHeader(HttpHeaders headers) {
        // Remove existing Connection header
        if (headers.contains(HttpHeaderNames.CONNECTION)) {
            headers.remove(HttpHeaderNames.CONNECTION);
        }
    }

    private void setOutboundUserAgent(HttpHeaders headers) {
        String userAgent;
        if (CACHE_BALLERINA_VERSION != null) {
            userAgent = "ballerina/" + CACHE_BALLERINA_VERSION;
        } else {
            userAgent = "ballerina";
        }
        if (!headers.contains(HttpHeaderNames.USER_AGENT)) { // If User-Agent is not already set from program
            headers.set(HttpHeaderNames.USER_AGENT, userAgent);
        }
    }

    /**
     * Cancel the call, and throws the exception.
     *
     * @param call client call.
     * @param t RuntimeException/Error.
     */
    static RuntimeException cancelThrow(ClientCall call, Throwable t) {
        try {
            call.cancel(null, t);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "RuntimeException encountered while closing call", e);
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else if (t instanceof Error) {
            throw (Error) t;
        }
        throw new ClientRuntimeException(t);
    }

    /**
     * Callbacks for receiving headers, response messages and completion status from the server.
     * <p>
     * Referenced from grpc-java implementation.
     * <p>
     */
    public interface Listener {

        /**
         * Calls when response headers is received.
         *
         * @param headers response headers.
         */
        void onHeaders(HttpHeaders headers);

        /**
         * Calls when response message is received.
         *
         * @param message response message.
         */
        void onMessage(Message message);

        /**
         *  Calls when call is closed.
         * <p>
         * <p>If {@code status} returns false for {@link Status#isOk()}, then the call failed.
         *
         * @param status   the result of the remote call.
         * @param trailers headers sent at call completion.
         */
        void onClose(Status status, HttpHeaders trailers);
    }
}
