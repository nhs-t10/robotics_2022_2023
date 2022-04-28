package org.firstinspires.ftc.teamcode.managers.input.nodes;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.auxilary.RobotTime;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;

public class AccelerationNode extends InputManagerInputNode{

    private final InputManagerNodeResult result = new InputManagerNodeResult();

    private InputManagerInputNode control;
    private InputManagerInputNode startingSpeed;
    private InputManagerInputNode endingSpeed;
    private InputManagerInputNode movementTime;

    private boolean wasPressed;
    private long accelerationStartTime;

    /**
     * Gradually accelerates an input over a given amount of time (in milliseconds). <br>
     * If the starting speed value is greater than the ending speed value, this can be used to gradually slow down as well. <br>
     * When the input is false, the node will instantly reset to the default value, and it will <i>not</i> return gradually.<br>
     *
     * <img src="./doc-files/acceleration-node.png" width="200">
     *
     * @param control The input that will be accelerated
     * @param startingSpeed The starting speed of the input
     * @param endingSpeed The ending speed of the input
     * @param movementTime How long it will take to get from the startingSpeed to the endingSpeed.
     * @see DecelerationNode#DecelerationNode(InputManagerInputNode, InputManagerInputNode, InputManagerInputNode, InputManagerInputNode) DecelerationNode
     * @see BothcelerationNode#BothcelerationNode(InputManagerInputNode, InputManagerInputNode, InputManagerInputNode, InputManagerInputNode) BothcelerationNode
     */
    public AccelerationNode(InputManagerInputNode control, InputManagerInputNode startingSpeed, InputManagerInputNode endingSpeed, InputManagerInputNode movementTime) {
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

        if(isPressed && wasPressed == false) {
            accelerationStartTime = RobotTime.currentTimeMillis();
        }

        float starting = startingSpeed.getResult().getFloat();
        float ending = endingSpeed.getResult().getFloat();

        float resultNumber = starting;

        if(isPressed) {
            long timeSinceStart = RobotTime.currentTimeMillis() - accelerationStartTime;
            float percentageCompleted = Math.min(1, timeSinceStart / totalMovementTime);

            resultNumber = starting + percentageCompleted * (ending - starting);
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
