    /*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.test.expressions.unaryoperations;

    import org.ballerinalang.launcher.util.BCompileUtil;
    import org.ballerinalang.launcher.util.BRunUtil;
    import org.ballerinalang.launcher.util.CompileResult;
    import org.ballerinalang.model.values.BInteger;
    import org.ballerinalang.model.values.BTypeValue;
    import org.ballerinalang.model.values.BValue;
    import org.testng.Assert;
    import org.testng.annotations.BeforeClass;
    import org.testng.annotations.Test;

public class TypeOfOperatorTest {

    CompileResult result;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile(this, "test-src", "expressions/unaryoperations/typeof-operation.bal");
    }

    @Test(description = "Test reference type access expression trivial equality positive case")
    public void testRefTypeAccessExprTrivialEqualityCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestTrivialEqualityPositiveCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression trivial equality with type declared as variables")
    public void testRefTypeAccessExprTrivialEqualityCaseWithTypeDeclared() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result,
                "refTypeAccessTestTrivialEqualityPositiveCaseWithTypeDeclared", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression trivial equality with type declared as var")
    public void testRefTypeAccessExprTrivialEqualityCaseWithTypeDeclaredWithVar() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result,
                "refTypeAccessTestTrivialEqualityPositiveCaseWithTypeDeclaredWithVar", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression trivial equality negative case")
    public void testRefTypeAccessExprTrivialEqualityNegativeCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestTrivialEqualityNegativeCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 2;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression trivial not equality case")
    public void testRefTypeAccessExprTrivialNotEqualityCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestTrivialNotEqualityCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }


    @Test(description = "Test reference type access expression Any type negative case")
    public void testRefTypeAccessExprAnyTypeNegativeCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestAnyTypeNegativeCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 2;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression Any type positive case")
    public void testRefTypeAccessExprAnyTypePositiveCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestAnyTypePositiveCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression Map access case")
    public void testRefTypeAccessExprMapAccessCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestMapAccessCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 2;
        Assert.assertEquals(actual, expected);
    }


    @Test(description = "Test reference type access expression Array access case")
    public void testRefTypeAccessExprArrayAccessCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestArrayAccessCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 2;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression Array equality case")
    public void testRefTypeAccessTestArrayEqualityCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestArrayEqualityCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 2;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression Array equality positive case")
    public void testRefTypeAccessTestArrayEqualityPositiveCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestArrayEqualityPositiveCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression Struct access case")
    public void testRefTypeAccessExprStructAccessCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestStructAccessCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 2;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression Struct equality case")
    public void testRefTypeAccessTestStructTypeEqualityCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestStructTypeEqualityCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression Struct not equality case")
    public void testRefTypeAccessTestStructTypeNotEqualityCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestStructTypeNotEqualityCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 2;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression Struct equality negative case")
    public void testRefTypeAccessTestStructTypeNegativeEqualityCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestStructTypeNegativeEqualityCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 2;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression Struct not equality negative case")
    public void testRefTypeAccessTestStructTypeNegativeNotEqualityCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestStructTypeNegativeNotEqualityCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression Struct field equality case")
    public void testRefTypeAccessTestStructFieldTypeEqualityCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestStructFieldTypeEqualityCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression Struct field not equality case")
    public void testRefTypeAccessTestStructFieldTypeNotEqualityCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestStructFieldTypeNotEqualityCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 2;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression JSON equality case")
    public void testRefTypeAccessTestJSONEqualityCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestJSONEqualityCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression type as return value")
    public void testRefTypeAccessTestTypeAsReturnValue() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestTypeAsReturnValue", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression type of multi dimensional array negative case")
    public void testRefTypeAccessTestMultiArrayNegativeCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestMultiArrayNegativeCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 2;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression type of multi dimensional array positive case")
    public void testRefTypeAccessTestMultiArrayPositiveCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestMultiArrayPositiveCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression type of different dimensional arrays case")
    public void testRefTypeAccessTestMultiArrayDifferentDimensionCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestMultiArrayDifferentDimensionCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 2;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression type of different dimensional two type arrays case")
    public void testRefTypeAccessTestMultiArrayDifferentDimensionCaseTwo() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "refTypeAccessTestMultiArrayDifferentDimensionCaseTwo", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 2;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test reference type access expression type of different dimensional two type arrays " +
            "check not quality case")
    public void testRefTypeAccessTestMultiArrayDifferentDimensionNotEqualityCase() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result,
                "refTypeAccessTestMultiArrayDifferentDimensionNotEqualityCase", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BInteger.class);

        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test type to any implicit cast.")
    public void testTypeToAnyImplicitCast() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "typeToAnyImplicitCast", args);

        Assert.assertEquals(returns.length, 2);
        Assert.assertSame(returns[0].getClass(), BTypeValue.class);
        Assert.assertSame(returns[1].getClass(), BTypeValue.class);
        Assert.assertEquals(returns[0], returns[1]);
    }

    @Test(description = "Test type to any explicit cast.")
    public void testTypeToAnyExplicitCast() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "typeToAnyExplicitCast", args);

        Assert.assertEquals(returns.length, 3);
        Assert.assertSame(returns[0].getClass(), BTypeValue.class);
        Assert.assertSame(returns[1].getClass(), BTypeValue.class);
        Assert.assertSame(returns[2].getClass(), BTypeValue.class);
        Assert.assertEquals(returns[0], returns[1]);
        Assert.assertEquals(returns[1], returns[2]);
    }

    @Test(description = "Test any to type explicit cast.")
    public void testAnyToTypeExplicitCast() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "anyToTypeExplicitCast", args);

        Assert.assertEquals(returns.length, 2);
        Assert.assertSame(returns[0].getClass(), BTypeValue.class);
        Assert.assertSame(returns[1].getClass(), BTypeValue.class);

        Assert.assertEquals(returns[0], returns[1]);
    }

    @Test(description = "Test access string value of a type")
    public void testTypeStringValue() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "getTypeStringValue", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BTypeValue.class);

        String typeString = returns[0].stringValue();
        String typeStringExpected = "int";
        Assert.assertEquals(typeString, typeStringExpected);
    }

    @Test(description = "Test access string value of a Struct type")
    public void getStructTypeStringValue() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result, "getStructTypeStringValue", args);

        Assert.assertEquals(returns.length, 1);
        Assert.assertSame(returns[0].getClass(), BTypeValue.class);

        String typeString = returns[0].stringValue();
        String typeStringExpected = "Person";
        Assert.assertEquals(typeString, typeStringExpected);
    }

    @Test(description = "Test TypeAccessExpr with ValueType.")
    public void testTypeAccessExprValueType() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result,
                "testTypeAccessExprValueType", args);

        Assert.assertEquals(returns.length, 1);


        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test TypeAccessExpr with ValueType negative.")
    public void testTypeAccessExprValueTypeNegative() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result,
                "testTypeAccessExprValueTypeNegative", args);

        Assert.assertEquals(returns.length, 1);


        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 0;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test TypeAccessExpr with ValueType array.")
    public void testTypeAccessExprValueTypeArray() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result,
                "testTypeAccessExprValueTypeArray", args);

        Assert.assertEquals(returns.length, 1);


        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 0;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test TypeAccessExpr with ValueType array negative.")
    public void testTypeAccessExprValueTypeArrayNegative() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result,
                "testTypeAccessExprValueTypeArrayNegative", args);

        Assert.assertEquals(returns.length, 1);


        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 0;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test TypeAccessExpr with Struct Type with its own value.")
    public void testTypeAccessExprStructTypeWithValue() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result,
                "testTypeAccessExprStructWithValue", args);

        Assert.assertEquals(returns.length, 1);


        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test TypeAccessExpr with two Struct with a value Negative.")
    public void testTypeAccessExprStructTypeWithValueNegative() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result,
                "testTypeAccessExprStructWithValueNegative", args);

        Assert.assertEquals(returns.length, 1);


        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 0;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test TypeAccessExpr with two Struct types.")
    public void testTypeAccessExprTwoStructTypes() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result,
                "testTypeAccessExprTwoStructTypes", args);

        Assert.assertEquals(returns.length, 1);


        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 0;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test TypeAccessExpr with Same Struct type.")
    public void testTypeAccessExprSameStructType() {
        BValue[] args = {};
        BValue[] returns = BRunUtil.invoke(result,
                "testTypeAccessExprSameStructType", args);

        Assert.assertEquals(returns.length, 1);


        int actual = (int) ((BInteger) returns[0]).intValue();
        int expected = 1;
        Assert.assertEquals(actual, expected);
    }

}


