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
package org.ballerinalang.test.utils.debug;

import io.netty.channel.Channel;
import org.ballerinalang.util.debugger.DebugClientHandler;
import org.ballerinalang.util.debugger.DebugCommand;
import org.ballerinalang.util.debugger.DebugContext;
import org.ballerinalang.util.debugger.DebugException;
import org.ballerinalang.util.debugger.dto.BreakPointDTO;
import org.ballerinalang.util.debugger.dto.MessageDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Test debug client handler for debugger tests.
 *
 * @since 0.95.4
 */
public class TestDebugClientHandler implements DebugClientHandler {

        volatile boolean isExit;
        int hitCount = -1;
        BreakPointDTO haltPosition;
        private volatile Semaphore executionSem;
        private volatile String threadId;

        //key - threadid
        private Map<String, DebugContext> contextMap;

        TestDebugClientHandler() {
            this.contextMap = new ConcurrentHashMap<>();
            this.executionSem = new Semaphore(0);
        }

        public void aquireSem() {
            try {
                executionSem.acquire();
            } catch (InterruptedException e) {
                //ignore
            }
        }

        public String getThreadId() {
            return threadId;
        }

        @Override
        public void addContext(DebugContext debugContext) {
            //for debugging tests, only single threaded execution supported
            String threadId = Thread.currentThread().getName() + ":" + Thread.currentThread().getId();
            debugContext.setThreadId(threadId);
            //TODO check if that thread id already exist in the map
            this.contextMap.put(threadId, debugContext);
        }

        @Override
        public DebugContext getContext(String threadId) {
            return this.contextMap.get(threadId);
        }

        @Override
        public void updateAllDebugContexts(DebugCommand debugCommand) {
            contextMap.forEach((k, v) -> v.setCurrentCommand(debugCommand));
        }

        @Override
        public void setChannel(Channel channel) throws DebugException {
        }

        @Override
        public void clearChannel() {
        }

        @Override
        public boolean isChannelActive() {
            return true;
        }

        @Override
        public void notifyExit() {
            isExit = true;
            executionSem.release();
        }

        @Override
        public void notifyHalt(MessageDTO message) {
            hitCount++;
            haltPosition = message.getLocation();
            threadId = message.getThreadId();
            executionSem.release();
        }

        @Override
        public void sendCustomMsg(MessageDTO message) {
        }
    }

