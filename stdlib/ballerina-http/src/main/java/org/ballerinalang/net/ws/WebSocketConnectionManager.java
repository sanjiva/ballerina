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
 */

package org.ballerinalang.net.ws;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This contains the connection information of successfully handshaked connections.
 */
public class WebSocketConnectionManager {

    private static final WebSocketConnectionManager CONNECTION_MANAGER = new WebSocketConnectionManager();

    // Map <sessionId, WebSocketConnectionStruct>
    private final Map<String, WsOpenConnectionInfo> wsConnectionsMap = new ConcurrentHashMap<>();

    private WebSocketConnectionManager() {
    }

    public static WebSocketConnectionManager getInstance() {
        return CONNECTION_MANAGER;
    }

    public WsOpenConnectionInfo getConnectionInfo(String connectionID) {
        return wsConnectionsMap.get(connectionID);
    }

    public void addConnection(String connectionID, WsOpenConnectionInfo wsConnection) {
        wsConnectionsMap.put(connectionID, wsConnection);
    }

    public WsOpenConnectionInfo removeConnection(String connectionID) {
        return wsConnectionsMap.remove(connectionID);
    }
}
