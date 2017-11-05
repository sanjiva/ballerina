import ballerina.net.http;

@http:configuration {
    basePath:"/hello",
    httpsPort:9095,
    keyStoreFile:"${ballerina.home}/bre/security/wso2carbon.jks",
    keyStorePassword:"wso2carbon",
    certPassword:"wso2carbon",
    sslVerifyClient:"require",
    trustStoreFile:"${ballerina.home}/bre/security/client-truststore.jks",
    trustStorePassword:"wso2carbon"
}

service<http> helloWorld {
    @http:resourceConfig {
        methods:["GET"],
        path:"/"
    }

    resource sayHello (http:Request req, http:Response res) {
        //Set response payload.
        res.setStringPayload("Successful");
        //Send response to client.
        res.send();
    }
}

@Description {value:"Ballerina client connector can be used to connect to the created https server. You have to run the service before running this main function. As this is a mutual ssl connection, client also needs to provide keyStoreFile, keyStorePassword, trustStoreFile and trustStorePassword."}
function main (string[] args) {
    endpoint<http:HttpClient> connectorEP {
        create http:HttpClient("https://localhost:9095", getConnectorConfigs());
    }
    //Creates a request.
    http:Request req = {};
    http:Response resp = {};
    resp, _ = connectorEP.get("/hello/", req);
    println("Response code: " + resp.getStatusCode());
    println("Response: " + resp.getStringPayload());
}

function getConnectorConfigs() (http:Options) {
    http:Options option = {
          ssl: {
                 keyStoreFile:"${ballerina.home}/bre/security/wso2carbon.jks",
                 keyStorePassword:"wso2carbon",
                 trustStoreFile:"${ballerina.home}/bre/security/client-truststore.jks",
                 trustStorePassword:"wso2carbon"
               },
          followRedirects: {}
      };
    return option;
}
