/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *
 */

package org.ballerinalang.test.util.websocket.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

/**
 * WebSocket Client Handler for Testing.
 */
public class WebSocketTestClientHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketTestClient.class);

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    private String textReceived = null;
    private ByteBuffer bufferReceived = null;
    private boolean isPong;
    private boolean isPing;
    private CountDownLatch countDownLatch = null;
    private ChannelHandlerContext ctx;
    private HttpHeaders headers;
    private CloseWebSocketFrame receivedCloseFrame;

    WebSocketTestClientHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
        this.ctx = ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("WebSocket Client disconnected!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            FullHttpResponse fullHttpResponse = (FullHttpResponse) msg;
            headers = fullHttpResponse.headers();
            handshaker.finishHandshake(ch, fullHttpResponse);
            logger.info("WebSocket Client connected!");
            handshakeFuture.setSuccess();
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof TextWebSocketFrame) {
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                textReceived = textFrame.text();
            } else if (frame instanceof BinaryWebSocketFrame) {
                BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;
                bufferReceived = binaryFrame.content().nioBuffer();
            } else if (frame instanceof PingWebSocketFrame) {
                PingWebSocketFrame pingFrame = (PingWebSocketFrame) frame;
                isPing = true;
                bufferReceived = pingFrame.content().nioBuffer();
            } else if (frame instanceof PongWebSocketFrame) {
                PongWebSocketFrame pongFrame = (PongWebSocketFrame) frame;
                isPong = true;
                bufferReceived = pongFrame.content().nioBuffer();
            } else if (frame instanceof CloseWebSocketFrame) {
                CloseWebSocketFrame closeWebSocketFrame = (CloseWebSocketFrame) frame;
                int statusCode = closeWebSocketFrame.statusCode();
                if (ch.isOpen()) {
                    ch.writeAndFlush(new CloseWebSocketFrame(statusCode, null)).addListener(future -> {
                        if (ch.isOpen()) {
                            ch.close();
                        }
                    }).sync();
                }
                receivedCloseFrame = closeWebSocketFrame.retain();
            }
            if (countDownLatch != null) {
                countDownLatch.countDown();
                countDownLatch = null;
            }
        }
    }


    /**
     * @return the text received from the server.
     */
    String getTextReceived() {
        String temp = textReceived;
        textReceived = null;
        return temp;
    }

    /**
     * @return the binary data received from the server.
     */
    ByteBuffer getBufferReceived() {
        ByteBuffer temp = bufferReceived;
        bufferReceived = null;
        return temp;
    }

    /**
     * Check whether the connection is opened or not.
     *
     * @return true if the connection is open.
     */
    boolean isOpen() {
        return ctx.channel().isOpen();
    }

    /**
     * Check whether a ping is received.
     *
     * @return true if a ping is received.
     */
    boolean isPing() {
        boolean temp = isPing;
        isPing = false;
        return temp;
    }

    /**
     * Check whether a ping is received.
     *
     * @return true if a ping is received.
     */
    boolean isPong() {
        boolean temp = isPong;
        isPong = false;
        return temp;
    }

    /**
     * Retrieve the received close frame to the client.
     * <b>Note: Release the close frame after using it using CloseWebSocketFrame.release()</b>
     *
     * @return the close frame received to the client.
     */
    CloseWebSocketFrame getReceivedCloseFrame() {
        CloseWebSocketFrame temp = receivedCloseFrame;
        receivedCloseFrame = null;
        return temp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!handshakeFuture.isDone()) {
            logger.error("Handshake failed : " + cause.getMessage(), cause);
            handshakeFuture.setFailure(cause);
        }
        logger.error("Error occurred: " + cause.getMessage(), cause);
        ctx.close();
    }

    /**
     * Gets the header value from the response headers.
     *
     * @param headerName the header name
     * @return the header value from the response headers.
     */
    public String getHeader(String headerName) {
        return headers.get(headerName);
    }
}
