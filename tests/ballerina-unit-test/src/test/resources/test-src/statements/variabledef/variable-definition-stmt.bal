function variableDefaultValue () returns (int, boolean, string, float) {
    int i;
    boolean b;
    string s;
    float f;

    return (i, b, s, f);
}

function inlineVarInit () returns (int, boolean, string, float) {
    int i = 10;
    boolean b = true;
    string s = "hello";
    float f = 2.6;

    return (i, b, s, f);
}


function updateDefaultValue (int v1, boolean v3, string v4, float v5) returns (int, boolean, string, float) {
    int i;
    boolean b;
    string s;
    float f;

    i = v1;
    b = v3;
    s = v4;
    f = v5;

    return (i, b, s, f);
}


function updateVarValue (int v1, boolean v3, string v4, float v5) returns (int, boolean, string, float) {
    int i = 10;
    boolean b = true;
    string s = "hello";
    float f = 2.6;

    i = v1;
    b = v3;
    s = v4;
    f = v5;

    return (i, b, s, f);
}
