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

package org.wso2.ballerinalang.compiler.semantics.analyzer;

import org.ballerinalang.compiler.CompilerPhase;
import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.model.symbols.SymbolKind;
import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.util.diagnostic.DiagnosticCode;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolEnv;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolTable;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BInvokableSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BPackageSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BVarSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.SymTag;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.Symbols;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.TaintRecord;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.tree.BLangAction;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotAttribute;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotation;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotationAttachment;
import org.wso2.ballerinalang.compiler.tree.BLangCompilationUnit;
import org.wso2.ballerinalang.compiler.tree.BLangDeprecatedNode;
import org.wso2.ballerinalang.compiler.tree.BLangDocumentation;
import org.wso2.ballerinalang.compiler.tree.BLangEndpoint;
import org.wso2.ballerinalang.compiler.tree.BLangEnum;
import org.wso2.ballerinalang.compiler.tree.BLangFunction;
import org.wso2.ballerinalang.compiler.tree.BLangIdentifier;
import org.wso2.ballerinalang.compiler.tree.BLangImportPackage;
import org.wso2.ballerinalang.compiler.tree.BLangInvokableNode;
import org.wso2.ballerinalang.compiler.tree.BLangNode;
import org.wso2.ballerinalang.compiler.tree.BLangNodeVisitor;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.tree.BLangResource;
import org.wso2.ballerinalang.compiler.tree.BLangService;
import org.wso2.ballerinalang.compiler.tree.BLangTypeDefinition;
import org.wso2.ballerinalang.compiler.tree.BLangVariable;
import org.wso2.ballerinalang.compiler.tree.BLangWorker;
import org.wso2.ballerinalang.compiler.tree.BLangXMLNS;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangFunctionClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangGroupBy;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangHaving;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangJoinStreamingInput;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangLimit;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangOrderBy;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangPatternClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangPatternStreamingEdgeInput;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangPatternStreamingInput;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangSelectClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangSelectExpression;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangSetAssignment;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangStreamAction;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangStreamingInput;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangTableQuery;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangWhere;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangWindow;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangWithinClause;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangAccessExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangAnnotAttachmentAttribute;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangAnnotAttachmentAttributeValue;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangArrayLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangAwaitExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangBinaryExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangBracedOrTupleExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangCheckedExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangDocumentationAttribute;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangElvisExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangFieldBasedAccess;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangIndexBasedAccess;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangIntRangeExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangInvocation;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangLambdaFunction;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangMatchExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangNamedArgsExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRestArgsExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangSimpleVarRef;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangStatementExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangStringTemplateLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTableLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTableQueryExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTernaryExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTypeCastExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTypeConversionExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTypeInit;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTypedescExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangUnaryExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangVariableReference;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLAttribute;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLAttributeAccess;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLCommentLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLElementLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLProcInsLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLQName;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLQuotedString;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLSequenceLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLTextLiteral;
import org.wso2.ballerinalang.compiler.tree.statements.BLangAbort;
import org.wso2.ballerinalang.compiler.tree.statements.BLangAssignment;
import org.wso2.ballerinalang.compiler.tree.statements.BLangBind;
import org.wso2.ballerinalang.compiler.tree.statements.BLangBlockStmt;
import org.wso2.ballerinalang.compiler.tree.statements.BLangBreak;
import org.wso2.ballerinalang.compiler.tree.statements.BLangCatch;
import org.wso2.ballerinalang.compiler.tree.statements.BLangCompensate;
import org.wso2.ballerinalang.compiler.tree.statements.BLangCompoundAssignment;
import org.wso2.ballerinalang.compiler.tree.statements.BLangContinue;
import org.wso2.ballerinalang.compiler.tree.statements.BLangDone;
import org.wso2.ballerinalang.compiler.tree.statements.BLangExpressionStmt;
import org.wso2.ballerinalang.compiler.tree.statements.BLangForeach;
import org.wso2.ballerinalang.compiler.tree.statements.BLangForever;
import org.wso2.ballerinalang.compiler.tree.statements.BLangForkJoin;
import org.wso2.ballerinalang.compiler.tree.statements.BLangIf;
import org.wso2.ballerinalang.compiler.tree.statements.BLangLock;
import org.wso2.ballerinalang.compiler.tree.statements.BLangMatch;
import org.wso2.ballerinalang.compiler.tree.statements.BLangPostIncrement;
import org.wso2.ballerinalang.compiler.tree.statements.BLangRetry;
import org.wso2.ballerinalang.compiler.tree.statements.BLangReturn;
import org.wso2.ballerinalang.compiler.tree.statements.BLangScope;
import org.wso2.ballerinalang.compiler.tree.statements.BLangStatement;
import org.wso2.ballerinalang.compiler.tree.statements.BLangStreamingQueryStatement;
import org.wso2.ballerinalang.compiler.tree.statements.BLangThrow;
import org.wso2.ballerinalang.compiler.tree.statements.BLangTransaction;
import org.wso2.ballerinalang.compiler.tree.statements.BLangTryCatchFinally;
import org.wso2.ballerinalang.compiler.tree.statements.BLangTupleDestructure;
import org.wso2.ballerinalang.compiler.tree.statements.BLangVariableDef;
import org.wso2.ballerinalang.compiler.tree.statements.BLangWhile;
import org.wso2.ballerinalang.compiler.tree.statements.BLangWorkerReceive;
import org.wso2.ballerinalang.compiler.tree.statements.BLangWorkerSend;
import org.wso2.ballerinalang.compiler.tree.statements.BLangXMLNSStatement;
import org.wso2.ballerinalang.compiler.tree.types.BLangArrayType;
import org.wso2.ballerinalang.compiler.tree.types.BLangBuiltInRefTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangConstrainedType;
import org.wso2.ballerinalang.compiler.tree.types.BLangFunctionTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangObjectTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangRecordTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangTupleTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangUnionTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangUserDefinedType;
import org.wso2.ballerinalang.compiler.tree.types.BLangValueType;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.CompilerUtils;
import org.wso2.ballerinalang.compiler.util.Name;
import org.wso2.ballerinalang.compiler.util.Names;
import org.wso2.ballerinalang.compiler.util.TypeTags;
import org.wso2.ballerinalang.compiler.util.diagnotic.BLangDiagnosticLog;
import org.wso2.ballerinalang.compiler.util.diagnotic.DiagnosticPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.XMLConstants;

/**
 * Generate taint-table for each invokable node.
 * <p>
 * Taint-table will contain the tainted status of return values, depending on the tainted status of parameters.
 * <p>
 * Propagate tainted status of variables across the program.
 * <p>
 * Evaluate invocations and generate errors if:
 * (*) Tainted value has been passed to a sensitive parameter.
 * (*) Tainted value has been passed to a global variable.
 *
 * @since 0.965.0
 */
public class TaintAnalyzer extends BLangNodeVisitor {
    private static final CompilerContext.Key<TaintAnalyzer> TAINT_ANALYZER_KEY = new CompilerContext.Key<>();

    private SymbolEnv env;
    private SymbolEnv currPkgEnv;
    private PackageID mainPkgId;
    private Names names;
    private SymbolTable symTable;
    private BLangDiagnosticLog dlog;

    private boolean overridingAnalysis = true;
    private boolean entryPointAnalysis;
    private boolean stopAnalysis;
    private boolean blockedOnWorkerInteraction;
    private BlockedNode blockedNode;
    private Set<TaintRecord.TaintError> taintErrorSet = new LinkedHashSet<>();
    private List<BlockedNode> blockedNodeList = new ArrayList<>();
    private List<BlockedNode> blockedEntryPointNodeList = new ArrayList<>();
    private BInvokableSymbol ignoredInvokableSymbol = null;
    private Map<BLangIdentifier, TaintedStatus> workerInteractionTaintedStatusMap;
    private BLangIdentifier currWorkerIdentifier;
    private BLangIdentifier currForkIdentifier;

    private enum TaintedStatus {
        TAINTED, UNTAINTED, IGNORED
    }
    private TaintedStatus taintedStatus;
    private TaintedStatus returnTaintedStatus;

    private static final String ANNOTATION_TAINTED = "tainted";
    private static final String ANNOTATION_UNTAINTED = "untainted";
    private static final String ANNOTATION_SENSITIVE = "sensitive";

    private static final int ALL_UNTAINTED_TABLE_ENTRY_INDEX = -1;
    private static final int RETURN_TAINTED_STATUS_COLUMN_INDEX = 0;

    private enum AnalyzerPhase {
        INITIAL_ANALYSIS,
        BLOCKED_NODE_ANALYSIS,
        LOOP_ANALYSIS,
        LOOP_ANALYSIS_COMPLETE,
        LOOPS_RESOLVED_ANALYSIS
    }
    private AnalyzerPhase analyzerPhase;

    public static TaintAnalyzer getInstance(CompilerContext context) {
        TaintAnalyzer taintAnalyzer = context.get(TAINT_ANALYZER_KEY);
        if (taintAnalyzer == null) {
            taintAnalyzer = new TaintAnalyzer(context);
        }
        return taintAnalyzer;
    }

    public TaintAnalyzer(CompilerContext context) {
        context.put(TAINT_ANALYZER_KEY, this);
        this.names = Names.getInstance(context);
        this.dlog = BLangDiagnosticLog.getInstance(context);
        this.symTable = SymbolTable.getInstance(context);
    }

