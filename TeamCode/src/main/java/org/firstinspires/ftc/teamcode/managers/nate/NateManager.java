package org.firstinspires.ftc.teamcode.managers.nate;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;

public class NateManager extends FeatureManager {
    private TouchSensor input;
    private boolean found;
    private int position;
    private int currentPosition;
    private double currentPos;
    private boolean clawState = false;
    ManipulationManager hands;

    private double resetOffset;

    public int positionOneLocation = 727;
    public int positionTwoLocation = 1738;
    public int positionThreeLocation = 2950;
    public int positionNeutralLocation = 0;
    public int positionSharedLocation;
    public int positionHomeLocation = -1826;

    public NateManager(ManipulationManager hands/*, TouchSensor input*/){
        /*this.input = input;*/
        this.hands = hands;
    }

    public NateManager(ManipulationManager hands, TouchSensor input) {
        this.hands = hands;
        this.input = input;
    }

    //TODO: test and fix tested warning.
    /**
     * Make the claw open or close
     *
     * @param open If {@code true}, open the claw; otherwise, close it.
     */
    public void setClawOpen(boolean open) {
        if(open) hands.setServoPosition("nateClaw", 0.7);
        else hands.setServoPosition("nateClaw", 0.25);
        clawState = open;
    }

    /**
     * Toggle whether the robot's claw is open.
     * If the claw is closed, it'll become open. If it's open, it'll become closed.
     */
    public void toggleClaw(){
        clawState = !clawState;
        setClawOpen(clawState);
    }

    //TODO: test and remove untested warning.
    /**
     * Get the current claw status
     * <h2>Warning: This method has not been tested in the wild. Use it, but don't rely fully until it's been tested.</h2>
     *
     * @return {@code true} if the claw is open, {@code false} if the claw is closed.
     */
    public boolean getClawStatus() {
        return clawState;
    }

    /**
     * Stop any asynchronous movement of the claw lift immediately
     * @see #positionOne()
     * @see #positionTwo()
     * @see #positionThree()
     * @see #positionHome()
     */
    public void emergencyStop(){
        hands.encodeMoveToPosition("ClawMotor", (int)hands.getMotorPosition("ClawMotor"), 0);
    }

    /**
     * Checks that the claw lift <u>isn't</u> moving asynchronously.
     *
     * @see #positionOne()
     * @see #positionTwo()
     * @see #positionThree()
     * @see #positionHome()
     *
     * @return {@code false} if the claw lift has any asynchronous movement; {@code true} otherwise
     */
    public boolean liftMovementFinished() {
        return !hands.hasEncodedMovement("ClawMotor");
    }

    /**
     * <p>Move the claw's lift to position 1, the highest position.</p>
     *
     * <p>Note that this is an <u>asynchronous</u> method, meaning that the method will complete before the action does.
     * Something like {@code positionOne(); toggleClawOpen()} will open the claw <u>right away</u>. It does <u>not</u> wait for the lift to move.
     * </p>
     * <p>
     * If you want autonomous to have synchronous-ish behaviour, use the {@link #liftMovementFinished clawMovementFinished()} method, like so:
     * <pre><code> positionOne(), when(clawMovementFinished()) next; </code></pre>
     * </p>
     */
    public void positionOne(){
        position = positionOneLocation;
        hands.encodeMoveToPosition("ClawMotor", position);
    }



    /**
     * <p>Move the claw's lift to position 2, the middle position.</p>
     *
     * <p>Note that this is an <u>asynchronous</u> method, meaning that the method will complete before the action does.
     * Something like {@code positionTwo(); toggleClawOpen()} will open the claw <u>right away</u>. It does <u>not</u> wait for the lift to move.
     * </p>
     * <p>
     * If you want autonomous to have synchronous-ish behaviour, use the {@link #liftMovementFinished clawMovementFinished()} method, like so:
     * <pre><code> positionTwo(), when(clawMovementFinished()) next; </code></pre>
     * </p>
     */
    public void positionTwo(){
        position = positionTwoLocation;
        hands.encodeMoveToPosition("ClawMotor", position);
    }

