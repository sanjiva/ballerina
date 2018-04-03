struct Person {
    string name;
    int age;
    Person | null parent;
    json info;
    map| null address;
    int[] | null marks;
    any a;
    float score;
    boolean alive;
    Person[]|null children;
}

struct Student {
    string name;
    int age;
}

function testStructToMap () returns (map) {
    Person p = {name:"Child",
                   age:25,
                   parent:{name:"Parent", age:50},
                   address:{"city":"Colombo", "country":"SriLanka"},
                   info:{status:"single"},
                   marks:[67, 38, 91]
               };
    map m = <map>p;
    return m;
}


function testMapToStruct () returns (Person | error) {
    int[] marks = [87, 94, 72];
    Person parent = {
                        name:"Parent",
                        age:50,
                        parent:null,
                        address:null,
                        info:null,
                        marks:null,
                        a:null,
                        score:4.57,
                        alive:false
                    };

    json info = {status:"single"};
    map m = {name:"Child",
                age:25,
                parent:parent,
                address:{"city":"Colombo", "country":"SriLanka"},
                info:info,
                marks:marks,
                a:"any value",
                score:5.67,
                alive:true
            };
    Person p =? <Person> m;
    return p;
}

function testStructToJson () returns (json) {
    Person | null p = {name:"Child",
                   age:25,
                   parent:{name:"Parent", age:50},
                   address:{"city":"Colombo", "country":"SriLanka"},
                   info:{status:"single"},
                   marks:[87, 94, 72]
               };

    json j =? <json>p;
    return j;
}

function testJsonToStruct () returns (Person | error) {
    json j = {name:"Child",
                 age:25,
                 parent:{
                            name:"Parent",
                            age:50,
                            parent:null,
                            address:null,
                            info:null,
                            marks:null,
                            a:null,
                            score:4.57,
                            alive:false,
                            children:null
                        },
                 address:{"city":"Colombo", "country":"SriLanka"},
                 info:{status:"single"},
                 marks:[56, 79],
                 a:"any value",
                 score:5.67,
                 alive:true,
                 children:null
             };
    var p = <Person>j;
    return p;
}

function testIncompatibleMapToStruct () returns (Person) {
    int[] marks = [87, 94, 72];
    map m = {name:"Child",
                age:25,
                address:{"city":"Colombo", "country":"SriLanka"},
                info:{status:"single"},
                marks:marks
            };
    Person p =? <Person>m;
    return p;
}

function testMapWithMissingFieldsToStruct () returns (Person) {
    int[] marks = [87, 94, 72];
    map m = {name:"Child",
                age:25,
                address:{"city":"Colombo", "country":"SriLanka"},
                marks:marks
            };
    Person p =? <Person>m;
    return p;
}

function testMapWithIncompatibleArrayToStruct () returns (Person) {
    float[] marks = [87, 94, 72];
    Person parent = {
                        name:"Parent",
                        age:50,
                        parent:null,
                        address:null,
                        info:null,
                        marks:null,
                        a:null,
                        score:5.67,
                        alive:false
                    };
    json info = {status:"single"};
    map m = {name:"Child",
                age:25,
                parent:parent,
                address:{"city":"Colombo", "country":"SriLanka"},
                info:info,
                marks:marks,
                a:"any value",
                score:5.67,
                alive:true
            };

    var p =? <Person>m;
    return p;
}

struct Employee {
    string name;
    int age;
    Person partner;
    json info;
    map address;
    int[] marks;
}

function testMapWithIncompatibleStructToStruct () returns (Employee) {
    int[] marks = [87, 94, 72];
    Student s = {name:"Supun",
                    age:25
                };

    map m = {name:"Child",
                age:25,
                partner:s,
                address:{"city":"Colombo", "country":"SriLanka"},
                info:{status:"single"},
                marks:marks
            };
            
    var e =? <Employee>m;
    return e;
}

function testJsonToStructWithMissingFields () returns (Person) {
    json j = {name:"Child",
                 age:25,
                 address:{"city":"Colombo", "country":"SriLanka"},
                 info:{status:"single"},
                 marks:[87, 94, 72]
             };

    var p =? <Person>j;
    return p;
}

function testIncompatibleJsonToStruct () returns (Person) {
    json j = {name:"Child",
                 age:"25",
                 address:{"city":"Colombo", "country":"SriLanka"},
                 info:{status:"single"},
                 marks:[87, 94, 72]
             };

    var p =? <Person>j;
    return p;
}