    public BLangPackage analyze(BLangPackage pkgNode) {
        blockedNode = null;
        taintErrorSet = new LinkedHashSet<>();
        blockedNodeList = new ArrayList<>();
        blockedEntryPointNodeList = new ArrayList<>();
        this.mainPkgId = pkgNode.packageID;
        pkgNode.accept(this);
        return pkgNode;
    }

    @Override
    public void visit(BLangPackage pkgNode) {
        analyzerPhase = AnalyzerPhase.INITIAL_ANALYSIS;
        if (pkgNode.completedPhases.contains(CompilerPhase.TAINT_ANALYZE)) {
            return;
        }
        SymbolEnv pkgEnv = symTable.pkgEnvMap.get(pkgNode.symbol);
        SymbolEnv prevPkgEnv = this.currPkgEnv;
        this.currPkgEnv = pkgEnv;
        this.env = pkgEnv;

        pkgNode.topLevelNodes.forEach(e -> {
            ((BLangNode) e).accept(this);
            this.blockedNode = null;
        });

        analyzerPhase = AnalyzerPhase.BLOCKED_NODE_ANALYSIS;
        resolveBlockedInvokable(blockedNodeList);
        resolveBlockedInvokable(blockedEntryPointNodeList);

        this.currPkgEnv = prevPkgEnv;
        pkgNode.completedPhases.add(CompilerPhase.TAINT_ANALYZE);
    }

    @Override
    public void visit(BLangCompilationUnit compUnit) {
        compUnit.topLevelNodes.forEach(e -> ((BLangNode) e).accept(this));
    }

    @Override
    public void visit(BLangTypeDefinition typeDefinition) {
        if (typeDefinition.typeNode.getKind() == NodeKind.OBJECT_TYPE
            || typeDefinition.typeNode.getKind() == NodeKind.RECORD_TYPE) {
            typeDefinition.typeNode.accept(this);
        }
    }

    @Override
    public void visit(BLangImportPackage importPkgNode) {
        BPackageSymbol pkgSymbol = importPkgNode.symbol;
        SymbolEnv pkgEnv = symTable.pkgEnvMap.get(pkgSymbol);
        if (pkgEnv == null) {
            return;
        }
        this.env = pkgEnv;
        pkgEnv.node.accept(this);
    }

    @Override
    public void visit(BLangXMLNS xmlnsNode) {
        xmlnsNode.namespaceURI.accept(this);
    }

    @Override
    public void visit(BLangFunction funcNode) {
        SymbolEnv funcEnv = SymbolEnv.createFunctionEnv(funcNode, funcNode.symbol.scope, env);
        if (CompilerUtils.isMainFunction(funcNode)) {
            visitEntryPoint(funcNode, funcEnv);
            // Following statements are used only when main method is called from a different function (test execution).
            if (funcNode.symbol.taintTable != null) {
                // Since main method has no return values, set the all untainted entry to empty, denoting that all
                // untainted case is not invalid for the an invocation.
                funcNode.symbol.taintTable.put(ALL_UNTAINTED_TABLE_ENTRY_INDEX,
                        new TaintRecord(new ArrayList<>(), null));
                // It is valid to have a case where first argument of main is tainted. Hence manually adding such
                // scenario.
                funcNode.symbol.taintTable.put(0, new TaintRecord(new ArrayList<>(), null));
            }
        } else {
            visitInvokable(funcNode, funcEnv);
        }
    }

    @Override
    public void visit(BLangService serviceNode) {
        BSymbol serviceSymbol = serviceNode.symbol;
        SymbolEnv serviceEnv = SymbolEnv.createPkgLevelSymbolEnv(serviceNode, serviceSymbol.scope, env);
        serviceNode.vars.forEach(var -> analyzeNode(var, serviceEnv));
        analyzeNode(serviceNode.initFunction, serviceEnv);
        serviceNode.resources.forEach(resource -> analyzeNode(resource, serviceEnv));
    }

    @Override
    public void visit(BLangResource resourceNode) {
        BSymbol resourceSymbol = resourceNode.symbol;
        SymbolEnv resourceEnv = SymbolEnv.createResourceActionSymbolEnv(resourceNode, resourceSymbol.scope, env);
        visitEntryPoint(resourceNode, resourceEnv);
    }

    @Override
    public void visit(BLangAction actionNode) {
        BSymbol actionSymbol = actionNode.symbol;
        SymbolEnv actionEnv = SymbolEnv.createResourceActionSymbolEnv(actionNode, actionSymbol.scope, env);
        visitInvokable(actionNode, actionEnv);
    }

    @Override
    public void visit(BLangObjectTypeNode objectNode) {
        BSymbol objectSymbol = objectNode.symbol;
        SymbolEnv objectEnv = SymbolEnv.createPkgLevelSymbolEnv(objectNode, objectSymbol.scope, env);
        objectNode.fields.forEach(field -> analyzeNode(field, objectEnv));
        analyzeNode(objectNode.initFunction, objectEnv);
        objectNode.functions.forEach(f -> analyzeNode(f, objectEnv));
    }

    @Override
    public void visit(BLangRecordTypeNode recordNode) {
        BSymbol objectSymbol = recordNode.symbol;
        SymbolEnv objectEnv = SymbolEnv.createPkgLevelSymbolEnv(recordNode, objectSymbol.scope, env);
        recordNode.fields.forEach(field -> analyzeNode(field, objectEnv));
        analyzeNode(recordNode.initFunction, objectEnv);
    }

    @Override
    public void visit(BLangEnum enumNode) {
        enumNode.symbol.tainted = false;
    }

    @Override
    public void visit(BLangEnum.BLangEnumerator enumeratorNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangVariable varNode) {
        if (varNode.expr != null) {
            SymbolEnv varInitEnv = SymbolEnv.createVarInitEnv(varNode, env, varNode.symbol);
            analyzeNode(varNode.expr, varInitEnv);
            setTaintedStatus(varNode, this.taintedStatus);
        }
    }

    @Override
    public void visit(BLangWorker workerNode) {
        currWorkerIdentifier = workerNode.name;
        SymbolEnv workerEnv = SymbolEnv.createWorkerEnv(workerNode, this.env);
        analyzeNode(workerNode.body, workerEnv);
    }

    @Override
    public void visit(BLangEndpoint endpoint) {
        /* ignore */
    }

    @Override
    public void visit(BLangIdentifier identifierNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangAnnotation annotationNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangAnnotAttribute annotationAttribute) {
        /* ignore */
    }

    @Override
    public void visit(BLangAnnotationAttachment annAttachmentNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangAnnotAttachmentAttributeValue annotAttributeValue) {
        /* ignore */
    }

    @Override
    public void visit(BLangAnnotAttachmentAttribute annotAttachmentAttribute) {
        /* ignore */
    }

    @Override
    public void visit(BLangDocumentationAttribute docAttribute) {
        /* ignore */
    }

    @Override
    public void visit(BLangDocumentation doc) {
        /* ignore */
    }

    @Override
    public void visit(BLangDeprecatedNode deprecatedNode) {
        /* ignore */
    }

    // Statements
    @Override
    public void visit(BLangBlockStmt blockNode) {
        SymbolEnv blockEnv = SymbolEnv.createBlockEnv(blockNode, env);
        for (BLangStatement stmt : blockNode.stmts) {
            if (stopAnalysis) {
                break;
            } else {
                analyzeNode(stmt, blockEnv);
            }
        }
    }

    @Override
    public void visit(BLangVariableDef varDefNode) {
        varDefNode.var.accept(this);
    }

    @Override
    public void visit(BLangAssignment assignNode) {
        assignNode.expr.accept(this);
        BLangExpression varRefExpr = assignNode.varRef;
        visitAssignment(varRefExpr, this.taintedStatus, assignNode.pos);
    }

    @Override
    public void visit(BLangPostIncrement postIncrement) {
        BLangExpression varRefExpr = postIncrement.varRef;
        varRefExpr.accept(this);
        visitAssignment(varRefExpr, this.taintedStatus, postIncrement.pos);
    }

    @Override
    public void visit(BLangCompoundAssignment compoundAssignment) {
        compoundAssignment.varRef.accept(this);
        TaintedStatus varRefTaintedStatus = this.taintedStatus;

        compoundAssignment.expr.accept(this);
        TaintedStatus exprTaintedStatus = this.taintedStatus;

        TaintedStatus combinedTaintedStatus = getCombinedTaintedStatus(varRefTaintedStatus, exprTaintedStatus);
        visitAssignment(compoundAssignment.varRef, combinedTaintedStatus, compoundAssignment.pos);
    }

    @Override
    public void visit(BLangCompensate node) {
        /* ignore */
    }

    @Override
    public void visit(BLangScope scopeNode) {
        scopeNode.scopeBody.accept(this);
    }

