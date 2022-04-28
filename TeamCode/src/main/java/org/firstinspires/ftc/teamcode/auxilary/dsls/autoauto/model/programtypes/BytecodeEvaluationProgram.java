package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.programtypes;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.AutoautoProgram;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.AutoautoProgramElement;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.bytecode.AutoautoBytecode;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

import java.util.Stack;

public class BytecodeEvaluationProgram implements AutoautoProgram {
    private final AutoautoBytecode[] bytecodeRecords;
    private final int[] program;
    public AutoautoPrimitive lastThisContext;
    private AutoautoRuntimeVariableScope scope;
    private final Stack<AutoautoPrimitive> stack;
    private final Stack<Integer> callStack;

    public int pc;
    public boolean yield;

    public BytecodeEvaluationProgram(int[] program, AutoautoBytecode[] bcs) {
        this.pc = 0;
        this.program = program;
        this.bytecodeRecords = bcs;
        this.lastThisContext = new AutoautoUndefined();
        scope = new AutoautoRuntimeVariableScope();
        stack = new Stack<>();
        callStack = new Stack<>();
    }

    public void runUntilYield() {
        while(!yield) {
            //FeatureManager.logger.log("calling: " + pc + " " + (bytecodeRecords[program[pc]].toString()));
            bytecodeRecords[program[pc]].invoke(this, scope, stack, callStack);
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
