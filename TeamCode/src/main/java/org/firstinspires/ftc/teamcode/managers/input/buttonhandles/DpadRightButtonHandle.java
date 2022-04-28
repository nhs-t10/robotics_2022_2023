package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class DpadRightButtonHandle extends ButtonHandle {
    private Gamepad g;
    public DpadRightButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.dpad_right?1f:0f;
    }
}