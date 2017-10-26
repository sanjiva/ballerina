/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.net.ws;

import org.ballerinalang.connector.api.AnnAttrValue;
import org.ballerinalang.connector.api.Annotation;
import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.net.http.HttpConnectionManager;
import org.ballerinalang.net.http.HttpUtil;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.transport.http.netty.config.ListenerConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Store all the WebSocket serviceEndpoints here.
 */
public class WebSocketServicesRegistry {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServicesRegistry.class);
    private static final WebSocketServicesRegistry REGISTRY = new WebSocketServicesRegistry();

    // Map<interface, Map<uri, service>>
    private final Map<String, Map<String, WebSocketService>> serviceEndpoints = new ConcurrentHashMap<>();
    // Map<clientServiceName, ClientService>
    private final Map<String, WebSocketService> clientServices = new ConcurrentHashMap<>();

    private WebSocketServicesRegistry() {
    }

    public static WebSocketServicesRegistry getInstance() {
        return REGISTRY;
    }

    /**
     * Register the service. Check for WebSocket upgrade path and client service.
     *
     * @param service service to register.
     */
    public void registerService(WebSocketService service) {
        if (WebSocketServiceValidator.isWebSocketClientService(service)
                && WebSocketServiceValidator.validateClientService(service)) {
            registerClientService(service);
        } else {
            if (WebSocketServiceValidator.validateServiceEndpoint(service)) {
                String upgradePath = findFullWebSocketUpgradePath(service);
                Annotation configAnnotation =
                        service.getAnnotation(Constants.WEBSOCKET_PACKAGE_NAME, Constants.ANNOTATION_CONFIGURATION);
                Set<ListenerConfiguration> listenerConfigurationSet =
                        HttpUtil.getDefaultOrDynamicListenerConfig(configAnnotation);

                for (ListenerConfiguration listenerConfiguration : listenerConfigurationSet) {
                    String entryListenerInterface =
                            listenerConfiguration.getHost() + ":" + listenerConfiguration.getPort();
                    Map<String, WebSocketService> servicesOnInterface = serviceEndpoints
                            .computeIfAbsent(entryListenerInterface, k -> new HashMap<>());

                    HttpConnectionManager.getInstance().createHttpServerConnector(listenerConfiguration);
                    // Assumption : this is always sequential, no two simultaneous calls can get here
                    if (servicesOnInterface.containsKey(upgradePath)) {
                        throw new BallerinaConnectorException(
                                "service with base path :" + upgradePath + " already exists in listener : "
                                        + entryListenerInterface);
                    }
                    servicesOnInterface.put(upgradePath, service);
                }

                logger.info("Service deployed : " + service.getName() + " with context " + upgradePath);
            }
        }
    }

    /**
     * Register a service as a client service.
     *
     * @param clientService {@link WebSocketService} of the client service.
     */
    private void registerClientService(WebSocketService clientService) {
        if (clientServices.containsKey(clientService.getName())) {
            throw new BallerinaException("Already contains a client service with name " + clientService.getName());
        } else {
            clientServices.put(clientService.getName(), clientService);
        }
    }

    /**
     * Unregister service for Map.
     *
     * @param service service to unregister.
     */
    public void unregisterService(WebSocketService service) {
        // TODO: Recorrect the logic behind unregistering service.
        String upgradePath = findFullWebSocketUpgradePath(service);
        String listenerInterface = getListenerInterface(service);
        if (serviceEndpoints.containsKey(listenerInterface)) {
            serviceEndpoints.get(listenerInterface).remove(upgradePath);
        }
    }

    /**
     * Find the best matching service.
     *
     * @param listenerInterface Listener interface of the the service.
     * @param uri uri of the service.
     * @return the service which matches.
     */
    public WebSocketService getServiceEndpoint(String listenerInterface, String uri) {
        return serviceEndpoints.get(listenerInterface).get(uri);
    }

    /**
     * Retrieve the client service by service name if exists.
     *
     * @param serviceName name of the service.
     * @return the service by service name if exists. Else return null.
     */
    public WebSocketService getClientService(String serviceName) {
        return clientServices.get(serviceName);
    }

    /**
     * Refactor the given URI.
     *
     * @param uri URI to refactor.
     * @return refactored URI.
     */
    public String refactorUri(String uri) {
        if (uri.startsWith("\"")) {
            uri = uri.substring(1, uri.length() - 1);
        }

        if (!uri.startsWith("/")) {
            uri = "/".concat(uri);
        }

        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        return uri;
    }

    /**
     * Find the Full path for WebSocket upgrade.
     *
     * @param service {@link WebSocketService} which the full path should be found.
     * @return the full path of the WebSocket upgrade.
     */
    private String findFullWebSocketUpgradePath(WebSocketService service) {
        // Find Base path for WebSocket

        Annotation configAnnotation = service.getAnnotation(Constants.WEBSOCKET_PACKAGE_NAME,
                Constants.ANN_NAME_CONFIG);
        String serviceName = service.getName();
        String basePath;
        if (configAnnotation != null) {
            AnnAttrValue annotationAttributeBasePathValue = configAnnotation.getAnnAttrValue
                    (Constants.ANN_CONFIG_ATTR_BASE_PATH);
            if (annotationAttributeBasePathValue != null && annotationAttributeBasePathValue.getStringValue() != null &&
                    !annotationAttributeBasePathValue.getStringValue().trim().isEmpty()) {
                basePath = annotationAttributeBasePathValue.getStringValue();
            } else {
                throw new BallerinaConnectorException("Cannot define WebSocket endpoint without BasePath for service: "
                                                     + serviceName);
            }
        } else {
            throw new BallerinaConnectorException("Cannot define WebSocket endpoint without BasePath for service: "
                                                 + serviceName);
        }

        return refactorUri(basePath);
    }

    /**
     * Find the listener interface of a given service.
     *
     * @param service {@link WebSocketService} which the listener interface should be found.
     * @return the listener interface of the service.
     */
    private String getListenerInterface(WebSocketService service) {
        // TODO : Handle correct interface addition to default interface.
        String listenerInterface = Constants.DEFAULT_INTERFACE;
        return listenerInterface;
    }

}
