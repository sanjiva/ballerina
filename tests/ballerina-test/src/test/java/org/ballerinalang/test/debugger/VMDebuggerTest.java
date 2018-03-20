/*
*   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.test.debugger;

import org.ballerinalang.test.utils.debug.DebugPoint;
import org.ballerinalang.test.utils.debug.ExpectedResults;
import org.ballerinalang.test.utils.debug.Util;
import org.ballerinalang.test.utils.debug.VMDebuggerUtil;
import org.ballerinalang.util.debugger.Debugger;
import org.ballerinalang.util.debugger.dto.BreakPointDTO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.ballerinalang.test.utils.debug.Step.RESUME;
import static org.ballerinalang.test.utils.debug.Step.STEP_IN;
import static org.ballerinalang.test.utils.debug.Step.STEP_OUT;
import static org.ballerinalang.test.utils.debug.Step.STEP_OVER;

/**
 * Test Cases for {@link Debugger}.
 */
public class VMDebuggerTest {

    private static final String FILE = "test-debug.bal";
    private PrintStream original;

    @BeforeClass
    public void setup() {
        original = System.out;
        // Hiding all test System outs.
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    @AfterClass
    public void tearDown() {
        System.setOut(original);
    }

    @Test(description = "Testing Resume with break points.")
    public void testResume() {
        BreakPointDTO[] breakPoints = Util.createBreakNodeLocations(".", FILE,
                3, 16, 27, 28, 31, 33, 35, 41, 42, 43, 44, 45);

        List<DebugPoint> debugPoints = new ArrayList<>();
        debugPoints.add(Util.createDebugPoint(".", FILE, 3, RESUME, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 16, RESUME, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 41, RESUME, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 28, RESUME, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 35, RESUME, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 42, RESUME, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 43, RESUME, 1));

        ExpectedResults expRes = new ExpectedResults(debugPoints, 7);

        VMDebuggerUtil.startDebug("test-src/debugger/test-debug.bal", breakPoints, expRes);
    }

    @Test(description = "Testing Debugger with breakpoint in non executable and not reachable lines.")
    public void testNegativeBreakPoints() {
        BreakPointDTO[] breakPoints = Util.createBreakNodeLocations(".", FILE, 4, 7, 51, 37);

        List<DebugPoint> debugPoints = new ArrayList<>();

        ExpectedResults expRes = new ExpectedResults(debugPoints, 0);
        VMDebuggerUtil.startDebug("test-src/debugger/test-debug.bal", breakPoints, expRes);
    }

    @Test(description = "Testing Step In.")
    public void testStepIn() {
        BreakPointDTO[] breakPoints = Util.createBreakNodeLocations(".", FILE, 5, 8, 41);

        List<DebugPoint> debugPoints = new ArrayList<>();
        debugPoints.add(Util.createDebugPoint(".", FILE, 5, STEP_IN,  1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 12, STEP_IN,  1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 13, STEP_IN,  1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 14, STEP_IN,  1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 15, STEP_IN,  1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 19, STEP_IN,  1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 13, RESUME,  1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 8, STEP_IN, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 41, STEP_IN, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 25, STEP_IN, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 26, STEP_IN, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 28, STEP_IN, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 29, STEP_IN,  1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 35, STEP_IN,  1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 36, STEP_IN,  1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 42, STEP_IN,  1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 43, STEP_IN,  1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 9, RESUME, 1));

        ExpectedResults expRes = new ExpectedResults(debugPoints, 18);

        VMDebuggerUtil.startDebug("test-src/debugger/test-debug.bal", breakPoints, expRes);
    }

    @Test(description = "Testing Step Out.")
    public void testStepOut() {
        BreakPointDTO[] breakPoints = Util.createBreakNodeLocations(".", FILE, 25);

        List<DebugPoint> debugPoints = new ArrayList<>();
        debugPoints.add(Util.createDebugPoint(".", FILE, 25, STEP_OUT, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 42, STEP_OUT, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 9, RESUME, 1));

        ExpectedResults expRes = new ExpectedResults(debugPoints, 3);

        VMDebuggerUtil.startDebug("test-src/debugger/test-debug.bal", breakPoints, expRes);
    }

    @Test(description = "Testing Step Over.")
    public void testStepOver() {
        BreakPointDTO[] breakPoints = Util.createBreakNodeLocations(".", FILE, 3);

        List<DebugPoint> debugPoints = new ArrayList<>();
        debugPoints.add(Util.createDebugPoint(".", FILE, 3, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 5, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 6, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 8, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 9, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 10, RESUME, 1));

        ExpectedResults expRes = new ExpectedResults(debugPoints, 6);

        VMDebuggerUtil.startDebug("test-src/debugger/test-debug.bal", breakPoints, expRes);
    }

    @Test(description = "Testing Step over in IfCondition.")
    public void testStepOverIfStmt() {
        BreakPointDTO[] breakPoints = Util.createBreakNodeLocations(".", FILE, 26);

        List<DebugPoint> debugPoints = new ArrayList<>();
        debugPoints.add(Util.createDebugPoint(".", FILE, 26, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 28, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 29, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 35, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 36, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 42, RESUME, 1));

        ExpectedResults expRes = new ExpectedResults(debugPoints, 6);

        VMDebuggerUtil.startDebug("test-src/debugger/test-debug.bal", breakPoints, expRes);
    }

    @Test(description = "Testing Step over in WhileStmt.")
    public void testStepOverWhileStmt() {
        BreakPointDTO[] breakPoints = Util.createBreakNodeLocations(".", FILE, 12, 13, 19, 21);

        List<DebugPoint> debugPoints = new ArrayList<>();
        debugPoints.add(Util.createDebugPoint(".", FILE, 12, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", FILE, 13, RESUME, 5));
        debugPoints.add(Util.createDebugPoint(".", FILE, 19, RESUME, 4));
        debugPoints.add(Util.createDebugPoint(".", FILE, 21, RESUME, 1));

        ExpectedResults expRes = new ExpectedResults(debugPoints, 11);

        VMDebuggerUtil.startDebug("test-src/debugger/test-debug.bal", breakPoints, expRes);
    }

    @Test(description = "Testing while statement resume")
    public void testWhileStatementResume() {
        BreakPointDTO[] breakPoints = Util.createBreakNodeLocations(".",
                "while-statement.bal", 5);

        List<DebugPoint> debugPoints = new ArrayList<>();
        debugPoints.add(Util.createDebugPoint(".", "while-statement.bal", 5, RESUME, 5));

        ExpectedResults expRes = new ExpectedResults(debugPoints, 5);

        VMDebuggerUtil.startDebug("test-src/debugger/while-statement.bal", breakPoints, expRes);
    }

    @Test(description = "Testing try catch finally scenario for path")
    public void testTryCatchScenarioForPath() {
        BreakPointDTO[] breakPoints = Util.createBreakNodeLocations(".",
                "try-catch-finally.bal", 19);

        String file  = "try-catch-finally.bal";

        List<DebugPoint> debugPoints = new ArrayList<>();
        debugPoints.add(Util.createDebugPoint(".", file, 19, STEP_IN, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 27, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 29, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 31, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 32, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 33, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 34, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 35, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 43, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 44, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 45, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 50, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 55, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 56, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 58, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 60, RESUME, 1));

        ExpectedResults expRes = new ExpectedResults(debugPoints, 16);

        VMDebuggerUtil.startDebug("test-src/debugger/try-catch-finally.bal", breakPoints, expRes);
    }

    @Test(description = "Testing debug paths in workers")
    public void testDebuggingWorkers() {
        BreakPointDTO[] breakPoints = Util.createBreakNodeLocations(".",
                "test-worker.bal", 3, 9, 10, 18, 19, 23, 46);

        String file  = "test-worker.bal";

        List<DebugPoint> debugPoints = new ArrayList<>();
        debugPoints.add(Util.createDebugPoint(".", file, 3, RESUME, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 9, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 10, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 12, STEP_IN, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 31, STEP_OUT, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 13, RESUME, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 18, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 19, RESUME, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 46, RESUME, 5));
        debugPoints.add(Util.createDebugPoint(".", file, 23, RESUME, 1));

        ExpectedResults expRes = new ExpectedResults(debugPoints, 14);

        VMDebuggerUtil.startDebug("test-src/debugger/test-worker.bal", breakPoints, expRes);
    }

    @Test(description = "Testing debug paths in package init")
    public void testDebuggingPackageInit() {
        BreakPointDTO[] breakPoints = Util.createBreakNodeLocations(".",
                "test-package-init.bal", 3, 9);

        String file  = "test-package-init.bal";

        List<DebugPoint> debugPoints = new ArrayList<>();
        debugPoints.add(Util.createDebugPoint(".", file, 3, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 5, STEP_IN, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 14, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 15, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 16, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 20, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 14, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 15, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 16, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 20, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 14, STEP_OVER, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 22, RESUME, 1));
        debugPoints.add(Util.createDebugPoint(".", file, 9, RESUME, 1));

        ExpectedResults expRes = new ExpectedResults(debugPoints, 13);

        VMDebuggerUtil.startDebug("test-src/debugger/test-package-init.bal", breakPoints, expRes);
    }
}
