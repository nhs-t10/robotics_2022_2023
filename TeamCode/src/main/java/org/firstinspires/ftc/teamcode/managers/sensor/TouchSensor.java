package org.firstinspires.ftc.teamcode.managers.sensor;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.auxilary.PaulMath;

public class TouchSensor extends Sensor {
    /** The colorSensor field will contain a reference to our color sensor hardware object */
    com.qualcomm.robotcore.hardware.TouchSensor distanceSensor;

    public TouchSensor(com.qualcomm.robotcore.hardware.TouchSensor sensor) {
        this.distanceSensor = sensor;
    }

    public boolean isSpecial() {
        return distanceSensor.isPressed();

    }

    @Override
    public float getNumberValue() {
        return distanceSensor.isPressed() ? 1 : 0;
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
