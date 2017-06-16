/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.ballerinalang.util.codegen;

/**
 * Bytecode instructions of a Ballerina compiled program.
 *
 * @since 0.87
 */
public interface InstructionCodes {

    int NOP = 0;
    int ICONST = 2;
    int FCONST = 3;
    int SCONST = 4;
    int ICONST_0 = 5;
    int ICONST_1 = 6;
    int ICONST_2 = 7;
    int ICONST_3 = 8;
    int ICONST_4 = 9;
    int ICONST_5 = 10;
    int FCONST_0 = 11;
    int FCONST_1 = 12;
    int FCONST_2 = 13;
    int FCONST_3 = 14;
    int FCONST_4 = 15;
    int FCONST_5 = 16;
    int BCONST_0 = 17;
    int BCONST_1 = 18;
    int RCONST_NULL = 19;

    int ILOAD = 22;
    int FLOAD = 23;
    int SLOAD = 24;
    int BLOAD = 25;
    int LLOAD = 26;
    int RLOAD = 27;
    int IALOAD = 28;
    int FALOAD = 29;
    int SALOAD = 30;
    int BALOAD = 31;
    int LALOAD = 32;
    int RALOAD = 33;
    int JSONALOAD = 34;
    int IGLOAD = 35;
    int FGLOAD = 36;
    int SGLOAD = 37;
    int BGLOAD = 38;
    int LGLOAD = 39;
    int RGLOAD = 40;

    int ISTORE = 41;
    int FSTORE = 42;
    int SSTORE = 43;
    int BSTORE = 44;
    int LSTORE = 45;
    int RSTORE = 46;
    int IASTORE = 47;
    int FASTORE = 48;
    int SASTORE = 49;
    int BASTORE = 50;
    int LASTORE = 51;
    int RASTORE = 52;
    int JSONASTORE = 53;
    int IGSTORE = 54;
    int FGSTORE = 55;
    int SGSTORE = 56;
    int BGSTORE = 57;
    int LGSTORE = 58;
    int RGSTORE = 59;

    int IFIELDLOAD = 60;
    int FFIELDLOAD = 61;
    int SFIELDLOAD = 62;
    int BFIELDLOAD = 63;
    int LFIELDLOAD = 64;
    int RFIELDLOAD = 65;

    int IFIELDSTORE = 66;
    int FFIELDSTORE = 67;
    int SFIELDSTORE = 68;
    int BFIELDSTORE = 69;
    int LFIELDSTORE = 70;
    int RFIELDSTORE = 71;

    // mapload map_reg_index key value_reg_index
    int MAPLOAD = 72;
    // mapstore map_reg_index key value_reg_index
    int MAPSTORE = 73;

    // jsonload json_reg_index key json_value_reg_index
    int JSONLOAD = 74;
    int JSONSTORE = 75;

    int IADD = 76;
    int FADD = 77;
    int SADD = 78;
    int XMLADD = 81;
    int ISUB = 82;
    int FSUB = 83;
    int IMUL = 84;
    int FMUL = 85;
    int IDIV = 86;
    int FDIV = 87;
    int IMOD = 88;
    int FMOD = 89;
    int INEG = 90;
    int FNEG = 91;
    int BNOT = 92;

    int IEQ = 93;
    int FEQ = 94;
    int SEQ = 95;
    int BEQ = 96;
    int REQ = 98;

    int INE = 99;
    int FNE = 100;
    int SNE = 101;
    int BNE = 102;
    int RNE = 104;

    int IGT = 105;
    int FGT = 106;

    int IGE = 107;
    int FGE = 108;

    int ILT = 109;
    int FLT = 110;

    int ILE = 111;
    int FLE = 112;

    int REQ_NULL = 113;
    int RNE_NULL = 114;

    int BR_TRUE = 115;
    int BR_FALSE = 116;

    int GOTO = 117;
    int HALT = 118;
    int CALL = 120;
    int NCALL = 121;
    int ACALL = 122;
    int NACALL = 123;
    int THROW = 124;
    int ERRSTORE = 125;

    // Type Conversion related instructions
    int I2F = 130;
    int I2S = 131;
    int I2B = 132;
    int I2JSON = 133;
    int F2I = 134;
    int F2S = 135;
    int F2B = 136;
    int F2JSON = 137;
    int S2I = 138;
    int S2F = 139;
    int S2B = 140;
    int S2JSON = 141;
    int B2I = 142;
    int B2F = 143;
    int B2S = 144;
    int B2JSON = 145;
    int JSON2I = 146;
    int JSON2F = 147;
    int JSON2S = 148;
    int JSON2B = 149;
    int DT2JSON = 150;
    int DT2XML = 151;
    int T2MAP = 152;
    int T2JSON = 153;
    int MAP2T = 154;
    int JSON2T = 155;
    int XML2JSON = 156;
    int JSON2XML = 157;

    // Type cast
    int I2ANY = 160;
    int F2ANY = 161;
    int S2ANY = 162;
    int B2ANY = 163;
    int L2ANY = 164;
    int ANY2I = 165;
    int ANY2F = 166;
    int ANY2S = 167;
    int ANY2B = 168;
    int ANY2L = 169;
    int ANY2JSON = 170;
    int ANY2XML = 171;
    int ANY2MAP = 172;
    int ANY2MSG = 173;
    int ANY2DT = 174;

    int ANY2T = 178;
    int ANY2C = 179;
    int CHECKCAST = 180;
    int NULL2JSON = 181;


    // Transactions
    int TRBGN = 188;
    int TREND = 189;

    int WRKINVOKE = 190;
    int WRKREPLY = 191;
    int FORKJOIN = 192;

    int INEWARRAY = 200;
    int FNEWARRAY = 201;
    int SNEWARRAY = 202;
    int BNEWARRAY = 203;
    int LNEWARRAY = 204;
    int RNEWARRAY = 205;
    int JSONNEWARRAY = 206;
    int ARRAYLEN = 207;

    int NEWSTRUCT = 210;
    int NEWCONNECTOR = 211;
    int NEWMAP = 212;
    int NEWJSON = 213;
    int NEWMESSAGE = 214;
    int NEWDATATABLE = 215;

    int REP = 230;
    int IRET = 231;
    int FRET = 232;
    int SRET = 233;
    int BRET = 234;
    int LRET = 235;
    int RRET = 236;
    int RET = 237;

    int INSTRUCTION_CODE_COUNT = 238;
}
