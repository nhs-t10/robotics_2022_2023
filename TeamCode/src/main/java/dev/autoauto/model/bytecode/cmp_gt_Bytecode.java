package dev.autoauto.model.bytecode;

import dev.autoauto.model.operators.AutoautoOperation;
import dev.autoauto.model.operators.HasAutoautoGreaterThanOperator;
import dev.autoauto.model.operators.HasAutoautoOperatorInterface;
import dev.autoauto.model.programtypes.BytecodeEvaluationProgram;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;

import java.util.Stack;

public class cmp_gt_Bytecode extends AutoautoBytecode {
    private static final Class<? extends HasAutoautoOperatorInterface> operatorClass = HasAutoautoGreaterThanOperator.class;

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoValue> stack, Stack<Integer> callStack) {

        AutoautoValue right = stack.pop();
        AutoautoValue left = stack.pop();

        stack.push(AutoautoOperation.invokeOperation(left, right, operatorClass, ">"));
    }
}