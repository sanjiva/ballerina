import ballerina/io;

@Description {value:"In scope variables can be accessed within workers of fork-join statement."}
function main (string[] args) {
    // Define variables which are visible to the forked workers.
    int i = 100;
    string s = "WSO2";
    map m = {"name":"Abhaya", "era":"Anuradhapura"};

    // Declare the fork-join statement.
    fork {
        worker W1 {
            // Change the value of the integer variable "i" within the worker W1.
            i = 23;
            // Change the value of map variable "m" within the worker W1.
            m["name"] = "Rajasinghe";
            // Define a variable within the worker to send to join block.
            string n = "Colombo";
            // Send data to join block of the fork-join from worker W1.
            i, n -> fork;
        }

        worker W2 {
            // Change the value of string variable "s" within the worker W2.
            s = "Ballerina";
            // Change the value of map variable "m" within the worker W2.
            m["era"] = "Kandy";
            // Send data to join block of the fork-join from worker W2.
            s -> fork;
        }
    } join (all) (map results) {

        any[] r1;
        any[] r2;
        // Declare variables to receive the results from forked workers W1 and W2.
        // The 'results' map contains a map of any type array from each worker
        // defined within the fork-join statement.
        // Values received from worker W1 are assigned to any array of r1.
        var x1 = <any[]>results["W1"];
        match x1 {
            any[] val  => {r1 = <any[]> val;}
            error e => {io:println(e.message);}
        }

        // Values received from worker W2 are assigned to any array of r2.
        var x2 = <any[]>results["W2"];
        match x2 {
            any[] val  => {r2 = <any[]> val;}
            error e => {io:println(e.message);}
        }

        // Getting the 0th index of array returned from worker W1.
        int p;
        p =? <int>r1[0];
        // Getting the 1th index of array returned from worker W1.
        string l;
        var indexL = <string>r1[1];
        match indexL {
            string val  => {l = <string > val;}
        }

        // Getting the 0th index of array returned from worker W2.
        string q;
        var indexQ = <string>r2[0];
        match indexQ {
            string val  => {q = <string > val;}
        }

        // Print values received from workers within join block.
        io:println("[default worker] within join:
        Value of integer from W1 is [" + p + "]");
        io:println("[default worker] within join:
        Value of string from W1 is [" + l + "]");
        io:println("[default worker] within join:
        Value of string from W2 [" + q + "]");
    }
    // Print values after the fork-join statement to check effect on variables.
    // Value type variables have not been changed since they are passed in as a
    // copy of the original variable.
    io:println("[default worker] after fork-join:
        Value of integer variable is [" + i + "]
        Value of string variable is [" + s + "]");
    // Reference type variables are changed since they have passed in as a
    // reference to the workers.

    string name;
    string era;

    var varName = <string>m["name"];
    match varName {
        string val  => {name = <string > val;}
    }

    var varEra = <string>m["era"];
    match varEra {
        string val  => {era = <string > val;}
    }

    io:println("[default worker] after fork-join:
        Value of name is [" + name + "]
        Value of era is [" + era + "]");
}
