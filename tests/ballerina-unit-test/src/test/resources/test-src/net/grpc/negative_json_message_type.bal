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
import ballerina/grpc;

endpoint grpc:Listener ep {
    host:"localhost",
    port:9090
};

// This is negative testcase, json field type is not supported currently. So we should be compilation error.
// Unsupported field type, field type json currently not supported.
service UnsupportedJsonType bind ep {

    testInputJsonType(endpoint caller, JsonMessage msg) {
        io:println(msg);
        string message = "Testing Json types";
        error? err = caller->send(message);
        io:println(err.message but { () => ("Server send response : " + message) });
        _ = caller->complete();
    }

    testOutputJsonType(endpoint caller, string msg) {
        io:println(msg);
        JsonMessage jsonMsg = {};
        error? err = caller->send(jsonMsg);
        io:println(err.message but { () => ("Server send response successfully") });
        _ = caller->complete();
    }
}

type JsonMessage record {
    json payload;
};
