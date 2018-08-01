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
package org.ballerinalang.model;

import org.ballerinalang.model.types.BArrayType;
import org.ballerinalang.model.types.BField;
import org.ballerinalang.model.types.BStructureType;
import org.ballerinalang.model.types.BType;
import org.ballerinalang.model.types.BTypes;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.types.TypeTags;
import org.ballerinalang.model.util.JsonGenerator;
import org.ballerinalang.model.util.JsonParser;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BRefType;
import org.ballerinalang.model.values.BRefValueArray;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BTable;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Struct;

/**
 * {@link JSONDataSource} implementation for table.
 *
 * @since 0.8.0
 */
public class TableJSONDataSource implements JSONDataSource {

    private BTable df;

    private JSONObjectGenerator objGen;

    private boolean isInTransaction;

    public TableJSONDataSource(BTable df, boolean isInTransaction) {
        this(df, new DefaultJSONObjectGenerator(), isInTransaction);
    }

    private TableJSONDataSource(BTable df, JSONObjectGenerator objGen, boolean isInTransaction) {
        this.df = df;
        this.objGen = objGen;
        this.isInTransaction = isInTransaction;
    }

    @Override
    public void serialize(JsonGenerator gen) throws IOException {
        gen.writeStartArray();
        while (this.df.hasNext(this.isInTransaction)) {
            this.df.moveToNext();
            gen.serialize(this.objGen.transform(this.df));
        }
        gen.writeEndArray();
    }

    /**
     * Default {@link TableJSONDataSource.JSONObjectGenerator} implementation based
     * on the table's in-built column definition.
     */
    private static class DefaultJSONObjectGenerator implements JSONObjectGenerator {

        @Override
        public BRefType<?> transform(BTable df) throws IOException {
            BMap<String, BRefType<?>> objNode = new BMap<>(BTypes.typeJSON);
            BStructureType structType = df.getStructType();
            BField[] structFields = null;
            if (structType != null) {
                structFields = structType.getFields();
            }
            int index = 0;
            for (ColumnDefinition col : df.getColumnDefs()) {
                String name;
                if (structFields != null) {
                    name = structFields[index].getFieldName();
                } else {
                    name = col.getName();
                }
                constructJsonData(df, objNode, name, col.getType(), index + 1, structFields);
                ++index;
            }

            return objNode;
        }

    }

    private static void constructJsonData(BTable df, BMap<String, BRefType<?>> jsonObject, String name, TypeKind type,
                                          int index, BField[] structFields) {
        switch (type) {
            case STRING:
                jsonObject.put(name, getBString(df.getString(index)));
                break;
            case INT:
                jsonObject.put(name, new BInteger(df.getInt(index)));
                break;
            case FLOAT:
                jsonObject.put(name, new BFloat(df.getFloat(index)));
                break;
            case BOOLEAN:
                jsonObject.put(name, new BBoolean(df.getBoolean(index)));
                break;
            case BLOB:
                jsonObject.put(name, getBString(df.getBlob(index)));
                break;
            case ARRAY:
                jsonObject.put(name, getDataArray(df, index));
                break;
            case JSON:
                jsonObject.put(name, JsonParser.parse(df.getString(index)));
                break;
            case OBJECT:
            case RECORD:
                jsonObject.put(name, getStructData(df.getStruct(index), structFields, index));
                break;
            case XML:
                jsonObject.put(name, getBString(df.getString(index)));
                break;
            default:
                jsonObject.put(name, getBString(df.getString(index)));
            break;
        }
    }

