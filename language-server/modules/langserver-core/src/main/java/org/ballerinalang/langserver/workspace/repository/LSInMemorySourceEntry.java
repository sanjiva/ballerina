/*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.langserver.workspace.repository;

import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.repository.PackageSourceEntry;

/**
 * LSInMemorySourceEntry.
 */
public class LSInMemorySourceEntry implements PackageSourceEntry {

    private String name;
    private byte[] code;

    public LSInMemorySourceEntry(String name, byte[] code) {
        this.name = name;
        this.code = code.clone();
    }

    @Override
    public PackageID getPackageID() {
        // TODO: FIX
        return null;
    }

    @Override
    public String getEntryName() {
        return name;
    }

    @Override
    public byte[] getCode() {
        return code.clone();
    }
}
