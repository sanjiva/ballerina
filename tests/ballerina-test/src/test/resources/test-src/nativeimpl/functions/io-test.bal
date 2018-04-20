import ballerina/io;

function testPrintAndPrintlnString(string s1, string s2){
    io:println(s1);
    io:print(s2);
    // output is equal to s1\ns2
}

function testPrintAndPrintlnInt(int v1, int v2){
    io:println(v1);
    io:print(v2);
    // output is equal to v1\nv2
}

function testPrintAndPrintlnFloat(float v1, float v2){
    io:println(v1);
    io:print(v2);
    // output is equal to v1\nv2
}

function testPrintAndPrintlnBoolean(boolean v1, boolean v2){
    io:println(v1);
    io:print(v2);
    // output is equal to v1\nv2
}

function testPrintAndPrintlnConnector() {
    Foo f1 =  new Foo();
    Foo f2 =  new Foo();
    io:println(f1);
    io:print(f2);
}

function testPrintAndPrintlnFunctionPointer() {
    function (int, int) returns (int) addFunction = func1;
    io:println(addFunction);
    io:print(addFunction);
}

function testSprintf(string fmtStr, any... fmtArgs) returns (string) {
    return io:sprintf(fmtStr, ...fmtArgs);
}

function testSprintfMix(string fmtStr, string s1, string s2, int i1) returns (string) {
    return io:sprintf(fmtStr, s1, s2, i1);
}

function printNewline() {
    io:print("hello\n");
}

function func1 (int a, int b) returns (int) {
    int c = a + b;
    return c;
}

type Foo object {
    function bar() returns (int) {
        return 5;
    }
};

function testPrintMixVarargs(string s1, int i1, float f1, boolean b1) {
    io:print(s1, i1, f1, b1);
}

function testPrintVarargs(string s1, string s2, string s3) {
    io:print(s1, s2, s3);
}

function testPrintlnVarargs(string s1, string s2, string s3) {
    io:println(s1, s2, s3);
}
