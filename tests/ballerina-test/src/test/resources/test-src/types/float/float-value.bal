function testFloatValue() returns (float) {
    float b;
    b = 10.1;
    return b;
}

function testNegativeFloatValue() returns (float) {
    float y;
    y = -10.1;
    return y;
}

function testFloatValueAssignmentByReturnValue() returns (float) {
    float x;
    x = testFloatValue();
    return x;
}

function testFloatAddition() returns (float) {
    float b;
    float a;
    a = 9.9;
    b = 10.1;
    return a + b;
}

function testFloatMultiplication() returns (float) {
    float b;
    float a;
    a = 2.5;
    b = 5.5;
    return a * b;
}

function testFloatSubtraction() returns (float) {
    float b;
    float a;
    a = 25.5;
    b = 15.5;
    return a - b;
}

function testFloatDivision() returns (float) {
    float b;
    float a;
    a = 25.5;
    b = 5.1;
    return a / b;
}

function testFloatParameter(float a) returns (float) {
    float b;
    b = a;
    return b;
}

function testFloatValues() returns (float, float, float, float){
    float a = 123.4;
    float b = 1.234e2;
    float c = 123.4f;
    float d = 1.234e2d;
    return (a, b, c, d);
}
