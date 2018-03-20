//------------ Testing a function with all types of parameters ---------

function functionWithAllTypesParams(int a, float b, string c = "John", int d = 5, string e = "Doe", int... z) 
            (int, float, string, int, string, int[]) {
    return a, b, c, d, e, z;
}

function testInvokeFunctionInOrder1() (int, float, string, int, string, int[]) {
    return functionWithAllTypesParams(10, 20.0, c="Alex", d=30, e="Bob", 40, 50, 60);
}

function testInvokeFunctionInOrder2() (int, float, string, int, string, int[]) {
    int[] array = [40, 50, 60];
    return functionWithAllTypesParams(10, 20.0, c="Alex", d=30, e="Bob", ...array);
}

function testInvokeFunctionInMixOrder1() (int, float, string, int, string, int[]) {
    return functionWithAllTypesParams(10, e="Bob", 20.0, c="Alex", 40, 50, d=30, 60);
}

function testInvokeFunctionInMixOrder2() (int, float, string, int, string, int[]) {
    int[] array = [40, 50, 60];
    return functionWithAllTypesParams(10, e="Bob", 20.0, c="Alex", ...array, d=30);
}

function testInvokeFunctionWithoutRestArgs() (int, float, string, int, string, int[]) {
    return functionWithAllTypesParams(10, e="Bob", 20.0, c="Alex", d=30);
}

function testInvokeFunctionWithoutSomeNamedArgs() (int, float, string, int, string, int[]) {
    return functionWithAllTypesParams(c="Alex", 10, 20.0);
}

function testInvokeFunctionWithRequiredArgsOnly() (int, float, string, int, string, int[]) {
    return functionWithAllTypesParams(10, 20.0);
}

function testInvokeFunctionWithRequiredAndRestArgs() (int, float, string, int, string, int[]) {
    return functionWithAllTypesParams(10, 20.0, 40, 50, 60);
}

function funcInvocAsRestArgs() (int, float, string, int, string, int[]) {
    return functionWithAllTypesParams(10, 20.0, c="Alex", d=30, e="Bob", ...getIntArray());
}

function getIntArray() (int[]) {
    return [1,2,3,4];
}

//------------- Testing a function having required and rest parameters --------

function functionWithoutRestParams(int a, float b, string c = "John", int d = 5, string e = "Doe") 
            (int, float, string, int, string) {
    return a, b, c, d, e;
}

function testInvokeFuncWithoutRestParams() (int, float, string, int, string) {
    return functionWithoutRestParams(10, e="Bob", 20.0, d=30);
}

//------------- Testing a function having only named parameters --------


function functionWithOnlyNamedParams(int a=5, float b=6.0, string c = "John", int d = 7, string e = "Doe") 
            (int, float, string, int, string) {
    return a, b, c, d, e;
}

function testInvokeFuncWithOnlyNamedParams1() (int, float, string, int, string) {
    return functionWithOnlyNamedParams(b = 20, e="Bob", d=30, a=10 , c="Alex");
}

function testInvokeFuncWithOnlyNamedParams2() (int, float, string, int, string) {
    return functionWithOnlyNamedParams(e="Bob", d=30, c="Alex");
}

function testInvokeFuncWithOnlyNamedParams3() (int, float, string, int, string) {
    return functionWithOnlyNamedParams();
}

//------------- Testing a function having only rest parameters --------

function functionWithOnlyRestParam(int... z) (int[]) {
    return z;
}

function testInvokeFuncWithOnlyRestParam1() (int[]) {
    return functionWithOnlyRestParam();
}

function testInvokeFuncWithOnlyRestParam2() (int[]) {
    return functionWithOnlyRestParam(10, 20, 30);
}

function testInvokeFuncWithOnlyRestParam3() (int[]) {
    int[] a = [10, 20, 30];
    return functionWithOnlyRestParam(...a);
}

//------------- Testing a function with rest parameter of any type --------

function functionAnyRestParam(any... z) (any[]) {
    return z;
}

function testInvokeFuncWithAnyRestParam1() (any[]) {
    int[] a = [10, 20, 30];
    json j = {"name":"John"};
    return functionAnyRestParam(a, j);
}

