/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.model;

import org.ballerinalang.model.expressions.BasicLiteral;
import org.ballerinalang.model.symbols.BLangSymbol;
import org.ballerinalang.model.types.SimpleTypeName;

/**
 * {@code AnnotationAttributeDef} represent a attribute definition in an annotation.
 * <p>
 * Attributes may be of type string, int, double, boolean, or can be another annotation.
 *
 * @since 0.85
 */
public class AnnotationAttributeDef implements BLangSymbol, Node {
    protected NodeLocation location;
    protected WhiteSpaceDescriptor whiteSpaceDescriptor;
    protected SimpleTypeName typeName;
    protected BasicLiteral annotationAttributeVal;

    // BLangSymbol related attributes
    protected Identifier identifier;
    protected String pkgPath;
    protected boolean isPublic = true;
    protected boolean isNative = false;
    protected SymbolName symbolName;
    protected SymbolScope symbolScope;

    public AnnotationAttributeDef(NodeLocation location,
                                  Identifier identifier,
                                  SimpleTypeName typeName,
                                  BasicLiteral annotationAttributeVal,
                                  SymbolName symbolName,
                                  SymbolScope symbolScope,
                                  String pkgPath) {
        this.location = location;
        this.typeName = typeName;
        this.identifier = identifier;
        this.annotationAttributeVal = annotationAttributeVal;
        this.symbolName = symbolName;
        this.symbolScope = symbolScope;
        this.pkgPath = pkgPath;
    }

    public SimpleTypeName getTypeName() {
        return typeName;
    }
    
    public BasicLiteral getAttributeValue() {
        return annotationAttributeVal;
    }

    // Methods in BLangSymbol interface

    @Override
    public String getName() {
        return identifier.getName();
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public String getPackagePath() {
        return pkgPath;
    }

    @Override
    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public boolean isNative() {
        return isNative;
    }

    @Override
    public SymbolName getSymbolName() {
        return symbolName;
    }

    @Override
    public SymbolScope getSymbolScope() {
        return symbolScope;
    }


    // Methods in Node interface

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeLocation getNodeLocation() {
        return location;
    }

    public void setWhiteSpaceDescriptor(WhiteSpaceDescriptor whiteSpaceDescriptor) {
        this.whiteSpaceDescriptor = whiteSpaceDescriptor;
    }

    @Override
    public WhiteSpaceDescriptor getWhiteSpaceDescriptor() {
        return whiteSpaceDescriptor;
    }
}
