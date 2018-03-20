/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ballerinalang.compiler.tree.clauses;

import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.model.tree.clauses.StreamingInput;
import org.ballerinalang.model.tree.clauses.WhereNode;
import org.ballerinalang.model.tree.clauses.WindowClauseNode;
import org.ballerinalang.model.tree.expressions.ExpressionNode;
import org.wso2.ballerinalang.compiler.tree.BLangNode;
import org.wso2.ballerinalang.compiler.tree.BLangNodeVisitor;

/**
 * @since 0.965.0
 *
 * Implementation of {@link StreamingInput}.
 */

public class BLangStreamingInput extends BLangNode implements StreamingInput {

    private WhereNode beforeStreamingCondition;
    private WindowClauseNode windowClause;
    private WhereNode afterStreamingCondition;
    private ExpressionNode streamReference;
    private String alias;
    private boolean isWindowTraversedAfterWhere;

    @Override
    public void setBeforeStreamingCondition(WhereNode where) {
        this.beforeStreamingCondition = where;
    }

    @Override
    public WhereNode getBeforeStreamingCondition() {
        return this.beforeStreamingCondition;
    }

    @Override
    public void setAfterStreamingCondition(WhereNode where) {
        this.afterStreamingCondition = where;
    }

    @Override
    public WhereNode getAfterStreamingCondition() {
        return afterStreamingCondition;
    }

    @Override
    public boolean isWindowTraversedAfterWhere() {
        return isWindowTraversedAfterWhere;
    }

    @Override
    public void setWindowTraversedAfterWhere(boolean windowTraversedAfterWhere) {
        isWindowTraversedAfterWhere = windowTraversedAfterWhere;
    }

    @Override
    public void setWindowClause(WindowClauseNode windowClause) {
        this.windowClause = windowClause;
    }

    @Override
    public WindowClauseNode getWindowClause() {
        return this.windowClause;
    }

    public ExpressionNode getStreamReference() {
        return streamReference;
    }

    public void setStreamReference(ExpressionNode streamReference) {
        this.streamReference = streamReference;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public void accept(BLangNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeKind getKind() {
        return NodeKind.STREAMING_INPUT;
    }


}
