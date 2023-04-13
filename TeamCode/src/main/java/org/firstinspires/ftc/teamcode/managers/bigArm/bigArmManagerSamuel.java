package org.firstinspires.ftc.teamcode.managers.bigArm;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;

@Config(value = "Samuel Arm")
public class bigArmManagerSamuel extends bigArmManager {

    DcMotor hands;
    ManipulationManager manipulator;

    public static int floorPosition = 250; //The position of the floor
    public static int lowPosition= 1250; //The position of the low tower
    public static int middlePosition = 2081; //The position of the middle tower
    public static int highPosition = 2956; //The position of the high tower
    public static int[] positions = {floorPosition, lowPosition, middlePosition, highPosition, highPosition+30, lowPosition-525, lowPosition-575};
    public double direction = 1.0;
    public boolean doOnce=false;
    public int towerPos = 0;
    int startingPos = 0;
    private boolean linSlidesMoving = false;
    private double speed = 1;
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
                hands.setPower(direction);
                while (Math.abs(getPosition() - startingPos) <= Math.abs(targetPos - startingPos) - 25 && linSlidesMoving) {
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
    private Thread linSlidesSpeedControl = new Thread(() -> {
        while(true){
            if(linSlidesMoving){
                startingPos = getPosition();
                int targetPos = positions[this.index];
                if (startingPos > targetPos) {
                    direction = -0.75*speed;
                } else {
                    direction = 1*speed;
                }
                hands.setPower(direction);
                while (Math.abs(getPosition() - startingPos) <= Math.abs(targetPos - startingPos) - 25 && linSlidesMoving) {
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

    public bigArmManagerSamuel(DcMotor hands, ManipulationManager manipulator){
        super(manipulator);
        this.hands = hands;
        this.manipulator = manipulator;
    }
    @Override
    public void extendArm(double power){
        hands.setPower(power);
    }
    @Override
    public void retractArm(double power){
        hands.setPower(power * -0.75);
    }
    @Override
    public void stopArm(){hands.setPower(0);}
    @Override
    public void openHand(){
        manipulator.setServoPosition("monkeyHand", 0.35);
    }
    @Override
    public void openHandAuto(){
        manipulator.setServoPosition("monkeyHand", 0.4);
    }
    @Override
    public void openHandTeleop(){manipulator.setServoPosition("monkeyHand", 0.30);}
    @Override
    public void closeHand(){manipulator.setServoPosition("monkeyHand", 0.5);}

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
    @Override
    public boolean finishedMoving(){
        return (Math.abs(hands.getPower())<0.1f);
    }
    @Override
    public int getPosition(){ return (int)hands.getCurrentPosition(); }
    @Override
    public boolean setPositionFloorLocation(){
//        if(!doOnce){
//            closeHand();
//            doOnce=true;
//        }
        retractArm(1);
        if (hands.getCurrentPosition() <= floorPosition + 25){
            stopArm();
            //openHand();
            towerPos=0;
            return false;
        }
        return true;
    }
    @Override
    public void resetDoOnce(){
        doOnce=false;
    }
    @Override
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
        hands.setPower(direction);
        if (Math.abs(hands.getCurrentPosition()-startingPos)>=Math.abs(lowPosition-startingPos)-25){
            stopArm();
            doOnce=false;
            return false;
        }
        return true;
    }
    @Override
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
        hands.setPower(direction);
        if (Math.abs(hands.getCurrentPosition()-startingPos)>=Math.abs(middlePosition-startingPos)-25){
            stopArm();
            doOnce=false;
            return false;
        }
        return true;
    }
    @Override
    public boolean setPositionHighLocation(){
        extendArm(1);
        if (hands.getCurrentPosition() >= highPosition - 25){
            stopArm();
            return false;
        }
        return true;
    }
    @Override
    public void ThreadedMoveToPosition(int index){
        if(!linSlidesSpeedControl.isAlive()) {
            //For reference:
            //  positions = {floorPosition,lowPosition,middlePosition,highPosition};
            if (linSlides.getState() == Thread.State.NEW) {
                linSlides.start();
            }
            if (!linSlidesMoving) {
                linSlidesMoving = true;
            }
            this.index = index;
        }
    }
    @Override
    public void ThreadedMoveToPositionControlled(int index, double speed){
        //For reference:
        //  positions = {floorPosition,lowPosition,middlePosition,highPosition};

        if(!linSlides.isAlive()) {

            if (linSlidesSpeedControl.getState() == Thread.State.NEW) {
                linSlidesSpeedControl.start();
            }
            if (!linSlidesMoving) {
                linSlidesMoving = true;
            }
            this.index = index;
            this.speed = speed;
        }
    }
    @Override
    public void stopThreadedMovement(){linSlidesMoving=false;}
}
