/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ballerinalang.net.http.actions.websocketconnector;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.net.http.WebSocketConstants;
import org.ballerinalang.net.http.WebSocketService;
import org.ballerinalang.net.http.WebSocketUtil;

import java.util.Set;

/**
 * {@code Get} is the GET action implementation of the HTTP Connector.
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "net.http",
        functionName = "upgradeToWebSocket",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = WebSocketConstants.WEBSOCKET_CONNECTOR,
                             structPackage = "ballerina.net.http"),
        args = {
                @Argument(name = "headers", type = TypeKind.MAP)
        },
        isPublic = true
)
public class UpgradeToWebSocket extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        BStruct serverConnector = (BStruct) context.getRefArgument(0);
        WebSocketService webSocketService = (WebSocketService) serverConnector.getNativeData(
                WebSocketConstants.WEBSOCKET_SERVICE);
        BMap<String, BString> headers = (BMap<String, BString>) context.getRefArgument(1);
        DefaultHttpHeaders httpHeaders = new DefaultHttpHeaders();
        Set<String> keys = headers.keySet();
        for (String key : keys) {
            httpHeaders.add(key, headers.get(key));
        }

        WebSocketUtil.handleHandshake(webSocketService, httpHeaders, serverConnector);

        context.setReturnValues();
    }
}
