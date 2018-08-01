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

package org.ballerinalang.stdlib.io.events.characters;

import org.ballerinalang.stdlib.io.channels.base.Channel;
import org.ballerinalang.stdlib.io.channels.base.CharacterChannel;
import org.ballerinalang.stdlib.io.events.Event;
import org.ballerinalang.stdlib.io.events.EventContext;
import org.ballerinalang.stdlib.io.events.EventResult;
import org.ballerinalang.stdlib.io.events.EventType;
import org.ballerinalang.stdlib.io.events.result.AlphaResult;
import org.ballerinalang.stdlib.io.utils.IOConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Represents an event which will read characters.
 */
public class ReadCharactersEvent implements Event {
    /**
     * Represents the character channel the characters will be read from.
     */
    private CharacterChannel channel;

    /**
     * Specified the number of characters which should be read.
     */
    private int numberOfCharacters;
    /**
     * Context of the event which will be called upon completion.
     */
    private EventContext context;
    /**
     * Represents empty string.
     */
    private static final String EMPTY = "";

    private static final Logger log = LoggerFactory.getLogger(ReadCharactersEvent.class);

    public ReadCharactersEvent(CharacterChannel channel, int numberOfCharacters) {
        this.channel = channel;
        this.numberOfCharacters = numberOfCharacters;
    }

    public ReadCharactersEvent(CharacterChannel channel, int numberOfCharacters, EventContext context) {
        this.channel = channel;
        this.numberOfCharacters = numberOfCharacters;
        this.context = context;
    }

    @Override
    public EventResult get() {
        AlphaResult result;
        try {
            if (channel.hasReachedEnd()) {
                if (log.isDebugEnabled()) {
                    log.debug("Channel " + channel.hashCode() + " reached it's end");
                }
                context.setError(new Throwable(IOConstants.IO_EOF));
                result = new AlphaResult(EMPTY, context);
            } else {
                String content = channel.read(numberOfCharacters);
                result = new AlphaResult(content, context);
            }
        } catch (IOException e) {
            log.error("Error occurred while reading from character channel", e);
            context.setError(e);
            result = new AlphaResult(context);
        } catch (Throwable e) {
            log.error("IO error occurred while reading from character channel", e);
            context.setError(e);
            result = new AlphaResult(context);
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
