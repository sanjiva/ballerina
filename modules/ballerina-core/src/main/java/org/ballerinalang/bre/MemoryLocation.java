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
package org.ballerinalang.bre;

import org.ballerinalang.model.Node;
import org.ballerinalang.model.NodeLocation;
import org.ballerinalang.model.NodeVisitor;
import org.ballerinalang.model.WhiteSpaceDescriptor;

/**
 * {@code MemoryLocation} represents a location where a variable or constant is stored at runtime.
 *
 * @since 0.8.0
 */
public class MemoryLocation implements Node {

    @Override
    public void accept(NodeVisitor visitor) {
    }

    @Override
    public NodeLocation getNodeLocation() {
        return null;
    }

    @Override
    public WhiteSpaceDescriptor getWhiteSpaceDescriptor() {
        return null;
    }
}
