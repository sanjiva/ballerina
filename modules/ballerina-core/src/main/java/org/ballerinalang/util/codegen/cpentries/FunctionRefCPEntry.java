/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.ballerinalang.util.codegen.cpentries;

import org.ballerinalang.util.codegen.FunctionInfo;

import java.util.Objects;

/**
 * {@code FunctionRefCPEntry} represents a Ballerina function in the constant pool.
 *
 * @since 0.87
 */
public class FunctionRefCPEntry implements ConstantPoolEntry {

    // Index to a valid Package entry in the constant pool
    private int packageCPIndex;

    // Index to a valid name index in the constant pool
    private int nameCPIndex;

    // TODO 
    private FunctionInfo functionInfo;

    public FunctionRefCPEntry(int packageCPIndex, int nameCPIndex) {
        this.packageCPIndex = packageCPIndex;
        this.nameCPIndex = nameCPIndex;
    }

    public int getPackageCPIndex() {
        return packageCPIndex;
    }

    public int getNameCPIndex() {
        return nameCPIndex;
    }

    public FunctionInfo getFunctionInfo() {
        return functionInfo;
    }

    public void setFunctionInfo(FunctionInfo functionInfo) {
        this.functionInfo = functionInfo;
    }

    public EntryType getEntryType() {
        return EntryType.CP_ENTRY_FUNCTION_REF;
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageCPIndex, nameCPIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FunctionRefCPEntry && packageCPIndex == (((FunctionRefCPEntry) obj).packageCPIndex) &&
                nameCPIndex == ((FunctionRefCPEntry) obj).nameCPIndex;
    }
}
