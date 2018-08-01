import pkg1;

type Person object {


    public function func1();

    private function func2();
};

public function Person::func1() {

}

function Person::func2();



public type Employee object {
    public int age;
    private string name;
    string email;

    public function getName() returns (string);

    private function getAge() returns (int);

    function getEmail() returns (string);
};


function Employee::getName() returns (string) {
    return self.name;
}

function Employee::getAge() returns (int) {
    return self.age;
}

function Employee::getEmail() returns (string) {
    return self.email;
}

function visibilityTest() {
    Employee emp1 = new;
    int a1 = emp1.age;
    string n1 = emp1.name;
    string e1 = emp1.email;

    string n2 = emp1.getName();
    int a2 = emp1.getAge();
    string e2 = emp1.getEmail();
}

function modVisibilityTest() {
    pkg1:Employee emp2 = new;
    int a3 = emp2.age;
    string n3 = emp2.name;
    string e3 = emp2.email;

    string n4 = emp2.getName();
    int a4 = emp2.getAge();
    string e4 = emp2.getEmail();
}
