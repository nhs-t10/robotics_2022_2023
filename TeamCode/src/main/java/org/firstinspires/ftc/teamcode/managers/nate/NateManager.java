package org.firstinspires.ftc.teamcode.managers.nate;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;

public class NateManager extends FeatureManager {
    private boolean input;
    private boolean found;
    private int position;
    ManipulationManager hands;

    public NateManager(ManipulationManager hands){
       this.hands = hands;
    }

    public void foldOut(){

    }

    public void positionOne(){
        hands.setMotorMode("outake", DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hands.setMotorMode("outake", DcMotor.RunMode.RUN_TO_POSITION);

        position = 1;
    }

    public void positionTwo(){
        hands.setMotorMode("outake", DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hands.setMotorMode("outake", DcMotor.RunMode.RUN_TO_POSITION);

        position = 2;
    }

    public void positionThree(){
        hands.setMotorMode("outake", DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hands.setMotorMode("outake", DcMotor.RunMode.RUN_TO_POSITION);

        position = 3;
    }

    public void homing(){
        if (position == 1) {
            boolean found=true;
            hands.setMotorPower("outake",0);
            hands.setMotorMode("outake", DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            // Move the distance from Pos1 to Home
        }
        else if (position == 2){
            boolean found=true;
            hands.setMotorPower("outake",0);
            hands.setMotorMode("outake", DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            // Move the distance from Pos2 to Home
        }
        else if (position == 3){
            boolean found=true;
            hands.setMotorPower("outake",0);
            hands.setMotorMode("outake", DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            // Move the distance from Pos3 to Home
        }
        else {
            FeatureManager.logger.log("This message should not appear, if it does, something is wrong with NateManager");
        }
        if (found) {
            hands.setMotorPower("outake",-1);
        } else {
            hands.setMotorPower("outake", PaulMath.proportionalPID((float) hands.getMotorPosition("outake"),0));
        }
    }
}