function testJsonWithIncompatibleMapToStruct () returns (Person) {
    json j = {name:"Child",
                 age:25,
                 parent:{
                            name:"Parent",
                            age:50,
                            parent:null,
                            address:"SriLanka",
                            info:null,
                            marks:null
                        },
                 address:{"city":"Colombo", "country":"SriLanka"},
                 info:{status:"single"},
                 marks:[87, 94, 72]
             };

    var p =? <Person>j;
    return p;
}

function testJsonWithIncompatibleTypeToStruct () returns (Person) {
    json j = {name:"Child",
                 age:25.8,
                 parent:{
                            name:"Parent",
                            age:50,
                            parent:null,
                            address:"SriLanka",
                            info:null,
                            marks:null
                        },
                 address:{"city":"Colombo", "country":"SriLanka"},
                 info:{status:"single"},
                 marks:[87, 94, 72]
             };

    var p =? <Person>j;
    return p;
}

function testJsonWithIncompatibleStructToStruct () returns (Person) {
    json j = {name:"Child",
                 age:25,
                 parent:{
                            name:"Parent",
                            age:50,
                            parent:"Parent",
                            address:{"city":"Colombo", "country":"SriLanka"},
                            info:null,
                            marks:null
                        },
                 address:{"city":"Colombo", "country":"SriLanka"},
                 info:{status:"single"},
                 marks:[87, 94, 72]
             };

    var p =? <Person>j;
    return p;
}

function testJsonArrayToStruct () returns (Person) {
    json j = [87, 94, 72];

    var p =? <Person>j;
    return p;
}

struct Info {
    map foo;
}

function testStructWithIncompatibleTypeMapToJson () returns (json) {
    blob b;
    map m = {bar:b};
    Info info = {foo:m};

    var j =? <json>info;
    return j;
}

function testJsonIntToString () returns (string) {
    json j = 5;
    int value;
    value =? <int>j;
    return <string>value;
}

function testBooleanInJsonToInt () returns (int) {
    json j = true;
    int value =? <int>j;
    return value;
}

function testIncompatibleJsonToInt () returns (int) {
    json j = "hello";
    int value;
    value =? <int>j;
    return value;
}

function testIntInJsonToFloat () returns (float) {
    json j = 7;
    float value;
    value =? <float>j;
    return value;
}

function testIncompatibleJsonToFloat () returns (float) {
    json j = "hello";
    float value;
    value =? <float>j;
    return value;
}

function testIncompatibleJsonToBoolean () returns (boolean) {
    json j = "hello";
    boolean value;
    value =? <boolean>j;
    return value;
}

struct Address {
    string city;
    string country;
}

struct AnyArray {
    any[] a;
}

function testJsonToAnyArray () returns (AnyArray) {
    json j = {a:[4, "Supun", 5.36, true, {lname:"Setunga"}, [4, 3, 7], null]};
    AnyArray value =? <AnyArray>j;
    return value;
}

struct IntArray {
    int[] a;
}

function testJsonToIntArray () returns (IntArray) {
    json j = {a:[4, 3, 9]};
    IntArray value =? <IntArray>j;
    return value;
}


struct StringArray {
    string[] a;
}

function testJsonToStringArray () returns (StringArray) {
    json j = {a:["a", "b", "c"]};
    StringArray a =? <StringArray>j;
    return a;
}

function testJsonIntArrayToStringArray () returns (StringArray) {
    json j = {a:[4, 3, 9]};
    StringArray a =? <StringArray>j;
    return a;
}

struct XmlArray {
    xml[] a;
}

function testJsonToXmlArray () returns (XmlArray) {
    json j = {a:["a", "b", "c"]};
    XmlArray a =? <XmlArray>j;
    return a;
}

function testNullJsonArrayToArray () returns (StringArray) {
    json j = {a:null};
    StringArray a =? <StringArray>j;
    return a;
}

function testNullJsonToArray () returns (StringArray) {
    json j;
    StringArray s =? <StringArray>j;
    return s;
}

function testNonArrayJsonToArray () returns (StringArray) {
    json j = {a:"im not an array"};
    StringArray a =? <StringArray>j;
    return a;
}

function testNullJsonToStruct () returns (Person | error) {
    json j;
    var p =? <Person>j;
    return p;
}

//function testNullMapToStruct () returns (Person | error) {
//    map|null m;
//    var p =? <Person> m;
//    return p;
//}

function testNullStructToJson () returns (json | error) {
    Person|null p;
    var j =? <json> p;
    return j;
}

