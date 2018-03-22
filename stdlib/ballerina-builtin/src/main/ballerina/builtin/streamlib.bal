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

package ballerina.builtin;

@Description {value:"Publish a struct to the stream"}
@Param {value:"s: The stream to which publishing is to be done"}
@Param {value:"data: The struct with data, to be published"}
public native function <stream s> publish (any data);

@Description {value:"Subscribe to structs from a stream"}
@Param {value:"s: The stream to which subscription is to be done"}
@Param {value:"func: The function pointer for subscription"}
public native function <stream s> subscribe (function (any) func);

@Description {value:"Creates the whenever runtime"}
@Param {value:"streamQuery: The siddhi query by which the siddhi app runtime is created"}
@Param {value:"inStreamRefs: References of the input streams in the whenever"}
@Param {value:"inTableRefs: References of the input tables in the whenever"}
@Param {value:"outStreamRefs: References of the output streams in the whenever"}
@Param {value:"outTableRefs: References of the output tables in the whenever"}
@Param {value:"funcPointers: References of the functions to invoke as the streaming action"}
public native function startWhenever (string streamQuery, any inStreamRefs, any inTableRefs, any outStreamRefs,
                                       any outTableRefs, any funcPointers);
