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

package org.ballerinalang.plugins.idea;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.antlr.jetbrains.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.jetbrains.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.jetbrains.adaptor.lexer.RuleIElementType;
import org.antlr.jetbrains.adaptor.lexer.TokenIElementType;
import org.antlr.jetbrains.adaptor.parser.ANTLRParserAdaptor;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.ballerinalang.plugins.idea.grammar.BallerinaLexer;
import org.ballerinalang.plugins.idea.grammar.BallerinaParser;
import org.ballerinalang.plugins.idea.psi.AliasNode;
import org.ballerinalang.plugins.idea.psi.AnnotationAttachmentNode;
import org.ballerinalang.plugins.idea.psi.AnnotationAttributeValueNode;
import org.ballerinalang.plugins.idea.psi.AnonStructTypeNameNode;
import org.ballerinalang.plugins.idea.psi.AnyIdentifierNameNode;
import org.ballerinalang.plugins.idea.psi.AssignmentStatementNode;
import org.ballerinalang.plugins.idea.psi.AttachmentPointNode;
import org.ballerinalang.plugins.idea.psi.BallerinaFile;
import org.ballerinalang.plugins.idea.psi.BuiltInReferenceTypeNameNode;
import org.ballerinalang.plugins.idea.psi.CallableUnitBodyNode;
import org.ballerinalang.plugins.idea.psi.CatchClauseNode;
import org.ballerinalang.plugins.idea.psi.CodeBlockBodyNode;
import org.ballerinalang.plugins.idea.psi.CodeBlockParameterNode;
import org.ballerinalang.plugins.idea.psi.CompilationUnitNode;
import org.ballerinalang.plugins.idea.psi.ConnectorInitNode;
import org.ballerinalang.plugins.idea.psi.ConstantDefinitionNode;
import org.ballerinalang.plugins.idea.psi.DefinitionNode;
import org.ballerinalang.plugins.idea.psi.DeprecatedAttachmentNode;
import org.ballerinalang.plugins.idea.psi.DeprecatedTextNode;
import org.ballerinalang.plugins.idea.psi.DocumentationAttachmentNode;
import org.ballerinalang.plugins.idea.psi.DocumentationTemplateAttributeDescriptionNode;
import org.ballerinalang.plugins.idea.psi.DocumentationTemplateInlineCodeNode;
import org.ballerinalang.plugins.idea.psi.DoubleBackTickDeprecatedInlineCodeNode;
import org.ballerinalang.plugins.idea.psi.DoubleBackTickInlineCodeNode;
import org.ballerinalang.plugins.idea.psi.EndpointDeclarationNode;
import org.ballerinalang.plugins.idea.psi.EnumDefinitionNode;
import org.ballerinalang.plugins.idea.psi.EnumFieldNode;
import org.ballerinalang.plugins.idea.psi.ExpressionListNode;
import org.ballerinalang.plugins.idea.psi.ExpressionNode;
import org.ballerinalang.plugins.idea.psi.FieldDefinitionNode;
import org.ballerinalang.plugins.idea.psi.FieldNode;
import org.ballerinalang.plugins.idea.psi.ForEachStatementNode;
import org.ballerinalang.plugins.idea.psi.ForkJoinStatementNode;
import org.ballerinalang.plugins.idea.psi.FormalParameterListNode;
import org.ballerinalang.plugins.idea.psi.FullyQualifiedPackageNameNode;
import org.ballerinalang.plugins.idea.psi.FunctionDefinitionNode;
import org.ballerinalang.plugins.idea.psi.FunctionInvocationNode;
import org.ballerinalang.plugins.idea.psi.FunctionReferenceNode;
import org.ballerinalang.plugins.idea.psi.FunctionTypeNameNode;
import org.ballerinalang.plugins.idea.psi.GlobalVariableDefinitionNode;
import org.ballerinalang.plugins.idea.psi.IfElseStatementNode;
import org.ballerinalang.plugins.idea.psi.ImportDeclarationNode;
import org.ballerinalang.plugins.idea.psi.IntegerLiteralNode;
import org.ballerinalang.plugins.idea.psi.InvocationNode;
import org.ballerinalang.plugins.idea.psi.JoinClauseNode;
import org.ballerinalang.plugins.idea.psi.JoinConditionNode;
import org.ballerinalang.plugins.idea.psi.LambdaFunctionNode;
import org.ballerinalang.plugins.idea.psi.MatchPatternClauseNode;
import org.ballerinalang.plugins.idea.psi.NameReferenceNode;
import org.ballerinalang.plugins.idea.psi.NamespaceDeclarationNode;
import org.ballerinalang.plugins.idea.psi.NonEmptyCodeBlockBodyNode;
import org.ballerinalang.plugins.idea.psi.PackageDeclarationNode;
import org.ballerinalang.plugins.idea.psi.PackageNameNode;
import org.ballerinalang.plugins.idea.psi.ParameterListNode;
import org.ballerinalang.plugins.idea.psi.ParameterNode;
import org.ballerinalang.plugins.idea.psi.ParameterTypeNameList;
import org.ballerinalang.plugins.idea.psi.RecordKeyNode;
import org.ballerinalang.plugins.idea.psi.RecordKeyValueNode;
import org.ballerinalang.plugins.idea.psi.RecordLiteralNode;
import org.ballerinalang.plugins.idea.psi.RecordValueNode;
import org.ballerinalang.plugins.idea.psi.ReferenceTypeNameNode;
import org.ballerinalang.plugins.idea.psi.ResourceDefinitionNode;
import org.ballerinalang.plugins.idea.psi.ReturnParameterNode;
import org.ballerinalang.plugins.idea.psi.ReturnStatementNode;
import org.ballerinalang.plugins.idea.psi.ServiceBodyNode;
import org.ballerinalang.plugins.idea.psi.ServiceDefinitionNode;
import org.ballerinalang.plugins.idea.psi.SimpleLiteralNode;
import org.ballerinalang.plugins.idea.psi.SingleBackTickDeprecatedInlineCodeNode;
import org.ballerinalang.plugins.idea.psi.SingleBackTickDocInlineCodeNode;
import org.ballerinalang.plugins.idea.psi.StatementNode;
import org.ballerinalang.plugins.idea.psi.StringTemplateContentNode;
import org.ballerinalang.plugins.idea.psi.StringTemplateLiteralNode;
import org.ballerinalang.plugins.idea.psi.StructBodyNode;
import org.ballerinalang.plugins.idea.psi.StructDefinitionNode;
import org.ballerinalang.plugins.idea.psi.ThrowStatementNode;
import org.ballerinalang.plugins.idea.psi.TimeoutClauseNode;
import org.ballerinalang.plugins.idea.psi.TransformerDefinitionNode;
import org.ballerinalang.plugins.idea.psi.TriggerWorkerNode;
import org.ballerinalang.plugins.idea.psi.TripleBackTickDeprecatedInlineCodeNode;
import org.ballerinalang.plugins.idea.psi.TripleBackTickInlineCodeNode;
import org.ballerinalang.plugins.idea.psi.TryCatchStatementNode;
import org.ballerinalang.plugins.idea.psi.TypeConversionNode;
import org.ballerinalang.plugins.idea.psi.TypeNameNode;
import org.ballerinalang.plugins.idea.psi.UserDefinedTypeName;
import org.ballerinalang.plugins.idea.psi.ValueTypeNameNode;
import org.ballerinalang.plugins.idea.psi.VariableDefinitionNode;
import org.ballerinalang.plugins.idea.psi.VariableReferenceListNode;
import org.ballerinalang.plugins.idea.psi.VariableReferenceNode;
import org.ballerinalang.plugins.idea.psi.WorkerBodyNode;
import org.ballerinalang.plugins.idea.psi.WorkerDeclarationNode;
import org.ballerinalang.plugins.idea.psi.WorkerReplyNode;
import org.ballerinalang.plugins.idea.psi.XmlAttribNode;
import org.ballerinalang.plugins.idea.psi.XmlContentNode;
import org.jetbrains.annotations.NotNull;

