package org.firstinspires.ftc.teamcode.managers.sensor;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;

public class ColorSensor extends Sensor {
    /** The colorSensor field will contain a reference to our color sensor hardware object */
    NormalizedColorSensor colorSensor;
    boolean weShouldRead, weveInitiated;
    int colorReturned;
    float[] hsvValues = new float[3];
    public int runCount = 0;

    public ColorSensor(NormalizedColorSensor sensor) {
        //   this.hardwareMap = _hardwareMap; //since we don't get the hardwaremap by default-- this isn't an OpMode-- we have to set it manually
        this.weveInitiated = true; //We have initiated the code
        this.colorReturned = 0;
        this.weShouldRead = true;
        this.colorSensor = sensor;
    }

    public boolean isSpecial() {
        return Color.alpha(this.colorReturned) >= 200;

    }

    @Override
    public float getNumberValue() {
        update();
        return this.colorReturned;
    }

    @Override
    public float[] getFloatArray() {
        update();
        return this.hsvValues;
    }

    @Override
    public boolean getBool() {
        update();
        return isSpecial();
    }

    @Override
    public void update() {
        this.runSample();
    }

    public void runSample() {
        // If possible, turn the light on in the beginning (it might already be on anyway,
        // we just make sure it is if we can).
        if (this.colorSensor instanceof SwitchableLight) {
            ((SwitchableLight)this.colorSensor).enableLight(true);
        }

        // Read the sensor
        NormalizedRGBA colors = this.colorSensor.getNormalizedColors();

        //Convert the color to HSV
        Color.colorToHSV(colors.toColor(), this.hsvValues);

        //normalize the colors-- make it so brightness won't affect our readout (much)
        float max = Math.max(Math.max(colors.red, colors.green), Math.max(colors.blue, colors.alpha));
        colors.red /= max;
        colors.green /= max;
        colors.blue /= max;


        //set the colorReturned variable so it can be used by the other methods
        this.colorReturned = colors.toColor();
    }
}
