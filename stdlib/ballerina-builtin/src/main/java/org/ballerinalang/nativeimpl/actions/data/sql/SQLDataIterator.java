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
package org.ballerinalang.nativeimpl.actions.data.sql;

import org.ballerinalang.model.ColumnDefinition;
import org.ballerinalang.model.DataIterator;
import org.ballerinalang.model.types.BStructType;
import org.ballerinalang.model.types.BType;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.types.TypeTags;
import org.ballerinalang.model.values.BBooleanArray;
import org.ballerinalang.model.values.BFloatArray;
import org.ballerinalang.model.values.BIntArray;
import org.ballerinalang.model.values.BNewArray;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStringArray;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.nativeimpl.Utils;
import org.ballerinalang.nativeimpl.actions.data.sql.client.SQLDatasourceUtils;
import org.ballerinalang.util.codegen.StructInfo;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.List;

/**
 * This iterator mainly wrap java.sql.ResultSet. This will provide datatable operations
 * related to ballerina.data.actions.sql connector.
 *
 * @since 0.8.0
 */
public class SQLDataIterator implements DataIterator {

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private Calendar utcCalendar;
    private List<ColumnDefinition> columnDefs;
    private BStructType bStructType;
    private StructInfo timeStructInfo;
    private StructInfo zoneStructInfo;

    public SQLDataIterator(Connection conn, Statement stmt, ResultSet rs, Calendar utcCalendar,
            List<ColumnDefinition> columnDefs, BStructType structType, StructInfo timeStructInfo,
            StructInfo zoneStructInfo) throws SQLException {
        this.conn = conn;
        this.stmt = stmt;
        this.rs = rs;
        this.utcCalendar = utcCalendar;
        this.columnDefs = columnDefs;
        this.bStructType = structType;
        this.timeStructInfo = timeStructInfo;
        this.zoneStructInfo = zoneStructInfo;
    }

    @Override
    public void close(boolean isInTransaction) {
        SQLDatasourceUtils.cleanupConnection(rs, stmt, conn, isInTransaction);
        rs = null;
        stmt = null;
        conn = null;
    }

    @Override
    public boolean next() {
        if (rs == null) {
            return false;
        }
        try {
            return rs.next();
        } catch (SQLException e) {
            throw new BallerinaException(e.getMessage(), e);
        }
    }

    @Override
    public String getString(int columnIndex) {
        try {
            return rs.getString(columnIndex);
        } catch (SQLException e) {
            throw new BallerinaException(e.getMessage(), e);
        }
    }

    @Override
    public long getInt(int columnIndex) {
        try {
            return rs.getLong(columnIndex);
        } catch (SQLException e) {
            throw new BallerinaException(e.getMessage(), e);
        }
    }

    @Override
    public double getFloat(int columnIndex) {
        try {
            return rs.getDouble(columnIndex);
        } catch (SQLException e) {
            throw new BallerinaException(e.getMessage(), e);
        }
    }

    @Override
    public boolean getBoolean(int columnIndex) {
        try {
            return rs.getBoolean(columnIndex);
        } catch (SQLException e) {
            throw new BallerinaException(e.getMessage(), e);
        }
    }

    @Override
    public String getBlob(int columnIndex) {
        try {
            Blob bValue = rs.getBlob(columnIndex);
            return SQLDatasourceUtils.getString(bValue);
        } catch (SQLException e) {
            throw new BallerinaException(e.getMessage(), e);
        }
    }

    @Override
    public Object[] getStruct(int columnIndex) {
        Object[] objArray = null;
        try {
            Struct data = (Struct) rs.getObject(columnIndex);
            if (data != null) {
                objArray = data.getAttributes();
            }
        } catch (SQLException e) {
            throw new BallerinaException(e.getMessage(), e);
        }
        return objArray;
    }

    @Override
    public Object[] getArray(int columnIndex) {
        try {
            return generateArrayDataResult(rs.getArray(columnIndex));
        } catch (SQLException e) {
            throw new BallerinaException(e.getMessage(), e);
        }
    }

