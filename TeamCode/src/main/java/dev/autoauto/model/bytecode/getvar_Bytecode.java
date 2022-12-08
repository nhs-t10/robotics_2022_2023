package dev.autoauto.model.bytecode;

import dev.autoauto.model.programtypes.BytecodeEvaluationProgram;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;

import java.util.Stack;

public class getvar_Bytecode extends AutoautoBytecode {

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoValue> stack, Stack<Integer> callStack) {
        AutoautoValue p = scope.get(stack.pop().getString());
        if(p == null) p = new AutoautoUndefined();
        stack.push(p);
    }
}
