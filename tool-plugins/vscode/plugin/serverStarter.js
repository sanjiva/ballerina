/**
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

const net = require('net');
const path = require('path');
const { spawn } = require('child_process');
const openport = require('openport');
const { workspace, window } = require('vscode');

let serverProcess;
const libPath = '/bre/lib/*'
const composerlibPath = '/lib/resources/composer/services/*';
const main = 'org.ballerinalang.vscode.server.Main';

let LSService;
let parserService;
let outputChannel = {
    // By default just ignore all calls to outputchannel
    append: () => {},
    show: () => {},
};

function getClassPath() {
    const customClassPath = workspace.getConfiguration('ballerina').get('classpath');
    const sdkPath = workspace.getConfiguration('ballerina').get('home');
    const jarPath = path.join(__dirname, 'server-build', 'plugin-vscode-server.jar');
	// in windows class path seperated by ';'
	const sep = process.platform === 'win32' ? ';' : ':';
    let classpath = path.join(sdkPath, composerlibPath) + sep + path.join(sdkPath, libPath) + sep + jarPath;

    if (customClassPath) {
        classpath =  customClassPath + sep + classpath;
    }
    return classpath;
}

function startServices() {
    if (LSService) {
        // services already started
        return;
    }

    let onParserStarted;
    let onParserError;
    let onLSStarted;
    let onLSError;

    LSService = new Promise((res, rej) => {
        onLSStarted = res;
        onLSError = rej;
    })

    parserService = new Promise((res, rej) => {
        onParserStarted = res;
        onParserError = rej;
    })

    openport.find({count: 2}, (err, [LSPort, parserPort]) => {
        let server = net.createServer(stream => {
            console.log('Ballerina Language Server connected');
            // Server is closed so that no more connections will be accepted
            server.close();
            onLSStarted({ reader: stream, writer: stream });
            console.log('Ballerina Language Server connected on port: ', LSPort);
            outputChannel.append('Ballerina Language Server connected on port: ' + LSPort + '\n');
        });
        server.on('error', onLSError);
        server.listen(LSPort, () => {
            console.log('Listening for Ballerina Language Server on: ', LSPort);
            outputChannel.append('Listening for Ballerina Language Server on: ' + LSPort + '\n');
            server.removeListener('error', onLSError);

            const args = ['-cp', getClassPath()]

            if (process.env.LSDEBUG === "true") {
                console.log('LSDEBUG is set to "true". Services will run on debug mode');
                outputChannel.append('LSDEBUG is set to "true". Services will run on debug mode\n');
                args.push('-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005,quiet=y')
            }

            const balHomePath = workspace.getConfiguration('ballerina').get('home');
            const balHomeSysProp = `-Dballerina.home=${balHomePath}`;

            console.log('Starting parser service on: ', parserPort);
            serverProcess = spawn('java', [balHomeSysProp, ...args, main, LSPort, parserPort]);

            serverProcess.on('error', (e) => {
                outputChannel.append(`Could not start services ${e}\n`);
                outputChannel.show();
                onParserError(e);
                onLSError(e);
            });
            
            serverProcess.stdout.on('data', (data) => {
                console.log(`ls: ${data}`);
                if (`${data}`.indexOf('Parser started successfully') > -1) {
                    outputChannel.append('Parser started successfully on port: ' + parserPort + '\n');
                    onParserStarted({port: parserPort});
                }
            });
            serverProcess.stderr.on('data', (data) => {
                console.log(`ls: ${data}`);
            });
        });
    });
}

function getLSService() {
    if (!LSService) {
        startServices();
    }

    return LSService;
}

function getParserService() {
    if (!parserService) {
        startServices();
    }

    return parserService;
}

function setOutputChannel(out) {
    outputChannel = out;
}

module.exports = {
    getLSService,
    getParserService,
    startServices,
    setOutputChannel,
}