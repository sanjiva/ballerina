import ballerina/http;
import ballerina/io;
import ballerina/auth;

endpoint http:APIListener listener {
    port:9090
};

@http:ServiceConfig {
      basePath:"/echo"
}

@auth:Config {
    authentication:{enabled:false},
    scopes:["xxx", "aaa"]
}   
service<http:Service> echo bind listener {
    @http:ResourceConfig {
        methods:["GET"],
        path:"/test"
    }
    @auth:Config {
        authentication:{enabled:true}
    }
    echo (endpoint client, http:Request req) {
        http:Response res = new;
        _ = client -> respond(res);
    }
}
