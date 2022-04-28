package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.crservo;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.motor;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.servo;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.auxilary.integratedasync.PriorityAsyncOpmodeComponent;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputOverlapResolutionMethod;
import org.firstinspires.ftc.teamcode.managers.input.nodes.AnyNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ButtonNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.IfNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.JoystickNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiplyNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.PlusNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ScaleNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.StaticValueNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ToggleNode;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;
import org.firstinspires.ftc.teamcode.managers.nate.NateManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;

import java.util.Arrays;

@TeleOp
public class DualControllerFools extends OpMode {
    public MovementManager driver;
    public ManipulationManager hands;
    public InputManager input;
    public NateManager clawPosition;
    private boolean precision = false;
    private boolean dashing = false;
    private double clawCheck;
    private int clawPos;

    @Override
    public void init() {
        // Phone is labelled as Not Ready For Use
        FeatureManager.setIsOpModeRunning(true);
        FeatureManager.reconfigureForTeleop();

        TelemetryManager telemetryManager = new TelemetryManager(telemetry, this, TelemetryManager.BITMASKS.NONE);
        telemetry = telemetryManager;
        FeatureManager.logger.setBackend(telemetry.log());

        DcMotor fl = hardwareMap.get(DcMotor.class, "fl");
        DcMotor fr = hardwareMap.get(DcMotor.class, "fr");
        DcMotor br = hardwareMap.get(DcMotor.class, "br");
        DcMotor bl = hardwareMap.get(DcMotor.class, "bl");
        driver = new MovementManager(fl, fr, br, bl);
        hands = new ManipulationManager(
                hardwareMap,
                crservo         ("nateMoverRight", "nateMoverLeft"),
                servo           ("nateClaw", "rampLeft", "rampRight"),
                motor           ("Carousel", "ClawMotor", "noodle", "intake")
        );

        clawPosition = new NateManager(hands, hardwareMap.get(TouchSensor.class, "limit"));
        input = new InputManager(gamepad1, gamepad2);
        input.registerInput("drivingControls",
                new PlusNode(
                        new MultiInputNode(
                                new ScaleNode(new JoystickNode("left_stick_y"), 1),
                                new ScaleNode(new JoystickNode("right_stick_x"), 0.9f),
                                new ScaleNode(new JoystickNode("left_stick_x"), 1)
                        ),
                        new MultiInputNode(
                                new ScaleNode(new JoystickNode("gamepad2right_stick_y"), 0.4f),
                                new ScaleNode(new JoystickNode("gamepad2left_stick_x"), 0.4f),
                                new ScaleNode(new JoystickNode("gamepad2right_stick_x"), 0.7f)
                        )
                )
        );
        input.setOverlapResolutionMethod(InputOverlapResolutionMethod.MOST_COMPLEX_ARE_THE_FAVOURITE_CHILD);
//        input.registerInput("precisionDriving",
//                new IfNode(
//                    new ToggleNode(new ButtonNode("b")),
//                    new StaticValueNode(0.3f),
//                    new StaticValueNode(0.6f)
//                )
//        );
//        input.registerInput("dashing",
//                new IfNode(
//                    new ToggleNode(new ButtonNode("x")),
//                    new StaticValueNode(1f),
//                    new StaticValueNode(0.6f)
//                )
//        );
        input.registerInput("Carousel",
                new PlusNode(
                    new MultiplyNode(new ButtonNode("y"), 0.75f),
                    new MultiplyNode(new ButtonNode("a"), -0.75f)
                )
        );
        input.registerInput("ClawPos1", new ButtonNode ("gamepad2y"));
        input.registerInput("ClawPos2", new ButtonNode ("gamepad2b"));
        input.registerInput("ClawPos3", new ButtonNode ("gamepad2a"));
        input.registerInput("ClawPosHome", new ButtonNode("gamepad2x"));
        input.registerInput("ClawManualMove",
                new PlusNode(
                        new MultiplyNode(new ButtonNode("gamepad2dpadup"), -0.25f),
                        new MultiplyNode(new ButtonNode("gamepad2dpaddown"), 0.25f)
                )
        );
        input.registerInput("ToggleClaw", new ButtonNode("gamepad2leftbumper"));
        input.registerInput("Intake",
                new PlusNode(
                    new IfNode(
                        new ToggleNode(
                                new AnyNode(
                                    new ButtonNode("lefttrigger"),
                                    new ButtonNode("gamepad2lefttrigger")
                                )
                        ),
                    new StaticValueNode(-0.5f),
                    new StaticValueNode(0f)
                    ),
                    new IfNode(
                            new ToggleNode(
                                    new AnyNode(
                                            new ButtonNode("righttrigger"),
                                            new ButtonNode("gamepad2righttrigger")
                                    )
                            ),
                            new StaticValueNode(0.9f),
                            new StaticValueNode(0f)
                    )
                )
        );
        input.registerInput("EmergencyStop",
                new AnyNode(
                        new ButtonNode("dpadleft"),
                        new ButtonNode("gamepad2dpadleft"),
                        new ButtonNode("dpadright"),
                        new ButtonNode("gamepad2dpadright")
                        )
                );

        PriorityAsyncOpmodeComponent.start(() -> {
            driver.driveOmni(input.getFloatArrayOfInput("drivingControls"));
        });


    }

