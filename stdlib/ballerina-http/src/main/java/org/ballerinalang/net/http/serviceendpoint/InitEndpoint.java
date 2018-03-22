/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.net.http.serviceendpoint;

import org.apache.commons.lang3.StringUtils;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.connector.api.Value;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BFunctionPointer;
import org.ballerinalang.model.values.BRefType;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.net.http.HTTPServicesRegistry;
import org.ballerinalang.net.http.HttpConnectionManager;
import org.ballerinalang.net.http.HttpConstants;
import org.ballerinalang.net.http.HttpUtil;
import org.ballerinalang.net.http.WebSocketServicesRegistry;
import org.ballerinalang.util.codegen.cpentries.FunctionRefCPEntry;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.wso2.transport.http.netty.config.ListenerConfiguration;
import org.wso2.transport.http.netty.config.Parameter;
import org.wso2.transport.http.netty.config.RequestSizeValidationConfig;
import org.wso2.transport.http.netty.contract.ServerConnector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import static org.ballerinalang.runtime.Constants.BALLERINA_VERSION;

/**
 * Get the ID of the connection.
 *
 * @since 0.966
 */

@BallerinaFunction(
        orgName = "ballerina", packageName = "net.http",
        functionName = "initEndpoint",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = "ServiceEndpoint",
                             structPackage = "ballerina.net.http"),
        isPublic = true
)
public class InitEndpoint extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        try {
            Struct serviceEndpoint = BLangConnectorSPIUtil.getConnectorEndpointStruct(context);

            // Creating server connector
            Struct serviceEndpointConfig = serviceEndpoint.getStructField(HttpConstants.SERVICE_ENDPOINT_CONFIG);
            ListenerConfiguration listenerConfiguration = getListerConfig(serviceEndpointConfig);
            ServerConnector httpServerConnector =
                    HttpConnectionManager.getInstance().createHttpServerConnector(listenerConfiguration);
            serviceEndpoint.addNativeData(HttpConstants.HTTP_SERVER_CONNECTOR, httpServerConnector);

            //Adding service registries to native data
            WebSocketServicesRegistry webSocketServicesRegistry = new WebSocketServicesRegistry();
            HTTPServicesRegistry httpServicesRegistry = new HTTPServicesRegistry(webSocketServicesRegistry);
            serviceEndpoint.addNativeData(HttpConstants.HTTP_SERVICE_REGISTRY, httpServicesRegistry);
            serviceEndpoint.addNativeData(HttpConstants.WS_SERVICE_REGISTRY, webSocketServicesRegistry);
            // set filters
            setFilters(serviceEndpointConfig, serviceEndpoint);

