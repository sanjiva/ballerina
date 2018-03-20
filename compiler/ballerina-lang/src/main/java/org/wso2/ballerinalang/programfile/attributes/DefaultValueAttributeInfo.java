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
package org.wso2.ballerinalang.programfile.attributes;


import org.wso2.ballerinalang.programfile.DefaultValue;

/**
 * {@code DefaultValueAttributeInfo} contains default value details.
 *
 * @since 0.92
 */
public class DefaultValueAttributeInfo implements AttributeInfo {

    // Index to a UTF8CPEntry
    private int attributeNameIndex;

    private DefaultValue defaultValue;

    public DefaultValueAttributeInfo(int attributeNameIndex, DefaultValue defaultValue) {
        this.defaultValue = defaultValue;
        this.attributeNameIndex = attributeNameIndex;
    }

    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Kind getKind() {
        return Kind.DEFAULT_VALUE_ATTRIBUTE;
    }

    @Override
    public int getAttributeNameIndex() {
        return attributeNameIndex;
    }
}