//function testNullStructToMap () returns (map) {
//    Person|null p;
//    map m = <map>p;
//    return m;
//}

function testIncompatibleJsonToStructWithErrors () returns (Person | error) {
    json j = {name:"Child",
                 age:25,
                 parent:{
                            name:"Parent",
                            age:50,
                            parent:"Parent",
                            address:{"city":"Colombo", "country":"SriLanka"},
                            info:null,
                            marks:null
                        },
                 address:{"city":"Colombo", "country":"SriLanka"},
                 info:{status:"single"},
                 marks:[87, 94, 72]
             };
             
    Person p  =? <Person>j;
    return p;
}

struct PersonA {
    string name;
    int age;
}

function JsonToStructWithErrors () returns (PersonA | error) {
    json j = {name:"supun"};

    PersonA pA =? <PersonA>j;

    return pA;
}

struct PhoneBook {
    string[] names;
}

function testStructWithStringArrayToJSON () returns (json) {
    PhoneBook phonebook = {names:["John", "Doe"]};
    var phonebookJson =? <json>phonebook;
    return phonebookJson;
}

struct person {
    string fname;
    string lname;
    int age;
}

struct movie {
    string title;
    int year;
    string released;
    string[] genre;
    person[] writers;
    person[] actors;
}

//function testStructToMapWithRefTypeArray () returns (map, int) {
//    movie theRevenant = {title:"The Revenant",
//                            year:2015,
//                            released:"08 Jan 2016",
//                            genre:["Adventure", "Drama", "Thriller"],
//                            writers:[{fname:"Michael", lname:"Punke", age:30}],
//                            actors:[{fname:"Leonardo", lname:"DiCaprio", age:35},
//                                    {fname:"Tom", lname:"Hardy", age:34}]};
//
//    map m = <map>theRevenant;
//
//   any a = m["writers"];
//    var writers =? (person[])a;
//
//    return (m, writers[0].age);
//}

struct StructWithDefaults {
    string s = "string value";
    int a = 45;
    float f = 5.3;
    boolean b = true;
    json j;
    blob blb;
}

function testEmptyJSONtoStructWithDefaults () returns (StructWithDefaults | error) {
    json j = {};
    var testStruct =? <StructWithDefaults>j;

    return testStruct;
}

struct StructWithoutDefaults {
    string s;
    int a;
    float f;
    boolean b;
    json j;
    blob blb;
}

function testEmptyJSONtoStructWithoutDefaults () returns (StructWithoutDefaults | error) {
    json j = {};
    var testStruct =? <StructWithoutDefaults>j;

    return testStruct;
}

function testEmptyMaptoStructWithDefaults () returns (StructWithDefaults) {
    map m = {};
    var testStruct =? <StructWithDefaults>m;

    return testStruct;
}

function testEmptyMaptoStructWithoutDefaults () returns (StructWithoutDefaults) {
    map m = {};
    var testStruct =? <StructWithoutDefaults>m;

    return testStruct;
}

function testSameTypeConversion() returns (int) {
    float f = 10.05;
    var i= <int> f;
    i = <int>i;
    return i;
}

//function testNullStringToOtherTypes() (int, error,
//                                       float, error,
//                                       boolean, error,
//                                       json, error,
//                                       xml, error) {
//    string s;
//    var i, err1 = <int> s;
//    var f, err2 = <float> s;
//    var b, err3 = <boolean> s;
//    var j, err4 = <json> s;
//    var x, err5 = <xml> s;
//    
//    return i, err1, f, err2, b, err3, j, err4, x, err5;
//}

function structWithComplexMapToJson() returns (json | error) {
    int a = 4;
    float b = 2.5;
    boolean c = true;
    string d = "apple";
    map e = {"foo":"bar"};
    PersonA f = {};
    int [] g = [1, 8, 7];
    map m = {"a":a, "b":b, "c":c, "d":d, "e":e, "f":f, "g":g, "h":null};
    
    Info info = {foo : m};
    var js =? <json> info;
    return js;
}

struct ComplexArrayStruct{
    int[] a;
    float[] b;
    boolean[] c;
    string[] d;
    map[] e;
    PersonA[] f;
    json[] g;
}

function structWithComplexArraysToJson() returns (json | error) {
    ComplexArrayStruct t = {a:[4, 6, 9], b:[4.6, 7.5], c:[true, true, false], d:["apple", "orange"], e:[{}, {}], f:[{}, {}], g:[{"foo":"bar"}]};
    var js =? <json> t;
    return js;
}
