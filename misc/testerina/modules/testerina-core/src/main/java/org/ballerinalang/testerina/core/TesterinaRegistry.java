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
package org.ballerinalang.testerina.core;

import org.ballerinalang.testerina.core.entity.TestSuite;
import org.ballerinalang.util.codegen.ProgramFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keep a registry of {@code {@link ProgramFile}} instances.
 * This is required to modify the runtime behavior.
 */
public class TesterinaRegistry {

    public Map<String, TestSuite> getTestSuites() {
        return testSuites;
    }

    private List<String> groups = new ArrayList<>();

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public boolean shouldIncludeGroups() {
        return shouldIncludeGroups;
    }

    public void setShouldIncludeGroups(boolean shouldIncludeGroups) {
        this.shouldIncludeGroups = shouldIncludeGroups;
    }

    private boolean shouldIncludeGroups;

    public void setTestSuites(Map<String, TestSuite> testSuites) {
        this.testSuites = testSuites;
    }

    private Map<String, TestSuite> testSuites = new HashMap<>();

    public static void setProgramFiles(List<ProgramFile> programFiles) {
        TesterinaRegistry.programFiles = programFiles;
    }

    private static List<ProgramFile> programFiles = new ArrayList<>();
    private static final TesterinaRegistry instance = new TesterinaRegistry();

    public static TesterinaRegistry getInstance() {
        return instance;
    }

    public Collection<ProgramFile> getProgramFiles() {
        return Collections.unmodifiableCollection(programFiles);
    }

    public void addProgramFile(ProgramFile programFile) {
        programFiles.add(programFile);
    }
 }
