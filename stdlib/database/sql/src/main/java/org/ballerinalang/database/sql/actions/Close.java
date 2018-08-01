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
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;

import static org.ballerinalang.database.sql.Constants.SQL_PACKAGE_PATH;

/**
 * {@code Close} is the Close function implementation of the SQL Connector Connection pool.
 *
 * @since 0.8.4
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "sql",
        functionName = "close",
        args = {
                @Argument(name = "callerActions", type = TypeKind.RECORD, structType = Constants.CALLER_ACTIONS ,
                          structPackage = SQL_PACKAGE_PATH)
        }
)
public class Close extends AbstractSQLAction {

    @Override
    public void execute(Context context) {
        BMap<String, BValue> bConnector = (BMap<String, BValue>) context.getRefArgument(0);
        SQLDatasource datasource = (SQLDatasource) bConnector.getNativeData(Constants.CALLER_ACTIONS);
        closeConnections(datasource);
    }
}
