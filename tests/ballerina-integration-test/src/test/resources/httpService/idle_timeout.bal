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
import ballerina/log;
import ballerina/runtime;

@http:ServiceConfig {
    basePath: "/idle"
}
service<http:Service> idleTimeout bind { port: 9090, timeoutMillis: 1000 } {
    @http:ResourceConfig {
        methods: ["POST"],
        path: "/timeout408"
    }
    timeoutTest408(endpoint outboundEP, http:Request req) {
        match req.getPayloadAsString() {
            string s => {
                log:printInfo(s);
            }
            error e => log:printError("Error reading request", err = e);
        }
        http:Response response = new;
        response.setTextPayload("some");
        outboundEP->respond(response) but {
            error e => log:printError("Error sending response", err = e)
        };
    }

    @http:ResourceConfig {
        methods: ["GET"],
        path: "/timeout500"
    }
    timeoutTest500(endpoint outboundEP, http:Request req) {
        runtime:sleep(3000);
        http:Response response = new;
        response.setTextPayload("some");
        outboundEP->respond(response) but {
            error e => log:printError("Error sending response", err = e)
        };
    }
}
