package dev.autoauto.model.bytecode;

import dev.autoauto.model.programtypes.BytecodeEvaluationProgram;
import dev.autoauto.runtime.values.AutoautoCallableValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

import java.util.List;
import java.util.Arrays;
import java.util.Stack;

public class callfunction_Bytecode extends AutoautoBytecode {

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoValue> stack, Stack<Integer> callStack) {


        int numNamedArgs = stack.pop().castToNumber().getInt();
        String[] argnames = new String[numNamedArgs];
        AutoautoValue[] namedargs = new AutoautoValue[numNamedArgs];
        for(int i = numNamedArgs - 1; i>=0; i--) {
            namedargs[i] = stack.pop();
            argnames[i] = stack.pop().getString();
        }


        int numPosArgs = stack.pop().castToNumber().getInt();
        AutoautoValue[] posargs = new AutoautoValue[numPosArgs];
        for(int i = numPosArgs - 1; i>=0; i--) posargs[i] = stack.pop();

        AutoautoValue func = stack.pop();

        callStack.push(bytecodeEvaluationProgram.pc);

        if(func instanceof AutoautoCallableValue) {
            AutoautoCallableValue fFunc = (AutoautoCallableValue)func;
            AutoautoValue[] finalArgs = mergeArgs(fFunc.getArgNames(), namedargs, argnames, posargs);
            stack.push(fFunc.call(bytecodeEvaluationProgram.lastThisContext, finalArgs));
        } else {
            stack.push(new AutoautoUndefined());
            FeatureManager.logger.warn("Attempt to call a non-callable in Autoauto");
        }

        callStack.pop();
    }

    private AutoautoValue[] mergeArgs(String[] cannonicalArgNames, AutoautoValue[] namedargs, String[] namedArgNames, AutoautoValue[] positionalArgs) {
        AutoautoValue[] finalArgs = new AutoautoValue[Math.max(cannonicalArgNames.length, positionalArgs.length)];
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
