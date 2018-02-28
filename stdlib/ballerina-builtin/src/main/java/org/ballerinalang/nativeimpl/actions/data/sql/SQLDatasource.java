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
package org.ballerinalang.nativeimpl.actions.data.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.ballerinalang.model.types.BType;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BRefType;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import javax.sql.XADataSource;

/**
 * Native SQL Connector.
 *
 * @since 0.8.0
 */
public class SQLDatasource implements BValue {

    private HikariDataSource hikariDataSource;
    private String databaseName;
    private String connectorId;
    private boolean xaConn;

    public String getDatabaseName() {
        return databaseName;
    }

    public SQLDatasource() {}

    public boolean init(BStruct options, String dbType, String hostOrPath, int port, String username, String password,
            String dbName) {
        buildDataSource(options, dbType, hostOrPath, dbName, port, username, password);
        connectorId = UUID.randomUUID().toString();
        xaConn = isXADataSource();
        try (Connection con = getSQLConnection()) {
            databaseName = con.getMetaData().getDatabaseProductName().toLowerCase(Locale.ENGLISH);
        } catch (SQLException e) {
            throw new BallerinaException(
                    "error in get connection: " + Constants.CONNECTOR_NAME + ": " + e.getMessage(), e);
        }
        return true;
    }

    public Connection getSQLConnection() {
        try {
           return  hikariDataSource.getConnection();
        } catch (SQLException e) {
            throw new BallerinaException(
                    "error in get connection: " + Constants.CONNECTOR_NAME + ": " + e.getMessage(), e);
        }
    }

    public String getConnectorId() {
        return this.connectorId;
    }

    public boolean isXAConnection() {
        return this.xaConn;
    }

    public XADataSource getXADataSource() {
        XADataSource xaDataSource;
        try {
            xaDataSource = hikariDataSource.unwrap(XADataSource.class);
        } catch (SQLException e) {
            throw new BallerinaException("error in get distributed data source");
        }
        return xaDataSource;
    }

    public void closeConnectionPool() {
        hikariDataSource.close();
    }

    private void buildDataSource(BStruct options, String dbType, String hostOrPath, String dbName, int port,
            String username, String password) {
        try {
            HikariConfig config = new HikariConfig();
            config.setUsername(username);
            config.setPassword(password);
            if (options != null) {
                boolean isXA = options.getBooleanField(4) != 0;
                BMap<String, BRefType> dataSourceConfigMap = (BMap) options.getRefField(0);
                String jdbcurl = options.getStringField(0);
                String dataSourceClassName = options.getStringField(1);
                if (!dataSourceClassName.isEmpty()) {
                    config.setDataSourceClassName(dataSourceClassName);
                    dataSourceConfigMap = setDataSourceProperties(dataSourceConfigMap, jdbcurl, username, password,
                            dbType, hostOrPath, port, dbName);
                } else {
                    if (jdbcurl.isEmpty()) {
                        jdbcurl = constructJDBCURL(dbType, hostOrPath, port, dbName, username, password);
                    }
                    config.setJdbcUrl(jdbcurl);
                    if (isXA) {
                        String datasourceClassName = getXADatasourceClassName(dbType, jdbcurl, username, password);
                        config.setDataSourceClassName(datasourceClassName);
                        dataSourceConfigMap = setDataSourceProperties(dataSourceConfigMap, jdbcurl, username, password,
                                dbType, hostOrPath, port, dbName);
                    }
                }
                String connectionTestQuery = options.getStringField(2);
                if (!connectionTestQuery.isEmpty()) {
                    config.setConnectionTestQuery(connectionTestQuery);
                }
                String poolName = options.getStringField(3);
                if (!poolName.isEmpty()) {
                    config.setPoolName(poolName);
                }
                String catalog = options.getStringField(4);
                if (!catalog.isEmpty()) {
                    config.setCatalog(catalog);
                }
                String connectionInitSQL = options.getStringField(5);
                if (!connectionInitSQL.isEmpty()) {
                    config.setConnectionInitSql(connectionInitSQL);
                }
                String driverClassName = options.getStringField(6);
                if (!driverClassName.isEmpty()) {
                    config.setDriverClassName(driverClassName);
                }
                String transactionIsolation = options.getStringField(7);
                if (!transactionIsolation.isEmpty()) {
                    config.setTransactionIsolation(transactionIsolation);
                }
                int maximumPoolSize = (int) options.getIntField(0);
                if (maximumPoolSize != -1) {
                    config.setMaximumPoolSize(maximumPoolSize);
                }
                long connectionTimeout = options.getIntField(1);
                if (connectionTimeout != -1) {
                    config.setConnectionTimeout(connectionTimeout);
                }
                long idleTimeout = options.getIntField(2);
                if (idleTimeout != -1) {
                    config.setIdleTimeout(idleTimeout);
                }
                int minimumIdle = (int) options.getIntField(3);
                if (minimumIdle != -1) {
                    config.setMinimumIdle(minimumIdle);
                }
                long maxLifetime = options.getIntField(4);
                if (maxLifetime != -1) {
                    config.setMaxLifetime(maxLifetime);
                }
                long validationTimeout = options.getIntField(5);
                if (validationTimeout != -1) {
                    config.setValidationTimeout(validationTimeout);
                }
                long leakDetectionThreshold = options.getIntField(6);
                if (leakDetectionThreshold != -1) {
                    config.setLeakDetectionThreshold(leakDetectionThreshold);
                }
                boolean autoCommit = options.getBooleanField(0) != 0;
                config.setAutoCommit(autoCommit);
                boolean isolateInternalQueries = options.getBooleanField(1) != 0;
                config.setIsolateInternalQueries(isolateInternalQueries);
                boolean allowPoolSuspension = options.getBooleanField(2) != 0;
                config.setAllowPoolSuspension(allowPoolSuspension);
                boolean readOnly = options.getBooleanField(3) != 0;
                config.setReadOnly(readOnly);
                if (dataSourceConfigMap != null) {
                    setDataSourceProperties(dataSourceConfigMap, config);
                }
            } else {
                String jdbcurl = constructJDBCURL(dbType, hostOrPath, port, dbName, username, password);
                config.setJdbcUrl(jdbcurl);
            }
            hikariDataSource = new HikariDataSource(config);
        } catch (Throwable t) {
            String errorMessage = "error in sql connector configuration";
            throw new BallerinaException(errorMessage + ": " + t.getMessage());
        }
    }

