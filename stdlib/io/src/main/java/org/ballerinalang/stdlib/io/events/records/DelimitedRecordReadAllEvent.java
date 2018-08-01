/*
 * Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 */

package org.ballerinalang.stdlib.io.events.records;

import org.ballerinalang.stdlib.io.channels.base.Channel;
import org.ballerinalang.stdlib.io.channels.base.DelimitedRecordChannel;
import org.ballerinalang.stdlib.io.events.Event;
import org.ballerinalang.stdlib.io.events.EventContext;
import org.ballerinalang.stdlib.io.events.EventResult;
import org.ballerinalang.stdlib.io.events.EventType;
import org.ballerinalang.stdlib.io.events.result.ListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents delimited record readAll event.
 *
 * @since 0.966.0
 */
public class DelimitedRecordReadAllEvent implements Event {
    /**
     * Source which the delimited records will be read from.
     */
    private DelimitedRecordChannel channel;
    /**
     * Holds the context to the event.
     */
    private EventContext context;

    private static final Logger log = LoggerFactory.getLogger(DelimitedRecordReadAllEvent.class);

    public DelimitedRecordReadAllEvent(DelimitedRecordChannel channel, EventContext context) {
        this.channel = channel;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventResult get() {
        ListResult result;
        try {
            List<String[]> records = new ArrayList<>();
            while (channel.hasNext()) {
                records.add(channel.read());
            }
            result = new ListResult(records, context);
            return result;
        } catch (IOException e) {
            log.error("Error occurred while reading from the DelimitedRecordChannel", e);
            context.setError(e);
            result = new ListResult(context);
            return result;
        } catch (Throwable e) {
            log.error("Unidentified error occurred while reading all delimited records", e);
            context.setError(e);
            result = new ListResult(context);
            return result;
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        }
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
        return EventType.READ;
    }

    @Override
    public Channel getChannel() {
        return channel.getChannel();
    }
}
