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
package org.ballerinalang.bre.bvm;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringEscapeUtils;
import org.ballerinalang.bre.BallerinaTransactionManager;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.nonblocking.debugger.BreakPointInfo;
import org.ballerinalang.bre.nonblocking.debugger.FrameInfo;
import org.ballerinalang.bre.nonblocking.debugger.VariableInfo;
import org.ballerinalang.connector.api.AbstractNativeAction;
import org.ballerinalang.connector.api.ConnectorFuture;
import org.ballerinalang.connector.impl.BClientConnectorFutureListener;
import org.ballerinalang.connector.impl.BServerConnectorFuture;
import org.ballerinalang.model.NodeLocation;
import org.ballerinalang.model.types.BArrayType;
import org.ballerinalang.model.types.BConnectorType;
import org.ballerinalang.model.types.BEnumType;
import org.ballerinalang.model.types.BJSONConstraintType;
import org.ballerinalang.model.types.BStructType;
import org.ballerinalang.model.types.BType;
import org.ballerinalang.model.types.BTypes;
import org.ballerinalang.model.types.TypeConstants;
import org.ballerinalang.model.types.TypeTags;
import org.ballerinalang.model.util.JSONUtils;
import org.ballerinalang.model.util.XMLUtils;
import org.ballerinalang.model.values.BBlob;
import org.ballerinalang.model.values.BBlobArray;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BBooleanArray;
import org.ballerinalang.model.values.BConnector;
import org.ballerinalang.model.values.BDataTable;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BFloatArray;
import org.ballerinalang.model.values.BFunctionPointer;
import org.ballerinalang.model.values.BIntArray;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BJSON;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BNewArray;
import org.ballerinalang.model.values.BRefType;
import org.ballerinalang.model.values.BRefValueArray;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStringArray;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BTypeValue;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.BXML;
import org.ballerinalang.model.values.BXMLAttributes;
import org.ballerinalang.model.values.BXMLQName;
import org.ballerinalang.model.values.StructureType;
import org.ballerinalang.natives.AbstractNativeFunction;
import org.ballerinalang.runtime.threadpool.ThreadPoolFactory;
import org.ballerinalang.util.codegen.ActionInfo;
import org.ballerinalang.util.codegen.CallableUnitInfo;
import org.ballerinalang.util.codegen.ConnectorInfo;
import org.ballerinalang.util.codegen.ErrorTableEntry;
import org.ballerinalang.util.codegen.ForkjoinInfo;
import org.ballerinalang.util.codegen.FunctionInfo;
import org.ballerinalang.util.codegen.Instruction;
import org.ballerinalang.util.codegen.InstructionCodes;
import org.ballerinalang.util.codegen.LineNumberInfo;
import org.ballerinalang.util.codegen.Mnemonics;
import org.ballerinalang.util.codegen.PackageInfo;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.codegen.StructFieldInfo;
import org.ballerinalang.util.codegen.StructInfo;
import org.ballerinalang.util.codegen.TransformerInfo;
import org.ballerinalang.util.codegen.WorkerDataChannelInfo;
import org.ballerinalang.util.codegen.WorkerInfo;
import org.ballerinalang.util.codegen.attributes.AttributeInfo;
import org.ballerinalang.util.codegen.attributes.AttributeInfoPool;
import org.ballerinalang.util.codegen.attributes.DefaultValueAttributeInfo;
import org.ballerinalang.util.codegen.attributes.LocalVariableAttributeInfo;
import org.ballerinalang.util.codegen.cpentries.ActionRefCPEntry;
import org.ballerinalang.util.codegen.cpentries.ConstantPoolEntry;
import org.ballerinalang.util.codegen.cpentries.FloatCPEntry;
import org.ballerinalang.util.codegen.cpentries.ForkJoinCPEntry;
import org.ballerinalang.util.codegen.cpentries.FunctionCallCPEntry;
import org.ballerinalang.util.codegen.cpentries.FunctionRefCPEntry;
import org.ballerinalang.util.codegen.cpentries.IntegerCPEntry;
import org.ballerinalang.util.codegen.cpentries.StringCPEntry;
import org.ballerinalang.util.codegen.cpentries.StructureRefCPEntry;
import org.ballerinalang.util.codegen.cpentries.TransformerRefCPEntry;
import org.ballerinalang.util.codegen.cpentries.TypeRefCPEntry;
import org.ballerinalang.util.codegen.cpentries.WorkerDataChannelRefCPEntry;
import org.ballerinalang.util.codegen.cpentries.WrkrInteractionArgsCPEntry;
import org.ballerinalang.util.debugger.DebugInfoHolder;
import org.ballerinalang.util.debugger.VMDebugManager;
import org.ballerinalang.util.exceptions.BLangExceptionHelper;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.ballerinalang.util.exceptions.RuntimeErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ballerinalang.util.Lists;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * This class executes Ballerina instruction codes.
 *
 * @since 0.88
 */
public class BLangVM {

    private static final String JOIN_TYPE_SOME = "some";
    private static final Logger logger = LoggerFactory.getLogger(BLangVM.class);
    private Context context;
    private ControlStackNew controlStack;
    private ProgramFile programFile;
    private ConstantPoolEntry[] constPool;
    // Instruction pointer;
    private int ip = 0;
    private Instruction[] code;

    private StructureType globalMemBlock;

    public BLangVM(ProgramFile programFile) {
        this.programFile = programFile;
        this.globalMemBlock = programFile.getGlobalMemoryBlock();
    }

    private void traceCode(PackageInfo packageInfo) {
        PrintStream printStream = System.out;
        for (int i = 0; i < code.length; i++) {
            printStream.println(i + ": " + Mnemonics.getMnem(code[i].getOpcode()) + " " +
                    getOperandsLine(code[i].getOperands()));
        }
    }

    public void run(Context ctx) {
        StackFrame currentFrame = ctx.getControlStackNew().getCurrentFrame();
        this.constPool = currentFrame.packageInfo.getConstPoolEntries();
        this.code = currentFrame.packageInfo.getInstructions();

        this.context = ctx;
        this.controlStack = context.getControlStackNew();
        this.ip = context.getStartIP();

        if (context.getError() != null) {
            handleError();
        } else if (isWaitingOnNonBlockingAction()) {
            // // TODO : Temporary to solution make non-blocking working.
            BType[] retTypes = context.actionInfo.getRetParamTypes();
            StackFrame calleeSF = controlStack.popFrame();
            this.constPool = controlStack.currentFrame.packageInfo.getConstPoolEntries();
            this.code = controlStack.currentFrame.packageInfo.getInstructions();
            handleReturnFromNativeCallableUnit(controlStack.currentFrame, context.funcCallCPEntry.getRetRegs(),
                    calleeSF.returnValues, retTypes);

            // TODO Remove
            //prepareStructureTypeFromNativeAction(context.nativeArgValues);
            context.nativeArgValues = null;
            context.funcCallCPEntry = null;
            context.actionInfo = null;
        }

        try {
            exec();
        } catch (Throwable e) {
            String message;
            if (e.getMessage() == null) {
                message = "unknown error occurred";
            } else {
                message = e.getMessage();
            }
            context.setError(BLangVMErrors.createError(context, ip, message));
            handleError();
        } finally {
            if (!isWaitingOnNonBlockingAction() || context.getError() != null) {
                // end of the active worker from the VM. ( graceful or forced exit on unhandled error. )
                // Doesn't count non-blocking action invocation.
                ctx.endTrackWorker();
            }
        }
    }

    public void execWorker(Context context, int startIP) {
        context.setStartIP(startIP);
        if (VMDebugManager.getInstance().isDebugSessionActive()) {
            VMDebugManager debugManager = VMDebugManager.getInstance();
            context.setAndInitDebugInfoHolder(new DebugInfoHolder());
            context.getDebugInfoHolder().setCurrentCommand(DebugInfoHolder.DebugCommand.RESUME);
            context.setDebugEnabled(true);
            debugManager.setDebuggerContext(context);
        }
        run(context);
    }

