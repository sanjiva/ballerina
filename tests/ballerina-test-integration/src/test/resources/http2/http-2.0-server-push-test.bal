import ballerina/io;
import ballerina/http;

endpoint http:Listener frontendEP {
    port:9090
};

endpoint http:Client backendClientEP {
    targets: [
        {
            url: "http://localhost:7090"
        }
    ],
    // HTTP version is set to 2.0.
    httpVersion:"2.0"
};

@http:ServiceConfig {
    basePath:"/frontend"
}
service<http:Service> frontendHttpService bind frontendEP {

    @http:ResourceConfig {
        methods:["GET"],
        path:"/"
    }
    frontendHttpResource (endpoint client, http:Request clientRequest) {

        http:Request serviceReq = new;
        http:HttpFuture httpFuture = new;
        // Submit a request
        var submissionResult = backendClientEP -> submit("GET", "/backend/main", serviceReq);
        match submissionResult {
            http:HttpConnectorError err => {
                io:println("Error occurred while submitting a request");
                http:Response errorResponse = new;
                json errMsg = {"error":"error occurred while submitting a request"};
                errorResponse.setJsonPayload(errMsg);
                _ = client -> respond(errorResponse);
            }
            http:HttpFuture resultantFuture => {
                httpFuture = resultantFuture;
            }
        }

        // Check whether promises exists
        http:PushPromise[] promises = [];
        int promiseCount = 0;
        boolean hasPromise = backendClientEP -> hasPromise(httpFuture);
        while (hasPromise) {
            http:PushPromise pushPromise = new;
            // Get the next promise
            var nextPromiseResult = backendClientEP -> getNextPromise(httpFuture);
            match nextPromiseResult {
                http:PushPromise resultantPushPromise => {
                    pushPromise = resultantPushPromise;
                }
                http:HttpConnectorError err => {
                    io:println("Error occurred while fetching a push promise");
                    http:Response errorResponse = new;
                    json errMsg = {"error":"error occurred while fetching a push promise"};
                    errorResponse.setJsonPayload(errMsg);
                    _ = client -> respond(errorResponse);
                }
            }

            io:println("Received a promise for " + pushPromise.path);
            // Store required promises
            promises[promiseCount] = pushPromise;
            promiseCount = promiseCount + 1;
            hasPromise = backendClientEP -> hasPromise(httpFuture);
        }
        // By this time 3 promises should be received, if not send an error response
        if (promiseCount != 3) {
            http:Response errorResponse = new;
            json errMsg = {"error":"expected number of promises not received"};
            errorResponse.setJsonPayload(errMsg);
            _ = client -> respond(errorResponse);
        }
        io:println("Number of promises received : " + promiseCount);

        // Get the requested resource
        http:Response res = new;
        var result = backendClientEP -> getResponse(httpFuture);
        match result {
            http:Response resultantResponse => {
                res = resultantResponse;
            }
            http:HttpConnectorError err => {
                io:println("Error occurred while fetching response");
                http:Response errorResponse = new;
                json errMsg = {"error":"error occurred while fetching response"};
                errorResponse.setJsonPayload(errMsg);
                _ = client -> respond(errorResponse);
            }
        }

        var responsePayload = res.getJsonPayload();
        json responseJsonPayload = {};
        match responsePayload {
            json resultantJsonPayload => {
                responseJsonPayload = resultantJsonPayload;
            }
            http:PayloadError err => {
                http:Response errorResponse = new;
                json errMsg = {"error":"expected response message not received"};
                errorResponse.setJsonPayload(errMsg);
                _ = client -> respond(errorResponse);
            }
        }
        // Check whether correct response received
        string responseStringPayload = responseJsonPayload.toString() but {() => ""};
        if (!(responseStringPayload.contains("main"))) {
            http:Response errorResponse = new;
            json errMsg = {"error":"expected response message not received"};
            errorResponse.setJsonPayload(errMsg);
            _ = client -> respond(errorResponse);
        }
        io:println("Response : " + responseStringPayload);

        // Fetch required promised responses
        foreach promise in promises {
            http:Response promisedResponse = new;
            var promisedResponseResult = backendClientEP -> getPromisedResponse(promise);
            match promisedResponseResult {
                http:Response resultantPromisedResponse => {
                    promisedResponse = resultantPromisedResponse;
                }
                http:HttpConnectorError err => {
                    io:println("Error occurred while fetching promised response");
                    http:Response errorResponse = new;
                    json errMsg = {"error":"error occurred while fetching promised response"};
                    errorResponse.setJsonPayload(errMsg);
                    _ = client -> respond(errorResponse);
                }
            }

            json promisedJsonPayload = {};
            var promisedPayload = promisedResponse.getJsonPayload();
            match promisedPayload {
                json resultantJsonPayload => {
                    promisedJsonPayload = resultantJsonPayload;
                }
                http:PayloadError err => {
                    http:Response errorResponse = new;
                    json errMsg = {"error":"expected promised response not received"};
                    errorResponse.setJsonPayload(errMsg);
                    _ = client -> respond(errorResponse);
                }
            }

            // check whether expected
            string expectedVal = promise.path.subString(1, 10);
            string promisedStringPayload = promisedJsonPayload.toString() but {() => ""};
            if (!(promisedStringPayload.contains(expectedVal))) {
                http:Response errorResponse = new;
                json errMsg = {"error":"expected promised response not received"};
                errorResponse.setJsonPayload(errMsg);
                _ = client -> respond(errorResponse);
            }
            io:println("Promised resource : " + promisedStringPayload);
        }

        // By this time everything has went well, hence send a success response
        http:Response successResponse = new;
        json successMsg = {"status":"successful"};
        successResponse.setJsonPayload(successMsg);
        _ = client -> respond(successResponse);
    }
}

endpoint http:Listener backendEP {
    port:7090,
    // HTTP version is set to 2.0
    httpVersion:"2.0"
};

@http:ServiceConfig {
    basePath:"/backend"
}
service<http:Service> backendHttp2Service bind backendEP {

  @http:ResourceConfig {
     path:"/main"
  }
  backendHttp2Resource (endpoint client, http:Request req) {

    io:println("Request received");

    // Send a Push Promise
    http:PushPromise promise1 = new;
    promise1.path = "/resource1";
    promise1.method = "POST";
    _ = client -> promise(promise1);

    // Send another Push Promise
    http:PushPromise promise2 = new;
    promise2.path = "/resource2";
    promise2.method = "POST";
    _ = client -> promise(promise2);

    // Send one more Push Promise
    http:PushPromise promise3 = new;
    promise3.path = "/resource3";
    promise3.method = "POST";
    _ = client -> promise(promise3);

    // Construct requested resource
    http:Response response = new;
    json msg = {"response":{"name":"main resource"}};
    response.setJsonPayload(msg);

    // Send the requested resource
    _ = client -> respond(response);

    // Construct promised resource1
    http:Response push1 = new;
    msg = {"push":{"name":"resource1"}};
    push1.setJsonPayload(msg);

    // Push promised resource1
    _ = client -> pushPromisedResponse(promise1, push1);

    http:Response push2 = new;
    msg = {"push":{"name":"resource2"}};
    push2.setJsonPayload(msg);

    // Push promised resource2
    _ = client -> pushPromisedResponse(promise2, push2);

    http:Response push3 = new;
    msg = {"push":{"name":"resource3"}};
    push3.setJsonPayload(msg);

    // Push promised resource3
    _ = client -> pushPromisedResponse(promise3, push3);
  }
}
