// Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.package http;


import ballerina/io;
import ballerina/runtime;
import ballerina/mime;

@final string EMPTY_STRING = "";
@final string WHITE_SPACE = " ";
@final string CONTENT_TYPE_HEADER = "Content-Type";
@final string BASIC_SCHEME = "basic";
@final string OAUTH_SCHEME = "oauth";
@final string JWT_SCHEME = "jwt";

documentation {
    Provides secure HTTP actions for interacting with HTTP endpoints. This will make use of the authentication schemes
    configured in the HTTP client endpoint to secure the HTTP requests.

    F{{serviceUri}} The URL of the remote HTTP endpoint
    F{{config}} The configurations of the client endpoint associated with this HttpActions instance
    F{{httpClient}} The underlying `HttpActions` instance which will be making the actual network calls
}
public type HttpSecureClient object {
    //These properties are populated from the init call to the client connector as these were needed later stage
    //for retry and other few places.
    public {
        string serviceUri;
        ClientEndpointConfig config;
        CallerActions httpClient;
    }

    public new(serviceUri, config) {
        self.httpClient = createSimpleHttpClient(serviceUri, config);
    }

    documentation {
        This wraps the `post()` function of the underlying HTTP actions provider. Add relevant authentication headers
        to the request and send the request to actual network call.

        P{{path}} Resource path
        P{{request}} An HTTP outbound request message
        R{{}} The inbound response message or an error occurred while attempting to fulfill the HTTP request
    }
    public function post(string path, Request? request = ()) returns (Response|error) {
        Request req = request ?: new;
        check generateSecureRequest(req, config);
        Response response = check httpClient.post(path, request = req);
        boolean isRetry = isRetryRequired(response, config);
        if (isRetry) {
            Request newOutRequest = check cloneRequest(req);
            check updateRequestAndConfig(newOutRequest, config);
            return httpClient.post(path, request = newOutRequest);
        }
        return response;
    }

    documentation {
        This wraps the `head()` function of the underlying HTTP actions provider. Add relevant authentication headers
        to the request and send the request to actual network call.

        P{{path}} Resource path
        P{{request}} An HTTP outbound request message
        R{{}} The inbound response message or an error occurred while attempting to fulfill the HTTP request
    }
    public function head(string path, Request? request = ()) returns (Response|error) {
        Request req = request ?: new;
        check generateSecureRequest(req, config);
        Response response = check httpClient.head(path, request = req);
        boolean isRetry = isRetryRequired(response, config);
        if (isRetry) {
            Request newOutRequest = check cloneRequest(req);
            check updateRequestAndConfig(newOutRequest, config);
            return httpClient.head(path, request = newOutRequest);
        }
        return response;
    }

    documentation {
        This wraps the `put()` function of the underlying HTTP actions provider. Add relevant authentication headers
        to the request and send the request to actual network call.

        P{{path}} Resource path
        P{{request}} An HTTP outbound request message
        R{{}} The inbound response message or an error occurred while attempting to fulfill the HTTP request
    }
    public function put(string path, Request? request = ()) returns (Response|error) {
        Request req = request ?: new;
        check generateSecureRequest(req, config);
        Response response = check httpClient.put(path, request = req);
        boolean isRetry = isRetryRequired(response, config);
        if (isRetry) {
            Request newOutRequest = check cloneRequest(req);
            check updateRequestAndConfig(newOutRequest, config);
            return httpClient.put(path, request = newOutRequest);
        }
        return response;
    }

    documentation {
        This wraps the `execute()` function of the underlying HTTP actions provider. Add relevant authentication headers
        to the request and send the request to actual network call.

        P{{httpVerb}} HTTP verb value
        P{{path}} Resource path
        P{{request}} An HTTP outbound request message
        R{{}} The inbound response message or an error occurred while attempting to fulfill the HTTP request
    }
    public function execute(string httpVerb, string path, Request request) returns (Response|error) {
        var details = generateSecureRequest(request, config);
        check generateSecureRequest(request, config);
        Response response = check httpClient.execute(httpVerb, path, request);
        boolean isRetry = isRetryRequired(response, config);
        if (isRetry) {
            Request newOutRequest = check cloneRequest(request);
            check updateRequestAndConfig(newOutRequest, config);
            return httpClient.execute(httpVerb, path, newOutRequest);
        }
        return response;
    }

    documentation {
        This wraps the `patch()` function of the underlying HTTP actions provider. Add relevant authentication headers
        to the request and send the request to actual network call.

        P{{path}} Resource path
        P{{request}} An HTTP outbound request message
        R{{}} The inbound response message or an error occurred while attempting to fulfill the HTTP request
    }
    public function patch(string path, Request? request = ()) returns (Response|error) {
        Request req = request ?: new;
        check generateSecureRequest(req, config);
        Response response = check httpClient.patch(path, request = req);
        boolean isRetry = isRetryRequired(response, config);
        if (isRetry) {
            Request newOutRequest = check cloneRequest(req);
            check updateRequestAndConfig(newOutRequest, config);
            return httpClient.patch(path, request = newOutRequest);
        }
        return response;
    }

    documentation {
        This wraps the `delete()` function of the underlying HTTP actions provider. Add relevant authentication headers
        to the request and send the request to actual network call.

        P{{path}} Resource path
        P{{request}} An HTTP outbound request message
        R{{}} The inbound response message or an error occurred while attempting to fulfill the HTTP request
    }
    public function delete(string path, Request? request = ()) returns (Response|error) {
        Request req = request ?: new;
        check generateSecureRequest(req, config);
        Response response = check httpClient.delete(path, request = req);
        boolean isRetry = isRetryRequired(response, config);
        if (isRetry) {
            Request newOutRequest = check cloneRequest(req);
            check updateRequestAndConfig(newOutRequest, config);
            return httpClient.delete(path, request = newOutRequest);
        }
        return response;
    }

    documentation {
        This wraps the `get()` function of the underlying HTTP actions provider. Add relevant authentication headers
        to the request and send the request to actual network call.

        P{{path}} Request path
        P{{request}} An HTTP outbound request message
        R{{}} The inbound response message or an error occurred while attempting to fulfill the HTTP request
    }
    public function get(string path, Request? request = ()) returns (Response|error) {
        Request req = request ?: new;
        check generateSecureRequest(req, config);
        Response response = check httpClient.get(path, request = req);
        boolean isRetry = isRetryRequired(response, config);
        if (isRetry) {
            Request newOutRequest = check cloneRequest(req);
            check updateRequestAndConfig(newOutRequest, config);
            return httpClient.get(path, request = newOutRequest);
        }
        return response;
    }

    documentation {
        This wraps the `options()` function of the underlying HTTP actions provider. Add relevant authentication headers
        to the request and send the request to actual network call.

        P{{path}} Request path
        P{{request}} An HTTP outbound request message
        R{{}} The inbound response message or an error occurred while attempting to fulfill the HTTP request
    }
    public function options(string path, Request? request = ()) returns (Response|error) {
        Request req = request ?: new;
        check generateSecureRequest(req, config);
        Response response = check httpClient.options(path, request = req);
        boolean isRetry = isRetryRequired(response, config);
        if (isRetry) {
            Request newOutRequest = check cloneRequest(req);
            check updateRequestAndConfig(newOutRequest, config);
            return httpClient.options(path, request = newOutRequest);
        }
        return response;
    }

    documentation {
        This wraps the `forward()` function of the underlying HTTP actions provider. Add relevant authentication headers
        to the request and send the request to actual network call.

        P{{path}} Request path
        P{{request}} An HTTP inbound request message
        R{{}} The inbound response message or an error occurred while attempting to fulfill the HTTP request
    }
    public function forward(string path, Request request) returns (Response|error) {
        check generateSecureRequest(request, config);
        Response response = check httpClient.forward(path, request);
        boolean isRetry = isRetryRequired(response, config);
        if (isRetry) {
            Request newOutRequest = check cloneRequest(request);
            check updateRequestAndConfig(newOutRequest, config);
            return httpClient.forward(path, newOutRequest);
        }
        return response;
    }

    documentation {
        This wraps the `submit()` function of the underlying HTTP actions provider. Add relevant authentication headers
        to the request and send the request to actual network call.

        P{{httpVerb}} The HTTP verb value
        P{{path}} The resource path
        P{{request}} An HTTP outbound request message
        R{{}} An `HttpFuture` that represents an asynchronous service invocation, or an error if the submission fails
    }
    public function submit(string httpVerb, string path, Request request) returns (HttpFuture|error) {
        check generateSecureRequest(request, config);
        return httpClient.submit(httpVerb, path, request);
    }

    documentation {
        This just pass the request to actual network call.

        P{{httpFuture}} The `HttpFuture` relates to a previous asynchronous invocation
        R{{}} An HTTP response message, or an error if the invocation fails
    }
    public function getResponse(HttpFuture httpFuture) returns (Response|error) {
        return httpClient.getResponse(httpFuture);
    }

    documentation {
        This just pass the request to actual network call.

        P{{httpFuture}} The `HttpFuture` relates to a previous asynchronous invocation
        R{{}} A `boolean` that represents whether a `PushPromise` exists
    }
    public function hasPromise(HttpFuture httpFuture) returns boolean {
        return httpClient.hasPromise(httpFuture);
    }

    documentation {
        This just pass the request to actual network call.

        P{{httpFuture}} The `HttpFuture` relates to a previous asynchronous invocation
        R{{}} An HTTP Push Promise message, or an error if the invocation fails
    }
    public function getNextPromise(HttpFuture httpFuture) returns (PushPromise|error) {
        return httpClient.getNextPromise(httpFuture);
    }

    documentation {
        This just pass the request to actual network call.

        P{{promise}} The related `PushPromise`
        R{{}} A promised HTTP `Response` message, or an error if the invocation fails
    }
    public function getPromisedResponse(PushPromise promise) returns (Response|error) {
        return httpClient.getPromisedResponse(promise);
    }

    documentation {
        This just pass the request to actual network call.

        P{{promise}} The Push Promise to be rejected
    }
    public function rejectPromise(PushPromise promise) {
        return httpClient.rejectPromise(promise);
    }
};

