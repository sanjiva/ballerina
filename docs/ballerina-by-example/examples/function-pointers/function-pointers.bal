import ballerina/io;

function test (int x, string s) returns (float) {
    int y = check <int>s;
    float f = x * 1.0 * y;
    return f;
}

@Description {value:"Function pointer as a parameter."}
function foo (int x, function (int, string) returns (float) bar)
             returns (float) {
    return x * bar(10, "2");
}

@Description {value:"Function pointer as a return type."}
function getIt () returns (function (int, string) returns (float)) {
    return test;
}

function main (string[] args) {
    // Value 'test' will serve as a pointer to 'test' function.
    io:println("Answer: " + foo(10, test));
    io:println("Answer: " + foo(10, getIt()));
    // Function pointer as a variable;
    function (int, string) returns (float) f = getIt();
    io:println("Answer: " + foo(10, f));
}