    private void visitAssignment(BLangExpression varRefExpr, TaintedStatus varTaintedStatus, DiagnosticPos pos) {
        if (varTaintedStatus != TaintedStatus.IGNORED) {
            // Generate error if a global variable has been assigned with a tainted value.
            if (varTaintedStatus == TaintedStatus.TAINTED && varRefExpr instanceof BLangVariableReference) {
                BLangVariableReference varRef = (BLangVariableReference) varRefExpr;
                if (varRef.symbol != null && varRef.symbol.owner != null
                        && (varRef.symbol.owner instanceof BPackageSymbol
                        || SymbolKind.SERVICE.equals(varRef.symbol.owner.kind))) {
                    addTaintError(pos, varRef.symbol.name.value,
                            DiagnosticCode.TAINTED_VALUE_PASSED_TO_GLOBAL_VARIABLE);
                    return;
                }
            }
            // TODO: Re-evaluating the full data-set (array) when a change occur.
            if (varRefExpr.getKind() == NodeKind.INDEX_BASED_ACCESS_EXPR) {
                overridingAnalysis = false;
                updatedVarRefTaintedState(varRefExpr, varTaintedStatus);
                overridingAnalysis = true;
            } else if (varRefExpr.getKind() == NodeKind.FIELD_BASED_ACCESS_EXPR) {
                BLangFieldBasedAccess fieldBasedAccessExpr = (BLangFieldBasedAccess) varRefExpr;
                // Propagate tainted status to fields, when field symbols are present (Example: struct).
                if (fieldBasedAccessExpr.symbol != null) {
                    setTaintedStatus(fieldBasedAccessExpr, varTaintedStatus);
                }
                overridingAnalysis = false;
                updatedVarRefTaintedState(fieldBasedAccessExpr, varTaintedStatus);
                overridingAnalysis = true;
            } else if (varRefExpr.getKind() == NodeKind.BRACED_TUPLE_EXPR) {
                BLangBracedOrTupleExpr bracedOrTupleExpr = (BLangBracedOrTupleExpr) varRefExpr;
                // Propagate tainted status to fields, when field symbols are present (Example: struct).
                bracedOrTupleExpr.expressions.forEach(expr -> visitAssignment(expr, varTaintedStatus,
                        bracedOrTupleExpr.pos));
            } else {
                setTaintedStatus((BLangVariableReference) varRefExpr, varTaintedStatus);
            }
        }
    }

    private void updatedVarRefTaintedState(BLangExpression varRef, TaintedStatus taintedState) {
        if (varRef.getKind() == NodeKind.SIMPLE_VARIABLE_REF) {
            setTaintedStatus((BLangVariableReference) varRef, taintedState);
        } else if (varRef.getKind() == NodeKind.INDEX_BASED_ACCESS_EXPR
                || varRef.getKind() == NodeKind.FIELD_BASED_ACCESS_EXPR) {
            BLangAccessExpression accessExpr = (BLangAccessExpression) varRef;
            updatedVarRefTaintedState(accessExpr.expr, taintedState);
        }
    }

    @Override
    public void visit(BLangBind bindNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangAbort abortNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangDone abortNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangRetry retryNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangContinue continueNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangBreak breakNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangReturn returnNode) {
        returnNode.expr.accept(this);
        if (taintedStatus == TaintedStatus.TAINTED) {
            this.returnTaintedStatus = TaintedStatus.TAINTED;
        }
        taintedStatus = this.returnTaintedStatus;
    }

    @Override
    public void visit(BLangThrow throwNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangXMLNSStatement xmlnsStmtNode) {
        xmlnsStmtNode.xmlnsDecl.accept(this);
    }

    @Override
    public void visit(BLangExpressionStmt exprStmtNode) {
        SymbolEnv stmtEnv = new SymbolEnv(exprStmtNode, this.env.scope);
        this.env.copyTo(stmtEnv);
        analyzeNode(exprStmtNode.expr, stmtEnv);
    }

    @Override
    public void visit(BLangIf ifNode) {
        ifNode.body.accept(this);
        overridingAnalysis = false;
        if (ifNode.elseStmt != null) {
            ifNode.elseStmt.accept(this);
        }
        overridingAnalysis = true;
    }

    @Override
    public void visit(BLangMatch matchStmt) {
        matchStmt.expr.accept(this);
        TaintedStatus observedTaintedStatus = this.taintedStatus;
        matchStmt.patternClauses.forEach(clause -> {
            setTaintedStatus(clause.variable.symbol, observedTaintedStatus);
            clause.body.accept(this);
        });
    }

    @Override
    public void visit(BLangMatch.BLangMatchStmtPatternClause patternClauseNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangForeach foreach) {
        SymbolEnv blockEnv = SymbolEnv.createBlockEnv(foreach.body, env);
        // Propagate the tainted status of collection to foreach variables.
        foreach.collection.accept(this);
        if (taintedStatus == TaintedStatus.TAINTED) {
            foreach.varRefs
                    .forEach(varRef -> setTaintedStatus((BLangVariableReference) varRef, this.taintedStatus));
        }
        analyzeNode(foreach.body, blockEnv);
    }

    @Override
    public void visit(BLangWhile whileNode) {
        SymbolEnv blockEnv = SymbolEnv.createBlockEnv(whileNode.body, env);
        analyzeNode(whileNode.body, blockEnv);
    }

    @Override
    public void visit(BLangLock lockNode) {
        lockNode.body.accept(this);
    }

    @Override
    public void visit(BLangTransaction transactionNode) {
        transactionNode.transactionBody.accept(this);
        overridingAnalysis = false;
        if (transactionNode.onRetryBody != null) {
            transactionNode.onRetryBody.accept(this);
        }
        overridingAnalysis = true;
    }

    @Override
    public void visit(BLangTryCatchFinally tryNode) {
        tryNode.tryBody.accept(this);
        overridingAnalysis = false;
        tryNode.catchBlocks.forEach(c -> c.accept(this));
        if (tryNode.finallyBody != null) {
            tryNode.finallyBody.accept(this);
        }
        overridingAnalysis = true;
    }

    @Override
    public void visit(BLangTupleDestructure stmt) {
        stmt.expr.accept(this);
        // Propagate tainted status of each variable separately (when multi returns are used).
        for (int varIndex = 0; varIndex < stmt.varRefs.size(); varIndex++) {
            BLangExpression varRefExpr = stmt.varRefs.get(varIndex);
            visitAssignment(varRefExpr, taintedStatus, stmt.pos);
        }
    }

    @Override
    public void visit(BLangCatch catchNode) {
        SymbolEnv catchBlockEnv = SymbolEnv.createBlockEnv(catchNode.body, env);
        analyzeNode(catchNode.body, catchBlockEnv);
    }

    @Override
    public void visit(BLangForkJoin forkJoin) {
        analyzeWorkers(forkJoin.workers);
        if (currForkIdentifier != null) {
            TaintedStatus taintedStatus = workerInteractionTaintedStatusMap.get(currForkIdentifier);
            if (taintedStatus != null) {
                setTaintedStatus(forkJoin.joinResultVar, taintedStatus);
            }
        }
        overridingAnalysis = false;
        if (forkJoin.joinedBody != null) {
            forkJoin.joinedBody.accept(this);
        }
        if (forkJoin.timeoutBody != null) {
            forkJoin.timeoutBody.accept(this);
        }
        overridingAnalysis = true;
    }

    @Override
    public void visit(BLangOrderBy orderBy) {
        /* ignore */
    }

    @Override
    public void visit(BLangLimit limit) {
        /* ignore */
    }

    @Override
    public void visit(BLangGroupBy groupBy) {
        /* ignore */
    }

    @Override
    public void visit(BLangHaving having) {
        /* ignore */
    }

    @Override
    public void visit(BLangSelectExpression selectExpression) {
        /* ignore */
    }

    @Override
    public void visit(BLangSelectClause selectClause) {
        /* ignore */
    }

    @Override
    public void visit(BLangWhere whereClause) {
        /* ignore */
    }

    @Override
    public void visit(BLangStreamingInput streamingInput) {
        /* ignore */
    }

    @Override
    public void visit(BLangJoinStreamingInput joinStreamingInput) {
        /* ignore */
    }

    @Override
    public void visit(BLangTableQuery tableQuery) {
        /* ignore */
    }

    @Override
    public void visit(BLangStreamAction streamAction) {
        /* ignore */
    }

    @Override
    public void visit(BLangFunctionClause functionClause) {
        /* ignore */
    }

    @Override
    public void visit(BLangSetAssignment setAssignmentClause) {
        /* ignore */
    }

    @Override
    public void visit(BLangPatternStreamingEdgeInput patternStreamingEdgeInput) {
        /* ignore */
    }

    @Override
    public void visit(BLangWindow windowClause) {
        /* ignore */
    }

    @Override
    public void visit(BLangPatternStreamingInput patternStreamingInput) {
        /* ignore */
    }

    @Override
    public void visit(BLangTableLiteral tableLiteral) {
        // TODO: Improve to include tainted status identification for table literals
        this.taintedStatus = TaintedStatus.UNTAINTED;
    }

    @Override
    public void visit(BLangWorkerSend workerSendNode) {
        if (workerSendNode.isForkJoinSend) {
            currForkIdentifier = workerSendNode.workerIdentifier;
        }
        workerSendNode.expr.accept(this);
        TaintedStatus taintedStatus = workerInteractionTaintedStatusMap.get(workerSendNode.workerIdentifier);
        if (taintedStatus == TaintedStatus.IGNORED) {
            workerInteractionTaintedStatusMap.put(workerSendNode.workerIdentifier, this.taintedStatus);
        } else if (this.taintedStatus == TaintedStatus.TAINTED) {
            workerInteractionTaintedStatusMap.put(workerSendNode.workerIdentifier, TaintedStatus.TAINTED);
        }
    }

    @Override
    public void visit(BLangWorkerReceive workerReceiveNode) {
        TaintedStatus taintedStatus = workerInteractionTaintedStatusMap.get(currWorkerIdentifier);
        if (taintedStatus == TaintedStatus.IGNORED) {
            blockedOnWorkerInteraction = true;
            stopAnalysis = true;
        } else {
            visitAssignment(workerReceiveNode.expr, taintedStatus, workerReceiveNode.pos);
        }
    }