documentation {
    Creates an HTTP client capable of securing HTTP requests with authentication.

    P{{url}} Base URL
    P{{config}} Client endpoint configurations
    R{{}} Created secure HTTP client
}
public function createHttpSecureClient(string url, ClientEndpointConfig config) returns CallerActions {
    match config.auth {
        AuthConfig => {
            HttpSecureClient httpSecureClient = new(url, config);
            return httpSecureClient;
        }
        () => {
            CallerActions httpClient = createSimpleHttpClient(url, config);
            return httpClient;
        }
    }
}

documentation {
    Prepare HTTP request with the required headers for authentication.

    P{{req}} An HTTP outbound request message
    P{{config}} Client endpoint configurations
    R{{}} The Error occured during HTTP client invocation
}
function generateSecureRequest(Request req, ClientEndpointConfig config) returns (()|error) {
    string scheme = config.auth.scheme but { () => EMPTY_STRING };
    if (scheme == BASIC_SCHEME) {
        string username = config.auth.username but { () => EMPTY_STRING };
        string password = config.auth.password but { () => EMPTY_STRING };
        string str = username + ":" + password;
        string token = check str.base64Encode();
        req.setHeader(AUTH_HEADER, AUTH_SCHEME_BASIC + WHITE_SPACE + token);
    } else if (scheme == OAUTH_SCHEME) {
        string accessToken = config.auth.accessToken but { () => EMPTY_STRING };
        if (accessToken == EMPTY_STRING) {
            string refreshToken = config.auth.refreshToken but { () => EMPTY_STRING };
            string clientId = config.auth.clientId but { () => EMPTY_STRING };
            string clientSecret = config.auth.clientSecret but { () => EMPTY_STRING };
            string refreshUrl = config.auth.refreshUrl but { () => EMPTY_STRING };

            if (refreshToken != EMPTY_STRING && clientId != EMPTY_STRING && clientSecret != EMPTY_STRING) {
                return updateRequestAndConfig(req, config);
            } else {
                error err = {};
                err.message = "Valid accessToken or refreshToken is not available to process the request"
                ;
                return err;
            }
        } else {
            req.setHeader(AUTH_HEADER, AUTH_SCHEME_BEARER + WHITE_SPACE + accessToken);
        }
    } else if (scheme == JWT_SCHEME){
        string authToken = runtime:getInvocationContext().authContext.authToken;
        req.setHeader(AUTH_HEADER, AUTH_SCHEME_BEARER + WHITE_SPACE + authToken);
    }
    return ();
}

