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
package org.ballerinalang.test.types.stream;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BRefValueArray;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.exceptions.BLangRuntimeException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Objects;

/**
 * Class to test stream literal.
 */
public class StreamLiteralTest {

    private CompileResult result;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/types/stream/stream-literal.bal");
    }

    @Test(description = "Test invalid stream creation",
          expectedExceptions = { BLangRuntimeException.class },
          expectedExceptionsMessageRegExp = ".*message: a stream cannot be created without a constraint.*")
    public void testInvalidStreamCreation() {
        BRunUtil.invoke(result, "testInvalidStreamCreation");
    }

    @Test(description = "Test publishing struct of invalid type to a stream",
            expectedExceptions = { BLangRuntimeException.class },
            expectedExceptionsMessageRegExp = ".*message: incompatible types: struct of type:Job cannot be added to "
                    + "a stream of type:Employee.*")
    public void testInvalidStructPublishingToStream() {
        BRunUtil.invoke(result, "testInvalidStructPublishingToStream");
    }

    @Test(description = "Test subscribing with a function accepting a type other than a struct",
            expectedExceptions = { BLangRuntimeException.class },
            expectedExceptionsMessageRegExp = ".*message: incompatible function: subscription function needs to be a"
                    + " function accepting a struct of type:Employee.*")
    public void testSubscriptionFunctionWithNonStructParameter() {
        BRunUtil.invoke(result, "testSubscriptionFunctionWithNonStructParameter");
    }

    @Test(description = "Test subscribing with a function accepting a different kind of struct",
            expectedExceptions = { BLangRuntimeException.class },
            expectedExceptionsMessageRegExp = ".*message: incompatible function: subscription function needs to be a "
                    + "function accepting a struct of type:Employee.*")
    public void testSubscriptionFunctionWithIncorrectStructParameter() {
        BRunUtil.invoke(result, "testSubscriptionFunctionWithIncorrectStructParameter");
    }

    @Test(description = "Test receipt of single event with correct subscription and publishing")
    public void testStreamPublishingAndSubscription() {
        BValue[] returns = BRunUtil.invoke(result, "testStreamPublishingAndSubscription");
        BStruct origEmployee = (BStruct) returns[0];
        BStruct publishedEmployee = (BStruct) returns[1];
        BStruct modifiedOrigEmployee = (BStruct) returns[2];
        Assert.assertNull(origEmployee);
        Assert.assertTrue(Objects.equals(publishedEmployee.getType().getName(),
                                         modifiedOrigEmployee.getType().getName()));
        Assert.assertEquals(publishedEmployee.getIntField(0), modifiedOrigEmployee.getIntField(0),
                            "Struct field \"id\" of received event does not match that of published event");
        Assert.assertEquals(publishedEmployee.getStringField(0), modifiedOrigEmployee.getStringField(0),
                            "Struct field \"name\" of received event does not match that of published event");
    }

    @Test(description = "Test receipt of multiple events with correct subscription and publishing")
    public void testStreamPublishingAndSubscriptionForMultipleEvents() {
        BValue[] returns = BRunUtil.invoke(result, "testStreamPublishingAndSubscriptionForMultipleEvents");
        BRefValueArray publishedEmployeeEvents = (BRefValueArray) returns[0];
        BRefValueArray receivedEmployeeEvents = (BRefValueArray) returns[1];

        Assert.assertNotNull(publishedEmployeeEvents);
        Assert.assertNotNull(receivedEmployeeEvents);
        Assert.assertEquals(publishedEmployeeEvents.size(), receivedEmployeeEvents.size(), "Number of Employee "
                + "Events received does not match the number published");
        for (int i = 0; i < publishedEmployeeEvents.size(); i++) {
            BStruct publishedEmployeeEvent = (BStruct) publishedEmployeeEvents.get(i);
            BStruct receivedEmployeeEvent = (BStruct) receivedEmployeeEvents.get(i);
            Assert.assertTrue(Objects.equals(publishedEmployeeEvent.getType().getName(),
                                             receivedEmployeeEvent.getType().getName()));
            Assert.assertEquals(publishedEmployeeEvent.getIntField(0), receivedEmployeeEvent.getIntField(0),
                                "Struct field \"id\" of received event does not match that of published event");
            Assert.assertEquals(publishedEmployeeEvent.getStringField(0), receivedEmployeeEvent.getStringField(0),
                                "Struct field \"name\" of received event does not match that of published event");
        }
    }
}
