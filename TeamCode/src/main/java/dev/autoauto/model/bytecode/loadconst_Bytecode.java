package dev.autoauto.model.bytecode;

import dev.autoauto.model.programtypes.BytecodeEvaluationProgram;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;

import java.util.Stack;

public class loadconst_Bytecode extends AutoautoBytecode {
    private final AutoautoValue constant;

    public loadconst_Bytecode(AutoautoValue constant) {
        this.constant = constant;
    }

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoValue> stack, Stack<Integer> callStack) {
        stack.push(constant);
    }

    public String toString() {
        return "loadconst " + constant.toString();
    }
}
