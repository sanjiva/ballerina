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
 */

package org.ballerinalang.stdlib.io.utils;

/**
 * I/O related constants.
 */
public class IOConstants {
    /**
     * Name of the ByteChannel which will be represented through the native struct.
     *
     * @see java.nio.channels.ByteChannel
     */
    public static final String BYTE_CHANNEL_NAME = "channel";

    /**
     * Represents the name of the data channel.
     */
    public static final String DATA_CHANNEL_NAME = "data_channel";

    /**
     * The name of the character channel which will be represented through the native struct.
     */
    public static final String CHARACTER_CHANNEL_NAME = "char_channel";

    /**
     * The name of the text record channel which will be represented through the native struct.
     */
    public static final String TXT_RECORD_CHANNEL_NAME = "txt_record";

    /**
     * The size of the buffer allocated for reading bytes from the channel (15~ KB).
     */
    public static final int CHANNEL_BUFFER_SIZE = 16384;

    /**
     * Name of the Socket which will be represented through the native struct.
     */
    public static final String CLIENT_SOCKET_NAME = "clientSocket";

    /**
     * Specifies the io package information.
     */
    public static final String IO_PACKAGE = "ballerina/io";

    /**
     * Error flag to indicate when file reaches it's end.
     */
    public static final String IO_EOF = "io.EOF";

    /**
     * Represents the base number of protobuf.
     */
    public static final int PROTO_BUF_BASE = 7;

    /**
     * The maximum value which will represent var int.
     */
    public static final long VAR_INT_MAX = 0xFFFFFFFFFFFFFL;

    /**
     * Minimum value which will represent var int.
     */
    public static final long VAR_INT_MIN = (0xFFFFFFFFFFFFFL * -1);
}
