import ballerina.data.sql;

struct ResultCustomers {
    string FIRSTNAME;
}

function testConnectionPoolProperties () (string firstName) {
    endpoint<sql:ClientConnector> testDB {}
    sql:ConnectionProperties properties = {url:"jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR",
                                              driverClassName:"org.hsqldb.jdbc.JDBCDriver", maximumPoolSize:1,
                                              idleTimeout:600000, connectionTimeout:30000, autoCommit:true, maxLifetime:1800000,
                                              minimumIdle:1, poolName:"testHSQLPool", isolateInternalQueries:false,
                                              allowPoolSuspension:false, readOnly:false, validationTimeout:5000, leakDetectionThreshold:0,
                                              connectionInitSql:"SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS",
                                              transactionIsolation:"TRANSACTION_READ_COMMITTED", catalog:"PUBLIC",
                                              connectionTestQuery:"SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS"};
    bind create sql:ClientConnector(sql:HSQLDB_FILE, "", 0, "", "SA", "", properties) with testDB;

    sql:Parameter[] parameters = [];
    datatable dt = testDB.select ("SELECT  FirstName from Customers where registrationID = 1", parameters);
    TypeCastError err;
    ResultCustomers rs;
    while (dt.hasNext()) {
        any dataStruct = dt.getNext();
        rs, err = (ResultCustomers) dataStruct;
        firstName = rs.FIRSTNAME;
    }
    testDB.close ();
    return;
}

function testConnectorWithDefaultPropertiesForListedDB () (string firstName) {
    endpoint<sql:ClientConnector> testDB {
        create sql:ClientConnector(sql:HSQLDB_FILE, "./target/tempdb/", 0,
                                                            "TEST_SQL_CONNECTOR", "SA", "", null);
    }

    sql:Parameter[] parameters = [];
    datatable dt = testDB.select("SELECT  FirstName from Customers where registrationID = 1", parameters);
    TypeCastError err;
    ResultCustomers rs;
    while (dt.hasNext()) {
        any dataStruct = dt.getNext();
        rs, err = (ResultCustomers)dataStruct;
        firstName = rs.FIRSTNAME;
    }
    testDB.close();
    return;
}


function testConnectorWithDirectUrl () (string firstName) {
    endpoint<sql:ClientConnector> testDB {}
    sql:ConnectionProperties Properties = {url:"jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR"};
    bind create sql:ClientConnector("", "", 0, "", "SA", "", Properties) with testDB;

    sql:Parameter[] parameters = [];
    datatable dt = testDB.select("SELECT  FirstName from Customers where registrationID = 1", parameters);
    TypeCastError err;
    ResultCustomers rs;
    while (dt.hasNext()) {
        any dataStruct = dt.getNext();
        rs, err = (ResultCustomers)dataStruct;
        firstName = rs.FIRSTNAME;
    }
    testDB.close();
    return;
}

function testConnectorWithDataSourceClass () (string firstName) {
    endpoint<sql:ClientConnector> testDB {}
    map propertiesMap = {"loginTimeout":109, "url":"jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR"};
    sql:ConnectionProperties properties = {dataSourceClassName:"org.hsqldb.jdbc.JDBCDataSource",
                                              datasourceProperties:propertiesMap};
    bind create sql:ClientConnector("", "", 0, "", "SA", "", properties) with testDB;

    sql:Parameter[] parameters = [];
    datatable dt = testDB.select("SELECT  FirstName from Customers where registrationID = 1", parameters);
    TypeCastError err;
    ResultCustomers rs;
    while (dt.hasNext()) {
        any dataStruct = dt.getNext();
        rs, err = (ResultCustomers)dataStruct;
        firstName = rs.FIRSTNAME;
    }
    testDB.close();
    return;
}

function testConnectorWithDataSourceClassWithoutURL () (string firstName) {
    endpoint<sql:ClientConnector> testDB {}
    sql:ConnectionProperties properties = {dataSourceClassName:"org.hsqldb.jdbc.JDBCDataSource"};
    bind create sql:ClientConnector(sql:HSQLDB_FILE, "./target/tempdb/", 0,
                                                            "TEST_SQL_CONNECTOR", "SA", "", properties) with testDB;

    sql:Parameter[] parameters = [];
    datatable dt = testDB.select("SELECT  FirstName from Customers where registrationID = 1", parameters);
    TypeCastError err;
    ResultCustomers rs;
    while (dt.hasNext()) {
        any dataStruct = dt.getNext();
        rs, err = (ResultCustomers)dataStruct;
        firstName = rs.FIRSTNAME;
    }
    testDB.close();
    return;
}

function testConnectorWithDataSourceClassURLPriority () (string firstName) {
    endpoint<sql:ClientConnector> testDB {}
    map propertiesMap = {"url":"jdbc:hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR"};
    sql:ConnectionProperties properties = {dataSourceClassName:"org.hsqldb.jdbc.JDBCDataSource",
                                              datasourceProperties:propertiesMap};
    bind create sql:ClientConnector(sql:HSQLDB_FILE, "./target/tempdb/", 0,
                                        "INVALID_DB_NAME", "SA", "", properties) with testDB;

    sql:Parameter[] parameters = [];
    datatable dt = testDB.select("SELECT  FirstName from Customers where registrationID = 1", parameters);
    TypeCastError err;
    ResultCustomers rs;
    while (dt.hasNext()) {
        any dataStruct = dt.getNext();
        rs, err = (ResultCustomers)dataStruct;
        firstName = rs.FIRSTNAME;
    }
    testDB.close();
    return;
}

function testInvalidDBType () (string firstName) {
    endpoint<sql:ClientConnector> testDB {
        create sql:ClientConnector("TESTDB", "./target/tempdb/",
                                                            0, "TEST_SQL_CONNECTOR", "SA", "", {maximumPoolSize:1});
    }

    sql:Parameter[] parameters = [];
    _ = testDB.update("Insert into Customers(firstName) values ('James')", parameters);
    testDB.close();
    return;
}