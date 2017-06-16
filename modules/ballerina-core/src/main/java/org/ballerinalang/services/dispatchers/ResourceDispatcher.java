/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.services.dispatchers;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.Resource;
import org.ballerinalang.model.Service;
import org.ballerinalang.util.codegen.ResourceInfo;
import org.ballerinalang.util.codegen.ServiceInfo;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

/**
 * {@code ServiceResourceDispatcherDispatcher} represents the resource level dispatcher interface.
 * <p>
 * Need to have a protocol specific dispatcher
 */
public interface ResourceDispatcher {

    /**
     * Find the resource which can handle a given cMsg.
     *
     * @param service  Ballerina Service which resources are belongs to
     * @param cMsg     Carbon Message
     * @param callback Carbon Messaging Callback
     * @param balContext Ballerina context
     * @return resource which can handle a given cMsg
     */
    @Deprecated
    Resource findResource(Service service, CarbonMessage cMsg, CarbonCallback callback, Context balContext)
            throws BallerinaException;

    /**
     * Find the resource which can handle a given cMsg.
     *
     * @param service  Ballerina Service which resources are belongs to
     * @param cMsg     Carbon Message
     * @param callback Carbon Messaging Callback
     * @return resource which can handle a given cMsg
     */
    ResourceInfo findResource(ServiceInfo service, CarbonMessage cMsg, CarbonCallback callback)
            throws BallerinaException;


    String getProtocol();

}