    private BMap<String, BRefType> setDataSourceProperties(BMap<String, BRefType> dataSourceConfigMap, String jdbcurl,
            String username, String password, String dbType, String hostOrPath, int port, String dbName) {
        if (dataSourceConfigMap != null) {
            if (dataSourceConfigMap.get(Constants.URL) == null) {
                if (jdbcurl.isEmpty()) {
                    jdbcurl = constructJDBCURL(dbType, hostOrPath, port, dbName, username, password);
                }
                dataSourceConfigMap.put(Constants.URL, new BString(jdbcurl));
            }
        } else {
            dataSourceConfigMap = new BMap<>();
            if (jdbcurl.isEmpty()) {
                jdbcurl = constructJDBCURL(dbType, hostOrPath, port, dbName, username, password);
            }
            dataSourceConfigMap.put(Constants.URL, new BString(jdbcurl));
        }
        dataSourceConfigMap.put(Constants.USER, new BString(username));
        dataSourceConfigMap.put(Constants.PASSWORD, new BString(password));
        return dataSourceConfigMap;
    }

    private String constructJDBCURL(String dbType, String hostOrPath, int port, String dbName, String username,
            String password) {
        StringBuilder jdbcUrl = new StringBuilder();
        dbType = dbType.toUpperCase(Locale.ENGLISH);
        hostOrPath = hostOrPath.replaceAll("/$", "");
        switch (dbType) {
        case Constants.DBTypes.MYSQL:
            if (port <= 0) {
                port = Constants.DefaultPort.MYSQL;
            }
            jdbcUrl.append("jdbc:mysql://").append(hostOrPath).append(":").append(port).append("/").append(dbName);
            break;
        case Constants.DBTypes.SQLSERVER:
            if (port <= 0) {
                port = Constants.DefaultPort.SQLSERVER;
            }
            jdbcUrl.append("jdbc:sqlserver://").append(hostOrPath).append(":").append(port).append(";databaseName=")
                    .append(dbName);
            break;
        case Constants.DBTypes.ORACLE:
            if (port <= 0) {
                port = Constants.DefaultPort.ORACLE;
            }
            jdbcUrl.append("jdbc:oracle:thin:").append(username).append("/").append(password).append("@")
                    .append(hostOrPath).append(":").append(port).append("/").append(dbName);
            break;
        case Constants.DBTypes.SYBASE:
            if (port <= 0) {
                port = Constants.DefaultPort.SYBASE;
            }
            jdbcUrl.append("jdbc:sybase:Tds:").append(hostOrPath).append(":").append(port).append("/").append(dbName);
            break;
        case Constants.DBTypes.POSTGRES:
            if (port <= 0) {
                port = Constants.DefaultPort.POSTGRES;
            }
            jdbcUrl.append("jdbc:postgresql://").append(hostOrPath).append(":").append(port).append("/").append(dbName);
            break;
        case Constants.DBTypes.IBMDB2:
            if (port <= 0) {
                port = Constants.DefaultPort.IBMDB2;
            }
            jdbcUrl.append("jdbc:db2:").append(hostOrPath).append(":").append(port).append("/").append(dbName);
            break;
        case Constants.DBTypes.HSQLDB_SERVER:
            if (port <= 0) {
                port = Constants.DefaultPort.HSQLDB_SERVER;
            }
            jdbcUrl.append("jdbc:hsqldb:hsql://").append(hostOrPath).append(":").append(port).append("/")
                    .append(dbName);
            break;
        case Constants.DBTypes.HSQLDB_FILE:
            jdbcUrl.append("jdbc:hsqldb:file:").append(hostOrPath).append(File.separator).append(dbName);
            break;
        case Constants.DBTypes.H2_SERVER:
            if (port <= 0) {
                port = Constants.DefaultPort.H2_SERVER;
            }
            jdbcUrl.append("jdbc:h2:tcp:").append(hostOrPath).append(":").append(port).append("/").append(dbName);
            break;
        case Constants.DBTypes.H2_FILE:
            jdbcUrl.append("jdbc:h2:file:").append(hostOrPath).append(File.separator).append(dbName);
            break;
        case Constants.DBTypes.H2_MEM:
            jdbcUrl.append("jdbc:h2:mem:").append(dbName);
            break;
        case Constants.DBTypes.DERBY_SERVER:
            if (port <= 0) {
                port = Constants.DefaultPort.DERBY_SERVER;
            }
            jdbcUrl.append("jdbc:derby:").append(hostOrPath).append(":").append(port).append("/").append(dbName);
            break;
        case Constants.DBTypes.DERBY_FILE:
            jdbcUrl.append("jdbc:derby:").append(hostOrPath).append(File.separator).append(dbName);
            break;
        default:
            throw new BallerinaException("cannot generate url for unknown database type : " + dbType);
        }
        return jdbcUrl.toString();
    }

