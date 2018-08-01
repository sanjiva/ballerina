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
 */

package io.ballerina.plugins.idea.codeinspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import io.ballerina.plugins.idea.psi.BallerinaAssignmentStatement;
import io.ballerina.plugins.idea.psi.BallerinaField;
import io.ballerina.plugins.idea.psi.BallerinaFile;
import io.ballerina.plugins.idea.psi.BallerinaForeverStatement;
import io.ballerina.plugins.idea.psi.BallerinaIdentifier;
import io.ballerina.plugins.idea.psi.BallerinaInvocation;
import io.ballerina.plugins.idea.psi.BallerinaObjectParameter;
import io.ballerina.plugins.idea.psi.BallerinaOrgName;
import io.ballerina.plugins.idea.psi.BallerinaPackageName;
import io.ballerina.plugins.idea.psi.BallerinaPackageReference;
import io.ballerina.plugins.idea.psi.BallerinaRecordKey;
import io.ballerina.plugins.idea.psi.BallerinaResourceDefinition;
import io.ballerina.plugins.idea.psi.BallerinaSimpleVariableReference;
import io.ballerina.plugins.idea.psi.BallerinaTableQueryExpression;
import io.ballerina.plugins.idea.psi.BallerinaTypes;
import io.ballerina.plugins.idea.psi.BallerinaVariableDefinitionStatement;
import io.ballerina.plugins.idea.psi.BallerinaVariableReferenceList;
import io.ballerina.plugins.idea.psi.BallerinaXmlAttrib;
import io.ballerina.plugins.idea.psi.reference.BallerinaPackageNameReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Checks for unresolved references in the code.
 */
public class BallerinaUnresolvedReferenceInspection extends LocalInspectionTool {

