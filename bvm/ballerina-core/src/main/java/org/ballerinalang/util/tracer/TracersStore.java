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

import io.opentracing.Tracer;
import org.ballerinalang.util.tracer.config.OpenTracingConfig;
import org.ballerinalang.util.tracer.config.TracerConfig;
import org.ballerinalang.util.tracer.exception.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Class that creates the list of tracers for a given service.
 */
class TracersStore {

    private List<TracerGenerator> tracers;
    private Map<String, Map<String, Tracer>> tracerStore;

    TracersStore(OpenTracingConfig openTracingConfig) {
        if (openTracingConfig != null) {
            this.tracers = new ArrayList<>();
            this.tracerStore = new HashMap<>();
            for (TracerConfig tracerConfig : openTracingConfig.getTracers()) {
                if (tracerConfig.isEnabled()) {
                    try {
                        Class<?> openTracerClass = Class
                                .forName(tracerConfig.getClassName()).asSubclass(OpenTracer.class);
                        this.tracers.add(new TracerGenerator(tracerConfig.getName(), (OpenTracer)
                                openTracerClass.newInstance(), tracerConfig.getConfiguration()));
                    } catch (Throwable ignored) {
                        //Tracers will get added only of there's no errors.
                        //If tracers contains errors, empty map will return.
                    }
                }
            }
        } else {
            this.tracers = Collections.emptyList();
            this.tracerStore = Collections.emptyMap();
        }
    }

    /**
     * Return trace implementations for a specific service
     *
     * @param serviceName name of service of whose trace implementations are needed
     * @return trace implementations i.e: zipkin, jaeger
     */
    Map<String, Tracer> getTracers(String serviceName) {
        if (tracerStore.containsKey(serviceName)) {
            return tracerStore.get(serviceName);
        } else {
            Map<String, Tracer> tracerMap = new HashMap<>();
            for (TracerGenerator tracerGenerator : tracers) {
                try {
                    Tracer tracer = tracerGenerator.generate(serviceName);
                    tracerMap.put(tracerGenerator.name, tracer);
                } catch (Throwable ignored) {
                    //Tracers will get added only of there's no errors.
                    //If tracers contains errors, tracer will be ignored.
                }
            }
            tracerStore.put(serviceName, tracerMap);
            return tracerMap;
        }
    }

    /**
     * Holds the tracerExt and properties, and generates a tracer upon request.
     */
    private static class TracerGenerator {
        String name;
        OpenTracer tracer;
        Properties properties;

        TracerGenerator(String name, OpenTracer tracer, Properties properties) {
            this.name = name;
            this.tracer = tracer;
            this.properties = properties;
        }

        Tracer generate(String serviceName) throws InvalidConfigurationException {
            return tracer.getTracer(name, properties, serviceName);
        }
    }
}
