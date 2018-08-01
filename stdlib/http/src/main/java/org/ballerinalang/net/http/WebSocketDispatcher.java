/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import io.netty.handler.codec.CorruptedFrameException;
import org.ballerinalang.bre.bvm.BLangVMErrors;
import org.ballerinalang.bre.bvm.BLangVMStructs;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.connector.api.Executor;
import org.ballerinalang.connector.api.ParamDetail;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BByteArray;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.services.ErrorHandlerUtils;
import org.ballerinalang.util.BLangConstants;
import org.ballerinalang.util.codegen.PackageInfo;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.codegen.StructureTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contract.websocket.WebSocketBinaryMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketCloseMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnection;
import org.wso2.transport.http.netty.contract.websocket.WebSocketControlMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketControlSignal;
import org.wso2.transport.http.netty.contract.websocket.WebSocketHandshaker;
import org.wso2.transport.http.netty.contract.websocket.WebSocketTextMessage;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code WebSocketDispatcher} This is the web socket request dispatcher implementation which finds best matching
 * resource for incoming web socket request.
 *
 * @since 0.94
 */
public class WebSocketDispatcher {

    private static final Logger log = LoggerFactory.getLogger(WebSocketDispatcher.class);

    private WebSocketDispatcher() {
    }

    /**
     * This will find the best matching service for given web socket request.
     *
     * @param webSocketHandshaker incoming message.
     * @return matching service.
     */
    static WebSocketService findService(WebSocketServicesRegistry servicesRegistry,
                                        WebSocketHandshaker webSocketHandshaker) {
        try {
            Map<String, String> pathParams = new HashMap<>();
            String serviceUri = webSocketHandshaker.getTarget();
            serviceUri = WebSocketUtil.refactorUri(serviceUri);
            URI requestUri = URI.create(serviceUri);
            WebSocketService service = servicesRegistry.getUriTemplate().matches(requestUri.getPath(), pathParams,
                                                                                 webSocketHandshaker);
            if (service == null) {
                throw new BallerinaConnectorException("no Service found to handle the service request: " + serviceUri);
            }
            HttpCarbonMessage msg = webSocketHandshaker.getHttpCarbonRequest();
            msg.setProperty(HttpConstants.QUERY_STR, requestUri.getRawQuery());
            msg.setProperty(HttpConstants.RESOURCE_ARGS, pathParams);
            return service;
        } catch (IllegalArgumentException e) {
            throw new BallerinaConnectorException(e.getMessage());
        } catch (Exception e) {
            String message = "No Service found to handle the service request";
            webSocketHandshaker.cancelHandshake(404, message);
            throw new BallerinaConnectorException(message, e);
        }
    }

    static void dispatchTextMessage(WebSocketOpenConnectionInfo connectionInfo,
                                    WebSocketTextMessage textMessage) {
        WebSocketConnection webSocketConnection = connectionInfo.getWebSocketConnection();
        WebSocketService wsService = connectionInfo.getService();
        Resource onTextMessageResource = wsService.getResourceByName(WebSocketConstants.RESOURCE_NAME_ON_TEXT);
        if (onTextMessageResource == null) {
            webSocketConnection.readNextFrame();
            return;
        }
        List<ParamDetail> paramDetails = onTextMessageResource.getParamDetails();
        BValue[] bValues = new BValue[paramDetails.size()];
        bValues[0] = connectionInfo.getWebSocketEndpoint();
        bValues[1] = new BString(textMessage.getText());
        if (paramDetails.size() == 3) {
            bValues[2] = new BBoolean(textMessage.isFinalFragment());
        }
        Executor.submit(onTextMessageResource, new WebSocketResourceCallableUnitCallback(webSocketConnection), null,
                        null, bValues);
    }

    static void dispatchBinaryMessage(WebSocketOpenConnectionInfo connectionInfo,
                                      WebSocketBinaryMessage binaryMessage) {
        WebSocketConnection webSocketConnection = connectionInfo.getWebSocketConnection();
        WebSocketService wsService = connectionInfo.getService();
        Resource onBinaryMessageResource = wsService.getResourceByName(
                WebSocketConstants.RESOURCE_NAME_ON_BINARY);
        if (onBinaryMessageResource == null) {
            webSocketConnection.readNextFrame();
            return;
        }
        List<ParamDetail> paramDetails = onBinaryMessageResource.getParamDetails();
        BValue[] bValues = new BValue[paramDetails.size()];
        bValues[0] = connectionInfo.getWebSocketEndpoint();
        bValues[1] = new BByteArray(binaryMessage.getByteArray());
        if (paramDetails.size() == 3) {
            bValues[2] = new BBoolean(binaryMessage.isFinalFragment());
        }
        Executor.submit(onBinaryMessageResource, new WebSocketResourceCallableUnitCallback(webSocketConnection), null,
                        null, bValues);
    }

