/*
*   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.ballerinalang.model.values;

import org.ballerinalang.model.ExceptionDef;
import org.ballerinalang.model.types.BType;
import org.ballerinalang.model.types.BTypes;

/**
 * {@link BException} represents a exception value in Ballerina.
 */
public class BException implements BRefType<ExceptionDef> {

    private ExceptionDef exceptionDef;

    public BException() {
        this.exceptionDef = new ExceptionDef();
    }

    public BException(String message) {
        this.exceptionDef = new ExceptionDef(message);
    }

    public BException(String message, String category) {
        this.exceptionDef = new ExceptionDef(message, category);
    }

    @Override
    public String stringValue() {
        return exceptionDef.toString();
    }

    @Override
    public BType getType() {
        return BTypes.typeException;
    }

    @Override
    public ExceptionDef value() {
        return exceptionDef;
    }
}
