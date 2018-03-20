/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.net.http;

/**
 * Constants for HTTP.
 *
 * @since 0.8.0
 */
public class HttpConstants {

    public static final String BASE_PATH = "BASE_PATH";
    public static final String SUB_PATH = "SUB_PATH";
    public static final String EXTRA_PATH_INFO = "EXTRA_PATH_INFO";
    public static final String RAW_URI = "RAW_URI";
    public static final String RESOURCE_ARGS = "RESOURCE_ARGS";
    public static final String MATRIX_PARAMS = "MATRIX_PARAMS";
    public static final String QUERY_STR = "QUERY_STR";
    public static final String RAW_QUERY_STR = "RAW_QUERY_STR";

    public static final String DEFAULT_INTERFACE = "0.0.0.0:8080";
    public static final String DEFAULT_BASE_PATH = "/";
    public static final String DEFAULT_SUB_PATH = "/*";

    public static final String PROTOCOL_HTTP = "http";
    public static final String PROTOCOL_PACKAGE_HTTP = "ballerina.net.http";
    public static final String HTTP_SERVICE_ENDPOINT_NAME = "ballerina.net.http:ServiceEndpoint";
    public static final String PROTOCOL_HTTPS = "https";
    public static final String HTTP_METHOD = "HTTP_METHOD";
    public static final String HTTP_STATUS_CODE = "HTTP_STATUS_CODE";
    public static final String HTTP_REASON_PHRASE = "HTTP_REASON_PHRASE";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String PROTOCOL = "PROTOCOL";
    public static final String TO = "TO";
    public static final String LOCAL_ADDRESS = "LOCAL_ADDRESS";
    public static final String HTTP_VERSION = "HTTP_VERSION";
    public static final String LISTENER_PORT = "LISTENER_PORT";
    public static final String HTTP_DEFAULT_HOST = "0.0.0.0";
    public static final String TLS_STORE_TYPE = "tlsStoreType";
    public static final String PKCS_STORE_TYPE = "PKCS12";
    public static final String AUTO = "AUTO";
    public static final String ALWAYS = "ALWAYS";
    public static final String NEVER = "NEVER";
    public static final String FORWARDED_ENABLE = "enable";
    public static final String FORWARDED_TRANSITION = "transition";
    public static final String FORWARDED_DISABLE = "disable";

    public static final String HTTP_PACKAGE_PATH = "ballerina.net.http";

    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_PUT = "PUT";
    public static final String HTTP_METHOD_PATCH = "PATCH";
    public static final String HTTP_METHOD_DELETE = "DELETE";
    public static final String HTTP_METHOD_OPTIONS = "OPTIONS";
    public static final String HTTP_METHOD_HEAD = "HEAD";

