@Description {value:"Defining Person struct."}
struct Person {
    string name;
    int age;
    string city;
}

@Description {value:"Defining transformer to convert from Person type to constrained JSON."}
transformer <Person p, json<Person> j> updateCity(string city) {
    j.name = p.name;
    j.age = p.age;
    j.city = city;
}


function main (string[] args) {
    json j = {"name":"Ann", "age": 30 ,"city":"New York"};

    // Declare a Person variable.
    Person p;

    // Declare a type conversion error to accept any type conversion errors thrown.
    TypeConversionError err;
    // Convert JSON to a Person type variable.
    p, err = <Person>j;

    // Print if an error is thrown.
    if (err != null) {
        println(err);
    }

    // Define a constant city value as "London".
    string city = "London";

    // Convert p of type Person to the response JSON, using the transformer defined earlier.
    json<Person> response = <json<Person>, updateCity(city)> p;

    println(response);
}

