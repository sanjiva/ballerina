/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.ballerinalang.model.TreeBuilder;
import org.ballerinalang.model.elements.DocAttachment;
import org.ballerinalang.model.elements.DocTag;
import org.ballerinalang.model.elements.Flag;
import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.model.symbols.SymbolKind;
import org.ballerinalang.model.tree.IdentifierNode;
import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.model.tree.TopLevelNode;
import org.ballerinalang.model.tree.statements.StatementNode;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.util.diagnostic.DiagnosticCode;
import org.wso2.ballerinalang.compiler.PackageCache;
import org.wso2.ballerinalang.compiler.PackageLoader;
import org.wso2.ballerinalang.compiler.desugar.ASTBuilderUtil;
import org.wso2.ballerinalang.compiler.semantics.model.Scope;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolEnv;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolTable;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BAnnotationAttributeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BAnnotationSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BAttachedFunction;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BEndpointVarSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BInvokableSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BObjectTypeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BPackageSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BRecordTypeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BServiceSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BTypeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BVarSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BXMLAttributeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BXMLNSSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.SymTag;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.Symbols;
import org.wso2.ballerinalang.compiler.semantics.model.types.BAnnotationType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BEnumType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BField;
import org.wso2.ballerinalang.compiler.semantics.model.types.BInvokableType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BRecordType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BServiceType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BStructureType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.tree.BLangAction;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotAttribute;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotation;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotationAttachment;
import org.wso2.ballerinalang.compiler.tree.BLangCompilationUnit;
import org.wso2.ballerinalang.compiler.tree.BLangDocumentation;
import org.wso2.ballerinalang.compiler.tree.BLangEndpoint;
import org.wso2.ballerinalang.compiler.tree.BLangEnum;
import org.wso2.ballerinalang.compiler.tree.BLangEnum.BLangEnumerator;
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
import org.wso2.ballerinalang.compiler.tree.expressions.BLangExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangSimpleVarRef;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLAttribute;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLQName;
import org.wso2.ballerinalang.compiler.tree.statements.BLangAssignment;
import org.wso2.ballerinalang.compiler.tree.statements.BLangBlockStmt;
import org.wso2.ballerinalang.compiler.tree.statements.BLangReturn;
import org.wso2.ballerinalang.compiler.tree.statements.BLangScope;
import org.wso2.ballerinalang.compiler.tree.statements.BLangStatement;
import org.wso2.ballerinalang.compiler.tree.statements.BLangVariableDef;
import org.wso2.ballerinalang.compiler.tree.statements.BLangXMLNSStatement;
import org.wso2.ballerinalang.compiler.tree.types.BLangObjectTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangRecordTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangStructureTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangUserDefinedType;
import org.wso2.ballerinalang.compiler.tree.types.BLangValueType;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.Name;
import org.wso2.ballerinalang.compiler.util.Names;
import org.wso2.ballerinalang.compiler.util.TypeTags;
import org.wso2.ballerinalang.compiler.util.diagnotic.BLangDiagnosticLog;
import org.wso2.ballerinalang.compiler.util.diagnotic.DiagnosticPos;
import org.wso2.ballerinalang.util.AttachPoints;
import org.wso2.ballerinalang.util.Flags;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;

import static org.ballerinalang.model.tree.NodeKind.IMPORT;

/**
 * @since 0.94
 */
public class SymbolEnter extends BLangNodeVisitor {

    private static final CompilerContext.Key<SymbolEnter> SYMBOL_ENTER_KEY =
            new CompilerContext.Key<>();

    private final PackageLoader pkgLoader;
    private final SymbolTable symTable;
    private final PackageCache packageCache;
    private final Names names;
    private final SymbolResolver symResolver;
    private final BLangDiagnosticLog dlog;
    private final EndpointSPIAnalyzer endpointSPIAnalyzer;
    private final Types types;
    private List<BLangTypeDefinition> unresolvedTypes;
    private int typePrecedence;

    private SymbolEnv env;

    public static SymbolEnter getInstance(CompilerContext context) {
        SymbolEnter symbolEnter = context.get(SYMBOL_ENTER_KEY);
        if (symbolEnter == null) {
            symbolEnter = new SymbolEnter(context);
        }

        return symbolEnter;
    }

    public SymbolEnter(CompilerContext context) {
        context.put(SYMBOL_ENTER_KEY, this);

        this.pkgLoader = PackageLoader.getInstance(context);
        this.symTable = SymbolTable.getInstance(context);
        this.packageCache = PackageCache.getInstance(context);
        this.names = Names.getInstance(context);
        this.symResolver = SymbolResolver.getInstance(context);
        this.endpointSPIAnalyzer = EndpointSPIAnalyzer.getInstance(context);
        this.dlog = BLangDiagnosticLog.getInstance(context);
        this.types = Types.getInstance(context);
    }

    public BLangPackage definePackage(BLangPackage pkgNode) {
        populatePackageNode(pkgNode);
        defineNode(pkgNode, null);
        return pkgNode;
    }

    public void defineNode(BLangNode node, SymbolEnv env) {
        SymbolEnv prevEnv = this.env;
        this.env = env;
        node.accept(this);
        this.env = prevEnv;
    }


    // Visitor methods

    @Override
    public void visit(BLangPackage pkgNode) {
        if (pkgNode.completedPhases.contains(CompilerPhase.DEFINE)) {
            return;
        }

        // Create PackageSymbol
        BPackageSymbol pkgSymbol = Symbols.createPackageSymbol(pkgNode.packageID, this.symTable);
        pkgNode.symbol = pkgSymbol;
        SymbolEnv builtinEnv = this.symTable.pkgEnvMap.get(symTable.builtInPackageSymbol);
        SymbolEnv pkgEnv = SymbolEnv.createPkgEnv(pkgNode, pkgSymbol.scope, builtinEnv);
        this.symTable.pkgEnvMap.put(pkgSymbol, pkgEnv);

        createPackageInitFunctions(pkgNode);
        // visit the package node recursively and define all package level symbols.
        // And maintain a list of created package symbols.
        pkgNode.imports.forEach(importNode -> defineNode(importNode, pkgEnv));

        // Define type definitions.
        this.typePrecedence = 0;
        defineTypeNodes(pkgNode.typeDefinitions, pkgEnv);

        pkgNode.globalVars.forEach(var -> defineNode(var, pkgEnv));

        // Enabled logging errors after type def visit.
        // TODO: Do this in a cleaner way
        pkgEnv.logErrors = true;

        // Define type def fields (if any)
        defineFields(pkgNode.typeDefinitions, pkgEnv);

        // Define type def members (if any)
        defineMembers(pkgNode.typeDefinitions, pkgEnv);

        // Define service and resource nodes.
        pkgNode.services.forEach(service -> defineNode(service, pkgEnv));

        // Define function nodes.
        pkgNode.functions.forEach(func -> defineNode(func, pkgEnv));

        // Define service resource nodes.
        defineServiceMembers(pkgNode.services, pkgEnv);

        // Define annotation nodes.
        pkgNode.annotations.forEach(annot -> defineNode(annot, pkgEnv));

        resolveAnnotationAttributeTypes(pkgNode.annotations, pkgEnv);

        pkgNode.globalEndpoints.forEach(ep -> defineNode(ep, pkgEnv));

        definePackageInitFunctions(pkgNode, pkgEnv);
        pkgNode.completedPhases.add(CompilerPhase.DEFINE);
    }

