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
import ballerina/testing;
import ballerina/observe;

endpoint http:Listener ep1 {
    port : 9090
};

@http:ServiceConfig {
    basePath:"/echoService"
}
service echoService bind ep1 {
    resourceOne (endpoint outboundEP, http:Request clientRequest) {
        observe:Span span = observe:startSpan("testService", "echo span", (), observe:REFERENCE_TYPE_ROOT, ());
        span.log("TestEvent", "This is a test info log");
        span.logError("TestError", "This is a test error log");
        span.addTag("TestTag", "Test tag message");
        http:Response outResponse = new;
        http:Request request = new;
        http:Response | () response = callNextResource(span);
        outResponse.setStringPayload("Hello, World!");
        match response {
            http:Response res => _ = outboundEP -> respond(res);
            () => _ = outboundEP -> respond(new http:Response());
        }

        span.finishSpan();
    }

    resourceTwo (endpoint outboundEP, http:Request clientRequest) {
        observe:SpanContext spanContext = observe:extractTraceContextFromHttpHeader(clientRequest, "test-group");
        observe:Span span = observe:startSpan("testService", "resource two", (), observe:REFERENCE_TYPE_CHILDOF, spanContext);
        string | () baggageItem = span.getBaggageItem("BaggageItem");
        http:Response res = new;
        res.setStringPayload("Hello, World 2!");
        _ = outboundEP -> respond(res);
        span.finishSpan();
    }

    getFinishedSpansCount(endpoint outboundEP, http:Request clientRequest) {
        http:Response res = new;
        string returnString = testing:getFinishedSpansCount();
        res.setStringPayload(returnString);
        _ = outboundEP -> respond(res);
    }
}

function callNextResource(observe:Span parentSpan) returns (http:Response | ()) {
    endpoint http:Client httpEndpoint {
        targets : [{url: "http://localhost:9090/echoService"}]
    };
    observe:Span span = observe:startSpan("testService", "calling next resource", (), observe:REFERENCE_TYPE_CHILDOF, parentSpan);
    span.setBaggageItem("BaggageItem", "Baggage message");
    http:Request request = new;
    request = span.injectTraceContextToHttpHeader(request, "test-group");
    var resp = httpEndpoint -> get("/resourceTwo", request);
    span.finishSpan();
    match resp {
        http:HttpConnectorError err => return ();
        http:Response response => return response;
    }
}
