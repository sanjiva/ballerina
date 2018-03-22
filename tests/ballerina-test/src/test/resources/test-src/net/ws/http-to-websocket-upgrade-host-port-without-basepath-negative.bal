import ballerina/io;
import ballerina/net.http;

endpoint http:ServiceEndpoint servicEp {
    port:9090
};

@http:ServiceConfig {
    basePath:"/test",
    webSocketUpgrade:{
        upgradePath: "/ws",
        upgradeService: typeof wsService
    }
}
service<http:Service> httpService bind servicEp {

    @http:ResourceConfig {
        path:"/world",
        methods:["POST","GET","PUT","My"]
    }
    testResource(endpoint conn, http:Request req) {
        http:Response resp = {};
        var payload =? req.getStringPayload();
        io:println(payload);
        resp.setStringPayload("I received");
        _ = conn->respond(resp);
    }
}

@http:WebSocketServiceConfig {
    subProtocols:["xml, json"],
    idleTimeoutInSeconds:5
}
service<http: WebSocketService > wsService bind servicEp{

    onOpen(endpoint ep) {
        var conn = ep.getClient();
        io:println("New WebSocket connection: " + conn.id);
    }

    onTextMessage(endpoint conn, http:TextFrame frame) {
        io:println(frame.text);
        conn->pushText(frame.text);
    }
    onIdleTimeout(endpoint ep) {
        var conn = ep.getClient();
        io:println("Idle timeout: " + conn.id);
    }

}