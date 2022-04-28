package org.firstinspires.ftc.teamcode.auxilary.pid;

import org.firstinspires.ftc.teamcode.auxilary.RobotTime;

public class NormalizedPIDController extends PIDController {
    private final float stability;
    public float kp, ki, kd;

    public float target;

    private float lastError;
    private long lastTime;

    private float errSum = 0;
    private float errSlope;

    public NormalizedPIDController(float kp, float ki, float kd, float stability) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.stability = stability;
    }

    public void setTarget(float t) {
        target = t;
    }

    private float pidCalc(float e) {
        return kp * e + ki * errSum + kd * errSlope;
    }

    public float getControl(float current) {
        float deltaTime = getDeltaTime();
        float error = getError(current);

        //yes, MATHEMATICALLY, integrating and deriving *should* get time, but that
        //causes errors, so we model each loop as taking 1 "time-step"
        updateIntegral(error);
        updateDerivative(error);

        lastError = error;

        if(isStable(current)) errSum = 0f;

        //in order to work intuitively (e.g. too big -> negative control; too small -> positive control), it needs to be multiplied by -1.
        return -1 * pidCalc(error);
    }

    public boolean isStable(float current) {
        return Math.abs(getError(current)) < stability;
        //&& Math.abs(errSlope) < Math.sqrt(stability
    }

    protected float getError(float current) {
        return target - current;
    }

    private void updateDerivative(float error) {
        errSlope = (error - lastError);
    }

    private void updateIntegral(float error) {
        errSum += error;
    }

    private float getDeltaTime() {
        long now = RobotTime.nanoTime();
        long delt = now - lastTime;
        lastTime = now;
        return (float)delt;
    }
}
