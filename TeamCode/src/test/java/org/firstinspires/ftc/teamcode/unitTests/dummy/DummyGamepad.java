package org.firstinspires.ftc.teamcode.unitTests.dummy;

import com.qualcomm.robotcore.hardware.Gamepad;

public class DummyGamepad extends Gamepad {
    public DummyGamepad() {
        this.left_stick_x = 0f;
        this.left_stick_y = 0f;
        this.right_stick_x = 0f;
        this.right_stick_y = 0f;
        this.left_trigger = 0f;
        this.right_trigger = 0f;
        this.dpad_up = false;
        this.dpad_down = false;
        this.dpad_left = false;
        this.dpad_right = false;
        this.a = false;
        this.b = false;
        this.x = false;
        this.y = false;
        this.guide = false;
        this.start = false;
        this.back = false;
        this.left_bumper = false;
        this.right_bumper = false;
        this.left_stick_button = false;
        this.right_stick_button = false;
        this.circle = false;
        this.cross = false;
        this.triangle = false;
        this.square = false;
        this.share = false;
        this.options = false;
        this.touchpad = false;
        this.ps = false;


    }

    public void buttonSmash() {
        this.left_stick_x = 1f;
        this.left_stick_y = 1f;
        this.right_stick_x = 1f;
        this.right_stick_y = 1f;
        this.left_trigger = 1f;
        this.right_trigger = 1f;
        this.dpad_up = true;
        this.dpad_down = true;
        this.dpad_left = true;
        this.dpad_right = true;
        this.a = true;
        this.b = true;
        this.x = true;
        this.y = true;
        this.guide = true;
        this.start = true;
        this.back = true;
        this.left_bumper = true;
        this.right_bumper = true;
        this.left_stick_button = true;
        this.right_stick_button = true;
        this.circle = true;
        this.cross = true;
        this.triangle = true;
        this.square = true;
        this.share = true;
        this.options = true;
        this.touchpad = true;
        this.ps = true;
    }
}
