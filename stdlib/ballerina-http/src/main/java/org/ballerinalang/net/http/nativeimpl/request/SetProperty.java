/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.net.http.nativeimpl.request;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.net.http.HttpUtil;

/**
 * Ballerina function to set a message property.
 */
@BallerinaFunction(
        packageName = "ballerina.net.http",
        functionName = "setProperty",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = "Request",
                             structPackage = "ballerina.net.http"),
        args = {@Argument(name = "propertyName", type = TypeKind.STRING),
                @Argument(name = "propertyValue", type = TypeKind.STRING)}, //TODO: property value could be of any type
        isPublic = true
)
public class SetProperty extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        HttpUtil.setProperty(context, true);
        context.setReturnValues();
    }
}
