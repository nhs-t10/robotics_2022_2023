package org.firstinspires.ftc.teamcode.managers.bigArm;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;

public class bigArmManager extends FeatureManager {
    ManipulationManager hands;
    final int positionFloorLocation = 0; //To be filled in later
    final int positionLowLocation = 0; //To be filled in later
    final int positionMiddleLocation = 0; //To be filled in later
    final int positionHighLocation = 0; //To be filled in later
    int currentPosition = 0; //Tracks whether we are floor, low, middle, and high

    public bigArmManager(ManipulationManager hands){
        this.hands = hands;
    }

    public void extendArm(){
        hands.setMotorPower("monkeyShoulder", 0.75);
    }

    public void retractArm(){
        hands.setMotorPower("monkeyShoulder", -0.75);
        closeHand();
    }
    public void stopArm(){hands.setMotorPower("monkeyShoulder",0);}
    public void openHand(){
        hands.setServoPosition("monkeyHand", -1);
    }
    public void closeHand(){
        hands.setServoPosition("monkeyHand", 0.35);
    }
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

    public void setPositionFloorLocation(){
        hands.encodeMoveToPosition("monkeyShoulder", positionFloorLocation);
        currentPosition = 0;
    }

    public void setPositionLowLocation(){
        hands.encodeMoveToPosition("monkeyShoulder", positionLowLocation);
        currentPosition = 1;
    }

    public void setPositionMiddleLocation(){
        hands.encodeMoveToPosition("monkeyShoulder", positionMiddleLocation);
        currentPosition = 2;
    }

    public void setPositionHighLocation(){
        hands.encodeMoveToPosition("monkeyShoulder", positionHighLocation);
        currentPosition = 3;
    }
}
