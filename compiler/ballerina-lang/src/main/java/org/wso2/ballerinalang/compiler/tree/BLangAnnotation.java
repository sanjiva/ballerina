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
package org.wso2.ballerinalang.compiler.tree;

import org.ballerinalang.model.elements.Flag;
import org.ballerinalang.model.tree.AnnotationAttachmentNode;
import org.ballerinalang.model.tree.AnnotationAttributeNode;
import org.ballerinalang.model.tree.AnnotationNode;
import org.ballerinalang.model.tree.DeprecatedNode;
import org.ballerinalang.model.tree.DocumentationNode;
import org.ballerinalang.model.tree.IdentifierNode;
import org.ballerinalang.model.tree.NodeKind;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BSymbol;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @since 0.94
 */
public class BLangAnnotation extends BLangNode implements AnnotationNode {
    public BLangIdentifier name;
    public List<BLangAnnotAttribute> attributes;
    public Set<Flag> flagSet;
    public List<BLangAnnotationAttachment> annAttachments;
    public List<BLangDocumentation> docAttachments;
    public List<BLangDeprecatedNode> deprecatedAttachments;
    public BSymbol symbol;
    public List<BLangAnnotationAttachmentPoint> attachmentPoints;

    public BLangAnnotation() {
        this.attributes = new ArrayList<>();
        this.flagSet = EnumSet.noneOf(Flag.class);
        this.annAttachments = new ArrayList<>();
        this.attachmentPoints = new ArrayList<>();
        this.docAttachments = new ArrayList<>();
        this.deprecatedAttachments = new ArrayList<>();
    }

    public void addAttachmentPoint(BLangAnnotationAttachmentPoint attachmentPoint) {
        attachmentPoints.add(attachmentPoint);
    }

    public List<BLangAnnotationAttachmentPoint> getAttachmentPoints() {
        return attachmentPoints;
    }

    @Override
    public IdentifierNode getName() {
        return name;
    }

    @Override
    public void setName(IdentifierNode name) {
        this.name = (BLangIdentifier) name;
    }

    @Override
    public List<BLangAnnotAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public void addAttribute(AnnotationAttributeNode attribute) {
        this.attributes.add((BLangAnnotAttribute) attribute);
    }

    @Override
    public Set<? extends Flag> getFlags() {
        return flagSet;
    }

    @Override
    public void addFlag(Flag flag) {
        this.flagSet.add(flag);
    }

    @Override
    public List<BLangAnnotationAttachment> getAnnotationAttachments() {
        return annAttachments;
    }

    @Override
    public void addAnnotationAttachment(AnnotationAttachmentNode annAttachment) {
        this.annAttachments.add((BLangAnnotationAttachment) annAttachment);
    }

    @Override
    public List<BLangDocumentation> getDocumentationAttachments() {
        return docAttachments;
    }

    @Override
    public void addDocumentationAttachment(DocumentationNode docAttachment) {
        this.docAttachments.add((BLangDocumentation) docAttachment);
    }

    @Override
    public List<BLangDeprecatedNode> getDeprecatedAttachments() {
        return deprecatedAttachments;
    }

    @Override
    public void addDeprecatedAttachment(DeprecatedNode deprecatedNode) {
        this.deprecatedAttachments.add((BLangDeprecatedNode) deprecatedNode);
    }

    @Override
    public void accept(BLangNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeKind getKind() {
        return NodeKind.ANNOTATION;
    }

    @Override
    public String toString() {
        return "BLangAnnotation: " + this.name + " -> " + this.attributes;
    }
}
