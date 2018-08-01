import testorg/foo version v1;

//------------ Testing a function with all types of parameters ---------

function testInvokeFunctionInOrder1() returns (int, float, string, int, string, int[]) {
    return foo:functionWithAllTypesParams(10, 20.0, c="Alex", d=30, e="Bob", 40, 50, 60);
}

function testInvokeFunctionInOrder2() returns (int, float, string, int, string, int[]) {
    int[] array = [40, 50, 60];
    return foo:functionWithAllTypesParams(10, 20.0, c="Alex", d=30, e="Bob", ...array);
}

function testInvokeFunctionInMixOrder1() returns (int, float, string, int, string, int[]) {
    return foo:functionWithAllTypesParams(10, e="Bob", 20.0, c="Alex", 40, 50, d=30, 60);
}

function testInvokeFunctionInMixOrder2() returns (int, float, string, int, string, int[]) {
    int[] array = [40, 50, 60];
    return foo:functionWithAllTypesParams(10, e="Bob", 20.0, c="Alex", ...array, d=30);
}

function testInvokeFunctionWithoutRestArgs() returns (int, float, string, int, string, int[]) {
    return foo:functionWithAllTypesParams(10, e="Bob", 20.0, c="Alex", d=30);
}

function testInvokeFunctionWithoutSomeNamedArgs() returns (int, float, string, int, string, int[]) {
    return foo:functionWithAllTypesParams(c="Alex", 10, 20.0);
}

function testInvokeFunctionWithRequiredArgsOnly() returns (int, float, string, int, string, int[]) {
    return foo:functionWithAllTypesParams(10, 20.0);
}

function testInvokeFunctionWithRequiredAndRestArgs() returns (int, float, string, int, string, int[]) {
    return foo:functionWithAllTypesParams(10, 20.0, 40, 50, 60);
}

function funcInvocAsRestArgs() returns (int, float, string, int, string, int[]) {
    return foo:functionWithAllTypesParams(10, 20.0, c="Alex", d=30, e="Bob", ...getIntArray());
}

function getIntArray() returns (int[]) {
    return [1,2,3,4];
}

//------------- Testing a function having required and rest parameters --------

function testInvokeFuncWithoutRestParams() returns (int, float, string, int, string) {
    return foo:functionWithoutRestParams(10, e="Bob", 20.0, d=30);
}

//------------- Testing a function having only named parameters --------

function testInvokeFuncWithOnlyNamedParams1() returns (int, float, string, int, string) {
    return foo:functionWithOnlyNamedParams(b = 20, e="Bob", d=30, a=10 , c="Alex");
}

function testInvokeFuncWithOnlyNamedParams2() returns (int, float, string, int, string) {
    return foo:functionWithOnlyNamedParams(e="Bob", d=30, c="Alex");
}

function testInvokeFuncWithOnlyNamedParams3() returns (int, float, string, int, string) {
    return foo:functionWithOnlyNamedParams();
}

//------------- Testing a function having only rest parameters --------

function testInvokeFuncWithOnlyRestParam1() returns (int[]) {
    return foo:functionWithOnlyRestParam();
}

function testInvokeFuncWithOnlyRestParam2() returns (int[]) {
    return foo:functionWithOnlyRestParam(10, 20, 30);
}

function testInvokeFuncWithOnlyRestParam3() returns (int[]) {
    int[] a = [10, 20, 30];
    return foo:functionWithOnlyRestParam(...a);
}

//------------- Testing a function with rest parameter of any type --------

function testInvokeFuncWithAnyRestParam1() returns (any[]) {
    int[] a = [10, 20, 30];
    json j = {"name":"John"};
    return foo:functionAnyRestParam(a, j);
}

// ------------------- Test function signature with union types for default parameter

function testFuncWithUnionTypedDefaultParam() returns json {
    return foo:funcWithUnionTypedDefaultParam();
}


// ------------------- Test function signature with null as default parameter value

function testFuncWithNilDefaultParamExpr() returns (any, any) {
   return (foo:funcWithNilDefaultParamExpr_1(), foo:funcWithNilDefaultParamExpr_2());
}

// ------------------- Test function signature for attached functions ------------------

function testAttachedFunction() returns (int, int) {
    foo:Employee emp = new;
    return (emp.getSalary("Alex"), emp.getSalary("Alex", b = 10));
}


function testDefaultableParamInnerFunc() returns (int, string) {
    foo:Person p = new;
    return p.test1(a = 50);
}

function testDefaultableParamOuterFunc() returns (int, string) {
    foo:Person p = new;
    return p.test2(a = 40);
}
