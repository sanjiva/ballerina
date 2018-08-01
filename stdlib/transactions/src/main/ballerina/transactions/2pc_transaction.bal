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

import ballerina/io;
import ballerina/log;
import ballerina/time;

type TwoPhaseCommitTransaction object {

    string transactionId;
    int transactionBlockId;
    string coordinationType;
    boolean isInitiated; // Indicates whether this is a transaction that was initiated or is participated in
    map<Participant> participants;
    Protocol[] coordinatorProtocols;
    int createdTime = time:currentTime().time;
    TransactionState state = TXN_STATE_ACTIVE;
    private boolean possibleMixedOutcome;

    new(transactionId, transactionBlockId, coordinationType = "2pc") {

    }

    // This function will be called by the initiator
    function twoPhaseCommit() returns string|error {
        log:printInfo(io:sprintf("Running 2-phase commit for transaction: %s:%d", self.transactionId,
                self.transactionBlockId));
        string|error ret = "";

        // Prepare local resource managers
        boolean localPrepareSuccessful = prepareResourceManagers(self.transactionId, self.transactionBlockId);
        if (!localPrepareSuccessful) {
            ret = {message:"Local prepare failed"};
            return ret;
        }

        // Prepare phase & commit phase
        // First call prepare on all volatile participants
        PrepareDecision prepareVolatilesDecision = self.prepareParticipants(PROTOCOL_VOLATILE);
        if (prepareVolatilesDecision == PREPARE_DECISION_COMMIT) {
            // if all volatile participants voted YES, Next call prepare on all durable participants
            PrepareDecision prepareDurablesDecision = self.prepareParticipants(PROTOCOL_DURABLE);
            if (prepareDurablesDecision == PREPARE_DECISION_COMMIT) {
                // If all durable participants voted YES (PREPARED or READONLY), next call notify(commit) on all
                // (durable & volatile) participants and return committed to the initiator
                var result = self.notifyParticipants(COMMAND_COMMIT, ());
                match result {
                    error => {
                        // return Hazard outcome if a participant cannot successfully end its branch of the transaction
                        ret = {message:OUTCOME_HAZARD};
                    }
                    NotifyResult => {
                        boolean localCommitSuccessful =
                            commitResourceManagers(self.transactionId, self.transactionBlockId);
                        if (!localCommitSuccessful) {
                            ret = {message:OUTCOME_HAZARD}; // "Local commit failed"
                        } else {
                            ret = OUTCOME_COMMITTED;
                        }
                    }
                }
            } else {
                // If some durable participants voted NO, next call notify(abort) on all participants
                // and return aborted to the initiator
                var result = self.notifyParticipants(COMMAND_ABORT, ());
                match result {
                    error => {
                        // return Hazard outcome if a participant cannot successfully end its branch of the transaction
                        ret = {message:OUTCOME_HAZARD};
                    }
                    NotifyResult => {
                        boolean localAbortSuccessful =
                            abortResourceManagers(self.transactionId, self.transactionBlockId);
                        if (!localAbortSuccessful) {
                            ret = {message:OUTCOME_HAZARD};
                        } else {
                            if (self.possibleMixedOutcome) {
                                ret = OUTCOME_MIXED;
                            } else {
                                ret = OUTCOME_ABORTED;
                            }
                        }
                    }
                }
            }
        } else {
            // If some volatile participants voted NO, next call notify(abort) on all volatile articipants
            // and return aborted to the initiator
            var result = self.notifyParticipants(COMMAND_ABORT, PROTOCOL_VOLATILE);
            match result {
                error => {
                    // return Hazard outcome if a participant cannot successfully end its branch of the transaction
                    ret = {message:OUTCOME_HAZARD};
                }
                NotifyResult => {
                    boolean localAbortSuccessful = abortResourceManagers(self.transactionId, self.transactionBlockId);
                    if (!localAbortSuccessful) {
                        ret = {message:OUTCOME_HAZARD};
                    } else {
                        if (self.possibleMixedOutcome) {
                            ret = OUTCOME_MIXED;
                        } else {
                            ret = OUTCOME_ABORTED;
                        }
                    }
                }
            }
        }
        return ret;
    }

    documentation {
        When an abort statement is executed, this function gets called. The transaction in concern will be marked for
        abortion.
    }
    function markForAbortion() returns error? {
        if (self.isInitiated) {
            self.state = TXN_STATE_ABORTED;
            log:printInfo("Marked initiated transaction for abortion");
        } else { // participant
            boolean successful = abortResourceManagers(self.transactionId, self.transactionBlockId);
            string participatedTxnId = getParticipatedTransactionId(self.transactionId, self.transactionBlockId);
            if (successful) {
                self.state = TXN_STATE_ABORTED;
                log:printInfo("Marked participated transaction for abort. Transaction:" + participatedTxnId);
            } else {
                string msg = "Aborting local resource managers failed for participated transaction:" +
                    participatedTxnId;
                log:printError(msg);
                error err = {message:msg};
                return err;
            }
        }
        return ();
    }

    // The result of this function is whether we can commit or abort
    function prepareParticipants(string protocol) returns PrepareDecision {
        PrepareDecision prepareDecision = PREPARE_DECISION_COMMIT;
        future<((PrepareResult|error)?, Participant)>[] results;
        foreach _, participant in self.participants {
            string participantId = participant.participantId;
            future<((PrepareResult|error)?, Participant)> f = start participant.prepare(protocol);
            results[lengthof results] = f;
        }
        foreach f in results {
            ((PrepareResult|error)?, Participant) r = await f;
            var (result, participant) = r;
            string participantId = participant.participantId;
            match result {
                PrepareResult prepRes => {
                    if (prepRes == PREPARE_RESULT_PREPARED) {
                        // All set for a PREPARE_DECISION_COMMIT so we can proceed without doing anything
                    } else if (prepRes == PREPARE_RESULT_COMMITTED) {
                        // If one or more participants returns "committed" and the overall prepare fails, we have to
                        // report a mixed-outcome to the initiator
                        self.possibleMixedOutcome = true;
                        // Don't send notify to this participant because it is has already committed.
                        // We can forget about this participant.
                        self.removeParticipant(participantId,
                                "Could not remove committed participant: " + participantId + " from transaction: " +
                                self.transactionId);
                        // All set for a PREPARE_DECISION_COMMIT so we can proceed without doing anything
                    } else if (prepRes == PREPARE_RESULT_READ_ONLY) {
                        // Don't send notify to this participant because it is read-only.
                        // We can forget about this participant.
                        self.removeParticipant(participantId,
                                "Could not remove read-only participant: " + participantId + " from transaction: " +
                                self.transactionId);
                        // All set for a PREPARE_DECISION_COMMIT so we can proceed without doing anything
                    } else if (prepRes == PREPARE_RESULT_ABORTED) {
                        // Remove the participant who sent the abort since we don't want to do a notify(Abort) to that
                        // participant
                        self.removeParticipant(participantId, "Could not remove aborted participant: " + participantId +
                                " from transaction: " + transactionId);
                        prepareDecision = PREPARE_DECISION_ABORT;
                    }
                }
                () => {}
                error err => {
                    self.removeParticipant(participantId,
                            "Could not remove prepare failed participant: " + participantId + " from transaction: " +
                            self.transactionId);
                    prepareDecision = PREPARE_DECISION_ABORT;
                }
            }
        }
        return prepareDecision;
    }

    function notifyParticipants(string action, string? protocolName) returns NotifyResult|error {
        NotifyResult|error notifyResult = (action == COMMAND_COMMIT) ? NOTIFY_RESULT_COMMITTED : NOTIFY_RESULT_ABORTED;
        future<(NotifyResult|error)?>[] results;
        foreach _, participant in self.participants {
            future<(NotifyResult|error)?> f = start participant.notify(action, protocolName);
            results[lengthof results] = f;

        }
        foreach f in results {
            (NotifyResult|error)? result = await f;
            match result {
                NotifyResult? => {}
                error err => notifyResult = err;
            }
        }
        return notifyResult;
    }

    // This function will be called by the initiator
    function abortInitiatorTransaction() returns string|error {
        log:printInfo(io:sprintf("Aborting initiated transaction: %s:%d", self.transactionId, self.transactionBlockId));
        string|error ret = "";
        // return response to the initiator. ( Aborted | Mixed )
        var result = self.notifyParticipants(COMMAND_ABORT, ());
        match result {
            error => {
                // return Hazard outcome if a participant cannot successfully end its branch of the transaction
                ret = {message:OUTCOME_HAZARD};
            }
            NotifyResult => {
                boolean localAbortSuccessful = abortResourceManagers(self.transactionId, self.transactionBlockId);
                if (!localAbortSuccessful) {
                    ret = {message:OUTCOME_HAZARD};
                } else {
                    if (self.possibleMixedOutcome) {
                        ret = OUTCOME_MIXED;
                    } else {
                        ret = OUTCOME_ABORTED;
                    }
                }
            }
        }
        return ret;
    }

    documentation {
        The participant should notify the initiator that it aborted. This function is called by the participant.
        The initiator is remote.
    }
    function abortLocalParticipantTransaction() returns string|error {
        string|error ret = "";
        boolean successful = abortResourceManagers(self.transactionId, self.transactionBlockId);
        string participatedTxnId = getParticipatedTransactionId(self.transactionId, self.transactionBlockId);
        if (successful) {
            self.state = TXN_STATE_ABORTED;
            log:printInfo("Local participant aborted transaction: " + participatedTxnId);
        } else {
            string msg = "Aborting local resource managers failed for transaction:" + participatedTxnId;
            log:printError(msg);
            ret = {message:msg};
        }
        return ret;
    }

    function removeParticipant(string participantId, string failedMessage) {
        boolean participantRemoved = self.participants.remove(participantId);
        if (!participantRemoved) {
            log:printError(failedMessage);
        }
    }
};