    private Object[] generateArrayDataResult(Array array) throws SQLException {
        Object[] objArray = null;
        if (!rs.wasNull()) {
            objArray = (Object[]) array.getArray();
        }
        return objArray;
    }

    @Override
    public BStruct generateNext() {
        if (bStructType == null) {
            throw new BallerinaException("the expected struct type is not specified in action");
        }
        BStruct bStruct = new BStruct(bStructType);
        int longRegIndex = -1;
        int doubleRegIndex = -1;
        int stringRegIndex = -1;
        int booleanRegIndex = -1;
        int blobRegIndex = -1;
        int refRegIndex = -1;
        int index = 0;
        String columnName = null;
        try {
            for (ColumnDefinition columnDef : columnDefs) {
                if (columnDef instanceof SQLColumnDefinition) {
                    SQLColumnDefinition def = (SQLColumnDefinition) columnDef;
                    columnName = def.getName();
                    int sqlType = def.getSqlType();
                    ++index;
                    switch (sqlType) {
                    case Types.ARRAY:
                        Array dataArray = rs.getArray(index);
                        bStruct.setRefField(++refRegIndex, getDataArray(dataArray));
                        break;
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.LONGVARCHAR:
                    case Types.NCHAR:
                    case Types.NVARCHAR:
                    case Types.LONGNVARCHAR:
                        String sValue = rs.getString(index);
                        bStruct.setStringField(++stringRegIndex, sValue);
                        break;
                    case Types.BLOB:
                    case Types.BINARY:
                    case Types.VARBINARY:
                    case Types.LONGVARBINARY:
                        Blob value = rs.getBlob(index);
                        if (value != null) {
                            bStruct.setBlobField(++blobRegIndex, value.getBytes(1L, (int) value.length()));
                        } else {
                            bStruct.setBlobField(++blobRegIndex, new byte[0]);
                        }
                        break;
                    case Types.CLOB:
                        String clobValue = SQLDatasourceUtils.getString((rs.getClob(index)));
                        bStruct.setStringField(++stringRegIndex, clobValue);
                        break;
                    case Types.NCLOB:
                        String nClobValue = SQLDatasourceUtils.getString(rs.getNClob(index));
                        bStruct.setStringField(++stringRegIndex, nClobValue);
                        break;
                    case Types.DATE:
                        Date date = rs.getDate(index);
                        int fieldType = bStructType.getStructFields()[index - 1].getFieldType().getTag();
                        if (fieldType == TypeTags.STRING_TAG) {
                            String dateValue = SQLDatasourceUtils.getString(date);
                            bStruct.setStringField(++stringRegIndex, dateValue);
                        } else if (fieldType == TypeTags.STRUCT_TAG) {
                            bStruct.setRefField(++refRegIndex, createTimeStruct(date.getTime()));
                        } else if (fieldType == TypeTags.INT_TAG) {
                            bStruct.setIntField(++longRegIndex, date.getTime());
                        }
                        break;
                    case Types.TIME:
                    case Types.TIME_WITH_TIMEZONE:
                        Time time = rs.getTime(index, utcCalendar);
                        fieldType = bStructType.getStructFields()[index - 1].getFieldType().getTag();
                        if (fieldType == TypeTags.STRING_TAG) {
                            String timeValue = SQLDatasourceUtils.getString(time);
                            bStruct.setStringField(++stringRegIndex, timeValue);
                        } else if (fieldType == TypeTags.STRUCT_TAG) {
                            bStruct.setRefField(++refRegIndex, createTimeStruct(time.getTime()));
                        } else if (fieldType == TypeTags.INT_TAG) {
                            bStruct.setIntField(++longRegIndex, time.getTime());
                        }
                        break;
                    case Types.TIMESTAMP:
                    case Types.TIMESTAMP_WITH_TIMEZONE:
                        Timestamp timestamp = rs.getTimestamp(index, utcCalendar);
                        fieldType = bStructType.getStructFields()[index - 1].getFieldType().getTag();
                        if (fieldType == TypeTags.STRING_TAG) {
                            String timestmpValue = SQLDatasourceUtils.getString(timestamp);
                            bStruct.setStringField(++stringRegIndex, timestmpValue);
                        } else if (fieldType == TypeTags.STRUCT_TAG) {
                            bStruct.setRefField(++refRegIndex, createTimeStruct(timestamp.getTime()));
                        } else if (fieldType == TypeTags.INT_TAG) {
                            bStruct.setIntField(++longRegIndex, timestamp.getTime());
                        }
                        break;
                    case Types.ROWID:
                        BValue strValue = new BString(new String(rs.getRowId(index).getBytes(), "UTF-8"));
                        bStruct.setStringField(++stringRegIndex, strValue.stringValue());
                        break;
                    case Types.TINYINT:
                    case Types.SMALLINT:
                        long iValue = rs.getInt(index);
                        bStruct.setIntField(++longRegIndex, iValue);
                        break;
                    case Types.INTEGER:
                    case Types.BIGINT:
                        long lValue = rs.getLong(index);
                        bStruct.setIntField(++longRegIndex, lValue);
                        break;
                    case Types.REAL:
                    case Types.FLOAT:
                        double fValue = rs.getFloat(index);
                        bStruct.setFloatField(++doubleRegIndex, fValue);
                        break;
                    case Types.DOUBLE:
                        double dValue = rs.getDouble(index);
                        bStruct.setFloatField(++doubleRegIndex, dValue);
                        break;
                    case Types.NUMERIC:
                    case Types.DECIMAL:
                        double decimalValue = 0;
                        BigDecimal bigDecimalValue = rs.getBigDecimal(index);
                        if (bigDecimalValue != null) {
                            decimalValue = bigDecimalValue.doubleValue();
                        }
                        bStruct.setFloatField(++doubleRegIndex, decimalValue);
                        break;
                    case Types.BIT:
                    case Types.BOOLEAN:
                        boolean boolValue = rs.getBoolean(index);
                        bStruct.setBooleanField(++booleanRegIndex, boolValue ? 1 : 0);
                        break;
                    case Types.STRUCT:
                        Struct structdata = (Struct) rs.getObject(index);
                        BType structFieldType = bStructType.getStructFields()[index - 1].getFieldType();
                        fieldType = structFieldType.getTag();
                        if (fieldType == TypeTags.STRUCT_TAG) {
                            bStruct.setRefField(++refRegIndex,
                                    createUserDefinedType(structdata, (BStructType) structFieldType));
                        }
                        break;
                    default:
                        throw new BallerinaException(
                                "unsupported sql type " + sqlType + " found for the column " + columnName + " index:"
                                        + index);
                    }
                }
            }
        }  catch (Throwable e) {
            throw new BallerinaException(
                    "error in retrieving next value for column: " + columnName + ": at index:" + index + ":" + e
                            .getMessage());
        }
        return bStruct;
    }

