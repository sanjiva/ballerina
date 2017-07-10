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
package org.ballerinalang.nativeimpl.lang.time;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeEnum;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.Attribute;
import org.ballerinalang.natives.annotations.BallerinaAnnotation;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

/**
 * Create a time from the given time components.
 *
 * @since 0.89
 */
@BallerinaFunction(
        packageName = "ballerina.lang.time",
        functionName = "createTime",
        args = {@Argument(name = "years", type = TypeEnum.INT),
                @Argument(name = "months", type = TypeEnum.INT),
                @Argument(name = "days", type = TypeEnum.INT),
                @Argument(name = "hours", type = TypeEnum.INT),
                @Argument(name = "minutes", type = TypeEnum.INT),
                @Argument(name = "seconds", type = TypeEnum.INT),
                @Argument(name = "milliseconds", type = TypeEnum.INT),
                @Argument(name = "zoneID", type = TypeEnum.STRING)},
        returnType = {@ReturnType(type = TypeEnum.STRUCT, structType = "Time",
                                  structPackage = "ballerina.lang.time")},
        isPublic = true
)
@BallerinaAnnotation(annotationName = "Description", attributes = { @Attribute(name = "value",
         value = "Create a time from the given components.")})
@BallerinaAnnotation(annotationName = "Return", attributes = {@Attribute(name = "struct) ",
         value = "The Time struct")})
public class CreateTime extends AbstractTimeFunction {

    @Override
    public BValue[] execute(Context context) {
        int years = (int) getIntArgument(context, 0);
        int months = (int) getIntArgument(context, 1);
        int dates = (int) getIntArgument(context, 2);
        int hours = (int) getIntArgument(context, 3);
        int minutes = (int) getIntArgument(context, 4);
        int seconds = (int) getIntArgument(context, 5);
        int milliSeconds = (int) getIntArgument(context, 6);
        String zoneId = getStringArgument(context, 0);
        return new BValue[] {
                createDateTime(context, years, months, dates, hours, minutes, seconds, milliSeconds, zoneId)
        };
    }
}
