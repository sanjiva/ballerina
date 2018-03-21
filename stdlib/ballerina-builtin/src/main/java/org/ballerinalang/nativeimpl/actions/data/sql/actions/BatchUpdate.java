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
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BRefValueArray;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.nativeimpl.actions.data.sql.Constants;
import org.ballerinalang.nativeimpl.actions.data.sql.SQLDatasource;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.natives.annotations.ReturnType;

/**
 * {@code BatchUpdate} is the Batch update action implementation of the SQL Connector.
 *
 * @since 0.8.6
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "data.sql",
        functionName = "batchUpdate",
        receiver = @Receiver(type = TypeKind.STRUCT,
                             structType = "ClientConnector",
                             structPackage = "ballerina.data.sql"),
        args = {@Argument(name = "client", type = TypeKind.STRUCT),
                @Argument(name = "sqlQuery", type = TypeKind.STRING),
                @Argument(name = "parameters",
                          type = TypeKind.ARRAY,
                          elementType = TypeKind.STRUCT,
                          arrayDimensions = 2,
                          structType = "Parameter")
        },
        returnType = { @ReturnType(type = TypeKind.ARRAY, elementType = TypeKind.INT) })
public class BatchUpdate extends AbstractSQLAction {
    @Override
    public void execute(Context context) {
        BStruct bConnector = (BStruct) context.getRefArgument(0);
        String query = context.getStringArgument(0);
        BRefValueArray parameters = (BRefValueArray) context.getNullableRefArgument(1);
        SQLDatasource datasource = (SQLDatasource) bConnector.getNativeData(Constants.CLIENT_CONNECTOR);
        executeBatchUpdate(context, datasource, query, parameters);
    }
}
