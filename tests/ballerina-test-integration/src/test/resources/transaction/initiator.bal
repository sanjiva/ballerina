// Copyright (c) 2017 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/http;
import ballerina/io;

endpoint http:ServiceEndpoint initiatorEP {
    port:8888
};

endpoint http:ClientEndpoint participant1EP {
    targets:[{url: "http://localhost:8889"}]
};

State state = new();

@http:ServiceConfig {
    basePath:"/"
}
service<http:Service> InitiatorService bind initiatorEP {

    getState(endpoint ep, http:Request req) {
        http:Response res = {};
        res.setStringPayload(state.toString());
        state.reset();
        _ = ep -> respond(res);
    }

    testInitiatorAbort(endpoint ep, http:Request req) {


        transaction with oncommit=onCommit, onabort=onAbort {
            http:Request newReq = {};
            _ = participant1EP -> get("/noOp", {});

            transaction with oncommit=onLocalParticipantCommit, onabort=onLocalParticipantAbort { // local participant
            }

            state.abortedByInitiator = true;
            abort;
        }

        http:Response res = {statusCode: 200};
        _ = ep -> respond(res);
    }

    testRemoteParticipantAbort(endpoint ep, http:Request req) {


        transaction with oncommit=onCommit, onabort=onAbort {
            http:Request newReq = {};
            _ = participant1EP -> get("/testRemoteParticipantAbort", {});

            transaction with oncommit=onLocalParticipantCommit, onabort=onLocalParticipantAbort { // local participant
            }
        }

        http:Response res = {statusCode: 200};
        _ = ep -> respond(res);
    }

    testLocalParticipantAbort(endpoint ep, http:Request req) {


        transaction with oncommit=onCommit, onabort=onAbort {
            http:Request newReq = {};
            _ = participant1EP -> get("/noOp", {});

            transaction with oncommit=onLocalParticipantCommit, onabort=onLocalParticipantAbort { // local participant
                state.abortedByLocalParticipant = true;
                abort;
            }
        }

        http:Response res = {statusCode: 200};
        _ = ep -> respond(res);
    }

    testLocalParticipantSuccess(endpoint ep, http:Request req) {


        transaction with oncommit=onCommit, onabort=onAbort {
            http:Request newReq = {};
            _ = participant1EP -> get("/noOp", {});

            transaction with oncommit=onLocalParticipantCommit, onabort=onLocalParticipantAbort { // local participant
            }
        }

        http:Response res = {statusCode: 200};
        _ = ep -> respond(res);
    }

    @http:ResourceConfig {
        transactionInfectable: false
    }
    testTransactionInfectableFalse (endpoint ep, http:Request req) {

        http:Response res = {statusCode: 500};
        transaction with oncommit=onCommit, onabort=onAbort {
            http:Request newReq = {};
            var result = participant1EP -> get("/nonInfectable", {});
            match result {
                http:Response participant1Res => {
                    transaction with oncommit=onLocalParticipantCommit, onabort=onLocalParticipantAbort { // local participant
                    }
                    res = participant1Res;
                    if(participant1Res.statusCode == 500) {
                        state.abortedByInitiator = true;
                        abort;
                    }
                }
                error => {
                    res.statusCode = 500;
                }
            }
        }
        _ = ep -> respond(res);
    }

    @http:ResourceConfig {
        transactionInfectable: true
    }
    testTransactionInfectableTrue (endpoint ep, http:Request req) {

        transaction with oncommit=onCommit, onabort=onAbort {
            http:Request newReq = {};
            _ = participant1EP -> get("/infectable", {});

            transaction with oncommit=onLocalParticipantCommit, onabort=onLocalParticipantAbort { // local participant
            }
        }
        http:Response res = {statusCode: 200};

        _ = ep -> respond(res);
    }

    @http:ResourceConfig {
        path:"/"
    }
    member (endpoint conn, http:Request req) {

        transaction {
            http:Request newReq = {};
            var getResult = participant1EP -> get("/", newReq);
            match getResult {
                http:HttpConnectorError err => {
                    io:print("Initiator could not send get request to participant. Error:");
                    sendErrorResponseToCaller(conn);
                    abort;
                }
                http:Response participant1Res => {
                    var fwdResult = conn -> forward(participant1Res); 
                    match fwdResult {
                        http:HttpConnectorError err => {
                            io:print("Initiator could not forward response from participant 1 to originating client. Error:");
                            io:print(err);
                        }
                        null => io:print("");
                    }
                }
            }
        } onretry {
            io:println("Intiator failed");
        }
    }

    testSaveToDatabaseSuccessfulInParticipant(endpoint ep, http:Request req) {
        http:Response res = {statusCode: 500};
        transaction with oncommit=onCommit, onabort=onAbort {
            http:Request newReq = {};
            var result = participant1EP -> get("/testSaveToDatabaseSuccessfulInParticipant", {});
            match result {
                http:Response participant1Res => {
                    transaction with oncommit=onLocalParticipantCommit, onabort=onLocalParticipantAbort { // local participant
                    }
                    res = participant1Res;
                    if(participant1Res.statusCode == 500) {
                        state.abortedByInitiator = true;
                        abort;
                    }
                }
                error => {
                    res.statusCode = 500;
                }
            }
        }
        _ = ep -> respond(res);
    }

    testSaveToDatabaseFailedInParticipant(endpoint ep, http:Request req) {
        http:Response res = {statusCode: 500};
        transaction with oncommit=onCommit, onabort=onAbort {
            http:Request newReq = {};
            var result = participant1EP -> get("/testSaveToDatabaseFailedInParticipant", {});
            match result {
                http:Response participant1Res => {
                    transaction with oncommit=onLocalParticipantCommit, onabort=onLocalParticipantAbort { // local participant
                    }
                    res = participant1Res;
                    if(participant1Res.statusCode == 500) {
                        state.abortedByInitiator = true;
                        abort;
                    }
                }
                error => {
                    res.statusCode = 500;
                }
            }
        }
        _ = ep -> respond(res);
    }
}

