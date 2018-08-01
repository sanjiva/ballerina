import org.foo;

public function testPrivateFieldAccess() {
    foo:person p = foo:newPerson();

    string name = p.name;
    string ssn = p.ssn;

}

public type personFoo object {
    public int age;
    public string name;
    public string ssn;
    public int id;

    public new(age, name, ssn, id) {
        self.age = age;
        self.name = name;
        self.ssn = ssn;
        self.id = id;
    }
};

public function testCompileTimeStructEq() {
    personFoo pf = new (10, "dd", "123-44-3333", 12);
    foo:person p = check <foo:person>pf;
}
