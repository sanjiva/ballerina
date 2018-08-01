import ballerina/http;
import ballerina/mime;
import ballerina/io;

function testContentType(http:Response res, string contentTypeValue) returns string? {
    res.setContentType(contentTypeValue);
    return res.getContentType();
}

function testGetContentLength(http:Response res) returns string {
    return res.getHeader("content-length");
}

function testAddHeader(http:Response res, string key, string value) returns http:Response {
    res.addHeader(key, value);
    return res;
}

function testGetHeader(http:Response res, string key) returns string {
    string contentType = res.getHeader(key);
    return contentType;
}

function testGetHeaders(http:Response res, string key) returns string[] {
    return res.getHeaders(key);
}

function testGetJsonPayload(http:Response res) returns json|error {
    return res.getJsonPayload();
}

function testGetTextPayload (http:Response res) returns (string | error) {
    return res.getTextPayload();
}

function testGetBinaryPayload(http:Response res) returns byte[]|error {
    return res.getBinaryPayload();
}

function testGetXmlPayload(http:Response res) returns xml|error {
    return res.getXmlPayload();
}

function testSetPayloadAndGetText((string|xml|json|byte[]|io:ByteChannel) payload) returns string|error {
    http:Response res = new;
    res.setPayload(payload);
    return res.getTextPayload();
}

function testRemoveHeader(http:Response res, string key) returns http:Response {
    res.removeHeader(key);
    return res;
}

function testRemoveAllHeaders(http:Response res) returns http:Response {
    res.removeAllHeaders();
    return res;
}

function testSetHeader(string key, string value) returns http:Response {
    http:Response res = new;
    res.setHeader(key, value);
    return res;
}

function testSetJsonPayload(json value) returns http:Response {
    http:Response res = new;
    res.setJsonPayload(value);
    return res;
}

function testSetStringPayload(string value) returns http:Response {
    http:Response res = new;
    res.setTextPayload(value);
    return res;
}

function testSetXmlPayload(xml value) returns http:Response {
    http:Response res = new;
    res.setXmlPayload(value);
    return res;
}

endpoint http:NonListener mockEP {
    port:9090
};

@http:ServiceConfig {basePath : "/hello"}
service<http:Service> hello bind mockEP {

    @http:ResourceConfig {
        path:"/11"
    }
    echo1 (endpoint conn, http:Request req) {
        http:Response res = new;
        _ = conn -> respond(res);
    }

    @http:ResourceConfig {
        path:"/12/{phase}"
    }
    echo2 (endpoint conn, http:Request req, string phase) {
        http:Response res = new;
        res.reasonPhrase = phase;
        _ = conn -> respond(untaint res);
    }

    @http:ResourceConfig {
        path:"/13"
    }
    echo3 (endpoint conn, http:Request req) {
        http:Response res = new;
        res.statusCode = 203;
        _ = conn -> respond(res);
    }

    @http:ResourceConfig {
        path:"/addheader/{key}/{value}"
    }
    addheader (endpoint conn, http:Request req, string key, string value) {
        http:Response res = new;
        res.addHeader(untaint key, value);
        string result = untaint res.getHeader(untaint key);
        res.setJsonPayload({lang:result});
        _ = conn -> respond(res);
    }

    @http:ResourceConfig {
        path:"/getHeader/{header}/{value}"
    }
    getHeader (endpoint conn, http:Request req, string header, string value) {
        http:Response res = new;
        res.setHeader(untaint header, value);
        string result = untaint res.getHeader(untaint header);
        res.setJsonPayload({value:result});
        _ = conn -> respond(res);
    }

    @http:ResourceConfig {
        path:"/getJsonPayload/{value}"
    }
    GetJsonPayload(endpoint conn, http:Request req, string value) {
        http:Response res = new;
        json jsonStr = {lang:value};
        res.setJsonPayload(untaint jsonStr);
        var returnResult = res.getJsonPayload();
        match returnResult {
            error err => {
                res.setTextPayload("Error occurred");
                res.statusCode = 500;
            }
            json payload => {
                res.setJsonPayload(untaint payload.lang);
            }
        }
        _ = conn -> respond(res);
    }

    @http:ResourceConfig {
        path:"/GetTextPayload/{valueStr}"
    }
    GetTextPayload(endpoint conn, http:Request req, string valueStr) {
        http:Response res = new;
        res.setTextPayload(untaint valueStr);
        match res.getTextPayload() {
            error err => {res.setTextPayload("Error occurred"); res.statusCode =500;}
            string payload =>  res.setTextPayload(untaint payload);
        }
        _ = conn -> respond(res);
    }

    @http:ResourceConfig {
        path:"/GetXmlPayload"
    }
    GetXmlPayload(endpoint conn, http:Request req) {
        http:Response res = new;
        xml xmlStr = xml `<name>ballerina</name>`;
        res.setXmlPayload(xmlStr);
        match res.getXmlPayload() {
           error err => {
                res.setTextPayload("Error occurred");
                res.statusCode =500;
           }
           xml xmlPayload => {
                var name = xmlPayload.getTextValue();
                res.setTextPayload(untaint name);
           }
        }
        _ = conn -> respond(res);
    }

    @http:ResourceConfig {
        path:"/RemoveHeader/{key}/{value}"
    }
    RemoveHeader (endpoint conn, http:Request req, string key, string value) {
        http:Response res = new;
        res.setHeader(untaint key, value);
        res.removeHeader(untaint key);
        string header;
        if (!res.hasHeader(key)) {
            header = "value is null";
        }
        res.setJsonPayload({value:header});
        _ = conn -> respond(res);
    }

    @http:ResourceConfig {
        path:"/RemoveAllHeaders"
    }
    RemoveAllHeaders (endpoint conn, http:Request req) {
        http:Response res = new;
        res.setHeader("Expect", "100-continue");
        res.setHeader("Range", "bytes=500-999");
        res.removeAllHeaders();
        string header;
        if(!res.hasHeader("Range")) {
            header = "value is null";
        }
        res.setJsonPayload({value:header});
        _ = conn -> respond(res);
    }
}
