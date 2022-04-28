package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.auxilary.FileSaver;
import org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.RobotConfiguration;

@TeleOp
public class GiraffeBoyWriter extends OpMode {

    @Override
    public void init() {
        FileSaver giraffeBoyConfiguration = new FileSaver(RobotConfiguration.fileName);
        giraffeBoyConfiguration.overwriteFile(RobotConfiguration.giraffeBoyFileContent);
    }

    @Override
    public void loop() {

    }
}