function sendErrorResponseToCaller(http:ServiceEndpoint conn) {
    endpoint http:ServiceEndpoint conn2 = conn;
    http:Response errRes = {statusCode: 500};
    var respondResult = conn2 -> respond(errRes);
    match respondResult {
        http:HttpConnectorError respondErr => {
            io:print("Initiator could not send error response to originating client. Error:");
            io:println(respondErr);
        }
        null => return;
    }
}

function onAbort() {
    state.abortedFunctionCalled = true;
}

function onCommit() {
    state.committedFunctionCalled = true;
}

function onLocalParticipantAbort() {
    state.localParticipantAbortedFunctionCalled = true;
}

function onLocalParticipantCommit() {
    state.localParticipantCommittedFunctionCalled = true;
}

type State object {
    private {
        boolean abortedByInitiator;
        boolean abortedByLocalParticipant;
        boolean abortedFunctionCalled;
        boolean committedFunctionCalled;
        boolean localParticipantAbortedFunctionCalled;
        boolean localParticipantCommittedFunctionCalled;
    }

    function reset() {
        abortedByInitiator = false;
        abortedByLocalParticipant = false;
        abortedFunctionCalled = false;
        committedFunctionCalled = false;
        localParticipantAbortedFunctionCalled = false;
        localParticipantCommittedFunctionCalled = false;
    }

    function toString() returns string {
        return io:sprintf("abortedByInitiator=%b,abortedByLocalParticipant=%b,abortedFunctionCalled=%b," +
                            "committedFunctionCalled=%s,localParticipantCommittedFunctionCalled=%s," +
                            "localParticipantAbortedFunctionCalled=%s",
                            [abortedByInitiator, abortedByLocalParticipant, abortedFunctionCalled,
                            committedFunctionCalled, localParticipantCommittedFunctionCalled,
                            localParticipantAbortedFunctionCalled]);
    }
}
