/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.net.grpc.proto;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import org.ballerinalang.connector.api.Annotation;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.tree.AnnotationAttachmentNode;
import org.ballerinalang.model.tree.ResourceNode;
import org.ballerinalang.model.tree.ServiceNode;
import org.ballerinalang.model.tree.statements.BlockNode;
import org.ballerinalang.model.tree.statements.StatementNode;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.net.grpc.config.ServiceConfiguration;
import org.ballerinalang.net.grpc.exception.GrpcServerException;
import org.ballerinalang.net.grpc.proto.definition.EmptyMessage;
import org.ballerinalang.net.grpc.proto.definition.EnumField;
import org.ballerinalang.net.grpc.proto.definition.Field;
import org.ballerinalang.net.grpc.proto.definition.File;
import org.ballerinalang.net.grpc.proto.definition.Message;
import org.ballerinalang.net.grpc.proto.definition.MessageKind;
import org.ballerinalang.net.grpc.proto.definition.Method;
import org.ballerinalang.net.grpc.proto.definition.Service;
import org.ballerinalang.net.grpc.proto.definition.StandardDescriptorBuilder;
import org.ballerinalang.net.grpc.proto.definition.UserDefinedEnumMessage;
import org.ballerinalang.net.grpc.proto.definition.UserDefinedMessage;
import org.ballerinalang.net.grpc.proto.definition.WrapperMessage;
import org.wso2.ballerinalang.compiler.semantics.model.Scope;
import org.wso2.ballerinalang.compiler.semantics.model.types.BArrayType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BEnumType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BField;
import org.wso2.ballerinalang.compiler.semantics.model.types.BNilType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BStructureType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.tree.BLangNode;
import org.wso2.ballerinalang.compiler.tree.BLangVariable;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangInvocation;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordLiteral;
import org.wso2.ballerinalang.compiler.tree.statements.BLangAssignment;
import org.wso2.ballerinalang.compiler.tree.statements.BLangBlockStmt;
import org.wso2.ballerinalang.compiler.tree.statements.BLangForeach;
import org.wso2.ballerinalang.compiler.tree.statements.BLangIf;
import org.wso2.ballerinalang.compiler.tree.statements.BLangMatch;
import org.wso2.ballerinalang.compiler.tree.statements.BLangVariableDef;
import org.wso2.ballerinalang.compiler.tree.statements.BLangWhile;
import org.wso2.ballerinalang.compiler.util.Name;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.ballerinalang.net.grpc.GrpcConstants.ANN_ATTR_RESOURCE_SERVER_STREAM;
import static org.ballerinalang.net.grpc.GrpcConstants.ANN_RESOURCE_CONFIG;
import static org.ballerinalang.net.grpc.GrpcConstants.ON_COMPLETE_RESOURCE;
import static org.ballerinalang.net.grpc.GrpcConstants.ON_MESSAGE_RESOURCE;
import static org.ballerinalang.net.grpc.GrpcConstants.WRAPPER_BOOL_MESSAGE;
import static org.ballerinalang.net.grpc.GrpcConstants.WRAPPER_FLOAT_MESSAGE;
import static org.ballerinalang.net.grpc.GrpcConstants.WRAPPER_INT64_MESSAGE;
import static org.ballerinalang.net.grpc.GrpcConstants.WRAPPER_STRING_MESSAGE;

/**
 * Utility class providing proto file based of the Ballerina service.
 *
 * @since 1.0.0
 */
public class ServiceProtoUtils {

    private static final String NO_PACKAGE_PATH = ".";

    private ServiceProtoUtils() {

    }

