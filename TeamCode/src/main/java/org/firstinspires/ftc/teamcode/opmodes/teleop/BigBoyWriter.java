package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.auxilary.FileSaver;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.RobotConfiguration;

@TeleOp
public class BigBoyWriter extends OpMode {

    @Override
    public void init() {
        FileSaver bigBoyConfiguration = new FileSaver(RobotConfiguration.fileName);
        bigBoyConfiguration.overwriteFile(RobotConfiguration.bigBoyFileContent);
    }

    @Override
    public void loop() {

    }
}
