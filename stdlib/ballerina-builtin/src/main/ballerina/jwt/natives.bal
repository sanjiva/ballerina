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

package ballerina.jwt;

//JOSH header parameters
const string ALG = "alg";
const string TYP = "typ";
const string CTY = "cty";
const string KID = "kid";

//Payload parameters
const string ISS = "iss";
const string SUB = "sub";
const string AUD = "aud";
const string JTI = "jti";
const string EXP = "exp";
const string NBF = "nbf";
const string IAT = "iat";

@Description {value:"Represents a JWT header"}
public struct Header {
    string alg;
    string typ;
    string cty;
    string kid;
    map customClaims;
}

@Description {value:"Represents a JWT payload"}
public struct Payload {
    string iss;
    string sub;
    string[] aud;
    string jti;
    int exp;
    int nbf;
    int iat;
    map customClaims;
}
