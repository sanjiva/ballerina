/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ballerinalang.composer.service.ballerina.launcher.service.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ballerinalang.composer.service.ballerina.launcher.service.LaunchManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LogParser.
 */
public class LogParser {

    static LogParser logParserInstance;
    static ServerSocket listenSocket;
    static BufferedReader logReader;

    static final Pattern ID_PATTERN = Pattern.compile("id: ([a-z0-9]*)");
    static final Pattern DIRECTION = Pattern.compile("(INBOUND|OUTBOUND)");
    static final Pattern HEADER = Pattern.compile("(?:INBOUND|OUTBOUND): (.*[\\n\\r])([\\s\\S]*)");
    static final Pattern HTTP_METHOD = Pattern.compile("(GET|POST|HEAD|POST|PUT|DELETE|CONNECT|OPTIONS|TRACE|PATCH)");
    static final Pattern PATH = Pattern.compile("(?:GET|POST|HEAD|POST|PUT|DELETE|CONNECT|OPTIONS|TRACE|PATCH)"
            + " ([^\\s]+)");
    static final Pattern CONTENT_TYPE = Pattern.compile("(?:content-type): ?(.*)",  Pattern.CASE_INSENSITIVE);

    public static LogParser getLogParserInstance() {
        if (logParserInstance == null) {
            logParserInstance = new LogParser();
        }
        return logParserInstance;
    }

    public void startListener(LaunchManager launchManagerInstance) {
        try {
            listenSocket = new ServerSocket(5010);
            Socket dataSocket = listenSocket.accept();
            logReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            String line;
            while ((line = logReader.readLine()) != null) {
                try {
                    JsonElement jelement = new JsonParser().parse(line);
                    JsonObject jobject = jelement.getAsJsonObject();
                    String rawRecord;
                    try {
                        rawRecord = jobject.get("record").getAsJsonObject().get("message").getAsString();
                    } catch (Exception e) {
                        rawRecord = jelement.getAsString();
                    }
                    jobject.addProperty("meta", parseLogLine(rawRecord));
                    launchManagerInstance.pushLogToClient(jobject.toString());
                } catch (Exception e) {
                    // do nothing
                }
            }
        } catch (Exception e) {
            stopListner();
        }
    }

    public void stopListner() {
        try {
            if (logReader != null) {
                logReader.close();
            }
            listenSocket.close();
        } catch (Exception e) {

        }
    }

    private String getId(String logLine) {
        Matcher matcher = ID_PATTERN.matcher(logLine);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private String getDirection(String logLine) {
        Matcher matcher = DIRECTION.matcher(logLine);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private String getHeaderType(String logLine) {
        Matcher matcher = HEADER.matcher(logLine);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private String getHeader(String logLine) {
        Matcher matcher = HEADER.matcher(logLine);
        if (matcher.find()) {
            return matcher.group(2);
        } else {
            return "";
        }
    }

    private String getHttpMethod(String logLine) {
        Matcher matcher = HTTP_METHOD.matcher(logLine);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private String getPath(String logLine) {
        Matcher matcher = PATH.matcher(logLine);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private String getContentType(String logLine) {
        Matcher matcher = CONTENT_TYPE.matcher(logLine);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private String getPayload(String header) {
        int startIndex = header.lastIndexOf("\n");
        if (startIndex != -1 && startIndex != header.length()) {
            return header.substring(startIndex + 1);
        }
        return "";
    }

    /**
     * Remove payload from header.
     * @param header String
     * @param payload String
     * @return header String
     */
    private String removePayload(String header, String payload) {
        return header.substring(0, header.length() - payload.length());
    }

    private String parseLogLine(String logLine) {

        Gson gson = new Gson();
        LogDTO log = new LogDTO();
        log.setId(getId(logLine));
        log.setDirection(getDirection(logLine));
        String header = getHeader(logLine);
        String headerType = getHeaderType(logLine);
        String payload = getPayload(header);
        log.setHeaders(removePayload(header, payload));
        log.setHeaderType(headerType);
        log.setContentType(getContentType(logLine));
        log.setHttpMethod(getHttpMethod(logLine));
        log.setPath(getPath(logLine));
        log.setPayload(payload);

        String json = gson.toJson(log);
        return json;
    }
}
