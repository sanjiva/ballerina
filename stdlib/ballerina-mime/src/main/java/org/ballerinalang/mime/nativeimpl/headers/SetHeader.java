/*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.mime.nativeimpl.headers;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;

import static org.ballerinalang.mime.util.Constants.ENTITY_HEADERS;
import static org.ballerinalang.mime.util.Constants.FIRST_PARAMETER_INDEX;
import static org.ballerinalang.mime.util.Constants.SECOND_PARAMETER_INDEX;

/**
 * Set the given header value against the given header. If a header already exist, its value will be
 * replaced with the given header value.
 *
 * @since 0.966.0
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "mime",
        functionName = "setHeader",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = "Entity", structPackage = "ballerina.mime"),
        args = {@Argument(name = "headerName", type = TypeKind.STRING),
                @Argument(name = "headerValue", type = TypeKind.STRING)},
        returnType = {@ReturnType(type = TypeKind.VOID)},
        isPublic = true
)
public class SetHeader extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        BStruct entityStruct = (BStruct) context.getRefArgument(FIRST_PARAMETER_INDEX);
        String headerName = context.getStringArgument(FIRST_PARAMETER_INDEX);
        String headerValue = context.getStringArgument(SECOND_PARAMETER_INDEX);
        if (headerName == null || headerValue == null) {
            return;
        }
        HttpHeaders httpHeaders;
        if (entityStruct.getNativeData(ENTITY_HEADERS) != null) {
            httpHeaders = (HttpHeaders) entityStruct.getNativeData(ENTITY_HEADERS);

        } else {
            httpHeaders = new DefaultHttpHeaders();
            entityStruct.addNativeData(ENTITY_HEADERS, httpHeaders);
        }
        httpHeaders.set(headerName, headerValue);
        context.setReturnValues();
    }
}