    @Override
    public List<ColumnDefinition> getColumnDefinitions() {
        return this.columnDefs;
    }

    @Override
    public BStructType getStructType() {
        return this.bStructType;
    }

    private BNewArray getDataArray(Array array) throws SQLException {
        Object[] dataArray = generateArrayDataResult(array);
        if (dataArray != null) {
            int length = dataArray.length;
            if (length > 0) {
                Object obj = dataArray[0];
                if (obj instanceof String) {
                    BStringArray stringDataArray = new BStringArray();
                    for (int i = 0; i < length; i++) {
                        stringDataArray.add(i, (String) dataArray[i]);
                    }
                    return stringDataArray;
                } else if (obj instanceof Boolean) {
                    BBooleanArray boolDataArray = new BBooleanArray();
                    for (int i = 0; i < length; i++) {
                        boolDataArray.add(i, ((Boolean) dataArray[i]) ? 1 : 0);
                    }
                    return boolDataArray;
                } else if (obj instanceof Integer) {
                    BIntArray intDataArray = new BIntArray();
                    for (int i = 0; i < length; i++) {
                        intDataArray.add(i, ((Integer) dataArray[i]));
                    }
                    return intDataArray;
                } else if (obj instanceof Long) {
                    BIntArray longDataArray = new BIntArray();
                    for (int i = 0; i < length; i++) {
                        longDataArray.add(i, (Long) dataArray[i]);
                    }
                    return longDataArray;
                } else if (obj instanceof Float) {
                    BFloatArray floatDataArray = new BFloatArray();
                    for (int i = 0; i < length; i++) {
                        floatDataArray.add(i, (Float) dataArray[i]);
                    }
                    return floatDataArray;
                } else if (obj instanceof Double) {
                    BFloatArray doubleDataArray = new BFloatArray();
                    for (int i = 0; i < dataArray.length; i++) {
                        doubleDataArray.add(i, (Double) dataArray[i]);
                    }
                    return doubleDataArray;
                }
            }
        }
        return null;
    }

