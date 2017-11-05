import ballerina.net.http;

function main (string[] args) {
    http:HttpClient httpConnector = create http:HttpClient("http://www.mocky.io", getConnectorConfigs());
    http:Request req = {};

    //Send a GET request to the specified endpoint
    http:Response resp = httpConnector.get("/v2/59d590762700000a049cd694", req);

    println("Response received for the GET request is : " + resp.getStringPayload());
}

function getConnectorConfigs () (http:Options) {
    //Enable auto redirects and give a maximum redirect count.
    http:Options option = {ssl:{}, followRedirects:{}};
    option.followRedirects.enabled = true;
    option.followRedirects.maxCount = 5;
    return option;
}
