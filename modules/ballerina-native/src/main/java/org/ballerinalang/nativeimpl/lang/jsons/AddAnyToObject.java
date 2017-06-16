/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 **/

package org.ballerinalang.nativeimpl.lang.jsons;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.WriteContext;
import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeEnum;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BJSON;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.nativeimpl.lang.utils.ErrorHandler;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.Attribute;
import org.ballerinalang.natives.annotations.BallerinaAnnotation;
import org.ballerinalang.natives.annotations.BallerinaFunction;

/**
 * Insert a named element to a JSON Object. This method will add a new integer element with
 * the given name, to the location identified by the given jsonpath. If an element with 
 * the same 'name' already exists, then it will update value of the existing element.
 */
@BallerinaFunction(
        packageName = "ballerina.lang.jsons",
        functionName = "addToObject",
        args = {@Argument(name = "j", type = TypeEnum.JSON),
                @Argument(name = "jsonPath", type = TypeEnum.STRING),
                @Argument(name = "key", type = TypeEnum.STRING),
                @Argument(name = "value", type = TypeEnum.ANY)},
        isPublic = true
)
@BallerinaAnnotation(annotationName = "Description", attributes = {@Attribute(name = "value",
        value = "Inserts a named element to a JSON object. "
                + "This function will add a new integer element with the given name (key)") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "j",
        value = "A JSON object") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "jsonPath",
        value = "The path of the JSON element") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "key",
        value = "The name of the element to be added") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "value",
        value = "An any value") })
public class AddAnyToObject extends AbstractJSONFunction {

    private static final String OPERATION = "add any to json object";

    @Override
    public BValue[] execute(Context ctx) {
        String jsonPath = null;
        try {
            // Accessing Parameters.
            BJSON json = (BJSON) getRefArgument(ctx, 0);
            jsonPath = getStringArgument(ctx, 0);
            String key = getStringArgument(ctx, 1);
            BValue value = getRefArgument(ctx, 1);

            // Adding the value to JSON Object
            WriteContext jsonCtx = JsonPath.parse(json.value());

            if (value instanceof BInteger) {
                jsonCtx.put(jsonPath, key, ((BInteger) value).intValue());
            } else if (value instanceof BFloat) {
                jsonCtx.put(jsonPath, key, ((BFloat) value).floatValue());
            } else if (value instanceof BBoolean) {
                jsonCtx.put(jsonPath, key, ((BBoolean) value).booleanValue());
            } else if (value instanceof BString) {
                jsonCtx.put(jsonPath, key, value.stringValue());
            } else if (value instanceof BJSON) {
                jsonCtx.put(jsonPath, key, ((BJSON) value).value());
            }
        } catch (PathNotFoundException e) {
            ErrorHandler.handleNonExistingJsonpPath(OPERATION, jsonPath, e);
        } catch (InvalidPathException e) {
            ErrorHandler.handleInvalidJsonPath(OPERATION, e);
        } catch (JsonPathException e) {
            ErrorHandler.handleJsonPathException(OPERATION, e);
        } catch (Throwable e) {
            ErrorHandler.handleJsonPathException(OPERATION, e);
        }

        return getBValues();
    }
}