import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ABORT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ALL;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ANNOTATION;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.AS;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ASYNC;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.AWAIT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.BIND;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.BREAK;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.BY;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.BooleanLiteral;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.CATCH;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.COLON;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.COMMA;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.CONST;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.CURRENT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.DAY;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.DELETE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.DEPRECATED;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.DOCUMENTATION;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.DOUBLE_COLON;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ELSE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ENDPOINT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ENUM;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.EQUAL_GT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ERRCHAR;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.EVENTS;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.EVERY;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.EXPIRED;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.FAIL;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.FINALLY;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.FIRST;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.FOLLOWED;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.FOR;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.FOREACH;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.FORK;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.FROM;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.FULL;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.FUNCTION;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.FloatingPointLiteral;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.GROUP;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.HAVING;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.HOUR;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.IF;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.IMPORT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.IN;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.INNER;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.INSERT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.INTO;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.Identifier;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.JOIN;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.LARROW;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.LAST;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.LEFT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.LENGTHOF;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.LINE_COMMENT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.LOCK;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.MATCH;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.MINUTE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.MONTH;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.NATIVE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.NEW;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.NEXT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.NullLiteral;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.OBJECT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ON;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ONABORT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ONCOMMIT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ONRETRY;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.ORDER;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.OUTER;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.OUTPUT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.PACKAGE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.PARAMETER;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.PRIVATE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.PUBLIC;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.QUERY;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.QUESTION_MARK;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.QuotedStringLiteral;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.RARROW;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.REDUCE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.RESOURCE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.RETRIES;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.RETURN;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.RETURNS;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.RIGHT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.SAFE_ASSIGNMENT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.SECOND;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.SELECT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.SEMICOLON;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.SERVICE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.SET;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.SNAPSHOT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.SOME;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.STRUCT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.THROW;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TILDE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TIMEOUT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TRANSACTION;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TRANSFORMER;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TRY;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPEOF;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_ANY;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_BLOB;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_BOOL;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_DESC;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_FLOAT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_FUTURE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_INT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_JSON;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_MAP;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_STREAM;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_STRING;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_TABLE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_TYPE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.TYPE_XML;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.UNIDIRECTIONAL;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.UNTAINT;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.UPDATE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.VAR;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.VERSION;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.WHENEVER;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.WHERE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.WHILE;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.WINDOW;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.WITH;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.WITHIN;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.WORKER;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.WS;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.XMLNS;
import static org.ballerinalang.plugins.idea.grammar.BallerinaLexer.YEAR;

