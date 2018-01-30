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

import org.ballerinalang.compiler.CompilerOptionName;
import org.ballerinalang.compiler.CompilerPhase;
import org.ballerinalang.model.TreeBuilder;
import org.ballerinalang.model.elements.Flag;
import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.model.tree.IdentifierNode;
import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.model.tree.TopLevelNode;
import org.ballerinalang.model.tree.statements.StatementNode;
import org.ballerinalang.util.diagnostic.DiagnosticCode;
import org.wso2.ballerinalang.compiler.PackageLoader;
import org.wso2.ballerinalang.compiler.semantics.model.Scope;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolEnv;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolTable;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BAnnotationAttributeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BAnnotationSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BInvokableSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BPackageSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BTransformerSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BTypeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BVarSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BXMLAttributeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BXMLNSSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.SymTag;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.Symbols;
import org.wso2.ballerinalang.compiler.semantics.model.types.BAnnotationType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BConnectorType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BEnumType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BInvokableType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BStructType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BStructType.BStructField;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.tree.BLangAction;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotAttribute;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotation;
import org.wso2.ballerinalang.compiler.tree.BLangCompilationUnit;
import org.wso2.ballerinalang.compiler.tree.BLangConnector;
import org.wso2.ballerinalang.compiler.tree.BLangEnum;
import org.wso2.ballerinalang.compiler.tree.BLangEnum.BLangEnumerator;
import org.wso2.ballerinalang.compiler.tree.BLangFunction;
import org.wso2.ballerinalang.compiler.tree.BLangIdentifier;
import org.wso2.ballerinalang.compiler.tree.BLangImportPackage;
import org.wso2.ballerinalang.compiler.tree.BLangInvokableNode;
import org.wso2.ballerinalang.compiler.tree.BLangNode;
import org.wso2.ballerinalang.compiler.tree.BLangNodeVisitor;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.tree.BLangPackageDeclaration;
import org.wso2.ballerinalang.compiler.tree.BLangResource;
import org.wso2.ballerinalang.compiler.tree.BLangService;
import org.wso2.ballerinalang.compiler.tree.BLangStruct;
import org.wso2.ballerinalang.compiler.tree.BLangTransformer;
import org.wso2.ballerinalang.compiler.tree.BLangVariable;
import org.wso2.ballerinalang.compiler.tree.BLangWorker;
import org.wso2.ballerinalang.compiler.tree.BLangXMLNS;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangInvocation;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangSimpleVarRef;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLAttribute;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLQName;
import org.wso2.ballerinalang.compiler.tree.statements.BLangAssignment;
import org.wso2.ballerinalang.compiler.tree.statements.BLangBlockStmt;
import org.wso2.ballerinalang.compiler.tree.statements.BLangExpressionStmt;
import org.wso2.ballerinalang.compiler.tree.statements.BLangReturn;
import org.wso2.ballerinalang.compiler.tree.statements.BLangXMLNSStatement;
import org.wso2.ballerinalang.compiler.tree.types.BLangUserDefinedType;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.CompilerOptions;
import org.wso2.ballerinalang.compiler.util.Name;
import org.wso2.ballerinalang.compiler.util.Names;
import org.wso2.ballerinalang.compiler.util.NodeUtils;
import org.wso2.ballerinalang.compiler.util.TypeDescriptor;
import org.wso2.ballerinalang.compiler.util.TypeTags;
import org.wso2.ballerinalang.compiler.util.diagnotic.DiagnosticLog;
import org.wso2.ballerinalang.compiler.util.diagnotic.DiagnosticPos;
import org.wso2.ballerinalang.util.Flags;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private PackageLoader pkgLoader;
    private SymbolTable symTable;
    private Names names;
    private SymbolResolver symResolver;
    private DiagnosticLog dlog;

    private BLangPackage rootPkgNode;

    private SymbolEnv env;
    public Map<BPackageSymbol, SymbolEnv> packageEnvs = new HashMap<>();

    private BLangPackageDeclaration currentPkgDecl = null;

    private final boolean skipPkgValidation;

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
        this.names = Names.getInstance(context);
        this.symResolver = SymbolResolver.getInstance(context);
        this.dlog = DiagnosticLog.getInstance(context);

        this.rootPkgNode = (BLangPackage) TreeBuilder.createPackageNode();
        this.rootPkgNode.symbol = symTable.rootPkgSymbol;

        CompilerOptions options = CompilerOptions.getInstance(context);
        this.skipPkgValidation = Boolean.parseBoolean(options.get(CompilerOptionName.SKIP_PACKAGE_VALIDATION));
    }

    public BPackageSymbol definePackage(BLangPackage pkgNode, PackageID pkgId) {
        populatePackageNode(pkgNode, pkgId);

        defineNode(pkgNode, null);
        return pkgNode.symbol;
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
        // Create PackageSymbol.
        BPackageSymbol pSymbol = createPackageSymbol(pkgNode);
        SymbolEnv builtinEnv = this.packageEnvs.get(symTable.builtInPackageSymbol);
        SymbolEnv pkgEnv = SymbolEnv.createPkgEnv(pkgNode, pSymbol.scope, builtinEnv);
        packageEnvs.put(pSymbol, pkgEnv);

        createPackageInitFunction(pkgNode);
        // visit the package node recursively and define all package level symbols.
        // And maintain a list of created package symbols.
        pkgNode.imports.forEach(importNode -> defineNode(importNode, pkgEnv));

        // Define struct nodes.
        pkgNode.enums.forEach(enumNode -> defineNode(enumNode, pkgEnv));

        // Define struct nodes.
        pkgNode.structs.forEach(struct -> defineNode(struct, pkgEnv));

        // Define connector nodes.
        pkgNode.connectors.forEach(con -> defineNode(con, pkgEnv));

        // Define connector params and type.
        defineConnectorParams(pkgNode.connectors, pkgEnv);

        // Define transformer nodes.
        pkgNode.transformers.forEach(tansformer -> defineNode(tansformer, pkgEnv));

        // Define service and resource nodes.
        pkgNode.services.forEach(service -> defineNode(service, pkgEnv));

        // Define struct field nodes.
        defineStructFields(pkgNode.structs, pkgEnv);

        // Define connector action nodes.
        defineConnectorMembers(pkgNode.connectors, pkgEnv);

        // Define function nodes.
        pkgNode.functions.forEach(func -> defineNode(func, pkgEnv));

        // Define transformer params
        defineTransformerMembers(pkgNode.transformers, pkgEnv);

        // Define service resource nodes.
        defineServiceMembers(pkgNode.services, pkgEnv);

        // Define annotation nodes.
        pkgNode.annotations.forEach(annot -> defineNode(annot, pkgEnv));

        resolveAnnotationAttributeTypes(pkgNode.annotations, pkgEnv);

        pkgNode.globalVars.forEach(var -> defineNode(var, pkgEnv));

        definePackageInitFunction(pkgNode, pkgEnv);
        pkgNode.completedPhases.add(CompilerPhase.DEFINE);
    }

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
        BSymbol annotationSymbol = Symbols.createAnnotationSymbol(Flags.asMask(annotationNode.flagSet), names.
                fromIdNode(annotationNode.name), env.enclPkg.symbol.pkgID, null, env.scope.owner);
        annotationSymbol.type = new BAnnotationType((BAnnotationSymbol) annotationSymbol);
        annotationNode.attachmentPoints.forEach(point ->
                ((BAnnotationSymbol) annotationSymbol).attachmentPoints.add(point));
        annotationNode.symbol = annotationSymbol;
        defineSymbol(annotationNode.pos, annotationSymbol);
        SymbolEnv annotationEnv = SymbolEnv.createAnnotationEnv(annotationNode, annotationSymbol.scope, env);
        annotationNode.attributes.forEach(att -> this.defineNode(att, annotationEnv));
    }

    public void visit(BLangAnnotAttribute annotationAttribute) {
        BAnnotationAttributeSymbol annotationAttributeSymbol = Symbols.createAnnotationAttributeSymbol(names.
                        fromIdNode(annotationAttribute.name), env.enclPkg.symbol.pkgID,
                null, env.scope.owner);
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

        // Create import package symbol
        List<Name> nameComps = importPkgNode.pkgNameComps.stream()
                .map(identifier -> names.fromIdNode(identifier))
                .collect(Collectors.toList());
        PackageID pkgID = new PackageID(nameComps, names.fromIdNode(importPkgNode.version));
        if (pkgID.name.getValue().startsWith(Names.BUILTIN_PACKAGE.value)) {
            dlog.error(importPkgNode.pos, DiagnosticCode.PACKAGE_NOT_FOUND,
                    importPkgNode.getQualifiedPackageName());
            return;
        }
        BPackageSymbol pkgSymbol = pkgLoader.getPackageSymbol(pkgID);
        if (pkgSymbol == null) {
            BLangPackage pkgNode = pkgLoader.loadPackageNode(pkgID);
            if (pkgNode == null) {
                dlog.error(importPkgNode.pos, DiagnosticCode.PACKAGE_NOT_FOUND,
                        importPkgNode.getQualifiedPackageName());
                return;
            } else {
                pkgSymbol = pkgNode.symbol;
                populateInitFunctionInvocation(importPkgNode, pkgSymbol);
            }
        }
        importPkgNode.symbol = pkgSymbol;
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

    @Override
    public void visit(BLangStruct structNode) {
        BSymbol structSymbol = Symbols.createStructSymbol(Flags.asMask(structNode.flagSet),
                names.fromIdNode(structNode.name), env.enclPkg.symbol.pkgID, null, env.scope.owner);
        structNode.symbol = structSymbol;
        defineSymbol(structNode.pos, structSymbol);

        // Create struct type
        structNode.symbol.type = new BStructType((BTypeSymbol) structNode.symbol, new ArrayList<>());
    }

    @Override
    public void visit(BLangEnum enumNode) {
        BTypeSymbol enumSymbol = Symbols.createEnumSymbol(Flags.asMask(enumNode.flagSet),
                names.fromIdNode(enumNode.name), env.enclPkg.symbol.pkgID, null, env.scope.owner);
        enumNode.symbol = enumSymbol;
        defineSymbol(enumNode.pos, enumSymbol);

        BEnumType enumType = new BEnumType(enumSymbol, null);
        enumSymbol.type = enumType;

        SymbolEnv enumEnv = SymbolEnv.createPkgLevelSymbolEnv(enumNode, enumSymbol.scope, this.env);
        for (int i = 0; i < enumNode.enumerators.size(); i++) {
            BLangEnumerator enumerator = enumNode.enumerators.get(i);
            BVarSymbol enumeratorSymbol = new BVarSymbol(Flags.PUBLIC,
                    names.fromIdNode(enumerator.name), enumSymbol.pkgID, enumType, enumSymbol);
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
        workerNode.symbol = workerSymbol;
        defineSymbolWithCurrentEnvOwner(workerNode.pos, workerSymbol);
    }

    @Override
    public void visit(BLangConnector connectorNode) {
        BTypeSymbol conSymbol = Symbols.createConnectorSymbol(Flags.asMask(connectorNode.flagSet),
                names.fromIdNode(connectorNode.name), env.enclPkg.symbol.pkgID, null, env.scope.owner);
        connectorNode.symbol = conSymbol;
        defineSymbol(connectorNode.pos, conSymbol);
    }

    @Override
    public void visit(BLangService serviceNode) {
        BSymbol serviceSymbol = Symbols.createServiceSymbol(Flags.asMask(serviceNode.flagSet),
                names.fromIdNode(serviceNode.name), env.enclPkg.symbol.pkgID, null, env.scope.owner);
        serviceNode.symbol = serviceSymbol;
        defineSymbol(serviceNode.pos, serviceSymbol);
    }

    @Override
    public void visit(BLangFunction funcNode) {
        validateFuncReceiver(funcNode);
        BInvokableSymbol funcSymbol = Symbols.createFunctionSymbol(Flags.asMask(funcNode.flagSet),
                getFuncSymbolName(funcNode), env.enclPkg.symbol.pkgID, null, env.scope.owner);
        SymbolEnv invokableEnv = SymbolEnv.createFunctionEnv(funcNode, funcSymbol.scope, env);
        defineInvokableSymbol(funcNode, funcSymbol, invokableEnv);
        ((BInvokableType) funcSymbol.type).typeDescriptor = TypeDescriptor.SIG_FUNCTION;

        // Define function receiver if any.
        if (funcNode.receiver != null) {
            BTypeSymbol structSymbol = funcNode.receiver.type.tsymbol;
            // Check whether there exists a struct field with the same name as the function name.
            if (structSymbol.tag == TypeTags.STRUCT) {
                BSymbol symbol = symResolver.lookupMemberSymbol(funcNode.receiver.pos, structSymbol.scope, invokableEnv,
                        names.fromIdNode(funcNode.name), SymTag.VARIABLE);
                if (symbol != symTable.notFoundSymbol) {
                    dlog.error(funcNode.pos, DiagnosticCode.STRUCT_FIELD_AND_FUNC_WITH_SAME_NAME,
                            funcNode.name.value, funcNode.receiver.type.toString());
                }
            }

            defineNode(funcNode.receiver, invokableEnv);
            funcSymbol.receiverSymbol = funcNode.receiver.symbol;
            ((BInvokableType) funcSymbol.type).setReceiverType(funcNode.receiver.symbol.type);
        }
    }

    @Override
    public void visit(BLangTransformer transformerNode) {
        validateTransformerMappingTypes(transformerNode);

        boolean safeConversion = transformerNode.retParams.size() == 1;
        Name name = getTransformerSymbolName(transformerNode);
        BTransformerSymbol transformerSymbol = Symbols.createTransformerSymbol(Flags.asMask(transformerNode.flagSet),
                name, env.enclPkg.symbol.pkgID, null, safeConversion, env.scope.owner);
        transformerNode.symbol = transformerSymbol;

        // If this is a default transformer, check whether this transformer conflicts with a built-in conversion
        if (transformerNode.name.value.isEmpty()) {
            BType targetType = transformerNode.retParams.get(0).type;
            BSymbol symbol = symResolver.resolveConversionOperator(transformerNode.source.type, targetType);
            if (symbol != symTable.notFoundSymbol) {
                dlog.error(transformerNode.pos, DiagnosticCode.TRANSFORMER_CONFLICTS_WITH_CONVERSION,
                        transformerNode.source.type, targetType);
                return;
            }
        }

        // Define the transformer
        SymbolEnv transformerEnv = SymbolEnv.createTransformerEnv(transformerNode, transformerSymbol.scope, env);

        transformerNode.symbol = transformerSymbol;
        defineSymbol(transformerNode.pos, transformerSymbol);
        transformerEnv.scope = transformerSymbol.scope;

        // Define transformer source.
        defineNode(transformerNode.source, transformerEnv);
    }
    
    @Override
    public void visit(BLangAction actionNode) {
        BInvokableSymbol actionSymbol = Symbols
                .createActionSymbol(Flags.asMask(actionNode.flagSet), names.fromIdNode(actionNode.name),
                        env.enclPkg.symbol.pkgID, null, env.scope.owner);
        SymbolEnv invokableEnv = SymbolEnv.createResourceActionSymbolEnv(actionNode, actionSymbol.scope, env);
        defineInvokableSymbol(actionNode, actionSymbol, invokableEnv);

        //TODO check below as it create a new symbol for the connector
        BVarSymbol varSymbol = new BVarSymbol(Flags.asMask(EnumSet.noneOf(Flag.class)),
                names.fromIdNode((BLangIdentifier) createIdentifier(Names.CONNECTOR.getValue())),
                env.enclPkg.symbol.pkgID, actionSymbol.owner.type, invokableEnv.scope.owner);

        actionSymbol.receiverSymbol = varSymbol;
        ((BInvokableType) actionSymbol.type).setReceiverType(varSymbol.type);
    }

    @Override
    public void visit(BLangResource resourceNode) {
        BInvokableSymbol resourceSymbol = Symbols
                .createResourceSymbol(Flags.asMask(resourceNode.flagSet), names.fromIdNode(resourceNode.name),
                        env.enclPkg.symbol.pkgID, null, env.scope.owner);
        SymbolEnv invokableEnv = SymbolEnv.createResourceActionSymbolEnv(resourceNode, resourceSymbol.scope, env);
        defineInvokableSymbol(resourceNode, resourceSymbol, invokableEnv);
    }

    @Override
    public void visit(BLangVariable varNode) {
        // assign the type to var type node
        BType varType = symResolver.resolveTypeNode(varNode.typeNode, env);
        varNode.type = varType;

        Name varName = names.fromIdNode(varNode.name);
        if (varName == Names.EMPTY) {
            // This is a variable created for a return type
            // e.g. function foo() (int);
            return;
        }

        varNode.symbol = defineVarSymbol(varNode.pos, varNode.flagSet, varType, varName, env);
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

    // Private methods

    private BPackageSymbol createPackageSymbol(BLangPackage pkgNode) {
        BPackageSymbol pSymbol;
        if (pkgNode.pkgDecl == null) {
            pSymbol = new BPackageSymbol(PackageID.DEFAULT, symTable.rootPkgSymbol);
        } else {
            PackageID pkgID = NodeUtils.getPackageID(names, pkgNode.pkgDecl.pkgNameComps, pkgNode.pkgDecl.version);
            pSymbol = new BPackageSymbol(pkgID, symTable.rootPkgSymbol);
        }
        pkgNode.symbol = pSymbol;
        if (pSymbol.name.value.startsWith(Names.BUILTIN_PACKAGE.value)) {
            pSymbol.scope = symTable.rootScope;
        } else {
            pSymbol.scope = new Scope(pSymbol);
        }
        return pSymbol;
    }

    /**
     * Visit each compilation unit (.bal file) and add each top-level node
     * in the compilation unit to the package node.
     *
     * @param pkgNode current package node
     */
    private void populatePackageNode(BLangPackage pkgNode, PackageID pkgId) {
        List<BLangCompilationUnit> compUnits = pkgNode.getCompilationUnits();
        compUnits.forEach(compUnit -> populateCompilationUnit(pkgNode, compUnit, pkgId));
    }

    /**
     * Visit each top-level node and add it to the package node.
     *
     * @param pkgNode  current package node
     * @param compUnit current compilation unit
     */
    private void populateCompilationUnit(BLangPackage pkgNode, BLangCompilationUnit compUnit, PackageID pkgId) {
        // TODO If the pkgID is null, then assign an unnamed package/default package.
        compUnit.getTopLevelNodes().forEach(node -> addTopLevelNode(pkgNode, node));
        validatePackageDecl(pkgId, pkgNode, compUnit);
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
            case PACKAGE_DECLARATION:
                // TODO verify the rules..
                currentPkgDecl = (BLangPackageDeclaration) node;
                break;
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
            case STRUCT:
                pkgNode.structs.add((BLangStruct) node);
                break;
            case ENUM:
                pkgNode.enums.add((BLangEnum) node);
                break;
            case CONNECTOR:
                pkgNode.connectors.add((BLangConnector) node);
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
            case TRANSFORMER:
                pkgNode.transformers.add((BLangTransformer) node);
                break;
        }
    }

    private void defineStructFields(List<BLangStruct> structNodes, SymbolEnv pkgEnv) {
        structNodes.forEach(struct -> {
            // Create struct type
            SymbolEnv structEnv = SymbolEnv.createPkgLevelSymbolEnv(struct, struct.symbol.scope, pkgEnv);
            BStructType structType = (BStructType) struct.symbol.type;
            structType.fields = struct.fields.stream()
                    .peek(field -> field.flagSet.add(Flag.PUBLIC))
                    .peek(field -> defineNode(field, structEnv))
                    .map(field -> new BStructField(names.fromIdNode(field.name), field.type))
                    .collect(Collectors.toList());
        });
    }

    private void defineConnectorMembers(List<BLangConnector> connectors, SymbolEnv pkgEnv) {
        connectors.forEach(connector -> {
            SymbolEnv conEnv = SymbolEnv.createConnectorEnv(connector, connector.symbol.scope, pkgEnv);

            connector.varDefs.forEach(varDef -> defineNode(varDef.var, conEnv));
            defineConnectorInitFunction(connector, conEnv);
            connector.actions.stream()
                    .peek(action -> action.flagSet.add(Flag.PUBLIC))
                    .forEach(action -> defineNode(action, conEnv));
        });
    }

    private void defineConnectorParams(List<BLangConnector> connectors, SymbolEnv pkgEnv) {
        connectors.forEach(connector -> {
            SymbolEnv conEnv = SymbolEnv.createConnectorEnv(connector, connector.symbol.scope, pkgEnv);
            defineConnectorSymbolParams(connector, connector.symbol, conEnv);
        });
    }

    private void defineServiceMembers(List<BLangService> services, SymbolEnv pkgEnv) {
        services.forEach(service -> {
            SymbolEnv serviceEnv = SymbolEnv.createServiceEnv(service, service.symbol.scope, pkgEnv);
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
        defineSymbol(invokableNode.pos, funcSymbol);
        invokableEnv.scope = funcSymbol.scope;
        defineInvokableSymbolParams(invokableNode, funcSymbol, invokableEnv);
    }

    private void defineInvokableSymbolParams(BLangInvokableNode invokableNode, BInvokableSymbol symbol,
                                             SymbolEnv invokableEnv) {
        List<BVarSymbol> paramSymbols =
                invokableNode.params.stream()
                        .peek(varNode -> defineNode(varNode, invokableEnv))
                        .map(varNode -> varNode.symbol)
                        .collect(Collectors.toList());

        List<BVarSymbol> retParamSymbols =
                invokableNode.retParams.stream()
                        .peek(varNode -> defineNode(varNode, invokableEnv))
                        .filter(varNode -> varNode.symbol != null)
                        .map(varNode -> varNode.symbol)
                        .collect(Collectors.toList());

        symbol.params = paramSymbols;
        symbol.retParams = retParamSymbols;

        // Create function type
        List<BType> paramTypes = paramSymbols.stream()
                .map(paramSym -> paramSym.type)
                .collect(Collectors.toList());
        List<BType> retTypes = invokableNode.retParams.stream()
                .map(varNode -> varNode.typeNode.type)
                .collect(Collectors.toList());

        symbol.type = new BInvokableType(paramTypes, retTypes, null);
    }

    private void defineConnectorSymbolParams(BLangConnector connectorNode, BTypeSymbol symbol,
                                             SymbolEnv connectorEnv) {
        List<BVarSymbol> paramSymbols =
                connectorNode.params.stream()
                        .peek(varNode -> defineNode(varNode, connectorEnv))
                        .map(varNode -> varNode.symbol)
                        .collect(Collectors.toList());

        symbol.params = paramSymbols;

        // Create connector type
        List<BType> paramTypes = paramSymbols.stream()
                .map(paramSym -> paramSym.type)
                .collect(Collectors.toList());

        symbol.type = new BConnectorType(paramTypes, symbol);
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
        BVarSymbol varSymbol = new BVarSymbol(Flags.asMask(flagSet), varName,
                env.enclPkg.symbol.pkgID, varType, enclScope.owner);

        // Add it to the enclosing scope
        // Find duplicates
        if (!symResolver.checkForUniqueSymbol(pos, env, varSymbol, SymTag.VARIABLE_NAME)) {
            varSymbol.type = symTable.errType;
        }
        enclScope.define(varSymbol.name, varSymbol);
        return varSymbol;
    }

    private void defineConnectorInitFunction(BLangConnector connector, SymbolEnv conEnv) {
        BLangFunction initFunction = createInitFunction(connector.pos, connector.getName().getValue());
        //Add connector as a parameter to the init function
        BLangVariable param = (BLangVariable) TreeBuilder.createVariableNode();
        param.pos = connector.pos;
        param.setName(this.createIdentifier(Names.CONNECTOR.getValue()));
        BLangUserDefinedType connectorType = (BLangUserDefinedType) TreeBuilder.createUserDefinedTypeNode();
        connectorType.pos = connector.pos;
        connectorType.typeName = connector.name;
        connectorType.pkgAlias = (BLangIdentifier) TreeBuilder.createIdentifierNode();
        param.setTypeNode(connectorType);
        initFunction.addParameter(param);
        //Add connector level variables to the init function
        connector.varDefs.stream().filter(f -> f.var.expr != null)
                .forEachOrdered(v -> initFunction.body.addStatement(createAssignmentStmt(v.var)));

        addInitReturnStatement(initFunction.body);
        connector.initFunction = initFunction;

        BLangAction initAction = createNativeInitAction(connector.pos);
        connector.initAction = initAction;

        defineNode(connector.initFunction, conEnv);
        defineNode(connector.initAction, conEnv);
        connector.symbol.initFunctionSymbol = connector.initFunction.symbol;
    }

    private void defineServiceInitFunction(BLangService service, SymbolEnv conEnv) {
        BLangFunction initFunction = createInitFunction(service.pos, service.getName().getValue());
        //Add service level variables to the init function
        service.vars.stream().filter(f -> f.var.expr != null)
                .forEachOrdered(v -> initFunction.body.addStatement(createAssignmentStmt(v.var)));

        addInitReturnStatement(initFunction.body);
        service.initFunction = initFunction;
        defineNode(service.initFunction, conEnv);
//        service.symbol.initFunctionSymbol = service.initFunction.symbol;
    }

    private StatementNode createAssignmentStmt(BLangVariable variable) {
        BLangSimpleVarRef varRef = (BLangSimpleVarRef) TreeBuilder
                .createSimpleVariableReferenceNode();
        varRef.pos = variable.pos;
        varRef.variableName = variable.name;
        varRef.pkgAlias = (BLangIdentifier) TreeBuilder.createIdentifierNode();

//        //TODO temporary solution to avoid endpoint assignment until endpoint model changed.
//        if (variable.typeNode.getKind() == NodeKind.ENDPOINT_TYPE) {
//            BLangBind bindStmt = (BLangBind) TreeBuilder.createBindNode();
//            bindStmt.setExpression(variable.expr);
//            bindStmt.pos = variable.pos;
//            bindStmt.addWS(variable.getWS());
//            bindStmt.setVariable(varRef);
//            return bindStmt;
//        }
        BLangAssignment assignmentStmt = (BLangAssignment) TreeBuilder.createAssignmentNode();
        assignmentStmt.expr = variable.expr;
        assignmentStmt.pos = variable.pos;
        assignmentStmt.addVariable(varRef);
        return assignmentStmt;
    }

    private BLangExpressionStmt createInitFuncInvocationStmt(BLangImportPackage importPackage,
                                                             BPackageSymbol pkgSymbol) {
        BLangInvocation invocationNode = (BLangInvocation) TreeBuilder.createInvocationNode();
        invocationNode.pos = importPackage.pos;
        invocationNode.addWS(importPackage.getWS());
        BLangIdentifier funcName = (BLangIdentifier) TreeBuilder.createIdentifierNode();
        funcName.value = pkgSymbol.initFunctionSymbol.name.value;
        invocationNode.name = funcName;
        invocationNode.pkgAlias = importPackage.alias;

        BLangExpressionStmt exprStmt = (BLangExpressionStmt) TreeBuilder.createExpressionStatementNode();
        exprStmt.pos = importPackage.pos;
        exprStmt.addWS(importPackage.getWS());
        exprStmt.expr = invocationNode;
        return exprStmt;
    }

    private void definePackageInitFunction(BLangPackage pkgNode, SymbolEnv env) {
        BLangFunction initFunction = pkgNode.initFunction;
        // Add package level namespace declarations to the init function
        pkgNode.xmlnsList.forEach(xmlns -> {
            initFunction.body.addStatement(createNamespaceDeclrStatement(xmlns));
        });

        //Add global variables to the init function
        pkgNode.globalVars.stream().filter(f -> f.expr != null)
                .forEachOrdered(v -> initFunction.body.addStatement(createAssignmentStmt(v)));

        addInitReturnStatement(initFunction.body);
        defineNode(pkgNode.initFunction, env);
        pkgNode.symbol.initFunctionSymbol = pkgNode.initFunction.symbol;
    }

    private void createPackageInitFunction(BLangPackage pkgNode) {
        BLangFunction initFunction = createInitFunction(pkgNode.pos, pkgNode.symbol.getName().getValue());
        pkgNode.initFunction = initFunction;
    }

    private BLangFunction createInitFunction(DiagnosticPos pos, String name) {
        BLangFunction initFunction = (BLangFunction) TreeBuilder.createFunctionNode();
        initFunction.setName(createIdentifier(name + Names.INIT_FUNCTION_SUFFIX.getValue()));
        initFunction.flagSet = EnumSet.of(Flag.PUBLIC);
        initFunction.pos = pos;
        //Create body of the init function
        BLangBlockStmt body = (BLangBlockStmt) TreeBuilder.createBlockNode();
        body.pos = pos;
        initFunction.setBody(body);
        return initFunction;
    }

    private BLangAction createNativeInitAction(DiagnosticPos pos) {
        BLangAction initAction = (BLangAction) TreeBuilder.createActionNode();
        initAction.setName(createIdentifier(Names.INIT_ACTION_SUFFIX.getValue()));
        initAction.flagSet = EnumSet.of(Flag.NATIVE, Flag.PUBLIC);
        initAction.pos = pos;
        return initAction;
    }

    private IdentifierNode createIdentifier(String value) {
        IdentifierNode node = TreeBuilder.createIdentifierNode();
        if (value != null) {
            node.setValue(value);
        }
        return node;
    }

    private void addInitReturnStatement(BLangBlockStmt bLangBlockStmt) {
        //Add return statement to the init function
        BLangReturn returnStmt = (BLangReturn) TreeBuilder.createReturnNode();
        returnStmt.pos = bLangBlockStmt.pos;
        bLangBlockStmt.addStatement(returnStmt);
    }

    private BLangXMLNSStatement createNamespaceDeclrStatement(BLangXMLNS xmlns) {
        BLangXMLNSStatement xmlnsStmt = (BLangXMLNSStatement) TreeBuilder.createXMLNSDeclrStatementNode();
        xmlnsStmt.xmlnsDecl = xmlns;
        xmlnsStmt.pos = xmlns.pos;
        return xmlnsStmt;
    }

    private void validateFuncReceiver(BLangFunction funcNode) {
        if (funcNode.receiver == null) {
            return;
        }

        BType varType = symResolver.resolveTypeNode(funcNode.receiver.typeNode, env);
        funcNode.receiver.type = varType;
        if (varType.tag == TypeTags.ERROR) {
            return;
        }

        if (varType.tag != TypeTags.BOOLEAN
                && varType.tag != TypeTags.STRING
                && varType.tag != TypeTags.INT
                && varType.tag != TypeTags.FLOAT
                && varType.tag != TypeTags.BLOB
                && varType.tag != TypeTags.JSON
                && varType.tag != TypeTags.XML
                && varType.tag != TypeTags.MAP
                && varType.tag != TypeTags.DATATABLE
                && varType.tag != TypeTags.STRUCT) {
            dlog.error(funcNode.receiver.pos, DiagnosticCode.FUNC_DEFINED_ON_NOT_SUPPORTED_TYPE,
                    funcNode.name.value, varType.toString());
            return;
        }

        if (!this.env.enclPkg.symbol.pkgID.equals(varType.tsymbol.pkgID)) {
            dlog.error(funcNode.receiver.pos, DiagnosticCode.FUNC_DEFINED_ON_NON_LOCAL_TYPE,
                    funcNode.name.value, varType.toString());
        }
    }

    private Name getFuncSymbolName(BLangFunction funcNode) {
        if (funcNode.receiver != null) {
            return names.fromString(funcNode.receiver.type + "." + funcNode.name.value);
        }
        return names.fromIdNode(funcNode.name);
    }

    private Name getTransformerSymbolName(BLangTransformer transformerNode) {
        if (transformerNode.name.value.isEmpty()) {
            return names.fromString(Names.TRANSFORMER.value + "<" + transformerNode.source.type + ","
                    + transformerNode.retParams.get(0).type + ">");
        }
        return names.fromIdNode(transformerNode.name);
    }

    private void populateInitFunctionInvocation(BLangImportPackage importPkgNode, BPackageSymbol pkgSymbol) {
        ((BLangPackage) env.node).initFunction.body
                .addStatement(createInitFuncInvocationStmt(importPkgNode, pkgSymbol));
    }

    private void validateTransformerMappingTypes(BLangTransformer transformerNode) {
        BType varType = symResolver.resolveTypeNode(transformerNode.source.typeNode, env);
        transformerNode.source.type = varType;

        transformerNode.retParams.forEach(returnParams -> {
            BType targetType = symResolver.resolveTypeNode(returnParams.typeNode, env);
            returnParams.type = targetType;
        });
    }

    private void defineTransformerMembers(List<BLangTransformer> transformers, SymbolEnv pkgEnv) {
        transformers.forEach(transformer -> {
            SymbolEnv transformerEnv = SymbolEnv.createTransformerEnv(transformer, transformer.symbol.scope, pkgEnv);
            defineInvokableSymbolParams(transformer, transformer.symbol, transformerEnv);

            BInvokableType transformerSymType = ((BInvokableType) transformer.symbol.type);
            transformerSymType.typeDescriptor = TypeDescriptor.SIG_FUNCTION;
        });
    }

    /**
     * Validate the package declaration of the current compilation unit. Updates the package declaration
     * of the package node only if the current package declaration is a valid one.
     *
     * @param pkgId    Current package ID
     * @param pkgNode  Current package node
     * @param compUnit Current compilation unit
     */
    private void validatePackageDecl(PackageID pkgId, BLangPackage pkgNode, BLangCompilationUnit compUnit) {
        if (isValidPackageDecl(currentPkgDecl, pkgId)) {
            pkgNode.pkgDecl = currentPkgDecl;
            currentPkgDecl = null;
            return;
        }

        if (currentPkgDecl == null) {
            dlog.error(compUnit.pos, DiagnosticCode.MISSING_PACKAGE_DECLARATION, pkgId.name.value);
        } else if (pkgId == PackageID.DEFAULT) {
            dlog.error(currentPkgDecl.pos, DiagnosticCode.UNEXPECTED_PACKAGE_DECLARATION,
                    currentPkgDecl.getPackageNameStr());
        } else {
            dlog.error(currentPkgDecl.pos, DiagnosticCode.INVALID_PACKAGE_DECLARATION, pkgId.name.value,
                    currentPkgDecl.getPackageNameStr());
        }

        currentPkgDecl = null;
    }

    private boolean isValidPackageDecl(BLangPackageDeclaration pkgDecl, PackageID pkgId) {
        if (skipPkgValidation) {
            return true;
        }

        if (pkgDecl == null) {
            return pkgId == PackageID.DEFAULT;
        }

        return pkgId.name.value.equals(pkgDecl.getPackageNameStr());
    }
}
