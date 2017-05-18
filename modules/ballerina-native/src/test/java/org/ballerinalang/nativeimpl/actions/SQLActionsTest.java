/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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
package org.ballerinalang.nativeimpl.actions;

import org.ballerinalang.model.BLangProgram;
import org.ballerinalang.model.values.BArray;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.nativeimpl.util.BTestUtils;
import org.ballerinalang.nativeimpl.util.SQLDBUtils;
import org.ballerinalang.util.program.BLangFunctions;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Test class for SQL Connector test.
 *
 * @since 0.8.0
 */
public class SQLActionsTest {
    private static final double DELTA = 0.01;
    BLangProgram bLangProgram;
    private static final String DB_NAME = "TEST_SQL_CONNECTOR";

    @BeforeClass()
    public void setup() {
        bLangProgram = BTestUtils.parseBalFile("samples/sqlConnectorTest.bal");
        SQLDBUtils.deleteFiles(new File(SQLDBUtils.DB_DIRECTORY), DB_NAME);
        SQLDBUtils.initDatabase(SQLDBUtils.DB_DIRECTORY, DB_NAME, "datafiles/SQLConnectorDataFile.sql");
    }

    @Test(groups = "ConnectorTest")
    public void testInsertTableData() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testInsertTableData");
        BInteger retValue = (BInteger) returns[0];
        Assert.assertEquals(retValue.intValue(), 1);
    }

    @Test(groups = "ConnectorTest")
    public void testCreateTable() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testCreateTable");
        BInteger retValue = (BInteger) returns[0];
        Assert.assertEquals(retValue.intValue(), 0);
    }

    @Test(groups = "ConnectorTest")
    public void testUpdateTableData() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testUpdateTableData");
        BInteger retValue = (BInteger) returns[0];
        Assert.assertEquals(retValue.intValue(), 1);
    }

    @Test(groups = "ConnectorTest")
    public void testGeneratedKeyOnInsert() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testGeneratedKeyOnInsert");
        BString retValue = (BString) returns[0];
        Assert.assertTrue(retValue.intValue() > 0);
    }

    @Test(groups = "ConnectorTest")
    public void testGeneratedKeyWithColumn() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testGeneratedKeyWithColumn");
        BString retValue = (BString) returns[0];
        Assert.assertTrue(retValue.intValue() > 0);
    }

    @Test(groups = "ConnectorTest")
    public void testSelectData() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testSelectData");
        BString retValue = (BString) returns[0];
        final String expected = "Peter";
        Assert.assertEquals(retValue.stringValue(), expected);
    }

    @Test(groups = "ConnectorTest for int float types")
    public void testSelectIntFloatData() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testSelectIntFloatData");
        Assert.assertEquals(returns.length, 4);
        Assert.assertSame(returns[0].getClass(), BInteger.class);
        Assert.assertSame(returns[1].getClass(), BInteger.class);
        Assert.assertSame(returns[2].getClass(), BFloat.class);
        Assert.assertSame(returns[3].getClass(), BFloat.class);
        BInteger intVal = (BInteger) returns[0];
        BInteger longVal = (BInteger) returns[1];
        BFloat floatVal = (BFloat) returns[2];
        BFloat doubleVal = (BFloat) returns[3];
        long intExpected = 10;
        long longExpected = 9223372036854774807L;
        double floatExpected = 123.34;
        double doubleExpected = 2139095039;
        Assert.assertEquals(intVal.intValue(), intExpected);
        Assert.assertEquals(longVal.intValue(), longExpected);
        Assert.assertEquals(floatVal.floatValue(), floatExpected, DELTA);
        Assert.assertEquals(doubleVal.floatValue(), doubleExpected);
    }

    @Test(groups = "ConnectorTest")
    public void testCallProcedure() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testCallProcedure");
        BString retValue = (BString) returns[0];
        final String expected = "James";
        Assert.assertEquals(retValue.stringValue(), expected);
    }

    @Test(groups = "ConnectorTest")
    public void testCallProcedureWithResultSet() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testCallProcedureWithResultSet");
        BString retValue = (BString) returns[0];
        final String expected = "Peter";
        Assert.assertEquals(retValue.stringValue(), expected);
    }

    @Test(groups = "ConnectorTest")
    public void testConnectorWithDataSource() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testConnectorWithDataSource");
        BString retValue = (BString) returns[0];
        final String expected = "Peter";
        Assert.assertEquals(retValue.stringValue(), expected);
    }

    @Test(groups = "ConnectorTest")
    public void testConnectionPoolProperties() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testConnectionPoolProperties");
        BString retValue = (BString) returns[0];
        final String expected = "Peter";
        Assert.assertEquals(retValue.stringValue(), expected);
    }

    @Test(groups = "ConnectorTest")
    public void testQueryParameters() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testQueryParameters");
        BString retValue = (BString) returns[0];
        final String expected = "Peter";
        Assert.assertEquals(retValue.stringValue(), expected);
    }

    @Test(groups = "ConnectorTest")
    public void testInsertTableDataWithParameters() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testInsertTableDataWithParameters");
        BInteger retValue = (BInteger) returns[0];
        Assert.assertEquals(retValue.intValue(), 1);
    }

    @Test(groups = "ConnectorTest")
    public void testOutParameters() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testOutParameters");
        Assert.assertEquals(returns.length, 14);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 9223372036854774807L);
        Assert.assertEquals(((BFloat) returns[2]).floatValue(), 123.34D, DELTA);
        Assert.assertEquals(((BFloat) returns[3]).floatValue(), 2139095039D);
        Assert.assertEquals(((BBoolean) returns[4]).booleanValue(), true);
        Assert.assertEquals(returns[5].stringValue(), "Hello");
        Assert.assertEquals(((BFloat) returns[6]).floatValue(), 1234.567D);
        Assert.assertEquals(((BFloat) returns[7]).floatValue(), 1234.567D);
        Assert.assertEquals(((BFloat) returns[8]).floatValue(), 1234.567D, DELTA);
        Assert.assertEquals(((BInteger) returns[9]).intValue(), 1);
        Assert.assertEquals(((BInteger) returns[10]).intValue(), 5555);
        Assert.assertEquals(returns[11].stringValue(), "very long text");
        Assert.assertEquals(returns[12].stringValue(), "d3NvMiBiYWxsZXJpbmEgYmxvYiB0ZXN0Lg==");
        Assert.assertEquals(returns[13].stringValue(), "wso2 ballerina binary test.");
    }

    @Test(groups = "ConnectorTest")
    public void testNullOutParameters() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testNullOutParameters");
        Assert.assertEquals(returns.length, 14);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 0);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 0);
        Assert.assertEquals(((BFloat) returns[2]).floatValue(), 0.0D);
        Assert.assertEquals(((BFloat) returns[3]).floatValue(), 0.0D);
        Assert.assertEquals(((BBoolean) returns[4]).booleanValue(), false);
        Assert.assertEquals(returns[5].stringValue(), null);
        Assert.assertEquals(((BFloat) returns[6]).floatValue(), 0.0D);
        Assert.assertEquals(((BFloat) returns[7]).floatValue(), 0.0D);
        Assert.assertEquals(((BFloat) returns[8]).floatValue(), 0.0D);
        Assert.assertEquals(((BInteger) returns[9]).intValue(), 0);
        Assert.assertEquals(((BInteger) returns[10]).intValue(), 0);
        Assert.assertEquals(returns[11].stringValue(), null);
        Assert.assertEquals(returns[12].stringValue(), null);
        Assert.assertEquals(returns[13].stringValue(), null);
    }

    @Test(groups = "ConnectorTest")
    public void testINParameters() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testINParameters");
        BInteger retValue = (BInteger) returns[0];
        Assert.assertEquals(retValue.intValue(), 1);
    }

    @Test(groups = "ConnectorTest")
    public void testNullINParameters() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testNullINParameters");
        BInteger retValue = (BInteger) returns[0];
        Assert.assertEquals(retValue.intValue(), 1);
    }

    @Test(groups = "ConnectorTest")
    public void testINOutParameters() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testINOutParameters");
        Assert.assertEquals(returns.length, 14);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 10);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 9223372036854774807L);
        Assert.assertEquals(((BFloat) returns[2]).floatValue(), 123.34D, DELTA);
        Assert.assertEquals(((BFloat) returns[3]).floatValue(), 2139095039D);
        Assert.assertEquals(((BBoolean) returns[4]).booleanValue(), true);
        Assert.assertEquals(returns[5].stringValue(), "Hello");
        Assert.assertEquals(((BFloat) returns[6]).floatValue(), 1234.567D);
        Assert.assertEquals(((BFloat) returns[7]).floatValue(), 1234.567D);
        Assert.assertEquals(((BFloat) returns[8]).floatValue(), 1234.567D, DELTA);
        Assert.assertEquals(((BInteger) returns[9]).intValue(), 1);
        Assert.assertEquals(((BInteger) returns[10]).intValue(), 5555);
        Assert.assertEquals(returns[11].stringValue(), "very long text");
        Assert.assertEquals(returns[12].stringValue(), "YmxvYiBkYXRh");
        Assert.assertEquals(returns[13].stringValue(), "wso2 ballerina binary test.");
    }

    @Test(groups = "ConnectorTest")
    public void testNullINOutParameters() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testNullINOutParameters");
        Assert.assertEquals(returns.length, 14);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 0);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 0);
        Assert.assertEquals(((BFloat) returns[2]).floatValue(), 0.0D);
        Assert.assertEquals(((BFloat) returns[3]).floatValue(), 0.0D);
        Assert.assertEquals(((BBoolean) returns[4]).booleanValue(), false);
        Assert.assertEquals(returns[5].stringValue(), null);
        Assert.assertEquals(((BFloat) returns[6]).floatValue(), 0.0D);
        Assert.assertEquals(((BFloat) returns[7]).floatValue(), 0.0D);
        Assert.assertEquals(((BFloat) returns[8]).floatValue(), 0.0D);
        Assert.assertEquals(((BInteger) returns[9]).intValue(), 0);
        Assert.assertEquals(((BInteger) returns[10]).intValue(), 0);
        Assert.assertEquals(returns[11].stringValue(), null);
        Assert.assertEquals(returns[12].stringValue(), null);
        Assert.assertEquals(returns[13].stringValue(), null);
    }

    @Test(groups = "ConnectorTest")
    public void testEmptySQLType() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testEmptySQLType");
        BInteger retValue = (BInteger) returns[0];
        Assert.assertEquals(retValue.intValue(), 1);
    }

    @Test(groups = "ConnectorTest")
    public void testArrayInParameters() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testArrayInParameters");
        BInteger retValue = (BInteger) returns[0];
        Assert.assertEquals(retValue.intValue(), 1);

        Assert.assertTrue(returns[1] instanceof BMap);
        BMap<BString, BInteger> intArray = (BMap) returns[1];
        Assert.assertEquals(intArray.get(new BString("0")).intValue(), 1);

        Assert.assertTrue(returns[2] instanceof BMap);
        BMap<BString, BInteger> longArray = (BMap) returns[2];
        Assert.assertEquals(longArray.get(new BString("0")).intValue(), 10000000);
        Assert.assertEquals(longArray.get(new BString("1")).intValue(), 20000000);
        Assert.assertEquals(longArray.get(new BString("2")).intValue(), 30000000);

        Assert.assertTrue(returns[3] instanceof BMap);
        BMap<BString, BFloat> doubleArray = (BMap) returns[3];
        Assert.assertEquals(doubleArray.get(new BString("0")).floatValue(), 245.23);
        Assert.assertEquals(doubleArray.get(new BString("1")).floatValue(), 5559.49);
        Assert.assertEquals(doubleArray.get(new BString("2")).floatValue(), 8796.123);

        Assert.assertTrue(returns[4] instanceof BMap);
        BMap<BString, BString> stringArray = (BMap) returns[4];
        Assert.assertEquals(stringArray.get(new BString("0")).stringValue(), "Hello");
        Assert.assertEquals(stringArray.get(new BString("1")).stringValue(), "Ballerina");

        Assert.assertTrue(returns[5] instanceof BMap);
        BMap<BString, BBoolean> booleanArray = (BMap) returns[5];
        Assert.assertEquals(booleanArray.get(new BString("0")).booleanValue(), true);
        Assert.assertEquals(booleanArray.get(new BString("1")).booleanValue(), false);
        Assert.assertEquals(booleanArray.get(new BString("2")).booleanValue(), true);

        Assert.assertTrue(returns[6] instanceof BMap);
        BMap<BString, BFloat> floatArray = (BMap) returns[6];
        Assert.assertEquals(floatArray.get(new BString("0")).floatValue(), 245.23);
        Assert.assertEquals(floatArray.get(new BString("1")).floatValue(), 5559.49);
        Assert.assertEquals(floatArray.get(new BString("2")).floatValue(), 8796.123);
    }

    @Test(groups = "ConnectorTest")
    public void testArrayOutParameters() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testArrayOutParameters");
        Assert.assertEquals(returns[0].stringValue(), "[1,2,3]");
        Assert.assertEquals(returns[1].stringValue(), "[100000000,200000000,300000000]");
        Assert.assertEquals(returns[2].stringValue(), "[245.23,5559.49,8796.123]");
        Assert.assertEquals(returns[3].stringValue(), "[245.23,5559.49,8796.123]");
        Assert.assertEquals(returns[4].stringValue(), "[true,false,true]");
        Assert.assertEquals(returns[5].stringValue(), "[Hello,Ballerina]");
    }

    @Test(groups = "ConnectorTest")
    public void testArrayInOutParameters() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testArrayInOutParameters");

        Assert.assertEquals(returns[0].stringValue(), "1");
        Assert.assertEquals(returns[1].stringValue(), "[1,2,3]");
        Assert.assertEquals(returns[2].stringValue(), "[100000000,200000000,300000000]");
        Assert.assertEquals(returns[3].stringValue(), "[245.23,5559.49,8796.123]");
        Assert.assertEquals(returns[4].stringValue(), "[245.23,5559.49,8796.123]");
        Assert.assertEquals(returns[5].stringValue(), "[true,false,true]");
        Assert.assertEquals(returns[6].stringValue(), "[Hello,Ballerina]");
    }

    @Test(groups = "ConnectorTest")
    public void testBatchUpdate() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testBatchUpdate");
        BArray retValue = (BArray) returns[0];
        Assert.assertEquals(retValue.get(0).stringValue(), "1");
        Assert.assertEquals(retValue.get(1).stringValue(), "1");
    }

    @Test(groups = "ConnectorTest")
    public void testLocalTransacton() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testLocalTransacton");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 0);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 2);

    }

    @Test(groups = "ConnectorTest")
    public void testTransactonRollback() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testTransactonRollback");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), -1);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 0);
    }

    @Test(groups = "ConnectorTest")
    public void testTransactonAbort() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testTransactonAbort");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), -1);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 0);

    }

    @Test(groups = "ConnectorTest")
    public void testTransactonThrow() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testTransactonErrorThrow");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), -1);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), -1);
        Assert.assertEquals(((BInteger) returns[2]).intValue(), 0);
    }

    @Test(groups = "ConnectorTest")
    public void testTransactonThrowAndCatch() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testTransactionErrorThrowAndCatch");
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 0);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), -1);
        Assert.assertEquals(((BInteger) returns[2]).intValue(), 1);
    }

    @Test(groups = "ConnectorTest")
    public void testInsertTimeData() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testDateTimeInParameters");
        BArray retValue = (BArray) returns[0];
        Assert.assertEquals(retValue.get(0).stringValue(), "1");
        Assert.assertEquals(retValue.get(1).stringValue(), "1");
    }

    @Test(dependsOnGroups = "ConnectorTest")
    public void testCloseConnectionPool() {
        BValue[] returns = BLangFunctions.invoke(bLangProgram, "testCloseConnectionPool");
        BInteger retValue = (BInteger) returns[0];
        Assert.assertEquals(retValue.intValue(), 1);
    }


    @AfterSuite
    public void cleanup() {
        SQLDBUtils.deleteDirectory(new File(SQLDBUtils.DB_DIRECTORY));
    }
}
