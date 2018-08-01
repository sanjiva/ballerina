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


documentation {
    Halts the current worker for a predefined amount of time.

    P{{millis}} Amount of time to sleep in milliseconds
}
public extern function sleep(int millis);

// Todo - Remove
documentation {
    Returns the value associated with the specified property name.

    P{{name}} Name of the property
    R{{}} Value of the property if the property exists, an empty string otherwise
}
public extern function getProperty(@sensitive string name) returns (string);