    private BStruct createTimeStruct(long millis) {
        return Utils.createTimeStruct(zoneStructInfo, timeStructInfo, millis, Constants.TIMEZONE_UTC);
    }

    private BStruct createUserDefinedType(Struct structValue, BStructType structType) {
        if (structValue == null) {
            return null;
        }
        BStructType.StructField[] internalStructFields = structType.getStructFields();
        BStruct struct = new BStruct(structType);
        try {
            Object[] dataArray = structValue.getAttributes();
            if (dataArray != null) {
                if (dataArray.length != internalStructFields.length) {
                    throw new BallerinaException("specified struct and returned struct are not compatible");
                }
                int longRegIndex = -1;
                int doubleRegIndex = -1;
                int stringRegIndex = -1;
                int booleanRegIndex = -1;
                int refRegIndex = -1;
                int index = 0;
                for (BStructType.StructField internalField : internalStructFields) {
                    int type = internalField.getFieldType().getTag();
                    Object value = dataArray[index];
                    switch (type) {
                    case TypeTags.INT_TAG:
                        if (value instanceof BigDecimal) {
                            struct.setIntField(++longRegIndex, ((BigDecimal) value).intValue());
                        } else {
                            struct.setIntField(++longRegIndex, (long) value);
                        }
                        break;
                    case TypeTags.FLOAT_TAG:
                        if (value instanceof BigDecimal) {
                            struct.setFloatField(++doubleRegIndex, ((BigDecimal) value).doubleValue());
                        } else {
                            struct.setFloatField(++doubleRegIndex, (double) value);
                        }
                        break;
                    case TypeTags.STRING_TAG:
                        struct.setStringField(++stringRegIndex, (String) value);
                        break;
                    case TypeTags.BOOLEAN_TAG:
                        struct.setBooleanField(++booleanRegIndex, (int) value);
                        break;
                    case TypeTags.STRUCT_TAG:
                        struct.setRefField(++refRegIndex,
                                createUserDefinedType((Struct) value, (BStructType) internalField.fieldType));
                        break;
                    default:
                        throw new BallerinaException("error in retrieving UDT data for unsupported type:" + type);
                    }
                    ++index;
                }
            }
        } catch (SQLException e) {
            throw new BallerinaException("error in retrieving UDT data:" + e.getMessage());
        }
        return struct;
    }

    /**
     * This represents a column definition for a column in a datatable.
     */
    public static class SQLColumnDefinition extends ColumnDefinition {

        private int sqlType;

        public SQLColumnDefinition(String name, TypeKind mappedType, int sqlType) {
            super(name, mappedType);
            this.sqlType = sqlType;
        }

        public String getName() {
            return name;
        }

        public TypeKind getType() {
            return mappedType;
        }

        public int getSqlType() {
            return sqlType;
        }
    }
}
