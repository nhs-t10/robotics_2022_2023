package org.firstinspires.ftc.teamcode.unitTests.teleop.longterm;

import com.qualcomm.robotcore.hardware.Gamepad;

public class TeleopTestingUtils {
    public static void setKeyState(String key, Gamepad gamepad, Gamepad gamepad2, float value) {
        String normalizedKey = key.toLowerCase().replace("_", "").replace(".", "");

        switch(normalizedKey) {
            case "dpadup":
            case "gamepad1dpadup":
                gamepad.dpad_up = (value!=0);
                break;
            case "dpaddown":
            case "gamepad1dpaddown":
                gamepad.dpad_down = (value!=0);
                break;
            case "dpadleft":
            case "gamepad1dpadleft":
                gamepad.dpad_left = (value!=0);
                break;
            case "dpadright":
            case "gamepad1dpadright":
                gamepad.dpad_right = (value!=0);
                break;
            case "a":
            case "gamepad1a":
                gamepad.a = (value!=0);
                break;
            case "b":
            case "gamepad1b":
                gamepad.b = (value!=0);
                break;
            case "x":
            case "gamepad1x":
                gamepad.x = (value!=0);
                break;
            case "y":
            case "gamepad1y":
                gamepad.y = (value!=0);
                break;
            case "guide":
            case "select":
            case "gamepad1guide":
                gamepad.guide = (value!=0);
                break;
            case "start":
            case "gamepad1start":
                gamepad.start = (value!=0);
                break;
            case "back":
            case "gamepad1back":
                gamepad.back = (value!=0);
                break;
            case "leftbumper":
            case "gamepad1leftbumper":
                gamepad.left_bumper = (value!=0);
                break;
            case "rightbumper":
            case "gamepad1rightbumper":
                gamepad.right_bumper = (value!=0);
                break;
            case "leftstickbutton":
            case "gamepad1leftstickbutton":
                gamepad.left_stick_button = (value!=0);
                break;
            case "rightstickbutton":
            case "gamepad1rightstickbutton":
                gamepad.right_stick_button = (value!=0);
                break;
            case "circle":
            case "gamepad1circle":
                gamepad.circle = (value!=0);
                break;
            case "cross":
            case "gamepad1cross":
                gamepad.cross = (value!=0);
                break;
            case "triangle":
            case "gamepad1triangle":
                gamepad.triangle = (value!=0);
                break;
            case "square":
            case "gamepad1square":
                gamepad.square = (value!=0);
                break;
            case "share":
            case "gamepad1share":
                gamepad.share = (value!=0);
                break;
            case "options":
            case "gamepad1options":
                gamepad.options = (value!=0);
                break;
            case "touchpad":
            case "gamepad1touchpad":
                gamepad.touchpad = (value!=0);
                break;
            case "ps":
            case "gamepad1ps":
                gamepad.ps = (value!=0);
                break;
            case "leftstickx":
            case "gamepad1leftstickx":
                gamepad.left_stick_x = value;
                break;
            case "leftsticky":
            case "gamepad1leftsticky":
                gamepad.left_stick_y = value;
                break;
            case "rightstickx":
            case "gamepad1rightstickx":
                gamepad.right_stick_x = value;
                break;
            case "rightsticky":
            case "gamepad1rightsticky":
                gamepad.right_stick_y = value;
                break;
            case "lefttrigger":
            case "gamepad1lefttrigger":
                gamepad.left_trigger = value;
                break;
            case "righttrigger":
            case "gamepad1righttrigger":
                gamepad.right_trigger = value;
                break;
            case "gamepad2leftstickx":
                gamepad2.left_stick_x = value;
                break;
            case "gamepad2leftsticky":
                gamepad2.left_stick_y = value;
                break;
            case "gamepad2rightstickx":
                gamepad2.right_stick_x = value;
                break;
            case "gamepad2rightsticky":
                gamepad2.right_stick_y = value;
                break;
            case "gamepad2lefttrigger":
                gamepad2.left_trigger = value;
                break;
            case "gamepad2righttrigger":
                gamepad2.right_trigger = value;
                break;
            case "gamepad2dpadup":
                gamepad2.dpad_up = (value!=0);
                break;
            case "gamepad2dpaddown":
                gamepad2.dpad_down = (value!=0);
                break;
            case "gamepad2dpadleft":
                gamepad2.dpad_left = (value!=0);
                break;
            case "gamepad2dpadright":
                gamepad2.dpad_right = (value!=0);
                break;
            case "gamepad2a":
                gamepad2.a = (value!=0);
                break;
            case "gamepad2b":
                gamepad2.b = (value!=0);
                break;
            case "gamepad2x":
                gamepad2.x = (value!=0);
                break;
            case "gamepad2y":
                gamepad2.y = (value!=0);
                break;
            case "gamepad2guide":
                gamepad2.guide = (value!=0);
                break;
            case "gamepad2start":
                gamepad2.start = (value!=0);
                break;
            case "gamepad2back":
                gamepad2.back = (value!=0);
                break;
            case "gamepad2leftbumper":
                gamepad2.left_bumper = (value!=0);
                break;
            case "gamepad2rightbumper":
                gamepad2.right_bumper = (value!=0);
                break;
            case "gamepad2leftstickbutton":
                gamepad2.left_stick_button = (value!=0);
                break;
            case "gamepad2rightstickbutton":
                gamepad2.right_stick_button = (value!=0);
                break;
            case "gamepad2circle":
                gamepad2.circle = (value!=0);
                break;
            case "gamepad2cross":
                gamepad2.cross = (value!=0);
                break;
            case "gamepad2triangle":
                gamepad2.triangle = (value!=0);
                break;
            case "gamepad2square":
                gamepad2.square = (value!=0);
                break;
            case "gamepad2share":
                gamepad2.share = (value!=0);
                break;
            case "gamepad2options":
                gamepad2.options = (value!=0);
                break;
            case "gamepad2touchpad":
                gamepad2.touchpad = (value!=0);
                break;
            case "gamepad2ps":
                gamepad2.ps = (value!=0);
                break;
        }
    }
}
