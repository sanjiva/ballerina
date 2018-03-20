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
import io.netty.handler.codec.http.HttpHeaders;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BRefValueArray;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.runtime.message.BallerinaMessageDataSource;
import org.ballerinalang.runtime.message.MessageDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import static org.ballerinalang.mime.util.Constants.BODY_PARTS;
import static org.ballerinalang.mime.util.Constants.BOUNDARY;
import static org.ballerinalang.mime.util.Constants.CONTENT_ID;
import static org.ballerinalang.mime.util.Constants.CONTENT_ID_INDEX;
import static org.ballerinalang.mime.util.Constants.ENTITY_HEADERS;
import static org.ballerinalang.mime.util.Constants.MEDIA_TYPE_INDEX;
import static org.ballerinalang.mime.util.Constants.PARAMETER_MAP_INDEX;

/**
 * Act as multipart encoder.
 *
 * @since 0.963.0
 */
public class MultipartDataSource extends BallerinaMessageDataSource {
    private static final Logger log = LoggerFactory.getLogger(MultipartDataSource.class);

    private BStruct parentEntity;
    private String boundaryString;
    private OutputStream outputStream;
    private static final String DASH_BOUNDARY = "--";
    private static final String CRLF_POST_DASH = "\r\n--";
    private static final String CRLF_PRE_DASH = "--\r\n";
    private static final String CRLF = "\r\n";
    private static final char COMMA = ',';
    private static final char COLON = ':';
    private static final char SPACE = ' ';

    public MultipartDataSource(BStruct entityStruct, String boundaryString) {
        this.parentEntity = entityStruct;
        this.boundaryString = boundaryString;
    }

    @Override
    public void serializeData(OutputStream outputStream) {
        this.outputStream = outputStream;
        serializeBodyPart(outputStream, boundaryString, parentEntity);
    }

    /**
     * Serialize body parts including nested parts within them.
     *
     * @param outputStream         Represent the outputstream that the body parts will be written to
     * @param parentBoundaryString Represent the parent boundary string
     * @param parentBodyPart       Represent parent body part
     */
    private void serializeBodyPart(OutputStream outputStream, String parentBoundaryString, BStruct parentBodyPart) {
        final Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, Charset.defaultCharset()));
        BRefValueArray childParts = parentBodyPart.getNativeData(BODY_PARTS) != null ?
                (BRefValueArray) parentBodyPart.getNativeData(BODY_PARTS) : null;
        try {
            if (childParts == null) {
                return;
            }
            boolean isFirst = true;
            for (int i = 0; i < childParts.size(); i++) {
                BStruct childPart = (BStruct) childParts.get(i);
                // Write leading boundary string
                if (isFirst) {
                    isFirst = false;
                    writer.write(DASH_BOUNDARY);

                } else {
                    writer.write(CRLF_POST_DASH);
                }
                writer.write(parentBoundaryString);
                writer.write(CRLF);
                checkForNestedParts(writer, childPart);
                writeBodyContent(outputStream, childPart);
            }
            writeFinalBoundaryString(writer, parentBoundaryString);
        } catch (IOException e) {
            log.error("Error occurred while writing body parts to outputstream", e.getMessage());
        }
    }

    /**
     * If child part has nested parts, get a new boundary string and set it to Content-Type. After that write the
     * child part headers to outputstream and serialize its nested parts if it has any.
     *
     * @param writer    Represent the outputstream writer
     * @param childPart Represent a child part
     * @throws IOException When an error occurs while writing child part headers
     */
    private void checkForNestedParts(Writer writer, BStruct childPart) throws IOException {
        String childBoundaryString = null;
        if (MimeUtil.isNestedPartsAvailable(childPart)) {
            childBoundaryString = MimeUtil.getNewMultipartDelimiter();
            BStruct mediaType = (BStruct) childPart.getRefField(MEDIA_TYPE_INDEX);
            BMap<String, BValue> paramMap = (mediaType.getRefField(PARAMETER_MAP_INDEX) != null) ?
                    (BMap<String, BValue>) mediaType.getRefField(PARAMETER_MAP_INDEX) : new BMap<>();
            paramMap.put(BOUNDARY, new BString(childBoundaryString));
            mediaType.setRefField(PARAMETER_MAP_INDEX, paramMap);
        }
        writeBodyPartHeaders(writer, childPart);
        //Serialize nested parts
        if (childBoundaryString != null) {
            BRefValueArray nestedParts = (BRefValueArray) childPart.getNativeData(BODY_PARTS);
            if (nestedParts != null && nestedParts.size() > 0) {
                serializeBodyPart(this.outputStream, childBoundaryString, childPart);
            }
        }
    }

    /**
     * Write body part headers to output stream.
     *
     * @param writer   Represent the outputstream writer
     * @param bodyPart Represent ballerina body part
     * @throws IOException When an error occurs while writing body part headers
     */
    private void writeBodyPartHeaders(Writer writer, BStruct bodyPart) throws IOException {
        HttpHeaders httpHeaders;
        if (bodyPart.getNativeData(ENTITY_HEADERS) != null) {
            httpHeaders = (HttpHeaders) bodyPart.getNativeData(ENTITY_HEADERS);
        } else {
            httpHeaders = new DefaultHttpHeaders();
            bodyPart.addNativeData(ENTITY_HEADERS, httpHeaders);
        }
        String contentType = MimeUtil.getContentTypeWithParameters(bodyPart);
        httpHeaders.set(HttpHeaderNames.CONTENT_TYPE.toString(), contentType);
        String contentDisposition = MimeUtil.getContentDisposition(bodyPart);
        if (!contentDisposition.isEmpty()) {
            httpHeaders.set(HttpHeaderNames.CONTENT_DISPOSITION.toString(), contentDisposition);
        }
        if (bodyPart.getStringField(CONTENT_ID_INDEX) != null) {
            httpHeaders.set(CONTENT_ID, bodyPart.getStringField(CONTENT_ID_INDEX));
        }
        Iterator<Map.Entry<String, String>> iterator = httpHeaders.iteratorAsString();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            writer.write(entry.getKey());
            writer.write(COLON);
            writer.write(entry.getValue());
            writer.write(CRLF);
        }
        // Mark the end of the headers for this body part
        writer.write(CRLF);
        writer.flush();
    }

    /**
     * Write the final boundary string.
     *
     * @param writer         Represent an outputstream writer
     * @param boundaryString Represent the boundary as a string
     * @throws IOException When an error occurs while writing final boundary string
     */
    private void writeFinalBoundaryString(Writer writer, String boundaryString) throws IOException {
        writer.write(CRLF_POST_DASH);
        writer.write(boundaryString);
        writer.write(CRLF_PRE_DASH);
        writer.flush();
    }

    /**
     * Write body part content to outputstream.
     *
     * @param outputStream Represent an outputstream
     * @param bodyPart     Represent a ballerina body part
     * @throws IOException When an error occurs while writing body content
     */
    private void writeBodyContent(OutputStream outputStream, BStruct bodyPart) throws IOException {
        MessageDataSource messageDataSource = EntityBodyHandler.getMessageDataSource(bodyPart);
        if (messageDataSource != null) {
            messageDataSource.serializeData(outputStream);
        } else {
            EntityBodyHandler.writeByteChannelToOutputStream(bodyPart, outputStream);
        }
    }
}
