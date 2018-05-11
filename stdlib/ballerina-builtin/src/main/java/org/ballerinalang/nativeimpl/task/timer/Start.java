/*
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.nativeimpl.task.timer;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BFunctionPointer;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.nativeimpl.task.SchedulingException;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.util.codegen.cpentries.FunctionRefCPEntry;
import org.ballerinalang.util.exceptions.BLangExceptionHelper;
import org.ballerinalang.util.exceptions.RuntimeErrors;

/**
 * Native function ballerina/task:Timer.start.
 */
@BallerinaFunction(
        orgName = "ballerina",
        packageName = "task",
        functionName = "start",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = "Timer", structPackage = "ballerina.task"),
        isPublic = true
)
public class Start extends BlockingNativeCallableUnit {
    public void execute(Context ctx) {
        BStruct task = (BStruct) ctx.getRefArgument(0);
        int isRunning = task.getBooleanField(0);
        if (isRunning == 1) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.TASK_ALREADY_RUNNING);
        }

        FunctionRefCPEntry onTriggerFunctionRefCPEntry = ((BFunctionPointer) task.getRefField(0)).value();
        FunctionRefCPEntry onErrorFunctionRefCPEntry =
                task.getRefField(1) != null ? ((BFunctionPointer) task.getRefField(1)).value() : null;
        long delay = task.getIntField(0);
        long interval = task.getIntField(1);
        if (delay == -1) {
            delay = interval;
        }

        try {
            Timer timer = new Timer(this, ctx, delay, interval, onTriggerFunctionRefCPEntry, onErrorFunctionRefCPEntry);
            task.setStringField(0, timer.getId());
            task.setBooleanField(0, 1);
        } catch (SchedulingException e) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.INVALID_TASK_CONFIG);
        }
    }
}
