import ballerina.net.http;
import ballerina.doc;

@doc:Description {value:"BasePath annotation associates a path to service."}
@http:config {basePath:"/foo"}
service<http> echo {
    @doc:Description {value:"Post annotation constrains the resource only to accept post requests. Similarly, for each HTTP verb there are different annotations."}
    @http:POST {}
    @doc:Description {value:"Path annotation associates a sub-path to resource."}
    @http:Path {value:"/bar"}
    resource echo (message m) {
        // A util method that can convert the request to a response.
        http:convertToResponse(m);
        // Send back the response to the client.
        reply m;
    }
}