    static File generateProtoDefinition(ServiceNode serviceNode) throws GrpcServerException {
        // Protobuf file definition builder.
        String packageName = serviceNode.getPosition().getSource().getPackageName();
        File.Builder fileBuilder;
        if (!NO_PACKAGE_PATH.equals(packageName)) {
            fileBuilder = File.newBuilder(serviceNode.getName() + ServiceProtoConstants.PROTO_FILE_EXTENSION)
                    .setSyntax(ServiceProtoConstants.PROTOCOL_SYNTAX).setPackage(serviceNode.getPosition().getSource()
                            .getPackageName());
        } else {
            fileBuilder = File.newBuilder(serviceNode.getName() + ServiceProtoConstants.PROTO_FILE_EXTENSION)
                    .setSyntax(ServiceProtoConstants.PROTOCOL_SYNTAX);
        }
        ServiceConfiguration serviceConfig = getServiceConfiguration(serviceNode);
        Service serviceDefinition;
        if (serviceConfig.getRpcEndpoint() != null && (serviceConfig.isClientStreaming())) {
            serviceDefinition = getStreamingServiceDefinition(serviceNode, serviceConfig, fileBuilder);
        } else {
            serviceDefinition = getUnaryServiceDefinition(serviceNode, fileBuilder);
        }
        
        fileBuilder.setService(serviceDefinition);
        return fileBuilder.build();
    }
    
