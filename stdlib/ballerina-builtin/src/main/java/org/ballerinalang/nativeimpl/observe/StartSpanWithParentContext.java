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

package org.ballerinalang.nativeimpl.observe;

import io.opentracing.SpanContext;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.util.tracer.OpenTracerBallerinaWrapper;
import org.ballerinalang.util.tracer.ReferenceType;
import org.ballerinalang.util.tracer.TraceConstants;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Map;

/**
 * This function which implements the startSpan method for tracing.
 */
@BallerinaFunction(
        orgName = "ballerina",
        packageName = "observe",
        functionName = "startSpanWithParentContext",
        args = {
                @Argument(name = "serviceName", type = TypeKind.STRING),
                @Argument(name = "spanName", type = TypeKind.STRING),
                @Argument(name = "tags", type = TypeKind.MAP),
                @Argument(name = "reference", type = TypeKind.ENUM),
                @Argument(name = "parentSpanContext", type = TypeKind.STRUCT, structType = "SpanContext",
                        structPackage = "ballerina.observe")
        },
        returnType = @ReturnType(type = TypeKind.STRUCT, structType = "Span", structPackage = "ballerina.observe"),
        isPublic = true
)
public class StartSpanWithParentContext extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {

        String serviceName = context.getStringArgument(0);
        String spanName = context.getStringArgument(1);
        BMap tags = (BMap) context.getNullableRefArgument(0);
        String reference = context.getRefArgument(1).stringValue();
        BStruct parentSpanContextStruct = (BStruct) context.getRefArgument(2);
        PrintStream err = System.err;

        Map<String, SpanContext> extractedSpanContextMap;
        if (ReferenceType.valueOf(reference) != ReferenceType.ROOT && parentSpanContextStruct != null &&
                parentSpanContextStruct.getRefField(0) != null) {

            Map<String, String> parentSpanContextMap =
                    Utils.toStringMap((BMap) parentSpanContextStruct.getRefField(0));
            extractedSpanContextMap = OpenTracerBallerinaWrapper.getInstance()
                    .extract(parentSpanContextMap.get(TraceConstants.USER_TRACE_HEADER));
        } else {
            extractedSpanContextMap = Collections.emptyMap();
        }

        String spanId = OpenTracerBallerinaWrapper.getInstance().startSpan(serviceName, spanName,
                Utils.toStringMap(tags), ReferenceType.valueOf(reference), extractedSpanContextMap);

        if (spanId != null) {
            context.setReturnValues(Utils.createSpanStruct(context, serviceName, spanName, spanId));
        } else {
            context.setReturnValues(Utils.createSpanStruct(context, null, null, null));
            err.println("ballerina: Can not use tracing API when tracing is disabled");
        }
    }
}
