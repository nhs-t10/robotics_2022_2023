package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.auxilary.FileSaver;
import org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.RobotConfiguration;

@TeleOp
public class SpeedyBoyWriter extends OpMode {

    @Override
    public void init() {
        FileSaver SpeedyBoyConfiguration = new FileSaver(RobotConfiguration.fileName);
        SpeedyBoyConfiguration.overwriteFile(RobotConfiguration.SpeedyBoyFileContent);
    }

    @Override
    public void loop() {

    }
}
