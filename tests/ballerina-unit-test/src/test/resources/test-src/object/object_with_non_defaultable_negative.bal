
Person pp;
Employee ee;

public function testObjectWithInterface () returns (int, string) {
    Person p;
    Employee e;
    return (p.attachInterface(7), p.month);
    //return (5, "kk");
}

type Person object {

    public int age = 10,


    string month = "february";


    new (age) {

    }
};

type Employee object {

    public int age = 10,
    public Person p;


    private string month = "february";
    
};
