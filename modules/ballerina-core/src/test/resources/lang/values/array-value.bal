function lazyInitThrowArrayIndexOutOfBound() {
    int[] arr = [];
    int x;

    // This should throw an exception
    x = arr[0];
}

function lazyInitSizeZero() (string[]) {
    string[] names = [];

    return names;
}

function addValueToIntArray() (int[]) {
    int[] arr = [];
    int x;
    x = 4;
    arr[0] = -10;
    arr[15] = 2 + 18;
    arr[99] = 2147483647;
    arr[100] = -x;
    arr[115] = -2147483647;
    arr[199] = 6;

    return arr;
}

function addValueToFloatArray() (float[]) {
    float[] arr = [];
    arr[0] = -10.0;
    arr[15] = 2.5 ;
    arr[99] = 2147483647.1;
    arr[100] = 4.3;
    arr[115] = -2147483647.7;
    arr[199] = 6.9;

    return arr;
}


function commnetFunction() {
// TODO
// 1) Array add value test
// 2) Array get value test
// 3) Array grow test
// 4) Array maximum size test
// 5) Array grow and size change test
}

function testDefaultValueOfIntArrayElement() (int, int, int){
    int[] a = [];
    a[5] = 45;
    return a[0], a[1], a[5];
}

function testDefaultValueOfJsonArrayElement() (json, json, json) {
    json[] j = [];
    j[5] = {name:"supun"};
    return j[0], j[1], j[5];
}