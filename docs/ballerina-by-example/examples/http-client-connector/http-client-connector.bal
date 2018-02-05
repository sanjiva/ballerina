import ballerina.net.http;

function main (string[] args) {
    endpoint<http:HttpClient> httpEndpoint {
        create http:HttpClient("https://postman-echo.com", {});
    }

    http:OutRequest req = {};
    // Send a GET request to the specified endpoint
    http:InResponse resp = {};
    resp, _ = httpEndpoint.get("/get?test=123", req);
    println("GET request:");
    println(resp.getJsonPayload());

    // Set a string payload to the message to be sent to the endpoint.
    req.setStringPayload("POST: Hello World");
    resp, _ = httpEndpoint.post("/post", req);
    println("\nPOST request:");
    println(resp.getJsonPayload());

    // Set a JSON payload to the message to be sent to the endpoint.
    json jsonMsg = {method:"PUT", payload:"Hello World"};
    req.setJsonPayload(jsonMsg);
    resp, _ = httpEndpoint.put("/put", req);
    println("\nPUT request:");
    println(resp.getJsonPayload());

    // Set an XML payload to the message to be sent to the endpoint.
    xml xmlMsg = xml `<request><method>PATCH</method><payload>Hello World!</payload></request>`;
    req.setXmlPayload(xmlMsg);
    json j = {};
    // Remove the json payload.
    req.setJsonPayload(j);
    resp, _ = httpEndpoint.patch("/patch", req);
    println("\nPATCH request:");
    println(resp.getJsonPayload());

    req.setStringPayload("DELETE: Hello World");
    resp, _ = httpEndpoint.delete("/delete", req);
    println("\nDELETE request:");
    println(resp.getJsonPayload());

    req.setStringPayload("CUSTOM: Hello World");
    // The execute() action can be used if one needs to use custom HTTP verbs.
    resp, _ = httpEndpoint.execute("COPY", "/get", req);

    http:HttpClient httpConn = create http:HttpClient("https://postman-echo.com", {});

    //bind statement can be used to bind new connections to existing endpoints.
    bind httpConn with httpEndpoint;

    // The messages and http packages provide various utility functions which are useful when dealing with HTTP requests/responses.
    req = {};
    req.addHeader("Sample-Name", "http-client-connector");
    resp, _ = httpEndpoint.get("/get", req);

    string contentType;
    contentType = resp.getHeader("Content-Type").value;
    println("\nContent-Type: " + contentType);

    int statusCode = resp.getStatusCode();
    println("Status code: " + statusCode);
}
