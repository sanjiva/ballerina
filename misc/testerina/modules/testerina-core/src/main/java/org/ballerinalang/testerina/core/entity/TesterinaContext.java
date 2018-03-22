/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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
package org.ballerinalang.testerina.core.entity;

import org.ballerinalang.util.codegen.PackageInfo;
import org.ballerinalang.util.codegen.ProgramFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TesterinaContext entity class.
 */
public class TesterinaContext {

    private Map<String, TestSuite> testSuites = new HashMap<>();
    private ArrayList<TesterinaFunction> testFunctions = new ArrayList<>();
    private ArrayList<TesterinaFunction> beforeTestFunctions = new ArrayList<>();
    private ArrayList<TesterinaFunction> afterTestFunctions = new ArrayList<>();

    public Map<String, TesterinaFunction> getMockFunctionsMap() {
        return mockFunctionsMap;
    }

    /**
     * Key - unique identifier for a function.
     * Value - a @{@link TesterinaFunction}
     */
    private Map<String, TesterinaFunction> mockFunctionsMap = new HashMap<>();

//    private List<String> groups;
//    private boolean excludeGroups = false;

    public TesterinaContext(List<String> groups, boolean excludeGroups) {
//        this.groups = groups;
//        this.excludeGroups = excludeGroups;
    }

    /**
     * Getter method for testFunction. Returns an ArrayList of functions starting with prefix 'test'.
     *
     * @return ArrayList
     */
    public ArrayList<TesterinaFunction> getTestFunctions() {
        return this.testFunctions;
    }

    /**
     * Getter method for testFunction. Returns an ArrayList of functions starting with prefix 'test'.
     *
     * @return ArrayList
     */
    public ArrayList<TesterinaFunction> getBeforeTestFunctions() {
        return this.beforeTestFunctions;
    }

    /**
     * Getter method for testFunction. Returns an ArrayList of functions starting with prefix 'test'.
     *
     * @return ArrayList
     */
    public ArrayList<TesterinaFunction> getAfterTestFunctions() {
        return this.afterTestFunctions;
    }

    public Map<String, TestSuite> getTestSuites() {
        return testSuites;
    }

    //    /**
    //     * Get the list of 'test/beforeTest' functions, parsed from the *.bal file.
    //     *
    //     * @param programFile {@link ProgramFile}.
    //     */
    //    private void extractTestFunctions(ProgramFile programFile) {
    //        Arrays.stream(programFile.getPackageInfoEntries()).map(PackageInfo::getFunctionInfoEntries)
    //                .flatMap(Arrays::stream).forEachOrdered(f -> addTestFunctions(programFile, f));
    //    }

    //    private void addTestFunctions(ProgramFile programFile, FunctionInfo functionInfo) {
    //        //TODO : We need exclude builtin packages in ballerina when searching for test functions
    //        String nameUpperCase = functionInfo.getName().toUpperCase(Locale.ENGLISH);
    //
    //        if (nameUpperCase.startsWith(TesterinaFunction.PREFIX_TEST) && !nameUpperCase
    //                .endsWith(TesterinaFunction.INIT_SUFFIX)) {
    //
    //            TesterinaFunction tFunction = TAnnotationProcessor
    //                    .processAnnotations(programFile, functionInfo, groups, excludeGroups);
    //
    //            this.testFunction.add(tFunction);
    //        } else if (nameUpperCase.startsWith(TesterinaFunction.PREFIX_BEFORETEST) && !nameUpperCase
    //                .endsWith(TesterinaFunction.INIT_SUFFIX)) {
    //            TesterinaFunction tFunction = new TesterinaFunction(programFile, functionInfo,
    //                    TesterinaFunction.Type.BEFORE_TEST);
    //            this.beforeTestFunction.add(tFunction);
    //        } else if (nameUpperCase.startsWith(TesterinaFunction.PREFIX_AFTERTEST) && !nameUpperCase
    //                .endsWith(TesterinaFunction.INIT_SUFFIX)) {
    //            TesterinaFunction tFunction = new TesterinaFunction(programFile, functionInfo,
    //                    TesterinaFunction.Type.AFTER_TEST);
    //            this.afterTestFunction.add(tFunction);
    //        }
    //    }

    public void process(ProgramFile programFile) {
        for (PackageInfo packageInfo : programFile.getPackageInfoEntries()) {
            if (packageInfo.getPkgPath().startsWith("ballerina")) {
                // skip this
            } else {
                if (testSuites.get(packageInfo.getPkgPath()) == null) {
                    // create a new suite instance
                    testSuites.put(packageInfo.getPkgPath(), new TestSuite(packageInfo.getPkgPath()));
                    //processTestSuites
                }
//                TAnnotationProcessor.processAnnotations(this, programFile, packageInfo, testSuites.get(packageInfo
//                        .getPkgPath()), groups, excludeGroups);
            }
        }
    }

    public void addMockFunction (String key, TesterinaFunction function) {
        this.mockFunctionsMap.put(key, function);
    }

    public TesterinaFunction getMockFunction (String key) {
        return this.mockFunctionsMap.get(key);
    }

}
