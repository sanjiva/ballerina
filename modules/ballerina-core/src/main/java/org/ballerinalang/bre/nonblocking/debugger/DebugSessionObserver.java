/*
*   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.ballerinalang.bre.nonblocking.debugger;

import org.ballerinalang.bre.Context;

/**
 * Represents a Debug sessions observer which will be notified by the the BLanExecutionDebugger.
 */
public interface DebugSessionObserver {

    /**
     * Called when adding a context.
     *
     * @param bContext to be added to the map.
     */
    void addContext(Context bContext);

    /**
     * Called when execution is end.
     */
    void notifyComplete();

    /**
     * Called when main program exit.
     */
    void notifyExit();

    /**
     * Called when executor hits a break point.
     *
     * @param breakPointInfo Break point information.
     */
    void notifyHalt(BreakPointInfo breakPointInfo);
}
