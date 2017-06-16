/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.model.types;

import org.ballerinalang.model.SymbolScope;
import org.ballerinalang.model.values.BBlob;
import org.ballerinalang.model.values.BValue;

/**
 * {@code BBlob} represents a byte[].
 *
 * @since 0.88
 */
public class BBlobType extends BType {
    protected BBlobType(SymbolScope symbolScope) {
        super(symbolScope);
    }

    protected BBlobType(String typeName, String pkgPath, SymbolScope symbolScope) {
        super(typeName, pkgPath, symbolScope, BBlob.class);
    }

    @Override public <V extends BValue> V getZeroValue() {
        return null;
    }

    @Override public <V extends BValue> V getEmptyValue() {
        return null;
    }

    @Override public TypeSignature getSig() {
        return new TypeSignature(TypeSignature.SIG_BLOB);
    }

    @Override public int getTag() {
        return TypeTags.BLOB_TAG;
    }
}
