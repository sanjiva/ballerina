/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.test.data.streaming;

import org.ballerinalang.test.context.ServerInstance;
import org.ballerinalang.test.util.HttpClientRequest;
import org.ballerinalang.test.util.HttpResponse;
import org.ballerinalang.test.util.SQLDBUtils;
import org.ballerinalang.test.util.SQLDBUtils.FileBasedTestDatabase;
import org.ballerinalang.test.util.SQLDBUtils.TestDatabase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * This test case tests the scenario of streaming the data from a table converted to XML.
 */
public class TableToXMLStreamingTestCase {
    private ServerInstance ballerinaServer;
    private TestDatabase testDatabase;
    private static final String DB_DIRECTORY = "./target/tempdb/";

    @BeforeClass
    private void setup() throws Exception {
        setUpDatabase();
        ballerinaServer = ServerInstance.initBallerinaServer();
        String balFile = Paths.get("src", "test", "resources", "data", "streaming", "xml_streaming_test.bal")
                .toAbsolutePath().toString();
        Map<String, String> envProperties = new HashMap<>(1);
        envProperties.put("JAVA_OPTS", "-Xms100m -Xmx100m");
        ballerinaServer.startBallerinaServer(balFile, envProperties);
    }

    private void setUpDatabase() throws SQLException {
        testDatabase = new FileBasedTestDatabase(SQLDBUtils.DBType.H2,
                "data/streaming/datafiles/streaming_test_data.sql", DB_DIRECTORY, "STREAMING_XML_TEST_DB");
        insertDummyData(testDatabase.getJDBCUrl(), testDatabase.getUsername(), testDatabase.getPassword());
    }

    @Test(description = "Tests streaming a large amount of data from a table, converted to XML")
    public void testStreamingLargeXML() throws Exception {
        HttpResponse response = HttpClientRequest
                .doGet(ballerinaServer.getServiceURLHttp("dataService/getData"), 60000, responseBuilder);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponseCode(), 200);
        Assert.assertEquals(Integer.parseInt(response.getData()), 211288909);
    }

    @AfterClass(alwaysRun = true)
    private void cleanup() throws Exception {
        ballerinaServer.stopServer();
        if (testDatabase != null) {
            testDatabase.stop();
        }
    }

    // This inserts about 212MB of data to the database.
    private static void insertDummyData(String jdbcUrl, String userName, String password) throws SQLException {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, userName, password)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 1024; i++) {
                stringBuilder.append("x");
            }
            String strDataEntry = stringBuilder.toString();
            String query = "INSERT INTO Data VALUES (?, ?, ?);";
            conn.setAutoCommit(true);
            PreparedStatement ps = conn.prepareStatement(query);
            for (int i = 0; i < 100000; i++) {
                ps.setInt(1, i);
                ps.setString(2, strDataEntry);
                ps.setString(3, strDataEntry);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * This reads a buffered stream and returns the number of characters.
     */
    private static HttpClientRequest.CheckedFunction<BufferedReader, String> responseBuilder = ((bufferedReader) -> {
        int count = 0;
        while (bufferedReader.read() != -1) {
            count++;
        }
        return String.valueOf(count);
    });
}
