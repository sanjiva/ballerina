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
package org.wso2.ballerinalang.programfile;

/**
 * @since 0.94
 */
public class ProgramFileConstants {

    public static final int MAGIC_NUMBER = 0xBA1DA4CE;
    public static final short VERSION_NUMBER = 17;

    // int, float, string, boolean, blob, reference type
    public static final int NO_OF_VAR_TYPE_CATEGORIES = 6;
    public static final int INT_OFFSET = 0;
    public static final int FLOAT_OFFSET = 1;
    public static final int STRING_OFFSET = 2;
    public static final int BOOL_OFFSET = 3;
    public static final int BLOB_OFFSET = 4;
    public static final int REF_OFFSET = 5;
}
