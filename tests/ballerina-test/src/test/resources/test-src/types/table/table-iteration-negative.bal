struct Employee {
    int id;
    string name;
    float salary;
}

struct EmployeeIncompatible {
    float id;
    string name;
    float salary;
}

struct EmployeeSalary {
    int id;
    float salary;
}

struct EmployeeSalaryIncompatible {
    float id;
    float salary;
}

function getSalaryInCompatibleInput(EmployeeIncompatible e) returns (EmployeeSalary) {
    EmployeeSalary s = {id: e.id, salary: e.salary};
    return s;
}

function getSalaryIncompatibleOutput(Employee e) returns (EmployeeSalaryIncompatible) {
    EmployeeSalaryIncompatible s = {id: e.id, salary: e.salary};
    return s;
}

function getSalaryIncompatibleInputOutput(EmployeeIncompatible e) returns (EmployeeSalaryIncompatible) {
    EmployeeSalaryIncompatible s = {id: e.id, salary: e.salary};
    return s;
}

function createTable() returns (table<Employee>) {
    table<Employee> dt = {};

    Employee e1 = {id:1, name:"A", salary:100};
    Employee e2 = {id:2, name:"B", salary:200};
    Employee e3 = {id:3, name:"C", salary:300};

    dt.add(e1);
    dt.add(e2);
    dt.add(e3);

    return dt;
}

function testInCompatibleInput() returns (table) {
    table<Employee> dt = createTable();

    table<EmployeeSalary> t = dt.select(getSalaryInCompatibleInput);
    return t;
}

function testIncompatibleOutput() returns (table) {
    table<Employee> dt = createTable();

    table<EmployeeSalary> t = dt.select(getSalaryIncompatibleOutput);
    return t;
}

function testIncompatibleInputOutput() returns (table) {
    table<Employee> dt = createTable();

    table<EmployeeSalary> t = dt.select(getSalaryIncompatibleInputOutput);
    return t;
}