/**
 * Responsible for identifying PSI tree nodes which corresponds to parser rules.
 */
public class BallerinaParserDefinition implements ParserDefinition {

    private static final IFileElementType FILE = new IFileElementType(BallerinaLanguage.INSTANCE);

    static {
        PSIElementTypeFactory.defineLanguageIElementTypes(BallerinaLanguage.INSTANCE,
                BallerinaParser.tokenNames, BallerinaParser.ruleNames);
    }

    public static final TokenSet IDENTIFIER = PSIElementTypeFactory.createTokenSet(BallerinaLanguage.INSTANCE,
            Identifier);

    public static final TokenSet COMMENTS = PSIElementTypeFactory.createTokenSet(BallerinaLanguage.INSTANCE,
            LINE_COMMENT);

    public static final TokenSet WHITESPACE = PSIElementTypeFactory.createTokenSet(BallerinaLanguage.INSTANCE, WS);

    public static final TokenSet STRING_LITERALS = PSIElementTypeFactory.createTokenSet(BallerinaLanguage.INSTANCE,
            QuotedStringLiteral);

    public static final TokenSet NUMBER = PSIElementTypeFactory.createTokenSet(BallerinaLanguage.INSTANCE,
            FloatingPointLiteral);

    public static final TokenSet KEYWORDS = PSIElementTypeFactory.createTokenSet(BallerinaLanguage.INSTANCE,
            ABORT, ALL, ANNOTATION, AS, ASYNC, AWAIT, BIND, BREAK, BY, CATCH, CONST, CURRENT, DAY, DELETE,
            DEPRECATED, DOCUMENTATION, ELSE, ENDPOINT, ENUM, EVENTS, EVERY, EXPIRED, FAIL, FINALLY, FIRST, FOLLOWED,
            FOREACH, FOR, FORK, FROM, FULL, FUNCTION, GROUP, HAVING, HOUR, IF, IMPORT, IN, INNER, INSERT, INTO, JOIN,
            LAST, LEFT, LENGTHOF, LOCK, MATCH, MINUTE, MONTH, NATIVE, NEW, NEXT, OBJECT, ON, ONABORT, ONCOMMIT,
            ONRETRY, ORDER, OUTER, OUTPUT, PACKAGE, PARAMETER, PRIVATE, PUBLIC, QUERY, REDUCE, RESOURCE, RETRIES,
            RETURN, RETURNS, RIGHT, SECOND, SELECT, SERVICE, SET, SNAPSHOT, SOME, STRUCT, THROW, TIMEOUT,
            TRANSACTION, TRANSFORMER, TRY, VAR, WHILE, WORKER, XMLNS, TYPEOF, TYPE_ANY, TYPE_BLOB, TYPE_BOOL,
            TYPE_DESC, TYPE_FLOAT, TYPE_FUTURE, TYPE_INT, TYPE_JSON, TYPE_MAP, TYPE_STREAM, TYPE_STRING, TYPE_TABLE,
            TYPE_TYPE, TYPE_XML, UNIDIRECTIONAL, UNTAINT, UPDATE, VERSION, WHENEVER, WHERE, WITHIN, WINDOW, WITH,
            YEAR, BooleanLiteral, NullLiteral);

