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
 *
 */

package org.ballerinalang.net.http;

import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.connector.api.Service;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.connector.api.Value;
import org.ballerinalang.logging.BLogManager;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.util.codegen.ProgramFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This services registry holds all the services of HTTP + WebSocket. This is a singleton class where all HTTP +
 * WebSocket Dispatchers can access.
 *
 * @since 0.8
 */
public class HTTPServicesRegistry {

    private static final Logger logger = LoggerFactory.getLogger(HTTPServicesRegistry.class);

    // Outer Map key=basePath
    protected final Map<String, HttpService> servicesInfoMap = new ConcurrentHashMap<>();
    protected CopyOnWriteArrayList<String> sortedServiceURIs = new CopyOnWriteArrayList<>();
    private final WebSocketServicesRegistry webSocketServicesRegistry;

    public HTTPServicesRegistry(WebSocketServicesRegistry webSocketServicesRegistry) {
        this.webSocketServicesRegistry = webSocketServicesRegistry;
    }

    /**
     * Get ServiceInfo isntance for given interface and base path.
     *
     * @param basepath basepath of the service.
     * @return the {@link HttpService} instance if exist else null
     */
    public HttpService getServiceInfo(String basepath) {
        return servicesInfoMap.get(basepath);
    }

    /**
     * Get ServiceInfo map for given interfaceId.
     *
     * @return the serviceInfo map if exists else null.
     */
    public Map<String, HttpService> getServicesInfoByInterface() {
        return servicesInfoMap;
    }

    /**
     * Register a service into the map.
     *
     * @param service requested serviceInfo to be registered.
     */
    public void registerService(Service service, Struct endpoint) {
        String accessLogConfig = HttpConnectionManager.getInstance().getHttpAccessLoggerConfig();
        if (accessLogConfig != null) {
            try {
                ((BLogManager) BLogManager.getLogManager()).setHttpAccessLogHandler(accessLogConfig);
            } catch (IOException e) {
                throw new BallerinaConnectorException("Invalid file path: " + accessLogConfig, e);
            }
        }

        HttpService httpService = HttpService.buildHttpService(service);

        //TODO check with new method
//        HttpUtil.populateKeepAliveAndCompressionStatus(service, annotation);

        // TODO: Add websocket services to the service registry when service creation get available.
        servicesInfoMap.put(httpService.getBasePath(), httpService);
        logger.info("Service deployed : " + service.getName() + " with context " + httpService.getBasePath());

        //basePath will get cached after registering service
        sortedServiceURIs.add(httpService.getBasePath());
        sortedServiceURIs.sort((basePath1, basePath2) -> basePath2.length() - basePath1.length());

        // If WebSocket upgrade path is available, then register the name of the WebSocket service.
        Struct websocketConfig = httpService.getWebSocketUpgradeConfig();
        if (websocketConfig != null) {
            registerWebSocketUpgradePath(
                    WebSocketUtil.getProgramFile(httpService.getBallerinaService().getResources()[0]),
                    websocketConfig, httpService.getBasePath(), endpoint);
        }

    }

    private String sanitizeBasePath(String basePath) {
        basePath = basePath.trim();
        if (!basePath.startsWith(HttpConstants.DEFAULT_BASE_PATH)) {
            basePath = HttpConstants.DEFAULT_BASE_PATH.concat(basePath);
        }
        if (basePath.endsWith(HttpConstants.DEFAULT_BASE_PATH) && basePath.length() != 1) {
            basePath = basePath.substring(0, basePath.length() - 1);
        }
        return basePath;
    }

    private void registerWebSocketUpgradePath(ProgramFile programFile, Struct websocketConfig, String basePath,
                                              Struct endpoint) {
        String upgradePath = sanitizeBasePath(
                websocketConfig.getStringField(HttpConstants.ANN_WEBSOCKET_ATTR_UPGRADE_PATH));
        Value serviceType = websocketConfig.getTypeField(WebSocketConstants.WEBSOCKET_UPGRADE_SERVICE_CONFIG);
        String uri = basePath.concat(upgradePath);
        WebSocketService service = new WebSocketService(
                BLangConnectorSPIUtil.getServiceFromType(programFile, serviceType));
        service.setServiceEndpoint((BStruct) endpoint.getVMValue());
        webSocketServicesRegistry.addUpgradableServiceByName(service, uri);
    }

    public String findTheMostSpecificBasePath(String requestURIPath, Map<String, HttpService> services) {
        for (Object key : sortedServiceURIs) {
            if (!requestURIPath.toLowerCase().contains(key.toString().toLowerCase())) {
                continue;
            }
            if (requestURIPath.length() <= key.toString().length()) {
                return key.toString();
            }
            if (requestURIPath.charAt(key.toString().length()) == '/') {
                return key.toString();
            }
        }
        if (services.containsKey(HttpConstants.DEFAULT_BASE_PATH)) {
            return HttpConstants.DEFAULT_BASE_PATH;
        }
        return null;
    }
}
