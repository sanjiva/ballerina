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

import ballerina/log;
import ballerina/time;
import ballerina/util;

@Description {value:"Represents JWT validator configurations"}
public struct JWTValidatorConfig {
    string issuer;
    string audience;
    string certificateAlias;
}

@Description {value:"Validity given JWT token"}
@Param {value:"jwtToken: JWT token that need to validate"}
@Param {value:"config: JWTValidatorConfig object"}
@Return {value:"boolean: If JWT token is valied true , else false"}
@Return {value:"Payload: If JWT token is valied return the JWT payload"}
@Return {value:"error: If token validation fails"}
public function validate (string jwtToken, JWTValidatorConfig config) returns (Payload|boolean|error) {
    string[] encodedJWTComponents;
    match getJWTComponents(jwtToken) {
        string[] encodedJWT => encodedJWTComponents = encodedJWT;
        error e => return e;
    }

    Header header = {};
    Payload payload = {};
    match parseJWT(encodedJWTComponents) {
        error e => return e;
        (Header, Payload) result => {
            (header, payload) = result;
        }
    }

    match validateJWT(encodedJWTComponents, header, payload, config) {
        error e => return e;
        boolean isValid => return isValid ? payload : false;
    }
}

function getJWTComponents (string jwtToken) returns (string[])|error {
    string[] jwtComponents = jwtToken.split("\\.");
    if (lengthof jwtComponents != 3) {
        log:printDebug("Invalid JWT token :" + jwtToken);
        error err = {message:"Invalid JWT token"};
        return err;
    }
    return jwtComponents;
}

function parseJWT (string[] encodedJWTComponents) returns ((Header, Payload)|error) {
    json headerJson = {};
    json payloadJson = {};
    match getDecodedJWTComponents(encodedJWTComponents) {
        error e => return e;
        (json, json) result => {
            (headerJson, payloadJson) = result;
        }
    }

    Header jwtHeader = parseHeader(headerJson);
    Payload jwtPayload = parsePayload(payloadJson);
    return (jwtHeader, jwtPayload);
}

function getDecodedJWTComponents (string[] encodedJWTComponents) returns ((json, json)|error) {
    string jwtHeader = util:base64Decode(urlDecode(encodedJWTComponents[0]));
    string jwtPayload = util:base64Decode(urlDecode(encodedJWTComponents[1]));

    json jwtHeaderJson = <json>jwtHeader;
    json jwtPayloadJson = <json>jwtPayload;
    // TODO remove safe casting.
    //json jwtHeaderJson = {};
    //match <json>jwtHeader {
    //    json headerJson => jwtHeaderJson = headerJson;
    //    error e1 => {
    //        error err = {message:"Invalid JWT payload"};
    //        return err;
    //    }
    //}
    //json jwtPayloadJson = {};
    //match <json>jwtPayload {
    //    json payloadJson => jwtPayloadJson = payloadJson;
    //    error e2 => {
    //        error err = {message:"Invalid JWT payload"};
    //        return err;
    //    }
    //}
    return (jwtHeaderJson, jwtPayloadJson);
}

function parseHeader (json jwtHeaderJson) returns (Header) {
    Header jwtHeader = {};
    map customClaims = {};
    foreach key in jwtHeaderJson.getKeys() {
        //TODO get alg from a constant
        if (key == "alg") {
            jwtHeader.alg = jwtHeaderJson[key].toString();
        } else if (key == TYP) {
            jwtHeader.typ = jwtHeaderJson[key].toString();
        } else if (key == CTY) {
            jwtHeader.cty = jwtHeaderJson[key].toString();
        } else if (key == KID) {
            jwtHeader.kid = jwtHeaderJson[key].toString();
        } else {
            if (lengthof jwtHeaderJson[key] > 0) {
                customClaims[key] = convertToStringArray(jwtHeaderJson[key]);
            } else {
                customClaims[key] = jwtHeaderJson[key].toString();
            }
        }
    }
    jwtHeader.customClaims = customClaims;
    return jwtHeader;
}

