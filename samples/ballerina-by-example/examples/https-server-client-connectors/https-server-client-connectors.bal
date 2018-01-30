import ballerina.net.http;

@http:configuration {
    basePath:"/hello",
    httpsPort:9095,
    keyStoreFile:"${ballerina.home}/bre/security/ballerinaKeystore.p12",
    keyStorePassword:"ballerina",
    certPassword:"ballerina"
}

service<http> helloWorld {
    @http:resourceConfig {
        methods:["GET"],
        path:"/"
    }

    resource sayHello (http:Connection conn, http:InRequest req) {
        http:OutResponse res = {};
        res.setStringPayload("Successful");
        _ = conn.respond(res);
    }
}

@Description {value:"Ballerina client connector can be used to connect to the created https server. You have to run the service before running this main function. As this is a 1-way ssl connection, client needs to provide trustStoreFile and trustStorePassword."}
function main (string[] args) {
    endpoint<http:HttpClient> httpEndpoint {
        create http:HttpClient("https://localhost:9095", getConnectorConfigs());
    }
    //Creates an outbound request.
    http:OutRequest req = {};
    http:InResponse resp = {};
    resp, _ = httpEndpoint.get("/hello/", req);
    println("Response code: " + resp.statusCode);
    println("Response: " + resp.getStringPayload());
}

function getConnectorConfigs() (http:Options) {
    http:Options option = {
          ssl: {
                 trustStoreFile:"${ballerina.home}/bre/security/ballerinaTruststore.p12",
                 trustStorePassword:"ballerina"
               },
          followRedirects: {}
    };
    return option;
}
