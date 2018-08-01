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
package org.ballerinalang.compiler;

/**
 * @since 0.94
 */
public enum CompilerPhase {

    DEFINE("define"),

    TYPE_CHECK("typeCheck"),

    CODE_ANALYZE("codeAnalyze"),

    DOCUMENTATION_ANALYZE("documentationAnalyze"),

    TAINT_ANALYZE("taintAnalyze"),

    COMPILER_PLUGIN("compilerPlugin"),

    DESUGAR("desugar"),

    CODE_GEN("codeGen");

    private String value;

    CompilerPhase(String value) {
        this.value = value;
    }

    public static CompilerPhase fromValue(String value) {
        switch (value) {
            case "define":
                return DEFINE;
            case "typeCheck":
                return TYPE_CHECK;
            case "codeAnalyze":
                return CODE_ANALYZE;
            case "documentationAnalyzer":
                return DOCUMENTATION_ANALYZE;
            case "taintAnalyze":
                return TAINT_ANALYZE;
            case "annotationProcess":
                return COMPILER_PLUGIN;
            case "desugar":
                return DESUGAR;
            case "codeGen":
                return CODE_GEN;
            default:
                throw new IllegalArgumentException("invalid compiler phase: " + value);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
