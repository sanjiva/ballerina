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

package org.ballerinalang.nativeimpl.io.channels;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.nativeimpl.io.IOConstants;
import org.ballerinalang.nativeimpl.io.channels.base.Channel;
import org.ballerinalang.util.exceptions.BallerinaException;

/**
 * <p>
 * For I/O APIs to work it requires each I/O source (file,tcp socket) to return a generic ByteChannel.
 * </p>
 * <p>
 * This will prepare the ByteChannel to perform I/O operations.
 * </p>
 *
 * @see org.ballerinalang.nativeimpl.io.OpenFile
 */
public abstract class AbstractNativeChannel extends BlockingNativeCallableUnit {
    /**
     * The package path of the byte channel.
     */
    private static final String BYTE_CHANNEL_PACKAGE = "ballerina.io";

    /**
     * The struct type of the byte channel.
     */
    private static final String STRUCT_TYPE = "ByteChannel";

    /**
     * <p>
     * Defines the set of actions which should be performed to created a byte channel.
     * </p>
     *
     * @param context holds the context received from Ballerina.
     * @return the channel which holds the reference.
     */
    public abstract Channel inFlow(Context context) throws BallerinaException;


    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Context context) {
        Channel channel = inFlow(context);
        BStruct channelStruct = BLangConnectorSPIUtil.createBStruct(context, BYTE_CHANNEL_PACKAGE, STRUCT_TYPE);
        channelStruct.addNativeData(IOConstants.BYTE_CHANNEL_NAME, channel);
        context.setReturnValues(channelStruct);
    }
}