            context.setReturnValues((BValue) null);
        } catch (Throwable throwable) {
            BStruct errorStruct = HttpUtil.getHttpConnectorError(context, throwable);
            context.setReturnValues(errorStruct);
        }

    }

    /**
     * Extract and attach the ordered set of filters to the service endpoint.
     * @param endpointConfig endpoint configuration
     * @param serviceEndpoint service endpoint object
     */
    private void setFilters (Struct endpointConfig, Struct serviceEndpoint) {
        Value[] filterValues = endpointConfig.getArrayField(HttpConstants.ENDPOINT_CONFIG_FILTERS);
        if (filterValues == null) {
            // no filters
            return;
        }
        HashSet<FilterHolder> filterFunctionSet = new LinkedHashSet<>();
        for (Value filterValue : filterValues) {
            filterFunctionSet.add(new FilterHolder(extractFilterFunction(filterValue.getVMValue(), 0),
                    extractFilterFunction(filterValue.getVMValue(), 1)));
        }

        serviceEndpoint.addNativeData(HttpConstants.FILTERS, filterFunctionSet);
    }

    private FunctionRefCPEntry extractFilterFunction(BValue functionValue, int refIndex) {
        if (functionValue == null) {
            return null;
        }
        BRefType bRefOnRequestFunction = ((BStruct) functionValue).getRefField(refIndex);
        if (bRefOnRequestFunction == null) {
            return null;
        }
        return ((BFunctionPointer) bRefOnRequestFunction).value();
    }

    private ListenerConfiguration getListerConfig(Struct endpointConfig) {
        String host = endpointConfig.getStringField(HttpConstants.ENDPOINT_CONFIG_HOST);
        long port = endpointConfig.getIntField(HttpConstants.ENDPOINT_CONFIG_PORT);
        String keepAlive = endpointConfig.getEnumField(HttpConstants.ENDPOINT_CONFIG_KEEP_ALIVE);
        String transferEncoding = endpointConfig.getEnumField(HttpConstants.ENDPOINT_CONFIG_TRANSFER_ENCODING);
        String chunking = endpointConfig.getEnumField(HttpConstants.ENDPOINT_CONFIG_CHUNKING);
        Struct sslConfig = endpointConfig.getStructField(HttpConstants.ENDPOINT_CONFIG_SECURE_SOCKET);
        String httpVersion = endpointConfig.getStringField(HttpConstants.ENDPOINT_CONFIG_VERSION);
        Struct requestLimits = endpointConfig.getStructField(HttpConstants.ENDPOINT_REQUEST_LIMITS);

        ListenerConfiguration listenerConfiguration = new ListenerConfiguration();

        if (host == null || host.isEmpty()) {
            listenerConfiguration.setHost(HttpConstants.HTTP_DEFAULT_HOST);
        } else {
            listenerConfiguration.setHost(host);
        }
        listenerConfiguration.setPort(Math.toIntExact(port));
        listenerConfiguration.setKeepAliveConfig(HttpUtil.getKeepAliveConfig(keepAlive));

        // For the moment we don't have to pass it down to transport as we only support
        // chunking. Once we start supporting gzip, deflate, etc, we need to parse down the config.
        if ((!transferEncoding.isEmpty()) && !HttpConstants.ANN_CONFIG_ATTR_CHUNKING
                .equalsIgnoreCase(transferEncoding)) {
            throw new BallerinaConnectorException("Unsupported configuration found for Transfer-Encoding : "
                                                          + transferEncoding);
        }

        listenerConfiguration.setChunkConfig(HttpUtil.getChunkConfig(chunking));

        // Set Request validation limits.
        if (requestLimits != null) {
            setRequestSizeValidationConfig(requestLimits, listenerConfiguration);
        }

        // Set HTTP version
        if (httpVersion != null) {
            listenerConfiguration.setVersion(httpVersion);
        }

        if (sslConfig != null) {
            return setSslConfig(sslConfig, listenerConfiguration);
        }

        listenerConfiguration.setServerHeader(getServerName());

        return listenerConfiguration;
    }

    private void setRequestSizeValidationConfig(Struct requestLimits, ListenerConfiguration listenerConfiguration) {
        long maxUriLength = requestLimits.getIntField(HttpConstants.REQUEST_LIMITS_MAXIMUM_URL_LENGTH);
        long maxHeaderSize = requestLimits.getIntField(HttpConstants.REQUEST_LIMITS_MAXIMUM_HEADER_SIZE);
        long maxEntityBodySize = requestLimits.getIntField(HttpConstants.REQUEST_LIMITS_MAXIMUM_ENTITY_BODY_SIZE);
        RequestSizeValidationConfig requestSizeValidationConfig = listenerConfiguration
                .getRequestSizeValidationConfig();

        if (maxUriLength != -1) {
            if (maxUriLength >= 0) {
                requestSizeValidationConfig.setMaxUriLength(Math.toIntExact(maxUriLength));
            } else {
                throw new BallerinaConnectorException("Invalid configuration found for maxUriLength : " + maxUriLength);
            }
        }

        if (maxHeaderSize != -1) {
            if (maxHeaderSize >= 0) {
                requestSizeValidationConfig.setMaxHeaderSize(Math.toIntExact(maxHeaderSize));
            } else {
                throw new BallerinaConnectorException(
                        "Invalid configuration found for maxHeaderSize : " + maxHeaderSize);
            }
        }

        if (maxEntityBodySize != -1) {
            if (maxEntityBodySize >= 0) {
                requestSizeValidationConfig.setMaxEntityBodySize(Math.toIntExact(maxEntityBodySize));
            } else {
                throw new BallerinaConnectorException(
                        "Invalid configuration found for maxEntityBodySize : " + maxEntityBodySize);
            }
        }
    }

    private String getServerName() {
        String userAgent;
        String version = System.getProperty(BALLERINA_VERSION);
        if (version != null) {
            userAgent = "ballerina/" + version;
        } else {
            userAgent = "ballerina";
        }
        return userAgent;
    }

    private ListenerConfiguration setSslConfig(Struct sslConfig, ListenerConfiguration listenerConfiguration) {
        listenerConfiguration.setScheme(HttpConstants.PROTOCOL_HTTPS);
        Struct trustStore = sslConfig.getStructField(HttpConstants.ENDPOINT_CONFIG_TRUST_STORE);
        Struct keyStore = sslConfig.getStructField(HttpConstants.ENDPOINT_CONFIG_KEY_STORE);
        Struct protocols = sslConfig.getStructField(HttpConstants.ENDPOINT_CONFIG_PROTOCOLS);
        Struct validateCert = sslConfig.getStructField(HttpConstants.ENDPOINT_CONFIG_VALIDATE_CERT);

        if (keyStore != null) {
            String keyStoreFile = keyStore.getStringField(HttpConstants.FILE_PATH);
            String keyStorePassword = keyStore.getStringField(HttpConstants.PASSWORD);
            if (StringUtils.isBlank(keyStoreFile)) {
                //TODO get from language pack, and add location
                throw new BallerinaConnectorException("Keystore location must be provided for secure connection");
            }
            if (StringUtils.isBlank(keyStorePassword)) {
                //TODO get from language pack, and add location
                throw new BallerinaConnectorException("Keystore password value must be provided for secure connection");
            }
            listenerConfiguration.setKeyStoreFile(keyStoreFile);
            listenerConfiguration.setKeyStorePass(keyStorePassword);
        }
        String sslVerifyClient = sslConfig.getStringField(HttpConstants.SSL_CONFIG_SSL_VERIFY_CLIENT);
        listenerConfiguration.setVerifyClient(sslVerifyClient);
        if (trustStore != null) {
            String trustStoreFile = trustStore.getStringField(HttpConstants.FILE_PATH);
            String trustStorePassword = trustStore.getStringField(HttpConstants.PASSWORD);
            if (StringUtils.isBlank(trustStoreFile) && StringUtils.isNotBlank(sslVerifyClient)) {
                //TODO get from language pack, and add location
                throw new BallerinaException("Truststore location must be provided to enable Mutual SSL");
            }
            if (StringUtils.isBlank(trustStorePassword) && StringUtils.isNotBlank(sslVerifyClient)) {
                //TODO get from language pack, and add location
                throw new BallerinaException("Truststore password value must be provided to enable Mutual SSL");
            }
            listenerConfiguration.setTrustStoreFile(trustStoreFile);
            listenerConfiguration.setTrustStorePass(trustStorePassword);
        }
        List<Parameter> serverParamList = new ArrayList<>();
        Parameter serverParameters;
        if (protocols != null) {
            String sslEnabledProtocols = protocols.getStringField(HttpConstants.ENABLED_PROTOCOLS);
            String sslProtocol = protocols.getStringField(HttpConstants.PROTOCOL_VERSION);
            if (StringUtils.isNotBlank(sslEnabledProtocols)) {
                serverParameters = new Parameter(HttpConstants.ANN_CONFIG_ATTR_SSL_ENABLED_PROTOCOLS,
                        sslEnabledProtocols);
                serverParamList.add(serverParameters);
            }
            if (StringUtils.isNotBlank(sslProtocol)) {
                listenerConfiguration.setSSLProtocol(sslProtocol);
            }
        }

        String cipher = sslConfig.getStringField(HttpConstants.SSL_CONFIG_CIPHERS);
        if (StringUtils.isNotBlank(cipher)) {
            serverParameters = new Parameter(HttpConstants.ANN_CONFIG_ATTR_CIPHERS, cipher);
            serverParamList.add(serverParameters);
        }
        if (validateCert != null) {
            boolean validateCertificateEnabled = validateCert.getBooleanField(HttpConstants.ENABLE);
            long cacheSize = validateCert.getIntField(HttpConstants.SSL_CONFIG_CACHE_SIZE);
            long cacheValidationPeriod = validateCert.getIntField(HttpConstants.SSL_CONFIG_CACHE_VALIDITY_PERIOD);
            listenerConfiguration.setValidateCertEnabled(validateCertificateEnabled);
            if (validateCertificateEnabled) {
                if (cacheSize != 0) {
                    listenerConfiguration.setCacheSize(Math.toIntExact(cacheSize));
                }
                if (cacheValidationPeriod != 0) {
                    listenerConfiguration.setCacheValidityPeriod(Math.toIntExact(cacheValidationPeriod));
                }
            }
        }
        listenerConfiguration.setTLSStoreType(HttpConstants.PKCS_STORE_TYPE);
        String serverEnableSessionCreation = String.valueOf(sslConfig
                .getBooleanField(HttpConstants.SSL_CONFIG_ENABLE_SESSION_CREATION));
        Parameter enableSessionCreationParam = new Parameter(HttpConstants.SSL_CONFIG_ENABLE_SESSION_CREATION,
                serverEnableSessionCreation);
        serverParamList.add(enableSessionCreationParam);
        if (!serverParamList.isEmpty()) {
            listenerConfiguration.setParameters(serverParamList);
        }

        listenerConfiguration
                .setId(HttpUtil.getListenerInterface(listenerConfiguration.getHost(), listenerConfiguration.getPort()));

        return listenerConfiguration;
    }
}
