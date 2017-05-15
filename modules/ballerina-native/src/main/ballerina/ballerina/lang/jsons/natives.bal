package ballerina.lang.jsons;

import ballerina.doc;

@doc:Description { value:"Inserts a Boolean to a JSON array. This function will add a new Boolean element to the end of the JSON array identified by the given JSONPath."}
@doc:Param { value:"j: A JSON array object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"value: A Boolean value" }
native function add (json j, string jsonPath, boolean value);

@doc:Description { value:"Evaluates the JSONPath on a JSON object and returns the integer value."}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Return { value:"float: The float element on the specified path" }
native function getFloat (json j, string jsonPath) (float);

@doc:Description { value:"Sets the float value of the element that matches the given JSONPath. If the JSONPath doesn't match any element, this operation will have no effect."}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"value: A float value" }
native function set (json j, string jsonPath, float value);

@doc:Description { value:"Sets the JSON value of the element that matches the given JSONPath. If the JSONPath doesn't match any element, this operation will have no effect."}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"value: A JSON value" }
native function set (json j, string jsonPath, json value);

@doc:Description { value:"Inserts a named element to a JSON object. This method will add a new JSON element with the given name (key)"}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"key: The name of the element to be added" }
@doc:Param { value:"value: A JSON value" }
native function add (json j, string jsonPath, string key, json value);

@doc:Description { value:"Inserts an element to a JSON array. This function will add a new string element to the end of the JSON array identified by the given JSONPath."}
@doc:Param { value:"j: A JSON array object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"value: A string value" }
native function add (json j, string jsonPath, string value);

@doc:Description { value:"Removes each element that matches the given JSONPath."}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
native function remove (json j, string jsonPath);

@doc:Description { value:"Inserts a named element to a JSON object. This function will add a new Boolean element with the given name (key) to the location identified by the given JSONPath."}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"key: The name of the element to be added" }
@doc:Param { value:"value: A Boolean value" }
native function add (json j, string jsonPath, string key, boolean value);

@doc:Description { value:"Sets the string value of the element that matches the given JSONPath. If the JSONPath doesn't match any element, this operation will have no effect."}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"value: A string value" }
native function set (json j, string jsonPath, string value);

@doc:Description { value:"Evaluates the JSONPath on a JSON object and returns the integer value."}
@doc:Param { value:"j: A json object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Return { value:"int: The integer element on the specified path" }
native function getInt (json j, string jsonPath) (int);

@doc:Description { value:"Evaluates the JSONPath on a JSON object and returns the Boolean value."}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Return { value:"boolean: The Boolean element on the specified path " }
native function getBoolean (json j, string jsonPath) (boolean);

@doc:Description { value:"Inserts a named element to a JSON object. This function will add a new float element with the given name (key)"}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"key: The name of the element to be added" }
@doc:Param { value:"value: A float value" }
native function add (json j, string jsonPath, string key, float value);

@doc:Description { value:"Evaluates the JSONPath on a JSON object and returns the string value"}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Return { value:"string: The string element on the specified path" }
native function getString (json j, string jsonPath) (string);

@doc:Description { value:"Sets the Boolean value of the element that matches the given JSONPath. If the JSONPath doesn't match any element, this operation will have no effect."}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"value: A Boolean value" }
native function set (json j, string jsonPath, boolean value);

@doc:Description { value:"Inserts a JSON element to a JSON array. This function will add a new JSON element to the end of the JSON array identified by the given JSONPath."}
@doc:Param { value:"j: A JSON array object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"value: The JSON element to be added" }
native function add (json j, string jsonPath, json value);

@doc:Description { value:"Inserts an integer element to a JSON array. This function will add a new integer element to the end of the JSON array identified by the given JSONPath."}
@doc:Param { value:"j: A JSON array object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"value: An integer value" }
native function add (json j, string jsonPath, int value);

@doc:Description { value:"Sets the integer value of the element that matches the given JSONPath. If the JSONPath doesn't match any element, this operation will have no effect."}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"value: An integer value" }
native function set (json j, string jsonPath, int value);

@doc:Description { value:"Evaluates the JSONPath on a JSON object and returns the matching JSON."}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Return { value:"json: The JSON element on the specified path" }
native function getJson (json j, string jsonPath) (json);

@doc:Description { value:"Inserts a named element to a JSON object. This function will add a new string element with the given name (key)"}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"key: The name of the element to be added" }
@doc:Param { value:"value: A string value" }
native function add (json j, string jsonPath, string key, string value);

@doc:Description { value:"Inserts a named element to a JSON object. This function will add a new integer element with the given name (key)"}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"key: The name of the element to be added" }
@doc:Param { value:"value: An integer value" }
native function add (json j, string jsonPath, string key, int value);

@doc:Description { value:"Converts a JSON object to a string representation"}
@doc:Param { value:"j: A JSON object" }
@doc:Return { value:"string: String value of the converted JSON" }
native function toString (json j) (string);

@doc:Description { value:"Inserts a float to a JSON array. This function will add a new float element to the end of the JSON array identified by the given JSONPath."}
@doc:Param { value:"j: A JSON array object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"value: A float value" }
native function add (json j, string jsonPath, float value);

@doc:Description { value:"Renames the key of the given element that is under the given JSONPath."}
@doc:Param { value:"j: A JSON object" }
@doc:Param { value:"jsonPath: The path of the JSON element" }
@doc:Param { value:"oldKey: The old key value" }
@doc:Param { value:"newKey: The new key value to use" }
native function rename (json j, string jsonPath, string oldKey, string newKey);

