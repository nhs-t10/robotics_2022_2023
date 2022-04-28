package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.bytecode;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.programtypes.BytecodeEvaluationProgram;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoCallableValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoTable;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoSystemVariableNames;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

import java.util.Stack;

public class makefunction_i_Bytecode extends AutoautoBytecode {

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoPrimitive> stack, Stack<Integer> callStack) {
        int numArgs = stack.pop().castToNumber().getInt();
        String[] argNames = new String[numArgs];
        AutoautoPrimitive[] defaults = new AutoautoPrimitive[numArgs];

        for(int i = numArgs - 1; i >= 0; i--) {
            defaults[i] = stack.pop();
            argNames[i] = stack.pop().getString();
        }

        int pcTo = stack.pop().castToNumber().getInt();

        stack.push(new BytecodeFunction(pcTo, argNames, bytecodeEvaluationProgram, scope, stack, callStack, defaults));
    }

    private static class BytecodeFunction extends AutoautoPrimitive implements AutoautoCallableValue {

        private final int functionStart;
        private final Stack<Integer> callStack;
        private final Stack<AutoautoPrimitive> stack;
        private String[] argNames;
        BytecodeEvaluationProgram bytecodeEvaluationProgram;
        private AutoautoPrimitive[] defaults;

        AutoautoRuntimeVariableScope scope;

        public BytecodeFunction(int pcTo, String[] argNames, BytecodeEvaluationProgram p, AutoautoRuntimeVariableScope scope, Stack<AutoautoPrimitive> stack, Stack<Integer> callStack, AutoautoPrimitive[] defaults) {
            this.functionStart = pcTo;
            this.argNames = argNames;
            this.bytecodeEvaluationProgram = p;
            this.scope = scope;
            this.stack = stack;
            this.callStack = callStack;
            this.defaults = defaults;
        }

        @Override
        public String[] getArgNames() {
            return argNames;
        }

        @Override
        public AutoautoPrimitive call(AutoautoPrimitive thisValue, AutoautoPrimitive[] args) throws ManagerSetupException {
            AutoautoRuntimeVariableScope scope = new AutoautoRuntimeVariableScope(this.scope);
            scope.systemSet(AutoautoSystemVariableNames.THIS, thisValue);
            scope.systemSet(AutoautoSystemVariableNames.FUNCTION_ARGUMENTS_NAME, new AutoautoTable(args));

            for(int i = 0; i < argNames.length; i++) {
                scope.systemSet(argNames[i], (i < args.length) ? args[i] : defaults[i]);
            }
            for(int i = args.length; i < argNames.length; i++) {
                scope.systemSet(argNames[i], defaults[i]);
            }

            bytecodeEvaluationProgram.setScope(scope);
            this.bytecodeEvaluationProgram.pc = functionStart;

            bytecodeEvaluationProgram.runUntilYield();
            bytecodeEvaluationProgram.yield = false;

            bytecodeEvaluationProgram.setScope(this.scope);

            return stack.pop();
        }

        @Override
        public AutoautoRuntimeVariableScope getScope() {
            return this.scope;
        }

        @Override
        public void setScope(AutoautoRuntimeVariableScope scope) {
            this.scope = scope;
        }

        @Override
        public Location getLocation() {
            return new Location("", "", 0, 0, 0);
        }

        @Override
        public void setLocation(Location location) {}

        @NonNull
        @Override
        public String getString() {
            return "function() {}";
        }

        @Override
        public String getJSONString() {
            return "\"function() {}\"";
        }

        @Override
        public AutoautoPrimitive clone() {
            return this;
        }

        @Override
        public int dataWidth() {
            return 5;
        }

        public String toString() {
            return getString();
        }
    }
}
