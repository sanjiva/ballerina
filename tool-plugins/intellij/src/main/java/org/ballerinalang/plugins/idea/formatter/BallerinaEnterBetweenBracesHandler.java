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

package org.ballerinalang.plugins.idea.formatter;

import com.intellij.codeInsight.editorActions.enter.EnterBetweenBracesHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.ballerinalang.plugins.idea.BallerinaLanguage;
import org.ballerinalang.plugins.idea.psi.BallerinaTypeDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the enter key press in braces.
 */
public class BallerinaEnterBetweenBracesHandler extends EnterBetweenBracesHandler {

    @Override
    public Result postProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
        if (!file.getLanguage().is(BallerinaLanguage.INSTANCE)) {
            return Result.Continue;
        }
        // We need to save the file before checking. Otherwise issues can occur when we press enter in a string.
        Project project = file.getProject();
        PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

        // Get the offset of the caret.
        int caretOffset = editor.getCaretModel().getOffset();
        // Get the element at the offset.
        PsiElement element = file.findElementAt(caretOffset);
        // If the element is null, return.
        if (element == null) {
            return Result.Continue;
        }
        // Check whether the semicolon is needed.
        if (needToInsertSemicolon(element)) {
            BallerinaTypeDefinition typeDefinition = PsiTreeUtil.getParentOfType(element,
                    BallerinaTypeDefinition.class);
            if (typeDefinition == null) {
                return Result.Continue;
            }
            // Get the end offset of the type definition.
            int endOffset = typeDefinition.getTextRange().getEndOffset();
            // Calculate the caret shift.
            int caretShift = endOffset - caretOffset;
            // Move the caret to the right.
            EditorModificationUtil.moveCaretRelatively(editor, caretShift);
            // Insert the semicolon and move the caret back to the original position.
            EditorModificationUtil.insertStringAtCaret(editor, ";", false, -(caretShift + 1));
            // Commit the document.
            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
        }
        return Result.Continue;
    }

    private boolean needToInsertSemicolon(PsiElement element) {
        // Get the type definition.
        BallerinaTypeDefinition typeDefinition = PsiTreeUtil.getParentOfType(element, BallerinaTypeDefinition.class);
        if (typeDefinition == null) {
            return false;
        }
        // Check whether the type definition has a semicolon.
        return typeDefinition.getSemicolon() == null;
    }

    @Override
    protected boolean isBracePair(char c1, char c2) {
        return c1 == '{' && c2 == '}';
    }
}
