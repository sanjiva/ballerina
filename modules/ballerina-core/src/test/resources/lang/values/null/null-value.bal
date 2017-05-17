function testXmlNull() (xml, xml, int) {
    xml x1;
    xml x2 = null;
    int a = 0;
    if (x2 == null) {
        a = 5;
    }
    return x1, x2, a;
}

function testJsonNull() (json, json, int) {
    json j1;
    json j2 = null;
    int a = 0;
    if (j2 == null) {
        a = 6;
    }
    return j1, j2, a;
}

function testStructNull() (Person, Person, int) {
    Person p1;
    Person p2 = null;
    int a = 0;
    if (p2 == null) {
        a = 7;
    }
    return p1, p2, a;
}

struct Person {
    string name;
}

function testConnectorNull() (TestConnector, TestConnector, int) {
    TestConnector c1;
    TestConnector c2 = null;
    int a = 0;
    if (c2 == null) {
        a = 8;
    }
    return c1, c2, a;
}

connector TestConnector() {
    string name;
    
    action testAction(TestConnector testConnector) (string) {
        return name;
    }
}

function testArrayNull() (string[], Person[], int) {
    string[] s;
    Person[] p;
    int a = 0;
    if (p == null) {
        a = 9;
    }
    return s, p, a;
}

function testMapNull() (map, map, int) {
    map m1;
    map m2 = null;
    int a = 0;
    if (m1 == null) {
        a = 10;
    }
    return m1, m2, a;
}

function testNullArrayAccess() (string) {
    string[] fruits;
    return fruits[0];
}

function testNullMapAccess() (string) {
    map marks;
    return (string) marks["maths"];
}


typemapper json2xml (json j) (xml) {
    if (j == null) {
        return null;
    }
    xml x = `<name>converted xml</name>`;
    return x;
}

function testCastingNull(json j) (xml) {
    xml x = (xml) j;
    
    return x;
}

function testFunctionCallWithNull() (any) {
    return foo(null);
}

function foo(string s) (string) {
    return s;
}

function foo(xml x) (xml) {
    return x;
}

function testActionInNullConenctor() {
    TestConnector testConnector;
    string result = TestConnector.testAction(testConnector);
}

function testNullLiteralComparison() (boolean) {
    return (null == null);
}

function testReturnNullLiteral() (any) {
    return null;
}

function testNullInWorker() (any) {
    message request;
    request -> worker1;
    
    message result;
    result <- worker1;
    
    return result;

    worker worker1 {
    message resp;
    resp <- default;
    resp -> default;
    }
}

function testNullInForkJoin() (message, message) {
    message m = null;
    fork {
        worker foo {
            message resp1 = null;
            reply resp1;
        }

        worker bar {
            message resp2 = {};
            reply resp2;
        }
    } join (all) (message[] allReplies) {
        return allReplies[0], allReplies[1];
    } timeout (30000) (message[] msgs) {
        return null, null;
    }
}

function testArrayOfNulls() (Person[]) {
    Person p1 = {};
    Person p2;
    Person p3 = null;
    Person p4 = {};
    Person[] personArray = [p1, p2, p3, p4, null];
    return personArray;
}

function testMapOfNulls() (map) {
    xml x1 = `<x1>test xml1<x1>`;
    xml x2;
    xml x3 = null;
    xml x4 = `<x4>test xml4<x4>`;
    map xmlMap = {"x1":x1, "x2":x2, "x3":x3, "x4":x4, "x5":null};
    return xmlMap;
}