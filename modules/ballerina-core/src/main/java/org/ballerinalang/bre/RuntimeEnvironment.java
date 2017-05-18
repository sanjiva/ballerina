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
package org.ballerinalang.bre;

import org.ballerinalang.model.BLangPackage;
import org.ballerinalang.model.BLangProgram;
import org.ballerinalang.model.BallerinaFile;
import org.ballerinalang.model.Function;
import org.ballerinalang.model.Service;
import org.ballerinalang.model.values.BValue;

/**
 * {@code RuntimeEnvironment} represents the runtime environment of a Ballerina application.
 *
 * @since 0.8.0
 */
public class RuntimeEnvironment {
    private StaticMemory staticMemory;

    private RuntimeEnvironment(StaticMemory staticMemory) {
        this.staticMemory = staticMemory;
    }

    public StaticMemory getStaticMemory() {
        return staticMemory;
    }

    public static RuntimeEnvironment get(BLangProgram bLangProgram) {
        StaticMemory staticMemory = new StaticMemory(bLangProgram.getSizeOfStaticMem());
        RuntimeEnvironment runtimeEnvironment = new RuntimeEnvironment(staticMemory);
        if (bLangProgram.getMainPackage() != null) {
            initPackages(bLangProgram.getMainPackage(), runtimeEnvironment);
        }

        for (BLangPackage servicePackage : bLangProgram.getServicePackages()) {
            initPackages(servicePackage, runtimeEnvironment);
            for (Service service : servicePackage.getServices()) {
                Function initFunction = service.getInitFunction();
                CallableUnitInfo functionInfo = new CallableUnitInfo(initFunction.getName(),
                        initFunction.getPackagePath(), initFunction.getNodeLocation());
                StackFrame currentStackFrame = new StackFrame(new BValue[0], new BValue[0], functionInfo);

                Context bContext = new Context();
                bContext.getControlStack().pushFrame(currentStackFrame);

                BLangExecutor bLangExecutor = new BLangExecutor(runtimeEnvironment, bContext);
                bLangExecutor.setParentScope(initFunction.getSymbolScope());
                initFunction.getCallableUnitBody().execute(bLangExecutor);
            }
        }

        return runtimeEnvironment;
    }

    /**
     * Helper method to initialize packages, in this, global variables will get initialized.
     *
     * @param bLangPackage
     * @param runtimeEnvironment
     */
    private static void initPackages(BLangPackage bLangPackage, RuntimeEnvironment runtimeEnvironment) {
        Function pkgInitFunction = bLangPackage.getInitFunction();
        CallableUnitInfo pkgFunctionInfo = new CallableUnitInfo(pkgInitFunction.getName(),
                pkgInitFunction.getPackagePath(), pkgInitFunction.getNodeLocation());
        StackFrame currentStackFrame = new StackFrame(new BValue[0], new BValue[0], pkgFunctionInfo);

        Context bContext = new Context();
        bContext.getControlStack().pushFrame(currentStackFrame);

        BLangExecutor bLangExecutor = new BLangExecutor(runtimeEnvironment, bContext);
        bLangExecutor.setParentScope(pkgInitFunction.getSymbolScope());
        pkgInitFunction.getCallableUnitBody().execute(bLangExecutor);
        //bLangPackage.setPkgInitialized(true);
    }

    public static RuntimeEnvironment get(BallerinaFile bFile) {
        StaticMemory staticMemory = new StaticMemory(bFile.getSizeOfStaticMem());
        RuntimeEnvironment runtimeEnvironment = new RuntimeEnvironment(staticMemory);

        if (bFile.getServices().length == 0) {
            return runtimeEnvironment;
        }

        for (Service service : bFile.getServices()) {
            Function initFunction = service.getInitFunction();
            CallableUnitInfo functionInfo = new CallableUnitInfo(initFunction.getName(),
                    initFunction.getPackagePath(), initFunction.getNodeLocation());
            StackFrame currentStackFrame = new StackFrame(new BValue[0], new BValue[0], functionInfo);

            Context bContext = new Context();
            bContext.getControlStack().pushFrame(currentStackFrame);

            BLangExecutor bLangExecutor = new BLangExecutor(runtimeEnvironment, bContext);
            bLangExecutor.setParentScope(initFunction.getSymbolScope());
            initFunction.getCallableUnitBody().execute(bLangExecutor);
        }

        return new RuntimeEnvironment(staticMemory);
    }

    /**
     * {@code StaticMemory} represents an statically allocated block of memory which is used to store data
     * which does not change when the program executes.
     *
     * @since 0.8.0
     */
    public static class StaticMemory {
        BValue[] memSlots;

        StaticMemory(int sizeofStaticMemory) {
            memSlots = new BValue[sizeofStaticMemory];
        }

        public void setValue(int address, BValue bValue) {
            memSlots[address] = bValue;
        }

        public BValue getValue(int address) {
            return memSlots[address];
        }
    }
}
