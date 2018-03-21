// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
// under the License.

package ballerina.net.http.authadaptor;

import ballerina/net.http;
import ballerina/auth.basic;
import ballerina/auth.utils;
import ballerina/auth.userstore;
import ballerina/security.crypto;
import ballerina/log;

@Description {value:"Authentication cache name"}
const string AUTH_CACHE = "basic_auth_cache";

@Description {value:"Basic authenticator instance"}
basic:BasicAuthenticator basicAuthenticator;

@Description {value:"Representation of Basic Auth handler for HTTP traffic"}
@Field {value:"name: Authentication handler name"}
public struct HttpBasicAuthnHandler {
    string name = "basic";
}

@Description {value:"Intercepts a request for authentication"}
@Param {value:"req: Request object"}
@Return {value:"boolean: true if authentication is a success, else false"}
public function <HttpBasicAuthnHandler basicAuthnHandler> handle (http:Request req) returns (boolean) {

    // extract the header value
    var basicAuthHeaderValue, err = extractBasicAuthHeaderValue(req);
    if (err != null) {
        log:printErrorCause("Error in extracting basic authentication header", err);
        return false;
    }

    if (basicAuthenticator == null) {
        userstore:FilebasedUserstore fileBasedUserstore = {};
        basicAuthenticator = basic:createAuthenticator((userstore:UserStore)fileBasedUserstore,
                                            utils:createCache(AUTH_CACHE));
    }
    basic:AuthenticationInfo authInfo;
    // check in the cache - cache key is the sha256 hash of the basic auth header value
    string basicAuthCacheKey = crypto:getHash(basicAuthHeaderValue, crypto:Algorithm.SHA256);
    any cachedAuthResult = basicAuthenticator.getCachedAuthResult(basicAuthCacheKey);
    if (cachedAuthResult != null) {
        // cache hit
        var authInfo, typeCastErr = (basic:AuthenticationInfo)cachedAuthResult;
        if (typeCastErr == null) {
            // no type cast error, return cached result.
            log:printDebug("Auth cache hit");
            return authInfo.isAuthenticated;
        }
        // if a casting error occurs, clear the cache entry
        basicAuthenticator.clearCachedAuthResult(basicAuthCacheKey);
    }

    var username, password, err = utils:extractBasicAuthCredentials(basicAuthHeaderValue);
    if (err != null) {
        log:printErrorCause("Error in decoding basic authentication header", err);
        return false;
    }
    log:printDebug("Auth cache miss");

    authInfo = basic:createAuthenticationInfo(username, basicAuthenticator.authenticate(username, password));
    // cache result
    basicAuthenticator.cacheAuthResult(basicAuthCacheKey, authInfo);
    if (authInfo.isAuthenticated) {
        log:printDebug("Successfully authenticated against the userstore");
    } else {
        log:printDebug("Authentication failure");
    }

    return authInfo.isAuthenticated;
}

@Description {value:"Checks if the provided request can be authenticated with basic auth"}
@Param {value:"req: Request object"}
@Return {value:"boolean: true if its possible authenticate with basic auth, else false"}
public function <HttpBasicAuthnHandler basicAuthnHandler> canHandle (http:Request req) returns (boolean) {
    string basicAuthHeader = req.getHeader(AUTH_HEADER);
    if (basicAuthHeader != null && basicAuthHeader.hasPrefix(AUTH_SCHEME_BASIC)) {
        return true;
    }
    return false;
}
