/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
package org.ballerinalang.langserver.completions.util.positioning.resolvers;

import org.ballerinalang.langserver.TextDocumentServiceContext;
import org.ballerinalang.langserver.completions.TreeVisitor;
import org.ballerinalang.model.tree.Node;
import org.wso2.ballerinalang.compiler.util.diagnotic.DiagnosticPos;

/**
 * Cursor position resolver interface.
 */
public abstract class CursorPositionResolver {

    /**
     * Check whether the cursor is positioned before the given node start.
     * @param nodePosition          Position of the node
     * @param node                  Node
     * @param treeVisitor           {@link TreeVisitor} current tree visitor instance
     * @param completionContext     Completion operation context
     * @return {@link Boolean}      Whether the cursor is before the node start or not
     */
    public abstract boolean isCursorBeforeNode(DiagnosticPos nodePosition, Node node, TreeVisitor treeVisitor,
                                               TextDocumentServiceContext completionContext);

    /**
     * Convert the diagnostic position to a zero based positioning diagnostic position.
     * @param diagnosticPos - diagnostic position to be cloned
     * @return {@link DiagnosticPos} converted diagnostic position
     */
    DiagnosticPos  toZeroBasedPosition(DiagnosticPos diagnosticPos) {
        int startLine = diagnosticPos.getStartLine() - 1;
        int endLine = diagnosticPos.getEndLine() - 1;
        int startColumn = diagnosticPos.getStartColumn() - 1;
        int endColumn = diagnosticPos.getEndColumn() - 1;
        return new DiagnosticPos(diagnosticPos.getSource(), startLine, endLine, startColumn, endColumn);
    }
}
