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

package org.ballerinalang.util.debugger.dto;

import org.ballerinalang.model.NodeLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * DTO class representing commands send to debugger from the client.
 *
 * @since 0.8.0
 */
public class CommandDTO {

    private String command;

    private ArrayList<BreakPointDTO> points;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public ArrayList<BreakPointDTO> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<BreakPointDTO> points) {
        this.points = points;
    }


    /**
     * This method is there to decouple core and editor data transfer.
     * 
     * @return list of locations of the break points
     */
    public ArrayList<NodeLocation> getBreakPoints() {
        ArrayList<NodeLocation> breakPoints = new ArrayList<NodeLocation>();
        for (BreakPointDTO bp: points) {
            // TODO : Change the API for accepting breakpoints
            // convert "/" into platform's file separator
            // Server should derive actual file path from path sent from debugger client which is normalized to /
            String fileName = bp.getFileName().replaceAll("/", Matcher.quoteReplacement(File.separator));
            breakPoints.add(new NodeLocation(fileName, bp.getLineNumber()));
        }
        return breakPoints;
    }
}
