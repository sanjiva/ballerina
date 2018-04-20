import ballerina/sql;
import ballerina/io;

type ResultCustomers {
    string FIRSTNAME,
};

type Person {
    int id,
    string name,
};

type ResultCustomers2 {
    string FIRSTNAME,
    string LASTNAME,
};

function testSelectData() returns (string) {
    endpoint sql:Client testDB {
        url:"hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR",
        username:"SA",
        poolOptions:{maximumPoolSize:1}
    };
    string returnData;
    try {
        var x = testDB->select("SELECT Name from Customers where registrationID = 1", (), ());

        match x {
            table dt => {
                var j = check <json>dt;
                returnData = io:sprintf("%j", j);
            }
            error err1 => {
                returnData = err1.message;
            }
        }

    } finally {
        _ = testDB->close();
    }
    return returnData;
}

function testGeneratedKeyOnInsert() returns (string) {
    endpoint sql:Client testDB {
        url:"hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR",
        username:"SA",
        poolOptions:{maximumPoolSize:1}
    };

    string id = "";
    try {
        string[] generatedID;
        int insertCount;
        var x = testDB->updateWithGeneratedKeys("insert into Customers (name,lastName,
                             registrationID,creditLimit,country) values ('Mary', 'Williams', 3, 5000.75, 'USA')", ());

        match x {
            (int, string[])=> {
                id = generatedID[0];
            }
            error err1 => {
                id = err1.message;
            }
        }

    } finally {
        _ = testDB->close();
    }
    return id;
}

function testCallProcedure() returns (string) {
    endpoint sql:Client testDB {
        url:"hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR",
        username:"SA",
        poolOptions:{maximumPoolSize:1}
    };
    string returnData;
    try {
        var x = testDB->call("{call InsertPersonDataInfo(100,'James')}", (), ());
        match x {
            table[] dt => {
                var j = check <json>dt[0];
                returnData = io:sprintf("%j", j);
            }
            error err1 => {
                returnData = err1.message;
            }
        }
    } finally {
        _ = testDB->close();
    }
    return returnData;
}

function testBatchUpdate() returns (string) {
    endpoint sql:Client testDB {
        url:"hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR",
        username:"SA",
        poolOptions:{maximumPoolSize:1}
    };

    int[] updateCount;
    string returnVal;
    try {
        //Batch 1
        sql:Parameter para1 = (sql:TYPE_VARCHAR, "Alex");
        sql:Parameter para2 = (sql:TYPE_VARCHAR, "Smith");
        sql:Parameter para3 = (sql:TYPE_INTEGER, 20);
        sql:Parameter para4 = (sql:TYPE_DOUBLE, 3400.5);
        sql:Parameter para5 = (sql:TYPE_VARCHAR, "Colombo");
        sql:Parameter[] parameters1 = [para1, para2, para3, para4, para5];

        //Batch 2
        para1 = (sql:TYPE_VARCHAR, "Alex");
        para2 = (sql:TYPE_VARCHAR, "Smith");
        para3 = (sql:TYPE_INTEGER, 20);
        para4 = (sql:TYPE_DOUBLE, 3400.5);
        para5 = (sql:TYPE_VARCHAR, "Colombo");
        sql:Parameter[] parameters2 = [para1, para2, para3, para4, para5];

        var x = testDB->batchUpdate("Insert into CustData (firstName,lastName,registrationID,creditLimit,country)
                                     values (?,?,?,?,?)", parameters1, parameters2);
        match x {
            int[] data => {
                updateCount = data;
                returnVal = "success";
            }
            error err1 => {
                returnVal = err1.message;
            }
        }
    } finally {
        _ = testDB->close();
    }
    return returnVal;
}

function testInvalidArrayofQueryParameters() returns (string) {
    endpoint sql:Client testDB {
        url:"hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR",
        username:"SA",
        poolOptions:{maximumPoolSize:1}
    };

    string returnData;
    try {
        xml x1 = xml `<book>The Lost World</book>`;
        xml x2 = xml `<book>The Lost World2</book>`;
        xml[] xmlDataArray = [x1, x2];
        sql:Parameter para0 = (sql:TYPE_INTEGER, xmlDataArray);
        var x = testDB->select("SELECT FirstName from Customers where registrationID in (?)", (), para0);

        match x {
            table dt => {
                var j = check <json>dt;
                returnData = io:sprintf("%j", j);
            }
            error err1 => {
                returnData = err1.message;
            }
        }

    } finally {
        _ = testDB->close();
    }
    return returnData;
}

function testCallProcedureWithMultipleResultSetsAndLowerConstraintCount() returns ((string, string)|error) {
    endpoint sql:Client testDB {
        url:"hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR",
        username:"SA",
        poolOptions:{maximumPoolSize:1}
    };

    var dtsRet = testDB->call("{call SelectPersonDataMultiple()}", [ResultCustomers]);

    match dtsRet {
        table[] dts => {
            string firstName1;
            string firstName2;

            while (dts[0].hasNext()) {
                ResultCustomers rs = check <ResultCustomers>dts[0].getNext();
                firstName1 = rs.FIRSTNAME;
            }

            while (dts[1].hasNext()) {
                ResultCustomers rs = check <ResultCustomers>dts[1].getNext();
                firstName2 = rs.FIRSTNAME;
            }

            _ = testDB->close();
            return (firstName1, firstName2);
        }
        error e => {
            _ = testDB->close();
            return e;
        }
    }
}

function testCallProcedureWithMultipleResultSetsAndHigherConstraintCount() returns ((string, string)|error) {
    endpoint sql:Client testDB {
        url:"hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR",
        username:"SA",
        poolOptions:{maximumPoolSize:1}
    };

    var dtsRet = testDB->call("{call SelectPersonDataMultiple()}", [ResultCustomers, ResultCustomers2, Person]);

    match dtsRet {
        table[] dts => {
            string firstName1;
            string firstName2;

            while (dts[0].hasNext()) {
                ResultCustomers rs = check <ResultCustomers>dts[0].getNext();
                firstName1 = rs.FIRSTNAME;
            }

            while (dts[1].hasNext()) {
                ResultCustomers rs = check <ResultCustomers>dts[1].getNext();
                firstName2 = rs.FIRSTNAME;
            }

            _ = testDB->close();
            return (firstName1, firstName2);
        }
        error e => {
            _ = testDB->close();
            return e;
        }
    }
}

function testCallProcedureWithMultipleResultSetsAndNilConstraintCount() returns ((string, string)|error) {
    endpoint sql:Client testDB {
        url:"hsqldb:file:./target/tempdb/TEST_SQL_CONNECTOR",
        username:"SA",
        poolOptions:{maximumPoolSize:1}
    };

    var dtsRet = testDB->call("{call SelectPersonDataMultiple()}", ());

    match dtsRet {
        table[] dts => {
            string firstName1;
            string firstName2;

            while (dts[0].hasNext()) {
                ResultCustomers rs = check <ResultCustomers>dts[0].getNext();
                firstName1 = rs.FIRSTNAME;
            }

            while (dts[1].hasNext()) {
                ResultCustomers rs = check <ResultCustomers>dts[1].getNext();
                firstName2 = rs.FIRSTNAME;
            }

            return (firstName1, firstName2);
        }
        error e => {
            _ = testDB->close();
            return e;
        }
    }
}
