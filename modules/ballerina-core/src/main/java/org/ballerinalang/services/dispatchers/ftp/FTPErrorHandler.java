/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.services.dispatchers.ftp;

import org.ballerinalang.services.ErrorHandlerUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.ServerConnectorErrorHandler;

/**
 * Error handler for FTP Protocol.
 */
@Component(
        name = "ballerina.net.ftp.error.handler",
        immediate = true,
        service = ServerConnectorErrorHandler.class)
public class FTPErrorHandler implements ServerConnectorErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(FTPErrorHandler.class);

    @Override
    public void handleError(Exception e, CarbonMessage carbonMessage, CarbonCallback carbonCallback) {
        //TODO: Further refine this later on
        log.error(e.getMessage(), e);
        ErrorHandlerUtils.printError(e);
    }

    @Override
    public String getProtocol() {
        return Constants.PROTOCOL_FTP;
    }
}
