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
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.model.types.BServiceType;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BTypeDescValue;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.net.websub.WebSubSubscriberConstants;

/**
 * Native function to retrieve annotations specified for the WebSub subscriber service.
 *
 * @since 0.965.0
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "net.websub",
        functionName = "retrieveSecret",
        args = {@Argument(name = "serviceType", type = TypeKind.TYPEDESC)},
        returnType = {@ReturnType(type = TypeKind.STRING)}
)
public class RetrieveSecret extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {

        String secret = ((BStruct) BLangConnectorSPIUtil.getService(context.getProgramFile(),
                    ((BServiceType) ((BTypeDescValue) (context).getLocalWorkerData().refRegs[0]).value()))
                .getAnnotationList(WebSubSubscriberConstants.WEBSUB_PACKAGE_PATH,
                                   WebSubSubscriberConstants.ANN_NAME_WEBSUB_SUBSCRIBER_SERVICE_CONFIG)
                .get(0).getValue().getVMValue()).getStringField(3);

        context.setReturnValues(new BString(secret));
    }

}
