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

package org.ballerinalang.net.websub.nativeimpl;

import org.ballerinalang.bre.Context;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.net.http.HttpService;
import org.ballerinalang.net.http.serviceendpoint.AbstractHttpNativeFunction;
import org.ballerinalang.net.websub.WebSubHttpService;
import org.ballerinalang.net.websub.WebSubServicesRegistry;
import org.ballerinalang.net.websub.WebSubSubscriberConstants;

/**
 * Set the hub and topic the service is subscribing, if the resource URL was specified as an annotation.
 *
 * @since 0.965.0
 */

@BallerinaFunction(
        orgName = "ballerina", packageName = "websub",
        functionName = "setTopic",
        args = {@Argument(name = "topic", type = TypeKind.STRING)},
        receiver = @Receiver(type = TypeKind.STRUCT, structType = "Listener",
                structPackage = WebSubSubscriberConstants.WEBSUB_PACKAGE_PATH),
        isPublic = true
)
public class SetTopic extends AbstractHttpNativeFunction {

    @Override
    public void execute(Context context) {

        Struct subscriberServiceEndpoint = BLangConnectorSPIUtil.getConnectorEndpointStruct(context);
        Struct serviceEndpoint = subscriberServiceEndpoint.getStructField("serviceEndpoint");

        HttpService httpService = ((HttpService) ((WebSubServicesRegistry) serviceEndpoint.getNativeData(
                WebSubSubscriberConstants.WEBSUB_SERVICE_REGISTRY)).getServicesInfoByInterface()
                .values().toArray()[0]);

        String topic = context.getStringArgument(0);
        ((WebSubHttpService) httpService).setTopic(topic);
        context.setReturnValues();

    }

}
