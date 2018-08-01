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
package org.ballerinalang.langserver.index.dao;

import org.eclipse.lsp4j.CompletionItem;

/**
 * DAO for BInvokableSymbol.
 */
public class PackageFunctionDAO {
    
    private String packageName;
    
    private String packageOrgName;
    
    private String functionName;
    
    private CompletionItem completionItem;
    
    private boolean isPrivate;
    
    private boolean isAttached;

    public PackageFunctionDAO(String packageName, String packageOrgName, String functionName,
                              CompletionItem completionItem, boolean isPrivate, boolean isAttached) {
        this.packageName = packageName;
        this.packageOrgName = packageOrgName;
        this.functionName = functionName;
        this.completionItem = completionItem;
        this.isPrivate = isPrivate;
        this.isAttached = isAttached;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPackageOrgName() {
        return packageOrgName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public CompletionItem getCompletionItem() {
        return completionItem;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isAttached() {
        return isAttached;
    }
}
