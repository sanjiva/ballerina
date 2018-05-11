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

package org.ballerinalang.net.websub.util;

import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.mime.util.EntityBodyHandler;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.net.http.HttpConstants;
import org.ballerinalang.net.http.HttpUtil;
import org.ballerinalang.util.codegen.ProgramFile;
import org.wso2.transport.http.netty.message.HTTPCarbonMessage;

import static org.ballerinalang.mime.util.Constants.ENTITY;
import static org.ballerinalang.mime.util.Constants.ENTITY_BYTE_CHANNEL;
import static org.ballerinalang.mime.util.Constants.MEDIA_TYPE;
import static org.ballerinalang.mime.util.Constants.PROTOCOL_PACKAGE_MIME;

/**
 * Util class for WebSub.
 */
public class WebSubUtils {

    public static BStruct getHttpRequest(ProgramFile programFile, HTTPCarbonMessage httpCarbonMessage) {
        BStruct httpRequest = createBStruct(programFile, HttpConstants.PROTOCOL_PACKAGE_HTTP, HttpConstants.REQUEST);
        BStruct inRequestEntity = createBStruct(programFile, PROTOCOL_PACKAGE_MIME, ENTITY);
        BStruct mediaType = createBStruct(programFile, PROTOCOL_PACKAGE_MIME, MEDIA_TYPE);
        HttpUtil.populateInboundRequest(httpRequest, inRequestEntity, mediaType, httpCarbonMessage, programFile);
        HttpUtil.populateEntityBody(null, httpRequest, inRequestEntity, true);
        EntityBodyHandler.addMessageDataSource(inRequestEntity,
                                               EntityBodyHandler.constructJsonDataSource(inRequestEntity));
        //Set byte channel to null, once the message data source has been constructed
        inRequestEntity.addNativeData(ENTITY_BYTE_CHANNEL, null);
        return httpRequest;
    }

    private static BStruct createBStruct(ProgramFile programFile, String packagePath, String structName) {
        return BLangConnectorSPIUtil.createBStruct(programFile, packagePath, structName);
    }
}
