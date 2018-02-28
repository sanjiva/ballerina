package routingServices.samples;

import ballerina.net.http;

@http:configuration {basePath:"/nasdaqStocks"}
service<http> nasdaqStocksQuote {

    @http:resourceConfig {
        methods:["POST"]
    }
    resource stocks (http:Connection conn, http:InRequest req) {
        json payload = {"exchange":"nasdaq", "name":"IBM", "value":"127.50"};
        http:OutResponse res = {};
        res.setJsonPayload(payload);
        _ = conn.respond(res);
    }
}
