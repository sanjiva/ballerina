/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/

package org.ballerinalang.services.dispatchers.jms;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.Resource;
import org.ballerinalang.model.Service;
import org.ballerinalang.services.dispatchers.ResourceDispatcher;
import org.ballerinalang.util.codegen.ResourceInfo;
import org.ballerinalang.util.codegen.ServiceInfo;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

/**
 * Dispatcher that handles the resources of a JMS Service.
 */
public class JMSResourceDispatcher implements ResourceDispatcher {
    private static final Logger log = LoggerFactory.getLogger(JMSResourceDispatcher.class);

    @Override
    @Deprecated
    public Resource findResource(Service service, CarbonMessage cMsg, CarbonCallback callback, Context balContext)
            throws BallerinaException {
        if (log.isDebugEnabled()) {
            log.debug("Starting to find resource in the jms service " + service.getSymbolName().toString() + " to "
                            + "deliver the message");
        }
        Resource[] resources = service.getResources();
        if (resources.length == 0) {
            throw new BallerinaException("No resources found to handle the JMS message in " + service.getSymbolName()
                    .toString(), balContext);
        }
        if (resources.length > 1) {
            throw new BallerinaException("More than one resources found in JMS service " + service.getSymbolName()
                    .toString() + ".JMS Service should only have one resource", balContext);
        }
        return resources[0];
    }

    @Override
    public ResourceInfo findResource(ServiceInfo service, CarbonMessage cMsg, CarbonCallback callback) throws
            BallerinaException {
        if (log.isDebugEnabled()) {
            log.debug("Starting to find resource in the jms service " + service.getName() + " to "
                    + "deliver the message");
        }
        ResourceInfo[] resources = service.getResourceInfoList();
        if (resources.length == 0) {
            throw new BallerinaException("No resources found to handle the JMS message in " + service.getName());
        }
        if (resources.length > 1) {
            throw new BallerinaException("More than one resources found in JMS service " + service.getName() +
                    ".JMS Service should only have one resource");
        }
        return resources[0];
    }

    @Override
    public String getProtocol() {
        return Constants.PROTOCOL_JMS;
    }
}
