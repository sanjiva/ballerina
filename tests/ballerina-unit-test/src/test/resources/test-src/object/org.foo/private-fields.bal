
public type userFoo object {
    public int age;
    public string name;
    public string address;
    public string zipcode = "23468";

    public function getName() returns (string);

    public function getAge() returns (int);
};

function userFoo::getName () returns (string) {
    return self.name;
}

function userFoo::getAge () returns (int) {
    return self.age;
}

public type user object {
    public int age;
    public string name;
    public string address;
    public string zipcode = "23468";
};

public type person object {
    public int age;
    public string name;

    string ssn;
    int id;
};

type student object {
    public int age;
    public string name;

    string ssn;
    int id;
    int schoolId;
};

public function newPerson() returns (person) {
    person p = new;
    p.age = 12;
    p.name = "mad";
    p.ssn = "234-90-8887";
    return p;
}

public function newUser() returns (user) {
    user u = new;
    u.age = 56;
    u.name = "mal";
    u.zipcode = "23126";
    return u;
}

public type FooObj object {
    public int age;
    public string name;

    public function getName() returns (string);
};

function FooObj::getName() returns (string) {
    return self.name;
}
