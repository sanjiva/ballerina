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

package org.ballerinalang.nativeimpl.builtin.stringlib;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

/**
 * Native function ballerina.model.strings:lastIndexOf.
 *
 * @since 0.8.0
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "builtin",
        functionName = "string.lastIndexOf",
        args = {@Argument(name = "mainString", type = TypeKind.STRING),
                @Argument(name = "subString", type = TypeKind.STRING)},
        returnType = {@ReturnType(type = TypeKind.INT)},
        isPublic = true
)
public class LastIndexOf extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        String param1 = context.getStringArgument(0);
        String subString = context.getStringArgument(1);

        BInteger bInteger = new BInteger(param1.lastIndexOf(subString));
        context.setReturnValues(bInteger);
    }
}
