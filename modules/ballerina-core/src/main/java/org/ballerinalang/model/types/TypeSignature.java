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

/**
 * @since 0.87
 */
public class TypeSignature {
    public static final String SIG_INT = "I";
    public static final String SIG_FLOAT = "F";
    public static final String SIG_STRING = "S";
    public static final String SIG_BOOLEAN = "B";
    public static final String SIG_BLOB = "L";
    public static final String SIG_REFTYPE = "R";
    public static final String SIG_CONNECTOR = "C";
    public static final String SIG_STRUCT = "T";
    public static final String SIG_ARRAY = "[";
    public static final String SIG_ANY = "A";
    public static final String SIG_VOID = "V";
    public static final String SIG_ANNOTATION = "@";

    private String sigChar;
    private TypeSignature elementTypeSig;
    private String pkgPath;
    private String name;

    public TypeSignature(String sigChar) {
        this.sigChar = sigChar;
    }

    public TypeSignature(String sigChar, TypeSignature elementTypeSig) {
        this(sigChar);
        this.elementTypeSig = elementTypeSig;
    }

    public TypeSignature(String sigChar, String name) {
        this(sigChar);
        this.name = name;
    }

    public TypeSignature(String sigChar, String packagePath, String name) {
        this(sigChar);
        this.pkgPath = packagePath;
        this.name = name;
    }

    public String getSigChar() {
        return sigChar;
    }

    public String getPkgPath() {
        return pkgPath;
    }

    public String getName() {
        return name;
    }

    public TypeSignature getElementTypeSig() {
        return elementTypeSig;
    }

    public static String getTypeSignature(String signature) {
        return null;
    }


}
