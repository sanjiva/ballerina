import ballerina/io;
import ballerina/util;

function remove () returns (json) {
    json j = {"name":{"fname":"Jack", "lname":"Taylor"}, "state":"CA", "age":20};
    _ = j.remove("name");
    return j;
}

function toString (json msg) returns (string) {
    return msg.toString();
}

function testParse (string jsonStr) returns (json | error) {
    return util:parseJson(jsonStr);
}

function testGetKeys () returns (string[], string[], string[], string[]) {
    json j1 = {fname:"Jhon", lname:"Doe", age:40};
    json j2 = ["cat", "dog", "horse"];
    json j3 = "Hello";
    json j4 = 5;
    return (j1.getKeys(), j2.getKeys(), j3.getKeys(), j4.getKeys());
}

function testToXML (json msg) returns (xml | error) {
    return msg.toXML({});
}

function testToXMLStringValue () returns (xml | error) {
    json j = "value";
    return j.toXML({});
}

function testToXMLBooleanValue () returns (xml | error) {
    json j = true;
    return j.toXML({});
}

function testToXMLString (json msg) returns (string) {
    var x = msg.toXML({});
    match(x){
        error e => return "";
        xml xmlData => return io:sprintf("%s", [xmlData]);
    }
}

function testToXMLWithXMLSequence (json msg) returns (string) {
    var x = msg.toXML({});
    match(x){
        error e => return "";
        xml xmlData => return io:sprintf("%s", [xmlData]);
    }
}

function testToXMLWithOptions (json msg) returns (xml | error) {
    return msg.toXML({attributePrefix:"#", arrayEntryTag:"wrapper"});
}

function testStringToJSONConversion() returns (json | error) {
    string s = "{\"foo\": \"bar\"}";
    return util:parseJson(s);
}

function testJSONArrayToJsonAssignment() returns (json) {
    json[] j1 = [{"a":"b"}, {"c":"d"}];
    json j2 = j1;
    return j2;
}