    @Override
    public void loop() {
        input.update();

//        driver.setScale(Math.min(input.getFloat("precisionDriving"), input.getFloat("dashing")));

        clawCheck = clawPosition.getClawOpenish();
        clawPos = clawPosition.getClawPosition();

        if (clawCheck == 1.0 && clawPos == 0) {
            hands.setMotorPower("noodle", (double) input.getFloat("Intake"));
        }
        else hands.setMotorPower("noodle", 0.0);

        hands.setMotorPower("intake", (double) input.getFloat("Intake"));

        if (input.getFloat("Intake") == 0.0f){
            hands.setServoPosition("rampLeft", 0.0);
            hands.setServoPosition("rampRight", -0.35);
        }
        else {
            hands.setServoPosition("rampLeft", 0.50);
            hands.setServoPosition("rampRight", 0.0);
        }

        if (input.getBool("EmergencyStop")){
            clawPosition.emergencyStop();
        }
        hands.setMotorPower("Carousel", (double) input.getFloat("Carousel"));

        if (hands.hasEncodedMovement("ClawMotor") == false) {

            hands.setMotorPower("ClawMotor", (double) input.getFloat("ClawManualMove"));

            if (input.getBool("ClawPos1") == true) {
                clawPosition.positionOne();
            }
            if (input.getBool("ClawPos2") == true) {
                clawPosition.positionTwo();
            }
            if (input.getBool("ClawPos3") == true) {
                clawPosition.positionThree();
            }
            if (input.getBool("ClawPosHome") == true) {
                clawPosition.positionHome();
            }
        }
        if (input.getBool("ToggleClaw")){
            clawPosition.setClawOpen(true);
        }
        else {
            clawPosition.setClawOpen(false);
        }

        telemetry.addData("FL Power", driver.frontLeft.getPower());
        telemetry.addData("FR Power", driver.frontRight.getPower());
        telemetry.addData("BR Power", driver.backLeft.getPower());
        telemetry.addData("BL Power", driver.backRight.getPower());
        telemetry.addData("Pos Y (encoders)", driver.getCentimeters());
        telemetry.addData("WhichBoy", FeatureManager.getRobotName());
        telemetry.addData("Claw Open Position", clawPosition.getClawOpenish());
        telemetry.addData("Carousel", hands.getMotorPower("Carousel"));
        telemetry.addData("driver control", Arrays.toString(input.getFloatArrayOfInput("drivingControls")));
        telemetry.addData("ClawTowerTicks", hands.getMotorPosition("ClawMotor"));
        telemetry.addData("ClawTowerTarTicks", hands.getMotorTargetPosition("ClawMotor"));
        telemetry.addData("ClawTowerPower", hands.getMotorPower("ClawMotor"));
        telemetry.addData("Is Found", clawPosition.isFound());
        telemetry.update();
    }

    public void stop() {
        FeatureManager.setIsOpModeRunning(false);
    }


}
