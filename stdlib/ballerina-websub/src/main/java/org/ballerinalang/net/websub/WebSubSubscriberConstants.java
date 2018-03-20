/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.net.websub;

/**
 * Constants for WebSubSubscriber Services.
 *
 * @since 0.965.0
 */
public class WebSubSubscriberConstants {

    public static final String WEBSUB_SERVICE_REGISTRY = "WEBSUB_SERVICE_REGISTRY";
    public static final String WEBSUB_SUBSCRIBER_SERVICE_ENDPOINT_NAME =
                                                        "ballerina.net.websub:SubscriberServiceEndpoint";
    public static final String ANN_NAME_WEBSUB_SUBSCRIBER_SERVICE_CONFIG = "SubscriberServiceConfig";
    public static final String WEBSUB_PACKAGE_PATH = "ballerina.net.websub";

    public static final String RESOURCE_NAME_VERIFY_INTENT = "onVerifyIntent";
    public static final String RESOURCE_NAME_ON_NOTIFICATION = "onNotification";

    public static final String ANN_WEBSUB_ATTR_SUBSCRIBE_ON_STARTUP = "subscribeOnStartUp";
    public static final String ANN_WEBSUB_ATTR_HUB = "hub";
    public static final String ANN_WEBSUB_ATTR_TOPIC = "topic";
    public static final String ANN_WEBSUB_ATTR_LEASE_SECONDS = "leaseSeconds";
    public static final String ANN_WEBSUB_ATTR_SECRET = "secret";

}
