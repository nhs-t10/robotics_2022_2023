package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class CrossButtonHandle extends ButtonHandle {
    private Gamepad g;
    public CrossButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.cross?1f:0f;
    }
}