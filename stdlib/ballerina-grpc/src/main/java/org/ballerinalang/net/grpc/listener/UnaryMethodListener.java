/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
package org.ballerinalang.net.grpc.listener;

import com.google.protobuf.Descriptors;
import io.grpc.stub.ServerCalls.UnaryMethod;
import io.grpc.stub.StreamObserver;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.Executor;
import org.ballerinalang.connector.api.ParamDetail;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.net.grpc.GrpcCallableUnitCallBack;
import org.ballerinalang.net.grpc.Message;

import java.util.List;


/**
 * This is Unary Method Implementation for gRPC Service Call.
 *
 * @since 1.0.0
 */
public class UnaryMethodListener extends MethodListener implements UnaryMethod<Message, Message> {

    public Resource resource;

    public UnaryMethodListener(Descriptors.MethodDescriptor methodDescriptor, Resource resource) {
        super(methodDescriptor);
        this.resource = resource;
    }

    @Override
    public void invoke(Message request, StreamObserver<Message> responseObserver) {
        List<ParamDetail> paramDetails = resource.getParamDetails();
        BValue[] signatureParams = new BValue[paramDetails.size()];
        signatureParams[0] = getConnectionParameter(resource, responseObserver);
        BValue requestParam = getRequestParameter(resource, request);
        if (requestParam != null) {
            signatureParams[1] = requestParam;
        }
        CallableUnitCallback callback = new GrpcCallableUnitCallBack(responseObserver, isEmptyResponse());
        Executor.submit(resource, callback, null, signatureParams);
    }

}
