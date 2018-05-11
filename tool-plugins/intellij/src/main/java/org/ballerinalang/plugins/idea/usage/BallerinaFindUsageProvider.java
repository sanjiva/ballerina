/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.ballerinalang.plugins.idea.usage;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.ballerinalang.plugins.idea.psi.BallerinaAnnotationDefinition;
import org.ballerinalang.plugins.idea.psi.BallerinaAnyIdentifierName;
import org.ballerinalang.plugins.idea.psi.BallerinaCallableUnitSignature;
import org.ballerinalang.plugins.idea.psi.BallerinaEndpointDefinition;
import org.ballerinalang.plugins.idea.psi.BallerinaEndpointParameter;
import org.ballerinalang.plugins.idea.psi.BallerinaFieldDefinition;
import org.ballerinalang.plugins.idea.psi.BallerinaGlobalEndpointDefinition;
import org.ballerinalang.plugins.idea.psi.BallerinaGlobalVariableDefinition;
import org.ballerinalang.plugins.idea.psi.BallerinaIdentifier;
import org.ballerinalang.plugins.idea.psi.BallerinaNamedPattern;
import org.ballerinalang.plugins.idea.psi.BallerinaObjectCallableUnitSignature;
import org.ballerinalang.plugins.idea.psi.BallerinaObjectInitializer;
import org.ballerinalang.plugins.idea.psi.BallerinaObjectParameter;
import org.ballerinalang.plugins.idea.psi.BallerinaOrgName;
import org.ballerinalang.plugins.idea.psi.BallerinaPackageReference;
import org.ballerinalang.plugins.idea.psi.BallerinaParameterWithType;
import org.ballerinalang.plugins.idea.psi.BallerinaPrivateObjectFields;
import org.ballerinalang.plugins.idea.psi.BallerinaPublicObjectFields;
import org.ballerinalang.plugins.idea.psi.BallerinaRestParameter;
import org.ballerinalang.plugins.idea.psi.BallerinaTypeDefinition;
import org.ballerinalang.plugins.idea.psi.BallerinaVariableDefinitionStatement;
import org.ballerinalang.plugins.idea.psi.BallerinaWorkerDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Find usage provider for Ballerina.
 */
public class BallerinaFindUsageProvider implements FindUsagesProvider {

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return null;
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof BallerinaIdentifier;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        PsiElement superParent = parent.getParent();
        if (parent instanceof BallerinaAnyIdentifierName) {
            if (superParent instanceof BallerinaCallableUnitSignature) {
                return "Function";
            } else if (superParent instanceof BallerinaObjectCallableUnitSignature) {
                return "Object Function";
            }
        } else if (parent instanceof BallerinaPackageReference) {
            return "Package";
        } else if (parent instanceof BallerinaOrgName) {
            return "Organization";
        } else if (parent instanceof BallerinaGlobalVariableDefinition) {
            return "Global Variable";
        } else if (parent instanceof BallerinaEndpointDefinition) {
            if (superParent instanceof BallerinaGlobalEndpointDefinition) {
                return "Global Endpoint";
            } else {
                return "Endpoint";
            }
        } else if (parent instanceof BallerinaTypeDefinition) {
            return "Type";
        } else if (parent instanceof BallerinaObjectCallableUnitSignature) {
            return "Object Function";
        } else if (parent instanceof BallerinaParameterWithType) {
            return "Parameter";
        } else if (parent instanceof BallerinaWorkerDefinition) {
            return "Worker";
        } else if (parent instanceof BallerinaAnnotationDefinition) {
            return "Annotation";
        } else if (parent instanceof BallerinaFieldDefinition) {
            if (superParent instanceof BallerinaPublicObjectFields) {
                return "Public Object Field";
            } else if (superParent instanceof BallerinaPrivateObjectFields) {
                return "Private Object Field";
            } else {
                return "Field";
            }
        } else if (parent instanceof BallerinaObjectParameter) {
            return "Object Field";
        } else if (parent instanceof BallerinaVariableDefinitionStatement) {
            return "Variable";
        } else if (parent instanceof BallerinaNamedPattern) {
            return "Variable";
        } else if (parent instanceof BallerinaRestParameter) {
            return "Parameter";
        } else if (parent instanceof BallerinaEndpointParameter) {
            return "Endpoint Parameter";
        } else if (parent instanceof BallerinaObjectInitializer) {
            return "Object Initializer";
        }
        return "";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        return element.getText();
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return element.getText();
    }
}
