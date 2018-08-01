/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.broker;

import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;
import org.ballerinalang.model.values.BValue;

/**
 * Implementation of {@link io.netty.buffer.ByteBuf}, to hold a {@link BValue}, to use with the internal broker core
 * used in Ballerina.
 *
 * @since 0.973.0
 */
public class BallerinaBrokerByteBuf extends UnpooledHeapByteBuf {

    private final BValue value;

    public BallerinaBrokerByteBuf(BValue value) {
        super(UnpooledByteBufAllocator.DEFAULT, 0, 0);
        this.value = value;
    }

    public BValue getValue() {
        return value;
    }
}
