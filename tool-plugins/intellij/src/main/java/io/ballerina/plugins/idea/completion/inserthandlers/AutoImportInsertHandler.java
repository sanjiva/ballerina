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

package io.ballerina.plugins.idea.completion.inserthandlers;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import io.ballerina.plugins.idea.completion.BallerinaCompletionUtils;
import io.ballerina.plugins.idea.psi.BallerinaFile;
import io.ballerina.plugins.idea.psi.impl.BallerinaElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides auto import support.
 */
public class AutoImportInsertHandler implements InsertHandler<LookupElement> {

    public static final InsertHandler<LookupElement> INSTANCE = new AutoImportInsertHandler(false);
    public static final InsertHandler<LookupElement> INSTANCE_WITH_AUTO_POPUP =
            new AutoImportInsertHandler(true);
    public static final InsertHandler<LookupElement> INSTANCE_WITH_ALIAS =
            new AutoImportInsertHandler(false, true);
    public static final InsertHandler<LookupElement> INSTANCE_WITH_ALIAS_WITH_POPUP =
            new AutoImportInsertHandler(true, true);

    private final boolean suggestAlias;
    private final boolean myTriggerAutoPopup;
    private String alias = null;

    private AutoImportInsertHandler(boolean triggerAutoPopup) {
        this(triggerAutoPopup, false);
    }

    private AutoImportInsertHandler(boolean triggerAutoPopup, boolean suggestAlias) {
        this.suggestAlias = suggestAlias;
        myTriggerAutoPopup = triggerAutoPopup;
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        ApplicationManager.getApplication().invokeLater(() -> {
            CommandProcessor.getInstance().runUndoTransparentAction(() -> {
                PsiElement element = item.getPsiElement();
                if (element == null) {
                    return;
                }
                String organizationName = element.getUserData(BallerinaCompletionUtils.ORGANIZATION_NAME);
                if (!(element instanceof PsiDirectory)) {
                    return;
                }
                String packageName = ((PsiDirectory) element).getName();
                PsiFile file = context.getFile();
                if (!(file instanceof BallerinaFile)) {
                    return;
                }
                Editor editor = context.getEditor();
                Project project = editor.getProject();
                if (suggestAlias) {
                    alias = Messages.showInputDialog(project, "Package '" + ((PsiDirectory) element).getName() +
                                    "' already imported. Please enter an alias:", "Enter Alias",
                            Messages.getInformationIcon());
                    if (alias == null || alias.isEmpty()) {
                        Messages.showErrorDialog("Alias cannot be null or empty.", "Error");
                        return;
                    }
                }
                // Import the package.
                autoImport(context, organizationName, packageName, alias);
                if (project == null) {
                    return;
                }
                if (!isCompletionCharAtSpace(editor)) {
                    if (suggestAlias) {
                        // InsertHandler inserts the old package name. So we need to change it to the new alias.
                        PsiElement currentPackageName = file.findElementAt(context.getStartOffset());
                        if (currentPackageName != null) {
                            if (alias == null || alias.isEmpty()) {
                                return;
                            }
                            ApplicationManager.getApplication().runWriteAction(() -> {
                                // Add a new identifier node.
                                PsiElement identifier = BallerinaElementFactory.createIdentifierFromText(project,
                                        alias);
                                currentPackageName.getParent().addBefore(identifier, currentPackageName);
                                // Delete the current identifier node.
                                currentPackageName.delete();
                            });
                        }
                    }
                    if (myTriggerAutoPopup) {
                        ApplicationManager.getApplication().runWriteAction(() -> {
                            PsiDocumentManager.getInstance(project)
                                    .doPostponedOperationsAndUnblockDocument(editor.getDocument());
                            EditorModificationUtil.insertStringAtCaret(editor, ":");
                            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
                        });
                    }
                } else {
                    editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() + 1);
                }
                // Invoke the popup.
                if (myTriggerAutoPopup) {
                    // We need to invoke the popup with a delay. Otherwise it might not show.
                    ApplicationManager.getApplication().invokeLater(
                            () -> AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null)
                    );
                }
            });
        });
    }

    private void autoImport(@NotNull InsertionContext context, @Nullable String organization,
                            @NotNull String packageName, @Nullable String alias) {
        PsiFile file = context.getFile();
        final String importPath = organization == null ? packageName : organization + "/" + packageName;
        ApplicationManager.getApplication().runWriteAction(() -> {
            BallerinaFile.addImport(((BallerinaFile) file), importPath, alias);
        });
    }

    private static boolean isCompletionCharAtSpace(Editor editor) {
        final int startOffset = editor.getCaretModel().getOffset();
        final Document document = editor.getDocument();
        return document.getTextLength() > startOffset && document.getCharsSequence().charAt(startOffset) == ':';
    }
}
