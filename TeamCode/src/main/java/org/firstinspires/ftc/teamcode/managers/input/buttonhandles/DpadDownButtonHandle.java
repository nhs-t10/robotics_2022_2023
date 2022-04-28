package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class DpadDownButtonHandle extends ButtonHandle {
    private Gamepad g;
    public DpadDownButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.dpad_down?1f:0f;
    }
}