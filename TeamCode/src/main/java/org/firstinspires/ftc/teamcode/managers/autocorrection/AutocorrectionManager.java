package org.firstinspires.ftc.teamcode.managers.autocorrection;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.statements.AfterStatement;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.imu.ImuManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;
//THE DEAD RECKONER!!!!!!!!!!
public class AutocorrectionManager extends FeatureManager {
    MovementManager driving;
    ImuManager imu;

    private int lastFL;
    private int lastFR;
    private int lastBL;
    private int lastBR;
    private float X;
    private float Y;
    private float lastX;
    private float lastY;
    private float Theta;
    private float[] correction;
    public AutocorrectionManager(MovementManager driving, ImuManager imu){
        this.driving =driving;
        this.imu = imu;
    }
    public void calculateCorrection(){
        //Compute change in encoder positions


        int fl = driving.frontLeft.getCurrentPosition();
        int fr = driving.frontRight.getCurrentPosition();
        int bl = driving.backLeft.getCurrentPosition();
        int br = driving.backRight.getCurrentPosition();
        int delt_fl = fl - lastFL;
        int delt_fr = fr - lastFR;
        int delt_bl = bl - lastBL;
        int delt_br = br - lastBR;
//Compute displacements for each wheel
        float displ_fl = PaulMath.encoderDistanceCm(delt_fl);
        float displ_fr = PaulMath.encoderDistanceCm(delt_fr);
        float displ_bl = PaulMath.encoderDistanceCm(delt_bl);
        float displ_br = PaulMath.encoderDistanceCm(delt_br);
//Compute the average displacement in order to untangle rotation from displacement
        float displ_average = (float) ((displ_fl + displ_fr + displ_bl + displ_br) / 4.0);
//Compute the component of the wheel displacements that yield robot displacement
        float dev_fl = displ_fl - displ_average;
        float dev_fr = displ_fr - displ_average;
        float dev_bl= displ_bl - displ_average;
        float dev_br = displ_br - displ_average;
//Compute the displacement of the holonomic drive, in robot reference frame
        //float twoSqrtTwo = (float) (2.0*Math.sqrt(2));
        float delt_Xr = 0;
        float delt_Yr = 0;
        if(displ_fr == displ_bl&&displ_bl == displ_fl&&displ_fl == displ_br){
            logger.log("Displacements match (Moving forward or backward");
            delt_Yr = displ_average;
            delt_Xr = 0;
        }else if(displ_fl == displ_br&&displ_br == -displ_fr&&displ_fr == displ_bl){
            logger.log("Displacements inverse (Moving left or right)");
            delt_Xr = (float) ((float)displ_br*0.675);
        }
//Move this holonomic displacement from robot to field frame of reference
        float robotTheta = imu.getThirdAngleOrientation();
        float cosTheta = (float)Math.cos(robotTheta);
        float sinTheta = (float)Math.sin(robotTheta);

        //TODO: DEVIATIONS OR DISPLACEMENTS?
        float[] vhr = PaulMath.omniCalcInverse(dev_fl, dev_fr, dev_bl, dev_br, true);
        float delt_Xf = vhr[1];
        float delt_Yf = vhr[0];
//Update the position
        lastX = X;
        lastY = Y;
        X = lastX + delt_Xf;
        Y = lastY + delt_Yf;
        Theta = robotTheta;
        lastFL = fl;
        lastFR = fr;
        lastBL = bl;
        lastBR = br;
        correction = new float[]{X, Y, Theta};
    }
    public float[] getCorrection(){
        return correction;
    }
    public void resetCorrection(){
        lastX = 0;
        lastY = 0;
        lastFL = 0;
        lastFR = 0;
        lastBL = 0;
        lastBR = 0;
        X=0;
        Y=0;
        Theta=0;
    }
    public void doCorrection(float targetX, float targetY, float targetTheta){
        //delta = final - initial
        float deltaX = targetX - correction[0];
        float deltaY = targetY - correction[1];
        float deltaTheta = targetTheta - correction[2];
        //other axes (y, theta)
        //x and y have a much bigger range than theta, so they need a larger coefficient
        driving.driveOmni(deltaX*0.1f, deltaY*0.1f, deltaTheta*0.05f); //this is using driveOmni like it's HVR; i think it's VHR though
    }
}