    // Expressions

    @Override
    public void visit(BLangLiteral literalExpr) {
        this.taintedStatus = TaintedStatus.UNTAINTED;
    }

    @Override
    public void visit(BLangArrayLiteral arrayLiteral) {
        if (arrayLiteral.exprs.size() == 0) {
            // Empty arrays are untainted.
            this.taintedStatus = TaintedStatus.UNTAINTED;
        } else {
            for (BLangExpression expr : arrayLiteral.exprs) {
                expr.accept(this);
                if (this.taintedStatus == TaintedStatus.TAINTED) {
                    break;
                }
            }
        }
    }

    @Override
    public void visit(BLangRecordLiteral recordLiteral) {
        TaintedStatus isTainted = TaintedStatus.UNTAINTED;
        for (BLangRecordLiteral.BLangRecordKeyValue keyValuePair : recordLiteral.keyValuePairs) {
            keyValuePair.valueExpr.accept(this);
            // Update field symbols with tainted status of the individual field (Example: struct).
            if (keyValuePair.key.fieldSymbol != null) {
                if (overridingAnalysis || !keyValuePair.key.fieldSymbol.tainted) {
                    setTaintedStatus(keyValuePair.key.fieldSymbol, this.taintedStatus);
                }
            }
            // Used to update the variable this literal is getting assigned to.
            if (this.taintedStatus == TaintedStatus.TAINTED) {
                isTainted = TaintedStatus.TAINTED;
            }
        }
        this.taintedStatus = isTainted;
    }

    @Override
    public void visit(BLangSimpleVarRef varRefExpr) {
        if (varRefExpr.symbol == null) {
            Name varName = names.fromIdNode(varRefExpr.variableName);
            if (varName != Names.IGNORE) {
                if (varRefExpr.pkgSymbol.tag == SymTag.XMLNS) {
                    this.taintedStatus = varRefExpr.pkgSymbol.tainted ? TaintedStatus.TAINTED : TaintedStatus.UNTAINTED;
                    return;
                }
            }
            this.taintedStatus = TaintedStatus.UNTAINTED;
        } else {
            this.taintedStatus  = varRefExpr.symbol.tainted ? TaintedStatus.TAINTED : TaintedStatus.UNTAINTED;
        }
    }

    @Override
    public void visit(BLangFieldBasedAccess fieldAccessExpr) {
        BType varRefType = fieldAccessExpr.expr.type;
        switch (varRefType.tag) {
            case TypeTags.OBJECT:
                fieldAccessExpr.expr.accept(this);
                break;
            case TypeTags.RECORD:
                fieldAccessExpr.expr.accept(this);
                break;
            case TypeTags.MAP:
                fieldAccessExpr.expr.accept(this);
                break;
            case TypeTags.JSON:
                fieldAccessExpr.expr.accept(this);
                break;
            case TypeTags.ENUM:
                this.taintedStatus = TaintedStatus.UNTAINTED;
                break;
        }
    }

    @Override
    public void visit(BLangIndexBasedAccess indexAccessExpr) {
        indexAccessExpr.expr.accept(this);
    }

    @Override
    public void visit(BLangInvocation invocationExpr) {
        if (invocationExpr.functionPointerInvocation) {
            // Skip function pointers and assume returns of function pointer executions are untainted.
            // TODO: Resolving function pointers / lambda expressions and perform analysis.
            taintedStatus = TaintedStatus.UNTAINTED;
        } else if (invocationExpr.iterableOperationInvocation) {
            invocationExpr.expr.accept(this);
            TaintedStatus exprTaintedStatus = this.taintedStatus;
            if (invocationExpr.argExprs != null) {
                invocationExpr.argExprs.forEach(argExpr -> {
                    // If argument of iterable operation is a lambda expression, propagate the tainted status
                    // to function parameters and validate function body.
                    if (argExpr.getKind() == NodeKind.LAMBDA) {
                        analyzeIterableLambdaInvocationArgExpression(argExpr);
                    }
                });
            }
            this.taintedStatus = exprTaintedStatus;
        } else {
            BInvokableSymbol invokableSymbol = (BInvokableSymbol) invocationExpr.symbol;
            if (invokableSymbol.taintTable == null) {
                if (analyzerPhase == AnalyzerPhase.LOOP_ANALYSIS) {
                    // If a looping invocation is being analyzed, skip analysis of invokable that does not have taint
                    // table already attached. This will prevent the analyzer to go in to a loop unnecessarily.
                    ignoredInvokableSymbol = invokableSymbol;
                }
                if (analyzerPhase == AnalyzerPhase.LOOP_ANALYSIS
                        || (analyzerPhase == AnalyzerPhase.LOOP_ANALYSIS_COMPLETE
                        && ignoredInvokableSymbol == invokableSymbol)) {
                    this.taintedStatus = TaintedStatus.IGNORED;
                    analyzerPhase = AnalyzerPhase.LOOP_ANALYSIS_COMPLETE;
                } else {
                    // If taint-table of invoked function is not generated yet, add it to the blocked list for latter
                    // processing.
                    addToBlockedList(invocationExpr);
                }
            } else {
                analyzeInvocation(invocationExpr);
            }
        }
    }

    @Override
    public void visit(BLangTypeInit typeInit) {
        TaintedStatus typeTaintedStatus = TaintedStatus.UNTAINTED;
        for (BLangExpression expr : typeInit.argsExpr) {
            expr.accept(this);
            // TODO: Improve: If one value ot type init is tainted, the complete type is tainted.
            if (this.taintedStatus == TaintedStatus.TAINTED) {
                typeTaintedStatus = TaintedStatus.TAINTED;
            }
        }
        typeInit.objectInitInvocation.accept(this);
        this.taintedStatus = typeTaintedStatus;
    }

    @Override
    public void visit(BLangInvocation.BLangActionInvocation actionInvocationExpr) {
        /* ignore */
    }

    @Override
    public void visit(BLangTernaryExpr ternaryExpr) {
        ternaryExpr.thenExpr.accept(this);
        TaintedStatus thenTaintedCheckResult = this.taintedStatus;
        overridingAnalysis = false;
        ternaryExpr.elseExpr.accept(this);
        overridingAnalysis = true;
        TaintedStatus elseTaintedCheckResult = this.taintedStatus;
        this.taintedStatus = getCombinedTaintedStatus(thenTaintedCheckResult, elseTaintedCheckResult);
    }

    @Override
    public void visit(BLangAwaitExpr awaitExpr) {
        awaitExpr.expr.accept(this);
    }

    @Override
    public void visit(BLangBinaryExpr binaryExpr) {
        binaryExpr.lhsExpr.accept(this);
        TaintedStatus lhsTaintedCheckResult = this.taintedStatus;
        binaryExpr.rhsExpr.accept(this);
        TaintedStatus rhsTaintedCheckResult = this.taintedStatus;
        this.taintedStatus = getCombinedTaintedStatus(lhsTaintedCheckResult, rhsTaintedCheckResult);
    }

    @Override
    public void visit(BLangElvisExpr elvisExpr) {
        elvisExpr.lhsExpr.accept(this);
        TaintedStatus lhsTaintedCheckResult = this.taintedStatus;
        elvisExpr.rhsExpr.accept(this);
        TaintedStatus rhsTaintedCheckResult = this.taintedStatus;
        this.taintedStatus = getCombinedTaintedStatus(lhsTaintedCheckResult, rhsTaintedCheckResult);
    }

    @Override
    public void visit(BLangBracedOrTupleExpr bracedOrTupleExpr) {
        bracedOrTupleExpr.expressions.forEach(expression -> expression.accept(this));
    }

    @Override
    public void visit(BLangUnaryExpr unaryExpr) {
        switch (unaryExpr.operator) {
            case LENGTHOF:
                this.taintedStatus = TaintedStatus.UNTAINTED;
                break;
            case UNTAINT:
                this.taintedStatus = TaintedStatus.UNTAINTED;
                break;
            default:
                unaryExpr.expr.accept(this);
                break;
        }
    }

    @Override
    public void visit(BLangTypedescExpr accessExpr) {
        this.taintedStatus = TaintedStatus.UNTAINTED;
    }

    @Override
    public void visit(BLangTypeCastExpr castExpr) {
        // Result of the cast is tainted if value being casted is tainted.
        castExpr.expr.accept(this);
    }

    @Override
    public void visit(BLangTypeConversionExpr conversionExpr) {
        // Result of the conversion is tainted if value being converted is tainted.
        conversionExpr.expr.accept(this);
    }

    @Override
    public void visit(BLangXMLQName xmlQName) {
        this.taintedStatus = TaintedStatus.UNTAINTED;
    }

    @Override
    public void visit(BLangXMLAttribute xmlAttribute) {
        xmlAttribute.name.accept(this);
        TaintedStatus attrNameTaintedStatus = this.taintedStatus;
        xmlAttribute.value.accept(this);
        TaintedStatus attrValueTaintedStatus = this.taintedStatus;
        this.taintedStatus = getCombinedTaintedStatus(attrNameTaintedStatus, attrValueTaintedStatus);
    }

