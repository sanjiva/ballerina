import ballerina.lang.messages;
import ballerina.net.http;

@http:BasePath {value:"/hello"}
service helloWorld {
    
    @http:GET{}
    @http:Path {value:"/"}
    resource sayHello (message m) {
        message response = {};
        messages:setStringPayload(response, "Hello, World!");
        reply response;
    
    }
    
}
