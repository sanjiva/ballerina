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

import ballerina/internal;

@Description {value:"Authz handler chain instance"}
AuthzHandlerChain authzHandlerChain;

@Description {value:"Representation of the Authorization filter"}
@Field {value:"filterRequest: request filter method which attempts to authorize the request"}
@Field {value:"filterRequest: response filter method (not used this scenario)"}
public struct AuthzFilter {
    function (http:Request request, http:FilterContext context) returns (http:FilterResult) filterRequest;
    function (http:Response response, http:FilterContext context) returns (http:FilterResult) filterResponse;
}

@Description {value:"Initializer for AuthzFilter"}
public function <AuthzFilter filter> AuthzFilter () {
    filter.filterRequest = authzRequestFilterFunc;
    filter.filterResponse = responseFilterFunc;
}

@Description {value:"Initializes the AuthzFilter"}
public function <AuthzFilter filter> init () {
    authzHandlerChain = createAuthzHandlerChain();
}

@Description {value:"Stops the AuthzFilter"}
public function <AuthzFilter filter> terminate () {
}

@Description {value:"Filter function implementation which tries to authorize the request"}
@Param {value:"request: Request instance"}
@Param {value:"context: FilterContext instance"}
@Return {value:"FilterResult: Authorization result to indicate if the request can proceed or not"}
public function authzRequestFilterFunc (http:Request request, http:FilterContext context) returns (http:FilterResult) {
    // check if this resource is protected
    string scope = getScopeForResource(context);
    boolean authorized;
    if (scope != "") {
        authorized = authzHandlerChain.handle(request, scope, context.resourceName);
    } else {
        // if scopes are not defined, no need to authorize
        return createAuthzResult(true);
    }
    return createAuthzResult(authorized);
}

@Description {value:"Creates an instance of FilterResult"}
@Param {value:"authorized: authorization status for the request"}
@Return {value:"FilterResult: Authorization result to indicate if the request can proceed or not"}
function createAuthzResult (boolean authorized) returns (http:FilterResult) {
    http:FilterResult requestFilterResult = {};
    if (authorized) {
        requestFilterResult = {canProceed:true, statusCode:200, message:"Successfully authorized"};
    } else {
        requestFilterResult = {canProceed:false, statusCode:403, message:"Authorization failure"};
    }
    return requestFilterResult;
}

@Description {value:"Retrieves the scope for the resource, if any"}
@Param {value:"context: FilterContext object"}
@Return {value:"string: Scope name if defined, else null"}
function getScopeForResource (http:FilterContext context) returns (string) {
    string|null scope = getAuthzAnnotation(internal:getResourceAnnotations(context.serviceType,
                                                                      context.resourceName));
    match scope {
        string scopeVal => {
            return scopeVal;
        }
        any => {
            // if not found in resource level, check in service level
            string|null serviceLevelScope = getAuthzAnnotation(internal:getServiceAnnotations(context.serviceType));
            match serviceLevelScope {
                string scopeVal => {
                    return scopeVal;
                }
                any => {
                    // if the scope is still null, means authorization is not needed.
                    return "";
                }
            }
        }
    }
}

@Description {value:"Tries to retrieve the annotation value for scope hierarchically - first from the resource level
and then from the service level, if its not there in the resource level"}
@Param {value:"annData: array of annotationData instances"}
@Return {value:"string: Scope name if defined, else null"}
function getAuthzAnnotation (internal:annotationData[] annData) returns (string|null) {
    if (lengthof annData == 0) {
        return null;
    }
    internal:annotationData|null authAnn;
    foreach ann in annData {
        if (ann.name == AUTH_ANN_NAME && ann.pkgName == AUTH_ANN_PACKAGE) {
            authAnn = ann;
            break;
        }
    }
    match authAnn {
        internal:annotationData annData1 => {
            var authConfig =? <auth:AuthConfig> annData1.value;
            return authConfig.scope;
        }
        any|null => {
            return null;
        }
    }
}
