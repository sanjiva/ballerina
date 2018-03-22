/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.testerina.core;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.Parameters;
import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.launcher.BLauncherCmd;
import org.ballerinalang.launcher.LauncherUtils;
import org.ballerinalang.logging.BLogManager;
import org.wso2.ballerinalang.compiler.FileSystemProjectDirectory;
import org.wso2.ballerinalang.compiler.SourceDirectory;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;

/**
 * Test command for ballerina launcher.
 */
@Parameters(commandNames = "test", commandDescription = "test Ballerina program")
public class TestCmd implements BLauncherCmd {

    private static final PrintStream errStream = System.err;
    private static final PrintStream outStream = System.out;


    private JCommander parentCmdParser;

    @Parameter(arity = 1, description = "ballerina package/s to be tested")
    private List<String> sourceFileList;

    @Parameter(names = { "--help", "-h" }, hidden = true)
    private boolean helpFlag;

    @Parameter(names = "--debug", hidden = true)
    private String debugPort;

    @Parameter(names = "--ballerina.debug", hidden = true, description = "remote debugging port")
    private String ballerinaDebugPort;

    @Parameter(names = "--groups", description = "test groups to be executed")
    private List<String> groupList;

    @Parameter(names = "--disable-groups", description = "test groups to be disabled")
    private List<String> disableGroupList;

    @Parameter(names = {"--sourceroot"}, description = "path to the directory containing source files and packages")
    private String sourceRoot;

    @DynamicParameter(names = "-B", description = "collects dynamic parameters")
    private Map<String, String> configRuntimeParams = new HashMap<>();

    public void execute() {
        if (helpFlag) {
            printCommandUsageInfo(parentCmdParser, "test");
            return;
        }

        if (sourceFileList == null || sourceFileList.isEmpty()) {
            Path userDir = Paths.get(System.getProperty("user.dir"));
            SourceDirectory srcDirectory = new FileSystemProjectDirectory(userDir);
            sourceFileList = srcDirectory.getSourcePackageNames();
        }

        if (groupList != null && disableGroupList != null) {
            throw LauncherUtils
                    .createUsageException("Cannot specify both --groups and --disable-groups flags at the same time");
        }

        Path sourceRootPath = LauncherUtils.getSourceRootPath(sourceRoot);
        Path ballerinaConfPath = sourceRootPath.resolve("ballerina.conf");
        try {
            ConfigRegistry.getInstance().initRegistry(configRuntimeParams, null, ballerinaConfPath);
            ((BLogManager) LogManager.getLogManager()).loadUserProvidedLogConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("failed to read the specified configuration file: " + ballerinaConfPath
                    .toString(), e);
        }

        Path[] paths = sourceFileList.stream().map(Paths::get).toArray(Path[]::new);

        BTestRunner testRunner = new BTestRunner();
        if (disableGroupList != null) {
            testRunner.runTest(sourceRoot, paths, disableGroupList, false);
        } else {
            testRunner.runTest(sourceRoot, paths, groupList, true);
        }
        if (testRunner.getTesterinaReport().isFailure()) {
            Runtime.getRuntime().exit(1);
        }
        Runtime.getRuntime().exit(0);
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("Complies and Run Ballerina test sources (*_test.bal) and prints " + System.lineSeparator());
        out.append("a summary of test results" + System.lineSeparator() + System.lineSeparator());
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append("ballerina test <filename>" + System.lineSeparator());
        stringBuilder.append("ballerina test command will have -mock flag enabled by default" + System.lineSeparator());
        stringBuilder.append(System.lineSeparator());
    }

    private static void printCommandUsageInfo(JCommander cmdParser, String commandName) {
        StringBuilder out = new StringBuilder();
        JCommander jCommander = cmdParser.getCommands().get(commandName);
        BLauncherCmd bLauncherCmd = (BLauncherCmd) jCommander.getObjects().get(0);

        out.append(cmdParser.getCommandDescription(commandName)).append("\n");
        out.append("\n");
        out.append("Usage:\n");
        bLauncherCmd.printUsage(out);
        out.append("\n");

        if (jCommander.getCommands().values().size() != 0) {
            out.append("Available Commands:\n");
            printCommandList(jCommander, out);
            out.append("\n");
        }

        printFlags(jCommander.getParameters(), out);
        errStream.println(out.toString());
    }

    private static void printCommandList(JCommander cmdParser, StringBuilder out) {
        int longestNameLen = 0;
        for (JCommander commander : cmdParser.getCommands().values()) {
            BLauncherCmd cmd = (BLauncherCmd) commander.getObjects().get(0);
            if (cmd.getName().equals("default-cmd") || cmd.getName().equals("help")) {
                continue;
            }

            int length = cmd.getName().length() + 2;
            if (length > longestNameLen) {
                longestNameLen = length;
            }
        }

        for (JCommander commander : cmdParser.getCommands().values()) {
            BLauncherCmd cmd = (BLauncherCmd) commander.getObjects().get(0);
            if (cmd.getName().equals("default-cmd") || cmd.getName().equals("help")) {
                continue;
            }

            String cmdName = cmd.getName();
            String cmdDesc = cmdParser.getCommandDescription(cmdName);

            int noOfSpaces = longestNameLen - (cmd.getName().length() + 2);
            char[] charArray = new char[noOfSpaces + 4];
            Arrays.fill(charArray, ' ');
            out.append("  ").append(cmdName).append(new String(charArray)).append(cmdDesc).append("\n");
        }
    }

    private static void printFlags(List<ParameterDescription> paramDescs, StringBuilder out) {
        int longestNameLen = 0;
        int count = 0;
        for (ParameterDescription parameterDesc : paramDescs) {
            if (parameterDesc.getParameter().hidden()) {
                continue;
            }

            String names = parameterDesc.getNames();
            int length = names.length() + 2;
            if (length > longestNameLen) {
                longestNameLen = length;
            }
            count++;
        }

        if (count == 0) {
            return;
        }
        out.append("Flags:\n");
        for (ParameterDescription parameterDesc : paramDescs) {
            if (parameterDesc.getParameter().hidden()) {
                continue;
            }
            String names = parameterDesc.getNames();
            String desc = parameterDesc.getDescription();
            int noOfSpaces = longestNameLen - (names.length() + 2);
            char[] charArray = new char[noOfSpaces + 4];
            Arrays.fill(charArray, ' ');
            out.append("  ").append(names).append(new String(charArray)).append(desc).append("\n");
        }
    }

    @Override
    public void setParentCmdParser(JCommander parentCmdParser) {
        this.parentCmdParser = parentCmdParser;
    }

    @Override
    public void setSelfCmdParser(JCommander selfCmdParser) {
        // ignore

    }
}
