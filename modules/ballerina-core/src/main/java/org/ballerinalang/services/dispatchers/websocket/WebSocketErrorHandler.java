/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.ballerinalang.services.dispatchers.websocket;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.ServerConnectorErrorHandler;
import org.wso2.carbon.transport.http.netty.common.Constants;

/**
 * Error handler for WebSocket protocol.
 * After the handshake, in WebSocket Protocol there is no explicit way of handling error and simply
 * informing the client about it. So for debugging purposes this error handler can be used.
 */
@Component(
        name = "ws.server.connector.error.handler",
        immediate = true,
        service = ServerConnectorErrorHandler.class)
public class WebSocketErrorHandler implements ServerConnectorErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketErrorHandler.class);

    @Override
    public void handleError(Exception e, CarbonMessage carbonMessage, CarbonCallback carbonCallback) throws Exception {
        //This debug log will be used in error debugging in the server connector.
        if (logger.isDebugEnabled()) {
            logger.debug("Error occurred : " + e.getMessage(), e);
        }
    }

    @Override
    public String getProtocol() {
        return Constants.WEBSOCKET_PROTOCOL;
    }
}
