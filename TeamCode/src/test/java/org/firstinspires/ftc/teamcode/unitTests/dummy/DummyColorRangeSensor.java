package org.firstinspires.ftc.teamcode.unitTests.dummy;

import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DummyColorRangeSensor implements ColorRangeSensor {
    @Override
    public int red() {
        return 0;
    }

    @Override
    public int green() {
        return 0;
    }

    @Override
    public int blue() {
        return 0;
    }

    @Override
    public int alpha() {
        return 0;
    }

    @Override
    public int argb() {
        return 0;
    }

    @Override
    public double getLightDetected() {
        return 0;
    }

    @Override
    public double getRawLightDetected() {
        return 0;
    }

    @Override
    public double getRawLightDetectedMax() {
        return 0;
    }

    @Override
    public void enableLed(boolean enable) {

    }

    @Override
    public String status() {
        return null;
    }

    @Override
    public void setI2cAddress(I2cAddr newAddress) {

    }

    @Override
    public I2cAddr getI2cAddress() {
        return null;
    }

    @Override
    public double getDistance(DistanceUnit unit) {
        return 0;
    }

    @Override
    public NormalizedRGBA getNormalizedColors() {
        return null;
    }

    @Override
    public float getGain() {
        return 0;
    }

    @Override
    public void setGain(float newGain) {

    }

    @Override
    public Manufacturer getManufacturer() {
        return null;
    }

    @Override
    public String getDeviceName() {
        return null;
    }

    @Override
    public String getConnectionInfo() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {

    }

    @Override
    public void close() {

    }
}
