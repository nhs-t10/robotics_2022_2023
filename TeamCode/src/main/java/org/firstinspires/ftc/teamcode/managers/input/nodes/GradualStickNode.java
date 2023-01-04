package org.firstinspires.ftc.teamcode.managers.input.nodes;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.auxilary.RobotTime;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;

public class GradualStickNode extends InputManagerInputNode{

    private final InputManagerNodeResult result = new InputManagerNodeResult();

    private InputManagerInputNode control;
    private float startingSpeed;
    private float endingSpeed;
    private float movementTime;

    private boolean wasActive;
    private long accelerationStartTime;

    /**
     * Gradually accelerates an stick input over a given amount of time (in milliseconds). <br>
     * If the starting speed value is greater than the ending speed value, this can be used to gradually slow down as well. <br>
     * When the input is false, the node will instantly reset to the default value, and it will <i>not</i> return gradually.<br>
     *
     * <img src="./doc-files/acceleration-node.png" width="200">
     *
     * @param control The input that will be accelerated
     * @param initialSpeed The initial speed of the input
     * @param moveTime How long it will take to get from the startingSpeed to the speed of the stick.
     * @see AccelerationNode#AccelerationNode(InputManagerInputNode, InputManagerInputNode, InputManagerInputNode, InputManagerInputNode) AccelerationNode
     * @see DecelerationNode#DecelerationNode(InputManagerInputNode, InputManagerInputNode, InputManagerInputNode, InputManagerInputNode) DecelerationNode
     * @see BothcelerationNode#BothcelerationNode(InputManagerInputNode, InputManagerInputNode, InputManagerInputNode, InputManagerInputNode) BothcelerationNode
     */
    public GradualStickNode(InputManagerInputNode control, float initialSpeed, float moveTime) {
        this.control = control;
        startingSpeed = initialSpeed;
        movementTime = moveTime;
    }

    @Override
    public void init(InputManager boss) {
        control.init(boss);
    }

    @Override
    public void update() {
        control.update();

        boolean isActive = control.getResult().getBool();

        if(isActive && wasActive == false) {
            accelerationStartTime = RobotTime.currentTimeMillis();
        }
        endingSpeed = control.getResult().getFloat();
        float resultNumber = startingSpeed;

        if(isActive) {
            long timeSinceStart = RobotTime.currentTimeMillis() - accelerationStartTime;
            float percentageCompleted = Math.min(1, timeSinceStart / movementTime);
            resultNumber = startingSpeed + percentageCompleted * (endingSpeed - startingSpeed);
        }

        wasActive = isActive;

        result.setFloat(resultNumber);
    }

    @NonNull
    @Override
    public InputManagerNodeResult getResult() {
        return result;
    }

    @Override
    public int complexity() {
        return control.complexity() + 1;
    }

    @Override
    public String[] getKeysUsed() {
        return PaulMath.concatArrays(control.getKeysUsed());
    }

    @Override
    public boolean usesKey(String s) {
        return control.usesKey(s);
    }
}
