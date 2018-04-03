/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.observe.trace;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.util.tracer.TraceConstants;

import java.util.Iterator;
import java.util.Map;

/**
 * This function injects a span context and returns the spans Id.
 */
@BallerinaFunction(
        orgName = "ballerina",
        packageName = "observe",
        functionName = "extractTraceContext",
        args = {
                @Argument(name = "headers", type = TypeKind.MAP),
                @Argument(name = "traceGroup", type = TypeKind.STRING)
        },
        returnType = {
                @ReturnType(type = TypeKind.STRUCT, structType = "SpanContext", structPackage = "ballerina.observe")
        },
        isPublic = true
)
public class ExtractTraceContext extends BlockingNativeCallableUnit {
    @Override
    public void execute(Context context) {

        BMap header = (BMap) context.getRefArgument(0);
        String group = context.getStringArgument(0) == null ? TraceConstants.DEFAULT_USER_API_GROUP
                : context.getStringArgument(0);
        Iterator<Map.Entry<String, String>> headerMap = Utils.toStringMap(header).entrySet().iterator();

        BMap<String, BString> spanHeaders = new BMap<>();

        while (headerMap.hasNext()) {
            Map.Entry<String, String> headers = headerMap.next();
            if (headers.getKey().startsWith(group)) {
                spanHeaders.put(headers.getKey().substring(group.length()), new BString(headers.getValue()));
            }
        }
        BStruct spanContextStruct = Utils.createSpanContextStruct(context, spanHeaders);
        context.setReturnValues(spanContextStruct);
    }
}
