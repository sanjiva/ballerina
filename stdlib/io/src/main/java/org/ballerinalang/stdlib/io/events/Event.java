/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.stdlib.io.events;

import org.ballerinalang.stdlib.io.channels.base.Channel;

import java.util.function.Supplier;

/**
 * <p>
 * Will represent an I/O event.
 * </p>
 * <p>
 * There will be several types of events read,write, close.
 * <p>
 * All these events should implement this interface.
 * </p>
 */
public interface Event extends Supplier<EventResult> {
    /**
     * Provides the unique channel id associated with the event.
     *
     * @return the id of the channel.
     */
    int getChannelId();
    /**
     * Specifies whether the event is selectable.
     *
     * @return true if the event is selectable.
     */
    boolean isSelectable();
    /**
     * Represents the type of the event.
     *
     * @return the type of the event.
     */
    EventType getType();
    /**
     * Represents the bytes channel included in the event.
     *
     * @return byte channel which represents the event.
     */
     Channel getChannel();
}
