/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.docgen;

import org.ballerinalang.docgen.docs.BallerinaDocConstants;
import org.ballerinalang.docgen.model.ActionDoc;
import org.ballerinalang.docgen.model.AnnotationDoc;
import org.ballerinalang.docgen.model.ConnectorDoc;
import org.ballerinalang.docgen.model.Documentable;
import org.ballerinalang.docgen.model.EnumDoc;
import org.ballerinalang.docgen.model.Field;
import org.ballerinalang.docgen.model.FunctionDoc;
import org.ballerinalang.docgen.model.GlobalVariableDoc;
import org.ballerinalang.docgen.model.Link;
import org.ballerinalang.docgen.model.PackageName;
import org.ballerinalang.docgen.model.Page;
import org.ballerinalang.docgen.model.PrimitiveTypeDoc;
import org.ballerinalang.docgen.model.StaticCaption;
import org.ballerinalang.docgen.model.StructDoc;
import org.ballerinalang.docgen.model.Variable;
import org.ballerinalang.model.elements.Flag;
import org.ballerinalang.model.tree.AnnotatableNode;
import org.ballerinalang.model.tree.AnnotationAttachmentNode;
import org.ballerinalang.model.tree.EnumNode;
import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.model.tree.types.TypeNode;
import org.wso2.ballerinalang.compiler.semantics.model.types.BStructType;
import org.wso2.ballerinalang.compiler.tree.BLangAction;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotAttribute;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotation;
import org.wso2.ballerinalang.compiler.tree.BLangConnector;
import org.wso2.ballerinalang.compiler.tree.BLangEnum;
import org.wso2.ballerinalang.compiler.tree.BLangFunction;
import org.wso2.ballerinalang.compiler.tree.BLangNode;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.tree.BLangStruct;
import org.wso2.ballerinalang.compiler.tree.BLangVariable;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordLiteral;
import org.wso2.ballerinalang.compiler.tree.types.BLangType;
import org.wso2.ballerinalang.compiler.tree.types.BLangUserDefinedType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Generates the Page objects for bal packages.
 */
public class Generator {
    private static final String ANONYMOUS_STRUCT = "$anonStruct$";

    /**
     * Generate the page when the bal package is passed.
     * @param balPackage The current package that is being viewed.
     * @param packages List of available packages.
     * @return A page model for the current package.
     */
    public static Page generatePage(BLangPackage balPackage, List<Link> packages) {
        ArrayList<Documentable> documentables = new ArrayList<>();
        String currentPackageName = (balPackage.symbol).pkgID.name.value;
        
        // Check for structs in the package
        if (balPackage.getStructs().size() > 0) {
            for (BLangStruct struct : balPackage.getStructs()) {
                if (struct.getFlags().contains(Flag.PUBLIC)) {
                    documentables.add(createDocForNode(struct));
                }
            }
        }
        // Check for functions in the package
        if (balPackage.getFunctions().size() > 0) {
            for (BLangFunction function : balPackage.getFunctions()) {
                if (function.getFlags().contains(Flag.PUBLIC)) {
                    if (function.getReceiver() != null) {
                        if (documentables.size() > 0) {
                            for (Documentable parentDocumentable : documentables) {
                                TypeNode langType = function.getReceiver().getTypeNode();
                                String typeName = (langType instanceof BLangUserDefinedType ?
                                               ((BLangUserDefinedType) langType).typeName.value : langType.toString());
                                
                                if (typeName.equals(parentDocumentable.name)) {
                                    parentDocumentable.children.add(createDocForNode(function));
                                }
                            }
                        }
                    } else {
                        // If there's no receiver type i.e. no struct binding to the function
                        documentables.add(createDocForNode(function));
                    }
                }
            }
        }
        // Check for connectors in the package
        for (BLangConnector connector : balPackage.getConnectors()) {
            if (connector.getFlags().contains(Flag.PUBLIC)) {
                documentables.add(createDocForNode(connector));
            }
        }
        // Check for connectors in the package
        for (EnumNode enumNode : balPackage.getEnums()) {
            if (enumNode.getFlags().contains(Flag.PUBLIC)) {
                documentables.add(createDocForNode(enumNode));
            }
        }
        // Check for annotations
        for (BLangAnnotation annotation : balPackage.getAnnotations()) {
            if (annotation.getFlags().contains(Flag.PUBLIC)) {
                documentables.add(createDocForNode(annotation));
            }
        }
        // Check for global variables
        for (BLangVariable var : balPackage.getGlobalVariables()) {
            if (var.getFlags().contains(Flag.PUBLIC)) {
                documentables.add(createDocForNode(var));
            }
        }
        
        // Create the links to select which page or package is active
        List<Link> links = new ArrayList<>();
        PackageName packageNameHeading = null;
        for (Link pkgLink : packages) {
            if (pkgLink.caption.value.equals(currentPackageName)) {
                packageNameHeading = (PackageName) pkgLink.caption;
                links.add(new Link(pkgLink.caption, pkgLink.href, true));
            } else {
                links.add(new Link(pkgLink.caption, pkgLink.href, false));
            }
        }
    
        return new Page(packageNameHeading, documentables, links);
    }
    
