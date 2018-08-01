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
package org.ballerinalang.test.closures;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BByteArray;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases for closure related scenarios in ballerina.
 */
public class ClosureTest {

    private CompileResult compileResult;

    @BeforeClass
    public void setup() {
        compileResult = BCompileUtil.compile("test-src/closures/closure.bal");
    }

    @Test(description = "Test basic closure operations")
    public void testBasicClosure() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test1");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 18);
    }

    @Test(description = "Test two level closure operations")
    public void testTwoLevelClosure() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test2");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 36);
    }

    @Test(description = "Test three level operations")
    public void testThreeLevelClosure() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test3");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 50);
    }

    @Test(description = "Test with if block")
    public void testClosureWithIfBlock() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test4");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 43);
    }

    @Test(description = "Test multi level function pointer call")
    public void testMultiLevelFPCallWithClosure() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test5");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);
    }

    @Test(description = "Test multi function pointer with same closure call")
    public void testMultiFunctionCallWithSameClosure() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test6");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 316);
    }

    @Test(description = "Test closure with different param ordering")
    public void testDifferentParamOrder() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test7");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 22);
    }

    @Test(description = "Test multi level function")
    public void testMultiLevelFunction() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test8");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 50);
    }

    @Test(description = "Test global var access with closure")
    public void testGlobalVarAccessWithClosure() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test9");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 23);
    }

    @Test(description = "Test different type args")
    public void testDifferentTypeArgs1() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test10");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 20);
    }

    @Test(description = "Test different type args 2")
    public void testDifferentTypeArgs2() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test11");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 9);
    }

    @Test(description = "Test different type args 3")
    public void testDifferentTypeArgs3() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test12");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);
    }

    @Test(description = "Test different type args 4")
    public void testDifferentTypeArgs4() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test13");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 9);
    }

    @Test(description = "Test string args with a closure")
    public void testStringArgsWithClosure() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test14");
        Assert.assertEquals((returns[0]).stringValue(), "HelloBallerinaWorld!!!");
    }

    @Test(description = "Test tuple type as argument")
    public void testTupleArgsWithClosure() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test16");
        Assert.assertEquals((returns[0]).stringValue(), "ImBallerina15.0Program !!!Hello11.1World !!!");
    }

    @Test(description = "Test tuple type as argument with an order")
    public void testTupleTypesOrderWithClosure() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test17");
        Assert.assertEquals((returns[0]).stringValue(),
                "I'mHello11.1World !!!Ballerina15.0Program!!!HelloInner44.8World Inner!!!");
    }

    @Test(description = "Test global var modify and access")
    public void testGlobalVarModifyAndAccess() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test18");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 16);
    }

    @Test(description = "Test closure with object attached function references")
    public void testClosureWithObjectAttachedFuncReferences() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test19");
        Assert.assertEquals((returns[0]).stringValue(), "Hello Ballerina7.4K43");
    }

    @Test(description = "Test closure with object attached function pointer references")
    public void testClosureWithObjectAttachedFuncPointerReferences() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test20");
        Assert.assertEquals((returns[0]).stringValue(), "16.3Ballerina !!!");
    }

    @Test(description = "Test closure with object external attached function pointer references")
    public void testClosureWithObjectExternalAttachedFuncPointerReferences() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test21");
        Assert.assertEquals((returns[0]).stringValue(), "7.3T45Hello Ballerina3");
    }

    @Test(description = "Test closure with different type args references")
    public void testClosureWithDifferentArgsReferences() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test22");
        Assert.assertEquals((returns[0]).stringValue(), "7InnerInt41.2InnerFloat4.5Ballerina !!!");
    }

    @Test(description = "Test closure with variable shadowing")
    public void testClosureWithVariableShadowing1() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test23");
        Assert.assertEquals((returns[0]).stringValue(), "Ballerina30");
    }

    @Test(description = "Test two level closure with variable shadowing")
    public void testClosureWithVariableShadowing2() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test24");
        Assert.assertEquals((returns[0]).stringValue(), "Out30In63Ballerina!!!");
    }

    @Test(description = "Test three level closure with variable shadowing")
    public void testClosureWithVariableShadowing3() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test25");
        Assert.assertEquals((returns[0]).stringValue(), "OutMost30Out44In77Ballerina!!!");
    }

    @Test(description = "Test three level closure with variable shadowing another test case")
    public void testClosureWithVariableShadowing4() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test26");
        Assert.assertEquals((returns[0]).stringValue(), "OutMost47Out47In35Ballerina!!!");
    }

    @Test(description = "Test iterable operations with lambda. This will verify whether local referred vars are " +
            "modified within closure")
    public void testIterableOperationsVarModification() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testLocalVarModifyWithinClosureScope");
        Assert.assertNotNull(returns);
        Assert.assertEquals(((BFloat) returns[0]).floatValue(), 0.0);
    }

    @Test(description = "Test byte and boolean")
    public void testByteAndBoolean() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test27");
        Assert.assertEquals(returns.length, 3);
        Assert.assertSame(returns[0].getClass(), BByteArray.class);
        Assert.assertSame(returns[1].getClass(), BByteArray.class);
        Assert.assertSame(returns[2].getClass(), BByteArray.class);
        BByteArray blob1 = (BByteArray) returns[0];
        BByteArray blob2 = (BByteArray) returns[1];
        BByteArray blob3 = (BByteArray) returns[2];
        assertJBytesWithBBytes(new byte[]{13, 7, 4, 3}, blob1);
        assertJBytesWithBBytes(new byte[]{3, 13, 5, 7, 3}, blob2);
        assertJBytesWithBBytes(new byte[]{1, 2, 3, 13, 7}, blob3);
    }

    private void assertJBytesWithBBytes(byte[] jBytes, BByteArray bBytes) {
        for (int i = 0; i < jBytes.length; i++) {
            Assert.assertEquals(bBytes.get(i), jBytes[i], "Invalid byte value returned.");
        }
    }

    @Test(description = "Test multi level block statements with closure test case")
    public void testMultiLevelBlockStatements() {
        BValue[] returns = BRunUtil.invoke(compileResult, "test28");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 57);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 167);
    }
}
