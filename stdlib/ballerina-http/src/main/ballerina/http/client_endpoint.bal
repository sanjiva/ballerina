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

import ballerina/io;
////////////////////////////////
///// HTTP Client Endpoint /////
////////////////////////////////

@Description {value:"Represents an HTTP client endpoint"}
@Field {value:"epName: The name of the endpoint"}
@Field {value:"config: The configurations associated with the endpoint"}
public type ClientEndpoint object {
    public {
        string epName;
        ClientEndpointConfiguration config;
        HttpClient httpClient;
    }

    @Description {value:"Gets called when the endpoint is being initialized during the package initialization."}
    @Param {value:"ep: The endpoint to be initialized"}
    @Param {value:"epName: The endpoint name"}
    @Param {value:"config: The ClientEndpointConfiguration of the endpoint"}
    public function init(ClientEndpointConfiguration config);

    public function register(typedesc serviceType) {
    }

    public function start() {
    }

    @Description { value:"Returns the connector that client code uses"}
    @Return { value:"The connector that client code uses" }
    public function getClient() returns HttpClient {
        return self.httpClient;
    }

    @Description { value:"Stops the registered service"}
    @Return { value:"Error occured during registration" }
    public function stop() {
    }
};

public type Algorithm "NONE" | "LOAD_BALANCE" | "FAIL_OVER";

@Description {value:"Represents the configurations applied to a particular service."}
@Field {value:"url: Target service URI"}
@Field {value:"secureSocket: SSL/TLS related options"}
public type TargetService {
    string url,
    SecureSocket? secureSocket,
};

@Description { value:"ClientEndpointConfiguration struct represents options to be used for HTTP client invocation" }
@Field {value:"circuitBreaker: Circuit Breaker configuration"}
@Field {value:"endpointTimeout: Endpoint timeout value in millisecond"}
@Field {value:"keepAlive: Specifies whether to reuse a connection for multiple requests"}
@Field {value:"transferEncoding: The types of encoding applied to the request"}
@Field {value:"chunking: The chunking behaviour of the request"}
@Field {value:"httpVersion: The HTTP version understood by the client"}
@Field {value:"forwarded: The choice of setting forwarded/x-forwarded header"}
@Field {value:"followRedirects: Redirect related options"}
@Field {value:"retry: Retry related options"}
@Field {value:"proxy: Proxy server related options"}
@Field {value:"connectionThrottling: Configurations for connection throttling"}
@Field {value:"targets: Service(s) accessible through the endpoint. Multiple services can be specified here when using techniques such as load balancing and fail over."}
@Field {value:"algorithm: The algorithm to be used for load balancing. The HTTP package provides 'roundRobin()' by default."}
@Field {value:"failoverConfig: Failover configuration"}
@Field {value:"cacheConfig: HTTP caching related configurations"}
@Field {value:"acceptEncoding: Specifies the way of handling accept-encoding header."}
public type ClientEndpointConfiguration {
    CircuitBreakerConfig? circuitBreaker,
    int endpointTimeout = 60000,
    boolean keepAlive = true,
    TransferEncoding transferEncoding = "CHUNKING",
    Chunking chunking = "AUTO",
    string httpVersion = "1.1",
    string forwarded = "disable",
    FollowRedirects? followRedirects,
    Retry? retry,
    Proxy? proxy,
    ConnectionThrottling? connectionThrottling,
    TargetService[] targets,
    string|FailoverConfig lbMode = ROUND_ROBIN,
    CacheConfig cacheConfig,
    string acceptEncoding = "auto",
};

public native function createHttpClient(string uri, ClientEndpointConfiguration config) returns HttpClient;

@Description { value:"Retry struct represents retry related options for HTTP client invocation" }
@Field {value:"count: Number of retry attempts before giving up"}
@Field {value:"interval: Retry interval in milliseconds"}
@Field {value:"backOffFactor: Multiplier of the retry interval to exponentailly increase retry interval"}
@Field {value:"maxWaitInterval: Maximum time of the retry interval in milliseconds"}
public type Retry {
    int count,
    int interval,
    float backOffFactor,
    int maxWaitInterval,
};

