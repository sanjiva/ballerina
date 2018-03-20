import ballerina.io;
import ballerina.log;
import ballerina.net.grpc;

endpoint grpc:Service ep {
  host:"localhost",
  port:9090
};

@grpc:serviceConfig {generateClientConnector:false}
service<grpc:Endpoint> helloWorld bind ep {

    @grpc:resourceConfig {streaming:true}
    lotsOfReplies (endpoint client, string name) {
        log:printInfo("Server received hello from " + name);
        string[] greets = ["Hi", "Hey", "GM"];
        foreach greet in greets {
            log:printInfo("send reply: " + greet + " " + name);
            grpc:ConnectorError err = client -> send(greet + " " + name);
            if (err != null) {
                io:println("Error at lotsOfReplies : " + err.message);
            }
        }
        // Once all messages are sent, server send complete message to notify the client, I’m done.
        _ = client -> complete();
        log:printInfo("send all responses sucessfully.");
    }

   @grpc:resourceConfig {streaming:true}
    lotsOfByes (endpoint client, string name) {
        log:printInfo("Server received hello from " + name);
        string[] greets = ["Hi", "Hey", "GM"];
        foreach greet in greets {
            log:printInfo("send reply: " + greet + " " + name);
            grpc:ConnectorError err = client -> send(greet + " " + name);
            if (err != null) {
                io:println("Error at lotsOfReplies : " + err.message);
            }
        }
        // Once all messages are sent, server send complete message to notify the client, I’m done.
        _ = client -> complete();
        log:printInfo("send all responses sucessfully.");
    }
}
