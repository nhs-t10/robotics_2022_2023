package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class YButtonHandle extends ButtonHandle {
    private Gamepad g;
    public YButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.y?1f:0f;
    }
}