    /**
     * Generate the page for primitive types.
     * @param balPackage The ballerina.builtin package.
     * @param packages List of available packages.
     * @return A page model for the primitive types.
     */
    public static Page generatePageForPrimitives(BLangPackage balPackage, List<Link> packages) {
        ArrayList<Documentable> primitiveTypes = new ArrayList<>();
        
        // Check for functions in the package
        if (balPackage.getFunctions().size() > 0) {
            for (BLangFunction function : balPackage.getFunctions()) {
                if (function.getFlags().contains(Flag.PUBLIC) && function.getReceiver() != null) {
                    TypeNode langType = function.getReceiver().getTypeNode();
                    if (!(langType instanceof BLangUserDefinedType)) {
                        // Check for primitives in ballerina.builtin
                        Optional<PrimitiveTypeDoc> existingPrimitiveType = primitiveTypes
                                .stream()
                                .filter((doc) -> doc instanceof PrimitiveTypeDoc &&
                                                 (((PrimitiveTypeDoc) doc)).name.equals(langType.toString()))
                                .map(doc -> (PrimitiveTypeDoc) doc)
                                .findFirst();
                        
                        PrimitiveTypeDoc primitiveTypeDoc;
                        if (existingPrimitiveType.isPresent()) {
                            primitiveTypeDoc = existingPrimitiveType.get();
                        } else {
                            primitiveTypeDoc = new PrimitiveTypeDoc(langType.toString(), new ArrayList<>());
                            primitiveTypes.add(primitiveTypeDoc);
                        }
                        
                        primitiveTypeDoc.children.add(createDocForNode(function));
                    }
                }
            }
        }
    
        // Create the links to select which page or package is active
        List<Link> links = new ArrayList<>();
        for (Link pkgLink : packages) {
            if (BallerinaDocConstants.PRIMITIVE_TYPES_PAGE_NAME.equals(pkgLink.caption.value)) {
                links.add(new Link(pkgLink.caption, pkgLink.href, true));
            } else {
                links.add(new Link(pkgLink.caption, pkgLink.href, false));
            }
        }
    
        StaticCaption primitivesPageHeading = new StaticCaption(BallerinaDocConstants.PRIMITIVE_TYPES_PAGE_NAME);
        return new Page(primitivesPageHeading, primitiveTypes, links);
    }
    
    /**
     * Create documentation for enums.
     * @param enumNode ballerina enum node.
     * @return documentation for enum.
     */
    public static EnumDoc createDocForNode(EnumNode enumNode) {
        String enumName = enumNode.getName().getValue();
        List<Variable> enumerators = new ArrayList<>();

        // Iterate through the enumerators
        if (enumNode.getEnumerators().size() > 0) {
            for (EnumNode.Enumerator enumerator : enumNode.getEnumerators()) {
                String desc = fieldAnnotation((BLangNode) enumNode, (BLangNode) enumerator);
                Variable variable = new Variable(enumerator.getName().getValue(), "", desc);
                enumerators.add(variable);
            }
        }
        return new EnumDoc(enumName, description((BLangNode) enumNode), new ArrayList<>(), enumerators);
    }

