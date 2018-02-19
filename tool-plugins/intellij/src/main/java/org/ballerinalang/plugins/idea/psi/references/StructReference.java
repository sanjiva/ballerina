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

package org.ballerinalang.plugins.idea.psi.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.ballerinalang.plugins.idea.completion.BallerinaCompletionUtils;
import org.ballerinalang.plugins.idea.psi.IdentifierPSINode;
import org.ballerinalang.plugins.idea.psi.PackageNameNode;
import org.ballerinalang.plugins.idea.psi.impl.BallerinaPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a struct reference.
 */
public class StructReference extends BallerinaElementReference {

    public StructReference(@NotNull IdentifierPSINode element) {
        super(element);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        IdentifierPSINode identifier = getElement();
        PsiElement parent = identifier.getParent();

        PackageNameNode packageNameNode = PsiTreeUtil.getChildOfType(parent, PackageNameNode.class);
        if (packageNameNode == null) {
            return resolveInCurrentPackage();
        } else {
            return resolveInPackage(packageNameNode);
        }
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> results = new LinkedList<>();
        IdentifierPSINode identifier = getElement();
        PsiElement parent = identifier.getParent();

        PackageNameNode packageNameNode = PsiTreeUtil.getChildOfType(parent, PackageNameNode.class);
        if (packageNameNode == null) {
            results.addAll(getVariantsFromCurrentPackage());
        } else {
            results.addAll(getVariantsFromPackage(packageNameNode));
        }
        return results.toArray(new LookupElement[results.size()]);
    }

    @Nullable
    private PsiElement resolveInCurrentPackage() {
        IdentifierPSINode identifier = getElement();
        PsiFile containingFile = identifier.getContainingFile();
        if (containingFile == null) {
            return null;
        }
        PsiFile originalFile = containingFile.getOriginalFile();
        PsiDirectory psiDirectory = originalFile.getParent();
        if (psiDirectory == null) {
            return null;
        }
        return BallerinaPsiImplUtil.resolveElementInPackage(psiDirectory, identifier, false,
                false, true, false, false, false, true, true);
    }

    @Nullable
    private PsiElement resolveInPackage(PackageNameNode packageNameNode) {
        PsiElement resolvedElement = BallerinaPsiImplUtil.resolvePackage(packageNameNode);
        if (resolvedElement == null || !(resolvedElement instanceof PsiDirectory)) {
            return null;
        }
        PsiDirectory psiDirectory = (PsiDirectory) resolvedElement;
        IdentifierPSINode identifier = getElement();
        return BallerinaPsiImplUtil.resolveElementInPackage(psiDirectory, identifier, false, false, true, false,
                false, false, false, false);
    }

    @NotNull
    private List<LookupElement> getVariantsFromCurrentPackage() {
        List<LookupElement> results = new LinkedList<>();
        IdentifierPSINode identifier = getElement();
        PsiFile containingFile = identifier.getContainingFile();
        PsiFile originalFile = containingFile.getOriginalFile();
        PsiDirectory containingPackage = originalFile.getParent();
        List<IdentifierPSINode> structs = BallerinaPsiImplUtil.getAllStructsFromPackage(containingPackage, true, true);
        results.addAll(BallerinaCompletionUtils.createStructLookupElements(structs));
        return results;
    }

    @NotNull
    private Collection<? extends LookupElement> getVariantsFromPackage(PackageNameNode packageNameNode) {
        List<LookupElement> results = new LinkedList<>();
        PsiElement resolvedElement = BallerinaPsiImplUtil.resolvePackage(packageNameNode);
        if (resolvedElement == null || !(resolvedElement instanceof PsiDirectory)) {
            return results;
        }

        PsiDirectory containingPackage = (PsiDirectory) resolvedElement;
        List<IdentifierPSINode> structs = BallerinaPsiImplUtil.getAllStructsFromPackage(containingPackage, false,
                false);
        results.addAll(BallerinaCompletionUtils.createStructLookupElements(structs));
        return results;
    }
}
