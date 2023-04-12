package org.firstinspires.ftc.teamcode.managers.input.nodes;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;

public class SwitchNode extends InputManagerInputNode {
    private final InputManagerInputNode input1;
    private final InputManagerInputNode input2;
    private final InputManagerNodeResult result = new InputManagerNodeResult();
    private enum State {GAMEPAD_1, GAMEPAD_2};
    private final InputManagerInputNode node;
    private final InputManagerInputNode node1;
    private boolean isPressed;
    private boolean wasPressed;
    private boolean isPressed2;
    private boolean wasPressed2;
    private State state = State.GAMEPAD_1;
    private RevBlinkinLedDriver led;

    /**
     * Switches the result value between two given input arrays.<br>
     * Ideal for 2 gamepad coordinated control.
     * @param input1 The first input listed in the SwitchNode
     * @param input2 The second input listed in the SwitchNode
     * @param node The node that represents the switch that is pressed
     * @param node The node that represents the alternate switch that is pressed
     * @see MinusNode#MinusNode(InputManagerInputNode, InputManagerInputNode) MinusNode
     * @see PlusNode#PlusNode(InputManagerInputNode, InputManagerInputNode) Pl
     */
    public SwitchNode(InputManagerInputNode input1, InputManagerInputNode input2, InputManagerInputNode node, InputManagerInputNode node1) {
        this.input1 = input1;
        this.input2 = input2;
        this.node = node;
        this.node1 = node1;
    }
    public SwitchNode(InputManagerInputNode input1, InputManagerInputNode input2, InputManagerInputNode node, InputManagerInputNode node1, RevBlinkinLedDriver led) {
        this.input1 = input1;
        this.input2 = input2;
        this.node = node;
        this.node1 = node1;
        this.led = led;
    }

    @Override
    public void init(InputManager boss) {
        input1.init(boss);
        input2.init(boss);
    }

    public void update() {
        input1.update();
        input2.update();
        node.update();
        node1.update();
        isPressed = node.getResult().getBool();
        isPressed2 = node1.getResult().getBool();
        if(state == State.GAMEPAD_1 && isPressed && !wasPressed) {
            state = State.GAMEPAD_2;
            InputManager.vibrategp2();
            if(led!= null) {
                led.setPattern(RevBlinkinLedDriver.BlinkinPattern.ORANGE);
            }
        }else if(state == State.GAMEPAD_2 && isPressed2 && !wasPressed2) {
            state = State.GAMEPAD_1;
            InputManager.vibrategp();
            if(led!= null) {
                led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE);
            }
        }

    }

    @NonNull
    @Override
    public InputManagerNodeResult getResult() {
        float[] input1Vals = input1.getResult().getFloatArray();
        float[] input2Vals = input2.getResult().getFloatArray();
        result.setFloatArray(input1Vals);
        if(state == State.GAMEPAD_1) {
            result.setFloatArray(input1Vals);
        }else if(state == State.GAMEPAD_2) {
            result.setFloatArray(input2Vals);
        }

        return result;
    }

    @Override
    public int complexity() {
        return input1.complexity() + input2.complexity() + 1;
    }

    @Override
    public String[] getKeysUsed() {
        return PaulMath.concatArrays(input1.getKeysUsed(), input2.getKeysUsed());
    }

    @Override
    public boolean usesKey(String s) {
        if (input1.usesKey(s) || input2.usesKey(s)){
            return true;
        }
        return false;
    }
}
