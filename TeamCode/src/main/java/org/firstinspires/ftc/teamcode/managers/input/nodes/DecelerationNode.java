package org.firstinspires.ftc.teamcode.managers.input.nodes;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.auxilary.RobotTime;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;

public class DecelerationNode extends InputManagerInputNode{

    private final InputManagerNodeResult result = new InputManagerNodeResult();

    private InputManagerInputNode control;
    private InputManagerInputNode startingSpeed;
    private InputManagerInputNode endingSpeed;
    private InputManagerInputNode movementTime;

    private boolean wasPressed;
    private long decelerationStartTime;

/**
 * Gradually decelerates an input over a given amount of time (in milliseconds). <br>
 * If the starting speed value is less than the ending speed value, this can be used to gradually speed up as well. <br>
 * This works in the opposite order of AccelerationNode. The speed will gradually decreased when released and not when it is pressed. <br>
 *
 * <img src="./doc-files/deceleration-node.png" width="200">
 *
 * @param control The input that will be decelerated
 * @param startingSpeed The starting speed of the input
 * @param endingSpeed The ending speed of the input
 * @param movementTime How long it will take to get from the startingSpeed to the endingSpeed.
 * @see AccelerationNode#AccelerationNode(InputManagerInputNode, InputManagerInputNode, InputManagerInputNode, InputManagerInputNode) AccelerationNode
 * @see BothcelerationNode#BothcelerationNode(InputManagerInputNode, InputManagerInputNode, InputManagerInputNode, InputManagerInputNode) BothcelerationNode
 * */
    public DecelerationNode(InputManagerInputNode control, InputManagerInputNode startingSpeed, InputManagerInputNode endingSpeed, InputManagerInputNode movementTime) {
        this.control = control;
        this.startingSpeed = startingSpeed;
        this.endingSpeed = endingSpeed;
        this.movementTime = movementTime;
    }

    @Override
    public void init(InputManager boss) {
        control.init(boss);
        startingSpeed.init(boss);
        endingSpeed.init(boss);
        movementTime.init(boss);
    }

    @Override
    public void update() {
        control.update();
        startingSpeed.update();
        endingSpeed.update();
        movementTime.update();

        float totalMovementTime = movementTime.getResult().getFloat();

        boolean isPressed = control.getResult().getBool();

        if(isPressed == false && wasPressed == true) {
            decelerationStartTime = RobotTime.currentTimeMillis();
        }

        float starting = startingSpeed.getResult().getFloat();
        float ending = endingSpeed.getResult().getFloat();

        float resultNumber = starting;

        if(!isPressed && RobotTime.currentTimeMillis() < (decelerationStartTime + totalMovementTime) && decelerationStartTime != 0) {
            long timeSinceStart = RobotTime.currentTimeMillis() - decelerationStartTime;
            float percentageCompleted = Math.min(1, timeSinceStart / totalMovementTime);

            resultNumber = starting - percentageCompleted * (starting - ending);
        }

        wasPressed = isPressed;

        result.setFloat(resultNumber);
    }

    @NonNull
    @Override
    public InputManagerNodeResult getResult() {
        return result;
    }

    @Override
    public int complexity() {
        return control.complexity() + startingSpeed.complexity() + endingSpeed.complexity() + movementTime.complexity() + 1;
    }

    @Override
    public String[] getKeysUsed() {
        return PaulMath.concatArrays(control.getKeysUsed(), startingSpeed.getKeysUsed(), endingSpeed.getKeysUsed(), movementTime.getKeysUsed());
    }

    @Override
    public boolean usesKey(String s) {
        return control.usesKey(s) || startingSpeed.usesKey(s) || endingSpeed.usesKey(s) || movementTime.usesKey(s);
    }
}
