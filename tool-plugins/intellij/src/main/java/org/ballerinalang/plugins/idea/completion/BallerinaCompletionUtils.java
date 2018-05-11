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

package org.ballerinalang.plugins.idea.completion;

import com.intellij.codeInsight.completion.AddSpaceInsertHandler;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.ballerinalang.plugins.idea.BallerinaIcons;
import org.ballerinalang.plugins.idea.completion.inserthandlers.BracesInsertHandler;
import org.ballerinalang.plugins.idea.completion.inserthandlers.ParenthesisWithSemicolonInsertHandler;
import org.ballerinalang.plugins.idea.completion.inserthandlers.SemiolonInsertHandler;
import org.ballerinalang.plugins.idea.psi.BallerinaAnyIdentifierName;
import org.ballerinalang.plugins.idea.psi.BallerinaCallableUnitSignature;
import org.ballerinalang.plugins.idea.psi.BallerinaFormalParameterList;
import org.ballerinalang.plugins.idea.psi.BallerinaFunctionDefinition;
import org.ballerinalang.plugins.idea.psi.BallerinaObjectCallableUnitSignature;
import org.ballerinalang.plugins.idea.psi.BallerinaObjectFunctionDefinition;
import org.ballerinalang.plugins.idea.psi.BallerinaObjectInitializer;
import org.ballerinalang.plugins.idea.psi.BallerinaObjectInitializerParameterList;
import org.ballerinalang.plugins.idea.psi.BallerinaObjectParameterList;
import org.ballerinalang.plugins.idea.psi.BallerinaReturnParameter;
import org.ballerinalang.plugins.idea.psi.BallerinaReturnType;
import org.ballerinalang.plugins.idea.psi.BallerinaTypeDefinition;
import org.ballerinalang.plugins.idea.psi.BallerinaUserDefineTypeName;
import org.ballerinalang.plugins.idea.psi.impl.BallerinaPsiImplUtil;
import org.ballerinalang.plugins.idea.psi.impl.BallerinaTopLevelDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Util methods and constants related to code completion.
 */
public class BallerinaCompletionUtils {

    private static final int VARIABLE_PRIORITY = 20;
    private static final int FUNCTION_PRIORITY = VARIABLE_PRIORITY - 1;
    private static final int GLOBAL_VARIABLE_PRIORITY = FUNCTION_PRIORITY - 1;
    private static final int VALUE_TYPES_PRIORITY = VARIABLE_PRIORITY - 2;
    private static final int REFERENCE_TYPES_PRIORITY = VARIABLE_PRIORITY - 2;
    private static final int TYPE_PRIORITY = VARIABLE_PRIORITY - 2;
    private static final int PACKAGE_PRIORITY = VALUE_TYPES_PRIORITY - 1;
    private static final int UNIMPORTED_PACKAGE_PRIORITY = PACKAGE_PRIORITY - 1;
    private static final int CONNECTOR_PRIORITY = VALUE_TYPES_PRIORITY - 1;
    private static final int ACTION_PRIORITY = VALUE_TYPES_PRIORITY - 1;
    private static final int ANNOTATION_PRIORITY = VALUE_TYPES_PRIORITY - 1;
    private static final int ENUM_PRIORITY = VALUE_TYPES_PRIORITY - 1;
    public static final int KEYWORDS_PRIORITY = VALUE_TYPES_PRIORITY - 2;


    public static final Key<String> HAS_A_RETURN_VALUE = Key.create("HAS_A_RETURN_VALUE");
    public static final Key<String> REQUIRE_PARAMETERS = Key.create("REQUIRE_PARAMETERS");

    public static final Key<String> PUBLIC_DEFINITIONS_ONLY = Key.create("PUBLIC_DEFINITIONS_ONLY");

    // File level keywords
    private static final LookupElementBuilder ANNOTATION;
    private static final LookupElementBuilder ENDPOINT;
    private static final LookupElementBuilder FUNCTION;
    private static final LookupElementBuilder IMPORT;
    private static final LookupElementBuilder PUBLIC;
    private static final LookupElementBuilder SERVICE;
    private static final LookupElementBuilder TYPE;
    private static final LookupElementBuilder XMLNS;