    /* Annotations */
    public static final String ANN_NAME_RESOURCE_CONFIG = "ResourceConfig";
    public static final String ANN_RESOURCE_ATTR_METHODS = "methods";
    public static final String ANN_RESOURCE_ATTR_PATH = "path";
    public static final String ANN_RESOURCE_ATTR_BODY = "body";
    public static final String ANN_RESOURCE_ATTR_CONSUMES = "consumes";
    public static final String ANN_RESOURCE_ATTR_PRODUCES = "produces";
    public static final String ANN_NAME_CONFIG = "configuration";
    public static final String ANN_NAME_HTTP_SERVICE_CONFIG = "ServiceConfig";
    public static final String ANN_CONFIG_ATTR_HOST = "host";
    public static final String ANN_CONFIG_ATTR_PORT = "port";
    public static final String ANN_CONFIG_ATTR_HTTP_VERSION = "httpVersion";
    public static final String ANN_CONFIG_ATTR_HTTPS_PORT = "httpsPort";
    public static final String ANN_CONFIG_ATTR_KEEP_ALIVE = "keepAlive";
    public static final String ANN_CONFIG_ATTR_BASE_PATH = "basePath";
    public static final String ANN_CONFIG_ATTR_SCHEME = "scheme";
    public static final String ANN_CONFIG_ATTR_TLS_STORE_TYPE = "tlsStoreType";
    public static final String ANN_CONFIG_ATTR_KEY_STORE_FILE = "keyStoreFile";
    public static final String ANN_CONFIG_ATTR_KEY_STORE_PASS = "keyStorePassword";
    public static final String ANN_CONFIG_ATTR_TRUST_STORE_FILE = "trustStoreFile";
    public static final String ANN_CONFIG_ATTR_TRUST_STORE_PASS = "trustStorePassword";
    public static final String ANN_CONFIG_ATTR_CERT_PASS = "certPassword";
    public static final String ANN_CONFIG_ATTR_SSL_VERIFY_CLIENT = "sslVerifyClient";
    public static final String ANN_CONFIG_ATTR_SSL_ENABLED_PROTOCOLS = "sslEnabledProtocols";
    public static final String ANN_CONFIG_ATTR_CIPHERS = "ciphers";
    public static final String ANN_CONFIG_ATTR_SSL_PROTOCOL = "sslProtocol";
    public static final String ANN_CONFIG_ATTR_VALIDATE_CERT_ENABLED = "validateCertEnabled";
    public static final String ANN_CONFIG_ATTR_COMPRESSION = "compression";
    public static final String ANN_CONFIG_ATTR_CACHE_SIZE = "cacheSize";
    public static final String ANN_CONFIG_ATTR_CACHE_VALIDITY_PERIOD = "cacheValidityPeriod";
    public static final String ANN_CONFIG_ATTR_WEBSOCKET = "webSocket";
    public static final String ANN_CONFIG_ATTR_TRANSFER_ENCODING = "transferEncoding";
    public static final String ANN_CONFIG_ATTR_MAXIMUM_URL_LENGTH = "maxUriLength";
    public static final String ANN_CONFIG_ATTR_MAXIMUM_HEADER_SIZE = "maxHeaderSize";
    public static final String ANN_CONFIG_ATTR_MAXIMUM_ENTITY_BODY_SIZE = "maxEntityBodySize";
    public static final String ANN_CONFIG_ATTR_CHUNKING = "chunking";
    public static final String ANN_WEBSOCKET_ATTR_UPGRADE_PATH = "upgradePath";;
    public static final String ANNOTATION_METHOD_GET = HTTP_METHOD_GET;
    public static final String ANNOTATION_METHOD_POST = HTTP_METHOD_POST;
    public static final String ANNOTATION_METHOD_PUT = HTTP_METHOD_PUT;
    public static final String ANNOTATION_METHOD_PATCH = HTTP_METHOD_PATCH;
    public static final String ANNOTATION_METHOD_DELETE = HTTP_METHOD_DELETE;
    public static final String ANNOTATION_METHOD_OPTIONS = HTTP_METHOD_OPTIONS;

    public static final String ANNOTATION_SOURCE_KEY_INTERFACE = "interface";
    public static final String VALUE_ATTRIBUTE = "value";

    public static final String COOKIE_HEADER = "Cookie";
    public static final String SESSION_ID = "BSESSIONID=";
    public static final String PATH = "Path=";
    public static final String RESPONSE_COOKIE_HEADER = "Set-Cookie";
    public static final String SESSION = "Session";
    public static final String HTTP_ONLY = "HttpOnly";
    public static final String SECURE = "Secure";

    public static final String ALLOW_ORIGIN = "allowOrigins";
    public static final String ALLOW_CREDENTIALS = "allowCredentials";
    public static final String ALLOW_METHODS = "allowMethods";
    public static final String MAX_AGE = "maxAge";
    public static final String ALLOW_HEADERS = "allowHeaders";
    public static final String EXPOSE_HEADERS = "exposeHeaders";
    public static final String PREFLIGHT_RESOURCES = "PREFLIGHT_RESOURCES";
    public static final String RESOURCES_CORS = "RESOURCES_CORS";
    public static final String LISTENER_INTERFACE_ID = "listener.interface.id";

    public static final String HTTP_CLIENT = "HttpClient";
    public static final String B_CONNECTOR = "BConnector";

    public static final String REQUEST_URL = "REQUEST_URL";
    public static final String SRC_HANDLER = "SRC_HANDLER";
    public static final String REMOTE_ADDRESS = "REMOTE_ADDRESS";
    public static final String ORIGIN_HOST = "ORIGIN_HOST";
    public static final String HTTP_SERVICE = "HTTP_SERVICE";

