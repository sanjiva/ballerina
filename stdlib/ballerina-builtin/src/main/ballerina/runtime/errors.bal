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

package ballerina.runtime;

public struct NullReferenceException {
    string message;
    error[] cause;
}

public struct IllegalStateException {
    string message;
    error[] cause;
}

public struct CallStackElement {
    string callableName;
    string packageName;
    string fileName;
    int lineNumber;
}

public native function getCallStack () returns (CallStackElement[]);

public native function getErrorCallStackFrame (error e) returns (CallStackElement);

public struct CallFailedException {
    string message;
    error[] cause;
}
