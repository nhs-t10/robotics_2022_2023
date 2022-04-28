package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class OptionsButtonHandle extends ButtonHandle {
    private Gamepad g;
    public OptionsButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.options?1f:0f;
    }
}