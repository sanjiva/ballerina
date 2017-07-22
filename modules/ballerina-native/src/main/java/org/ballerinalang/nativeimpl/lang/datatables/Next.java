/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.nativeimpl.lang.datatables;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeEnum;
import org.ballerinalang.model.values.BDataTable;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.AbstractNativeFunction;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.Attribute;
import org.ballerinalang.natives.annotations.BallerinaAnnotation;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

/**
 * Native function to get the current row of a datatable as a struct.
 * ballerina.model.datatables:next(datatable)
 *
 * @since 0.88
 */
@BallerinaFunction(
        packageName = "ballerina.lang.datatables",
        functionName = "next",
        args = {@Argument(name = "dt", type = TypeEnum.DATATABLE)},
        returnType = {@ReturnType(type = TypeEnum.ANY)},
        isPublic = true
)
@BallerinaAnnotation(annotationName = "Description", attributes = {@Attribute(name = "value",
               value = "Returns the current row of the datatable as a struct.") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "dt",
               value = "The datatable object") })
@BallerinaAnnotation(annotationName = "Return", attributes = {@Attribute(name = "struct",
               value = "The struct created with the data of the current row") })
public class Next extends AbstractNativeFunction {

    public BValue[] execute(Context ctx) {
        BDataTable dataTable = (BDataTable) getRefArgument(ctx, 0);
        return getBValues(dataTable.getNext());
    }
}
