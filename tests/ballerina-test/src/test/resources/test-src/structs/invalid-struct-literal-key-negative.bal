struct Department {
    string dptName;
    boolean y;
}

function testInvalidFieldNameInit () {
    string name;
    Department dpt = {dptName[0]:54};
}

function testVarRefAsKey() {
    person p = {(a): "HR"};
}

function testFuncCallAsKey() {
    person p = {foo(): "HR"};
}

function foo() returns (string) {
	return "dptName";
}