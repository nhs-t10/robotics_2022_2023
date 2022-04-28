package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class RightBumperButtonHandle extends ButtonHandle {
    private Gamepad g;
    public RightBumperButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.right_bumper?1f:0f;
    }
}