    @Deprecated
    private void resolveAnnotationAttributeTypes(List<BLangAnnotation> annotations, SymbolEnv pkgEnv) {
        annotations.forEach(annotation -> {
            annotation.attributes.forEach(attribute -> {
                SymbolEnv annotationEnv = SymbolEnv.createAnnotationEnv(annotation, annotation.symbol.scope, pkgEnv);
                BType actualType = this.symResolver.resolveTypeNode(attribute.typeNode, annotationEnv);
                attribute.symbol.type = actualType;
            });
        });
    }

    public void visit(BLangAnnotation annotationNode) {
        BSymbol annotationSymbol = Symbols.createAnnotationSymbol(Flags.asMask(annotationNode.flagSet),
                AttachPoints.asMask(annotationNode.attachPoints), names.fromIdNode(annotationNode.name),
                env.enclPkg.symbol.pkgID, null, env.scope.owner);
        annotationSymbol.documentation = getDocAttachment(annotationNode.docAttachments);
        annotationSymbol.type = new BAnnotationType((BAnnotationSymbol) annotationSymbol);
        annotationNode.symbol = annotationSymbol;
        defineSymbol(annotationNode.name.pos, annotationSymbol);
        SymbolEnv annotationEnv = SymbolEnv.createAnnotationEnv(annotationNode, annotationSymbol.scope, env);
        annotationNode.attributes.forEach(att -> this.defineNode(att, annotationEnv));
        if (annotationNode.typeNode != null) {
            BType structType = this.symResolver.resolveTypeNode(annotationNode.typeNode, annotationEnv);
            ((BAnnotationSymbol) annotationSymbol).attachedType = structType.tsymbol;
        }
    }

    public void visit(BLangAnnotAttribute annotationAttribute) {
        BAnnotationAttributeSymbol annotationAttributeSymbol = Symbols.createAnnotationAttributeSymbol(names.
                        fromIdNode(annotationAttribute.name), env.enclPkg.symbol.pkgID,
                null, env.scope.owner);
        annotationAttributeSymbol.docTag = DocTag.FIELD;
        annotationAttributeSymbol.expr = annotationAttribute.expr;
        annotationAttribute.symbol = annotationAttributeSymbol;
        ((BAnnotationSymbol) env.scope.owner).attributes.add(annotationAttributeSymbol);
        defineSymbol(annotationAttribute.pos, annotationAttributeSymbol);
    }

    @Override
    public void visit(BLangImportPackage importPkgNode) {
        Name pkgAlias = names.fromIdNode(importPkgNode.alias);
        if (symResolver.lookupSymbol(env, pkgAlias, SymTag.IMPORT) != symTable.notFoundSymbol) {
            dlog.error(importPkgNode.pos, DiagnosticCode.REDECLARED_SYMBOL, pkgAlias);
            return;
        }

        // TODO Clean this code up. Can we move the this to BLangPackageBuilder class
        // Create import package symbol
        Name orgName;
        if (importPkgNode.orgName.value == null || importPkgNode.orgName.value.isEmpty()) {
            // means it's in 'import <pkg-name>' style
            orgName = env.enclPkg.packageID.orgName;
        } else {
            // means it's in 'import <org-name>/<pkg-name>' style
            orgName = names.fromIdNode(importPkgNode.orgName);
        }
        List<Name> nameComps = importPkgNode.pkgNameComps.stream()
                .map(identifier -> names.fromIdNode(identifier))
                .collect(Collectors.toList());

        String version = names.fromIdNode(importPkgNode.version).getValue();
        PackageID pkgId = new PackageID(orgName, nameComps, new Name(version));
        if (pkgId.name.getValue().startsWith(Names.BUILTIN_PACKAGE.value)) {
            dlog.error(importPkgNode.pos, DiagnosticCode.PACKAGE_NOT_FOUND,
                    importPkgNode.getQualifiedPackageName());
            return;
        }

        BPackageSymbol pkgSymbol = pkgLoader.loadPackageSymbol(pkgId, env.enclPkg.packageID,
                env.enclPkg.packageRepository);
        if (pkgSymbol == null) {
            dlog.error(importPkgNode.pos, DiagnosticCode.PACKAGE_NOT_FOUND,
                    importPkgNode.getQualifiedPackageName());
            return;
        }

        // define the import package symbol in the current package scope
        importPkgNode.symbol = pkgSymbol;
        ((BPackageSymbol) this.env.scope.owner).imports.add(pkgSymbol);
        this.env.scope.define(pkgAlias, pkgSymbol);
    }

    @Override
    public void visit(BLangXMLNS xmlnsNode) {
        String nsURI = (String) ((BLangLiteral) xmlnsNode.namespaceURI).value;

        if (xmlnsNode.prefix.value != null && nsURI.isEmpty()) {
            dlog.error(xmlnsNode.pos, DiagnosticCode.INVALID_NAMESPACE_DECLARATION, xmlnsNode.prefix);
        }

        // set the prefix of the default namespace
        if (xmlnsNode.prefix.value == null) {
            xmlnsNode.prefix.value = XMLConstants.DEFAULT_NS_PREFIX;
        }

        BXMLNSSymbol xmlnsSymbol = Symbols.createXMLNSSymbol(names.fromIdNode(xmlnsNode.prefix), nsURI,
                env.enclPkg.symbol.pkgID, env.scope.owner);
        xmlnsNode.symbol = xmlnsSymbol;

        // First check for package-imports with the same alias.
        // Here we do not check for owner equality, since package import is always at the package
        // level, but the namespace declaration can be at any level.
        BSymbol foundSym = symResolver.lookupSymbol(env, xmlnsSymbol.name, SymTag.PACKAGE);
        if (foundSym != symTable.notFoundSymbol) {
            dlog.error(xmlnsNode.pos, DiagnosticCode.REDECLARED_SYMBOL, xmlnsSymbol.name);
            return;
        }

        // Define it in the enclosing scope. Here we check for the owner equality,
        // to support overriding of namespace declarations defined at package level.
        defineSymbol(xmlnsNode.pos, xmlnsSymbol);
    }

    public void visit(BLangXMLNSStatement xmlnsStmtNode) {
        defineNode(xmlnsStmtNode.xmlnsDecl, env);
    }

    private void defineTypeNodes(List<BLangTypeDefinition> typeDefs, SymbolEnv env) {
        if (typeDefs.size() == 0) {
            return;
        }
        this.unresolvedTypes = new ArrayList<>();
        for (BLangTypeDefinition typeDef : typeDefs) {
            defineNode(typeDef, env);
        }
        if (typeDefs.size() <= unresolvedTypes.size()) {
            dlog.error(typeDefs.get(0).pos, DiagnosticCode.CYCLIC_TYPE_REFERENCE, unresolvedTypes);
            return;
        }
        defineTypeNodes(unresolvedTypes, env);
    }

    @Override
    public void visit(BLangTypeDefinition typeDefinition) {
        BType definedType = symResolver.resolveTypeNode(typeDefinition.typeNode, env);
        if (definedType == symTable.noType) {
            this.unresolvedTypes.add(typeDefinition);
            return;
        }
        typeDefinition.precedence = this.typePrecedence++;
        BTypeSymbol typeDefSymbol;
        if (definedType.tsymbol.name != Names.EMPTY) {
            typeDefSymbol = definedType.tsymbol.createLabelSymbol();
        } else {
            typeDefSymbol = definedType.tsymbol;
        }
        typeDefSymbol.documentation = getDocAttachment(typeDefinition.docAttachments);
        typeDefSymbol.name = names.fromIdNode(typeDefinition.getName());
        typeDefSymbol.pkgID = env.enclPkg.packageID;
        typeDefSymbol.flags = Flags.asMask(typeDefinition.flagSet);

        typeDefinition.symbol = typeDefSymbol;

        defineSymbol(typeDefinition.name.pos, typeDefSymbol);
    }

