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

import ballerina.net.http;
import ballerina.net.http.resiliency;

http:Retry retryConfiguration = {count:0};

http:HttpClient endPoint1 = (http:HttpClient) create MockHttpFailOverClient("http://invalidEP", {});
http:HttpClient endPoint2 = (http:HttpClient) create MockHttpFailOverClient("http://localhost:8080",
                                                   {endpointTimeout:5000,
                                                       keepAlive:true,
                                                       retryConfig:retryConfiguration});

http:HttpClient[] failoverGroup = [endPoint1, endPoint2];
int[] errorCodes = [404, 502];

resiliency:FailoverConfig errorCode = {failoverCodes:errorCodes};

function testSuccessScenario () (http:InResponse, http:HttpConnectorError) {
    endpoint<http:HttpClient> ep {
        create resiliency:Failover(failoverGroup, errorCode);
    }

    http:InResponse clientResponse = {};
    http:HttpConnectorError err;
    http:OutRequest outReq = {};

    int counter = 0;

    while (counter < 2) {
        clientResponse, err = ep.post("/", outReq);
        counter = counter + 1;
    }

    return clientResponse, err;
}

function testFailureScenario () (http:InResponse, http:HttpConnectorError) {
    endpoint<http:HttpClient> ep {
        create resiliency:Failover(failoverGroup, errorCode);
    }

    http:InResponse clientResponse = {};
    http:HttpConnectorError err;
    http:OutRequest outReq = {};

    int counter = 0;

    while (counter < 1) {
        clientResponse, err = ep.post("/", outReq);
        counter = counter + 1;
    }

    return clientResponse, err;
}

connector MockHttpFailOverClient (string serviceUri, http:Options connectorOptions) {

    endpoint<http:HttpClient> endPoint {
    }

    int actualRequestNumber = 0;

    action post (string path, http:OutRequest request) (http:InResponse, http:HttpConnectorError) {
        http:InResponse response;
        http:HttpConnectorError err;
        response, err = generateResponse(actualRequestNumber);
        actualRequestNumber = actualRequestNumber + 1;
        return response, err;
    }

    action head (string path, http:OutRequest req) (http:InResponse, http:HttpConnectorError) {
        return null, null;
    }

    action put (string path, http:OutRequest req) (http:InResponse, http:HttpConnectorError) {
        return null, null;
    }

    action execute (string httpVerb, string path, http:OutRequest req) (http:InResponse, http:HttpConnectorError) {
        return null, null;
    }

    action patch (string path, http:OutRequest req) (http:InResponse, http:HttpConnectorError) {
        return null, null;
    }

    action delete (string path, http:OutRequest req) (http:InResponse, http:HttpConnectorError) {
        return null, null;
    }

    action get (string path, http:OutRequest req) (http:InResponse, http:HttpConnectorError) {
        return null, null;
    }

    action options (string path, http:OutRequest req) (http:InResponse, http:HttpConnectorError) {
        return null, null;
    }

    action forward (string path, http:InRequest req) (http:InResponse, http:HttpConnectorError) {
        return null, null;
    }
}

function generateErrorResponse () (http:InResponse, http:HttpConnectorError) {
    http:HttpConnectorError err = {};
    err.message = "Connection refused";
    err.statusCode = 502;
    return null, err;
}

function generateResponse (int count) (http:InResponse, http:HttpConnectorError) {

    http:InResponse inResponse = {};
    http:HttpConnectorError err = {};
    if (count == 0) {
        err.message = "Connection refused";
        err.statusCode = 502;
        return null, err;
    } else {
        inResponse.statusCode = 200;
        return inResponse, null;
    }
}
