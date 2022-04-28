package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class TouchpadFinger1YButtonHandle extends ButtonHandle {
    private Gamepad g;
    public TouchpadFinger1YButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.touchpad_finger_1_y;
    }

}