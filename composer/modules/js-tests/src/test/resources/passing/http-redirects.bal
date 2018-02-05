import ballerina.lang.system;
import ballerina.net.http;
import ballerina.net.http.request;
import ballerina.net.http.response;

function main (string[] args) {
  http:ClientConnector httpConnector = create http:ClientConnector("http://www.mocky.io", getConnectorConfigs());
  http:Request req = {};
  //Send a GET request to the specified endpoint
  http:Response resp = httpConnector.get("/v2/59d590762700000a049cd694", req);
  system:println("Response received for the GET request is : " + response:getStringPayload(resp));
}

function getConnectorConfigs() (http:Options) {
   //Set followRedirect property to true to enable auto redirects and give a maximum redirect count.
   http:Options option = {followRedirect:true, maximumRedirectCount:5};
   return option;
}
