/*
*   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.nativeimpl.time;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

/**
 * Create a time from the given time components.
 *
 * @since 0.89
 */
@BallerinaFunction(
        packageName = "ballerina.time",
        functionName = "createTime",
        args = {@Argument(name = "years", type = TypeKind.INT),
                @Argument(name = "months", type = TypeKind.INT),
                @Argument(name = "days", type = TypeKind.INT),
                @Argument(name = "hours", type = TypeKind.INT),
                @Argument(name = "minutes", type = TypeKind.INT),
                @Argument(name = "seconds", type = TypeKind.INT),
                @Argument(name = "milliseconds", type = TypeKind.INT),
                @Argument(name = "zoneID", type = TypeKind.STRING)},
        returnType = {@ReturnType(type = TypeKind.STRUCT, structType = "Time",
                                  structPackage = "ballerina.time")},
        isPublic = true
)
public class CreateTime extends AbstractTimeFunction {

    @Override
    public void execute(Context context) {
        int years = (int) context.getIntArgument(0);
        int months = (int) context.getIntArgument(1);
        int dates = (int) context.getIntArgument(2);
        int hours = (int) context.getIntArgument(3);
        int minutes = (int) context.getIntArgument(4);
        int seconds = (int) context.getIntArgument(5);
        int milliSeconds = (int) context.getIntArgument(6);
        String zoneId = context.getStringArgument(0);
        context.setReturnValues(
                createDateTime(context, years, months, dates, hours, minutes, seconds, milliSeconds, zoneId));
    }
}
