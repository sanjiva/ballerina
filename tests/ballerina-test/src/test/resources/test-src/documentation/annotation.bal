documentation { Documentation for Test annotation
F{{a}} annotation `field a` documentation
F{{b}} annotation `field b` documentation
F{{c}} annotation `field c` documentation}
struct Tst {
    string a;
    string b;
    string c;
}

documentation { Documentation for Test annotation
}
annotation Test Tst;
