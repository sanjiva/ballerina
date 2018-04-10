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
package org.ballerinalang.nativeimpl.sql.actions;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.BStructType;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BRefValueArray;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.nativeimpl.sql.Constants;
import org.ballerinalang.nativeimpl.sql.SQLDatasource;
import org.ballerinalang.nativeimpl.sql.SQLDatasourceUtils;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;
import org.ballerinalang.util.observability.ObservabilityUtils;
import org.ballerinalang.util.observability.ObserverContext;

import static org.ballerinalang.util.observability.ObservabilityConstants.TAG_DB_TYPE_SQL;
import static org.ballerinalang.util.observability.ObservabilityConstants.TAG_KEY_DB_STATEMENT;
import static org.ballerinalang.util.observability.ObservabilityConstants.TAG_KEY_DB_TYPE;

/**
 * {@code Select} is the Select action implementation of the SQL Connector.
 *
 * @since 0.8.0
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "sql",
        functionName = "select",
        receiver = @Receiver(type = TypeKind.STRUCT, structType = Constants.SQL_CLIENT),
        args = {
                @Argument(name = "sqlQuery", type = TypeKind.STRING),
                @Argument(name = "parameters", type = TypeKind.ARRAY, elementType = TypeKind.STRUCT,
                        structType = "Parameter")
        },
        returnType = {
                @ReturnType(type = TypeKind.TABLE),
                @ReturnType(type = TypeKind.STRUCT, structType = "SQLConnectorError",
                            structPackage = "ballerina.sql")
        }
)
public class Select extends AbstractSQLAction {

    @Override
    public void execute(Context context) {
        try {
            BStruct bConnector = (BStruct) context.getRefArgument(0);
            String query = context.getStringArgument(0);
            BRefValueArray parameters = (BRefValueArray) context.getNullableRefArgument(1);
            BStructType structType = getStructType(context, 2);
            SQLDatasource datasource = (SQLDatasource) bConnector.getNativeData(Constants.SQL_CLIENT);

            ObserverContext observerContext = ObservabilityUtils.getCurrentContext(context.
                    getParentWorkerExecutionContext());
            observerContext.addTag(TAG_KEY_DB_STATEMENT, query);
            observerContext.addTag(TAG_KEY_DB_TYPE, TAG_DB_TYPE_SQL);

            executeQuery(context, datasource, query, parameters, structType);
        } catch (Throwable e) {
            context.setReturnValues(SQLDatasourceUtils.getSQLConnectorError(context, e));
            SQLDatasourceUtils.handleErrorOnTransaction(context);
        }
    }
}
