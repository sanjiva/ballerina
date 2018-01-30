/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.runtime.message;

import org.ballerinalang.util.exceptions.BallerinaException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * {@code StringValue} represents a string value in Ballerina.
 *
 * @since 0.8.0
 */
public class StringDataSource extends BallerinaMessageDataSource {
    private String value;

    /**
     * Create a String datasource with a string.
     * 
     * @param value String value
     */
    public StringDataSource(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void serializeData(OutputStream outputStream) {
        try {
            outputStream.write(this.value.getBytes(Charset.defaultCharset()));
        } catch (IOException e) {
            throw new BallerinaException("Error occurred during writing the string message to the output stream", e);
        }
    }

    @Override
    public String getMessageAsString() {
        return this.value;
    }

    @Override
    public BallerinaMessageDataSource clone() {
        String clonedContent = this.getMessageAsString();
        return new StringDataSource(clonedContent);
    }
}
