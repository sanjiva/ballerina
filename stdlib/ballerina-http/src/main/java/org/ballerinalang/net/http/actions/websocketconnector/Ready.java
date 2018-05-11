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

package org.ballerinalang.net.http.actions.websocketconnector;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.model.NativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.net.http.HttpConstants;
import org.ballerinalang.net.http.WebSocketConstants;
import org.ballerinalang.net.http.WebSocketOpenConnectionInfo;

/**
 * {@code Get} is the GET action implementation of the HTTP Connector.
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "http",
        functionName = "ready",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = WebSocketConstants.WEBSOCKET_CONNECTOR,
                             structPackage = "ballerina.http"),
        args = {
                @Argument(name = "wsConnector", type = TypeKind.STRUCT),
        }
)
public class Ready implements NativeCallableUnit {

    @Override
    public void execute(Context context, CallableUnitCallback callback) {
        BStruct webSocketConnector = (BStruct) context.getRefArgument(0);
        WebSocketOpenConnectionInfo connectionInfo = (WebSocketOpenConnectionInfo) webSocketConnector
                .getNativeData(WebSocketConstants.NATIVE_DATA_WEBSOCKET_CONNECTION_INFO);
        if (webSocketConnector.getBooleanField(0) == 0) {
            connectionInfo.getWebSocketConnection().readNextFrame();
            context.setReturnValues();
        } else {
            context.setReturnValues(BLangConnectorSPIUtil.createBStruct(context, HttpConstants.PROTOCOL_PACKAGE_HTTP,
                                                                        WebSocketConstants.WEBSOCKET_CONNECTOR_ERROR,
                                                                        "Already started reading frames"));
        }
        callback.notifySuccess();
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
