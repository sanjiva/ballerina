import ballerina/net.http;

@Description {value:"Attributes associated with the service endpoint is defined here."}
endpoint http:ServiceEndpoint echoEP {
    port:9090
};

@Description {value:"BasePath attribute associates a path to the service."}
@Description {value:"Bind the service endpoint to the service."}
@http:ServiceConfig { basePath:"/foo" }
service<http:Service> echo bind echoEp {
    @Description {value:"Post annotation restricts the resource only to accept post requests. Similarly, for each HTTP verb there are different annotations."}
    @Description {value:"Path attribute associates a sub-path to the resource."}
    @http:ResourceConfig {
        methods:["POST"],
        path:"/bar"
    }
    echo (endpoint conn, http:Request req) {
        // A util method that can get the request payload.
        var (payload, payloadError) = req.getJsonPayload();
        http:Response res = {};
        if (payloadError == null) {
            res.setJsonPayload(payload);
        } else {
            res = {statusCode:500};
            res.setStringPayload(payloadError.message);
        }

        // Reply to the client with the response.
        _ = conn -> respond(res);
    }
}
