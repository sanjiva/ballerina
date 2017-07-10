package routingServices.samples;

import ballerina.lang.messages;
import ballerina.net.http;

@http:config {basePath:"/nasdaqStocks"}
service<http> nasdaqStocksQuote {
    
    @http:POST{}
    resource stocks (message m) {
        message response = {};
        json payload = {"exchange":"nasdaq", "name":"IBM", "value":"127.50"};
        messages:setJsonPayload(response, payload);
        reply response;
        
    }
    
}
