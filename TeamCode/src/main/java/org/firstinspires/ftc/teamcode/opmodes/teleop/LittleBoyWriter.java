package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.auxilary.FileSaver;
import org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.RobotConfiguration;

@TeleOp
public class LittleBoyWriter extends OpMode {

    @Override
    public void init() {
        FileSaver littleBoyConfiguration = new FileSaver(RobotConfiguration.fileName);
        littleBoyConfiguration.overwriteFile(RobotConfiguration.littleBoyFileContent);
    }

    @Override
    public void loop() {

    }
}
