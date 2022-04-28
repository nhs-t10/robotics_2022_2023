package org.firstinspires.ftc.teamcode.auxilary.pid;

public abstract class PIDController {
    public abstract void setTarget(float f);
    public abstract float getControl(float current);
    public abstract boolean isStable(float current);
}
