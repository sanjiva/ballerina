import ballerina.io;

struct Person {
    string name;
    int age;
    Person | null parent;
    json info;
    map address;
    int[] marks;
}

function testMultiValuedStructInlineInit () returns (Person) {
    Person p1 = {name:"aaa", age:25,
                    parent:{name:"bbb", age:50},
                    address:{"city":"Colombo", "country":"SriLanka"},
                    info:{status:"single"}
                };
    return p1;
}

function testAccessJsonInStruct () returns (string, string, string) {
    Person p1 = {name:"aaa",
                    age:25,
                    parent:{name:"bbb",
                               age:50,
                               address:{"city":"Colombo", "country":"SriLanka"},
                               info:{status:"married"}
                           },
                    info:{status:"single"}
                };
    string statusKey = "status";
    string status1;
    string status2;
    string status3;

    match p1.parent {
     Person p2  => status1,_ = (string)p2.info.status;
     any | null => io:println("Person is null");
   }

    match p1["parent"] {
     Person p2 => {
                    status2, _ = (string) p2["info"]["status"];
                    status3, _ = (string)p2.info["status"];
     }
     any | null => io:println("Person is null");
    }
    return ( status1, status2, status3);
}

function testAccessMapInStruct () returns (any, any, any, string) {
    Person p1 = {name:"aaa",
                    age:25,
                    parent:{name:"bbb",
                               age:50,
                               address:{"city":"Colombo", "country":"SriLanka"},
                               info:{status:"married"}
                           },
                    info:{status:"single"}
                };
    string cityKey = "city";
    string city;

    match p1["parent"] {
        Person p2 =>{
                city, _ = (string)p2.address[cityKey];
                return (p2.address.city, p2["address"]["city"], p2.address["city"], city);
         }
        any | null => {
                io:println("Person is null");
                return (null, null, null, city);
        }
    }
}

function testSetValueToJsonInStruct () returns (json) {
    Person p1 = {name:"aaa",
                    age:25,
                    parent:{name:"bbb",
                               age:50,
                               address:{"city":"Colombo", "country":"SriLanka"},
                               info:{status:"married"}
                           },
                    info:{status:"single"}
                };

    match p1.parent {
         Person p2 => {
         p2.info.status = "widowed";
         p2["info"]["retired"] = true;
         return p2.info;
        }
    any | null => {
         io:println("Person is null");
         return null;
         }
    }
}

function testAccessArrayInStruct () returns (int, int) {
    Person p1 = {marks:[87, 94, 72]};
    string statusKey = "status";
    return (p1.marks[1], p1["marks"][2]);
}

function testMapInitWithAnyType () returns (any, map) {
    any a = {name:"Supun"};
    map mapCast;
    mapCast, _ = (map)a;
    return (a, mapCast);
}
