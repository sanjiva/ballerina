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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.ballerinalang.net.jms.utils;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BLangVMErrors;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;

import static org.ballerinalang.bre.bvm.BLangVMErrors.STRUCT_GENERIC_ERROR;
import static org.ballerinalang.util.BLangConstants.BALLERINA_BUILTIN_PKG;

/**
 * Adapter class use used to bridge the connector native codes and Ballerina API.
 */
public class BallerinaAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BallerinaAdapter.class);

    private BallerinaAdapter() {
    }

    public static Struct getReceiverObject(Context context) {
        return BLangConnectorSPIUtil.getConnectorEndpointStruct(context);
    }

    public static <T> T getNativeObject(Struct struct, String objectId, Class<T> objectClass, Context context) {
        Object messageNativeData = struct.getNativeData(objectId);
        return verifyNativeObject(context, struct.getName(), objectClass, messageNativeData);
    }

    public static <T> T getNativeObject(BMap<String, BValue> struct, String objectId, Class<T> objectClass,
                                        Context context) {
        Object messageNativeData = struct.getNativeData(objectId);
        return verifyNativeObject(context, struct.getType().getName(), objectClass, messageNativeData);
    }

    private static <T> T verifyNativeObject(Context context, String structName, Class<T> objectClass, Object
            nativeData) {
        if (!objectClass.isInstance(nativeData)) {
            throw new BallerinaException(structName + " is not properly initialized.", context);
        }
        return objectClass.cast(nativeData);
    }

    public static void throwBallerinaException(String message, Context context, Throwable throwable) {
        LOGGER.error(message, throwable);
        throw new BallerinaException(message + " " + throwable.getMessage(), throwable, context);
    }

    private static BMap<String, BValue> createErrorRecord(Context context, String errorMsg, JMSException e) {
        BMap<String, BValue> errorStruct =
                BLangConnectorSPIUtil.createBStruct(context, BALLERINA_BUILTIN_PKG, STRUCT_GENERIC_ERROR);
        errorStruct.put(BLangVMErrors.ERROR_MESSAGE_FIELD, new BString(errorMsg + " " + e.getMessage()));
        return errorStruct;
    }

    public static void returnError(String errorMessage, Context context, JMSException e) {
        LOGGER.error(errorMessage, e);
        BMap<String, BValue> errorRecord = createErrorRecord(context, errorMessage, e);
        context.setReturnValues(errorRecord);
    }
}
