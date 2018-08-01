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
package org.wso2.ballerinalang.compiler;

import org.ballerinalang.compiler.CompilerOptionName;
import org.ballerinalang.compiler.CompilerPhase;
import org.wso2.ballerinalang.compiler.codegen.CodeGenerator;
import org.wso2.ballerinalang.compiler.desugar.Desugar;
import org.wso2.ballerinalang.compiler.semantics.analyzer.CodeAnalyzer;
import org.wso2.ballerinalang.compiler.semantics.analyzer.CompilerPluginRunner;
import org.wso2.ballerinalang.compiler.semantics.analyzer.DocumentationAnalyzer;
import org.wso2.ballerinalang.compiler.semantics.analyzer.SemanticAnalyzer;
import org.wso2.ballerinalang.compiler.semantics.analyzer.SymbolEnter;
import org.wso2.ballerinalang.compiler.semantics.analyzer.SymbolResolver;
import org.wso2.ballerinalang.compiler.semantics.analyzer.TaintAnalyzer;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolTable;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BPackageSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BTypeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.SymTag;
import org.wso2.ballerinalang.compiler.semantics.model.types.BField;
import org.wso2.ballerinalang.compiler.semantics.model.types.BStructureType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BUnionType;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.CompilerOptions;
import org.wso2.ballerinalang.compiler.util.Names;
import org.wso2.ballerinalang.compiler.util.diagnotic.BLangDiagnosticLog;

import java.util.HashSet;
import java.util.Set;

import static org.wso2.ballerinalang.compiler.semantics.model.SymbolTable.BUILTIN;
import static org.wso2.ballerinalang.util.RepoUtils.LOAD_BUILTIN_FROM_SOURCE;

/**
 * This class drives the compilation of packages through various phases
 * such as symbol enter, semantic analysis, type checking, code analysis,
 * desugar and code generation.
 *
 * @since 0.965.0
 */
public class CompilerDriver {

    private static final CompilerContext.Key<CompilerDriver> COMPILER_DRIVER_KEY =
            new CompilerContext.Key<>();

    private final CompilerOptions options;
    private final BLangDiagnosticLog dlog;
    private final PackageLoader pkgLoader;
    private final PackageCache pkgCache;
    private final SymbolTable symbolTable;
    private final SymbolEnter symbolEnter;
    private final SemanticAnalyzer semAnalyzer;
    private final CodeAnalyzer codeAnalyzer;
    private final TaintAnalyzer taintAnalyzer;
    private final DocumentationAnalyzer documentationAnalyzer;
    private final CompilerPluginRunner compilerPluginRunner;
    private final Desugar desugar;
    private final CodeGenerator codeGenerator;
    private final CompilerPhase compilerPhase;
    private final SymbolResolver symResolver;

    public static CompilerDriver getInstance(CompilerContext context) {
        CompilerDriver compilerDriver = context.get(COMPILER_DRIVER_KEY);
        if (compilerDriver == null) {
            compilerDriver = new CompilerDriver(context);
        }
        return compilerDriver;
    }

    private CompilerDriver(CompilerContext context) {
        context.put(COMPILER_DRIVER_KEY, this);

        this.options = CompilerOptions.getInstance(context);
        this.dlog = BLangDiagnosticLog.getInstance(context);
        this.pkgLoader = PackageLoader.getInstance(context);
        this.pkgCache = PackageCache.getInstance(context);
        this.symbolTable = SymbolTable.getInstance(context);
        this.symbolEnter = SymbolEnter.getInstance(context);
        this.semAnalyzer = SemanticAnalyzer.getInstance(context);
        this.codeAnalyzer = CodeAnalyzer.getInstance(context);
        this.documentationAnalyzer = DocumentationAnalyzer.getInstance(context);
        this.taintAnalyzer = TaintAnalyzer.getInstance(context);
        this.compilerPluginRunner = CompilerPluginRunner.getInstance(context);
        this.desugar = Desugar.getInstance(context);
        this.codeGenerator = CodeGenerator.getInstance(context);
        this.compilerPhase = getCompilerPhase();
        this.symResolver = SymbolResolver.getInstance(context);
    }

    public BLangPackage compilePackage(BLangPackage packageNode) {
        compilePackageSymbol(packageNode.symbol);
        return packageNode;
    }

    public void loadBuiltinPackage() {
        // Load built-in packages.
        if (LOAD_BUILTIN_FROM_SOURCE) {
            BLangPackage builtInPkg = getBuiltInPackage();
            symbolTable.builtInPackageSymbol = builtInPkg.symbol;
        } else {
            symbolTable.builtInPackageSymbol = pkgLoader.loadPackageSymbol(BUILTIN, null, null);
        }

        // Update the error symbol
        updateErrorSymbol();
    }

    // Private methods

