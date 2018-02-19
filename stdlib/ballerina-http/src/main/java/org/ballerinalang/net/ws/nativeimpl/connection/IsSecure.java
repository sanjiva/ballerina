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

package org.ballerinalang.net.ws.nativeimpl.connection;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.AbstractNativeFunction;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.net.ws.WebSocketConstants;

import javax.websocket.Session;

/**
 * Check whether the connection is secure connection or not.
 *
 * @since 0.94
 */

@BallerinaFunction(
        packageName = "ballerina.net.ws",
        functionName = "isSecure",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = "Connection",
                             structPackage = "ballerina.net.ws"),
        returnType = {@ReturnType(type = TypeKind.BOOLEAN)},
        isPublic = true
)
public class IsSecure extends AbstractNativeFunction {

    @Override
    public BValue[] execute(Context context) {
        BStruct wsConnection = (BStruct) getRefArgument(context, 0);
        Session session = (Session) wsConnection.getNativeData(WebSocketConstants.NATIVE_DATA_WEBSOCKET_SESSION);
        boolean isSecuredConnection = session.isSecure();
        return getBValues(new BBoolean(isSecuredConnection));
    }
}
