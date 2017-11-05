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

package org.ballerinalang.net.ws.actions;

import org.ballerinalang.bre.Context;
import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.connector.api.ConnectorFuture;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BConnector;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.nativeimpl.actions.ClientConnectorFuture;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaAction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.net.ws.BallerinaWsClientConnectorListener;
import org.ballerinalang.net.ws.Constants;
import org.ballerinalang.net.ws.WebSocketService;
import org.ballerinalang.net.ws.WebSocketServicesRegistry;
import org.wso2.carbon.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.carbon.transport.http.netty.contract.websocket.HandshakeFuture;
import org.wso2.carbon.transport.http.netty.contract.websocket.HandshakeListener;
import org.wso2.carbon.transport.http.netty.contract.websocket.WebSocketClientConnector;
import org.wso2.carbon.transport.http.netty.contract.websocket.WsClientConnectorConfig;
import org.wso2.carbon.transport.http.netty.contractimpl.HttpWsConnectorFactoryImpl;

import javax.websocket.Session;

/**
 * Connect to the remote endpoint.
 *
 * @since 0.94
 */
@BallerinaAction(
        packageName = "ballerina.net.ws",
        actionName = "connectWithDefault",
        connectorName = Constants.CONNECTOR_NAME,
        args = {
                @Argument(name = "c", type = TypeKind.CONNECTOR)
        },
        returnType = {@ReturnType(type = TypeKind.STRUCT, structType = "Connection",
                                  structPackage = "ballerina.net.ws")}
)
public class ConnectWithDefault extends AbstractNativeWsAction {
    @Override
    public ConnectorFuture execute(Context context) {
        BConnector bconnector = (BConnector) getRefArgument(context, 0);
        String remoteUrl = getUrlFromConnector(bconnector);
        String clientServiceName = getClientServiceNameFromConnector(bconnector);
        WebSocketService wsService = WebSocketServicesRegistry.getInstance().getClientService(clientServiceName);
        if (wsService == null) {
            throw new BallerinaConnectorException("Cannot find client service: " + clientServiceName);
        }

        WsClientConnectorConfig clientConnectorConfig = new WsClientConnectorConfig(remoteUrl);
        clientConnectorConfig.setTarget(clientServiceName);
        HttpWsConnectorFactory connectorFactory = new HttpWsConnectorFactoryImpl();

        ClientConnectorFuture connectorFuture = new ClientConnectorFuture();
        WebSocketClientConnector clientConnector =
                connectorFactory.createWsClientConnector(clientConnectorConfig);
        HandshakeFuture handshakeFuture = clientConnector.connect(new BallerinaWsClientConnectorListener(wsService));
        handshakeFuture.setHandshakeListener(new HandshakeListener() {
            @Override
            public void onSuccess(Session session) {
                BStruct wsConnection = createWsConnectionStruct(context, session, null);
                context.getControlStackNew().currentFrame.returnValues[0] = wsConnection;
                storeWsConnection(session.getId(), wsConnection);
                connectorFuture.notifySuccess();
            }

            @Override
            public void onError(Throwable t) {
                BStruct wsError = createWsErrorStruct(context, t);
                context.getControlStackNew().currentFrame.returnValues[1] = wsError;
                connectorFuture.notifySuccess();
            }
        });
        return connectorFuture;
    }
}
