package org.firstinspires.ftc.teamcode.unitTests.dummy;

import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

public class DummyNormalizedColorSensor implements NormalizedColorSensor {
    public NormalizedRGBA rgba;

    @Override
    public NormalizedRGBA getNormalizedColors() {
        if(this.rgba != null) return this.rgba;
        else return new NormalizedRGBA();
    }

    public void setNormalizedColors(NormalizedRGBA r) {
        this.rgba = r;
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
