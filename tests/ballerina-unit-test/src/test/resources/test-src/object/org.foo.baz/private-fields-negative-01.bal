
public type ParentFoo object {

    public int i;
    public ChildFoo c;



    private string s;


    new (i, c){}
};

type ChildFoo object {

    private string name;


    new (name) {}
};

type privatePerson object {

    public int age;
    public string name;


    new (age, name){}
    public function getPrivatePersonName() returns string;
};

public function newPrivatePerson() returns (privatePerson) {
    return new privatePerson(12, "mad");
}

public function privatePersonAsParam(privatePerson p) returns (string){
    return p.name;
}

public function privatePersonAsParamAndReturn(privatePerson p) returns (privatePerson) {
    return p;
}

function privatePerson::getPrivatePersonName() returns (string) {
    return self.name;
}
