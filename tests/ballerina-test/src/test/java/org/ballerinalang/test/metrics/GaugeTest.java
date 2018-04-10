/*
 *
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */
package org.ballerinalang.test.metrics;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests for Gauge metric.
 */
public class GaugeTest extends MetricTest {
    private CompileResult compileResult;

    @BeforeClass
    public void setup() {
        compileResult = BCompileUtil.compile("test-src/metrics/gauge-test.bal");
    }

    @Test
    public void testCounterIncrementByOne() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testIncrementGaugeByOne");
        Assert.assertEquals(returns[0], new BFloat(1.0));
    }

    @Test
    public void testCounterIncrement() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testIncrementGauge");
        Assert.assertEquals(returns[0], new BFloat(5.0));
    }

    @Test
    public void testDecrementGaugeByOne() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testDecrementGaugeByOne");
        Assert.assertEquals(returns[0], new BFloat(9.0));
    }

    @Test
    public void testDecrementGauge() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testDecrementGauge");
        Assert.assertEquals(returns[0], new BFloat(8.0));
    }

    @Test
    public void testGaugeWithoutTags() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testGaugeWithoutTags");
        Assert.assertEquals(returns[0], new BFloat(6));
    }
}
