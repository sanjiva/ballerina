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
package org.wso2.ballerinalang.compiler.tree.expressions;


import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.model.tree.expressions.FieldBasedAccessNode;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BVarSymbol;
import org.wso2.ballerinalang.compiler.tree.BLangIdentifier;
import org.wso2.ballerinalang.compiler.tree.BLangNodeVisitor;
import org.wso2.ballerinalang.compiler.util.FieldType;
import org.wso2.ballerinalang.compiler.util.diagnotic.DiagnosticPos;

/**
 * Implementation of {@link FieldBasedAccessNode}.
 *
 * @since 0.94
 */
public class BLangFieldBasedAccess extends BLangVariableReference implements FieldBasedAccessNode {

    public BLangIdentifier field;

    public BLangVariableReference expr;

    public FieldType fieldType;

    @Override
    public BLangVariableReference getExpression() {
        return expr;
    }

    @Override
    public BLangIdentifier getFieldName() {
        return field;
    }

    @Override
    public String toString() {
        return String.valueOf(expr) + "." + String.valueOf(field);
    }

    @Override
    public void accept(BLangNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeKind getKind() {
        return NodeKind.FIELD_BASED_ACCESS_EXPR;
    }

    /**
     * @since 0.94
     */
    public static class BLangStructFieldAccessExpr extends BLangFieldBasedAccess {

        public BLangStructFieldAccessExpr(DiagnosticPos pos, BLangVariableReference varRef, BVarSymbol fieldSymbol) {
            this.pos = pos;
            this.expr = varRef;
            this.symbol = fieldSymbol;
        }

        @Override
        public void accept(BLangNodeVisitor visitor) {
            visitor.visit(this);
        }
    }

    /**
     * @since 0.94
     */
    public static class BLangEnumeratorAccessExpr extends BLangFieldBasedAccess {

        public BLangIdentifier enumeratorName;

        public BLangEnumeratorAccessExpr(DiagnosticPos pos, BLangIdentifier enumeratorName,
                                         BVarSymbol enumeratorSymbol) {
            this.pos = pos;
            this.enumeratorName = enumeratorName;
            this.symbol = enumeratorSymbol;
        }

        @Override
        public void accept(BLangNodeVisitor visitor) {
            visitor.visit(this);
        }
    }
}
