xmlns "http://sample.com/wso2/a1" as ns0;
public function main (string[] args) {
    string x = "static";
    x += "static";
    secureFunction(x, x);

    var x1, _ = <xml> "<root xmlns:ns3=\"http://sample.com/wso2/f\"></root>";
    x1@[ns0:foo1] = "bar1";
    x1@[ns0:foo1] += "static";
    secureFunction(x1,x1);

    int x2;
    x2, _ = <int>"100";
    x2++;
    secureFunction(x2,x2);
}

public function secureFunction (@sensitive any secureIn, any insecureIn) {

}
