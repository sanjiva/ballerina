/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ballerinalang.net.grpc.nativeimpl.headers;

import io.netty.handler.codec.http.HttpHeaders;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BStringArray;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;

import java.util.List;

import static org.ballerinalang.net.grpc.GrpcConstants.MESSAGE_HEADERS;
import static org.ballerinalang.net.grpc.GrpcConstants.ORG_NAME;
import static org.ballerinalang.net.grpc.GrpcConstants.PROTOCOL_PACKAGE_GRPC;
import static org.ballerinalang.net.grpc.GrpcConstants.PROTOCOL_STRUCT_PACKAGE_GRPC;

/**
 * Get the Headers of the Message.
 *
 * @since 1.0.0
 */
@BallerinaFunction(
        orgName = ORG_NAME,
        packageName = PROTOCOL_PACKAGE_GRPC,
        functionName = "getAll",
        receiver = @Receiver(type = TypeKind.OBJECT, structType = "Headers",
                structPackage = PROTOCOL_STRUCT_PACKAGE_GRPC),
        args = {@Argument(name = "headerName", type = TypeKind.STRING)},
        returnType = {@ReturnType(type = TypeKind.ARRAY)},
        isPublic = true
)
public class GetAll extends BlockingNativeCallableUnit {
    @Override
    public void execute(Context context) {
        String headerName = context.getStringArgument(0);
        BMap<String, BValue> headerValues = (BMap<String, BValue>) context.getRefArgument(0);
        HttpHeaders headers = headerValues != null ? (HttpHeaders) headerValues.getNativeData(MESSAGE_HEADERS) : null;
        List<String> headersList =  headers != null ? headers.getAll(headerName) : null;

        if (headersList != null) {
            String[] headerValue = new String[headersList.size()];
            headerValue = headers.getAll(headerName).toArray(headerValue);
            context.setReturnValues(new BStringArray(headerValue));
        } else {
            context.setReturnValues();
        }
    }
}
