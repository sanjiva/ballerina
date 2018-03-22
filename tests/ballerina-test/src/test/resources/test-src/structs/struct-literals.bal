struct Department {
    string dptName;
    Person[] employees;
    Person manager;
}

struct Person {
    string name = "default first name";
    string lname;
    map adrs;
    int age = 999;
    Person|null child;
}

function testStructLiteral1 () returns (Department) {
    Department p = {};
    return p;
}

function testStructLiteral2 () returns (Person) {
    Person p = {};
    return p;
}

