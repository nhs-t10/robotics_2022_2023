package dev.autoauto.model.bytecode;

import dev.autoauto.model.programtypes.BytecodeEvaluationProgram;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;

import java.util.Stack;

public class ret_Bytecode extends AutoautoBytecode {

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoValue> stack, Stack<Integer> callStack) {
        bytecodeEvaluationProgram.pc = callStack.peek() - 1;
        bytecodeEvaluationProgram.yield = true;
    }
}
