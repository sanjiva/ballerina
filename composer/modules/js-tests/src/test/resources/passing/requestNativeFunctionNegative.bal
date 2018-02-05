import ballerina.net.http;
import ballerina.net.http.request;

function testGetContentLength (http:Request req) (int) {
    int length = request:getContentLength(req);
    return length;
}

function testGetHeader (http:Request req, string key) (string) {
    string contentType = request:getHeader(req, key);
    return contentType;
}

function testGetJsonPayload (http:Request req) (json) {
    json payload = request:getJsonPayload(req);
    return payload;
}

function testGetMethod (http:Request req) (string ) {
    string method = request:getMethod(req);
    return method;
}

function testGetProperty (http:Request req, string propertyName) (string) {
    string payload = request:getProperty(req, propertyName);
    return payload;
}

function testGetRequestURL (http:Request req) (string) {
    string url = request:getRequestURL(req);
    if (url == "") {
        url = "no url";
    }
    return url;
}

function testGetStringPayload (http:Request req) (string) {
    string payload = request:getStringPayload(req);
    return payload;
}

function testGetBinaryPayload (http:Request req) (blob) {
    blob payload = request:getBinaryPayload(req);
    return payload;
}

function testGetXmlPayload (http:Request req) (xml) {
    xml payload = request:getXmlPayload(req);
    return payload;
}

function testRemoveHeader (http:Request req, string key) (http:Request) {
    request:removeHeader(req, key);
    return req;
}

function testRemoveAllHeaders (http:Request req) (http:Request) {
    request:removeAllHeaders(req);
    return req;
}

function testSetContentLength (http:Request req, int contentLength) (http:Request) {
    request:setContentLength(req, contentLength);
    return req;
}

function testSetHeader (http:Request req, string key, string value) (http:Request) {
    request:setHeader(req, key, value);
    return req;
}

function testSetJsonPayload (http:Request req, json value) (http:Request) {
    request:setJsonPayload(req, value);
    return req;
}

function testSetProperty (http:Request req, string name, string value) (http:Request) {
    request:setProperty(req, name, value);
    return req;
}

function testSetStringPayload (http:Request req, string value) (http:Request) {
    request:setStringPayload(req, value);
    return req;
}

function testSetXmlPayload (http:Request req, xml value) (http:Request) {
    request:setXmlPayload(req, value);
    return req;
}