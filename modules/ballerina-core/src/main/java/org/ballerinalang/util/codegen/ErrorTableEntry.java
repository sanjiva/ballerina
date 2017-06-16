/*
*   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.ballerinalang.util.codegen;

import org.ballerinalang.bre.bvm.BLangVM;
import org.ballerinalang.model.types.BStructType;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.util.codegen.cpentries.StructureRefCPEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Describes an error handing section defined using try block in a Ballerina program.
 */
public class ErrorTableEntry {

    protected int ipFrom;
    protected int ipTo;
    protected int ipTarget;
    // Defined order in try catch.
    protected int priority;
    protected int errorStructCPEntryIndex = -100;

    // Cache values.
    private StructInfo error;
    private PackageInfo packageInfo;

    public ErrorTableEntry(int ipFrom, int ipTo, int ipTarget, int priority, int errorStructCPEntryIndex) {
        this.ipFrom = ipFrom;
        this.ipTo = ipTo;
        this.ipTarget = ipTarget;
        this.priority = priority;
        this.errorStructCPEntryIndex = errorStructCPEntryIndex;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
        // Load Cache values.
        if (errorStructCPEntryIndex < 0) {
            return;
        }
        StructureRefCPEntry structureRefCPEntry = (StructureRefCPEntry)
                packageInfo.getCPEntry(errorStructCPEntryIndex);
        this.error = (StructInfo) structureRefCPEntry.getStructureTypeInfo();
    }

    public StructInfo getError() {
        return error;
    }

    public int getIpTarget() {
        return ipTarget;
    }

    /**
     * returns ErrorStructCPEntryIndex.
     *
     * @return ErrorStructCPEntryIndex, if unhandled error returns -1.
     */
    public int getErrorStructCPEntryIndex() {
        return errorStructCPEntryIndex;
    }

    public boolean matchRange(int currentIP) {
        if (currentIP >= ipFrom && currentIP <= ipTo) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "\t\t" + ipFrom + "\t\t" + ipTo + "\t" + ipTarget + "\t\t" + (error != null ? error.getName() : "any");

    }

    private static class MatchedEntry {
        protected ErrorTableEntry errorTableEntry;
        // 0 - exact, 1 - equivalent, 2 - any.
        int status;
        int ipSize;
    }

    public static ErrorTableEntry getMatch(PackageInfo packageInfo, int currentIP, final BStruct error) {
        List<ErrorTableEntry> errorTableEntries = packageInfo.getErrorTableEntriesList();
        List<MatchedEntry> rangeMatched = new ArrayList<>();
        errorTableEntries.stream().filter(errorTableEntry -> errorTableEntry.matchRange(currentIP)).forEach
                (errorTableEntry -> {
                    MatchedEntry entry = new MatchedEntry();
                    entry.errorTableEntry = errorTableEntry;
                    entry.ipSize = errorTableEntry.ipTo - errorTableEntry.ipFrom;
                    if (errorTableEntry.getErrorStructCPEntryIndex() == -1) {
                        // match any.
                        entry.status = 2;
                        rangeMatched.add(entry);
                    } else if (errorTableEntry.getError().getType().equals(error.getType())) {
                        // exact match.
                        entry.status = 0;
                        rangeMatched.add(entry);
                    } else if (BLangVM.checkStructEquivalency((BStructType) error.getType(),
                            (BStructType) errorTableEntry.getError().getType())) {
                        entry.status = 1;
                        rangeMatched.add(entry);
                    }
                });
        if (rangeMatched.size() == 0) {
            return null;
        }
        if (rangeMatched.size() == 1) {
            return rangeMatched.get(0).errorTableEntry;
        }
        MatchedEntry[] matchedEntries = rangeMatched.stream().sorted(Comparator.comparingInt(o -> o.ipSize)).toArray
                (MatchedEntry[]::new);
        int currentSize = 0;
        ErrorTableEntry errorTableEntry = null;
        for (int i = 0; i < matchedEntries.length; i++) {
            MatchedEntry entry = matchedEntries[i];
            if (currentSize < entry.ipSize) {
                if (errorTableEntry == null) {
                    // Expand scope.
                    currentSize = entry.ipSize;
                } else {
                    // Return best match.
                    return errorTableEntry;
                }
            }
            if (entry.status == 0) {
                // Best case.
                return entry.errorTableEntry;
            } else {
                if (errorTableEntry == null) {
                    errorTableEntry = entry.errorTableEntry;
                } else {
                    if (errorTableEntry.priority > entry.errorTableEntry.priority) {
                        // found a high order entry
                        errorTableEntry = entry.errorTableEntry;
                    }
                }
            }
        }
        return errorTableEntry;
    }
}
