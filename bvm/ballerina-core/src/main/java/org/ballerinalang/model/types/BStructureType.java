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
package org.ballerinalang.model.types;

import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.codegen.TypeInfo;

/**
 * {@code BStructureType} represents a user defined structure type in Ballerina.
 *
 * @since 0.971.0
 */
public abstract class BStructureType extends BType {

    private BField[] fields;
    private int[] fieldTypeCount;
    private BAttachedFunction[] attachedFunctions;
    public BAttachedFunction initializer;
    public BAttachedFunction defaultsValuesInitFunc;
    public int flags;

    /**
     * Create a {@code BStructType} which represents the user defined struct type.
     *
     * @param typeName string name of the type
     * @param pkgPath  package of the struct
     * @param flags of the structure type
     * @param valueClass of the structure type
     */
    public BStructureType(String typeName, String pkgPath, int flags, Class<? extends BValue> valueClass) {
        super(typeName, pkgPath, valueClass);
        this.flags = flags;
    }

    public BField[] getFields() {
        return fields;
    }

    public void setFields(BField[] fields) {
        this.fields = fields;
    }

    public int[] getFieldTypeCount() {
        return fieldTypeCount;
    }

    public void setFieldTypeCount(int[] fieldCount) {
        this.fieldTypeCount = fieldCount;
    }

    public BAttachedFunction[] getAttachedFunctions() {
        return attachedFunctions;
    }

    public void setAttachedFunctions(BAttachedFunction[] attachedFunctions) {
        this.attachedFunctions = attachedFunctions;
    }

    public abstract TypeInfo getTypeInfo();
}

