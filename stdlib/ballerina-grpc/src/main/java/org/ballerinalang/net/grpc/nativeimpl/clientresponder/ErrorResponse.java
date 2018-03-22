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
package org.ballerinalang.net.grpc.nativeimpl.clientresponder;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.net.grpc.MessageUtils;

import static org.ballerinalang.net.grpc.MessageConstants.CLIENT_RESPONDER;
import static org.ballerinalang.net.grpc.MessageConstants.CLIENT_RESPONDER_REF_INDEX;
import static org.ballerinalang.net.grpc.MessageConstants.CONNECTOR_ERROR;
import static org.ballerinalang.net.grpc.MessageConstants.ORG_NAME;
import static org.ballerinalang.net.grpc.MessageConstants.PROTOCOL_PACKAGE_GRPC;
import static org.ballerinalang.net.grpc.MessageConstants.PROTOCOL_STRUCT_PACKAGE_GRPC;
import static org.ballerinalang.net.grpc.MessageConstants.RESPONSE_MESSAGE_REF_INDEX;
import static org.ballerinalang.net.grpc.MessageConstants.SERVER_ERROR;

/**
 * Native function to send server error the caller.
 *
 * @since 1.0.0
 */
@BallerinaFunction(
        orgName = ORG_NAME,
        packageName = PROTOCOL_PACKAGE_GRPC,
        functionName = "errorResponse",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = CLIENT_RESPONDER,
                structPackage = PROTOCOL_STRUCT_PACKAGE_GRPC),
        args = {
                @Argument(name = "serverError", type = TypeKind.STRUCT, structType = SERVER_ERROR,
                        structPackage = PROTOCOL_STRUCT_PACKAGE_GRPC)
        },
        returnType = {
                @ReturnType(type = TypeKind.STRUCT, structType = CONNECTOR_ERROR,
                        structPackage = PROTOCOL_STRUCT_PACKAGE_GRPC)
        },
        isPublic = true
)
public class ErrorResponse extends BlockingNativeCallableUnit {
    
    @Override
    public void execute(Context context) {
        BStruct endpointClient = (BStruct) context.getRefArgument(CLIENT_RESPONDER_REF_INDEX);
        BValue responseValue = context.getRefArgument(RESPONSE_MESSAGE_REF_INDEX);
        if (responseValue instanceof BStruct) {
            BStruct responseStruct = (BStruct) responseValue;
            int statusCode = Integer.parseInt(String.valueOf(responseStruct.getIntField(0)));
            String errorMsg = responseStruct.getStringField(0);
            StreamObserver responseObserver = MessageUtils.getResponseObserver(endpointClient);
            if (responseObserver == null) {
                context.setError(MessageUtils.getConnectorError(context, new StatusRuntimeException(Status
                        .fromCode(Status.INTERNAL.getCode()).withDescription("Error while sending the error. Response" +
                                " observer not found."))));
            } else {
                responseObserver.onError(new StatusRuntimeException(Status.fromCodeValue(statusCode).withDescription
                        (errorMsg)));
            }
        }
    }
}