    private static BRefType<?> getStructData(Object[] data, BField[] structFields, int index) {
        try {
            if (structFields == null) {
                BRefValueArray jsonArray = new BRefValueArray(new BArrayType(BTypes.typeJSON));
                if (data != null) {
                    for (Object value : data) {
                        if (value instanceof String) {
                            jsonArray.append(new BString((String) value));
                        } else if (value instanceof Boolean) {
                            jsonArray.append(new BBoolean((Boolean) value));
                        } else if (value instanceof Long) {
                            jsonArray.append(new BInteger((long) value));
                        } else if (value instanceof Double) {
                            jsonArray.append(new BFloat((double) value));
                        } else if (value instanceof Integer) {
                            jsonArray.append(new BInteger((int) value));
                        } else if (value instanceof Float) {
                            jsonArray.append(new BFloat((float) value));
                        } else if (value instanceof BigDecimal) {
                            jsonArray.append(new BFloat(((BigDecimal) value).doubleValue()));
                        }
                    }
                }
                return jsonArray;
            } else {
                BMap<String, BValue> jsonData = new BMap<>();
                boolean structError = true;
                if (data != null) {
                    int i = 0;
                    for (Object value : data) {
                        BType internaltType = structFields[index - 1].fieldType;
                        if (internaltType.getTag() == TypeTags.OBJECT_TYPE_TAG
                                || internaltType.getTag() == TypeTags.RECORD_TYPE_TAG) {
                            BField[] interanlStructFields = ((BStructureType) internaltType).getFields();
                            if (interanlStructFields != null) {
                                if (value instanceof String) {
                                    jsonData.put(interanlStructFields[i].fieldName, new BString((String) value));
                                } else if (value instanceof Boolean) {
                                    jsonData.put(interanlStructFields[i].fieldName, new BBoolean((Boolean) value));
                                } else if (value instanceof Long) {
                                    jsonData.put(interanlStructFields[i].fieldName, new BInteger((long) value));
                                } else if (value instanceof Double) {
                                    jsonData.put(interanlStructFields[i].fieldName, new BFloat((double) value));
                                } else if (value instanceof Integer) {
                                    jsonData.put(interanlStructFields[i].fieldName, new BInteger((int) value));
                                } else if (value instanceof Float) {
                                    jsonData.put(interanlStructFields[i].fieldName, new BFloat((float) value));
                                } else if (value instanceof BigDecimal) {
                                    jsonData.put(interanlStructFields[i].fieldName,
                                            new BFloat(((BigDecimal) value).doubleValue()));
                                } else if (value instanceof Struct) {
                                    jsonData.put(interanlStructFields[i].fieldName,
                                            getStructData(((Struct) value).getAttributes(), interanlStructFields,
                                                    i + 1));
                                }
                                structError = false;
                            }
                        }
                        ++i;
                    }
                }
                if (structError) {
                    throw new BallerinaException("error in constructing the json object from struct type data");
                }
                
                return jsonData;
            }
        } catch (SQLException e) {
            throw new BallerinaException(
                    "error in retrieving struct data to construct the inner json object:" + e.getMessage());
        }
    }

    private static BRefType<?> getDataArray(BTable df, int columnIndex) {
        Object[] dataArray = df.getArray(columnIndex);
        int length = dataArray.length;
        BRefValueArray jsonArray = new BRefValueArray(new BArrayType(BTypes.typeJSON));
        if (length > 0) {
            Object obj = dataArray[0];
            if (obj instanceof String) {
                for (Object value : dataArray) {
                    jsonArray.append(new BString((String) value));
                }
            } else if (obj instanceof Boolean) {
                for (Object value : dataArray) {
                    jsonArray.append(new BBoolean((boolean) value));
                }
            } else if (obj instanceof Integer) {
                for (Object value : dataArray) {
                    jsonArray.append(new BInteger((int) value));
                }
            } else if (obj instanceof Long) {
                for (Object value : dataArray) {
                    jsonArray.append(new BInteger((long) value));
                }
            } else if (obj instanceof Float) {
                for (Object value : dataArray) {
                    jsonArray.append(new BFloat((float) value));
                }
            } else if (obj instanceof Double) {
                for (Object value : dataArray) {
                    jsonArray.append(new BFloat((double) value));
                }
            } else if (obj instanceof BigDecimal) {
                for (Object value : dataArray) {
                    if (value != null) {
                        jsonArray.append(new BFloat(((BigDecimal) value).doubleValue()));
                    } else {
                        jsonArray.append(null);
                    }
                }
            }
        }
        return  jsonArray;
    }

    private static BString getBString(String str) {
        return str != null ? new BString(str) : null;
    }

    /**
     * This represents the logic that will transform the current entry of a
     * data table to a JSON.
     */
    public static interface JSONObjectGenerator {

        /**
         * Converts the current position of the given table to a JSON.
         *
         * @param table The table that should be used in the current position
         * @return The generated JSON object
         * @throws IOException for JSON reading/serializing errors
         */
        BRefType<?> transform(BTable table) throws IOException;

    }
}
