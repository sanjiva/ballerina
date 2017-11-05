/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.logging;

import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.logging.formatters.HTTPTraceLogFormatter;
import org.ballerinalang.logging.util.BLogLevel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ballerinalang.logging.util.Constants.BALLERINA_LOG_INSTANCES;
import static org.ballerinalang.logging.util.Constants.BALLERINA_USER_LOG;
import static org.ballerinalang.logging.util.Constants.EMPTY_CONFIG;
import static org.ballerinalang.logging.util.Constants.HTTP_TRACE_LOG;
import static org.ballerinalang.logging.util.Constants.LOG_LEVEL;

/**
 * Java util logging manager for ballerina which overrides the readConfiguration method to replace placeholders
 * having system or environment variables.
 *
 * @since 0.8.0
 */
public class BLogManager extends LogManager {

    public static final String BALLERINA_ROOT_LOGGER_NAME = "ballerina";
    public static final int LOGGER_PREFIX_LENGTH = BALLERINA_ROOT_LOGGER_NAME.length() + 1; // +1 to account for the .

    private static final Pattern varPattern = Pattern.compile("\\$\\{([^}]*)}");
    private static final String LOG_CONFIG_FILE = "logging.properties";
    private static final String SP_LOG_CONFIG_FILE = "java.util.logging.config.file";

    private Map<String, BLogLevel> loggerLevels = new HashMap<>();
    private BLogLevel ballerinaUserLogLevel;
    private Logger httpTraceLogger;

    @Override
    public void readConfiguration(InputStream ins) throws IOException, SecurityException {
        Properties properties = getDefaultLogConfiguration();

        // override the default configs if the user has provided a config file
        if (System.getProperty(SP_LOG_CONFIG_FILE) != null) {
            properties.load(ins);
        }

        properties.forEach((k, v) -> {
            String val = substituteVariables((String) v);
            properties.setProperty((String) k, val);
        });

        super.readConfiguration(propertiesToInputStream(properties));
    }

    public void loadUserProvidedLogConfiguration() {
        ConfigRegistry configRegistry = ConfigRegistry.getInstance();

        String instancesVal = configRegistry.getGlobalConfigValue(BALLERINA_LOG_INSTANCES);
        if (!EMPTY_CONFIG.equals(instancesVal)) {
            String[] loggerInstances = instancesVal.split(",");

            for (String instanceId : loggerInstances) {
                loggerLevels.put(instanceId,
                                 BLogLevel.toBLogLevel(configRegistry.getInstanceConfigValue(instanceId, LOG_LEVEL)));
            }
        }

        // setup Ballerina user-level log level configuration
        String userLogLevel = configRegistry.getInstanceConfigValue(BALLERINA_USER_LOG, LOG_LEVEL);
        if (!EMPTY_CONFIG.equals(userLogLevel)) {
            ballerinaUserLogLevel = BLogLevel.toBLogLevel(userLogLevel);
        } else {
            // Default to INFO
            ballerinaUserLogLevel = BLogLevel.INFO;
        }

        // setup HTTP trace log level configuration
        String traceLogLevel = configRegistry.getInstanceConfigValue(HTTP_TRACE_LOG, LOG_LEVEL);
        if (!EMPTY_CONFIG.equals(traceLogLevel)) {
            loggerLevels.put(HTTP_TRACE_LOG, BLogLevel.toBLogLevel(traceLogLevel));
        }

        loggerLevels.put(BALLERINA_USER_LOG, ballerinaUserLogLevel);
    }

    public BLogLevel getPackageLogLevel(String pkg) {
        return loggerLevels.containsKey(pkg) ? loggerLevels.get(pkg) : ballerinaUserLogLevel;
    }

    public void setHttpTraceLogHandler() throws IOException {
        Handler handler = new ConsoleHandler();
        handler.setFormatter(new HTTPTraceLogFormatter());
        handler.setLevel(Level.FINE);

        if (httpTraceLogger == null) {
            // keep a reference to prevent this logger from being garbage collected
            httpTraceLogger = Logger.getLogger(HTTP_TRACE_LOG);
        }

        removeHandlers(httpTraceLogger);
        httpTraceLogger.addHandler(handler);
        httpTraceLogger.setLevel(Level.FINE);
    }

    private static void removeHandlers(Logger logger) {
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            logger.removeHandler(handler);
        }
    }

    private String substituteVariables(String value) {
        Matcher matcher = varPattern.matcher(value);
        boolean found = matcher.find();
        if (!found) {
            return value;
        }
        StringBuffer buffer = new StringBuffer();
        do {
            String sysPropertyKey = matcher.group(1);
            String sysPropertyValue = getSystemVariableValue(sysPropertyKey);
            if (sysPropertyValue != null && !sysPropertyValue.isEmpty()) {
                sysPropertyValue = sysPropertyValue.replace("\\", "\\\\");
                matcher.appendReplacement(buffer, sysPropertyValue);
            }
        } while (matcher.find());
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String getSystemVariableValue(String variableName) {
        String value;
        if (System.getProperty(variableName) != null) {
            value = System.getProperty(variableName);
        } else if (System.getenv(variableName) != null) {
            value = System.getenv(variableName);
        } else {
            value = variableName;
        }
        return value;
    }

    private InputStream propertiesToInputStream(Properties properties) throws IOException {
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            properties.store(outputStream, "Java util logging configuration properties");
            return new ByteArrayInputStream(outputStream.toByteArray());
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private Properties getDefaultLogConfiguration() throws IOException {
        Properties properties = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream(LOG_CONFIG_FILE);
        properties.load(in);
        return properties;
    }
}
