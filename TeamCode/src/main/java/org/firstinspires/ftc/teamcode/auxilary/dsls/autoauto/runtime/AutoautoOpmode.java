package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.AutoautoProgram;

public abstract class AutoautoOpmode extends OpMode {
    public AutoautoProgram program;

    public abstract AutoautoProgram generateProgram();
}
