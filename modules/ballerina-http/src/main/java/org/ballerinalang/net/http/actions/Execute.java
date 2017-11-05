/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ballerinalang.net.http.actions;

import org.ballerinalang.bre.Context;
import org.ballerinalang.connector.api.ConnectorFuture;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BConnector;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaAction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.net.http.Constants;
import org.ballerinalang.net.http.HttpUtil;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.transport.http.netty.contract.ClientConnectorException;
import org.wso2.carbon.transport.http.netty.message.HTTPCarbonMessage;

import java.util.Locale;

/**
 * {@code Execute} action can be used to invoke execute a http call with any httpVerb.
 */
@BallerinaAction(
        packageName = "ballerina.net.http",
        actionName = "execute",
        connectorName = Constants.CONNECTOR_NAME,
        args = {
                @Argument(name = "c", type = TypeKind.CONNECTOR),
                @Argument(name = "httpVerb", type = TypeKind.STRING),
                @Argument(name = "path", type = TypeKind.STRING),
                @Argument(name = "req", type = TypeKind.STRUCT, structType = "Request",
                        structPackage = "ballerina.net.http")
        },
        returnType = {
                @ReturnType(type = TypeKind.STRUCT, structType = "Response", structPackage = "ballerina.net.http"),
                @ReturnType(type = TypeKind.STRUCT, structType = "HttpConnectorError",
                        structPackage = "ballerina.net.http"),
        },
        connectorArgs = {
                @Argument(name = "serviceUri", type = TypeKind.STRING),
                @Argument(name = "options", type = TypeKind.STRUCT, structType = "Options",
                          structPackage = "ballerina.net.http")
        }
)
public class Execute extends AbstractHTTPAction {

    private static final Logger logger = LoggerFactory.getLogger(Execute.class);

    @Override
    public ConnectorFuture execute(Context context) {

        if (logger.isDebugEnabled()) {
            logger.debug("Executing Native Action : {}", this.getName());
        }
        try {
            // Execute the operation
            return executeNonBlockingAction(context, createCarbonMsg(context));
        } catch (ClientConnectorException clientConnectorException) {
            throw new BallerinaException("Failed to invoke 'execute' action in " + Constants.CONNECTOR_NAME
                    + ". " + clientConnectorException.getMessage(), context);
        }
    }

    protected HTTPCarbonMessage createCarbonMsg(Context context) {
        // Extract Argument values
        BConnector bConnector = (BConnector) getRefArgument(context, 0);
        String httpVerb = getStringArgument(context, 0);
        String path = getStringArgument(context, 1);
        BStruct requestStruct = ((BStruct) getRefArgument(context, 1));
        //TODO check below line
        HTTPCarbonMessage defaultCarbonMsg = HttpUtil.createHttpCarbonMessage(true);
        HTTPCarbonMessage cMsg = HttpUtil.getCarbonMsg(requestStruct, defaultCarbonMsg);
        prepareRequest(bConnector, path, cMsg);

        // If the verb is not specified, use the verb in incoming message
        if (httpVerb == null || "".equals(httpVerb)) {
            httpVerb = (String) cMsg.getProperty(Constants.HTTP_METHOD);
        }
        cMsg.setProperty(Constants.HTTP_METHOD, httpVerb.trim().toUpperCase(Locale.getDefault()));
        return cMsg;
    }
}
