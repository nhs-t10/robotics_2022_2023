package org.firstinspires.ftc.teamcode.unitTests.dummy;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DummyOpmode extends OpMode {
    public DummyOpmode() {
        this.telemetry = new DummyTelemetry();
        this.hardwareMap = new DummyHardwareMap();
        this.gamepad1 = new DummyGamepad();
        this.gamepad2 = new DummyGamepad();
    }
    @Override
    public void init() {

    }

    @Override
    public void loop() {

    }
}
