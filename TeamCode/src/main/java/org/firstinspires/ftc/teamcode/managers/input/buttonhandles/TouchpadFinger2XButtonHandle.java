package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class TouchpadFinger2XButtonHandle extends ButtonHandle {
    private Gamepad g;
    public TouchpadFinger2XButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.touchpad_finger_2_x;
    }

}