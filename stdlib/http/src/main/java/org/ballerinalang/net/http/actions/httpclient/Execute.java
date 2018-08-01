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

package org.ballerinalang.net.http.actions.httpclient;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.net.http.AcceptEncodingConfig;
import org.ballerinalang.net.http.DataContext;
import org.ballerinalang.net.http.HttpConstants;
import org.ballerinalang.net.http.HttpUtil;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;

import java.util.Locale;

/**
 * {@code Execute} action can be used to invoke execute a http call with any httpVerb.
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "http",
        functionName = "nativeExecute",
        args = {
                @Argument(name = "callerActions", type = TypeKind.OBJECT),
                @Argument(name = "httpVerb", type = TypeKind.STRING),
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
public class Execute extends AbstractHTTPAction {

    @Override
    public void execute(Context context, CallableUnitCallback callback) {
        DataContext dataContext = new DataContext(context, callback, createOutboundRequestMsg(context));
        // Execute the operation
        executeNonBlockingAction(dataContext, false);
    }

    @Override
    protected HttpCarbonMessage createOutboundRequestMsg(Context context) {
        // Extract Argument values
        BMap<String, BValue> bConnector = (BMap<String, BValue>) context.getRefArgument(0);
        String httpVerb = context.getStringArgument(0);
        String path = context.getStringArgument(1);
        BMap<String, BValue> requestStruct = ((BMap<String, BValue>) context.getRefArgument(1));

        HttpCarbonMessage outboundRequestMsg = HttpUtil
                .getCarbonMsg(requestStruct, HttpUtil.createHttpCarbonMessage(true));

        HttpUtil.checkEntityAvailability(context, requestStruct);
        HttpUtil.enrichOutboundMessage(outboundRequestMsg, requestStruct);
        prepareOutboundRequest(context, bConnector, path, outboundRequestMsg);

        // If the verb is not specified, use the verb in incoming message
        if (httpVerb == null || httpVerb.isEmpty()) {
            httpVerb = (String) outboundRequestMsg.getProperty(HttpConstants.HTTP_METHOD);
        }
        outboundRequestMsg.setProperty(HttpConstants.HTTP_METHOD, httpVerb.trim().toUpperCase(Locale.getDefault()));
        AcceptEncodingConfig acceptEncodingConfig =
                getAcceptEncodingConfig(getAcceptEncodingConfigFromEndpointConfig(bConnector));
        handleAcceptEncodingHeader(outboundRequestMsg, acceptEncodingConfig);

        return outboundRequestMsg;
    }
}
