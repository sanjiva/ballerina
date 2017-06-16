/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.model.statements;

import org.ballerinalang.model.NodeLocation;
import org.ballerinalang.model.NodeVisitor;
import org.ballerinalang.model.expressions.FunctionInvocationExpr;

/**
 * An {@code FunctionInvocationStmt} represents a function invocation statement.
 *
 * @since 0.8.0
 */
public class FunctionInvocationStmt extends AbstractStatement {

    private FunctionInvocationExpr functionInvocationExpr;

    public FunctionInvocationStmt(NodeLocation location, FunctionInvocationExpr functionInvocationExpr) {
        super(location);
        this.functionInvocationExpr = functionInvocationExpr;
    }

    public FunctionInvocationExpr getFunctionInvocationExpr() {
        return functionInvocationExpr;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}