    @Override
    @Nullable
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager,
                                         boolean isOnTheFly) {
        // does not work in tests since CodeInsightTestCase copies file into temporary location
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return new ProblemDescriptor[0];
        }
        if (!(file instanceof BallerinaFile)) {
            return new ProblemDescriptor[0];
        }

        List<ProblemDescriptor> problemDescriptors = new LinkedList<>();
        LocalQuickFix[] availableFixes = new LocalQuickFix[0];
        Collection<BallerinaIdentifier> nameReferenceNodes = PsiTreeUtil.findChildrenOfType(file,
                BallerinaIdentifier.class);
        problemDescriptors.addAll(getUnresolvedNameReferenceDescriptors(manager, isOnTheFly, availableFixes,
                nameReferenceNodes));
        return problemDescriptors.toArray(new ProblemDescriptor[0]);
    }

    private List<ProblemDescriptor> getUnresolvedNameReferenceDescriptors(@NotNull InspectionManager manager,
                                                                          boolean isOnTheFly,
                                                                          @NotNull LocalQuickFix[] availableFixes,
                                                                          @NotNull Collection<BallerinaIdentifier>
                                                                                  identifiers) {
        List<ProblemDescriptor> problemDescriptors = new LinkedList<>();
        for (BallerinaIdentifier identifier : identifiers) {
            ProgressManager.checkCanceled();
            if (identifier == null || "_".equals(identifier.getText())) {
                continue;
            }
            BallerinaOrgName orgName = PsiTreeUtil.getParentOfType(identifier, BallerinaOrgName.class);
            if (orgName != null) {
                continue;
            }
            BallerinaPackageName packageNameNode = PsiTreeUtil.getParentOfType(identifier, BallerinaPackageName.class);
            if (packageNameNode != null) {
                continue;
            }
            if (identifier.getParent() instanceof BallerinaResourceDefinition) {
                continue;
            }
            BallerinaVariableReferenceList variableReferenceList = PsiTreeUtil.getParentOfType(identifier,
                    BallerinaVariableReferenceList.class);
            if (variableReferenceList != null) {
                continue;
            }

            // Skip record key highlighting in json.
            BallerinaRecordKey recordKey = PsiTreeUtil.getParentOfType(identifier, BallerinaRecordKey.class);
            BallerinaAssignmentStatement assignmentStatement = PsiTreeUtil.getParentOfType(identifier,
                    BallerinaAssignmentStatement.class);
            BallerinaVariableDefinitionStatement definitionStatement = PsiTreeUtil.getParentOfType(identifier,
                    BallerinaVariableDefinitionStatement.class);
            if (recordKey != null && (assignmentStatement == null || definitionStatement == null)) {
                continue;
            }

            // Skip var assignment.
            BallerinaSimpleVariableReference variableReference = PsiTreeUtil.getParentOfType(identifier,
                    BallerinaSimpleVariableReference.class);
            if (variableReference != null && variableReference.getParent() instanceof BallerinaAssignmentStatement
                    && assignmentStatement != null && assignmentStatement.getVar() != null) {
                continue;
            }

            // Skip object creation.
            if (identifier.getText().equals("new")) {
                continue;
            }

            // Skip self reference.
            if (identifier.getText().equals("self")) {
                continue;
            }

            // Skip unresolved fields.
            BallerinaField ballerinaField = PsiTreeUtil.getParentOfType(identifier, BallerinaField.class);
            if (ballerinaField != null) {
                continue;
            }

            // Skip unresolved invocations.
            BallerinaInvocation invocation = PsiTreeUtil.getParentOfType(identifier, BallerinaInvocation.class);
            if (invocation != null) {
                continue;
            }

            // Skip parameters in object initializer.
            BallerinaObjectParameter objectParameter = PsiTreeUtil.getParentOfType(identifier,
                    BallerinaObjectParameter.class);
            if (objectParameter != null) {
                continue;
            }

            // Skip xml attributes.
            BallerinaXmlAttrib xmlAttrib = PsiTreeUtil.getParentOfType(identifier, BallerinaXmlAttrib.class);
            if (xmlAttrib != null) {
                continue;
            }
            // Skip namespaces.
            PsiElement prevSibling = PsiTreeUtil.prevVisibleLeaf(identifier);
            if (prevSibling instanceof LeafPsiElement) {
                if (((LeafPsiElement) prevSibling).getElementType() == BallerinaTypes.COLON) {
                    PsiElement parent = prevSibling.getParent();
                    if (parent instanceof BallerinaPackageReference) {
                        PsiReference reference = parent.getReference();
                        if (!(reference instanceof BallerinaPackageNameReference)) {
                            continue;
                        }
                    }
                }
            }

            // Todo - Remove after completion support is added.
            BallerinaForeverStatement ballerinaForeverStatement = PsiTreeUtil.getParentOfType(identifier,
                    BallerinaForeverStatement.class);
            if (ballerinaForeverStatement != null) {
                continue;
            }

            BallerinaTableQueryExpression tableQueryExpression = PsiTreeUtil.getParentOfType(identifier,
                    BallerinaTableQueryExpression.class);
            if (tableQueryExpression != null) {
                continue;
            }

            PsiReference reference = identifier.getReference();
            if (reference != null && reference.resolve() == null) {
                problemDescriptors.add(createProblemDescriptor(manager, identifier, isOnTheFly, availableFixes));
                continue;
            }

            PsiElement parent = identifier.getParent();
            if (parent instanceof BallerinaPackageReference) {
                reference = parent.getReference();
                if (reference == null || reference.resolve() == null) {
                    availableFixes = new LocalQuickFix[]{new BallerinaImportPackageQuickFix(identifier)};
                    problemDescriptors.add(createProblemDescriptor(manager, identifier, isOnTheFly, availableFixes));
                }
            }
        }
        return problemDescriptors;
    }

    @NotNull
    private ProblemDescriptor createProblemDescriptor(@NotNull InspectionManager manager, @NotNull PsiElement element,
                                                      boolean isOnTheFly, @NotNull LocalQuickFix[] availableFixes) {
        String description = "Unresolved reference <code>#ref</code> #loc";
        return createProblemDescriptor(manager, description, isOnTheFly, element, availableFixes);
    }

    @NotNull
    private ProblemDescriptor createProblemDescriptor(@NotNull InspectionManager manager, @NotNull String description,
                                                      boolean isOnTheFly, @NotNull PsiElement element,
                                                      @NotNull LocalQuickFix[] availableFixes) {
        return manager.createProblemDescriptor(element, description, isOnTheFly, availableFixes,
                ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
    }
}
