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

package ballerina.transactions.coordinator;

import ballerina/log;

const string PROTOCOL_COMPLETION = "completion";
const string PROTOCOL_VOLATILE = "volatile";
const string PROTOCOL_DURABLE = "durable";

enum Protocols {
    COMPLETION, DURABLE, VOLATILE
}

public enum TransactionState {
    ACTIVE, PREPARED, COMMITTED, ABORTED
}

struct TwoPhaseCommitTransaction {
    string transactionId;
    string coordinationType = "2pc";
    map participants;
    Protocol[] coordinatorProtocols;
    TransactionState state;
    boolean possibleMixedOutcome;
}

public struct PrepareRequest {
    string transactionId;
}

public struct PrepareResponse {
    string message;
}

public struct NotifyRequest {
    string transactionId;
    string message;
}

public struct NotifyResponse {
    string message;
}

function twoPhaseCommit (TwoPhaseCommitTransaction txn, int transactionBlockId) returns (string message, error err) {
    log:printInfo("Running 2-phase commit for transaction: " + txn.transactionId);

    string transactionId = txn.transactionId;

    // Prepare local resource managers
    boolean localPrepareSuccessful = prepareResourceManagers(transactionId, transactionBlockId);
    if (!localPrepareSuccessful) {
        err = {message:"Local prepare failed"};
        return;
    }

    // Prepare phase & commit phase
    // First call prepare on all volatile participants
    boolean prepareVolatilesSuccessful = prepareParticipants(txn, PROTOCOL_VOLATILE);
    if (prepareVolatilesSuccessful) {
        // if all volatile participants voted YES, Next call prepare on all durable participants
        boolean prepareDurablesSuccessful = prepareParticipants(txn, PROTOCOL_DURABLE);
        if (prepareDurablesSuccessful) {
            // If all durable participants voted YES (PREPARED or READONLY), next call notify(commit) on all
            // (durable & volatile) participants and return committed to the initiator
            boolean notifyCommitSuccessful = notify(txn, "commit");
            if (notifyCommitSuccessful) {
                message = "committed";
            } else {
                // return Hazard outcome if a participant cannot successfully end its branch of the transaction
                err = {message:"Hazard-Outcome"};
            }
            boolean localCommitSuccessful = commitResourceManagers(transactionId, transactionBlockId);
            if (!localCommitSuccessful) {
                err = {message:"Local commit failed"};
            }
        } else {
            // If some durable participants voted NO, next call notify(abort) on all durable participants
            // and return aborted to the initiator
            boolean notifyAbortSuccessful = notify(txn, "abort");
            if (notifyAbortSuccessful) {
                if (txn.possibleMixedOutcome) {
                    message = "mixed";
                } else {
                    message = "aborted";
                }
            } else {
                // return Hazard outcome if a participant cannot successfully end its branch of the transaction
                err = {message:"Hazard-Outcome"};
            }
            boolean localAbortSuccessful = abortResourceManagers(transactionId, transactionBlockId);
            if (!localAbortSuccessful) {
                err = {message:"Local abort failed"};
            }
        }
    } else {
        boolean notifySuccessful = notifyAbortToVolatileParticipants(txn);
        if (notifySuccessful) {
            if (txn.possibleMixedOutcome) {
                message = "mixed";
            } else {
                message = "aborted";
            }
        } else {
            // return Hazard outcome if a participant cannot successfully end its branch of the transaction
            err = {message:"Hazard-Outcome"};
        }
        boolean localAbortSuccessful = abortResourceManagers(transactionId, transactionBlockId);
        if (!localAbortSuccessful) {
            err = {message:"Local abort failed"};
        }
    }
    return;
}

function notifyAbortToVolatileParticipants (TwoPhaseCommitTransaction txn) returns (boolean successful) {
    map participants = txn.participants;
    foreach _, p in participants {
        var participant, _ = (Participant)p;
        Protocol[] protocols = participant.participantProtocols;
        if (protocols != null) {
            foreach proto in protocols {
                if (proto.name == PROTOCOL_VOLATILE) {
                    var _, e = notifyRemoteParticipant(txn, participant, "abort");
                    if (e != null) {
                        successful = false;
                        return;
                    }
                }
            }
        }
    }
    successful = true;
    return;
}

function notifyAbort (TwoPhaseCommitTransaction txn) returns (string message, error err) {
    map participants = txn.participants;
    message = "aborted";
    foreach _, p in participants {
        var participant, _ = (Participant)p;
        Protocol[] protocols = participant.participantProtocols;
        if (protocols != null) {
            foreach proto in protocols {
                if (proto.protocolFn != null) {
                    // if the participant if a local participant, i.e. protoFn is set, then call that fn
                    log:printInfo("Notify(" + message + ") local participant: " + participant.participantId);
                    boolean successful = proto.protocolFn(txn.transactionId, proto.transactionBlockId, "notifyabort");
                    if (!successful) {
                        err = {message:"Hazard-Outcome"};
                    }
                } else if (proto.url != null) {
                    var status, e = notifyRemoteParticipant(txn, participant, "abort");
                    if (e != null) {
                        err = {message:"Hazard-Outcome"};
                        return;
                    } else if (status == "committed") {
                        txn.possibleMixedOutcome = true;
                        message = "mixed";
                        return;
                    }
                } else {
                    error e = {message:"Both protocol function and URL are null"};
                    throw e;
                }
            }
        }
    }
    return;
}

