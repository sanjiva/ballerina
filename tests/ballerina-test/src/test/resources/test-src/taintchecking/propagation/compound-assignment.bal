xmlns "http://sample.com/wso2/a1" as ns0;
public function main (string[] args) {
    string x = "static";
    x += "static";
    secureFunction(x, x);

    int x2;
    x2 =? <int>"100";
    x2++;
    secureFunction(x2,x2);
}

public function secureFunction (@sensitive any secureIn, any insecureIn) {

}
