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

import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.codegen.ObjectTypeInfo;
import org.ballerinalang.util.codegen.TypeInfo;

/**
 * {@code BObjectType} represents a user defined object type in Ballerina.
 *
 * @since 0.971.0
 */
public class BObjectType extends BStructureType {

    public ObjectTypeInfo objectTypeInfo;

    /**
     * Create a {@code BStructType} which represents the user defined struct type.
     *
     * @param objectTypeInfo object type info object
     * @param typeName string name of the type
     * @param pkgPath  package of the struct
     * @param flags flags of the object type
     */
    public BObjectType(ObjectTypeInfo objectTypeInfo, String typeName, String pkgPath, int flags) {
        super(typeName, pkgPath, flags, BMap.class);
        this.objectTypeInfo = objectTypeInfo;
    }

    public TypeInfo getTypeInfo() {
        return objectTypeInfo;
    }

    @Override
    public <V extends BValue> V getZeroValue() {
        return null;
    }

    @Override
    public <V extends BValue> V getEmptyValue() {
        return (V) new BMap<>(this);
    }

    @Override
    public TypeSignature getSig() {
        String packagePath = (pkgPath == null) ? "." : pkgPath;
        return new TypeSignature(TypeSignature.SIG_STRUCT, packagePath, typeName);
    }

    @Override
    public int getTag() {
        return TypeTags.OBJECT_TYPE_TAG;
    }

}

