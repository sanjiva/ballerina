package main;

import variable;

const int constNegativeInt = -342;

const int constNegativeIntWithSpace = -     88;

const float constNegativeFloat = -88.2;

const float constNegativeFloatWithSpace = -      3343.88;

float glbVarFloat = variable:constFloat;

function accessConstantFromOtherPkg() returns (float) {
    return variable:constFloat;
}

function assignConstFromOtherPkgToGlobalVar() returns (float) {
    return glbVarFloat;
}

function getNegativeConstants() returns (int, int, float, float) {
    return (constNegativeInt, constNegativeIntWithSpace, constNegativeFloat, constNegativeFloatWithSpace);
}


const float a = 4;

function floatIntConversion() returns (float, float, float){
    float[] f = [1,2,3,4,5,6,8];
    return (a, f[5], 10.0);
}

