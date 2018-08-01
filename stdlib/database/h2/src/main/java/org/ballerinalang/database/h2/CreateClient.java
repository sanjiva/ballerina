/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.database.h2;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.connector.api.Value;
import org.ballerinalang.database.sql.Constants;
import org.ballerinalang.database.sql.SQLDatasourceUtils;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;

import java.util.Map;

/**
 * Returns the H2 Client connector.
 *
 * @since 0.970
 */

@BallerinaFunction(
        orgName = "ballerina", packageName = "h2",
        functionName = "createClient",
        args = {@Argument(name = "config", type = TypeKind.RECORD, structType = "ClientEndpointConfiguration")},
        isPublic = true
)
public class CreateClient extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        BMap<String, BValue> configBStruct = (BMap<String, BValue>) context.getRefArgument(0);
        Struct clientEndpointConfig = BLangConnectorSPIUtil.toStruct(configBStruct);
        Map<String, Value> dbOptions = clientEndpointConfig.getMapField(Constants.EndpointConfig.DB_OPTIONS);
        String urlOptions = "";
        if (dbOptions != null) {
            urlOptions = SQLDatasourceUtils.createJDBCDbOptions(Constants.JDBCUrlSeparators.H2_PROPERTY_BEGIN_SYMBOL,
                    Constants.JDBCUrlSeparators.H2_SEPARATOR, dbOptions);
        }
        BMap<String, BValue> sqlClient = SQLDatasourceUtils
                .createMultiModeDBClient(context, Constants.DBTypes.H2, clientEndpointConfig, urlOptions);
        context.setReturnValues(sqlClient);
    }
}
