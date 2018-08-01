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
package org.ballerinalang.database.sql.actions;

import org.ballerinalang.bre.Context;
import org.ballerinalang.database.sql.Constants;
import org.ballerinalang.database.sql.SQLDatasource;
import org.ballerinalang.database.sql.SQLDatasourceUtils;
import org.ballerinalang.model.types.BStructureType;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BRefValueArray;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;

import static org.ballerinalang.util.BLangConstants.BALLERINA_BUILTIN_PKG;

/**
 * {@code Select} is the Select action implementation of the SQL Connector.
 *
 * @since 0.8.0
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "sql",
        functionName = "select",
        receiver = @Receiver(type = TypeKind.OBJECT, structType = Constants.CALLER_ACTIONS),
        args = {
                @Argument(name = "sqlQuery", type = TypeKind.STRING),
                @Argument(name = "recordType", type = TypeKind.TYPEDESC),
                @Argument(name = "loadToMemory", type = TypeKind.BOOLEAN),
                @Argument(name = "parameters", type = TypeKind.ARRAY, elementType = TypeKind.UNION,
                          structType = "Param")
        },
        returnType = {
                @ReturnType(type = TypeKind.TABLE),
                @ReturnType(type = TypeKind.RECORD, structType = "error", structPackage = BALLERINA_BUILTIN_PKG)
        }
)
public class Select extends AbstractSQLAction {

    @Override
    public void execute(Context context) {
        try {
            BMap<String, BValue> bConnector = (BMap<String, BValue>) context.getRefArgument(0);

            String query = context.getStringArgument(0);
            BStructureType structType = getStructType(context, 1);
            boolean loadSQLTableToMemory = context.getBooleanArgument(0);

            BRefValueArray parameters = (BRefValueArray) context.getNullableRefArgument(2);
            SQLDatasource datasource = (SQLDatasource) bConnector.getNativeData(Constants.CALLER_ACTIONS);

            checkAndObserveSQLAction(context, datasource, query);
            executeQuery(context, datasource, query, parameters, structType, loadSQLTableToMemory);
        } catch (Throwable e) {
            context.setReturnValues(SQLDatasourceUtils.getSQLConnectorError(context, e));
            SQLDatasourceUtils.handleErrorOnTransaction(context);
            checkAndObserveSQLError(context, e.getMessage());
        }
    }
}
