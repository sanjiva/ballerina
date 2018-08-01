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
package org.wso2.ballerinalang.compiler.tree.types;

import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.model.tree.VariableNode;
import org.ballerinalang.model.tree.types.StructureTypeNode;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BSymbol;
import org.wso2.ballerinalang.compiler.tree.BLangFunction;
import org.wso2.ballerinalang.compiler.tree.BLangVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code BLangStructureTypeNode} represents a structure type node in Ballerina.
 *
 * @since 0.971.0
 */
public abstract class BLangStructureTypeNode extends BLangType implements StructureTypeNode {

    public List<BLangVariable> fields;
    public BLangFunction initFunction;
    public boolean isAnonymous;
    public boolean isFieldAnalyseRequired;

    public BSymbol symbol;

    public BLangStructureTypeNode() {
        this.fields = new ArrayList<>();
    }

    @Override
    public List<? extends VariableNode> getFields() {
        return fields;
    }

    @Override
    public void addField(VariableNode field) {
        this.fields.add((BLangVariable) field);
    }

    @Override
    public NodeKind getKind() {
        return NodeKind.RECORD_TYPE;
    }

    @Override
    public String toString() {
        return "record { " + this.fields + " }";
    }

}
