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

package ballerina.auth;

import ballerina.net.http;

@Description {value:"Representation of Authentication handler for HTTP traffic. Any authention interceptor for
basic authentication should be struct-wise similar to HttpAuthnInterceptor"}
public struct HttpAuthnHandler {
}

@Description {value:"Intercepts a HTTP request for authentication"}
@Param {value:"req: InRequest object"}
@Return {value:"boolean: true if authentication is a success, else false"}
public function <HttpAuthnHandler authnHandler> canHandle (http:InRequest req) (boolean) {
    error e = {message:"Not implemented"};
    throw e;
    return false;
}

@Description {value:"Checks if the provided HTTP request can be authenticated with basic auth"}
@Param {value:"req: InRequest object"}
@Return {value:"boolean: true if its possible authenticate with basic auth, else false"}
public function <HttpAuthnHandler authnHandler> handle (http:InRequest req) (boolean) {
    error e = {message:"Not implemented"};
    throw e;
    return false;
}
