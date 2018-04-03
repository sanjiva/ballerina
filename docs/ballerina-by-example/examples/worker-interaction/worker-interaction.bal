import ballerina/io;

@Description {value:"Workers interact with each other by sending and receiving messages. Ballerina checks the send/receive signatures of every pair of workers and validate them in order to avoid deadlocks."}
function main (string[] args) {

    worker w1 {
        int i = 100;
        float k = 2.34;
        io:println("[w1 -> w2] i: " + i + " k: " + k);
        // Send messages to worker 'w2'. This message contains two values of type int and float.
        i, k -> w2;
        // Receive a message from worker w2. This message contains a JSON typed value.
        json j = {};
        j <- w2;
        io:println("[w1 <- w2] j: " + j.toString());
    }

    worker w2 {
        // Receive a message from the default worker.
        int iw;
        float kw;
        iw, kw <- w1;
        io:println("[w2 <- w1] iw: " + iw + " kw: " + kw);
        // Send a message to the default worker.
        json jw = {"name":"Ballerina"};
        io:println("[w2 -> w1] jw: " + jw.toString());
        jw -> w1;
    }
}
