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

/////////////////////////////
/// HTTP Service Endpoint ///
/////////////////////////////
documentation {
    This is used for creating HTTP service endpoints. An HTTP service endpoint is capable of responding to
    remote callers. The `Listener` is responsible for initializing the endpoint using the provided configurations and
    providing the actions for communicating with the caller.

    E{{}}
    F{{remote}} The remote address
    F{{local}} The local address
    F{{protocol}} The protocol associated with the service endpoint
}
public type Listener object {

    public {
        @readonly Remote remote;
        @readonly Local local;
        @readonly string protocol;
    }

    private {
        Connection conn;
        ServiceEndpointConfiguration config;
    }

    documentation {
        Gets invoked during package initialization to initialize the endpoint.

        R{{}} Error occurred during initialization
    }
    public function init(ServiceEndpointConfiguration config);

    public native function initEndpoint() returns error;

    documentation {
        Gets invoked when binding a service to the endpoint.

        P{{serviceType}} The type of the service to be registered
    }
    public native function register(typedesc serviceType);

    documentation {
        Starts the registered service.
    }
    public native function start();

    documentation {
        Returns the connector that client code uses.

        R{{}} The connector that client code uses
    }
    public native function getCallerActions() returns (Connection);

    documentation {
        Stops the registered service.
    }
    public native function stop();
};

documentation {
    Presents a read-only view of the remote address.

    F{{host}} The remote host name/IP
    F{{port}} The remote port
}
public type Remote {
    @readonly string host;
    @readonly int port;
};

documentation {
    Presents a read-only view of the local address.

    F{{host}} The local host name/IP
    F{{port}} The local port
}
public type Local {
    @readonly string host;
    @readonly int port;
};

documentation {
    Configures limits for requests. If these limits are violated, the request is rejected.

    F{{maxUriLength}} Maximum allowed length for a URI. Exceeding this limit will result in a
                      `414 - URI Too Long` response.
    F{{maxHeaderSize}} Maximum allowed size for headers. Exceeding this limit will result in a
                       `413 - Payload Too Large` response.
    F{{maxEntityBodySize}} Maximum allowed size for the entity body. Exceeding this limit will result in a
                           `413 - Payload Too Large` response.
}
public type RequestLimits {
    int maxUriLength = -1;
    int maxHeaderSize = -1;
    int maxEntityBodySize = -1;
};

documentation {
    Provides a set of configurations for HTTP service endpoints.

    F{{host}} The host name/IP of the endpoint
    F{{port}} The port to which the endpoint should bind to
    F{{keepAlive}} Can be set to either `KEEPALIVE_AUTO`, which respects the `connection` header, or `KEEPALIVE_ALWAYS`,
                   which always keeps the connection alive, or `KEEPALIVE_NEVER`, which always closes the connection
    F{{secureSocket}} The SSL configurations for the service endpoint. This needs to be configured in order to
                      communicate through HTTPS.
    F{{httpVersion}} Highest HTTP version supported by the endpoint
    F{{requestLimits}} Configures the parameters for request validation
    F{{filters}} If any pre-processing needs to be done to the request before dispatching the request to the
                 resource, filters can applied
}
public type ServiceEndpointConfiguration {
    string host,
    int port,
    KeepAlive keepAlive = KEEPALIVE_AUTO,
    ServiceSecureSocket? secureSocket,
    string httpVersion = "1.1",
    RequestLimits? requestLimits,
    Filter[] filters,
};

documentation {
    Configures the SSL/TLS options to be used for HTTP service.

    F{{trustStore}} Configures the trust store to be used
    F{{keyStore}} Configures the key store to be used
    F{{protocol}} SSL/TLS protocol related options
    F{{certValidation}} Certificate validation against CRL or OCSP related options
    F{{ciphers}} List of ciphers to be used (e.g.: TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                 TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA)
    F{{sslVerifyClient}} The type of client certificate verification
    F{{shareSession}} Enable/disable new SSL session creation
    F{{ocspStapling}} Enable/disable OCSP stapling
}
public type ServiceSecureSocket {
    TrustStore? trustStore,
    KeyStore? keyStore,
    Protocols? protocol,
    ValidateCert? certValidation,
    string[] ciphers,
    string sslVerifyClient,
    boolean shareSession = true,
    ServiceOcspStapling? ocspStapling,
};

documentation {
    Defines the possible values for the keep-alive configuration in service and client endpoints.
}
public type KeepAlive "AUTO"|"ALWAYS"|"NEVER";

documentation { Decides to keep the connection alive or not based on the `connection` header of the client request }
@final public KeepAlive KEEPALIVE_AUTO = "AUTO";
documentation { Keeps the connection alive irrespective of the `connection` header value }
@final public KeepAlive KEEPALIVE_ALWAYS = "ALWAYS";
documentation { Closes the connection irrespective of the `connection` header value }
@final public KeepAlive KEEPALIVE_NEVER = "NEVER";

public function Listener::init (ServiceEndpointConfiguration config) {
    self.config = config;
    var err = self.initEndpoint();
    if (err != null) {
        throw err;
    }
}

//////////////////////////////////
/// WebSocket Service Endpoint ///
//////////////////////////////////
documentation {
    Represents a WebSocket service endpoint.

    F{{id}} The connection ID
    F{{negotiatedSubProtocol}} The subprotocols negotiated with the client
    F{{isSecure}} `true` if the connection is secure
    F{{isOpen}} `true` if the connection is open
    F{{attributes}} A `map` to store connection related attributes
}
public type WebSocketListener object {
    public {
        @readonly string id;
        @readonly string negotiatedSubProtocol;
        @readonly boolean isSecure;
        @readonly boolean isOpen;
        @readonly map attributes;
    }

    private {
        WebSocketConnector conn;
        ServiceEndpointConfiguration config;
        Listener httpEndpoint;
    }

    public new() {
    }

    documentation {
        Gets invoked during package initialization to initialize the endpoint.

        P{{config}} The `ServiceEndpointConfiguration` of the endpoint
    }
    public function init(ServiceEndpointConfiguration config) {
        self.config = config;
        httpEndpoint.init(config);
    }

    documentation {
        Gets invoked when binding a service to the endpoint.

        P{{serviceType}} The service type
    }
    public function register(typedesc serviceType) {
        httpEndpoint.register(serviceType);
    }

    documentation {
        Starts the registered service.
    }
    public function start() {
        httpEndpoint.start();
    }

    documentation {
        Returns a WebSocket actions provider which can be used to communicate with the remote host.

        R{{}} The connector that listener endpoint uses
    }
    public function getCallerActions() returns (WebSocketConnector) {
        return conn;
    }

    documentation {
        Stops the registered service.
    }
    public function stop() {
        httpEndpoint.stop();
    }
};
