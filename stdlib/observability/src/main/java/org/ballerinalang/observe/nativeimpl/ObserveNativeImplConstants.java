/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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
package org.ballerinalang.observe.nativeimpl;

/**
 * Constants used in Ballerina Observe package.
 *
 * @since 0.980.0
 */
public final class ObserveNativeImplConstants {

    private ObserveNativeImplConstants() {
    }

    public static final String OBSERVE_PACKAGE_PATH = "ballerina/observe";
    public static final String GAUGE = "Gauge";
    public static final String COUNTER = "Counter";
    public static final String SNAPSHOT = "Snapshot";
    public static final String METRIC = "Metric";
    public static final String STATISTIC_CONFIG = "StatisticConfig";
    public static final String PERCENTILE_VALUE = "PercentileValue";
    public static final String METRIC_NATIVE_INSTANCE_KEY = "__metric_native_instance__";

    public static final String NAME_FIELD = "name";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String TAGS_FIELD = "metricTags";
    public static final String STATISTICS_CONFIG_FIELD = "statisticConfigs";
    public static final String EXPIRY_FIELD = "timeWindow";
    public static final String BUCKETS_FIELD = "buckets";
    public static final String PERCENTILES_FIELD = "percentiles";
}
