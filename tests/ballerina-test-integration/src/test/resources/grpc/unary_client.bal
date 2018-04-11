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

// Client endpoint configuration
endpoint HelloWorldBlockingClient helloWorldBlockingEp {
    host:"localhost",
    port:9090
};

function testUnaryBlockingClient (string name) returns (string) {

    //Working with custom headers
    grpc:Headers headers = new;
    headers.setEntry("x-id", "0987654321");
    // Executing unary blocking call
    (string,grpc:Headers)|error unionResp = helloWorldBlockingEp -> hello("WSO2", headers);
    match unionResp {
        (string,grpc:Headers) payload => {
            string result;
            grpc:Headers resHeaders;
            (result, resHeaders) = payload;
            io:println("Client Got Response : ");
            io:println(result);
            //io:println(resHeaders.get("x-id"));
            return "Client got response: " + result;
        }
        error err => {
            io:println("Error from Connector: " + err.message);
            return "Error from Connector: " + err.message;
        }
    }
}

function testBlockingHeader (string name) returns (string) {

    grpc:Headers headers = new;
    headers.setEntry("x-id", "0987654321");
    // Executing unary blocking call
    (string,grpc:Headers)|error unionResp = helloWorldBlockingEp -> hello("WSO2", headers);
    match unionResp {
        (string,grpc:Headers) payload => {
            string result;
            grpc:Headers resHeaders;
            (result, resHeaders) = payload;
            io:println("Client Got Response : ");
            io:println(result);
            //io:println(resHeaders.get("x-id"));
            string headerValue = resHeaders.get("x-id") but {() => "none"};
            return "Header: " + headerValue;
        }
        error err => {
            io:println("Error from Connector: " + err.message);
            return "Error: " + err.message;
        }
    }
}

// Blocking client
public type HelloWorldBlockingStub object {
    public {
        grpc:Client clientEndpoint;
        grpc:ServiceStub serviceStub;
    }

    function initStub (grpc:Client clientEndpoint) {
        grpc:ServiceStub navStub = new;
        navStub.initStub(clientEndpoint, "blocking", DESCRIPTOR_KEY, descriptorMap);
        self.serviceStub = navStub;
    }

    function hello (string req, grpc:Headers... headers) returns ((string, grpc:Headers)|error) {
        (any,grpc:Headers)|grpc:ConnectorError unionResp = self.serviceStub.blockingExecute("HelloWorld/hello", req, ...headers);
        match unionResp {
            grpc:ConnectorError payloadError => {
                error err = {message:payloadError.message};
                return err;
            }
            (any,grpc:Headers) payload => {
                any result;
                grpc:Headers resHeaders;
                (result, resHeaders)= payload;
                return (<string>result,resHeaders);
            }
        }
    }
};

// Non-blocking client
public type HelloWorldStub object {
    public {
        grpc:Client clientEndpoint;
        grpc:ServiceStub serviceStub;
    }

    function initStub (grpc:Client clientEndpoint) {
        grpc:ServiceStub navStub = new;
        navStub.initStub(clientEndpoint, "non-blocking", DESCRIPTOR_KEY, descriptorMap);
        self.serviceStub = navStub;
    }

    function hello (string req, typedesc listener, grpc:Headers... headers) returns (error| ()) {
        var err1 = self.serviceStub.nonBlockingExecute("HelloWorld/hello", req, listener, ...headers);
        if (err1 != ()) {
            error err = {message:err1.message};
            return err;
        }
        return ();
    }
};

// Blocking endpoint.
public type HelloWorldBlockingClient object {
    public {
        grpc:Client client;
        HelloWorldBlockingStub stub;
    }

    public function init (grpc:ClientEndpointConfig config) {
        // initialize client endpoint.
        grpc:Client client = new;
        client.init(config);
        self.client = client;
        // initialize service stub.
        HelloWorldBlockingStub stub = new;
        stub.initStub(client);
        self.stub = stub;
    }

    public function getClient () returns (HelloWorldBlockingStub) {
        return self.stub;
    }
};

//Non-blocking endpoint
public type HelloWorldClient object {
    public {
        grpc:Client client;
        HelloWorldStub stub;
    }

    public function init (grpc:ClientEndpointConfig config) {
        // initialize client endpoint.
        grpc:Client client = new;
        client.init(config);
        self.client = client;
        // initialize service stub.
        HelloWorldStub stub = new;
        stub.initStub(client);
        self.stub = stub;
    }

    public function getClient () returns (HelloWorldStub) {
        return self.stub;
    }
};

@final string DESCRIPTOR_KEY = "HelloWorld.proto";
map descriptorMap =
{
    "HelloWorld.proto":"0A1048656C6C6F576F726C642E70726F746F1A1E676F6F676C652F70726F746F6275662F77726170706572732E70726F746F32530A0A48656C6C6F576F726C6412450A0568656C6C6F121B676F6F676C652E70726F746F6275662E537472696E6756616C75651A1B676F6F676C652E70726F746F6275662E537472696E6756616C756528003000620670726F746F33",

    "google.protobuf.google/protobuf/wrappers.proto":"0A1E676F6F676C652F70726F746F6275662F77726170706572732E70726F746F120F676F6F676C652E70726F746F627566221C0A0B446F75626C6556616C7565120D0A0576616C7565180120012801221B0A0A466C6F617456616C7565120D0A0576616C7565180120012802221B0A0A496E74363456616C7565120D0A0576616C7565180120012803221C0A0B55496E74363456616C7565120D0A0576616C7565180120012804221B0A0A496E74333256616C7565120D0A0576616C7565180120012805221C0A0B55496E74333256616C7565120D0A0576616C756518012001280D221A0A09426F6F6C56616C7565120D0A0576616C7565180120012808221C0A0B537472696E6756616C7565120D0A0576616C7565180120012809221B0A0A427974657356616C7565120D0A0576616C756518012001280C427C0A13636F6D2E676F6F676C652E70726F746F627566420D577261707065727350726F746F50015A2A6769746875622E636F6D2F676F6C616E672F70726F746F6275662F7074797065732F7772617070657273F80101A20203475042AA021E476F6F676C652E50726F746F6275662E57656C6C4B6E6F776E5479706573620670726F746F33"

};