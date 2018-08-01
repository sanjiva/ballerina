/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.net.http.actions.httpclient;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.net.http.DataContext;
import org.ballerinalang.net.http.HttpConstants;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;


/**
 * {@code Options} is the OPTIONS action implementation of the HTTP Connector.
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "http",
        functionName = "nativeOptions",
        args = {
                @Argument(name = "callerActions", type = TypeKind.CONNECTOR),
                @Argument(name = "path", type = TypeKind.STRING),
                @Argument(name = "req", type = TypeKind.OBJECT, structType = "Request",
                        structPackage = "ballerina/http")
        },
        returnType = {
                @ReturnType(type = TypeKind.OBJECT, structType = "Response", structPackage = "ballerina/http"),
                @ReturnType(type = TypeKind.RECORD, structType = "HttpConnectorError",
                        structPackage = "ballerina/http"),
        }
)
public class Options extends AbstractHTTPAction {

    @Override
    public void execute(Context context, CallableUnitCallback callback) {
        DataContext dataContext = new DataContext(context, callback, createOutboundRequestMsg(context));
        executeNonBlockingAction(dataContext, false);
    }

    @Override
    protected HttpCarbonMessage createOutboundRequestMsg(Context context) {
        HttpCarbonMessage outboundRequestMsg = super.createOutboundRequestMsg(context);
        outboundRequestMsg.setProperty(HttpConstants.HTTP_METHOD, HttpConstants.HTTP_METHOD_OPTIONS);
        return outboundRequestMsg;
    }
}