    @Override
    public void visit(BLangEnum enumNode) {
        BTypeSymbol enumSymbol = Symbols.createEnumSymbol(Flags.asMask(enumNode.flagSet),
                names.fromIdNode(enumNode.name), env.enclPkg.symbol.pkgID, null, env.scope.owner);
        enumSymbol.documentation = getDocAttachment(enumNode.docAttachments);
        enumNode.symbol = enumSymbol;
        defineSymbol(enumNode.name.pos, enumSymbol);

        BEnumType enumType = new BEnumType(enumSymbol, null);
        enumSymbol.type = enumType;

        SymbolEnv enumEnv = SymbolEnv.createPkgLevelSymbolEnv(enumNode, enumSymbol.scope, this.env);
        for (int i = 0; i < enumNode.enumerators.size(); i++) {
            BLangEnumerator enumerator = enumNode.enumerators.get(i);
            BVarSymbol enumeratorSymbol = new BVarSymbol(Flags.PUBLIC,
                    names.fromIdNode(enumerator.name), enumSymbol.pkgID, enumType, enumSymbol);
            enumeratorSymbol.docTag = DocTag.FIELD;
            enumerator.symbol = enumeratorSymbol;

            if (symResolver.checkForUniqueSymbol(enumerator.pos, enumEnv, enumeratorSymbol, enumeratorSymbol.tag)) {
                enumEnv.scope.define(enumeratorSymbol.name, enumeratorSymbol);
            }
        }
    }

    @Override
    public void visit(BLangWorker workerNode) {
        BInvokableSymbol workerSymbol = Symbols.createWorkerSymbol(Flags.asMask(workerNode.flagSet),
                names.fromIdNode(workerNode.name), env.enclPkg.symbol.pkgID, null, env.scope.owner);
        workerSymbol.documentation = getDocAttachment(workerNode.docAttachments);
        workerNode.symbol = workerSymbol;
        defineSymbolWithCurrentEnvOwner(workerNode.pos, workerSymbol);
    }

    @Override
    public void visit(BLangService serviceNode) {
        BServiceSymbol serviceSymbol = Symbols.createServiceSymbol(Flags.asMask(serviceNode.flagSet),
                names.fromIdNode(serviceNode.name), env.enclPkg.symbol.pkgID, serviceNode.type, env.scope.owner);
        serviceSymbol.documentation = getDocAttachment(serviceNode.docAttachments);
        serviceNode.symbol = serviceSymbol;
        serviceNode.symbol.type = new BServiceType(serviceSymbol);
        defineSymbol(serviceNode.name.pos, serviceSymbol);
    }

    @Override
    public void visit(BLangFunction funcNode) {
        boolean validAttachedFunc = validateFuncReceiver(funcNode);
        if (funcNode.attachedOuterFunction) {
            if (Symbols.isFlagOn(Flags.asMask(funcNode.flagSet), Flags.PUBLIC)) { //no visibility modifiers allowed
                dlog.error(funcNode.pos, DiagnosticCode.ATTACHED_FUNC_CANT_HAVE_VISIBILITY_MODIFIERS, funcNode.name);
            }
            if (funcNode.receiver.type.tsymbol.kind == SymbolKind.RECORD) {
                dlog.error(funcNode.pos, DiagnosticCode.CANNOT_ATTACH_FUNCTIONS_TO_RECORDS, funcNode.name,
                        funcNode.receiver.type.tsymbol.name);
                createDummyFunctionSymbol(funcNode);
                visitObjectAttachedFunction(funcNode);
                return;
            }
            SymbolEnv objectEnv = SymbolEnv.createTypeDefEnv(null, funcNode.receiver.type.
                    tsymbol.scope, env);
            BSymbol funcSymbol = symResolver.lookupSymbol(objectEnv, getFuncSymbolName(funcNode), SymTag.FUNCTION);
            if (funcSymbol == symTable.notFoundSymbol) {
                dlog.error(funcNode.pos, DiagnosticCode.CANNOT_FIND_MATCHING_FUNCTION, funcNode.name,
                        funcNode.receiver.type.tsymbol.name);
                createDummyFunctionSymbol(funcNode);
                visitObjectAttachedFunction(funcNode);
                return;
            }
            funcNode.symbol = (BInvokableSymbol) funcSymbol;
            if (funcNode.symbol.bodyExist) {
                dlog.error(funcNode.pos, DiagnosticCode.IMPLEMENTATION_ALREADY_EXIST, funcNode.name);
            }
            validateAttachedFunction(funcNode, funcNode.receiver.type.tsymbol.name);
            visitObjectAttachedFunction(funcNode);
            return;
        }
        BInvokableSymbol funcSymbol = Symbols.createFunctionSymbol(Flags.asMask(funcNode.flagSet),
                getFuncSymbolName(funcNode), env.enclPkg.symbol.pkgID, null, env.scope.owner, funcNode.body != null);
        funcSymbol.documentation = getDocAttachment(funcNode.docAttachments);
        SymbolEnv invokableEnv = SymbolEnv.createFunctionEnv(funcNode, funcSymbol.scope, env);
        defineInvokableSymbol(funcNode, funcSymbol, invokableEnv);
        // Define function receiver if any.
        if (funcNode.receiver != null) {
            defineAttachedFunctions(funcNode, funcSymbol, invokableEnv, validAttachedFunc);
        }
    }

    private void createDummyFunctionSymbol(BLangFunction funcNode) {
        // This is only to keep the flow running so that at the end there will be proper semantic errors
        funcNode.symbol = Symbols.createFunctionSymbol(Flags.asMask(funcNode.flagSet),
                getFuncSymbolName(funcNode), env.enclPkg.symbol.pkgID, null, env.scope.owner, true);
        funcNode.symbol.scope = new Scope(funcNode.symbol);
    }

    private void visitObjectAttachedFunction(BLangFunction funcNode) {
        //TODO check function parameters and return types
        SymbolEnv invokableEnv = SymbolEnv.createFunctionEnv(funcNode, funcNode.symbol.scope, env);

        invokableEnv.scope = funcNode.symbol.scope;
        defineObjectAttachedInvokableSymbolParams(funcNode, invokableEnv);

        if (env.enclPkg.objAttachedFunctions.contains(funcNode.symbol)) {
            dlog.error(funcNode.pos, DiagnosticCode.IMPLEMENTATION_ALREADY_EXIST, funcNode.name);
            return;
        }

        env.enclPkg.objAttachedFunctions.add(funcNode.symbol);

        funcNode.receiver.symbol = funcNode.symbol.receiverSymbol;
    }

