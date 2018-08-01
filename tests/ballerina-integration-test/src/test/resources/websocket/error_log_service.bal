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

import ballerina/io;
import ballerina/http;


@http:WebSocketServiceConfig {
    path: "/error/ws"
}
service<http:WebSocketService> errorService bind {port: 9090} {
    onOpen(endpoint ep) {
        io:println("connection open");
    }

    onText(endpoint ep, string text) {
        io:println(string `text received: {{text}}`);
        ep->pushText(text) but {
            error => io:println("error sending message")
        };
    }

    onError(endpoint ep, error err) {
        io:println(string `error occurred: {{err.message}}`);
    }

    onClose(endpoint ep, int statusCode, string reason) {
        io:println(string `Connection closed with {{statusCode}}, {{reason}}`);
    }
}
