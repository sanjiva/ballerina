/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballerinalang.langserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.quartz.utils.FindbugsSuppressWarnings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Auto generated source generation class for java.
 */
public class SourceGen {
    private static final String TAB = "    ";
    private int l = 0;
    private Map<String, JsonObject> anonTypes = new HashMap<>();

    public SourceGen(int l) {
        this.l = l;
    }

    // auto gen start
    
        public String getSourceForPackageDeclaration(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "package" + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("packageName"), pretty, replaceLambda, "", ".", false, sourceGenParams) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForImport(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("isInternal") != null
                         && node.get("isInternal") .getAsBoolean()) {
                return "";
            } else if (node.get("userDefinedAlias") != null
                         && node.get("userDefinedAlias") .getAsBoolean()
                         && node.getAsJsonObject("orgName").get("valueWithBar") != null
                         && !node.getAsJsonObject("orgName").get("valueWithBar").getAsString().isEmpty()
                         && node.get("packageName") != null && node.getAsJsonObject("alias").get("valueWithBar") != null
                         && !node.getAsJsonObject("alias").get("valueWithBar").getAsString().isEmpty()) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "import" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams)
                 + node.getAsJsonObject("orgName").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "/" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("packageName"), pretty, replaceLambda, "", ".", false, sourceGenParams) + w(" ", sourceGenParams)
                 + "as" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("alias").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("userDefinedAlias") != null
                         && node.get("userDefinedAlias") .getAsBoolean() && node.get("packageName") != null
                         && node.getAsJsonObject("alias").get("valueWithBar") != null
                         && !node.getAsJsonObject("alias").get("valueWithBar").getAsString().isEmpty()) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "import" + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("packageName"), pretty, replaceLambda, "", ".", false, sourceGenParams) + w(" ", sourceGenParams) + "as"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("alias").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + ";" + a("", sourceGenParams.isShouldIndent());
            } else if (node.getAsJsonObject("orgName").get("valueWithBar") != null
                         && !node.getAsJsonObject("orgName").get("valueWithBar").getAsString().isEmpty() && node.get("packageName") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "import" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams)
                 + node.getAsJsonObject("orgName").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "/" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("packageName"), pretty, replaceLambda, "", ".", false, sourceGenParams) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "import" + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("packageName"), pretty, replaceLambda, "", ".", false, sourceGenParams) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForIdentifier(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + node.get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForAbort(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "abort" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForAction(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("parameters") != null
                         && node.get("returnParameters") != null
                         && node.getAsJsonArray("returnParameters").size() > 0 && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "action" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("parameters"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams)
                 + ")" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnParameters"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "action" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("parameters"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams)
                 + ")" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForAnnotation(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + "annotation"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + "<" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("attachmentPoints"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ">"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForAnnotationAttachment(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("builtin") != null
                         && node.get("builtin") .getAsBoolean()
                         && node.getAsJsonObject("annotationName").get("valueWithBar") != null
                         && !node.getAsJsonObject("annotationName").get("valueWithBar").getAsString().isEmpty() && node.get("expression") != null) {
                return w("", sourceGenParams) + "@"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("annotationName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
            } else if (node.get("builtin") != null
                         && node.get("builtin") .getAsBoolean()
                         && node.getAsJsonObject("annotationName").get("valueWithBar") != null
                         && !node.getAsJsonObject("annotationName").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams) + "@"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("annotationName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.getAsJsonObject("packageAlias").get("valueWithBar") != null
                         && !node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("annotationName").get("valueWithBar") != null
                         && !node.getAsJsonObject("annotationName").get("valueWithBar").getAsString().isEmpty()
                         && node.get("expression") != null) {
                return w("", sourceGenParams) + "@"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ":"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("annotationName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
            } else if (node.getAsJsonObject("packageAlias").get("valueWithBar") != null
                         && !node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("annotationName").get("valueWithBar") != null
                         && !node.getAsJsonObject("annotationName").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams) + "@"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ":"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("annotationName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.getAsJsonObject("annotationName").get("valueWithBar") != null
                         && !node.getAsJsonObject("annotationName").get("valueWithBar").getAsString().isEmpty() && node.get("expression") != null) {
                return w("", sourceGenParams) + "@"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("annotationName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
            } else {
                return w("", sourceGenParams) + "@"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("annotationName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForArrayLiteralExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "["
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("expressions"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams)
                 + "]" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForArrayType(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("isRestParam") != null
                         && node.get("isRestParam") .getAsBoolean() && node.get("grouped") != null
                         && node.get("grouped") .getAsBoolean() && node.get("elementType") != null) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("elementType"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("isRestParam") != null
                         && node.get("isRestParam") .getAsBoolean() && node.get("elementType") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("elementType"), pretty, replaceLambda);
            } else if (node.get("grouped") != null
                         && node.get("grouped") .getAsBoolean() && node.get("elementType") != null
                         && node.get("dimensionAsString") != null) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("elementType"), pretty, replaceLambda)
                 + w("", sourceGenParams) + node.get("dimensionAsString").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + ")" + a("", sourceGenParams.isShouldIndent());
            } else {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("elementType"), pretty, replaceLambda)
                 + w("", sourceGenParams) + node.get("dimensionAsString").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForAssignment(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent())
                 + (node.has("declaredWithVar") && node.get("declaredWithVar").getAsBoolean() ? w("", sourceGenParams) + "var"
                 + a(" ", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("variable"), pretty, replaceLambda)
                 + w(" ", sourceGenParams) + "=" + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForAwaitExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "await"
                 + a(" ", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
        }
        public String getSourceForBinaryExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("inTemplateLiteral") != null
                         && node.get("inTemplateLiteral") .getAsBoolean() && node.get("leftExpression") != null
                         && node.get("operatorKind") != null
                         && node.get("rightExpression") != null) {
                return w("", sourceGenParams) + "{{"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("leftExpression"), pretty, replaceLambda)
                 + w(" ", sourceGenParams) + node.get("operatorKind").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("rightExpression"), pretty, replaceLambda) + w("", sourceGenParams) + "}}"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("leftExpression"), pretty, replaceLambda)
                 + w(" ", sourceGenParams) + node.get("operatorKind").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("rightExpression"), pretty, replaceLambda);
            }
        }
        public String getSourceForBind(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "bind" + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda) + w(" ", sourceGenParams)
                 + "with" + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("variable"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForBlock(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("isElseBlock") != null
                         && node.get("isElseBlock") .getAsBoolean() && node.get("statements") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "else" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent()
                 + join(node.getAsJsonArray("statements"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return join(node.getAsJsonArray("statements"), pretty, replaceLambda, "", null, false, sourceGenParams);
            }
        }
        public String getSourceForBreak(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "break" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForBracedTupleExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("expressions"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams)
                 + ")" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForBuiltInRefType(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + node.get("typeKind").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForCatch(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "catch" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("parameter"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForCheckExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "check"
                 + a(" ", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
        }
        public String getSourceForComment(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.get("comment").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForCompoundAssignment(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("variable"), pretty, replaceLambda) + w("", sourceGenParams) + "+="
                 + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForConnector(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("parameters") != null
                         && node.get("variableDefs") != null && node.get("actions") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "connector"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("parameters"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("variableDefs"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("actions"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "connector"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("parameters"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("actions"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForConnectorInitExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("connectorType") != null
                         && node.get("expressions") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "create" + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("connectorType"), pretty, replaceLambda) + w("", sourceGenParams)
                 + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("expressions"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "create" + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("connectorType"), pretty, replaceLambda) + w("", sourceGenParams)
                 + "(" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForConstrainedType(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("type"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "<" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("constraint"), pretty, replaceLambda) + w("", sourceGenParams) + ">"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForDocumentation(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + node.get("startDoc").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.get("documentationText").getAsString()
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("attributes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForDocumentationAttribute(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + node.get("paramType").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "{{" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams)
                 + node.getAsJsonObject("documentationField").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}}"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.get("documentationText").getAsString() + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForDeprecated(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "deprecated" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + w("", sourceGenParams)
                 + node.get("documentationText").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForDone(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "done" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForElvisExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("leftExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "?:" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("rightExpression"), pretty, replaceLambda);
        }
        public String getSourceForEndpoint(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("skipSourceGen") != null
                         && node.get("skipSourceGen") .getAsBoolean()) {
                return "";
            } else if (node.get("isConfigAssignment") != null
                         && node.get("isConfigAssignment") .getAsBoolean()
                         && node.get("annotationAttachments") != null && node.get("endPointType") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("configurationExpression") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + "endpoint"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("endPointType"), pretty, replaceLambda) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("configurationExpression"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + "endpoint"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("endPointType"), pretty, replaceLambda) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("configurationExpression"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForEndpointType(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("constraint"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ">" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForExpressionStatement(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForFieldBasedAccessExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("errorLifting") != null
                         && node.get("errorLifting") .getAsBoolean() && node.get("expression") != null
                         && node.getAsJsonObject("fieldName").get("valueWithBar") != null
                         && !node.getAsJsonObject("fieldName").get("valueWithBar").getAsString().isEmpty()) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "!" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams)
                 + node.getAsJsonObject("fieldName").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent());
            } else {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "." + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams)
                 + node.getAsJsonObject("fieldName").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForForeach(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("withParantheses") != null
                         && node.get("withParantheses") .getAsBoolean() && node.get("variables") != null
                         && node.get("collection") != null && node.get("body") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "foreach" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("variables"), pretty, replaceLambda, " ", ",", false, sourceGenParams) + w(" ", sourceGenParams) + "in"
                 + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("collection"), pretty, replaceLambda) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "foreach" + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("variables"), pretty, replaceLambda, " ", ",", false, sourceGenParams) + w(" ", sourceGenParams) + "in"
                 + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("collection"), pretty, replaceLambda) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForForever(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "forever" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent()
                 + join(node.getAsJsonArray("streamingQueryStatements"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForForkJoin(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("workers") != null && node.get("joinType") != null
                         && node.get("joinCount").getAsInt() >= 0
                         && node.get("joinedWorkerIdentifiers") != null && node.get("joinResultVar") != null
                         && node.get("joinBody") != null && node.get("timeOutExpression") != null
                         && node.get("timeOutVariable") != null
                         && node.get("timeoutBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + dent(sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "fork"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "join" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + node.get("joinType").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.get("joinCount").getAsString()
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("joinedWorkerIdentifiers"), pretty, replaceLambda, " ", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinResultVar"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinBody"), pretty, replaceLambda) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "timeout"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("timeOutExpression"), pretty, replaceLambda) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("timeOutVariable"), pretty, replaceLambda) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("timeoutBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("workers") != null && node.get("joinType") != null
                         && node.get("joinCount").getAsInt() >= 0
                         && node.get("joinedWorkerIdentifiers") != null && node.get("joinResultVar") != null
                         && node.get("joinBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "fork"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "join"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.get("joinType").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + node.get("joinCount").getAsString()
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("joinedWorkerIdentifiers"), pretty, replaceLambda, " ", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinResultVar"), pretty, replaceLambda) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("workers") != null && node.get("joinType") != null
                         && node.get("joinCount").getAsInt() >= 0
                         && node.get("joinedWorkerIdentifiers") != null && node.get("joinResultVar") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "fork"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "join"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.get("joinType").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + node.get("joinCount").getAsString()
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("joinedWorkerIdentifiers"), pretty, replaceLambda, " ", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinResultVar"), pretty, replaceLambda) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("workers") != null && node.get("joinType") != null
                         && node.get("joinedWorkerIdentifiers") != null
                         && node.get("joinResultVar") != null && node.get("joinBody") != null
                         && node.get("timeOutExpression") != null && node.get("timeOutVariable") != null
                         && node.get("timeoutBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + dent(sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "fork"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "join" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + node.get("joinType").getAsString()
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("joinedWorkerIdentifiers"), pretty, replaceLambda, " ", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinResultVar"), pretty, replaceLambda) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "timeout"
                 + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("timeOutExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("timeOutVariable"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("timeoutBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("workers") != null && node.get("joinType") != null
                         && node.get("joinedWorkerIdentifiers") != null
                         && node.get("joinResultVar") != null && node.get("joinBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "fork"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "join"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.get("joinType").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("joinedWorkerIdentifiers"), pretty, replaceLambda, " ", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinResultVar"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "fork"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "join"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.get("joinType").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("joinedWorkerIdentifiers"), pretty, replaceLambda, " ", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinResultVar"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForFunction(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("defaultConstructor") != null
                         && node.get("defaultConstructor") .getAsBoolean()) {
                return "";
            } else if (node.get("isConstructor") != null
                         && node.get("isConstructor") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null
                         && node.get("restParameters") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + "," + a("", sourceGenParams.isShouldIndent()) : "")
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("isConstructor") != null
                         && node.get("isConstructor") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams)
                 + ")" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("interface") != null
                         && node.get("interface") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null
                         && node.get("restParameters") != null && node.get("hasReturns") != null
                         && node.get("hasReturns") .getAsBoolean()
                         && node.get("returnTypeAnnotationAttachments") != null
                         && node.get("returnTypeNode") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "returns"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnTypeAnnotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("interface") != null
                         && node.get("interface") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null
                         && node.get("restParameters") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("interface") != null
                         && node.get("interface") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null
                         && node.get("hasReturns") != null && node.get("hasReturns") .getAsBoolean()
                         && node.get("returnTypeAnnotationAttachments") != null
                         && node.get("returnTypeNode") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "returns"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnTypeAnnotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("interface") != null
                         && node.get("interface") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("lambda") != null && node.get("lambda") .getAsBoolean()
                         && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("isStreamAction") != null
                         && node.get("isStreamAction") .getAsBoolean() && node.get("allParams") != null
                         && node.get("restParameters") != null
                         && node.get("endpointNodes") != null && node.get("body") != null && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "=>" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams)
                 + "," + a("", sourceGenParams.isShouldIndent()) : "")
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("lambda") != null && node.get("lambda") .getAsBoolean()
                         && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("isStreamAction") != null
                         && node.get("isStreamAction") .getAsBoolean() && node.get("allParams") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "=>" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("lambda") != null && node.get("lambda") .getAsBoolean()
                         && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("allParams") != null
                         && node.get("restParameters") != null && node.get("hasReturns") != null
                         && node.get("hasReturns") .getAsBoolean() && node.get("returnTypeNode") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent()) : "")
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda) + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "=>" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda) + w("", sourceGenParams)
                 + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("lambda") != null && node.get("lambda") .getAsBoolean()
                         && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("allParams") != null
                         && node.get("hasReturns") != null && node.get("hasReturns") .getAsBoolean()
                         && node.get("returnTypeNode") != null && node.get("endpointNodes") != null
                         && node.get("body") != null && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "=>"
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("lambda") != null && node.get("lambda") .getAsBoolean()
                         && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("allParams") != null
                         && node.get("restParameters") != null && node.get("endpointNodes") != null
                         && node.get("body") != null && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent()) : "")
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda) + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "=>" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("lambda") != null && node.get("lambda") .getAsBoolean()
                         && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("allParams") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "=>"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("noVisibleReceiver") != null
                         && node.get("noVisibleReceiver") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("allParams") != null && node.get("restParameters") != null
                         && node.get("hasReturns") != null && node.get("hasReturns") .getAsBoolean()
                         && node.get("returnTypeAnnotationAttachments") != null
                         && node.get("returnTypeNode") != null && node.get("endpointNodes") != null
                         && node.get("body") != null && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "returns"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnTypeAnnotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("noVisibleReceiver") != null
                         && node.get("noVisibleReceiver") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("allParams") != null && node.get("hasReturns") != null
                         && node.get("hasReturns") .getAsBoolean()
                         && node.get("returnTypeAnnotationAttachments") != null && node.get("returnTypeNode") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "returns"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnTypeAnnotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("noVisibleReceiver") != null
                         && node.get("noVisibleReceiver") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("allParams") != null && node.get("restParameters") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("noVisibleReceiver") != null
                         && node.get("noVisibleReceiver") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("allParams") != null && node.get("endpointNodes") != null
                         && node.get("body") != null && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("objectOuterFunction") != null
                         && node.get("objectOuterFunction") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("objectOuterFunctionTypeName").get("valueWithBar") != null
                         && !node.getAsJsonObject("objectOuterFunctionTypeName").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null
                         && node.get("restParameters") != null
                         && node.get("hasReturns") != null && node.get("hasReturns") .getAsBoolean()
                         && node.get("returnTypeAnnotationAttachments") != null
                         && node.get("returnTypeNode") != null && node.get("endpointNodes") != null
                         && node.get("body") != null && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("objectOuterFunctionTypeName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "::" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent()) : "")
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda) + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "returns" + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnTypeAnnotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda) + w("", sourceGenParams)
                 + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("objectOuterFunction") != null
                         && node.get("objectOuterFunction") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("objectOuterFunctionTypeName").get("valueWithBar") != null
                         && !node.getAsJsonObject("objectOuterFunctionTypeName").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null
                         && node.get("hasReturns") != null
                         && node.get("hasReturns") .getAsBoolean() && node.get("returnTypeAnnotationAttachments") != null
                         && node.get("returnTypeNode") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("objectOuterFunctionTypeName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "::" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "returns" + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnTypeAnnotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("objectOuterFunction") != null
                         && node.get("objectOuterFunction") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("objectOuterFunctionTypeName").get("valueWithBar") != null
                         && !node.getAsJsonObject("objectOuterFunctionTypeName").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null
                         && node.get("restParameters") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("objectOuterFunctionTypeName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "::" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent()) : "")
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda) + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("objectOuterFunction") != null
                         && node.get("objectOuterFunction") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("objectOuterFunctionTypeName").get("valueWithBar") != null
                         && !node.getAsJsonObject("objectOuterFunctionTypeName").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("objectOuterFunctionTypeName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "::" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("hasReturns") != null
                         && node.get("hasReturns") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("receiver") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("allParams") != null && node.get("restParameters") != null
                         && node.get("returnTypeAnnotationAttachments") != null
                         && node.get("returnTypeNode") != null && node.get("endpointNodes") != null
                         && node.get("body") != null && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("receiver"), pretty, replaceLambda) + w("", sourceGenParams) + ">"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "returns"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnTypeAnnotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("hasReturns") != null
                         && node.get("hasReturns") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("receiver") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("allParams") != null
                         && node.get("returnTypeAnnotationAttachments") != null && node.get("returnTypeNode") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("receiver"), pretty, replaceLambda) + w("", sourceGenParams) + ">"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "returns"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnTypeAnnotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("hasReturns") != null
                         && node.get("hasReturns") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null
                         && node.get("restParameters") != null
                         && node.get("returnTypeAnnotationAttachments") != null && node.get("returnTypeNode") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "returns"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnTypeAnnotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("hasReturns") != null
                         && node.get("hasReturns") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null
                         && node.get("returnTypeAnnotationAttachments") != null
                         && node.get("returnTypeNode") != null && node.get("endpointNodes") != null
                         && node.get("body") != null && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "returns"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnTypeAnnotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("receiver") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("allParams") != null && node.get("restParameters") != null
                         && node.get("endpointNodes") != null && node.get("body") != null
                         && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("receiver"), pretty, replaceLambda) + w("", sourceGenParams) + ">"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("receiver") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("allParams") != null && node.get("endpointNodes") != null
                         && node.get("body") != null && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("receiver"), pretty, replaceLambda) + w("", sourceGenParams) + ">"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("allParams") != null
                         && node.get("restParameters") != null && node.get("endpointNodes") != null
                         && node.get("body") != null && node.get("workers") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + (node.has("hasRestParams") && node.get("hasRestParams").getAsBoolean() ? w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restParameters"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("allParams"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForFunctionType(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("hasReturn") != null
                         && node.get("hasReturn") .getAsBoolean() && node.get("withParantheses") != null
                         && node.get("withParantheses") .getAsBoolean() && node.get("paramTypeNode") != null
                         && node.get("returnTypeNode") != null) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("paramTypeNode"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent())
                 + (node.has("returnKeywordExists") && node.get("returnKeywordExists").getAsBoolean() ? w("", sourceGenParams) + "returns"
                 + a("", sourceGenParams.isShouldIndent()) : "")
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda) + w("", sourceGenParams)
                 + ")" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("hasReturn") != null
                         && node.get("hasReturn") .getAsBoolean() && node.get("paramTypeNode") != null
                         && node.get("returnTypeNode") != null) {
                return w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("paramTypeNode"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent())
                 + (node.has("returnKeywordExists") && node.get("returnKeywordExists").getAsBoolean() ? w("", sourceGenParams) + "returns"
                 + a("", sourceGenParams.isShouldIndent()) : "")
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("returnTypeNode"), pretty, replaceLambda);
            } else if (node.get("withParantheses") != null
                         && node.get("withParantheses") .getAsBoolean() && node.get("paramTypeNode") != null) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("paramTypeNode"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return w("", sourceGenParams) + "function"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("paramTypeNode"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForGroupBy(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "group"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "by"
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("variables"), pretty, replaceLambda, "", ",", false, sourceGenParams);
        }
        public String getSourceForHaving(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "having"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
        }
        public String getSourceForIf(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("ladderParent") != null
                         && node.get("ladderParent") .getAsBoolean() && node.get("isElseIfBlock") != null
                         && node.get("isElseIfBlock") .getAsBoolean() && node.get("condition") != null
                         && node.get("body") != null && node.get("elseStatement") != null) {
                return (node.getAsJsonObject("parent").get("kind").getAsString() .equals("If") ? "" : dent(sourceGenParams.isShouldIndent()))
                 + w(" ", sourceGenParams) + "else"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "if"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("condition"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("elseStatement"), pretty, replaceLambda);
            } else if (node.get("isElseIfBlock") != null
                         && node.get("isElseIfBlock") .getAsBoolean() && node.get("condition") != null
                         && node.get("body") != null && node.get("elseStatement") != null) {
                return (node.getAsJsonObject("parent").get("kind").getAsString() .equals("If") ? "" : dent(sourceGenParams.isShouldIndent()))
                 + w(" ", sourceGenParams) + "else"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "if"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("condition"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("elseStatement"), pretty, replaceLambda);
            } else if (node.get("isElseIfBlock") != null
                         && node.get("isElseIfBlock") .getAsBoolean() && node.get("condition") != null
                         && node.get("body") != null) {
                return (node.getAsJsonObject("parent").get("kind").getAsString() .equals("If") ? "" : dent(sourceGenParams.isShouldIndent()))
                 + w(" ", sourceGenParams) + "else"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "if"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("condition"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("ladderParent") != null
                         && node.get("ladderParent") .getAsBoolean() && node.get("condition") != null
                         && node.get("body") != null && node.get("elseStatement") != null) {
                return (node.getAsJsonObject("parent").get("kind").getAsString() .equals("If") ? "" : dent(sourceGenParams.isShouldIndent()))
                 + w("", sourceGenParams) + "if" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("condition"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("elseStatement"), pretty, replaceLambda);
            } else {
                return (node.getAsJsonObject("parent").get("kind").getAsString() .equals("If") ? "" : dent(sourceGenParams.isShouldIndent()))
                 + w("", sourceGenParams) + "if" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("condition"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForIndexBasedAccessExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "[" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("index"), pretty, replaceLambda) + w("", sourceGenParams)
                 + "]" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForIntRangeExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("isWrappedWithParenthesis") != null
                         && node.get("isWrappedWithParenthesis") .getAsBoolean()
                         && node.get("startExpression") != null && node.get("endExpression") != null) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("startExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ".."
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("endExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("isWrappedWithParenthesis") != null
                         && node.get("isWrappedWithParenthesis") .getAsBoolean()
                         && node.get("startExpression") != null) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("startExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ".."
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("isWrappedWithBracket") != null
                         && node.get("isWrappedWithBracket") .getAsBoolean()
                         && node.get("startExpression") != null && node.get("endExpression") != null) {
                return w(" ", sourceGenParams) + "["
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("startExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ".."
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("endExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "]"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return w(" ", sourceGenParams) + "["
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("startExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ".."
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "]"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForInvocation(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("actionInvocation") != null
                         && node.get("actionInvocation") .getAsBoolean() && node.get("expression") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("argumentExpressions") != null) {
                return (node.has("async") && node.get("async").getAsBoolean() ? w("", sourceGenParams) + "start"
                 + a(" ", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "->"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("argumentExpressions"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("expression") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("argumentExpressions") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "." + a("", sourceGenParams.isShouldIndent())
                 + (node.has("async") && node.get("async").getAsBoolean() ? w("", sourceGenParams) + "start"
                 + a(" ", sourceGenParams.isShouldIndent()) : "") + w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("argumentExpressions"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent());
            } else if (node.getAsJsonObject("packageAlias").get("valueWithBar") != null
                         && !node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("argumentExpressions") != null) {
                return w("", sourceGenParams)
                 + node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ":"
                 + a("", sourceGenParams.isShouldIndent())
                 + (node.has("async") && node.get("async").getAsBoolean() ? w("", sourceGenParams) + "start"
                 + a(" ", sourceGenParams.isShouldIndent()) : "") + w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("argumentExpressions"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return (node.has("async") && node.get("async").getAsBoolean() ? w("", sourceGenParams) + "start"
                 + a(" ", sourceGenParams.isShouldIndent()) : "") + w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("argumentExpressions"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForJoinStreamingInput(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("unidirectionalBeforeJoin") != null
                         && node.get("unidirectionalBeforeJoin") .getAsBoolean()
                         && node.get("streamingInput") != null && node.get("onExpression") != null) {
                return w("", sourceGenParams) + "unidirectional"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "join"
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda) + w("", sourceGenParams) + "on"
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onExpression"), pretty, replaceLambda);
            } else if (node.get("unidirectionalAfterJoin") != null
                         && node.get("unidirectionalAfterJoin") .getAsBoolean()
                         && node.get("streamingInput") != null && node.get("onExpression") != null) {
                return w("", sourceGenParams) + "join"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "unidirectional"
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda) + w("", sourceGenParams) + "on"
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onExpression"), pretty, replaceLambda);
            } else {
                return w("", sourceGenParams) + "join"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "on"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onExpression"), pretty, replaceLambda);
            }
        }
        public String getSourceForLambda(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("functionNode"), pretty, replaceLambda);
        }
        public String getSourceForLimit(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w(" ", sourceGenParams) + "limit"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.get("limitValue").getAsString() + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForLiteral(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("startTemplateLiteral") != null
                         && node.get("startTemplateLiteral") .getAsBoolean()
                         && node.get("endTemplateLiteral") != null && node.get("endTemplateLiteral") .getAsBoolean()
                         && node.get("value") != null) {
                return w("", sourceGenParams) + "}}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + node.get("value").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("lastNodeValue") != null
                         && node.get("lastNodeValue") .getAsBoolean() && node.get("endTemplateLiteral") != null
                         && node.get("endTemplateLiteral") .getAsBoolean()
                         && node.get("value") != null) {
                return w("", sourceGenParams) + "}}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + node.get("value").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("endTemplateLiteral") != null
                         && node.get("endTemplateLiteral") .getAsBoolean()) {
                return w("", sourceGenParams) + "}}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("startTemplateLiteral") != null
                         && node.get("startTemplateLiteral") .getAsBoolean() && node.get("value") != null) {
                return w("", sourceGenParams) + node.get("value").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("inTemplateLiteral") != null
                         && node.get("inTemplateLiteral") .getAsBoolean() && node.get("unescapedValue") != null) {
                return w("", sourceGenParams) + node.get("unescapedValue").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("inTemplateLiteral") != null
                         && node.get("inTemplateLiteral") .getAsBoolean()) {
                return "";
            } else if (node.get("emptyParantheses") != null
                         && node.get("emptyParantheses") .getAsBoolean()) {
                return w(" ", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return w("", sourceGenParams) + node.get("value").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForLock(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "lock" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForMatch(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "match" + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda) + w(" ", sourceGenParams)
                 + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("patternClauses"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForMatchPatternClause(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("withCurlies") != null
                         && node.get("withCurlies") .getAsBoolean() && node.get("variableNode") != null
                         && node.get("statement") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("variableNode"), pretty, replaceLambda) + w(" ", sourceGenParams) + "=>"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("statement"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("variableNode"), pretty, replaceLambda)
                 + w(" ", sourceGenParams) + "=>" + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("statement"), pretty, replaceLambda);
            }
        }
        public String getSourceForMatchExpression(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda) + w("", sourceGenParams) + "but"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("patternClauses"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForMatchExpressionPatternClause(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("withCurlies") != null
                         && node.get("withCurlies") .getAsBoolean() && node.get("variableNode") != null
                         && node.get("statement") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("variableNode"), pretty, replaceLambda) + w("", sourceGenParams) + "=>"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("statement"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("variableNode"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "=>" + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("statement"), pretty, replaceLambda);
            }
        }
        public String getSourceForNamedArgsExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "="
                 + a(" ", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
        }
        public String getSourceForNext(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "continue" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForOrderBy(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "order"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "by"
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("variables"), pretty, replaceLambda, "", ",", false, sourceGenParams);
        }
        public String getSourceForOrderByVariable(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("variableReference"), pretty, replaceLambda)
                 + w(" ", sourceGenParams) + node.get("orderByType").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForPostIncrement(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("variable"), pretty, replaceLambda) + w("", sourceGenParams)
                 + node.get("operator").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForRecordLiteralExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("keyValuePairs") != null) {
                return w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("keyValuePairs"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else {
                return w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForRecordLiteralKeyValue(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("key"), pretty, replaceLambda) + w("", sourceGenParams)
                 + ":" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("value"), pretty, replaceLambda);
        }
        public String getSourceForResource(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("parameters"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("workers"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForRestArgsExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "..."
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
        }
        public String getSourceForRetry(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "retry" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForReturn(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("noExpressionAvailable") != null
                         && node.get("noExpressionAvailable") .getAsBoolean()) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "return" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "return" + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda) + w("", sourceGenParams)
                 + ";" + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForSelectClause(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("selectAll") != null
                         && node.get("selectAll") .getAsBoolean() && node.get("groupBy") != null
                         && node.get("having") != null) {
                return w("", sourceGenParams) + "select"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "*"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("groupBy"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("having"), pretty, replaceLambda);
            } else if (node.get("selectAll") != null
                         && node.get("selectAll") .getAsBoolean() && node.get("groupBy") != null) {
                return w("", sourceGenParams) + "select"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "*"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("groupBy"), pretty, replaceLambda);
            } else if (node.get("selectAll") != null
                         && node.get("selectAll") .getAsBoolean() && node.get("having") != null) {
                return w("", sourceGenParams) + "select"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "*"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("having"), pretty, replaceLambda);
            } else if (node.get("selectAll") != null
                         && node.get("selectAll") .getAsBoolean()) {
                return w("", sourceGenParams) + "select"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "*"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("selectExpressions") != null
                         && node.get("groupBy") != null && node.get("having") != null) {
                return w("", sourceGenParams) + "select"
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("selectExpressions"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("groupBy"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("having"), pretty, replaceLambda);
            } else if (node.get("selectExpressions") != null
                         && node.get("groupBy") != null) {
                return w("", sourceGenParams) + "select"
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("selectExpressions"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("groupBy"), pretty, replaceLambda);
            } else if (node.get("selectExpressions") != null
                         && node.get("having") != null) {
                return w("", sourceGenParams) + "select"
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("selectExpressions"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("having"), pretty, replaceLambda);
            } else {
                return w("", sourceGenParams) + "select"
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("selectExpressions"), pretty, replaceLambda, "", ",", false, sourceGenParams);
            }
        }
        public String getSourceForSelectExpression(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("identifierAvailable") != null
                         && node.get("identifierAvailable") .getAsBoolean() && node.get("expression") != null
                         && node.get("identifier") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda)
                 + w(" ", sourceGenParams) + "as" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + node.get("identifier").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
            }
        }
        public String getSourceForService(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("isServiceTypeUnavailable") != null
                         && node.get("isServiceTypeUnavailable") .getAsBoolean()
                         && node.get("bindNotAvailable") != null && node.get("bindNotAvailable") .getAsBoolean()
                         && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("endpointNodes") != null
                         && node.get("variables") != null && node.get("resources") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "service" + a(" ", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("variables"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("resources"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("isServiceTypeUnavailable") != null
                         && node.get("isServiceTypeUnavailable") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("anonymousEndpointBind") != null
                         && node.get("boundEndpoints") != null && node.get("endpointNodes") != null
                         && node.get("variables") != null && node.get("resources") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "service" + a(" ", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "bind"
                 + a(" ", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("anonymousEndpointBind"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("boundEndpoints"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w(" ", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("variables"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("resources"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("isServiceTypeUnavailable") != null
                         && node.get("isServiceTypeUnavailable") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("boundEndpoints") != null && node.get("endpointNodes") != null
                         && node.get("variables") != null && node.get("resources") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "service" + a(" ", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "bind"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("boundEndpoints"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w(" ", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("variables"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("resources"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("bindNotAvailable") != null
                         && node.get("bindNotAvailable") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.get("serviceTypeStruct") != null && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("endpointNodes") != null
                         && node.get("variables") != null && node.get("resources") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "service" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("serviceTypeStruct"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ">"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("variables"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("resources"), pretty, replaceLambda, "", null, false, sourceGenParams) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("serviceTypeStruct") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("anonymousEndpointBind") != null
                         && node.get("boundEndpoints") != null && node.get("endpointNodes") != null
                         && node.get("variables") != null && node.get("resources") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "service" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("serviceTypeStruct"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ">"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "bind"
                 + a(" ", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("anonymousEndpointBind"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("boundEndpoints"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w(" ", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("variables"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("resources"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "service" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("serviceTypeStruct"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ">"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "bind"
                 + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("boundEndpoints"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + join(node.getAsJsonArray("endpointNodes"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("variables"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("resources"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForSimpleVariableRef(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("inTemplateLiteral") != null
                         && node.get("inTemplateLiteral") .getAsBoolean()
                         && node.getAsJsonObject("packageAlias").get("valueWithBar") != null
                         && !node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("variableName").get("valueWithBar") != null
                         && !node.getAsJsonObject("variableName").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams) + "{{"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ":"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("variableName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("inTemplateLiteral") != null
                         && node.get("inTemplateLiteral") .getAsBoolean()
                         && node.getAsJsonObject("variableName").get("valueWithBar") != null
                         && !node.getAsJsonObject("variableName").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams) + "{{"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("variableName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.getAsJsonObject("packageAlias").get("valueWithBar") != null
                         && !node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("variableName").get("valueWithBar") != null
                         && !node.getAsJsonObject("variableName").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams)
                 + node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ":"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("variableName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return w("", sourceGenParams)
                 + node.getAsJsonObject("variableName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForStreamAction(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("invokableBody"), pretty, replaceLambda);
        }
        public String getSourceForStreamingInput(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("windowTraversedAfterWhere") != null
                         && node.get("windowTraversedAfterWhere") .getAsBoolean()
                         && node.get("streamReference") != null && node.get("beforeStreamingCondition") != null
                         && node.get("windowClause") != null
                         && node.get("afterStreamingCondition") != null && node.get("aliasAvailable") != null
                         && node.get("aliasAvailable") .getAsBoolean() && node.get("alias") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("beforeStreamingCondition"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("windowClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("afterStreamingCondition"), pretty, replaceLambda) + w("", sourceGenParams)
                 + "as" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + node.get("alias").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("windowTraversedAfterWhere") != null
                         && node.get("windowTraversedAfterWhere") .getAsBoolean()
                         && node.get("streamReference") != null && node.get("beforeStreamingCondition") != null
                         && node.get("windowClause") != null
                         && node.get("afterStreamingCondition") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("beforeStreamingCondition"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("windowClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("afterStreamingCondition"), pretty, replaceLambda);
            } else if (node.get("windowTraversedAfterWhere") != null
                         && node.get("windowTraversedAfterWhere") .getAsBoolean()
                         && node.get("streamReference") != null && node.get("windowClause") != null
                         && node.get("afterStreamingCondition") != null
                         && node.get("aliasAvailable") != null && node.get("aliasAvailable") .getAsBoolean()
                         && node.get("alias") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("windowClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("afterStreamingCondition"), pretty, replaceLambda) + w("", sourceGenParams)
                 + "as" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + node.get("alias").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("windowTraversedAfterWhere") != null
                         && node.get("windowTraversedAfterWhere") .getAsBoolean()
                         && node.get("streamReference") != null && node.get("windowClause") != null
                         && node.get("afterStreamingCondition") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("windowClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("afterStreamingCondition"), pretty, replaceLambda);
            } else if (node.get("windowTraversedAfterWhere") != null
                         && node.get("windowTraversedAfterWhere") .getAsBoolean()
                         && node.get("streamReference") != null && node.get("windowClause") != null
                         && node.get("afterStreamingCondition") != null
                         && node.get("aliasAvailable") != null && node.get("aliasAvailable") .getAsBoolean()
                         && node.get("alias") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("windowClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("afterStreamingCondition"), pretty, replaceLambda) + w("", sourceGenParams)
                 + "as" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + node.get("alias").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("windowTraversedAfterWhere") != null
                         && node.get("windowTraversedAfterWhere") .getAsBoolean()
                         && node.get("streamReference") != null && node.get("windowClause") != null
                         && node.get("afterStreamingCondition") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("windowClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("afterStreamingCondition"), pretty, replaceLambda);
            } else if (node.get("windowTraversedAfterWhere") != null
                         && node.get("windowTraversedAfterWhere") .getAsBoolean()
                         && node.get("streamReference") != null && node.get("beforeStreamingCondition") != null
                         && node.get("windowClause") != null
                         && node.get("aliasAvailable") != null && node.get("aliasAvailable") .getAsBoolean()
                         && node.get("alias") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("beforeStreamingCondition"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("windowClause"), pretty, replaceLambda) + w("", sourceGenParams)
                 + "as" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + node.get("alias").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("windowTraversedAfterWhere") != null
                         && node.get("windowTraversedAfterWhere") .getAsBoolean()
                         && node.get("streamReference") != null && node.get("beforeStreamingCondition") != null
                         && node.get("windowClause") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("beforeStreamingCondition"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("windowClause"), pretty, replaceLambda);
            } else if (node.get("streamReference") != null
                         && node.get("beforeStreamingCondition") != null && node.get("afterStreamingCondition") != null
                         && node.get("aliasAvailable") != null
                         && node.get("aliasAvailable") .getAsBoolean() && node.get("alias") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("beforeStreamingCondition"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("afterStreamingCondition"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "as" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + node.get("alias").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("streamReference") != null
                         && node.get("beforeStreamingCondition") != null
                         && node.get("afterStreamingCondition") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("beforeStreamingCondition"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("afterStreamingCondition"), pretty, replaceLambda);
            } else if (node.get("streamReference") != null
                         && node.get("beforeStreamingCondition") != null && node.get("aliasAvailable") != null
                         && node.get("aliasAvailable") .getAsBoolean()
                         && node.get("alias") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("beforeStreamingCondition"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "as" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + node.get("alias").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("streamReference") != null
                         && node.get("beforeStreamingCondition") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("beforeStreamingCondition"), pretty, replaceLambda);
            } else if (node.get("streamReference") != null
                         && node.get("afterStreamingCondition") != null && node.get("aliasAvailable") != null
                         && node.get("aliasAvailable") .getAsBoolean()
                         && node.get("alias") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("afterStreamingCondition"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "as" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + node.get("alias").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("streamReference") != null
                         && node.get("afterStreamingCondition") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("afterStreamingCondition"), pretty, replaceLambda);
            } else if (node.get("streamReference") != null
                         && node.get("aliasAvailable") != null && node.get("aliasAvailable") .getAsBoolean()
                         && node.get("alias") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "as" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + node.get("alias").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamReference"), pretty, replaceLambda);
            }
        }
        public String getSourceForStreamingQuery(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("streamingInput") != null
                         && node.get("joinStreamingInput") != null && node.get("selectClause") != null
                         && node.get("orderbyClause") != null && node.get("streamingAction") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinStreamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("orderbyClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingAction"), pretty, replaceLambda);
            } else if (node.get("streamingInput") != null
                         && node.get("selectClause") != null && node.get("orderbyClause") != null
                         && node.get("streamingAction") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("orderbyClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingAction"), pretty, replaceLambda);
            } else if (node.get("streamingInput") != null
                         && node.get("joinStreamingInput") != null && node.get("selectClause") != null
                         && node.get("streamingAction") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinStreamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingAction"), pretty, replaceLambda);
            } else if (node.get("streamingInput") != null
                         && node.get("selectClause") != null && node.get("streamingAction") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingAction"), pretty, replaceLambda);
            } else if (node.get("streamingInput") != null
                         && node.get("joinStreamingInput") != null && node.get("selectClause") != null
                         && node.get("orderbyClause") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinStreamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("orderbyClause"), pretty, replaceLambda);
            } else if (node.get("streamingInput") != null
                         && node.get("selectClause") != null && node.get("orderbyClause") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClause"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("orderbyClause"), pretty, replaceLambda);
            } else if (node.get("streamingInput") != null
                         && node.get("joinStreamingInput") != null && node.get("selectClause") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinStreamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClause"), pretty, replaceLambda);
            } else {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClause"), pretty, replaceLambda);
            }
        }
        public String getSourceForStringTemplateLiteral(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + node.get("startTemplate").getAsString()
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("expressions"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + "`"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForTable(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "table"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("configurationExpression"), pretty, replaceLambda);
        }
        public String getSourceForTableQueryExpression(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("tableQuery"), pretty, replaceLambda);
        }
        public String getSourceForTableQuery(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("streamingInput") != null
                         && node.get("joinStreamingInput") != null && node.get("selectClauseNode") != null
                         && node.get("orderByNode") != null && node.get("limitClause") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinStreamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClauseNode"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("orderByNode"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("limitClause"), pretty, replaceLambda);
            } else if (node.get("streamingInput") != null
                         && node.get("selectClauseNode") != null && node.get("orderByNode") != null
                         && node.get("limitClause") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClauseNode"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("orderByNode"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("limitClause"), pretty, replaceLambda);
            } else if (node.get("streamingInput") != null
                         && node.get("selectClauseNode") != null && node.get("limitClause") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClauseNode"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("limitClause"), pretty, replaceLambda);
            } else if (node.get("streamingInput") != null
                         && node.get("joinStreamingInput") != null && node.get("selectClauseNode") != null
                         && node.get("limitClause") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinStreamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClauseNode"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("limitClause"), pretty, replaceLambda);
            } else if (node.get("streamingInput") != null
                         && node.get("joinStreamingInput") != null && node.get("selectClauseNode") != null
                         && node.get("orderByNode") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("joinStreamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClauseNode"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("orderByNode"), pretty, replaceLambda);
            } else if (node.get("streamingInput") != null
                         && node.get("selectClauseNode") != null && node.get("orderByNode") != null) {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClauseNode"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("orderByNode"), pretty, replaceLambda);
            } else {
                return w("", sourceGenParams) + "from"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("streamingInput"), pretty, replaceLambda)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("selectClauseNode"), pretty, replaceLambda);
            }
        }
        public String getSourceForTernaryExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("condition"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "?" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("thenExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ":" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("elseExpression"), pretty, replaceLambda);
        }
        public String getSourceForThrow(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "throw" + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expressions"), pretty, replaceLambda) + w("", sourceGenParams)
                 + ";" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForTransaction(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("retryCount") != null
                         && node.get("onCommitFunction") != null && node.get("onAbortFunction") != null
                         && node.get("transactionBody") != null && node.get("onRetryBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "transaction"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "with" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "retries" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("retryCount"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "," + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "oncommit"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onCommitFunction"), pretty, replaceLambda) + w("", sourceGenParams) + ","
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "onabort"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onAbortFunction"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "onretry" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onRetryBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("retryCount") != null
                         && node.get("onCommitFunction") != null && node.get("transactionBody") != null
                         && node.get("onRetryBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "transaction"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "with" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "retries" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("retryCount"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "," + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "oncommit"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onCommitFunction"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "onretry"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onRetryBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("onCommitFunction") != null
                         && node.get("onAbortFunction") != null && node.get("transactionBody") != null
                         && node.get("onRetryBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "transaction"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "with" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "oncommit" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onCommitFunction"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ","
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "onabort"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onAbortFunction"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "onretry"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onRetryBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("retryCount") != null
                         && node.get("onAbortFunction") != null && node.get("transactionBody") != null
                         && node.get("onRetryBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "transaction"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "with" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "retries" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("retryCount"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "," + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "onabort"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onAbortFunction"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "onretry"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onRetryBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("retryCount") != null
                         && node.get("transactionBody") != null && node.get("onRetryBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "transaction"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "with" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "retries" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("retryCount"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "onretry"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onRetryBody"), pretty, replaceLambda) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("onCommitFunction") != null
                         && node.get("transactionBody") != null && node.get("onRetryBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "transaction"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "with" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "oncommit" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onCommitFunction"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "onretry"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onRetryBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("onAbortFunction") != null
                         && node.get("transactionBody") != null && node.get("onRetryBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "transaction"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "with" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "onabort" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onAbortFunction"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "onretry"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onRetryBody"), pretty, replaceLambda) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("transactionBody") != null
                         && node.get("onRetryBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "transaction"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "onretry" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onRetryBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("retryCount") != null
                         && node.get("onCommitFunction") != null && node.get("onAbortFunction") != null
                         && node.get("transactionBody") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "transaction" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "with"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "retries"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("retryCount"), pretty, replaceLambda) + w("", sourceGenParams) + ","
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "oncommit"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "=" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onCommitFunction"), pretty, replaceLambda) + w("", sourceGenParams) + ","
                 + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "onabort" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "=" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onAbortFunction"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("retryCount") != null
                         && node.get("onCommitFunction") != null && node.get("transactionBody") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "transaction" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "with"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "retries"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("retryCount"), pretty, replaceLambda) + w("", sourceGenParams) + ","
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "oncommit"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "=" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onCommitFunction"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("retryCount") != null
                         && node.get("onAbortFunction") != null && node.get("transactionBody") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "transaction" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "with"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "retries"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("retryCount"), pretty, replaceLambda) + w("", sourceGenParams) + ","
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "onabort"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onAbortFunction"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("onCommitFunction") != null
                         && node.get("onAbortFunction") != null && node.get("transactionBody") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "transaction" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "with"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "oncommit"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onCommitFunction"), pretty, replaceLambda) + w("", sourceGenParams) + ","
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "onabort"
                 + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "=" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onAbortFunction"), pretty, replaceLambda) + w("", sourceGenParams)
                 + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("retryCount") != null
                         && node.get("transactionBody") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "transaction" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "with"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "retries"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("retryCount"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("onCommitFunction") != null
                         && node.get("transactionBody") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "transaction" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "with"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "oncommit"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onCommitFunction"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("onAbortFunction") != null
                         && node.get("transactionBody") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "transaction" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "with"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "onabort"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("onAbortFunction"), pretty, replaceLambda) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "transaction" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transactionBody"), pretty, replaceLambda) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForTransform(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "transform" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForTransformer(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("source") != null
                         && node.get("returnParameters") != null && node.getAsJsonArray("returnParameters").size() > 0
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("parameters") != null && node.get("body") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "transformer"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("source"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnParameters"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ">" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("parameters"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("source") != null
                         && node.get("returnParameters") != null && node.getAsJsonArray("returnParameters").size() > 0
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("body") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "transformer"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("source"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnParameters"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ">" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent())
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "transformer"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("source"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("returnParameters"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ">" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForTry(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("body") != null && node.get("catchBlocks") != null
                         && node.get("finallyBody") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "try"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("catchBlocks"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "finally" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("finallyBody"), pretty, replaceLambda) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "try" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("catchBlocks"), pretty, replaceLambda, "", null, false, sourceGenParams);
            }
        }
        public String getSourceForTupleDestructure(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("declaredWithVar") != null
                         && node.get("declaredWithVar") .getAsBoolean() && node.get("variableRefs") != null
                         && node.getAsJsonArray("variableRefs").size() > 0
                         && node.get("expression") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "var" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("variableRefs"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("variableRefs"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "="
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForTupleTypeNode(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("memberTypeNodes"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForTypeCastExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
        }
        public String getSourceForTypeConversionExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("typeNode") != null
                         && node.get("transformerInvocation") != null && node.get("expression") != null) {
                return w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "," + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("transformerInvocation"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ">"
                 + a(" ", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
            } else {
                return w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ">" + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
            }
        }
        public String getSourceForTypeDefinition(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("notVisible") != null
                         && node.get("notVisible") .getAsBoolean()) {
                return "";
            } else if (node.get("isObjectType") != null
                         && node.get("isObjectType") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("typeNode") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams) + "public"
                 + a("", sourceGenParams.isShouldIndent()) : "") + w("", sourceGenParams)
                 + "type" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "object" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("isRecordType") != null
                         && node.get("isRecordType") .getAsBoolean() && node.get("isRecordKeywordAvailable") != null
                         && node.get("isRecordKeywordAvailable") .getAsBoolean()
                         && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("typeNode") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams) + "public"
                 + a("", sourceGenParams.isShouldIndent()) : "") + w("", sourceGenParams)
                 + "type" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "record" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("isRecordType") != null
                         && node.get("isRecordType") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("typeNode") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams) + "public"
                 + a("", sourceGenParams.isShouldIndent()) : "") + w("", sourceGenParams)
                 + "type" + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("typeNode") != null
                         && node.get("valueSet") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a("", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "type"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "|" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("valueSet"), pretty, replaceLambda, "", "|", false, sourceGenParams) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a("", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "type"
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("valueSet"), pretty, replaceLambda, "", "|", false, sourceGenParams) + w("", sourceGenParams)
                 + ";" + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForObjectType(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return join(node.getAsJsonArray("fields"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initFunction"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("functions"), pretty, replaceLambda, "", null, false, sourceGenParams);
        }
        public String getSourceForRecordType(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("isRestFieldAvailable") != null
                         && node.get("isRestFieldAvailable") .getAsBoolean() && node.get("fields") != null
                         && node.get("restFieldType") != null) {
                return join(node.getAsJsonArray("fields"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("restFieldType"), pretty, replaceLambda) + w("", sourceGenParams) + "..."
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("sealed") != null && node.get("sealed") .getAsBoolean()
                         && node.get("fields") != null) {
                return join(node.getAsJsonArray("fields"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + "!"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "..." + a("", sourceGenParams.isShouldIndent());
            } else {
                return join(node.getAsJsonArray("fields"), pretty, replaceLambda, "", null, false, sourceGenParams);
            }
        }
        public String getSourceForTypedescExpression(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda);
        }
        public String getSourceForTypeofExpression(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "typeof"
                 + a("", sourceGenParams.isShouldIndent()) + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda);
        }
        public String getSourceForTypeInitExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("noExpressionAvailable") != null
                         && node.get("noExpressionAvailable") .getAsBoolean()
                         && node.get("noTypeAttached") != null && node.get("noTypeAttached") .getAsBoolean()
                         && node.get("hasParantheses") != null
                         && node.get("hasParantheses") .getAsBoolean()) {
                return w("", sourceGenParams) + "new"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("noExpressionAvailable") != null
                         && node.get("noExpressionAvailable") .getAsBoolean()
                         && node.get("noTypeAttached") != null && node.get("noTypeAttached") .getAsBoolean()) {
                return w("", sourceGenParams) + "new"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("noExpressionAvailable") != null
                         && node.get("noExpressionAvailable") .getAsBoolean() && node.get("type") != null) {
                return w("", sourceGenParams) + "new"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("type"), pretty, replaceLambda)
                 + w(" ", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("noTypeAttached") != null
                         && node.get("noTypeAttached") .getAsBoolean() && node.get("expressions") != null) {
                return w("", sourceGenParams) + "new"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("expressions"), pretty, replaceLambda, "", ",", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent());
            } else {
                return w("", sourceGenParams) + "new"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("type"), pretty, replaceLambda)
                 + w(" ", sourceGenParams) + "(" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("expressions"), pretty, replaceLambda, "", ",", false, sourceGenParams) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForUnaryExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("inTemplateLiteral") != null
                         && node.get("inTemplateLiteral") .getAsBoolean() && node.get("operatorKind") != null
                         && node.get("expression") != null) {
                return w("", sourceGenParams) + "{{"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.get("operatorKind").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda) + w("", sourceGenParams)
                 + "}}" + a("", sourceGenParams.isShouldIndent());
            } else {
                return w("", sourceGenParams) + node.get("operatorKind").getAsString()
                 + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
            }
        }
        public String getSourceForUnionTypeNode(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("emptyParantheses") != null
                         && node.get("emptyParantheses") .getAsBoolean()) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("withParantheses") != null
                         && node.get("withParantheses") .getAsBoolean() && node.get("memberTypeNodes") != null) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("memberTypeNodes"), pretty, replaceLambda, "", "|", false, sourceGenParams)
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent());
            } else {
                return join(node.getAsJsonArray("memberTypeNodes"), pretty, replaceLambda, "", "|", false, sourceGenParams);
            }
        }
        public String getSourceForUserDefinedType(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("isAnonType") != null
                         && node.get("isAnonType") .getAsBoolean() && node.get("anonType") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("anonType"), pretty, replaceLambda);
            } else if (node.get("nullableOperatorAvailable") != null
                         && node.get("nullableOperatorAvailable") .getAsBoolean()
                         && node.get("grouped") != null && node.get("grouped") .getAsBoolean()
                         && node.getAsJsonObject("packageAlias").get("valueWithBar") != null
                         && !node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("typeName").get("valueWithBar") != null
                         && !node.getAsJsonObject("typeName").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ":"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("typeName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "?"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + ")" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("nullableOperatorAvailable") != null
                         && node.get("nullableOperatorAvailable") .getAsBoolean()
                         && node.getAsJsonObject("packageAlias").get("valueWithBar") != null
                         && !node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("typeName").get("valueWithBar") != null
                         && !node.getAsJsonObject("typeName").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams)
                 + node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ":"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("typeName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "?"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("nullableOperatorAvailable") != null
                         && node.get("nullableOperatorAvailable") .getAsBoolean()
                         && node.get("grouped") != null && node.get("grouped") .getAsBoolean()
                         && node.getAsJsonObject("typeName").get("valueWithBar") != null
                         && !node.getAsJsonObject("typeName").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("typeName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "?"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("nullableOperatorAvailable") != null
                         && node.get("nullableOperatorAvailable") .getAsBoolean()
                         && node.getAsJsonObject("typeName").get("valueWithBar") != null
                         && !node.getAsJsonObject("typeName").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams)
                 + node.getAsJsonObject("typeName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "?"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("grouped") != null
                         && node.get("grouped") .getAsBoolean()
                         && node.getAsJsonObject("packageAlias").get("valueWithBar") != null
                         && !node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("typeName").get("valueWithBar") != null
                         && !node.getAsJsonObject("typeName").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ":"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("typeName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.getAsJsonObject("packageAlias").get("valueWithBar") != null
                         && !node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("typeName").get("valueWithBar") != null
                         && !node.getAsJsonObject("typeName").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams)
                 + node.getAsJsonObject("packageAlias").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ":"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("typeName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("grouped") != null
                         && node.get("grouped") .getAsBoolean() && node.getAsJsonObject("typeName").get("valueWithBar") != null
                         && !node.getAsJsonObject("typeName").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("typeName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return w("", sourceGenParams)
                 + node.getAsJsonObject("typeName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForValueType(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("emptyParantheses") != null
                         && node.get("emptyParantheses") .getAsBoolean()) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ")"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("withParantheses") != null
                         && node.get("withParantheses") .getAsBoolean() && node.get("typeKind") != null
                         && node.get("nullableOperatorAvailable") != null
                         && node.get("nullableOperatorAvailable") .getAsBoolean()) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.get("typeKind").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "?" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("withParantheses") != null
                         && node.get("withParantheses") .getAsBoolean() && node.get("typeKind") != null) {
                return w("", sourceGenParams) + "("
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.get("typeKind").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ")" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("typeKind") != null
                         && node.get("nullableOperatorAvailable") != null
                         && node.get("nullableOperatorAvailable") .getAsBoolean()) {
                return w("", sourceGenParams) + node.get("typeKind").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "?" + a("", sourceGenParams.isShouldIndent());
            } else {
                return w("", sourceGenParams) + node.get("typeKind").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForVariable(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("isAnonType") != null
                         && node.get("isAnonType") .getAsBoolean() && node.get("endWithSemicolon") != null
                         && node.get("endWithSemicolon") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.get("typeNode") != null && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("const") && node.get("const").getAsBoolean() ? w("", sourceGenParams) + "const"
                 + a(" ", sourceGenParams.isShouldIndent()) : "") + w("", sourceGenParams)
                 + "record" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("isAnonType") != null
                         && node.get("isAnonType") .getAsBoolean() && node.get("endWithComma") != null
                         && node.get("endWithComma") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("const") && node.get("const").getAsBoolean() ? w("", sourceGenParams)
                 + "const" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "record"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("isAnonType") != null
                         && node.get("isAnonType") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("const") && node.get("const").getAsBoolean() ? w("", sourceGenParams)
                 + "const" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + w("", sourceGenParams) + "record"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda) + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent());
            } else if (node.get("noVisibleName") != null
                         && node.get("noVisibleName") .getAsBoolean() && node.get("typeNode") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda);
            } else if (node.get("endpoint") != null
                         && node.get("endpoint") .getAsBoolean() && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("initialExpression") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "endpoint"
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "{"
                 + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent())
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("endpoint") != null
                         && node.get("endpoint") .getAsBoolean() && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "endpoint" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + outdent(node, sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "}" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("serviceEndpoint") != null
                         && node.get("serviceEndpoint") .getAsBoolean()
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams) + "endpoint"
                 + a("", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent());
            } else if (node.get("defaultable") != null
                         && node.get("defaultable") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("initialExpression") != null) {
                return join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "="
                 + a(" ", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda);
            } else if (node.get("defaultable") != null
                         && node.get("defaultable") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("initialExpression") != null) {
                return join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "="
                 + a(" ", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda);
            } else if (node.get("global") != null && node.get("global") .getAsBoolean()
                         && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("safeAssignment") != null
                         && node.get("safeAssignment") .getAsBoolean() && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("initialExpression") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + (node.has("const") && node.get("const").getAsBoolean() ? w("", sourceGenParams) + "const"
                 + a(" ", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "=?"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("global") != null && node.get("global") .getAsBoolean()
                         && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("initialExpression") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + (node.has("const") && node.get("const").getAsBoolean() ? w("", sourceGenParams) + "const"
                 + a(" ", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "="
                 + a(" ", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("global") != null && node.get("global") .getAsBoolean()
                         && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + (node.has("const") && node.get("const").getAsBoolean() ? w("", sourceGenParams) + "const"
                 + a(" ", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("global") != null && node.get("global") .getAsBoolean()
                         && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("endWithSemicolon") != null
                         && node.get("endWithSemicolon") .getAsBoolean() && node.get("safeAssignment") != null
                         && node.get("safeAssignment") .getAsBoolean()
                         && node.get("typeNode") != null && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("initialExpression") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "=?"
                 + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("endWithComma") != null
                         && node.get("endWithComma") .getAsBoolean() && node.get("safeAssignment") != null
                         && node.get("safeAssignment") .getAsBoolean() && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty() && node.get("initialExpression") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "=?" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "," + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("endWithSemicolon") != null
                         && node.get("endWithSemicolon") .getAsBoolean() && node.get("inObject") != null
                         && node.get("inObject") .getAsBoolean() && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("initialExpression") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + (node.has("private") && node.get("private").getAsBoolean() ? w("", sourceGenParams) + "private"
                 + a("", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "=" + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("endWithComma") != null
                         && node.get("endWithComma") .getAsBoolean() && node.get("inObject") != null
                         && node.get("inObject") .getAsBoolean() && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("initialExpression") != null) {
                return (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams) + "public"
                 + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + (node.has("private") && node.get("private").getAsBoolean() ? w("", sourceGenParams) + "private"
                 + a("", sourceGenParams.isShouldIndent()) : "")
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "="
                 + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda) + w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("endWithSemicolon") != null
                         && node.get("endWithSemicolon") .getAsBoolean() && node.get("inObject") != null
                         && node.get("inObject") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null
                         && node.get("typeNode") != null && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams)
                 + "public" + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + (node.has("private") && node.get("private").getAsBoolean() ? w("", sourceGenParams) + "private"
                 + a("", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + (node.has("rest") && node.get("rest").getAsBoolean() ? w("", sourceGenParams) + "..." + a("", sourceGenParams.isShouldIndent()) : "")
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("endWithComma") != null
                         && node.get("endWithComma") .getAsBoolean() && node.get("inObject") != null
                         && node.get("inObject") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()) {
                return join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams) + "public"
                 + a(" ", sourceGenParams.isShouldIndent()) : "")
                 + (node.has("private") && node.get("private").getAsBoolean() ? w("", sourceGenParams) + "private"
                 + a("", sourceGenParams.isShouldIndent()) : "")
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + (node.has("rest") && node.get("rest").getAsBoolean() ? w("", sourceGenParams) + "..."
                 + a("", sourceGenParams.isShouldIndent()) : "") + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("endWithSemicolon") != null
                         && node.get("endWithSemicolon") .getAsBoolean() && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("initialExpression") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda) + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w(" ", sourceGenParams) + "="
                 + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("endWithComma") != null
                         && node.get("endWithComma") .getAsBoolean() && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("initialExpression") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "=" + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "," + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("endWithSemicolon") != null
                         && node.get("endWithSemicolon") .getAsBoolean()
                         && node.get("documentationAttachments") != null && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()) {
                return dent(sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + (node.has("rest") && node.get("rest").getAsBoolean() ? w("", sourceGenParams) + "..."
                 + a("", sourceGenParams.isShouldIndent()) : "") + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + ";" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("endWithComma") != null
                         && node.get("endWithComma") .getAsBoolean() && node.get("documentationAttachments") != null
                         && node.get("annotationAttachments") != null
                         && node.get("deprecatedAttachments") != null && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()) {
                return join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + (node.has("rest") && node.get("rest").getAsBoolean() ? w("", sourceGenParams) + "..." + a("", sourceGenParams.isShouldIndent()) : "")
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ","
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("safeAssignment") != null
                         && node.get("safeAssignment") .getAsBoolean() && node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("initialExpression") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "=?" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda);
            } else if (node.get("typeNode") != null
                         && node.getAsJsonObject("name").get("valueWithBar") != null
                         && !node.getAsJsonObject("name").get("valueWithBar").getAsString().isEmpty()
                         && node.get("initialExpression") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a(" ", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "=" + a(" ", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("initialExpression"), pretty, replaceLambda);
            } else {
                return join(node.getAsJsonArray("documentationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("annotationAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + join(node.getAsJsonArray("deprecatedAttachments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + (node.has("public") && node.get("public").getAsBoolean() ? w("", sourceGenParams) + "public"
                 + a(" ", sourceGenParams.isShouldIndent()) : "") + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("typeNode"), pretty, replaceLambda)
                 + (node.has("rest") && node.get("rest").getAsBoolean() ? w("", sourceGenParams) + "..."
                 + a("", sourceGenParams.isShouldIndent()) : "") + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString()
                 + a(" ", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForVariableDef(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("endpoint") != null
                         && node.get("endpoint") .getAsBoolean() && node.get("variable") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("variable"), pretty, replaceLambda);
            } else if (node.get("defaultable") != null
                         && node.get("defaultable") .getAsBoolean() && node.get("variable") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("variable"), pretty, replaceLambda);
            } else {
                return dent(sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("variable"), pretty, replaceLambda) + w("", sourceGenParams) + ";"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForWhere(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "where"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda);
        }
        public String getSourceForWhile(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "while" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("condition"), pretty, replaceLambda) + w(" ", sourceGenParams)
                 + "{" + a("", sourceGenParams.isShouldIndent()) + indent()
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "}"
                 + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForWindowClause(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return w("", sourceGenParams) + "window"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("functionInvocation"), pretty, replaceLambda);
        }
        public String getSourceForWorker(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "worker" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("name").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams) + "{" + a("", sourceGenParams.isShouldIndent())
                 + indent() + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("body"), pretty, replaceLambda)
                 + outdent(node, sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "}" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForWorkerReceive(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return dent(sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda) + w("", sourceGenParams) + "<-"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("workerName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + ";" + a("", sourceGenParams.isShouldIndent());
        }
        public String getSourceForWorkerSend(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("forkJoinedSend") != null
                         && node.get("forkJoinedSend") .getAsBoolean() && node.get("expression") != null) {
                return dent(sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda) + w("", sourceGenParams) + "->"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "fork" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
            } else {
                return dent(sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda) + w("", sourceGenParams) + "->"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("workerName").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + ";" + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForXmlAttribute(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("name"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "=" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("value"), pretty, replaceLambda);
        }
        public String getSourceForXmlAttributeAccessExpr(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("expression") != null && node.get("index") != null) {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "@" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "[" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("index"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "]" + a("", sourceGenParams.isShouldIndent());
            } else {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("expression"), pretty, replaceLambda)
                 + w("", sourceGenParams) + "@" + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForXmlCommentLiteral(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("root") != null && node.get("root") .getAsBoolean()
                         && node.get("startLiteral") != null
                         && node.get("textFragments") != null) {
                return w("", sourceGenParams) + node.get("startLiteral").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "<!--" + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("textFragments"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + "-->"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "`"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return w("", sourceGenParams) + "<!--"
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("textFragments"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "-->" + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForXmlElementLiteral(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("root") != null && node.get("root") .getAsBoolean()
                         && node.get("startLiteral") != null
                         && node.get("startTagName") != null && node.get("attributes") != null
                         && node.get("content") != null && node.get("endTagName") != null) {
                return w("", sourceGenParams) + node.get("startLiteral").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "<" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("startTagName"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("attributes"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + ">"
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("content"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "</" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("endTagName"), pretty, replaceLambda) + w("", sourceGenParams)
                 + ">" + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + "`" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("root") != null && node.get("root") .getAsBoolean()
                         && node.get("startLiteral") != null
                         && node.get("startTagName") != null && node.get("attributes") != null) {
                return w("", sourceGenParams) + node.get("startLiteral").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "<" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("startTagName"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("attributes"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + "/>"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "`"
                 + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("startTagName") != null
                         && node.get("attributes") != null && node.get("content") != null
                         && node.get("endTagName") != null) {
                return w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("startTagName"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("attributes"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + ">"
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("content"), pretty, replaceLambda, "", null, false, sourceGenParams)
                 + w("", sourceGenParams) + "</"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("endTagName"), pretty, replaceLambda)
                 + w("", sourceGenParams) + ">"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return w("", sourceGenParams) + "<"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("startTagName"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("attributes"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + "/>"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForXmlPiLiteral(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("root") != null && node.get("root") .getAsBoolean()
                         && node.get("startLiteral") != null && node.get("target") != null
                         && node.get("dataTextFragments") != null) {
                return w("", sourceGenParams) + node.get("startLiteral").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "<?" + a("", sourceGenParams.isShouldIndent())
                 + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("target"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("dataTextFragments"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + "?>"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + "`"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return w("", sourceGenParams) + "<?"
                 + a("", sourceGenParams.isShouldIndent()) + a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("target"), pretty, replaceLambda)
                 + join(node.getAsJsonArray("dataTextFragments"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + "?>"
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForXmlQname(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.getAsJsonObject("prefix").get("valueWithBar") != null
                         && !node.getAsJsonObject("prefix").get("valueWithBar").getAsString().isEmpty()
                         && node.getAsJsonObject("localname").get("valueWithBar") != null
                         && !node.getAsJsonObject("localname").get("valueWithBar").getAsString().isEmpty()) {
                return w("", sourceGenParams)
                 + node.getAsJsonObject("prefix").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams) + ":"
                 + a("", sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + node.getAsJsonObject("localname").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return w("", sourceGenParams)
                 + node.getAsJsonObject("localname").get("valueWithBar").getAsString()
                 + a("", sourceGenParams.isShouldIndent());
            }
        }
        public String getSourceForXmlQuotedString(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            return join(node.getAsJsonArray("textFragments"), pretty, replaceLambda, "", null, false, sourceGenParams);
        }
        public String getSourceForXmlTextLiteral(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("root") != null && node.get("root") .getAsBoolean()
                         && node.get("startLiteral") != null
                         && node.get("textFragments") != null) {
                return w("", sourceGenParams) + node.get("startLiteral").getAsString()
                 + a("", sourceGenParams.isShouldIndent())
                 + join(node.getAsJsonArray("textFragments"), pretty, replaceLambda, "", null, false, sourceGenParams) + w("", sourceGenParams) + "`"
                 + a("", sourceGenParams.isShouldIndent());
            } else {
                return join(node.getAsJsonArray("textFragments"), pretty, replaceLambda, "", null, false, sourceGenParams);
            }
        }
        public String getSourceForXmlns(JsonObject node, boolean pretty, boolean replaceLambda, SourceGenParams sourceGenParams) {
            if (node.get("namespaceURI") != null
                         && node.getAsJsonObject("prefix").get("valueWithBar") != null
                         && !node.getAsJsonObject("prefix").get("valueWithBar").getAsString().isEmpty()) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "xmlns" + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("namespaceURI"), pretty, replaceLambda) + w(" ", sourceGenParams)
                 + "as" + a("", sourceGenParams.isShouldIndent())
                 + w(" ", sourceGenParams)
                 + node.getAsJsonObject("prefix").get("valueWithBar").getAsString() + a("", sourceGenParams.isShouldIndent())
                 + w("", sourceGenParams) + ";" + a("", sourceGenParams.isShouldIndent());
            } else if (node.get("namespaceURI") != null) {
                return dent(sourceGenParams.isShouldIndent()) + w("", sourceGenParams)
                 + "xmlns" + a("", sourceGenParams.isShouldIndent())
                 + a(" ", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("namespaceURI"), pretty, replaceLambda) + w("", sourceGenParams)
                 + ";" + a("", sourceGenParams.isShouldIndent());
            } else {
                return a("", sourceGenParams.isShouldIndent())
                 + getSourceOf(node.getAsJsonObject("namespaceDeclaration"), pretty, replaceLambda);
            }
        }

    // auto gen end


    @FindbugsSuppressWarnings
    public String getSourceOf(JsonObject node, boolean pretty, boolean replaceLambda) {
        if (node == null) {
            return "";
        }

        SourceGenParams sourceGenParams = new SourceGenParams();
        sourceGenParams.setI(0);

        JsonArray wsArray = node.getAsJsonArray("ws");
        JsonArray ws = new JsonArray();

        if (wsArray != null) {
            for (JsonElement wsObj : wsArray) {
                ws.add(wsObj.getAsJsonObject().get("ws"));
            }
        }

        sourceGenParams.setWs(ws);

        sourceGenParams.setShouldIndent(pretty || !(ws != null && ws.size() > 0));

        if (replaceLambda && node.get("kind").getAsString().equals("Lambda")) {
            return "$ function LAMBDA $";
        }

        switch (node.get("kind").getAsString()) {
            case "CompilationUnit":
                return join(node.getAsJsonArray("topLevelNodes"), pretty, replaceLambda,
                        "", null, false, sourceGenParams) +
                        w("", sourceGenParams);
            /* eslint-disable max-len */
            // auto gen start
            
        case "PackageDeclaration":
            return getSourceForPackageDeclaration(node, pretty, replaceLambda, sourceGenParams);
        case "Import":
            return getSourceForImport(node, pretty, replaceLambda, sourceGenParams);
        case "Identifier":
            return getSourceForIdentifier(node, pretty, replaceLambda, sourceGenParams);
        case "Abort":
            return getSourceForAbort(node, pretty, replaceLambda, sourceGenParams);
        case "Action":
            return getSourceForAction(node, pretty, replaceLambda, sourceGenParams);
        case "Annotation":
            return getSourceForAnnotation(node, pretty, replaceLambda, sourceGenParams);
        case "AnnotationAttachment":
            return getSourceForAnnotationAttachment(node, pretty, replaceLambda, sourceGenParams);
        case "ArrayLiteralExpr":
            return getSourceForArrayLiteralExpr(node, pretty, replaceLambda, sourceGenParams);
        case "ArrayType":
            return getSourceForArrayType(node, pretty, replaceLambda, sourceGenParams);
        case "Assignment":
            return getSourceForAssignment(node, pretty, replaceLambda, sourceGenParams);
        case "AwaitExpr":
            return getSourceForAwaitExpr(node, pretty, replaceLambda, sourceGenParams);
        case "BinaryExpr":
            return getSourceForBinaryExpr(node, pretty, replaceLambda, sourceGenParams);
        case "Bind":
            return getSourceForBind(node, pretty, replaceLambda, sourceGenParams);
        case "Block":
            return getSourceForBlock(node, pretty, replaceLambda, sourceGenParams);
        case "Break":
            return getSourceForBreak(node, pretty, replaceLambda, sourceGenParams);
        case "BracedTupleExpr":
            return getSourceForBracedTupleExpr(node, pretty, replaceLambda, sourceGenParams);
        case "BuiltInRefType":
            return getSourceForBuiltInRefType(node, pretty, replaceLambda, sourceGenParams);
        case "Catch":
            return getSourceForCatch(node, pretty, replaceLambda, sourceGenParams);
        case "CheckExpr":
            return getSourceForCheckExpr(node, pretty, replaceLambda, sourceGenParams);
        case "Comment":
            return getSourceForComment(node, pretty, replaceLambda, sourceGenParams);
        case "CompoundAssignment":
            return getSourceForCompoundAssignment(node, pretty, replaceLambda, sourceGenParams);
        case "Connector":
            return getSourceForConnector(node, pretty, replaceLambda, sourceGenParams);
        case "ConnectorInitExpr":
            return getSourceForConnectorInitExpr(node, pretty, replaceLambda, sourceGenParams);
        case "ConstrainedType":
            return getSourceForConstrainedType(node, pretty, replaceLambda, sourceGenParams);
        case "Documentation":
            return getSourceForDocumentation(node, pretty, replaceLambda, sourceGenParams);
        case "DocumentationAttribute":
            return getSourceForDocumentationAttribute(node, pretty, replaceLambda, sourceGenParams);
        case "Deprecated":
            return getSourceForDeprecated(node, pretty, replaceLambda, sourceGenParams);
        case "Done":
            return getSourceForDone(node, pretty, replaceLambda, sourceGenParams);
        case "ElvisExpr":
            return getSourceForElvisExpr(node, pretty, replaceLambda, sourceGenParams);
        case "Endpoint":
            return getSourceForEndpoint(node, pretty, replaceLambda, sourceGenParams);
        case "EndpointType":
            return getSourceForEndpointType(node, pretty, replaceLambda, sourceGenParams);
        case "ExpressionStatement":
            return getSourceForExpressionStatement(node, pretty, replaceLambda, sourceGenParams);
        case "FieldBasedAccessExpr":
            return getSourceForFieldBasedAccessExpr(node, pretty, replaceLambda, sourceGenParams);
        case "Foreach":
            return getSourceForForeach(node, pretty, replaceLambda, sourceGenParams);
        case "Forever":
            return getSourceForForever(node, pretty, replaceLambda, sourceGenParams);
        case "ForkJoin":
            return getSourceForForkJoin(node, pretty, replaceLambda, sourceGenParams);
        case "Function":
            return getSourceForFunction(node, pretty, replaceLambda, sourceGenParams);
        case "FunctionType":
            return getSourceForFunctionType(node, pretty, replaceLambda, sourceGenParams);
        case "GroupBy":
            return getSourceForGroupBy(node, pretty, replaceLambda, sourceGenParams);
        case "Having":
            return getSourceForHaving(node, pretty, replaceLambda, sourceGenParams);
        case "If":
            return getSourceForIf(node, pretty, replaceLambda, sourceGenParams);
        case "IndexBasedAccessExpr":
            return getSourceForIndexBasedAccessExpr(node, pretty, replaceLambda, sourceGenParams);
        case "IntRangeExpr":
            return getSourceForIntRangeExpr(node, pretty, replaceLambda, sourceGenParams);
        case "Invocation":
            return getSourceForInvocation(node, pretty, replaceLambda, sourceGenParams);
        case "JoinStreamingInput":
            return getSourceForJoinStreamingInput(node, pretty, replaceLambda, sourceGenParams);
        case "Lambda":
            return getSourceForLambda(node, pretty, replaceLambda, sourceGenParams);
        case "Limit":
            return getSourceForLimit(node, pretty, replaceLambda, sourceGenParams);
        case "Literal":
            return getSourceForLiteral(node, pretty, replaceLambda, sourceGenParams);
        case "Lock":
            return getSourceForLock(node, pretty, replaceLambda, sourceGenParams);
        case "Match":
            return getSourceForMatch(node, pretty, replaceLambda, sourceGenParams);
        case "MatchPatternClause":
            return getSourceForMatchPatternClause(node, pretty, replaceLambda, sourceGenParams);
        case "MatchExpression":
            return getSourceForMatchExpression(node, pretty, replaceLambda, sourceGenParams);
        case "MatchExpressionPatternClause":
            return getSourceForMatchExpressionPatternClause(node, pretty, replaceLambda, sourceGenParams);
        case "NamedArgsExpr":
            return getSourceForNamedArgsExpr(node, pretty, replaceLambda, sourceGenParams);
        case "Next":
            return getSourceForNext(node, pretty, replaceLambda, sourceGenParams);
        case "OrderBy":
            return getSourceForOrderBy(node, pretty, replaceLambda, sourceGenParams);
        case "OrderByVariable":
            return getSourceForOrderByVariable(node, pretty, replaceLambda, sourceGenParams);
        case "PostIncrement":
            return getSourceForPostIncrement(node, pretty, replaceLambda, sourceGenParams);
        case "RecordLiteralExpr":
            return getSourceForRecordLiteralExpr(node, pretty, replaceLambda, sourceGenParams);
        case "RecordLiteralKeyValue":
            return getSourceForRecordLiteralKeyValue(node, pretty, replaceLambda, sourceGenParams);
        case "Resource":
            return getSourceForResource(node, pretty, replaceLambda, sourceGenParams);
        case "RestArgsExpr":
            return getSourceForRestArgsExpr(node, pretty, replaceLambda, sourceGenParams);
        case "Retry":
            return getSourceForRetry(node, pretty, replaceLambda, sourceGenParams);
        case "Return":
            return getSourceForReturn(node, pretty, replaceLambda, sourceGenParams);
        case "SelectClause":
            return getSourceForSelectClause(node, pretty, replaceLambda, sourceGenParams);
        case "SelectExpression":
            return getSourceForSelectExpression(node, pretty, replaceLambda, sourceGenParams);
        case "Service":
            return getSourceForService(node, pretty, replaceLambda, sourceGenParams);
        case "SimpleVariableRef":
            return getSourceForSimpleVariableRef(node, pretty, replaceLambda, sourceGenParams);
        case "StreamAction":
            return getSourceForStreamAction(node, pretty, replaceLambda, sourceGenParams);
        case "StreamingInput":
            return getSourceForStreamingInput(node, pretty, replaceLambda, sourceGenParams);
        case "StreamingQuery":
            return getSourceForStreamingQuery(node, pretty, replaceLambda, sourceGenParams);
        case "StringTemplateLiteral":
            return getSourceForStringTemplateLiteral(node, pretty, replaceLambda, sourceGenParams);
        case "Table":
            return getSourceForTable(node, pretty, replaceLambda, sourceGenParams);
        case "TableQueryExpression":
            return getSourceForTableQueryExpression(node, pretty, replaceLambda, sourceGenParams);
        case "TableQuery":
            return getSourceForTableQuery(node, pretty, replaceLambda, sourceGenParams);
        case "TernaryExpr":
            return getSourceForTernaryExpr(node, pretty, replaceLambda, sourceGenParams);
        case "Throw":
            return getSourceForThrow(node, pretty, replaceLambda, sourceGenParams);
        case "Transaction":
            return getSourceForTransaction(node, pretty, replaceLambda, sourceGenParams);
        case "Transform":
            return getSourceForTransform(node, pretty, replaceLambda, sourceGenParams);
        case "Transformer":
            return getSourceForTransformer(node, pretty, replaceLambda, sourceGenParams);
        case "Try":
            return getSourceForTry(node, pretty, replaceLambda, sourceGenParams);
        case "TupleDestructure":
            return getSourceForTupleDestructure(node, pretty, replaceLambda, sourceGenParams);
        case "TupleTypeNode":
            return getSourceForTupleTypeNode(node, pretty, replaceLambda, sourceGenParams);
        case "TypeCastExpr":
            return getSourceForTypeCastExpr(node, pretty, replaceLambda, sourceGenParams);
        case "TypeConversionExpr":
            return getSourceForTypeConversionExpr(node, pretty, replaceLambda, sourceGenParams);
        case "TypeDefinition":
            return getSourceForTypeDefinition(node, pretty, replaceLambda, sourceGenParams);
        case "ObjectType":
            return getSourceForObjectType(node, pretty, replaceLambda, sourceGenParams);
        case "RecordType":
            return getSourceForRecordType(node, pretty, replaceLambda, sourceGenParams);
        case "TypedescExpression":
            return getSourceForTypedescExpression(node, pretty, replaceLambda, sourceGenParams);
        case "TypeofExpression":
            return getSourceForTypeofExpression(node, pretty, replaceLambda, sourceGenParams);
        case "TypeInitExpr":
            return getSourceForTypeInitExpr(node, pretty, replaceLambda, sourceGenParams);
        case "UnaryExpr":
            return getSourceForUnaryExpr(node, pretty, replaceLambda, sourceGenParams);
        case "UnionTypeNode":
            return getSourceForUnionTypeNode(node, pretty, replaceLambda, sourceGenParams);
        case "UserDefinedType":
            return getSourceForUserDefinedType(node, pretty, replaceLambda, sourceGenParams);
        case "ValueType":
            return getSourceForValueType(node, pretty, replaceLambda, sourceGenParams);
        case "Variable":
            return getSourceForVariable(node, pretty, replaceLambda, sourceGenParams);
        case "VariableDef":
            return getSourceForVariableDef(node, pretty, replaceLambda, sourceGenParams);
        case "Where":
            return getSourceForWhere(node, pretty, replaceLambda, sourceGenParams);
        case "While":
            return getSourceForWhile(node, pretty, replaceLambda, sourceGenParams);
        case "WindowClause":
            return getSourceForWindowClause(node, pretty, replaceLambda, sourceGenParams);
        case "Worker":
            return getSourceForWorker(node, pretty, replaceLambda, sourceGenParams);
        case "WorkerReceive":
            return getSourceForWorkerReceive(node, pretty, replaceLambda, sourceGenParams);
        case "WorkerSend":
            return getSourceForWorkerSend(node, pretty, replaceLambda, sourceGenParams);
        case "XmlAttribute":
            return getSourceForXmlAttribute(node, pretty, replaceLambda, sourceGenParams);
        case "XmlAttributeAccessExpr":
            return getSourceForXmlAttributeAccessExpr(node, pretty, replaceLambda, sourceGenParams);
        case "XmlCommentLiteral":
            return getSourceForXmlCommentLiteral(node, pretty, replaceLambda, sourceGenParams);
        case "XmlElementLiteral":
            return getSourceForXmlElementLiteral(node, pretty, replaceLambda, sourceGenParams);
        case "XmlPiLiteral":
            return getSourceForXmlPiLiteral(node, pretty, replaceLambda, sourceGenParams);
        case "XmlQname":
            return getSourceForXmlQname(node, pretty, replaceLambda, sourceGenParams);
        case "XmlQuotedString":
            return getSourceForXmlQuotedString(node, pretty, replaceLambda, sourceGenParams);
        case "XmlTextLiteral":
            return getSourceForXmlTextLiteral(node, pretty, replaceLambda, sourceGenParams);
        case "Xmlns":
            return getSourceForXmlns(node, pretty, replaceLambda, sourceGenParams);

            // auto gen end
            /* eslint-enable max-len */
            default:
                return "";
        }
    }

    @FindbugsSuppressWarnings
    public String w(String defaultWS, SourceGenParams sourceGenParams) {
        JsonArray ws = sourceGenParams.getWs();
        int i = sourceGenParams.getI();

        if (ws.size() > 0 &&
                (ws.size() >= (i + 1))) {
            String wsI = ws.get(i).getAsString();
            sourceGenParams.setI(i + 1);
            // Check if the whitespace have comments
            boolean hasComment = (wsI != null) && wsI.trim().length() > 0;
            if (hasComment || (!sourceGenParams.isShouldIndent() && wsI != null)) {
                return wsI;
            }
        }
        return defaultWS;
    }

    @FindbugsSuppressWarnings
    public String a(String afterWS, boolean shouldIndent) {
        if (shouldIndent) {
            return afterWS;
        }
        return "";
    }

    @FindbugsSuppressWarnings
    private String indent() {
        ++l;
        return "";
    }

    @FindbugsSuppressWarnings
    private String outdent(JsonObject node, boolean shouldIndent) {
        --l;
        if (shouldIndent) {
            if (node.has("documentationText")) {
                String[] indent = node.get("documentationText").getAsString().split("\n");
                if (indent != null && indent.length > 0) {
                    if (indent[indent.length - 1].equals(repeat(TAB, l))) {
                        // if documentation text already contains the correct dent
                        return "";
                    }
                }
            }
            return "\r\n" + repeat(TAB, l);
        }
        return "";
    }

    @FindbugsSuppressWarnings
    private String dent(boolean shouldIndent) {
        if (shouldIndent) {
            return "\r\n" + repeat(TAB, l);
        }
        return "";
    }

    @FindbugsSuppressWarnings
    private String repeat(String tab, int l) {
        StringBuilder result = new StringBuilder();
        for (int j = 0; j < l; j++) {
            result.append(tab);
        }
        return result.toString();
    }

    @FindbugsSuppressWarnings
    public String join(JsonArray arr, boolean pretty, boolean replaceLambda,
                       String defaultWS, String separator, boolean suffixLast, SourceGenParams sourceGenParams) {
        StringBuilder str = new StringBuilder();
        for (int j = 0; j < arr.size(); j++) {
            JsonObject node = arr.get(j).getAsJsonObject();
            if (node.get("kind").getAsString().equals("Identifier")) {
                defaultWS = w(defaultWS, sourceGenParams);
                str.append(defaultWS);
            }
            str.append(getSourceOf(node, pretty, replaceLambda));
            if (separator != null && (suffixLast || j != (arr.size() - 1))) {
                defaultWS = w(defaultWS, sourceGenParams);
                str.append(defaultWS).append(separator);
            }
        }
        return str.toString();
    }

    @FindbugsSuppressWarnings
    private void modifyNode(JsonObject node, String parentKind) {
        String kind = node.get("kind").getAsString();

        if (kind.equals("If")) {
            if (node.getAsJsonObject("elseStatement") != null) {
                node.addProperty("ladderParent", true);
            }

            if (node.has("ws") && node.getAsJsonArray("ws").size() > 1 &&
                    node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString().equals("else") &&
                    node.getAsJsonArray("ws").get(1).getAsJsonObject().get("text").getAsString().equals("if")) {
                node.addProperty("isElseIfBlock", true);
            }
        }

        if (kind.equals("Transaction")) {
            if (node.has("condition") && node.getAsJsonObject("condition").has("value")) {
                JsonObject retry = null;
                if (node.has("failedBody") &&
                        node.getAsJsonObject("failedBody").has("statements")) {
                    for (JsonElement statement :
                            node.getAsJsonObject("failedBody").get("statements").getAsJsonArray()) {
                        if (statement.isJsonObject() && statement.getAsJsonObject().has("kind") &&
                                statement.getAsJsonObject().get("kind").getAsString().equals("retry")) {
                            retry = statement.getAsJsonObject();
                        }
                    }
                }
                if (node.has("committedBody") &&
                        node.getAsJsonObject("committedBody").has("statements")) {
                    for (JsonElement statement :
                            node.getAsJsonObject("committedBody").get("statements").getAsJsonArray()) {
                        if (statement.isJsonObject() && statement.getAsJsonObject().has("kind") &&
                                statement.getAsJsonObject().get("kind").getAsString().equals("retry")) {
                            retry = statement.getAsJsonObject();
                        }
                    }
                }

                if (node.has("transactionBody") &&
                        node.getAsJsonObject("transactionBody").has("statements")) {
                    for (JsonElement statement :
                            node.getAsJsonObject("transactionBody").get("statements").getAsJsonArray()) {
                        if (statement.isJsonObject() && statement.getAsJsonObject().has("kind") &&
                                statement.getAsJsonObject().get("kind").getAsString().equals("retry")) {
                            retry = statement.getAsJsonObject();
                        }
                    }
                }

                if (retry != null) {
                    retry.addProperty("count", node.getAsJsonObject("condition").get("value").getAsString());
                }
            }
        }

        if ((kind.equals("XmlCommentLiteral") ||
                kind.equals("XmlElementLiteral") ||
                kind.equals("XmlTextLiteral") ||
                kind.equals("XmlPiLiteral")) &&
                        node.has("ws") &&
                        node.getAsJsonArray("ws").get(0) != null &&
                        node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString().contains("xml")
                        && node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString().contains("`")) {
            node.addProperty("root", true);
            node.addProperty("startLiteral", node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString());
        }

        if (parentKind.equals("XmlElementLiteral") ||
                parentKind.equals("XmlTextLiteral") ||
                parentKind.equals("XmlPiLiteral")) {
            node.addProperty("inTemplateLiteral", true);
        }

        if (kind.equals("XmlPiLiteral") && node.has("ws")) {
            JsonObject startTagWS = new JsonObject();
            startTagWS.addProperty("text", "<?");
            startTagWS.addProperty("ws", "");

            JsonObject endTagWS = new JsonObject();
            endTagWS.addProperty("text", "?>");
            endTagWS.addProperty("ws", "");

            if (node.has("root") &&
                    node.get("root").getAsBoolean() && node.getAsJsonArray("ws").size() > 1) {
                node.add("ws", addDataToArray(1, startTagWS, node.getAsJsonArray("ws")));
                node.add("ws", addDataToArray(2, endTagWS, node.getAsJsonArray("ws")));
            }

            if (!node.has("root") || !(node.has("root") && node.get("root").getAsBoolean())) {
                node.add("ws", addDataToArray(0, startTagWS, node.getAsJsonArray("ws")));
                node.add("ws",
                        addDataToArray(node.getAsJsonArray("ws").size(), endTagWS,
                                node.getAsJsonArray("ws")));
            }

            if (node.has("target") &&
                    node.getAsJsonObject("target").has("unescapedValue")) {
                JsonObject target = node.getAsJsonObject("target");
                for (int i = 0; i < target.getAsJsonArray("ws").size(); i++) {
                    if (target.getAsJsonArray("ws").get(i).getAsJsonObject().get("text").getAsString().contains("<?")
                            && target.getAsJsonArray("ws").get(i).getAsJsonObject().get("text").getAsString().contains(target.get("unescapedValue").getAsString())) {
                        target.addProperty("unescapedValue", target.getAsJsonArray("ws").get(i).getAsJsonObject().get("text").getAsString().replace("<?", ""));
                    }
                }
            }
        }

        if (kind.equals("AnnotationAttachment") &&
                node.getAsJsonObject("packageAlias").get("value").getAsString().equals("builtin")) {
            node.addProperty("builtin", true);
        }

        if (kind.equals("Identifier")) {
            if (node.has("literal") && node.get("literal").getAsBoolean()) {
                node.addProperty("valueWithBar", "^\"" + node.get("value").getAsString() + "\"");
            } else {
                node.addProperty("valueWithBar", node.get("value").getAsString());
            }
        }

        if (kind.equals("Import")) {
            if (node.getAsJsonObject("alias") != null &&
                    node.getAsJsonObject("alias").get("value") != null &&
                    node.getAsJsonArray("packageName") != null && node.getAsJsonArray("packageName").size() != 0) {
                if (!node.getAsJsonObject("alias").get("value").getAsString()
                        .equals(node.getAsJsonArray("packageName").get(node
                                .getAsJsonArray("packageName").size() - 1).getAsJsonObject()
                                .get("value").getAsString())) {
                    node.addProperty("userDefinedAlias", true);
                }
            }

            if ((node.getAsJsonArray("packageName") != null && node.getAsJsonArray("packageName").size() == 2 &&
                    node.getAsJsonArray("packageName").get(0).getAsJsonObject().get("value").getAsString()
                            .equals("transactions") && node.getAsJsonArray("packageName").get(1).getAsJsonObject()
                    .get("value").getAsString().equals("coordinator")) || (node.getAsJsonObject("alias") != null &&
                    node.getAsJsonObject("alias").get("value") != null &&
                    node.getAsJsonObject("alias").get("value").getAsString().startsWith("."))) {
                node.addProperty("isInternal", true);
            }
        }

        if (parentKind.equals("CompilationUnit") && (kind.equals("Variable") || kind.equals("Xmlns"))) {
            node.addProperty("global", true);
        }

        if (kind.equals("VariableDef") && node.getAsJsonObject("variable") != null &&
                node.getAsJsonObject("variable").getAsJsonObject("typeNode") != null &&
                node.getAsJsonObject("variable").getAsJsonObject("typeNode")
                        .get("kind").getAsString().equals("EndpointType")) {
            node.getAsJsonObject("variable").addProperty("endpoint", true);
            node.addProperty("endpoint", true);
        }

        if (kind.equals("Variable")) {
            if (parentKind.equals("ObjectType")) {
                node.addProperty("inObject", true);
            }

            if (node.has("typeNode")
                    && node.getAsJsonObject("typeNode").has("isAnonType")
                    && node.getAsJsonObject("typeNode").get("isAnonType").getAsBoolean()) {
                node.addProperty("isAnonType", true);
            }

            if (node.has("initialExpression") &&
                    node.getAsJsonObject("initialExpression").has("async") &&
                    node.getAsJsonObject("initialExpression").get("async").getAsBoolean()) {
                if (node.has("ws")) {
                    JsonArray ws = node.getAsJsonArray("ws");
                    for (int i = 0; i < ws.size(); i++) {
                        if (ws.get(i).getAsJsonObject().get("text").getAsString().equals("start")) {
                            if (node.getAsJsonObject("initialExpression").has("ws")) {
                                node.getAsJsonObject("initialExpression").add("ws",
                                        addDataToArray(0, node.getAsJsonArray("ws").get(i),
                                                node.getAsJsonObject("initialExpression").getAsJsonArray("ws")));
                                node.getAsJsonArray("ws").remove(i);
                            }
                        }
                    }
                }
            }

            if (node.has("typeNode") && node.getAsJsonObject("typeNode").has("nullable") &&
                    node.getAsJsonObject("typeNode").get("nullable").getAsBoolean() &&
                    node.getAsJsonObject("typeNode").has("ws")) {
                JsonArray ws = node.getAsJsonObject("typeNode").get("ws").getAsJsonArray();
                for (int i = 0; i < ws.size(); i++) {
                    if (ws.get(i).getAsJsonObject().get("text").getAsString().equals("?")) {
                        node.getAsJsonObject("typeNode").addProperty("nullableOperatorAvailable", true);
                        break;
                    }
                }
            }

            if (node.has("typeNode") &&
                    node.getAsJsonObject("typeNode").has("ws") &&
                    !node.has("ws")) {
                node.addProperty("noVisibleName", true);
            }

            if (node.has("ws")) {
                JsonArray ws = node.getAsJsonArray("ws");
                for (int i = 0; i < ws.size(); i++) {
                    if (ws.get(i).getAsJsonObject().get("text").getAsString().equals(";")) {
                        node.addProperty("endWithSemicolon", true);
                    }

                    if (ws.get(i).getAsJsonObject().get("text").getAsString().equals(",")) {
                        node.addProperty("endWithComma", true);
                    }
                }
            }
        }

        if (kind.equals("Service")) {
            if (!node.has("serviceTypeStruct")) {
                node.addProperty("isServiceTypeUnavailable", true);
            }

            if (!node.has("anonymousEndpointBind") &&
                    node.has("boundEndpoints") &&
                    node.getAsJsonArray("boundEndpoints").size() <= 0) {
                boolean bindAvailable = false;
                for (JsonElement ws : node.getAsJsonArray("ws")) {
                    if (ws.getAsJsonObject().get("text").getAsString().equals("bind")) {
                        bindAvailable = true;
                        break;
                    }
                }

                if (!bindAvailable) {
                    node.addProperty("bindNotAvailable", true);
                }
            }
        }

        if (kind.equals("Resource") && node.has("parameters") && node.getAsJsonArray("parameters").size() > 0) {
            if (node.getAsJsonArray("parameters").get(0).getAsJsonObject().has("ws")) {
                for (JsonElement ws : node.getAsJsonArray("parameters").get(0).getAsJsonObject().getAsJsonArray("ws")) {
                    if (ws.getAsJsonObject().get("text").getAsString().equals("endpoint")) {
                        JsonObject endpointParam = node.getAsJsonArray("parameters").get(0).getAsJsonObject();
                        String valueWithBar = endpointParam.get("name").getAsJsonObject().has("valueWithBar")
                                ? endpointParam.get("name").getAsJsonObject().get("valueWithBar").getAsString()
                                : endpointParam.get("name").getAsJsonObject().get("value").getAsString();

                        endpointParam.addProperty("serviceEndpoint", true);
                        endpointParam.get("name").getAsJsonObject().addProperty("value",
                                endpointParam.get("name").getAsJsonObject().get("value").getAsString().replace("$", ""));
                        endpointParam.get("name").getAsJsonObject().addProperty("valueWithBar",
                                valueWithBar.replace("$", ""));
                        break;
                    }
                }
            }
        }


        if (kind.equals("ForkJoin")) {
            if (node.getAsJsonObject("joinBody") != null) {
                node.getAsJsonObject("joinBody").add("position",
                        node.getAsJsonObject("joinResultVar").getAsJsonObject("position"));
            }

            if (node.getAsJsonObject("timeoutBody") != null) {
                node.getAsJsonObject("timeoutBody").add("position",
                        node.getAsJsonObject("timeOutExpression").getAsJsonObject("position"));
            }
        }

        // Check if sorrounded by curlies
        if (kind.equals("MatchPatternClause") || kind.equals("MatchExpressionPatternClause")) {
            if (node.has("ws") && node.getAsJsonArray("ws").size() > 2) {
                node.addProperty("withCurlies", true);
            }
        }

        // Check if sorrounded by parantheses
        if (kind.equals("ValueType")) {
            if (node.has("ws") && node.getAsJsonArray("ws").size() > 2) {
                node.addProperty("withParantheses", true);
            }

            if (node.has("typeKind") && node.get("typeKind").getAsString().equals("nil") && node.has("ws")) {
                node.addProperty("emptyParantheses", true);
            }

            if (node.has("nullable") && node.get("nullable").getAsBoolean() && node.has("ws")) {
                for (int i = 0; i < node.get("ws").getAsJsonArray().size(); i++) {
                    if (node.get("ws").getAsJsonArray().get(i)
                            .getAsJsonObject().get("text").getAsString().equals("?")) {
                        node.addProperty("nullableOperatorAvailable", true);
                        break;
                    }
                }
            }
        }

        if (kind.equals("UnionTypeNode") && node.has("ws")) {
            if (node.getAsJsonArray("ws").size() > 2) {
                for (JsonElement ws : node.getAsJsonArray("ws")) {
                    if (ws.getAsJsonObject().get("text").getAsString().equals("(")) {
                        node.addProperty("withParantheses", true);
                        break;
                    }
                }
            }

            JsonArray memberTypeNodes = node.get("memberTypeNodes").getAsJsonArray();
            for (int i = 0; i < memberTypeNodes.size(); i++) {
                if (memberTypeNodes.get(i).getAsJsonObject().has("nullable")
                        && memberTypeNodes.get(i).getAsJsonObject().get("nullable").getAsBoolean()) {
                    for (JsonElement ws : node.getAsJsonArray("ws")) {
                        if (ws.getAsJsonObject().get("text").getAsString().equals("?")) {
                            memberTypeNodes.get(i).getAsJsonObject()
                                    .addProperty("nullableOperatorAvailable", true);
                            break;
                        }
                    }
                }
            }
        }

        if (kind.equals("Function")) {
            if (node.has("returnTypeNode") &&
                    node.getAsJsonObject("returnTypeNode").has("ws") &&
                    node.getAsJsonObject("returnTypeNode").getAsJsonArray("ws").size() > 0) {
                node.addProperty("hasReturns", true);
            }

            if (node.has("defaultableParameters")) {
                JsonArray defaultableParameters = node.getAsJsonArray("defaultableParameters");
                for (int i = 0; i < defaultableParameters.size(); i++) {
                    defaultableParameters.get(i).getAsJsonObject().addProperty("defaultable", true);
                    defaultableParameters.get(i).getAsJsonObject().getAsJsonObject("variable")
                            .addProperty("defaultable", true);
                }
            }

            // Sort and add all the parameters.
            JsonArray allParamsTemp = node.getAsJsonArray("parameters");
            allParamsTemp.addAll(node.getAsJsonArray("defaultableParameters"));
            List<JsonElement> allParamElements = new ArrayList<>();
            allParamsTemp.forEach(jsonElement -> {
                allParamElements.add(jsonElement);
            });

            Collections.sort(allParamElements, (a, b) -> {
                int comparator = 0;
                comparator = (((a.getAsJsonObject().getAsJsonObject("position").get("endColumn").getAsInt() >
                        b.getAsJsonObject().getAsJsonObject("position").get("startColumn").getAsInt())
                        && (a.getAsJsonObject().getAsJsonObject("position").get("endLine").getAsInt() ==
                        b.getAsJsonObject().getAsJsonObject("position").get("endLine").getAsInt())) ||
                        (a.getAsJsonObject().getAsJsonObject("position").get("endLine").getAsInt() >
                                b.getAsJsonObject().getAsJsonObject("position").get("endLine").getAsInt())) ? 1 : -1;
                return comparator;
            });

            JsonArray allParams = new JsonArray();

            allParamElements.forEach(jsonElement -> {
                allParams.add(jsonElement);
            });

            node.add("allParams", allParams);

            if (node.has("receiver") &&
                    !node.getAsJsonObject("receiver").has("ws")) {
                if (node.getAsJsonObject("receiver").has("typeNode")
                        && node.getAsJsonObject("receiver").getAsJsonObject("typeNode").has("ws")
                        && node.getAsJsonObject("receiver")
                        .getAsJsonObject("typeNode").getAsJsonArray("ws").size() > 0) {
                    for (JsonElement ws : node.get("ws").getAsJsonArray()) {
                        if (ws.getAsJsonObject().get("text").getAsString().equals("::")) {
                            node.addProperty("objectOuterFunction", true);
                            if (node.getAsJsonObject("receiver")
                                    .getAsJsonObject("typeNode").getAsJsonArray("ws").get(0)
                                    .getAsJsonObject().get("text").getAsString().equals("function")) {
                                node.getAsJsonObject("receiver")
                                        .getAsJsonObject("typeNode").getAsJsonArray("ws").remove(0);
                            }
                            node.add("objectOuterFunctionTypeName", node.getAsJsonObject("receiver")
                                    .getAsJsonObject("typeNode").getAsJsonObject("typeName"));
                            break;
                        }
                    }
                } else {
                    node.addProperty("noVisibleReceiver", true);
                }
            }

            if (node.has("restParameters") &&
                    node.has("parameters") && node.getAsJsonArray("parameters").size() > 0) {
                node.addProperty("hasRestParams", true);
            }

            if (node.has("restParameters") &&
                    node.getAsJsonObject("restParameters").has("typeNode")) {
                node.getAsJsonObject("restParameters").getAsJsonObject("typeNode").addProperty("isRestParam", true);
            }
        }

        if (kind.equals("TypeDefinition") && node.has("typeNode")) {
            if (!node.has("ws")) {
                node.addProperty("notVisible", true);
            }

            if (node.has("name") &&
                    node.getAsJsonObject("name").get("value").getAsString().startsWith("$anonType$")) {
                this.anonTypes.put(node.getAsJsonObject("name").get("value").getAsString(),
                        node.getAsJsonObject("typeNode"));
            }

            if (node.getAsJsonObject("typeNode").get("kind").getAsString().equals("ObjectType")) {
                node.addProperty("isObjectType", true);
            }

            if (node.getAsJsonObject("typeNode").get("kind").getAsString().equals("RecordType")) {
                node.addProperty("isRecordType", true);
                if (node.has("ws")) {
                    for (int i = 0; i < node.getAsJsonArray("ws").size(); i++) {
                        if (node.getAsJsonArray("ws").get(i)
                                .getAsJsonObject().get("text").getAsString().equals("record")) {
                            node.addProperty("isRecordKeywordAvailable", true);
                        }
                    }
                }
            }
        }

        if (kind.equals("ObjectType")) {
            if (node.has("initFunction")) {
                if (!node.getAsJsonObject("initFunction").has("ws")) {
                    node.getAsJsonObject("initFunction").addProperty("defaultConstructor", true);
                } else {
                    node.getAsJsonObject("initFunction").addProperty("isConstructor", true);
                }
            }
        }

        if (kind.equals("RecordType")) {
            if (node.has("restFieldType")) {
                node.addProperty("isRestFieldAvailable", true);
            }
        }

        if (kind.equals("TypeInitExpr")) {
            if (node.getAsJsonArray("expressions").size() <= 0) {
                node.addProperty("noExpressionAvailable", true);
            }

            if (node.has("ws")) {
                for (int i = 0; i < node.getAsJsonArray("ws").size(); i++) {
                    if (node.getAsJsonArray("ws").get(i).getAsJsonObject().get("text").getAsString()
                            .equals("(")) {
                        node.addProperty("hasParantheses", true);
                        break;
                    }
                }
            }

            if (!node.has("type")) {
                node.addProperty("noTypeAttached", true);
            } else {
                node.add("typeName", node.getAsJsonObject("type").get("typeName"));
            }
        }

        if (kind.equals("Return")) {
            if (node.has("expression") &&
                    node.getAsJsonObject("expression").get("kind").getAsString().equals("Literal")) {
                if (node.getAsJsonObject("expression").get("value").getAsString().equals("()")) {
                    node.addProperty("noExpressionAvailable", true);
                }

                if (node.getAsJsonObject("expression").get("value").getAsString().equals("null")) {
                    node.getAsJsonObject("expression").addProperty("emptyParantheses", true);
                }
            }
        }

        if (kind.equals("Documentation")) {
            if (node.has("ws") && node.getAsJsonArray("ws").size() > 1) {
                node.addProperty("startDoc",
                        node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString());
            }

            for (int j = 0; j < node.getAsJsonArray("attributes").size(); j++) {
                JsonObject attribute = node.getAsJsonArray("attributes").get(j).getAsJsonObject();
                if (attribute.has("ws")) {
                    for (int i = 0; i < attribute.getAsJsonArray("ws").size(); i++) {
                        String text = attribute.getAsJsonArray("ws").get(i).getAsJsonObject()
                                .get("text").getAsString();
                        if (text.contains("{{") && !attribute.has("paramType")) {
                            int lastIndex = text.lastIndexOf("{{");
                            String paramType = text.substring(0, lastIndex);
                            String startCurl = text.substring(lastIndex, text.length());
                            attribute.getAsJsonArray("ws").get(i).getAsJsonObject()
                                    .addProperty("text", paramType);
                            attribute.addProperty("paramType", paramType);

                            JsonObject ws = new JsonObject();
                            ws.addProperty("text", startCurl);
                            ws.addProperty("ws", "");
                            ws.addProperty("static", false);
                            attribute.add("ws", addDataToArray(++i, ws, attribute.getAsJsonArray("ws")));
                        }
                    }
                }
            }
        }

        // Tag rest variable nodes
        if (kind.equals("Function") || kind.equals("Resource")) {
            if (node.has("restParameters")) {
                node.getAsJsonObject("restParameters").addProperty("rest", true);
            }
        }

        if (kind.equals("PostIncrement")) {
            node.addProperty("operator",
                    (node.get("operatorKind").getAsString() + node.get("operatorKind").getAsString()));
        }

        if (kind.equals("SelectExpression") && node.has("identifier")) {
            node.addProperty("identifierAvailable", true);
        }

        if (kind.equals("StreamAction") && node.has("invokableBody")) {
            if (node.getAsJsonObject("invokableBody").has("functionNode")) {
                node.getAsJsonObject("invokableBody").getAsJsonObject("functionNode")
                        .addProperty("isStreamAction", true);
            }
        }

        if (kind.equals("StreamingInput") && node.has("alias")) {
            node.addProperty("aliasAvailable", true);
        }

        if (kind.equals("IntRangeExpr")) {
            if (node.has("ws") && node.getAsJsonArray("ws").size() > 0) {
                if (node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text")
                        .getAsString().equals("[")) {
                    node.addProperty("isWrappedWithBracket", true);
                } else if (node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text")
                        .getAsString().equals("(")) {
                    node.addProperty("isWrappedWithParenthesis", true);
                }
            }
        }

        if (kind.equals("FunctionType")) {
            if (node.has("returnTypeNode")
                    && node.getAsJsonObject("returnTypeNode").has("ws")) {
                node.addProperty("hasReturn", true);
            }

            if (node.has("ws")
                    && node.getAsJsonArray("ws").size() > 0
                    && node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text")
                    .getAsString().equals("(")) {
                node.addProperty("withParantheses", true);
            }
        }

        if (kind.equals("Literal") && !parentKind.equals("StringTemplateLiteral")) {
            if (node.has("ws")
                    && node.getAsJsonArray("ws").size() == 1
                    && node.getAsJsonArray("ws").get(0).getAsJsonObject().has("text")) {
                node.addProperty("value",
                        node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString());
            }

            if ((node.get("value").getAsString().equals("nil")
                    || node.get("value").getAsString().equals("null"))
                    && node.has("ws")
                    && node.getAsJsonArray("ws").size() < 3
                    && node.getAsJsonArray("ws").get(0) != null
                    && node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text")
                    .getAsString().equals("(")) {
                node.addProperty("emptyParantheses", true);
            }
        }

        if (kind.equals("Foreach")) {
            if (node.has("ws")) {
                for (JsonElement ws : node.getAsJsonArray("ws")) {
                    if (ws.getAsJsonObject().get("text").getAsString().equals("(")) {
                        node.addProperty("withParantheses", true);
                        break;
                    }
                }
            }
        }

        if (kind.equals("Endpoint")) {
            if (node.has("ws")) {
                for (JsonElement ws : node.getAsJsonArray("ws")) {
                    if (ws.getAsJsonObject().get("text").getAsString().equals("=")) {
                        node.addProperty("isConfigAssignment", true);
                        break;
                    }
                }
            }
        }

        if (kind.equals("UserDefinedType")) {
            if (node.has("ws") && node.has("nullable") && node.get("nullable").getAsBoolean()) {
                for (JsonElement ws : node.getAsJsonArray("ws")) {
                    if (ws.getAsJsonObject().get("text").getAsString().equals("?")) {
                        node.addProperty("nullableOperatorAvailable", true);
                        break;
                    }
                }
            }

            if (node.has("typeName") && node.getAsJsonObject("typeName").has("value")
                    && anonTypes.containsKey(node.getAsJsonObject("typeName").get("value").getAsString())) {
                node.addProperty("isAnonType", true);
                node.add("anonType",
                        anonTypes.get(node.getAsJsonObject("typeName").get("value").getAsString()));
                anonTypes.remove(node.getAsJsonObject("typeName").get("value").getAsString());
            }
        }

        if (kind.equals("ArrayType")) {
            if (node.has("dimensions") &&
                    node.get("dimensions").getAsInt() > 0 &&
                    node.has("ws")) {
                String dimensionAsString = "";
                JsonObject startingBracket = null;
                JsonObject endingBracket = null;
                StringBuilder content = new StringBuilder();
                JsonArray ws = node.getAsJsonArray("ws");
                for (int j = 0; j < ws.size(); j++) {
                    if (ws.get(j).getAsJsonObject().get("text").getAsString().equals("[")) {
                        startingBracket = ws.get(j).getAsJsonObject();
                    } else if (ws.get(j).getAsJsonObject().get("text").getAsString().equals("]")) {
                        endingBracket = ws.get(j).getAsJsonObject();

                        dimensionAsString += startingBracket.get("text").getAsString() + content.toString()
                                + endingBracket.get("ws").getAsString()
                                + endingBracket.get("text").getAsString();

                        startingBracket = null;
                        endingBracket = null;
                        content = new StringBuilder();
                    } else if (startingBracket != null) {
                        content.append(ws.get(j).getAsJsonObject().get("ws").getAsString())
                                .append(ws.get(j).getAsJsonObject().get("text").getAsString());
                    }
                }

                node.addProperty("dimensionAsString", dimensionAsString);
            }
        }

        if (kind.equals("Block")
                && node.has("ws")
                && node.getAsJsonArray("ws").size() > 0
                && node.getAsJsonArray("ws").get(0)
                .getAsJsonObject().get("text").getAsString().equals("else")) {
            node.addProperty("isElseBlock", true);
        }

        if (kind.equals("FieldBasedAccessExpr")
                && node.has("ws")
                && node.getAsJsonArray("ws").size() > 0
                && node.getAsJsonArray("ws").get(0)
                .getAsJsonObject().get("text").getAsString().equals("!")) {
            node.addProperty("errorLifting", true);
        }

        if (kind.equals("StringTemplateLiteral")) {
            if (node.has("ws")
                    && node.getAsJsonArray("ws").size() > 0
                    && node.getAsJsonArray("ws").get(0).getAsJsonObject()
                    .get("text").getAsString().contains("string")
                    && node.getAsJsonArray("ws").get(0).getAsJsonObject()
                    .get("text").getAsString().contains("`")) {

                node.addProperty("startTemplate",
                        node.getAsJsonArray("ws").get(0).getAsJsonObject().get("text").getAsString());
                literalWSAssignForTemplates(1, 2, node.getAsJsonArray("expressions"),
                        node.getAsJsonArray("ws"), 2);
            }
        }

        if (kind.equals("XmlCommentLiteral") && node.has("ws")) {
            int length = node.getAsJsonArray("ws").size();
            for (int i = 0; i < length; i++) {
                if (node.getAsJsonArray("ws").get(i).getAsJsonObject()
                        .get("text").getAsString().contains("-->")
                        && node.getAsJsonArray("ws").get(i).getAsJsonObject()
                        .get("text").getAsString().length() > 3) {
                    JsonObject ws = new JsonObject();
                    ws.addProperty("text", "-->");
                    ws.addProperty("ws", "");

                    node.getAsJsonArray("ws").get(i).getAsJsonObject().addProperty("text",
                            node.getAsJsonArray("ws").get(i).getAsJsonObject()
                                    .get("text").getAsString().replace("-->", ""));
                    node.add("ws", addDataToArray(i + 1, ws, node.getAsJsonArray("ws")));
                    break;
                }
            }

            if (node.has("root") && node.get("root").getAsBoolean()) {
                literalWSAssignForTemplates(2, 3, node.getAsJsonArray("textFragments"),
                        node.getAsJsonArray("ws"), 4);
            } else {
                literalWSAssignForTemplates(1, 2, node.getAsJsonArray("textFragments"),
                        node.getAsJsonArray("ws"), 2);
            }
        }
    }

    @FindbugsSuppressWarnings
    void literalWSAssignForTemplates(int currentWs, int nextWs,
                                     JsonArray literals, JsonArray ws, int wsStartLocation) {
        if (literals.size() == (ws.size() - wsStartLocation)) {
            for (int i = 0; i < literals.size(); i++) {
                if (literals.get(i).getAsJsonObject().get("kind").getAsString().equals("Literal")) {
                    if (!literals.get(i).getAsJsonObject().has("ws")) {
                        literals.get(i).getAsJsonObject().add("ws", new JsonArray());
                    }

                    if (ws.get(currentWs).getAsJsonObject().get("text").getAsString().contains("{{")) {
                        literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(currentWs));
                        literals.get(i).getAsJsonObject().addProperty("value",
                                ws.get(currentWs).getAsJsonObject().get("text").getAsString());
                        // TODO: use splice
                        ws.remove(currentWs);
                        literals.get(i).getAsJsonObject().addProperty("startTemplateLiteral", true);

                    } else if (ws.get(currentWs).getAsJsonObject().get("text").getAsString().contains("}}")) {
                        literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(currentWs));
                        if (ws.get(nextWs).getAsJsonObject().get("text").getAsString().contains("{{")) {
                            literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(nextWs));
                            literals.get(i).getAsJsonObject().addProperty("value",
                                    ws.get(nextWs).getAsJsonObject().get("text").getAsString());
                            literals.get(i).getAsJsonObject().addProperty("startTemplateLiteral", true);
                            // TODO: use splice
                            ws.remove(nextWs);
                        }
                        // TODO: use splice
                        ws.remove(currentWs);
                        literals.get(i).getAsJsonObject().addProperty("endTemplateLiteral", true);
                    }

                    if (i == (literals.size() - 1)) {
                        literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(currentWs));
                        literals.get(i).getAsJsonObject().addProperty("value",
                                ws.get(currentWs).getAsJsonObject().get("text").getAsString());
                        literals.get(i).getAsJsonObject().addProperty("lastNodeValue", true);
                        // TODO: use splice.
                        ws.remove(currentWs);
                    }
                }
            }
        } else if ((literals.size() - 1) == (ws.size() - wsStartLocation)) {
            for (int i = 0; i < literals.size(); i++) {
                if (literals.get(i).getAsJsonObject().get("kind").getAsString().equals("Literal")) {
                    if (!literals.get(i).getAsJsonObject().has("ws")) {
                        literals.get(i).getAsJsonObject().add("ws", new JsonArray());
                    }

                    if (ws.get(currentWs).getAsJsonObject().get("text").getAsString().contains("{{")) {
                        literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(currentWs));
                        literals.get(i).getAsJsonObject().addProperty("value",
                                ws.get(currentWs).getAsJsonObject().get("text").getAsString());
                        //TODO: use splice.
                        ws.remove(currentWs);
                        literals.get(i).getAsJsonObject().addProperty("startTemplateLiteral", true);
                    } else if (ws.get(currentWs).getAsJsonObject().get("text").getAsString().contains("}}")) {
                        literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(currentWs));
                        if (ws.get(nextWs).getAsJsonObject().get("text").getAsString().contains("{{")) {
                            literals.get(i).getAsJsonObject().get("ws").getAsJsonArray().add(ws.get(nextWs));
                            literals.get(i).getAsJsonObject().addProperty("value",
                                    ws.get(nextWs).getAsJsonObject().get("text").getAsString());
                            literals.get(i).getAsJsonObject().addProperty("startTemplateLiteral", true);
                            //TODO: use splice.
                            ws.remove(nextWs);
                        }
                        //TODO: use splice.
                        ws.remove(currentWs);
                        literals.get(i).getAsJsonObject().addProperty("endTemplateLiteral", true);
                    }
                }
            }
        }
    }

    @FindbugsSuppressWarnings
    JsonArray addDataToArray(int index, JsonElement element, JsonArray ws) {
        int length = ws.size() + 1;
        JsonArray array = new JsonArray();
        boolean added = false;

        for (int i = 0; i < length; i++) {
            if (i == index) {
                array.add(element);
                added = true;
            } else if (added) {
                array.add(ws.get(i - 1));
            } else {
                array.add(ws.get(i));
            }
        }

        return array;
    }

    @FindbugsSuppressWarnings
    public JsonObject build(JsonObject json, JsonObject parent, String parentKind) {
        String kind = json.get("kind").getAsString();
        for (Map.Entry<String, JsonElement> child : json.entrySet()) {
            if (!child.getKey().equals("position") && !child.getKey().equals("ws")) {
                if (child.getValue().isJsonObject() &&
                        child.getValue().getAsJsonObject().get("kind") != null) {
                    json.add(child.getKey(), build(child.getValue().getAsJsonObject(), json, kind));
                } else if (child.getValue().isJsonArray()) {
                    JsonArray childArray = child.getValue().getAsJsonArray();
                    for (int j = 0; j < childArray.size(); j++) {
                        JsonElement childItem = childArray.get(j);
                        if (kind.equals("CompilationUnit") &&
                                childItem.getAsJsonObject().get("kind").getAsString().equals("Function") &&
                                childItem.getAsJsonObject().has("lambda") &&
                                childItem.getAsJsonObject().get("lambda").getAsBoolean()) {
                            childArray.remove(j);
                            j--;
                        } else if (childItem.isJsonObject() &&
                                childItem.getAsJsonObject().get("kind") != null) {
                            childItem = build(childItem.getAsJsonObject(), json, kind);
                        }
                    }
                }
            }
        }
        modifyNode(json, parentKind);
        json.add("parent", parent);
        return json;
    }

    @FindbugsSuppressWarnings
    class SourceGenParams {
        private boolean shouldIndent = false;
        private int i;
        private JsonArray ws;

        public SourceGenParams() {
            this.ws = new JsonArray();
            this.i = 0;
            this.shouldIndent = false;
        }

        public boolean isShouldIndent() {
            return shouldIndent;
        }

        public int getI() {
            return i;
        }

        public JsonArray getWs() {
            return ws;
        }

        public void setI(int i) {
            this.i = i;
        }

        public void setShouldIndent(boolean shouldIndent) {
            this.shouldIndent = shouldIndent;
        }

        public void setWs(JsonArray ws) {
            this.ws = ws;
        }
    }
}

