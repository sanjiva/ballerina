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

/**
 * {@code AttachedFunctionInfo} represents an attached function in the program file.
 * <p>
 * In Ballerina, you can attach functions to types, hence the name.
 *
 * @since 0.96
 */
public class AttachedFunctionInfo {
    public int nameCPIndex;
    public String name;

    public int signatureCPIndex;
    public String typeSignature;

    public int flags;

    public FunctionInfo functionInfo;

    public AttachedFunctionInfo(int nameCPIndex, String name, int signatureCPIndex,
                                String typeSignature, int flags) {
        this.nameCPIndex = nameCPIndex;
        this.name = name;
        this.signatureCPIndex = signatureCPIndex;
        this.typeSignature = typeSignature;
        this.flags = flags;
    }

    public static String getUniqueFuncName(String pkgQualifiedTypeName, String funcName) {
        return pkgQualifiedTypeName + "." + funcName;
    }
}
