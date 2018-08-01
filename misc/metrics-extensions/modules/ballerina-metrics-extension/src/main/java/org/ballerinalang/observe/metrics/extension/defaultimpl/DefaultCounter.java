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
package org.ballerinalang.observe.metrics.extension.defaultimpl;

import org.ballerinalang.util.metrics.AbstractMetric;
import org.ballerinalang.util.metrics.Counter;
import org.ballerinalang.util.metrics.MetricId;

import java.util.concurrent.atomic.LongAdder;

/**
 * An implementation of {@link Counter}.
 */
public class DefaultCounter extends AbstractMetric implements Counter {

    private final LongAdder count = new LongAdder();

    public DefaultCounter(MetricId id) {
        super(id);
    }

    @Override
    public void increment() {
        count.increment();
    }

    @Override
    public void reset() {
        count.reset();
    }

    @Override
    public void increment(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to increment must be non-negative.");
        }
        count.add(amount);
    }

    @Override
    public long getValue() {
        return count.sum();
    }
}
