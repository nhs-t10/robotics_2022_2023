package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.bytecode;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.programtypes.BytecodeEvaluationProgram;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoTable;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;

import java.util.Stack;

public class construct_table_Bytecode extends AutoautoBytecode {

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoPrimitive> stack, Stack<Integer> callStack) {
        int numKeys = stack.pop().castToNumber().getInt();

        AutoautoTable tbl = new AutoautoTable();

        for(int i = 0; i < numKeys; i++) {
            AutoautoPrimitive v = stack.pop();
            AutoautoPrimitive k = stack.pop();
            tbl.setProperty(k, v);
        }

        stack.push(tbl);
    }
}