    private String getXADatasourceClassName(String dbType, String url, String userName, String password) {
        String xaDataSource = null;
        switch (dbType) {
        case Constants.DBTypes.MYSQL:
            int driverMajorVersion;
            try (Connection conn = DriverManager.getConnection(url, userName, password)) {
                driverMajorVersion = conn.getMetaData().getDriverMajorVersion();
                if (driverMajorVersion == 5) {
                    xaDataSource = Constants.XADataSources.MYSQL_5_XA_DATASOURCE;
                } else if (driverMajorVersion > 5) {
                    xaDataSource = Constants.XADataSources.MYSQL_6_XA_DATASOURCE;
                }
            } catch (SQLException e) {
                throw new BallerinaException(
                        "error in get connection: " + Constants.CONNECTOR_NAME + ": " + e.getMessage(), e);
            }
            break;
        case Constants.DBTypes.SQLSERVER:
            xaDataSource = Constants.XADataSources.SQLSERVER_XA_DATASOURCE;
            break;
        case Constants.DBTypes.ORACLE:
            xaDataSource = Constants.XADataSources.ORACLE_XA_DATASOURCE;
            break;
        case Constants.DBTypes.SYBASE:
            xaDataSource = Constants.XADataSources.SYBASE_XA_DATASOURCE;
            break;
        case Constants.DBTypes.POSTGRES:
            xaDataSource = Constants.XADataSources.POSTGRES_XA_DATASOURCE;
            break;
        case Constants.DBTypes.IBMDB2:
            xaDataSource = Constants.XADataSources.IBMDB2_XA_DATASOURCE;
            break;
        case Constants.DBTypes.HSQLDB_SERVER:
        case Constants.DBTypes.HSQLDB_FILE:
            xaDataSource = Constants.XADataSources.HSQLDB_XA_DATASOURCE;
            break;
        case Constants.DBTypes.H2_SERVER:
        case Constants.DBTypes.H2_FILE:
        case Constants.DBTypes.H2_MEM:
            xaDataSource = Constants.XADataSources.H2_XA_DATASOURCE;
            break;
        case Constants.DBTypes.DERBY_SERVER:
            xaDataSource = Constants.XADataSources.DERBY_SERVER_XA_DATASOURCE;
            break;
        case Constants.DBTypes.DERBY_FILE:
            xaDataSource = Constants.XADataSources.DERBY_FILE_XA_DATASOURCE;
            break;
        default:
            throw new BallerinaException("unknown database type used for xa connection : " + dbType);
        }
        return xaDataSource;
    }


    private void setDataSourceProperties(BMap options, HikariConfig config) {
        Set<String> keySet = options.keySet();
        for (String key : keySet) {
            BValue value = options.get(key);
            if (value instanceof BString) {
                config.addDataSourceProperty(key, value.stringValue());
            } else if (value instanceof BInteger) {
                config.addDataSourceProperty(key, ((BInteger) value).intValue());
            } else if (value instanceof BBoolean) {
                config.addDataSourceProperty(key, ((BBoolean) value).booleanValue());
            } else if (value instanceof BFloat) {
                config.addDataSourceProperty(key, ((BFloat) value).floatValue());
            }
        }
    }

    @Override
    public String stringValue() {
        return null;
    }

    @Override
    public BType getType() {
        return null;
    }

    @Override
    public BValue copy() {
        return null;
    }
    
    private boolean isXADataSource() {
        try {
            return hikariDataSource.isWrapperFor(XADataSource.class);
        } catch (SQLException e) {
            throw new BallerinaException("error in check distributed data source: " + e.getCause().getMessage());
        }
    }
}
