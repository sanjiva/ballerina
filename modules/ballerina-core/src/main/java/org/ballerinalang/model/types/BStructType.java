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
package org.ballerinalang.model.types;

import org.ballerinalang.model.Identifier;
import org.ballerinalang.model.SymbolScope;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;

/**
 * {@code BStructType} represents a user defined {@code StructDef} in Ballerina.
 *
 * @since 1.0.0
 */
public class BStructType extends BType {

    private Identifier identifier;

    /**
     * Create a {@code BStructType} which represents the user defined struct type.
     *
     * @param typeName string name of the type
     * @param pkgPath package of the struct
     * @param symbolScope symbol scope of the struct
     * @param identifier identifier with string name
     */
    public BStructType(String typeName, String pkgPath, SymbolScope symbolScope, Identifier identifier) {
        super(typeName, pkgPath, symbolScope, BStruct.class);
        this.identifier = identifier;
    }

    @Override
    public <V extends BValue> V getZeroValue() {
        return null;
    }

    @Override
    public <V extends BValue> V getEmptyValue() {
        return (V) new BStruct();
    }

    @Override
    public String getName() {
        return identifier.getName();
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }
}

