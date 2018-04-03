// This is server implementation for client streaming scenario
import ballerina/io;
import ballerina/net.grpc;

// Server endpoint configuration
endpoint grpc:Service ep {
    host:"localhost",
    port:9090
};

@grpc:serviceConfig {rpcEndpoint:"LotsOfGreetings",
    clientStreaming:true,
    generateClientConnector:false}
service<grpc:Endpoint> helloWorld bind ep {
    onOpen (endpoint client) {
        io:println("connected sucessfully.");
    }

    onMessage (endpoint client, string name) {
        io:println("greet received: " + name);
    }

    onError (endpoint client, grpc:ServerError err) {
        if (err != null) {
            io:println("Something unexpected happens at server : " + err.message);
        }
    }

    onComplete (endpoint client) {
        io:println("Server Response");
        grpc:ConnectorError err = client -> send("Ack");
        if (err != null) {
            io:println("Error at onComplete send message : " + err.message);
        }
    }
}
