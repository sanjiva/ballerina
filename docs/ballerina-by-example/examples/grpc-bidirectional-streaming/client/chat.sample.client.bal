// This is client implementation for bidirectional streaming scenario
package client;

import ballerina/net.grpc;
import ballerina/io;
import ballerina/log;
import ballerina.runtime;

int total = 0;
function main (string[] args) {

    endpoint chatClient chatEp {
        host:"localhost",
        port:9090
    };

    endpoint grpc:Client ep;
    // Executing unary non-blocking call registering server message listener.
    var res = chatEp -> chat(typeof chatMessageListener);
    match res {
        grpc:error err => {
            io:print("error");
        }
        grpc:Client con => {
            ep = con;
        }
    }
    ChatMessage mes = {};
    mes.name = "Sam";
    mes.message = "Hi ";
    grpc:ConnectorError connErr = ep -> send(mes);
    if (connErr != null) {
        io:println("Error at LotsOfGreetings : " + connErr.message);
    }
    //this will hold forever since this is chat application
    runtime:sleepCurrentWorker(6000);
    _ = ep -> complete();
}


service<grpc:Listener> chatMessageListener {

    onMessage (string message) {
        io:println("Response received from server: " + message);
    }

    onError (grpc:ServerError err) {
        if (err != null) {
            io:println("Error reported from server: " + err.message);
        }
    }

    onComplete () {
        io:println("Server Complete Sending Responses.");
    }
}

