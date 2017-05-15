package lang.expressions.type.cast.foo;

import lang.expressions.type.cast.foo.bar;

struct Person {
    string name;
    int age;
    Person parent;
    json info;
    map address;
    int[] marks;
}

function testCastToStructInDifferentPkg() (Person) {
    Person p1 = { name:"aaa",
                  age:25, 
                  parent:{ name:"bbb", 
                           age:50, 
                           address:{"city":"Colombo", "country":"SriLanka"}, 
                           info:{status:"married"}
                         },
                  info:{status:"single"}
                 };
    string statusKey = "status";
    
    bar:Student s = p1;

}