    public static final TokenSet BRACES_AND_OPERATORS = PSIElementTypeFactory.createTokenSet(BallerinaLanguage.INSTANCE,
            COLON, COMMA, DOUBLE_COLON, EQUAL_GT, LARROW, QUESTION_MARK, RARROW, SAFE_ASSIGNMENT, SEMICOLON, TILDE);

    public static final TokenSet BAD_CHARACTER = PSIElementTypeFactory.createTokenSet(BallerinaLanguage.INSTANCE,
            ERRCHAR);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        BallerinaLexer lexer = new BallerinaLexer(null);
        return new ANTLRLexerAdaptor(BallerinaLanguage.INSTANCE, lexer);
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        final BallerinaParser parser = new BallerinaParser(null);
        return new ANTLRParserAdaptor(BallerinaLanguage.INSTANCE, parser) {
            @Override
            protected ParseTree parse(Parser parser, IElementType root) {
                // Start rule depends on root passed in; sometimes we want to create an ID node etc...
                // Eg: if (root instanceof IFileElementType) { }
                return ((BallerinaParser) parser).compilationUnit();
            }
        };
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITESPACE;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return STRING_LITERALS;
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new BallerinaFile(viewProvider);
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        IElementType elementType = node.getElementType();
        if (elementType instanceof TokenIElementType) {
            return new ANTLRPsiNode(node);
        }
        if (!(elementType instanceof RuleIElementType)) {
            return new ANTLRPsiNode(node);
        }

