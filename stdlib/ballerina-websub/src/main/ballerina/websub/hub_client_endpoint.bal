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

import ballerina/http;

//////////////////////////////////////////
/////// WebSub Hub Client Endpoint ///////
//////////////////////////////////////////

documentation {
    Object representing the WebSub Hub Client Endpoint.

    E{{}}
    F{{config}} The configuration for the endpoint
}
public type Client object {

    public {
        HubClientEndpointConfig config;
    }

    private {
        http:Client httpClientEndpoint;
    }

    documentation {
        Called when the endpoint is being initialized during package initialization.

        P{{config}} The configuration for the endpoint
    }
    public function init(HubClientEndpointConfig config) {
        endpoint http:Client httpClientEndpoint {
            url:config.url,
            secureSocket:config.secureSocket,
            auth:config.auth,
            followRedirects:config.followRedirects
        };

        self.httpClientEndpoint = httpClientEndpoint;
        self.config = config;
    }

    documentation {
        Retrieves the caller actions client code uses.

        R{{}} `CallerActions` The caller actions available for clients
    }
    public function getCallerActions() returns (CallerActions) {
        //TODO: create a single object - move to init
        CallerActions webSubHubClientConn = new CallerActions(config.url, httpClientEndpoint, config.followRedirects);
        return webSubHubClientConn;
    }

};

documentation {
    Record representing the configuration parameters for the WebSub Hub Client Endpoint.

    F{{url}} The URL of the target Hub
    F{{secureSocket}} SSL/TLS related options for the underlying HTTP Client
    F{{auth}} Authentication mechanism for the underlying HTTP Client
    F{{followRedirects}} HTTP redirect related configuration
}
public type HubClientEndpointConfig {
    string url,
    http:SecureSocket? secureSocket,
    http:AuthConfig? auth,
    http:FollowRedirects? followRedirects,
};
