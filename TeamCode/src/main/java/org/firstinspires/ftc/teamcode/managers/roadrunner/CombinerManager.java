package org.firstinspires.ftc.teamcode.managers.roadrunner;

import androidx.annotation.VisibleForTesting;

import com.acmerobotics.roadrunner.drive.DriveSignal;
import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.roadrunner.RoadRunnerManager;
import org.firstinspires.ftc.teamcode.managers.bigArm.bigArmManager;
import org.firstinspires.ftc.teamcode.managers.sensor.SensorManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.junit.Test;

import java.util.Objects;

/**
 * Combines various advanced managers to complete complex tasks with the help of Roadrunner. Add more methods for more complex tasks... This is only the starting point. Make sure to add documentation!
 */
public class CombinerManager extends FeatureManager {
    private final RoadRunnerManager rr;
    private final bigArmManager bigArm;
    private final SensorManager sensor;
    private final TelemetryManager telemetry;
    private Thread crashDetect;

    /**
     * Creates a CombinerManager from the provided inputs
     * @param rr Roadrunner Manager {@link RoadRunnerManager}
     * @param arm bigArm Manager for arm manipulation {@link bigArmManager}
     * @param sensor Sensor Manager for sensor readings {@link SensorManager}
     * @param telemetry Telemetry Manager for logging {@link TelemetryManager}
     * @param crashDetectionEnabled Whether or not to enable crash detection
     */
    public CombinerManager(RoadRunnerManager rr, bigArmManager arm, SensorManager sensor, TelemetryManager telemetry, boolean crashDetectionEnabled){
        this.rr = rr;
        this.bigArm = arm;
        this.sensor = sensor;
        this.telemetry = telemetry;
        if(crashDetectionEnabled){
            this.crashDetect = new Thread(() -> {
                while(true){
                    if(rr.getDrive().getImu().getAngularVelocity().yRotationRate >= 5 || rr.getDrive().getImu().getAngularVelocity().zRotationRate >= 5 || Math.abs(rr.getDrive().getImu().getAcceleration().xAccel)>=100 || Math.abs(rr.getDrive().getImu().getAcceleration().yAccel)>=100){
                        stopMoving();
                    }
                    try {
                        wait(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Allows movement of the robot and the arm at the same time using multithreading.
     * @param moveID How roadrunner should move: See ids in {@link RoadRunnerManager}.moveToPosWithID()
     * @param liftID What preset the lift should move to
     */
    public void multipleMovement(int moveID, int liftID){
        if(readyForNext()){
            bigArm.ThreadedMoveToPosition(liftID);
            rr.moveToPosWithIDAsync(moveID);
            if(crashDetect != null){
                crashDetect.start();
            }
        }
    }
    public void multipleMovementFullControl(int moveID, int liftID, double speed){
        if(readyForNext()){
            bigArm.ThreadedMoveToPositionControlled(liftID, speed);
            rr.moveToPosWithIDAsync(moveID);
            if(crashDetect != null){
                crashDetect.start();
            }
        }
    }

    /**
     * Stop all movement
     */
    public void stopMoving(){
        rr.stopDrive();
        rr.getDrive().setDriveSignal(new DriveSignal());
        bigArm.stopThreadedMovement();
        crashDetect.interrupt();
    }

    /**
     * Allows the claw to open while movement is occurring
     */
    public void openDuringMovement(){
        bigArm.openHand();
    }
    /**
     * Allows the claw to close while movement is occurring
     */
    public void closeDuringMovement(){
        bigArm.closeHand();
    }

    /**
     * Tells the user whether the system is ready for the next command (all aspects of system: lift, roadrunner, sensors, etc.)
     * @return boolean stating whether the system is ready
     */
    public boolean readyForNext(){
        if(rr.notBusy() && bigArm.finishedMoving()){
            return true;
        }
        return false;
    }

    /**
     * Returns the value of a sensor asynchronously as RoadRunner handles movement. Roadrunner should be unimpeded by sensor reads.
     * @param type type of sensor (all lowercase)
     * @param nameOfSensor Name of the sensor in the hardware map
     * @return returns a float no matter what type it is... It is up to you to interpret it in the code
     */
    public float readSensor(String type, @Nullable String nameOfSensor){
        switch(type){
            case "color":
                return sensor.getColor(nameOfSensor);
            case "dist":
                return sensor.getDist(nameOfSensor);
            case "touch":
                return sensor.getTouching(nameOfSensor);
        }
        return 0f;
    }

    /**
     * Add combined macros here... In {@link RoadRunnerManager}, add more macros and add the cases in this methods with the arm movements that correspond to them...
     * @param id id of roadrunner and arm movement.
     */
    public void combinedMacro(int id){

        switch (id) {
            case 1:
                rr.activateMacro(1);
                break;
            case 2:
                rr.activateMacro(2);
                break;
            case 3:
                rr.activateMacro(3);
                break;
        }

    }

    @Override
    /**
     * Returns whether one manager is the same as the other
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CombinerManager)) return false;
        CombinerManager that = (CombinerManager) o;
        return Objects.equals(getRr(), that.getRr()) && Objects.equals(getBigArm(), that.getBigArm()) && Objects.equals(getSensor(), that.getSensor()) && Objects.equals(getTelemetry(), that.getTelemetry());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRr(), getBigArm(), getSensor(), getTelemetry());
    }

    @Override
    /**
     * Reports all aspects of the managers within the system...
     */
    public String toString() {
        return "CombinerManager{" +
                "rr=" + rr +
                ", bigArm=" + bigArm +
                ", sensor=" + sensor +
                ", telemetry=" + telemetry +
                ", crashDetect=" + crashDetect +
                '}';

    }

    /**
     * Stop the robot in an emergency
     */
    public void EMERGENCY_STOP(){
        bigArm.stopArm();
        bigArm.stopThreadedMovement();
        rr.errorInterrupt();

    }

    /**
     * Returns the manager for the lift
     * @return {@link bigArmManager}
     */
    public bigArmManager getBigArm() {
        return bigArm;
    }

    /**
     * Returns the road runner manager
     * @return {@link RoadRunnerManager}
     */
    public RoadRunnerManager getRr() {
        return rr;
    }

    /**
     * Returns the managers for all sensors in the robot
     * @return {@link SensorManager}
     */
    public SensorManager getSensor() {
        return sensor;
    }

    /**
     * Returns the manager for all logging to the app
     * @return {@link TelemetryManager}
     */
    public TelemetryManager getTelemetry() {
        return telemetry;
    }
}
