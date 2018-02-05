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
package org.ballerinalang.test.utils;

import org.ballerinalang.launcher.util.BCompileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Util class for SQL DB Tests.
 *
 * @since 0.8.0
 */
public class SQLDBUtils {

    public static final String DB_DIRECTORY = "./target/tempdb/";
    public static final String DB_DIRECTORY_H2_1 = "./target/H2_1/";
    public static final String DB_DIRECTORY_H2_2 = "./target/H2_2/";

    /**
     * Create HyperSQL DB with the given name and initialize with given SQL file.
     *
     * @param dbDirectory Name of the DB directory.
     * @param dbName  Name of the DB instance.
     * @param sqlFile SQL statements for initialization.
     */
    public static void initDatabase(String dbDirectory, String dbName, String sqlFile) {
        Connection connection = null;
        Statement st = null;
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            String jdbcURL = "jdbc:hsqldb:file:" + dbDirectory + dbName;
            connection = DriverManager.getConnection(jdbcURL, "SA", "");
            String sql = readFileToString(sqlFile);
            String[] sqlQuery = sql.trim().split("/");
            st = connection.createStatement();
            for (String query : sqlQuery) {
                st.executeUpdate(query.trim());
            }
        } catch (ClassNotFoundException | SQLException e) {
            //Do nothing
        } finally {
            releaseResources(connection, st);
        }
    }

    /**
     * Create H2 DB with the given name and initialize with given SQL file.
     *
     * @param dbDirectory Name of the DB directory.
     * @param dbName  Name of the DB instance.
     * @param sqlFile SQL statements for initialization.
     */
    public static void initH2Database(String dbDirectory, String dbName, String sqlFile) {
        Connection connection = null;
        Statement st = null;
        try {
            Class.forName("org.h2.Driver");
            String jdbcURL = "jdbc:h2:file:" + dbDirectory + dbName;
            connection = DriverManager.getConnection(jdbcURL, "sa", "");
            String sql = readFileToString(sqlFile);
            String[] sqlQuery = sql.trim().split("/");
            st = connection.createStatement();
            for (String query : sqlQuery) {
                st.executeUpdate(query.trim());
            }
            connection.commit();
        } catch (ClassNotFoundException | SQLException e) {
            //Do nothing
        } finally {
            releaseResources(connection, st);
        }
    }


    /**
     * Delete the given directory along with all files and sub directories.
     *
     * @param directory Directory to delete.
     */
    public static boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            for (File f : directory.listFiles()) {
                boolean success = deleteDirectory(f);
                if (!success) {
                    return false;
                }
            }
        }
        return directory.delete();
    }

    /**
     * Delete all the files and sub directories which matches given prefix in a given directory.
     *
     * @param directory Directory which contains files to delete.
     * @param affix    Affix for finding the matching files to delete.
     */
    public static void deleteFiles(File directory, String affix) {
        if (directory.isDirectory()) {
            for (File f : directory.listFiles()) {
                if (f.getName().startsWith(affix) || f.getName().endsWith(affix)) {
                    deleteDirectory(f);
                }
            }
        }
    }

    private static String readFileToString(String path) {
        InputStream is = null;
        String fileAsString = null;
        URL fileResource = BCompileUtil.class.getClassLoader().getResource(path);
        try {
            is = new FileInputStream(fileResource.getFile());
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
            fileAsString = sb.toString();
        } catch (IOException e) {
            // Ignore here
        }
        return fileAsString;
    }

    private static void releaseResources(Connection connection, Statement st) {
        try {
            if (st != null) {
                st.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            //Do nothing
        }
    }
}
