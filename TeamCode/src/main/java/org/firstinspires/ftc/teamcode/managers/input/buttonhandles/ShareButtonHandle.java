package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

import com.qualcomm.robotcore.hardware.Gamepad;

public class ShareButtonHandle extends ButtonHandle {
    private Gamepad g;
    public ShareButtonHandle(Gamepad g) {
        this.g = g;
    }
    public float get() {
        return g.share?1f:0f;
    }
}