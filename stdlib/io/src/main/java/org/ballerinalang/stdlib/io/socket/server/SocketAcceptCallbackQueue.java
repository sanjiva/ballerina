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

package org.ballerinalang.stdlib.io.socket.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * This is the Socket accept callback queue where, ballerina socket accept function register a callback to execute
 * once new socket accept.
 *
 * @since 0.975.1
 */
public class SocketAcceptCallbackQueue {

    private static SocketAcceptCallbackQueue instance = new SocketAcceptCallbackQueue();
    private final Object keyLock = new Object();

    private SocketAcceptCallbackQueue() {
    }

    public static SocketAcceptCallbackQueue getInstance() {
        return instance;
    }

    private Map<Integer, Queue<SocketAcceptCallback>> callbackRegistry = new HashMap<>();

    public void registerSocketAcceptCallback(int serverSocketHash, SocketAcceptCallback e) {
        synchronized (keyLock) {
            Queue<SocketAcceptCallback> socketChannels = callbackRegistry.get(serverSocketHash);
            if (socketChannels == null) {
                Queue<SocketAcceptCallback> queue = new LinkedList<>();
                callbackRegistry.put(serverSocketHash, queue);
                socketChannels = queue;
            }
            socketChannels.add(e);
        }
    }

    public Queue<SocketAcceptCallback> getCallbackQueue(int serverSocketHash) {
        synchronized (keyLock) {
            return callbackRegistry.get(serverSocketHash);
        }
    }
}
