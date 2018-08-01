import org.foo;

public type userA object {

    public int age;
    public string name;

};

public type userB object {

    public int age;
    public string name;
    public string address;


    string zipcode;

};

public function testRuntimeObjEqNegative() returns (string) {
    foo:user u = foo:newUser();

    // This is a safe cast
    var uA = <userA> u;

    // This is a unsafe cast
    var uB = <userB> uA;
    match uB {
        error err => return err.message;
        userB usrB  => return usrB.zipcode;
    }
}
