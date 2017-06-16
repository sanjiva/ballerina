package passthroughservice.samples;

import ballerina.net.http;

@http:BasePath {value:"/passthrough"}
service passthrough {

    @http:GET{}
    @http:Path {value:"/"}
    resource passthrough (message m) {
        http:ClientConnector nyseEP = create http:ClientConnector("http://localhost:9090");
        message response = http:ClientConnector.get(nyseEP, "/nyseStock/stocks", m);
        reply response;

    }

}
