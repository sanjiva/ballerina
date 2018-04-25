/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.testerina.util;

import org.ballerinalang.testerina.core.TesterinaRegistry;
import org.ballerinalang.util.codegen.PackageInfo;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.debugger.Debugger;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.ballerinalang.util.program.BLangFunctions;
import org.wso2.ballerinalang.compiler.util.Names;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * Utility methods.
 */
public class Utils {

    private static PrintStream errStream = System.err;
    private static TesterinaRegistry registry = TesterinaRegistry.getInstance();


    public static void startService(ProgramFile programFile) {
        if (!programFile.isServiceEPAvailable()) {
            throw new BallerinaException(String.format("no services found in package: %s", programFile
                    .getEntryPkgName()));
        }
        PackageInfo servicesPackage = programFile.getEntryPackage();

        Debugger debugger = new Debugger(programFile);
        initDebugger(programFile, debugger);

        // Invoke package init function
        if (isPackageInitialized(programFile.getEntryPkgName())) {
            BLangFunctions.invokePackageInitFunction(servicesPackage.getInitFunctionInfo());
            registry.addInitializedPackage(programFile.getEntryPkgName());
        }
        BLangFunctions.invokeVMUtilFunction(servicesPackage.getStartFunctionInfo());
    }

    public static boolean isPackageInitialized(String entryPkgName) {
        return !registry.getInitializedPackages().contains(entryPkgName);
    }

    /**
     * Cleans up any remaining testerina metadata.
     * @param path The path of the Directory/File to be deleted
     */
    public static void cleanUpDir(Path path) {
        try {
            if (Files.exists(path)) {
                Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
        } catch (IOException e) {
            errStream.println("Error occurred while deleting the dir : " + path.toString() + " with error : "
                              + e.getMessage());
        }
    }

    /**
     * Initialize the debugger.
     * @param programFile ballerina executable programFile
     * @param debugger Debugger instance
     */
    public static void initDebugger(ProgramFile programFile, Debugger debugger) {
        programFile.setDebugger(debugger);
        if (debugger.isDebugEnabled()) {
            debugger.init();
            debugger.waitTillDebuggeeResponds();
        }
    }

    /**
     * Returns the full package name with org name for a given package.
     * @param packageName package name
     * @return full package name with organization name if org name exists
     */
    public static String getFullPackageName(String packageName) {
        String orgName = registry.getOrgName();
        // If the orgName is null there is no package, .bal execution
        if (orgName == null) {
            return ".";
        }
        if (orgName.isEmpty() || orgName.equals(Names.ANON_ORG.toString())) {
            return packageName;
        }
       return orgName + "." + packageName;
    }
}
