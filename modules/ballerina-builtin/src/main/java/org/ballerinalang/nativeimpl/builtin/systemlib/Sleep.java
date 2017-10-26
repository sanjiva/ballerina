/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.nativeimpl.builtin.systemlib;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.AbstractNativeFunction;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.util.exceptions.BallerinaException;

/**
 * Native function ballerina.lang.system:sleep.
 */
@BallerinaFunction(
        packageName = "ballerina.builtin",
        functionName = "sleep",
        args = {@Argument(name = "t", type = TypeKind.INT)},
        isPublic = true
)
public class Sleep extends AbstractNativeFunction {

    public BValue[] execute(Context ctx) {
        long timeout = getIntArgument(ctx, 0);
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            throw new BallerinaException("System sleep has been interrupted", e);
        }
        return VOID_RETURN;
    }
}
