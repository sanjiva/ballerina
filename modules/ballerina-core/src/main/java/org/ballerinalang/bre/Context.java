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
package org.ballerinalang.bre;

import org.ballerinalang.bre.bvm.ControlStackNew;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.runtime.BalCallback;
import org.ballerinalang.util.codegen.ActionInfo;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.codegen.ServiceInfo;
import org.ballerinalang.util.codegen.cpentries.FunctionCallCPEntry;
import org.ballerinalang.util.debugger.DebugInfoHolder;
import org.wso2.carbon.messaging.CarbonMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code Context} represents the runtime state of a program.
 *
 * @since 0.8.0
 */
public class Context {

    //TODO: Rename this into BContext and move this to runtime package
    private ControlStackNew controlStackNew;
    private CarbonMessage cMsg;
    private BalCallback balCallback;
    protected Map<String, Object> properties = new HashMap<>();
    private ServiceInfo serviceInfo;
    private BallerinaTransactionManager ballerinaTransactionManager;
    private DebugInfoHolder debugInfoHolder;
    private boolean debugEnabled = false;

    private int startIP;
    private BStruct errorThrown;

    // TODO : Temporary solution to make non-blocking working.
    public boolean initFunction = false;
    public BValue[] nativeArgValues;
    public ProgramFile programFile;
    public FunctionCallCPEntry funcCallCPEntry;
    public ActionInfo actionInfo;

    @Deprecated
    public Context() {
        this.controlStackNew = new ControlStackNew();
    }

    public Context(ProgramFile programFile) {
        this.programFile = programFile;
        this.controlStackNew = new ControlStackNew();
    }

    public DebugInfoHolder getDebugInfoHolder() {
        return debugInfoHolder;
    }

    public void setDebugInfoHolder(DebugInfoHolder debugInfoHolder) {
        this.debugInfoHolder = debugInfoHolder;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public ControlStackNew getControlStackNew() {
        return controlStackNew;
    }

    public CarbonMessage getCarbonMessage() {
        return this.cMsg;
    }

    public void setCarbonMessage(CarbonMessage cMsg) {
        this.cMsg = cMsg;
    }

    public Object getProperty(String key) {
        return this.properties.get(key);
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    public BalCallback getBalCallback() {
        return this.balCallback;
    }

    public void setBalCallback(BalCallback balCallback) {
        this.balCallback = balCallback;
    }
    
    public ServiceInfo getServiceInfo() {
        return this.serviceInfo;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public void setBallerinaTransactionManager(BallerinaTransactionManager ballerinaTransactionManager) {
        this.ballerinaTransactionManager = ballerinaTransactionManager;
    }

    public BallerinaTransactionManager getBallerinaTransactionManager() {
        return this.ballerinaTransactionManager;
    }

    public boolean isInTransaction() {
        return this.ballerinaTransactionManager != null;
    }

    public BStruct getError() {
        return errorThrown;
    }

    public void setError(BStruct error) {
        this.errorThrown = error;
    }

    public int getStartIP() {
        return startIP;
    }

    public void setStartIP(int startIP) {
        this.startIP = startIP;
    }

    public ProgramFile getProgramFile() {
        return programFile;
    }
}