    @Override
    public void visit(BLangXMLElementLiteral xmlElementLiteral) {
        // Visit in-line namespace declarations
        boolean inLineNamespaceTainted = false;
        for (BLangXMLAttribute attribute : xmlElementLiteral.attributes) {
            if (attribute.name.getKind() == NodeKind.XML_QNAME && ((BLangXMLQName) attribute.name).prefix.value
                    .equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                attribute.accept(this);
                if (this.taintedStatus == TaintedStatus.IGNORED) {
                    return;
                }
                setTaintedStatus(attribute.symbol, this.taintedStatus);
                if (attribute.symbol.tainted) {
                    inLineNamespaceTainted = true;
                }
            }
        }

        // Visit attributes.
        boolean attributesTainted = false;
        for (BLangXMLAttribute attribute : xmlElementLiteral.attributes) {
            if (attribute.name.getKind() == NodeKind.XML_QNAME && !((BLangXMLQName) attribute.name).prefix.value
                    .equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                attribute.accept(this);
                if (this.taintedStatus == TaintedStatus.IGNORED) {
                    return;
                }
                setTaintedStatus(attribute.symbol, this.taintedStatus);
                if (attribute.symbol.tainted) {
                    attributesTainted = true;
                }
            }
        }

        // Visit the tag names
        xmlElementLiteral.startTagName.accept(this);
        if (this.taintedStatus == TaintedStatus.IGNORED) {
            return;
        }
        TaintedStatus startTagTaintedStatus = this.taintedStatus;
        TaintedStatus endTagTaintedStatus = TaintedStatus.UNTAINTED;
        if (xmlElementLiteral.endTagName != null) {
            xmlElementLiteral.endTagName.accept(this);
            if (this.taintedStatus == TaintedStatus.IGNORED) {
                return;
            }
            endTagTaintedStatus = this.taintedStatus;
        }
        boolean tagNamesTainted = startTagTaintedStatus == TaintedStatus.TAINTED
                || endTagTaintedStatus == TaintedStatus.TAINTED;

        // Visit the children
        boolean childrenTainted = false;
        for (BLangExpression expr : xmlElementLiteral.children) {
            expr.accept(this);
            if (this.taintedStatus == TaintedStatus.IGNORED) {
                return;
            }
            if (this.taintedStatus == TaintedStatus.TAINTED) {
                childrenTainted = true;
            }
        }

        this.taintedStatus = inLineNamespaceTainted || attributesTainted || tagNamesTainted || childrenTainted ?
                TaintedStatus.TAINTED : TaintedStatus.UNTAINTED;
    }

    @Override
    public void visit(BLangXMLTextLiteral xmlTextLiteral) {
        analyzeExprList(xmlTextLiteral.textFragments);
    }

    @Override
    public void visit(BLangXMLCommentLiteral xmlCommentLiteral) {
        analyzeExprList(xmlCommentLiteral.textFragments);
    }

    @Override
    public void visit(BLangXMLProcInsLiteral xmlProcInsLiteral) {
        xmlProcInsLiteral.target.accept(this);
        if (this.taintedStatus == TaintedStatus.UNTAINTED) {
            analyzeExprList(xmlProcInsLiteral.dataFragments);
        }
    }

    @Override
    public void visit(BLangXMLQuotedString xmlQuotedString) {
        analyzeExprList(xmlQuotedString.textFragments);
    }

    @Override
    public void visit(BLangStringTemplateLiteral stringTemplateLiteral) {
        analyzeExprList(stringTemplateLiteral.exprs);
    }

    @Override
    public void visit(BLangLambdaFunction bLangLambdaFunction) {
        /* ignore */
    }

    @Override
    public void visit(BLangXMLAttributeAccess xmlAttributeAccessExpr) {
        xmlAttributeAccessExpr.expr.accept(this);
    }

    @Override
    public void visit(BLangIntRangeExpression intRangeExpression) {
        this.taintedStatus = TaintedStatus.UNTAINTED;
    }

    @Override
    public void visit(BLangTableQueryExpression tableQueryExpression) {
        /* ignore */
    }

    @Override
    public void visit(BLangRestArgsExpression varArgsExpression) {
        varArgsExpression.expr.accept(this);
    }

    @Override
    public void visit(BLangNamedArgsExpression namedArgsExpression) {
        namedArgsExpression.expr.accept(this);
    }

    @Override
    public void visit(BLangStreamingQueryStatement streamingQueryStatement) {
        /* ignore */
    }

    @Override
    public void visit(BLangWithinClause withinClause) {
        /* ignore */
    }

    @Override
    public void visit(BLangPatternClause patternClause) {
        /* ignore */
    }

    @Override
    public void visit(BLangForever foreverStatement) {
        /* ignore */
    }

    @Override
    public void visit(BLangMatchExpression bLangMatchExpression) {
        bLangMatchExpression.expr.accept(this);
    }

    @Override
    public void visit(BLangCheckedExpr match) {
        match.expr.accept(this);
    }

    // Type nodes

    @Override
    public void visit(BLangValueType valueType) {
        /* ignore */
    }

    @Override
    public void visit(BLangArrayType arrayType) {
        /* ignore */
    }

    @Override
    public void visit(BLangBuiltInRefTypeNode builtInRefType) {
        /* ignore */
    }

    @Override
    public void visit(BLangConstrainedType constrainedType) {
        /* ignore */
    }

    @Override
    public void visit(BLangUserDefinedType userDefinedType) {
        /* ignore */
    }

    @Override
    public void visit(BLangFunctionTypeNode functionTypeNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangUnionTypeNode unionTypeNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangTupleTypeNode tupleTypeNode) {
        /* ignore */
    }

    // expressions that will used only from the Desugar phase

    @Override
    public void visit(BLangSimpleVarRef.BLangLocalVarRef localVarRef) {
        /* ignore */
    }

    @Override
    public void visit(BLangSimpleVarRef.BLangFieldVarRef fieldVarRef) {
        /* ignore */
    }

    @Override
    public void visit(BLangSimpleVarRef.BLangPackageVarRef packageVarRef) {
        /* ignore */
    }

    @Override
    public void visit(BLangSimpleVarRef.BLangFunctionVarRef functionVarRef) {
        /* ignore */
    }

    @Override
    public void visit(BLangSimpleVarRef.BLangTypeLoad typeLoad) {
        /* ignore */
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangStructFieldAccessExpr fieldAccessExpr) {
        /* ignore */
    }

    @Override
    public void visit(BLangFieldBasedAccess.BLangStructFunctionVarRef functionVarRef) {
        /* ignore */
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangMapAccessExpr mapKeyAccessExpr) {
        /* ignore */
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangArrayAccessExpr arrayIndexAccessExpr) {
        /* ignore */
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangTupleAccessExpr arrayIndexAccessExpr) {
        /* ignore */
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangXMLAccessExpr xmlIndexAccessExpr) {
        /* ignore */
    }

    @Override
    public void visit(BLangRecordLiteral.BLangJSONLiteral jsonLiteral) {
        /* ignore */
    }

    @Override
    public void visit(BLangRecordLiteral.BLangMapLiteral mapLiteral) {
        /* ignore */
    }

    @Override
    public void visit(BLangRecordLiteral.BLangStructLiteral structLiteral) {
        /* ignore */
    }

    @Override
    public void visit(BLangRecordLiteral.BLangStreamLiteral streamLiteral) {
        /* ignore */
    }

    @Override
    public void visit(BLangInvocation.BFunctionPointerInvocation bFunctionPointerInvocation) {
        /* ignore */
    }

    @Override
    public void visit(BLangInvocation.BLangAttachedFunctionInvocation iExpr) {
        /* ignore */
    }

    @Override
    public void visit(BLangArrayLiteral.BLangJSONArrayLiteral jsonArrayLiteral) {
        /* ignore */
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangJSONAccessExpr jsonAccessExpr) {
        /* ignore */
    }

    @Override
    public void visit(BLangXMLNS.BLangLocalXMLNS xmlnsNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangXMLNS.BLangPackageXMLNS xmlnsNode) {
        /* ignore */
    }

    @Override
    public void visit(BLangFieldBasedAccess.BLangEnumeratorAccessExpr enumeratorAccessExpr) {
        /* ignore */
    }

    @Override
    public void visit(BLangXMLSequenceLiteral bLangXMLSequenceLiteral) {
        /* ignore */
    }

    @Override
    public void visit(BLangStatementExpression bLangStatementExpression) {
        /* ignore */
    }

    // Private

    private <T extends BLangNode, U extends SymbolEnv> void analyzeNode(T t, U u) {
        SymbolEnv prevEnv = this.env;
        this.env = u;
        t.accept(this);
        this.env = prevEnv;
    }

    /**
     * If any one of the given expressions are tainted, the final result will be tainted.
     *
     * @param exprs List of expressions to analyze.
     */
    private void analyzeExprList(List<BLangExpression> exprs) {
        for (BLangExpression expr : exprs) {
            expr.accept(this);
            if (this.taintedStatus == TaintedStatus.TAINTED) {
                break;
            }
        }
    }

    private boolean hasAnnotation(BLangVariable variable, String expectedAnnotation) {
        return hasAnnotation(variable.annAttachments, expectedAnnotation);
    }

    private boolean hasAnnotation(List<BLangAnnotationAttachment> annotationAttachmentList, String expectedAnnotation) {
        return annotationAttachmentList.stream().anyMatch(annotation ->
                annotation.annotationName.value.equals(expectedAnnotation));
    }

