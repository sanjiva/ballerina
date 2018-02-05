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

import org.apache.commons.lang3.StringEscapeUtils;
import org.ballerinalang.model.util.JsonNode.Type;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

/**
 * This class represents a JSON parser.
 * 
 * @since 0.95.5
 */
public class JsonParser {

    /**
     * Parses the contents in the given {@link InputStream} and returns a {@link JsonNode}.
     * 
     * @param in input stream which contains the JSON content
     * @return JSON structure as a {@link JsonNode} object
     * @throws BallerinaException for any parsing error
     */
    public static JsonNode parse(InputStream in) throws BallerinaException {
        return parse(in, Charset.defaultCharset().name());
    }
    
    /**
     * Parses the contents in the given {@link InputStream} and returns a {@link JsonNode}.
     * 
     * @param in input stream which contains the JSON content
     * @param charsetName the character set name of the input stream
     * @return JSON structure as a {@link JsonNode} object
     * @throws BallerinaException for any parsing error
     */
    public static JsonNode parse(InputStream in, String charsetName) throws BallerinaException {
        try {
            return parse(new InputStreamReader(new BufferedInputStream(in), charsetName));
        } catch (IOException e) {
            throw new BallerinaException("Error in parsing JSON data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parses the contents in the given string and returns a {@link JsonNode}.
     * 
     * @param jsonStr the string which contains the JSON content
     * @return JSON structure as a {@link JsonNode} object
     * @throws BallerinaException for any parsing error
     */
    public static JsonNode parse(String jsonStr) throws BallerinaException {
        return parse(new StringReader(jsonStr));
    }
    
    /**
     * Parses the contents in the given {@link Reader} and returns a {@link JsonNode}.
     * 
     * @param reader reader which contains the JSON content
     * @return JSON structure as a {@link JsonNode} object
     * @throws BallerinaException for any parsing error
     */
    public static JsonNode parse(Reader reader) throws BallerinaException {
        return new StateMachine(reader).execute();
    }
    
    /**
     * Represents a JSON parser related exception.
     */
    private static class JsonParserException extends Exception {
        
        private static final long serialVersionUID = 6359022327525293320L;

        public JsonParserException(String msg) {
            super(msg);
        }
        
    }
    
    /**
     * Represents the state machine used for JSON parsing.
     */
    private static class StateMachine {
        
        private static final char CR = 0x000D;
        private static final char NEWLINE = 0x000A;
        private static final char HZ_TAB = 0x0009;
        private static final char SPACE = 0x0020;
        private static final char BACKSPACE = 0x0008;
        private static final char FORMFEED = 0x000C;
        private static final char QUOTES = '"';
        private static final char SINGLE_QUOTES = '\'';
        private static final char REV_SOL = '\\';
        private static final char SOL = '/';
        private static final char EOF = (char) -1;
        private static final String NULL = "null";
        private static final String TRUE = "true";
        private static final String FALSE = "false";
        
        private static final State DOC_START_STATE = new DocumentStartState();
        private static final State DOC_END_STATE = new DocumentEndState();
        private static final State FIRST_FIELD_READY_STATE = new FirstFieldReadyState();
        private static final State NON_FIRST_FIELD_READY_STATE = new NonFirstFieldReadyState();
        private static final State FIELD_NAME_STATE = new FieldNameState();
        private static final State END_FIELD_NAME_STATE = new EndFieldNameState();
        private static final State FIELD_VALUE_READY_STATE = new FieldValueReadyState();
        private static final State STRING_FIELD_VALUE_STATE = new StringFieldValueState();
        private static final State NON_STRING_FIELD_VALUE_STATE = new NonStringFieldValueState();
        private static final State NON_STRING_VALUE_STATE = new NonStringValueState();
        private static final State STRING_VALUE_STATE = new StringValueState();
        private static final State FIELD_END_STATE = new FieldEndState();        
        private static final State STRING_AE_ESC_CHAR_PROCESSING_STATE = new StringAEEscapedCharacterProcessingState();
        private static final State STRING_AE_PROCESSING_STATE = new StringAEProcessingState();
        private static final State FIELD_NAME_UNICODE_HEX_PROCESSING_STATE = new FieldNameUnicodeHexProcessingState();
        private static final State FIRST_ARRAY_ELEMENT_READY_STATE = new FirstArrayElementReadyState();
        private static final State NON_FIRST_ARRAY_ELEMENT_READY_STATE = new NonFirstArrayElementReadyState();
        private static final State STRING_ARRAY_ELEMENT_STATE = new StringArrayElementState();
        private static final State NON_STRING_ARRAY_ELEMENT_STATE = new NonStringArrayElementState();
        private static final State ARRAY_ELEMENT_END_STATE = new ArrayElementEndState();        
        private static final State STRING_FIELD_ESC_CHAR_PROCESSING_STATE = 
                new StringFieldEscapedCharacterProcessingState();        
        private static final State STRING_VAL_ESC_CHAR_PROCESSING_STATE = 
                new StringValueEscapedCharacterProcessingState();        
        private static final State FIELD_NAME_ESC_CHAR_PROCESSING_STATE = 
                new FieldNameEscapedCharacterProcessingState();        
        private static final State STRING_FIELD_UNICODE_HEX_PROCESSING_STATE = 
                new StringFieldUnicodeHexProcessingState();
        private static final State STRING_VALUE_UNICODE_HEX_PROCESSING_STATE = 
                new StringValueUnicodeHexProcessingState();
        
        private Reader reader;
        private JsonNode currentJsonNode;
        private StringBuilder hexBuilder = new StringBuilder(4);
        private char[] charBuff = new char[1024];
        private int charBuffIndex;
        
        private int index;
        private int line = 1;
        private int column = 0;
        private char currentQuoteChar;
        
        public StateMachine(Reader reader) {
            this.reader = reader;
        }
        
        private static boolean isWhitespace(char ch) {
            return ch == SPACE || ch == HZ_TAB || ch == NEWLINE || ch == CR;
        }
        
        private static void throwExpected(String... chars) throws JsonParserException {
            throw new JsonParserException("expected " + String.join(" or ", chars));
        }
        
        private void processLocation(char ch) {
            if (ch == '\n') {
                this.line++;
                this.column = 0;
            } else {
                this.column++;
            }
        }
        
        public JsonNode execute() throws BallerinaException {
            State currentState = DOC_START_STATE;
            try {
                char[] buff = new char[1024];
                int count;
                while ((count = this.reader.read(buff)) > 0) {
                    this.index = 0;
                    while (this.index < count) {
                        currentState = currentState.transition(this, buff, this.index, count);
                    }
                }
                currentState = currentState.transition(this, new char[] { EOF }, 0, 1);
                if (currentState != DOC_END_STATE) {
                    throw new BallerinaException("invalid JSON document");
                }
                return this.currentJsonNode;
            } catch (IOException e) {
                throw new BallerinaException("Error reading JSON: " + e.getMessage());
            } catch (JsonParserException e) {
                throw new BallerinaException(e.getMessage() + " at line: " + this.line + " column: " + this.column);
            }
        }
        
        private void append(char ch) {
            try {
                this.charBuff[this.charBuffIndex] = ch;
                this.charBuffIndex++;
            } catch (ArrayIndexOutOfBoundsException e) {
                /* this approach is faster than checking for the size by ourself */
                this.growCharBuff();
                this.charBuff[this.charBuffIndex++] = ch;
            }
        }
        
        private void growCharBuff() {
            char[] newBuff = new char[charBuff.length * 2];
            System.arraycopy(this.charBuff, 0, newBuff, 0, this.charBuff.length);
            this.charBuff = newBuff;
        }
        
        private State finalizeObject() {
            JsonNode parentNode = currentJsonNode.parentNode;
            if (parentNode != null) {
                if (parentNode.getType() == Type.OBJECT) {
                    parentNode.set(parentNode.fieldName, currentJsonNode);
                    currentJsonNode = parentNode;
                    return FIELD_END_STATE;
                } else {
                    parentNode.add(currentJsonNode);
                    currentJsonNode = parentNode;
                    return ARRAY_ELEMENT_END_STATE;
                }
            } else {
                return DOC_END_STATE;
            }
        }
        
        private State initNewObject() {
            if (currentJsonNode != null) {
                JsonNode parentNode = currentJsonNode;
                currentJsonNode = new JsonNode();
                currentJsonNode.parentNode = parentNode;
            } else {
                currentJsonNode = new JsonNode();
            }
            return FIRST_FIELD_READY_STATE;
        }
        
        private State initNewArray() {
            if (currentJsonNode != null) {
                JsonNode parentNode = currentJsonNode;
                currentJsonNode = new JsonNode(Type.ARRAY);
                currentJsonNode.parentNode = parentNode;
            } else {
                currentJsonNode = new JsonNode(Type.ARRAY);
            }
            return FIRST_ARRAY_ELEMENT_READY_STATE;
        }
        
        /**
         * A specific state in the JSON parsing state machine.
         */
        private interface State {
            
            /**
             * Input given to the current state for a transition. 
             * 
             * @param sm the state machine
             * @param buff the input characters for the current state
             * @param i the location from the character should be read from
             * @param count the number of characters to read from the buffer
             * @return the new resulting state
             */
            State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException;
            
        }
        
        /**
         * Represents the JSON document start state.
         */
        private static class DocumentStartState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                char ch;
                State state = null;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (ch == '{') {
                        state = sm.initNewObject();
                    } else if (ch == '[') {
                        state = sm.initNewArray();
                    } else if (StateMachine.isWhitespace(ch)) {
                        state = this;
                        continue;
                    } else if (ch == QUOTES || ch == SINGLE_QUOTES) {
                        sm.currentQuoteChar = ch;
                        state = STRING_VALUE_STATE;
                    } else if (ch == EOF) {
                        throw new JsonParserException("empty JSON document");
                    } else {
                        state = NON_STRING_VALUE_STATE;
                    }
                    break;
                }
                if (state == NON_STRING_VALUE_STATE) {
                    sm.index = i;
                } else {
                    sm.index = i + 1;
                }
                return state;
            }
            
        }
        
        /**
         * Represents the JSON document end state.
         */
        private static class DocumentEndState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                char ch;
                State state = null;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (StateMachine.isWhitespace(ch) || ch == EOF) {
                        state = this;
                        continue;
                    } else {
                        throw new JsonParserException("JSON document has already ended");
                    }
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
        /**
         * Represents the state just before the first object field is defined.
         */
        private static class FirstFieldReadyState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                char ch;
                State state =  null;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (ch == QUOTES || ch == SINGLE_QUOTES) {
                        state = FIELD_NAME_STATE;
                        sm.currentQuoteChar = ch;
                    } else if (StateMachine.isWhitespace(ch)) {
                        state = this;
                        continue;
                    } else if (ch ==  '}') {
                        state = sm.finalizeObject();
                    } else {
                        StateMachine.throwExpected("\"", "}");
                    }
                    break;
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
        /**
         * Represents the state just before the first array element is defined.
         */
        private static class FirstArrayElementReadyState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (StateMachine.isWhitespace(ch)) {
                        state = this;
                        continue;
                    } else if (ch == QUOTES || ch == SINGLE_QUOTES) {
                        state = STRING_ARRAY_ELEMENT_STATE;
                        sm.currentQuoteChar = ch;
                    } else if (ch == '{') {
                        state = sm.initNewObject(); 
                    } else if (ch == '[') {
                        state = sm.initNewArray(); 
                    } else if (ch == ']') {
                        state = sm.finalizeObject(); 
                    } else {
                        state = NON_STRING_ARRAY_ELEMENT_STATE;
                    }
                    break;
                }
                if (state == NON_STRING_ARRAY_ELEMENT_STATE) {
                    sm.index = i;
                } else {
                    sm.index = i + 1;
                }
                return state;
            }
            
        }
        
        /**
         * Represents the state just before a non-first object field is defined.
         */
        private static class NonFirstFieldReadyState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (ch == QUOTES || ch == SINGLE_QUOTES) {
                        sm.currentQuoteChar = ch;
                        state = FIELD_NAME_STATE;
                    } else if (StateMachine.isWhitespace(ch)) {
                        state = this;
                        continue;
                    } else {
                        StateMachine.throwExpected("\"");
                    }
                    break;
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
        /**
         * Represents the state just before a non-first array element is defined.
         */
        private static class NonFirstArrayElementReadyState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (StateMachine.isWhitespace(ch)) {
                        state = this;
                        continue;
                    } else if (ch == QUOTES || ch == SINGLE_QUOTES) {
                        state = STRING_ARRAY_ELEMENT_STATE;
                        sm.currentQuoteChar = ch;
                    } else if (ch == '{') {
                        state = sm.initNewObject(); 
                    } else if (ch == '[') {
                        state = sm.initNewArray(); 
                    } else {
                        state = NON_STRING_ARRAY_ELEMENT_STATE;
                    }
                    break;
                }
                if (state == NON_STRING_ARRAY_ELEMENT_STATE) {
                    sm.index = i;
                } else {
                    sm.index = i + 1;
                }
                return state;
            }
            
        }
        
        private String value() {
            String result = new String(this.charBuff, 0, this.charBuffIndex);
            this.charBuffIndex = 0;
            return result;
        }
        
        private void processFieldName() {
            this.currentJsonNode.fieldName = this.value();
        }
        
        /**
         * Represents the state during a field name.
         */
        private static class FieldNameState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                char ch;
                State state = null;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (ch == sm.currentQuoteChar) {
                        sm.processFieldName();
                        state = END_FIELD_NAME_STATE;
                    } else if (ch == REV_SOL) { 
                        state = FIELD_NAME_ESC_CHAR_PROCESSING_STATE;
                    } else if (ch == EOF) {
                        throw new JsonParserException("unexpected end of JSON document");
                    } else {
                        sm.append(ch);
                        state = this;
                        continue;
                    }
                    break;
                }
                sm.index = i + 1;
                return state;
            }
             
        }
        
        /**
         * Represents the state where a field name definition has ended.
         */
        private static class EndFieldNameState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (StateMachine.isWhitespace(ch)) {
                        state = this;
                        continue;
                    } else if (ch == ':') {
                        state = FIELD_VALUE_READY_STATE;
                    } else {
                        StateMachine.throwExpected(":");
                    }
                    break;
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
        /**
         * Represents the state where a field value is about to be defined.
         */
        private static class FieldValueReadyState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (StateMachine.isWhitespace(ch)) {
                        state = this;
                        continue;
                    } else if (ch == QUOTES || ch == SINGLE_QUOTES) {
                        state = STRING_FIELD_VALUE_STATE;
                        sm.currentQuoteChar = ch;
                    } else if (ch == '{') {
                        state = sm.initNewObject(); 
                    } else if (ch == '[') {
                        state = sm.initNewArray(); 
                    } else {
                        state = NON_STRING_FIELD_VALUE_STATE;
                    }
                    break;
                }
                if (state == NON_STRING_FIELD_VALUE_STATE) {
                    sm.index = i;
                } else {
                    sm.index = i + 1;
                }
                return state;
            }
            
        }
        
        /**
         * Represents the state during a string field value is defined.
         */
        private static class StringFieldValueState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (ch == sm.currentQuoteChar) {
                        sm.currentJsonNode.set(sm.currentJsonNode.fieldName, sm.value());
                        state = FIELD_END_STATE;
                    } else if (ch == REV_SOL) { 
                        state = STRING_FIELD_ESC_CHAR_PROCESSING_STATE;
                    } else if (ch == EOF) {
                        throw new JsonParserException("unexpected end of JSON document");
                    } else {
                        sm.append(ch);
                        state = this;
                        continue;
                    }
                    break;
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
        /**
         * Represents the state during a string array element is defined.
         */
        private static class StringArrayElementState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (ch == sm.currentQuoteChar) {
                        sm.currentJsonNode.add(new JsonNode(sm.value()));
                        state = ARRAY_ELEMENT_END_STATE;
                    } else if (ch == REV_SOL) { 
                        state = STRING_AE_ESC_CHAR_PROCESSING_STATE;
                    } else if (ch == EOF) {
                        throw new JsonParserException("unexpected end of JSON document");
                    } else {
                        sm.append(ch);
                        state = this;
                        continue;
                    }
                    break;
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
        /**
         * Represents the state during a non-string field value is defined.
         */
        private static class NonStringFieldValueState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (ch == '{') {
                        state = sm.initNewObject();
                    } else if (ch == '[') {
                        state = sm.initNewArray();
                    } else if (ch == '}' || ch == ']') {
                        sm.processNonStringValue(ValueType.FIELD);
                        state = sm.finalizeObject();
                    } else if (ch == ',') {
                        sm.processNonStringValue(ValueType.FIELD);
                        state = NON_FIRST_FIELD_READY_STATE;
                    } else if (StateMachine.isWhitespace(ch)) {
                        sm.processNonStringValue(ValueType.FIELD);
                        state = FIELD_END_STATE;
                    } else if (ch == EOF) {
                        throw new JsonParserException("unexpected end of JSON document");
                    } else {
                        sm.append(ch);
                        state = this;
                        continue;
                    }
                    break;
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
        /**
         * Represents the state during a non-string array element is defined.
         */
        private static class NonStringArrayElementState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (ch == '{') {
                        state = sm.initNewObject();
                    } else if (ch == '[') {
                        state = sm.initNewArray();
                    } else if (ch == ']') {
                        sm.processNonStringValue(ValueType.ARRAY_ELEMENT);
                        state = sm.finalizeObject();
                    } else if (ch == ',') {
                        sm.processNonStringValue(ValueType.ARRAY_ELEMENT);
                        state = NON_FIRST_ARRAY_ELEMENT_READY_STATE;
                    } else if (StateMachine.isWhitespace(ch)) {
                        sm.processNonStringValue(ValueType.ARRAY_ELEMENT);
                        state = ARRAY_ELEMENT_END_STATE;
                    } else if (ch == EOF) {
                        throw new JsonParserException("unexpected end of JSON document");
                    } else {
                        sm.append(ch);
                        state = this;
                        continue;
                    }
                    break;
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
        /**
         * Represents the state during a string value is defined.
         */
        private static class StringValueState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (ch == sm.currentQuoteChar) {
                        sm.currentJsonNode = new JsonNode(false);
                        sm.currentJsonNode.setString(sm.value());
                        state = DOC_END_STATE;
                    } else if (ch == REV_SOL) { 
                        state = STRING_VAL_ESC_CHAR_PROCESSING_STATE;
                    } else if (ch == EOF) {
                        throw new JsonParserException("unexpected end of JSON document");
                    } else {
                        sm.append(ch);
                        state = this;
                        continue;
                    } 
                    break;
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
        private enum ValueType {
            FIELD,
            VALUE,
            ARRAY_ELEMENT
        }
        
        private void processNonStringValue(ValueType type) throws JsonParserException {
            String str = value();
            try {
                long longValue = Long.parseLong(str);
                switch (type) {
                case ARRAY_ELEMENT:
                    currentJsonNode.add(new JsonNode(longValue));
                    break;
                case FIELD:
                    currentJsonNode.set(currentJsonNode.fieldName, longValue);
                    break;
                case VALUE:
                    currentJsonNode.setNumber(longValue);
                    break;
                default:
                    break;                
                }
            } catch (NumberFormatException ignore) {
                try {
                    double doubleValue = Double.parseDouble(str);
                    switch (type) {
                    case ARRAY_ELEMENT:
                        currentJsonNode.add(new JsonNode(doubleValue));
                        break;
                    case FIELD:
                        currentJsonNode.set(currentJsonNode.fieldName, doubleValue);
                        break;
                    case VALUE:
                        currentJsonNode.setNumber(doubleValue);
                        break;
                    default:
                        break;                
                    }
                } catch (NumberFormatException ignore2) {
                    if (NULL.equals(str)) {
                        switch (type) {
                        case ARRAY_ELEMENT:
                            currentJsonNode.add(new JsonNode(Type.NULL));
                            break;
                        case FIELD:
                            currentJsonNode.set(currentJsonNode.fieldName, (String) null);
                            break;
                        case VALUE:
                            currentJsonNode.setNull();
                            break;
                        default:
                            break;                
                        }
                    } else if (TRUE.equals(str)) {
                        switch (type) {
                        case ARRAY_ELEMENT:
                            currentJsonNode.add(new JsonNode(true));
                            break;
                        case FIELD:
                            currentJsonNode.set(currentJsonNode.fieldName, true);
                            break;
                        case VALUE:
                            currentJsonNode.setBooleanValue(true);
                            break;
                        default:
                            break;                
                        }
                    } else if (FALSE.equals(str)) {
                        switch (type) {
                        case ARRAY_ELEMENT:
                            currentJsonNode.add(new JsonNode(false));
                            break;
                        case FIELD:
                            currentJsonNode.set(currentJsonNode.fieldName, false);
                            break;
                        case VALUE:
                            currentJsonNode.setBooleanValue(false);
                            break;
                        default:
                            break;                
                        }
                    } else {
                        throw new JsonParserException("unrecognized token '" + str + "'");
                    }
                }
            }
        }
        
        /**
         * Represents the state during a non-string value is defined.
         */
        private static class NonStringValueState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (StateMachine.isWhitespace(ch) || ch == EOF) {
                        sm.currentJsonNode = new JsonNode(false);
                        sm.processNonStringValue(ValueType.VALUE);
                        state = DOC_END_STATE;
                    } else {
                        sm.append(ch);
                        state = this;
                        continue;
                    }
                    break;
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
        /**
         * Represents the state where an object field has ended.
         */
        private static class FieldEndState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (StateMachine.isWhitespace(ch)) {
                        state = this;
                        continue;
                    } else if (ch == ',') {
                        state = NON_FIRST_FIELD_READY_STATE;
                    } else if (ch == '}') {
                        state = sm.finalizeObject();
                    } else {
                        StateMachine.throwExpected(",", "}");
                    }
                    break;
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
        /**
         * Represents the state where an array element has ended.
         */
        private static class ArrayElementEndState implements State {

            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if (StateMachine.isWhitespace(ch)) {
                        state = this;
                        continue;
                    } else if (ch == ',') {
                        state = NON_FIRST_ARRAY_ELEMENT_READY_STATE;
                    } else if (ch == ']') {
                        state = sm.finalizeObject();
                    } else {
                        StateMachine.throwExpected(",", "]");
                    }
                    break;
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
        /**
         * Represents the state where an escaped unicode character in hex format is processed
         * from a object string field.
         */
        private static class StringFieldUnicodeHexProcessingState extends UnicodeHexProcessingState {

            @Override
            protected State getSourceState() {
                return STRING_FIELD_VALUE_STATE;
            }
            
        }
        
        /**
         * Represents the state where an escaped unicode character in hex format is processed
         * from an array string field.
         */
        private static class StringAEProcessingState extends UnicodeHexProcessingState {

            @Override
            protected State getSourceState() {
                return STRING_ARRAY_ELEMENT_STATE;
            }
            
        }
        
        /**
         * Represents the state where an escaped unicode character in hex format is processed
         * from a string value.
         */
        private static class StringValueUnicodeHexProcessingState extends UnicodeHexProcessingState {

            @Override
            protected State getSourceState() {
                return STRING_VALUE_STATE;
            }
            
        }
        
        /**
         * Represents the state where an escaped unicode character in hex format is processed
         * from a field name.
         */
        private static class FieldNameUnicodeHexProcessingState extends UnicodeHexProcessingState {

            @Override
            protected State getSourceState() {
                return FIELD_NAME_STATE;
            }
            
        }
        
        /**
         * Represents the state where an escaped unicode character in hex format is processed.
         */
        private abstract static class UnicodeHexProcessingState implements State {
            
            protected abstract State getSourceState();
                        
            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                for (; i < count; i++) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F')) {
                        sm.hexBuilder.append(ch);
                        if (sm.hexBuilder.length() >= 4) {
                            sm.append(this.extractUnicodeChar(sm));
                            this.reset(sm);
                            state = this.getSourceState();
                            break;
                        } else {
                            state = this;
                            continue;
                        }
                    } else {
                        this.reset(sm);
                        StateMachine.throwExpected("hexadecimal value of an unicode character");
                    }
                    break;
                }
                sm.index = i + 1;
                return state;
            }
            
            private void reset(StateMachine sm) {
                sm.hexBuilder.setLength(0);
            }
            
            private char extractUnicodeChar(StateMachine sm) {
                return StringEscapeUtils.unescapeJava("\\u" + sm.hexBuilder.toString()).charAt(0);
            }
            
        }
        
        /**
         * Represents the state where an escaped character is processed in a object string field.
         */
        private static class StringFieldEscapedCharacterProcessingState extends EscapedCharacterProcessingState {

            @Override
            protected State getSourceState() {
                return STRING_FIELD_VALUE_STATE;
            }
            
        }
        
        /**
         * Represents the state where an escaped character is processed in an array string field.
         */
        private static class StringAEEscapedCharacterProcessingState extends EscapedCharacterProcessingState {

            @Override
            protected State getSourceState() {
                return STRING_ARRAY_ELEMENT_STATE;
            }
            
        }
        
        /**
         * Represents the state where an escaped character is processed in a string value.
         */
        private static class StringValueEscapedCharacterProcessingState extends EscapedCharacterProcessingState {

            @Override
            protected State getSourceState() {
                return STRING_VALUE_STATE;
            }
            
        }
        
        /**
         * Represents the state where an escaped character is processed in a field name.
         */
        private static class FieldNameEscapedCharacterProcessingState extends EscapedCharacterProcessingState {

            @Override
            protected State getSourceState() {
                return FIELD_NAME_STATE;
            }
            
        }
        
        /**
         * Represents the state where an escaped character is processed.
         */
        private abstract static class EscapedCharacterProcessingState implements State {

            protected abstract State getSourceState();
            
            @Override
            public State transition(StateMachine sm, char[] buff, int i, int count) throws JsonParserException {
                State state = null;
                char ch;
                if (i < count) {
                    ch = buff[i];
                    sm.processLocation(ch);
                    switch (ch) {
                    case '"':
                        sm.append(QUOTES);
                        state = this.getSourceState();
                        break;
                    case '\\':
                        sm.append(REV_SOL);
                        state = this.getSourceState();
                        break;
                    case '/':
                        sm.append(SOL);
                        state = this.getSourceState();
                        break;
                    case 'b':
                        sm.append(BACKSPACE);
                        state = this.getSourceState();
                        break;
                    case 'f':
                        sm.append(FORMFEED);
                        state = this.getSourceState();
                        break;
                    case 'n':
                        sm.append(NEWLINE);
                        state = this.getSourceState();
                        break;
                    case 'r':
                        sm.append(CR);
                        state = this.getSourceState();
                        break;
                    case 't':
                        sm.append(HZ_TAB);
                        state = this.getSourceState();
                        break;
                    case 'u':
                        if (this.getSourceState() == STRING_FIELD_VALUE_STATE) {
                            state = STRING_FIELD_UNICODE_HEX_PROCESSING_STATE;
                        } else if (this.getSourceState() == STRING_VALUE_STATE) {
                            state = STRING_VALUE_UNICODE_HEX_PROCESSING_STATE;
                        } else if (this.getSourceState() == FIELD_NAME_STATE) {
                            state = FIELD_NAME_UNICODE_HEX_PROCESSING_STATE;
                        } else if (this.getSourceState() == STRING_ARRAY_ELEMENT_STATE) {
                            state = STRING_AE_PROCESSING_STATE;
                        } else {
                            throw new JsonParserException("unknown source '" + this.getSourceState() + 
                                    "' in escape char processing state");
                        }
                        break;
                    default:
                        StateMachine.throwExpected("escaped characters");
                    }
                }
                sm.index = i + 1;
                return state;
            }
            
        }
        
    }
    
}