function parsePayload (json jwtPayloadJson) returns (Payload) {
    Payload jwtPayload = {};
    map customClaims = {};
    foreach key in jwtPayloadJson.getKeys() {
        if (key == ISS) {
            jwtPayload.iss = jwtPayloadJson[key].toString();
        } else if (key == SUB) {
            jwtPayload.sub = jwtPayloadJson[key].toString();
        } else if (key == AUD) {
            jwtPayload.aud = convertToStringArray(jwtPayloadJson[key]);
        } else if (key == JTI) {
            jwtPayload.jti = jwtPayloadJson[key].toString();
        } else if (key == EXP) {
            var value = jwtPayloadJson[key].toString();
            jwtPayload.exp =? <int>value;
        } else if (key == NBF) {
            var value = jwtPayloadJson[key].toString();
            jwtPayload.nbf =? <int>value;
        } else if (key == IAT) {
            var value = jwtPayloadJson[key].toString();
            jwtPayload.iat =? <int>value;
        }
        else {
            if (lengthof jwtPayloadJson[key] > 0) {
                customClaims[key] = convertToStringArray(jwtPayloadJson[key]);
            } else {
                customClaims[key] = jwtPayloadJson[key].toString();
            }
        }
    }
    jwtPayload.customClaims = customClaims;
    return jwtPayload;
}

function validateJWT (string[] encodedJWTComponents, Header jwtHeader, Payload jwtPayload, JWTValidatorConfig config) returns (boolean|error) {
    if (!validateMandatoryFields(jwtPayload)) {
        error err = {message:"Mandatory fields(Issuer, Subject, Expiration time or Audience) are empty in the given JSON Web Token."};
        return err;
    }
    if (!validateSignature(encodedJWTComponents, jwtHeader, config)) {
        error err = {message:"Invalid signature"};
        return err;
    }
    if (!validateIssuer(jwtPayload, config)) {
        error err = {message:"No Registered IDP found for the JWT with issuer name : " + jwtPayload.iss};
        return err;
    }
    if (!validateAudience(jwtPayload, config)) {
        //TODO need to set expected audience or available audience list
        error err = {message:"Invalid audience"};
        return err;
    }
    if (!validateExpirationTime(jwtPayload)) {
        error err = {message:"JWT token is expired"};
        return err;
    }
    if (!validateNotBeforeTime(jwtPayload)) {
        error err = {message:"JWT token is used before Not_Before_Time"};
        return err;
    }
    //TODO : Need to validate jwt id (jti) and custom claims.
    return true;
}

function validateMandatoryFields (Payload jwtPayload) returns (boolean) {
    if (jwtPayload.iss == "" || jwtPayload.sub == "" || jwtPayload.exp == 0 || lengthof jwtPayload.aud == 0) {
        return false;
    }
    return true;
}

function validateSignature (string[] encodedJWTComponents, Header jwtHeader, JWTValidatorConfig config) returns (boolean) {
    string assertion = encodedJWTComponents[0] + "." + encodedJWTComponents[1];
    string signPart = encodedJWTComponents[2];
    return signature:verify(assertion, signPart, jwtHeader.alg, config.certificateAlias);
}

function validateIssuer (Payload jwtPayload, JWTValidatorConfig config) returns (boolean) {
    return jwtPayload.iss == config.issuer;
}

function validateAudience (Payload jwtPayload, JWTValidatorConfig config) returns (boolean) {
    foreach audience in jwtPayload.aud {
        if (audience == config.audience) {
            return true;
        }
    }
    return false;
}

function validateExpirationTime (Payload jwtPayload) returns (boolean) {
    return jwtPayload.exp > time:currentTime().time;
}

function validateNotBeforeTime (Payload jwtPayload) returns (boolean) {
    return time:currentTime().time > jwtPayload.nbf;
}

function convertToStringArray (json jsonData) returns (string[]) {
    string[] outData = [];
    if (lengthof jsonData > 0) {
        int i = 0;
        while (i < lengthof jsonData) {
            outData[i] = jsonData[i].toString();
            i = i + 1;
        }
    } else {
        outData[0] = jsonData.toString();
    }
    return outData;
}

function urlDecode (string encodedString) returns (string) {
    string decodedString = encodedString.replaceAll("-", "+");
    decodedString = decodedString.replaceAll("_", "/");
    return decodedString;
}
