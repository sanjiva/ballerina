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
package org.ballerinalang.model;

import org.ballerinalang.model.types.BType;

/**
 * {@code ExecutableMultiReturnExpr} interface makes an {@link org.ballerinalang.model.expressions.Expression}.
 * which returns multiple values executable.
 *
 * @since 0.8.0
 */
public interface ExecutableMultiReturnExpr {

    /**
     * Returns an arrays of argument types of this expression.
     *
     * @return an arrays of argument types
     */
    BType[] getTypes();

    /**
     * Sets an arrays of argument types.
     *
     * @param types arrays of argument types
     */
    void setTypes(BType[] types);

    int[] getOffsets();

    void setOffsets(int[] offsets);
}
