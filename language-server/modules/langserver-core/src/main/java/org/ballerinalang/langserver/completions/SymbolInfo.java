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

package org.ballerinalang.langserver.completions;

import org.ballerinalang.model.symbols.BLangSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.Scope;

/**
 * Represents the symbol information.
 */
public class SymbolInfo {
    private String symbolName;
    private BLangSymbol symbol;
    private Scope.ScopeEntry scopeEntry;
    private boolean isIterableOperation;
    private IterableOperationSignature iterableOperationSignature;

    public SymbolInfo(String symbolName, BLangSymbol symbol) {
        this.symbolName = symbolName;
        this.symbol = symbol;
    }

    public SymbolInfo(String symbolName, Scope.ScopeEntry scopeEntry) {
        this.symbolName = symbolName;
        this.scopeEntry = scopeEntry;
    }
    
    public SymbolInfo() {
    }

    public String getSymbolName() {
        return symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public BLangSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(BLangSymbol symbol) {
        this.symbol = symbol;
    }

    public Scope.ScopeEntry getScopeEntry() {
        return scopeEntry;
    }

    public boolean isIterableOperation() {
        return isIterableOperation;
    }

    public void setIterableOperation(boolean iterableOperation) {
        isIterableOperation = iterableOperation;
    }

    public IterableOperationSignature getIterableOperationSignature() {
        return iterableOperationSignature;
    }

    public void setIterableOperationSignature(IterableOperationSignature iterableOperationSignature) {
        this.iterableOperationSignature = iterableOperationSignature;
    }

    /**
     * Holds the iterable operation signature information.
     */
    public static class IterableOperationSignature {
        String label;
        String insertText;

        public IterableOperationSignature(String label, String insertText) {
            this.label = label;
            this.insertText = insertText;
        }

        public String getLabel() {
            return label;
        }

        public String getInsertText() {
            return insertText;
        }
    }
}
