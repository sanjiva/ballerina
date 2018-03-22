public string globalvariable = "input4";

public function main(string[] args)  {
    secureFunction(untaintWithAddOperatorInReturn(args));
    secureFunction(untaintWithAddOperatorWithVariable(args));
    secureFunction(untaintWithFunctionParam(args));
    secureFunction(untaintWithFunctionReturn(args));
    secureFunction(untaintWithReceiver(args));
}

function untaintWithAddOperatorInReturn (string[] args) returns (string) {
    string data1 = "input1";
    string data2 = "input2";
    return untaint (data1 + data2 + args[0] + "input3" + globalvariable);
}

function untaintWithAddOperatorWithVariable (string[] args) returns (string) {
    string data1 = "input1";
    string data2 = "input2";
    string data3 = data1 + data2 + args[0] + "input3" + globalvariable;
    return untaint data3;
}

function untaintWithFunctionParam (string[] args) returns (string) {
    string data1 = "input1";
    string data2 = "input2";
    return returnInput(untaint (data1 + data2 + args[0] + "input3" + globalvariable));
}

function untaintWithFunctionReturn (string[] args) returns (string) {
    string data1 = "input1";
    string data2 = "input2";
    return untaint returnInput(data1 + data2 + args[0] + "input3" + globalvariable);
}

function untaintWithReceiver (string[] args) returns (string) {
    string data1 = "input1";
    string data2 = "input2";
    return untaint returnInput(data1 + data2 + args[0] + "input3" + globalvariable).trim();
}

function returnInput (string inputData) returns (string) {
    return inputData;
}

function secureFunction (@sensitive string sensitiveInput) {

}
