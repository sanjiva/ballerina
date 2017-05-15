struct Person {
    string name;
    int age;
    Person parent;
    json info;
    map address;
    int[] marks;
}


struct Student {
    string name;
    int age;
    map address;
    int[] marks;
}

function testStructToStruct() (Person) {
    Student s = { name:"Supun", 
                  age:25, 
                  address:{"city":"Kandy", "country":"SriLanka"}, 
                  marks:[24, 81]
                };
    Person p = s;
    return s;
}
