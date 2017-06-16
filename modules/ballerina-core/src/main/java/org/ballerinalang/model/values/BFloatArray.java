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
package org.ballerinalang.model.values;

import org.ballerinalang.model.types.BArrayType;
import org.ballerinalang.model.types.BType;
import org.ballerinalang.model.types.BTypes;

import java.util.Arrays;

/**
 * @since 0.87
 */
public class BFloatArray extends BNewArray {

    private static BType arrayType = new BArrayType(BTypes.typeFloat);

    private double[] values;

    private BFloatArray(double[] values) {
        this.values = values;
    }

    public BFloatArray() {
        values = (double[]) newArrayInstance(Double.TYPE);
    }

    public void add(long index, double value) {
        prepareForAdd(index, values.length);
        values[(int) index] = value;
    }

    public double get(long index) {
        rangeCheckForGet(index, size);
        return values[(int) index];
    }

    @Override
    public BType getType() {
        return arrayType;
    }

    @Override
    public void grow(int newLength) {
        values = Arrays.copyOf(values, newLength);
    }

    @Override
    public BValue copy() {
        BFloatArray floatArray = new BFloatArray(Arrays.copyOf(values, values.length));
        floatArray.size = size;
        return floatArray;
    }
}
