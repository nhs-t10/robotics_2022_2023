package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class BackButtonHandle extends ButtonHandle {
    private Gamepad g;
    public BackButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.back?1f:0f;
    }
}