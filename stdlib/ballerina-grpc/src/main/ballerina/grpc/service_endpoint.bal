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
package ballerina.grpc;

documentation {
    Represents the gRPC service endpoint

    F{{config}} - gRPC service endpoint configuration.
}
public type Service object {
    public {
        ServiceEndpointConfiguration config;
    }

    documentation {
        Gets called when the endpoint is being initialize during package init time

        P{{config}} - The ServiceEndpointConfiguration of the endpoint.
    }
    public function init (ServiceEndpointConfiguration config) {
        self.config = config;
        var err = self.initEndpoint();
        if (err != ()) {
            throw err;
        }
    }

    public native function initEndpoint() returns (error);

    documentation {
        Gets called every time a service attaches itself to this endpoint - also happens at package
    init time. not supported in client connector

        P{{serviceType}} - The type of the service to be registered.
    }
    public native function register (typedesc serviceType);

    documentation {
        Starts the registered service
    }
    public native function start ();

    documentation {
        Stops the registered service
    }
    public native function stop ();

    documentation {
        Returns the client connection that servicestub code uses
    }
    public native function getClient () returns (ClientResponder);
};

documentation {
    Represents the gRPC server endpoint configuration

    F{{host}} - The server hostname.
    F{{port}} - The server port.
    F{{ssl}} - The SSL configurations for the client endpoint.
}
public type ServiceEndpointConfiguration {
    string host;
    int port;
    SslConfiguration ssl;
};

documentation {
    SslConfiguration struct represents SSL/TLS options to be used for client invocation

    F{{trustStoreFile}} - File path to trust store file.
    F{{trustStorePassword}} - Trust store password.
    F{{keyStoreFile}} - File path to key store file.
    F{{keyStorePassword}} - Key store password.
    F{{sslEnabledProtocols}} - SSL/TLS protocols to be enabled. eg: TLSv1,TLSv1.1,TLSv1.2.
    F{{ciphers}} - List of ciphers to be used. eg: TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
    TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA.
    F{{sslProtocol}} - SSL Protocol to be used. eg: TLS1.2
    F{{validateCertEnabled}} - The status of validateCertEnabled
    F{{sslVerifyClient}} - SSL Verify client
    F{{certPassword}} - certificate password
    F{{tlsStoreType}} - TLS store type
    F{{cacheSize}} - Maximum size of the cache
    F{{cacheValidityPeriod}} - Time duration of cache validity period
}
public type SslConfiguration {
    string trustStoreFile;
    string trustStorePassword;
    string keyStoreFile;
    string keyStorePassword;
    string sslEnabledProtocols;
    string ciphers;
    string sslProtocol;
    boolean validateCertEnabled;
    string sslVerifyClient;
    string certPassword;
    string tlsStoreType;
    int cacheSize;
    int cacheValidityPeriod;
};

public type Listener object {
    function getEndpoint() returns (Service) {
        return new;
    }
};