    /**
     * <p>Move the claw's lift to position 3, the lowest position.</p>
     *
     * <p>Note that this is an <u>asynchronous</u> method, meaning that the method will complete before the action does.
     * Something like {@code positionThree(); toggleClawOpen()} will open the claw <u>right away</u>. It does <u>not</u> wait for the lift to move.
     * </p>
     * <p>
     * If you want autonomous to have synchronous-ish behaviour, use the {@link #liftMovementFinished clawMovementFinished()} method, like so:
     * <pre><code> positionThree(), when(clawMovementFinished()) next; </code></pre>
     * </p>
     */
    public void positionThree(){
        position = positionThreeLocation;
        hands.encodeMoveToPosition("ClawMotor", position);
    }

    public void positionNeutral() {
        position = positionNeutralLocation;
        hands.encodeMoveToPosition("ClawMotor", position);
    }

    public void positionShared() {
        position = positionSharedLocation;
        hands.encodeMoveToPosition("ClawMotor", position);
    }

    /**
     * <p>Move the claw's lift back to the home position inside the chassis in order to intake objects.</p>
     *
     * <p>Note that this is an <u>asynchronous</u> method, meaning that the method will complete before the action does.
     * Something like {@code positionHome(); toggleClawOpen()} will open the claw <u>right away</u>. It does <u>not</u> wait for the lift to move.
     * </p>
     * <p>
     * If you want autonomous to have synchronous-ish behaviour, use the {@link #liftMovementFinished clawMovementFinished()} method, like so:
     * <pre><code> positionHome(), when(clawMovementFinished()) next; </code></pre>
     * </p>
     */
    public void positionHome(){
        position = positionHomeLocation;
        hands.encodeMoveToPosition("ClawMotor", position);
    }

    /**
     * <p>Move the claw back to the {@link #positionHome() home position} by scanning the sensors on the lift.</p>
     *
     * <p>This is an asynchronous method, meaning that the method will complete before the action does. Unfortunately, the {@link #liftMovementFinished()}
     * method won't work here. Try using time instead :(</p>
     *
     * <h2>
     * <p>To do that, we need a sensor connected in the code.</p>
     */

    public boolean homing(){
        //`input` will always be false for now, but we need to replace it with `sensor.isPressed()` or whichever the correct method is
        if (found) {
            hands.encodeMoveToPosition("ClawMotor", 0, 0.25);
        } else if (input.isPressed()) {
            found = true;
            positionOneLocation = -3470;
            positionTwoLocation = -5295;
            positionThreeLocation = -6893;
            positionHomeLocation = 570;
            hands.setMotorPower("ClawMotor",0);
            hands.setMotorMode("ClawMotor", DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        } else {
            hands.setMotorPower("ClawMotor", -0.75);
        }
        return found;
    }

    public void ifStableBobDownThenUp() {
        if(liftMovementFinished()) {
            hands.setMotorPower("ClawMotor", 0.2);
            hands.setMotorPower("ClawMotor", -0.2);
        }
    }

    /**
     * Checks whether or not the homing process has completed.
     *
     * @see #homing()
     * @return whether the finding process has completed
     */
    public boolean isFound() {
        return found;
    }

    /**
     * Get how open the claw is.
     * @return how open the claw is, with {@code 0} being "Completely Shut" and {@code 1} being "Completely Open"
     */
    public double getClawOpenish() {
        return hands.getServoPosition("nateClaw");
    }

    /**
     * Get the current position of the lift.
     * @return 0 for the "home position"; 1 for the top, 2 for the middle; 3 for the bottom. If the claw's at none of the above, returns -1.
     */
    public int getClawPosition() {
        if(position == positionHomeLocation) return 0;
        else if(position == positionOneLocation) return 1;
        else if(position == positionTwoLocation) return 2;
        else if(position == positionThreeLocation) return 3;

        return -1;
    }
}
