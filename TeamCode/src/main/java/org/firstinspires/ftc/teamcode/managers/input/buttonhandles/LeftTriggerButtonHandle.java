package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class LeftTriggerButtonHandle extends ButtonHandle {
    private Gamepad g;
    public LeftTriggerButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.left_trigger;
    }
}