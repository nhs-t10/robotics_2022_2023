package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.teamcode.auxilary.FileSaver;
import org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.RobotConfiguration;

@TeleOp
public class TouchSensorTest extends OpMode {
    DigitalChannel limit;

    @Override
    public void init() {
        limit = hardwareMap.get(DigitalChannel.class, "limit");
    }

    @Override
    public void loop() {
        if (limit.getState() == true) {
            telemetry.addData("Limit ", "Is Not Pressed");
        } else {
            telemetry.addData("Limit ", "Is Pressed");
        }

        telemetry.update();
    }
}
