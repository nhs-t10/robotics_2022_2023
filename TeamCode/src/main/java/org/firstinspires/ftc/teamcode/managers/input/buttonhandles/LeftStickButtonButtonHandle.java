package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class LeftStickButtonButtonHandle extends ButtonHandle {
    private Gamepad g;
    public LeftStickButtonButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.left_stick_button?1f:0f;
    }
}