function prepareParticipants (TwoPhaseCommitTransaction txn, string protocol) returns (boolean successful) {
    successful = true;
    foreach _, p in txn.participants {
        var participant, _ = (Participant)p;
        Protocol[] protocols = participant.participantProtocols;
        if (protocols != null) {
            foreach proto in protocols {
                if (proto.name == protocol) {
                    if (proto.protocolFn != null) {
                        // if the participant if a local participant, i.e. protoFn is set, then call that fn
                        log:printInfo("Preparing local participant: " + participant.participantId);
                        if (!proto.protocolFn(txn.transactionId, proto.transactionBlockId, "prepare")) {
                            successful = false;
                        }
                    } else if (proto.url != null) {
                        if (!prepareRemoteParticipant(txn, participant, proto.url)) {
                            successful = false;
                        }
                    } else {
                        error err = {message:"Both protocol function and URL are null"};
                        throw err;
                    }
                }
            }
        }
    }
    return;
}

function getParticipant2pcClientEP(string participantURL) returns(Participant2pcClientEP) {
    var participantEP, cacheErr = (Participant2pcClientEP)httpClientCache.get(participantURL);
    if (cacheErr != null) {
        throw cacheErr; // We can't continue due to a programmer error
    }
    if (participantEP == null) {
        participantEP = {};
        Participant2pcClientConfig config = {participantURL: participantURL,
                                                endpointTimeout:120000, retryConfig:{count:5, interval:5000}};
        participantEP.init(config);
        httpClientCache.put(participantURL, participantEP);
    }
    return participantEP;
}

function prepareRemoteParticipant (TwoPhaseCommitTransaction txn,
                                   Participant participant, string protocolUrl) returns (boolean successful) {
    endpoint Participant2pcClientEP participantEP;
    participantEP = getParticipant2pcClientEP(protocolUrl);
    
    string transactionId = txn.transactionId;
    // Let's set this to true and change it to false only if a participant aborted or an error occurred while trying
    // to prepare a participant
    successful = true;
    string participantId = participant.participantId;

    log:printInfo("Preparing remote participant: " + participantId);
    // If a participant voted NO or failed then abort
    var status, err = participantEP -> prepare(transactionId);
    if (status == "aborted") {
        log:printInfo("Remote participant: " + participantId + " aborted.");
        // Remove the participant who sent the abort since we don't want to do a notify(Abort) to that
        // participant
        _ = txn.participants.remove(participantId);
        successful = false;
    } else if (status == "committed") {
        log:printInfo("Remote participant: " + participantId + " committed");
        // If one or more participants returns "committed" and the overall prepare fails, we have to
        // report a mixed-outcome to the initiator
        txn.possibleMixedOutcome = true;
        // Don't send notify to this participant because it is has already committed. We can forget about this participant.
        boolean participantRemoved = txn.participants.remove(participantId);
        if(!participantRemoved) {
            log:printError("Could not remove committed participant: " + participantId + " from transaction: " + transactionId);
        }
    } else if (status == "read-only") {
        log:printInfo("Remote participant: " + participantId + " read-only");
        // Don't send notify to this participant because it is read-only. We can forget about this participant.
        boolean participantRemoved = txn.participants.remove(participantId);
        if(!participantRemoved) {
            log:printError("Could not remove read-only participant: " + participantId + " from transaction: " + transactionId);
        }
    } else if (err != null) {
        log:printErrorCause("Remote participant: " + participantId + " failed", err);
        boolean participantRemoved = txn.participants.remove(participantId);
        if(!participantRemoved) {
            log:printError("Could not remove failed participant: " + participantId + " from transaction: " + transactionId);
        }
        successful = false;
    } else if (status == "prepared") {
        log:printInfo("Remote participant: " + participantId + " prepared");
    } else {
        log:printInfo("Remote participant: " + participantId + ", status: " + status);
        successful = false;
    }
    return;
}

