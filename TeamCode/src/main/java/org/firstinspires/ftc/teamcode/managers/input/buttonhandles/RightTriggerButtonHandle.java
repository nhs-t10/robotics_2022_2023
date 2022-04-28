package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class RightTriggerButtonHandle extends ButtonHandle {
    private Gamepad g;
    public RightTriggerButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.right_trigger;
    }
}