    /**
     * Create documentation for annotations.
     * @param annotationNode ballerina annotation node.
     * @return documentation for annotation.
     */
    public static AnnotationDoc createDocForNode(BLangAnnotation annotationNode) {
        String annotationName = annotationNode.getName().getValue();
        List<Variable> attributes = new ArrayList<>();

        // Iterate through the attributes of the annotation
        if (annotationNode.getAttributes().size() > 0) {
            for (BLangAnnotAttribute annotAttribute : annotationNode.getAttributes()) {
                String dataType = getTypeName(annotAttribute.getTypeNode());
                String desc = annotFieldAnnotation(annotationNode, annotAttribute);
                Variable variable = new Variable(annotAttribute.getName().value, dataType, desc);
                attributes.add(variable);
            }
        }
        return new AnnotationDoc(annotationName, description(annotationNode), new ArrayList<>(), attributes);
    }

    /**
     * Create documentation for global variables.
     * @param bLangVariable ballerina variable node.
     * @return documentation for global variables.
     */
    public static GlobalVariableDoc createDocForNode(BLangVariable bLangVariable) {
        String globalVarName = bLangVariable.getName().getValue();
        String dataType = getTypeName(bLangVariable.getTypeNode());
        String desc = description(bLangVariable);
        return new GlobalVariableDoc(globalVarName, desc, new ArrayList<>(), dataType);
    }

    /**
     * Create documentation for functions.
     * @param functionNode ballerina function node.
     * @return documentation for functions.
     */
    public static FunctionDoc createDocForNode(BLangFunction functionNode) {
        String functionName = functionNode.getName().value;
        List<Variable> parameters = new ArrayList<>();
        List<Variable> returnParams = new ArrayList<>();
        // Iterate through the parameters
        if (functionNode.getParameters().size() > 0) {
            for (BLangVariable param : functionNode.getParameters()) {
                String dataType = type(param);
                String desc = paramAnnotation(functionNode, param);
                Variable variable = new Variable(param.getName().value, dataType, desc);
                parameters.add(variable);
            }
        }

        // Iterate through the return types
        if (functionNode.getReturnParameters().size() > 0) {
            for (int i = 0; i < functionNode.getReturnParameters().size(); i++) {
                BLangVariable returnParam = functionNode.getReturnParameters().get(i);
                String dataType = type(returnParam);
                String desc = returnParamAnnotation(functionNode, i);
                Variable variable = new Variable(returnParam.getName().value, dataType, desc);
                returnParams.add(variable);
            }
        }
        return new FunctionDoc(functionName, description(functionNode), new ArrayList<>(), parameters, returnParams);
    }

    /**
     * Create documentation for actions.
     * @param actionNode ballerina action node.
     * @return documentation for actions.
     */
    public static ActionDoc createDocForNode(BLangAction actionNode) {
        String actionName = actionNode.getName().value;
        List<Variable> parameters = new ArrayList<>();
        List<Variable> returnParams = new ArrayList<>();
        // Iterate through the parameters
        if (actionNode.getParameters().size() > 0) {
            for (BLangVariable param : actionNode.getParameters()) {
                String dataType = type(param);
                String desc = paramAnnotation(actionNode, param);
                Variable variable = new Variable(param.getName().value, dataType, desc);
                parameters.add(variable);
            }
        }

        // Iterate through the return types
        if (actionNode.getReturnParameters().size() > 0) {
            for (int i = 0; i < actionNode.getReturnParameters().size(); i++) {
                BLangVariable returnParam = actionNode.getReturnParameters().get(i);
                String dataType = type(returnParam);
                String desc = returnParamAnnotation(actionNode, i);
                Variable variable = new Variable(returnParam.getName().value, dataType, desc);
                returnParams.add(variable);
            }
        }
        return new ActionDoc(actionName, description(actionNode), new ArrayList<>(),
                parameters, returnParams);
    }

    /**
     * Create documentation for structs.
     * @param structNode ballerina struct node.
     * @return documentation for structs.
     */
    public static StructDoc createDocForNode(BLangStruct structNode) {
        String structName = structNode.getName().value;
        // Check if its an anonymous struct
        if (structName.contains(ANONYMOUS_STRUCT)) {
            structName = "Anonymous Struct";
        }
        List<Field> fields = new ArrayList<>();

        // Iterate through the struct fields
        if (structNode.getFields().size() > 0) {
            for (BLangVariable param : structNode.getFields()) {
                String dataType = type(param);
                String desc = fieldAnnotation(structNode, param);
                String defaultValue = "";
                if (null != param.getInitialExpression()) {
                    defaultValue = param.getInitialExpression().toString();
                }
                Field variable = new Field(param.getName().value, dataType, desc, defaultValue);
                fields.add(variable);
            }
        }

        return new StructDoc(structName, description(structNode), new ArrayList<>(), fields);
    }