    private void compilePackageSymbol(BPackageSymbol packageSymbol) {
        BLangPackage pkgNode = this.pkgCache.get(packageSymbol.pkgID);
        if (pkgNode == null) {
            // This is a package loaded from a BALO.
            return;
        }

        if (pkgNode.completedPhases.contains(CompilerPhase.TYPE_CHECK)) {
            return;
        }

        pkgNode.imports.stream()
                .filter(pkg -> pkg.symbol != null)
                .forEach(importPkgNode -> this.compilePackageSymbol(importPkgNode.symbol));
        compile(pkgNode);
    }

    private void compile(BLangPackage pkgNode) {
        if (this.stopCompilation(pkgNode, CompilerPhase.TYPE_CHECK)) {
            return;
        }

        typeCheck(pkgNode);
        if (this.stopCompilation(pkgNode, CompilerPhase.CODE_ANALYZE)) {
            return;
        }

        codeAnalyze(pkgNode);
        if (this.stopCompilation(pkgNode, CompilerPhase.DOCUMENTATION_ANALYZE)) {
            return;
        }

        documentationAnalyze(pkgNode);
        if (this.stopCompilation(pkgNode, CompilerPhase.TAINT_ANALYZE)) {
            return;
        }

        taintAnalyze(pkgNode);
        if (this.stopCompilation(pkgNode, CompilerPhase.COMPILER_PLUGIN)) {
            return;
        }

        annotationProcess(pkgNode);
        if (this.stopCompilation(pkgNode, CompilerPhase.DESUGAR)) {
            return;
        }

        desugar(pkgNode);
        if (this.stopCompilation(pkgNode, CompilerPhase.CODE_GEN)) {
            return;
        }

        codegen(pkgNode);
    }

    public BLangPackage define(BLangPackage pkgNode) {
        return this.symbolEnter.definePackage(pkgNode);
    }

    private BLangPackage typeCheck(BLangPackage pkgNode) {
        return this.semAnalyzer.analyze(pkgNode);
    }

    private BLangPackage documentationAnalyze(BLangPackage pkgNode) {
        return this.documentationAnalyzer.analyze(pkgNode);
    }

    private BLangPackage codeAnalyze(BLangPackage pkgNode) {
        return this.codeAnalyzer.analyze(pkgNode);
    }

    private BLangPackage taintAnalyze(BLangPackage pkgNode) {
        return this.taintAnalyzer.analyze(pkgNode);
    }

    private BLangPackage annotationProcess(BLangPackage pkgNode) {
        return this.compilerPluginRunner.runPlugins(pkgNode);
    }

    public BLangPackage desugar(BLangPackage pkgNode) {
        return this.desugar.perform(pkgNode);
    }

    public BLangPackage codegen(BLangPackage pkgNode) {
        return this.codeGenerator.generateBALO(pkgNode);
    }

    private CompilerPhase getCompilerPhase() {
        String phaseName = options.get(CompilerOptionName.COMPILER_PHASE);
        if (phaseName == null || phaseName.isEmpty()) {
            return CompilerPhase.CODE_GEN;
        }

        return CompilerPhase.fromValue(phaseName);
    }

    private boolean stopCompilation(BLangPackage pkgNode, CompilerPhase nextPhase) {
        if (compilerPhase.compareTo(nextPhase) < 0) {
            return true;
        }

        return (nextPhase == CompilerPhase.TAINT_ANALYZE ||
                nextPhase == CompilerPhase.COMPILER_PLUGIN ||
                nextPhase == CompilerPhase.DESUGAR)
                && (dlog.errorCount > 0 || pkgNode.getCompilationUnits().isEmpty());
    }

    private BLangPackage getBuiltInPackage() {
        return codegen(desugar(taintAnalyze(codeAnalyze(semAnalyzer.analyze(
                pkgLoader.loadAndDefinePackage(SymbolTable.BUILTIN))))));
    }

    private void updateErrorSymbol() {
        BSymbol errorStructSymbol = symResolver
                .lookupSymbol(symbolTable.pkgEnvMap.get(symbolTable.builtInPackageSymbol), Names.ERROR, SymTag.RECORD);
        symbolTable.errStructType.fields = ((BStructureType) errorStructSymbol.type).fields;

        // change type of the 'cause' field
        BField cause = symbolTable.errStructType.fields.get(1);
        BUnionType causeType = (BUnionType) cause.type;
        Set<BType> memberTypes = new HashSet<BType>() {{
            add(symbolTable.errStructType);
            add(symbolTable.nilType);
        }};
        BType newCauseType = new BUnionType(causeType.tsymbol, memberTypes, true);
        cause.type = newCauseType;
        cause.symbol.type = newCauseType;

        // change the type of the error symbol
        errorStructSymbol.type = symbolTable.errStructType;
        errorStructSymbol.type.tsymbol = (BTypeSymbol) errorStructSymbol;
    }
}