    /**
     * Act as a virtual CPU.
     */
    private void exec() {
        int i;
        int j;
        int k;
        int cpIndex; // Index of the constant pool
        FunctionCallCPEntry funcCallCPEntry;
        FunctionRefCPEntry funcRefCPEntry;
        TypeRefCPEntry typeRefCPEntry;
        ActionRefCPEntry actionRefCPEntry;
        FunctionInfo functionInfo;
        ActionInfo actionInfo;
        WorkerDataChannelRefCPEntry workerRefCPEntry;
        WrkrInteractionArgsCPEntry wrkrIntRefCPEntry;
        WorkerDataChannelInfo workerDataChannel;
        ForkJoinCPEntry forkJoinCPEntry;

        boolean isDebugging = context.isDebugEnabled();

        StackFrame currentSF, callersSF;
        int callersRetRegIndex;

        while (ip >= 0 && ip < code.length && controlStack.currentFrame != null) {

            if (isDebugging) {
                debugging(ip);
            }
            Instruction instruction = code[ip];
            int opcode = instruction.getOpcode();
            int[] operands = instruction.getOperands();
            ip++;
            StackFrame sf = controlStack.currentFrame;

            switch (opcode) {
                case InstructionCodes.ICONST:
                    cpIndex = operands[0];
                    i = operands[1];
                    sf.longRegs[i] = ((IntegerCPEntry) constPool[cpIndex]).getValue();
                    break;
                case InstructionCodes.FCONST:
                    cpIndex = operands[0];
                    i = operands[1];
                    sf.doubleRegs[i] = ((FloatCPEntry) constPool[cpIndex]).getValue();
                    break;
                case InstructionCodes.SCONST:
                    cpIndex = operands[0];
                    i = operands[1];
                    sf.stringRegs[i] = ((StringCPEntry) constPool[cpIndex]).getValue();
                    break;
                case InstructionCodes.ICONST_0:
                    i = operands[0];
                    sf.longRegs[i] = 0;
                    break;
                case InstructionCodes.ICONST_1:
                    i = operands[0];
                    sf.longRegs[i] = 1;
                    break;
                case InstructionCodes.ICONST_2:
                    i = operands[0];
                    sf.longRegs[i] = 2;
                    break;
                case InstructionCodes.ICONST_3:
                    i = operands[0];
                    sf.longRegs[i] = 3;
                    break;
                case InstructionCodes.ICONST_4:
                    i = operands[0];
                    sf.longRegs[i] = 4;
                    break;
                case InstructionCodes.ICONST_5:
                    i = operands[0];
                    sf.longRegs[i] = 5;
                    break;
                case InstructionCodes.FCONST_0:
                    i = operands[0];
                    sf.doubleRegs[i] = 0;
                    break;
                case InstructionCodes.FCONST_1:
                    i = operands[0];
                    sf.doubleRegs[i] = 1;
                    break;
                case InstructionCodes.FCONST_2:
                    i = operands[0];
                    sf.doubleRegs[i] = 2;
                    break;
                case InstructionCodes.FCONST_3:
                    i = operands[0];
                    sf.doubleRegs[i] = 3;
                    break;
                case InstructionCodes.FCONST_4:
                    i = operands[0];
                    sf.doubleRegs[i] = 4;
                    break;
                case InstructionCodes.FCONST_5:
                    i = operands[0];
                    sf.doubleRegs[i] = 5;
                    break;
                case InstructionCodes.BCONST_0:
                    i = operands[0];
                    sf.intRegs[i] = 0;
                    break;
                case InstructionCodes.BCONST_1:
                    i = operands[0];
                    sf.intRegs[i] = 1;
                    break;
                case InstructionCodes.RCONST_NULL:
                    i = operands[0];
                    sf.refRegs[i] = null;
                    break;

                case InstructionCodes.ILOAD:
                case InstructionCodes.FLOAD:
                case InstructionCodes.SLOAD:
                case InstructionCodes.BLOAD:
                case InstructionCodes.LLOAD:
                case InstructionCodes.RLOAD:
                case InstructionCodes.IALOAD:
                case InstructionCodes.FALOAD:
                case InstructionCodes.SALOAD:
                case InstructionCodes.BALOAD:
                case InstructionCodes.LALOAD:
                case InstructionCodes.RALOAD:
                case InstructionCodes.JSONALOAD:
                case InstructionCodes.IGLOAD:
                case InstructionCodes.FGLOAD:
                case InstructionCodes.SGLOAD:
                case InstructionCodes.BGLOAD:
                case InstructionCodes.LGLOAD:
                case InstructionCodes.RGLOAD:
                case InstructionCodes.IFIELDLOAD:
                case InstructionCodes.FFIELDLOAD:
                case InstructionCodes.SFIELDLOAD:
                case InstructionCodes.BFIELDLOAD:
                case InstructionCodes.LFIELDLOAD:
                case InstructionCodes.RFIELDLOAD:
                case InstructionCodes.MAPLOAD:
                case InstructionCodes.JSONLOAD:
                case InstructionCodes.ENUMERATORLOAD:
                    execLoadOpcodes(sf, opcode, operands);
                    break;

                case InstructionCodes.ISTORE:
                case InstructionCodes.FSTORE:
                case InstructionCodes.SSTORE:
                case InstructionCodes.BSTORE:
                case InstructionCodes.LSTORE:
                case InstructionCodes.RSTORE:
                case InstructionCodes.IASTORE:
                case InstructionCodes.FASTORE:
                case InstructionCodes.SASTORE:
                case InstructionCodes.BASTORE:
                case InstructionCodes.LASTORE:
                case InstructionCodes.RASTORE:
                case InstructionCodes.JSONASTORE:
                case InstructionCodes.IGSTORE:
                case InstructionCodes.FGSTORE:
                case InstructionCodes.SGSTORE:
                case InstructionCodes.BGSTORE:
                case InstructionCodes.LGSTORE:
                case InstructionCodes.RGSTORE:
                case InstructionCodes.IFIELDSTORE:
                case InstructionCodes.FFIELDSTORE:
                case InstructionCodes.SFIELDSTORE:
                case InstructionCodes.BFIELDSTORE:
                case InstructionCodes.LFIELDSTORE:
                case InstructionCodes.RFIELDSTORE:
                case InstructionCodes.MAPSTORE:
                case InstructionCodes.JSONSTORE:
                    execStoreOpcodes(sf, opcode, operands);
                    break;

                case InstructionCodes.IADD:
                case InstructionCodes.FADD:
                case InstructionCodes.SADD:
                case InstructionCodes.XMLADD:
                case InstructionCodes.ISUB:
                case InstructionCodes.FSUB:
                case InstructionCodes.IMUL:
                case InstructionCodes.FMUL:
                case InstructionCodes.IDIV:
                case InstructionCodes.FDIV:
                case InstructionCodes.IMOD:
                case InstructionCodes.FMOD:
                case InstructionCodes.INEG:
                case InstructionCodes.FNEG:
                case InstructionCodes.BNOT:
                case InstructionCodes.IEQ:
                case InstructionCodes.FEQ:
                case InstructionCodes.SEQ:
                case InstructionCodes.BEQ:
                case InstructionCodes.REQ:
                case InstructionCodes.TEQ:
                case InstructionCodes.INE:
                case InstructionCodes.FNE:
                case InstructionCodes.SNE:
                case InstructionCodes.BNE:
                case InstructionCodes.RNE:
                case InstructionCodes.TNE:
                    execBinaryOpCodes(sf, opcode, operands);
                    break;

                case InstructionCodes.LENGTHOF:
                    i = operands[0];
                    j = operands[1];
                    if (sf.refRegs[i] == null) {
                        handleNullRefError();
                        break;
                    }

                    BValue array = sf.refRegs[i];
                    if (array.getType().getTag() == TypeTags.XML_TAG) {
                        sf.longRegs[j] = ((BXML) array).length();
                        break;
                    } else if (array.getType().getTag() == TypeTags.JSON_TAG) {
                        if (JSONUtils.isJSONArray((BJSON) array)) {
                            sf.longRegs[j] = JSONUtils.getJSONArrayLength((BJSON) sf.refRegs[i]);
                        } else {
                            sf.longRegs[j] = -1;
                        }
                        break;
                    }

                    BNewArray newArray = (BNewArray) array;
                    sf.longRegs[j] = newArray.size();
                    break;

                case InstructionCodes.TYPELOAD:
                    cpIndex = operands[0];
                    j = operands[1];
                    TypeRefCPEntry typeEntry = (TypeRefCPEntry) constPool[cpIndex];
                    sf.refRegs[j] = new BTypeValue(typeEntry.getType());
                    break;
                case InstructionCodes.TYPEOF:
                    i = operands[0];
                    j = operands[1];
                    if (sf.refRegs[i] == null) {
                        handleNullRefError();
                        break;
                    }
                    sf.refRegs[j] = new BTypeValue(sf.refRegs[i].getType());
                    break;

                case InstructionCodes.IGT:
                case InstructionCodes.FGT:
                case InstructionCodes.IGE:
                case InstructionCodes.FGE:
                case InstructionCodes.ILT:
                case InstructionCodes.FLT:
                case InstructionCodes.ILE:
                case InstructionCodes.FLE:
                case InstructionCodes.REQ_NULL:
                case InstructionCodes.RNE_NULL:
                case InstructionCodes.BR_TRUE:
                case InstructionCodes.BR_FALSE:
                case InstructionCodes.GOTO:
                case InstructionCodes.HALT:
                    execCmpAndBranchOpcodes(sf, opcode, operands);
                    break;

                case InstructionCodes.TR_RETRY:
                    i = operands[0];
                    j = operands[1];
                    retryTransaction(i, j);
                    break;
                case InstructionCodes.CALL:
                    cpIndex = operands[0];
                    funcRefCPEntry = (FunctionRefCPEntry) constPool[cpIndex];
                    functionInfo = funcRefCPEntry.getFunctionInfo();

                    cpIndex = operands[1];
                    funcCallCPEntry = (FunctionCallCPEntry) constPool[cpIndex];
                    invokeCallableUnit(functionInfo, funcCallCPEntry);
                    break;
                case InstructionCodes.TR_BEGIN:
                    i = operands[0];
                    j = operands[1];
                    beginTransaction(i, j);
                    break;
                case InstructionCodes.TR_END:
                    i = operands[0];
                    endTransaction(i);
                    break;
                case InstructionCodes.WRKINVOKE:
                    cpIndex = operands[0];
                    workerRefCPEntry = (WorkerDataChannelRefCPEntry) constPool[cpIndex];
                    workerDataChannel = workerRefCPEntry.getWorkerDataChannelInfo();

                    cpIndex = operands[1];
                    wrkrIntRefCPEntry = (WrkrInteractionArgsCPEntry) constPool[cpIndex];
                    invokeWorker(workerDataChannel, wrkrIntRefCPEntry);
                    break;
                case InstructionCodes.WRKREPLY:
                    cpIndex = operands[0];
                    workerRefCPEntry = (WorkerDataChannelRefCPEntry) constPool[cpIndex];
                    workerDataChannel = workerRefCPEntry.getWorkerDataChannelInfo();

                    cpIndex = operands[1];
                    wrkrIntRefCPEntry = (WrkrInteractionArgsCPEntry) constPool[cpIndex];
                    replyWorker(workerDataChannel, wrkrIntRefCPEntry);
                    break;
                case InstructionCodes.FORKJOIN:
                    cpIndex = operands[0];

                    forkJoinCPEntry = (ForkJoinCPEntry) constPool[cpIndex];
                    invokeForkJoin(forkJoinCPEntry);
                    break;
                case InstructionCodes.WRKSTART:
                    startWorkers();
                    break;
                case InstructionCodes.WRKRETURN:
                    handleWorkerReturn();
                    break;
                case InstructionCodes.NCALL:
                    cpIndex = operands[0];
                    funcRefCPEntry = (FunctionRefCPEntry) constPool[cpIndex];
                    functionInfo = funcRefCPEntry.getFunctionInfo();

                    cpIndex = operands[1];
                    funcCallCPEntry = (FunctionCallCPEntry) constPool[cpIndex];
                    invokeNativeFunction(functionInfo, funcCallCPEntry);
                    break;
                case InstructionCodes.ACALL:
                    cpIndex = operands[0];
                    actionRefCPEntry = (ActionRefCPEntry) constPool[cpIndex];
                    actionInfo = actionRefCPEntry.getActionInfo();

                    cpIndex = operands[1];
                    funcCallCPEntry = (FunctionCallCPEntry) constPool[cpIndex];
                    invokeAction(actionInfo, funcCallCPEntry);
                    break;
                case InstructionCodes.NACALL:
                    cpIndex = operands[0];
                    actionRefCPEntry = (ActionRefCPEntry) constPool[cpIndex];
                    actionInfo = actionRefCPEntry.getActionInfo();

                    cpIndex = operands[1];
                    funcCallCPEntry = (FunctionCallCPEntry) constPool[cpIndex];
                    invokeNativeAction(actionInfo, funcCallCPEntry);
                    break;
                case InstructionCodes.THROW:
                    i = operands[0];
                    if (i >= 0) {
                        BStruct error = (BStruct) sf.refRegs[i];
                        if (error == null) {
                            handleNullRefError();
                            break;
                        }

                        BLangVMErrors.setStackTrace(context, ip, error);
                        context.setError(error);
                    }
                    handleError();
                    break;
                case InstructionCodes.ERRSTORE:
                    i = operands[0];
                    sf.refLocalVars[i] = context.getError();
                    // clear error.
                    context.setError(null);
                    break;
                case InstructionCodes.FPCALL:
                    i = operands[0];
                    if (sf.refRegs[i] == null) {
                        handleNullRefError();
                        break;
                    }
                    cpIndex = operands[1];
                    funcCallCPEntry = (FunctionCallCPEntry) constPool[cpIndex];
                    funcRefCPEntry = ((BFunctionPointer) sf.refRegs[i]).value();
                    functionInfo = funcRefCPEntry.getFunctionInfo();
                    if (functionInfo.isNative()) {
                        invokeNativeFunction(functionInfo, funcCallCPEntry);
                    } else {
                        invokeCallableUnit(functionInfo, funcCallCPEntry);
                    }
                    break;
                case InstructionCodes.FPLOAD:
                    i = operands[0];
                    j = operands[1];
                    funcRefCPEntry = (FunctionRefCPEntry) constPool[i];
                    sf.refRegs[j] = new BFunctionPointer(funcRefCPEntry);
                    break;
                case InstructionCodes.TCALL:
                    cpIndex = operands[0];
                    TransformerInfo transformerInfo = ((TransformerRefCPEntry) constPool[cpIndex]).getTransformerInfo();

                    cpIndex = operands[1];
                    funcCallCPEntry = (FunctionCallCPEntry) constPool[cpIndex];
                    invokeCallableUnit(transformerInfo, funcCallCPEntry);
                    break;

                case InstructionCodes.I2ANY:
                case InstructionCodes.F2ANY:
                case InstructionCodes.S2ANY:
                case InstructionCodes.B2ANY:
                case InstructionCodes.L2ANY:
                case InstructionCodes.ANY2I:
                case InstructionCodes.ANY2F:
                case InstructionCodes.ANY2S:
                case InstructionCodes.ANY2B:
                case InstructionCodes.ANY2L:
                case InstructionCodes.ANY2JSON:
                case InstructionCodes.ANY2XML:
                case InstructionCodes.ANY2MAP:
                case InstructionCodes.ANY2TYPE:
                case InstructionCodes.ANY2E:
                case InstructionCodes.ANY2T:
                case InstructionCodes.ANY2C:
                case InstructionCodes.NULL2JSON:
                case InstructionCodes.CHECKCAST:
                    execTypeCastOpcodes(sf, opcode, operands);
                    break;

                case InstructionCodes.I2F:
                case InstructionCodes.I2S:
                case InstructionCodes.I2B:
                case InstructionCodes.I2JSON:
                case InstructionCodes.F2I:
                case InstructionCodes.F2S:
                case InstructionCodes.F2B:
                case InstructionCodes.F2JSON:
                case InstructionCodes.S2I:
                case InstructionCodes.S2F:
                case InstructionCodes.S2B:
                case InstructionCodes.S2JSON:
                case InstructionCodes.B2I:
                case InstructionCodes.B2F:
                case InstructionCodes.B2S:
                case InstructionCodes.B2JSON:
                case InstructionCodes.JSON2I:
                case InstructionCodes.JSON2F:
                case InstructionCodes.JSON2S:
                case InstructionCodes.JSON2B:
                case InstructionCodes.DT2XML:
                case InstructionCodes.DT2JSON:
                case InstructionCodes.T2MAP:
                case InstructionCodes.T2JSON:
                case InstructionCodes.MAP2T:
                case InstructionCodes.JSON2T:
                case InstructionCodes.XMLATTRS2MAP:
                case InstructionCodes.S2XML:
                case InstructionCodes.S2JSONX:
                case InstructionCodes.XML2S:
                    execTypeConversionOpcodes(sf, opcode, operands);
                    break;

                case InstructionCodes.INEWARRAY:
                    i = operands[0];
                    sf.refRegs[i] = new BIntArray();
                    break;
                case InstructionCodes.ARRAYLEN:
                    i = operands[0];
                    j = operands[1];

                    BValue value = sf.refRegs[i];

                    if (value == null) {
                        handleNullRefError();
                        break;
                    }

                    if (value.getType().getTag() == TypeTags.JSON_TAG) {
                        sf.longRegs[j] = ((BJSON) value).value().size();
                        break;
                    }

                    sf.longRegs[j] = ((BNewArray) value).size();
                    break;
                case InstructionCodes.FNEWARRAY:
                    i = operands[0];
                    sf.refRegs[i] = new BFloatArray();
                    break;
                case InstructionCodes.SNEWARRAY:
                    i = operands[0];
                    sf.refRegs[i] = new BStringArray();
                    break;
                case InstructionCodes.BNEWARRAY:
                    i = operands[0];
                    sf.refRegs[i] = new BBooleanArray();
                    break;
                case InstructionCodes.LNEWARRAY:
                    i = operands[0];
                    sf.refRegs[i] = new BBlobArray();
                    break;
                case InstructionCodes.RNEWARRAY:
                    i = operands[0];
                    cpIndex = operands[1];
                    typeRefCPEntry = (TypeRefCPEntry) constPool[cpIndex];
                    sf.refRegs[i] = new BRefValueArray(typeRefCPEntry.getType());
                    break;
                case InstructionCodes.JSONNEWARRAY:
                    i = operands[0];
                    j = operands[1];
                    // This is a temporary solution to create n-valued JSON array
                    StringJoiner stringJoiner = new StringJoiner(",", "[", "]");
                    for (int index = 0; index < sf.longRegs[j]; index++) {
                        stringJoiner.add("0");
                    }
                    sf.refRegs[i] = new BJSON(stringJoiner.toString());
                    break;

                case InstructionCodes.NEWSTRUCT:
                    createNewStruct(operands, sf);
                    break;
                case InstructionCodes.NEWCONNECTOR:
                    createNewConnector(operands, sf);
                    break;
                case InstructionCodes.NEWMAP:
                    i = operands[0];
                    sf.refRegs[i] = new BMap<String, BRefType>();
                    break;
                case InstructionCodes.NEWJSON:
                    i = operands[0];
                    sf.refRegs[i] = new BJSON("{}");
                    break;
                case InstructionCodes.NEWDATATABLE:
                    i = operands[0];
                    sf.refRegs[i] = new BDataTable(null);
                    break;
                case InstructionCodes.IRET:
                    i = operands[0];
                    j = operands[1];
                    currentSF = controlStack.currentFrame;
                    callersSF = controlStack.currentFrame.prevStackFrame;
                    callersRetRegIndex = currentSF.retRegIndexes[i];
                    callersSF.longRegs[callersRetRegIndex] = currentSF.longRegs[j];
                    break;
                case InstructionCodes.FRET:
                    i = operands[0];
                    j = operands[1];
                    currentSF = controlStack.currentFrame;
                    callersSF = controlStack.currentFrame.prevStackFrame;
                    callersRetRegIndex = currentSF.retRegIndexes[i];
                    callersSF.doubleRegs[callersRetRegIndex] = currentSF.doubleRegs[j];
                    break;
                case InstructionCodes.SRET:
                    i = operands[0];
                    j = operands[1];
                    currentSF = controlStack.currentFrame;
                    callersSF = controlStack.currentFrame.prevStackFrame;
                    callersRetRegIndex = currentSF.retRegIndexes[i];
                    callersSF.stringRegs[callersRetRegIndex] = currentSF.stringRegs[j];
                    break;
                case InstructionCodes.BRET:
                    i = operands[0];
                    j = operands[1];
                    currentSF = controlStack.currentFrame;
                    callersSF = controlStack.currentFrame.prevStackFrame;
                    callersRetRegIndex = currentSF.retRegIndexes[i];
                    callersSF.intRegs[callersRetRegIndex] = currentSF.intRegs[j];
                    break;
                case InstructionCodes.LRET:
                    i = operands[0];
                    j = operands[1];
                    currentSF = controlStack.currentFrame;
                    callersSF = controlStack.currentFrame.prevStackFrame;
                    callersRetRegIndex = currentSF.retRegIndexes[i];
                    callersSF.byteRegs[callersRetRegIndex] = currentSF.byteRegs[j];
                    break;
                case InstructionCodes.RRET:
                    i = operands[0];
                    j = operands[1];
                    currentSF = controlStack.currentFrame;
                    callersSF = controlStack.currentFrame.prevStackFrame;
                    callersRetRegIndex = currentSF.retRegIndexes[i];
                    callersSF.refRegs[callersRetRegIndex] = currentSF.refRegs[j];
                    break;
                case InstructionCodes.RET:
                    handleReturn();
                    break;
                case InstructionCodes.XMLATTRSTORE:
                case InstructionCodes.XMLATTRLOAD:
                case InstructionCodes.XML2XMLATTRS:
                case InstructionCodes.S2QNAME:
                case InstructionCodes.NEWQNAME:
                case InstructionCodes.NEWXMLELEMENT:
                case InstructionCodes.NEWXMLCOMMENT:
                case InstructionCodes.NEWXMLTEXT:
                case InstructionCodes.NEWXMLPI:
                case InstructionCodes.XMLSTORE:
                case InstructionCodes.XMLLOAD:
                    execXMLOpcodes(sf, opcode, operands);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    private void execCmpAndBranchOpcodes(StackFrame sf, int opcode, int[] operands) {
        int i;
        int j;
        int k;
        switch (opcode) {
            case InstructionCodes.IGT:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.longRegs[i] > sf.longRegs[j] ? 1 : 0;
                break;
            case InstructionCodes.FGT:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.doubleRegs[i] > sf.doubleRegs[j] ? 1 : 0;
                break;

            case InstructionCodes.IGE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.longRegs[i] >= sf.longRegs[j] ? 1 : 0;
                break;
            case InstructionCodes.FGE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.doubleRegs[i] >= sf.doubleRegs[j] ? 1 : 0;
                break;

            case InstructionCodes.ILT:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.longRegs[i] < sf.longRegs[j] ? 1 : 0;
                break;
            case InstructionCodes.FLT:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.doubleRegs[i] < sf.doubleRegs[j] ? 1 : 0;
                break;

            case InstructionCodes.ILE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.longRegs[i] <= sf.longRegs[j] ? 1 : 0;
                break;
            case InstructionCodes.FLE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.doubleRegs[i] <= sf.doubleRegs[j] ? 1 : 0;
                break;

            case InstructionCodes.REQ_NULL:
                i = operands[0];
                j = operands[1];
                if (sf.refRegs[i] == null) {
                    sf.intRegs[j] = 1;
                } else {
                    sf.intRegs[j] = 0;
                }
                break;
            case InstructionCodes.RNE_NULL:
                i = operands[0];
                j = operands[1];
                if (sf.refRegs[i] != null) {
                    sf.intRegs[j] = 1;
                } else {
                    sf.intRegs[j] = 0;
                }
                break;

            case InstructionCodes.BR_TRUE:
                i = operands[0];
                j = operands[1];
                if (sf.intRegs[i] == 1) {
                    ip = j;
                }
                break;
            case InstructionCodes.BR_FALSE:
                i = operands[0];
                j = operands[1];
                if (sf.intRegs[i] == 0) {
                    ip = j;
                }
                break;
            case InstructionCodes.GOTO:
                i = operands[0];
                ip = i;
                break;
            case InstructionCodes.HALT:
                ip = -1;
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void execLoadOpcodes(StackFrame sf, int opcode, int[] operands) {
        int i;
        int j;
        int k;
        int lvIndex; // Index of the local variable
        int fieldIndex;

        BIntArray bIntArray;
        BFloatArray bFloatArray;
        BStringArray bStringArray;
        BBooleanArray bBooleanArray;
        BBlobArray bBlobArray;
        BRefValueArray bArray;
        StructureType structureType;
        BMap<String, BRefType> bMap;
        BJSON jsonVal;
        switch (opcode) {
            case InstructionCodes.ILOAD:
                lvIndex = operands[0];
                i = operands[1];
                sf.longRegs[i] = sf.longLocalVars[lvIndex];
                break;
            case InstructionCodes.FLOAD:
                lvIndex = operands[0];
                i = operands[1];
                sf.doubleRegs[i] = sf.doubleLocalVars[lvIndex];
                break;
            case InstructionCodes.SLOAD:
                lvIndex = operands[0];
                i = operands[1];
                sf.stringRegs[i] = sf.stringLocalVars[lvIndex];
                break;
            case InstructionCodes.BLOAD:
                lvIndex = operands[0];
                i = operands[1];
                sf.intRegs[i] = sf.intLocalVars[lvIndex];
                break;
            case InstructionCodes.LLOAD:
                lvIndex = operands[0];
                i = operands[1];
                sf.byteRegs[i] = sf.byteLocalVars[lvIndex];
                break;
            case InstructionCodes.RLOAD:
                lvIndex = operands[0];
                i = operands[1];
                sf.refRegs[i] = sf.refLocalVars[lvIndex];
                break;
            case InstructionCodes.IALOAD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bIntArray = (BIntArray) sf.refRegs[i];
                if (bIntArray == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    sf.longRegs[k] = bIntArray.get(sf.longRegs[j]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.FALOAD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bFloatArray = (BFloatArray) sf.refRegs[i];
                if (bFloatArray == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    sf.doubleRegs[k] = bFloatArray.get(sf.longRegs[j]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.SALOAD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bStringArray = (BStringArray) sf.refRegs[i];
                if (bStringArray == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    sf.stringRegs[k] = bStringArray.get(sf.longRegs[j]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.BALOAD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bBooleanArray = (BBooleanArray) sf.refRegs[i];
                if (bBooleanArray == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    sf.intRegs[k] = bBooleanArray.get(sf.longRegs[j]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.LALOAD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bBlobArray = (BBlobArray) sf.refRegs[i];
                if (bBlobArray == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    sf.byteRegs[k] = bBlobArray.get(sf.longRegs[j]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.RALOAD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bArray = (BRefValueArray) sf.refRegs[i];
                if (bArray == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    sf.refRegs[k] = bArray.get(sf.longRegs[j]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.JSONALOAD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                jsonVal = (BJSON) sf.refRegs[i];
                if (jsonVal == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    sf.refRegs[k] = JSONUtils.getArrayElement(jsonVal, sf.longRegs[j]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.IGLOAD:
                // Global variable index
                i = operands[0];
                // Stack registry index
                j = operands[1];
                sf.longRegs[j] = globalMemBlock.getIntField(i);
                break;
            case InstructionCodes.FGLOAD:
                i = operands[0];
                j = operands[1];
                sf.doubleRegs[j] = globalMemBlock.getFloatField(i);
                break;
            case InstructionCodes.SGLOAD:
                i = operands[0];
                j = operands[1];
                sf.stringRegs[j] = globalMemBlock.getStringField(i);
                break;
            case InstructionCodes.BGLOAD:
                i = operands[0];
                j = operands[1];
                sf.intRegs[j] = globalMemBlock.getBooleanField(i);
                break;
            case InstructionCodes.LGLOAD:
                i = operands[0];
                j = operands[1];
                sf.byteRegs[j] = globalMemBlock.getBlobField(i);
                break;
            case InstructionCodes.RGLOAD:
                i = operands[0];
                j = operands[1];
                sf.refRegs[j] = globalMemBlock.getRefField(i);
                break;

            case InstructionCodes.IFIELDLOAD:
                i = operands[0];
                fieldIndex = operands[1];
                j = operands[2];
                structureType = (StructureType) sf.refRegs[i];
                if (structureType == null) {
                    handleNullRefError();
                    break;
                }

                sf.longRegs[j] = structureType.getIntField(fieldIndex);
                break;
            case InstructionCodes.FFIELDLOAD:
                i = operands[0];
                fieldIndex = operands[1];
                j = operands[2];
                structureType = (StructureType) sf.refRegs[i];
                if (structureType == null) {
                    handleNullRefError();
                    break;
                }

                sf.doubleRegs[j] = structureType.getFloatField(fieldIndex);
                break;
            case InstructionCodes.SFIELDLOAD:
                i = operands[0];
                fieldIndex = operands[1];
                j = operands[2];
                structureType = (StructureType) sf.refRegs[i];
                if (structureType == null) {
                    handleNullRefError();
                    break;
                }

                sf.stringRegs[j] = structureType.getStringField(fieldIndex);
                break;
            case InstructionCodes.BFIELDLOAD:
                i = operands[0];
                fieldIndex = operands[1];
                j = operands[2];
                structureType = (StructureType) sf.refRegs[i];
                if (structureType == null) {
                    handleNullRefError();
                    break;
                }

                sf.intRegs[j] = structureType.getBooleanField(fieldIndex);
                break;
            case InstructionCodes.LFIELDLOAD:
                i = operands[0];
                fieldIndex = operands[1];
                j = operands[2];
                structureType = (StructureType) sf.refRegs[i];
                if (structureType == null) {
                    handleNullRefError();
                    break;
                }

                sf.byteRegs[j] = structureType.getBlobField(fieldIndex);
                break;
            case InstructionCodes.RFIELDLOAD:
                i = operands[0];
                fieldIndex = operands[1];
                j = operands[2];
                structureType = (StructureType) sf.refRegs[i];
                if (structureType == null) {
                    handleNullRefError();
                    break;
                }

                sf.refRegs[j] = structureType.getRefField(fieldIndex);
                break;

            case InstructionCodes.MAPLOAD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bMap = (BMap<String, BRefType>) sf.refRegs[i];
                if (bMap == null) {
                    handleNullRefError();
                    break;
                }

                sf.refRegs[k] = bMap.get(sf.stringRegs[j]);
                break;

            case InstructionCodes.JSONLOAD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                jsonVal = (BJSON) sf.refRegs[i];
                if (jsonVal == null) {
                    handleNullRefError();
                    break;
                }

                sf.refRegs[k] = JSONUtils.getElement(jsonVal, sf.stringRegs[j]);
                break;
            case InstructionCodes.ENUMERATORLOAD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                TypeRefCPEntry typeRefCPEntry = (TypeRefCPEntry) constPool[i];
                BEnumType enumType = (BEnumType) typeRefCPEntry.getType();
                sf.refRegs[k] = enumType.getEnumerator(j);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void execStoreOpcodes(StackFrame sf, int opcode, int[] operands) {
        int i;
        int j;
        int k;
        int lvIndex; // Index of the local variable
        int fieldIndex;

        BIntArray bIntArray;
        BFloatArray bFloatArray;
        BStringArray bStringArray;
        BBooleanArray bBooleanArray;
        BBlobArray bBlobArray;
        BRefValueArray bArray;
        StructureType structureType;
        BMap<String, BRefType> bMap;
        BJSON jsonVal;
        switch (opcode) {
            case InstructionCodes.ISTORE:
                i = operands[0];
                lvIndex = operands[1];
                sf.longLocalVars[lvIndex] = sf.longRegs[i];
                break;
            case InstructionCodes.FSTORE:
                i = operands[0];
                lvIndex = operands[1];
                sf.doubleLocalVars[lvIndex] = sf.doubleRegs[i];
                break;
            case InstructionCodes.SSTORE:
                i = operands[0];
                lvIndex = operands[1];
                sf.stringLocalVars[lvIndex] = sf.stringRegs[i];
                break;
            case InstructionCodes.BSTORE:
                i = operands[0];
                lvIndex = operands[1];
                sf.intLocalVars[lvIndex] = sf.intRegs[i];
                break;
            case InstructionCodes.LSTORE:
                i = operands[0];
                lvIndex = operands[1];
                sf.byteLocalVars[lvIndex] = sf.byteRegs[i];
                break;
            case InstructionCodes.RSTORE:
                i = operands[0];
                lvIndex = operands[1];
                sf.refLocalVars[lvIndex] = sf.refRegs[i];
                break;
            case InstructionCodes.IASTORE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bIntArray = (BIntArray) sf.refRegs[i];
                if (bIntArray == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    bIntArray.add(sf.longRegs[j], sf.longRegs[k]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.FASTORE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bFloatArray = (BFloatArray) sf.refRegs[i];
                if (bFloatArray == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    bFloatArray.add(sf.longRegs[j], sf.doubleRegs[k]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.SASTORE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bStringArray = (BStringArray) sf.refRegs[i];
                if (bStringArray == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    bStringArray.add(sf.longRegs[j], sf.stringRegs[k]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.BASTORE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bBooleanArray = (BBooleanArray) sf.refRegs[i];
                if (bBooleanArray == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    bBooleanArray.add(sf.longRegs[j], sf.intRegs[k]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.LASTORE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bBlobArray = (BBlobArray) sf.refRegs[i];
                if (bBlobArray == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    bBlobArray.add(sf.longRegs[j], sf.byteRegs[k]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.RASTORE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bArray = (BRefValueArray) sf.refRegs[i];
                if (bArray == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    bArray.add(sf.longRegs[j], sf.refRegs[k]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.JSONASTORE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                jsonVal = (BJSON) sf.refRegs[i];
                if (jsonVal == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    JSONUtils.setArrayElement(jsonVal, sf.longRegs[j], (BJSON) sf.refRegs[k]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.IGSTORE:
                // Stack reg index
                i = operands[0];
                // Global var index
                j = operands[1];
                globalMemBlock.setIntField(j, sf.longRegs[i]);
                break;
            case InstructionCodes.FGSTORE:
                i = operands[0];
                j = operands[1];
                globalMemBlock.setFloatField(j, sf.doubleRegs[i]);
                break;
            case InstructionCodes.SGSTORE:
                i = operands[0];
                j = operands[1];
                globalMemBlock.setStringField(j, sf.stringRegs[i]);
                break;
            case InstructionCodes.BGSTORE:
                i = operands[0];
                j = operands[1];
                globalMemBlock.setBooleanField(j, sf.intRegs[i]);
                break;
            case InstructionCodes.LGSTORE:
                i = operands[0];
                j = operands[1];
                globalMemBlock.setBlobField(j, sf.byteRegs[i]);
                break;
            case InstructionCodes.RGSTORE:
                i = operands[0];
                j = operands[1];
                globalMemBlock.setRefField(j, sf.refRegs[i]);
                break;

            case InstructionCodes.IFIELDSTORE:
                i = operands[0];
                fieldIndex = operands[1];
                j = operands[2];
                structureType = (StructureType) sf.refRegs[i];
                if (structureType == null) {
                    handleNullRefError();
                    break;
                }

                structureType.setIntField(fieldIndex, sf.longRegs[j]);
                break;
            case InstructionCodes.FFIELDSTORE:
                i = operands[0];
                fieldIndex = operands[1];
                j = operands[2];
                structureType = (StructureType) sf.refRegs[i];
                if (structureType == null) {
                    handleNullRefError();
                    break;
                }

                structureType.setFloatField(fieldIndex, sf.doubleRegs[j]);
                break;
            case InstructionCodes.SFIELDSTORE:
                i = operands[0];
                fieldIndex = operands[1];
                j = operands[2];
                structureType = (StructureType) sf.refRegs[i];
                if (structureType == null) {
                    handleNullRefError();
                    break;
                }

                structureType.setStringField(fieldIndex, sf.stringRegs[j]);
                break;
            case InstructionCodes.BFIELDSTORE:
                i = operands[0];
                fieldIndex = operands[1];
                j = operands[2];
                structureType = (StructureType) sf.refRegs[i];
                if (structureType == null) {
                    handleNullRefError();
                    break;
                }

                structureType.setBooleanField(fieldIndex, sf.intRegs[j]);
                break;
            case InstructionCodes.LFIELDSTORE:
                i = operands[0];
                fieldIndex = operands[1];
                j = operands[2];
                structureType = (StructureType) sf.refRegs[i];
                if (structureType == null) {
                    handleNullRefError();
                    break;
                }

                structureType.setBlobField(fieldIndex, sf.byteRegs[j]);
                break;
            case InstructionCodes.RFIELDSTORE:
                i = operands[0];
                fieldIndex = operands[1];
                j = operands[2];
                structureType = (StructureType) sf.refRegs[i];
                if (structureType == null) {
                    handleNullRefError();
                    break;
                }

                structureType.setRefField(fieldIndex, sf.refRegs[j]);
                break;


            case InstructionCodes.MAPSTORE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                bMap = (BMap<String, BRefType>) sf.refRegs[i];
                if (bMap == null) {
                    handleNullRefError();
                    break;
                }

                bMap.put(sf.stringRegs[j], sf.refRegs[k]);
                break;


            case InstructionCodes.JSONSTORE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                jsonVal = (BJSON) sf.refRegs[i];
                if (jsonVal == null) {
                    handleNullRefError();
                    break;
                }
                JSONUtils.setElement(jsonVal, sf.stringRegs[j], (BJSON) sf.refRegs[k]);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void execBinaryOpCodes(StackFrame sf, int opcode, int[] operands) {
        int i;
        int j;
        int k;
        switch (opcode) {
            case InstructionCodes.IADD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.longRegs[k] = sf.longRegs[i] + sf.longRegs[j];
                break;
            case InstructionCodes.FADD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.doubleRegs[k] = sf.doubleRegs[i] + sf.doubleRegs[j];
                break;
            case InstructionCodes.SADD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.stringRegs[k] = sf.stringRegs[i] + sf.stringRegs[j];
                break;
            case InstructionCodes.XMLADD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                BXML lhsXMLVal = (BXML) sf.refRegs[i];
                BXML rhsXMLVal = (BXML) sf.refRegs[j];
                if (lhsXMLVal == null || rhsXMLVal == null) {
                    handleNullRefError();
                    break;
                }

                // Here it is assumed that a refType addition can only be a xml-concat.
                sf.refRegs[k] = XMLUtils.concatenate(lhsXMLVal, rhsXMLVal);
                break;
            case InstructionCodes.ISUB:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.longRegs[k] = sf.longRegs[i] - sf.longRegs[j];
                break;
            case InstructionCodes.FSUB:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.doubleRegs[k] = sf.doubleRegs[i] - sf.doubleRegs[j];
                break;
            case InstructionCodes.IMUL:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.longRegs[k] = sf.longRegs[i] * sf.longRegs[j];
                break;
            case InstructionCodes.FMUL:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.doubleRegs[k] = sf.doubleRegs[i] * sf.doubleRegs[j];
                break;
            case InstructionCodes.IDIV:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                if (sf.longRegs[j] == 0) {
                    context.setError(BLangVMErrors.createError(context, ip, " / by zero"));
                    handleError();
                    break;
                }

                sf.longRegs[k] = sf.longRegs[i] / sf.longRegs[j];
                break;
            case InstructionCodes.FDIV:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                if (sf.doubleRegs[j] == 0) {
                    context.setError(BLangVMErrors.createError(context, ip, " / by zero"));
                    handleError();
                    break;
                }

                sf.doubleRegs[k] = sf.doubleRegs[i] / sf.doubleRegs[j];
                break;
            case InstructionCodes.IMOD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                if (sf.longRegs[j] == 0) {
                    context.setError(BLangVMErrors.createError(context, ip, " / by zero"));
                    handleError();
                    break;
                }

                sf.longRegs[k] = sf.longRegs[i] % sf.longRegs[j];
                break;
            case InstructionCodes.FMOD:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                if (sf.doubleRegs[j] == 0) {
                    context.setError(BLangVMErrors.createError(context, ip, " / by zero"));
                    handleError();
                    break;
                }

                sf.doubleRegs[k] = sf.doubleRegs[i] % sf.doubleRegs[j];
                break;
            case InstructionCodes.INEG:
                i = operands[0];
                j = operands[1];
                sf.longRegs[j] = -sf.longRegs[i];
                break;
            case InstructionCodes.FNEG:
                i = operands[0];
                j = operands[1];
                sf.doubleRegs[j] = -sf.doubleRegs[i];
                break;
            case InstructionCodes.BNOT:
                i = operands[0];
                j = operands[1];
                sf.intRegs[j] = sf.intRegs[i] == 0 ? 1 : 0;
                break;
            case InstructionCodes.IEQ:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.longRegs[i] == sf.longRegs[j] ? 1 : 0;
                break;
            case InstructionCodes.FEQ:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.doubleRegs[i] == sf.doubleRegs[j] ? 1 : 0;
                break;
            case InstructionCodes.SEQ:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.stringRegs[i].equals(sf.stringRegs[j]) ? 1 : 0;
                break;
            case InstructionCodes.BEQ:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.intRegs[i] == sf.intRegs[j] ? 1 : 0;
                break;
            case InstructionCodes.REQ:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.refRegs[i] == sf.refRegs[j] ? 1 : 0;
                break;
            case InstructionCodes.TEQ:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                if (sf.refRegs[i] == null || sf.refRegs[j] == null) {
                    handleNullRefError();
                }
                sf.intRegs[k] = sf.refRegs[i].equals(sf.refRegs[j]) ? 1 : 0;
                break;

            case InstructionCodes.INE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.longRegs[i] != sf.longRegs[j] ? 1 : 0;
                break;
            case InstructionCodes.FNE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.doubleRegs[i] != sf.doubleRegs[j] ? 1 : 0;
                break;
            case InstructionCodes.SNE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = !(sf.stringRegs[i].equals(sf.stringRegs[j])) ? 1 : 0;
                break;
            case InstructionCodes.BNE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.intRegs[i] != sf.intRegs[j] ? 1 : 0;
                break;
            case InstructionCodes.RNE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[k] = sf.refRegs[i] != sf.refRegs[j] ? 1 : 0;
                break;
            case InstructionCodes.TNE:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                if (sf.refRegs[i] == null || sf.refRegs[j] == null) {
                    handleNullRefError();
                }
                sf.intRegs[k] = (!sf.refRegs[i].equals(sf.refRegs[j])) ? 1 : 0;
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void execXMLOpcodes(StackFrame sf, int opcode, int[] operands) {
        int i;
        int j;
        int k;
        int localNameIndex;
        int uriIndex;
        int prefixIndex;

        BXML<?> xmlVal;
        BXMLQName xmlQName;

        switch (opcode) {
            case InstructionCodes.XMLATTRSTORE:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                xmlVal = (BXML) sf.refRegs[i];
                if (xmlVal == null) {
                    handleNullRefError();
                    break;
                }

                xmlQName = (BXMLQName) sf.refRegs[j];
                if (xmlQName == null) {
                    handleNullRefError();
                    break;
                }

                xmlVal.setAttribute(xmlQName.getLocalName(), xmlQName.getUri(), xmlQName.getPrefix(),
                        sf.stringRegs[k]);
                break;
            case InstructionCodes.XMLATTRLOAD:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                xmlVal = (BXML) sf.refRegs[i];
                if (xmlVal == null) {
                    handleNullRefError();
                    break;
                }

                xmlQName = (BXMLQName) sf.refRegs[j];
                if (xmlQName == null) {
                    handleNullRefError();
                    break;
                }

                sf.stringRegs[k] = xmlVal.getAttribute(xmlQName.getLocalName(), xmlQName.getUri(),
                        xmlQName.getPrefix());
                break;
            case InstructionCodes.XML2XMLATTRS:
                i = operands[0];
                j = operands[1];

                xmlVal = (BXML) sf.refRegs[i];
                if (xmlVal == null) {
                    sf.refRegs[j] = null;
                    break;
                }

                sf.refRegs[j] = new BXMLAttributes(xmlVal);
                break;
            case InstructionCodes.S2QNAME:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                String qNameStr = sf.stringRegs[i];
                int parenEndIndex = qNameStr.indexOf('}');

                if (qNameStr.startsWith("{") && parenEndIndex > 0) {
                    sf.stringRegs[j] = qNameStr.substring(parenEndIndex + 1, qNameStr.length());
                    sf.stringRegs[k] = qNameStr.substring(1, parenEndIndex);
                } else {
                    sf.stringRegs[j] = qNameStr;
                    sf.stringRegs[k] = "";
                }

                break;
            case InstructionCodes.NEWQNAME:
                localNameIndex = operands[0];
                uriIndex = operands[1];
                prefixIndex = operands[2];
                i = operands[3];

                String localname = sf.stringRegs[localNameIndex];
                localname = StringEscapeUtils.escapeXml11(localname);

                String prefix = sf.stringRegs[prefixIndex];
                prefix = StringEscapeUtils.escapeXml11(prefix);

                sf.refRegs[i] = new BXMLQName(localname, sf.stringRegs[uriIndex], prefix);
                break;
            case InstructionCodes.XMLLOAD:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                xmlVal = (BXML) sf.refRegs[i];
                if (xmlVal == null) {
                    handleNullRefError();
                    break;
                }

                long index = sf.longRegs[j];
                sf.refRegs[k] = xmlVal.getItem(index);
                break;
            case InstructionCodes.NEWXMLELEMENT:
            case InstructionCodes.NEWXMLCOMMENT:
            case InstructionCodes.NEWXMLTEXT:
            case InstructionCodes.NEWXMLPI:
            case InstructionCodes.XMLSTORE:
                execXMLCreationOpcodes(sf, opcode, operands);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void execTypeCastOpcodes(StackFrame sf, int opcode, int[] operands) {
        int i;
        int j;
        int k;
        int cpIndex; // Index of the constant pool

        BRefType bRefType;
        TypeRefCPEntry typeRefCPEntry;

        switch (opcode) {
            case InstructionCodes.I2ANY:
                i = operands[0];
                j = operands[1];
                sf.refRegs[j] = new BInteger(sf.longRegs[i]);
                break;
            case InstructionCodes.F2ANY:
                i = operands[0];
                j = operands[1];
                sf.refRegs[j] = new BFloat(sf.doubleRegs[i]);
                break;
            case InstructionCodes.S2ANY:
                i = operands[0];
                j = operands[1];
                sf.refRegs[j] = new BString(sf.stringRegs[i]);
                break;
            case InstructionCodes.B2ANY:
                i = operands[0];
                j = operands[1];
                sf.refRegs[j] = new BBoolean(sf.intRegs[i] == 1);
                break;
            case InstructionCodes.L2ANY:
                i = operands[0];
                j = operands[1];
                sf.refRegs[j] = new BBlob(sf.byteRegs[i]);
                break;
            case InstructionCodes.ANY2I:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                bRefType = sf.refRegs[i];
                if (bRefType == null) {
                    sf.longRegs[j] = 0;
                    handleTypeCastError(sf, k, BTypes.typeNull, BTypes.typeInt);
                } else if (bRefType.getType() == BTypes.typeInt) {
                    sf.refRegs[k] = null;
                    sf.longRegs[j] = ((BInteger) bRefType).intValue();
                } else {
                    sf.longRegs[j] = 0;
                    handleTypeCastError(sf, k, bRefType.getType(), BTypes.typeInt);
                }
                break;
            case InstructionCodes.ANY2F:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                bRefType = sf.refRegs[i];
                if (bRefType == null) {
                    sf.doubleRegs[j] = 0;
                    handleTypeCastError(sf, k, BTypes.typeNull, BTypes.typeFloat);
                } else if (bRefType.getType() == BTypes.typeFloat) {
                    sf.refRegs[k] = null;
                    sf.doubleRegs[j] = ((BFloat) bRefType).floatValue();
                } else {
                    sf.doubleRegs[j] = 0;
                    handleTypeCastError(sf, k, bRefType.getType(), BTypes.typeFloat);
                }
                break;
            case InstructionCodes.ANY2S:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                bRefType = sf.refRegs[i];
                if (bRefType == null) {
                    sf.stringRegs[j] = "";
                    handleTypeCastError(sf, k, BTypes.typeNull, BTypes.typeString);
                } else if (bRefType.getType() == BTypes.typeString) {
                    sf.refRegs[k] = null;
                    sf.stringRegs[j] = bRefType.stringValue();
                } else {
                    sf.stringRegs[j] = "";
                    handleTypeCastError(sf, k, bRefType.getType(), BTypes.typeString);
                }
                break;
            case InstructionCodes.ANY2B:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                bRefType = sf.refRegs[i];
                if (bRefType == null) {
                    sf.intRegs[j] = 0;
                    handleTypeCastError(sf, k, BTypes.typeNull, BTypes.typeBoolean);
                } else if (bRefType.getType() == BTypes.typeBoolean) {
                    sf.refRegs[k] = null;
                    sf.intRegs[j] = ((BBoolean) bRefType).booleanValue() ? 1 : 0;
                } else {
                    sf.intRegs[j] = 0;
                    handleTypeCastError(sf, k, bRefType.getType(), BTypes.typeBoolean);
                }
                break;
            case InstructionCodes.ANY2L:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                bRefType = sf.refRegs[i];
                if (bRefType == null) {
                    sf.byteRegs[j] = new byte[0];
                    handleTypeCastError(sf, k, BTypes.typeNull, BTypes.typeBlob);
                } else if (bRefType.getType() == BTypes.typeBlob) {
                    sf.refRegs[k] = null;
                    sf.byteRegs[j] = ((BBlob) bRefType).blobValue();
                } else {
                    sf.byteRegs[j] = new byte[0];
                    handleTypeCastError(sf, k, bRefType.getType(), BTypes.typeBlob);
                }
                break;
            case InstructionCodes.ANY2JSON:
                handleAnyToRefTypeCast(sf, operands, BTypes.typeJSON);
                break;
            case InstructionCodes.ANY2XML:
                handleAnyToRefTypeCast(sf, operands, BTypes.typeXML);
                break;
            case InstructionCodes.ANY2MAP:
                handleAnyToRefTypeCast(sf, operands, BTypes.typeMap);
                break;
            case InstructionCodes.ANY2TYPE:
                handleAnyToRefTypeCast(sf, operands, BTypes.typeType);
                break;
            case InstructionCodes.ANY2DT:
                handleAnyToRefTypeCast(sf, operands, BTypes.typeDatatable);
                break;
            case InstructionCodes.ANY2E:
            case InstructionCodes.ANY2T:
            case InstructionCodes.ANY2C:
            case InstructionCodes.CHECKCAST:
                i = operands[0];
                cpIndex = operands[1];
                j = operands[2];
                k = operands[3];
                typeRefCPEntry = (TypeRefCPEntry) constPool[cpIndex];

                bRefType = sf.refRegs[i];

                if (bRefType == null) {
                    sf.refRegs[j] = null;
                } else if (checkCast(bRefType, typeRefCPEntry.getType())) {
                    sf.refRegs[j] = sf.refRegs[i];
                    sf.refRegs[k] = null;
                } else {
                    sf.refRegs[j] = null;
                    handleTypeCastError(sf, k, bRefType.getType(), typeRefCPEntry.getType());
                }
                break;
            case InstructionCodes.NULL2JSON:
                j = operands[1];
                sf.refRegs[j] = new BJSON("null");
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private boolean checkConstraintJSONEquivalency(BJSON json, BStructType targetType) {
        BStructType.StructField[] tFields = targetType.getStructFields();
        for (int i = 0; i < tFields.length; i++) {
            if (JSONUtils.hasElement(json, tFields[i].getFieldName())) {
                if (tFields[i].getFieldType() instanceof BStructType) {
                    if (checkConstraintJSONEquivalency(JSONUtils.getElement(json, tFields[i].getFieldName()),
                            (BStructType) tFields[i].getFieldType())) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            return false;
        }
        return true;
    }

    private void execTypeConversionOpcodes(StackFrame sf, int opcode, int[] operands) {
        int i;
        int j;
        int k;
        BRefType bRefType;

        switch (opcode) {
            case InstructionCodes.I2F:
                i = operands[0];
                j = operands[1];
                sf.doubleRegs[j] = (double) sf.longRegs[i];
                break;
            case InstructionCodes.I2S:
                i = operands[0];
                j = operands[1];
                sf.stringRegs[j] = Long.toString(sf.longRegs[i]);
                break;
            case InstructionCodes.I2B:
                i = operands[0];
                j = operands[1];
                sf.intRegs[j] = sf.longRegs[i] != 0 ? 1 : 0;
                break;
            case InstructionCodes.I2JSON:
                i = operands[0];
                j = operands[1];
                sf.refRegs[j] = new BJSON(Long.toString(sf.longRegs[i]));
                break;
            case InstructionCodes.F2I:
                i = operands[0];
                j = operands[1];
                sf.longRegs[j] = (long) sf.doubleRegs[i];
                break;
            case InstructionCodes.F2S:
                i = operands[0];
                j = operands[1];
                sf.stringRegs[j] = Double.toString(sf.doubleRegs[i]);
                break;
            case InstructionCodes.F2B:
                i = operands[0];
                j = operands[1];
                sf.intRegs[j] = sf.doubleRegs[i] != 0.0 ? 1 : 0;
                break;
            case InstructionCodes.F2JSON:
                i = operands[0];
                j = operands[1];
                sf.refRegs[j] = new BJSON(Double.toString(sf.doubleRegs[i]));
                break;
            case InstructionCodes.S2I:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                try {
                    sf.longRegs[j] = Long.parseLong(sf.stringRegs[i]);
                } catch (NumberFormatException e) {
                    sf.longRegs[j] = 0;
                    handleTypeConversionError(sf, k, TypeConstants.STRING_TNAME, TypeConstants.INT_TNAME);
                }
                break;
            case InstructionCodes.S2F:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                try {
                    sf.doubleRegs[j] = Double.parseDouble(sf.stringRegs[i]);
                } catch (NumberFormatException e) {
                    sf.doubleRegs[j] = 0;
                    handleTypeConversionError(sf, k, TypeConstants.STRING_TNAME, TypeConstants.FLOAT_TNAME);
                }
                break;
            case InstructionCodes.S2B:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                sf.intRegs[j] = Boolean.parseBoolean(sf.stringRegs[i]) ? 1 : 0;
                break;
            case InstructionCodes.S2JSON:
                i = operands[0];
                j = operands[1];
                String jsonStr = StringEscapeUtils.escapeJson(sf.stringRegs[i]);
                sf.refRegs[j] = new BJSON("\"" + jsonStr + "\"");
                break;
            case InstructionCodes.B2I:
                i = operands[0];
                j = operands[1];
                sf.longRegs[j] = sf.intRegs[i];
                break;
            case InstructionCodes.B2F:
                i = operands[0];
                j = operands[1];
                sf.doubleRegs[j] = sf.intRegs[i];
                break;
            case InstructionCodes.B2S:
                i = operands[0];
                j = operands[1];
                sf.stringRegs[j] = sf.intRegs[i] == 1 ? "true" : "false";
                break;
            case InstructionCodes.B2JSON:
                i = operands[0];
                j = operands[1];
                sf.refRegs[j] = new BJSON(sf.intRegs[i] == 1 ? "true" : "false");
                break;
            case InstructionCodes.JSON2I:
                convertJSONToInt(operands, sf);
                break;
            case InstructionCodes.JSON2F:
                convertJSONToFloat(operands, sf);
                break;
            case InstructionCodes.JSON2S:
                convertJSONToString(operands, sf);
                break;
            case InstructionCodes.JSON2B:
                convertJSONToBoolean(operands, sf);
                break;
            case InstructionCodes.DT2XML:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                bRefType = sf.refRegs[i];
                if (bRefType == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    sf.refRegs[j] = XMLUtils.datatableToXML((BDataTable) bRefType, context.isInTransaction());
                } catch (Exception e) {
                    sf.refRegs[j] = null;
                    handleTypeConversionError(sf, k, TypeConstants.DATATABLE_TNAME, TypeConstants.XML_TNAME);
                }
                break;
            case InstructionCodes.DT2JSON:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                bRefType = sf.refRegs[i];
                if (bRefType == null) {
                    handleNullRefError();
                    break;
                }

                try {
                    sf.refRegs[j] = JSONUtils.toJSON((BDataTable) bRefType, context.isInTransaction());
                } catch (Exception e) {
                    sf.refRegs[j] = null;
                    handleTypeConversionError(sf, k, TypeConstants.DATATABLE_TNAME, TypeConstants.XML_TNAME);
                }
                break;
            case InstructionCodes.T2MAP:
                convertStructToMap(operands, sf);
                break;
            case InstructionCodes.T2JSON:
                convertStructToJSON(operands, sf);
                break;
            case InstructionCodes.MAP2T:
                convertMapToStruct(operands, sf);
                break;
            case InstructionCodes.JSON2T:
                convertJSONToStruct(operands, sf);
                break;
            case InstructionCodes.XMLATTRS2MAP:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                bRefType = sf.refRegs[i];
                if (bRefType == null) {
                    sf.refRegs[j] = null;
                    break;
                }

                try {
                    sf.refRegs[j] = ((BXMLAttributes) sf.refRegs[i]).value();
                } catch (BallerinaException e) {
                    sf.refRegs[j] = null;
                    handleTypeConversionError(sf, k, TypeConstants.XML_ATTRIBUTES_TNAME, TypeConstants.MAP_TNAME);
                }
                break;
            case InstructionCodes.S2XML:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                try {
                    sf.refRegs[j] = XMLUtils.parse(sf.stringRegs[i]);
                } catch (BallerinaException e) {
                    sf.refRegs[j] = null;
                    handleTypeConversionError(sf, k, e.getMessage(), TypeConstants.STRING_TNAME,
                            TypeConstants.XML_TNAME);
                }
                break;
            case InstructionCodes.S2JSONX:
                i = operands[0];
                j = operands[1];
                sf.refRegs[j] = new BJSON(sf.stringRegs[i]);
                break;
            case InstructionCodes.XML2S:
                i = operands[0];
                j = operands[1];
                sf.stringRegs[j] = sf.refRegs[j].stringValue();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void execXMLCreationOpcodes(StackFrame sf, int opcode, int[] operands) {
        int i;
        int j;
        int k;
        int l;
        BXML<?> xmlVal;

        switch (opcode) {
            case InstructionCodes.NEWXMLELEMENT:
                i = operands[0];
                j = operands[1];
                k = operands[2];
                l = operands[3];

                BXMLQName startTagName = (BXMLQName) sf.refRegs[j];
                BXMLQName endTagName = (BXMLQName) sf.refRegs[k];

                try {
                    sf.refRegs[i] = XMLUtils.createXMLElement(startTagName, endTagName, sf.stringRegs[l]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.NEWXMLCOMMENT:
                i = operands[0];
                j = operands[1];

                try {
                    sf.refRegs[i] = XMLUtils.createXMLComment(sf.stringRegs[j]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.NEWXMLTEXT:
                i = operands[0];
                j = operands[1];

                try {
                    sf.refRegs[i] = XMLUtils.createXMLText(sf.stringRegs[j]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.NEWXMLPI:
                i = operands[0];
                j = operands[1];
                k = operands[2];

                try {
                    sf.refRegs[i] = XMLUtils.createXMLProcessingInstruction(sf.stringRegs[j], sf.stringRegs[k]);
                } catch (Exception e) {
                    context.setError(BLangVMErrors.createError(context, ip, e.getMessage()));
                    handleError();
                }
                break;
            case InstructionCodes.XMLSTORE:
                i = operands[0];
                j = operands[1];

                xmlVal = (BXML<?>) sf.refRegs[i];
                BXML<?> child = (BXML<?>) sf.refRegs[j];
                xmlVal.addChildren(child);
                break;
        }
    }

    /**
     * Method to calculate and detect debug points when the instruction point is given.
     *
     * @param cp Current instruction point.
     */
    public void debugging(int cp) {
        DebugInfoHolder holder = context.getDebugInfoHolder();
        LineNumberInfo currentExecLine = holder.getLineNumber(controlStack.currentFrame.packageInfo.getPkgPath(), cp);
        if (currentExecLine.equals(holder.getLastLine()) || debugPointCheck(currentExecLine, holder)) {
            return;
        }

        switch (holder.getCurrentCommand()) {
            case RESUME:
                holder.setLastLine(null);
                break;
            case STEP_IN:
                debugHit(currentExecLine, holder);
                break;
            case STEP_OVER:
                if (controlStack.currentFrame == holder.getSF()) {
                    debugHit(currentExecLine, holder);
                    return;
                }
                if (holder.getLastLine().checkIpRangeForInstructionCode(code, InstructionCodes.RET)
                        && controlStack.currentFrame == holder.getSF().prevStackFrame) {
                    debugHit(currentExecLine, holder);
                    return;
                }
                holder.setCurrentCommand(DebugInfoHolder.DebugCommand.STEP_OVER_INTMDT);
                break;
            case STEP_OVER_INTMDT:
                if (controlStack.currentFrame != holder.getSF()) {
                    return;
                }
                debugHit(currentExecLine, holder);
                break;
            case STEP_OUT:
                holder.setCurrentCommand(DebugInfoHolder.DebugCommand.STEP_OUT_INTMDT);
                holder.setSF(holder.getSF().prevStackFrame);
                interMediateDebugCheck(currentExecLine, holder);
                break;
            case STEP_OUT_INTMDT:
                interMediateDebugCheck(currentExecLine, holder);
                break;
        }
    }

    /**
     * Inter mediate debug check to avoid switch case falling through.
     *
     * @param currentExecLine Current execution line.
     * @param holder          Debug info holder.
     */
    private void interMediateDebugCheck(LineNumberInfo currentExecLine, DebugInfoHolder holder) {
        if (controlStack.currentFrame != holder.getSF()) {
            return;
        }
        debugHit(currentExecLine, holder);
    }

    /**
     * Helper method to check whether given point is a debug point or not.
     * If it's a debug point, then notify the debugger.
     *
     * @param currentExecLine Current execution line.
     * @param holder          Debug info holder.
     * @return Boolean true if it's a debug point, false otherwise.
     */
    private boolean debugPointCheck(LineNumberInfo currentExecLine, DebugInfoHolder holder) {
        if (!currentExecLine.isDebugPoint()) {
            return false;
        }
        debugHit(currentExecLine, holder);
        return true;
    }

    /**
     * Helper method to set required details when a debug point hits.
     * And also to notify the debugger.
     *
     * @param currentExecLine Current execution line.
     * @param holder          Debug info holder.
     */
    private void debugHit(LineNumberInfo currentExecLine, DebugInfoHolder holder) {
        holder.setLastLine(currentExecLine);
        holder.setSF(controlStack.currentFrame);
        holder.getDebugSessionObserver().notifyHalt(getBreakPointInfo(currentExecLine));
        holder.waitTillDebuggeeResponds();
    }

    public BreakPointInfo getBreakPointInfo(LineNumberInfo current) {
        NodeLocation location = new NodeLocation(current.getPackageInfo().getPkgPath(),
                current.getFileName(), current.getLineNumber());
        BreakPointInfo breakPointInfo = new BreakPointInfo(location);
        breakPointInfo.setThreadId(context.getThreadId());

        int callingIp = current.getIp();
        StackFrame frame = controlStack.currentFrame;
        while (frame != null) {
            String pck = frame.packageInfo.getPkgPath();
            String functionName = frame.callableUnitInfo.getName();
            LineNumberInfo callingLine = context.getDebugInfoHolder()
                    .getLineNumber(frame.packageInfo.getPkgPath(), callingIp);
            FrameInfo frameInfo = new FrameInfo(pck, functionName, callingLine.getFileName(),
                    callingLine.getLineNumber());
            LocalVariableAttributeInfo localVarAttrInfo = (LocalVariableAttributeInfo) frame.callableUnitInfo
                    .getDefaultWorkerInfo().getAttributeInfo(AttributeInfo.Kind.LOCAL_VARIABLES_ATTRIBUTE);
            if (localVarAttrInfo == null) {
                frame = frame.prevStackFrame;
                continue;
            }
            final StackFrame fcp = frame;
            localVarAttrInfo.getLocalVariables().forEach(localVarInfo -> {
                VariableInfo variableInfo = new VariableInfo(localVarInfo.getVariableName(), "Local");
                if (BTypes.typeInt.equals(localVarInfo.getVariableType())) {
                    variableInfo.setBValue(new BInteger(fcp.longLocalVars[localVarInfo.getVariableIndex()]));
                } else if (BTypes.typeFloat.equals(localVarInfo.getVariableType())) {
                    variableInfo.setBValue(new BFloat(fcp.doubleLocalVars[localVarInfo.getVariableIndex()]));
                } else if (BTypes.typeString.equals(localVarInfo.getVariableType())) {
                    variableInfo.setBValue(new BString(fcp.stringLocalVars[localVarInfo.getVariableIndex()]));
                } else if (BTypes.typeBoolean.equals(localVarInfo.getVariableType())) {
                    variableInfo.setBValue(new BBoolean(fcp.intLocalVars[localVarInfo
                            .getVariableIndex()] == 1 ? true : false));
                } else if (BTypes.typeBlob.equals(localVarInfo.getVariableType())) {
                    variableInfo.setBValue(new BBlob(fcp.byteLocalVars[localVarInfo.getVariableIndex()]));
                } else {
                    variableInfo.setBValue(fcp.refLocalVars[localVarInfo.getVariableIndex()]);
                }
                frameInfo.addVariableInfo(variableInfo);
            });
            callingIp = frame.retAddrs - 1;
            if (callingIp < 0) {
                callingIp = 0;
            }
            breakPointInfo.addFrameInfo(frameInfo);
            frame = frame.prevStackFrame;
        }
        return breakPointInfo;
    }

    private void handleAnyToRefTypeCast(StackFrame sf, int[] operands, BType targetType) {
        int i = operands[0];
        int j = operands[1];
        int k = operands[2];

        BRefType bRefType = sf.refRegs[i];
        if (bRefType == null) {
            sf.refRegs[j] = null;
        } else if (bRefType.getType() == targetType) {
            sf.refRegs[j] = bRefType;
        } else {
            sf.refRegs[j] = null;
            handleTypeCastError(sf, k, bRefType.getType(), targetType);
        }
    }

    private void handleTypeCastError(StackFrame sf, int errorRegIndex, BType sourceType, BType targetType) {
        BStruct errorVal;
        errorVal = BLangVMErrors.createTypeCastError(context, ip, sourceType, targetType);
        if (errorRegIndex == -1) {
            context.setError(errorVal);
            handleError();
            return;
        }

        sf.refRegs[errorRegIndex] = errorVal;
    }

    private void handleTypeConversionError(StackFrame sf, int errorRegIndex,
                                           String sourceTypeName, String targetTypeName) {
        String errorMsg = "'" + sourceTypeName + "' cannot be converted to '" + targetTypeName + "'";
        handleTypeConversionError(sf, errorRegIndex, errorMsg, sourceTypeName, targetTypeName);
    }

    private void handleTypeConversionError(StackFrame sf, int errorRegIndex, String errorMessage,
                                           String sourceTypeName, String targetTypeName) {
        BStruct errorVal;
        errorVal = BLangVMErrors.createTypeConversionError(context, ip, errorMessage, sourceTypeName, targetTypeName);
        if (errorRegIndex == -1) {
            context.setError(errorVal);
            handleError();
            return;
        }

        sf.refRegs[errorRegIndex] = errorVal;
    }

    private void createNewConnector(int[] operands, StackFrame sf) {
        int cpIndex = operands[0];
        int i = operands[1];
        StructureRefCPEntry structureRefCPEntry = (StructureRefCPEntry) constPool[cpIndex];
        ConnectorInfo connectorInfo = (ConnectorInfo) structureRefCPEntry.getStructureTypeInfo();
        BConnector bConnector = new BConnector(connectorInfo.getType());
        sf.refRegs[i] = bConnector;
    }

    private void createNewStruct(int[] operands, StackFrame sf) {
        int cpIndex = operands[0];
        int i = operands[1];
        StructureRefCPEntry structureRefCPEntry = (StructureRefCPEntry) constPool[cpIndex];
        StructInfo structInfo = (StructInfo) structureRefCPEntry.getStructureTypeInfo();
        BStruct bStruct = new BStruct(structInfo.getType());

        // Populate default values
        int longRegIndex = -1;
        int doubleRegIndex = -1;
        int stringRegIndex = -1;
        int booleanRegIndex = -1;
        for (StructFieldInfo fieldInfo : structInfo.getFieldInfoEntries()) {
            DefaultValueAttributeInfo defaultValueInfo =
                    (DefaultValueAttributeInfo) fieldInfo.getAttributeInfo(AttributeInfo.Kind.DEFAULT_VALUE_ATTRIBUTE);
            switch (fieldInfo.getFieldType().getTag()) {
                case TypeTags.INT_TAG:
                    longRegIndex++;
                    if (defaultValueInfo != null) {
                        bStruct.setIntField(longRegIndex, defaultValueInfo.getDefaultValue().getIntValue());
                    }
                    break;
                case TypeTags.FLOAT_TAG:
                    doubleRegIndex++;
                    if (defaultValueInfo != null) {
                        bStruct.setFloatField(doubleRegIndex, defaultValueInfo.getDefaultValue().getFloatValue());
                    }
                    break;
                case TypeTags.STRING_TAG:
                    stringRegIndex++;
                    if (defaultValueInfo != null) {
                        bStruct.setStringField(stringRegIndex, defaultValueInfo.getDefaultValue().getStringValue());
                    }
                    break;
                case TypeTags.BOOLEAN_TAG:
                    booleanRegIndex++;
                    if (defaultValueInfo != null) {
                        bStruct.setBooleanField(booleanRegIndex,
                                defaultValueInfo.getDefaultValue().getBooleanValue() ? 1 : 0);
                    }
                    break;
            }
        }

        sf.refRegs[i] = bStruct;
    }

    private void endTransaction(int status) {
        BallerinaTransactionManager ballerinaTransactionManager = context.getBallerinaTransactionManager();
        if (ballerinaTransactionManager != null) {
            if (status == 0) { //Transaction success
                ballerinaTransactionManager.commitTransactionBlock();
            } else if (status == -1) { //Transaction failed
                ballerinaTransactionManager.setTransactionError(true);
                ballerinaTransactionManager.rollbackTransactionBlock();
            } else { //status = 1 Transaction end
                ballerinaTransactionManager.endTransactionBlock();
                if (ballerinaTransactionManager.isOuterTransaction()) {
                    context.setBallerinaTransactionManager(null);
                }
            }
        }
    }

    private void beginTransaction(int transactionId, int retryCountAvailable) {
        //Transaction is attempted three times by default to improve resiliency
        int retryCount = 3;
        if (retryCountAvailable == 1) {
            retryCount = (int) controlStack.currentFrame.getLongRegs()[0];
            if (retryCount < 0) {
                throw BLangExceptionHelper.getRuntimeException(RuntimeErrors.INVALID_RETRY_COUNT);
            }
        }
        BallerinaTransactionManager ballerinaTransactionManager = context.getBallerinaTransactionManager();
        if (ballerinaTransactionManager == null) {
            ballerinaTransactionManager = new BallerinaTransactionManager();
            context.setBallerinaTransactionManager(ballerinaTransactionManager);
        }
        ballerinaTransactionManager.beginTransactionBlock(transactionId, retryCount);
    }

    private void retryTransaction(int transactionId, int startOfAbortIP) {
        BallerinaTransactionManager ballerinaTransactionManager = context.getBallerinaTransactionManager();
        int allowedRetryCount = ballerinaTransactionManager.getAllowedRetryCount(transactionId);
        int currentRetryCount = ballerinaTransactionManager.getCurrentRetryCount(transactionId);
        if (currentRetryCount >= allowedRetryCount) {
            if (currentRetryCount != 0) {
                ip = startOfAbortIP;
            }
        }
        ballerinaTransactionManager.incrementCurrentRetryCount(transactionId);
    }

    public void invokeCallableUnit(CallableUnitInfo callableUnitInfo, FunctionCallCPEntry funcCallCPEntry) {
        int[] argRegs = funcCallCPEntry.getArgRegs();
        BType[] paramTypes = callableUnitInfo.getParamTypes();
        StackFrame callerSF = controlStack.currentFrame;

        WorkerInfo defaultWorkerInfo = callableUnitInfo.getDefaultWorkerInfo();
        StackFrame calleeSF = new StackFrame(callableUnitInfo, defaultWorkerInfo, ip, funcCallCPEntry.getRetRegs());
        controlStack.pushFrame(calleeSF);

        // Copy arg values from the current StackFrame to the new StackFrame
        copyArgValues(callerSF, calleeSF, argRegs, paramTypes);

        // TODO Improve following two lines
        this.constPool = calleeSF.packageInfo.getConstPoolEntries();
        this.code = calleeSF.packageInfo.getInstructions();
        ip = defaultWorkerInfo.getCodeAttributeInfo().getCodeAddrs();

    }

    public void invokeAction(ActionInfo actionInfo, FunctionCallCPEntry funcCallCPEntry) {
        int[] argRegs = funcCallCPEntry.getArgRegs();
        StackFrame callerSF = controlStack.currentFrame;

        if (callerSF.refRegs[argRegs[0]] == null) {
            context.setError(BLangVMErrors.createNullRefError(this.context, ip));
            handleError();
            return;
        }
        BConnectorType actualCon = (BConnectorType) ((BConnector) callerSF.refRegs[argRegs[0]]).getConnectorType();
        //TODO find a way to change this to method table
        ActionInfo newActionInfo = programFile.getPackageInfo(actualCon.getPackagePath())
                .getConnectorInfo(actualCon.getName()).getActionInfo(actionInfo.getName());

        if (newActionInfo.getNativeAction() != null) {
            invokeNativeAction(newActionInfo, funcCallCPEntry);
        } else {
            invokeCallableUnit(newActionInfo, funcCallCPEntry);
        }
    }

    public void invokeWorker(WorkerDataChannelInfo workerDataChannel,
                             WrkrInteractionArgsCPEntry wrkrIntRefCPEntry) {
        StackFrame currentFrame = controlStack.currentFrame;

        // Extract the outgoing expressions
        BValue[] arguments = new BValue[wrkrIntRefCPEntry.getbTypes().length];
        copyArgValuesForWorkerInvoke(currentFrame, wrkrIntRefCPEntry.getArgRegs(),
                wrkrIntRefCPEntry.getbTypes(), arguments);

        //populateArgumentValuesForWorker(expressions, arguments);
        workerDataChannel.setTypes(wrkrIntRefCPEntry.getbTypes());
        workerDataChannel.putData(arguments);
    }

    public void invokeForkJoin(ForkJoinCPEntry forkJoinCPEntry) {
        ForkjoinInfo forkjoinInfo = forkJoinCPEntry.getForkjoinInfo();
        List<BLangVMWorkers.WorkerExecutor> workerRunnerList = new ArrayList<>();
        long timeout = Long.MAX_VALUE;
        if (forkjoinInfo.isTimeoutAvailable()) {
            timeout = this.controlStack.currentFrame.getLongRegs()[0];
        }
        Queue<WorkerResult> resultMsgs = new ConcurrentLinkedQueue<>();
        Map<String, BLangVMWorkers.WorkerExecutor> workers = new HashMap<>();
        for (WorkerInfo workerInfo : forkjoinInfo.getWorkerInfoMap().values()) {
            Context workerContext = new WorkerContext(this.programFile, context);
            workerContext.blockingInvocation = true;
            StackFrame callerSF = this.controlStack.currentFrame;
            int[] argRegs = forkjoinInfo.getArgRegs();
            ControlStackNew workerControlStack = workerContext.getControlStackNew();
            StackFrame calleeSF = new StackFrame(this.controlStack.currentFrame.getCallableUnitInfo(),
                    workerInfo, -1, new int[1]);
            workerControlStack.pushFrame(calleeSF);
            BLangVM.copyValuesForForkJoin(callerSF, calleeSF, argRegs);
            BLangVM bLangVM = new BLangVM(this.programFile);
            BLangVMWorkers.WorkerExecutor workerRunner = new BLangVMWorkers.WorkerExecutor(bLangVM,
                    workerContext, workerInfo, resultMsgs);
            workerRunnerList.add(workerRunner);
            workerContext.startTrackWorker();
            workers.put(workerInfo.getWorkerName(), workerRunner);
        }
        Set<String> joinWorkerNames = new LinkedHashSet<>(Lists.of(forkjoinInfo.getJoinWorkerNames()));
        if (joinWorkerNames.isEmpty()) {
            /* if no join workers are specified, that means, all should be considered */
            joinWorkerNames.addAll(workers.keySet());
        }
        int workerCount;
        if (forkjoinInfo.getJoinType().equalsIgnoreCase(JOIN_TYPE_SOME)) {
            workerCount = forkjoinInfo.getWorkerCount();
        } else {
            workerCount = joinWorkerNames.size();
        }
        boolean success = this.invokeJoinWorkers(workers, joinWorkerNames, workerCount, timeout);
        if (success) {
            this.ip = forkjoinInfo.getJoinIp();
            /* assign values to join block message arrays */
            int offsetJoin = forkjoinInfo.getJoinMemOffset();
            BMap<String, BRefValueArray> mbMap = new BMap<>();
            for (WorkerResult workerResult : resultMsgs) {
                mbMap.put(workerResult.getWorkerName(), workerResult.getResult());
            }
            this.controlStack.currentFrame.getRefLocalVars()[offsetJoin] = mbMap;
        } else {
            /* timed out */
            this.ip = forkjoinInfo.getTimeoutIp();
            /* execute the timeout block */
            int offsetTimeout = forkjoinInfo.getTimeoutMemOffset();
            BMap<String, BRefValueArray> mbMap = new BMap<>();
            for (WorkerResult workerResult : resultMsgs) {
                mbMap.put(workerResult.getWorkerName(), workerResult.getResult());
            }
            this.controlStack.currentFrame.getRefLocalVars()[offsetTimeout] = mbMap;
        }     
    }
    
    private boolean invokeJoinWorkers(Map<String, BLangVMWorkers.WorkerExecutor> workers, 
            Set<String> joinWorkerNames, int joinCount, long timeout) {
        ExecutorService exec = ThreadPoolFactory.getInstance().getWorkerExecutor();
        Semaphore resultCounter = new Semaphore(-joinCount + 1);
        workers.forEach((k, v) -> {
            if (joinWorkerNames.contains(k)) {
                v.setResultCounterSemaphore(resultCounter);
            }
            exec.submit(v);
        });
        try {
            return resultCounter.tryAcquire(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {
            return false;
        }
    }

    private void startWorkers() {
        CallableUnitInfo callableUnitInfo = this.controlStack.currentFrame.callableUnitInfo;
        BLangVMWorkers.invoke(programFile, callableUnitInfo, this.context);
    }

    private void handleWorkerReturn() {
        WorkerContext workerContext = (WorkerContext) this.context;
        if (workerContext.parentSF.tryReturn()) {
            StackFrame workerCallerSF = workerContext.getControlStackNew().currentFrame;
            workerContext.parentSF.returnedWorker = workerCallerSF.workerInfo.getWorkerName();

            ControlStackNew parentControlStack = workerContext.parent.getControlStackNew();
            StackFrame parentSF = workerContext.parentSF;
            StackFrame parentCallersSF = parentControlStack.currentFrame.prevStackFrame;

            copyWorkersReturnValues(workerCallerSF, parentSF, parentCallersSF);
            // Switch to parent context
            this.context = workerContext.parent;
            this.controlStack = this.context.getControlStackNew();
            controlStack.popFrame();
            this.constPool = this.controlStack.currentFrame.packageInfo.getConstPoolEntries();
            this.code = this.controlStack.currentFrame.packageInfo.getInstructions();
            ip = parentSF.retAddrs;
        } else {
            String msg = workerContext.parentSF.returnedWorker + " already returned.";
            context.setError(BLangVMErrors.createIllegalStateException(context, ip, msg));
            handleError();
        }
    }

    public void replyWorker(WorkerDataChannelInfo workerDataChannel,
                            WrkrInteractionArgsCPEntry wrkrIntCPEntry) {

        BValue[] passedInValues = (BValue[]) workerDataChannel.takeData();
        StackFrame currentFrame = controlStack.currentFrame;
        copyArgValuesForWorkerReply(currentFrame, wrkrIntCPEntry.getArgRegs(),
                wrkrIntCPEntry.getbTypes(), passedInValues);
    }

    public static void copyArgValuesForWorkerInvoke(StackFrame callerSF, int[] argRegs, BType[] paramTypes,
                                                    BValue[] arguments) {
        for (int i = 0; i < argRegs.length; i++) {
            BType paramType = paramTypes[i];
            int argReg = argRegs[i];
            switch (paramType.getTag()) {
                case TypeTags.INT_TAG:
                    arguments[i] = new BInteger(callerSF.longRegs[argReg]);
                    break;
                case TypeTags.FLOAT_TAG:
                    arguments[i] = new BFloat(callerSF.doubleRegs[argReg]);
                    break;
                case TypeTags.STRING_TAG:
                    arguments[i] = new BString(callerSF.stringRegs[argReg]);
                    break;
                case TypeTags.BOOLEAN_TAG:
                    boolean temp = (callerSF.intRegs[argReg]) > 0 ? true : false;
                    arguments[i] = new BBoolean(temp);
                    break;
                case TypeTags.BLOB_TAG:
                    arguments[i] = new BBlob(callerSF.byteRegs[argReg]);
                    break;
                default:
                    arguments[i] = callerSF.refRegs[argReg];
            }
        }
    }

    public static void copyArgValuesForWorkerReply(StackFrame currentSF, int[] argRegs, BType[] paramTypes,
                                                   BValue[] passedInValues) {
        int longRegIndex = -1;
        int doubleRegIndex = -1;
        int stringRegIndex = -1;
        int booleanRegIndex = -1;
        int blobRegIndex = -1;
        int refRegIndex = -1;

        for (int i = 0; i < argRegs.length; i++) {
            BType paramType = paramTypes[i];
            switch (paramType.getTag()) {
                case TypeTags.INT_TAG:
                    currentSF.getLongRegs()[++longRegIndex] = ((BInteger) passedInValues[i]).intValue();
                    break;
                case TypeTags.FLOAT_TAG:
                    currentSF.getDoubleRegs()[++doubleRegIndex] = ((BFloat) passedInValues[i]).floatValue();
                    break;
                case TypeTags.STRING_TAG:
                    currentSF.getStringRegs()[++stringRegIndex] = ((BString) passedInValues[i]).stringValue();
                    break;
                case TypeTags.BOOLEAN_TAG:
                    currentSF.getIntRegs()[++booleanRegIndex] = (((BBoolean) passedInValues[i]).booleanValue()) ? 1 : 0;
                    break;
                case TypeTags.BLOB_TAG:
                    currentSF.getByteRegs()[++blobRegIndex] = ((BBlob) passedInValues[i]).blobValue();
                    break;
                default:
                    currentSF.getRefRegs()[++refRegIndex] = (BRefType) passedInValues[i];
            }
        }
    }

    public static void copyValuesForForkJoin(StackFrame callerSF, StackFrame calleeSF, int[] argRegs) {
        int longLocalVals = argRegs[0];
        int doubleLocalVals = argRegs[1];
        int stringLocalVals = argRegs[2];
        int booleanLocalVals = argRegs[3];
        int blobLocalVals = argRegs[4];
        int refLocalVals = argRegs[5];

        for (int i = 0; i <= longLocalVals; i++) {
            calleeSF.getLongLocalVars()[i] = callerSF.getLongLocalVars()[i];
        }

        for (int i = 0; i <= doubleLocalVals; i++) {
            calleeSF.getDoubleLocalVars()[i] = callerSF.getDoubleLocalVars()[i];
        }

        for (int i = 0; i <= stringLocalVals; i++) {
            calleeSF.getStringLocalVars()[i] = callerSF.getStringLocalVars()[i];
        }

        for (int i = 0; i <= booleanLocalVals; i++) {
            calleeSF.getIntLocalVars()[i] = callerSF.getIntLocalVars()[i];
        }

        for (int i = 0; i <= refLocalVals; i++) {
            calleeSF.getRefLocalVars()[i] = callerSF.getRefLocalVars()[i];
        }

        for (int i = 0; i <= blobLocalVals; i++) {
            calleeSF.getByteLocalVars()[i] = callerSF.getByteLocalVars()[i];
        }


    }

    public static void copyValues(StackFrame parent, StackFrame workerSF) {
        System.arraycopy(parent.longLocalVars, 0, workerSF.longLocalVars, 0, parent.longLocalVars.length);
        System.arraycopy(parent.doubleLocalVars, 0, workerSF.doubleLocalVars, 0, parent.doubleLocalVars.length);
        System.arraycopy(parent.intLocalVars, 0, workerSF.intLocalVars, 0, parent.intLocalVars.length);
        System.arraycopy(parent.stringLocalVars, 0, workerSF.stringLocalVars, 0, parent.stringLocalVars.length);
        System.arraycopy(parent.byteLocalVars, 0, workerSF.byteLocalVars, 0, parent.byteLocalVars.length);
        System.arraycopy(parent.refLocalVars, 0, workerSF.refLocalVars, 0, parent.refLocalVars.length);
    }


    public static void copyArgValues(StackFrame callerSF, StackFrame calleeSF, int[] argRegs, BType[] paramTypes) {
        int longRegIndex = -1;
        int doubleRegIndex = -1;
        int stringRegIndex = -1;
        int booleanRegIndex = -1;
        int refRegIndex = -1;
        int blobRegIndex = -1;

        for (int i = 0; i < argRegs.length; i++) {
            BType paramType = paramTypes[i];
            int argReg = argRegs[i];
            switch (paramType.getTag()) {
                case TypeTags.INT_TAG:
                    calleeSF.longLocalVars[++longRegIndex] = callerSF.longRegs[argReg];
                    break;
                case TypeTags.FLOAT_TAG:
                    calleeSF.doubleLocalVars[++doubleRegIndex] = callerSF.doubleRegs[argReg];
                    break;
                case TypeTags.STRING_TAG:
                    calleeSF.stringLocalVars[++stringRegIndex] = callerSF.stringRegs[argReg];
                    break;
                case TypeTags.BOOLEAN_TAG:
                    calleeSF.intLocalVars[++booleanRegIndex] = callerSF.intRegs[argReg];
                    break;
                case TypeTags.BLOB_TAG:
                    calleeSF.byteLocalVars[++blobRegIndex] = callerSF.byteRegs[argReg];
                    break;
                default:
                    calleeSF.refLocalVars[++refRegIndex] = callerSF.refRegs[argReg];
            }
        }
    }

    private void handleReturn() {
        StackFrame currentSF = controlStack.popFrame();
        if (controlStack.currentFrame != null) {
            StackFrame callersSF = controlStack.currentFrame;
            // TODO Improve
            this.constPool = callersSF.packageInfo.getConstPoolEntries();
            this.code = callersSF.packageInfo.getInstructions();
        }
        ip = currentSF.retAddrs;
    }

    private void copyWorkersReturnValues(StackFrame workerCallerSF, StackFrame parentsSF, StackFrame parentCallersSF) {
        int callersRetRegIndex;
        int longRegCount = 0;
        int doubleRegCount = 0;
        int stringRegCount = 0;
        int intRegCount = 0;
        int refRegCount = 0;
        int byteRegCount = 0;
        BType[] retTypes = parentsSF.getCallableUnitInfo().getRetParamTypes();
        for (int i = 0; i < retTypes.length; i++) {
            BType retType = retTypes[i];
            callersRetRegIndex = parentsSF.retRegIndexes[i];
            switch (retType.getTag()) {
                case TypeTags.INT_TAG:
                    parentCallersSF.longRegs[callersRetRegIndex] = workerCallerSF.longRegs[longRegCount++];
                    break;
                case TypeTags.FLOAT_TAG:
                    parentCallersSF.doubleRegs[callersRetRegIndex] = workerCallerSF.doubleRegs[doubleRegCount++];
                    break;
                case TypeTags.STRING_TAG:
                    parentCallersSF.stringRegs[callersRetRegIndex] = workerCallerSF.stringRegs[stringRegCount++];
                    break;
                case TypeTags.BOOLEAN_TAG:
                    parentCallersSF.intRegs[callersRetRegIndex] = workerCallerSF.intRegs[intRegCount++];
                    break;
                case TypeTags.BLOB_TAG:
                    parentCallersSF.byteRegs[callersRetRegIndex] = workerCallerSF.byteRegs[byteRegCount++];
                    break;
                default:
                    parentCallersSF.refRegs[callersRetRegIndex] = workerCallerSF.refRegs[refRegCount++];
                    break;
            }
        }
    }

    private String getOperandsLine(int[] operands) {
        if (operands.length == 0) {
            return "";
        }

        if (operands.length == 1) {
            return "" + operands[0];
        }

        StringBuilder sb = new StringBuilder();
        sb.append(operands[0]);
        for (int i = 1; i < operands.length; i++) {
            sb.append(" ");
            sb.append(operands[i]);
        }
        return sb.toString();
    }

    private void invokeNativeFunction(FunctionInfo functionInfo, FunctionCallCPEntry funcCallCPEntry) {
        StackFrame callerSF = controlStack.currentFrame;

        // TODO : Remove once we handle this properly for return values
        BType[] retTypes = functionInfo.getRetParamTypes();
        BValue[] returnValues = new BValue[retTypes.length];

        StackFrame caleeSF = new StackFrame(functionInfo, functionInfo.getDefaultWorkerInfo(), ip, null, returnValues);
        copyArgValues(callerSF, caleeSF, funcCallCPEntry.getArgRegs(),
                functionInfo.getParamTypes());

        controlStack.pushFrame(caleeSF);

        // Invoke Native function;
        AbstractNativeFunction nativeFunction = functionInfo.getNativeFunction();
        try {
            nativeFunction.executeNative(context);
        } catch (Throwable e) {
            context.setError(BLangVMErrors.createError(this.context, ip, e.getMessage()));
            handleError();
            return;
        }
        // Copy return values to the callers stack
        controlStack.popFrame();
        handleReturnFromNativeCallableUnit(callerSF, funcCallCPEntry.getRetRegs(), returnValues, retTypes);
    }

    private void invokeNativeAction(ActionInfo actionInfo, FunctionCallCPEntry funcCallCPEntry) {
        StackFrame callerSF = controlStack.currentFrame;

        WorkerInfo defaultWorkerInfo = actionInfo.getDefaultWorkerInfo();
        AbstractNativeAction nativeAction = actionInfo.getNativeAction();

        if (nativeAction == null) {
            return;
        }

        // TODO : Remove once we handle this properly for return values
        BType[] retTypes = actionInfo.getRetParamTypes();
        BValue[] returnValues = new BValue[retTypes.length];

        StackFrame caleeSF = new StackFrame(actionInfo, defaultWorkerInfo, ip, null, returnValues);
        copyArgValues(callerSF, caleeSF, funcCallCPEntry.getArgRegs(),
                actionInfo.getParamTypes());


        controlStack.pushFrame(caleeSF);

        try {
            boolean nonBlocking = !context.isInTransaction() && nativeAction.isNonBlockingAction() &&
                    !context.blockingInvocation;
            BClientConnectorFutureListener listener = new BClientConnectorFutureListener(context, nonBlocking);
            if (nonBlocking) {
                // Enable non-blocking.
                context.setStartIP(ip);
                // TODO : Temporary solution to make non-blocking working.
                if (caleeSF.packageInfo == null) {
                    caleeSF.packageInfo = actionInfo.getPackageInfo();
                }
                context.programFile = programFile;
                context.funcCallCPEntry = funcCallCPEntry;
                context.actionInfo = actionInfo;

                ConnectorFuture future = nativeAction.execute(context);
                if (future == null) {
                    throw new BallerinaException("Native action doesn't provide a future object to sync");
                }
                future.setConnectorFutureListener(listener);

                ip = -1;
                return;
            } else {
                ConnectorFuture future = nativeAction.execute(context);
                if (future == null) {
                    throw new BallerinaException("Native action doesn't provide a future object to sync");
                }
                future.setConnectorFutureListener(listener);
                //default nonBlocking timeout 5 mins
                long timeout = 300000;
                boolean res = listener.sync(timeout);
                if (!res) {
                    //non blocking execution timed out.
                    throw new BallerinaException("Action execution timed out, timeout period - " + timeout
                            + ", Action - " + nativeAction.getPackagePath() + ":" + nativeAction.getName());
                }
                if (context.getError() != null) {
                    handleError();
                }
                // Copy return values to the callers stack
                controlStack.popFrame();
                handleReturnFromNativeCallableUnit(callerSF, funcCallCPEntry.getRetRegs(), returnValues, retTypes);

            }
        } catch (Throwable e) {
            context.setError(BLangVMErrors.createError(this.context, ip, e.getMessage()));
            handleError();
            return;
        }
    }

    public static void handleReturnFromNativeCallableUnit(StackFrame callerSF, int[] returnRegIndexes,
                                                          BValue[] returnValues, BType[] retTypes) {
        for (int i = 0; i < returnValues.length; i++) {
            int callersRetRegIndex = returnRegIndexes[i];
            BType retType = retTypes[i];
            switch (retType.getTag()) {
                case TypeTags.INT_TAG:
                    if (returnValues[i] == null) {
                        callerSF.longRegs[callersRetRegIndex] = 0;
                        break;
                    }
                    callerSF.longRegs[callersRetRegIndex] = ((BInteger) returnValues[i]).intValue();
                    break;
                case TypeTags.FLOAT_TAG:
                    if (returnValues[i] == null) {
                        callerSF.doubleRegs[callersRetRegIndex] = 0;
                        break;
                    }
                    callerSF.doubleRegs[callersRetRegIndex] = ((BFloat) returnValues[i]).floatValue();
                    break;
                case TypeTags.STRING_TAG:
                    if (returnValues[i] == null) {
                        callerSF.stringRegs[callersRetRegIndex] = "";
                        break;
                    }
                    callerSF.stringRegs[callersRetRegIndex] = returnValues[i].stringValue();
                    break;
                case TypeTags.BOOLEAN_TAG:
                    if (returnValues[i] == null) {
                        callerSF.intRegs[callersRetRegIndex] = 0;
                        break;
                    }
                    callerSF.intRegs[callersRetRegIndex] = ((BBoolean) returnValues[i]).booleanValue() ? 1 : 0;
                    break;
                case TypeTags.BLOB_TAG:
                    if (returnValues[i] == null) {
                        callerSF.byteRegs[callersRetRegIndex] = new byte[0];
                        break;
                    }
                    callerSF.byteRegs[callersRetRegIndex] = ((BBlob) returnValues[i]).blobValue();
                    break;
                default:
                    callerSF.refRegs[callersRetRegIndex] = (BRefType) returnValues[i];
            }
        }
    }

    private boolean checkConstraintJSONCast(BType targetType, BRefType value) {
        if (checkConstraintJSONEquivalency((BJSON) value,
                (BStructType) ((BJSONConstraintType) targetType).getConstraint())) {
            return true;
        }
        return false;
    }

    private boolean checkCast(BValue sourceValue, BType targetType) {
        BType sourceType = sourceValue.getType();

        if (sourceType.equals(targetType)) {
            return true;
        }

        if (sourceType.getTag() == TypeTags.STRUCT_TAG && targetType.getTag() == TypeTags.STRUCT_TAG) {
            return checkStructEquivalency((BStructType) sourceType, (BStructType) targetType);

        }

        if (targetType.getTag() == TypeTags.ANY_TAG) {
            return true;
        }

        if (targetType.getTag() == TypeTags.C_JSON_TAG &&
                sourceValue.getType().getTag() == TypeTags.JSON_TAG) {
            return checkConstraintJSONCast(targetType, (BRefType) sourceValue);
        }

        // Check JSON casting
        if (getElementType(sourceType).getTag() == TypeTags.JSON_TAG) {
            return JSONUtils.checkJSONCast(((BJSON) sourceValue).value(), targetType);
        }

        // Array casting
        if (targetType.getTag() == TypeTags.ARRAY_TAG || sourceType.getTag() == TypeTags.ARRAY_TAG) {
            return checkArrayCast(sourceType, targetType);
        }

        return false;
    }

    private boolean checkArrayCast(BType sourceType, BType targetType) {
        if (targetType.getTag() == TypeTags.ARRAY_TAG && sourceType.getTag() == TypeTags.ARRAY_TAG) {
            BArrayType sourceArrayType = (BArrayType) sourceType;
            BArrayType targetArrayType = (BArrayType) targetType;
            if (targetArrayType.getDimensions() > sourceArrayType.getDimensions()) {
                return false;
            }

            return checkArrayCast(sourceArrayType.getElementType(), targetArrayType.getElementType());
        } else if (sourceType.getTag() == TypeTags.ARRAY_TAG) {
            return targetType.getTag() == TypeTags.ANY_TAG;
        }

        return sourceType.equals(targetType);
    }

    private BType getElementType(BType type) {
        if (type.getTag() != TypeTags.ARRAY_TAG) {
            return type;
        }

        return getElementType(((BArrayType) type).getElementType());
    }

    public static boolean checkStructEquivalency(BStructType sourceType, BStructType targetType) {
        // Struct Type equivalency
        BStructType.StructField[] sFields = sourceType.getStructFields();
        BStructType.StructField[] tFields = targetType.getStructFields();

        if (tFields.length > sFields.length) {
            return false;
        }

        for (int i = 0; i < tFields.length; i++) {
            if (isAssignable(tFields[i].getFieldType(), sFields[i].getFieldType()) &&
                    tFields[i].getFieldName().equals(sFields[i].getFieldName())) {
                continue;
            }
            return false;
        }

        return true;
    }

    private static boolean isAssignable(BType actualType, BType expType) {
        // First check whether both references points to the same object.
        if (actualType == expType) {
            return true;
        }

        // If the both type tags are equal, then perform following checks.
        if (actualType.getTag() == expType.getTag() && isValueType(actualType)) {
            return true;
        } else if (actualType.getTag() == expType.getTag() &&
                !isUserDefinedType(actualType) && !isConstrainedType(actualType)) {
            return true;
        } else if (actualType.getTag() == expType.getTag() && actualType.getTag() == TypeTags.ARRAY_TAG) {
            return checkArrayEquivalent(actualType, expType);
        } else if (actualType.getTag() == expType.getTag() && actualType.getTag() == TypeTags.STRUCT_TAG &&
                checkStructEquivalency((BStructType) actualType, (BStructType) expType)) {
            // If both types are structs then check for their equivalency
            return true;
        }
        return false;
    }

    private static boolean isValueType(BType type) {
        return type.getTag() <= TypeTags.BLOB_TAG;
    }

    private static boolean isUserDefinedType(BType type) {
        return type.getTag() == TypeTags.STRUCT_TAG || type.getTag() == TypeTags.CONNECTOR_TAG ||
                type.getTag() == TypeTags.ENUM_TAG || type.getTag() == TypeTags.ARRAY_TAG;
    }

    private static boolean isConstrainedType(BType type) {
        return type.getTag() == TypeTags.JSON_TAG;
    }

    private static boolean checkArrayEquivalent(BType actualType, BType expType) {
        if (expType.getTag() == TypeTags.ARRAY_TAG && actualType.getTag() == TypeTags.ARRAY_TAG) {
            // Both types are array types
            BArrayType lhrArrayType = (BArrayType) expType;
            BArrayType rhsArrayType = (BArrayType) actualType;
            return checkArrayEquivalent(lhrArrayType.getElementType(), rhsArrayType.getElementType());
        }
        // Now one or both types are not array types and they have to be equal
        if (expType == actualType) {
            return true;
        }
        return false;
    }

    private void convertJSONToInt(int[] operands, StackFrame sf) {
        int i = operands[0];
        int j = operands[1];
        int k = operands[2];

        BJSON jsonValue = (BJSON) sf.refRegs[i];
        if (jsonValue == null) {
            handleNullRefError();
            return;
        }

        JsonNode jsonNode;
        try {
            jsonNode = jsonValue.value();
        } catch (BallerinaException e) {
            String errorMsg = BLangExceptionHelper.getErrorMessage(RuntimeErrors.CASTING_FAILED_WITH_CAUSE,
                    BTypes.typeJSON, BTypes.typeInt, e.getMessage());
            context.setError(BLangVMErrors.createError(context, ip, errorMsg));
            handleError();
            return;
        }

        if (jsonNode.isInt() || jsonNode.isLong()) {
            sf.longRegs[j] = jsonNode.longValue();
            return;
        }

        sf.longRegs[j] = 0;
        handleTypeConversionError(sf, k, JSONUtils.getTypeName(jsonNode), TypeConstants.INT_TNAME);
    }

    private void convertJSONToFloat(int[] operands, StackFrame sf) {
        int i = operands[0];
        int j = operands[1];
        int k = operands[2];

        BJSON jsonValue = (BJSON) sf.refRegs[i];
        if (jsonValue == null) {
            handleNullRefError();
            return;
        }

        JsonNode jsonNode;
        try {
            jsonNode = jsonValue.value();
        } catch (BallerinaException e) {
            String errorMsg = BLangExceptionHelper.getErrorMessage(RuntimeErrors.CASTING_FAILED_WITH_CAUSE,
                    BTypes.typeJSON, BTypes.typeFloat, e.getMessage());
            context.setError(BLangVMErrors.createError(context, ip, errorMsg));
            handleError();
            return;
        }

        if (jsonNode.isFloat() || jsonNode.isDouble()) {
            sf.doubleRegs[j] = jsonNode.doubleValue();
            return;
        }

        sf.doubleRegs[j] = 0;
        handleTypeConversionError(sf, k, JSONUtils.getTypeName(jsonNode), TypeConstants.FLOAT_TNAME);
    }

    private void convertJSONToString(int[] operands, StackFrame sf) {
        int i = operands[0];
        int j = operands[1];
        int k = operands[2];

        BJSON jsonValue = (BJSON) sf.refRegs[i];
        if (jsonValue == null) {
            handleNullRefError();
            return;
        }

        JsonNode jsonNode;
        try {
            jsonNode = jsonValue.value();
        } catch (BallerinaException e) {
            sf.stringRegs[j] = "";
            String errorMsg = BLangExceptionHelper.getErrorMessage(RuntimeErrors.CASTING_FAILED_WITH_CAUSE,
                    BTypes.typeJSON, BTypes.typeString, e.getMessage());
            context.setError(BLangVMErrors.createError(context, ip, errorMsg));
            handleError();
            return;
        }

        if (jsonNode.isTextual()) {
            sf.stringRegs[j] = jsonNode.textValue();
            return;
        }

        sf.stringRegs[j] = "";
        handleTypeConversionError(sf, k, JSONUtils.getTypeName(jsonNode), TypeConstants.STRING_TNAME);
    }

    private void convertJSONToBoolean(int[] operands, StackFrame sf) {
        int i = operands[0];
        int j = operands[1];
        int k = operands[2];

        BJSON jsonValue = (BJSON) sf.refRegs[i];
        if (jsonValue == null) {
            handleNullRefError();
            return;
        }

        JsonNode jsonNode;
        try {
            jsonNode = jsonValue.value();
        } catch (BallerinaException e) {
            String errorMsg = BLangExceptionHelper.getErrorMessage(RuntimeErrors.CASTING_FAILED_WITH_CAUSE,
                    BTypes.typeJSON, BTypes.typeBoolean, e.getMessage());
            context.setError(BLangVMErrors.createError(context, ip, errorMsg));
            handleError();
            return;
        }

        if (jsonNode.isBoolean()) {
            sf.intRegs[j] = jsonNode.booleanValue() ? 1 : 0;
            return;
        }

        // Reset the value in the case of an error;
        sf.intRegs[j] = 0;
        handleTypeConversionError(sf, k, JSONUtils.getTypeName(jsonNode), TypeConstants.BOOLEAN_TNAME);
    }

    private void convertStructToMap(int[] operands, StackFrame sf) {
        int i = operands[0];
        int j = operands[1];

        BStruct bStruct = (BStruct) sf.refRegs[i];
        if (bStruct == null) {
            sf.refRegs[j] = null;
            return;
        }

        int longRegIndex = -1;
        int doubleRegIndex = -1;
        int stringRegIndex = -1;
        int booleanRegIndex = -1;
        int blobRegIndex = -1;
        int refRegIndex = -1;

        BStructType.StructField[] structFields = ((BStructType) bStruct.getType()).getStructFields();
        BMap<String, BValue> map = BTypes.typeMap.getEmptyValue();
        for (BStructType.StructField structField : structFields) {
            String key = structField.getFieldName();
            BType fieldType = structField.getFieldType();
            switch (fieldType.getTag()) {
                case TypeTags.INT_TAG:
                    map.put(key, new BInteger(bStruct.getIntField(++longRegIndex)));
                    break;
                case TypeTags.FLOAT_TAG:
                    map.put(key, new BFloat(bStruct.getFloatField(++doubleRegIndex)));
                    break;
                case TypeTags.STRING_TAG:
                    map.put(key, new BString(bStruct.getStringField(++stringRegIndex)));
                    break;
                case TypeTags.BOOLEAN_TAG:
                    map.put(key, new BBoolean(bStruct.getBooleanField(++booleanRegIndex) == 1));
                    break;
                case TypeTags.BLOB_TAG:
                    map.put(key, new BBlob(bStruct.getBlobField(++blobRegIndex)));
                    break;
                default:
                    BValue value = bStruct.getRefField(++refRegIndex);
                    map.put(key, value == null ? null : value.copy());
            }
        }

        sf.refRegs[j] = map;
    }

    private void convertStructToJSON(int[] operands, StackFrame sf) {
        int i = operands[0];
        int j = operands[1];
        int k = operands[2];

        BStruct bStruct = (BStruct) sf.refRegs[i];
        if (bStruct == null) {
            sf.refRegs[j] = null;
            return;
        }

        try {
            sf.refRegs[j] = JSONUtils.convertStructToJSON(bStruct);
        } catch (Exception e) {
            sf.refRegs[j] = null;
            String errorMsg = "cannot convert '" + bStruct.getType() + "' to type '" + BTypes.typeJSON + "': " +
                    e.getMessage();
            handleTypeConversionError(sf, k, errorMsg, bStruct.getType().toString(), TypeConstants.JSON_TNAME);
        }
    }

    private void convertMapToStruct(int[] operands, StackFrame sf) {
        int i = operands[0];
        int cpIndex = operands[1];
        int j = operands[2];
        int k = operands[3];

        TypeRefCPEntry typeRefCPEntry = (TypeRefCPEntry) constPool[cpIndex];
        BMap<String, BValue> bMap = (BMap<String, BValue>) sf.refRegs[i];
        if (bMap == null) {
            sf.refRegs[j] = null;
            return;
        }

        int longRegIndex = -1;
        int doubleRegIndex = -1;
        int stringRegIndex = -1;
        int booleanRegIndex = -1;
        int blobRegIndex = -1;
        int refRegIndex = -1;
        BStructType structType = (BStructType) typeRefCPEntry.getType();
        BStruct bStruct = new BStruct(structType);
        StructInfo structInfo = sf.packageInfo.getStructInfo(structType.getName());

        Set<String> keys = bMap.keySet();
        for (StructFieldInfo fieldInfo : structInfo.getFieldInfoEntries()) {
            String key = fieldInfo.getName();
            BType fieldType = fieldInfo.getFieldType();
            BValue mapVal = null;
            try {
                boolean containsField = keys.contains(key);
                DefaultValueAttributeInfo defaultValAttrInfo = null;
                if (containsField) {
                    mapVal = bMap.get(key);
                    if (mapVal == null && BTypes.isValueType(fieldType)) {
                        throw BLangExceptionHelper.getRuntimeException(
                                RuntimeErrors.INCOMPATIBLE_FIELD_TYPE_FOR_CASTING, key, fieldType, null);
                    }

                    if (mapVal != null && !checkCast(mapVal, fieldType)) {
                        throw BLangExceptionHelper.getRuntimeException(
                                RuntimeErrors.INCOMPATIBLE_FIELD_TYPE_FOR_CASTING, key, fieldType, mapVal.getType());
                    }
                } else {
                    defaultValAttrInfo = (DefaultValueAttributeInfo) getAttributeInfo(fieldInfo,
                            AttributeInfo.Kind.DEFAULT_VALUE_ATTRIBUTE);
                }

                switch (fieldType.getTag()) {
                    case TypeTags.INT_TAG:
                        longRegIndex++;
                        if (containsField) {
                            bStruct.setIntField(longRegIndex, ((BInteger) mapVal).intValue());
                        } else if (defaultValAttrInfo != null) {
                            bStruct.setIntField(longRegIndex, defaultValAttrInfo.getDefaultValue().getIntValue());
                        }
                        break;
                    case TypeTags.FLOAT_TAG:
                        doubleRegIndex++;
                        if (containsField) {
                            bStruct.setFloatField(doubleRegIndex, ((BFloat) mapVal).floatValue());
                        } else if (defaultValAttrInfo != null) {
                            bStruct.setFloatField(doubleRegIndex, defaultValAttrInfo.getDefaultValue().getFloatValue());
                        }
                        break;
                    case TypeTags.STRING_TAG:
                        stringRegIndex++;
                        if (containsField) {
                            bStruct.setStringField(stringRegIndex, ((BString) mapVal).stringValue());
                        } else if (defaultValAttrInfo != null) {
                            bStruct.setStringField(stringRegIndex,
                                    defaultValAttrInfo.getDefaultValue().getStringValue());
                        }
                        break;
                    case TypeTags.BOOLEAN_TAG:
                        booleanRegIndex++;
                        if (containsField) {
                            bStruct.setBooleanField(booleanRegIndex, ((BBoolean) mapVal).booleanValue() ? 1 : 0);
                        } else if (defaultValAttrInfo != null) {
                            bStruct.setBooleanField(booleanRegIndex,
                                    defaultValAttrInfo.getDefaultValue().getBooleanValue() ? 1 : 0);
                        }
                        break;
                    case TypeTags.BLOB_TAG:
                        blobRegIndex++;
                        if (containsField && mapVal != null) {
                            bStruct.setBlobField(blobRegIndex, ((BBlob) mapVal).blobValue());
                        }
                        break;
                    default:
                        bStruct.setRefField(++refRegIndex, (BRefType) mapVal);
                }
            } catch (BallerinaException e) {
                sf.refRegs[j] = null;
                String errorMsg = "cannot convert '" + bMap.getType() + "' to type '" + structType + ": " +
                        e.getMessage();
                handleTypeConversionError(sf, k, errorMsg, TypeConstants.MAP_TNAME, structType.toString());
                return;
            }
        }

        sf.refRegs[j] = bStruct;
    }

    private void convertJSONToStruct(int[] operands, StackFrame sf) {
        int i = operands[0];
        int cpIndex = operands[1];
        int j = operands[2];
        int k = operands[3];

        TypeRefCPEntry typeRefCPEntry = (TypeRefCPEntry) constPool[cpIndex];
        BJSON bjson = (BJSON) sf.refRegs[i];
        if (bjson == null) {
            sf.refRegs[j] = null;
            return;
        }

        try {
            sf.refRegs[j] = JSONUtils.convertJSONToStruct(bjson, (BStructType) typeRefCPEntry.getType(),
                    sf.packageInfo);
        } catch (Exception e) {
            sf.refRegs[j] = null;
            String errorMsg = "cannot convert '" + TypeConstants.JSON_TNAME + "' to type '" +
                    typeRefCPEntry.getType() + "': " + e.getMessage();
            handleTypeConversionError(sf, k, errorMsg, TypeConstants.JSON_TNAME, typeRefCPEntry.getType().toString());
        }
    }

    private void handleNullRefError() {
        context.setError(BLangVMErrors.createNullRefError(context, ip));
        handleError();
    }

    private void handleError() {
        int currentIP = ip - 1;
        StackFrame currentFrame = controlStack.currentFrame;
        ErrorTableEntry match = null;

        while (controlStack.currentFrame != null) {
            match = ErrorTableEntry.getMatch(currentFrame.packageInfo, currentIP, context.getError());
            if (match != null) {
                break;
            }

            controlStack.popFrame();
            context.setError(currentFrame.errorThrown);
            if (controlStack.currentFrame == null) {
                break;
            }

            currentIP = currentFrame.retAddrs - 1;
            currentFrame = controlStack.currentFrame;
        }

        if (controlStack.currentFrame == null) {
            // root level error handling.
            ip = -1;
            if (context.getServiceInfo() == null) {
                return;
            }

            BServerConnectorFuture connectorFuture = context.getConnectorFuture();
            try {
                connectorFuture.notifyFailure(new BallerinaException(BLangVMErrors
                        .getPrintableStackTrace(context.getError())));
            } catch (Exception e) {
                logger.error("cannot handle error using the error handler: " + e.getMessage(), e);
            }
            return;
        }

        // match should be not null at this point.
        if (match != null) {
            PackageInfo packageInfo = currentFrame.packageInfo;
            this.constPool = packageInfo.getConstPoolEntries();
            this.code = packageInfo.getInstructions();
            ip = match.getIpTarget();
            return;
        }

        ip = -1;
        logger.error("fatal error. incorrect error table entry.");
    }

    private AttributeInfo getAttributeInfo(AttributeInfoPool attrInfoPool, AttributeInfo.Kind attrInfoKind) {
        for (AttributeInfo attributeInfo : attrInfoPool.getAttributeInfoEntries()) {
            if (attributeInfo.getKind() == attrInfoKind) {
                return attributeInfo;
            }
        }
        return null;
    }

    private boolean isWaitingOnNonBlockingAction() {
        return context.actionInfo != null;
    }
}
