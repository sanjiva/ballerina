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

package org.ballerinalang.util.debugger.dto;

import org.ballerinalang.model.values.BJSON;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BNewArray;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.BValueType;
import org.ballerinalang.model.values.BXML;

/**
 * DTO class representing variables in the stack upon a debug hit.
 *
 * @since 0.8.0
 */
public class VariableDTO {

    private String scope, name;
    private String type, value;

    public VariableDTO(String name, String scope) {
        this.scope = scope;
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setBValue(BValue bValue) {
        if (bValue == null) {
            type = "null";
            value = "null";
            return;
        }
        type = bValue.getType().toString();
        value = getStringValue(bValue);
    }

    private String getStringValue(BValue bValue) {
        String bValueString;
        if (bValue instanceof BValueType || bValue instanceof BXML || bValue instanceof BJSON) {
            bValueString = bValue.stringValue();
        } else if (bValue instanceof BNewArray) {
            BNewArray bArray = (BNewArray) bValue;
            bValueString = "Array[" + bArray.size() + "] ";
            bValueString = bValueString + bArray.stringValue();
        } else if (bValue instanceof BMap) {
            BMap bmap = (BMap) bValue;
            bValueString = "Map[" + bmap.size() + "] ";
            bValueString = bValueString + bmap.stringValue();
        } else if (bValue instanceof BStruct) {
            BStruct bStruct = (BStruct) bValue;
            bValueString = "struct " + bStruct.getType().getName() + " ";
            bValueString = bValueString + bStruct.stringValue();
        } else {
            bValueString = "<Complex_Value>";
        }
        return bValueString;
    }

    @Override
    public String toString() {
        return "(" + scope + ") " + name + " = " + value + " {" + type + "}";
    }
}
