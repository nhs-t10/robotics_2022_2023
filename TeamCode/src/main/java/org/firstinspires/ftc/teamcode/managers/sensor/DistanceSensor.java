package org.firstinspires.ftc.teamcode.managers.sensor;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.auxilary.PaulMath;

public class DistanceSensor extends Sensor {
    /** The colorSensor field will contain a reference to our color sensor hardware object */
    com.qualcomm.robotcore.hardware.DistanceSensor distanceSensor;

    public DistanceSensor(com.qualcomm.robotcore.hardware.DistanceSensor sensor) {
        this.distanceSensor = sensor;
    }

    public boolean isSpecial() {
        return distanceSensor.getDistance(DistanceUnit.INCH) > 1;

    }

    @Override
    public float getNumberValue() {
        return (float) distanceSensor.getDistance(DistanceUnit.INCH);
    }

    @Override
    public float[] getFloatArray() {
        update();
        return new float[] { getNumberValue() };
    }

    @Override
    public boolean getBool() {
        update();
        return isSpecial();
    }
}
