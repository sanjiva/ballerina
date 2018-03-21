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
import org.ballerinalang.model.types.BTypes;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BRefValueArray;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

/**
 * Get the year,month and date value for the given time.
 *
 * @since 0.89
 */
@BallerinaFunction(
        packageName = "ballerina.time",
        functionName = "Time.getDate",
        args = {@Argument(name = "time", type = TypeKind.STRUCT, structType = "Time",
                          structPackage = "ballerina.time")},
        returnType = {@ReturnType(type = TypeKind.INT),
                      @ReturnType(type = TypeKind.INT),
                      @ReturnType(type = TypeKind.INT)},
        isPublic = true
)
public class GetDate extends AbstractTimeFunction {

    @Override
    public void execute(Context context) {
        BStruct timeStruct = ((BStruct) context.getRefArgument(0));
        BRefValueArray date = new BRefValueArray(BTypes.typeInt);
        date.add(0, new BInteger(getYear(timeStruct)));
        date.add(1, new BInteger(getMonth(timeStruct)));
        date.add(2, new BInteger(getDay(timeStruct)));
        context.setReturnValues(date);
    }
}
