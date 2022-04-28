package org.firstinspires.ftc.teamcode.unitTests.dummy;

import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;

public class DummyTouchSensor implements TouchSensor {
    @Override
    public double getValue() {
        return 0;
    }

    @Override
    public boolean isPressed() {
        return false;
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "Dummy touch sensor";
    }

    @Override
    public String getConnectionInfo() {
        return "Dummy touch sensor";
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
