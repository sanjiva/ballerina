// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/http;
import ballerina/io;

@final string REMOTE_BACKEND_URL = "ws://localhost:15500/websocket";
@final string ASSOCIATED_CONNECTION = "ASSOCIATED_CONNECTION";
@final string data = "data";
@final blob APPLICATION_DATA = data.toBlob("UTF-8");

endpoint http:WebSocketListener ep {
    port:9090
};

@http:WebSocketServiceConfig {
    path: "/pingpong/ws"
}
service <http:WebSocketService> PingPongTestService bind ep {

    onOpen(endpoint wsEp) {
        endpoint http:WebSocketClient wsClientEp {
            url: REMOTE_BACKEND_URL,
            callbackService: clientCallbackService
        };
        wsEp.attributes[ASSOCIATED_CONNECTION] = wsClientEp;
        wsClientEp.attributes[ASSOCIATED_CONNECTION] = wsEp;
    }

    onText (endpoint wsEp, string text) {
        endpoint http:WebSocketClient clientEp;

        if(text == "ping-me") {
            wsEp -> ping(APPLICATION_DATA) but {error e => io:println("error sending server ping")};
        }

        if(text == "ping-remote-server") {
            clientEp = getAssociatedClientEndpoint(wsEp);
            clientEp -> ping(APPLICATION_DATA) but {error e => io:println("error sending client ping")};
        }

        if(text == "tell-remote-server-to-ping") {
            clientEp = getAssociatedClientEndpoint(wsEp);
            clientEp -> pushText("ping") but {error e => io:println("error sending client ping")};
        }
    }

    onPing (endpoint wsEp, blob data) {
        wsEp -> pong(data) but {error e => io:println("Error sending server pong")};
    }

    onPong (endpoint wsEp, blob data) {
        wsEp -> pushText("pong-from-you") but {error e => io:println("server text error")};
    }

}

service <http:WebSocketClientService> clientCallbackService {

    onPing (endpoint wsEp, blob data) {
        endpoint http:WebSocketListener serverEp = getAssociatedListener(wsEp);
        serverEp -> pushText("ping-from-remote-server-received") but {error e => io:println("error sending client text")};
    }

    onPong (endpoint wsEp, blob data) {
        endpoint http:WebSocketListener serverEp = getAssociatedListener(wsEp);
        serverEp -> pushText("pong-from-remote-server-received") but {error e => io:println("error sending client text")};
    }
}

public function getAssociatedClientEndpoint(http:WebSocketListener wsServiceEp) returns (http:WebSocketClient) {
    return check <http:WebSocketClient> wsServiceEp.attributes[ASSOCIATED_CONNECTION];
}

public function getAssociatedListener(http:WebSocketClient wsClientEp) returns (http:WebSocketListener) {
    return check <http:WebSocketListener> wsClientEp.attributes[ASSOCIATED_CONNECTION];
}