    /**
     * Create documentation for connectors.
     * @param connectorNode ballerina connector node.
     * @return documentation for connectors.
     */
    public static ConnectorDoc createDocForNode(BLangConnector connectorNode) {
        String connectorName = connectorNode.getName().value;
        List<Variable> parameters = new ArrayList<>();
        List<Documentable> actions = new ArrayList<>();

        // Iterate through the connector parameters
        if (connectorNode.getParameters().size() > 0) {
            for (BLangVariable param : connectorNode.getParameters()) {
                String dataType = type(param);
                String desc = paramAnnotation(connectorNode, param);
                Variable variable = new Variable(param.getName().value, dataType, desc);
                parameters.add(variable);
            }
        }

        //Iterate through the actions of the connectors
        if (connectorNode.getActions().size() > 0) {
            for (BLangAction action : connectorNode.getActions()) {
                actions.add(createDocForNode(action));
            }
        }
        return new ConnectorDoc(connectorName, description(connectorNode), actions, parameters);
    }

    /**
     * Get the type of the variable.
     * @param bLangVariable
     * @return data type of the variable.
     */
    private static String type(final BLangVariable bLangVariable) {
        if (bLangVariable.typeNode.getKind() == NodeKind.USER_DEFINED_TYPE) {
            if (((BLangUserDefinedType) bLangVariable.typeNode).typeName.value.contains(ANONYMOUS_STRUCT)) {
                return getAnonStructString((BStructType) bLangVariable.type);
            }
        }
        return getTypeName(bLangVariable.typeNode);
    }

    /**
     * Get the type name of the type node.
     * @param bLangType
     * @return type name.
     */
    private static String getTypeName(BLangType bLangType) {
        return (bLangType instanceof BLangUserDefinedType ?
                ((BLangUserDefinedType) bLangType).typeName.value : bLangType.toString());
    }

    /**
     * Get the annotation attachments for the node.
     * @param node
     * @return list of annotation attachments.
     */
    private static List<? extends AnnotationAttachmentNode> getAnnotationAttachments(BLangNode node) {
        return ((AnnotatableNode) node).getAnnotationAttachments();
    }

    /**
     * Get description annotation of the parameter.
     * @param node parent node.
     * @param param parameter.
     * @return description of the parameter.
     */
    private static String paramAnnotation(BLangNode node, BLangVariable param) {
        String subName =
                param.getName() == null ? param.type.tsymbol.name.value : param.getName().getValue();
        for (AnnotationAttachmentNode annotation : getAnnotationAttachments(node)) {
            BLangRecordLiteral bLangRecordLiteral = (BLangRecordLiteral) annotation.getExpression();
            if (bLangRecordLiteral.getKeyValuePairs().size() != 1) {
                continue;
            }
            BLangExpression bLangLiteral = bLangRecordLiteral.getKeyValuePairs().get(0).getValue();
            String attribVal = bLangLiteral.toString();
            if ((annotation.getAnnotationName().getValue().equals("Param")) &&
                    attribVal.startsWith(subName + ":")) {
                return attribVal.split(subName + ":")[1].trim();
            }
        }
        return "";
    }

    /**
     * Get description annotation of the return parameter.
     * @param node parent node.
     * @param returnTypeIndex The index of the return type.
     * @return description of the return parameter.
     */
    private static String returnParamAnnotation(BLangNode node, int returnTypeIndex) {
        int currentReturnAnnotationIndex = 0;
        for (AnnotationAttachmentNode annotation : getAnnotationAttachments(node)) {
            BLangRecordLiteral bLangRecordLiteral = (BLangRecordLiteral) annotation.getExpression();
            if (bLangRecordLiteral.getKeyValuePairs().size() != 1) {
                continue;
            }
            if (annotation.getAnnotationName().getValue().equals("Return")) {
                if (currentReturnAnnotationIndex == returnTypeIndex) {
                    BLangExpression bLangLiteral = bLangRecordLiteral.getKeyValuePairs().get(0).getValue();
                    return bLangLiteral.toString();
                }
                currentReturnAnnotationIndex++;
            }
        }
        return "";
    }

