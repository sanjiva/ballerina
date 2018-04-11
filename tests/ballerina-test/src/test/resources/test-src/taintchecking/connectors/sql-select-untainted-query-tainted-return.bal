import ballerina/mysql;

type Employee {
    int id;
    string name;
};

public function main (string[] args) {
    testSelectWithUntaintedQueryProducingTaintedReturn(args);
}

public function testSelectWithUntaintedQueryProducingTaintedReturn(string[] args) {
    endpoint mysql:Client testDB {
        host: "localhost",
        port: 3306,
        name: "testdb",
        username: "root",
        password: "root",
        poolOptions: {maximumPoolSize:5}
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
        error => return;
    }
    var closeStatus = testDB -> close();
    return;
}

public function testFunction (string anyValue) {

}