    static void dispatchControlMessage(WebSocketOpenConnectionInfo connectionInfo,
                                       WebSocketControlMessage controlMessage) {
        if (controlMessage.getControlSignal() == WebSocketControlSignal.PING) {
            WebSocketDispatcher.dispatchPingMessage(connectionInfo, controlMessage);
        } else if (controlMessage.getControlSignal() == WebSocketControlSignal.PONG) {
            WebSocketDispatcher.dispatchPongMessage(connectionInfo, controlMessage);
        } else {
            throw new BallerinaConnectorException("Received unknown control signal");
        }
    }

    private static void dispatchPingMessage(WebSocketOpenConnectionInfo connectionInfo,
                                            WebSocketControlMessage controlMessage) {
        WebSocketConnection webSocketConnection = connectionInfo.getWebSocketConnection();
        WebSocketService wsService = connectionInfo.getService();
        Resource onPingMessageResource = wsService.getResourceByName(WebSocketConstants.RESOURCE_NAME_ON_PING);
        if (onPingMessageResource == null) {
            pingAutomatically(controlMessage);
            return;
        }
        List<ParamDetail> paramDetails = onPingMessageResource.getParamDetails();
        BValue[] bValues = new BValue[paramDetails.size()];
        bValues[0] = connectionInfo.getWebSocketEndpoint();
        bValues[1] = new BByteArray(controlMessage.getByteArray());
        Executor.submit(onPingMessageResource, new WebSocketResourceCallableUnitCallback(webSocketConnection), null,
                        null, bValues);
    }

    private static void dispatchPongMessage(WebSocketOpenConnectionInfo connectionInfo,
                                            WebSocketControlMessage controlMessage) {
        WebSocketConnection webSocketConnection = connectionInfo.getWebSocketConnection();
        WebSocketService wsService = connectionInfo.getService();
        Resource onPongMessageResource = wsService.getResourceByName(WebSocketConstants.RESOURCE_NAME_ON_PONG);
        if (onPongMessageResource == null) {
            webSocketConnection.readNextFrame();
            return;
        }
        List<ParamDetail> paramDetails = onPongMessageResource.getParamDetails();
        BValue[] bValues = new BValue[paramDetails.size()];
        bValues[0] = connectionInfo.getWebSocketEndpoint();
        bValues[1] = new BByteArray(controlMessage.getByteArray());
        Executor.submit(onPongMessageResource, new WebSocketResourceCallableUnitCallback(webSocketConnection), null,
                        null, bValues);
    }

    static void dispatchCloseMessage(WebSocketOpenConnectionInfo connectionInfo,
                                     WebSocketCloseMessage closeMessage) {
        WebSocketConnection webSocketConnection = connectionInfo.getWebSocketConnection();
        WebSocketService wsService = connectionInfo.getService();
        Resource onCloseResource = wsService.getResourceByName(WebSocketConstants.RESOURCE_NAME_ON_CLOSE);
        int closeCode = closeMessage.getCloseCode();
        String closeReason = closeMessage.getCloseReason();
        if (onCloseResource == null) {
            if (webSocketConnection.isOpen()) {
                webSocketConnection.finishConnectionClosure(closeCode, null);
            }
            return;
        }
        List<ParamDetail> paramDetails = onCloseResource.getParamDetails();
        BValue[] bValues = new BValue[paramDetails.size()];
        bValues[0] = connectionInfo.getWebSocketEndpoint();
        bValues[1] = new BInteger(closeCode);
        bValues[2] = new BString(closeReason == null ? "" : closeReason);
        CallableUnitCallback onCloseCallback = new CallableUnitCallback() {
            @Override
            public void notifySuccess() {
                if (closeMessage.getCloseCode() != WebSocketConstants.STATUS_CODE_ABNORMAL_CLOSURE
                        && webSocketConnection.isOpen()) {
                    webSocketConnection.finishConnectionClosure(closeCode, null).addListener(
                            closeFuture -> connectionInfo.getWebSocketEndpoint()
                                    .put(WebSocketConstants.LISTENER_IS_SECURE_FIELD, new BBoolean(false)));
                }
            }

            @Override
            public void notifyFailure(BMap<String, BValue> error) {
                ErrorHandlerUtils.printError(BLangVMErrors.getPrintableStackTrace(error));
                WebSocketUtil.closeDuringUnexpectedCondition(webSocketConnection);
            }
        };
        Executor.submit(onCloseResource, onCloseCallback, null, null, bValues);
    }

