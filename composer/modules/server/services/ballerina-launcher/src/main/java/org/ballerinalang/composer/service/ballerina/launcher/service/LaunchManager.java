/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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

package org.ballerinalang.composer.service.ballerina.launcher.service;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.ballerinalang.composer.server.core.ServerConfig;
import org.ballerinalang.composer.service.ballerina.launcher.service.dto.CommandDTO;
import org.ballerinalang.composer.service.ballerina.launcher.service.dto.MessageDTO;
import org.ballerinalang.composer.service.ballerina.launcher.service.util.LaunchUtils;
import org.ballerinalang.composer.service.ballerina.launcher.service.util.LogParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import javax.websocket.Session;

/**
 * Launch Manager which manage launch requests from the clients.
 */
public class LaunchManager {

    private static final Logger logger = LoggerFactory.getLogger(LaunchManager.class);

    private static final String LAUNCHER_CONFIG_KEY = "launcher";

    private static final String SERVICE_TRY_URL_CONFIG = "serviceTryURL";

    private ServerConfig serverConfig;

    private static LaunchManager launchManagerInstance;

    private Session launchSession;

    private volatile Command command;

    private HashSet<String> ports = new HashSet<String>();

    /**
     * Instantiates a new Debug manager.
     */
    protected LaunchManager() {
    }

    /**
     * Launch manager singleton.
     *
     * @return LaunchManager instance
     */
    public static LaunchManager getInstance(ServerConfig serverConfig) {
        synchronized (LaunchManager.class) {
            if (launchManagerInstance == null) {
                launchManagerInstance = new LaunchManager();
                launchManagerInstance.serverConfig = serverConfig;
            }
        }
        return launchManagerInstance;
    }

    public void pushLogToClient(String logLine) {
        pushMessageToClient(launchSession, LauncherConstants.OUTPUT, LauncherConstants.TRACE, logLine);
    }

    private void run(Command command) {
        Process program = null;
        this.command = command;

        // send a message if ballerina home is not set
        if (null == serverConfig.getBallerinaHome()) {
            pushMessageToClient(launchSession, LauncherConstants.ERROR, LauncherConstants.ERROR, LauncherConstants
                    .INVALID_BAL_PATH_MESSAGE);
            pushMessageToClient(launchSession, LauncherConstants.ERROR, LauncherConstants.ERROR, LauncherConstants
                    .SET_BAL_PATH_MESSAGE);
            return;
        }

        try {
            String[] cmdArray = command.getCommandArray();

            if (command.getSourceRoot() == null) {
                program = Runtime.getRuntime().exec(cmdArray);
            } else {
                program = Runtime.getRuntime().exec(cmdArray, null, new File(command.getSourceRoot()));

            }

            command.setProgram(program);

            pushMessageToClient(launchSession, LauncherConstants.EXECUTION_STARTED, LauncherConstants.INFO,
                    String.format(LauncherConstants.RUN_MESSAGE, command.getFileName()));

            if (command.isDebug()) {
                MessageDTO debugMessage = new MessageDTO();
                debugMessage.setCode(LauncherConstants.DEBUG);
                debugMessage.setPort(command.getPort());
                pushMessageToClient(launchSession, debugMessage);
            }

            new Thread(() -> LogParser.getLogParserInstance().startListener(launchManagerInstance)).start();

            // start a new thread to stream command output.
            Runnable output = LaunchManager.this::streamOutput;
            (new Thread(output)).start();

            Runnable error = LaunchManager.this::streamError;
            (new Thread(error)).start();
        } catch (IOException e) {
            pushMessageToClient(launchSession, LauncherConstants.EXIT, LauncherConstants.ERROR, e.getMessage());
        }
    }

    private void streamOutput() {
        try (InputStream inputStream = this.command.getProgram().getInputStream()) {
            while (this.command != null && this.command.getProgram().isAlive()) {
                String output = consumeStreamOutput(inputStream);
                if (!output.isEmpty()) {
                    String[] lines = output.split(System.lineSeparator());
                    for (String line : lines) {
                        // improve "server connector started" log message to have the service URL in it.
                        // This is to handle the cloud use case.
                        if (line.startsWith(LauncherConstants.SERVER_CONNECTOR_STARTED_AT_HTTP_CLOUD)
                                && getServerStartedURL() != null) {
                            this.updatePort(getServerStartedURL());
                            line = LauncherConstants.SERVER_CONNECTOR_STARTED_AT_HTTP_CLOUD + " "
                                    + getServerStartedURL();
                        }

                        // This is to handle local service run use case.
                        if (line.startsWith(LauncherConstants.SERVER_CONNECTOR_STARTED_AT_HTTP_LOCAL)
                                && getServerStartedURL() == null) {
                            // This is to handle local service run use case.
                            this.updatePort(line);
                            pushMessageToClient(launchSession, LauncherConstants.OUTPUT, LauncherConstants.DATA, line);
                        } else {
                            pushMessageToClient(launchSession, LauncherConstants.OUTPUT, LauncherConstants.DATA, line);
                        }
                    }
                }
            }
            pushMessageToClient(launchSession, LauncherConstants.EXECUTION_STOPPED, LauncherConstants.INFO,
                                LauncherConstants.END_MESSAGE);
            LogParser.getLogParserInstance().stopListner();
        } catch (IOException e) {
            logger.error("Error while sending output stream to client.", e);
        }
    }

