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
package org.wso2.ballerinalang.compiler.tree.statements;

import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.model.tree.statements.BlockNode;
import org.ballerinalang.model.tree.statements.TransformNode;
import org.wso2.ballerinalang.compiler.tree.BLangNodeVisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 0.94
 */
public class BLangTransform extends BLangStatement implements TransformNode {
    public BLangBlockStmt body;
    public Set<String> inputs;
    public Set<String> outputs;

    public BLangTransform() {
        this.inputs = new HashSet<>();
        this.outputs = new HashSet<>();
    }

    @Override
    public BLangBlockStmt getBody() {
        return body;
    }

    @Override
    public void setBody(BlockNode body) {
        this.body = (BLangBlockStmt) body;
    }

    @Override
    public Set<String> getInputs() {
        return inputs;
    }

    @Override
    public void addInput(String name) {
        this.inputs.add(name);
    }

    @Override
    public Set<String> getOutputs() {
        return outputs;
    }

    @Override
    public void addOutput(String name) {
        this.outputs.add(name);
    }

    @Override
    public void accept(BLangNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeKind getKind() {
        return NodeKind.TRANSFORM;
    }
}
