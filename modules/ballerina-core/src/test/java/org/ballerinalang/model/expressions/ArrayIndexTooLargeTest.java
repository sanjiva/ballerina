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
package org.ballerinalang.model.expressions;

import org.ballerinalang.core.utils.BTestUtils;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.exceptions.BLangRuntimeException;
import org.ballerinalang.util.program.BLangFunctions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Array index too large test.
 *
 * @since 0.86
 */
public class ArrayIndexTooLargeTest {

    private ProgramFile programFile;

    @BeforeClass
    public void setup() {
        programFile = BTestUtils.getProgramFile("lang/expressions/array-index-too-large.bal");
    }

    @Test(description = "Test adding too large index to an array",
          expectedExceptions = {BLangRuntimeException.class },
          expectedExceptionsMessageRegExp = ".*index number too large: 2,147,483,648.*")
    public void addTooLargeIndex() {
        BValue[] args = {new BInteger(2147483648L), new BInteger(7)};
        BLangFunctions.invokeNew(programFile, "addTooLargeIndex", args);
    }

    @Test(description = "Test accessing too large index from an array",
          expectedExceptions = {BLangRuntimeException.class },
          expectedExceptionsMessageRegExp = ".*index number too large: 2,147,483,648.*")
    public void accessTooLargeIndex() {
        BValue[] args = {new BInteger(2147483648L)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "accessTooLargeIndex", args);
    }

    @Test(description = "Test adding minus index to an array",
          expectedExceptions = {BLangRuntimeException.class },
          expectedExceptionsMessageRegExp = ".*array index out of range: index: -4, size: 0.*")
    public void addMinusIndex() {
        BValue[] args = {new BInteger(-4), new BInteger(7)};
        BLangFunctions.invokeNew(programFile, "addMinusIndex", args);
    }

    @Test(description = "Test accessing minus index from an array",
          expectedExceptions = {BLangRuntimeException.class },
          expectedExceptionsMessageRegExp = ".*array index out of range: index: -4, size: 0.*")
    public void accessMinusIndex() {
        BValue[] args = {new BInteger(-4)};
        BValue[] returns = BLangFunctions.invokeNew(programFile, "accessMinusIndex", args);
    }
}
