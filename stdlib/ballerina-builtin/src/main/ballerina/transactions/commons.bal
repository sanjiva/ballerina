// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package ballerina.transactions;

import ballerina/caching;
import ballerina/log;
import ballerina/http;
import ballerina/task;
import ballerina/time;
import ballerina/util;

documentation {
    ID of the local participant used when registering with the initiator.
}
string localParticipantId = util:uuid();

documentation {
    This map is used for caching transaction that are initiated.
}
map<TwoPhaseCommitTransaction> initiatedTransactions;

documentation {
    This map is used for caching transaction that are this Ballerina instance participates in.
}
map<TwoPhaseCommitTransaction> participatedTransactions;

documentation {
    This cache is used for caching HTTP connectors against the URL, since creating connectors is expensive.
}
caching:Cache httpClientCache = caching:createCache("ballerina.http.client.cache", 900000, 100, 0.1);

@final boolean scheduleInit = scheduleTimer(1000, 60000);

function scheduleTimer (int delay, int interval) returns boolean {
    (function() returns error?) onTriggerFunction = cleanupTransactions;
    _ = task:scheduleTimer(onTriggerFunction, (), {delay:delay, interval:interval});
    return true;
}

function cleanupTransactions() returns error? {
    worker w1 {
        foreach _, twopcTxn in participatedTransactions {
            string participatedTxnId = getParticipatedTransactionId(twopcTxn.transactionId, twopcTxn.transactionBlockId);
            if (time:currentTime().time - twopcTxn.createdTime >= 120000) {
                if (twopcTxn.state != TXN_STATE_ABORTED && twopcTxn.state != TXN_STATE_COMMITTED) {
                    if (twopcTxn.state != TXN_STATE_PREPARED) {
                        boolean prepareSuccessful = prepareResourceManagers(twopcTxn.transactionId, twopcTxn.transactionBlockId);
                        if (prepareSuccessful) {
                            twopcTxn.state = TXN_STATE_PREPARED;
                            log:printInfo("Auto-prepared participated  transaction: " + participatedTxnId);
                        } else {
                            log:printError("Auto-prepare of participated transaction: " + participatedTxnId + " failed");
                        }
                    }
                    if (twopcTxn.state == TXN_STATE_PREPARED) {
                        boolean commitSuccessful = commitResourceManagers(twopcTxn.transactionId, twopcTxn.transactionBlockId);
                        if (commitSuccessful) {
                            twopcTxn.state = TXN_STATE_COMMITTED;
                            log:printInfo("Auto-committed participated  transaction: " + participatedTxnId);
                        } else {
                            log:printError("Auto-commit of participated transaction: " + participatedTxnId + " failed");
                        }
                    }
                }
            }
            if (time:currentTime().time - twopcTxn.createdTime >= 600000) {
                // We don't want dead transactions hanging around
                removeParticipatedTransaction(participatedTxnId);
            }
        }
    }
    worker w2 {
        foreach _, twopcTxn in initiatedTransactions {
            if (time:currentTime().time - twopcTxn.createdTime >= 120000) {
                if (twopcTxn.state != TXN_STATE_ABORTED) {
                    // Commit the transaction since prepare hasn't been received
                    var result = twopcTxn.twoPhaseCommit();
                    match result {
                        string str => log:printInfo("Auto-committed initiated transaction: " +
                                                    twopcTxn.transactionId + ". Result: " + str);
                        error err => log:printErrorCause("Auto-commit of participated transaction: " +
                                                         twopcTxn.transactionId + " failed", err);
                    }
                }
            }
            if (time:currentTime().time - twopcTxn.createdTime >= 600000) {
                // We don't want dead transactions hanging around
                removeInitiatedTransaction(twopcTxn.transactionId);
            }
        }
        return ();
    }
}


function isRegisteredParticipant (string participantId, map<Participant> participants) returns boolean {
    return participants.hasKey(participantId);
}

function isValidCoordinationType (string coordinationType) returns boolean {
    foreach coordType in coordinationTypes {
        if (coordinationType == coordType) {
            return true;
        }
    }
    return false;
}

function protocolCompatible (string coordinationType,
                             Protocol[] participantProtocols) returns boolean {
    boolean participantProtocolIsValid = false;
    string[] validProtocols = coordinationTypeToProtocolsMap[coordinationType];
    foreach participantProtocol in participantProtocols {
        foreach validProtocol in validProtocols {
            if (participantProtocol.name == validProtocol) {
                participantProtocolIsValid = true;
                break;
            } else {
                participantProtocolIsValid = false;
            }
        }
        if (!participantProtocolIsValid) {
            break;
        }
    }
    return participantProtocolIsValid;
}

function respondToBadRequest (http:ServiceEndpoint conn, string msg) {
    endpoint http:ServiceEndpoint ep = conn;
    log:printError(msg);
    http:Response res = {statusCode:http:BAD_REQUEST_400};
    RequestError err = {errorMessage:msg};
    json resPayload = check <json>err;
    res.setJsonPayload(resPayload);
    var resResult = ep -> respond(res);
    match resResult {
        http:HttpConnectorError respondErr => {
            log:printErrorCause("Could not send Bad Request error response to caller", respondErr);
        }
        () => return;
    }
}

