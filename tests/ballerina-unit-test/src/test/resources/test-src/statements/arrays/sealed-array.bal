// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

// Int Arrays

function createIntSealedArray() returns int {
    int[5] sealedArray = [2, 15, 200, 1500, 5000];
    return lengthof sealedArray;
}

function createIntSealedArrayWithLabel() returns int {
    int[!...] sealedArray = [2, 15, 200, 1500, 5000];
    return lengthof sealedArray;
}

function createIntDefaultSealedArray() returns (int[], int) {
    int[5] sealedArray;
    return (sealedArray, lengthof sealedArray);
}

// Boolean Arrays

function createBoolSealedArray() returns int {
    boolean[5] sealedArray = [true, false, false, true, false];
    return lengthof sealedArray;
}

function createBoolSealedArrayWithLabel() returns int {
    boolean[!...] sealedArray = [true, false, false, true, false];
    return lengthof sealedArray;
}

function createBoolDefaultSealedArray() returns (boolean[], int) {
    boolean[5] sealedArray;
    return (sealedArray, lengthof sealedArray);
}

// Float Arrays

function createFloatSealedArray() returns int {
    float[5] sealedArray = [0.0, 15.2, 1100, -25.8, -10];
    return lengthof sealedArray;
}

function createFloatSealedArrayWithLabel() returns int {
    float[!...] sealedArray = [0.0, 15.2, 1100, -25.8, -10];
    return lengthof sealedArray;
}

function createFloatDefaultSealedArray() returns (float[], int) {
    float[5] sealedArray;
    return (sealedArray, lengthof sealedArray);
}

// String Arrays

function createStringSealedArray() returns int {
    string[5] sealedArray = ["a", "abc", "12", "-12", "."];
    return lengthof sealedArray;
}

function createStringSealedArrayWithLabel() returns int {
    string[!...] sealedArray = ["a", "abc", "12", "-12", "."];
    return lengthof sealedArray;
}

function createStringDefaultSealedArray() returns (string[], int) {
    string[5] sealedArray;
    return (sealedArray, lengthof sealedArray);
}

// Any Type Arrays

function createAnySealedArray() returns int {
    any[5] sealedArray = ["a", true, 12, -12.5, "."];
    return lengthof sealedArray;
}

function createAnySealedArrayWithLabel() returns int {
    any[!...] sealedArray = ["a", true, 12, -12.5, "."];
    return lengthof sealedArray;
}

// Record Type Arrays

type Person record {
    string name;
    int age;
};

function createRecordSealedArray() returns int {
    Person[5] sealedArray = [{}, {}, {}, {}, {}];
    return lengthof sealedArray;
}

function createRecordSealedArrayWithLabel() returns int {
    Person[!...] sealedArray = [{}, {}, {}, {}, {}];
    return lengthof sealedArray;
}

// Byte Arrays

function createByteSealedArray() returns int {
    byte a = 5;
    byte[5] sealedArray = [a, a, a, a, a];
    return lengthof sealedArray;
}

function createByteSealedArrayWithLabel() returns int {
    byte a = 7;
    byte[!...] sealedArray = [a, a, a, a, a];
    return lengthof sealedArray;
}

function createByteDefaultSealedArray() returns (byte[], int) {
    byte[5] sealedArray;
    return (sealedArray, lengthof sealedArray);
}

// Tuple Arrays

function createTupleSealedArray() returns int {
    (int, boolean)[3] sealedArray = [(2, true), (3, false), (6, true)];
    sealedArray[2] = (5, false);
    return lengthof sealedArray;
}

function createTupleSealedArrayWithLabel() returns int {
    (int, boolean)[!...] sealedArray = [(2, true), (3, false), (6, true)];
    return lengthof sealedArray;
}

// Function Params and Returns

function functionParametersAndReturns() returns (int, int) {
    boolean[3] sealedArray = [true, false, false];
    boolean[3] returnedBoolArray;
    string[2] returnedStrArray;
    (returnedBoolArray, returnedStrArray) = mockFunction(sealedArray);

    return (lengthof returnedBoolArray, lengthof returnedStrArray);
}

function mockFunction(boolean[3] sealedArray) returns (boolean[3], string[2]) {
    string[!...] sealedStrArray = ["Sam", "Smith"];
    return (sealedArray, sealedStrArray);
}

// Runtime Exceptions

function invalidIndexAccess(int index) {
    boolean[3] x1 = [true, false, true];
    boolean x2 = x1[index];
}

function assignedArrayInvalidIndexAccess() {
    int[3] x1 = [1, 2, 3];
    int[] x2 = x1;
    x2[4] = 10;
}

// Match Statments

function unionAndMatchStatementSealedArray(float[] x) returns string {
    return unionTestSealedArray(x);
}

function unionAndMatchStatementUnsealedArray(float[] x) returns string {
    return unionTestNoSealedArray(x);
}

function unionTestSealedArray(boolean | int[] | float[4] | float[] | float[6] x) returns string {
    match x {
        boolean k => return "matched boolean";
        int[] k => return "matched int array";
        float[6] k => return "matched sealed float array size 6";
        float[4] k => return "matched sealed float array size 4";
        float[] k => return "matched float array";
    }
}

function unionTestNoSealedArray(boolean | int[] | float[4] | float[] x) returns string {
    match x {
        boolean k => return "matched boolean";
        int[] k => return "matched int array";
        float[] k => return "matched float array";
    }
}

function accessIndexOfMatchedSealedArray(int[] | int[3] x, int index) returns int {
    match x {
        int[3] k => {
            k[index] = 10;
            return k[index];
        }
        int[] k => {
            k[index] = 10;
            return k[index];
        }
    }
}

// JSON Arrays

function createJSONSealedArray() returns int {
    json[5] sealedArray = [false, "abc", "12", -12, "."];
    return lengthof sealedArray;
}

function createJSONSealedArrayWithLabel() returns int {
    json[!...] sealedArray = [false, "abc", "12", -12, "."];
    return lengthof sealedArray;
}

function invalidIndexJSONArray(int index) {
    json[3] x1 = [true, 12, false];
    x1[index] = 100.1;
}

function invalidIndexReferenceJSONArray() {
    json[3] x1 = [1, true, "3"];
    json[] x2 = x1;
    x2[3] = 1.0;
}

function createJSONDefaultSealedArray() returns (json[], int) {
    json[5] sealedArray;
    return (sealedArray, lengthof sealedArray);
}

function testSealedArrayConstrainedMap (int[3] x1, int[] x2) returns int {
    map<int[]> x;
    x["v1"] = x1;
    x["v2"] = x2;
    return x.v1[2];
}

function testSealedArrayConstrainedMapInvalidIndex (int[3] x1, int index) {
    map<int[]> x;
    x["v1"] = x1;
    x.v1[index] = 4;
}
