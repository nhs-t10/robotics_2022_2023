package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class TriangleButtonHandle extends ButtonHandle {
    private Gamepad g;
    public TriangleButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.triangle?1f:0f;
    }
}