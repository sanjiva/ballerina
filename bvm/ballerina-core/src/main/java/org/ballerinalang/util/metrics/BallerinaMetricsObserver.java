/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.util.metrics;

import org.ballerinalang.bre.bvm.WorkerExecutionContext;
import org.ballerinalang.util.observability.BallerinaObserver;
import org.ballerinalang.util.observability.ObserverContext;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Observe the runtime and collect measurements.
 */
public class BallerinaMetricsObserver implements BallerinaObserver {

    private static final String PROPERTY_START_TIME = "_observation_start_time_";
    private static final String TAG_KEY_SERVICE = "service";
    private static final String TAG_KEY_RESOURCE = "resource";
    private static final String TAG_KEY_ACTION = "action";

    private static final PrintStream consoleError = System.err;

    @Override
    public void startServerObservation(ObserverContext observerContext, WorkerExecutionContext executionContext) {
        startObservation(observerContext);
    }

    @Override
    public void startClientObservation(ObserverContext observerContext, WorkerExecutionContext executionContext) {
        startObservation(observerContext);
    }

    private void startObservation(ObserverContext observerContext) {
        observerContext.addProperty(PROPERTY_START_TIME, System.nanoTime());
    }

    @Override
    public void stopServerObservation(ObserverContext observerContext, WorkerExecutionContext executionContext) {
        if (!observerContext.isStarted()) {
            // Do not collect metrics if the observation hasn't started
            return;
        }
        String[] additionalTags = {TAG_KEY_SERVICE, observerContext.getServiceName(), TAG_KEY_RESOURCE,
                observerContext.getResourceName()};
        stopObservation(observerContext, additionalTags);
    }

    @Override
    public void stopClientObservation(ObserverContext observerContext, WorkerExecutionContext executionContext) {
        if (!observerContext.isStarted()) {
            // Do not collect metrics if the observation hasn't started
            return;
        }
        String[] additionalTags = {TAG_KEY_ACTION, observerContext.getActionName()};
        stopObservation(observerContext, additionalTags);
    }

    private void stopObservation(ObserverContext observerContext, String[] additionalTags) {
        try {
            Long startTime = (Long) observerContext.getProperty(PROPERTY_START_TIME);
            long duration = System.nanoTime() - startTime;
            // Connector name must be a part of the metric name to make sure that every metric is unique with
            // the combination of name and tags.
            String namePrefix = observerContext.getConnectorName();
            Map<String, String> tags = observerContext.getTags();
            Timer responseTimer = Timer.builder(namePrefix + "_response_time").description("Response Time")
                    .tags(tags).tags(additionalTags).register();
            responseTimer.record(duration, TimeUnit.NANOSECONDS);
            Counter requestsCounter = Counter.builder(namePrefix + "_requests_total")
                    .description("Total number of requests").tags(tags).tags(additionalTags).register();
            requestsCounter.increment();
        } catch (RuntimeException e) {
            // Metric Provider may throw exceptions if there is a mismatch in tags.
            consoleError.println("ballerina: error collecting metrics: " + e.getMessage());
        }
    }
}
