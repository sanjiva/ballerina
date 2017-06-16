/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.nativeimpl.lang.messages;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeEnum;
import org.ballerinalang.model.values.BMessage;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.AbstractNativeFunction;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.Attribute;
import org.ballerinalang.natives.annotations.BallerinaAnnotation;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Native function to set given header to carbon message.
 * ballerina.model.messages:setHeader
 */

@BallerinaFunction(
        packageName = "ballerina.lang.messages",
        functionName = "setHeader",
        args = {@Argument(name = "m", type = TypeEnum.MESSAGE),
                @Argument(name = "key", type = TypeEnum.STRING),
                @Argument(name = "value", type = TypeEnum.STRING)},
        isPublic = true
)
@BallerinaAnnotation(annotationName = "Description", attributes = {@Attribute(name = "value",
        value = "Sets the value of a transport header") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "m",
        value = "The message object") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "key",
        value = "The header name") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "value",
        value = "The header value") })
public class SetHeader extends AbstractNativeFunction {

    private static final Logger LOG = LoggerFactory.getLogger(SetHeader.class);

    @Override
    public BValue[] execute(Context context) {
        BMessage msg = (BMessage) getRefArgument(context, 0);
        String headerName = getStringArgument(context, 0);
        String headerValue = getStringArgument(context, 1);
        // Set new header.
        msg.setHeader(headerName, headerValue);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Set " + headerName + " header with value: " + headerValue);
        }
        return VOID_RETURN;
    }
}
