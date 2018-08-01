import ballerina/jdbc;

type Person record {
    int id;
    int age;
    float salary;
    string name;
};

type ResultCount record {
    int COUNTVAL;
};

type Employee record {
    int id;
    string name;
    float salary;
};

type EmployeeCompatible record {
    int id;
    string name;
    float salary;
};

type EmployeeSalary record {
    int id;
    float salary;
};

type EmployeeSalaryCompatible record {
    int id;
    float salary;
};

int idValue = -1;
int ageValue = -1;
float salValue = -1;
string nameValue = "";

function testForEachInTableWithStmt() returns (int, int, float, string) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person where id = 1", Person);

    int id;
    int age;
    float salary;
    string name;

    foreach x in dt {
        id = x.id;
        age = x.age;
        salary = x.salary;
        name = x.name;
    }
    testDB.stop();
    return (id, age, salary, name);
}

function testForEachInTable() returns (int, int, float, string) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person where id = 1", Person);

    dt.foreach((Person p) => {
            idValue = untaint p.id;
            ageValue = untaint p.age;
            salValue = untaint p.salary;
            nameValue = untaint p.name;
        }
    );
    int id = idValue;
    int age = ageValue;
    float salary = salValue;
    string name = nameValue;
    testDB.stop();
    return (id, age, salary, name);
}

function testCountInTable() returns (int) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person where id < 10", Person);
    int count = dt.count();
    testDB.stop();
    return count;
}

function testFilterTable() returns (int, int, int) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person", Person);
    Person[] personBelow35 = dt.filter(isBellow35);
    int count = lengthof personBelow35;
    int id1 = personBelow35[0].id;
    int id2 = personBelow35[1].id;
    testDB.stop();
    return (count, id1, id2);
}

function testFilterWithAnnonymousFuncOnTable() returns (int, int, int) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person", Person);
    Person[] personBelow35 = dt.filter((Person p) => (boolean) {
            return p.age < 35;
        });
    int count = lengthof personBelow35;
    int id1 = personBelow35[0].id;
    int id2 = personBelow35[1].id;
    testDB.stop();
    return (count, id1, id2);
}

function testFilterTableWithCount() returns (int) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person", Person);
    int count = dt.filter(isBellow35).count();
    testDB.stop();
    return count;
}

function testMapTable() returns (string[]) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person order by id", Person);
    string[] names = dt.map(getName);
    testDB.stop();
    return names;
}

function testMapWithFilterTable() returns (string[]) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person order by id", Person);
    string[] names = dt.map(getName).filter(isGeraterThan4String);
    testDB.stop();
    return names;
}

function testFilterWithMapTable() returns (string[]) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person order by id", Person);
    string[] names = dt.filter(isGeraterThan4).map(getName);
    testDB.stop();
    return names;
}

function testFilterWithMapAndCountTable() returns (int) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person order by id", Person);
    int count = dt.filter(isGeraterThan4).map(getName).count();
    testDB.stop();
    return count;
}

function testAverageWithTable() returns (float) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person order by id", Person);
    float avgSal = dt.map(getSalary).average();
    testDB.stop();
    return avgSal;
}

function testMinWithTable() returns (float) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person order by id", Person);
    float avgSal = dt.map(getSalary).min();
    testDB.stop();
    return avgSal;
}

function testMaxWithTable() returns (float) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person order by id", Person);
    float avgSal = dt.map(getSalary).max();
    testDB.stop();
    return avgSal;
}

function testSumWithTable() returns (float) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT * from Person order by id", Person);
    float avgSal = dt.map(getSalary).sum();
    testDB.stop();
    return avgSal;
}

function testCloseConnectionPool() returns (int) {
    endpoint jdbc:Client testDB {
        url: "jdbc:hsqldb:file:./target/tempdb/TEST_DATA_TABLE__ITR_DB",
        username: "SA",
        poolOptions: { maximumPoolSize: 1 }
    };

    table<Person> dt = check testDB->select("SELECT COUNT(*) as countVal FROM INFORMATION_SCHEMA.SYSTEM_SESSIONS",
        ResultCount);
    int count;
    while (dt.hasNext()) {
        var rs = check <ResultCount>dt.getNext();
        count = rs.COUNTVAL;
    }
    testDB.stop();
    return count;
}

function testSelect() returns (json) {

    table<Employee> dt = createTable();

    table<EmployeeSalary> salaryTable = dt.select(getEmployeeSalary);
    return check <json>salaryTable;
}

function testSelectCompatibleLambdaInput() returns (json) {
    table<Employee> dt = createTable();

    table<EmployeeSalary> salaryTable = dt.select(getEmployeeSalaryCompatibleInput);
    return check <json>salaryTable;
}

function testSelectCompatibleLambdaOutput() returns (json) {
    table<Employee> dt = createTable();

    table<EmployeeSalary> salaryTable = dt.select(getEmployeeSalaryCompatibleOutput);
    return check <json>salaryTable;
}

function testSelectCompatibleLambdaInputOutput() returns (json) {
    table<Employee> dt = createTable();

    table<EmployeeSalary> salaryTable = dt.select(getEmployeeSalaryCompatibleInputOutput);
    return check <json>salaryTable;
}

function getEmployeeSalary(Employee e) returns (EmployeeSalary) {
    EmployeeSalary s = { id: e.id, salary: e.salary };
    return s;
}

function getEmployeeSalaryCompatibleInput(EmployeeCompatible e) returns (EmployeeSalary) {
    EmployeeSalary s = { id: e.id, salary: e.salary };
    return s;
}

function getEmployeeSalaryCompatibleOutput(Employee e) returns (EmployeeSalaryCompatible) {
    EmployeeSalaryCompatible s = { id: e.id, salary: e.salary };
    return s;
}

function getEmployeeSalaryCompatibleInputOutput(EmployeeCompatible e) returns (EmployeeSalaryCompatible) {
    EmployeeSalaryCompatible s = { id: e.id, salary: e.salary };
    return s;
}

function createTable() returns (table<Employee>) {
    table<Employee> dt = table{};

    Employee e1 = { id: 1, name: "A", salary: 100 };
    Employee e2 = { id: 2, name: "B", salary: 200 };
    Employee e3 = { id: 3, name: "C", salary: 300 };

    _ = dt.add(e1);
    _ = dt.add(e2);
    _ = dt.add(e3);

    return dt;
}

function isBellow35(Person p) returns (boolean) {
    return p.age < 35;
}

function getName(Person p) returns (string) {
    return p.name;
}

function getSalary(Person p) returns (float) {
    return p.salary;
}

function isGeraterThan4(Person p) returns (boolean) {
    return lengthof p.name > 4;
}

function isGeraterThan4String(string s) returns (boolean) {
    return lengthof s > 4;
}
