package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.bytecode;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.programtypes.BytecodeEvaluationProgram;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;

import java.util.Stack;

public class yieldto_i_Bytecode extends AutoautoBytecode {

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoPrimitive> stack, Stack<Integer> callStack) {
        int i = stack.pop().castToNumber().getInt();
        bytecodeEvaluationProgram.pc = i - 1;
        bytecodeEvaluationProgram.yield = true;
    }
}
