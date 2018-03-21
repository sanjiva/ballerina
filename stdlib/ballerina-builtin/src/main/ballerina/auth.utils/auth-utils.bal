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

package ballerina.auth.utils;

import ballerina/config;
import ballerina/caching;
import ballerina/util;

@Description {value:"Configuration entry to check if a cache is enabled"}
const string CACHE_ENABLED = "enabled";
@Description {value:"Configuration entry for cache expiry time"}
const string CACHE_EXPIRY_TIME = "expiryTime";
@Description {value:"Configuration entry for cache capacity"}
const string CACHE_CAPACITY = "capacity";
@Description {value:"Configuration entry for eviction factor"}
const string CACHE_EVICTION_FACTOR = "evictionFactor";
@Description {value:"Authentication header name"}
const string AUTH_HEADER = "Authorization";
@Description {value:"Basic authentication scheme"}
const string AUTH_SCHEME = "Basic";

@Description {value:"Default value for enabling cache"}
const boolean CACHE_ENABLED_DEFAULT_VALUE = true;
@Description {value:"Default value for cache expiry"}
const int CACHE_EXPIRY_DEFAULT_VALUE = 300000;
@Description {value:"Default value for cache capacity"}
const int CACHE_CAPACITY_DEFAULT_VALUE = 100;
@Description {value:"Default value for cache eviction factor"}
const float CACHE_EVICTION_FACTOR_DEFAULT_VALUE = 0.25;

@Description {value:"Creates a cache to store authentication results against basic auth headers"}
@Return {value:"cache: authentication cache instance"}
public function createCache (string cacheName) returns (caching:Cache) {
    if (isCacheEnabled(cacheName)) {
        int expiryTime;
        int capacity;
        float evictionFactor;
        expiryTime, capacity, evictionFactor = getCacheConfigurations(cacheName);
        return caching:createCache(cacheName, expiryTime, capacity, evictionFactor);
    }
    return null;
}

@Description {value:"Checks if the specified cache is enalbed"}
@Param {value:"cacheName: cache name"}
@Return {value:"boolean: true of the cache is enabled, else false"}
function isCacheEnabled (string cacheName) returns (boolean) {
    string isCacheEnabled = config:getInstanceValue(cacheName, CACHE_ENABLED);
    if (isCacheEnabled == null) {
        // by default we enable the cache
        return CACHE_ENABLED_DEFAULT_VALUE;
    } else {
        var boolIsCacheEnabled, typeConversionErr = <boolean>isCacheEnabled;
        if (typeConversionErr != null) {
            return CACHE_ENABLED_DEFAULT_VALUE;
        } else {
            return boolIsCacheEnabled;
        }
    }
}

@Description {value:"Reads the cache configurations"}
@Param {value:"cacheName: cache name"}
@Return {value:"int: cache expiry time"}
@Return {value:"int: cache capacity"}
@Return {value:"float: cache eviction factor"}
function getCacheConfigurations (string cacheName) returns (int, int, float) {
    return getExpiryTime(cacheName), getCapacity(cacheName), getEvictionFactor(cacheName);
}

@Description {value:"Reads the cache expiry time"}
@Param {value:"cacheName: cache name"}
@Return {value:"int: cache expiry time read from ballerina.conf, default value if no configuration entry found"}
function getExpiryTime (string cacheName) returns (int) {
    // expiry time
    string expiryTime = config:getInstanceValue(cacheName, CACHE_EXPIRY_TIME);
    if (expiryTime == null) {
        // set the default
        return CACHE_EXPIRY_DEFAULT_VALUE;
    } else {
        var intExpiryTime, typeConversionErr = <int>expiryTime;
        if (typeConversionErr != null) {
            return CACHE_EXPIRY_DEFAULT_VALUE;
        }
        return intExpiryTime;
    }
}

@Description {value:"Reads the cache capacity"}
@Param {value:"cacheName: cache name"}
@Return {value:"int: cache capacity read from ballerina.conf, default value if no configuration entry found"}
function getCapacity (string cacheName) returns (int) {
    string capacity = config:getInstanceValue(cacheName, CACHE_CAPACITY);
    if (capacity == null) {
        return CACHE_CAPACITY_DEFAULT_VALUE;
    } else {
        var intCapacity, typeConversionErr = <int>capacity;
        if (typeConversionErr != null) {
            return CACHE_CAPACITY_DEFAULT_VALUE;
        }
        return intCapacity;
    }
}

@Description {value:"Reads the cache eviction factor"}
@Param {value:"cacheName: cache name"}
@Return {value:"float: cache eviction factor read from ballerina.conf, default value if no configuration entry found"}
function getEvictionFactor (string cacheName) returns (float) {
    string evictionFactor = config:getInstanceValue(cacheName, CACHE_EVICTION_FACTOR);
    if (evictionFactor == null) {
        return CACHE_EVICTION_FACTOR_DEFAULT_VALUE;
    } else {
        var floatEvictionFactor, typeConversionErr = <float>evictionFactor;
        if (typeConversionErr != null || floatEvictionFactor > 1.0) {
            return CACHE_EVICTION_FACTOR_DEFAULT_VALUE;
        }
        return floatEvictionFactor;
    }
}

@Description {value:"Extracts the basic authentication credentials from the header value"}
@Param {value:"authHeader: basic authentication header"}
@Return {value:"string: username extracted"}
@Return {value:"string: password extracted"}
@Return {value:"error: any error occurred while extracting creadentials"}
public function extractBasicAuthCredentials (string authHeader) returns (string, string, error) {
    // extract user credentials from basic auth header
    string decodedBasicAuthHeader;
    try {
        decodedBasicAuthHeader = util:base64Decode(authHeader.subString(5, authHeader.length()).trim());
    } catch (error err) {
        return null, null, err;
    }
    string[] decodedCredentials = decodedBasicAuthHeader.split(":");
    if (lengthof decodedCredentials != 2) {
        return null, null, handleError("Incorrect basic authentication header format");
    } else {
        return decodedCredentials[0], decodedCredentials[1], null;
    }
}

@Description {value:"Error handler"}
@Param {value:"message: error message"}
@Return {value:"error: error populated with the message"}
function handleError (string message) returns (error) {
    error e = {message:message};
    return e;
}