function createNewTransaction (string coordinationType, int transactionBlockId) returns TwoPhaseCommitTransaction {
    if (coordinationType == TWO_PHASE_COMMIT) {
        TwoPhaseCommitTransaction twopcTxn = new (util:uuid(), transactionBlockId, coordinationType = coordinationType);
        return twopcTxn;
    } else {
        error e = {message:"Unknown coordination type: " + coordinationType};
        throw e;
    }
}

function getCoordinatorProtocolAt (ProtocolName protocolName, int transactionBlockId) returns string {
    //TODO: protocolName is unused for the moment
    return "http://" + coordinatorHost + ":" + coordinatorPort + initiator2pcCoordinatorBasePath + "/" +
           transactionBlockId;
}

function getParticipantProtocolAt (ProtocolName protocolName, int transactionBlockId) returns string {
    //TODO: protocolName is unused for the moment
    return "http://" + coordinatorHost + ":" + coordinatorPort + participant2pcCoordinatorBasePath + "/" +
           transactionBlockId;
}

// The initiator will create a new transaction context by calling this function
function createTransactionContext (string coordinationType,
                                   int transactionBlockId) returns TransactionContext|error {
    if (!isValidCoordinationType(coordinationType)) {
        string msg = "Invalid-Coordination-Type:" + coordinationType;
        log:printError(msg);
        error err = {message:msg};
        return err;
    } else {
        TwoPhaseCommitTransaction txn = createNewTransaction(coordinationType, transactionBlockId);
        string txnId = txn.transactionId;
        txn.isInitiated = true;
        initiatedTransactions[txnId] = txn;
        TransactionContext txnContext = {transactionId:txnId,
                                            transactionBlockId:transactionBlockId,
                                            coordinationType:coordinationType,
                                            registerAtURL:"http://" + coordinatorHost + ":" + coordinatorPort +
                                                          initiatorCoordinatorBasePath + "/" + transactionBlockId +
                                                          registrationPath};
        log:printInfo("Created transaction: " + txnId);
        return txnContext;
    }
}

function registerParticipantWithLocalInitiator (string transactionId,
                                                int transactionBlockId,
                                                string registerAtURL) returns TransactionContext|error {
    string participantId = getParticipantId(transactionBlockId);
    //TODO: Protocol name should be passed down from the transaction statement
    Protocol participantProtocol = {name:PROTOCOL_DURABLE, transactionBlockId:transactionBlockId,
                                       protocolFn:localParticipantProtocolFn};
    if (!initiatedTransactions.hasKey(transactionId)) {
        error err = {message:"Transaction-Unknown. Invalid TID:" + transactionId};
        return err;
    } else {
        TwoPhaseCommitTransaction txn = initiatedTransactions[transactionId];
        if (isRegisteredParticipant(participantId, txn.participants)) { // Already-Registered
            error err = {message:"Already-Registered. TID:" + transactionId + ",participant ID:" + participantId};
            return err;
        } else if (!protocolCompatible(txn.coordinationType, [participantProtocol])) { // Invalid-Protocol
            error err = {message:"Invalid-Protocol. TID:" + transactionId + ",participant ID:" + participantId};
            return err;
        } else {
            Participant participant = {participantId:participantId};
            participant.participantProtocols = [participantProtocol];
            txn.participants[participantId] = participant;

            //Set initiator protocols
            TwoPhaseCommitTransaction twopcTxn = new (transactionId, transactionBlockId);
            Protocol initiatorProto = {name: PROTOCOL_DURABLE, transactionBlockId:transactionBlockId};
            twopcTxn.coordinatorProtocols = [initiatorProto];

            string participatedTxnId = getParticipatedTransactionId(transactionId, transactionBlockId);
            participatedTransactions[participatedTxnId] = twopcTxn;
            TransactionContext txnCtx = {transactionId:transactionId, transactionBlockId:transactionBlockId,
                                            coordinationType: TWO_PHASE_COMMIT, registerAtURL:registerAtURL};
            log:printInfo("Registered local participant: " + participantId + " for transaction:" + transactionId);
            return txnCtx;
        }
    }
}

