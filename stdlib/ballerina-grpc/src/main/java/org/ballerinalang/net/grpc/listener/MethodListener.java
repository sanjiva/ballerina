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
package org.ballerinalang.net.grpc.listener;

import com.google.protobuf.Descriptors;
import io.grpc.stub.StreamObserver;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Executor;
import org.ballerinalang.connector.api.ParamDetail;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.model.types.BStructType;
import org.ballerinalang.model.types.BType;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.net.grpc.GrpcCallableUnitCallBack;
import org.ballerinalang.net.grpc.GrpcConstants;
import org.ballerinalang.net.grpc.Message;
import org.ballerinalang.net.grpc.MessageHeaders;
import org.ballerinalang.net.grpc.MessageUtils;
import org.ballerinalang.util.codegen.ProgramFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.ballerinalang.net.grpc.MessageHeaders.METADATA_KEY;
import static org.ballerinalang.net.grpc.MessageUtils.getHeaderStruct;
import static org.ballerinalang.net.grpc.MessageUtils.getProgramFile;

/**
 * Abstract Method listener.
 * This provide method for all method listeners.
 *
 * @since 1.0.0
 */
abstract class MethodListener {
    
    private Descriptors.MethodDescriptor methodDescriptor;
    private static final Logger LOG = LoggerFactory.getLogger(MethodListener.class);
    
    MethodListener(Descriptors.MethodDescriptor methodDescriptor) {
        this.methodDescriptor = methodDescriptor;
    }
    
    /**
     * Returns endpoint instance which is used to respond to the client.
     *
     * @param responseObserver client responder instance.
     * @return instance of endpoint type.
     */
    private BValue getConnectionParameter(Resource resource, StreamObserver<Message> responseObserver) {
        ProgramFile programFile = getProgramFile(resource);
        // generate client responder struct on request message with response observer and response msg type.
        BStruct clientEndpoint = BLangConnectorSPIUtil.createBStruct(programFile,
                GrpcConstants.PROTOCOL_STRUCT_PACKAGE_GRPC, GrpcConstants.CALLER_ACTION);
        clientEndpoint.addNativeData(GrpcConstants.RESPONSE_OBSERVER, responseObserver);
        clientEndpoint.addNativeData(GrpcConstants.RESPONSE_MESSAGE_DEFINITION, methodDescriptor.getOutputType());
        
        // create endpoint type instance on request.
        BStruct endpoint = BLangConnectorSPIUtil.createBStruct(programFile,
                GrpcConstants.PROTOCOL_STRUCT_PACKAGE_GRPC, GrpcConstants.SERVICE_ENDPOINT_TYPE);
        endpoint.setRefField(0, clientEndpoint);
        endpoint.setIntField(0, responseObserver.hashCode());
        return endpoint;
    }
    
    /**
     * Returns BValue object corresponding to the protobuf request message.
     *
     * @param requestMessage protobuf request message.
     * @return b7a message.
     */
    private BValue getRequestParameter(Resource resource, Message requestMessage, boolean isHeaderRequired) {
        if (resource == null || resource.getParamDetails() == null || resource.getParamDetails().size() > 3) {
            throw new RuntimeException("Invalid resource input arguments. arguments must not be greater than three");
        }
        
        List<ParamDetail> paramDetails = resource.getParamDetails();
        if ((isHeaderRequired && paramDetails.size() == 3) || (!isHeaderRequired && paramDetails.size() == 2)) {
            BType requestType = paramDetails.get(GrpcConstants.REQUEST_MESSAGE_PARAM_INDEX)
                    .getVarType();
            String requestName = paramDetails.get(GrpcConstants.REQUEST_MESSAGE_PARAM_INDEX)
                    .getVarName();
            return MessageUtils.generateRequestStruct(requestMessage, getProgramFile(resource), requestName,
                    requestType);
        } else {
            return null;
        }
    }
    
    /**
     * Checks whether service method has a response message.
     *
     * @return true if method response is empty, false otherwise
     */
    boolean isEmptyResponse() {
        return methodDescriptor != null && MessageUtils.isEmptyResponse(methodDescriptor.getOutputType());
    }
    
