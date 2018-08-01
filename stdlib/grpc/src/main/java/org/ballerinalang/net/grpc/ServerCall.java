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
package org.ballerinalang.net.grpc;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import org.ballerinalang.net.grpc.exception.ServerRuntimeException;
import org.ballerinalang.net.grpc.listener.ServerCallHandler;
import org.wso2.transport.http.netty.contract.ServerConnectorException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.ballerinalang.net.grpc.GrpcConstants.MESSAGE_ENCODING;

/**
 * Encapsulates a single call received from a remote client.
 * A call will receive zero or more request messages from the client and send zero or more response messages back.
 *
 * <p>
 * Referenced from grpc-java implementation.
 * <p>
 *
 * @since 0.980.0
 */
public final class ServerCall {

    /**
     * The accepted message encodings (i.e. compression) that can be used in the stream.
     */
    private static final String MESSAGE_ACCEPT_ENCODING = "grpc-accept-encoding";

    private static final String TOO_MANY_RESPONSES = "Too many responses";
    private static final String MISSING_RESPONSE = "Completed without a response";

    private final InboundMessage inboundMessage;
    private final OutboundMessage outboundMessage;
    private final MethodDescriptor method;

    private volatile boolean cancelled;
    private boolean sendHeadersCalled;
    private boolean closeCalled;
    private boolean messageSent;
    private Compressor compressor;
    private final String messageAcceptEncoding;

    private DecompressorRegistry decompressorRegistry;
    private CompressorRegistry compressorRegistry;

    ServerCall(InboundMessage inboundMessage, OutboundMessage outboundMessage, MethodDescriptor
            method, DecompressorRegistry decompressorRegistry, CompressorRegistry compressorRegistry) {
        this.inboundMessage = inboundMessage;
        this.outboundMessage = outboundMessage;
        this.method = method;
        this.decompressorRegistry = decompressorRegistry;
        this.compressorRegistry = compressorRegistry;
        this.messageAcceptEncoding = inboundMessage.getHeader(MESSAGE_ACCEPT_ENCODING);
    }

    public void sendHeaders(HttpHeaders headers) {
        if (sendHeadersCalled) {
            throw new IllegalStateException("sendHeaders has already been called");
        }
        if (closeCalled) {
            throw new IllegalStateException("call is closed");
        }
        outboundMessage.removeHeader(MESSAGE_ENCODING);
        if (compressor == null) {
            compressor = Codec.Identity.NONE;
        } else {
            if (messageAcceptEncoding != null) {
                List<String> acceptEncodings = Arrays.stream(messageAcceptEncoding.split("\\s*,\\s*"))
                        .map(mediaType -> mediaType.split("\\s*;\\s*")[0])
                        .collect(Collectors.toList());
                if (!acceptEncodings.contains(compressor.getMessageEncoding())) {
                    // resort to using no compression.
                    compressor = Codec.Identity.NONE;
                }
            } else {
                compressor = Codec.Identity.NONE;
            }
        }
        // Always put compressor, even if it's identity.
        outboundMessage.setHeader(MESSAGE_ENCODING, compressor.getMessageEncoding());
        outboundMessage.framer().setCompressor(compressor);
        outboundMessage.removeHeader(MESSAGE_ACCEPT_ENCODING);
        String advertisedEncodings = String.join(",", decompressorRegistry.getAdvertisedMessageEncodings());
        if (advertisedEncodings != null) {
            outboundMessage.setHeader(MESSAGE_ACCEPT_ENCODING, advertisedEncodings);
        }
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entries()) {
                outboundMessage.setHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        try {
            // Send response headers.
            inboundMessage.respond(outboundMessage.getResponseMessage());
        } catch (ServerConnectorException e) {
            throw new ServerRuntimeException("Error while sending the response.", e);
        }
        sendHeadersCalled = true;
    }

    public void sendMessage(Message message) {
        if (!sendHeadersCalled) {
            throw new IllegalStateException("sendHeaders has not been called");
        }
        if (closeCalled) {
            throw new IllegalStateException("call is closed");
        }
        if (method.getType().serverSendsOneMessage() && messageSent) {
            outboundMessage.complete(Status.Code.INTERNAL.toStatus().withDescription(TOO_MANY_RESPONSES), new
                    DefaultHttpHeaders());
            return;
        }
        messageSent = true;
        try {
            InputStream resp = method.streamResponse(message);
            outboundMessage.sendMessage(resp);
        } catch (Exception e) {
            close(Status.fromThrowable(e), new DefaultHttpHeaders());
        }
    }

    public void setCompression(String compressorName) {
        // Added here to give a better error message.
        if (sendHeadersCalled) {
            throw new IllegalStateException("sendHeaders has been called");
        }
        compressor = compressorRegistry.lookupCompressor(compressorName);
        if (compressor == null) {
            throw new IllegalArgumentException("Unable to find compressor by name " + compressorName);
        }
    }

    public void setMessageCompression(boolean enable) {
        outboundMessage.setMessageCompression(enable);
    }

    public boolean isReady() {
        return outboundMessage.isReady();
    }

    public void close(Status status, HttpHeaders trailers) {
        if (closeCalled) {
            throw new ServerRuntimeException("call already closed");
        }
        closeCalled = true;

        if (status.isOk() && method.getType().serverSendsOneMessage() && !messageSent) {
            outboundMessage.complete(Status.Code.INTERNAL.toStatus().withDescription(MISSING_RESPONSE), new
                    DefaultHttpHeaders());
            return;
        }
        outboundMessage.complete(status, trailers);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    ServerStreamListener newServerStreamListener(ServerCallHandler.Listener listener) {
        return new ServerStreamListener(this, listener);
    }

    public MethodDescriptor getMethodDescriptor() {
        return method;
    }

    /**
     * Server Stream Listener instance.
     */
    public static final class ServerStreamListener implements StreamListener {

        private final ServerCall call;
        private final ServerCallHandler.Listener listener;

        ServerStreamListener(ServerCall call, ServerCallHandler.Listener listener) {
            this.call = call;
            this.listener = listener;
        }

        @Override
        public void messagesAvailable(final InputStream message) {
            if (call.cancelled) {
                MessageUtils.closeQuietly(message);
                return;
            }
            try {
                Message request = call.method.parseRequest(message);
                request.setHeaders(call.inboundMessage.getHeaders());
                listener.onMessage(request);
            } catch (Exception ex) {
                MessageUtils.closeQuietly(message);
                throw new ServerRuntimeException(ex);
            } finally {
                try {
                    message.close();
                } catch (IOException ignore) {
                    // ignore the error.
                }
            }
        }

        public void halfClosed() {
            if (call.cancelled) {
                return;
            }
            listener.onHalfClose();
        }

        public void closed(Status status) {
            if (status.isOk()) {
                listener.onComplete();
            } else {
                call.cancelled = true;
                listener.onCancel();
            }
        }
    }
}
