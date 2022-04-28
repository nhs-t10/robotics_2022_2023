package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;


public class TouchpadFinger2ButtonHandle extends ButtonHandle {
    private Gamepad g;
    public TouchpadFinger2ButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.touchpad_finger_2?1f:0f;
    }

}