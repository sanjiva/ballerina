/*
 * Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.nativeimpl.functions.io.socket;

import org.ballerinalang.bre.bvm.BLangVMStructs;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BBlob;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.codegen.PackageInfo;
import org.ballerinalang.util.codegen.StructInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Unit tests for client socket.
 */
public class ClientSocketTest {

    private static final Logger log = LoggerFactory.getLogger(ClientSocketTest.class);

    private CompileResult socketClient;
    private Process server;

    @BeforeClass
    public void setup() {
        socketClient = BCompileUtil.compile("test-src/io/clientsocketio.bal");
        boolean connectionStatus;
        int numberOfRetryAttempts = 10;
        try {
            server = MockSocketServer.start();
            connectionStatus = isConnected(MockSocketServer.SERVER_HOST, numberOfRetryAttempts);
            if (!connectionStatus) {
                Assert.fail("Unable to open connection with the test TCP server");
            }
        } catch (IOException | InterruptedException e) {
            log.error("Unable to open Socket Server: " + e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Closes a provided socket connection.
     *
     * @param socket socket which should be closed.
     */
    private void close(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            log.error("Error occurred while closing the Socket connection", e);
        }
    }

    /**
     * Will enforce to sleep the thread for the provided time.
     *
     * @param retryInterval the time in milliseconds the thread should sleep
     */
    private void sleep(int retryInterval) {
        try {
            Thread.sleep(retryInterval);
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Attempts to establish a connection with the test server.
     *
     * @param hostName        hostname of the server.
     * @param numberOfRetries number of retry attempts.
     * @return true if the connection is established successfully.
     */
    private boolean isConnected(String hostName, int numberOfRetries) {
        Socket temporarySocketConnection = null;
        boolean isConnected = false;
        final int retryInterval = 1000;
        final int initialRetryCount = 0;
        for (int retryCount = initialRetryCount; retryCount < numberOfRetries && !isConnected; retryCount++) {
            try {
                //Attempts to establish a connection with the server
                temporarySocketConnection = new Socket(hostName, MockSocketServer.SERVER_PORT);
                isConnected = true;
            } catch (IOException e) {
                log.error("Error occurred while establishing a connection with test server", e);
                sleep(retryInterval);
            } finally {
                if (null != temporarySocketConnection) {
                    //We close the connection once completed.
                    close(temporarySocketConnection);
                }
            }
        }
        return isConnected;
    }

    @AfterClass
    public void cleanup() {
        server.destroy();
    }

    @Test(description = "Open client socket connection to the remote server")
    public void testOpenClientSocket() {
        BValue[] args = { new BString("localhost"), new BInteger(MockSocketServer.SERVER_PORT) };
        BRunUtil.invoke(socketClient, "openSocketConnection", args);
    }

    @Test(dependsOnMethods = "testOpenClientSocket", description = "Test content read/write")
    public void testWriteReadContent() {
        String content = "Hello World\n";
        byte[] contentBytes = content.getBytes();
        BValue[] args = { new BBlob(contentBytes)};
        final BValue[] writeReturns = BRunUtil.invoke(socketClient, "write", args);
        BInteger returnedSize = (BInteger) writeReturns[0];
        Assert.assertEquals(returnedSize.intValue(), content.length(), "Write content size is not match.");
        args = new BValue[] { new BInteger(content.length()) };
        final BValue[] readReturns = BRunUtil.invoke(socketClient, "read", args);
        final BBlob readContent = (BBlob) readReturns[0];
        returnedSize = (BInteger) readReturns[1];

        Assert.assertEquals(readContent.stringValue(), content, "Return content are not match with written content.");
        Assert.assertEquals(returnedSize.intValue(), content.length(), "Read size not match with the request size");
    }

    @Test(dependsOnMethods = "testWriteReadContent",
          description = "Test the connection closure")
    public void testClosure() {
        BRunUtil.invoke(socketClient, "closeSocket");
    }

    @Test(dependsOnMethods = "testClosure",
          description = "Test connection open with properties")
    public void testOpenWithProperties() {
        int port = ThreadLocalRandom.current().nextInt(33000, 46000);
        PackageInfo ioPackageInfo = socketClient.getProgFile().getPackageInfo("ballerina.io");
        StructInfo socketProperties = ioPackageInfo.getStructInfo("SocketProperties");
        BStruct propertyStruct = BLangVMStructs.createBStruct(socketProperties, port);
        BValue[] args = { new BString("localhost"), new BInteger(MockSocketServer.SERVER_PORT), propertyStruct };
        final BValue[] returns = BRunUtil.invoke(socketClient, "openSocketConnectionWithProps", args);
        final BStruct socket = (BStruct) returns[0];
        Assert.assertEquals(socket.getIntField(1), port, "Client port didn't bind to assign port.");
        args = new BValue[] { socket };
        BRunUtil.invoke(socketClient, "close", args);
    }
}