    static void dispatchError(WebSocketOpenConnectionInfo connectionInfo, Throwable throwable) {
        WebSocketService webSocketService = connectionInfo.getService();
        Resource onErrorResource = webSocketService.getResourceByName(WebSocketConstants.RESOURCE_NAME_ON_ERROR);
        if (isUnexpectedError(throwable)) {
            log.error("Unexpected error", throwable);
        }
        if (onErrorResource == null) {
            ErrorHandlerUtils.printError(throwable);
            return;
        }
        BValue[] bValues = new BValue[onErrorResource.getParamDetails().size()];
        bValues[0] = connectionInfo.getWebSocketEndpoint();
        bValues[1] = getError(webSocketService, throwable);
        CallableUnitCallback onErrorCallback = new CallableUnitCallback() {
            @Override
            public void notifySuccess() {
                // Do nothing.
            }

            @Override
            public void notifyFailure(BMap<String, BValue> error) {
                ErrorHandlerUtils.printError(BLangVMErrors.getPrintableStackTrace(error));
            }
        };
        Executor.submit(onErrorResource, onErrorCallback, null, null, bValues);
    }

    private static BMap<String, BValue> getError(WebSocketService webSocketService, Throwable throwable) {
        ProgramFile programFile = webSocketService.getServiceInfo().getPackageInfo().getProgramFile();
        PackageInfo errorPackageInfo = programFile.getPackageInfo(BLangConstants.BALLERINA_BUILTIN_PKG);
        StructureTypeInfo errorStructInfo = errorPackageInfo.getStructInfo(BLangVMErrors.STRUCT_GENERIC_ERROR);
        String errMsg;
        if (isUnexpectedError(throwable)) {
            errMsg = "Unexpected internal error. Please check internal-log for more details!";
        } else {
            errMsg = throwable.getMessage();
        }
        return BLangVMStructs.createBStruct(errorStructInfo, errMsg);
    }

    private static boolean isUnexpectedError(Throwable throwable) {
        return !(throwable instanceof CorruptedFrameException);
    }

    static void dispatchIdleTimeout(WebSocketOpenConnectionInfo connectionInfo) {
        WebSocketConnection webSocketConnection = connectionInfo.getWebSocketConnection();
        WebSocketService wsService = connectionInfo.getService();
        Resource onIdleTimeoutResource = wsService.getResourceByName(WebSocketConstants.RESOURCE_NAME_ON_IDLE_TIMEOUT);
        if (onIdleTimeoutResource == null) {
            webSocketConnection.readNextFrame();
            return;
        }
        List<ParamDetail> paramDetails = onIdleTimeoutResource.getParamDetails();
        BValue[] bValues = new BValue[paramDetails.size()];
        bValues[0] = connectionInfo.getWebSocketEndpoint();
        CallableUnitCallback onIdleTimeoutCallback = new CallableUnitCallback() {
            @Override
            public void notifySuccess() {
                // Do nothing.
            }

            @Override
            public void notifyFailure(BMap<String, BValue> error) {
                ErrorHandlerUtils.printError(BLangVMErrors.getPrintableStackTrace(error));
                WebSocketUtil.closeDuringUnexpectedCondition(webSocketConnection);
            }
        };
        Executor.submit(onIdleTimeoutResource, onIdleTimeoutCallback, null,
                        null, bValues);
    }

    private static void pingAutomatically(WebSocketControlMessage controlMessage) {
        WebSocketConnection webSocketConnection = controlMessage.getWebSocketConnection();
        webSocketConnection.pong(controlMessage.getByteBuffer()).addListener(future -> {
            Throwable cause = future.cause();
            if (!future.isSuccess() && cause != null) {
                ErrorHandlerUtils.printError(cause);
            }
            webSocketConnection.readNextFrame();
        });
    }
}