function notify (TwoPhaseCommitTransaction txn, string message) returns (boolean successful) {
    foreach _, p in txn.participants {
        var participant, _ = (Participant)p;
        Protocol[] protocols = participant.participantProtocols;
        if (protocols != null) {
            foreach proto in protocols {
                if (proto.protocolFn != null) {
                    // if the participant if a local participant, i.e. protoFn is set, then call that fn
                    log:printInfo("Notify(" + message + ") local participant: " + participant.participantId);
                    successful = proto.protocolFn(txn.transactionId, proto.transactionBlockId, "notify" + message);
                    return;
                } else if (proto.url != null) {
                    var _, e = notifyRemoteParticipant(txn, participant, message);
                    if (e != null) {
                        successful = false;
                        return;
                    }
                } else {
                    error e = {message:"Both protocol function and URL are null"};
                    throw e;
                }
            }
        }
    }
    successful = true;
    return;
}

function notifyRemoteParticipant (TwoPhaseCommitTransaction txn,
                                  Participant participant, string message) returns (string status, error err) {
    endpoint Participant2pcClientEP participantEP;

    string participantId = participant.participantId;
    string transactionId = txn.transactionId;
    log:printInfo("Notify(" + message + ") remote participant: " + participantId);

    foreach protocol in participant.participantProtocols {
        string protoURL = protocol.url;
        participantEP = getParticipant2pcClientEP(protoURL);
        var notificationStatus, participantErr, communicationErr = participantEP -> notify(transactionId, message);
        status = notificationStatus;
        if (communicationErr != null) {
            if (message != "abort") {
                err = communicationErr;
            }
            log:printErrorCause("Communication error occurred while notify(" + message + ") participant: " + protoURL +
                                " for transaction: " + transactionId, communicationErr);
        } else if (participantErr != null) { // participant may return "Transaction-Unknown", "Not-Prepared" or "Failed-EOT"
            log:printErrorCause("Remote participant: " + participantId + " replied with an error", participantErr);
            err = participantErr;
        } else if (notificationStatus == "aborted") {
            log:printInfo("Remote participant: " + participantId + " aborted");
        } else if (notificationStatus == "committed") {
            log:printInfo("Remote participant: " + participantId + " committed");
        }
    }
    return;
}

// This function will be called by the initiator
function commitTransaction (string transactionId, int transactionBlockId) returns (string message, error e) {
    var txn, _ = (TwoPhaseCommitTransaction)initiatedTransactions.get(transactionId);
    if (txn == null) {
        string msg = "Transaction-Unknown. Invalid TID:" + transactionId;
        log:printError(msg);
        e = {message:msg};
    } else {
        log:printInfo("Committing transaction: " + transactionId);
        // return response to the initiator. ( Committed | Aborted | Mixed )
        var msg, err = twoPhaseCommit(txn, transactionBlockId);
        if (err == null) {
            message = msg;
        } else {
            e = err;
        }
    }
    return;
}

// This function will be called by the initiator
function abortInitiatorTransaction (string transactionId, int transactionBlockId) returns (string message, error e) {
    var txn, _ = (TwoPhaseCommitTransaction)initiatedTransactions.get(transactionId);
    if (txn == null) {
        string msg = "Transaction-Unknown. Invalid TID:" + transactionId;
        log:printError(msg);
        e = {message:msg};
    } else {
        log:printInfo("Aborting transaction: " + transactionId);
        // return response to the initiator. ( Aborted | Mixed )
        var msg, err = notifyAbort(txn);
        if (err == null) {
            message = msg;
            txn.state = TransactionState.ABORTED;
        } else {
            e = err;
        }
        boolean localAbortSuccessful = abortResourceManagers(transactionId, transactionBlockId);
        if (!localAbortSuccessful) {
            log:printError("Aborting local resource managers failed");
        }
        initiatedTransactions.remove(transactionId);
    }
    return;
}

documentation {
    The participant should notify the initiator that it aborted. This function is called by the participant.
    The initiator is remote.

    P{{transactionId}} - Transaction ID
    P{{transactionBlockId}} - Transaction block ID. Each transaction block in a process will have a unique ID.
    R{{message}} - The message returned from the initiator which informs the state of the abort request.
    R{{err}} - Error if an error occurred while aborting.
}
function abortLocalParticipantTransaction (string transactionId, int transactionBlockId) returns (string message,
                                                                                                  error err) {
    string participatedTxnId = getParticipatedTransactionId(transactionId, transactionBlockId);
    boolean successful = abortResourceManagers(transactionId, transactionBlockId);
    if (!successful) {
        err = {message:"Aborting local resource managers failed for transaction:" + participatedTxnId};
        return;
    }
    var txn, _ = (TwoPhaseCommitTransaction)participatedTransactions.get(participatedTxnId);
    if (txn == null) {
        string msg = "Transaction-Unknown. Invalid TID:" + transactionId;
        log:printError(msg);
        err = {message:msg};
    } else {
        if (err == null) {
            txn.state = TransactionState.ABORTED;
            log:printInfo("Local participant aborted transaction: " + participatedTxnId);
            //TODO do not remove the transaction since we may get a msg from the initiator
        } else {
            log:printErrorCause("Local participant transaction: " + participatedTxnId + " failed to abort", err);
        }
    }
    return;
}
