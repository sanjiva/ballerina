/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.nativeimpl.actions.data.sql.actions;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.ColumnDefinition;
import org.ballerinalang.model.types.BArrayType;
import org.ballerinalang.model.types.BStructType;
import org.ballerinalang.model.types.BTypes;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.types.TypeTags;
import org.ballerinalang.model.values.BBlob;
import org.ballerinalang.model.values.BBlobArray;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BBooleanArray;
import org.ballerinalang.model.values.BEnumerator;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BFloatArray;
import org.ballerinalang.model.values.BIntArray;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BNewArray;
import org.ballerinalang.model.values.BRefValueArray;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStringArray;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BTable;
import org.ballerinalang.model.values.BTypeDescValue;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.nativeimpl.Utils;
import org.ballerinalang.nativeimpl.actions.data.sql.Constants;
import org.ballerinalang.nativeimpl.actions.data.sql.SQLDataIterator;
import org.ballerinalang.nativeimpl.actions.data.sql.SQLDatasource;
import org.ballerinalang.nativeimpl.actions.data.sql.SQLTransactionContext;
import org.ballerinalang.util.TableResourceManager;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.ballerinalang.util.transactions.BallerinaTransactionContext;
import org.ballerinalang.util.transactions.LocalTransactionInfo;
import org.ballerinalang.util.transactions.TransactionResourceManager;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.BatchUpdateException;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

/**
 * {@code AbstractSQLAction} is the base class for all SQL Action.
 *
 * @since 0.8.0
 */
public abstract class AbstractSQLAction extends BlockingNativeCallableUnit {

    private Calendar utcCalendar;