    void onErrorInvoke(Resource resource, StreamObserver<Message> responseObserver, Throwable t) {
        if (resource == null) {
            String message = "Error in listener service definition. onError resource does not exists";
            LOG.error(message);
            throw new RuntimeException(message);
        }
        List<ParamDetail> paramDetails = resource.getParamDetails();
        BValue[] signatureParams = new BValue[paramDetails.size()];
        signatureParams[0] = getConnectionParameter(resource, responseObserver);
        BType errorType = paramDetails.get(1).getVarType();
        BStruct errorStruct = MessageUtils.getConnectorError((BStructType) errorType, t);
        signatureParams[1] = errorStruct;
        BStruct headerStruct = getHeaderStruct(resource);
        if (headerStruct != null && MessageHeaders.isPresent()) {
            MessageHeaders context = MessageHeaders.current();
            headerStruct.addNativeData(METADATA_KEY, new MessageHeaders(context));
        }
        
        if (headerStruct != null && signatureParams.length == 3) {
            signatureParams[2] = headerStruct;
        }
        CallableUnitCallback callback = new GrpcCallableUnitCallBack(null);
        Executor.submit(resource, callback, null, null, signatureParams);
    }
    
    void onMessageInvoke(Resource resource, Message request, StreamObserver<Message> responseObserver) {
        CallableUnitCallback callback = new GrpcCallableUnitCallBack(responseObserver, isEmptyResponse());
        Executor.submit(resource, callback, null, null, computeMessageParams(resource, request, responseObserver));
    }
    
    BValue[] computeMessageParams(Resource resource, Message request, StreamObserver<Message> responseObserver) {
        List<ParamDetail> paramDetails = resource.getParamDetails();
        BValue[] signatureParams = new BValue[paramDetails.size()];
        signatureParams[0] = getConnectionParameter(resource, responseObserver);
        BStruct headerStruct = getHeaderStruct(resource);
        if (headerStruct != null && MessageHeaders.isPresent()) {
            MessageHeaders context = MessageHeaders.current();
            headerStruct.addNativeData(METADATA_KEY, new MessageHeaders(context));
        }
        BValue requestParam = getRequestParameter(resource, request, (headerStruct != null));
        if (requestParam != null) {
            signatureParams[1] = requestParam;
        }
        if (headerStruct != null) {
            signatureParams[signatureParams.length - 1] = headerStruct;
        }
        return signatureParams;
    }

    class DefaultStreamObserver implements StreamObserver<Message> {

        private final Map<String, Resource> resourceMap;
        private final StreamObserver<Message> responseObserver;

        DefaultStreamObserver(Map<String, Resource> resourceMap, StreamObserver<Message> responseObserver) {
            this.resourceMap = resourceMap;
            this.responseObserver = responseObserver;
        }

        @Override
        public void onNext(Message value) {
            Resource onMessage = resourceMap.get(GrpcConstants.ON_MESSAGE_RESOURCE);
            CallableUnitCallback callback = new GrpcCallableUnitCallBack(null);
            Executor.submit(onMessage, callback, null, null, computeMessageParams
                    (onMessage, value, responseObserver));
        }

        @Override
        public void onError(Throwable t) {
            Resource onError = resourceMap.get(GrpcConstants.ON_ERROR_RESOURCE);
            onErrorInvoke(onError, responseObserver, t);
        }

        @Override
        public void onCompleted() {
            Resource onCompleted = resourceMap.get(GrpcConstants.ON_COMPLETE_RESOURCE);
            if (onCompleted == null) {
                String message = "Error in listener service definition. onError resource does not exists";
                LOG.error(message);
                throw new RuntimeException(message);
            }

            CallableUnitCallback callback = new GrpcCallableUnitCallBack(responseObserver, Boolean.FALSE);
            Executor.submit(onCompleted, callback, null, null, computeMessageParams
                    (onCompleted, null, responseObserver));
        }
    };
}
