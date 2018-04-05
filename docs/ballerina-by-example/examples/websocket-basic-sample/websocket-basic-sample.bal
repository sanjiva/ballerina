import ballerina/io;
import ballerina/http;

@Description {value:"This example gives you the basic idea of WebSocket endpoint."}
endpoint http:ServiceEndpoint ep {
    host:"0.0.0.0",
    port:9090
};

@http:WebSocketServiceConfig {
    basePath:"/basic/ws",
    subProtocols:["xml", "json"],
    idleTimeoutInSeconds:120
}
service<http:WebSocketService> SimpleSecureServer bind ep {

    string ping = "ping";
    blob pingData = ping.toBlob("UTF-8");

    @Description {value:"This resource is triggered after a successful client connection."}
    onOpen (endpoint conn) {
        io:println("\nNew client connected");
        io:println("Connection ID: " + conn.id);
        io:println("Negotiated Sub protocol: " + conn.negotiatedSubProtocol);
        io:println("Is connection open: " + conn.isOpen);
        io:println("Is connection secured: " + conn.isSecure);
        io:println("Upgrade headers -> ");
        printHeaders(conn.upgradeHeaders);
    }

    @Description {value:"This resource is triggered when a new text frame is received from a client."}
    onText (endpoint conn, string text, boolean more) {
        io:println("\ntext message: " + text + " & more fragments: " + more);

        if (text == "ping") {
            io:println("Pinging...");
            conn -> ping(pingData);
        } else if (text == "closeMe") {
            var val = conn -> close(1001, "You asked me to close the connection");
            handleError(val);
        } else {
            var val = conn -> pushText("You said: " + text);
            handleError(val);
        }
    }

    @Description {value:"This resource is triggered when a new binary frame is received from a client."}
    onBinary (endpoint conn, blob b) {
        io:println("\nNew binary message received");
        io:println("UTF-8 decoded binary message: " + b.toString("UTF-8"));
        var val = conn -> pushBinary(b);
        handleError(val);
    }

    @Description {value:"This resource is triggered when a ping message is received from the client. If this resource is not implemented then pong message will be sent automatically to the connected endpoint when a ping is received."}
    onPing (endpoint conn, blob data) {
        conn -> pong(data);
    }

    @Description {value:"This resource is triggered when a pong message is received"}
    onPong (endpoint conn, blob data) {
        io:println("Pong received");
    }

    @Description {value:"This resource is triggered when a particular client reaches it's idle timeout defined in the ws:configuration annotation."}
    onIdleTimeout (endpoint conn) {
        // This resource will be triggered after 180 seconds if there is no activity in a given channel.
        io:println("\nReached idle timeout");
        io:println("Closing connection " + conn.id);
        var val = conn -> close(1001, "Connection timeout");
        handleError(val);
    }

    @Description {value:"This resource is triggered when a client connection is closed from the client side."}
    onClose (endpoint conn, int statusCode, string reason) {
        io:println("\nClient left with status code " + statusCode + " because " + reason);
    }
}

function printHeaders (map<string> headers) {
    string[] headerKeys = headers.keys();
    int len = lengthof headerKeys;
    int i = 0;
    while (i < len) {
        string key = headerKeys[i];
        var value = headers[key];
        io:println(key + ": " + value);
        i = i + 1;
    }
}

function handleError (http:WebSocketConnectorError|null val) {
    match val {
        http:WebSocketConnectorError err => {io:println("Error: " + err.message);}
        any|null err => {//ignore x
            var x = err;
        }
    }
}
