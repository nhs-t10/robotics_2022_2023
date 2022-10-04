package dev.autoauto.model.bytecode;

import dev.autoauto.model.programtypes.BytecodeEvaluationProgram;
import dev.autoauto.runtime.values.AutoautoCallableValue;
import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUnitValue;
import dev.autoauto.runtime.values.prototype.universal.NoopFunction;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;
import dev.autoauto.runtime.AutoautoSystemVariableNames;
import dev.autoauto.runtime.errors.AutoautoNameException;

import java.util.Stack;

public class unit_currentv_Bytecode extends AutoautoBytecode {

    private boolean inited;
    private AutoautoCallableValue getCmFunction;
    private AutoautoCallableValue getDegsFunction;

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoValue> stack, Stack<Integer> callStack) {

        if(!inited) init(scope);

        AutoautoValue p = stack.pop();
        if(p instanceof AutoautoUnitValue) {
            AutoautoUnitValue uv = (AutoautoUnitValue)p;
            switch(uv.unit) {
                case "ms": stack.push(new AutoautoNumericValue(System.currentTimeMillis()));
                break;
                case "cm": stack.push(getCmFunction.call(uv, AutoautoValue.EMPTY_ARRAY));
                break;
                case "degs": stack.push(getDegsFunction.call(uv, AutoautoValue.EMPTY_ARRAY));
                break;
                default: throw new AutoautoNameException("Unknown unit type '" + uv.unit + "'");
            }
        } else {
            stack.push(new AutoautoNumericValue(0));
        }
    }

    private void init(AutoautoRuntimeVariableScope scope) {
        getCmFunction = (AutoautoCallableValue) scope.get(AutoautoSystemVariableNames.GET_CENTIMETERS_FUNCTION_NAME);
        if(getCmFunction == null) getCmFunction = new NoopFunction();

        getDegsFunction = (AutoautoCallableValue) scope.get(AutoautoSystemVariableNames.GET_DEGREES_FUNCTION_NAME);
        if(getDegsFunction == null) getDegsFunction = new NoopFunction();

        inited = true;
    }
}
