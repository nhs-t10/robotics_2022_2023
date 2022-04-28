package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class LeftBumperButtonHandle extends ButtonHandle {
    private Gamepad g;
    public LeftBumperButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.left_bumper?1f:0f;
    }
}