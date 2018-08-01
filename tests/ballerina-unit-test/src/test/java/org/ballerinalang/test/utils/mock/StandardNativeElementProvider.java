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
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.ballerinalang.test.utils.mock;

import org.ballerinalang.annotation.JavaSPIService;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.NativeElementRepository;
import org.ballerinalang.natives.NativeElementRepository.NativeFunctionDef;
import org.ballerinalang.spi.NativeElementProvider;

/**
 * Standard native element provider class.
 */
@JavaSPIService("org.ballerinalang.spi.NativeElementProvider")
public class StandardNativeElementProvider implements NativeElementProvider {

    @Override
    public void populateNatives(NativeElementRepository repo) {
        repo.registerNativeFunction(new NativeFunctionDef("testorg", "foo.bar:0.0.0",
                "mockedNativeFuncWithOptionalParams",
                new TypeKind[] { TypeKind.INT, TypeKind.FLOAT, TypeKind.STRING, TypeKind.INT, TypeKind.STRING,
                        TypeKind.ARRAY },
                new TypeKind[] {}, "org.ballerinalang.test.utils.mock.TestOptionalArgsInNativeFunc"));
    }

}
