
public function testObjectInsideObject () returns (string, string) {
    Person p = new Person();
    return (p.getNameWrapperInside1(), p.getNameFromDiffObject());
}


function testGetValueFromPassedSelf() returns string {
    Person p = new Person();
    return p.selfAsValue();
}

type Person object {
    public {
        int age = 10,
        string name = "sample name";
    }
    private {
        int year = 50;
        string month = "february";
    }

    function getName() returns string {
        return name;
    }

    function getNameWrapperInside1() returns string {
        return self.getName();
    }

    function getNameFromDiffObject() returns string {
        Person p = new ();
        p.name = "changed value";
        return p.getName();
    }

    function selfAsValue() returns string {
        return passSelfAsValue(self);
    }

};

function passSelfAsValue(Person p) returns string {
    return p.getName();
}


