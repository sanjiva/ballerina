import ballerina/net.http;
import ballerina/io;
import ballerina/mime;

endpoint http:ClientEndpoint clientEP {
    targets: [{
        uri: "https://localhost:9095",
        secureSocket: {
            keyStore: {
                filePath: "${ballerina.home}/bre/security/ballerinaKeystore.p12",
                password: "ballerina"
            },
            trustStore: {
                filePath: "${ballerina.home}/bre/security/ballerinaTruststore.p12",
                password: "ballerina"
            },
            protocols: {
                protocolName: "TLSv1.2",
                versions: "TLSv1.2,TLSv1.1"
            },
            ciphers:"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA"
        }
    }]
};

function main (string[] args) {
    http:Request req = {};
    var resp = clientEP -> get("/echo/", req);
    match resp {
        http:HttpConnectorError err => io:println(err.message);
        http:Response response => {
             match (response.getStringPayload()) {
                mime:EntityError payloadError => io:println(payloadError.message);
                string res => io:println(res);
                any | null => io:println("Error occured");
             }
        }
    }
}