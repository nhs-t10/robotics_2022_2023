package org.firstinspires.ftc.teamcode.managers.apple;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;

public class AppleManager extends FeatureManager {
    ManipulationManager fruitGrabber;

    public AppleManager(ManipulationManager f) {
        fruitGrabber = f;
    }

    public void grab() {
        fruitGrabber.setServoPosition("AppleHand", 1);
    }

    public void lift(int level) {
        if(level == 1) {
            fruitGrabber.setMotorTargetPosition("AppleHoist", 2000);
        } else if(level == 2) {
            fruitGrabber.setMotorTargetPosition("AppleHoist", 3000);
        }
    }

    public void reset() {
        fruitGrabber.setMotorTargetPosition("AppleHoist", 0);
    }

    public void resetDrop() {
        fruitGrabber.setMotorTargetPosition("AppleHoist", 0);
        fruitGrabber.setServoPosition("AppleHand", 0);
    }

    public void drop() {
        fruitGrabber.setServoPosition("AppleHand", 0);
    }

    public int getLevel() {
        if(fruitGrabber.getMotorTargetPosition("AppleHoist") == 2000) {
            return 1;
        } else {
            return 2;
        }
    }
}
