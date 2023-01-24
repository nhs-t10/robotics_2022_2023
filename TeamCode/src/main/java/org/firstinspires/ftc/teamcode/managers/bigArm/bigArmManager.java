package org.firstinspires.ftc.teamcode.managers.bigArm;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;

public class bigArmManager extends FeatureManager {
    ManipulationManager hands;
    public final int floorPosition = 250; //The position of the floor
    public final int lowPosition=1450; //The position of the low tower
    public final int middlePosition = 2181; //The position of the middle tower
    public final int highPosition = 2956; //The position of the high tower
    public double direction =1.0;
    public boolean doOnce=false;
    public int towerPos = 0;
    int startingPos = 0;

    public bigArmManager(ManipulationManager hands){
        this.hands = hands;
    }

    public void extendArm(double power){
        hands.setMotorPower("monkeyShoulder", power);
    }

    public void retractArm(double power){
        hands.setMotorPower("monkeyShoulder", power * -0.75);
        closeHand();
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

    public boolean setPositionFloorLocation(){
        if(!doOnce){
            closeHand();
            doOnce=true;
        }
        retractArm(1);
        if (hands.getMotorPosition("monkeyShoulder") <= floorPosition + 25){
            stopArm();
            openHand();
            towerPos=0;
            return false;
        }
        return true;
    }
    public void resetDoOnce(){
        doOnce=false;
    }
    public boolean setPositionLowLocation(){
        towerPos = (int)hands.getMotorPosition("monkeyShoulder");

        if(!doOnce){
            startingPos=towerPos;
            if(towerPos>lowPosition){
                direction=-0.75;
            } else {
                direction=1;
            }
            doOnce=true;
        }
        hands.setMotorPower("monkeyShoulder",direction);
        if (Math.abs(hands.getMotorPosition("monkeyShoulder")-startingPos)>=Math.abs(lowPosition-startingPos)-25){
            stopArm();
            doOnce=false;
            return false;
        }
        return true;
    }

    public boolean setPositionMiddleLocation(){
        towerPos = (int)hands.getMotorPosition("monkeyShoulder");

        if(!doOnce){
            startingPos=towerPos;
            if(towerPos>middlePosition){
                direction=-0.75;
            } else {
                direction=1;
            }
            doOnce=true;
        }
        hands.setMotorPower("monkeyShoulder",direction);
        if (Math.abs(hands.getMotorPosition("monkeyShoulder")-startingPos)>=Math.abs(middlePosition-startingPos)-25){
            stopArm();
            doOnce=false;
            return false;
        }
        return true;
    }

    public boolean setPositionHighLocation(){
        extendArm(1);
        if (hands.getMotorPosition("monkeyShoulder") >= highPosition - 25){
            stopArm();
            return false;
        }
        return true;
    }
}
