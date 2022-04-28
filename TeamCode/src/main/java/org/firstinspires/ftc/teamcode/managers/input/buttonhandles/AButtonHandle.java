package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class AButtonHandle extends ButtonHandle {
    private Gamepad g;
    public AButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        if (g.start) return 0;
        return g.a?1f:0f;
    }
}