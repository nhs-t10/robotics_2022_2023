package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class StartButtonHandle extends ButtonHandle {
    private Gamepad g;
    public StartButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.start?1f:0f;
    }
}