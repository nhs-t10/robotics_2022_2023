package org.firstinspires.ftc.teamcode.unitTests.input;

import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ButtonNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.JoystickNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyGamepad;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class InputManagerTest {
    @Test
    public void test() {
        DummyGamepad gamepad1 = new DummyGamepad();
        DummyGamepad gamepad2 = new DummyGamepad();
        InputManager input = new InputManager(gamepad1, gamepad2);

        input.registerInput("test", new ButtonNode("a"));
        input.registerInput("lsy", new JoystickNode("left_stick_y"));
        input.registerInput("drivingControls",
                new MultiInputNode(
                        new JoystickNode("left_stick_x"),
                        new JoystickNode("left_stick_y"),
                        new JoystickNode("right_stick_x")
                )
        );

        assertEquals(0, input.getFloat("test"), 0);

        gamepad1.a = true;

        assertEquals(1, input.getFloat("test"), 0);

        assertArrayEquals(new float[] {0,0,0}, input.getFloatArrayOfInput("drivingControls"), 0);

        gamepad1.left_stick_y  = 1;

        assertEquals(1, input.getFloat("lsy"), 0);

        assertArrayEquals(new float[] {0,1,0}, input.getFloatArrayOfInput("drivingControls"), 0);
    }

}
