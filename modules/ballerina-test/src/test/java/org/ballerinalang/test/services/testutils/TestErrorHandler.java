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
 */
package org.ballerinalang.test.services.testutils;

import org.ballerinalang.net.http.Constants;
import org.ballerinalang.runtime.message.StringDataSource;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.DefaultCarbonMessage;
import org.wso2.carbon.messaging.ServerConnectorErrorHandler;

/**
 * Test error handler used with handling server connector error with http related test cases.
 */
public class TestErrorHandler implements ServerConnectorErrorHandler {
    @Override
    public void handleError(Exception exception, CarbonMessage carbonMessage, CarbonCallback carbonCallback) {
        DefaultCarbonMessage response = new DefaultCarbonMessage();
        if (exception.getMessage() != null) {
            response.setMessageDataSource(new StringDataSource(exception.getMessage()));
        }
        response.setProperty(Constants.HTTP_STATUS_CODE,
                carbonMessage.getProperty(Constants.HTTP_STATUS_CODE));
        carbonCallback.done(response);
    }

    @Override
    public String getProtocol() {
        return "http";
    }
}