    /**
     * Combines multiple tainted-statuses together and decide the final tainted-status.
     * If any one of the combined tainted-statuses is "tainted", the combined status is tainted.
     *
     *   Example: string x = a + b;
     *   if "a" or "b" is tainted "x" is tainted.
     *
     * @param taintedStatuses list of tainted statues
     * @return tainted status after combining multiple tainted statuses
     */
    private TaintedStatus getCombinedTaintedStatus(TaintedStatus... taintedStatuses) {
        TaintedStatus combinedTaintedStatus = TaintedStatus.IGNORED;
        for (TaintedStatus taintedStatus : taintedStatuses) {
            if (taintedStatus == TaintedStatus.TAINTED) {
                combinedTaintedStatus = TaintedStatus.TAINTED;
            } else if (combinedTaintedStatus == TaintedStatus.IGNORED && taintedStatus == TaintedStatus.UNTAINTED) {
                combinedTaintedStatus = TaintedStatus.UNTAINTED;
            }
        }
        return combinedTaintedStatus;
    }

    /**
     * Set tainted status of the variable. When non-overriding analysis is in progress, this will not override "tainted"
     * status with "untainted" status. As an example, the "else" section of a "if-else" block, cannot change a value
     * marked "tainted" by the "if" block.
     *
     * @param varNode       Variable node to be updated.
     * @param taintedStatus Tainted status.
     */
    private void setTaintedStatus(BLangVariable varNode, TaintedStatus taintedStatus) {
        if (taintedStatus != TaintedStatus.IGNORED && (overridingAnalysis || !varNode.symbol.tainted)) {
            setTaintedStatus(varNode.symbol, taintedStatus);
        }
    }

    /**
     * Set tainted status of the variable. When non-overriding analysis is in progress, this will not override "tainted"
     * status with "untainted" status. As an example, the "else" section of a "if-else" block, cannot change a value
     * marked "tainted" by the "if" block.
     *
     * @param varNode       Variable node to be updated.
     * @param taintedStatus Tainted status.
     */
    private void setTaintedStatus(BLangVariableReference varNode, TaintedStatus taintedStatus) {
        if (taintedStatus != TaintedStatus.IGNORED && (overridingAnalysis || !varNode.symbol.tainted)) {
            setTaintedStatus(varNode.symbol, taintedStatus);
        }
    }

    private void setTaintedStatus(BSymbol symbol, TaintedStatus taintedStatus) {
        if (taintedStatus != TaintedStatus.IGNORED && symbol != null) {
            if (taintedStatus == TaintedStatus.TAINTED) {
                symbol.tainted = true;
            } else {
                symbol.tainted = false;
            }
        }
    }

    // Private methods related to invokable node analysis and taint-table generation.

    private void visitEntryPoint(BLangInvokableNode invNode, SymbolEnv funcEnv) {
        if (analyzerPhase == AnalyzerPhase.LOOP_ANALYSIS) {
            return;
        }
        // Entry point input parameters are all tainted, since they contain user controlled data.
        // If any value has been marked "sensitive" generate an error.
        if (isEntryPointParamsInvalid(invNode.requiredParams)) {
            return;
        }
        List<BLangVariable> defaultableParamsVarList = new ArrayList<>();
        invNode.defaultableParams.forEach(defaultableParam -> defaultableParamsVarList.add(defaultableParam.var));
        if (isEntryPointParamsInvalid(defaultableParamsVarList)) {
            return;
        }
        if (invNode.restParam != null && isEntryPointParamsInvalid(Collections.singletonList(invNode.restParam))) {
            return;
        }
        // Perform end point analysis.
        entryPointAnalysis = true;
        analyzeReturnTaintedStatus(invNode, funcEnv);
        entryPointAnalysis = false;
        boolean isBlocked = processBlockedNode(invNode, true);
        if (isBlocked) {
            return;
        } else {
            // Display errors only if scan of was fully complete, so that errors will not get duplicated.
            taintErrorSet.forEach(error -> this.dlog.error(error.pos, error.diagnosticCode, error.paramName));
            taintErrorSet.clear();
        }
        invNode.symbol.taintTable = new HashMap<>();
    }

    private boolean isEntryPointParamsInvalid(List<BLangVariable> params) {
        if (params != null) {
            for (BLangVariable param : params) {
                param.symbol.tainted = true;
                if (hasAnnotation(param, ANNOTATION_SENSITIVE)) {
                    this.dlog.error(param.pos, DiagnosticCode.ENTRY_POINT_PARAMETERS_CANNOT_BE_SENSITIVE,
                            param.name.value);
                    return true;
                }
            }
        }
        return false;
    }

    private void visitInvokable(BLangInvokableNode invNode, SymbolEnv symbolEnv) {
        if (analyzerPhase == AnalyzerPhase.LOOPS_RESOLVED_ANALYSIS || invNode.symbol.taintTable == null
                || (invNode.getKind() == NodeKind.FUNCTION && ((BLangFunction) invNode).attachedOuterFunction)) {
            if (Symbols.isNative(invNode.symbol)
                    || (invNode.getKind() == NodeKind.FUNCTION && ((BLangFunction) invNode).interfaceFunction)) {
                attachTaintTableBasedOnAnnotations(invNode);
                return;
            }
            Map<Integer, TaintRecord> taintTable = new HashMap<>();
            this.returnTaintedStatus = TaintedStatus.UNTAINTED;
            // Check the tainted status of return values when no parameter is tainted.
            analyzeAllParamsUntaintedReturnTaintedStatus(taintTable, invNode, symbolEnv);
            if (analyzerPhase == AnalyzerPhase.LOOP_ANALYSIS_COMPLETE) {
                analyzerPhase = AnalyzerPhase.LOOP_ANALYSIS;
            }
            boolean isBlocked = processBlockedNode(invNode, false);
            if (isBlocked) {
                return;
            }
            int requiredParamCount = invNode.requiredParams.size();
            int defaultableParamCount = invNode.defaultableParams.size();
            int totalParamCount = requiredParamCount + defaultableParamCount + (invNode.restParam == null ? 0 : 1);
            if (taintErrorSet.size() > 0) {
                // If taint error occurred when no parameter is tainted, there is no point of checking tainted status of
                // returns when each parameter is tainted. If done, it will generate errors saying return is tainted
                // when each input parameter is tainted, which can be invalid.
                taintErrorSet.clear();
                for (int paramIndex = 0; paramIndex < totalParamCount; paramIndex++) {
                    List<Boolean> returnTaintedStatusList = new ArrayList<>();
                    if (invNode.returnTypeNode.type != symTable.nilType) {
                        returnTaintedStatusList.add(RETURN_TAINTED_STATUS_COLUMN_INDEX, false);
                    }
                    taintTable.put(paramIndex, new TaintRecord(returnTaintedStatusList, null));
                }
            } else {
                for (int paramIndex = 0; paramIndex < totalParamCount; paramIndex++) {
                    BLangVariable param = getParam(invNode, paramIndex, requiredParamCount, defaultableParamCount);
                    // If parameter is sensitive, it's invalid to have a case where tainted status of parameter is true.
                    if (hasAnnotation(param, ANNOTATION_SENSITIVE)) {
                        continue;
                    }
                    this.returnTaintedStatus = TaintedStatus.UNTAINTED;
                    // Set each parameter "tainted", then analyze the body to observe the outcome of the function.
                    analyzeReturnTaintedStatus(taintTable, invNode, symbolEnv, paramIndex, requiredParamCount,
                            defaultableParamCount);
                    if (analyzerPhase == AnalyzerPhase.LOOP_ANALYSIS_COMPLETE) {
                        analyzerPhase = AnalyzerPhase.LOOP_ANALYSIS;
                    }
                    if (taintErrorSet.size() > 0) {
                        taintErrorSet.clear();
                    }
                }
            }
            invNode.symbol.taintTable = taintTable;
        }
    }

    private void analyzeAllParamsUntaintedReturnTaintedStatus(Map<Integer, TaintRecord> taintTable,
                                                              BLangInvokableNode invokableNode, SymbolEnv symbolEnv) {
        analyzeReturnTaintedStatus(taintTable, invokableNode, symbolEnv, ALL_UNTAINTED_TABLE_ENTRY_INDEX, 0, 0);
    }

    private void analyzeReturnTaintedStatus(Map<Integer, TaintRecord> taintTable, BLangInvokableNode invokableNode,
                                            SymbolEnv symbolEnv, int paramIndex, int requiredParamCount,
                                            int defaultableParamCount) {
        resetTaintedStatusOfVariables(invokableNode.requiredParams);
        resetTaintedStatusOfVariableDef(invokableNode.defaultableParams);
        if (invokableNode.restParam != null) {
            resetTaintedStatusOfVariables(Collections.singletonList(invokableNode.restParam));
        }
        // Mark the given parameter "tainted".
        if (paramIndex != ALL_UNTAINTED_TABLE_ENTRY_INDEX) {
            if (paramIndex < requiredParamCount) {
                invokableNode.requiredParams.get(paramIndex).symbol.tainted = true;
            } else if (paramIndex < requiredParamCount + defaultableParamCount) {
                invokableNode.defaultableParams.get(paramIndex - requiredParamCount).var.symbol.tainted = true;
            } else {
                invokableNode.restParam.symbol.tainted = true;
            }
        }
        analyzeReturnTaintedStatus(invokableNode, symbolEnv);
        if (taintErrorSet.size() > 0) {
            // When invocation returns an error (due to passing a tainted argument to a sensitive parameter) add current
            // error to the table for future reference. However, if taint-error is raised when analyzing all-parameters
            // are untainted, the code of the function is wrong (and passes a tainted value generated within the
            // function body to a sensitive parameter). Hence, instead of adding error to table, directly generate the
            // error and fail the compilation.
            if (paramIndex == ALL_UNTAINTED_TABLE_ENTRY_INDEX && (analyzerPhase == AnalyzerPhase.INITIAL_ANALYSIS
                    || analyzerPhase == AnalyzerPhase.BLOCKED_NODE_ANALYSIS
                    || analyzerPhase == AnalyzerPhase.LOOPS_RESOLVED_ANALYSIS)) {
                taintErrorSet.forEach(error -> this.dlog.error(error.pos, error.diagnosticCode, error.paramName));
            } else {
                taintTable.put(paramIndex, new TaintRecord(null, new ArrayList<>(taintErrorSet)));
            }
        } else if (this.blockedNode == null) {
            List<Boolean> returnTaintedStatusList = new ArrayList<>();
            if (invokableNode.returnTypeNode.type != symTable.nilType) {
                updatedReturnTaintedStatusBasedOnAnnotations(invokableNode.returnTypeAnnAttachments);
                returnTaintedStatusList.add(RETURN_TAINTED_STATUS_COLUMN_INDEX,
                        this.returnTaintedStatus == TaintedStatus.TAINTED);
            }
            taintTable.put(paramIndex, new TaintRecord(returnTaintedStatusList, null));
        }
    }

