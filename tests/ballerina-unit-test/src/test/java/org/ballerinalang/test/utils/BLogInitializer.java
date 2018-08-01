/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.utils;

import org.ballerinalang.logging.BLogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.util.logging.LogManager;

/**
 * A test suit listener for configuring logger, prior to running the tests.
 *
 * @since 0.964.0
 */
public class BLogInitializer implements ISuiteListener {

    private static Logger log = LoggerFactory.getLogger(BLogInitializer.class);

    @Override
    public void onStart(ISuite iSuite) {
        ((BLogManager) LogManager.getLogManager()).loadUserProvidedLogConfiguration();
        log.info("Logging initialized...");
    }

    @Override
    public void onFinish(ISuite iSuite) {
    }
}
