/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.net.http.nativeimpl.connection;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.mime.util.EntityBodyHandler;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.net.http.DataContext;
import org.ballerinalang.net.http.HttpUtil;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.wso2.transport.http.netty.contract.HttpConnectorListener;
import org.wso2.transport.http.netty.contract.HttpResponseFuture;
import org.wso2.transport.http.netty.message.Http2PushPromise;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;
import org.wso2.transport.http.netty.message.HttpMessageDataStreamer;

import java.io.OutputStream;

import static org.ballerinalang.net.http.HttpUtil.extractEntity;

/**
 * {@code PushPromisedResponse} is the extern function to respond back the client with Server Push response.
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "http",
        functionName = "pushPromisedResponse",
        receiver = @Receiver(type = TypeKind.OBJECT, structType = "Connection",
                             structPackage = "ballerina/http"),
        args = {@Argument(name = "promise", type = TypeKind.OBJECT, structType = "PushPromise",
                structPackage = "ballerina/http"),
                @Argument(name = "res", type = TypeKind.OBJECT, structType = "OutResponse",
                structPackage = "ballerina/http")

        },
        returnType = @ReturnType(type = TypeKind.RECORD, structType = "HttpConnectorError",
                                 structPackage = "ballerina/http"),
        isPublic = true
)
public class PushPromisedResponse extends ConnectionAction {

    @Override
    public void execute(Context context, CallableUnitCallback callback) {
        BMap<String, BValue> connectionStruct = (BMap<String, BValue>) context.getRefArgument(0);
        HttpCarbonMessage inboundRequestMsg = HttpUtil.getCarbonMsg(connectionStruct, null);
        DataContext dataContext = new DataContext(context, callback, inboundRequestMsg);
        HttpUtil.serverConnectionStructCheck(inboundRequestMsg);

        BMap<String, BValue> pushPromiseStruct = (BMap<String, BValue>) context.getRefArgument(1);
        Http2PushPromise http2PushPromise = HttpUtil.getPushPromise(pushPromiseStruct, null);
        if (http2PushPromise == null) {
            throw new BallerinaException("invalid push promise");
        }

        BMap<String, BValue> outboundResponseStruct = (BMap<String, BValue>) context.getRefArgument(2);
        HttpCarbonMessage outboundResponseMsg = HttpUtil
                .getCarbonMsg(outboundResponseStruct, HttpUtil.createHttpCarbonMessage(false));

        HttpUtil.prepareOutboundResponse(context, inboundRequestMsg, outboundResponseMsg, outboundResponseStruct);
        pushResponseRobust(dataContext, inboundRequestMsg, outboundResponseStruct, outboundResponseMsg,
                           http2PushPromise);
    }

    private void pushResponseRobust(DataContext dataContext, HttpCarbonMessage requestMessage,
                                    BMap<String, BValue> outboundResponseStruct, HttpCarbonMessage responseMessage,
                                    Http2PushPromise http2PushPromise) {
        HttpResponseFuture outboundRespStatusFuture =
                HttpUtil.pushResponse(requestMessage, responseMessage, http2PushPromise);
        HttpMessageDataStreamer outboundMsgDataStreamer = getMessageDataStreamer(responseMessage);
        HttpConnectorListener outboundResStatusConnectorListener =
                new HttpResponseConnectorListener(dataContext, outboundMsgDataStreamer);
        outboundRespStatusFuture.setHttpConnectorListener(outboundResStatusConnectorListener);
        OutputStream messageOutputStream = outboundMsgDataStreamer.getOutputStream();

        BMap<String, BValue> entityStruct = extractEntity(outboundResponseStruct);
        if (entityStruct != null) {
            BValue outboundMessageSource = EntityBodyHandler.getMessageDataSource(entityStruct);
            serializeMsgDataSource(outboundMessageSource, entityStruct, messageOutputStream);
        }
    }
}
