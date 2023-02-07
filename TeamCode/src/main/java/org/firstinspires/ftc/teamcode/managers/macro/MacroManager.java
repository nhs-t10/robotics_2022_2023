package org.firstinspires.ftc.teamcode.managers.macro;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.imu.ImuManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;

public class MacroManager extends FeatureManager {
    MovementManager driver;
    ImuManager imu;
    public boolean movingAndRotating=false;
    //Note: this is x,y or h,v while driveOmni uses v,h
    private float[] driveVector={0f,0f};
    private float power=0f;
    private Thread macroThread;

    public MacroManager(MovementManager driver,ImuManager imu){
        this.driver=driver;
        this.imu=imu;
        macroThread=new Thread(()->{
            while(isOpModeRunning){
                if(movingAndRotating){
                    float startAngle=imu.getAngle();
                    while(Math.abs(imu.getAngle()-startAngle)<180){
                        driveVector=PaulMath.rotateVector(driveVector,Math.abs(imu.getAngle()-startAngle));
                        driver.driveOmni(driveVector[1],driveVector[0],power);
                        if(!isOpModeRunning){
                            break;
                        }
                    }
                    movingAndRotating=false;
                }
            }
        });
    }

    public void moveAndRotate(float v, float h,float power){
        if (macroThread.getState() == Thread.State.NEW){
            macroThread.start();
        }
        this.driveVector=new float[] {h,v};
        this.power=Math.abs(power);
        if (!movingAndRotating){
            movingAndRotating=true;
        }


    }


}
