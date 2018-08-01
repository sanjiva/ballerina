/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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
package org.ballerinalang.stdlib.internal.compression;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BLangVMStructs;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.codegen.PackageInfo;
import org.ballerinalang.util.codegen.StructureTypeInfo;

/**
 * Util class for compression related operations.
 */
public class CompressionUtils {
    public static final String PROTOCOL_PACKAGE_COMPRESSION = "ballerina/internal";

    /**
     * Get compression error as a ballerina struct.
     *
     * @param context Represent ballerina context
     * @param msg     Error message in string form
     * @return Ballerina struct with compression error
     */
    public static BMap<String, BValue> createCompressionError(Context context, String msg) {
        PackageInfo filePkg = context.getProgramFile().getPackageInfo(PROTOCOL_PACKAGE_COMPRESSION);
        StructureTypeInfo compressionErrorStruct = filePkg.getStructInfo("CompressionError");
        return BLangVMStructs.createBStruct(compressionErrorStruct, msg);
    }
}
