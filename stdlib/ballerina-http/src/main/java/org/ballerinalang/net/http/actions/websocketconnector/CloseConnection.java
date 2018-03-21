/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.net.http.WebSocketConnectionManager;
import org.ballerinalang.net.http.WebSocketConstants;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.io.IOException;
import javax.websocket.CloseReason;
import javax.websocket.Session;

/**
 * {@code Get} is the GET action implementation of the HTTP Connector.
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "net.http",
        functionName = "closeConnection",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = WebSocketConstants.WEBSOCKET_CONNECTOR,
                structPackage = "ballerina.net.http"),
        args = {
                @Argument(name = "wsConnector", type = TypeKind.STRUCT),
                @Argument(name = "statusCode", type = TypeKind.INT),
                @Argument(name = "reason", type = TypeKind.STRING)
        }
)
public class CloseConnection extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        BStruct wsConnection = (BStruct) context.getRefArgument(0);
        int statusCode = (int) context.getIntArgument(0);
        String reason = context.getStringArgument(0);
        Session session = (Session) wsConnection.getNativeData(WebSocketConstants.NATIVE_DATA_WEBSOCKET_SESSION);
        try {
            session.close(new CloseReason(() -> statusCode, reason));
        } catch (IOException e) {
            throw new BallerinaException("Could not close the connection: " + e.getMessage());
        } finally {
            WebSocketConnectionManager.getInstance().removeConnection(session.getId());
        }
        context.setReturnValues();
    }
}
