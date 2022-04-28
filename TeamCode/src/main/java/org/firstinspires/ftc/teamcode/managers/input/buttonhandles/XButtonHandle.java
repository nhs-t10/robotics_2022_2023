package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class XButtonHandle extends ButtonHandle {
    private Gamepad g;
    public XButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.x?1f:0f;
    }
}