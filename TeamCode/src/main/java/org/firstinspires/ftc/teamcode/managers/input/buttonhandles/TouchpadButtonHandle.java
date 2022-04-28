package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class TouchpadButtonHandle extends ButtonHandle {
    private Gamepad g;
    public TouchpadButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.touchpad?1f:0f;
    }
}