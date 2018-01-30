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
package org.wso2.ballerinalang.compiler.semantics.model.symbols;

import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.model.symbols.VariableSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.util.Name;
import org.wso2.ballerinalang.programfile.Instruction.RegIndex;

import static org.wso2.ballerinalang.compiler.semantics.model.symbols.SymTag.VARIABLE;

/**
 * @since 0.94
 */
public class BVarSymbol extends BSymbol implements VariableSymbol {

    /**
     * Represent the index of the variable in a memory block of the VM.
     */
    public RegIndex varIndex;

    public BVarSymbol(int flags, Name name, PackageID pkgID, BType type, BSymbol owner) {
        super(VARIABLE, flags, name, pkgID, type, owner);
    }

    @Override
    public Object getConstValue() {
        return null;
    }
}
