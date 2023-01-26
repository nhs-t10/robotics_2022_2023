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
    private float accelerationConstant;
    private float currentSpeed;

    private boolean wasActive = false;
    private boolean isActive = false;
    private long accelerationStartTime;

    /**
     * Gradually accelerates an stick input by a certain amount of speed per frame <br>
     * If the starting speed value is greater than the ending speed value, this can be used to gradually slow down as well. <br>
     * When the input is false, the node will instantly reset to the default value, and it will <i>not</i> return gradually.<br>
     *
     * <img src="./doc-files/acceleration-node.png" width="200">
     *
     * @param control The input that will be accelerated
     * @param initialSpeed The initial speed of the input. If this is too low, (below 0.25) the robot may move slowly without input.
     * @param accelConstant A scale from 0 to 1 that affects the speed of acceleration. 0 is no acceleration, 1 is instantaneous. 0.5 is a good average acceleration.
     * @see AccelerationNode#AccelerationNode(InputManagerInputNode, InputManagerInputNode, InputManagerInputNode, InputManagerInputNode) AccelerationNode
     * @see DecelerationNode#DecelerationNode(InputManagerInputNode, InputManagerInputNode, InputManagerInputNode, InputManagerInputNode) DecelerationNode
     * @see BothcelerationNode#BothcelerationNode(InputManagerInputNode, InputManagerInputNode, InputManagerInputNode, InputManagerInputNode) BothcelerationNode
     */
    public GradualStickNode(InputManagerInputNode control, float initialSpeed, float accelConstant) {
        this.control = control;
        startingSpeed = initialSpeed;
        accelerationConstant = accelConstant;
    }

    @Override
    public void init(InputManager boss) {
        control.init(boss);
    }

    @Override
    public void update() {
        control.update();
        isActive = Math.abs(control.getResult().getFloat()) >= startingSpeed;
        endingSpeed = control.getResult().getFloat();
        if(isActive) {
            if(!wasActive) {
                if (endingSpeed > 0) {
                    currentSpeed = startingSpeed;
                }
                else {
                    currentSpeed = -startingSpeed;
                }
            }
            //Old implementation
            //long timeSinceStart = RobotTime.currentTimeMillis() - accelerationStartTime;
            //float percentageCompleted = Math.min(1, timeSinceStart / movementTime);
            //resultNumber = startingSpeed + percentageCompleted * (endingSpeed - startingSpeed);
            if (currentSpeed > 0) {
                if ((currentSpeed + (accelerationConstant/2000)) < endingSpeed) {
                    currentSpeed = currentSpeed + (accelerationConstant/5000);
                } else {
                    currentSpeed = endingSpeed;
                }
            }
            else {
                if ((currentSpeed - (accelerationConstant/2000)) > endingSpeed) {
                    currentSpeed = currentSpeed - (accelerationConstant/5000);
                } else {
                    currentSpeed = endingSpeed;
                }
            }
        }
        else {
            currentSpeed = 0.0f;
        }
        wasActive = isActive;
        //Multiplied by 1.6 to allow reaching full power, otherwise it caps out at around 60% speed.
        result.setFloat(currentSpeed*1.6f);
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
