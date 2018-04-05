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

package ballerina.http;

import ballerina/internal;

@Description {value:"Authz handler chain instance"}
AuthzHandlerChain authzHandlerChain;

@Description {value:"Representation of the Authorization filter"}
@Field {value:"filterRequest: request filter method which attempts to authorize the request"}
@Field {value:"filterRequest: response filter method (not used this scenario)"}
public type AuthzFilter object {
    public {
        function (Request request, FilterContext context) returns (FilterResult) filterRequest;
        function (Response response, FilterContext context) returns (FilterResult) filterResponse;
    }

    new (function (Request, FilterContext) returns (FilterResult) requestFilter,
        function (Response, FilterContext) returns (FilterResult) responseFilter) {
        filterRequest = requestFilter;
        filterResponse = responseFilter;
    }

    public function init ();

    public function terminate ();
};

@Description {value:"Initializes the AuthzFilter"}
public function AuthzFilter::init () {
    authzHandlerChain = createAuthzHandlerChain();
}

@Description {value:"Stops the AuthzFilter"}
public function AuthzFilter::terminate () {
}

@Description {value:"Filter function implementation which tries to authorize the request"}
@Param {value:"request: Request instance"}
@Param {value:"context: FilterContext instance"}
@Return {value:"FilterResult: Authorization result to indicate if the request can proceed or not"}
public function authzRequestFilterFunc (Request request, FilterContext context) returns (FilterResult) {
    // first check if the resource is marked to be authenticated. If not, no need to authorize.
    // TODO: check if we can remove this once the security context is there.
    if (!isResourceSecured(context)) {
        // let the request pass
        return createAuthzResult(true);
    }
    // check if this resource is protected
    string[]? scopes = getScopesForResource(context);
    boolean authorized;
    match scopes {
        string[] scopeNames => {
            authorized = authzHandlerChain.handle(request, scopeNames, context.resourceName);
        }
        () => {
            // scopes are not defined, no need to authorize
            authorized = true;
        }
    }
    return createAuthzResult(authorized);
}

@Description {value:"Creates an instance of FilterResult"}
@Param {value:"authorized: authorization status for the request"}
@Return {value:"FilterResult: Authorization result to indicate if the request can proceed or not"}
function createAuthzResult (boolean authorized) returns (FilterResult) {
    FilterResult requestFilterResult = {};
    if (authorized) {
        requestFilterResult = {canProceed:true, statusCode:200, message:"Successfully authorized"};
    } else {
        requestFilterResult = {canProceed:false, statusCode:403, message:"Authorization failure"};
    }
    return requestFilterResult;
}

@Description {value:"Retrieves the scope for the resource, if any"}
@Param {value:"context: FilterContext object"}
@Return {value:"string: Scope name if defined, else nil"}
function getScopesForResource (FilterContext context) returns (string[]|()) {
    string[]|() scope = getAuthzAnnotation(internal:getResourceAnnotations(context.serviceType,
                                                                      context.resourceName));
    match scope {
        string[] scopeVal => {
            return scopeVal;
        }
        () => {
            // if not found in resource level, check in service level
            return getAuthzAnnotation(internal:getServiceAnnotations(context.serviceType));
        }
    }
}

@Description {value:"Tries to retrieve the annotation value for scope hierarchically - first from the resource level
and then from the service level, if its not there in the resource level"}
@Param {value:"annData: array of annotationData instances"}
@Return {value:"string[]: array of scope name if defined, else nil"}
function getAuthzAnnotation (internal:annotationData[] annData) returns (string[]|()) {
    if (lengthof annData == 0) {
        return ();
    }
    internal:annotationData|() authAnn;
    foreach ann in annData {
        if (ann.name == AUTH_ANN_NAME && ann.pkgName == AUTH_ANN_PACKAGE) {
            authAnn = ann;
            break;
        }
    }
    match authAnn {
        internal:annotationData annData1 => {
            var authConfig = check <auth:AuthConfig> annData1.value;
            return authConfig.scopes;
        }
        () => {
            return ();
        }
    }
}