    /* Annotations */
    public static final String ANNOTATION_NAME_SOURCE = "Source";
    public static final String ANNOTATION_NAME_BASE_PATH = "BasePath";
    public static final String ANNOTATION_NAME_PATH = "Path";
    public static final String HTTP_CLIENT_EXCEPTION_CATEGORY = "http-client";
    public static final String SERVICE_ENDPOINT = "ServiceEndpoint";
    public static final String CONNECTION = "Connection";
    public static final String REQUEST = "Request";
    public static final String RESPONSE = "Response";
    public static final String HTTP_HANDLE = "HttpHandle";
    public static final String PUSH_PROMISE = "PushPromise";
    public static final String ENTITY = "Entity";
    public static final String HTTP_CONNECTOR_ERROR = "HttpConnectorError";
    public static final String HTTP_TIMEOUT_ERROR = "HttpTimeoutError";
    public static final String TYPE_STRING = "string";
    public static final String TRANSPORT_MESSAGE = "transport_message";
    public static final String TRANSPORT_HANDLE = "transport_handle";
    public static final String TRANSPORT_PUSH_PROMISE = "transport_push_promise";
    public static final String MESSAGE_OUTPUT_STREAM = "message_output_stream";
    public static final String HTTP_SESSION = "http_session";

    public static final String HTTP_TRANSPORT_CONF = "transports.netty.conf";
    public static final String CIPHERS = "ciphers";
    public static final String SSL_ENABLED_PROTOCOLS = "sslEnabledProtocols";
    public static final int OPTIONS_STRUCT_INDEX = 0;

    public static final int HTTP_MESSAGE_INDEX = 0;
    public static final int ENTITY_INDEX = 1;

    // ServeConnector struct indices
    public static final int SERVICE_ENDPOINT_CONNECTION_INDEX = 0;

    //Connection struct indexes
    public static final int CONNECTION_HOST_INDEX = 0;
    public static final int CONNECTION_PORT_INDEX = 0;

    //Request struct indexes
    public static final int REQUEST_RAW_PATH_INDEX = 0;
    public static final int REQUEST_METHOD_INDEX = 1;
    public static final int REQUEST_VERSION_INDEX = 2;
    public static final int REQUEST_USER_AGENT_INDEX = 3;
    public static final int REQUEST_EXTRA_PATH_INFO_INDEX = 4;

    //Response struct indexes
    public static final int RESPONSE_STATUS_CODE_INDEX = 0;
    public static final int RESPONSE_REASON_PHRASE_INDEX = 0;
    public static final int RESPONSE_SERVER_INDEX = 1;

    //PushPromise struct indexes
    public static final int PUSH_PROMISE_PATH_INDEX = 0;
    public static final int PUSH_PROMISE_METHOD_INDEX = 1;

    //Proxy server struct indexes
    public static final int PROXY_STRUCT_INDEX = 3;
    public static final int PROXY_HOST_INDEX = 0;
    public static final int PROXY_PORT_INDEX = 0;
    public static final int PROXY_USER_NAME_INDEX = 1;
    public static final int PROXY_PASSWORD_INDEX = 2;

    //Connection Throttling struct indexes
    public static final int CONNECTION_THROTTLING_STRUCT_INDEX = 4;
    public static final int CONNECTION_THROTTLING_MAX_ACTIVE_CONNECTIONS_INDEX = 0;
    public static final int CONNECTION_THROTTLING_WAIT_TIME_INDEX = 1;

    //Retry Struct indexes
    public static final int RETRY_STRUCT_INDEX = 4;
    public static final int RETRY_COUNT_INDEX = 0;
    public static final int RETRY_INTERVAL_INDEX = 1;

    public static final String CONNECTION_HEADER = "Connection";
    public static final String HEADER_VAL_CONNECTION_CLOSE = "Close";
    public static final String HEADER_VAL_CONNECTION_KEEP_ALIVE = "Keep-Alive";
    public static final String HEADER_VAL_100_CONTINUE = "100-continue";

    //Response codes
    public static final String HTTP_BAD_REQUEST = "400";
    public static final String HEADER_X_XID = "X-XID";
    public static final String HEADER_X_REGISTER_AT_URL = "X-Register-At-URL";


    public static final String HTTP_SERVER_CONNECTOR = "HTTP_SERVER_CONNECTOR";
    public static final String HTTP_SERVICE_REGISTRY = "HTTP_SERVICE_REGISTRY";
    public static final String WS_SERVICE_REGISTRY = "WS_SERVICE_REGISTRY";

    //Service Endpoint
    public static final int SERVICE_ENDPOINT_NAME_INDEX = 0;
    public static final String SERVICE_ENDPOINT_CONFIG = "config";

