/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ballerinalang.test.types.table;

import org.ballerinalang.launcher.util.BAssertUtil;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.exceptions.BLangRuntimeException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Class to test table literal syntax.
 */
public class TableLiteralSyntaxTest {

    private CompileResult result;
    private CompileResult resultNegative;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/types/table/table_literal_syntax.bal");
        resultNegative = BCompileUtil.compile("test-src/types/table/table_literal_syntax_negative.bal");
    }

    @Test
    public void testTableDefaultValueForLocalVariable() {
        BValue[] returns = BRunUtil.invoke(result, "testTableDefaultValueForLocalVariable");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 1);
    }

    @Test
    public void testTableDefaultValueForGlobalVariable() {
        BValue[] returns = BRunUtil.invoke(result, "testTableDefaultValueForGlobalVariable");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 1);
    }

    @Test
    public void testTableAddOnUnconstrainedTable() {
        BValue[] returns = BRunUtil.invoke(result, "testTableAddOnUnconstrainedTable");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 1);
    }

    @Test
    public void testTableAddOnConstrainedTable() {
        BValue[] returns = BRunUtil.invoke(result, "testTableAddOnConstrainedTable");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 2);
    }

    @Test
    public void testValidTableVariable() {
        BValue[] returns = BRunUtil.invoke(result, "testValidTableVariable");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 0);
    }

    @Test
    public void testTableLiteralData() {
        BValue[] returns = BRunUtil.invoke(result, "testTableLiteralData");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 3);
    }

    @Test
    public void testTableLiteralDataAndAdd() {
        BValue[] returns = BRunUtil.invoke(result, "testTableLiteralDataAndAdd");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 5);
    }

    @Test
    public void testTableLiteralDataAndAdd2() {
        BValue[] returns = BRunUtil.invoke(result, "testTableLiteralDataAndAdd2");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 5);
    }

    @Test(expectedExceptions = BLangRuntimeException.class,
          expectedExceptionsMessageRegExp = ".*Unique index or primary key violation:.*")
    public void testTableAddOnConstrainedTableWithViolation() {
        BRunUtil.invoke(result, "testTableAddOnConstrainedTableWithViolation");
    }

    @Test
    public void testTableAddOnConstrainedTableWithViolation2() {
        BValue[] returns = BRunUtil.invoke(result, "testTableAddOnConstrainedTableWithViolation2");
        Assert.assertTrue((returns[0]).stringValue().contains("Unique index or primary key violation:"));
    }

    @Test(description = "Test table remove with function pointer of invalid return type")
    public void testTableReturnNegativeCases() {
        Assert.assertEquals(resultNegative.getErrorCount(), 9);
        BAssertUtil.validateError(resultNegative, 0, "object type not allowed as the constraint", 21, 11);
        BAssertUtil.validateError(resultNegative, 1, "undefined column 'married2' for table of type 'Person'", 27, 24);
        BAssertUtil.validateError(resultNegative, 2, "undefined field 'married2' in record 'Person'", 29, 10);
        BAssertUtil.validateError(resultNegative, 3, "undefined field 'married2' in record 'Person'", 30, 9);
        BAssertUtil.validateError(resultNegative, 4, "undefined field 'married2' in record 'Person'", 31, 9);
        BAssertUtil.validateError(resultNegative, 5, "incompatible types: expected 'Person', found 'int'", 45, 10);
        BAssertUtil.validateError(resultNegative, 6, "incompatible types: expected 'Person', found 'int'", 45, 13);
        BAssertUtil.validateError(resultNegative, 7, "object type not allowed as the constraint", 57, 5);
        BAssertUtil.validateError(resultNegative, 8, "table cannot be created without a constraint", 69, 16);
    }
}
