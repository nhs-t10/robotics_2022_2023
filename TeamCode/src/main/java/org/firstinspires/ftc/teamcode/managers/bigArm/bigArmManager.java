package org.firstinspires.ftc.teamcode.managers.bigArm;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;

public class bigArmManager extends FeatureManager {
    ManipulationManager hands;
    final int FloorToLow = 0; //To be filled in later
    final int LowToMiddle = 0; //To be filled in later
    final int MiddleToHigh = 0; //To be filled in later
    int currentPosition = 0; //Tracks whether we are floor, low, middle, and high
    int currentEncoderTicks = 0;

    public bigArmManager(ManipulationManager hands){
        this.hands = hands;
    }

    public void extendArm(){
        hands.setMotorPower("monkeyShoulder", 1);
    }

    public void retractArm(){
        hands.setMotorPower("monkeyShoulder", -0.75);
        //Done in teleop
        //closeHand();
    }
    public void stopArm(){hands.setMotorPower("monkeyShoulder",0);}
    public void openHand(){
        hands.setServoPosition("monkeyHand", 0.1);
    }
    public void closeHand(){
        hands.setServoPosition("monkeyHand", 0.35);
    }

    /*
    public void rotateShoulderRight() {
        if (currentPosition > 1) {
            closeHand();
            hands.setServoPower("monkeyShoulderBladeLeft", -1);
            hands.setServoPower("monkeyShoulderBladeRight", 1);
        }
    }
    public void rotateShoulderLeft() {
        if (currentPosition > 1) {
            closeHand();
            hands.setServoPower("monkeyShoulderBladeLeft", 1);
            hands.setServoPower("monkeyShoulderBladeRight", -1);
        }
    }
     */

    public boolean finishedMoving(){
        return !hands.hasEncodedMovement("monkeyShoulder");
    }

    public void setPositionFloorLocation(){
        currentEncoderTicks = (int)hands.getMotorPosition("monkeyShoulder");
        if (currentPosition == 1){
            hands.encodeMoveToPosition("monkeyShoulder", (currentEncoderTicks + FloorToLow));
        }
        else if (currentPosition == 2){
            hands.encodeMoveToPosition("monkeyShoulder", (currentEncoderTicks + FloorToLow + LowToMiddle));
        }
        else if (currentPosition == 3){
            hands.encodeMoveToPosition("monkeyShoulder", (currentEncoderTicks + FloorToLow + LowToMiddle + MiddleToHigh));
        }
        currentPosition = 0;
    }

    public void setPositionLowLocation(){
        currentEncoderTicks = (int)hands.getMotorPosition("monkeyShoulder");
        if (currentPosition == 0){
            hands.encodeMoveToPosition("monkeyShoulder", (currentEncoderTicks - FloorToLow));
        }
        else if (currentPosition == 2){
            hands.encodeMoveToPosition("monkeyShoulder", (currentEncoderTicks + LowToMiddle));
        }
        else if (currentPosition == 3){
            hands.encodeMoveToPosition("monkeyShoulder", (currentEncoderTicks + LowToMiddle + MiddleToHigh));
        }
        currentPosition = 1;
    }

    public void setPositionMiddleLocation(){
        if (currentPosition == 0){
            hands.encodeMoveToPosition("monkeyShoulder", (currentEncoderTicks - FloorToLow - LowToMiddle));
        }
        else if (currentPosition == 1){
            hands.encodeMoveToPosition("monkeyShoulder", (currentEncoderTicks - LowToMiddle));
        }
        else if (currentPosition == 3){
            hands.encodeMoveToPosition("monkeyShoulder", (currentEncoderTicks + MiddleToHigh));
        }
        currentPosition = 2;
    }

    public void setPositionHighLocation(){
        if (currentPosition == 0){
            hands.encodeMoveToPosition("monkeyShoulder", (currentEncoderTicks - FloorToLow - LowToMiddle - MiddleToHigh));
        }
        else if (currentPosition == 1){
            hands.encodeMoveToPosition("monkeyShoulder", (currentEncoderTicks - LowToMiddle - MiddleToHigh));
        }
        else if (currentPosition == 2) {
            hands.encodeMoveToPosition("monkeyShoulder", (currentEncoderTicks - MiddleToHigh));
        }
        currentPosition = 3;
    }
}
