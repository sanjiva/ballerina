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

import ballerina.io;
import ballerina.runtime;

struct StatusCount {
    string status;
    int totalCount;
}

struct Teacher {
    string name;
    int age;
    string status;
    string batch;
    string school;
}

StatusCount[] globalStatusCountArray = [];
int index = 0;
stream<StatusCount> filteredStatusCountStream = {};
stream<Teacher> teacherStream = {};

streamlet aggregationStreamlet () {
        from teacherStream where age > 18 window lengthBatch(3)
        select status, count(status) as totalCount
        group by status
        having totalCount > 1
        insert into filteredStatusCountStream
}


function testAggregationQuery () (StatusCount []) {

    aggregationStreamlet pStreamlet = {};

    Teacher t1 = {name:"Raja", age:25, status:"single", batch:"LK2014", school:"Hindu College"};
    Teacher t2 = {name:"Shareek", age:33, status:"single", batch:"LK1998", school:"Thomas College"};
    Teacher t3 = {name:"Nimal", age:45, status:"married", batch:"LK1988", school:"Ananda College"};

    filteredStatusCountStream.subscribe(printStatusCount);

    teacherStream.publish(t1);
    teacherStream.publish(t2);
    teacherStream.publish(t3);

    runtime:sleepCurrentWorker(1000);
    pStreamlet.stop();
    return globalStatusCountArray;
}

function printStatusCount (StatusCount s) {
    io:println("printStatusCount function invoked for status:" + s.status +" and total count :"+s.totalCount);
    addToGlobalStatusCountArray(s);
}

function addToGlobalStatusCountArray (StatusCount s) {
    globalStatusCountArray[index] = s;
    index = index + 1;
}