@Description { value:"SecureSocket struct represents SSL/TLS options to be used for HTTP client invocation" }
@Field {value: "trustStore: TrustStore related options"}
@Field {value: "keyStore: KeyStore related options"}
@Field {value: "protocols: SSL/TLS protocol related options"}
@Field {value: "validateCert: Certificate validation against CRL or OCSP related options"}
@Field {value:"ciphers: List of ciphers to be used. eg: TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA"}
@Field {value:"hostNameVerification: Enable/disable host name verification"}
@Field {value:"sessionCreation: Enable/disable new ssl session creation"}
@Field {value:"ocspStapling: Enable/disable ocsp stapling"}
public type SecureSocket {
    TrustStore? trustStore,
    KeyStore? keyStore,
    Protocols? protocols,
    ValidateCert? validateCert,
    string ciphers,
    boolean hostNameVerification = true,
    boolean sessionCreation = true,
    boolean ocspStapling,
};

@Description { value:"FollowRedirects struct represents HTTP redirect related options to be used for HTTP client invocation" }
@Field {value:"enabled: Enable redirect"}
@Field {value:"maxCount: Maximun number of redirects to follow"}
public type FollowRedirects {
    boolean enabled = false,
    int maxCount = 5,
};

@Description { value:"Proxy struct represents proxy server configurations to be used for HTTP client invocation" }
@Field {value:"proxyHost: host name of the proxy server"}
@Field {value:"proxyPort: proxy server port"}
@Field {value:"proxyUserName: Proxy server user name"}
@Field {value:"proxyPassword: proxy server password"}
public type Proxy {
    string host,
    int port,
    string userName,
    string password,
};

@Description { value:"This struct represents the options to be used for connection throttling" }
@Field {value:"maxActiveConnections: Number of maximum active connections for connection throttling. Default value -1, indicates the number of connections are not restricted"}
@Field {value:"waitTime: Maximum waiting time for a request to grab an idle connection from the client connector"}
public type ConnectionThrottling {
    int maxActiveConnections = -1,
    int waitTime = 60000,
};

public function ClientEndpoint::init(ClientEndpointConfiguration config) {
    boolean httpClientRequired = false;
    string url = config.targets[0].url;
    match config.lbMode {
        FailoverConfig failoverConfig => {
            if (lengthof config.targets > 1) {
                self.config = config;
                self.httpClient = createFailOverClient(config, failoverConfig);
            } else {
                if (url.hasSuffix("/")) {
                    int lastIndex = url.length() - 1;
                    url = url.subString(0, lastIndex);
                }
                self.config = config;

                if (config.cacheConfig.enabled) {
                    self.httpClient = createHttpCachingClient(url, config, config.cacheConfig);
                } else{
                    self.httpClient = createHttpClient(url, config);
                }
            }
        }

        string lbAlgorithm => {
            if (lengthof config.targets > 1) {
                self.httpClient = createLoadBalancerClient(config, lbAlgorithm);
            } else {
                if (url.hasSuffix("/")) {
                    int lastIndex = url.length() - 1;
                    url = url.subString(0, lastIndex);
                }
                self.config = config;
                var cbConfig = config.circuitBreaker;
                match cbConfig {
                    CircuitBreakerConfig cb => {
                        if (url.hasSuffix("/")) {
                            int lastIndex = url.length() - 1;
                            url = url.subString(0, lastIndex);
                        }
                        httpClientRequired = false;
                    }
                    () => {
                        httpClientRequired = true;
                    }
                }
                if (httpClientRequired) {
                    var retryConfig = config.retry;
                    match retryConfig {
                        Retry retry => {
                            self.httpClient = createRetryClient(url, config);
                        }
                        () => {
                            if (config.cacheConfig.enabled) {
                                self.httpClient = createHttpCachingClient(url, config, config.cacheConfig);
                            } else{
                                self.httpClient = createHttpClient(url, config);
                            }
                        }
                    }
                } else {
                    self.httpClient = createCircuitBreakerClient(url, config);
                }
            }
        }
    }
}

