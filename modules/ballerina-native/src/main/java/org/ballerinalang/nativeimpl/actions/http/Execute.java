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

package org.ballerinalang.nativeimpl.actions.http;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeEnum;
import org.ballerinalang.model.values.BConnector;
import org.ballerinalang.model.values.BException;
import org.ballerinalang.model.values.BMessage;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.Attribute;
import org.ballerinalang.natives.annotations.BallerinaAction;
import org.ballerinalang.natives.annotations.BallerinaAnnotation;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.natives.connectors.AbstractNativeAction;
import org.ballerinalang.natives.connectors.BalConnectorCallback;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.exceptions.ClientConnectorException;

import java.util.Locale;

/**
 * {@code Execute} action can be used to invoke execute a http call with any httpVerb.
 */
@BallerinaAction(
        packageName = "ballerina.net.http",
        actionName = "execute",
        connectorName = Constants.CONNECTOR_NAME,
        args = {
                @Argument(name = "c",
                        type = TypeEnum.CONNECTOR),
                @Argument(name = "httpVerb", type = TypeEnum.STRING),
                @Argument(name = "path", type = TypeEnum.STRING),
                @Argument(name = "m", type = TypeEnum.MESSAGE)
        },
        returnType = {@ReturnType(type = TypeEnum.MESSAGE)},
        connectorArgs = {
                @Argument(name = "serviceUri", type = TypeEnum.STRING)
        })
@BallerinaAnnotation(annotationName = "Description", attributes = {@Attribute(name = "value",
        value = "Invokes an HTTP call with the specified HTTP verb.") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "c",
        value = "A connector object") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "httpVerb",
        value = "HTTP verb value") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "path",
        value = "Resource path ") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "m",
        value = "A message object") })
@BallerinaAnnotation(annotationName = "Return", attributes = {@Attribute(name = "message",
        value = "The response message object") })
@Component(
        name = "action.net.http.execute",
        immediate = true,
        service = AbstractNativeAction.class)
public class Execute extends AbstractHTTPAction {

    private static final Logger logger = LoggerFactory.getLogger(Execute.class);

    @Override
    public BValue execute(Context context) {

        logger.debug("Executing Native Action : Execute");
        try {
            // Execute the operation
            return executeAction(context, createCarbonMsg(context));
        } catch (Throwable t) {
            throw new BallerinaException("Failed to invoke 'execute' action in " + Constants.CONNECTOR_NAME
                    + ". " + t.getMessage(), context);
        }
    }

    @Override
    public void execute(Context context, BalConnectorCallback connectorCallback) {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing Native Action (non-blocking): {}", this.getName());
        }
        try {
            // Execute the operation
            executeNonBlockingAction(context, createCarbonMsg(context), connectorCallback);
        } catch (ClientConnectorException | RuntimeException e) {
            String msg = "Failed to invoke 'execute' action in " + Constants.CONNECTOR_NAME
                    + ". " + e.getMessage();
            BException exception = new BException(msg, Constants.HTTP_CLIENT_EXCEPTION_CATEGORY);
            context.getExecutor().handleBException(exception);
        } catch (Throwable t) {
            // This is should be a JavaError. Need to handle this properly.
            throw new BallerinaException("Failed to invoke 'execute' action in " + Constants.CONNECTOR_NAME
                    + ". " + t.getMessage(), context);
        }
    }

    private CarbonMessage createCarbonMsg(Context context) {
        // Extract Argument values
        BConnector bConnector = (BConnector) getArgument(context, 0);
        String httpVerb = getArgument(context, 1).stringValue();
        String path = getArgument(context, 2).stringValue();
        BMessage bMessage = (BMessage) getArgument(context, 3);

        // Prepare the message
        CarbonMessage cMsg = bMessage.value();
        prepareRequest(bConnector, path, cMsg);

        // If the verb is not specified, use the verb in incoming message
        if (httpVerb == null || "".equals(httpVerb)) {
            httpVerb = (String) cMsg.getProperty(Constants.HTTP_METHOD);
        }
        cMsg.setProperty(Constants.HTTP_METHOD, httpVerb.trim().toUpperCase(Locale.getDefault()));
        return cMsg;
    }
}
