/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.nativeimpl.builtin.jsonlib;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BJSON;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.nativeimpl.lang.utils.ErrorHandler;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Native function ballerina.model.json:toString.
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "builtin",
        functionName = "json.toString",
        args = {@Argument(name = "j", type = TypeKind.JSON)},
        returnType = {@ReturnType(type = TypeKind.STRING)},
        isPublic = true
)
public class ToString extends BlockingNativeCallableUnit {

    private static final Logger log = LoggerFactory.getLogger(ToString.class);

    @Override
    public void execute(Context ctx) {
        String jsonStr = null;
        try {
            // Accessing Parameters.
            BJSON json = (BJSON) ctx.getRefArgument(0);

            jsonStr = json.stringValue();
            if (log.isDebugEnabled()) {
                log.debug("Output JSON: " + jsonStr);
            }
        } catch (Throwable e) {
            ErrorHandler.handleJsonException("convert json to string", e);
        }

        ctx.setReturnValues(new BString(jsonStr));
    }
}