    private void validateAttachedFunction(BLangFunction funcNode, Name objName) {
        SymbolEnv invokableEnv = SymbolEnv.createDummyEnv(funcNode, funcNode.symbol.scope, env);
        List<BType> paramTypes = funcNode.requiredParams.stream()
                .peek(varNode -> varNode.type = symResolver.resolveTypeNode(varNode.typeNode, invokableEnv))
                .map(varNode -> varNode.type)
                .collect(Collectors.toList());

        funcNode.defaultableParams.forEach(p -> paramTypes.add(symResolver
                .resolveTypeNode(p.var.typeNode, invokableEnv)));

        if (!funcNode.desugaredReturnType) {
            symResolver.resolveTypeNode(funcNode.returnTypeNode, invokableEnv);
        }

        if (funcNode.restParam != null) {
            if (!funcNode.symbol.restParam.name.equals(names.fromIdNode(funcNode.restParam.name))) {
                dlog.error(funcNode.pos, DiagnosticCode.CANNOT_FIND_MATCHING_INTERFACE, funcNode.name, objName);
                return;
            }
            BType restParamType = symResolver.resolveTypeNode(funcNode.restParam.typeNode, invokableEnv);
            paramTypes.add(restParamType);
        }

        BInvokableType sourceType = (BInvokableType) funcNode.symbol.type;
        //this was used earlier to one to one match object declaration with definitions for attached functions
        // keeping this commented as we may need uncomment this later.
        //        int flags = Flags.asMask(funcNode.flagSet);
        //        if (((flags & Flags.NATIVE) != (funcNode.symbol.flags & Flags.NATIVE))
        //                || ((flags & Flags.PUBLIC) != (funcNode.symbol.flags & Flags.PUBLIC))) {
        //            dlog.error(funcNode.pos, DiagnosticCode.CANNOT_FIND_MATCHING_INTERFACE, funcNode.name, objName);
        //            return;
        //        }

        if (typesMissMatch(paramTypes, sourceType.paramTypes)
                || namesMissMatch(funcNode.requiredParams, funcNode.symbol.params)
                || namesMissMatchDef(funcNode.defaultableParams, funcNode.symbol.defaultableParams)) {
            dlog.error(funcNode.pos, DiagnosticCode.CANNOT_FIND_MATCHING_INTERFACE, funcNode.name, objName);
            return;
        }

        if (funcNode.returnTypeNode.type == null && sourceType.retType == null) {
            return;
        } else if (funcNode.returnTypeNode.type == null || sourceType.retType == null) {
            dlog.error(funcNode.pos, DiagnosticCode.CANNOT_FIND_MATCHING_INTERFACE, funcNode.name, objName);
            return;
        }

        if (funcNode.returnTypeNode.type.tag != sourceType.retType.tag) {
            dlog.error(funcNode.pos, DiagnosticCode.CANNOT_FIND_MATCHING_INTERFACE, funcNode.name, objName);
            return;
        }
        //Reset symbol flags to remove interface flag
        funcNode.symbol.flags = funcNode.symbol.flags ^ Flags.INTERFACE;
    }

