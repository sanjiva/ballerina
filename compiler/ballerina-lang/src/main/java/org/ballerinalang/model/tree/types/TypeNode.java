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
package org.ballerinalang.model.tree.types;

import org.ballerinalang.model.tree.Node;

/**
 * {@code TypeNode} represents a type node in the Ballerina AST.
 *
 * @since 0.94
 */
public interface TypeNode extends Node {

    /**
     * Returns whether this {@code TypeNode} is a nullable type.
     * <p>
     * e.g. T?
     *
     * @return whether this {@code TypeNode} is a nullable type.
     */
    boolean isNullable();

    /**
     * Returns whether this {@code TypeNode} is a grouped type.
     * e.g. (T)
     *
     * @return whether this {@code TypeNode} is a grouped type.
     */
    boolean isGrouped();
}