documentation {
    Update request and client config with new access tokens retrieved.

    P{{req}} `Request` object to be updated
    P{{config}} Client endpoint configurations
    R{{}} The Error occured during HTTP client invocation
}
function updateRequestAndConfig(Request req, ClientEndpointConfig config) returns (()|error) {
    string accessToken = check getAccessTokenFromRefreshToken(config);
    req.setHeader(AUTH_HEADER, AUTH_SCHEME_BEARER + WHITE_SPACE + accessToken);
    AuthConfig? authConfig = config.auth;
    match authConfig {
        () => {}
        AuthConfig ac => ac.accessToken = accessToken;
    }
    return ();
}

documentation {
    Request an access token from authorization server using the provided refresh token.

    P{{config}} Client endpoint configurations
    R{{}} AccessToken received from the authorization server or `error` if error occured during HTTP client invocation
}
function getAccessTokenFromRefreshToken(ClientEndpointConfig config) returns (string|error) {
    string refreshToken = config.auth.refreshToken but { () => EMPTY_STRING };
    string clientId = config.auth.clientId but { () => EMPTY_STRING };
    string clientSecret = config.auth.clientSecret but { () => EMPTY_STRING };
    string refreshUrl = config.auth.refreshUrl but { () => EMPTY_STRING };

    CallerActions refreshTokenClient = createHttpSecureClient(refreshUrl, {});

    string clientIdSecret = clientId + ":" + clientSecret;
    string base64ClientIdSecret = check clientIdSecret.base64Encode();

    Request refreshTokenRequest = new;
    refreshTokenRequest.addHeader(AUTH_HEADER, AUTH_SCHEME_BASIC + WHITE_SPACE + base64ClientIdSecret);
    refreshTokenRequest.setTextPayload("grant_type=refresh_token&refresh_token=" + refreshToken,
        contentType = mime:APPLICATION_FORM_URLENCODED);
    Response refreshTokenResponse = check refreshTokenClient.post(EMPTY_STRING, request = refreshTokenRequest);

    json generatedToken = check refreshTokenResponse.getJsonPayload();
    if (refreshTokenResponse.statusCode == OK_200) {
        return generatedToken.access_token.toString();
    } else {
        error err = {};
        err.message = "Failed to generate new access token from the given refresh token";
        return err;
    }
}

documentation {
    Clone the given request into a new request with request entity.

    P{{req}} `Request` object to be cloned
    R{{}} New request object created or `error` if entity construction failed
}
function cloneRequest(Request req) returns (Request|error) {
    mime:Entity mimeEntity = check req.getEntity();
    Request newOutRequest = new;
    newOutRequest.setEntity(mimeEntity);
    return newOutRequest;
}

documentation {
    Check whether retry is required for the response. This returns true if the scheme is OAuth and the response status
    is 401 only. That implies user has given a expired access token and the client should update it with the given
    refresh url.

    P{{response}} Response object
    P{{config}} Client endpoint configurations
    R{{}} Whether the client should retry or not
}
function isRetryRequired(Response response, ClientEndpointConfig config) returns boolean {
    string scheme = config.auth.scheme but { () => EMPTY_STRING };
    if (scheme == OAUTH_SCHEME && response.statusCode == UNAUTHORIZED_401) {
        return true;
    }
    return false;
}
