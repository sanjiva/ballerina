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

package org.ballerinalang.test.service.websocket;

import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.ballerinalang.test.context.BallerinaTestException;
import org.ballerinalang.test.util.websocket.client.WebSocketTestClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test WebSocket endpoint method pushText and the onText resource.
 */
public class WebSocketPushAndOnTextResourceTest extends WebSocketTestCommons {

    private WebSocketTestClient client;

    @BeforeClass(description = "Initializes Ballerina")
    public void setup() throws BallerinaTestException {
        initBallerinaServer("push_and_onText.bal");
    }

    @Test(description = "Tests string support for pushText and onText")
    public void testString() throws URISyntaxException, InterruptedException {
        String url = "http://localhost:9090/onTextString";
        client = new WebSocketTestClient(url);
        client.handshake();
        String msg = "Hello";
        assertSuccess(msg, msg);
        client.shutDown();
    }

    @Test(description = "Tests JSON support for pushText and onText")
    public void testJson() throws URISyntaxException, InterruptedException {
        String url = "http://localhost:9091/onTextJSON";
        testJsonAndRecord(url);
    }

    @Test(description = "Tests XML support for pushText and onText")
    public void testXml() throws URISyntaxException, InterruptedException {
        String url = "http://localhost:9092/onTextXML";
        client = new WebSocketTestClient(url);
        client.handshake();
        String msg = "<note><to>Tove</to></note>";
        assertSuccess(msg, msg);
        assertContinuationSuccess(msg, "<note>", "<to>Tove", "</to>", "</note>");
        assertFailure("<note><to>Tove</to>",
                      "ParseError at [row,col]:[1,28]\nMessage: The element type \"note\"" +
                              " must be terminated by the matching end-tag \"</note>\".");
        client.shutDown();
    }

    @Test(description = "Tests Record support for pushText and onText")
    public void testRecord() throws URISyntaxException, InterruptedException {
        String url = "http://localhost:9093/onTextRecord";
        testJsonAndRecord(url);
    }

    @Test(description = "Tests byte array support for pushText and onText")
    public void testByteArray() throws URISyntaxException, InterruptedException {
        String url = "http://localhost:9094/onTextByteArray";
        client = new WebSocketTestClient(url);
        client.handshake();
        String msg = "Hello";
        assertSuccess(msg, msg);
        assertContinuationSuccess(msg, "Hel", "lo");
        client.shutDown();
    }

    private void testJsonAndRecord(String url) throws URISyntaxException, InterruptedException {
        client = new WebSocketTestClient(url);
        client.handshake();
        String expectedMsg = "{\"id\":1234, \"name\":\"Riyafa\"}";
        assertSuccess("{'id':1234, 'name':'Riyafa'}", expectedMsg);
        assertContinuationSuccess(expectedMsg, "{'id':1234, ", "'name':'Riyafa'}");
        assertFailure("{'id':1234, 'name':'Riyafa'", "expected , or } at line: 1 column: 29");
        client.shutDown();
    }

    private void assertSuccess(String msg, String expectedMsg)
            throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.setCountDownLatch(countDownLatch);
        client.sendText(msg);
        countDownLatch.await(TIMEOUT_IN_SECS, TimeUnit.SECONDS);
        Assert.assertEquals(client.getTextReceived(), expectedMsg, "Invalid message received");
    }

    private void assertFailure(String msg, String expected)
            throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.setCountDownLatch(countDownLatch);
        client.sendText(msg);
        countDownLatch.await(TIMEOUT_IN_SECS, TimeUnit.SECONDS);
        CloseWebSocketFrame closeFrame = client.getReceivedCloseFrame();
        Assert.assertEquals(closeFrame.statusCode(), 1003, "Invalid status code");
        Assert.assertEquals(closeFrame.reasonText(), expected, "Invalid close reason");
    }

    private void assertContinuationSuccess(String expectedMsg, String... msg)
            throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.setCountDownLatch(countDownLatch);
        for (int i = 0; i < msg.length - 1; i++) {
            client.sendText(msg[i], false);
        }
        client.sendText(msg[msg.length - 1], true);
        countDownLatch.await(TIMEOUT_IN_SECS, TimeUnit.SECONDS);
        Assert.assertEquals(client.getTextReceived(), expectedMsg, "Invalid message received");
    }

    @AfterClass(description = "Stops Ballerina")
    public void cleanup() throws BallerinaTestException {
        stopBallerinaServerInstance();
    }
}
