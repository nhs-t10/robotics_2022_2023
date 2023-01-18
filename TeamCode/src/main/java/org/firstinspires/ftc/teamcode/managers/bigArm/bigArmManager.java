package org.firstinspires.ftc.teamcode.managers.bigArm;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;

public class bigArmManager extends FeatureManager {
    ManipulationManager hands;
    public final int lowPosition = 100; //The distance from the floor position to the lowest tower's height
    public final int middlePosition = 1951; //The distance from the floor position to the middle tower's height
    public final int highPosition = 2956; //The distance from the floor position  to the high tower's height
    int currentPosition = 0; //Tracks whether we are floor, low, middle, and high

    public bigArmManager(ManipulationManager hands){
        this.hands = hands;
    }

    public void extendArm(double power){
        hands.setMotorPower("monkeyShoulder", power);
    }

    public void retractArm(double power){
        hands.setMotorPower("monkeyShoulder", -power*0.75);
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

    }

    public void setPositionLowLocation(){

    }

    public void setPositionMiddleLocation(){

    }

    public void setPositionHighLocation(){

    }
}
