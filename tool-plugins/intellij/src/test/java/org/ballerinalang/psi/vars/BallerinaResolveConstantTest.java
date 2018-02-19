/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.psi.vars;

import org.ballerinalang.psi.BallerinaResolveTestBase;

import java.io.IOException;

/**
 * Test constant resolving.
 */
public class BallerinaResolveConstantTest extends BallerinaResolveTestBase {

    private String constant = "public const int /*def*/a;";

    @Override
    protected String getTestDataPath() {
        return getTestDataPath("psi/resolve/vars/constant");
    }

    public void testConstantInSameFileInConnector() {
        doFileTest();
    }

    public void testConstantInSameFileInFunction() {
        doFileTest();
    }

    public void testConstantInSameFileInService() {
        doFileTest();
    }

    public void testConstantInDifferentFileInConnector() throws IOException {
        doFileTest(constant);
    }

    public void testConstantInDifferentFileInFunction() throws IOException {
        doFileTest(constant);
    }

    public void testConstantInDifferentFileInService() throws IOException {
        doFileTest(constant);
    }


    public void testConstantInDifferentPackageInConnector() throws IOException {
        doFileTest(constant, "org/test/test.bal");
    }

    public void testConstantInDifferentPackageInFunction() throws IOException {
        doFileTest(constant, "org/test/test.bal");
    }

    public void testConstantInDifferentPackageInService() throws IOException {
        doFileTest(constant, "org/test/test.bal");
    }
}