    // Simple types
    private static final LookupElementBuilder BLOB;
    private static final LookupElementBuilder BOOLEAN;
    private static final LookupElementBuilder FLOAT;
    private static final LookupElementBuilder INT;
    private static final LookupElementBuilder STRING;

    // Reference types
    private static final LookupElementBuilder ANY;
    private static final LookupElementBuilder FUTURE;
    private static final LookupElementBuilder JSON;
    private static final LookupElementBuilder MAP;
    private static final LookupElementBuilder STREAM;
    private static final LookupElementBuilder TABLE;
    private static final LookupElementBuilder TYPE_DESC;
    private static final LookupElementBuilder XML;

    // Other types
    private static final LookupElementBuilder VAR;

    // Expression keywords
    private static final LookupElementBuilder AWAIT;
    private static final LookupElementBuilder CHECK;
    private static final LookupElementBuilder LENGTH_OF;
    private static final LookupElementBuilder START;
    private static final LookupElementBuilder UNTAINT;

    private static final LookupElementBuilder BIND;

    private static final LookupElementBuilder OBJECT;

    private static final LookupElementBuilder NEW;

    // Other keywords

    private static final LookupElementBuilder RETURN;
    private static final LookupElementBuilder IF;
    private static final LookupElementBuilder ELSE;
    private static final LookupElementBuilder MATCH;
    private static final LookupElementBuilder FORK;
    private static final LookupElementBuilder JOIN;
    private static final LookupElementBuilder ALL;
    private static final LookupElementBuilder SOME;
    private static final LookupElementBuilder TIMEOUT;
    private static final LookupElementBuilder WORKER;
    private static final LookupElementBuilder TRANSACTION;
    private static final LookupElementBuilder RETRY;
    private static final LookupElementBuilder ABORT;
    private static final LookupElementBuilder TRY;
    private static final LookupElementBuilder CATCH;
    private static final LookupElementBuilder FINALLY;
    private static final LookupElementBuilder WHILE;
    private static final LookupElementBuilder NEXT;
    private static final LookupElementBuilder BREAK;
    private static final LookupElementBuilder THROW;
    private static final LookupElementBuilder FOREACH;
    private static final LookupElementBuilder IN;
    private static final LookupElementBuilder LOCK;

    private static final LookupElementBuilder TRUE;
    private static final LookupElementBuilder FALSE;


    static {
        ANNOTATION = createKeywordLookupElement("annotation");
        ENDPOINT = createKeywordLookupElement("endpoint");
        FUNCTION = createKeywordLookupElement("function");
        IMPORT = createKeywordLookupElement("import");
        PUBLIC = createKeywordLookupElement("public");
        SERVICE = createKeywordLookupElement("service");
        TYPE = createKeywordLookupElement("type");
        XMLNS = createKeywordLookupElement("xmlns");

        BLOB = createLookupElement("blob", AddSpaceInsertHandler.INSTANCE);
        BOOLEAN = createLookupElement("boolean", AddSpaceInsertHandler.INSTANCE);
        FLOAT = createLookupElement("float", AddSpaceInsertHandler.INSTANCE);
        INT = createLookupElement("int", AddSpaceInsertHandler.INSTANCE);
        STRING = createLookupElement("string", AddSpaceInsertHandler.INSTANCE);

        ANY = createLookupElement("any", AddSpaceInsertHandler.INSTANCE);
        FUTURE = createLookupElement("future", AddSpaceInsertHandler.INSTANCE);
        JSON = createLookupElement("json", AddSpaceInsertHandler.INSTANCE);
        MAP = createLookupElement("map", AddSpaceInsertHandler.INSTANCE);
        STREAM = createLookupElement("stream", null);
        TABLE = createLookupElement("table", null);
        TYPE_DESC = createLookupElement("typedesc", AddSpaceInsertHandler.INSTANCE);
        XML = createLookupElement("xml", AddSpaceInsertHandler.INSTANCE);

        VAR = createLookupElement("var", AddSpaceInsertHandler.INSTANCE);

        AWAIT = createKeywordLookupElement("await");
        CHECK = createKeywordLookupElement("check");
        LENGTH_OF = createKeywordLookupElement("lengthof");
        START = createKeywordLookupElement("start");
        UNTAINT = createKeywordLookupElement("untaint");


        RETURN = createKeywordLookupElement("return");

        IF = createKeywordLookupElement("if");
        ELSE = createKeywordLookupElement("else");

        MATCH = createKeywordLookupElement("match");

        FORK = createKeywordLookupElement("fork");
        JOIN = createKeywordLookupElement("join");
        ALL = createKeywordLookupElement("all");
        SOME = createKeywordLookupElement("some");
        TIMEOUT = createKeywordLookupElement("timeout");

        WORKER = createKeywordLookupElement("worker");

        TRANSACTION = createKeywordLookupElement("transaction");

        RETRY = createKeywordLookupElement("retry");
        ABORT = createKeywordLookupElement("abort");
        TRY = createKeywordLookupElement("try");
        CATCH = createKeywordLookupElement("catch");
        FINALLY = createKeywordLookupElement("finally");
        WHILE = createKeywordLookupElement("while");
        NEXT = createKeywordLookupElement("next", ";");
        BREAK = createKeywordLookupElement("break", ";");
        THROW = createKeywordLookupElement("throw");
        FOREACH = createKeywordLookupElement("foreach");
        IN = createKeywordLookupElement("in");
        LOCK = createKeywordLookupElement("lock");

        BIND = createKeywordLookupElement("bind");

        OBJECT = createKeywordLookupElement("object");

        NEW = createKeywordLookupElement("new");

        TRUE = createKeywordLookupElement("true", null);
        FALSE = createKeywordLookupElement("false", null);
    }

