/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.launcher;

import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.codegen.Mnemonics;
import org.ballerinalang.util.codegen.PackageInfo;
import org.ballerinalang.util.codegen.ProgramFileReader;
import org.ballerinalang.util.program.BLangFunctions;
import org.wso2.ballerinalang.compiler.Compiler;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.CompilerOptions;
import org.wso2.ballerinalang.programfile.ProgramFileWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.ballerinalang.compiler.CompilerOptionName.COMPILER_PHASE;
import static org.ballerinalang.compiler.CompilerOptionName.PRESERVE_WHITESPACE;
import static org.ballerinalang.compiler.CompilerOptionName.SOURCE_ROOT;

/**
 * Tester class.
 */
public class BTester {

    private static CompilerOptions options;

    public static void main(String[] args) throws Exception {
        // -sorceroot == current directory

        CompilerContext context = new CompilerContext();
        options = CompilerOptions.getInstance(context);
        options.put(SOURCE_ROOT, System.getProperty("user.dir") + "/bal-src");
//        options.put(SOURCE_ROOT, "./");
        options.put(COMPILER_PHASE, "codeGen");
        options.put(PRESERVE_WHITESPACE, "false");

        // How to set a custom diagnostic listener
//        DiagnosticListener listener = diagnostic -> out.println(diagnostic.getMessage());
        //context.put(DiagnosticListener.class, listener);


        // How to set a custom program dir package repository
        //context.put(PackageRepository.class, repo);

        Compiler compiler = Compiler.getInstance(context);
//        compiler.compile("bar.bal");
        compiler.compile("pkg.bal");
//        compiler.compile("a.b.c");
        org.wso2.ballerinalang.programfile.ProgramFile programFile = compiler.getCompiledProgram();

        if (programFile != null) {
            org.ballerinalang.util.codegen.ProgramFile executableProgram = getExecutableProgram(programFile);
//            traceCode(executableProgram.getEntryPackage());
            BLangFunctions.invokeNew(executableProgram, executableProgram.getEntryPkgName(),
                                     "main", new BValue[1]);
        }
    }

    private static void traceCode(PackageInfo packageInfo) {
        PrintStream printStream = System.out;
        for (int i = 0; i < packageInfo.getInstructions().length; i++) {
            printStream.println(i + ": " + Mnemonics.getMnem(packageInfo.getInstructions()[i].getOpcode()) + " " +
                                        getOperandsLine(packageInfo.getInstructions()[i].getOperands()));
        }
    }

    private static String getOperandsLine(int[] operands) {
        if (operands.length == 0) {
            return "";
        }

        if (operands.length == 1) {
            return "" + operands[0];
        }

        StringBuilder sb = new StringBuilder();
        sb.append(operands[0]);
        for (int i = 1; i < operands.length; i++) {
            sb.append(" ");
            sb.append(operands[i]);
        }
        return sb.toString();
    }

    private static org.ballerinalang.util.codegen.ProgramFile getExecutableProgram(org.wso2.ballerinalang.programfile
                                                                                           .ProgramFile programFile) {
        ByteArrayInputStream byteIS = null;
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        try {
            ProgramFileWriter.writeProgram(programFile, byteOutStream);

            ProgramFileReader reader = new ProgramFileReader();
            byteIS = new ByteArrayInputStream(byteOutStream.toByteArray());
            return reader.readProgram(byteIS);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (byteIS != null) {
                try {
                    byteIS.close();
                } catch (IOException ignore) {
                }
            }

            try {
                byteOutStream.close();
            } catch (IOException ignore) {
            }
        }
    }
}
