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
package org.ballerinalang.model.tree;

import java.util.List;

/**
 * @since 0.94
 */
public interface PackageNode extends Node {

    List<? extends CompilationUnitNode> getCompilationUnits();

    void addCompilationUnit(CompilationUnitNode compUnit);

    void setPackageDeclaration(PackageDeclarationNode pkgDecl);

    PackageDeclarationNode getPackageDeclaration();

    List<? extends ImportPackageNode> getImports();

    void addImport(ImportPackageNode importPkg);

    List<? extends XMLNSDeclarationNode> getNamespaceDeclarations();

    void addNamespaceDeclaration(XMLNSDeclarationNode xmlnsDecl);

    List<? extends VariableNode> getGlobalVariables();

    void addGlobalVariable(VariableNode globalVar);

    List<? extends ServiceNode> getServices();

    void addService(ServiceNode service);

    List<? extends ConnectorNode> getConnectors();

    void addConnector(ConnectorNode connector);

    List<? extends FunctionNode> getFunctions();

    void addFunction(FunctionNode function);

    List<? extends StructNode> getStructs();

    void addStruct(StructNode struct);

    List<? extends EnumNode> getEnums();

    void addEnum(EnumNode enumNode);

    List<? extends AnnotationNode> getAnnotations();

    void addAnnotation(AnnotationNode annotation);

    List<? extends TransformerNode> getTransformers();

    void addTransformer(TransformerNode transformer);
}
