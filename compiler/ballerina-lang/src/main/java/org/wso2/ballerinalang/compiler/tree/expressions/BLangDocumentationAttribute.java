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
package org.wso2.ballerinalang.compiler.tree.expressions;

import org.ballerinalang.model.elements.DocTag;
import org.ballerinalang.model.tree.IdentifierNode;
import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.model.tree.expressions.DocumentationAttributeNode;
import org.wso2.ballerinalang.compiler.tree.BLangIdentifier;
import org.wso2.ballerinalang.compiler.tree.BLangNodeVisitor;

/**
 * @since 0.962.0
 */
public class BLangDocumentationAttribute extends
        BLangExpression implements DocumentationAttributeNode {

    public BLangIdentifier documentationField;
    public String documentationText;
    public DocTag docTag;

    @Override
    public BLangIdentifier getDocumentationField() {
        return documentationField;
    }

    @Override
    public void setDocumentationField(IdentifierNode field) {
        this.documentationField = (BLangIdentifier) field;
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
        return NodeKind.DOCUMENTATION_ATTRIBUTE;
    }

    @Override
    public String toString() {
        return "BLangDocAttachmentAttribute: " + (documentationField != null ? documentationField.toString() + " : "
                + documentationText.toString() : documentationText.toString());
    }

}
