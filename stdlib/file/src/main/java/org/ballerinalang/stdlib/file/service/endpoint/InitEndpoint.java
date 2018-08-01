/*
 * Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.stdlib.file.service.endpoint;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BLangVMErrors;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.stdlib.file.service.DirectoryListenerConstants;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Initialize endpoints.
 */

@BallerinaFunction(
        orgName = "ballerina",
        packageName = "file",
        functionName = "initEndpoint",
        receiver = @Receiver(type = TypeKind.OBJECT, structType = "Listener", structPackage = "ballerina/file"),
        args = {@Argument(name = "config", type = TypeKind.RECORD, structType = "ListenerEndpointConfiguration",
                          structPackage = "ballerina/file")
        },
        isPublic = true
)
public class InitEndpoint extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        Struct serviceEndpoint = BLangConnectorSPIUtil.getConnectorEndpointStruct(context);
        Struct serviceEndpointConfig = serviceEndpoint
                .getStructField(DirectoryListenerConstants.SERVICE_ENDPOINT_CONFIG);
        final String path = serviceEndpointConfig.getStringField(DirectoryListenerConstants.ANNOTATION_PATH);
        final Path dirPath = Paths.get(path);
        if (Files.notExists(dirPath)) {
            context.setReturnValues(BLangVMErrors.createError(context, "Folder does not exist: " + path));
            return;
        }
        if (!Files.isDirectory(dirPath)) {
            context.setReturnValues(BLangVMErrors.createError(context, "Unable to find a directory : " + path));
            return;
        }
        context.setReturnValues();
    }
}
