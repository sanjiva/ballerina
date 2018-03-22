
function testForkJoinReturnAnyType() returns (int, string) {
    int p;
    string q;
    string r;
    float t;
    fork {
        worker W1 {
            int x = 23;
            string a = "aaaaa";
            x, a -> fork;
        }
        worker W2 {
            string s = "test";
            float u = 10.23;
            s, u -> fork;
        }
    } join (all) (map results) {
        any[] t1;
        t1 =? <any[]> results["W1"];
        p =? <int> t1[0];
        q =? <string> t1[1];
        t1 =? <any[]> results["W2"];
        r =? <string> t1[0];
        t =? <float> t1[1];
    }
    return (p, q);
}