    private BallerinaCompletionUtils() {

    }

    /**
     * Creates a lookup element.
     *
     * @param name          name of the lookup
     * @param insertHandler insert handler of the lookup
     * @return {@link LookupElementBuilder} which will be used to create the lookup element.
     */
    @NotNull
    private static LookupElementBuilder createLookupElement(@NotNull String name,
                                                            @Nullable InsertHandler<LookupElement> insertHandler) {
        return LookupElementBuilder.create(name).withBoldness(true).withInsertHandler(insertHandler);
    }

    /**
     * Creates a keyword lookup element.
     *
     * @param name name of the lookup
     * @return {@link LookupElementBuilder} which will be used to create the lookup element.
     */
    @NotNull
    public static LookupElementBuilder createKeywordLookupElement(@NotNull String name) {
        return createKeywordLookupElement(name, " ");
    }

    @NotNull
    private static LookupElementBuilder createKeywordLookupElement(@NotNull String name,
                                                                   @Nullable String traileringString) {

        return createLookupElement(name, createTemplateBasedInsertHandler("ballerina_lang_" + name,
                traileringString));
    }

    @NotNull
    private static InsertHandler<LookupElement> createTemplateBasedInsertHandler(@NotNull String templateId,
                                                                                 @Nullable String traileringString) {
        return (context, item) -> {
            Template template = TemplateSettings.getInstance().getTemplateById(templateId);
            Editor editor = context.getEditor();
            if (template != null) {
                editor.getDocument().deleteString(context.getStartOffset(), context.getTailOffset());
                TemplateManager.getInstance(context.getProject()).startTemplate(editor, template);
            } else {
                int currentOffset = editor.getCaretModel().getOffset();
                CharSequence documentText = editor.getDocument().getImmutableCharSequence();
                if (documentText.length() <= currentOffset || documentText.charAt(currentOffset) != ' ') {
                    if (traileringString != null) {
                        EditorModificationUtil.insertStringAtCaret(editor, traileringString);
                    }
                } else {
                    EditorModificationUtil.moveCaretRelatively(editor, 1);
                }
            }
        };
    }

