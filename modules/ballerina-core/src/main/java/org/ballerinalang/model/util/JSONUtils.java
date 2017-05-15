/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.model.util;

import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.ballerinalang.model.StructDef;
import org.ballerinalang.model.VariableDef;
import org.ballerinalang.model.statements.VariableDefStmt;
import org.ballerinalang.model.types.BArrayType;
import org.ballerinalang.model.types.BType;
import org.ballerinalang.model.types.BTypes;
import org.ballerinalang.model.values.BArray;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BJSON;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.exceptions.BLangExceptionHelper;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.ballerinalang.util.exceptions.RuntimeErrors;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Common utility methods used for JSON manipulation.
 * 
 * @since 0.87
 */
public class JSONUtils {

    private static final String NULL = "null";
    
    /**
     * Convert {@link BJSON} to {@link BInteger}.
     * 
     * @param json JSON to be converted
     * @return BInteger value of the JSON, if its a integer or a long JSON node. Error, otherwise.
     */
    public static BInteger toBInteger(BJSON json) {
        return jsonNodeToInteger(json.value());
    }

    /**
     * Convert {@link BJSON} to {@link BFloat}.
     * 
     * @param json JSON to be converted
     * @return BFloat value of the JSON, if its a double or a float JSON node. Error, otherwise.
     */
    public static BFloat toBFloat(BJSON json) {
        return jsonNodeToFloat(json.value());
    }
    
    /**
     * Convert {@link BJSON} to {@link BBoolean}.
     * 
     * @param json JSON to be converted
     * @return BBoolean value of the JSON, if its a boolean node. Error, otherwise.
     */
    public static BBoolean toBBoolean(BJSON json) {
        return jsonNodeToBoolean(json.value());
    }
    
    /**
     * Convert {@link BJSON} to {@link BMap}.
     * 
     * @param json JSON to be converted
     * @return If the provided JSON is of object-type, this method will return a {@link BMap} containing the values 
     * of the JSON object. Otherwise a {@link BallerinaException} will be thrown.
     */
    public static BMap<BString, ?> toBMap(BJSON json) {
        return jsonNodeToMap(json.value());
    }
    
    /**
     * Converts a JSON array to {@link BArray}.
     * 
     * @param json JSON to be converted
     * @param arrayType Type of the target array
     * @return If the provided {@link BJSON} is of array type, this method will return a {@link BArray} containing 
     * the values of the JSON array. Otherwise the method will throw a {@link BallerinaException}.
     */
    public static BArray<?> toBArray(BJSON json, BArrayType arrayType) {
        return jsonNodeToArray(json.value(), arrayType);
    }
    
    /**
     * Convert {@link BJSON} to {@link BStruct}.
     * 
     * @param json JSON to be converted to struct
     * @param structDef Target struct type
     * @return If the provided JSON is of object-type, this method will return a {@link BStruct} containing the values 
     * of the JSON object. Otherwise the method will throw a {@link BallerinaException}.
     */
    public static BValue toBStruct(BJSON json, StructDef structDef) {
        return jsonNodeToStruct(json.value(), structDef);
    }
    
