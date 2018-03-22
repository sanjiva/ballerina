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
package org.ballerinalang.net.http;

import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.logging.BLogManager;
import org.ballerinalang.logging.util.BLogLevel;
import org.ballerinalang.net.http.util.ConnectorStartupSynchronizer;
import org.wso2.transport.http.netty.config.ConfigurationBuilder;
import org.wso2.transport.http.netty.config.ListenerConfiguration;
import org.wso2.transport.http.netty.config.TransportsConfiguration;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.ServerConnector;
import org.wso2.transport.http.netty.contract.websocket.WebSocketClientConnector;
import org.wso2.transport.http.netty.contract.websocket.WsClientConnectorConfig;
import org.wso2.transport.http.netty.listener.ServerBootstrapConfiguration;
import org.wso2.transport.http.netty.message.HTTPConnectorUtil;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.LogManager;

import static org.ballerinalang.logging.util.Constants.HTTP_ACCESS_LOG;
import static org.ballerinalang.logging.util.Constants.LOG_TO;

/**
 * {@code HttpConnectionManager} is responsible for managing all the server connectors with ballerina runtime.
 *
 * @since 0.94
 */
public class HttpConnectionManager {

    private static HttpConnectionManager instance = new HttpConnectionManager();
    private Map<String, ServerConnector> startupDelayedHTTPServerConnectors = new HashMap<>();
    private Map<String, HttpServerConnectorContext> serverConnectorPool = new HashMap<>();
    private ServerBootstrapConfiguration serverBootstrapConfiguration;
    private TransportsConfiguration trpConfig;
    private HttpWsConnectorFactory httpConnectorFactory = HttpUtil.createHttpWsConnectionFactory();

    private HttpConnectionManager() {
        String nettyConfigFile = System.getProperty(HttpConstants.HTTP_TRANSPORT_CONF,
                                                    "conf" + File.separator + "transports" +
                        File.separator + "netty-transports.yml");
        trpConfig = ConfigurationBuilder.getInstance().getConfiguration(nettyConfigFile);
        serverBootstrapConfiguration = HTTPConnectorUtil
                .getServerBootstrapConfiguration(trpConfig.getTransportProperties());

        if (isHTTPTraceLoggerEnabled()) {
            ((BLogManager) BLogManager.getLogManager()).setHttpTraceLogHandler();
        }
    }

    public static HttpConnectionManager getInstance() {
        return instance;
    }

    public Set<ListenerConfiguration> getDefaultListenerConfiugrationSet() {
        Set<ListenerConfiguration> listenerConfigurationSet = new HashSet<>();
        for (ListenerConfiguration listenerConfiguration : trpConfig.getListenerConfigurations()) {
            listenerConfiguration.setId(listenerConfiguration.getHost() == null ?
                    "0.0.0.0" : listenerConfiguration.getHost() + ":" + listenerConfiguration.getPort());
            listenerConfigurationSet.add(listenerConfiguration);
        }
        return listenerConfigurationSet;
    }

    public ServerConnector createHttpServerConnector(ListenerConfiguration listenerConfig) {
        String listenerInterface = listenerConfig.getHost() + ":" + listenerConfig.getPort();
        HttpServerConnectorContext httpServerConnectorContext =
                serverConnectorPool.get(listenerInterface);
        if (httpServerConnectorContext != null) {
            if (checkForConflicts(listenerConfig, httpServerConnectorContext)) {
                throw new BallerinaConnectorException("Conflicting configuration detected for listener " +
                        "configuration id " + listenerConfig.getId());
            }
            httpServerConnectorContext.incrementReferenceCount();
            return httpServerConnectorContext.getServerConnector();
        }

        if (isHTTPTraceLoggerEnabled()) {
            listenerConfig.setHttpTraceLogEnabled(true);
        }

        serverBootstrapConfiguration = HTTPConnectorUtil
                .getServerBootstrapConfiguration(trpConfig.getTransportProperties());
        ServerConnector serverConnector =
                httpConnectorFactory.createServerConnector(serverBootstrapConfiguration, listenerConfig);

        httpServerConnectorContext = new HttpServerConnectorContext(serverConnector, listenerConfig);
        serverConnectorPool.put(serverConnector.getConnectorID(), httpServerConnectorContext);
        httpServerConnectorContext.incrementReferenceCount();
        addStartupDelayedHTTPServerConnector(listenerInterface, serverConnector);
        return serverConnector;
    }