    private boolean typesMissMatch(List<BType> lhs, List<BType> rhs) {
        if (lhs.size() != rhs.size()) {
            return true;
        }

        for (int i = 0; i < lhs.size(); i++) {
            if (!types.isSameType(lhs.get(i), rhs.get(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean namesMissMatch(List<BLangVariable> lhs, List<BVarSymbol> rhs) {
        if (lhs.size() != rhs.size()) {
            return true;
        }

        for (int i = 0; i < lhs.size(); i++) {
            if (!rhs.get(i).name.equals(names.fromIdNode(lhs.get(i).name))) {
                return true;
            }
        }
        return false;
    }

    private boolean namesMissMatchDef(List<BLangVariableDef> lhs, List<BVarSymbol> rhs) {
        if (lhs.size() != rhs.size()) {
            return true;
        }

        for (int i = 0; i < lhs.size(); i++) {
            if (!rhs.get(i).name.equals(names.fromIdNode(lhs.get(i).var.name))) {
                return true;
            }
        }
        return false;
    }

    private void defineObjectAttachedInvokableSymbolParams(BLangInvokableNode invokableNode, SymbolEnv invokableEnv) {
        // visit required params of the function
        invokableNode.requiredParams.forEach(varNode -> {
            visitObjectAttachedFunctionParam(varNode, invokableEnv);
        });

        invokableNode.defaultableParams.forEach(varDefNode -> {
            visitObjectAttachedFunctionParam(varDefNode.var, invokableEnv);
        });

        if (invokableNode.returnTypeNode != null) {
            invokableNode.returnTypeNode.type = symResolver.resolveTypeNode(invokableNode.returnTypeNode, env);
        }

        if (invokableNode.restParam != null) {
            visitObjectAttachedFunctionParam(invokableNode.restParam, invokableEnv);
        }
    }

    void visitObjectAttachedFunctionParam(BLangVariable variable, SymbolEnv invokableEnv) {
        // assign the type to var type node
        if (variable.type == null) {
            variable.type = symResolver.resolveTypeNode(variable.typeNode, env);
        }

        visitObjectAttachedFunctionParamSymbol(variable, invokableEnv);
    }

    void visitObjectAttachedFunctionParamSymbol(BLangVariable variable, SymbolEnv invokableEnv) {
        BSymbol varSymbol = symResolver.lookupSymbol(invokableEnv, names.fromIdNode(variable.name),
                SymTag.VARIABLE);
        if (varSymbol == symTable.notFoundSymbol) {
            defineNode(variable, invokableEnv);
        } else {
            variable.symbol = (BVarSymbol) varSymbol;
        }
        if (variable.expr == null) {
            return;
        }
        if (variable.expr.getKind() != NodeKind.LITERAL) {
            this.dlog.error(variable.expr.pos, DiagnosticCode.INVALID_DEFAULT_PARAM_VALUE, variable.name);
            return;
        }
        BLangLiteral literal = (BLangLiteral) variable.expr;
        variable.symbol.defaultValue = literal.value;
    }

    @Override
    public void visit(BLangAction actionNode) {
        BInvokableSymbol actionSymbol = Symbols
                .createActionSymbol(Flags.asMask(actionNode.flagSet), names.fromIdNode(actionNode.name),
                        env.enclPkg.symbol.pkgID, null, env.scope.owner);
        actionSymbol.documentation = getDocAttachment(actionNode.docAttachments);
        SymbolEnv invokableEnv = SymbolEnv.createResourceActionSymbolEnv(actionNode, actionSymbol.scope, env);
        defineInvokableSymbol(actionNode, actionSymbol, invokableEnv);
        actionNode.endpoints.forEach(ep -> defineNode(ep, invokableEnv));
    }

    @Override
    public void visit(BLangResource resourceNode) {
        BInvokableSymbol resourceSymbol = Symbols
                .createResourceSymbol(Flags.asMask(resourceNode.flagSet), names.fromIdNode(resourceNode.name),
                        env.enclPkg.symbol.pkgID, null, env.scope.owner);
        resourceSymbol.documentation = getDocAttachment(resourceNode.docAttachments);
        SymbolEnv invokableEnv = SymbolEnv.createResourceActionSymbolEnv(resourceNode, resourceSymbol.scope, env);
        if (!resourceNode.getParameters().isEmpty()
                && resourceNode.getParameters().get(0) != null
                && resourceNode.getParameters().get(0).typeNode == null) {
            // This is endpoint variable. Setting temporary type for now till we find actual type at semantic phase.
            resourceNode.getParameters().get(0).type = symTable.endpointType;
        }
        defineInvokableSymbol(resourceNode, resourceSymbol, invokableEnv);
    }

    @Override
    public void visit(BLangVariable varNode) {
        // this is a field variable defined for object init function
        if (varNode.isField) {
            Name varName = names.fromIdNode(varNode.name);
            BSymbol symbol;
            if (env.enclTypeDefinition != null) {
                symbol = symResolver.resolveObjectField(varNode.pos, env, varName,
                        env.enclTypeDefinition.symbol.type.tsymbol);
            } else {
                symbol = symResolver.lookupSymbol(env, varName, SymTag.VARIABLE);
            }

            if (symbol == symTable.notFoundSymbol) {
                dlog.error(varNode.pos, DiagnosticCode.UNDEFINED_STRUCTURE_FIELD, varName,
                        env.enclTypeDefinition.symbol.type.getKind().typeName(), env.enclTypeDefinition.name);
            }
            varNode.type = symbol.type;
            Name updatedVarName = varName;
            if (((BLangFunction) env.enclInvokable).receiver != null) {
               updatedVarName = getFieldSymbolName(((BLangFunction) env.enclInvokable).receiver, varNode);
            }
            BVarSymbol varSymbol = defineVarSymbol(varNode.pos, varNode.flagSet, varNode.type, updatedVarName, env);

            // Reset the name of the symbol to the original var name
            varSymbol.name = varName;

            // This means enclosing type definition is a object type defintion
            if (env.enclTypeDefinition != null) {
                BLangObjectTypeNode objectTypeNode = (BLangObjectTypeNode) env.enclTypeDefinition.typeNode;
                objectTypeNode.initFunction.initFunctionStmts
                        .put(symbol, (BLangStatement) createAssignmentStmt(varNode, varSymbol, symbol));
            }
            varSymbol.docTag = varNode.docTag;
            varNode.symbol = varSymbol;
            return;
        }

        // assign the type to var type node
        if (varNode.type == null) {
            varNode.type = symResolver.resolveTypeNode(varNode.typeNode, env);
        }

        Name varName = names.fromIdNode(varNode.name);
        if (varName == Names.EMPTY || varName == Names.IGNORE) {
            // This is a variable created for a return type
            // e.g. function foo() (int);
            return;
        }

        //Check annotations attached to the variable
        if (varNode.annAttachments.size() > 0) {
            if (hasAnnotation(varNode.annAttachments, Names.ANNOTATION_FINAL.getValue())) {
                varNode.flagSet.add(Flag.FINAL);
            }
            if (hasAnnotation(varNode.annAttachments, Names.ANNOTATION_READONLY.getValue())) {
                varNode.flagSet.add(Flag.READONLY);
            }
        }

        BVarSymbol varSymbol = defineVarSymbol(varNode.pos, varNode.flagSet,
                varNode.type, varName, env);
        varSymbol.documentation = getDocAttachment(varNode.docAttachments);
        varSymbol.docTag = varNode.docTag;
        varNode.symbol = varSymbol;
    }

    @Override
    public void visit(BLangEndpoint endpoint) {
        BType varType = symResolver.resolveTypeNode(endpoint.endpointTypeNode, env);
        Name varName = names.fromIdNode(endpoint.name);
        endpoint.type = varType;
        endpoint.symbol = defineEndpointVarSymbol(endpoint.pos, endpoint.flagSet, varType, varName, env);
        endpointSPIAnalyzer.resolveEndpointSymbol(endpoint);
    }

    public void visit(BLangXMLAttribute bLangXMLAttribute) {
        if (!(bLangXMLAttribute.name.getKind() == NodeKind.XML_QNAME)) {
            return;
        }

        BLangXMLQName qname = (BLangXMLQName) bLangXMLAttribute.name;

        // If the attribute is not an in-line namespace declaration, check for duplicate attributes.
        // If no duplicates, then define this attribute symbol.
        if (!bLangXMLAttribute.isNamespaceDeclr) {
            BXMLAttributeSymbol attrSymbol = new BXMLAttributeSymbol(qname.localname.value, qname.namespaceURI,
                    env.enclPkg.symbol.pkgID, env.scope.owner);
            if (symResolver.checkForUniqueMemberSymbol(bLangXMLAttribute.pos, env, attrSymbol)) {
                env.scope.define(attrSymbol.name, attrSymbol);
                bLangXMLAttribute.symbol = attrSymbol;
            }
            return;
        }

        List<BLangExpression> exprs = bLangXMLAttribute.value.textFragments;
        String nsURI = null;

        // We reach here if the attribute is an in-line namesapce declaration.
        // Get the namespace URI, only if it is statically defined. Then define the namespace symbol.
        // This namespace URI is later used by the attributes, when they lookup for duplicate attributes.
        // TODO: find a better way to get the statically defined URI.
        if (exprs.size() == 1 && exprs.get(0).getKind() == NodeKind.LITERAL) {
            nsURI = (String) ((BLangLiteral) exprs.get(0)).value;
        }

        String symbolName = qname.localname.value;
        if (symbolName.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            symbolName = XMLConstants.DEFAULT_NS_PREFIX;
        }
        BXMLNSSymbol xmlnsSymbol =
                new BXMLNSSymbol(names.fromString(symbolName), nsURI, env.enclPkg.symbol.pkgID, env.scope.owner);
        if (symResolver.checkForUniqueMemberSymbol(bLangXMLAttribute.pos, env, xmlnsSymbol)) {
            env.scope.define(xmlnsSymbol.name, xmlnsSymbol);
            bLangXMLAttribute.symbol = xmlnsSymbol;
        }
    }

    @Override
    public void visit(BLangScope scopeNode) {
        BTypeSymbol symbol = Symbols
                .createScopeSymbol(names.fromString(scopeNode.name.getValue()), env.enclPkg.symbol.pkgID,
                        scopeNode.type, env.scope.owner);
        scopeNode.type = symbol.type;
        env.scope.define(names.fromString(scopeNode.name.getValue()), symbol);
    }


    // Private methods

    private boolean hasAnnotation(List<BLangAnnotationAttachment> annotationAttachmentList, String expectedAnnotation) {
        return annotationAttachmentList.stream()
                .filter(annotation -> annotation.annotationName.value.equals(expectedAnnotation)).count() > 0;
    }

    /**
     * Visit each compilation unit (.bal file) and add each top-level node
     * in the compilation unit to the package node.
     *
     * @param pkgNode current package node
     */
    private void populatePackageNode(BLangPackage pkgNode) {
        List<BLangCompilationUnit> compUnits = pkgNode.getCompilationUnits();
        compUnits.forEach(compUnit -> populateCompilationUnit(pkgNode, compUnit));
    }

    /**
     * Visit each top-level node and add it to the package node.
     *
     * @param pkgNode  current package node
     * @param compUnit current compilation unit
     */
    private void populateCompilationUnit(BLangPackage pkgNode, BLangCompilationUnit compUnit) {
        compUnit.getTopLevelNodes().forEach(node -> addTopLevelNode(pkgNode, node));
    }

    private void addTopLevelNode(BLangPackage pkgNode, TopLevelNode node) {
        NodeKind kind = node.getKind();

        // Here we keep all the top-level nodes of a compilation unit (aka file) in exact same
        // order as they appear in the compilation unit. This list contains all the top-level
        // nodes of all the compilation units grouped by the compilation unit.
        // This allows other compiler phases to visit top-level nodes in the exact same order
        // as they appear in compilation units. This is required for error reporting.
        if (kind != NodeKind.PACKAGE_DECLARATION && kind != IMPORT) {
            pkgNode.topLevelNodes.add(node);
        }

        switch (kind) {
            case IMPORT:
                // TODO Verify the rules..
                // TODO Check whether the same package alias (if any) has been used for the same import
                // TODO The version of an import package can be specified only once for a package
                if (!pkgNode.imports.contains(node)) {
                    pkgNode.imports.add((BLangImportPackage) node);
                }
                break;
            case FUNCTION:
                pkgNode.functions.add((BLangFunction) node);
                break;
            case ENUM:
                pkgNode.enums.add((BLangEnum) node);
                break;
            case TYPE_DEFINITION:
                pkgNode.typeDefinitions.add((BLangTypeDefinition) node);
                break;
            case SERVICE:
                pkgNode.services.add((BLangService) node);
                break;
            case VARIABLE:
                pkgNode.globalVars.add((BLangVariable) node);
                // TODO There are two kinds of package level variables, constant and regular variables.
                break;
            case ANNOTATION:
                // TODO
                pkgNode.annotations.add((BLangAnnotation) node);
                break;
            case XMLNS:
                pkgNode.xmlnsList.add((BLangXMLNS) node);
                break;
            case ENDPOINT:
                pkgNode.globalEndpoints.add((BLangEndpoint) node);
                break;
        }
    }

    private void defineFields(List<BLangTypeDefinition> typeDefNodes, SymbolEnv pkgEnv) {
        for (BLangTypeDefinition typeDef : typeDefNodes) {
            if (typeDef.typeNode.getKind() == NodeKind.USER_DEFINED_TYPE ||
                    (typeDef.symbol.kind != SymbolKind.OBJECT && typeDef.symbol.kind != SymbolKind.RECORD)) {
                continue;
            }

            // Create typeDef type
            BStructureType structureType = (BStructureType) typeDef.symbol.type;
            BLangStructureTypeNode structureTypeNode = (BLangStructureTypeNode) typeDef.typeNode;
            SymbolEnv typeDefEnv = SymbolEnv.createTypeDefEnv(typeDef, typeDef.symbol.scope, pkgEnv);
            structureType.fields = structureTypeNode.fields.stream()
                    .peek(field -> defineNode(field, typeDefEnv))
                    .map(field -> new BField(names.fromIdNode(field.name),
                            field.symbol, field.expr != null))
                    .collect(Collectors.toList());

            if (typeDef.symbol.kind != SymbolKind.RECORD) {
                continue;
            }

            BLangRecordTypeNode recordTypeNode = (BLangRecordTypeNode) structureTypeNode;
            BRecordType recordType = (BRecordType) structureType;
            recordType.sealed = recordTypeNode.sealed;

            if (recordTypeNode.sealed && recordTypeNode.restFieldType != null) {
                dlog.error(recordTypeNode.restFieldType.pos, DiagnosticCode.REST_FIELD_NOT_ALLOWED_IN_SEALED_RECORDS);
                continue;
            }

            if (recordTypeNode.restFieldType == null) {
                if (recordTypeNode.sealed) {
                    recordType.restFieldType = symTable.noType;
                    continue;
                }
                recordType.restFieldType = symTable.anyType;
                continue;
            }

            recordType.restFieldType = symResolver.resolveTypeNode(recordTypeNode.restFieldType, typeDefEnv);
        }
    }

    private void defineMembers(List<BLangTypeDefinition> typeDefNodes, SymbolEnv pkgEnv) {
        for (BLangTypeDefinition typeDef : typeDefNodes) {
            if (typeDef.typeNode.getKind() == NodeKind.USER_DEFINED_TYPE) {
                continue;
            }
            if (typeDef.symbol.kind == SymbolKind.OBJECT) {
                BLangObjectTypeNode objTypeNode = (BLangObjectTypeNode) typeDef.typeNode;
                SymbolEnv objEnv = SymbolEnv.createTypeDefEnv(typeDef, typeDef.symbol.scope, pkgEnv);
                defineObjectInitFunction(objTypeNode, objEnv);
                objTypeNode.functions.forEach(f -> {
                    f.setReceiver(createReceiver(typeDef.pos, typeDef.symbol.type));
                    defineNode(f, objEnv);
                });
            } else if (typeDef.symbol.kind == SymbolKind.RECORD) {
                // Create typeDef type
                BLangRecordTypeNode recordTypeNode = (BLangRecordTypeNode) typeDef.typeNode;
                SymbolEnv typeDefEnv = SymbolEnv.createPkgLevelSymbolEnv(recordTypeNode, typeDef.symbol.scope, pkgEnv);
                defineRecordInitFunction(typeDef, typeDefEnv);
            }
        }
    }

    private void defineServiceMembers(List<BLangService> services, SymbolEnv pkgEnv) {
        services.forEach(service -> {
            SymbolEnv serviceEnv = SymbolEnv.createServiceEnv(service, service.symbol.scope, pkgEnv);
            service.nsDeclarations.forEach(xmlns -> defineNode(xmlns, serviceEnv));
            service.vars.forEach(varDef -> defineNode(varDef.var, serviceEnv));
            defineServiceInitFunction(service, serviceEnv);
            service.resources.stream()
                    .peek(action -> action.flagSet.add(Flag.PUBLIC))
                    .forEach(resource -> defineNode(resource, serviceEnv));
        });
    }

    private void defineInvokableSymbol(BLangInvokableNode invokableNode, BInvokableSymbol funcSymbol,
                                       SymbolEnv invokableEnv) {
        invokableNode.symbol = funcSymbol;
        defineSymbol(invokableNode.name.pos, funcSymbol);
        invokableEnv.scope = funcSymbol.scope;
        defineInvokableSymbolParams(invokableNode, funcSymbol, invokableEnv);
    }

    private void defineInvokableSymbolParams(BLangInvokableNode invokableNode, BInvokableSymbol invokableSymbol,
                                             SymbolEnv invokableEnv) {
        List<BVarSymbol> paramSymbols =
                invokableNode.requiredParams.stream()
                        .peek(varNode -> defineNode(varNode, invokableEnv))
                        .map(varNode -> varNode.symbol)
                        .collect(Collectors.toList());

        List<BVarSymbol> namedParamSymbols =
                invokableNode.defaultableParams.stream()
                        .peek(varDefNode -> defineNode(varDefNode.var, invokableEnv))
                        .map(varDefNode -> {
                            BVarSymbol varSymbol = varDefNode.var.symbol;
                            if (varDefNode.var.expr.getKind() != NodeKind.LITERAL) {
                                this.dlog.error(varDefNode.var.expr.pos, DiagnosticCode.INVALID_DEFAULT_PARAM_VALUE,
                                        varDefNode.var.name);
                            } else {
                                BLangLiteral literal = (BLangLiteral) varDefNode.var.expr;
                                varSymbol.defaultValue = literal.value;
                            }
                            return varSymbol;
                        })
                        .collect(Collectors.toList());

        if (!invokableNode.desugaredReturnType) {
            symResolver.resolveTypeNode(invokableNode.returnTypeNode, invokableEnv);
        }
        invokableSymbol.params = paramSymbols;
        invokableSymbol.retType = invokableNode.returnTypeNode.type;
        invokableSymbol.defaultableParams = namedParamSymbols;

        // Create function type
        List<BType> paramTypes = paramSymbols.stream()
                .map(paramSym -> paramSym.type)
                .collect(Collectors.toList());
        namedParamSymbols.forEach(paramSymbol -> paramTypes.add(paramSymbol.type));

        if (invokableNode.restParam != null) {
            defineNode(invokableNode.restParam, invokableEnv);
            invokableSymbol.restParam = invokableNode.restParam.symbol;
            paramTypes.add(invokableSymbol.restParam.type);
        }
        invokableSymbol.type = new BInvokableType(paramTypes, invokableNode.returnTypeNode.type, null);
    }

    private void defineSymbol(DiagnosticPos pos, BSymbol symbol) {
        symbol.scope = new Scope(symbol);
        if (symResolver.checkForUniqueSymbol(pos, env, symbol, symbol.tag)) {
            env.scope.define(symbol.name, symbol);
        }
    }

    private void defineSymbolWithCurrentEnvOwner(DiagnosticPos pos, BSymbol symbol) {
        symbol.scope = new Scope(env.scope.owner);
        if (symResolver.checkForUniqueSymbol(pos, env, symbol, symbol.tag)) {
            env.scope.define(symbol.name, symbol);
        }
    }

    public BVarSymbol defineVarSymbol(DiagnosticPos pos, Set<Flag> flagSet, BType varType, Name varName,
                                      SymbolEnv env) {
        // Create variable symbol
        Scope enclScope = env.scope;
        BVarSymbol varSymbol = createVarSymbol(flagSet, varType, varName, env);

        // Add it to the enclosing scope
        if (!symResolver.checkForUniqueSymbol(pos, env, varSymbol, SymTag.VARIABLE_NAME)) {
            varSymbol.type = symTable.errType;
        }
        enclScope.define(varSymbol.name, varSymbol);
        return varSymbol;
    }

    private BVarSymbol createVarSymbol(Set<Flag> flagSet, BType varType, Name varName, SymbolEnv env) {
        BVarSymbol varSymbol;
        if (varType.tag == TypeTags.INVOKABLE) {
            varSymbol = new BInvokableSymbol(SymTag.VARIABLE, Flags.asMask(flagSet), varName,
                    env.enclPkg.symbol.pkgID, varType, env.scope.owner);
        } else {
            varSymbol = new BVarSymbol(Flags.asMask(flagSet), varName,
                    env.enclPkg.symbol.pkgID, varType, env.scope.owner);
        }
        return varSymbol;
    }

    public BEndpointVarSymbol defineEndpointVarSymbol(DiagnosticPos pos, Set<Flag> flagSet, BType varType,
                                                      Name varName, SymbolEnv env) {
        // Create variable symbol
        Scope enclScope = env.scope;
        BEndpointVarSymbol varSymbol = new BEndpointVarSymbol(Flags.asMask(flagSet), varName, env.enclPkg.symbol
                .pkgID, varType, enclScope.owner);
        Scope.ScopeEntry scopeEntry = enclScope.entries.get(names.fromString("$" + varName.value));
        if (scopeEntry != null && scopeEntry.symbol != null && scopeEntry.symbol instanceof BVarSymbol) {
            varSymbol.docTag = ((BVarSymbol) scopeEntry.symbol).docTag;
        }

        // Add it to the enclosing scope
        // Find duplicates
        if (!symResolver.checkForUniqueSymbol(pos, env, varSymbol, SymTag.VARIABLE_NAME)) {
            varSymbol.type = symTable.errType;
        }
        enclScope.define(varSymbol.name, varSymbol);
        return varSymbol;
    }

    private void defineObjectInitFunction(BLangObjectTypeNode object, SymbolEnv conEnv) {
        BLangFunction initFunction = object.initFunction;
        if (initFunction == null) {
            initFunction = createInitFunction(object.pos, "", Names.OBJECT_INIT_SUFFIX);
        }

        initFunction.attachedFunction = true;

        //Set cached receiver to the init function
        initFunction.receiver = createReceiver(object.pos, object.type);

        initFunction.flagSet.add(Flag.ATTACHED);

        object.initFunction = initFunction;

        defineNode(object.initFunction, conEnv);
    }

    private void defineRecordInitFunction(BLangTypeDefinition typeDef, SymbolEnv conEnv) {
        BLangRecordTypeNode recordTypeNode = (BLangRecordTypeNode) typeDef.typeNode;
        recordTypeNode.initFunction = createInitFunction(typeDef.pos, "", Names.INIT_FUNCTION_SUFFIX);

        recordTypeNode.initFunction.receiver = createReceiver(typeDef.pos, typeDef.name);
        recordTypeNode.initFunction.attachedFunction = true;
        recordTypeNode.initFunction.flagSet.add(Flag.ATTACHED);

        // Adding record level variables to the init function is done at desugar phase

        defineNode(recordTypeNode.initFunction, conEnv);
    }

    private BLangVariable createReceiver(DiagnosticPos pos, BType type) {
        BLangVariable receiver = (BLangVariable) TreeBuilder.createVariableNode();
        receiver.pos = pos;
        IdentifierNode identifier = createIdentifier(Names.SELF.getValue());
        receiver.setName(identifier);
        receiver.docTag = DocTag.RECEIVER;

        receiver.type = type;
        return receiver;
    }

    private void defineServiceInitFunction(BLangService service, SymbolEnv conEnv) {
        BLangFunction initFunction = createInitFunction(service.pos, service.getName().getValue(),
                Names.INIT_FUNCTION_SUFFIX);
        service.initFunction = initFunction;
        defineNode(service.initFunction, conEnv);
    }

    private void defineAttachedFunctions(BLangFunction funcNode, BInvokableSymbol funcSymbol,
                                         SymbolEnv invokableEnv, boolean isValidAttachedFunc) {
        BTypeSymbol typeSymbol = funcNode.receiver.type.tsymbol;

        // Check whether there exists a struct field with the same name as the function name.
        if (isValidAttachedFunc) {
            if (typeSymbol.tag == SymTag.OBJECT) {
                validateFunctionsAttachedToObject(funcNode, funcSymbol, invokableEnv);
            } else if (typeSymbol.tag == SymTag.RECORD) {
                validateFunctionsAttachedToRecords(funcNode, funcSymbol, invokableEnv);
            }
        }

        defineNode(funcNode.receiver, invokableEnv);
        funcSymbol.receiverSymbol = funcNode.receiver.symbol;
    }

    private void validateFunctionsAttachedToRecords(BLangFunction funcNode, BInvokableSymbol funcSymbol,
                                                    SymbolEnv invokableEnv) {
        BInvokableType funcType = (BInvokableType) funcSymbol.type;
        BRecordTypeSymbol recordSymbol = (BRecordTypeSymbol) funcNode.receiver.type.tsymbol;

        recordSymbol.initializerFunc = new BAttachedFunction(
                names.fromIdNode(funcNode.name), funcSymbol, funcType);
    }

    private void validateFunctionsAttachedToObject(BLangFunction funcNode, BInvokableSymbol funcSymbol,
                                                   SymbolEnv invokableEnv) {

        BInvokableType funcType = (BInvokableType) funcSymbol.type;
        BObjectTypeSymbol objectSymbol = (BObjectTypeSymbol) funcNode.receiver.type.tsymbol;
        BSymbol symbol = symResolver.lookupMemberSymbol(funcNode.receiver.pos, objectSymbol.scope, invokableEnv,
                names.fromIdNode(funcNode.name), SymTag.VARIABLE);
        if (symbol != symTable.notFoundSymbol) {
            dlog.error(funcNode.pos, DiagnosticCode.OBJECT_FIELD_AND_FUNC_WITH_SAME_NAME,
                    funcNode.name.value, funcNode.receiver.type.toString());
            return;
        }

        BAttachedFunction attachedFunc = new BAttachedFunction(
                names.fromIdNode(funcNode.name), funcSymbol, funcType);
        objectSymbol.attachedFuncs.add(attachedFunc);

        // Check whether this attached function is a object initializer.
        if (!Names.OBJECT_INIT_SUFFIX.value.equals(funcNode.name.value)) {
            // Not a object initializer.
            return;
        }

        if (funcNode.returnTypeNode.type != symTable.nilType) {
            dlog.error(funcNode.pos, DiagnosticCode.INVALID_OBJECT_CONSTRUCTOR,
                    funcNode.name.value, funcNode.receiver.type.toString());
        }
        objectSymbol.initializerFunc = attachedFunc;
    }

    private StatementNode createAssignmentStmt(BLangVariable variable, BVarSymbol varSym, BSymbol fieldVar) {
        //Create LHS reference variable
        BLangSimpleVarRef varRef = (BLangSimpleVarRef) TreeBuilder.createSimpleVariableReferenceNode();
        varRef.pos = variable.pos;
        varRef.variableName = (BLangIdentifier) createIdentifier(fieldVar.name.getValue());
        varRef.pkgAlias = (BLangIdentifier) TreeBuilder.createIdentifierNode();
        varRef.symbol = fieldVar;
        varRef.type = fieldVar.type;

        //Create RHS variable reference
        BLangSimpleVarRef exprVar = (BLangSimpleVarRef) TreeBuilder.createSimpleVariableReferenceNode();
        exprVar.pos = variable.pos;
        exprVar.variableName = (BLangIdentifier) createIdentifier(varSym.name.getValue());
        exprVar.pkgAlias = (BLangIdentifier) TreeBuilder.createIdentifierNode();
        exprVar.symbol = varSym;
        exprVar.type = varSym.type;

        //Create assignment statement
        BLangAssignment assignmentStmt = (BLangAssignment) TreeBuilder.createAssignmentNode();
        assignmentStmt.expr = exprVar;
        assignmentStmt.pos = variable.pos;
        assignmentStmt.setVariable(varRef);
        return assignmentStmt;
    }

    private BLangVariable createReceiver(DiagnosticPos pos, BLangIdentifier name) {
        BLangVariable receiver = (BLangVariable) TreeBuilder.createVariableNode();
        receiver.pos = pos;
        IdentifierNode identifier = createIdentifier(Names.SELF.getValue());
        receiver.setName(identifier);
        receiver.docTag = DocTag.RECEIVER;

        BLangUserDefinedType structTypeNode = (BLangUserDefinedType) TreeBuilder.createUserDefinedTypeNode();
        structTypeNode.pkgAlias = new BLangIdentifier();
        structTypeNode.typeName = name;
        receiver.setTypeNode(structTypeNode);
        return receiver;
    }

    private void definePackageInitFunctions(BLangPackage pkgNode, SymbolEnv env) {
        BLangFunction initFunction = pkgNode.initFunction;
        // Add package level namespace declarations to the init function
        pkgNode.xmlnsList.forEach(xmlns -> {
            initFunction.body.addStatement(createNamespaceDeclrStatement(xmlns));
        });

        defineNode(pkgNode.initFunction, env);
        pkgNode.symbol.initFunctionSymbol = pkgNode.initFunction.symbol;

        addInitReturnStatement(pkgNode.startFunction.body);
        defineNode(pkgNode.startFunction, env);
        pkgNode.symbol.startFunctionSymbol = pkgNode.startFunction.symbol;

        addInitReturnStatement(pkgNode.stopFunction.body);
        defineNode(pkgNode.stopFunction, env);
        pkgNode.symbol.stopFunctionSymbol = pkgNode.stopFunction.symbol;
    }

    private void createPackageInitFunctions(BLangPackage pkgNode) {
        String alias = pkgNode.symbol.pkgID.toString();
        pkgNode.initFunction = createInitFunction(pkgNode.pos, alias,
                Names.INIT_FUNCTION_SUFFIX);
        pkgNode.startFunction = createInitFunction(pkgNode.pos, alias,
                Names.START_FUNCTION_SUFFIX);
        pkgNode.stopFunction = createInitFunction(pkgNode.pos, alias,
                Names.STOP_FUNCTION_SUFFIX);
    }

    private BLangFunction createInitFunction(DiagnosticPos pos, String name, Name sufix) {
        BLangFunction initFunction = (BLangFunction) TreeBuilder.createFunctionNode();
        initFunction.setName(createIdentifier(name + sufix.getValue()));
        initFunction.flagSet = EnumSet.of(Flag.PUBLIC);
        initFunction.pos = pos;

        BLangValueType typeNode = (BLangValueType) TreeBuilder.createValueTypeNode();
        typeNode.pos = pos;
        typeNode.typeKind = TypeKind.NIL;
        initFunction.returnTypeNode = typeNode;

        //Create body of the init function
        BLangBlockStmt body = (BLangBlockStmt) TreeBuilder.createBlockNode();
        body.pos = pos;
        initFunction.setBody(body);
        return initFunction;
    }

    private IdentifierNode createIdentifier(String value) {
        IdentifierNode node = TreeBuilder.createIdentifierNode();
        if (value != null) {
            node.setValue(value);
        }
        return node;
    }

    private void addInitReturnStatement(BLangBlockStmt bLangBlockStmt) {
        BLangReturn returnStmt = ASTBuilderUtil.createNilReturnStmt(bLangBlockStmt.pos, symTable.nilType);
        bLangBlockStmt.addStatement(returnStmt);
    }

    private BLangXMLNSStatement createNamespaceDeclrStatement(BLangXMLNS xmlns) {
        BLangXMLNSStatement xmlnsStmt = (BLangXMLNSStatement) TreeBuilder.createXMLNSDeclrStatementNode();
        xmlnsStmt.xmlnsDecl = xmlns;
        xmlnsStmt.pos = xmlns.pos;
        return xmlnsStmt;
    }

    private boolean validateFuncReceiver(BLangFunction funcNode) {
        if (funcNode.receiver == null) {
            return true;
        }

        if (funcNode.receiver.type == null) {
            funcNode.receiver.type = symResolver.resolveTypeNode(funcNode.receiver.typeNode, env);
        }
        if (funcNode.receiver.type.tag == TypeTags.ERROR) {
            return true;
        }

        if (funcNode.receiver.type.tag != TypeTags.BOOLEAN
                && funcNode.receiver.type.tag != TypeTags.STRING
                && funcNode.receiver.type.tag != TypeTags.INT
                && funcNode.receiver.type.tag != TypeTags.FLOAT
                && funcNode.receiver.type.tag != TypeTags.JSON
                && funcNode.receiver.type.tag != TypeTags.XML
                && funcNode.receiver.type.tag != TypeTags.MAP
                && funcNode.receiver.type.tag != TypeTags.TABLE
                && funcNode.receiver.type.tag != TypeTags.STREAM
                && funcNode.receiver.type.tag != TypeTags.FUTURE
                && funcNode.receiver.type.tag != TypeTags.OBJECT
                && funcNode.receiver.type.tag != TypeTags.RECORD) {
            dlog.error(funcNode.receiver.pos, DiagnosticCode.FUNC_DEFINED_ON_NOT_SUPPORTED_TYPE,
                    funcNode.name.value, funcNode.receiver.type.toString());
            return false;
        }

        if (!this.env.enclPkg.symbol.pkgID.equals(funcNode.receiver.type.tsymbol.pkgID)) {
            dlog.error(funcNode.receiver.pos, DiagnosticCode.FUNC_DEFINED_ON_NON_LOCAL_TYPE,
                    funcNode.name.value, funcNode.receiver.type.toString());
            return false;
        }
        return true;
    }

    private Name getFuncSymbolName(BLangFunction funcNode) {
        if (funcNode.receiver != null) {
            return names.fromString(Symbols.getAttachedFuncSymbolName(
                    funcNode.receiver.type.tsymbol.name.value, funcNode.name.value));
        }
        return names.fromIdNode(funcNode.name);
    }

    private Name getFieldSymbolName(BLangVariable receiver, BLangVariable variable) {
        return names.fromString(Symbols.getAttachedFuncSymbolName(
                receiver.type.tsymbol.name.value, variable.name.value));
    }

    private DocAttachment getDocAttachment(List<BLangDocumentation> docNodes) {
        if (docNodes.isEmpty()) {
            return new DocAttachment();
        }

        // At max we can have only one doc node.
        BLangDocumentation docNode = docNodes.get(0);

        DocAttachment docAttachment = new DocAttachment();
        docAttachment.description = docNode.documentationText;
        docNode.attributes.forEach(attr -> {
            DocAttachment.DocAttribute attribute = new DocAttachment
                    .DocAttribute(attr.documentationField.getValue(),
                    attr.documentationText,
                    attr.docTag);
            docAttachment.attributes.add(attribute);

        });
        return docAttachment;
    }
}
