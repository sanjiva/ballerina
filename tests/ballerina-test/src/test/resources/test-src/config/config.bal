import ballerina/config;

function testGetAsString(string key) returns (string|null) {
    return config:getAsString(key);
}

function testSetConfig(string key, string value) {
    config:setConfig(key, value);
}

function testContains(string key) returns (boolean) {
    return config:contains(key);
}