    //Service Endpoint Config
    public static final String ENDPOINT_CONFIG_HOST = "host";
    public static final String ENDPOINT_CONFIG_TRANSFER_ENCODING = "transferEncoding";
    public static final String ENDPOINT_CONFIG_PORT = "port";
    public static final String ENDPOINT_CONFIG_KEEP_ALIVE = "keepAlive";
    public static final String ENDPOINT_CONFIG_CHUNKING = "chunking";
    public static final String ENDPOINT_CONFIG_VERSION = "httpVersion";
    public static final String ENDPOINT_REQUEST_LIMITS = "requestLimits";
    public static final String REQUEST_LIMITS_MAXIMUM_URL_LENGTH = "maxUriLength";
    public static final String REQUEST_LIMITS_MAXIMUM_HEADER_SIZE = "maxHeaderSize";
    public static final String REQUEST_LIMITS_MAXIMUM_ENTITY_BODY_SIZE = "maxEntityBodySize";

    public static final String ENDPOINT_CONFIG_SECURE_SOCKET = "secureSocket";

    public static final String ENDPOINT_CONFIG_TRUST_STORE = "trustStore";
    public static final String FILE_PATH = "filePath";
    public static final String PASSWORD = "password";
    public static final String PROTOCOL_VERSION = "protocolName";
    public static final String ENABLED_PROTOCOLS = "versions";
    public static final String ENABLE = "enable";
    public static final String ENDPOINT_CONFIG_KEY_STORE = "keyStore";
    public static final String ENDPOINT_CONFIG_PROTOCOLS = "protocols";
    public static final String ENDPOINT_CONFIG_VALIDATE_CERT = "validateCert";

    //SslConfiguration indexes
    public static final String SSL_CONFIG_SSL_VERIFY_CLIENT = "sslVerifyClient";
    public static final String SSL_CONFIG_CIPHERS = "ciphers";
    public static final String SSL_CONFIG_CACHE_SIZE = "cacheSize";
    public static final String SSL_CONFIG_CACHE_VALIDITY_PERIOD = "cacheValidityPeriod";
    public static final String SSL_CONFIG_HOST_NAME_VERIFICATION_ENABLED = "hostNameVerification";
    public static final String SSL_CONFIG_ENABLE_SESSION_CREATION = "sessionCreation";

    //Client Endpoint
    public static final String CLIENT_ENDPOINT_CONFIG = "config";

    //Client Endpoint Config
    public static final String URI = "uri";
    public static final String CLIENT_EP_TRNASFER_ENCODING = "transferEncoding";
    public static final String CLIENT_EP_CHUNKING = "chunking";
    public static final String CLIENT_EP_ENDPOINT_TIMEOUT = "endpointTimeout";
    public static final String CLIENT_EP_IS_KEEP_ALIVE = "keepAlive";
    public static final String CLIENT_EP_HTTP_VERSION = "httpVersion";
    public static final String CLIENT_EP_FORWARDED = "forwarded";
    public static final String TARGET_SERVICES = "targets";

    //Connection Throttling Indexes
    public static final String CONNECTION_THROTTLING_STRUCT_REFERENCE = "connectionThrottling";
    public static final String CONNECTION_THROTTLING_MAX_ACTIVE_CONNECTIONS = "maxActiveConnections";
    public static final String CONNECTION_THROTTLING_WAIT_TIME = "waitTime";

    //FollowRedirect Indexes
    public static final String FOLLOW_REDIRECT_STRUCT_REFERENCE = "followRedirects";
    public static final String FOLLOW_REDIRECT_ENABLED = "enabled";
    public static final String FOLLOW_REDIRECT_MAXCOUNT = "maxCount";

    //Proxy Indexed
    public static final String PROXY_STRUCT_REFERENCE = "proxy";
    public static final String PROXY_HOST = "host";
    public static final String PROXY_PORT = "port";
    public static final String PROXY_USERNAME = "userName";
    public static final String PROXY_PASSWORD = "pasword";

    public static final String HTTP_SERVICE_TYPE = "Service";
    // Filter related
    public static final String ENDPOINT_CONFIG_FILTERS = "filters";
    public static final String FILTERS = "FILTERS";

    // Retry Config
    public static final String CLIENT_EP_RETRY = "retry";
    public static final String RETRY_COUNT = "count";
    public static final String RETRY_INTERVAL = "interval";
}