function localParticipantProtocolFn (string transactionId,
                                     int transactionBlockId,
                                     string protocolAction) returns boolean {
    string participatedTxnId = getParticipatedTransactionId(transactionId, transactionBlockId);
    if (!participatedTransactions.hasKey(participatedTxnId)) {
        return false;
    }
    TwoPhaseCommitTransaction txn = participatedTransactions[participatedTxnId];
    if (protocolAction == COMMAND_PREPARE) {
        if (txn.state == TXN_STATE_ABORTED) {
            removeParticipatedTransaction(participatedTxnId);
            return false;
        } else if (txn.state == TXN_STATE_COMMITTED) {
            removeParticipatedTransaction(participatedTxnId);
            txn.possibleMixedOutcome = true;
            return true;
        } else {
            boolean successful = prepareResourceManagers(transactionId, transactionBlockId);
            if (successful) {
                txn.state = TXN_STATE_PREPARED;
            }
            return successful;
        }
    } else if (protocolAction == COMMAND_COMMIT) {
        if (txn.state == TXN_STATE_PREPARED) {
            boolean successful = commitResourceManagers(transactionId, transactionBlockId);
            removeParticipatedTransaction(participatedTxnId);
            return successful;
        }
    } else if (protocolAction == COMMAND_ABORT) {
        boolean successful = abortResourceManagers(transactionId, transactionBlockId);
        removeParticipatedTransaction(participatedTxnId);
        return successful;
    } else {
        error err = {message:"Invalid protocol action:" + protocolAction};
        throw err;
    }
    return false;
}

function removeParticipatedTransaction (string participatedTxnId) {
    boolean removed = participatedTransactions.remove(participatedTxnId);
    if (!removed) {
        error err = {message:"Removing participated transaction: " + participatedTxnId + " failed"};
        throw err;
    }
}

function removeInitiatedTransaction (string transactionId) {
    boolean removed = initiatedTransactions.remove(transactionId);
    if (!removed) {
        error err = {message:"Removing initiated transaction: " + transactionId + " failed"};
        throw err;
    }
}

function getInitiatorClientEP (string registerAtURL) returns InitiatorClientEP {
    if (httpClientCache.hasKey(registerAtURL)) {
        InitiatorClientEP initiatorEP = check <InitiatorClientEP>httpClientCache.get(registerAtURL);
        return initiatorEP;
    } else {
        InitiatorClientEP initiatorEP = new;
        InitiatorClientConfig config = {registerAtURL:registerAtURL,
                                           endpointTimeout:120000, retryConfig:{count:5, interval:5000}};
        initiatorEP.init(config);
        httpClientCache.put(registerAtURL, initiatorEP);
        return initiatorEP;
    }
}

function getParticipant2pcClientEP (string participantURL) returns Participant2pcClientEP {
    if (httpClientCache.hasKey(participantURL)) {
        Participant2pcClientEP participantEP = check <Participant2pcClientEP>httpClientCache.get(participantURL);
        return participantEP;
    } else {
        Participant2pcClientEP participantEP = {};
        Participant2pcClientConfig config = {participantURL:participantURL,
                                                endpointTimeout:120000, retryConfig:{count:5, interval:5000}};
        participantEP.init(config);
        httpClientCache.put(participantURL, participantEP);
        return participantEP;
    }
}

documentation {
    Registers a participant with the initiator's coordinator. This function will be called by the participant

    P{{transactionId}} - ID of the transaction to which this participant is registering with
    P{{transactionBlockId}} - The local ID of the transaction block on the participant
    P{{registerAtURL}} - The URL of the initiator to which this participant will register with
}
public function registerParticipantWithRemoteInitiator (string transactionId,
                                                        int transactionBlockId,
                                                        string registerAtURL,
                                                        Protocol[] participantProtocols) returns TransactionContext|error {
    endpoint InitiatorClientEP initiatorEP;
    initiatorEP = getInitiatorClientEP(registerAtURL);
    string participatedTxnId = getParticipatedTransactionId(transactionId, transactionBlockId);

    // Register with the coordinator only if the participant has not already done so
    if (participatedTransactions.hasKey(participatedTxnId)) {
        string msg = "Already registered with initiator for transaction:" + participatedTxnId;
        log:printError(msg);
        error err = {message:msg};
        return err;
    }
    log:printInfo("Registering for transaction: " + participatedTxnId + " with coordinator: " + registerAtURL);

    var result = initiatorEP -> register(transactionId, transactionBlockId, participantProtocols);
    match result {
        error e => {
            string msg = "Cannot register with coordinator for transaction: " + transactionId;
            log:printErrorCause(msg, e);
            error err = {message:msg, cause:[e]};
            return err;
        }
        RegistrationResponse regRes => {
            Protocol[] coordinatorProtocols = regRes.coordinatorProtocols;
            TwoPhaseCommitTransaction twopcTxn = new (transactionId, transactionBlockId);
            twopcTxn.coordinatorProtocols = coordinatorProtocols;
            participatedTransactions[participatedTxnId] = twopcTxn;
            TransactionContext txnCtx = {transactionId:transactionId, transactionBlockId:transactionBlockId,
                                            coordinationType: TWO_PHASE_COMMIT, registerAtURL:registerAtURL};
            log:printInfo("Registered with coordinator for transaction: " + transactionId);
            return txnCtx;
        }
    }
}

function getParticipatedTransactionId (string transactionId, int transactionBlockId) returns string {
    string id = transactionId + ":" + transactionBlockId;
    return id;
}

function getParticipantId (int transactionBlockId) returns string {
    string participantId = localParticipantId + ":" + transactionBlockId;
    return participantId;
}

native function getAvailablePort () returns int;

native function getHostAddress () returns string;
