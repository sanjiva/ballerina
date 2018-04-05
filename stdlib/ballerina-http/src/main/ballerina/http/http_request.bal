package ballerina.http;

import ballerina/file;
import ballerina/io;
import ballerina/mime;

@Description { value:"Represents an HTTP request message"}
@Field {value:"path: Resource path of request URI"}
@Field {value:"method: HTTP request method"}
@Field {value:"httpVersion: The version of HTTP"}
@Field {value:"userAgent: User-Agent request header"}
@Field {value:"extraPathInfo: Additional information associated with the URL sent by the client"}
public type Request object {
    public {
        string rawPath;
        string method;
        string httpVersion;
        string userAgent;
        string extraPathInfo;
        RequestCacheControl cacheControl;
    }

    @Description {value:"Gets the Session struct for a valid session cookie from the connection. Otherwise creates a new Session struct."}
    @Return {value:"HTTP Session record type"}
    public native function createSessionIfAbsent() returns (Session);

    @Description {value:"Gets the Session struct from the connection if it is present"}
    @Return {value:"The HTTP Session record type assoicated with the request"}
    public native function getSession() returns (Session);

    @Description {value:"Set the entity to request"}
    @Return {value:"Entity of the request"}
    public native function setEntity (mime:Entity entity);

    @Description {value:"Gets the query parameters from the HTTP request as a map"}
    @Return {value:"The map of query params"}
    public native function getQueryParams () returns (map);

    @Description {value:"Get matrix parameters from the request"}
    @Param {value:"path: Path to the location of matrix parameters"}
    @Return {value:"A map of matrix paramters which can be found for a given path"}
    public native function getMatrixParams (string path) returns (map);

    @Description {value:"Get the entity from the request"}
    @Return {value:"Entity of the request"}
    @Return {value:"EntityError will might get thrown during entity construction in case of errors"}
    public native function getEntity () returns (mime:Entity | mime:EntityError);

    @Description {value:"Get the entity from the request without the body. This function is to be used only internally"}
    @Return {value:"Entity of the request"}
    native function getEntityWithoutBody () returns (mime:Entity);

    @Description {value:"Check whether the requested header exists"}
    @Param {value:"headerName: The header name"}
    @Return {value:"Boolean representing the existence of a given header"}
    public function hasHeader (string headerName) returns (boolean);

    @Description {value:"Returns the header value with the specified header name. If there are more than one header value for the specified header name, the first value is returned."}
    @Param {value:"headerName: The header name"}
    @Return {value:"The first header value for the provided header name. Returns null if the header does not exist."}
    public function getHeader (string headerName) returns (string);

    @Description {value:"Gets transport headers from the request"}
    @Param {value:"headerName: The header name"}
    @Return {value:"The header values struct array for a given header name"}
    public function getHeaders (string headerName) returns (string[]);

    @Description {value:"Sets the value of a transport header"}
    @Param {value:"headerName: The header name"}
    @Param {value:"headerValue: The header value"}
    public function setHeader (string headerName, string headerValue);

    @Description {value:"Adds the specified key/value pair as an HTTP header to the request"}
    @Param {value:"headerName: The header name"}
    @Param {value:"headerValue: The header value"}
    public function addHeader (string headerName, string headerValue);

    @Description {value:"Removes a transport header from the request"}
    @Param {value:"key: The header name"}
    public function removeHeader (string key);

    @Description {value:"Removes all transport headers from the message"}
    public function removeAllHeaders ();

    @Description {value:"Get all transport headers from the request. Manipulating the return map does not have any impact to the original copy"}
    public function getCopyOfAllHeaders () returns (map);

    @Description {value:"Checks whether the client expects a 100-continue response."}
    @Return {value:"Returns true if the client expects a 100-continue response. If not, returns false."}
    public function expects100Continue () returns (boolean);

    @Description {value:"Gets the request payload in JSON format"}
    @Return {value:"The JSON reresentation of the message payload or 'PayloadError' in case of errors"}
    public function getJsonPayload () returns (json | PayloadError);

    @Description {value:"Gets the request payload in XML format"}
    @Return {value:"The XML representation of the message payload or 'PayloadError' in case of errors"}
    public function getXmlPayload () returns (xml | PayloadError);

    @Description {value:"Gets the request payload as a string"}
    @Return {value:"The string representation of the message payload or 'PayloadError' in case of errors"}
    public function getStringPayload () returns (string | PayloadError);

    @Description {value:"Gets the request payload in blob format"}
    @Return {value:"The blob representation of the message payload or 'PayloadError' in case of errors"}
    public function getBinaryPayload () returns (blob | PayloadError);

    @Description {value:"Get the request payload as a byte channel except for multiparts. In case of multiparts,
    please use 'getMultiparts()' instead."}
    @Return {value:"A byte channel as the message payload or 'PayloadError' in case of errors"}
    public function getByteChannel () returns (io:ByteChannel | PayloadError);

    @Description {value:"Gets the form parameters from the HTTP request as a map"}
    @Return {value:"The map of form params or 'PayloadError' in case of errors"}
    public function getFormParams () returns (map | PayloadError);

    @Description {value:"Get multiparts from request"}
    @Return {value:"Returns the body parts as an array of entities"}
    public function getMultiparts () returns (mime:Entity[] | mime:EntityError);

    @Description {value:"Sets a JSON as the request payload"}
    @Param {value:"payload: The JSON payload to be set to the request"}
    public function setJsonPayload (json payload);

    @Description {value:"Sets an XML as the payload"}
    @Param {value:"payload: The XML payload object"}
    public function setXmlPayload (xml payload);

    @Description {value:"Sets a string as the request payload"}
    @Param {value:"payload: The payload to be set to the request as a string"}
    public function setStringPayload (string payload);

    @Description {value:"Sets a blob as the request payload"}
    @Param {value:"payload: The blob representation of the message payload"}
    public function setBinaryPayload (blob payload);

    @Description {value:"Set multiparts as the request payload"}
    @Param {value:"bodyParts: Represent body parts that needs to be set to the request"}
    @Param {value:"contentType: Content type of the top level message"}
    public function setMultiparts (mime:Entity[] bodyParts, string contentType);

    @Description {value:"Sets the entity body of the request with the given file content"}
    @Param {value:"fileHandler: File that needs to be set to the payload"}
    @Param {value:"contentType: Content-Type of the given file"}
    public function setFileAsPayload (file:File fileHandler, string contentType);

    @Description {value:"Set a byte channel as the request payload"}
    @Param {value:"payload: The byte channel representation of the message payload"}
    public function setByteChannel (io:ByteChannel payload);

    function parseCacheControlHeader();
}

/////////////////////////////////
/// Ballerina Implementations ///
/////////////////////////////////

public function Request::hasHeader (string headerName) returns (boolean) {
    mime:Entity entity = self.getEntityWithoutBody();
    return entity.hasHeader(headerName);
}

public function Request::getHeader (string headerName) returns (string) {
    mime:Entity entity = self.getEntityWithoutBody();
    return entity.getHeader(headerName);
}

public function Request::getHeaders (string headerName) returns (string[]) {
    mime:Entity entity = self.getEntityWithoutBody();
    return entity.getHeaders(headerName);
}

public function Request::setHeader (string headerName, string headerValue) {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setHeader(headerName, headerValue);
}

public function Request::addHeader (string headerName, string headerValue) {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.addHeader(headerName, headerValue);
}

public function Request::removeHeader (string key) {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.removeHeader(key);
}

public function Request::removeAllHeaders () {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.removeAllHeaders();
}

public function Request::getCopyOfAllHeaders () returns (map) {
    mime:Entity entity = self.getEntityWithoutBody();
    return entity.getCopyOfAllHeaders();
}

public function Request::expects100Continue () returns (boolean) {
    return self.hasHeader(EXPECT) ? self.getHeader(EXPECT) ==  "100-continue" : false;
}

public function Request::getJsonPayload () returns (json | PayloadError) {
    match self.getEntity() {
        mime:EntityError err => return <PayloadError>err;
        mime:Entity mimeEntity => {
            match mimeEntity.getJson() {
                mime:EntityError payloadErr => return <PayloadError>payloadErr;
                json jsonPayload => return jsonPayload;
            }
        }
    }
}

public function Request::getXmlPayload () returns (xml | PayloadError) {
    match self.getEntity() {
        mime:EntityError err => return <PayloadError>err;
        mime:Entity mimeEntity => {
            match mimeEntity.getXml() {
                mime:EntityError payloadErr => return <PayloadError>payloadErr;
                xml xmlPayload => return xmlPayload;
            }
        }
    }
}

public function Request::getStringPayload () returns (string | PayloadError) {
    match self.getEntity() {
        mime:EntityError err => return <PayloadError>err;
        mime:Entity mimeEntity => {
            match mimeEntity.getText() {
                mime:EntityError payloadErr => return <PayloadError>payloadErr;
                string textPayload => return textPayload;
            }
        }
    }
}

public function Request::getBinaryPayload () returns (blob | PayloadError) {
    match self.getEntity() {
        mime:EntityError err => return <PayloadError>err;
        mime:Entity mimeEntity => {
            match mimeEntity.getBlob() {
                mime:EntityError payloadErr => return <PayloadError>payloadErr;
                blob binaryPayload => return binaryPayload;
            }
        }
    }
}

public function Request::getByteChannel () returns (io:ByteChannel | PayloadError) {
    match self.getEntity() {
        mime:EntityError err => return <PayloadError>err;
            mime:Entity mimeEntity => {
            match mimeEntity.getByteChannel() {
                mime:EntityError payloadErr => return <PayloadError>payloadErr;
                io:ByteChannel byteChannel => return byteChannel;
            }
        }
    }
}

public function Request::getFormParams () returns (map | PayloadError) {
    var mimeEntity = self.getEntity();
    match mimeEntity {
        mime:EntityError err => return <PayloadError>err;
        mime:Entity entity => {

            map parameters = {};
            var entityText = entity.getText();
            match entityText {
                mime:EntityError txtErr => return <PayloadError>txtErr; // TODO: Check if this is ok

                string formData => {
                    if (formData != null && formData != "") {
                        string[] entries = formData.split("&");
                        int entryIndex = 0;
                        while (entryIndex < lengthof entries) {
                            int index = entries[entryIndex].indexOf("=");
                            if (index != -1) {
                                string name = entries[entryIndex].subString(0, index).trim();
                                int size = entries[entryIndex].length();
                                string value = entries[entryIndex].subString(index + 1, size).trim();
                                if (value != "") {
                                    parameters[name] = value;
                                }
                            }
                            entryIndex = entryIndex + 1;
                        }
                    }
                }
            }
            return parameters;
        }
    }
}

public function Request::getMultiparts () returns (mime:Entity[] | mime:EntityError) {
    var mimeEntity = self.getEntity();
    match mimeEntity {
        mime:Entity entity => return entity.getBodyParts();
        mime:EntityError err => return err;
    }
}

public function Request::setJsonPayload (json payload) {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setJson(payload);
    entity.contentType = getMediaTypeFromRequest(request, mime:APPLICATION_JSON);
    self.setEntity(entity);
}

public function Request::setXmlPayload (xml payload) {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setXml(payload);
    entity.contentType = getMediaTypeFromRequest(request, mime:APPLICATION_XML);
    self.setEntity(entity);
}

public function Request::setStringPayload (string payload) {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setText(payload);
    entity.contentType = getMediaTypeFromRequest(request, mime:TEXT_PLAIN);
    self.setEntity(entity);
}

public function Request::setBinaryPayload (blob payload) {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setBlob(payload);
    entity.contentType = getMediaTypeFromRequest(request, mime:APPLICATION_OCTET_STREAM);
    self.setEntity(entity);
}

public function Request::setMultiparts (mime:Entity[] bodyParts, string contentType) {
    mime:Entity entity = self.getEntityWithoutBody();
    mime:MediaType mediaType = getMediaTypeFromRequest(request, mime:MULTIPART_MIXED);
    if (contentType != null && contentType != "") {
        mediaType = mime:getMediaType(contentType);
    }
    entity.contentType = mediaType;
    entity.setBodyParts(bodyParts);
    self.setEntity(entity);
}

public function Request::setFileAsPayload (file:File fileHandler, string contentType) {
    mime:MediaType mediaType = mime:getMediaType(contentType);
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setFileAsEntityBody(fileHandler);
    entity.contentType = mediaType;
    self.setEntity(entity);
}

public function Request::setByteChannel (io:ByteChannel payload) {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setByteChannel(payload);
    self.setEntity(entity);
}

@Description {value:"Construct MediaType struct from the content-type header value"}
@Param {value:"request: The outbound request message"}
@Param {value:"defaultContentType: Default content-type to be used in case the content-type header doesn't contain any value"}
@Return {value:"Return 'MediaType' struct"}
function getMediaTypeFromRequest (Request request, string defaultContentType) returns (mime:MediaType) {
    mime:MediaType mediaType = mime:getMediaType(defaultContentType);

    if (request.hasHeader(mime:CONTENT_TYPE)) {
        string contentTypeValue = request.getHeader(mime:CONTENT_TYPE);
        if (contentTypeValue != "") { // TODO: may need to trim this before doing an empty string check
            return mime:getMediaType(contentTypeValue);
        } else {
            return mediaType;
        }
    } else {
        return mediaType;
    }
}
