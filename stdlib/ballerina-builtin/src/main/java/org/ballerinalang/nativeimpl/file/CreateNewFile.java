/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.nativeimpl.file;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BLangVMErrors;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Creates the file at the path specified in the File struct.
 *
 * @since 0.94.1
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "file",
        functionName = "createNewFile",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = "File", structPackage = "ballerina.file"),
        returnType = {@ReturnType(type = TypeKind.BOOLEAN), @ReturnType(type = TypeKind.STRUCT),
                @ReturnType(type = TypeKind.STRUCT)},
        isPublic = true
)
public class CreateNewFile extends BlockingNativeCallableUnit {

    private static final Logger log = LoggerFactory.getLogger(CreateNewFile.class);

    @Override
    public void execute(Context context) {
        BStruct fileStruct = (BStruct) context.getRefArgument(0);
        Path filePath = Paths.get(fileStruct.getStringField(0));
        Path newFile;
        try {
            newFile = Files.createFile(filePath);
            context.setReturnValues(new BBoolean(Files.exists(newFile)));
        } catch (IOException | UnsupportedOperationException | SecurityException e) {
            String msg;
            if (e instanceof SecurityException) {
                msg = "Permission denied. Failed to create the file: " + filePath.toString();
            } else {
                msg = "Failed to create the file: " + filePath.toString();   
            }
            log.error(msg, e);
            context.setReturnValues(BLangVMErrors.createError(context, msg));
        }
    }
}
