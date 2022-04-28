package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.bytecode;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.programtypes.BytecodeEvaluationProgram;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoCallableValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

import java.util.List;
import java.util.Arrays;
import java.util.Stack;

public class callfunction_Bytecode extends AutoautoBytecode {

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoPrimitive> stack, Stack<Integer> callStack) {


        int numNamedArgs = stack.pop().castToNumber().getInt();
        String[] argnames = new String[numNamedArgs];
        AutoautoPrimitive[] namedargs = new AutoautoPrimitive[numNamedArgs];
        for(int i = numNamedArgs - 1; i>=0; i--) {
            namedargs[i] = stack.pop();
            argnames[i] = stack.pop().getString();
        }

        int numPosArgs = stack.pop().castToNumber().getInt();
        AutoautoPrimitive[] posargs = new AutoautoPrimitive[numPosArgs];
        for(int i = numPosArgs - 1; i>=0; i--) posargs[i] = stack.pop();

        AutoautoPrimitive func = stack.pop();

        callStack.push(bytecodeEvaluationProgram.pc);

        if(func instanceof AutoautoCallableValue) {
            AutoautoCallableValue fFunc = (AutoautoCallableValue)func;
            AutoautoPrimitive[] finalArgs = mergeArgs(fFunc.getArgNames(), namedargs, argnames, posargs);
            stack.push(fFunc.call(bytecodeEvaluationProgram.lastThisContext, finalArgs));
        } else {
            stack.push(new AutoautoUndefined());
        }

        callStack.pop();
    }

    private AutoautoPrimitive[] mergeArgs(String[] cannonicalArgNames, AutoautoPrimitive[] namedargs, String[] namedArgNames, AutoautoPrimitive[] positionalArgs) {
        AutoautoPrimitive[] finalArgs = new AutoautoPrimitive[Math.max(cannonicalArgNames.length, positionalArgs.length)];
        for(int i = 0; i < finalArgs.length; i++) {
            if(i < positionalArgs.length) finalArgs[i] = positionalArgs[i];
            else finalArgs[i] = new AutoautoUndefined();
        }
        List<String> cArgNamesList = Arrays.asList(cannonicalArgNames);
        for(int i = 0; i < namedArgNames.length; i++) {
            int cI = cArgNamesList.indexOf(namedArgNames[i]);
            if(cI != -1) finalArgs[cI] = namedargs[i];
        }

        return finalArgs;
    }
}
