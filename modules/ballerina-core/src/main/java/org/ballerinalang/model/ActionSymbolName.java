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

/**
 * {@code ActionSymbolName} represents a package qualified
 * name of a {@link org.ballerinalang.model.symbols.BLangSymbol} in Ballerina package.
 *
 * @since 0.89
 */
public class ActionSymbolName extends CallableUnitSymbolName {

    public ActionSymbolName(String name, String pkgPath, int noOfParameters) {
        super(name, pkgPath, noOfParameters);
    }

    public ActionSymbolName(String name, int noOfParameters) {
        super(name, noOfParameters);
    }
}
