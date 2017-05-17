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

package org.ballerinalang.services.dispatchers.ws;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.AnnotationAttachment;
import org.ballerinalang.model.Service;
import org.ballerinalang.services.dispatchers.http.Constants;
import org.ballerinalang.services.dispatchers.http.HTTPServiceDispatcher;
import org.ballerinalang.services.dispatchers.http.HTTPServicesRegistry;
import org.ballerinalang.services.dispatchers.uri.URIUtil;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;


/**
 * Service Dispatcher for WebSocket Endpoint.
 */
public class WebSocketServiceDispatcher extends HTTPServiceDispatcher {

    @Override
    public Service findService(CarbonMessage cMsg, CarbonCallback callback, Context balContext) {
        String interfaceId = getInterface(cMsg);
        String serviceUri = (String) cMsg.getProperty(Constants.TO);
        serviceUri = refactorUri(serviceUri);
        if (serviceUri == null) {
            throw new BallerinaException("No service found to dispatch");
        }
        String basePath = URIUtil.getFirstPathSegment(serviceUri);
        Service service = HTTPServicesRegistry.getInstance().
                getService(interfaceId, Constants.DEFAULT_BASE_PATH + basePath);
        if (service == null) {
            throw new BallerinaException("No service found to handle message for " + serviceUri);
        }
        String webSocketUpgradePath = findWebSocketUpgradePath(service);
        if (webSocketUpgradePath == null) {
            throw new BallerinaException("No service found to handle message for " + serviceUri);
        }
        if (webSocketUpgradePath.equals(serviceUri)) {
            return service;
        }
        throw new BallerinaException("No service found to handle message for " + serviceUri);
    }

    @Override
    public String getProtocol() {
        return Constants.PROTOCOL_WEBSOCKET;
    }


    @Override
    public void serviceRegistered(Service service) {
        if (findWebSocketUpgradePath(service) != null) {
            super.serviceRegistered(service);
        }
    }

    private String findWebSocketUpgradePath(Service service) {
        AnnotationAttachment websocketUpgradePathAnnotation = null;
        AnnotationAttachment basePathAnnotation = null;
        AnnotationAttachment[] annotations = service.getAnnotations();
        for (AnnotationAttachment annotation: annotations) {
            if (annotation.getPkgName().equals(Constants.PROTOCOL_HTTP) &&
                    annotation.getName().equals(Constants.ANNOTATION_NAME_BASE_PATH)) {
                basePathAnnotation = annotation;
            } else if (annotation.getPkgName().equals(Constants.PROTOCOL_WEBSOCKET) &&
                    annotation.getName().equals(Constants.ANNOTATION_NAME_WEBSOCKET_UPGRADE_PATH)) {
                websocketUpgradePathAnnotation = annotation;
            }
        }
        if (websocketUpgradePathAnnotation != null && websocketUpgradePathAnnotation.getValue() != null && 
                !websocketUpgradePathAnnotation.getValue().trim().isEmpty()) {
            if (basePathAnnotation == null || basePathAnnotation.getValue() == null ||
                    basePathAnnotation.getValue().trim().isEmpty()) {
                throw new BallerinaException("Cannot define @WebSocketPathUpgrade without @BasePath");
            }
            String basePath = refactorUri(basePathAnnotation.getValue());
            String websocketUpgradePath = refactorUri(websocketUpgradePathAnnotation.getValue());
            return refactorUri(basePath.concat(websocketUpgradePath));
        }
        return null;
    }

    private String refactorUri(String uri) {
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
}
