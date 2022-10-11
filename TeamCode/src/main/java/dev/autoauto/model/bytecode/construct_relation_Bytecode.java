package dev.autoauto.model.bytecode;

import dev.autoauto.model.programtypes.BytecodeEvaluationProgram;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoRelation;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;

import java.util.Stack;

public class construct_relation_Bytecode extends AutoautoBytecode {

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoValue> stack, Stack<Integer> callStack) {

        AutoautoValue value = stack.pop();
        AutoautoValue title = stack.pop();

        stack.push(new AutoautoRelation(title, value));
    }
}
