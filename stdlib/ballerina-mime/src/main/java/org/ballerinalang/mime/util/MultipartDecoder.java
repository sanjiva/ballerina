/*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/

package org.ballerinalang.mime.util;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.ballerinalang.bre.Context;
import org.ballerinalang.connector.api.ConnectorUtils;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.jvnet.mimepull.MIMEConfig;
import org.jvnet.mimepull.MIMEMessage;
import org.jvnet.mimepull.MIMEPart;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import static org.ballerinalang.mime.util.Constants.BOUNDARY;
import static org.ballerinalang.mime.util.Constants.BYTE_LIMIT;
import static org.ballerinalang.mime.util.Constants.CONTENT_DISPOSITION_STRUCT;
import static org.ballerinalang.mime.util.Constants.CONTENT_ID_INDEX;
import static org.ballerinalang.mime.util.Constants.ENTITY;
import static org.ballerinalang.mime.util.Constants.ENTITY_HEADERS;
import static org.ballerinalang.mime.util.Constants.FIRST_ELEMENT;
import static org.ballerinalang.mime.util.Constants.MEDIA_TYPE;
import static org.ballerinalang.mime.util.Constants.NO_CONTENT_LENGTH_FOUND;
import static org.ballerinalang.mime.util.Constants.PROTOCOL_PACKAGE_MIME;

/**
 * Responsible for decoding an inputstream to get a set of multiparts.
 *
 * @since 0.963.0
 */
public class MultipartDecoder {

    /**
     * Decode inputstream and populate ballerina body parts.
     *
     * @param context     Represent ballerina context
     * @param entity      Represent ballerina entity which needs to be populated with body parts
     * @param contentType Content-Type of the top level message
     * @param inputStream Represent input stream coming from the request/response
     */
    public static void parseBody(Context context, BStruct entity, String contentType, InputStream inputStream) {
        try {
            List<MIMEPart> mimeParts = decodeBodyParts(contentType, inputStream);
            if (mimeParts != null && !mimeParts.isEmpty()) {
                populateBallerinaParts(context, entity, mimeParts);
            }
        } catch (MimeTypeParseException e) {
            throw new BallerinaException("Error occurred while decoding body parts from inputstream " + e.getMessage());
        }
    }

    /**
     * Decode multiparts from a given input stream.
     *
     * @param contentType Content-Type of the top level message
     * @param inputStream Represent input stream coming from the request/response
     * @return A list of mime parts
     * @throws MimeTypeParseException When an inputstream cannot be decoded properly
     */
    public static List<MIMEPart> decodeBodyParts(String contentType, InputStream inputStream)
            throws MimeTypeParseException {
        MimeType mimeType = new MimeType(contentType);
        final MIMEMessage mimeMessage = new MIMEMessage(inputStream,
                mimeType.getParameter(BOUNDARY),
                getMimeConfig());
        return mimeMessage.getAttachments();
    }

    /**
     * Create mime configuration with the maximum memory limit.
     *
     * @return MIMEConfig which defines configuration for MIME message parsing and storing
     */
    private static MIMEConfig getMimeConfig() {
        MIMEConfig mimeConfig = new MIMEConfig();
        mimeConfig.setMemoryThreshold(BYTE_LIMIT);
        return mimeConfig;
    }

    /**
     * Populate ballerina body parts from the given mime parts and set it to top level entity.
     *
     * @param context   Represent ballerina context
     * @param entity    Represent top level entity that the body parts needs to be attached to
     * @param mimeParts List of decoded mime parts
     */
    private static void populateBallerinaParts(Context context, BStruct entity, List<MIMEPart> mimeParts) {
        ArrayList<BStruct> bodyParts = new ArrayList<>();
        for (final MIMEPart mimePart : mimeParts) {
            BStruct partStruct = ConnectorUtils.createAndGetStruct(context, PROTOCOL_PACKAGE_MIME, ENTITY);
            BStruct mediaType = ConnectorUtils.createAndGetStruct(context, PROTOCOL_PACKAGE_MIME, MEDIA_TYPE);
            populateBodyPart(context, mimePart, partStruct, mediaType);
            bodyParts.add(partStruct);
        }
        EntityBodyHandler.setPartsToTopLevelEntity(entity, bodyParts);
    }

    /**
     * Populate ballerina body part with header info and actual body.
     *
     * @param context    Represent ballerina context
     * @param mimePart   Represent a decoded mime part
     * @param partStruct Represent a ballerina body part that needs to be filled with data
     * @param mediaType  Represent the content type of the body part
     */
    private static void populateBodyPart(Context context, MIMEPart mimePart, BStruct partStruct, BStruct mediaType) {
        partStruct.addNativeData(ENTITY_HEADERS, HeaderUtil.setBodyPartHeaders(mimePart.getAllHeaders(),
                new DefaultHttpHeaders()));
        populateContentLength(mimePart, partStruct);
        populateContentId(mimePart, partStruct);
        populateContentType(mimePart, partStruct, mediaType);
        List<String> contentDispositionHeaders = mimePart.getHeader(HttpHeaderNames.CONTENT_DISPOSITION.toString());
        if (HeaderUtil.isHeaderExist(contentDispositionHeaders)) {
            BStruct contentDisposition = ConnectorUtils.createAndGetStruct(context, PROTOCOL_PACKAGE_MIME,
                    CONTENT_DISPOSITION_STRUCT);
            populateContentDisposition(partStruct, contentDispositionHeaders, contentDisposition);
        }
        EntityBodyHandler.populateBodyContent(partStruct, mimePart);
    }

    private static void populateContentDisposition(BStruct partStruct, List<String> contentDispositionHeaders,
                                                   BStruct contentDisposition) {
        MimeUtil.setContentDisposition(contentDisposition, partStruct, contentDispositionHeaders
                .get(FIRST_ELEMENT));
    }

    private static void populateContentType(MIMEPart mimePart, BStruct partStruct, BStruct mediaType) {
        MimeUtil.setContentType(mediaType, partStruct, mimePart.getContentType());
    }

    private static void populateContentId(MIMEPart mimePart, BStruct partStruct) {
        partStruct.setStringField(CONTENT_ID_INDEX, mimePart.getContentId());
    }

    private static void populateContentLength(MIMEPart mimePart, BStruct partStruct) {
        List<String> lengthHeaders = mimePart.getHeader(HttpHeaderNames.CONTENT_LENGTH.toString());
        if (HeaderUtil.isHeaderExist(lengthHeaders)) {
            MimeUtil.setContentLength(partStruct, Integer.parseInt(lengthHeaders.get(FIRST_ELEMENT)));
        } else {
            MimeUtil.setContentLength(partStruct, NO_CONTENT_LENGTH_FOUND);
        }
    }
}
