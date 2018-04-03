// This is client implementation for client streaming scenario
package client;

import ballerina/io;
import ballerina/log;

int total = 0;
function main (string[] args) {
    // Client endpoint configuration
    endpoint helloWorldClient helloWorldEp {
        host:"localhost",
        port:9090
    };

    endpoint grpc:Client ep;
    // Executing unary non-blocking call registering server message listener.
    var res = helloWorldEp -> LotsOfGreetings(typeof helloWorldMessageListener);
    match res {
        grpc:error err => {
            io:print("error");
        }
        grpc:Client con => {
            ep = con;
        }
    }

    log:printInfo("Initialized connection sucessfully.");

    string[] greets = ["Hi", "Hey", "GM"];
    var name = "John";
    foreach greet in greets {
        log:printInfo("send greeting: " + greet + " " + name);
        grpc:ConnectorError connErr = ep -> send(greet + " " + name);
        if (connErr != null) {
            io:println("Error at LotsOfGreetings : " + connErr.message);
        }
    }
    _ = ep -> complete();

    //to hold the programme
    while (total == 0) {}
    io:println("completed successfully");
}

// Server Message Listener.
service<grpc:Listener> helloWorldMessageListener {

    // Resource registered to receive server messages
    onMessage (string message) {
        total = 1;
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
        total = 1;
        io:println("Server Complete Sending Responses.");
    }
}

