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
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.ballerinalang.compiler.semantics.model.iterable;

import org.wso2.ballerinalang.compiler.semantics.model.SymbolEnv;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BInvokableSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.types.BInvokableType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.tree.BLangVariable;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangInvocation;
import org.wso2.ballerinalang.compiler.util.diagnotic.DiagnosticPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model of an iterable operation, that used for semantic and desugar analysis.
 *
 * @since 0.961.0
 */
public class Operation {

    public BLangInvocation iExpr;
    public DiagnosticPos pos;
    public IterableKind kind;
    public SymbolEnv env;

    public Operation previous;

    public BType collectionType;
    public List<BType> expectedTypes;
    public List<BType> resultTypes; // Reduced value or intermediate collection type.
    public List<BType> argTypes = new ArrayList<>();        // Operation's input arguments types.
    public List<BType> retArgTypes = new ArrayList<>();     // Operation's output arguments types.

    /* variables for lambda based operations. */
    public int arity;
    public BInvokableSymbol lambdaSymbol;
    public BInvokableType lambdaType;

    /* fields required for code generation. */
    public List<BLangVariable> argVars;
    public List<BLangVariable> retVars;

    public Operation(IterableKind iterableKind, BLangInvocation iExpr, List<BType> expTypes, SymbolEnv env) {
        this.iExpr = iExpr;
        if (iExpr.argExprs.isEmpty()) {
            this.pos = iExpr.pos;
        } else {
            this.pos = iExpr.argExprs.get(0).pos;
        }
        this.collectionType = iExpr.expr.type;
        this.kind = iterableKind;
        this.expectedTypes = expTypes;
        this.env = env;
        this.argVars = new ArrayList<>();
        this.retVars = new ArrayList<>();
    }
}
