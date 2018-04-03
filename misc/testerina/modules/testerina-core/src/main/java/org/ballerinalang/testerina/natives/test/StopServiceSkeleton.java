    /*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.ballerinalang.testerina.natives.test;

import org.apache.commons.io.FileUtils;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.nativeimpl.io.BallerinaIOException;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.Attribute;
import org.ballerinalang.natives.annotations.BallerinaAnnotation;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.testerina.core.TesterinaRegistry;
import org.ballerinalang.util.program.BLangFunctions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Native function ballerina.test:stopServiceSkeleton.
 * Stop a service skeleton and cleanup the directories that got created.
 *
 * @since 0.97.0
 */
@BallerinaFunction(orgName = "ballerina", packageName = "test", functionName = "stopServiceSkeleton", args =
        {@Argument(name = "packageName", type = TypeKind.STRING)}, isPublic = true)
@BallerinaAnnotation(annotationName = "Description", attributes = {@Attribute(name = "value", value = "Stop a " +
        "service skeleton and cleanup created directories of a given ballerina package.")})
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "packageName", value = "Name of the " +
        "package")})
public class StopServiceSkeleton extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context ctx) {
        String packageName = ctx.getStringArgument(0);

        TesterinaRegistry.getInstance().getSkeletonProgramFiles().forEach(skeletonProgramFile -> {
            if (skeletonProgramFile.getEntryPkgName().equals(packageName)) {
                Path sourceRoot = skeletonProgramFile.getProgramFilePath();
                Path projectRoot = Paths.get(sourceRoot.toString(), ".ballerina");
                Path projectDir = Paths.get(sourceRoot.toString(), packageName);

                // stop the service
                BLangFunctions.invokeVMUtilFunction(skeletonProgramFile.getEntryPackage().getStopFunctionInfo());

                try {
                    // cleanup .ballerina folder
                    if (projectRoot.toFile().list().length == 0) {
                        // delete iff empty
                        Files.delete(projectRoot);
                    }
                } catch (IOException e) {
                    throw new BallerinaIOException(String.format("Service skeleton cleanup failed. Failed to " +
                            "delete [.ballerina] %s [cause] %s", projectRoot.toString(), e.getMessage()), e);
                }

                try {
                    // cleanup service skeleton package
                    FileUtils.deleteDirectory(projectDir.toFile());
                } catch (IOException e) {
                    throw new BallerinaIOException(String.format("Service skeleton cleanup failed. Failed to " +
                            "delete [package] %s [cause] %s", projectDir.toString(), e.getMessage()), e);
                }
                return;
            }
        });
    }

}