    private void resetTaintedStatusOfVariables(List<BLangVariable> params) {
        if (params != null) {
            params.forEach(param -> param.symbol.tainted = false);
        }
    }

    private void resetTaintedStatusOfVariableDef(List<BLangVariableDef> params) {
        if (params != null) {
            List<BLangVariable> defaultableParamsVarList = new ArrayList<>();
            params.forEach(defaultableParam -> defaultableParamsVarList.add(defaultableParam.var));
            resetTaintedStatusOfVariables(defaultableParamsVarList);
        }
    }

    private void analyzeReturnTaintedStatus(BLangInvokableNode invokableNode, SymbolEnv symbolEnv) {
        ignoredInvokableSymbol = null;
        invokableNode.endpoints.forEach(endpoint -> endpoint.accept(this));
        if (invokableNode.workers.isEmpty()) {
            analyzeNode(invokableNode.body, symbolEnv);
        } else {
            analyzeWorkers(invokableNode.workers);
        }
        if (stopAnalysis) {
            stopAnalysis = false;
        }
    }

    private void analyzeWorkers (List<BLangWorker> workers) {
        workerInteractionTaintedStatusMap = new HashMap<>();
        boolean doScan = true;
        while (doScan) {
            doScan = false;
            for (BLangWorker worker : workers) {
                blockedOnWorkerInteraction = false;
                worker.endpoints.forEach(endpoint -> endpoint.accept(this));
                worker.accept(this);
                if (this.blockedNode != null || taintErrorSet.size() > 0) {
                    return;
                } else if (blockedOnWorkerInteraction) {
                    doScan = true;
                    stopAnalysis = false;
                } else if (stopAnalysis) {
                    return;
                }
            }
        }
    }

    private void attachTaintTableBasedOnAnnotations(BLangInvokableNode invokableNode) {
        if (invokableNode.symbol.taintTable == null) {
            // Extract tainted status of the function by looking at annotations added to returns.
            List<Boolean> retParamsTaintedStatus = new ArrayList<>();
            retParamsTaintedStatus.add(hasAnnotation(invokableNode.returnTypeAnnAttachments, ANNOTATION_TAINTED));

            // Append taint table with tainted status when no parameter is tainted.
            Map<Integer, TaintRecord> taintTable = new HashMap<>();
            taintTable.put(ALL_UNTAINTED_TABLE_ENTRY_INDEX, new TaintRecord(retParamsTaintedStatus, null));
            int requiredParamCount = invokableNode.requiredParams.size();
            int defaultableParamCount = invokableNode.defaultableParams.size();
            int totalParamCount =
                    requiredParamCount + defaultableParamCount + (invokableNode.restParam == null ? 0 : 1);
            if (totalParamCount > 0) {
                // Append taint table with tainted status when each parameter is tainted.
                for (int paramIndex = 0; paramIndex < totalParamCount; paramIndex++) {
                    BLangVariable param = getParam(invokableNode, paramIndex, requiredParamCount,
                            defaultableParamCount);
                    // If parameter is sensitive, test for this parameter being tainted is invalid.
                    if (hasAnnotation(param, ANNOTATION_SENSITIVE)) {
                        continue;
                    }
                    taintTable.put(paramIndex, new TaintRecord(retParamsTaintedStatus, null));
                }
            }
            invokableNode.symbol.taintTable = taintTable;
        }
    }

    private void updatedReturnTaintedStatusBasedOnAnnotations(List<BLangAnnotationAttachment> retParamsAnnotations) {
        if (hasAnnotation(retParamsAnnotations, ANNOTATION_UNTAINTED)) {
            this.returnTaintedStatus = TaintedStatus.UNTAINTED;
        }
        if (hasAnnotation(retParamsAnnotations, ANNOTATION_TAINTED)) {
            this.returnTaintedStatus = TaintedStatus.TAINTED;
        }
    }

    private boolean processBlockedNode(BLangInvokableNode invokableNode, boolean isEntryPoint) {
        boolean isBlocked = false;
        if (this.blockedNode != null) {
            // Add the function being blocked into the blocked node list for later processing.
            this.blockedNode.invokableNode = invokableNode;
            if (analyzerPhase == AnalyzerPhase.INITIAL_ANALYSIS) {
                if (isEntryPoint) {
                    blockedEntryPointNodeList.add(blockedNode);
                } else {
                    blockedNodeList.add(blockedNode);
                }
            }
            this.blockedNode = null;
            // Discard any error generated if invokable was found to be blocked. This will avoid duplicates when
            // blocked invokable is re-examined.
            taintErrorSet.clear();
            isBlocked = true;
        }
        return isBlocked;
    }

    private void analyzeIterableLambdaInvocationArgExpression(BLangExpression argExpr) {
        BLangFunction function = ((BLangLambdaFunction) argExpr).function;
        if (function.symbol.taintTable == null) {
            this.blockedNode = new BlockedNode(this.currPkgEnv, null);
            stopAnalysis = true;
            this.taintedStatus = TaintedStatus.UNTAINTED;
        } else if (this.taintedStatus == TaintedStatus.TAINTED) {
            int requiredParamCount = function.requiredParams.size();
            int defaultableParamCount = function.defaultableParams.size();
            int totalParamCount = requiredParamCount + defaultableParamCount + (function.restParam == null ? 0 : 1);
            Map<Integer, TaintRecord> taintTable = function.symbol.taintTable;
            for (int paramIndex = 0; paramIndex < totalParamCount; paramIndex++) {
                TaintRecord taintRecord = taintTable.get(paramIndex);
                BLangVariable param = getParam(function, paramIndex, requiredParamCount, defaultableParamCount);
                if (taintRecord == null) {
                    addTaintError(argExpr.pos, param.name.value,
                            DiagnosticCode.TAINTED_VALUE_PASSED_TO_SENSITIVE_PARAMETER);
                } else if (taintRecord.taintError != null && taintRecord.taintError.size() > 0) {
                    addTaintError(taintRecord.taintError);
                }
                if (stopAnalysis) {
                    break;
                }
            }
        }
    }

    private void addTaintError(DiagnosticPos diagnosticPos, String paramName, DiagnosticCode diagnosticCode) {
        TaintRecord.TaintError taintError = new TaintRecord.TaintError(diagnosticPos, paramName, diagnosticCode);
        taintErrorSet.add(taintError);
        if (!entryPointAnalysis) {
            stopAnalysis = true;
        }
    }

    private void addTaintError(List<TaintRecord.TaintError> taintErrors) {
        taintErrorSet.addAll(taintErrors);
        if (!entryPointAnalysis) {
            stopAnalysis = true;
        }
    }

    private void addToBlockedList(BLangInvocation invocationExpr) {
        BlockingNode blockingNode = new BlockingNode(invocationExpr.symbol.pkgID, invocationExpr.symbol.name);
        this.blockedNode = new BlockedNode(this.currPkgEnv, blockingNode);
        stopAnalysis = true;
        this.taintedStatus = TaintedStatus.UNTAINTED;
    }

    // Private methods relevant to invocation analysis.

