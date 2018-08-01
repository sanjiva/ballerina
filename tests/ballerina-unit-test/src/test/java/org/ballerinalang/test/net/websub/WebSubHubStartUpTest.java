package org.ballerinalang.test.net.websub;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Class to test WebSub Hub startup.
 */
public class WebSubHubStartUpTest {

    private static final String HUB_URL_FIELD = "hubUrl";
    private static final String STARTED_UP_HUB_FIELD = "startedUpHub";

    private CompileResult result;
    private int port = 9191;
    private BMap<String, BValue> hubStartUpObject = null;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/net/websub/hub_startup_test.bal");
    }

    @Test(description = "Test hub start up and URL identification")
    public void testHubStartUp() {
        BValue[] returns = BRunUtil.invoke(result, "startupHub", new BValue[]{new BInteger(port)});
        Assert.assertEquals(returns.length, 1);
        Assert.assertTrue(returns[0] instanceof BMap);
        hubStartUpObject = (BMap<String, BValue>) returns[0];
        Assert.assertEquals(hubStartUpObject.get(HUB_URL_FIELD).stringValue(),
                            "http://localhost:" + port + "/websub/hub");
    }

    @Test(description = "Test hub start up call when already started", dependsOnMethods = "testHubStartUp")
    public void testHubStartUpWhenStarted() {
        BValue[] returns = BRunUtil.invoke(result, "startupHub", new BValue[]{new BInteger(9292)});
        Assert.assertEquals(returns.length, 1);
        Assert.assertTrue(returns[0] instanceof BMap);
        hubStartUpObject = (BMap<String, BValue>) returns[0];
        Assert.assertEquals(hubStartUpObject.get("message").stringValue(), "Ballerina Hub already started up");
        Assert.assertTrue(hubStartUpObject.get(STARTED_UP_HUB_FIELD) instanceof BMap);
        BMap<String, BValue> hubObject = (BMap<String, BValue>) hubStartUpObject.get(STARTED_UP_HUB_FIELD);
        Assert.assertEquals(hubObject.get(HUB_URL_FIELD).stringValue(), "http://localhost:" + port + "/websub/hub");
    }

    @Test(description = "Test shut down and restart", dependsOnMethods = "testHubStartUpWhenStarted")
    public void testHubShutdownAndStart() {
        int port = 9393;
        BValue[] returns = BRunUtil.invoke(result, "stopHub", new BValue[]{hubStartUpObject});
        Assert.assertEquals(returns.length, 1);
        Assert.assertTrue(returns[0] instanceof BBoolean);
        Assert.assertTrue((((BBoolean) returns[0]).value()));

        returns = BRunUtil.invoke(result, "startupHub", new BValue[]{new BInteger(port)});
        Assert.assertEquals(returns.length, 1);
        Assert.assertTrue(returns[0] instanceof BMap);
        hubStartUpObject = (BMap<String, BValue>) returns[0];
        Assert.assertEquals(hubStartUpObject.get(HUB_URL_FIELD).stringValue(),
                            "http://localhost:" + port + "/websub/hub");
    }

    @AfterClass
    public void tearDown() {
        BRunUtil.invoke(result, "stopHub", new BValue[]{hubStartUpObject});
    }

}
