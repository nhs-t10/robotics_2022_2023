package dev.autoauto.runtime;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import dev.autoauto.model.AutoautoProgram;

public abstract class AutoautoOpmode extends OpMode {
    public AutoautoProgram program;

    public abstract AutoautoProgram generateProgram();
}
