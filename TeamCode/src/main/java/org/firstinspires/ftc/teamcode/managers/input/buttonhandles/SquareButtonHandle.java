package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class SquareButtonHandle extends ButtonHandle {
    private Gamepad g;
    public SquareButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.square?1f:0f;
    }
}