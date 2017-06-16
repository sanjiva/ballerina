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
 * @since 0.87
 */
public class Mnemonics {
    private static final String[] mnemonics = new String[InstructionCodes.INSTRUCTION_CODE_COUNT];

    static {
        mnemonics[InstructionCodes.ICONST] = "iconst";
        mnemonics[InstructionCodes.FCONST] = "fconst";
        mnemonics[InstructionCodes.SCONST] = "sconst";
        mnemonics[InstructionCodes.ICONST_0] = "iconst_0";
        mnemonics[InstructionCodes.ICONST_1] = "iconst_1";
        mnemonics[InstructionCodes.ICONST_2] = "iconst_2";
        mnemonics[InstructionCodes.ICONST_3] = "iconst_3";
        mnemonics[InstructionCodes.ICONST_4] = "iconst_4";
        mnemonics[InstructionCodes.ICONST_5] = "iconst_5";
        mnemonics[InstructionCodes.FCONST_0] = "fconst_0";
        mnemonics[InstructionCodes.FCONST_1] = "fconst_1";
        mnemonics[InstructionCodes.FCONST_2] = "fconst_2";
        mnemonics[InstructionCodes.FCONST_3] = "fconst_3";
        mnemonics[InstructionCodes.FCONST_4] = "fconst_4";
        mnemonics[InstructionCodes.FCONST_5] = "fconst_5";
        mnemonics[InstructionCodes.BCONST_0] = "bconst_0";
        mnemonics[InstructionCodes.BCONST_1] = "bconst_1";
        mnemonics[InstructionCodes.RCONST_NULL] = "rconst_null";

        mnemonics[InstructionCodes.ILOAD] = "iload";
        mnemonics[InstructionCodes.FLOAD] = "fload";
        mnemonics[InstructionCodes.SLOAD] = "sload";
        mnemonics[InstructionCodes.BLOAD] = "bload";
        mnemonics[InstructionCodes.LLOAD] = "lload";
        mnemonics[InstructionCodes.RLOAD] = "rload";
        mnemonics[InstructionCodes.IALOAD] = "iaload";
        mnemonics[InstructionCodes.FALOAD] = "faload";
        mnemonics[InstructionCodes.SALOAD] = "saload";
        mnemonics[InstructionCodes.BALOAD] = "baload";
        mnemonics[InstructionCodes.LALOAD] = "laload";
        mnemonics[InstructionCodes.RALOAD] = "raload";
        mnemonics[InstructionCodes.JSONALOAD] = "jsonaload";
        mnemonics[InstructionCodes.IGLOAD] = "igload";
        mnemonics[InstructionCodes.FGLOAD] = "fgload";
        mnemonics[InstructionCodes.SGLOAD] = "sgload";
        mnemonics[InstructionCodes.BGLOAD] = "bgload";
        mnemonics[InstructionCodes.LGLOAD] = "lgload";
        mnemonics[InstructionCodes.RGLOAD] = "rgload";

        mnemonics[InstructionCodes.ISTORE] = "istore";
        mnemonics[InstructionCodes.FSTORE] = "fstore";
        mnemonics[InstructionCodes.SSTORE] = "sstore";
        mnemonics[InstructionCodes.BSTORE] = "bstore";
        mnemonics[InstructionCodes.LSTORE] = "lstore";
        mnemonics[InstructionCodes.RSTORE] = "rstore";
        mnemonics[InstructionCodes.IASTORE] = "iastore";
        mnemonics[InstructionCodes.FASTORE] = "fastore";
        mnemonics[InstructionCodes.SASTORE] = "sastore";
        mnemonics[InstructionCodes.BASTORE] = "bastore";
        mnemonics[InstructionCodes.LASTORE] = "lastore";
        mnemonics[InstructionCodes.RASTORE] = "rastore";
        mnemonics[InstructionCodes.JSONASTORE] = "jsonastore";
        mnemonics[InstructionCodes.IGSTORE] = "igstore";
        mnemonics[InstructionCodes.FGSTORE] = "fgstore";
        mnemonics[InstructionCodes.SGSTORE] = "sgstore";
        mnemonics[InstructionCodes.BGSTORE] = "bgstore";
        mnemonics[InstructionCodes.LGSTORE] = "lgstore";
        mnemonics[InstructionCodes.RGSTORE] = "rgstore";

        mnemonics[InstructionCodes.IFIELDLOAD] = "ifieldload";
        mnemonics[InstructionCodes.FFIELDLOAD] = "ffieldload";
        mnemonics[InstructionCodes.SFIELDLOAD] = "sfieldload";
        mnemonics[InstructionCodes.BFIELDLOAD] = "bfieldload";
        mnemonics[InstructionCodes.LFIELDLOAD] = "lfieldload";
        mnemonics[InstructionCodes.RFIELDLOAD] = "rfieldload";
        mnemonics[InstructionCodes.IFIELDSTORE] = "ifieldstore";
        mnemonics[InstructionCodes.FFIELDSTORE] = "ffieldstore";
        mnemonics[InstructionCodes.SFIELDSTORE] = "sfieldstore";
        mnemonics[InstructionCodes.BFIELDSTORE] = "bfieldstore";
        mnemonics[InstructionCodes.LFIELDSTORE] = "lfieldstore";
        mnemonics[InstructionCodes.RFIELDSTORE] = "rfieldstore";

        mnemonics[InstructionCodes.MAPLOAD] = "mapload";
        mnemonics[InstructionCodes.MAPSTORE] = "mapstore";
        mnemonics[InstructionCodes.JSONLOAD] = "jsonload";
        mnemonics[InstructionCodes.JSONSTORE] = "jsonstore";

        mnemonics[InstructionCodes.IADD] = "iadd";
        mnemonics[InstructionCodes.FADD] = "fadd";
        mnemonics[InstructionCodes.SADD] = "sadd";
        mnemonics[InstructionCodes.XMLADD] = "radd";
        
        mnemonics[InstructionCodes.ISUB] = "isub";
        mnemonics[InstructionCodes.FSUB] = "fsub";
        mnemonics[InstructionCodes.SADD] = "sadd";
        mnemonics[InstructionCodes.IMUL] = "imul";
        mnemonics[InstructionCodes.FMUL] = "fmul";
        mnemonics[InstructionCodes.IDIV] = "idiv";
        mnemonics[InstructionCodes.FDIV] = "fdiv";
        mnemonics[InstructionCodes.IMOD] = "imod";
        mnemonics[InstructionCodes.FMOD] = "fmod";
        mnemonics[InstructionCodes.INEG] = "ineg";
        mnemonics[InstructionCodes.FNEG] = "fneg";
        mnemonics[InstructionCodes.BNOT] = "bnot";

        mnemonics[InstructionCodes.IEQ] = "ieq";
        mnemonics[InstructionCodes.FEQ] = "feq";
        mnemonics[InstructionCodes.SEQ] = "seq";
        mnemonics[InstructionCodes.BEQ] = "beq";
        mnemonics[InstructionCodes.REQ] = "req";
        mnemonics[InstructionCodes.INE] = "ine";
        mnemonics[InstructionCodes.FNE] = "fne";
        mnemonics[InstructionCodes.SNE] = "sne";
        mnemonics[InstructionCodes.BNE] = "bne";
        mnemonics[InstructionCodes.RNE] = "rne";

        mnemonics[InstructionCodes.IGT] = "igt";
        mnemonics[InstructionCodes.FGT] = "fgt";
        mnemonics[InstructionCodes.IGE] = "ige";
        mnemonics[InstructionCodes.FGE] = "fge";
        mnemonics[InstructionCodes.ILT] = "ilt";
        mnemonics[InstructionCodes.FLT] = "flt";
        mnemonics[InstructionCodes.ILE] = "ile";
        mnemonics[InstructionCodes.FLE] = "fle";
        
        mnemonics[InstructionCodes.REQ_NULL] = "reg_null";
        mnemonics[InstructionCodes.RNE_NULL] = "rne_null";
        mnemonics[InstructionCodes.BR_TRUE] = "br_true";
        mnemonics[InstructionCodes.BR_FALSE] = "br_false";

        mnemonics[InstructionCodes.GOTO] = "goto";
        mnemonics[InstructionCodes.CALL] = "call";
        mnemonics[InstructionCodes.NCALL] = "ncall";
        mnemonics[InstructionCodes.ACALL] = "acall";
        mnemonics[InstructionCodes.NACALL] = "nacall";
        mnemonics[InstructionCodes.THROW] = "throw";
        mnemonics[InstructionCodes.ERRSTORE] = "errstore";

        mnemonics[InstructionCodes.I2F] = "i2f";
        mnemonics[InstructionCodes.I2S] = "i2s";
        mnemonics[InstructionCodes.I2B] = "i2b";
        mnemonics[InstructionCodes.I2ANY] = "i2any";
        mnemonics[InstructionCodes.I2JSON] = "i2json";
        mnemonics[InstructionCodes.F2I] = "f2i";
        mnemonics[InstructionCodes.F2S] = "f2s";
        mnemonics[InstructionCodes.F2B] = "f2b";
        mnemonics[InstructionCodes.F2ANY] = "f2any";
        mnemonics[InstructionCodes.F2JSON] = "f2json";
        mnemonics[InstructionCodes.S2I] = "s2i";
        mnemonics[InstructionCodes.S2F] = "s2f";
        mnemonics[InstructionCodes.S2B] = "s2b";
        mnemonics[InstructionCodes.S2ANY] = "s2any";
        mnemonics[InstructionCodes.S2JSON] = "s2json";
        mnemonics[InstructionCodes.B2I] = "b2i";
        mnemonics[InstructionCodes.B2F] = "b2f";
        mnemonics[InstructionCodes.B2S] = "b2s";
        mnemonics[InstructionCodes.B2ANY] = "b2any";
        mnemonics[InstructionCodes.B2JSON] = "b2json";
        mnemonics[InstructionCodes.L2ANY] = "l2any";
        mnemonics[InstructionCodes.JSON2I] = "json2i";
        mnemonics[InstructionCodes.JSON2F] = "json2f";
        mnemonics[InstructionCodes.JSON2S] = "json2s";
        mnemonics[InstructionCodes.JSON2B] = "json2b";

        // Type cast
        mnemonics[InstructionCodes.ANY2I] = "any2i";
        mnemonics[InstructionCodes.ANY2F] = "any2f";
        mnemonics[InstructionCodes.ANY2S] = "any2s";
        mnemonics[InstructionCodes.ANY2B] = "any2b";
        mnemonics[InstructionCodes.ANY2L] = "any2l";
        mnemonics[InstructionCodes.ANY2JSON] = "any2json";
        mnemonics[InstructionCodes.ANY2XML] = "any2xml";
        mnemonics[InstructionCodes.ANY2T] = "any2t";
        mnemonics[InstructionCodes.ANY2MAP] = "any2map";
        mnemonics[InstructionCodes.NULL2JSON] = "null2json";
        mnemonics[InstructionCodes.CHECKCAST] = "checkcast";
        mnemonics[InstructionCodes.DT2JSON] = "dt2json";
        mnemonics[InstructionCodes.DT2XML] = "dt2xml";

        // Transactions
        mnemonics[InstructionCodes.TRBGN] = "trbgn";
        mnemonics[InstructionCodes.TREND] = "trend";

        mnemonics[InstructionCodes.WRKINVOKE] = "wrkinvoke";
        mnemonics[InstructionCodes.WRKREPLY] = "wrkreply";

        mnemonics[InstructionCodes.INEWARRAY] = "inewarray";
        mnemonics[InstructionCodes.FNEWARRAY] = "fnewarray";
        mnemonics[InstructionCodes.SNEWARRAY] = "snewarray";
        mnemonics[InstructionCodes.BNEWARRAY] = "bnewarray";
        mnemonics[InstructionCodes.LNEWARRAY] = "lnewarray";
        mnemonics[InstructionCodes.RNEWARRAY] = "rnewarray";
        mnemonics[InstructionCodes.JSONNEWARRAY] = "jsonnewarray";
        mnemonics[InstructionCodes.ARRAYLEN] = "arraylength";

        mnemonics[InstructionCodes.NEWSTRUCT] = "newstruct";
        mnemonics[InstructionCodes.NEWCONNECTOR] = "newconnector";
        mnemonics[InstructionCodes.NEWMAP] = "newmap";
        mnemonics[InstructionCodes.NEWJSON] = "newjson";
        mnemonics[InstructionCodes.NEWMESSAGE] = "newmessage";
        mnemonics[InstructionCodes.NEWDATATABLE] = "newdatatable";


        mnemonics[InstructionCodes.REP] = "rep";
        mnemonics[InstructionCodes.IRET] = "iret";
        mnemonics[InstructionCodes.FRET] = "fret";
        mnemonics[InstructionCodes.SRET] = "sret";
        mnemonics[InstructionCodes.BRET] = "bret";
        mnemonics[InstructionCodes.LRET] = "lret";
        mnemonics[InstructionCodes.RRET] = "rret";
        mnemonics[InstructionCodes.RET] = "ret";
    }

    public static String getMnem(int opcode) {
        String mnem = mnemonics[opcode];
        if (mnem == null) {
            throw new IllegalStateException("opcode " + opcode + " is not added to mnemonics");
        }
        return mnem;
    }
}
