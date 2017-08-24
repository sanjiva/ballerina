/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.util.codegen.cpentries;

import org.ballerinalang.util.codegen.ForkjoinInfo;

/**
 * {@code ForkJoinCPEntry} represents a Ballerina fork join statement in the constant pool.
 *
 * @since 0.90
 */
public class ForkJoinCPEntry implements ConstantPoolEntry {
    private int forkJoinCPIndex;

    private ForkjoinInfo forkjoinInfo;

    public ForkJoinCPEntry(int forkJoinCPIndex) {
        this.forkJoinCPIndex = forkJoinCPIndex;
    }

    public int getForkJoinCPIndex() {
        return forkJoinCPIndex;
    }

    public ForkjoinInfo getForkjoinInfo() {
        return forkjoinInfo;
    }

    public void setForkjoinInfo(ForkjoinInfo forkjoinInfo) {
        this.forkjoinInfo = forkjoinInfo;
    }

    public void setForkJoinCPIndex(int forkJoinCPIndex) {
        this.forkJoinCPIndex = forkJoinCPIndex;
    }

    public EntryType getEntryType() {
        return EntryType.CP_ENTRY_FORK_JOIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ForkJoinCPEntry that = (ForkJoinCPEntry) o;
        return forkJoinCPIndex == that.forkJoinCPIndex;
    }

    @Override
    public int hashCode() {
        return forkJoinCPIndex;
    }
}
