package dev.autoauto.model.bytecode;

import dev.autoauto.model.programtypes.BytecodeEvaluationProgram;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoTable;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;

import java.util.Stack;

public class construct_table_Bytecode extends AutoautoBytecode {

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoValue> stack, Stack<Integer> callStack) {
        int numKeys = stack.pop().castToNumber().getInt();

        AutoautoTable tbl = new AutoautoTable();

        for(int i = 0; i < numKeys; i++) {
            AutoautoValue v = stack.pop();
            AutoautoValue k = stack.pop();
            tbl.setProperty(k, v);
        }

        stack.push(tbl);
    }
}
