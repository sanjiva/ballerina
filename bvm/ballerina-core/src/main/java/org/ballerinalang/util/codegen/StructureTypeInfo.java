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
package org.ballerinalang.util.codegen;

import org.ballerinalang.model.types.BStructureType;
import org.ballerinalang.util.codegen.attributes.AttributeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code StructureTypeInfo} contains metadata of a Ballerina structure entry in the program file.
 *
 * @since 0.971.0
 */
public abstract class StructureTypeInfo implements TypeInfo {

    private List<StructFieldInfo> fieldInfoEntries = new ArrayList<>();
    public Map<String, AttachedFunctionInfo> funcInfoEntries = new HashMap<>();
    //TODO separate below when record init function is removed
    public AttachedFunctionInfo initializer;
    public AttachedFunctionInfo defaultsValuesInitFunc;

    private Map<AttributeInfo.Kind, AttributeInfo> attributeInfoMap = new HashMap<>();

    public void addFieldInfo(StructFieldInfo fieldInfo) {
        fieldInfoEntries.add(fieldInfo);
    }

    public StructFieldInfo[] getFieldInfoEntries() {
        return fieldInfoEntries.toArray(new StructFieldInfo[0]);
    }

    @Override
    public AttributeInfo getAttributeInfo(AttributeInfo.Kind attributeKind) {
        return attributeInfoMap.get(attributeKind);
    }

    @Override
    public void addAttributeInfo(AttributeInfo.Kind attributeKind, AttributeInfo attributeInfo) {
        attributeInfoMap.put(attributeKind, attributeInfo);
    }

    @Override
    public AttributeInfo[] getAttributeInfoEntries() {
        return attributeInfoMap.values().toArray(new AttributeInfo[0]);
    }

    public abstract BStructureType getType();
}