    /**
     * Add a HTTP ServerConnector which startup is delayed at the service deployment time.
     *
     * @param serverConnector ServerConnector
     */
    public void addStartupDelayedHTTPServerConnector(String id, ServerConnector serverConnector) {
        startupDelayedHTTPServerConnectors.put(id, serverConnector);
    }

//    /**
//     * Start all the ServerConnectors which startup is delayed at the service deployment time.
//     *
//     * @param httpServerConnector {@link BallerinaHttpServerConnector} of the pending transport server connectors.
//     * @throws ServerConnectorException if exception occurs while starting at least one connector.
//     */
//    void startPendingHTTPConnectors(BallerinaHttpServerConnector httpServerConnector)
//                                                                       throws ServerConnectorException {
//        ConnectorStartupSynchronizer startupSyncer =
//                new ConnectorStartupSynchronizer(startupDelayedHTTPServerConnectors.size());
//
//        for (Map.Entry<String, ServerConnector> serverConnectorEntry :
//                                                        startupDelayedHTTPServerConnectors.entrySet()) {
//            ServerConnector serverConnector = serverConnectorEntry.getValue();
//            ServerConnectorFuture connectorFuture = serverConnector.start();
//            setConnectorListeners(connectorFuture, serverConnector.getConnectorID(), startupSyncer,
//                                  httpServerConnector);
//        }
//        try {
//            // Wait for all the connectors to start
//            startupSyncer.syncConnectors();
//        } catch (InterruptedException e) {
//            throw new BallerinaConnectorException("Error in starting HTTP server connector(s)");
//        }
//        validateConnectorStartup(startupSyncer);
//        startupDelayedHTTPServerConnectors.clear();
//    }

    private static class HttpServerConnectorContext {
        private ServerConnector serverConnector;
        private ListenerConfiguration listenerConfiguration;
        private int referenceCount = 0;

        HttpServerConnectorContext(ServerConnector serverConnector, ListenerConfiguration listenerConfiguration) {
            this.serverConnector = serverConnector;
            this.listenerConfiguration = listenerConfiguration;
        }

        void incrementReferenceCount() {
            this.referenceCount++;
        }

        void decrementReferenceCount() {
            this.referenceCount--;
        }

        ServerConnector getServerConnector() {
            return this.serverConnector;
        }

        ListenerConfiguration getListenerConfiguration() {
            return this.listenerConfiguration;
        }

        int getReferenceCount() {
            return this.referenceCount;
        }
    }

    private boolean checkForConflicts(ListenerConfiguration listenerConfiguration,
            HttpServerConnectorContext context) {
        if (context == null) {
            return false;
        }
        if (listenerConfiguration.getScheme().equalsIgnoreCase("https")) {
            ListenerConfiguration config = context.getListenerConfiguration();
            if (!listenerConfiguration.getKeyStoreFile().equals(config.getKeyStoreFile())
                    || !listenerConfiguration.getKeyStorePass().equals(config.getKeyStorePass())
                    || !listenerConfiguration.getCertPass().equals(config.getCertPass())) {
                return true;
            }
        }
        return false;
    }

    public WebSocketClientConnector getWebSocketClientConnector(WsClientConnectorConfig configuration) {
        return  httpConnectorFactory.createWsClientConnector(configuration);
    }

    public TransportsConfiguration getTransportConfig() {
        return trpConfig;
    }

//    private void setConnectorListeners(ServerConnectorFuture connectorFuture, String serverConnectorId,
//                                       ConnectorStartupSynchronizer startupSyncer,
//                                       BallerinaHttpServerConnector httpServerConnector) {
//        HTTPServicesRegistry httpServicesRegistry = httpServerConnector.getHttpServicesRegistry();
//        WebSocketServicesRegistry webSocketServicesRegistry = httpServerConnector.getWebSocketServicesRegistry();
//        connectorFuture.setHttpConnectorListener(new BallerinaHTTPConnectorListener(httpServicesRegistry));
//        connectorFuture.setWSConnectorListener(new WebSocketServerConnectorListener
//        (webSocketServicesRegistry));
//        connectorFuture.setPortBindingEventListener(
//                new HttpConnectorPortBindingListener(startupSyncer, serverConnectorId));
//    }

    private void validateConnectorStartup(ConnectorStartupSynchronizer startupSyncer) {
        int noOfExceptions = startupSyncer.getNoOfFailedConnectors();
        if (noOfExceptions <= 0) {
            return;
        }
        PrintStream console = System.err;

        startupSyncer.failedConnectorsIterator()
                .forEachRemaining(exceptionEntry -> console.println(
                        "ballerina: " + makeFirstLetterLowerCase(exceptionEntry.getValue().getMessage())
                                + ": [" + exceptionEntry.getKey() + "]"));

        if (noOfExceptions > 0) {
            // If there are any exceptions, stop all the connectors which started correctly and terminate the runtime.
            startupSyncer.inUseConnectorsIterator()
                    .forEachRemaining(connectorId -> startupDelayedHTTPServerConnectors.get(connectorId).stop());
            throw new BallerinaConnectorException("failed to start the server connectors");
        }
    }

    public boolean isHTTPTraceLoggerEnabled() {
        // TODO: Take a closer look at this since looking up from the Config Registry here caused test failures
        return ((BLogManager) LogManager.getLogManager()).getPackageLogLevel(
                org.ballerinalang.logging.util.Constants.HTTP_TRACE_LOG) == BLogLevel.TRACE;
    }

    /**
     * Gets the access logto value if available.
     * @return the access logto value from the ConfigRegistry
     */
    public String getHttpAccessLoggerConfig() {
        return ConfigRegistry.getInstance().getConfiguration(HTTP_ACCESS_LOG, LOG_TO);
    }

    private String makeFirstLetterLowerCase(String str) {
        if (str == null) {
            return null;
        }
        char ch[] = str.toCharArray();
        ch[0] = Character.toLowerCase(ch[0]);
        return new String(ch);
    }
}
