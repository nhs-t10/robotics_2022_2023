package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.auxilary.FileSaver;
import org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.RobotConfiguration;

@TeleOp
public class TankBoyWriter extends OpMode {

    @Override
    public void init() {
        FileSaver tankBoyConfiguration = new FileSaver(RobotConfiguration.fileName);
        tankBoyConfiguration.overwriteFile(RobotConfiguration.tankBoyFileContent);
    }

    @Override
    public void loop() {

    }
}
