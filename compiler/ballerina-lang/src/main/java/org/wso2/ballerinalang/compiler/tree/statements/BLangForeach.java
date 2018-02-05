/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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
package org.wso2.ballerinalang.compiler.tree.statements;

import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.model.tree.expressions.ExpressionNode;
import org.ballerinalang.model.tree.expressions.VariableReferenceNode;
import org.ballerinalang.model.tree.statements.BlockNode;
import org.ballerinalang.model.tree.statements.ForeachNode;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.tree.BLangNodeVisitor;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangVariableReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Foreach Statement.
 *
 * @since 0.96.0
 */
public class BLangForeach extends BLangStatement implements ForeachNode {

    public BLangExpression collection;
    public List<BLangExpression> varRefs = new ArrayList<>();
    public BLangBlockStmt body;
    public List<BType> varTypes;

    @Override
    public ExpressionNode getCollection() {
        return collection;
    }

    @Override
    public void setCollection(ExpressionNode collection) {
        this.collection = (BLangExpression) collection;
    }

    @Override
    public List<BLangExpression> getVariables() {
        return varRefs;
    }

    @Override
    public void addVariable(VariableReferenceNode variableReferenceNode) {
        this.varRefs.add((BLangVariableReference) variableReferenceNode);
    }

    @Override
    public BlockNode getBody() {
        return body;
    }

    @Override
    public void setBody(BlockNode body) {
        this.body = (BLangBlockStmt) body;
    }

    @Override
    public NodeKind getKind() {
        return NodeKind.FOREACH;
    }

    @Override
    public void accept(BLangNodeVisitor visitor) {
        visitor.visit(this);
    }
}
