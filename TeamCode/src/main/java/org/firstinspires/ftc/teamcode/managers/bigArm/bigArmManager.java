package org.firstinspires.ftc.teamcode.managers.bigArm;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;

public class bigArmManager extends FeatureManager {
    ManipulationManager hands;
    public final int floorPosition = 250; //The position of the floor
    public final int lowPosition= 1250; //The position of the low tower
    public final int middlePosition = 2081; //The position of the middle tower
    public final int highPosition = 2956; //The position of the high tower
    public final int[] positions = {floorPosition, lowPosition, middlePosition, highPosition, highPosition+70};
    public double direction = 1.0;
    public boolean doOnce=false;
    public int towerPos = 0;
    int startingPos = 0;
    private boolean linSlidesMoving = false;
    private Thread linSlides = new Thread(() -> {
        while(true){
            if(linSlidesMoving){
                startingPos = getPosition();
                int targetPos = positions[this.index];
                if (startingPos > targetPos) {
                    direction = -0.75;
                } else {
                    direction = 1;
                }
                hands.setMotorPower("monkeyShoulder", direction);
                while (Math.abs(getPosition() - startingPos) <= Math.abs(targetPos - startingPos) - 25) {
                    if (!FeatureManager.isOpModeRunning) {
                        stopArm();
                        break;
                    }
                }
                stopArm();
                linSlidesMoving = false;
            }

        }


    });
    private int index;

    public bigArmManager(ManipulationManager hands){
        this.hands = hands;
    }

    public void extendArm(double power){
        hands.setMotorPower("monkeyShoulder", power);
    }

    public void retractArm(double power){
        hands.setMotorPower("monkeyShoulder", power * -0.75);
    }
    public void stopArm(){hands.setMotorPower("monkeyShoulder",0);}
    public void openHand(){
        hands.setServoPosition("monkeyHand", 0.1);
    }
    public void closeHand(){
        hands.setServoPosition("monkeyHand", 0.45);
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
        return (Math.abs(hands.getMotorPower("monkeyShoulder"))<0.1f);
    }

    public int getPosition(){ return (int)hands.getMotorPosition("monkeyShoulder"); }

    public boolean setPositionFloorLocation(){
//        if(!doOnce){
//            closeHand();
//            doOnce=true;
//        }
        retractArm(1);
        if (hands.getMotorPosition("monkeyShoulder") <= floorPosition + 25){
            stopArm();
            //openHand();
            towerPos=0;
            return false;
        }
        return true;
    }
    public void resetDoOnce(){
        doOnce=false;
    }
    public boolean setPositionLowLocation(){
        towerPos = getPosition();

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
        towerPos = getPosition();

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

    public void ThreadedMoveToPosition(int index){
        //For reference:
        //  positions = {floorPosition,lowPosition,middlePosition,highPosition};
        if(linSlides.getState() == Thread.State.NEW){
            linSlides.start();
        }
        if(!linSlidesMoving) {
            linSlidesMoving = true;
        }
        this.index = index;

    }
}
