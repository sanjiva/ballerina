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

package org.ballerinalang.net.http.nativeimpl.promise;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.net.http.HttpUtil;
import org.wso2.transport.http.netty.message.Http2PushPromise;

/**
 * {@code GetHeader} is the ballerina native function to get header value of a Push Promise.
 * <p>
 * If there are more than one header value for the specified header name exists, the first value is returned.
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "net.http",
        functionName = "getHeader",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = "PushPromise",
                structPackage = "ballerina.net.http"),
        args = {@Argument(name = "headerName", type = TypeKind.STRING)},
        returnType = {@ReturnType(type = TypeKind.STRING)},
        isPublic = true
)
public class GetHeader extends BlockingNativeCallableUnit {
    @Override
    public void execute(Context context) {
        BStruct pushPromiseStruct = (BStruct) context.getRefArgument(0);
        Http2PushPromise http2PushPromise =
                HttpUtil.getPushPromise(pushPromiseStruct, HttpUtil.createHttpPushPromise(pushPromiseStruct));
        String headerName = context.getStringArgument(0);
        context.setReturnValues(new BString(http2PushPromise.getHeader(headerName)));
    }
}
