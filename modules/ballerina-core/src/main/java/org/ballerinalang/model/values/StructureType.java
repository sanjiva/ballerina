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

import org.ballerinalang.model.types.BType;

/**
 * @since 0.87
 */
public interface StructureType {

    void init(int[] fieldIndexes);

    long getIntField(int index);

    void setIntField(int index, long value);

    double getFloatField(int index);

    void setFloatField(int index, double value);

    String getStringField(int index);

    void setStringField(int index, String value);

    int getBooleanField(int index);

    void setBooleanField(int index, int value);

    byte[] getBlobField(int index);

    void setBlobField(int index, byte[] value);

    BRefType getRefField(int index);

    void setRefField(int index, BRefType value);

    // TODO Remove all following methods when old executor is removed
    BType[] getFieldTypes();

    void setFieldTypes(BType[] fieldTypes);

    BValue[] getMemoryBlock();

    void setMemoryBlock(BValue[] memoryBlock);


}
