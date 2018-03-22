/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.nativeimpl.io;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.nativeimpl.io.channels.base.CharacterChannel;
import org.ballerinalang.nativeimpl.io.channels.base.DelimitedRecordChannel;
import org.ballerinalang.nativeimpl.io.utils.IOUtils;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Native function ballerina.io#createDelimitedRecordChannel.
 *
 * @since 0.963.0
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "io",
        functionName = "createDelimitedRecordChannel",
        args = {@Argument(name = "channel", type = TypeKind.STRUCT, structType = "DelimitedRecordChannel",
                structPackage = "ballerina.io"),
                @Argument(name = "recordSeparator", type = TypeKind.STRING),
                @Argument(name = "fieldSeparator", type = TypeKind.STRING)},
        returnType = {@ReturnType(type = TypeKind.STRUCT, structType = "DelimitedRecordChannel",
                structPackage = "ballerina.io"),
                @ReturnType(type = TypeKind.STRUCT, structType = "IOError", structPackage = "ballerina.io")},
        isPublic = true
)
public class CreateDelimitedRecordChannel extends BlockingNativeCallableUnit {

    private static final Logger log = LoggerFactory.getLogger(CreateDelimitedRecordChannel.class);

    /**
     * The index od the text record channel in ballerina.io#createDelimitedRecordChannel().
     */
    private static final int RECORD_CHANNEL_INDEX = 0;
    /**
     * The index of the record channel separator in ballerina.io#createDelimitedRecordChannel().
     */
    private static final int RECORD_SEPARATOR_INDEX = 0;
    /**
     * The index of the field separator in ballerina.io#createDelimitedRecordChannel().
     */
    private static final int FIELD_SEPARATOR_INDEX = 1;
    /**
     * The package path of the byte channel.
     */
    private static final String RECORD_CHANNEL_PACKAGE = "ballerina.io";
    /**
     * The type of the byte channel.
     */
    private static final String STRUCT_TYPE = "DelimitedRecordChannel";

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Context context) {
        try {
            //File which holds access to the channel information
            BStruct textRecordChannelInfo = (BStruct) context.getRefArgument(RECORD_CHANNEL_INDEX);
            String recordSeparator = context.getStringArgument(RECORD_SEPARATOR_INDEX);
            String fieldSeparator = context.getStringArgument(FIELD_SEPARATOR_INDEX);

            BStruct textRecordChannel = BLangConnectorSPIUtil.createBStruct(context, RECORD_CHANNEL_PACKAGE,
                    STRUCT_TYPE);

            //Will get the relevant byte channel and will create a character channel
            CharacterChannel characterChannel = (CharacterChannel) textRecordChannelInfo.getNativeData(IOConstants
                    .CHARACTER_CHANNEL_NAME);
            DelimitedRecordChannel bCharacterChannel = new DelimitedRecordChannel(characterChannel, recordSeparator,
                    fieldSeparator);
            textRecordChannel.addNativeData(IOConstants.TXT_RECORD_CHANNEL_NAME, bCharacterChannel);
            context.setReturnValues(textRecordChannel);
        } catch (Throwable e) {
            String message =
                    "Error occurred while converting character channel to textRecord channel:" + e.getMessage();
            log.error(message, e);
            context.setReturnValues(IOUtils.createError(context, message));
        }
    }
}
