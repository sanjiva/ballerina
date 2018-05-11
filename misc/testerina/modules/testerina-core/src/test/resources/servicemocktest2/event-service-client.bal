import ballerina/http;
import ballerina/io;
import ballerina/mime;

string eventServiceEP = "http://localhost:9092/events";

public function getEvents () returns (json) {

    endpoint http:Client httpEndpoint {
        url:eventServiceEP
    };
    var response = httpEndpoint -> get("/");

    match response {
                   http:Response resp => {
                        var jsonRes = resp.getJsonPayload();
                        match jsonRes {
                            json payload => return payload;
                            error err => io:println(err);
                        }
                   }
                   error err => io:println(err);
    }

    return {};
}