function createCircuitBreakerClient (string uri, ClientEndpointConfiguration configuration) returns HttpClient {
    var cbConfig = configuration.circuitBreaker;
    match cbConfig {
        CircuitBreakerConfig cb => {
            validateCircuitBreakerConfiguration(cb);
            boolean [] statusCodes = populateErrorCodeIndex(cb.statusCodes);
            HttpClient cbHttpClient = {};
            var retryConfig = configuration.retry;
            match retryConfig {
                Retry retry => {
                    cbHttpClient = createRetryClient(uri, configuration);
                }
                () => {
                    if (configuration.cacheConfig.enabled) {
                        cbHttpClient = createHttpCachingClient(uri, configuration, configuration.cacheConfig);
                    } else{
                        cbHttpClient = createHttpClient(uri, configuration);
                    }
                }
            }

            time:Time circuitStartTime = time:currentTime();
            int numberOfBuckets = (cb.rollingWindow.timeWindow / cb.rollingWindow.bucketSize);
            Bucket[] bucketArray = [];
            int bucketIndex = 0;
            while (bucketIndex < numberOfBuckets) {
                bucketArray[bucketIndex] = {};
                bucketIndex = bucketIndex + 1;
            }

            CircuitBreakerInferredConfig circuitBreakerInferredConfig = {
                                                                failureThreshold:cb.failureThreshold,
                                                                resetTimeout:cb.resetTimeout,
                                                                statusCodes:statusCodes,
                                                                noOfBuckets:numberOfBuckets,
                                                                rollingWindow:cb.rollingWindow
                                                            };

            CircuitBreakerClient cbClient = {
                                                serviceUri:uri,
                                                config:configuration,
                                                circuitBreakerInferredConfig:circuitBreakerInferredConfig,
                                                httpClient:cbHttpClient,
                                                circuitHealth:{startTime:circuitStartTime, totalBuckets: bucketArray},
                                                currentCircuitState:CircuitState.CLOSED
                                            };
            HttpClient httpClient =  cbClient;
            return httpClient;
        }
        () => {
            //remove following once we can ignore
            if (configuration.cacheConfig.enabled) {
                return createHttpCachingClient(uri, configuration, configuration.cacheConfig);
            } else {
                return createHttpClient(uri, configuration);
            }
        }
    }
}

function createLoadBalancerClient(ClientEndpointConfiguration config, string lbAlgorithm) returns HttpClient {
    HttpClient[] lbClients = createHttpClientArray(config);

    LoadBalancer lb = {
                        serviceUri: config.targets[0].url,
                        config: config,
                        loadBalanceClientsArray: lbClients,
                        algorithm: lbAlgorithm
                      };
    HttpClient lbClient = lb;
    return lbClient;
}

public function createFailOverClient(ClientEndpointConfiguration config, FailoverConfig foConfig) returns HttpClient {
        HttpClient[] clients = createHttpClientArray(config);

        boolean[] failoverCodes = populateErrorCodeIndex(foConfig.failoverCodes);
        FailoverInferredConfig failoverInferredConfig = {failoverClientsArray : clients,
                                                            failoverCodesIndex : failoverCodes,
                                                            failoverInterval : foConfig.interval};

        Failover failover = {serviceUri:config.targets[0].url, config:config,
                                failoverInferredConfig:failoverInferredConfig};
        HttpClient foClient = failover;
        return foClient;
}

function createRetryClient (string uri, ClientEndpointConfiguration configuration) returns HttpClient {
    var retryConfig = configuration.retry;
    match retryConfig {
        Retry retry => {
            HttpClient retryHttpClient = {};
            if (configuration.cacheConfig.enabled) {
                retryHttpClient = createHttpCachingClient(uri, configuration, configuration.cacheConfig);
            } else{
                retryHttpClient = createHttpClient(uri, configuration);
            }
            RetryClient retryClient = {
                serviceUri:uri,
                config:configuration,
                retry: retry,
                httpClient: retryHttpClient
            };
            HttpClient httpClient =  retryClient;
            return httpClient;
        }
        () => {
            //remove following once we can ignore
            if (configuration.cacheConfig.enabled) {
                return createHttpCachingClient(uri, configuration, configuration.cacheConfig);
            } else {
                return createHttpClient(uri, configuration);
            }
        }
    }
}
