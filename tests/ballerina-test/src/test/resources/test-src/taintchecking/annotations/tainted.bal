public function main (string[] args) {
    secureFunction(taintedReturn(), taintedReturn());
}

public function secureFunction (@sensitive string secureIn, string insecureIn) {
    string data = secureIn + insecureIn;
}

public function taintedReturn () returns (@tainted string) {
    return "staticValue";
}
