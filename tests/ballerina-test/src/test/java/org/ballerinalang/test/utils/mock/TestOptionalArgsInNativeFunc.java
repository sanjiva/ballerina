/*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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

package org.ballerinalang.test.utils.mock;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BIntArray;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.util.exceptions.BallerinaException;

/**
 * Mocked native function.
 * 
 * @since 0.965
 */
@BallerinaFunction(
        packageName = "foo.bar",
        functionName = "testOptionalArgsInNativeFunc",
        args = {@Argument(name = "a", type = TypeKind.INT),
                @Argument(name = "b", type = TypeKind.FLOAT),
                @Argument(name = "c", type = TypeKind.STRING),
                @Argument(name = "d", type = TypeKind.INT),
                @Argument(name = "e", type = TypeKind.STRING),
                @Argument(name = "z", type = TypeKind.ARRAY, elementType = TypeKind.INT)},
        isPublic = true
)
public class TestOptionalArgsInNativeFunc extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context ctx) {
        long a;
        double b;
        String c;
        long d;
        String e;
        BIntArray z;

        try {
            a = ctx.getIntArgument(0);
            b = ctx.getFloatArgument(0);
            c = ctx.getStringArgument(0);
            d = ctx.getIntArgument(1);
            e = ctx.getStringArgument(1);
            z = (BIntArray) ctx.getRefArgument(0);
        } catch (Throwable t) {
            throw new BallerinaException("Mocked function failed: " + t.getMessage());
        }
        
        // Setting output value.
        ctx.setReturnValues(new BInteger(a), new BFloat(b), new BString(c), new BInteger(d), new BString(e), z);
    }
}
