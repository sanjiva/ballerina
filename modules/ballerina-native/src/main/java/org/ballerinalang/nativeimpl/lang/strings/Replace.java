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

package org.ballerinalang.nativeimpl.lang.strings;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeEnum;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.AbstractNativeFunction;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.Attribute;
import org.ballerinalang.natives.annotations.BallerinaAnnotation;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

/**
 * Native function ballerina.model.strings:replace.
 *
 * @since 0.8.0
 */
@BallerinaFunction(
        packageName = "ballerina.lang.strings",
        functionName = "replace",
        args = {@Argument(name = "mainString", type = TypeEnum.STRING),
                @Argument(name = "replacePattern", type = TypeEnum.STRING),
                @Argument(name = "replaceWith", type = TypeEnum.STRING)},
        returnType = {@ReturnType(type = TypeEnum.STRING)},
        isPublic = true
)
@BallerinaAnnotation(annotationName = "Description", attributes = {@Attribute(name = "value",
        value = "Replaces the replacePattern string with the replaceWith string and returns the result") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "mainString",
        value = "The original string argument") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "replacePattern",
        value = "The pattern to search for ") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "replaceWith",
        value = "The replacement string") })
@BallerinaAnnotation(annotationName = "Return", attributes = {@Attribute(name = "string",
        value = "The derived string") })
public class Replace extends AbstractNativeFunction {

    @Override
    public BValue[] execute(Context context) {
        String mainString = getStringArgument(context, 0);
        String replacePattern = getStringArgument(context, 1);
        String replaceWith = getStringArgument(context, 2);

        String replacedString = mainString.replace(replacePattern, replaceWith);
        return getBValues(new BString(replacedString));
    }
}
