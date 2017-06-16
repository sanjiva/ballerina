/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.nativeimpl.net.jms;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeEnum;
import org.ballerinalang.model.values.BMessage;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.nativeimpl.actions.jms.utils.JMSConstants;
import org.ballerinalang.natives.AbstractNativeFunction;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.Attribute;
import org.ballerinalang.natives.annotations.BallerinaAnnotation;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.messaging.CarbonMessage;

import javax.jms.Session;


/**
 * To rollback the transactions.
 */
@BallerinaFunction(packageName = "ballerina.net.jms", functionName = "rollback", args = {
        @Argument(name = "m", type = TypeEnum.MESSAGE) }, isPublic = true)
@BallerinaAnnotation(annotationName = "Description", attributes = { @Attribute(name = "value",
        value = "Session rollback action implementation for jms connector when using jms session transaction mode") })
@BallerinaAnnotation(annotationName = "Param", attributes = { @Attribute(name = "message",
        value = "message")})
public class Rollback extends AbstractNativeFunction {
    private static final Logger log = LoggerFactory.getLogger(Rollback.class);

    public BValue[] execute(Context ctx) {
        BMessage msg = (BMessage) getRefArgument(ctx, 0);
        CarbonMessage carbonMessage = msg.value();
        Object jmsSessionAcknowledgementMode = carbonMessage
                .getProperty(JMSConstants.JMS_SESSION_ACKNOWLEDGEMENT_MODE);

        if (null == jmsSessionAcknowledgementMode) {
            log.warn("JMS Rollback function can only be used with JMS Messages. "
                    + JMSConstants.JMS_SESSION_ACKNOWLEDGEMENT_MODE + " property is not found in the message.");
            return VOID_RETURN;
        }
        if (!(jmsSessionAcknowledgementMode instanceof Integer)) {
            throw new BallerinaException(JMSConstants.JMS_SESSION_ACKNOWLEDGEMENT_MODE + " property should hold a "
                    + "integer value. ");
        }
        if (Session.SESSION_TRANSACTED == (Integer) jmsSessionAcknowledgementMode) {
            carbonMessage
                    .setProperty(JMSConstants.JMS_MESSAGE_DELIVERY_STATUS, JMSConstants.JMS_MESSAGE_DELIVERY_ERROR);
            ctx.getBalCallback().done(carbonMessage);

        } else {
            log.warn("JMS Rollback function can only be used with JMS Session Transacted Mode.");
        }
        return VOID_RETURN;
    }
}
