package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class RightStickButtonButtonHandle extends ButtonHandle {
    private Gamepad g;
    public RightStickButtonButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.right_stick_button?1f:0f;
    }
}