        RuleIElementType ruleElType = (RuleIElementType) elementType;
        switch (ruleElType.getRuleIndex()) {
            case BallerinaParser.RULE_functionDefinition:
                return new FunctionDefinitionNode(node);
            case BallerinaParser.RULE_callableUnitBody:
                return new CallableUnitBodyNode(node);
            case BallerinaParser.RULE_nameReference:
                return new NameReferenceNode(node);
            case BallerinaParser.RULE_variableReference:
                return new VariableReferenceNode(node);
            case BallerinaParser.RULE_variableDefinitionStatement:
                return new VariableDefinitionNode(node);
            case BallerinaParser.RULE_parameter:
                return new ParameterNode(node);
            case BallerinaParser.RULE_resourceDefinition:
                return new ResourceDefinitionNode(node);
            case BallerinaParser.RULE_packageName:
                return new PackageNameNode(node);
            case BallerinaParser.RULE_fullyQualifiedPackageName:
                return new FullyQualifiedPackageNameNode(node);
            case BallerinaParser.RULE_expressionList:
                return new ExpressionListNode(node);
            case BallerinaParser.RULE_expression:
                return new ExpressionNode(node);
            case BallerinaParser.RULE_functionInvocation:
                return new FunctionInvocationNode(node);
            case BallerinaParser.RULE_compilationUnit:
                return new CompilationUnitNode(node);
            case BallerinaParser.RULE_packageDeclaration:
                return new PackageDeclarationNode(node);
            case BallerinaParser.RULE_annotationAttachment:
                return new AnnotationAttachmentNode(node);
            case BallerinaParser.RULE_serviceBody:
                return new ServiceBodyNode(node);
            case BallerinaParser.RULE_importDeclaration:
                return new ImportDeclarationNode(node);
            case BallerinaParser.RULE_statement:
                return new StatementNode(node);
            case BallerinaParser.RULE_typeName:
                return new TypeNameNode(node);
            case BallerinaParser.RULE_constantDefinition:
                return new ConstantDefinitionNode(node);
            case BallerinaParser.RULE_structDefinition:
                return new StructDefinitionNode(node);
            case BallerinaParser.RULE_simpleLiteral:
                return new SimpleLiteralNode(node);
            case BallerinaParser.RULE_packageAlias:
                return new AliasNode(node);
            case BallerinaParser.RULE_parameterList:
                return new ParameterListNode(node);
            case BallerinaParser.RULE_fieldDefinition:
                return new FieldDefinitionNode(node);
            case BallerinaParser.RULE_parameterTypeNameList:
                return new ParameterTypeNameList(node);
            case BallerinaParser.RULE_parameterTypeName:
                return new ConnectorInitNode(node);
            case BallerinaParser.RULE_serviceDefinition:
                return new ServiceDefinitionNode(node);
            case BallerinaParser.RULE_valueTypeName:
                return new ValueTypeNameNode(node);
            case BallerinaParser.RULE_annotationDefinition:
                return new AnnotationAttributeValueNode(node);
            case BallerinaParser.RULE_structBody:
                return new StructBodyNode(node);
            case BallerinaParser.RULE_returnParameter:
                return new ReturnParameterNode(node);
            case BallerinaParser.RULE_attachmentPoint:
                return new AttachmentPointNode(node);
            case BallerinaParser.RULE_definition:
                return new DefinitionNode(node);
            case BallerinaParser.RULE_ifElseStatement:
                return new IfElseStatementNode(node);
            case BallerinaParser.RULE_assignmentStatement:
                return new AssignmentStatementNode(node);
            case BallerinaParser.RULE_variableReferenceList:
                return new VariableReferenceListNode(node);
            case BallerinaParser.RULE_recordLiteral:
                return new RecordLiteralNode(node);
            case BallerinaParser.RULE_globalVariableDefinition:
                return new GlobalVariableDefinitionNode(node);
            case BallerinaParser.RULE_recordKeyValue:
                return new RecordKeyValueNode(node);
            case BallerinaParser.RULE_forkJoinStatement:
                return new ForkJoinStatementNode(node);
            case BallerinaParser.RULE_returnStatement:
                return new ReturnStatementNode(node);
            case BallerinaParser.RULE_throwStatement:
                return new ThrowStatementNode(node);
            case BallerinaParser.RULE_transformerDefinition:
                return new TransformerDefinitionNode(node);
            case BallerinaParser.RULE_workerReply:
                return new WorkerReplyNode(node);
            case BallerinaParser.RULE_triggerWorker:
                return new TriggerWorkerNode(node);
            case BallerinaParser.RULE_workerDeclaration:
                return new WorkerDeclarationNode(node);
            case BallerinaParser.RULE_workerBody:
                return new WorkerBodyNode(node);
            case BallerinaParser.RULE_joinConditions:
                return new JoinConditionNode(node);
            case BallerinaParser.RULE_field:
                return new FieldNode(node);
            case BallerinaParser.RULE_recordKey:
                return new RecordKeyNode(node);
            case BallerinaParser.RULE_recordValue:
                return new RecordValueNode(node);
            case BallerinaParser.RULE_functionReference:
                return new FunctionReferenceNode(node);
            case BallerinaParser.RULE_codeBlockBody:
                return new CodeBlockBodyNode(node);
            case BallerinaParser.RULE_nonEmptyCodeBlockBody:
                return new NonEmptyCodeBlockBodyNode(node);
            case BallerinaParser.RULE_tryCatchStatement:
                return new TryCatchStatementNode(node);
            case BallerinaParser.RULE_catchClause:
                return new CatchClauseNode(node);
            case BallerinaParser.RULE_codeBlockParameter:
                return new CodeBlockParameterNode(node);
            case BallerinaParser.RULE_foreachStatement:
                return new ForEachStatementNode(node);
            case BallerinaParser.RULE_joinClause:
                return new JoinClauseNode(node);
            case BallerinaParser.RULE_timeoutClause:
                return new TimeoutClauseNode(node);
            case BallerinaParser.RULE_xmlAttrib:
                return new XmlAttribNode(node);
            case BallerinaParser.RULE_namespaceDeclaration:
                return new NamespaceDeclarationNode(node);
            case BallerinaParser.RULE_stringTemplateLiteral:
                return new StringTemplateLiteralNode(node);
            case BallerinaParser.RULE_stringTemplateContent:
                return new StringTemplateContentNode(node);
            case BallerinaParser.RULE_typeConversion:
                return new TypeConversionNode(node);
            case BallerinaParser.RULE_xmlContent:
                return new XmlContentNode(node);
            case BallerinaParser.RULE_invocation:
                return new InvocationNode(node);
            case BallerinaParser.RULE_enumDefinition:
                return new EnumDefinitionNode(node);
            case BallerinaParser.RULE_enumField:
                return new EnumFieldNode(node);
            case BallerinaParser.RULE_builtInReferenceTypeName:
                return new BuiltInReferenceTypeNameNode(node);
            case BallerinaParser.RULE_referenceTypeName:
                return new ReferenceTypeNameNode(node);
            case BallerinaParser.RULE_functionTypeName:
                return new FunctionTypeNameNode(node);
            case BallerinaParser.RULE_lambdaFunction:
                return new LambdaFunctionNode(node);
            case BallerinaParser.RULE_endpointDeclaration:
                return new EndpointDeclarationNode(node);
            case BallerinaParser.RULE_anonStructTypeName:
                return new AnonStructTypeNameNode(node);
            case BallerinaParser.RULE_userDefineTypeName:
                return new UserDefinedTypeName(node);
            case BallerinaParser.RULE_anyIdentifierName:
                return new AnyIdentifierNameNode(node);
            case BallerinaParser.RULE_documentationAttachment:
                return new DocumentationAttachmentNode(node);
            case BallerinaParser.RULE_documentationTemplateInlineCode:
                return new DocumentationTemplateInlineCodeNode(node);
            case BallerinaParser.RULE_singleBackTickDocInlineCode:
                return new SingleBackTickDocInlineCodeNode(node);
            case BallerinaParser.RULE_doubleBackTickDocInlineCode:
                return new DoubleBackTickInlineCodeNode(node);
            case BallerinaParser.RULE_tripleBackTickDocInlineCode:
                return new TripleBackTickInlineCodeNode(node);
            case BallerinaParser.RULE_deprecatedAttachment:
                return new DeprecatedAttachmentNode(node);
            case BallerinaParser.RULE_deprecatedText:
                return new DeprecatedTextNode(node);
            case BallerinaParser.RULE_singleBackTickDeprecatedInlineCode:
                return new SingleBackTickDeprecatedInlineCodeNode(node);
            case BallerinaParser.RULE_doubleBackTickDeprecatedInlineCode:
                return new DoubleBackTickDeprecatedInlineCodeNode(node);
            case BallerinaParser.RULE_tripleBackTickDeprecatedInlineCode:
                return new TripleBackTickDeprecatedInlineCodeNode(node);
            case BallerinaParser.RULE_documentationTemplateAttributeDescription:
                return new DocumentationTemplateAttributeDescriptionNode(node);
            case BallerinaParser.RULE_formalParameterList:
                return new FormalParameterListNode(node);
            case BallerinaParser.RULE_integerLiteral:
                return new IntegerLiteralNode(node);
            case BallerinaParser.RULE_matchPatternClause:
                return new MatchPatternClauseNode(node);
            default:
                return new ANTLRPsiNode(node);
        }
    }
}
