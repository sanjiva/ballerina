import ballerina.net.http;
import ballerina.doc;

@doc:Description {value:"BasePath attribute associates a path to the service."}
@http:configuration {basePath:"/foo"}
service<http> echo {
    @doc:Description {value:"Post annotation constrains the resource only to accept post requests. Similarly, for each HTTP verb there are different annotations."}
    @doc:Description {value:"Path attribute associates a sub-path to resource."}
    @http:resourceConfig {
        methods:["POST"],
        path:"/bar"
    }
    resource echo (http:Request req, http:Response res) {
        // A util method that can get the request payload.
        json payload = req.getJsonPayload();
        res.setJsonPayload(payload);
        // Send back the response to the client.
        res.send();
    }
}
