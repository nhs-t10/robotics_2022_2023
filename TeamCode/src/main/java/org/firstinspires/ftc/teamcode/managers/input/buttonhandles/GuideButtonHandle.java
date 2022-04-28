package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GuideButtonHandle extends ButtonHandle {
    private Gamepad g;
    public GuideButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.guide?1f:0f;
    }
}