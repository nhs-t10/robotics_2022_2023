package dev.autoauto.model.bytecode;

import androidx.annotation.NonNull;

import dev.autoauto.model.Location;
import dev.autoauto.model.programtypes.BytecodeEvaluationProgram;
import dev.autoauto.runtime.values.AutoautoCallableValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoTable;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;
import dev.autoauto.runtime.AutoautoSystemVariableNames;

import java.util.Stack;

public class makefunction_i_Bytecode extends AutoautoBytecode {

    @Override
    public void invoke(BytecodeEvaluationProgram bytecodeEvaluationProgram, AutoautoRuntimeVariableScope scope, Stack<AutoautoValue> stack, Stack<Integer> callStack) {
        int numArgs = stack.pop().castToNumber().getInt();
        String[] argNames = new String[numArgs];
        AutoautoValue[] defaults = new AutoautoValue[numArgs];

        for(int i = numArgs - 1; i >= 0; i--) {
            defaults[i] = stack.pop();
            argNames[i] = stack.pop().getString();
        }

        //The offset is relative, so we need to add the current program-counter, plus one, to get the actual start of the function
        int pcTo = bytecodeEvaluationProgram.pc + stack.pop().castToNumber().getInt() + 1;

        stack.push(new BytecodeFunction(pcTo, argNames, bytecodeEvaluationProgram, scope, stack, callStack, defaults));
    }

    private static class BytecodeFunction extends AutoautoValue implements AutoautoCallableValue {

        private final int functionStart;
        private final Stack<Integer> callStack;
        private final Stack<AutoautoValue> stack;
        private String[] argNames;
        BytecodeEvaluationProgram bytecodeEvaluationProgram;
        private AutoautoValue[] defaults;

        AutoautoRuntimeVariableScope scope;

        public BytecodeFunction(int pcTo, String[] argNames, BytecodeEvaluationProgram p, AutoautoRuntimeVariableScope scope, Stack<AutoautoValue> stack, Stack<Integer> callStack, AutoautoValue[] defaults) {
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
        public AutoautoValue call(AutoautoValue thisValue, AutoautoValue[] args) {
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
        public AutoautoValue clone() {
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
