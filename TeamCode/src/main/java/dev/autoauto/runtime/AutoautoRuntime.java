package dev.autoauto.runtime;

import dev.autoauto.model.AutoautoProgram;

public class AutoautoRuntime {
    private final AutoautoProgram program;
    public RobotFunctionLoader hardwareAccess;

    public final AutoautoRuntimeVariableScope globalScope;

    public static AutoautoRuntime R(AutoautoProgram program, String creatorAddress, Object... managers) {
        return new AutoautoRuntime(program, creatorAddress, managers);
    }

    public AutoautoRuntime(AutoautoProgram program, String creatorAddress, Object... managers) {
        this.program = program;
        this.globalScope = new AutoautoRuntimeVariableScope();
        this.hardwareAccess = new RobotFunctionLoader(managers);

        initProgram();
    }

    public void loop() {
            program.loop();
    }

    private void initProgram() {
        globalScope.initSugarVariables();
        globalScope.initBuiltinFunctions(hardwareAccess);

        this.program.setScope(globalScope);

        this.program.init();
        this.program.stepInit();
    }
}