    /**
     * Convert {@link BMap} to {@link BJSON}.
     * 
     * @param map {@link BMap} to be converted to {@link BJSON}
     * @return JSON representation of the provided map
     */
    public static BJSON toJSON(BMap<BString, BValue> map) {
        Set<BString> keys = map.keySet();
        BJSON bjson = new BJSON("{}");
        ObjectNode jsonNode = (ObjectNode) bjson.value();
        for (BString key : keys) {
            try {
                BValue bvalue = map.get(key);
                if (bvalue == null) {
                    jsonNode.set(key.stringValue(), new BJSON(NULL).value());
                } else if (bvalue.getType() == BTypes.typeString) {
                    jsonNode.put(key.stringValue(), bvalue.stringValue());
                } else if (bvalue.getType() == BTypes.typeInt) {
                    jsonNode.put(key.stringValue(), ((BInteger) bvalue).intValue());
                } else if (bvalue.getType() == BTypes.typeFloat) {
                    jsonNode.put(key.stringValue(), ((BFloat) bvalue).floatValue());
                } else if (bvalue.getType() == BTypes.typeBoolean) {
                    jsonNode.put(key.stringValue(), ((BBoolean) bvalue).booleanValue());
                } else if (bvalue.getType() == BTypes.typeMap) {
                    jsonNode.set(key.stringValue(), toJSON((BMap<BString, BValue>) bvalue).value());
                } else if (bvalue.getType() == BTypes.typeJSON) {
                    jsonNode.set(key.stringValue(), ((BJSON) bvalue).value());
                } else if (bvalue instanceof BArray) {
                    jsonNode.set(key.stringValue(), toJSON((BArray<?>) bvalue).value());
                } else if (bvalue instanceof BStruct) {
                    jsonNode.set(key.stringValue(), toJSON((BStruct) bvalue).value());
                } else {
                    throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.INCOMPATIBLE_TYPE_FOR_CASTING,
                            BTypes.typeJSON, bvalue.getType());
                }
            } catch (BallerinaException e) {
                handleError(e, key.stringValue());
            }
        }
        return bjson;
    }
    
    /**
     * Convert {@link BArray} to {@link BJSON}.
     * 
     * @param array {@link BArray} to be converted to {@link BJSON}
     * @return JSON representation of the provided array
     */
    public static BJSON toJSON(BArray<?> array) {
        BJSON bjson = new BJSON("[]");
        ArrayNode arrayNode = (ArrayNode) bjson.value();
        for (int i = 0; i < array.size(); i++) {
            BValue bvalue = array.get(i);
            if (bvalue == null) {
                arrayNode.add(new BJSON(NULL).value());
            } else if (bvalue.getType() == BTypes.typeString) {
                arrayNode.add(bvalue.stringValue());
            } else if (bvalue.getType() == BTypes.typeInt) {
                arrayNode.add(((BInteger) bvalue).intValue());
            } else if (bvalue.getType() == BTypes.typeFloat) {
                arrayNode.add(((BFloat) bvalue).floatValue());
            } else if (bvalue.getType() == BTypes.typeBoolean) {
                arrayNode.add(((BBoolean) bvalue).booleanValue());
            } else if (bvalue.getType() == BTypes.typeMap) {
                arrayNode.add(toJSON((BMap<BString, BValue>) bvalue).value());
            } else if (bvalue.getType() == BTypes.typeJSON) {
                arrayNode.add(((BJSON) bvalue).value());
            } else if (bvalue instanceof BArray) {
                arrayNode.add(toJSON((BArray<?>) bvalue).value());
            } else if (bvalue instanceof BStruct) {
                arrayNode.add(toJSON((BStruct) bvalue).value());
            } else {
                throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.INCOMPATIBLE_TYPE_FOR_CASTING,
                        BTypes.typeJSON, bvalue.getType());
            }
        }
        return bjson;
    }
    
    /**
     * Convert {@link BStruct} to {@link BJSON}.
     * 
     * @param struct {@link BStruct} to be converted to {@link BJSON}
     * @return JSON representation of the provided array
     */
    public static BJSON toJSON(BStruct struct) {
        BJSON bjson = new BJSON("{}");
        ObjectNode jsonNode = (ObjectNode) bjson.value();
        StructDef structDef = (StructDef) struct.getType();
        int memoryOffset = 0;
        for (VariableDefStmt fieldDef : structDef.getFieldDefStmts()) {
            BValue bvalue = struct.getValue(memoryOffset++);
            String key = fieldDef.getVariableDef().getSymbolName().getName();
            try {
                if (bvalue == null) {
                    jsonNode.set(key, new BJSON(NULL).value());
                } else if (bvalue.getType() == BTypes.typeString) {
                    jsonNode.put(key, bvalue.stringValue());
                } else if (bvalue.getType() == BTypes.typeInt) {
                    jsonNode.put(key, ((BInteger) bvalue).intValue());
                } else if (bvalue.getType() == BTypes.typeFloat) {
                    jsonNode.put(key, ((BFloat) bvalue).floatValue());
                } else if (bvalue.getType() == BTypes.typeBoolean) {
                    jsonNode.put(key, ((BBoolean) bvalue).booleanValue());
                } else if (bvalue.getType() == BTypes.typeMap) {
                    jsonNode.set(key, toJSON((BMap<BString, BValue>) bvalue).value());
                } else if (bvalue.getType() == BTypes.typeJSON) {
                    jsonNode.set(key, ((BJSON) bvalue).value());
                } else if (bvalue instanceof BArray) {
                    jsonNode.set(key, toJSON((BArray<?>) bvalue).value());
                } else if (bvalue instanceof BStruct) {
                    jsonNode.set(key, toJSON((BStruct) bvalue).value());
                } else {
                    throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.INCOMPATIBLE_TYPE_FOR_CASTING,
                            BTypes.typeJSON, bvalue.getType());
                }
            } catch (BallerinaException e) {
                handleError(e, key);
            }
        }

        return bjson;
    }
    
    /**
     * Get an element from a JSON.
     * 
     * @param json JSON object to get the element from
     * @param elementName Name of the element to be retrieved
     * @return Element of JSON having the provided name
     */
    public static BJSON getElement(BJSON json, String elementName) {
        JsonNode jsonNode = json.value();
        
        if (!jsonNode.isObject()) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.CANNOT_GET_VALUE_INCOMPATIBLE_TYPES,
                    elementName, getComplexObjectTypeName(JsonNodeType.OBJECT), getTypeName(jsonNode));
        }
        
        try {
            JsonNode element = jsonNode.get(elementName);
            if (element == null || element.isNull()) {
                return null;
            }
            return new BJSON(element);
        } catch (Throwable t) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.JSON_GET_ERROR, t.getMessage());
        }
    }
    
    /**
     * Set an element in a JSON. If an element with the given name already exists,
     * this method will update the existing element. Otherwise, a new element with
     * the given name will be added.
     * 
     * @param json JSON object to set the element
     * @param elementName Name of the element to be set
     */
    public static void setElement(BJSON json, String elementName, BJSON element) {
        JsonNode jsonNode = json.value();
        JsonNode jsonElement = element == null ? null : element.value();

        if (!jsonNode.isObject()) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.CANNOT_SET_VALUE_INCOMPATIBLE_TYPES,
                    elementName, getComplexObjectTypeName(JsonNodeType.OBJECT), getTypeName(jsonNode));
        }

        try {
            ((ObjectNode) jsonNode).set(elementName, jsonElement);
        } catch (Throwable t) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.JSON_SET_ERROR, t.getMessage());
        }
    }
    
    /**
     * Get an element from a JSON array.
     * 
     * @param json JSON array to get the element from
     * @param index Index of the element needed
     * @return Element at the given index, if the provided JSON is an array. Error, otherwise. 
     */
    public static BJSON getArrayElement(BJSON json, long index) {
        JsonNode jsonNode = json.value();

        if (!jsonNode.isArray()) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.CANNOT_GET_VALUE_INCOMPATIBLE_TYPES,
                    index, getComplexObjectTypeName(JsonNodeType.ARRAY), getTypeName(jsonNode));
        }

        try {
            if (jsonNode.size() <= index) {
                throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.ARRAY_INDEX_OUT_OF_RANGE,
                    index, jsonNode.size());
            }
            JsonNode element = jsonNode.get((int) index);
            if (element == null || element.isNull()) {
                return null;
            }
            return new BJSON(element);
        } catch (Throwable t) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.JSON_GET_ERROR, t.getMessage());
        }
    }
    
    /**
     * Set an element in the given position of a JSON array. This method will update the existing value.
     * 
     * @param json JSON array to set the element
     * @param index Index of the element to be set
     * @param element Element to be set
     */
    public static void setArrayElement(BJSON json, long index, BJSON element) {
        JsonNode jsonNode = json.value();
        
        if (!jsonNode.isArray()) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.CANNOT_SET_VALUE_INCOMPATIBLE_TYPES,
                    index, getComplexObjectTypeName(JsonNodeType.ARRAY), getTypeName(jsonNode));
        }
        
        JsonNode jsonElement = element == null ? null : element.value();
        try {
            if (jsonNode.size() <= index) {
                throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.ARRAY_INDEX_OUT_OF_RANGE,
                    index, jsonNode.size());
            }
            ((ArrayNode) jsonNode).set((int) index, jsonElement);
        } catch (Throwable t) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.JSON_SET_ERROR, t.getMessage());
        }
    }
    
    /**
     * Convert {@link JsonNode} to {@link BInteger}.
     * 
     * @param jsonNode {@link JsonNode} to be converted
     * @return BInteger value of the JSON, if its a integer or a long JSON node. Error, otherwise.
     */
    private static BInteger jsonNodeToInteger(JsonNode jsonNode) {
        if (jsonNode.isInt() || jsonNode.isLong()) {
            return new BInteger(jsonNode.longValue());
        }
        
        throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.INCOMPATIBLE_TYPE_FOR_CASTING_JSON,
            BTypes.typeInt, getTypeName(jsonNode));
    }

    /**
     * Convert {@link JsonNode} to {@link BFloat}.
     * 
     * @param jsonNode {@link JsonNode} to be converted
     * @return BFloat value of the JSON, if its a double or a float JSON node. Error, otherwise.
     */
    private static BFloat jsonNodeToFloat(JsonNode jsonNode) {
        if (jsonNode.isFloat() || jsonNode.isDouble()) {
            return new BFloat(jsonNode.doubleValue());
        }
        
        throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.INCOMPATIBLE_TYPE_FOR_CASTING_JSON,
            BTypes.typeFloat, getTypeName(jsonNode));
    }
    
    /**
     * Convert {@link JsonNode} to {@link BBoolean}. 
     * 
     * @param jsonNode {@link JsonNode} to be converted
     * @return BBoolean value of the JSON, if its a boolean node. Error, otherwise.
     */
    private static BBoolean jsonNodeToBoolean(JsonNode jsonNode) {
        if (jsonNode.isBoolean()) {
            return new BBoolean(jsonNode.booleanValue());
        }
        
        throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.INCOMPATIBLE_TYPE_FOR_CASTING_JSON,
            BTypes.typeBoolean, getTypeName(jsonNode));
    }
    
    /**
     * Convert a JSON node to a map.
     * 
     * @param jsonNode JSON to convert
     * @return If the provided JSON is of object-type, this method will return a {@link BMap} containing the values 
     * of the JSON object. Otherwise a {@link BallerinaException} will be thrown.
     */
    private static BMap<BString, ?> jsonNodeToMap(JsonNode jsonNode) {
        BMap<BString, BValue> map = BTypes.typeMap.getEmptyValue();

        if (!jsonNode.isObject()) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.INCOMPATIBLE_TYPE_FOR_CASTING,
                    getComplexObjectTypeName(JsonNodeType.OBJECT), getTypeName(jsonNode));
        }

        Iterator<Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> field = fields.next();
            map.put(new BString(field.getKey()), getBValue(field.getValue()));
        }
        return map;
    }
    
    /**
     * Convert a JSON node to a user defined struct.
     * 
     * @param jsonNode JSON to convert
     * @param structDef Type (definition) of the target struct
     * @return If the provided JSON is of object-type, this method will return a {@link BStruct} containing the values 
     * of the JSON object. Otherwise the method will throw a {@link BallerinaException}.
     */
    private static BStruct jsonNodeToStruct(JsonNode jsonNode, StructDef structDef) {
        BValue[] structMemoryBlock = new BValue[structDef.getStructMemorySize()];
        int memoryOffset = 0;

        if (!jsonNode.isObject()) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.INCOMPATIBLE_TYPE_FOR_CASTING,
                    getComplexObjectTypeName(JsonNodeType.OBJECT), getTypeName(jsonNode));
        }

        for (VariableDefStmt fieldDef : structDef.getFieldDefStmts()) {
            VariableDef varDef = fieldDef.getVariableDef();
            BType fieldType = varDef.getType();
            String fieldName = varDef.getSymbolName().getName();
            if (!jsonNode.has(fieldName)) {
                throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.MISSING_FIELD, fieldName);
            }
            
            BValue bValue = null;
            try {
                JsonNode jsonValue = jsonNode.get(fieldName);
                if (fieldType == BTypes.typeAny) {
                    bValue = new BJSON(jsonValue);
                } else {
                    bValue = getBValue(jsonValue, fieldType);
                }
            } catch (BallerinaException e) {
                handleError(e, fieldName);
            }
            
            structMemoryBlock[memoryOffset++] = bValue;
        }

        return new BStruct(structDef, structMemoryBlock);
    }
    
    /**
     * Convert a JSON node to an array.
     * 
     * @param jsonNode JSON to convert
     * @param targetArrayType Type of the target array
     * @return If the provided JSON is of array type, this method will return a {@link BArray} containing the values 
     * of the JSON array. Otherwise the method will throw a {@link BallerinaException}.
     */
    private static BArray<?> jsonNodeToArray(JsonNode jsonNode, BArrayType targetArrayType) {
        BType elementType = targetArrayType.getElementType();
        BArray<?> array = targetArrayType.getEmptyValue();
        
        if (!jsonNode.isArray()) {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.INCOMPATIBLE_TYPE_FOR_CASTING,
                    getComplexObjectTypeName(JsonNodeType.ARRAY), getTypeName(jsonNode));
        }
        ArrayNode arrayNode = (ArrayNode) jsonNode;
        if (elementType == BTypes.typeAny) {
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode element = arrayNode.get(i);
                array.add(i, getBValue(element));
            }
        } else {
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode jsonValue = arrayNode.get(i);
                BValue bValue = getBValue(jsonValue, elementType);
                array.add(i, bValue);
            }
        }
        return array;
    }
    
    /**
     * Convert the jsonValue to the targetType and get the BValue of it. If the targetType and the 
     * type of the jsonValue are not the same, this method will try to implicitly cast the value
     * to the target type, if possible.
     * 
     * @param jsonValue JSON value to be converted
     * @param targetType Type of the expected BValue
     * @return BValue containing the value of the json
     */
    private static BValue getBValue(JsonNode jsonValue, BType targetType) {
        if ((jsonValue == null || jsonValue.isNull()) && !BTypes.isValueType(targetType)) {
            return null;
        } else if (targetType == BTypes.typeString) {
            if (jsonValue.isTextual()) {
                return new BString(jsonValue.textValue());
            }
            return new BString(jsonValue.toString());
        } else if (targetType == BTypes.typeInt) {
            return jsonNodeToInteger(jsonValue);
        } else if (targetType == BTypes.typeFloat) {
            return jsonNodeToFloat(jsonValue);
        } else if (targetType == BTypes.typeBoolean) {
            return jsonNodeToBoolean(jsonValue);
        } else if (targetType == BTypes.typeJSON) {
            return new BJSON(jsonValue);
        } else if (targetType == BTypes.typeMap) {
            return jsonNodeToMap(jsonValue);
        } else if (targetType instanceof StructDef) {
            return jsonNodeToStruct(jsonValue, (StructDef) targetType);
        } else if (targetType instanceof BArrayType) {
            return jsonNodeToArray(jsonValue, (BArrayType) targetType);
        } else {
            throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.INCOMPATIBLE_TYPE_FOR_CASTING,
                    targetType, getTypeName(jsonValue));
        }
    }
    /**
     * Get the corresponding BValue to hold the json-value, depending on its type.
     * 
     * @param json json node to get the BValue
     * @return BValue represents provided json
     */
    private static BValue getBValue(JsonNode json) {
        if (json == null || json.isNull()) {
            return null;
        } else if (json.isTextual()) {
            return new BString(json.textValue());
        } else if (json.isInt() || json.isLong()) {
            return new BInteger(json.longValue());
        } else if (json.isFloat() || json.isDouble()) {
            return new BFloat(json.doubleValue());
        } else if (json.isBoolean()) {
            return new BBoolean(json.booleanValue());
        }
        
        return new BJSON(json);
    }
    
    private static String getTypeName(JsonNode jsonValue) {
        JsonNodeType nodeType = jsonValue.getNodeType();
        switch(nodeType) {
            case NUMBER:
                NumberType numberType = jsonValue.numberType();
                switch(numberType) {
                    case DOUBLE:
                        return NumberType.FLOAT.name().toLowerCase();
                    case LONG:
                        return NumberType.INT.name().toLowerCase();
                    default:
                        return numberType.name().toLowerCase();
                }
            case ARRAY:
            case OBJECT:
                return getComplexObjectTypeName(nodeType);
            default:
                return nodeType.name().toLowerCase();
        }
    }
    
    private static String getComplexObjectTypeName(JsonNodeType nodeType) {
        return "json-" + nodeType.name().toLowerCase();
    }
    
    private static void handleError(BallerinaException e, String fieldName) {
        String errorMsg = e.getCause() == null ? "error while mapping '" + fieldName + "': " : "";
        throw new BallerinaException(errorMsg + e.getMessage(), e);
    }
}
