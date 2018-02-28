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

package org.wso2.ballerinalang.compiler.tree;

import org.ballerinalang.model.tree.DeprecatedNode;
import org.ballerinalang.model.tree.NodeKind;

/**
 * @since 0.962.0
 */
public class BLangDeprecatedNode extends BLangNode implements DeprecatedNode {

    public String documentationText;

    public BLangDeprecatedNode() {
    }

    @Override
    public String getDocumentationText() {
        return documentationText;
    }

    @Override
    public void setDocumentationText(String documentationText) {
        this.documentationText = documentationText;
    }

    @Override
    public void accept(BLangNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeKind getKind() {
        return NodeKind.DEPRECATED;
    }

    @Override
    public String toString() {
        return "BLangDeprecatedNode: " + documentationText;
    }
}
