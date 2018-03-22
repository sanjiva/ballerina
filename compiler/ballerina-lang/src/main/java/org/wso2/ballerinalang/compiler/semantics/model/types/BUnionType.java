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
package org.wso2.ballerinalang.compiler.semantics.model.types;

import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.types.UnionType;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BTypeSymbol;
import org.wso2.ballerinalang.compiler.util.TypeDescriptor;
import org.wso2.ballerinalang.compiler.util.TypeTags;

import java.util.Set;
import java.util.StringJoiner;

/**
 * {@code UnionType} represents a union type in Ballerina.
 *
 * @since 0.966.0
 */
public class BUnionType extends BType implements UnionType {
    private boolean nullable;

    public Set<BType> memberTypes;

    public BUnionType(BTypeSymbol tsymbol, Set<BType> memberTypes, boolean nullable) {
        super(TypeTags.UNION, tsymbol);
        this.memberTypes = memberTypes;
        this.nullable = nullable;
    }

    @Override
    public Set<BType> getMemberTypes() {
        return memberTypes;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.UNION;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public <T, R> R accept(BTypeVisitor<T, R> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(getKind().typeName());
        this.memberTypes.forEach(memberType -> joiner.add(memberType.toString()));
        return joiner.toString();
    }

    @Override
    public String getDesc() {
        return TypeDescriptor.SIG_ANY;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
}
