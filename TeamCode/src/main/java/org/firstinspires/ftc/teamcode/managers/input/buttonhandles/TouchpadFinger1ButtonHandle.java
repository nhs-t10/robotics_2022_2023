package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;


public class TouchpadFinger1ButtonHandle extends ButtonHandle {
    private Gamepad g;
    public TouchpadFinger1ButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.touchpad_finger_1?1f:0f;
    }

}