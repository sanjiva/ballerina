import ballerina/math;

function testFuncInvocation (int a, int b, int c) returns (int) {
    int x;
    x = 10;
    a = add(a, b);
    a = add(a, c);
    return add(a, x);
}

function add(int x, int y) returns (int) {
    int z;
    z = x  + y;
    return z;
}

function multiply(int x, int y) returns (int) {
    int z;
    z = x * y;
    return z;
}

function funcInvocationWithinFuncInvocation(int a, int b, int c) returns (int){
    int result;

    result = add( add( add(a, c), b), add(b, c) );
    return result + add(a, b) + add(a, b);
}

function testReturnFuncInvocationWithinFuncInvocation(int a, int b) returns (int){
    return add(a, multiply(a, b));
}

function testReturnNativeFuncInvocationWithinNativeFuncInvocation(float x) returns (float) {
    return math:sqrt(math:pow(x, 2));
}

function sum (int a) returns int {
    int x;
    if (a > 0) {
        x = sum(a - 1);
        a =  a + x;
    }
    return a;
}

function getPowerOfN (float a, float n) returns (float) {
    float v = math:pow(a, n);
    return v;
}
