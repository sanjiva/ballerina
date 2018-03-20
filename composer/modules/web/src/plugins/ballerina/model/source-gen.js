/**
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
import _ from 'lodash';

let join;
const tab = '    ';

function times(n, f) {
    let s = '';
    for (let i = 0; i < n; i++) {
        s += f();
    }
    return s;
}

export default function getSourceOf(node, pretty = false, l = 0, replaceLambda) {
    if (!node) {
        return '';
    }
    let i = 0;
    const ws = node.ws ? node.ws.map(wsObj => wsObj.ws) : [];
    const shouldIndent = pretty || !ws.length;

    /**
     * White space generator function,
     * @param {string?} defaultWS
     * @return {string}
     */
    function w(defaultWS) {
        const wsI = ws[i++];
        // Check if whitespces have comments
        const hasComment = (wsI !== undefined) && wsI.trim().length > 0;
        if (hasComment || (!shouldIndent && wsI !== undefined)) {
            return wsI;
        }
        return defaultWS || '';
    }

    function a(afterWS) {
        if (shouldIndent) {
            return afterWS || '';
        }
        return '';
    }

    /* eslint-disable no-unused-vars */
    const b = a;

    function indent() {
        ++l;
        return '';
    }

    function outdent() {
        --l;
        if (shouldIndent) {
            if (node.documentationText) {
                const indent = _.last(node.documentationText.split('\n'));
                if (indent === _.repeat(tab, l)) {
                    // if documentation text already contains the correct dent
                    return '';
                }
            }
            return '\n' + _.repeat(tab, l);
        }
        return '';
    }

    function dent() {
        if (shouldIndent) {
            return '\n' + _.repeat(tab, l);
        }
        return '';
    }
    /* eslint-enable no-unused-vars */

    if (replaceLambda && node.kind === 'Lambda') {
        return '$ function LAMBDA $';
    }
    // if this is a primitive value, return value directly
    if (Object(node) !== node) {
        return node;
    }

    switch (node.kind) {
        case 'CompilationUnit':
            return join(node.topLevelNodes, pretty, replaceLambda, l, w) + w();
        case 'ArrayType':
            return getSourceOf(node.elementType, pretty, l, replaceLambda) +
                times(node.dimensions, () => w() + '[' + w() + ']');

        /* eslint-disable max-len */
        // auto gen start

        case 'PackageDeclaration':
            return dent() + w() + 'package' + a(' ')
                 + join(node.packageName, pretty, replaceLambda, l, w, '', '.') + w() + ';';
        case 'Import':
            if (node.userDefinedAlias && node.packageName
                         && node.alias.valueWithBar) {
                return dent() + w() + 'import' + a(' ')
                 + join(node.packageName, pretty, replaceLambda, l, w, '', '.') + w(' ') + 'as' + w(' ')
                 + node.alias.valueWithBar + w() + ';';
            } else {
                return dent() + w() + 'import' + a(' ')
                 + join(node.packageName, pretty, replaceLambda, l, w, '', '.') + w() + ';';
            }
        case 'Identifier':
            return w() + node.valueWithBar;
        case 'Abort':
            return dent() + w() + 'abort' + w() + ';';
        case 'Action':
            if (node.annotationAttachments && node.documentationAttachments
                         && node.deprecatedAttachments && node.name.valueWithBar
                         && node.parameters && node.returnParameters && node.returnParameters.length
                         && node.body && node.workers) {
                return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + dent() + w() + 'action' + w(' ')
                 + node.name.valueWithBar + w(' ') + '('
                 + join(node.parameters, pretty, replaceLambda, l, w, '', ',') + w() + ')' + w(' ') + '('
                 + join(node.returnParameters, pretty, replaceLambda, l, w, '', ',') + w() + ')'
                 + w(' ') + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + join(node.workers, pretty, replaceLambda, l, w, '')
                 + outdent() + w() + '}';
            } else {
                return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + dent() + w() + 'action' + w(' ')
                 + node.name.valueWithBar + w(' ') + '('
                 + join(node.parameters, pretty, replaceLambda, l, w, '', ',') + w() + ')' + w(' ') + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda)
                 + join(node.workers, pretty, replaceLambda, l, w, '') + outdent() + w() + '}';
            }
        case 'Annotation':
            return dent()
                 + join(node.annotationAttachments, pretty, replaceLambda, l, w, '') + w() + 'annotation' + w(' ') + '<'
                 + join(node.attachmentPoints, pretty, replaceLambda, l, w, '', ',') + w() + '>'
                 + w(' ') + node.name.valueWithBar + a(' ') + b(' ')
                 + getSourceOf(node.typeNode, pretty, l, replaceLambda) + w() + ';';
        case 'AnnotationAttachment':
            if (node.builtin && node.annotationName.valueWithBar
                         && node.expression) {
                return w() + '@' + w() + node.annotationName.valueWithBar + b(' ')
                 + getSourceOf(node.expression, pretty, l, replaceLambda);
            } else if (node.packageAlias.valueWithBar
                         && node.annotationName.valueWithBar && node.expression) {
                return w() + '@' + w() + node.packageAlias.valueWithBar + w() + ':'
                 + w() + node.annotationName.valueWithBar + b(' ')
                 + getSourceOf(node.expression, pretty, l, replaceLambda);
            } else {
                return w() + '@' + w() + node.annotationName.valueWithBar + b(' ')
                 + getSourceOf(node.expression, pretty, l, replaceLambda);
            }
        case 'ArrayLiteralExpr':
            return w() + '['
                 + join(node.expressions, pretty, replaceLambda, l, w, '', ',') + w() + ']';
        case 'Assignment':
            return dent() + (node.declaredWithVar ? w() + 'var' + a(' ') : '')
                 + join(node.variables, pretty, replaceLambda, l, w, '', ',') + w(' ')
                 + '=' + a(' ')
                 + getSourceOf(node.expression, pretty, l, replaceLambda) + w() + ';';
        case 'BinaryExpr':
            if (node.inTemplateLiteral && node.leftExpression
                         && node.operatorKind && node.rightExpression) {
                return w() + '{{'
                 + getSourceOf(node.leftExpression, pretty, l, replaceLambda) + w(' ') + node.operatorKind + a(' ')
                 + getSourceOf(node.rightExpression, pretty, l, replaceLambda) + w() + '}}';
            } else {
                return getSourceOf(node.leftExpression, pretty, l, replaceLambda)
                 + w(' ') + node.operatorKind + a(' ')
                 + getSourceOf(node.rightExpression, pretty, l, replaceLambda);
            }
        case 'Bind':
            return dent() + w() + 'bind' + b(' ')
                 + getSourceOf(node.expression, pretty, l, replaceLambda) + w(' ') + 'with' + b(' ')
                 + getSourceOf(node.variable, pretty, l, replaceLambda) + w() + ';';
        case 'Block':
            return join(node.statements, pretty, replaceLambda, l, w, '');
        case 'Break':
            return dent() + w() + 'break' + w() + ';';
        case 'BuiltInRefType':
            return w() + node.typeKind;
        case 'Catch':
            return dent() + w() + 'catch' + w() + '('
                 + getSourceOf(node.parameter, pretty, l, replaceLambda) + w() + ')' + w(' ') + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + outdent()
                 + w() + '}';
        case 'Comment':
            return dent() + w() + node.comment;
        case 'Connector':
            if (node.annotationAttachments && node.documentationAttachments
                         && node.deprecatedAttachments && node.name.valueWithBar
                         && node.parameters && node.variableDefs && node.actions) {
                return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + dent() + (node.public ? w() + 'public' + a(' ') : '')
                 + w() + 'connector' + w(' ') + node.name.valueWithBar + w(' ')
                 + '('
                 + join(node.parameters, pretty, replaceLambda, l, w, '', ',') + w() + ')' + w(' ') + '{' + indent()
                 + join(node.variableDefs, pretty, replaceLambda, l, w, '')
                 + join(node.actions, pretty, replaceLambda, l, w, '') + outdent() + w() + '}';
            } else {
                return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + dent() + (node.public ? w() + 'public' + a(' ') : '')
                 + w() + 'connector' + w(' ') + node.name.valueWithBar + w(' ')
                 + '('
                 + join(node.parameters, pretty, replaceLambda, l, w, '', ',') + w() + ')' + w(' ') + '{' + indent()
                 + join(node.actions, pretty, replaceLambda, l, w, '') + outdent() + w() + '}';
            }
        case 'ConnectorInitExpr':
            if (node.connectorType && node.expressions) {
                return dent() + w() + 'create' + b(' ')
                 + getSourceOf(node.connectorType, pretty, l, replaceLambda) + w() + '('
                 + join(node.expressions, pretty, replaceLambda, l, w, '', ',') + w() + ')';
            } else {
                return dent() + w() + 'create' + b(' ')
                 + getSourceOf(node.connectorType, pretty, l, replaceLambda) + w() + '(' + w() + ')';
            }
        case 'ConstrainedType':
            return getSourceOf(node.type, pretty, l, replaceLambda) + w() + '<'
                 + getSourceOf(node.constraint, pretty, l, replaceLambda) + w() + '>';
        case 'Documentation':
            return dent() + w() + 'documentation' + a(' ') + w() + '{' + indent()
                 + w() + node.documentationText + outdent() + w() + '}';
        case 'Deprecated':
            return dent() + w() + 'deprecated' + a(' ') + w() + '{' + indent() + w()
                 + node.documentationText + outdent() + w() + '}';
        case 'Endpoint':
            return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + w() + 'endpoint' + w() + '<'
                 + getSourceOf(node.endPointType, pretty, l, replaceLambda) + w() + '>' + w()
                 + node.name.valueWithBar
                 + getSourceOf(node.configurationExpression, pretty, l, replaceLambda);
        case 'EndpointType':
            return w() + '<'
                 + getSourceOf(node.constraint, pretty, l, replaceLambda) + w() + '>';
        case 'ExpressionStatement':
            return dent() + getSourceOf(node.expression, pretty, l, replaceLambda)
                 + w() + ';';
        case 'Enum':
            return dent() + w() + 'enum\u0020'
                 + getSourceOf(node.name, pretty, l, replaceLambda) + w() + '{' + indent()
                 + join(node.enumerators, pretty, replaceLambda, l, w, '', ',') + outdent() + w() + '}';
        case 'Enumerator':
            return getSourceOf(node.name, pretty, l, replaceLambda);
        case 'FieldBasedAccessExpr':
            return getSourceOf(node.expression, pretty, l, replaceLambda) + w()
                 + '.' + w() + node.fieldName.valueWithBar;
        case 'Foreach':
            return dent() + w() + 'foreach' + a(' ')
                 + join(node.variables, pretty, replaceLambda, l, w, ' ', ',') + w(' ') + 'in' + b(' ')
                 + getSourceOf(node.collection, pretty, l, replaceLambda) + w(' ') + '{'
                 + indent() + getSourceOf(node.body, pretty, l, replaceLambda)
                 + outdent() + w() + '}';
        case 'ForkJoin':
            if (node.workers && node.joinType && node.joinCount >= 0
                         && node.joinedWorkerIdentifiers && node.joinResultVar && node.joinBody
                         && node.timeOutExpression && node.timeOutVariable && node.timeoutBody) {
                return dent() + dent() + dent() + w() + 'fork' + w(' ') + '{' + indent()
                 + join(node.workers, pretty, replaceLambda, l, w, '')
                 + outdent() + w() + '}' + w() + 'join' + w() + '(' + w() + node.joinType
                 + w(' ') + node.joinCount
                 + join(node.joinedWorkerIdentifiers, pretty, replaceLambda, l, w, ' ', ',') + w() + ')' + w() + '('
                 + getSourceOf(node.joinResultVar, pretty, l, replaceLambda) + w() + ')'
                 + w(' ') + '{' + indent()
                 + getSourceOf(node.joinBody, pretty, l, replaceLambda) + outdent() + w() + '}' + w() + 'timeout' + w()
                 + '('
                 + getSourceOf(node.timeOutExpression, pretty, l, replaceLambda) + w() + ')' + w() + '('
                 + getSourceOf(node.timeOutVariable, pretty, l, replaceLambda) + w() + ')' + w(' ') + '{' + indent()
                 + getSourceOf(node.timeoutBody, pretty, l, replaceLambda)
                 + outdent() + w() + '}';
            } else if (node.workers && node.joinType && node.joinCount >= 0
                         && node.joinedWorkerIdentifiers && node.joinResultVar && node.joinBody) {
                return dent() + dent() + w() + 'fork' + w(' ') + '{' + indent()
                 + join(node.workers, pretty, replaceLambda, l, w, '') + outdent() + w()
                 + '}' + w() + 'join' + w() + '(' + w() + node.joinType + w(' ')
                 + node.joinCount
                 + join(node.joinedWorkerIdentifiers, pretty, replaceLambda, l, w, ' ', ',') + w() + ')' + w() + '('
                 + getSourceOf(node.joinResultVar, pretty, l, replaceLambda) + w() + ')' + w(' ')
                 + '{' + indent()
                 + getSourceOf(node.joinBody, pretty, l, replaceLambda) + outdent() + w() + '}';
            } else if (node.workers && node.joinType && node.joinCount >= 0
                         && node.joinedWorkerIdentifiers && node.joinResultVar) {
                return dent() + dent() + w() + 'fork' + w(' ') + '{' + indent()
                 + join(node.workers, pretty, replaceLambda, l, w, '') + outdent() + w()
                 + '}' + w() + 'join' + w() + '(' + w() + node.joinType + w(' ')
                 + node.joinCount
                 + join(node.joinedWorkerIdentifiers, pretty, replaceLambda, l, w, ' ', ',') + w() + ')' + w() + '('
                 + getSourceOf(node.joinResultVar, pretty, l, replaceLambda) + w() + ')' + w(' ')
                 + '{' + indent() + outdent() + w() + '}';
            } else if (node.workers && node.joinType && node.joinedWorkerIdentifiers
                         && node.joinResultVar && node.joinBody && node.timeOutExpression
                         && node.timeOutVariable && node.timeoutBody) {
                return dent() + dent() + dent() + w() + 'fork' + w(' ') + '{' + indent()
                 + join(node.workers, pretty, replaceLambda, l, w, '')
                 + outdent() + w() + '}' + w() + 'join' + w() + '(' + w() + node.joinType
                 + join(node.joinedWorkerIdentifiers, pretty, replaceLambda, l, w, ' ', ',') + w() + ')' + w() + '('
                 + getSourceOf(node.joinResultVar, pretty, l, replaceLambda) + w() + ')' + w(' ') + '{' + indent()
                 + getSourceOf(node.joinBody, pretty, l, replaceLambda)
                 + outdent() + w() + '}' + w() + 'timeout' + w() + '('
                 + getSourceOf(node.timeOutExpression, pretty, l, replaceLambda) + w() + ')' + w()
                 + '(' + getSourceOf(node.timeOutVariable, pretty, l, replaceLambda)
                 + w() + ')' + w(' ') + '{' + indent()
                 + getSourceOf(node.timeoutBody, pretty, l, replaceLambda) + outdent() + w() + '}';
            } else if (node.workers && node.joinType && node.joinedWorkerIdentifiers
                         && node.joinResultVar && node.joinBody) {
                return dent() + dent() + w() + 'fork' + w(' ') + '{' + indent()
                 + join(node.workers, pretty, replaceLambda, l, w, '') + outdent() + w()
                 + '}' + w() + 'join' + w() + '(' + w() + node.joinType
                 + join(node.joinedWorkerIdentifiers, pretty, replaceLambda, l, w, ' ', ',')
                 + w() + ')' + w() + '('
                 + getSourceOf(node.joinResultVar, pretty, l, replaceLambda) + w() + ')' + w(' ') + '{' + indent()
                 + getSourceOf(node.joinBody, pretty, l, replaceLambda) + outdent() + w()
                 + '}';
            } else {
                return dent() + dent() + w() + 'fork' + w(' ') + '{' + indent()
                 + join(node.workers, pretty, replaceLambda, l, w, '') + outdent() + w()
                 + '}' + w() + 'join' + w() + '(' + w() + node.joinType
                 + join(node.joinedWorkerIdentifiers, pretty, replaceLambda, l, w, ' ', ',')
                 + w() + ')' + w() + '('
                 + getSourceOf(node.joinResultVar, pretty, l, replaceLambda) + w() + ')' + w(' ') + '{' + indent()
                 + outdent() + w() + '}';
            }
        case 'Function':
            if (node.lambda && node.annotationAttachments
                         && node.documentationAttachments && node.deprecatedAttachments && node.parameters
                         && node.returnParameters && node.returnParameters.length && node.body
                         && node.workers) {
                return dent()
                 + join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + w() + 'function' + w() + '('
                 + join(node.parameters, pretty, replaceLambda, l, w, '', ',') + w() + ')'
                 + a(' ') + w() + '('
                 + join(node.returnParameters, pretty, replaceLambda, l, w, '', ',') + w() + ')' + a(' ') + w() + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda)
                 + join(node.workers, pretty, replaceLambda, l, w, '') + outdent() + w() + '}';
            } else if (node.lambda && node.annotationAttachments
                         && node.documentationAttachments && node.deprecatedAttachments && node.parameters
                         && node.body && node.workers) {
                return dent()
                 + join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + w() + 'function' + w() + '('
                 + join(node.parameters, pretty, replaceLambda, l, w, '', ',') + w() + ')'
                 + a(' ') + w() + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + join(node.workers, pretty, replaceLambda, l, w, '')
                 + outdent() + w() + '}';
            } else if (node.annotationAttachments && node.documentationAttachments
                         && node.deprecatedAttachments && node.receiver
                         && node.name.valueWithBar && node.parameters && node.returnParameters
                         && node.returnParameters.length && node.body && node.workers) {
                return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + dent() + (node.public ? w() + 'public' + a(' ') : '')
                 + w() + 'function' + w() + '<'
                 + getSourceOf(node.receiver, pretty, l, replaceLambda) + w() + '>' + w(' ')
                 + node.name.valueWithBar + w() + '('
                 + join(node.parameters, pretty, replaceLambda, l, w, '', ',') + w() + ')' + a(' ') + w() + '('
                 + join(node.returnParameters, pretty, replaceLambda, l, w, '', ',') + w() + ')'
                 + a(' ') + w() + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda)
                 + join(node.workers, pretty, replaceLambda, l, w, '') + outdent() + w() + '}';
            } else if (node.annotationAttachments && node.documentationAttachments
                         && node.deprecatedAttachments && node.name.valueWithBar
                         && node.parameters && node.returnParameters && node.returnParameters.length
                         && node.body && node.workers) {
                return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + dent() + (node.public ? w() + 'public' + a(' ') : '')
                 + w() + 'function' + w(' ') + node.name.valueWithBar + w() + '('
                 + join(node.parameters, pretty, replaceLambda, l, w, '', ',')
                 + w() + ')' + a(' ') + w() + '('
                 + join(node.returnParameters, pretty, replaceLambda, l, w, '', ',') + w() + ')' + a(' ') + w()
                 + '{' + indent() + getSourceOf(node.body, pretty, l, replaceLambda)
                 + join(node.workers, pretty, replaceLambda, l, w, '') + outdent()
                 + w() + '}';
            } else if (node.annotationAttachments && node.documentationAttachments
                         && node.deprecatedAttachments && node.receiver
                         && node.name.valueWithBar && node.parameters && node.body && node.workers) {
                return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + dent() + (node.public ? w() + 'public' + a(' ') : '')
                 + w() + 'function' + w() + '<'
                 + getSourceOf(node.receiver, pretty, l, replaceLambda) + w() + '>' + w(' ')
                 + node.name.valueWithBar + w() + '('
                 + join(node.parameters, pretty, replaceLambda, l, w, '', ',') + w() + ')' + a(' ') + w() + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda)
                 + join(node.workers, pretty, replaceLambda, l, w, '') + outdent() + w() + '}';
            } else {
                return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + dent() + (node.public ? w() + 'public' + a(' ') : '')
                 + w() + 'function' + w(' ') + node.name.valueWithBar + w() + '('
                 + join(node.parameters, pretty, replaceLambda, l, w, '', ',')
                 + w() + ')' + a(' ') + w() + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda)
                 + join(node.workers, pretty, replaceLambda, l, w, '') + outdent() + w() + '}';
            }
        case 'FunctionType':
            if (node.paramTypeNode && node.returnParamTypeNode
                         && node.returnParamTypeNode.length) {
                return w() + 'function' + w() + '('
                 + join(node.paramTypeNode, pretty, replaceLambda, l, w, '', ',') + w() + ')'
                 + (node.returnKeywordExists ? w() + 'returns' : '') + w() + '('
                 + join(node.returnParamTypeNode, pretty, replaceLambda, l, w, '') + w() + ')';
            } else {
                return w() + 'function' + w() + '('
                 + join(node.paramTypeNode, pretty, replaceLambda, l, w, '', ',') + w() + ')';
            }
        case 'If':
            if (node.ladderParent && node.condition && node.body
                         && node.elseStatement) {
                return (node.parent.kind === 'If' ? '' : dent()) + w() + 'if' + w(' ')
                 + '(' + getSourceOf(node.condition, pretty, l, replaceLambda)
                 + w() + ')' + a(' ') + w() + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + outdent() + w() + '}' + w(' ')
                 + 'else' + a(' ')
                 + getSourceOf(node.elseStatement, pretty, l, replaceLambda);
            } else if (node.condition && node.body && node.elseStatement) {
                return (node.parent.kind === 'If' ? '' : dent())
                 + (node.parent.kind === 'If' ? '' : dent()) + w() + 'if' + w(' ') + '('
                 + getSourceOf(node.condition, pretty, l, replaceLambda) + w() + ')' + a(' ')
                 + w() + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + outdent() + w() + '}' + w(' ') + 'else' + a(' ') + w()
                 + '{' + indent()
                 + getSourceOf(node.elseStatement, pretty, l, replaceLambda) + outdent() + w() + '}';
            } else {
                return (node.parent.kind === 'If' ? '' : dent()) + w() + 'if' + w(' ')
                 + '(' + getSourceOf(node.condition, pretty, l, replaceLambda)
                 + w() + ')' + a(' ') + w() + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + outdent() + w() + '}';
            }
        case 'IndexBasedAccessExpr':
            return getSourceOf(node.expression, pretty, l, replaceLambda) + w()
                 + '[' + getSourceOf(node.index, pretty, l, replaceLambda) + w()
                 + ']';
        case 'Invocation':
            if (node.actionInvocation && node.expression
                         && node.name.valueWithBar && node.argumentExpressions) {
                return getSourceOf(node.expression, pretty, l, replaceLambda) + w()
                 + '->' + w() + node.name.valueWithBar + w() + '('
                 + join(node.argumentExpressions, pretty, replaceLambda, l, w, '', ',') + w() + ')';
            } else if (node.expression && node.name.valueWithBar
                         && node.argumentExpressions) {
                return getSourceOf(node.expression, pretty, l, replaceLambda) + w()
                 + '.' + w() + node.name.valueWithBar + w() + '('
                 + join(node.argumentExpressions, pretty, replaceLambda, l, w, '', ',') + w() + ')';
            } else if (node.packageAlias.valueWithBar && node.name.valueWithBar
                         && node.argumentExpressions) {
                return w() + node.packageAlias.valueWithBar + w() + ':' + w()
                 + node.name.valueWithBar + w() + '('
                 + join(node.argumentExpressions, pretty, replaceLambda, l, w, '', ',') + w() + ')';
            } else {
                return w() + node.name.valueWithBar + w() + '('
                 + join(node.argumentExpressions, pretty, replaceLambda, l, w, '', ',') + w() + ')';
            }
        case 'Lambda':
            return getSourceOf(node.functionNode, pretty, l, replaceLambda);
        case 'Literal':
            if (node.inTemplateLiteral && node.unescapedValue) {
                return w() + node.unescapedValue;
            } else if (node.inTemplateLiteral) {
                return '';
            } else {
                return w() + node.value;
            }
        case 'Next':
            return dent() + w() + 'next' + w() + ';';
        case 'RecordLiteralExpr':
            if (node.keyValuePairs) {
                return w() + '{'
                 + join(node.keyValuePairs, pretty, replaceLambda, l, w, '', ',') + w() + '}';
            } else {
                return w() + '{' + w() + '}';
            }
        case 'RecordLiteralKeyValue':
            return getSourceOf(node.key, pretty, l, replaceLambda) + w() + ':'
                 + getSourceOf(node.value, pretty, l, replaceLambda);
        case 'Resource':
            return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + dent() + w() + 'resource' + w(' ')
                 + node.name.valueWithBar + w(' ') + '('
                 + join(node.parameters, pretty, replaceLambda, l, w, '', ',') + w() + ')' + w(' ') + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda)
                 + join(node.workers, pretty, replaceLambda, l, w, '') + outdent() + w() + '}';
        case 'Return':
            return dent() + w() + 'return' + a(' ')
                 + join(node.expressions, pretty, replaceLambda, l, w, ' ', ',') + w() + ';';
        case 'Service':
            return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + dent() + w() + 'service' + w() + '<'
                 + getSourceOf(node.endpointType, pretty, l, replaceLambda) + w() + '>' + w(' ')
                 + node.name.valueWithBar + w(' ') + '{' + indent()
                 + join(node.variables, pretty, replaceLambda, l, w, '')
                 + join(node.resources, pretty, replaceLambda, l, w, '') + outdent() + w() + '}';
        case 'SimpleVariableRef':
            if (node.inTemplateLiteral && node.packageAlias.valueWithBar
                         && node.variableName.valueWithBar) {
                return w() + '{{' + w() + node.packageAlias.valueWithBar + w() + ':'
                 + w() + node.variableName.valueWithBar + w() + '}}';
            } else if (node.inTemplateLiteral && node.variableName.valueWithBar) {
                return w() + '{{' + w() + node.variableName.valueWithBar + w() + '}}';
            } else if (node.packageAlias.valueWithBar && node.variableName.valueWithBar) {
                return w() + node.packageAlias.valueWithBar + w() + ':' + w()
                 + node.variableName.valueWithBar;
            } else {
                return w() + node.variableName.valueWithBar;
            }
        case 'StringTemplateLiteral':
            return w() + 'string\u0020`'
                 + join(node.expressions, pretty, replaceLambda, l, w, '') + w() + '`';
        case 'Struct':
            if (node.anonStruct && node.fields) {
                return dent() + (node.public ? w() + 'public' + a(' ') : '') + w()
                 + 'struct' + w(' ') + '{' + indent()
                 + join(node.fields, pretty, replaceLambda, l, w, '', ';', true) + outdent() + w() + '}';
            } else {
                return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + dent() + (node.public ? w() + 'public' + a(' ') : '')
                 + w() + 'struct' + w(' ') + node.name.valueWithBar + w(' ')
                 + '{' + indent()
                 + join(node.fields, pretty, replaceLambda, l, w, '', ';', true) + outdent() + w() + '}';
            }
        case 'TernaryExpr':
            return getSourceOf(node.condition, pretty, l, replaceLambda) + w() + '?'
                 + getSourceOf(node.thenExpression, pretty, l, replaceLambda)
                 + w() + ':'
                 + getSourceOf(node.elseExpression, pretty, l, replaceLambda);
        case 'Throw':
            return dent() + w() + 'throw' + b(' ')
                 + getSourceOf(node.expressions, pretty, l, replaceLambda) + w() + ';';
        case 'Transaction':
            if (node.condition && node.transactionBody && node.failedBody) {
                return dent() + dent() + w() + 'transaction' + w(' ') + 'with' + w(' ')
                 + 'retries' + w(' ') + '('
                 + getSourceOf(node.condition, pretty, l, replaceLambda) + w() + ')' + w(' ') + '{' + indent()
                 + getSourceOf(node.transactionBody, pretty, l, replaceLambda) + outdent()
                 + w() + '}' + w(' ') + 'failed' + w(' ') + '{' + indent()
                 + getSourceOf(node.failedBody, pretty, l, replaceLambda) + outdent()
                 + w() + '}';
            } else if (node.transactionBody && node.failedBody) {
                return dent() + dent() + w() + 'transaction' + w(' ') + '{' + indent()
                 + getSourceOf(node.transactionBody, pretty, l, replaceLambda)
                 + outdent() + w() + '}' + w(' ') + 'failed' + w(' ') + '{' + indent()
                 + getSourceOf(node.failedBody, pretty, l, replaceLambda)
                 + outdent() + w() + '}';
            } else if (node.condition && node.transactionBody) {
                return dent() + w() + 'transaction' + w(' ') + 'with' + w(' ')
                 + 'retries' + w(' ') + '('
                 + getSourceOf(node.condition, pretty, l, replaceLambda) + w() + ')' + w(' ') + '{' + indent()
                 + getSourceOf(node.transactionBody, pretty, l, replaceLambda) + outdent() + w()
                 + '}';
            } else {
                return dent() + w() + 'transaction' + w(' ') + '{' + indent()
                 + getSourceOf(node.transactionBody, pretty, l, replaceLambda) + outdent()
                 + w() + '}';
            }
        case 'Transform':
            return dent() + w() + 'transform' + w(' ') + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + outdent() + w() + '}';
        case 'Transformer':
            if (node.source && node.returnParameters
                         && node.returnParameters.length && node.name.valueWithBar && node.parameters && node.body) {
                return dent() + (node.public ? w() + 'public' + a(' ') : '') + w()
                 + 'transformer' + w() + '<'
                 + getSourceOf(node.source, pretty, l, replaceLambda) + w() + ','
                 + join(node.returnParameters, pretty, replaceLambda, l, w, '', ',') + w() + '>' + w()
                 + node.name.valueWithBar + w() + '('
                 + join(node.parameters, pretty, replaceLambda, l, w, '', ',') + w() + ')' + w() + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + outdent() + w() + '}';
            } else if (node.source && node.returnParameters
                         && node.returnParameters.length && node.name.valueWithBar && node.body) {
                return dent() + (node.public ? w() + 'public' + a(' ') : '') + w()
                 + 'transformer' + w() + '<'
                 + getSourceOf(node.source, pretty, l, replaceLambda) + w() + ','
                 + join(node.returnParameters, pretty, replaceLambda, l, w, '', ',') + w() + '>' + w()
                 + node.name.valueWithBar + w() + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + outdent() + w() + '}';
            } else {
                return dent() + (node.public ? w() + 'public' + a(' ') : '') + w()
                 + 'transformer' + w() + '<'
                 + getSourceOf(node.source, pretty, l, replaceLambda) + w() + ','
                 + join(node.returnParameters, pretty, replaceLambda, l, w, '', ',') + w() + '>' + w() + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + outdent() + w()
                 + '}';
            }
        case 'Try':
            if (node.body && node.catchBlocks && node.finallyBody) {
                return dent() + dent() + w() + 'try' + w(' ') + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + outdent() + w() + '}'
                 + join(node.catchBlocks, pretty, replaceLambda, l, w, '') + w()
                 + 'finally' + w(' ') + '{' + indent()
                 + getSourceOf(node.finallyBody, pretty, l, replaceLambda) + outdent() + w() + '}';
            } else {
                return dent() + w() + 'try' + w(' ') + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + outdent() + w() + '}'
                 + join(node.catchBlocks, pretty, replaceLambda, l, w, '');
            }
        case 'TypeCastExpr':
            return w() + '(' + getSourceOf(node.typeNode, pretty, l, replaceLambda)
                 + w() + ')'
                 + getSourceOf(node.expression, pretty, l, replaceLambda);
        case 'TypeConversionExpr':
            if (node.typeNode && node.transformerInvocation && node.expression) {
                return w() + '<' + getSourceOf(node.typeNode, pretty, l, replaceLambda)
                 + w() + ','
                 + getSourceOf(node.transformerInvocation, pretty, l, replaceLambda) + w() + '>'
                 + getSourceOf(node.expression, pretty, l, replaceLambda);
            } else {
                return w() + '<' + getSourceOf(node.typeNode, pretty, l, replaceLambda)
                 + w() + '>'
                 + getSourceOf(node.expression, pretty, l, replaceLambda);
            }
        case 'TypeofExpression':
            return w() + 'typeof' + b(' ')
                 + getSourceOf(node.typeNode, pretty, l, replaceLambda);
        case 'UnaryExpr':
            return w() + node.operatorKind + b(' ')
                 + getSourceOf(node.expression, pretty, l, replaceLambda);
        case 'UserDefinedType':
            if (node.anonStruct) {
                return getSourceOf(node.anonStruct, pretty, l, replaceLambda);
            } else if (node.packageAlias.valueWithBar && node.typeName.valueWithBar) {
                return w() + node.packageAlias.valueWithBar + w() + ':' + w()
                 + node.typeName.valueWithBar;
            } else {
                return w() + node.typeName.valueWithBar;
            }
        case 'ValueType':
            return w() + node.typeKind;
        case 'Variable':
            if (node.endpoint && node.typeNode && node.name.valueWithBar
                         && node.initialExpression) {
                return dent() + dent() + w() + 'endpoint'
                 + getSourceOf(node.typeNode, pretty, l, replaceLambda) + w(' ') + node.name.valueWithBar + w()
                 + '{' + indent()
                 + getSourceOf(node.initialExpression, pretty, l, replaceLambda) + w() + ';' + outdent() + w() + '}';
            } else if (node.endpoint && node.typeNode && node.name.valueWithBar) {
                return dent() + w() + 'endpoint'
                 + getSourceOf(node.typeNode, pretty, l, replaceLambda) + w(' ') + node.name.valueWithBar + w() + '{'
                 + indent() + outdent() + w() + '}';
            } else if (node.global && node.annotationAttachments
                         && node.documentationAttachments && node.deprecatedAttachments && node.typeNode
                         && node.name.valueWithBar && node.initialExpression) {
                return dent()
                 + join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + (node.public ? w() + 'public' + a(' ') : '')
                 + (node.const ? w() + 'const' + a(' ') : '')
                 + getSourceOf(node.typeNode, pretty, l, replaceLambda) + w(' ')
                 + node.name.valueWithBar + w(' ') + '=' + a(' ')
                 + getSourceOf(node.initialExpression, pretty, l, replaceLambda) + w() + ';';
            } else if (node.global && node.annotationAttachments
                         && node.documentationAttachments && node.deprecatedAttachments && node.typeNode
                         && node.name.valueWithBar) {
                return dent()
                 + join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '')
                 + getSourceOf(node.typeNode, pretty, l, replaceLambda) + w(' ') + node.name.valueWithBar + w() + ';';
            } else if (node.typeNode && node.name.valueWithBar
                         && node.initialExpression) {
                return getSourceOf(node.typeNode, pretty, l, replaceLambda) + w(' ')
                 + node.name.valueWithBar + w(' ') + '=' + a(' ')
                 + getSourceOf(node.initialExpression, pretty, l, replaceLambda);
            } else if (node.annotationAttachments && node.documentationAttachments
                         && node.deprecatedAttachments && node.typeNode
                         && node.name.valueWithBar) {
                return join(node.annotationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.documentationAttachments, pretty, replaceLambda, l, w, '')
                 + join(node.deprecatedAttachments, pretty, replaceLambda, l, w, '') + getSourceOf(node.typeNode, pretty, l, replaceLambda)
                 + w(' ') + node.name.valueWithBar;
            } else {
                return getSourceOf(node.typeNode, pretty, l, replaceLambda);
            }
        case 'VariableDef':
            if (node.endpoint && node.variable) {
                return getSourceOf(node.variable, pretty, l, replaceLambda);
            } else {
                return dent() + getSourceOf(node.variable, pretty, l, replaceLambda)
                 + w() + ';';
            }
        case 'While':
            return dent() + w() + 'while' + w(' ') + '('
                 + getSourceOf(node.condition, pretty, l, replaceLambda) + w() + ')' + w(' ') + '{'
                 + indent() + getSourceOf(node.body, pretty, l, replaceLambda) + outdent()
                 + w() + '}';
        case 'Worker':
            return dent() + w() + 'worker' + w(' ') + node.name.valueWithBar
                 + w(' ') + '{' + indent()
                 + getSourceOf(node.body, pretty, l, replaceLambda) + outdent() + w() + '}';
        case 'WorkerReceive':
            return dent()
                 + join(node.expressions, pretty, replaceLambda, l, w, '', ',') + w() + '<-' + w() + node.workerName.valueWithBar + w()
                 + ';';
        case 'WorkerSend':
            if (node.forkJoinedSend && node.expressions) {
                return dent()
                 + join(node.expressions, pretty, replaceLambda, l, w, '', ',') + w() + '->' + w() + 'fork' + w() + ';';
            } else {
                return dent()
                 + join(node.expressions, pretty, replaceLambda, l, w, '', ',') + w() + '->' + w() + node.workerName.valueWithBar + w()
                 + ';';
            }
        case 'XmlAttribute':
            return getSourceOf(node.name, pretty, l, replaceLambda) + w() + '='
                 + getSourceOf(node.value, pretty, l, replaceLambda);
        case 'XmlAttributeAccessExpr':
            if (node.expression && node.index) {
                return getSourceOf(node.expression, pretty, l, replaceLambda) + w()
                 + '@' + w() + '[' + getSourceOf(node.index, pretty, l, replaceLambda)
                 + w() + ']';
            } else {
                return getSourceOf(node.expression, pretty, l, replaceLambda) + w()
                 + '@';
            }
        case 'XmlCommentLiteral':
            if (node.root && node.textFragments) {
                return w() + 'xml`' + w() + '<!--'
                 + join(node.textFragments, pretty, replaceLambda, l, w, '') + w() + '-->' + w() + '`';
            } else {
                return w() + '<!--'
                 + join(node.textFragments, pretty, replaceLambda, l, w, '') + w() + '-->';
            }
        case 'XmlElementLiteral':
            if (node.root && node.startTagName && node.attributes && node.content
                         && node.endTagName) {
                return w() + 'xml`' + w() + '<'
                 + getSourceOf(node.startTagName, pretty, l, replaceLambda)
                 + join(node.attributes, pretty, replaceLambda, l, w, '') + w() + '>'
                 + join(node.content, pretty, replaceLambda, l, w, '') + w() + '</'
                 + getSourceOf(node.endTagName, pretty, l, replaceLambda) + w() + '>' + w() + '`';
            } else if (node.startTagName && node.attributes && node.content
                         && node.endTagName) {
                return w() + '<'
                 + getSourceOf(node.startTagName, pretty, l, replaceLambda) + join(node.attributes, pretty, replaceLambda, l, w, '')
                 + w() + '>' + join(node.content, pretty, replaceLambda, l, w, '')
                 + w() + '</'
                 + getSourceOf(node.endTagName, pretty, l, replaceLambda) + w() + '>';
            } else if (node.root && node.startTagName && node.attributes) {
                return w() + 'xml`' + w() + '<'
                 + getSourceOf(node.startTagName, pretty, l, replaceLambda)
                 + join(node.attributes, pretty, replaceLambda, l, w, '') + w() + '/>`';
            } else {
                return w() + '<'
                 + getSourceOf(node.startTagName, pretty, l, replaceLambda) + join(node.attributes, pretty, replaceLambda, l, w, '')
                 + w() + '/>';
            }
        case 'XmlPiLiteral':
            if (node.target && node.dataTextFragments) {
                return getSourceOf(node.target, pretty, l, replaceLambda)
                 + join(node.dataTextFragments, pretty, replaceLambda, l, w, '');
            } else if (node.dataTextFragments) {
                return join(node.dataTextFragments, pretty, replaceLambda, l, w, '');
            } else {
                return getSourceOf(node.target, pretty, l, replaceLambda);
            }
        case 'XmlQname':
            if (node.prefix.valueWithBar && node.localname.valueWithBar) {
                return w() + node.prefix.valueWithBar + w() + ':' + w()
                 + node.localname.valueWithBar;
            } else {
                return w() + node.localname.valueWithBar;
            }
        case 'XmlQuotedString':
            return join(node.textFragments, pretty, replaceLambda, l, w, '');
        case 'XmlTextLiteral':
            return join(node.textFragments, pretty, replaceLambda, l, w, '');
        case 'Xmlns':
            if (node.namespaceURI && node.prefix.valueWithBar) {
                return dent() + w() + 'xmlns' + b(' ')
                 + getSourceOf(node.namespaceURI, pretty, l, replaceLambda) + w(' ') + 'as' + w(' ')
                 + node.prefix.valueWithBar + w() + ';';
            } else if (node.namespaceURI) {
                return dent() + w() + 'xmlns' + b(' ')
                 + getSourceOf(node.namespaceURI, pretty, l, replaceLambda) + w() + ';';
            } else {
                return getSourceOf(node.namespaceDeclaration, pretty, l, replaceLambda);
            }

        // auto gen end
        /* eslint-enable max-len */

        default:
            console.error('no source gen for' + node.kind);
            return '';

    }
}

/**
 * Joins sources of a array of nodes with given delimiters.
 *
 * @private
 * @param {Node[]} arr Nodes to be joined.
 * @param {boolean} pretty
 * @param {number} l indent level.
 * @param {function(number): string} wsFunc White space generator function.
 * @param defaultWS
 * @param {string} separator
 * @param {boolean} suffixLast
 * @param {boolean} replaceLambda
 * @return {string}
 */
join = function (arr, pretty, replaceLambda, l, wsFunc, defaultWS, separator, suffixLast = false) {
    let str = '';
    for (let i = 0; i < arr.length; i++) {
        const node = arr[i];
        if (node.kind === 'Identifier') {
            str += wsFunc(defaultWS);
        }
        str += getSourceOf(node, pretty, l, replaceLambda);
        if (separator && (suffixLast || i !== arr.length - 1)) {
            str += wsFunc(defaultWS) + separator;
        }
    }

    return str;
};