    /**
     * Get description annotation of the field.
     * @param node parent node.
     * @param param field.
     * @return description of the field.
     */
    private static String fieldAnnotation(BLangNode node, BLangNode param) {
        String subName = "";
        if (param instanceof BLangVariable) {
            BLangVariable paramVariable = (BLangVariable) param;
            subName = (paramVariable.getName() == null) ? paramVariable.type.tsymbol.name.value :
                    paramVariable.getName().getValue();
        } else if (param instanceof BLangEnum.Enumerator) {
            BLangEnum.Enumerator paramEnumVal = (BLangEnum.Enumerator) param;
            subName = paramEnumVal.getName().getValue();
        }

        for (AnnotationAttachmentNode annotation : getAnnotationAttachments(node)) {
            BLangRecordLiteral bLangRecordLiteral = (BLangRecordLiteral) annotation.getExpression();
            if (bLangRecordLiteral.getKeyValuePairs().size() != 1) {
                continue;
            }
            BLangExpression bLangLiteral = bLangRecordLiteral.getKeyValuePairs().get(0).getValue();
            String attribVal = bLangLiteral.toString();
            if (annotation.getAnnotationName().getValue().equals("Field") && attribVal.startsWith(subName + ":")) {
                return attribVal.split(subName + ":")[1].trim();
            }
        }
        // if the annotation values cannot be found still, return the first matching
        // annotation's value
        for (AnnotationAttachmentNode annotation : getAnnotationAttachments(node)) {
            BLangRecordLiteral bLangRecordLiteral = (BLangRecordLiteral) annotation.getExpression();
            if (bLangRecordLiteral.getKeyValuePairs().size() != 1) {
                continue;
            }
            if (annotation.getAnnotationName().getValue().equals("Field")) {
                BLangExpression bLangLiteral = bLangRecordLiteral.getKeyValuePairs().get(0).getValue();
                return bLangLiteral.toString();
            }
        }
        return "";
    }

    /**
     * Get description annotation of the annotation attribute.
     * @param annotationNode parent node.
     * @param annotAttribute annotation attribute.
     * @return description of the annotation attribute.
     */
    private static String annotFieldAnnotation(BLangAnnotation annotationNode, BLangAnnotAttribute annotAttribute) {
        List<? extends AnnotationAttachmentNode> annotationAttachments = getAnnotationAttachments(annotationNode);

        for (AnnotationAttachmentNode annotation : annotationAttachments) {
            if ("Field".equals(annotation.getAnnotationName().getValue())) {
                BLangRecordLiteral bLangRecordLiteral = (BLangRecordLiteral) annotation.getExpression();
                BLangExpression bLangLiteral = bLangRecordLiteral.getKeyValuePairs().get(0).getValue();
                String value = bLangLiteral.toString();
                if (value.startsWith(annotAttribute.getName().getValue())) {
                    String[] valueParts = value.split(":");
                    return valueParts.length == 2 ? valueParts[1] : valueParts[0];
                }
            }
        }
        return "";
    }

    /**
     * Get the description annotation of the node.
     * @param node top level node.
     * @return description of the node.
     */
    private static String description(BLangNode node) {
        if (getAnnotationAttachments(node).size() == 0) {
            return null;
        }
        for (AnnotationAttachmentNode annotation : getAnnotationAttachments(node)) {
            BLangRecordLiteral bLangRecordLiteral = (BLangRecordLiteral) annotation.getExpression();
            if (bLangRecordLiteral.getKeyValuePairs().size() != 1) {
                continue;
            }
            if (annotation.getAnnotationName().getValue().equals("Description")) {
                BLangExpression bLangLiteral =  bLangRecordLiteral.getKeyValuePairs().get(0).getValue();
                return bLangLiteral.toString();
            }
        }
        return "";
    }

    /**
     * Get the anonymous struct string.
     * @param type struct type.
     * @return anonymous struct string.
     */
    private static String getAnonStructString(BStructType type) {
        StringBuilder builder = new StringBuilder();
        builder.append("struct {");

        BStructType.BStructField field;
        int nFields = type.fields.size();
        for (int i = 0; i < nFields; i++) {
            field = type.fields.get(i);
            builder.append(field.type.toString()).append(" ").append(field.name.value);
            if (i == nFields - 1) {
                return builder.append("}").toString();
            }
            builder.append(", ");
        }

        return builder.append("}").toString();
    }
}
