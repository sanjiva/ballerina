/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.net.http.nativeimpl.request;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.AbstractNativeFunction;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.net.http.HttpUtil;

/**
 * Get the payload of the Message as a XML.
 */
@BallerinaFunction(
        packageName = "ballerina.net.http",
        functionName = "getXmlPayload",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = "Request",
                             structPackage = "ballerina.net.http"),
        returnType = {@ReturnType(type = TypeKind.XML)},
        isPublic = true
)
public class GetXMLPayload extends AbstractNativeFunction {

    @Override
    public BValue[] execute(Context context) {
        return HttpUtil.getXMLPayload(context, this, true);
    }
}
