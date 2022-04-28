package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputOverlapResolutionMethod;
import org.firstinspires.ftc.teamcode.managers.input.nodes.JoystickNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.StaticValueNode;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;
import org.firstinspires.ftc.teamcode.managers.nate.NateManager;

@TeleOp
public class TankTeleop extends OpMode {
    public MovementManager driver;
    public ManipulationManager hands;
    public InputManager input;
    public NateManager clawPosition;
    private boolean precision = false;
    private boolean dashing = false;

    @Override
    public void init() {
        // Phone is labelled as Not Ready For Use
        FeatureManager.setIsOpModeRunning(true);
        FeatureManager.reconfigureForTeleop();

        FeatureManager.logger.setBackend(telemetry.log());

        DcMotor fl = hardwareMap.get(DcMotor.class, "fl");
        DcMotor fr = hardwareMap.get(DcMotor.class, "fr");
        DcMotor br = hardwareMap.get(DcMotor.class, "br");
        DcMotor bl = hardwareMap.get(DcMotor.class, "bl");
        driver = new MovementManager(fl, fr, br, bl);

        input = new InputManager(gamepad1, gamepad2);
        input.registerInput("drivingControls",
                new MultiInputNode(
                        new JoystickNode("left_stick_y"),
                        new JoystickNode("right_stick_x"),
                        new StaticValueNode(0f)
                )
        );
        input.setOverlapResolutionMethod(InputOverlapResolutionMethod.MOST_COMPLEX_ARE_THE_FAVOURITE_CHILD);
    }

    @Override
    public void loop() {
        input.update();
        driver.driveOmni(input.getFloatArrayOfInput("drivingControls"));

        telemetry.update();
        telemetry.addData("WhichBoy", FeatureManager.getRobotName());
    }

    public void stop() {
        FeatureManager.setIsOpModeRunning(false);
    }


}
