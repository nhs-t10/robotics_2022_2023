package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class CircleButtonHandle extends ButtonHandle {
    private Gamepad g;
    public CircleButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.circle?1f:0f;
    }
}