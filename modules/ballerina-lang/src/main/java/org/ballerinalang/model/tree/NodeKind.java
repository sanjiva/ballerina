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
package org.ballerinalang.model.tree;

/**
 * @since 0.94
 */
public enum NodeKind {

    ACTION,
    ANNOTATION,
    ANNOTATION_ATTACHMENT,
    ANNOTATION_ATTRIBUTE,
    CATCH,
    COMPILATION_UNIT,
    CONNECTOR,
    ENUM,
    ENUMERATOR,
    FUNCTION,
    IDENTIFIER,
    IMPORT,
    PACKAGE,
    PACKAGE_DECLARATION,
    RECORD_LITERAL_KEY_VALUE,
    RESOURCE,
    SERVICE,
    STRUCT,
    VARIABLE,
    WORKER,
    XMLNS,
    TRANSFORMER,

    /* Expressions */
    ANNOTATION_ATTACHMENT_ATTRIBUTE,
    ANNOTATION_ATTACHMENT_ATTRIBUTE_VALUE,
    ARRAY_LITERAL_EXPR,
    BINARY_EXPR,
    CONNECTOR_INIT_EXPR,
    FIELD_BASED_ACCESS_EXPR,
    INDEX_BASED_ACCESS_EXPR,
    INT_RANGE_EXPR,
    INVOCATION,
    LAMBDA,
    LITERAL,
    RECORD_LITERAL_EXPR,
    SIMPLE_VARIABLE_REF,
    STRING_TEMPLATE_LITERAL,
    TERNARY_EXPR,
    TYPEOF_EXPRESSION,
    TYPE_CAST_EXPR,
    TYPE_CONVERSION_EXPR,
    UNARY_EXPR,
    XML_QNAME,
    XML_ATTRIBUTE,
    XML_ATTRIBUTE_ACCESS_EXPR,
    XML_QUOTED_STRING,
    XML_ELEMENT_LITERAL,
    XML_TEXT_LITERAL,
    XML_COMMENT_LITERAL,
    XML_PI_LITERAL,

    /* Statements */
    ABORT,
    ASSIGNMENT,
    BIND,
    BLOCK,
    BREAK,
    NEXT,
    EXPRESSION_STATEMENT,
    FOREACH,
    FORK_JOIN,
    IF,
    REPLY,
    RETURN,
    THROW,
    TRANSACTION,
    TRANSFORM,
    TRY,
    VARIABLE_DEF,
    WHILE,
    WORKER_RECEIVE,
    WORKER_SEND,

    /* Types */
    ARRAY_TYPE,
    BUILT_IN_REF_TYPE,
    CONSTRAINED_TYPE,
    FUNCTION_TYPE,
    USER_DEFINED_TYPE,
    ENDPOINT_TYPE,
    VALUE_TYPE,
}
