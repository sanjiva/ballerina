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

import org.ballerinalang.model.Whitespace;
import org.ballerinalang.util.diagnostic.Diagnostic.DiagnosticPosition;

import java.util.Set;

/**
 * {@code Node} is the base interface for all tree nodes in Ballerina abstract syntax tree.
 *
 * @since 0.94
 */
public interface Node {

    void addWS(Set<Whitespace> whitespaces);

    Set<Whitespace> getWS();

    /**
     * Returns the kind of this node.
     *
     * @return the kind of this node.
     */
    NodeKind getKind();

    /**
     * Returns position of this node in a source file.
     *
     * @return the position of this node.
     */
    DiagnosticPosition getPosition();
}
