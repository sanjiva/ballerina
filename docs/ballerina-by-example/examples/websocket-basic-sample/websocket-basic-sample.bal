import ballerina/io;
import ballerina/net.http;

@Description {value:"This example gives you the basic idea of WebSocket endpoint."}
endpoint http:ServiceEndpoint ep {
    host: "0.0.0.0",
    port: 9090
};

@http:WebSocketServiceConfig {
    basePath: "/basic/ws",
    subProtocols: ["xml", "json"],
    idleTimeoutInSeconds: 120
}
service<http: WebSocketService > SimpleSecureServer bind ep{

    string ping = "ping";
    blob pingData = ping.toBlob("UTF-8");

    @Description {value:"This resource is responsible for handling user logic on handshake time. Note that the connection is not yet established while this code is running."}
    onUpgrade(endpoint ep,  http:Request req) {
        var conn = ep.getClient();
        io:println("\nNew client is going to connect");
        io:println("Connection ID: " + conn.id);
        io:println("Is connection secure: " + conn.isSecure);

        io:println("pre upgrade headers -> ");
        printHeaders(conn.upgradeHeaders);
    }

    @Description {value:"This resource is triggered after a successful client connection."}
     onOpen(endpoint ep) {
        var conn = ep.getClient();
        io:println("\nNew client connected");
        io:println("Connection ID: " + conn.id);
        io:println("Negotiated Sub protocol: " + conn.negotiatedSubProtocol);
        io:println("Is connection open: " + conn.isOpen);
        io:println("Is connection secured: " + conn.isSecure);
        //io:println("Connection header value: " + conn.getUpgradeHeader("Connection"));
        io:println("Upgrade headers -> " );
        printHeaders(conn.upgradeHeaders);
    }

    @Description {value:"This resource is triggered when a new text frame is received from a client."}
     onTextMessage (endpoint conn, http:TextFrame frame) {
        io:println("\ntext message: " + frame.text + " & is final fragment: " + frame.isFinalFragment);
        string text = frame.text;

        if (text == "ping") {
            io:println("Pinging...");
            conn->ping(pingData);
        } else if (text == "closeMe") {
            conn->closeConnection(1001, "You asked me to close connection");
        } else {
            conn->pushText("You said: " + frame.text);
        }
    }

    @Description {value:"This resource is triggered when a new binary frame is received from a client."}
     onBinaryMessage(endpoint conn, http:BinaryFrame frame) {
        io:println("\nNew binary message received");
        blob b = frame.data;
        io:println("UTF-8 decoded binary message: " + b.toString("UTF-8"));
        conn->pushBinary(b);
    }

    @Description {value:"This resource is triggered when a ping message is received from the client. If this resource is not implemented then pong message will be sent automatically to the connected endpoint when a ping is received."}
     onPing(endpoint conn, http:PingFrame frame) {
        conn->pong(frame.data);
    }

    @Description {value:"This resource is triggered when a pong message is received"}
     onPong(endpoint conn, http:PongFrame frame) {
        io:println("Pong received");
    }

    @Description {value:"This resource is triggered when a particular client reaches it's idle timeout defined in the ws:configuration annotation."}
     onIdleTimeout(endpoint ep) {
        var conn = ep.getClient();
        // This resource will be triggered after 180 seconds if there is no activity in a given channel.
        io:println("\nReached idle timeout");
        io:println("Closing connection " + conn.id);
        ep->closeConnection(1001, "Connection timeout");
    }

    @Description {value:"This resource is triggered when a client connection is closed from the client side."}
     onClose(endpoint conn, http:CloseFrame closeFrame) {
        io:println("\nClient left with status code " + closeFrame.statusCode + " because " + closeFrame.reason);
    }
}

function printHeaders(map<string> headers) {
    string [] headerKeys = headers.keys();
    int len = lengthof headerKeys;
    int i = 0;
    while (i < len) {
        string key = headerKeys[i];
        var value = headers[key];
        io:println(key + ": " + value);
        i = i + 1;
    }
}