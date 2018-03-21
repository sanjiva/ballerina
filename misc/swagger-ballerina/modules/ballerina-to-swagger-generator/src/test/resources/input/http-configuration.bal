import ballerina/net.http;
import ballerina/net.http.swagger;

@http:configuration {
    basePath:"/api",
    host: "localhost",
    port: 4545
}
@swagger:ServiceInfo {
    version: "1.0.0",
    description: "A sample API that uses a petstore as an example to demonstrate features in the swagger-2.0 specification",
    termsOfService: "http://swagger.io/terms/"
}
@swagger:ServiceConfig {
    schemes:[
        "http", "https"
    ]
}
service<http> Service5 {

    @http:resourceConfig {
        methods:["POST"],
        path:"/pets/{id}",
        produces:["application/json"]
    }
    resource Resource1 (message m, @http:PathParam{value: "id"} string id) {
        reply m;
    }
}