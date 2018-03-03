package ballerina.net.http;

@Description {value:"Configuration for HTTP service"}
@Field {value:"host: Host of the service"}
@Field {value:"port: Port number of the service"}
@Field {value:"httpsPort: HTTPS port number of service"}
@Field {value:"basePath: Service base path"}
@Field {value:"keyStoreFile: File path to keystore file"}
@Field {value:"keyStorePassword: The keystore password"}
@Field {value:"trustStoreFile: File path to truststore file"}
@Field {value:"trustStorePassword: The truststore password"}
@Field {value:"sslVerifyClient: The type of client certificate verification"}
@Field {value:"certPassword: The certificate password"}
@Field {value:"sslEnabledProtocols: SSL/TLS protocols to be enabled"}
@Field {value:"ciphers: List of ciphers to be used"}
@Field {value:"sslProtocol: The SSL protocol version"}
@Field {value:"validateCertEnabled: The status of validateCertEnabled {default value : false (disable)}"}
@Field {value:"compressionEnabled: The status of compressionEnabled {default value : true (enabled)}"}
@Field {value:"cacheSize: Maximum size of the cache"}
@Field {value:"cacheValidityPeriod: Time duration of cache validity period"}
@Field {value:"allowOrigins: The array of origins with which the response is shared by the service"}
@Field {value:"allowCredentials: Specifies whether credentials are required to access the service"}
@Field {value:"allowMethods: The array of allowed methods by the service"}
@Field {value:"allowHeaders: The array of allowed headers by the service"}
@Field {value:"maxAge: The maximum duration to cache the preflight from client side"}
@Field {value:"exposeHeaders: The array of allowed headers which are exposed to the client"}
@Field {value:"keepAlive: The keepAlive behaviour of the connection for a particular port"}
@Field {value:"transferEncoding: The types of encoding applied to the response"}
@Field {value:"chunking: The chunking behaviour of the response"}
@Field {value:"maxUriLength: Maximum length allowed for the URL"}
@Field {value:"maxHeaderSize: Maximum size allowed for the headers"}
@Field {value:"maxEntityBodySize: Maximum size allowed for the entity body"}
@Field {value:"webSocket: Annotation to define HTTP to WebSocket upgrade"}
public annotation configuration attach service<> {
    string host;
    int port;
    int httpsPort;
    string basePath;
    string keyStoreFile;
    string keyStorePassword;
    string trustStoreFile;
    string trustStorePassword;
    string sslVerifyClient;
    string certPassword;
    string sslEnabledProtocols;
    string ciphers;
    string sslProtocol;
    boolean validateCertEnabled;
    boolean compressionEnabled;
    int cacheSize;
    int cacheValidityPeriod;
    string[] allowOrigins;
    boolean allowCredentials;
    string[] allowMethods;
    string[] allowHeaders;
    int maxAge;
    string[] exposeHeaders;
    string keepAlive;
    string transferEncoding;
    string chunking;
    int maxUriLength;
    int maxHeaderSize;
    int maxEntityBodySize;
    webSocket webSocket;
}

@Description {value: "Annotation to upgrade connection from HTTP to WS in the same base path."}
@Field {value:"upgradePath: Upgrade path for the WebSocket service from HTTP to WS."}
@Field {value:"serviceName: Name of the WebSocket service where the HTTP service should upgrade to."}
public annotation webSocket attach service<> {
    string upgradePath;
    string serviceName;
}

@Description {value:"Configuration for HTTP resource"}
@Field {value:"methods: The array of allowed HTTP methods"}
@Field {value:"path: The path of resource"}
@Field {value:"body: Inbound request entity body name which declared in signature"}
@Field {value:"consumes: The media types which are accepted by resource"}
@Field {value:"produces: The media types which are produced by resource"}
@Field {value:"allowOrigins: The array of origins with which the response is shared by the resource"}
@Field {value:"allowCredentials: Specifies whether credentials are required to access the resource"}
@Field {value:"allowMethods: The array of allowed methods by the resource"}
@Field {value:"allowHeaders: The array of allowed headers by the resource"}
@Field {value:"maxAge: The duration to cache the preflight from client side"}
@Field {value:"exposeHeaders: The array of allowed headers which are exposed to the client"}
public annotation resourceConfig attach resource {
    string[] methods;
    string path;
    string body;
    string[] consumes;
    string[] produces;
    string[] allowOrigins;
    boolean allowCredentials;
    string[] allowMethods;
    string[] allowHeaders;
    int maxAge;
    string[] exposeHeaders;
}