    public AbstractSQLAction() {
        utcCalendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.TIMEZONE_UTC));
    }

    protected void executeQuery(Context context, SQLDatasource datasource, String query, BRefValueArray parameters,
            BStructType structType) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isInTransaction = context.isInTransaction();
        try {
            conn = getDatabaseConnection(context, datasource, isInTransaction);
            String processedQuery = createProcessedQueryString(query, parameters);
            stmt = getPreparedStatement(conn, datasource, processedQuery);
            createProcessedStatement(conn, stmt, parameters);
            rs = stmt.executeQuery();
            TableResourceManager rm  = new TableResourceManager(conn, stmt);
            rm.addResultSet(rs);
            context.setReturnValues(constructTable(rm, context, rs, structType));
        } catch (Throwable e) {
            SQLDatasourceUtils.cleanupConnection(rs, stmt, conn, isInTransaction);
            throw new BallerinaException("execute query failed: " + e.getMessage(), e);
        }
    }

    protected void executeUpdate(Context context, SQLDatasource datasource, String query, BRefValueArray parameters) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean isInTransaction = context.isInTransaction();
        try {
            conn = getDatabaseConnection(context, datasource, isInTransaction);
            String processedQuery = createProcessedQueryString(query, parameters);
            stmt = conn.prepareStatement(processedQuery);
            createProcessedStatement(conn, stmt, parameters);
            int count = stmt.executeUpdate();
            context.setReturnValues(new BInteger(count));
        } catch (SQLException e) {
            throw new BallerinaException("execute update failed: " + e.getMessage(), e);
        } finally {
            SQLDatasourceUtils.cleanupConnection(null, stmt, conn, isInTransaction);
        }
    }

    protected void executeUpdateWithKeys(Context context, SQLDatasource datasource, String query,
                                         BStringArray keyColumns, BRefValueArray parameters) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isInTransaction = context.isInTransaction();
        try {
            conn = getDatabaseConnection(context, datasource, isInTransaction);
            String processedQuery = createProcessedQueryString(query, parameters);
            int keyColumnCount = 0;
            if (keyColumns != null) {
                keyColumnCount = (int) keyColumns.size();
            }
            if (keyColumnCount > 0) {
                String[] columnArray = new String[keyColumnCount];
                for (int i = 0; i < keyColumnCount; i++) {
                    columnArray[i] = keyColumns.get(i);
                }
                stmt = conn.prepareStatement(processedQuery, columnArray);
            } else {
                stmt = conn.prepareStatement(processedQuery, Statement.RETURN_GENERATED_KEYS);
            }
            createProcessedStatement(conn, stmt, parameters);
            int count = stmt.executeUpdate();
            BInteger updatedCount = new BInteger(count);
            rs = stmt.getGeneratedKeys();
            /*The result set contains the auto generated keys. There can be multiple auto generated columns
            in a table.*/
            BStringArray generatedKeys = null;
            if (rs.next()) {
                generatedKeys = getGeneratedKeys(rs);
            }
            BRefValueArray tuple = new BRefValueArray(BTypes.typeAny);
            tuple.add(0, updatedCount);
            tuple.add(1, generatedKeys);
            context.setReturnValues(tuple);
        } catch (SQLException e) {
            throw new BallerinaException("execute update with generated keys failed: " + e.getMessage(), e);
        } finally {
            SQLDatasourceUtils.cleanupConnection(rs, stmt, conn, isInTransaction);
        }
    }

    protected void executeProcedure(Context context, SQLDatasource datasource,
                                    String query, BRefValueArray parameters, BStructType structType) {
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<ResultSet> resultSets;
        boolean isInTransaction = context.isInTransaction();
        try {
           conn = getDatabaseConnection(context, datasource, isInTransaction);
            stmt = getPreparedCall(conn, datasource, query, parameters);
            createProcessedStatement(conn, stmt, parameters, datasource.getDatabaseName());
            resultSets = executeStoredProc(stmt);
            boolean refCursorOutParamsPresent = parameters != null && isRefCursorOutParamPresent(parameters);
            boolean resultSetsReturned = !resultSets.isEmpty();
            TableResourceManager rm = null;
            if (resultSetsReturned || refCursorOutParamsPresent) {
                rm = new TableResourceManager(conn, stmt);
            }
            setOutParameters(context, stmt, parameters, rm);
            if (resultSetsReturned) {
                rm.addAllResultSets(resultSets);
                // If a result set has been returned from the stored procedure it needs to be pushed in to return values
                context.setReturnValues(createBRefValueArray(resultSets, rm, context, structType));
            } else if (!refCursorOutParamsPresent) {
                // Even if there aren't any result sets returned from the procedure there could be ref cursors
                // returned as OUT params. If there are present we cannot clean up the connection. If there is no
                // returned result set or ref cursor OUT params we should cleanup the connection.
                SQLDatasourceUtils.cleanupConnection(null, stmt, conn, isInTransaction);
                context.setReturnValues();
            }
        } catch (Throwable e) {
            SQLDatasourceUtils.cleanupConnection(rs, stmt, conn, isInTransaction);
            throw new BallerinaException("execute stored procedure failed: " + e.getMessage(), e);
        }
    }

    private BRefValueArray createBRefValueArray(List<ResultSet> resultSets, TableResourceManager rm, Context context,
            BStructType structType) throws SQLException {
        BRefValueArray bTableRefArray = new BRefValueArray();
        for (int i = 0; i < resultSets.size(); i++) {
            bTableRefArray.add(i, constructTable(rm, context, resultSets.get(i), structType));
        }
        return bTableRefArray;
    }

    protected void executeBatchUpdate(Context context, SQLDatasource datasource,
                                      String query, BRefValueArray parameters) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int[] updatedCount;
        int paramArrayCount = 0;
        try {
            conn = datasource.getSQLConnection();
            stmt = conn.prepareStatement(query);
            setConnectionAutoCommit(conn, false);
            if (parameters != null) {
                paramArrayCount = (int) parameters.size();
                for (int index = 0; index < paramArrayCount; index++) {
                    BRefValueArray params = (BRefValueArray) parameters.get(index);
                    createProcessedStatement(conn, stmt, params);
                    stmt.addBatch();
                }
            } else {
                stmt.addBatch();
            }
            updatedCount = stmt.executeBatch();
            conn.commit();
        } catch (BatchUpdateException e) {
            updatedCount = e.getUpdateCounts();
        } catch (SQLException e) {
            throw new BallerinaException("execute batch update failed: " + e.getMessage(), e);
        } finally {
            setConnectionAutoCommit(conn, true);
            SQLDatasourceUtils.cleanupConnection(null, stmt, conn, false);
        }
        //After a command in a batch update fails to execute properly and a BatchUpdateException is thrown, the driver
        // may or may not continue to process the remaining commands in the batch. If the driver does not continue
        // processing after a failure, the array returned by the method will have -3 (EXECUTE_FAILED) for those updates.
        long[] returnedCount = new long[paramArrayCount];
        Arrays.fill(returnedCount, Statement.EXECUTE_FAILED);
        BIntArray countArray = new BIntArray(returnedCount);
        if (updatedCount != null) {
            int iSize = updatedCount.length;
            for (int i = 0; i < iSize; ++i) {
                countArray.add(i, updatedCount[i]);
            }
        }
        context.setReturnValues(countArray);
    }

    protected BStructType getStructType(Context context) {
        BStructType structType = null;
        BTypeDescValue type = (BTypeDescValue) context.getNullableRefArgument(2);
        if (type != null) {
            structType = (BStructType) type.value();
        }
        return structType;
    }

    /**
     * If there are any arrays of parameter for types other than sql array, the given query is expanded by adding "?" s
     * to match with the array size.
     */
    private String createProcessedQueryString(String query, BRefValueArray parameters) {
        String currentQuery = query;
        if (parameters != null) {
            int start = 0;
            Object[] vals;
            int count;
            int paramCount = (int) parameters.size();
            for (int i = 0; i < paramCount; i++) {
                BStruct paramValue = (BStruct) parameters.get(i);
                if (paramValue != null) {
                    String sqlType = getSQLType(paramValue);
                    BValue value = paramValue.getRefField(1);
                    if (value != null && value.getType().getTag() == TypeTags.ARRAY_TAG &&
                            !Constants.SQLDataTypes.ARRAY.equalsIgnoreCase(sqlType)) {
                        count = (int) ((BNewArray) value).size();
                    } else {
                        count = 1;
                    }
                    vals = this.expandQuery(start, count, currentQuery);
                    start = (Integer) vals[0];
                    currentQuery = (String) vals[1];
                }
            }
        }
        return currentQuery;
    }

    /**
     * Search for the first occurrence of "?" from the given starting point and replace it with given number of "?"'s.
     */
    private Object[] expandQuery(int start, int count, String query) {
        StringBuilder result = new StringBuilder();
        int n = query.length();
        boolean doubleQuoteExists = false;
        boolean singleQuoteExists = false;
        int end = n;
        for (int i = start; i < n; i++) {
            if (query.charAt(i) == '\'') {
                singleQuoteExists = !singleQuoteExists;
            } else if (query.charAt(i) == '\"') {
                doubleQuoteExists = !doubleQuoteExists;
            } else if (query.charAt(i) == '?' && !(doubleQuoteExists || singleQuoteExists)) {
                result.append(query.substring(0, i));
                result.append(this.generateQuestionMarks(count));
                end = result.length() + 1;
                if (i + 1 < n) {
                    result.append(query.substring(i + 1));
                }
                break;
            }
        }
        return new Object[] { end, result.toString() };
    }

    private String generateQuestionMarks(int n) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            builder.append(Constants.QUESTION_MARK);
            if (i + 1 < n) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    private void setConnectionAutoCommit(Connection conn, boolean status) {
        try {
            if (conn != null) {
                conn.setAutoCommit(status);
            }
        } catch (SQLException e) {
            throw new BallerinaException("set connection commit status failed: " + e.getMessage(), e);
        }
    }

    protected void closeConnections(SQLDatasource datasource) {
        datasource.closeConnectionPool();
    }

    private PreparedStatement getPreparedStatement(Connection conn, SQLDatasource datasource, String query)
            throws SQLException {
        PreparedStatement stmt;
        boolean mysql = datasource.getDatabaseName().contains("mysql");
        /* In MySQL by default, ResultSets are completely retrieved and stored in memory.
           Following properties are set to stream the results back one row at a time.*/
        if (mysql) {
            stmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            // To fulfill OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE findbugs validation.
            try {
                stmt.setFetchSize(Integer.MIN_VALUE);
            } catch (SQLException e) {
                stmt.close();
            }
        } else {
            stmt = conn.prepareStatement(query);
        }
        return stmt;
    }

    private CallableStatement getPreparedCall(Connection conn, SQLDatasource datasource, String query,
                                              BRefValueArray parameters) throws SQLException {
        CallableStatement stmt;
        boolean mysql = datasource.getDatabaseName().contains("mysql");
        if (mysql) {
            stmt = conn.prepareCall(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            /* Only stream if there aren't any OUT parameters since can't use streaming result sets with callable
               statements that have output parameters */
            if (parameters != null && !hasOutParams(parameters)) {
                stmt.setFetchSize(Integer.MIN_VALUE);
            }
        } else {
            stmt = conn.prepareCall(query);
        }
        return stmt;
    }

    private List<ColumnDefinition> getColumnDefinitions(ResultSet rs)
            throws SQLException {
        List<ColumnDefinition> columnDefs = new ArrayList<>();
        Set<String> columnNames = new HashSet<>();
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int cols = rsMetaData.getColumnCount();
        for (int i = 1; i <= cols; i++) {
            String colName = rsMetaData.getColumnLabel(i);
            if (columnNames.contains(colName)) {
                String tableName = rsMetaData.getTableName(i).toUpperCase();
                colName = tableName + "." + colName;
            }
            int colType = rsMetaData.getColumnType(i);
            TypeKind mappedType = SQLDatasourceUtils.getColumnType(colType);
            columnDefs.add(new SQLDataIterator.SQLColumnDefinition(colName, mappedType, colType));
            columnNames.add(colName);
        }
        return columnDefs;
    }

    private BStringArray getGeneratedKeys(ResultSet rs) throws SQLException {
        BStringArray generatedKeys = new BStringArray();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        int columnType;
        String value;
        BigDecimal bigDecimal;
        for (int i = 1; i <= columnCount; i++) {
            columnType = metaData.getColumnType(i);
            switch (columnType) {
            case Types.INTEGER:
            case Types.TINYINT:
            case Types.SMALLINT:
                value = Integer.toString(rs.getInt(i));
                break;
            case Types.DOUBLE:
                value = Double.toString(rs.getDouble(i));
                break;
            case Types.FLOAT:
                value = Float.toString(rs.getFloat(i));
                break;
            case Types.BOOLEAN:
            case Types.BIT:
                value = Boolean.toString(rs.getBoolean(i));
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                bigDecimal = rs.getBigDecimal(i);
                if (bigDecimal != null) {
                    value = bigDecimal.toPlainString();
                } else {
                    value = null;
                }
                break;
            case Types.BIGINT:
                value = Long.toString(rs.getLong(i));
                break;
            default:
                value = rs.getString(i);
                break;
            }
            generatedKeys.add(i - 1, value);
        }
        return generatedKeys;
    }

    private void createProcessedStatement(Connection conn, PreparedStatement stmt, BRefValueArray param) {
        createProcessedStatement(conn, stmt, param, null);
    }

    private void createProcessedStatement(Connection conn, PreparedStatement stmt, BRefValueArray params, String
            dataSourceName) {
        if (params == null) {
            return;
        }
        int paramCount = (int) params.size();
        int currentOrdinal = 0;
        for (int index = 0; index < paramCount; index++) {
            BStruct paramStruct = (BStruct) params.get(index);
            if (paramStruct != null) {
                String sqlType = getSQLType(paramStruct);
                BValue value = paramStruct.getRefField(1);
                int direction = getParameterDirection(paramStruct);
                //If the parameter is an array and sql type is not "array" then treat it as an array of parameters
                if (value != null && value.getType().getTag() == TypeTags.ARRAY_TAG && !Constants.SQLDataTypes.ARRAY
                        .equalsIgnoreCase(sqlType)) {
                    int arrayLength = (int) ((BNewArray) value).size();
                    int typeTag = ((BArrayType) value.getType()).getElementType().getTag();
                    for (int i = 0; i < arrayLength; i++) {
                        BValue paramValue;
                        switch (typeTag) {
                        case TypeTags.INT_TAG:
                            paramValue = new BInteger(((BIntArray) value).get(i));
                            break;
                        case TypeTags.FLOAT_TAG:
                            paramValue = new BFloat(((BFloatArray) value).get(i));
                            break;
                        case TypeTags.STRING_TAG:
                            paramValue = new BString(((BStringArray) value).get(i));
                            break;
                        case TypeTags.BOOLEAN_TAG:
                            paramValue = new BBoolean(((BBooleanArray) value).get(i) > 0);
                            break;
                        case TypeTags.BLOB_TAG:
                            paramValue = new BBlob(((BBlobArray) value).get(i));
                            break;
                        default:
                            throw new BallerinaException("unsupported array type for parameter index " + index);
                        }
                        if (Constants.SQLDataTypes.REFCURSOR.equals(sqlType)) {
                            setParameter(conn, stmt, sqlType, paramValue, direction, currentOrdinal, dataSourceName);
                        } else {
                            setParameter(conn, stmt, sqlType, paramValue, direction, currentOrdinal);
                        }
                        currentOrdinal++;
                    }
                } else {
                    if (Constants.SQLDataTypes.REFCURSOR.equals(sqlType)) {
                        setParameter(conn, stmt, sqlType, value, direction, currentOrdinal, dataSourceName);
                    } else {
                        setParameter(conn, stmt, sqlType, value, direction, currentOrdinal);
                    }
                    currentOrdinal++;
                }
            } else {
                SQLDatasourceUtils.setNullObject(stmt, index);
                currentOrdinal++;
            }
        }
    }

    private void setParameter(Connection conn, PreparedStatement stmt, String sqlType, BValue value, int direction,
            int index) {
        setParameter(conn, stmt, sqlType, value, direction, index, null);
    }

    private void setParameter(Connection conn, PreparedStatement stmt, String sqlType, BValue value, int direction,
            int index, String databaseName) {
        if (sqlType == null || sqlType.isEmpty()) {
            SQLDatasourceUtils.setStringValue(stmt, value, index, direction, Types.VARCHAR);
        } else {
            String sqlDataType = sqlType.toUpperCase(Locale.getDefault());
            switch (sqlDataType) {
            case Constants.SQLDataTypes.SMALLINT:
                SQLDatasourceUtils.setIntValue(stmt, value, index, direction, Types.INTEGER);
                break;
            case Constants.SQLDataTypes.VARCHAR:
                SQLDatasourceUtils.setStringValue(stmt, value, index, direction, Types.VARCHAR);
                break;
            case Constants.SQLDataTypes.CHAR:
                SQLDatasourceUtils.setStringValue(stmt, value, index, direction, Types.CHAR);
                break;
            case Constants.SQLDataTypes.LONGVARCHAR:
                SQLDatasourceUtils.setStringValue(stmt, value, index, direction, Types.LONGVARCHAR);
                break;
            case Constants.SQLDataTypes.NCHAR:
                SQLDatasourceUtils.setNStringValue(stmt, value, index, direction, Types.NCHAR);
                break;
            case Constants.SQLDataTypes.NVARCHAR:
                SQLDatasourceUtils.setNStringValue(stmt, value, index, direction, Types.NVARCHAR);
                break;
            case Constants.SQLDataTypes.LONGNVARCHAR:
                SQLDatasourceUtils.setNStringValue(stmt, value, index, direction, Types.LONGNVARCHAR);
                break;
            case Constants.SQLDataTypes.DOUBLE:
                SQLDatasourceUtils.setDoubleValue(stmt, value, index, direction, Types.DOUBLE);
                break;
            case Constants.SQLDataTypes.NUMERIC:
            case Constants.SQLDataTypes.DECIMAL:
                SQLDatasourceUtils.setNumericValue(stmt, value, index, direction, Types.NUMERIC);
                break;
            case Constants.SQLDataTypes.BIT:
            case Constants.SQLDataTypes.BOOLEAN:
                SQLDatasourceUtils.setBooleanValue(stmt, value, index, direction, Types.BIT);
                break;
            case Constants.SQLDataTypes.TINYINT:
                SQLDatasourceUtils.setTinyIntValue(stmt, value, index, direction, Types.TINYINT);
                break;
            case Constants.SQLDataTypes.BIGINT:
            case Constants.SQLDataTypes.INTEGER:
                SQLDatasourceUtils.setBigIntValue(stmt, value, index, direction, Types.BIGINT);
                break;
            case Constants.SQLDataTypes.REAL:
            case Constants.SQLDataTypes.FLOAT:
                SQLDatasourceUtils.setRealValue(stmt, value, index, direction, Types.FLOAT);
                break;
            case Constants.SQLDataTypes.DATE:
                SQLDatasourceUtils.setDateValue(stmt, value, index, direction, Types.DATE);
                break;
            case Constants.SQLDataTypes.TIMESTAMP:
            case Constants.SQLDataTypes.DATETIME:
                SQLDatasourceUtils.setTimeStampValue(stmt, value, index, direction, Types.TIMESTAMP, utcCalendar);
                break;
            case Constants.SQLDataTypes.TIME:
                SQLDatasourceUtils.setTimeValue(stmt, value, index, direction, Types.TIME, utcCalendar);
                break;
            case Constants.SQLDataTypes.BINARY:
                SQLDatasourceUtils.setBinaryValue(stmt, value, index, direction, Types.BINARY);
                break;
            case Constants.SQLDataTypes.BLOB:
                SQLDatasourceUtils.setBlobValue(stmt, value, index, direction, Types.BLOB);
                break;
            case Constants.SQLDataTypes.LONGVARBINARY:
                SQLDatasourceUtils.setBlobValue(stmt, value, index, direction, Types.LONGVARBINARY);
                break;
            case Constants.SQLDataTypes.VARBINARY:
                SQLDatasourceUtils.setBinaryValue(stmt, value, index, direction, Types.VARBINARY);
                break;
            case Constants.SQLDataTypes.CLOB:
                SQLDatasourceUtils.setClobValue(stmt, value, index, direction, Types.CLOB);
                break;
            case Constants.SQLDataTypes.NCLOB:
                SQLDatasourceUtils.setNClobValue(stmt, value, index, direction, Types.NCLOB);
                break;
            case Constants.SQLDataTypes.ARRAY:
                SQLDatasourceUtils.setArrayValue(conn, stmt, value, index, direction, Types.ARRAY);
                break;
            case Constants.SQLDataTypes.STRUCT:
                SQLDatasourceUtils
                        .setUserDefinedValue(conn, stmt, value, index, direction, Types.STRUCT);
                break;
            case Constants.SQLDataTypes.REFCURSOR:
                SQLDatasourceUtils.setRefCursorValue(conn, stmt, index, direction, databaseName);
                break;
            default:
                throw new BallerinaException("unsupported datatype as parameter: " + sqlType + " index:" + index);
            }
        }
    }

    private boolean isRefCursorOutParamPresent(BRefValueArray params) {
        boolean refCursorOutParamPresent = false;
        int paramCount = (int) params.size();
        for (int index = 0; index < paramCount; index++) {
            BStruct paramValue = (BStruct) params.get(index);
            if (paramValue != null) {
                String sqlType = getSQLType(paramValue);
                int direction = getParameterDirection(paramValue);
                if (direction == Constants.QueryParamDirection.OUT && Constants.SQLDataTypes.REFCURSOR
                        .equals(sqlType)) {
                    refCursorOutParamPresent = true;
                    break;
                }
            }
        }
        return refCursorOutParamPresent;
    }

    private void setOutParameters(Context context, CallableStatement stmt, BRefValueArray
            params, TableResourceManager rm) {
        if (params == null) {
            return;
        }
        int paramCount = (int) params.size();
        for (int index = 0; index < paramCount; index++) {
            BStruct paramValue = (BStruct) params.get(index);
            if (paramValue != null) {
                String sqlType = getSQLType(paramValue);
                int direction = getParameterDirection(paramValue);
                if (direction == Constants.QueryParamDirection.INOUT
                        || direction == Constants.QueryParamDirection.OUT) {
                    setOutParameterValue(context, stmt, sqlType, index, paramValue, rm);
                }
            } else {
                throw new BallerinaException("out value cannot set for null parameter with index: " + index);
            }
        }
    }

    private void setOutParameterValue(Context context, CallableStatement stmt, String sqlType,
            int index, BStruct paramValue, TableResourceManager resourceManager) {
        try {
            String sqlDataType = sqlType.toUpperCase(Locale.getDefault());
            switch (sqlDataType) {
            case Constants.SQLDataTypes.INTEGER: {
                int value = stmt.getInt(index + 1);
                paramValue.setRefField(1, new BInteger(value)); //Value is the first position of the struct
            }
            break;
            case Constants.SQLDataTypes.VARCHAR: {
                String value = stmt.getString(index + 1);
                paramValue.setRefField(1, new BString(value));
            }
            break;
            case Constants.SQLDataTypes.NUMERIC:
            case Constants.SQLDataTypes.DECIMAL: {
                BigDecimal value = stmt.getBigDecimal(index + 1);
                if (value == null) {
                    paramValue.setRefField(1, new BFloat(0));
                } else {
                    paramValue.setRefField(1, new BFloat(value.doubleValue()));
                }
            }
            break;
            case Constants.SQLDataTypes.BIT:
            case Constants.SQLDataTypes.BOOLEAN: {
                boolean value = stmt.getBoolean(index + 1);
                paramValue.setRefField(1, new BBoolean(value));
            }
            break;
            case Constants.SQLDataTypes.TINYINT: {
                byte value = stmt.getByte(index + 1);
                paramValue.setRefField(1, new BInteger(value));
            }
            break;
            case Constants.SQLDataTypes.SMALLINT: {
                short value = stmt.getShort(index + 1);
                paramValue.setRefField(1, new BInteger(value));
            }
            break;
            case Constants.SQLDataTypes.BIGINT: {
                long value = stmt.getLong(index + 1);
                paramValue.setRefField(1, new BInteger(value));
            }
            break;
            case Constants.SQLDataTypes.REAL:
            case Constants.SQLDataTypes.FLOAT: {
                float value = stmt.getFloat(index + 1);
                paramValue.setRefField(1, new BFloat(value));
            }
            break;
            case Constants.SQLDataTypes.DOUBLE: {
                double value = stmt.getDouble(index + 1);
                paramValue.setRefField(1, new BFloat(value));
            }
            break;
            case Constants.SQLDataTypes.CLOB: {
                Clob value = stmt.getClob(index + 1);
                paramValue.setRefField(1, new BString(SQLDatasourceUtils.getString(value)));
            }
            break;
            case Constants.SQLDataTypes.BLOB: {
                Blob value = stmt.getBlob(index + 1);
                paramValue.setRefField(1, new BString(SQLDatasourceUtils.getString(value)));
            }
            break;
            case Constants.SQLDataTypes.BINARY: {
                byte[] value = stmt.getBytes(index + 1);
                paramValue.setRefField(1, new BString(SQLDatasourceUtils.getString(value)));
            }
            break;
            case Constants.SQLDataTypes.DATE: {
                Date value = stmt.getDate(index + 1);
                paramValue.setRefField(1, new BString(SQLDatasourceUtils.getString(value)));
            }
            break;
            case Constants.SQLDataTypes.TIMESTAMP:
            case Constants.SQLDataTypes.DATETIME: {
                Timestamp value = stmt.getTimestamp(index + 1, utcCalendar);
                paramValue.setRefField(1, new BString(SQLDatasourceUtils.getString(value)));
            }
            break;
            case Constants.SQLDataTypes.TIME: {
                Time value = stmt.getTime(index + 1, utcCalendar);
                paramValue.setRefField(1, new BString(SQLDatasourceUtils.getString(value)));
            }
            break;
            case Constants.SQLDataTypes.ARRAY: {
                Array value = stmt.getArray(index + 1);
                paramValue.setRefField(1, new BString(SQLDatasourceUtils.getString(value)));
            }
            break;
            case Constants.SQLDataTypes.STRUCT: {
                Object value = stmt.getObject(index + 1);
                String stringValue = "";
                if (value != null) {
                    if (value instanceof Struct) {
                        stringValue = SQLDatasourceUtils.getString((Struct) value);
                    } else {
                        stringValue = value.toString();
                    }
                }
                paramValue.setRefField(1, new BString(stringValue));
            }
            break;
            case Constants.SQLDataTypes.REFCURSOR: {
                ResultSet rs = (ResultSet) stmt.getObject(index + 1);
                BStructType structType = getStructType(paramValue);
                if (structType !=  null) {
                    resourceManager.addResultSet(rs);
                    paramValue.setRefField(1,
                            constructTable(resourceManager, context, rs, getStructType(paramValue)));
                } else {
                    throw new BallerinaException("The Struct Type for the result set pointed by the Ref Cursor cannot"
                            + " be null");
                }
                break;
            }
            default:
                throw new BallerinaException(
                        "unsupported datatype as out/inout parameter: " + sqlType + " index:" + index);
            }
        } catch (SQLException e) {
            throw new BallerinaException("error in getting out parameter value: " + e.getMessage(), e);
        }
    }

    private boolean hasOutParams(BRefValueArray params) {
        int paramCount = (int) params.size();
        for (int index = 0; index < paramCount; index++) {
            BStruct paramValue = (BStruct) params.get(index);
            int direction = getParameterDirection(paramValue);
            if (direction == Constants.QueryParamDirection.OUT || direction == Constants.QueryParamDirection.INOUT) {
                return true;
            }
        }
        return false;
    }

    private List<ResultSet> executeStoredProc(CallableStatement stmt) throws SQLException {
        boolean resultAndNoUpdateCount = stmt.execute();
        List<ResultSet> resultSets = new ArrayList<>();
        ResultSet result = null;
        while (true) {
            if (!resultAndNoUpdateCount) {
                // Current result is an update count(not a ResultSet) or there is no result at all
                int updateCount = stmt.getUpdateCount();
                if (updateCount == -1) {
                    // There is no result at all
                    break;
                }
            } else {
                // Current result is a ResultSet
                result = stmt.getResultSet();
                resultSets.add(result);
            }
            // This point reaches if current result was an update count. So it is needed to capture any remaining
            // results
            try {
                resultAndNoUpdateCount = stmt.getMoreResults(Statement.KEEP_CURRENT_RESULT);
            } catch (SQLException e) {
                break;
            }
        }
        return resultSets;
    }

    private Connection getDatabaseConnection(Context context, SQLDatasource datasource, boolean isInTransaction)
            throws SQLException {
        Connection conn;
        if (!isInTransaction) {
            conn = datasource.getSQLConnection();
            return conn;
        } else {
            //This is when there is an infected transaction block. But this is not participated to the transaction
            //since the action call is outside of the transaction block.
            if (!context.getLocalTransactionInfo().hasTransactionBlock()) {
                conn = datasource.getSQLConnection();
                return conn;
            }
        }
        String connectorId = datasource.getConnectorId();
        boolean isXAConnection = datasource.isXAConnection();
        LocalTransactionInfo localTransactionInfo = context.getLocalTransactionInfo();
        String globalTxId = localTransactionInfo.getGlobalTransactionId();
        int currentTxBlockId = localTransactionInfo.getCurrentTransactionBlockId();
        BallerinaTransactionContext txContext = localTransactionInfo.getTransactionContext(connectorId);
        if (txContext == null) {
            if (isXAConnection) {
                XAConnection xaConn = datasource.getXADataSource().getXAConnection();
                XAResource xaResource = xaConn.getXAResource();
                TransactionResourceManager.getInstance().beginXATransaction(globalTxId, currentTxBlockId, xaResource);
                conn = xaConn.getConnection();
                txContext = new SQLTransactionContext(conn, xaResource);
            } else {
                conn = datasource.getSQLConnection();
                conn.setAutoCommit(false);
                txContext = new SQLTransactionContext(conn);
            }
            localTransactionInfo.registerTransactionContext(connectorId, txContext);
            TransactionResourceManager.getInstance().register(globalTxId, currentTxBlockId, txContext);
        } else {
            conn = ((SQLTransactionContext) txContext).getConnection();
        }
        return conn;
    }

    private BTable constructTable(TableResourceManager rm, Context context, ResultSet rs, BStructType structType)
            throws SQLException {
        List<ColumnDefinition> columnDefinitions = getColumnDefinitions(rs);
        return new BTable(new SQLDataIterator(rm, rs, utcCalendar, columnDefinitions, structType,
                Utils.getTimeStructInfo(context), Utils.getTimeZoneStructInfo(context)));
    }

    private String getSQLType(BStruct parameter) {
        String sqlType = "";
        BEnumerator typeEnum = (BEnumerator) parameter.getRefField(0);
        if (typeEnum != null) {
            sqlType = typeEnum.getName();
        }
        return sqlType;

    }

    private BStructType getStructType(BStruct parameter) {
        BTypeDescValue type = (BTypeDescValue) parameter.getRefField(3);
        BStructType structType = null;
        if (type != null) {
            structType = (BStructType) type.value();
        }
        return structType;
    }

    private int getParameterDirection(BStruct parameter) {
        int direction = 0;
        BEnumerator dirEnum = (BEnumerator) parameter.getRefField(2);
        if (dirEnum != null) {
            String sqlType = dirEnum.getName();
            switch (sqlType) {
            case Constants.QueryParamDirection.DIR_OUT:
                direction = Constants.QueryParamDirection.OUT;
                break;
            case Constants.QueryParamDirection.DIR_INOUT:
                direction = Constants.QueryParamDirection.INOUT;
                break;
            }
        }
        return direction;

    }
}
