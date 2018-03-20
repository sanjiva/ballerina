/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.test.types.uniontypes;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class contains union type related test cases.
 */
public class UnionTypeTest {
    private CompileResult result;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/types/uniontypes/union_types_basic.bal");
    }


    @Test(description = "Test basics of union types")
    public void testUnionTypeBasics1() {
        BValue[] returns = BRunUtil.invoke(result, "testUnionTypeBasics1", new BValue[]{});
        Assert.assertEquals(returns.length, 2);
        Assert.assertSame(returns[0].getClass(), BFloat.class);
        Assert.assertSame(returns[1].getClass(), BString.class);
        Assert.assertEquals(((BFloat) returns[0]).floatValue(), 12.0, "Invalid float value returned.");
        Assert.assertEquals(returns[1].stringValue(), "sameera", "Invalid string value returned.");
    }

    @Test(description = "Test basics of union types")
    public void testUnionTypeBasics2() {
        BValue[] returns = BRunUtil.invoke(result, "testUnionTypeBasics2", new BValue[]{});
        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BString.class);
        Assert.assertEquals(returns[0].stringValue(), "union types", "Invalid string value returned.");
    }

    @Test(description = "Test basics of union types")
    public void testNullableTypeBasics1() {
        BValue[] returns = BRunUtil.invoke(result, "testNullableTypeBasics1", new BValue[]{});
        Assert.assertEquals(returns.length, 1);
        Assert.assertNull(returns[0]);
    }

    @Test(description = "Test basics of union types")
    public void testNullableTypeBasics2() {
        BValue[] returns = BRunUtil.invoke(result, "testNullableTypeBasics2", new BValue[]{});
        Assert.assertEquals(returns.length, 1);
        Assert.assertNull(returns[0]);
    }
}
