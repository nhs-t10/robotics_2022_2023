package org.firstinspires.ftc.teamcode.managers.manipulation;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.auxilary.clocktower.Clocktower;
import org.firstinspires.ftc.teamcode.auxilary.clocktower.ClocktowerCodes;
import org.firstinspires.ftc.teamcode.auxilary.pid.AutotuningPIDController;
import org.firstinspires.ftc.teamcode.auxilary.pid.NormalizedPIDController;
import org.firstinspires.ftc.teamcode.auxilary.pid.PIDController;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

public class PIDlessEncoderMovementThread extends Thread {
    DcMotor[] motors;
    private int[] targets;
    private boolean[] isMoving;
    private boolean[] hasTarget;
    private double[] powerCoefs;
    //checks
    private float[] direction;
    private float[] lastDeltas;

    private float[][] pidCoefs;

    PIDController[] controllers;

    private double slowRange;

    private boolean running;

    public PIDlessEncoderMovementThread(DcMotor[] motors, String[] motorNames) {
        this.motors = motors;
        this.targets = new int[motors.length];
        this.hasTarget = new boolean[motors.length];
        this.isMoving = new boolean[motors.length];
        this.powerCoefs = new double[motors.length];
        
        this.lastDeltas = new float[motors.length];

        this.direction = new float[motors.length];
        for (int i = 0; i < motors.length; i++) direction[i] = 1;

        this.controllers = new PIDController[motors.length];

        this.pidCoefs = new float[motors.length][];
        for(int i = 0; i < motorNames.length; i++) {
            this.pidCoefs[i] = FeatureManager.getRobotConfiguration().pidCoefs.get(motorNames[i]);
        }

        running = true;

        this.slowRange = FeatureManager.getRobotConfiguration().encoderTicksPerRotation;
    }

    @Override
    public void run() {
        try {
            while(FeatureManager.isOpModeRunning) {
                for(int i = 0; i < motors.length; i++) {
                    if(hasTarget[i]) {
                        if(this.controllers[i] == null) initPidController(i);

                        if(motors[i].getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                        int currentPos = motors[i].getCurrentPosition();

                        controllers[i].setTarget(targets[i]);

                        double power = powerCoefs[i] * controllers[i].getControl(currentPos);

                        motors[i].setPower(Range.clip(power, -0.65, 0.65));

                        isMoving[i] = !controllers[i].isStable(currentPos);
                    }
                }
                Clocktower.time(ClocktowerCodes.MOTOR_ENCODER_THREAD);
                Thread.yield();
            }
        } catch(Throwable t) {
            FeatureManager.logger.log("Silent error in 'PIDlessEncoderMovementThread'");
            FeatureManager.logger.log(t.toString());
        }
    }

    private void initPidController(int i) {
        float[] pidc = pidCoefs[i];
        if(pidc == null) {
            this.controllers[i] = new AutotuningPIDController(-1.0 / powerCoefs[i],
                    1.0 / powerCoefs[i],
                    ManipulationManager.ENCODER_TICK_VALUE_TOLERANCE);
        } else {
            this.controllers[i] = new NormalizedPIDController(pidc[0], pidc[1], pidc[2], ManipulationManager.ENCODER_TICK_VALUE_TOLERANCE);
        }
    }

    public boolean isMoving(int i) {
        return isMoving[i];
    }

    public void move(int i, int target, double power) {
        hasTarget[i] = true;
        powerCoefs[i] = power;
        targets[i] = target;
    }

    public int getTarget(int i) {
        return targets[i];
    }
    public void cancelMovement(int i) {
        hasTarget[i] = false;
    }

    public void incrementTarget(int i, int increment) {
        if(!hasTarget[i]) {
            hasTarget[i] = true;
            targets[i] = motors[i].getCurrentPosition();
        }
        powerCoefs[i] = ManipulationManager.DEFAULT_ENCODED_MOVEMENT_POWER;
        targets[i] += increment;
    }
}
