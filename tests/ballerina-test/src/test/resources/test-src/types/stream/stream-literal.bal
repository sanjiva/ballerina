import ballerina/io;
import ballerina/time;

struct Employee {
    int id;
    string name;
}

struct Job {
    string description;
}

function testInvalidStreamCreation () {
    stream t1 = {};
}

function testInvalidStructPublishingToStream () {
    stream<Employee> s1 = {};
    Job j1 = { description:"Dummy Description 1" };
    s1.publish(j1);
}

function testSubscriptionFunctionWithNonStructParameter () {
    stream<Employee> s1 = {};
    s1.subscribe(printInteger);
}

function testSubscriptionFunctionWithIncorrectStructParameter () {
    stream<Employee> s1 = {};
    s1.subscribe(printJobDescription);
}

Employee globalEmployee;
Employee[] globalEmployeeArray = [];
int employeeIndex = 0;

function testStreamPublishingAndSubscription () returns (Employee, Employee, Employee) {
    Employee origEmployee = globalEmployee;
    stream<Employee> s1 = {};
    s1.subscribe(assignGlobalEmployee);
    Employee publishedEmployee = { id:1234, name:"Maryam" };
    s1.publish(publishedEmployee);
    int startTime = time:currentTime().time;
    while (globalEmployee == null && time:currentTime().time - startTime <1000) {
        //allow for value update
    }
    Employee newEmployee = globalEmployee;
    return (origEmployee, publishedEmployee, newEmployee);
}

function testStreamPublishingAndSubscriptionForMultipleEvents () returns (Employee[], Employee[]) {
    stream<Employee> s1 = {};
    s1.subscribe(addToGlobalEmployeeArray);
    Employee e1 = { id:1234, name:"Maryam" };
    Employee e2 = { id:2345, name:"Aysha" };
    Employee e3 = { id:3456, name:"Sumayya" };
    Employee[] publishedEmployees = [e1, e2, e3];
    s1.publish(e1);
    s1.publish(e2);
    s1.publish(e3);
    int startTime = time:currentTime().time;
    while (lengthof globalEmployeeArray < 3 && time:currentTime().time - startTime < 2000) {
        //allow for value update
    }
    return (publishedEmployees, globalEmployeeArray);
}

function printInteger (int i) {
    io:println(i);
}

function printJobDescription (Job j) {
    io:println(j.description);
}

function assignGlobalEmployee (Employee e) {
    globalEmployee = e;
}

function addToGlobalEmployeeArray (Employee e) {
    globalEmployeeArray[employeeIndex] = e;
    employeeIndex = employeeIndex + 1;
}