    private void analyzeInvocation(BLangInvocation invocationExpr) {
        BInvokableSymbol invokableSymbol = (BInvokableSymbol) invocationExpr.symbol;
        Map<Integer, TaintRecord> taintTable = invokableSymbol.taintTable;
        TaintedStatus returnTaintedStatus = TaintedStatus.UNTAINTED;
        // Get tainted status when all parameters are untainted
        TaintRecord allParamsUntaintedRecord = taintTable.get(ALL_UNTAINTED_TABLE_ENTRY_INDEX);
        if (allParamsUntaintedRecord != null) {
            if (allParamsUntaintedRecord.taintError != null && allParamsUntaintedRecord.taintError.size() > 0) {
                // This can occur when there is a error regardless of tainted status of parameters.
                // Example: Tainted value returned by function is passed to another functions's sensitive parameter.
                addTaintError(allParamsUntaintedRecord.taintError);
            } else if (allParamsUntaintedRecord.retParamTaintedStatus.size() > 0) {
                returnTaintedStatus = allParamsUntaintedRecord.retParamTaintedStatus
                        .get(RETURN_TAINTED_STATUS_COLUMN_INDEX) ? TaintedStatus.TAINTED : TaintedStatus.UNTAINTED;
            }
        }
        // Check tainted status of each argument. If argument is tainted, get the taint table when the given parameter
        // is tainted and use it to update "allParamsUntaintedRecord". Do same for all parameters to identify the
        // complete taint outcome of the current function.
        if (invocationExpr.argExprs != null) {
            int requiredParamCount = invokableSymbol.params.size();
            int defaultableParamCount = invokableSymbol.defaultableParams.size();
            for (int argIndex = 0; argIndex < invocationExpr.requiredArgs.size(); argIndex++) {
                BLangExpression argExpr = invocationExpr.requiredArgs.get(argIndex);
                TaintedStatus argumentAnalysisResult = analyzeInvocationArgument(argIndex, invokableSymbol,
                        invocationExpr, argExpr);
                if (argumentAnalysisResult == TaintedStatus.IGNORED) {
                    return;
                } else if (argumentAnalysisResult == TaintedStatus.TAINTED) {
                    returnTaintedStatus = TaintedStatus.TAINTED;
                }
                if (stopAnalysis) {
                    break;
                }
            }
            for (int argIndex = 0; argIndex < invocationExpr.namedArgs.size(); argIndex++) {
                BLangExpression argExpr = invocationExpr.namedArgs.get(argIndex);
                if (argExpr.getKind() == NodeKind.NAMED_ARGS_EXPR) {
                    String currentNamedArgExprName = ((BLangNamedArgsExpression) argExpr).name.value;
                    // Pick the index of this defaultable parameter in the invokable definition.
                    int paramIndex = 0;
                    for (int defaultableParamIndex = 0; defaultableParamIndex < invokableSymbol.defaultableParams
                            .size(); defaultableParamIndex++) {
                        BVarSymbol defaultableParam = invokableSymbol.defaultableParams.get(defaultableParamIndex);
                        if (defaultableParam.name.value.equals(currentNamedArgExprName)) {
                            paramIndex = requiredParamCount + defaultableParamIndex;
                            break;
                        }
                    }
                    TaintedStatus argumentAnalysisResult = analyzeInvocationArgument(paramIndex, invokableSymbol,
                            invocationExpr, argExpr);
                    if (argumentAnalysisResult == TaintedStatus.IGNORED) {
                        return;
                    } else if (argumentAnalysisResult == TaintedStatus.TAINTED) {
                        returnTaintedStatus = TaintedStatus.TAINTED;
                    }
                    if (stopAnalysis) {
                        break;
                    }
                }
            }
            for (int argIndex = 0; argIndex < invocationExpr.restArgs.size(); argIndex++) {
                BLangExpression argExpr = invocationExpr.restArgs.get(argIndex);
                // Pick the index of the rest parameter in the invokable definition.
                int paramIndex = requiredParamCount + defaultableParamCount;
                TaintedStatus argumentAnalysisResult = analyzeInvocationArgument(paramIndex, invokableSymbol,
                        invocationExpr, argExpr);
                if (argumentAnalysisResult == TaintedStatus.IGNORED) {
                    return;
                } else if (argumentAnalysisResult == TaintedStatus.TAINTED) {
                    returnTaintedStatus = TaintedStatus.TAINTED;
                }
                if (stopAnalysis) {
                    break;
                }
            }
        }
        // When an invocation like stringValue.trim() happens, if stringValue is tainted, the result should also be
        // tainted.
        if (invocationExpr.expr != null) {
            //TODO: TaintedIf annotation, so that it's possible to define what can taint or untaint the return.
            invocationExpr.expr.accept(this);
            if (this.taintedStatus == TaintedStatus.IGNORED) {
                return;
            } else if (this.taintedStatus == TaintedStatus.TAINTED) {
                returnTaintedStatus = TaintedStatus.TAINTED;
            }
        }
        taintedStatus = returnTaintedStatus;
    }

    private TaintedStatus analyzeInvocationArgument(int paramIndex, BInvokableSymbol invokableSymbol,
                                                    BLangInvocation invocationExpr, BLangExpression argExpr) {
        argExpr.accept(this);
        // If current argument is tainted, look-up the taint-table for the record of return-tainted-status when the
        // given argument is in tainted state.
        if (this.taintedStatus == TaintedStatus.TAINTED) {
            TaintRecord taintRecord = invokableSymbol.taintTable.get(paramIndex);
            int requiredParamCount = invokableSymbol.params.size();
            int defaultableParamCount = invokableSymbol.defaultableParams.size();
            BVarSymbol paramSymbol = getParamSymbol(invokableSymbol, paramIndex, requiredParamCount,
                    defaultableParamCount);

            if (taintRecord == null) {
                // This is when current parameter is "sensitive". Therefore, providing a tainted value to a sensitive
                // parameter is invalid and should return a compiler error.
                DiagnosticPos argPos = argExpr.pos != null ? argExpr.pos : invocationExpr.pos;
                addTaintError(argPos, paramSymbol.name.value,
                        DiagnosticCode.TAINTED_VALUE_PASSED_TO_SENSITIVE_PARAMETER);
            } else if (taintRecord.taintError != null && taintRecord.taintError.size() > 0) {
                // This is when current parameter is derived to be sensitive.
                taintRecord.taintError.forEach(error -> {
                    if (error.diagnosticCode == DiagnosticCode.TAINTED_VALUE_PASSED_TO_GLOBAL_VARIABLE) {
                        addTaintError(taintRecord.taintError);
                    } else {
                        DiagnosticPos argPos = argExpr.pos != null ? argExpr.pos : invocationExpr.pos;
                        addTaintError(argPos, paramSymbol.name.value,
                                DiagnosticCode.TAINTED_VALUE_PASSED_TO_SENSITIVE_PARAMETER);
                    }
                });
            } else {
                return taintRecord.retParamTaintedStatus.size() > 0
                        && taintRecord.retParamTaintedStatus.get(RETURN_TAINTED_STATUS_COLUMN_INDEX) ?
                        TaintedStatus.TAINTED : TaintedStatus.UNTAINTED;
            }
        } else if (this.taintedStatus == TaintedStatus.IGNORED) {
            return TaintedStatus.IGNORED;
        }
        return TaintedStatus.UNTAINTED;
    }

    private void resolveBlockedInvokable(List<BlockedNode> blockedNodeList) {
        List<BlockedNode> remainingBlockedNodeList = new ArrayList<>();
        // Revisit blocked nodes and attempt to generate the table.
        for (BlockedNode blockedNode : blockedNodeList) {
            this.env = blockedNode.pkgSymbol;
            blockedNode.invokableNode.accept(this);
            if (blockedNode.invokableNode.symbol.taintTable == null) {
                remainingBlockedNodeList.add(blockedNode);
            }
        }
        // If list is not moving, there is a recursion. Derive the tainted status of all the blocked functions by using
        // annotations and if annotations are not present generate error.
        if (remainingBlockedNodeList.size() != 0 && blockedNodeList.size() == remainingBlockedNodeList.size()) {
            for (BlockedNode blockedNode : remainingBlockedNodeList) {
                analyzerPhase = AnalyzerPhase.LOOP_ANALYSIS;
                this.env = blockedNode.pkgSymbol;
                blockedNode.invokableNode.accept(this);
            }
            analyzerPhase = AnalyzerPhase.LOOPS_RESOLVED_ANALYSIS;
            resolveBlockedInvokable(remainingBlockedNodeList);
        } else if (remainingBlockedNodeList.size() > 0) {
            resolveBlockedInvokable(remainingBlockedNodeList);
        }
        // If remainingBlockedNodeList is empty, end the recursion.
    }

    private BLangVariable getParam(BLangInvokableNode invNode, int paramIndex, int requiredParamCount,
                                   int defaultableParamCount) {
        BLangVariable param;
        if (paramIndex < requiredParamCount) {
            param = invNode.requiredParams.get(paramIndex);
        } else if (paramIndex < requiredParamCount + defaultableParamCount) {
            param = invNode.defaultableParams.get(paramIndex - requiredParamCount).var;
        } else {
            param = invNode.restParam;
        }
        return param;
    }

    private BVarSymbol getParamSymbol(BInvokableSymbol invSymbol, int paramIndex, int requiredParamCount,
                                      int defaultableParamCount) {
        BVarSymbol param;
        if (paramIndex < requiredParamCount) {
            param = invSymbol.params.get(paramIndex);
        } else if (paramIndex < requiredParamCount + defaultableParamCount) {
            param = invSymbol.defaultableParams.get(paramIndex - requiredParamCount);
        } else {
            param = invSymbol.restParam;
        }
        return param;
    }

    private class BlockingNode {

        PackageID packageID;
        Name name;

        BlockingNode(PackageID packageID, Name name) {
            this.packageID = packageID;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            BlockingNode that = (BlockingNode) o;

            if (!packageID.equals(that.packageID)) {
                return false;
            }
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            int result = packageID.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }

    private class BlockedNode {

        SymbolEnv pkgSymbol;
        BLangInvokableNode invokableNode;
        BlockingNode blockingNode;

        BlockedNode(SymbolEnv pkgSymbol, BlockingNode blockingNode) {
            this.pkgSymbol = pkgSymbol;
            this.blockingNode = blockingNode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            BlockedNode that = (BlockedNode) o;

            return invokableNode.symbol.pkgID.equals(that.invokableNode.symbol.pkgID) && invokableNode.symbol.name
                    .equals(that.invokableNode.symbol.name);
        }

        @Override
        public int hashCode() {
            int result = invokableNode.symbol.pkgID.hashCode();
            result = 31 * result + invokableNode.symbol.name.hashCode();
            return result;
        }
    }
}
