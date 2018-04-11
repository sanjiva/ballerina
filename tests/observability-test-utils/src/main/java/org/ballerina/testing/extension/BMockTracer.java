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

package org.ballerina.testing.extension;

import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import org.ballerinalang.annotation.JavaSPIService;
import org.ballerinalang.util.tracer.OpenTracer;
import org.ballerinalang.util.tracer.exception.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tracer extension that returns an instance of Mock tracer.
 */
@JavaSPIService("org.ballerinalang.util.tracer.OpenTracer")
public class BMockTracer implements OpenTracer {

    private static List<MockTracer> tracerMap = new ArrayList<>();
    private static final String NAME = "BMockTracer";

    @Override
    public Tracer getTracer(String tracerName, String serviceName) throws InvalidConfigurationException {
        MockTracer mockTracer = new MockTracer();
        BMockTracer.tracerMap.add(mockTracer);
        return mockTracer;
    }

    @Override
    public void init(Map<String, String> configProperties) {
        // Do Nothing
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static List<MockTracer> getTracerMap() {
        return tracerMap;
    }
}
