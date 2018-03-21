// Copyright (c) 2017 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package ballerina.reflect;

@Description {value:"Check whether 2 values are deeply equal. Supports string, int, float, boolean, type, structs, maps,
 arrays, any, JSON. Any other type returns FALSE."}
@Param {value:"value1: The first value for equality."}
@Param {value:"value2: The second value for equality."}
@Return {value:"TRUE if values are deeply equal, else FALSE."}
public native function equals (any value1, any value2) returns (boolean);