import ballerina/http;
import ballerina/mime;
import ballerina/auth;

function testCanHandleHttpJwtAuthWithoutHeader () returns (boolean) {
    http:HttpJwtAuthnHandler handler = new(createJwtAuthProvider("ballerina/security/ballerinaTruststore.p12"));
    http:Request request = createRequest ();
    string authHeaderValue = "Basic xxxxxx";
    mime:Entity requestEntity = new;
    requestEntity.setHeader("Authorization", authHeaderValue);
    request.setEntity(requestEntity);
    return handler.canHandle(request);
}

function testCanHandleHttpJwtAuth () returns (boolean) {
    http:HttpJwtAuthnHandler handler = new(createJwtAuthProvider("ballerina/security/ballerinaTruststore.p12"));
    http:Request request = createRequest ();
    string authHeaderValue = "Bearer xxx.yyy.zzz";
    mime:Entity requestEntity = new;
    requestEntity.setHeader("Authorization", authHeaderValue);
    request.setEntity(requestEntity);
    return handler.canHandle(request);
}

function testHandleHttpJwtAuthFailure () returns (boolean) {
    http:HttpJwtAuthnHandler handler = new(createJwtAuthProvider("ballerina/security/ballerinaTruststore.p12"));
    http:Request request = createRequest ();
    string authHeaderValue = "Bearer xxx.yyy.zzz";
    mime:Entity requestEntity = new;
    requestEntity.setHeader("Authorization", authHeaderValue);
    request.setEntity(requestEntity);
    return handler.handle(request);
}

function testHandleHttpJwtAuth (string token, string trustStorePath) returns (boolean) {
    http:HttpJwtAuthnHandler handler = new(createJwtAuthProvider(trustStorePath));
    http:Request request = createRequest ();
    string authHeaderValue = "Bearer " + token;
    mime:Entity requestEntity = new;
    requestEntity.setHeader("Authorization", authHeaderValue);
    request.setEntity(requestEntity);
    return handler.handle(request);
}

function createRequest () returns (http:Request) {
    http:Request inRequest = new;
    inRequest.rawPath = "/helloWorld/sayHello";
    inRequest.method = "GET";
    inRequest.httpVersion = "1.1";
    return inRequest;
}

function createJwtAuthProvider(string trustStorePath) returns auth:JWTAuthProvider {
    auth:JWTAuthProviderConfig jwtConfig = {};
    jwtConfig.issuer = "wso2";
    jwtConfig.audience = "ballerina";
    jwtConfig.certificateAlias = "ballerina";
    jwtConfig.trustStoreFilePath = trustStorePath;
    jwtConfig.trustStorePassword = "ballerina";
    auth:JWTAuthProvider jwtAuthProvider = new (jwtConfig);
    return jwtAuthProvider;
}