    static ServiceConfiguration getServiceConfiguration(ServiceNode serviceNode) {
        String rpcEndpoint = null;
        boolean clientStreaming = false;
        boolean serverStreaming = false;
        
        for (AnnotationAttachmentNode annotationNode : serviceNode.getAnnotationAttachments()) {
            if (!ServiceProtoConstants.ANN_SERVICE_CONFIG.equals(annotationNode.getAnnotationName().getValue())) {
                continue;
            }
            if (annotationNode.getExpression() instanceof BLangRecordLiteral) {
                List<BLangRecordLiteral.BLangRecordKeyValue> attributes = ((BLangRecordLiteral) annotationNode
                        .getExpression()).getKeyValuePairs();
                for (BLangRecordLiteral.BLangRecordKeyValue attributeNode : attributes) {
                    String attributeName = attributeNode.getKey().toString();
                    String attributeValue = attributeNode.getValue() != null ? attributeNode.getValue().toString() :
                            null;
                    
                    switch (attributeName) {
                        case ServiceProtoConstants.SERVICE_CONFIG_RPC_ENDPOINT: {
                            rpcEndpoint = attributeValue;
                            break;
                        }
                        case ServiceProtoConstants.SERVICE_CONFIG_CLIENT_STREAMING: {
                            clientStreaming = attributeValue != null && Boolean.parseBoolean(attributeValue);
                            break;
                        }
                        case ServiceProtoConstants.SERVICE_CONFIG_SERVER_STREAMING: {
                            serverStreaming = attributeValue != null && Boolean.parseBoolean(attributeValue);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            }
        }
        return new ServiceConfiguration(rpcEndpoint, clientStreaming, serverStreaming);
    }
    
    private static Service getUnaryServiceDefinition(ServiceNode serviceNode, File.Builder fileBuilder) throws
            GrpcServerException {
        // Protobuf service definition builder.
        Service.Builder serviceBuilder = Service.newBuilder(serviceNode.getName().getValue());
        
        for (ResourceNode resourceNode : serviceNode.getResources()) {
            Message requestMessage = getRequestMessage(resourceNode);
            if (requestMessage == null) {
                throw new GrpcServerException("Error while deriving request message of the resource");
            }
            if (requestMessage.getMessageKind() == MessageKind.USER_DEFINED) {
                updateFileBuilder(fileBuilder, requestMessage);
            }
            if (requestMessage.getDependency() != null && !fileBuilder.getRegisteredDependencies().contains
                    (requestMessage.getDependency())) {
                fileBuilder.setDependency(requestMessage.getDependency());
            }

            Message responseMessage = getResponseMessage(resourceNode);
            if (responseMessage == null) {
                throw new GrpcServerException("Connection send expression not found in resource body");
            }
            if (responseMessage.getMessageKind() == MessageKind.USER_DEFINED) {
                updateFileBuilder(fileBuilder, responseMessage);
            }
            if (responseMessage.getDependency() != null && !fileBuilder.getRegisteredDependencies().contains
                    (responseMessage.getDependency())) {
                fileBuilder.setDependency(responseMessage.getDependency());
            }
            
            boolean serverStreaming = isServerStreaming(resourceNode);
            Method resourceMethod = Method.newBuilder(resourceNode.getName().getValue())
                    .setClientStreaming(false)
                    .setServerStreaming(serverStreaming)
                    .setInputType(requestMessage.getCanonicalName())
                    .setOutputType(responseMessage.getCanonicalName())
                    .build();
            serviceBuilder.addMethod(resourceMethod);
        }
        return serviceBuilder.build();
    }

    private static boolean isNewMessageDefinition(File.Builder fileBuilder, Message message) {
        for (DescriptorProtos.DescriptorProto messageProto : fileBuilder.getRegisteredMessages()) {
            if (messageProto.getName() == null) {
                continue;
            }
            if (messageProto.getName().equals(message.getSimpleName())) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNewEnumDefinition(File.Builder fileBuilder, Message enumMsg) {
        for (DescriptorProtos.EnumDescriptorProto enumDescriptorProto : fileBuilder.getRegisteredEnums()) {
            if (enumDescriptorProto.getName() == null) {
                continue;
            }
            if (enumDescriptorProto.getName().equals(enumMsg.getSimpleName())) {
                return false;
            }
        }
        return true;
    }

    private static Service getStreamingServiceDefinition(ServiceNode serviceNode, ServiceConfiguration serviceConfig,
                                                         File.Builder fileBuilder) throws GrpcServerException {
        // Protobuf service definition builder.
        Service.Builder serviceBuilder = Service.newBuilder(serviceNode.getName().getValue());
        Message requestMessage = null;
        Message responseMessage = null;
        for (ResourceNode resourceNode : serviceNode.getResources()) {
            if (ON_MESSAGE_RESOURCE.equals(resourceNode.getName().getValue())) {
                requestMessage = getRequestMessage(resourceNode);
                Message respMsg = getResponseMessage(resourceNode);
                if (respMsg != null && !(MessageKind.EMPTY.equals(respMsg.getMessageKind()))) {
                    responseMessage = respMsg;
                    break;
                }
            }
            
            if (ON_COMPLETE_RESOURCE.equals(resourceNode.getName().getValue())) {
                Message respMsg = getResponseMessage(resourceNode);
                if (respMsg != null && !(MessageKind.EMPTY.equals(respMsg.getMessageKind()))) {
                    responseMessage = respMsg;
                }
            }
        }
        // if we cannot retrieve request/response messages. assuming it is empty type.
        if (requestMessage == null) {
            requestMessage = generateMessageDefinition(new BNilType());
        }
        if (responseMessage == null) {
            responseMessage = generateMessageDefinition(new BNilType());
        }
        // update file builder with request/response messages.
        if (requestMessage.getMessageKind() == MessageKind.USER_DEFINED) {
            updateFileBuilder(fileBuilder, requestMessage);
        }
        if (requestMessage.getDependency() != null && !fileBuilder.getRegisteredDependencies().contains
                (requestMessage.getDependency())) {
            fileBuilder.setDependency(requestMessage.getDependency());
        }
        if (responseMessage.getMessageKind() == MessageKind.USER_DEFINED) {
            updateFileBuilder(fileBuilder, responseMessage);
        }
        if (responseMessage.getDependency() != null && !fileBuilder.getRegisteredDependencies().contains
                (responseMessage.getDependency())) {
            fileBuilder.setDependency(responseMessage.getDependency());
        }
        Method resourceMethod = Method.newBuilder(serviceConfig.getRpcEndpoint())
                .setClientStreaming(serviceConfig.isClientStreaming())
                .setServerStreaming(serviceConfig.isServerStreaming())
                .setInputType(requestMessage.getCanonicalName())
                .setOutputType(responseMessage.getCanonicalName())
                .build();
        serviceBuilder.addMethod(resourceMethod);
        return serviceBuilder.build();
    }

    private static void updateFileBuilder(File.Builder fileBuilder, Message message) {
        if (isNewMessageDefinition(fileBuilder, message)) {
            fileBuilder.setMessage(message);
        }
        for (UserDefinedMessage msg : message.getNestedMessageList()) {
            if (isNewMessageDefinition(fileBuilder, msg)) {
                fileBuilder.setMessage(msg);
            }
        }
        for (UserDefinedEnumMessage enumMessage : message.getNestedEnumList()) {
            if (isNewEnumDefinition(fileBuilder, enumMessage)) {
                fileBuilder.setEnum(enumMessage);
            }
        }
    }

    private static boolean isServerStreaming(ResourceNode resourceNode) {
        boolean serverStreaming = false;
        for (AnnotationAttachmentNode annotationNode : resourceNode.getAnnotationAttachments()) {
            if (!ANN_RESOURCE_CONFIG.equals(annotationNode.getAnnotationName().getValue())) {
                continue;
            }
            
            if (annotationNode.getExpression() instanceof BLangRecordLiteral) {
                List<BLangRecordLiteral.BLangRecordKeyValue> attributes = ((BLangRecordLiteral) annotationNode
                        .getExpression()).getKeyValuePairs();
                for (BLangRecordLiteral.BLangRecordKeyValue attributeNode : attributes) {
                    String attributeName = attributeNode.getKey().toString();
                    String attributeValue = attributeNode.getValue() != null ? attributeNode.getValue().toString() :
                            null;
                    if (ANN_ATTR_RESOURCE_SERVER_STREAM.equals(attributeName)) {
                        serverStreaming = attributeValue != null && Boolean.parseBoolean(attributeValue);
                    }
                }
            }
        }
        return serverStreaming;
    }
    
    private static Message getResponseMessage(ResourceNode resourceNode) throws GrpcServerException {
        org.wso2.ballerinalang.compiler.semantics.model.types.BType responseType;
        BLangInvocation sendExpression = getInvocationExpression(resourceNode.getBody());
        if (sendExpression != null) {
            responseType = getReturnType(sendExpression);
        } else {
            // if compiler plugin could not find
            responseType = new BNilType();
        }
        return generateMessageDefinition(responseType);
    }
    
    private static Message getRequestMessage(ResourceNode resourceNode) throws GrpcServerException {
        if (!(!resourceNode.getParameters().isEmpty() || resourceNode.getParameters().size() < 4)) {
            throw new GrpcServerException("Service resource definition should contain more than one and less than " +
                    "four params. but contains " + resourceNode.getParameters().size());
        }
        BType requestType = getMessageParamType(resourceNode.getParameters());
        return generateMessageDefinition(requestType);
    }

    private static BType getMessageParamType(List<?> variableNodes) throws GrpcServerException {
        BType requestType = null;
        for (Object variable : variableNodes)  {
            if (variable instanceof BLangNode) {
                BType tempType = ((BLangNode) variable).type;
                if (tempType.getKind().equals(TypeKind.ARRAY)) {
                    requestType = tempType;
                    break;
                }
                // union type and tuple type doesn't have type symbol. If those values pass as response, compiler
                // failed to derive response message type. Validate and send proper error message.
                if (tempType.tsymbol == null) {
                    throw new GrpcServerException("Invalid message type. Message type doesn't have type symbol");
                }

                if ("ballerina/grpc:Listener".equals(tempType.tsymbol.toString()) || "ballerina/grpc:Headers"
                        .equals(tempType.tsymbol.toString())) {
                    continue;
                }
                requestType = tempType;
                break;
            } else {
                throw new GrpcServerException("Request Message type is not supported, should be lang variable.");
            }
        }
        if (requestType == null) {
            requestType = new BNilType();
        }
        return requestType;
    }

    /**
     * Returns protobuf message definition from ballerina struct type.
     *
     * @param messageType ballerina struct type
     * @return message definition
     */
    private static Message generateMessageDefinition(org.wso2.ballerinalang.compiler.semantics.model.types.BType
                                                             messageType) throws GrpcServerException {
        Message message = null;
        switch (messageType.getKind()) {
            case STRING: {
                message = WrapperMessage.newBuilder(WRAPPER_STRING_MESSAGE).build();
                break;
            }
            case INT: {
                message = WrapperMessage.newBuilder(WRAPPER_INT64_MESSAGE).build();
                break;
            }
            case FLOAT: {
                message = WrapperMessage.newBuilder(WRAPPER_FLOAT_MESSAGE).build();
                break;
            }
            case BOOLEAN: {
                message = WrapperMessage.newBuilder(WRAPPER_BOOL_MESSAGE).build();
                break;
            }
            case OBJECT:
            case RECORD: {
                if (messageType instanceof org.wso2.ballerinalang.compiler.semantics.model.types.BStructureType) {
                    org.wso2.ballerinalang.compiler.semantics.model.types.BStructureType structType = (org
                            .wso2.ballerinalang.compiler.semantics.model.types.BStructureType) messageType;
                    message = getStructMessage(structType);
                }
                break;
            }
            case ENUM: {
                if (messageType instanceof org.wso2.ballerinalang.compiler.semantics.model.types.BEnumType) {
                    org.wso2.ballerinalang.compiler.semantics.model.types.BEnumType enumType = (org
                            .wso2.ballerinalang.compiler.semantics.model.types.BEnumType) messageType;
                    message = getEnumMessage(enumType);
                }
                break;
            }
            case NIL: {
                message = EmptyMessage.newBuilder().build();
                break;
            }
            default: {
                throw new GrpcServerException("Unsupported field type, field type " + messageType.getKind()
                        .typeName() + " currently not supported.");
            }
        }
        return message;
    }
    
    private static Message getStructMessage(BStructureType messageType) throws
            GrpcServerException {
        UserDefinedMessage.Builder messageBuilder = UserDefinedMessage.newBuilder(messageType.tsymbol.name.value);
        int fieldIndex = 0;
        for (BField structField : messageType.fields) {
            Field messageField;
            String fieldName = structField.getName().getValue();
            BType fieldType = structField.getType();
            String fieldLabel = null;
            if (fieldType instanceof BEnumType) {
                BEnumType enumType = (BEnumType) fieldType;
                messageBuilder.addMessageDefinition(getEnumMessage(enumType));
            } else if (fieldType instanceof BStructureType) {
                BStructureType structType = (BStructureType) fieldType;
                messageBuilder.addMessageDefinition(getStructMessage(structType));
            } else if (fieldType instanceof BArrayType) {
                BArrayType arrayType = (BArrayType) fieldType;
                BType elementType = arrayType.getElementType();
                if (elementType instanceof BStructureType) {
                    messageBuilder.addMessageDefinition(getStructMessage((BStructureType) elementType));
                }
                fieldType = elementType;
                fieldLabel = "repeated";
            }
            messageField = Field.newBuilder(fieldName)
                    .setIndex(++fieldIndex)
                    .setLabel(fieldLabel)
                    .setType(fieldType).build();
            messageBuilder.addFieldDefinition(messageField);
        }
        return messageBuilder.build();
    }

    private static UserDefinedEnumMessage getEnumMessage(BEnumType messageType) throws GrpcServerException {
        UserDefinedEnumMessage.Builder messageBuilder = UserDefinedEnumMessage.newBuilder(messageType.toString());
        int fieldIndex = 0;
        Map<Name, Scope.ScopeEntry> enumField = (messageType.tsymbol.scope.entries);
        for (Map.Entry<Name, Scope.ScopeEntry> field : enumField.entrySet()) {
            String fieldName = field.getKey().getValue();
            EnumField messageField = EnumField.newBuilder().setName(fieldName).setIndex(fieldIndex++).build();
            messageBuilder.addFieldDefinition(messageField);
        }
        return messageBuilder.build();
    }
    
    private static BLangInvocation getInvocationExpression(BlockNode body) {
        if (body == null) {
            return null;
        }
        for (StatementNode statementNode : body.getStatements()) {
            BLangExpression expression = null;
            // send inside while block.
            if (statementNode instanceof BLangWhile) {
                BLangWhile langWhile = (BLangWhile) statementNode;
                BLangInvocation invocExp = getInvocationExpression(langWhile.getBody());
                if (invocExp != null) {
                    return invocExp;
                }
            }
            // send inside for block.
            if (statementNode instanceof BLangForeach) {
                BLangForeach langForeach = (BLangForeach) statementNode;
                BLangInvocation invocExp = getInvocationExpression(langForeach.getBody());
                if (invocExp != null) {
                    return invocExp;
                }
            }
            // send inside if block.
            if (statementNode instanceof BLangIf) {
                BLangIf langIf = (BLangIf) statementNode;
                BLangInvocation invocExp = getInvocationExpression(langIf.getBody());
                if (invocExp != null) {
                    return invocExp;
                }
                invocExp = getInvocationExpression((BLangBlockStmt) langIf.getElseStatement());
                if (invocExp != null) {
                    return invocExp;
                }
            }
            //send inside match block.
            if (statementNode instanceof BLangMatch) {
                BLangMatch langMatch = (BLangMatch) statementNode;
                for (BLangMatch.BLangMatchStmtPatternClause patternClause : langMatch.patternClauses) {
                    BLangInvocation invocExp = getInvocationExpression(patternClause.body);
                    if (invocExp != null) {
                        return invocExp;
                    }
                }
            }
            // ignore return value of send method.
            if (statementNode instanceof BLangAssignment) {
                BLangAssignment assignment = (BLangAssignment) statementNode;
                expression = assignment.getExpression();
            }
            // variable assignment.
            if (statementNode instanceof BLangVariableDef) {
                BLangVariableDef variableDef = (BLangVariableDef) statementNode;
                BLangVariable variable = variableDef.getVariable();
                expression = variable.getInitialExpression();
            }
            
            if (expression != null && expression instanceof BLangInvocation) {
                BLangInvocation invocation = (BLangInvocation) expression;
                if ("send".equals(invocation.getName().getValue())) {
                    return invocation;
                }
            }
        }
        return null;
    }
    
    private static org.wso2.ballerinalang.compiler.semantics.model.types.BType getReturnType(BLangInvocation invocation)
            throws GrpcServerException {
        if (invocation.getArgumentExpressions() != null &&
                invocation.getArgumentExpressions().size() > 2) {
            throw new GrpcServerException("Incorrect argument expressions defined in send function: " +
                    invocation.toString());
        }
        return getMessageParamType(invocation.getArgumentExpressions());
    }

    /**
     * Returns file descriptor for the service.
     * Reads file descriptor from internal annotation attached to the service at compile time.
     *
     * @param service gRPC service.
     * @return File Descriptor of the service.
     */
    public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor(
            org.ballerinalang.connector.api.Service service) throws GrpcServerException {
        try {
            List<Annotation> annotationList = service.getAnnotationList("ballerina/grpc", "ServiceDescriptor");
            if (annotationList == null || annotationList.size() != 1) {
                throw new GrpcServerException("Couldn't find the service descriptor.");
            }
            Annotation descriptorAnn = annotationList.get(0);
            Struct descriptorStruct = descriptorAnn.getValue();
            if (descriptorStruct == null) {
                throw new GrpcServerException("Couldn't find the service descriptor.");
            }
            String descriptorData = descriptorStruct.getStringField("descriptor");
            byte[] descriptor = hexStringToByteArray(descriptorData);
            DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.parseFrom(descriptor);
            return Descriptors.FileDescriptor.buildFrom(proto,
                    StandardDescriptorBuilder.getFileDescriptors(proto.getDependencyList().toArray()));
        } catch (IOException | Descriptors.DescriptorValidationException e) {
            throw new GrpcServerException("Error while reading the service proto descriptor. check the service " +
                    "implementation. ", e);
        }
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    
    /**
     * Write protobuf file definition content to the filename.
     *
     * @param protoFileDefinition protobuf file definition.
     * @param filename            filename
     * @throws GrpcServerException when error occur while writing content to file.
     */
    static void writeServiceFiles(Path targetDirPath, String filename, File protoFileDefinition)
            throws GrpcServerException {
        if (targetDirPath == null) {
            throw new GrpcServerException("Target file directory path is null");
        }
        try {
            // create parent directory. if doesn't exist.
            if (!Files.exists(targetDirPath)) {
                Files.createDirectories(targetDirPath);
            }
            // write the proto string to the file in protobuf contract directory
            Path protoFilePath = Paths.get(targetDirPath.toString(), filename + ServiceProtoConstants
                    .PROTO_FILE_EXTENSION);
            Files.write(protoFilePath, protoFileDefinition.getFileDefinition().getBytes(ServiceProtoConstants
                    .UTF_8_CHARSET));
        } catch (IOException e) {
            throw new GrpcServerException("Error while writing file descriptor to file.", e);
        }
    }
}
