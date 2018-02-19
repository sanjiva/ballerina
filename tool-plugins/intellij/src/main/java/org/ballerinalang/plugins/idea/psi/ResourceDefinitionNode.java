/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.ballerinalang.plugins.idea.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.antlr.jetbrains.adaptor.psi.IdentifierDefSubtree;
import org.ballerinalang.plugins.idea.BallerinaIcons;
import org.ballerinalang.plugins.idea.BallerinaTypes;
import org.ballerinalang.plugins.idea.psi.impl.BallerinaItemPresentation;
import org.ballerinalang.plugins.idea.psi.scopes.LowerLevelDefinition;
import org.ballerinalang.plugins.idea.psi.scopes.ParameterContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 * Represents an ResourceDefinition in PSI tree.
 */
public class ResourceDefinitionNode extends IdentifierDefSubtree implements LowerLevelDefinition, ParameterContainer {

    public ResourceDefinitionNode(@NotNull ASTNode node) {
        super(node, BallerinaTypes.IDENTIFIER);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new BallerinaItemPresentation(getNameIdentifier()) {

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return BallerinaIcons.RESOURCE;
            }
        };
    }

    @Nullable
    @Override
    public PsiElement resolve(PsiNamedElement element) {
        return null;
    }
}
