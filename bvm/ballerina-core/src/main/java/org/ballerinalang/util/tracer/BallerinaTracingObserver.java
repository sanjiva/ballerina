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
package org.ballerinalang.util.tracer;

import org.ballerinalang.bre.bvm.BLangVMErrors;
import org.ballerinalang.bre.bvm.WorkerExecutionContext;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.util.observability.BallerinaObserver;
import org.ballerinalang.util.observability.ObserverContext;

import java.util.HashMap;
import java.util.Map;

import static org.ballerinalang.util.observability.ObservabilityConstants.PROPERTY_BSTRUCT_ERROR;
import static org.ballerinalang.util.observability.ObservabilityConstants.PROPERTY_TRACE_PROPERTIES;
import static org.ballerinalang.util.tracer.TraceConstants.LOG_ERROR_KIND_EXCEPTION;
import static org.ballerinalang.util.tracer.TraceConstants.LOG_EVENT_TYPE_ERROR;
import static org.ballerinalang.util.tracer.TraceConstants.LOG_KEY_ERROR_KIND;
import static org.ballerinalang.util.tracer.TraceConstants.LOG_KEY_EVENT_TYPE;
import static org.ballerinalang.util.tracer.TraceConstants.LOG_KEY_MESSAGE;

/**
 * Observe the runtime and start/stop tracing.
 */
public class BallerinaTracingObserver implements BallerinaObserver {

    @Override
    public void startServerObservation(ObserverContext observerContext, WorkerExecutionContext executionContext) {
        BSpan span = new BSpan(executionContext, false);
        span.setConnectorName(observerContext.getServiceName());
        span.setActionName(observerContext.getResourceName());
        Map<String, String> httpHeaders = (Map<String, String>) observerContext.getProperty(PROPERTY_TRACE_PROPERTIES);
        if (httpHeaders != null) {
            httpHeaders.entrySet().stream()
                    .filter(c -> c.getKey().startsWith(TraceConstants.TRACE_PREFIX))
                    .forEach(e -> span.addProperty(e.getKey(), e.getValue()));
        }
        TraceUtil.setBSpan(executionContext, span);
        span.startSpan();
    }

    @Override
    public void startClientObservation(ObserverContext observerContext, WorkerExecutionContext executionContext) {
        BSpan activeSpan = new BSpan(executionContext, true);
        TraceUtil.setBSpan(executionContext, activeSpan);
        activeSpan.setConnectorName(observerContext.getConnectorName());
        activeSpan.setActionName(observerContext.getActionName());
        observerContext.addProperty(PROPERTY_TRACE_PROPERTIES, activeSpan.getProperties());
        activeSpan.startSpan();
    }

    @Override
    public void stopServerObservation(ObserverContext observerContext, WorkerExecutionContext executionContext) {
        stopObservation(observerContext, executionContext);
    }

    @Override
    public void stopClientObservation(ObserverContext observerContext, WorkerExecutionContext executionContext) {
        stopObservation(observerContext, executionContext);
    }

    public void stopObservation(ObserverContext observerContext, WorkerExecutionContext executionContext) {
        BSpan span = TraceUtil.getBSpan(executionContext);
        if (span != null) {
            BStruct error = (BStruct) observerContext.getProperty(PROPERTY_BSTRUCT_ERROR);
            if (error != null) {
                Map<String, Object> logProps = new HashMap<>();
                logProps.put(LOG_KEY_ERROR_KIND, LOG_ERROR_KIND_EXCEPTION);
                logProps.put(LOG_KEY_MESSAGE, BLangVMErrors.getPrintableStackTrace(error));
                logProps.put(LOG_KEY_EVENT_TYPE, LOG_EVENT_TYPE_ERROR);
                span.logError(logProps);
            }
            span.addTags(observerContext.getTags());
            TraceUtil.finishBSpan(span);
        }
    }
}