    private void streamError() {
        try (InputStream inputStream = this.command.getProgram().getErrorStream()) {
            while (this.command != null && this.command.getProgram().isAlive()) {
                String output = consumeStreamOutput(inputStream);
                if (!output.isEmpty()) {
                    String[] lines = output.split(System.lineSeparator());
                    for (String line : lines) {
                        if (this.command.isErrorOutputEnabled()) {
                            pushMessageToClient(launchSession, LauncherConstants.OUTPUT, LauncherConstants.ERROR, line);
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error while sending error stream to client.", e);
        }
    }

    private void pushMessageToProcess(String[] input) {
        if (input.length > 0 && this.command != null && this.command.getProgram().isAlive()) {
            try {
                PrintWriter pw = new PrintWriter(this.command.getProgram().getOutputStream());
                pw.println(input[0]);
                pw.flush();
            } catch (Exception e) {
                logger.error("Error while sending input stream to client.", e);
            }
        }
    }

    private String consumeStreamOutput(InputStream inputStream) throws IOException {
        StringWriter outputWriter = new StringWriter();
        boolean stop = false;
        while (!stop) {
            int rv;
            if (inputStream.available() > 0 && (rv = inputStream.read()) != -1) {
                outputWriter.write(rv);
            } else {
                stop = true;
            }
        }
        return outputWriter.toString();
    }

    /**
     * Stop a running ballerina program.
     */
    public void stopProcess() {
        int pid = -1;
        try {
            if (this.command != null && this.command.getProgram().isAlive()) {
                //shutdown error streaming to prevent kill message displaying to user.
                this.command.setErrorOutputEnabled(false);

                String os = getOperatingSystem();
                if (os == null) {
                    logger.error("unsupported operating system");
                    pushMessageToClient(launchSession, LauncherConstants.UNSUPPORTED_OPERATING_SYSTEM,
                                        LauncherConstants.ERROR, LauncherConstants.TERMINATE_MESSAGE);
                    return;
                }
                Terminator terminator = new TerminatorFactory().getTerminator(os, this.command);
                if (terminator == null) {
                    logger.error("unsupported operating system");
                    pushMessageToClient(launchSession, LauncherConstants.UNSUPPORTED_OPERATING_SYSTEM,
                                        LauncherConstants.ERROR, LauncherConstants.TERMINATE_MESSAGE);
                    return;
                }

                terminator.terminate();
                LogParser.getLogParserInstance().stopListner();
                pushMessageToClient(launchSession, LauncherConstants.EXECUTION_TERMINATED, LauncherConstants.INFO,
                                    LauncherConstants.TERMINATE_MESSAGE);
            }
        } finally {
            this.command = null;
        }
    }

    private String getServerStartedURL() {
        // read configs provided in server config yaml file for launcher
        if (serverConfig.getCustomConfigs() != null &&
                serverConfig.getCustomConfigs().containsKey(LAUNCHER_CONFIG_KEY)) {
            return serverConfig.getCustomConfigs().get(LAUNCHER_CONFIG_KEY)
                    .get(SERVICE_TRY_URL_CONFIG);
        }
        return null;

    }

    /**
     * Returns name of the operating system running. null if not a unsupported operating system.
     *
     * @return operating system
     */
    private String getOperatingSystem() {
        if (LaunchUtils.isWindows()) {
            return "windows";
        } else if (LaunchUtils.isUnix() || LaunchUtils.isSolaris()) {
            return "unix";
        } else if (LaunchUtils.isMac()) {
            return "mac";
        }
        return null;
    }

    public void setSession(Session session) {
        this.launchSession = session;
    }

    public void processCommand(String json) {
        Gson gson = new Gson();
        CommandDTO command = gson.fromJson(json, CommandDTO.class);
        MessageDTO message;
        switch (command.getCommand()) {
            case LauncherConstants.RUN_PROGRAM:
                run(new Command(command.getFileName(), command.getFilePath(), command.getCommandArgs(), false));
                break;
            case LauncherConstants.DEBUG_PROGRAM:
                run(new Command(command.getFileName(), command.getFilePath(), command.getCommandArgs(), true));
                break;
            case LauncherConstants.TERMINATE:
                stopProcess();
                break;
            case LauncherConstants.INPUT:
                pushMessageToProcess(command.getCommandArgs());
                break;
            case LauncherConstants.PING:
                message = new MessageDTO();
                message.setCode(LauncherConstants.PONG);
                pushMessageToClient(launchSession, message);
                break;
            default:
                message = new MessageDTO();
                message.setCode(LauncherConstants.INVALID_CMD);
                message.setMessage(LauncherConstants.MSG_INVALID);
                pushMessageToClient(launchSession, message);
        }
    }

    /**
     * Push message to client.
     *
     * @param session the debug session
     * @param status  the status
     */
    public void pushMessageToClient(Session session, MessageDTO status) {
        Gson gson = new Gson();
        String json = gson.toJson(status);
        try {
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            logger.error("Error while pushing messages to client.", e);
        }
    }

    public void pushMessageToClient(Session session, String code, String type, String text) {
        MessageDTO message = new MessageDTO();
        message.setCode(code);
        message.setType(type);
        message.setMessage(text);
        pushMessageToClient(session, message);
    }

    /**
     * Gets the port of the from console log that starts with
     * LauncherConstants.SERVER_CONNECTOR_STARTED_AT_HTTP_LOCAL.
     *
     * @param line The log line.
     */
    private void updatePort(String line) {
        String port = this.getPort(line);
        if (StringUtils.isNotBlank(port)) {
            this.ports.add(port);
        }
    }

    private String getPort(String line) {
        String hostPort = StringUtils.substringAfterLast(line,
                LauncherConstants.SERVER_CONNECTOR_STARTED_AT_HTTP_LOCAL).trim();
        return StringUtils.substringAfterLast(hostPort, ":");
    }

    /**
     * Getter for running ports.
     *
     * @return ports.
     */
    public HashSet<String> getPorts() {
        return this.ports;
    }
}
