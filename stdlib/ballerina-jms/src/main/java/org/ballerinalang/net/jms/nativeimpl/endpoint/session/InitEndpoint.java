/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.ballerinalang.net.jms.nativeimpl.endpoint.session;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.NativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.net.jms.Constants;
import org.ballerinalang.net.jms.JMSUtils;
import org.ballerinalang.net.jms.utils.BallerinaAdapter;

import javax.jms.Connection;
import javax.jms.Session;

/**
 * Connection init function for JMS connection endpoint.
 *
 * @since 0.970
 */

@BallerinaFunction(
        orgName = "ballerina", packageName = "jms",
        functionName = "initEndpoint",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = "Session", structPackage = "ballerina.jms"),
        args = {@Argument(name = "connection", type = TypeKind.STRUCT, structType = "Connection")
        },
        isPublic = true
)
public class InitEndpoint implements NativeCallableUnit {

    @Override
    public void execute(Context context, CallableUnitCallback callableUnitCallback) {
        Struct sessionBObject = BallerinaAdapter.getReceiverStruct(context);

        Struct sessionConfig = sessionBObject.getStructField(Constants.SESSION_CONFIG);

        BStruct connectionBObject = (BStruct) context.getRefArgument(1);
        Connection connection = BallerinaAdapter.getNativeObject(connectionBObject,
                                                                   Constants.JMS_CONNECTION,
                                                                   Connection.class,
                                                                   context);

        Session session = JMSUtils.createSession(connection, sessionConfig);
        sessionBObject.addNativeData(Constants.JMS_SESSION, session);
    }

    @Override
    public boolean isBlocking() {
        return true;
    }
}
