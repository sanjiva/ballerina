import ballerina/io;

function main (string[] args) {
    //The fork-join allows developers to spawn(fork) multiple workers within a ballerina program and join
    //the results from those workers and execute code on joined results.
    fork {
        worker w1 {
            int i = 23;
            string s = "Colombo";
            io:println("[w1] i: " + i + " s: " + s);
            // Reply to the join block from worker w1.
            i, s -> fork;
        }

        worker w2 {
            float f = 10.344;
            io:println("[w2] f: " + f);
            // Reply to the join block from worker w2.
            f -> fork;
        }

    } join (all) (map results) {
        //Here we use "all" as the join condition which means wait for all the workers.
        //When the join condition has been satisfied, results 'map' will be filled with
        //the returned values from the workers.

        // Get values received from worker 'w1'.
        any[] resW1 =? <any[]>results["w1"];
        int iW1 =? <int>resW1[0];
        string sW1 =? <string>resW1[1];
        io:println("[join-block] iW1: " + iW1 + " sW1: " + sW1);
        // Get values received from worker 'w2'.
        any[] resW2 =? <any[]>results["w2"];
        float fW2 =? <float>resW2[0];
        io:println("[join-block] fW2: " + fW2);
    }
}
