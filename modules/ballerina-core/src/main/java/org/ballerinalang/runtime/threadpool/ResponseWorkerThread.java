/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package org.ballerinalang.runtime.threadpool;

import org.ballerinalang.bre.nonblocking.BLangAbstractExecutionVisitor;
import org.ballerinalang.natives.connectors.BalConnectorCallback;
import org.ballerinalang.runtime.ServerConnectorMessageHandler;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

/**
 * Worker Thread which is responsible for response processing.
 */
public class ResponseWorkerThread extends WorkerThread {

    public ResponseWorkerThread(CarbonMessage cMsg, CarbonCallback callback) {
        super(cMsg, callback);
    }

    public void run() {
        // TODO : Fix this properly.
        // Connector callback's done method is called from different locations, i.e: MessageProcessor, from Netty etc.
        // Because of this we have to start new thread from the callback, if non-blocking is enabled.
        BalConnectorCallback connectorCallback = (BalConnectorCallback) this.callback;
        BLangAbstractExecutionVisitor executor = connectorCallback.getContext().getExecutor();
        try {
            boolean errorOccurred = false;
            try {
                connectorCallback.getActionNode().getCallableUnit().validate(connectorCallback);
            } catch (RuntimeException e) {
                errorOccurred = true;
                executor.createBErrorFromException(e, null);
            }
            if (errorOccurred) {
                // Pass this to catch statement.
                executor.handleBException();
                executor.continueExecution();
            } else {
                executor.continueExecution(connectorCallback.getCurrentNode().next());
            }
        } catch (Throwable unhandled) {
            // Root level Error handler. we have to notify server connector.
            ServerConnectorMessageHandler.handleErrorFromOutbound(connectorCallback.getContext(), unhandled);
        }
    }
}