    /**
     * Adds any type as a lookup.
     *
     * @param resultSet result list which is used to add lookups
     */
    static void addVarAsLookup(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(VAR, VALUE_TYPES_PRIORITY));
    }

    static void addXmlnsAsLookup(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(XMLNS, VALUE_TYPES_PRIORITY));
    }

    static void addImportAsLookup(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(IMPORT, KEYWORDS_PRIORITY));
    }

    static void addPublicAsLookup(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(PUBLIC, KEYWORDS_PRIORITY));
    }

    static void addReturnAsLookup(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(RETURN, KEYWORDS_PRIORITY));
    }

    static void addLockAsLookup(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(LOCK, KEYWORDS_PRIORITY));
    }

    static void addBindAsLookup(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(BIND, KEYWORDS_PRIORITY));
    }

    static void addEndpointAsLookup(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(ENDPOINT, KEYWORDS_PRIORITY));
    }

    static void addEObjectAsLookup(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(OBJECT, KEYWORDS_PRIORITY));
    }

    static void addNewAsLookup(@NotNull CompletionResultSet resultSet,
                               @NotNull BallerinaTypeDefinition typeDefinition) {
        LookupElementBuilder builder = NEW.withInsertHandler(SemiolonInsertHandler.INSTANCE);
        BallerinaObjectInitializer initializer = BallerinaPsiImplUtil.getInitializer(typeDefinition);
        if (initializer != null) {
            BallerinaObjectInitializerParameterList parameterList =
                    initializer.getObjectInitializerParameterList();
            if (parameterList != null) {
                BallerinaObjectParameterList objectParameterList = parameterList.getObjectParameterList();
                if (objectParameterList != null) {
                    // Todo - parenthesis with semicolon
                    builder = NEW.withInsertHandler(ParenthesisWithSemicolonInsertHandler.INSTANCE);
                }
            }
        }
        resultSet.addElement(PrioritizedLookupElement.withPriority(builder, KEYWORDS_PRIORITY));
    }

    /**
     * Adds value types as lookups.
     *
     * @param resultSet result list which is used to add lookups
     */
    static void addValueTypesAsLookups(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(BOOLEAN, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(BLOB, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(FLOAT, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(INT, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(STRING, VALUE_TYPES_PRIORITY));
    }

    /**
     * Adds reference types as lookups.
     *
     * @param resultSet result list which is used to add lookups
     */
    static void addReferenceTypesAsLookups(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANY, REFERENCE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(FUTURE, REFERENCE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(JSON, REFERENCE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(MAP, REFERENCE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(STREAM, REFERENCE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(TABLE, REFERENCE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(TYPE_DESC, REFERENCE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(XML, REFERENCE_TYPES_PRIORITY));
    }

    static void addTopLevelDefinitionsAsLookups(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION, REFERENCE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(ENDPOINT, REFERENCE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(FUNCTION, REFERENCE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(SERVICE, REFERENCE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(TYPE, REFERENCE_TYPES_PRIORITY));
    }

    static void addExpressionKeywordsAsLookups(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(AWAIT, KEYWORDS_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(CHECK, KEYWORDS_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(LENGTH_OF, KEYWORDS_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(START, KEYWORDS_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(UNTAINT, KEYWORDS_PRIORITY));
    }

    private static LookupElement createKeywordAsLookup(@NotNull LookupElement lookupElement) {
        return PrioritizedLookupElement.withPriority(lookupElement, KEYWORDS_PRIORITY);
    }

    @NotNull
    static void addCommonKeywords(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(createKeywordAsLookup(MATCH));
        resultSet.addElement(createKeywordAsLookup(FOREACH));
        resultSet.addElement(createKeywordAsLookup(WHILE));
        resultSet.addElement(createKeywordAsLookup(WORKER));

        resultSet.addElement(createKeywordAsLookup(TRANSACTION));

        resultSet.addElement(createKeywordAsLookup(NEXT));
        resultSet.addElement(createKeywordAsLookup(BREAK));

        resultSet.addElement(createKeywordAsLookup(IF));
        resultSet.addElement(createKeywordAsLookup(ELSE));

        resultSet.addElement(createKeywordAsLookup(FORK));
        resultSet.addElement(createKeywordAsLookup(JOIN));
        resultSet.addElement(createKeywordAsLookup(TIMEOUT));
        resultSet.addElement(createKeywordAsLookup(ABORT));
        resultSet.addElement(createKeywordAsLookup(RETRY));

        resultSet.addElement(createKeywordAsLookup(TRY));
        resultSet.addElement(createKeywordAsLookup(CATCH));
        resultSet.addElement(createKeywordAsLookup(FINALLY));

        resultSet.addElement(createKeywordAsLookup(THROW));

        resultSet.addElement(createKeywordAsLookup(IN));
    }

    public static LookupElement createPackageLookup(@NotNull PsiElement identifier,
                                                    @Nullable InsertHandler<LookupElement> insertHandler) {
        LookupElementBuilder builder = LookupElementBuilder.create(identifier.getText()).withTypeText("Package")
                .withIcon(BallerinaIcons.PACKAGE).withInsertHandler(insertHandler);
        return PrioritizedLookupElement.withPriority(builder, PACKAGE_PRIORITY);
    }

    // Todo - Update icon getting logic to get the icon from a util method.
    @NotNull
    public static LookupElement createFunctionLookupElement(@NotNull BallerinaTopLevelDefinition definition,
                                                            @Nullable InsertHandler<LookupElement> insertHandler) {
        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(definition.getIdentifier()
                .getText(), definition).withIcon(definition.getIcon(Iconable.ICON_FLAG_VISIBILITY)).bold()
                .withInsertHandler(insertHandler);
        if (definition instanceof BallerinaFunctionDefinition) {
            BallerinaCallableUnitSignature callableUnitSignature =
                    ((BallerinaFunctionDefinition) definition).getCallableUnitSignature();
            if (callableUnitSignature != null) {
                // Add parameters.
                BallerinaReturnParameter returnParameter = callableUnitSignature.getReturnParameter();
                if (returnParameter != null) {
                    BallerinaReturnType returnType = returnParameter.getReturnType();
                    if (returnType != null) {
                        builder = builder.withTypeText(BallerinaPsiImplUtil.formatBallerinaFunctionReturnType
                                (returnType));
                    }
                } else {
                    builder = builder.withTypeText("nil");
                    definition.putUserData(HAS_A_RETURN_VALUE, "nil");
                }
                // Add return type.
                BallerinaFormalParameterList formalParameterList = callableUnitSignature.getFormalParameterList();

                builder = builder.withTailText(BallerinaPsiImplUtil.formatBallerinaFunctionParameters
                        (formalParameterList));
                if (formalParameterList != null) {
                    definition.putUserData(REQUIRE_PARAMETERS, "YES");
                }
            }
        }
        return PrioritizedLookupElement.withPriority(builder, FUNCTION_PRIORITY);
    }

    @NotNull
    public static LookupElement createFunctionLookupElement(@NotNull BallerinaObjectFunctionDefinition definition,
                                                            @NotNull PsiElement owner,
                                                            @Nullable InsertHandler<LookupElement> insertHandler) {

        BallerinaObjectCallableUnitSignature objectCallableUnitSignature = definition.getObjectCallableUnitSignature();
        BallerinaAnyIdentifierName anyIdentifierName = objectCallableUnitSignature.getAnyIdentifierName();
        PsiElement identifier = anyIdentifierName.getIdentifier();
        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(identifier.getText(), identifier)
                .withIcon(BallerinaIcons.FUNCTION).bold().withInsertHandler(insertHandler);
        // Add parameters.
        BallerinaReturnParameter returnParameter = objectCallableUnitSignature.getReturnParameter();
        if (returnParameter != null) {
            BallerinaReturnType returnType = returnParameter.getReturnType();
            if (returnType != null) {
                builder = builder.withTypeText(BallerinaPsiImplUtil.formatBallerinaFunctionReturnType(returnType));
            }
        } else {
            builder = builder.withTypeText("nil");
            identifier.putUserData(HAS_A_RETURN_VALUE, "nil");
        }
        // Add return type.
        BallerinaFormalParameterList formalParameterList = objectCallableUnitSignature.getFormalParameterList();
        builder = builder.withTailText(BallerinaPsiImplUtil.formatBallerinaFunctionParameters(formalParameterList)
                + " -> " + owner.getText());
        if (formalParameterList != null) {
            identifier.putUserData(REQUIRE_PARAMETERS, "YES");
        }
        return PrioritizedLookupElement.withPriority(builder, FUNCTION_PRIORITY);
    }

    @NotNull
    public static LookupElement createGlobalVariableLookupElement(@NotNull BallerinaTopLevelDefinition definition,
                                                                  @Nullable String type) {
        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(definition.getIdentifier().getText(),
                definition).withIcon(definition.getIcon(Iconable.ICON_FLAG_VISIBILITY));
        if (type == null || type.isEmpty()) {
            builder = builder.withTypeText("Variable");
        } else {
            builder = builder.withTypeText(type);
        }

        return PrioritizedLookupElement.withPriority(builder, GLOBAL_VARIABLE_PRIORITY);
    }

    @NotNull
    public static LookupElement createTypeLookupElement(@NotNull BallerinaTopLevelDefinition definition) {
        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(definition.getIdentifier()
                .getText(), definition)
                .withTypeText("Type").withIcon(definition.getIcon(Iconable.ICON_FLAG_VISIBILITY)).bold();
        // Todo - Add tail text
        //                .withTailText(BallerinaDocumentationProvider.getParametersAndReturnTypes(element
        // .getParent()));
        return PrioritizedLookupElement.withPriority(builder, TYPE_PRIORITY);
    }

    @NotNull
    public static LookupElement createGlobalEndpointLookupElement(@NotNull BallerinaTopLevelDefinition definition) {
        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(definition.getIdentifier().getText(),
                definition).withTypeText("Endpoint").withIcon(definition.getIcon(Iconable.ICON_FLAG_VISIBILITY));
        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
    }

    @NotNull
    public static LookupElement createVariableLookupElement(@NotNull PsiElement element, @Nullable String type) {
        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(element.getText(), element)
                .withIcon(BallerinaIcons.VARIABLE);
        if (type == null || type.isEmpty()) {
            builder = builder.withTypeText("Variable");
        } else {
            builder = builder.withTypeText(type);
        }
        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
    }

    @NotNull
    public static LookupElement createParameterLookupElement(@NotNull PsiElement element, @Nullable String type,
                                                             @Nullable String defaultValue) {
        // Todo - Add support to render default value
        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(element.getText(), element)
                .withIcon(BallerinaIcons.PARAMETER);
        if (type == null || type.isEmpty()) {
            builder = builder.withTypeText("Parameter");
        } else {
            builder = builder.withTypeText(type);
        }

        if (defaultValue != null && !defaultValue.isEmpty()) {
            builder = builder.withTailText(" ( = " + defaultValue + " )");
        }

        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
    }

    @NotNull
    public static LookupElement createWorkerLookupElement(@NotNull PsiElement workerName) {
        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(workerName.getText(), workerName)
                .withTypeText("Worker").withIcon(BallerinaIcons.WORKER);
        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
    }

    @NotNull
    public static LookupElement createEndpointLookupElement(@NotNull PsiElement element) {
        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(element.getText(), element)
                .withTypeText("Endpoint").withIcon(BallerinaIcons.ENDPOINT);
        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
    }

    @NotNull
    public static LookupElement createFieldLookupElement(@NotNull PsiElement fieldName,
                                                         @NotNull PsiElement ownerName,
                                                         @NotNull String type, @Nullable String defaultValue,
                                                         boolean isPublic) {
        LookupElementBuilder lookupElementBuilder = LookupElementBuilder.createWithSmartPointer(fieldName.getText(),
                fieldName).withTypeText(type).bold();

        if (defaultValue == null || defaultValue.isEmpty()) {
            lookupElementBuilder = lookupElementBuilder.withTailText(" -> " + ownerName.getText(), true);
        } else {
            String tailText = "(= " + defaultValue + ") -> " + ownerName.getText();
            lookupElementBuilder = lookupElementBuilder.withTailText(tailText, true);
        }

        if (isPublic) {
            lookupElementBuilder = lookupElementBuilder.withIcon(BallerinaIcons.PUBLIC_FIELD);
        } else {
            lookupElementBuilder = lookupElementBuilder.withIcon(BallerinaIcons.PRIVATE_FIELD);
        }
        return PrioritizedLookupElement.withPriority(lookupElementBuilder, VARIABLE_PRIORITY);
    }

    @NotNull
    public static LookupElement createAnnotationLookupElement(@NotNull PsiElement identifier) {
        BallerinaUserDefineTypeName userDefineTypeName = PsiTreeUtil.getNextSiblingOfType(identifier,
                BallerinaUserDefineTypeName.class);
        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(identifier.getText(), identifier)
                .withTypeText("Annotation").withIcon(BallerinaIcons.ANNOTATION)
                .withInsertHandler(userDefineTypeName != null ? BracesInsertHandler.INSTANCE_WITH_AUTO_POPUP
                        : AddSpaceInsertHandler.INSTANCE);
        return PrioritizedLookupElement.withPriority(builder, ANNOTATION_PRIORITY);
    }
}
