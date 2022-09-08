package dev.autoauto.model.programtypes;

import dev.autoauto.model.AutoautoProgram;
import dev.autoauto.model.AutoautoProgramElement;
import dev.autoauto.model.Location;
import dev.autoauto.model.bytecode.AutoautoBytecode;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;

import java.util.Stack;

public class BytecodeEvaluationProgram implements AutoautoProgram {
    private final AutoautoBytecode[] bytecodeRecords;
    public AutoautoValue lastThisContext;
    private AutoautoRuntimeVariableScope scope;
    private final Stack<AutoautoValue> stack;
    private final Stack<Integer> callStack;

    public int pc;
    public boolean yield;

    public BytecodeEvaluationProgram(AutoautoBytecode[] bcs) {
        this.pc = 0;
        this.bytecodeRecords = bcs;
        this.lastThisContext = new AutoautoUndefined();

        this.scope = new AutoautoRuntimeVariableScope();

        scope.initStandaloneBuiltins();
        scope.initSugarVariables();

        stack = new Stack<>();
        callStack = new Stack<>();
    }

    public void runUntilYield() {
        while(!yield) {
            //FeatureManager.logger.log("calling: " + pc + " " + (bytecodeRecords[program[pc]].toString()));
            bytecodeRecords[pc].invoke(this, scope, stack, callStack);
            //FeatureManager.logger.log(stack.toString());
            pc++;
        }
        yield = false;
    }

    @Override
    public void loop() {
        runUntilYield();
    }

    @Override
    public void init() {}

    @Override
    public void stepInit() {}

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
    }

    @Override
    public Location getLocation() {
        return new Location("", 0, 0, 0);
    }

    @Override
    public void setLocation(Location location) { }

    @Override
    public AutoautoProgramElement clone() { return this; }
}
