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

package org.ballerinalang.stdlib.io.events.records;

import org.ballerinalang.stdlib.io.channels.base.Channel;
import org.ballerinalang.stdlib.io.channels.base.DelimitedRecordChannel;
import org.ballerinalang.stdlib.io.events.Event;
import org.ballerinalang.stdlib.io.events.EventContext;
import org.ballerinalang.stdlib.io.events.EventResult;
import org.ballerinalang.stdlib.io.events.EventType;
import org.ballerinalang.stdlib.io.events.result.BooleanResult;
import org.ballerinalang.stdlib.io.utils.IOConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Validates whether there's another text record.
 */
public class HasNextDelimitedRecordEvent implements Event {
    /**
     * Delimited record channel which will hold the validation result.
     */
    private DelimitedRecordChannel channel;
    /**
     * Holds the context to the event.
     */
    private EventContext context;

    private static final Logger log = LoggerFactory.getLogger(HasNextDelimitedRecordEvent.class);

    public HasNextDelimitedRecordEvent(DelimitedRecordChannel channel, EventContext context) {
        this.channel = channel;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventResult get() {
        BooleanResult result;
        try {
            if (channel.hasReachedEnd()) {
                if (log.isDebugEnabled()) {
                    log.debug("Channel " + channel.hashCode() + " reached its end");
                }
                context.setError(new Throwable(IOConstants.IO_EOF));
                result = new BooleanResult(false, context);
            } else {
                boolean hasNext = channel.hasNext();
                result = new BooleanResult(hasNext, context);
            }
        } catch (IOException e) {
            String message = "Error occurred while reading bytes for hasNext()";
            log.error(message, e);
            context.setError(e);
            result = new BooleanResult(context);
        } catch (Throwable e) {
            log.error("Unidentified error occurred while performing hasNext()", e);
            context.setError(e);
            result = new BooleanResult(context);
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
        return EventType.READ;
    }

    @Override
    public Channel getChannel() {
        return channel.getChannel();
    }
}
