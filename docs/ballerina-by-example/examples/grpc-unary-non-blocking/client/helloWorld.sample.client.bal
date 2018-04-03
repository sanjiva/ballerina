// This is client implementation for unary non blocking scenario
package client;

import ballerina/io;

int total = 0;
function main (string[] args) {
    // Client endpoint configuration
    endpoint helloWorldClient helloWorldEp {
        host:"localhost",
        port:9090
    };
    // Executing unary non-blocking call registering server message listener.
    error| null result = helloWorldEp -> hello("WSO2", typeof helloWorldMessageListener);
    match result {
        error payloadError => {
            io:println("Error occured while sending event " + payloadError.message);
        }
        any| null => {
            io:println("Connected successfully");
        }
    }

    while (total == 0) {}
    io:println("Client got response successfully.");
}

// Server Message Listener.
service<grpc:Listener> helloWorldMessageListener {

    // Resource registered to receive server messages
    onMessage (string message) {
        io:println("Response received from server: " + message);
    }

    // Resource registered to receive server error messages
    onError (grpc:ServerError err) {
        if (err != null) {
            io:println("Error reported from server: " + err.message);
        }
    }

    // Resource registered to receive server completed message.
    onComplete () {
        io:println("Server Complete Sending Response.");
        total = 1;
    }
}

