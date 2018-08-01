/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.ballerinalang.stdlib.io.events.data;

import org.ballerinalang.stdlib.io.channels.base.Channel;
import org.ballerinalang.stdlib.io.channels.base.DataChannel;
import org.ballerinalang.stdlib.io.events.Event;
import org.ballerinalang.stdlib.io.events.EventContext;
import org.ballerinalang.stdlib.io.events.EventResult;
import org.ballerinalang.stdlib.io.events.EventType;
import org.ballerinalang.stdlib.io.events.result.NumericResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Writes boolean to a given source.
 */
public class WriteBoolEvent implements Event {
    /**
     * Will be used to write bool.
     */
    private DataChannel channel;
    /**
     * Holds context to the event.
     */
    private EventContext context;
    /**
     * Represents the value which will be written.
     */
    private boolean value;

    private static final Logger log = LoggerFactory.getLogger(WriteBoolEvent.class);

    public WriteBoolEvent(DataChannel dataChannel, boolean value, EventContext context) {
        this.channel = dataChannel;
        this.context = context;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventResult get() {
        NumericResult result;
        try {
            channel.writeBoolean(value);
            result = new NumericResult(context);
        } catch (IOException e) {
            log.error("Error occurred while writing boolean", e);
            context.setError(e);
            result = new NumericResult(context);
        } catch (Throwable e) {
            log.error("Unidentified error occurred while writing boolean", e);
            context.setError(e);
            result = new NumericResult(context);
        }
        return result;
    }

    @Override
    public int getChannelId() {
        return channel.id();
    }

    @Override
    public boolean isSelectable() {
        return channel.isSelectable();
    }

    @Override
    public EventType getType() {
        return EventType.WRITE;
    }

    @Override
    public Channel getChannel() {
        return channel.getChannel();
    }
}
