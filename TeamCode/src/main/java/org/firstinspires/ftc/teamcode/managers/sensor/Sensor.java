package org.firstinspires.ftc.teamcode.managers.sensor;

public abstract class Sensor {
    public abstract boolean isSpecial();
    public abstract float getNumberValue();
    public abstract float[] getFloatArray();
    public abstract boolean getBool();
    public void update() {
    }
}
