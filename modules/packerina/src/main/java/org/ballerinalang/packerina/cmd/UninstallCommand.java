/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.packerina.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.ballerinalang.launcher.BLauncherCmd;
import org.ballerinalang.launcher.LauncherUtils;
import org.ballerinalang.packerina.UserRepositoryUtils;

import java.io.PrintStream;
import java.util.List;

/**
 * This class represents the "ballerina uninstall" command.
 *
 * @since 0.90
 */
@Parameters(commandNames = "uninstall", commandDescription = "uninstall packages from the user repository")
public class UninstallCommand implements BLauncherCmd {

    private static PrintStream outStream = System.err;
    private JCommander parentCmdParser;

    @Parameter(arity = 1)
    private List<String> argList;

    @Parameter(names = {"--help", "-h"}, hidden = true)
    private boolean helpFlag;

    @Parameter(names = "--debug", hidden = true)
    private String debugPort;

    @Override
    public void execute() {
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(parentCmdParser, "uninstall");
            outStream.println(commandUsageInfo);
            return;
        }

        if (argList == null || argList.size() == 0) {
            throw LauncherUtils.createUsageException("no package given");
        }

        if (argList.size() > 1) {
            throw LauncherUtils.createUsageException("too many arguments");
        }
        String packageStr = argList.get(0);
        UserRepositoryUtils.uninstallSourcePackage(packageStr);
    }

    @Override
    public String getName() {
        return "uninstall";
    }

    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("uninstall packages from the user repository \n");
    }

    @Override
    public void printUsage(StringBuilder out) {
        out.append("  ballerina uninstall <packagename> \n");
    }

    @Override
    public void setParentCmdParser(JCommander parentCmdParser) {
        this.parentCmdParser = parentCmdParser;
    }

    @Override
    public void setSelfCmdParser(JCommander selfCmdParser) {
    }
}
