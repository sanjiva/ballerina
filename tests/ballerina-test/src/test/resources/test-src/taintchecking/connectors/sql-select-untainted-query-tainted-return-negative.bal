import ballerina/sql;

type Employee {
    int id;
    string name;
};

public function main (string[] args) {
    testSelectWithUntaintedQueryProducingTaintedReturnNegative(args);
}

public function testSelectWithUntaintedQueryProducingTaintedReturnNegative(string[] args) {
    endpoint sql:Client testDB {
        database: sql:DB_MYSQL,
        host: "localhost",
        port: 3306,
        name: "testdb",
        username: "root",
        password: "root",
        options: {maximumPoolSize:5}
    };

    var output = testDB -> select("SELECT  FirstName from Customers where registrationID = 1", null, null);
    match output {
	table dt => {
            while (dt.hasNext()) {
                var rs = <Employee>dt.getNext();
                match rs {
                    Employee emp => testFunction(emp.name);
                    error => return;
                }
            }
	}
        sql:SQLConnectorError => return;
    }
    var closeStatus = testDB -> close();
    return;
}

public function testFunction (@sensitive string anyValue) {

}

