package org.firstinspires.ftc.teamcode.managers.bigArm;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;

public class bigArmManager extends FeatureManager {
    ManipulationManager hands;
    private boolean armStatus = false;
    final int positionFloorLocation = 0; //To be filled in later
    final int positionLowLocation = 0; //To be filled in later
    final int positionMiddleLocation = 0; //To be filled in later
    final int positionHighLocation = 0; //To be filled in later

    public bigArmManager(ManipulationManager hands){
        this.hands = hands;
    }

    public void extendArm(){
        hands.setMotorPower("monkeyShoulder", 0.5);
    }

    public void retractArm(){
        hands.setMotorPower("monkeyShoulder", -0.5);
    }

    public void toggleArm(){
        if (armStatus) {
            hands.setServoPosition("monkeyHand", 0.5);
            armStatus = false;
        }
        else {
            hands.setServoPosition("monkeyHand", -0.5);
            armStatus = true;
        }
    }

    public void setPositionFloorLocation(){
        hands.encodeMoveToPosition("monkeyShoulder", positionFloorLocation);
    }

    public void setPositionLowLocation(){
        hands.encodeMoveToPosition("monkeyShoulder", positionLowLocation);
    }

    public void setPositionMiddleLocation(){
        hands.encodeMoveToPosition("monkeyShoulder", positionMiddleLocation);
    }

    public void setPositionHighLocation(){
        hands.encodeMoveToPosition("monkeyShoulder", positionHighLocation);
    }
}
