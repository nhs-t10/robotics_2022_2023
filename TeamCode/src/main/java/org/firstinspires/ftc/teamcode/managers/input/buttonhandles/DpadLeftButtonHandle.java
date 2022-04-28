package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class DpadLeftButtonHandle extends ButtonHandle {
    private Gamepad g;
    public DpadLeftButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.dpad_left?1f:0f;
    }
}