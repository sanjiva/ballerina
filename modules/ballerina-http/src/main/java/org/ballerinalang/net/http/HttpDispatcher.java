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
package org.ballerinalang.net.http;

import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.connector.api.ConnectorUtils;
import org.ballerinalang.connector.api.ParamDetail;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.transport.http.netty.message.HTTPCarbonMessage;

import java.util.List;
import java.util.Map;

/**
 * {@code HttpDispatcher} is responsible for dispatching incoming http requests to the correct resource.
 *
 * @since 0.94
 */
public class HttpDispatcher {

    private static final Logger breLog = LoggerFactory.getLogger(HttpDispatcher.class);

    public static void handleError(HTTPCarbonMessage cMsg, Throwable throwable) {
        String errorMsg = throwable.getMessage();

        // bre log should contain bre stack trace, not the ballerina stack trace
        breLog.error("error: " + errorMsg, throwable);
        try {
            HttpUtil.handleFailure(cMsg, new BallerinaConnectorException(errorMsg, throwable.getCause()));
        } catch (Exception e) {
            breLog.error("Cannot handle error using the error handler for: " + e.getMessage(), e);
        }
    }

    /**
     * This method finds the matching resource for the incoming request.
     *
     * @param httpCarbonMessage incoming message.
     * @return matching resource.
     */
    public static HttpResource findResource(HTTPCarbonMessage httpCarbonMessage) {
        HttpResource resource = null;
        String protocol = (String) httpCarbonMessage.getProperty(org.wso2.carbon.messaging.Constants.PROTOCOL);
        if (protocol == null) {
            throw new BallerinaConnectorException("protocol not defined in the incoming request");
        }

        // Find the Service Dispatcher
        HttpServerConnector serverConnector = (HttpServerConnector) ConnectorUtils
                .getBallerinaServerConnector(Constants.PROTOCOL_PACKAGE_HTTP);
        if (serverConnector == null) {
            throw new BallerinaConnectorException("no service dispatcher available to handle protocol: " + protocol);
        }

        try {
            // Find the Service TODO can be improved
            HttpService service = serverConnector.findService(httpCarbonMessage);
            if (service == null) {
                throw new BallerinaConnectorException("no Service found to handle the service request");
                // Finer details of the errors are thrown from the dispatcher itself, Ideally we shouldn't get here.
            }

            // Find the Resource
            resource = HTTPResourceDispatcher.findResource(service, httpCarbonMessage);
        } catch (Throwable throwable) {
            handleError(httpCarbonMessage, throwable);
        }
        return resource;
    }

    public static BValue[] getSignatureParameters(HttpResource httpResource, HTTPCarbonMessage httpCarbonMessage) {

        BStruct request = ConnectorUtils.createStruct(httpResource.getBalResource(),
                Constants.PROTOCOL_PACKAGE_HTTP, Constants.REQUEST);
        BStruct response = ConnectorUtils.createStruct(httpResource.getBalResource(),
                Constants.PROTOCOL_PACKAGE_HTTP, Constants.RESPONSE);
        HttpUtil.addCarbonMsg(request, httpCarbonMessage);
        HttpUtil.addCarbonMsg(response, HttpUtil.createHttpCarbonMessage(false));
        HttpUtil.addRequestResponseFlag(request, response);

        List<ParamDetail> paramDetails = httpResource.getParamDetails();
        Map<String, String> resourceArgumentValues =
                (Map<String, String>) httpCarbonMessage.getProperty(org.ballerinalang.runtime.Constants.RESOURCE_ARGS);

        BValue[] bValues = new BValue[paramDetails.size()];
        bValues[0] = request;
        bValues[1] = response;
        if (paramDetails.size() <= 2) {
            return bValues;
        }
        for (int i = 2; i < paramDetails.size(); i++) {
            //No need for validation(validation already happened at deployment time),
            //only string parameters can be found here,
            bValues[i] = new BString(resourceArgumentValues.get(paramDetails.get(i).getVarName()));
        }
        return bValues;
    }
}
