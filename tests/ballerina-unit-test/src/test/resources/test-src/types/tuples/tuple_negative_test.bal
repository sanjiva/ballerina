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

function invalidTupleAssignment() {
    (int, boolean) x1 = (1);
    (int, boolean, string) x2 = (1, false);
    (int, boolean, string) x3 = (1, false, "abc");
}

type Person record {
    string name;
};

type Employee record {
    string name;
    string id;
};

function invalidTupleAssignmentToUnion() {
    (int, boolean, string)? x1 = (1, false, "abc");
    (int, boolean, string) | (any, boolean, string) | () x2 = (1, false, "abc"); // ambiguous
    Employee p = {name:"foo", id:"12"};
    (Person, int) k1 = (p, 3);
    (Person, int) | () k2 = (p, 3);
    (Person, int) | (Employee, int) | () k3 = (p, 3); // ambiguous
}

function tupleAssignmentToAnyAndVar () {
    var x1 = (1); // brace hence valid
    any x2 = (1); // brace hence valid
    var x3 = (1, 2);
    any x4 = (1, 2);
}

function testInvalidNumberedLiterals () {
    (float, int, string) x = (1.2, 3);
}

function testInvalidIndexAccess () {
    (float, int, string) x = (1.2, 3, "abc");
    any x1 = x[-1];
    any x2 = x[3];
    int index = 0;
    float x3 = x[index];
    any x4 = x["0"];
}
