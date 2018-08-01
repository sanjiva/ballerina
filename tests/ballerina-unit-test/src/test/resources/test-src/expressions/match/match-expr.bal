function foo(string|int|float|error a) returns (string|int|float|error) {
    return a;
}

function getJson() returns json|error  {
    json j = {name:"John"};
    return j;
}

function getError() returns error {
    return {message: "test error"};
}  

function testMatchExpr(string|int|float|error a) returns string {
    string x = foo(a) but { int => "value1", 
                            float => "value2", 
                            error => "value3",
                            string => "value4"  
                };
    return x;
}

function testMatchExprWithImplicitDefault_1(string|int|float|error a) returns string {
    string x = foo(a) but { int => "value1", 
                            float => "value2", 
                            error => "value3" 
                };
    return x;
}

function testMatchExprWithImplicitDefault_2() returns json {
    json j = getJson() but { error => null };
    return j;
}

function testMatchExprInUnaryOperator(string|int|float|error a) returns string {
    string x = "HELLO "  + <string> (foo(a) but { int => "value1", 
                                                  float => "value2", 
                                                  error => "value3" 
                });
    return x;
}

function testMatchExprInBinaryOperator(string|int|float|error a) returns string {
    string x = "HELLO "  + (foo(a) but { int => "value1", 
                                         float => "value2", 
                                         error => "value3" 
                });
    return x;
}

function testMatchExprInFuncCall(string|int|float|error a) returns string {
    string x = bar(foo(a) but { 
                    int => "value1", 
                    float => "value2", 
                    error => "value3" 
                });
    return x;
}

function bar(string b) returns string {
    return b;
}

function fooWithNull(string|int|float|error|() a) returns (string|int|float|error|()) {
    return a;
}

function testNestedMatchExpr(string|int|float|error|() a) returns string {
    string x = bar(fooWithNull(a) but { 
            int => "value1", 
            float => "value2", 
            string|() b => b but {
                    string => "value is string",
                () => "value is null"
            },
            error => "value3"
    });
    return x;
}

function testMatchExprMultipleTypes(string|int|float|error a) returns string|int {
    string|int x = foo(a) but { int => "value1", 
                            float => "value2", 
                            error => 3,
                            string => 4  
                };
    return x;
}


function testAssignabileTypesInPatterns(string|int|float|error a) returns json {
    json j = a but { 
                    json => "jsonStr1", 
                    error => "jsonStr2"
             };
    return j;
}

function testMatchErrorWithCauses() {
    error cause1 = {message: "root cause"};
    error cause2 = {message: "intermediate cause", cause: cause1};
    error err1 = {message: "actual error", cause: cause2};

    error? err2 = err1;
    match err2 {
        error err3 => throw err3.cause but { 
            error err4 => err4.cause but { 
                () => err4 
            },
            () => err3 
        };
        () => {}
    }
}

