package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.crservo;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.motor;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.servo;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.auxilary.buildhistory.BuildHistory;
import org.firstinspires.ftc.teamcode.auxilary.integratedasync.PriorityAsyncOpmodeComponent;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputOverlapResolutionMethod;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ButtonNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.AnyNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.IfNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.JoystickNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.PlusNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ScaleNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.StaticValueNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ToggleNode;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;
import org.firstinspires.ftc.teamcode.managers.nate.NateManager;
import org.firstinspires.ftc.teamcode.managers.sensor.SensorManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;

import java.util.Arrays;

@TeleOp
public class DualController extends OpMode {
    public MovementManager driver;
    public ManipulationManager hands;
    public InputManager input;
    public NateManager clawPosition;
    public SensorManager sensor;
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
                            new ScaleNode(new JoystickNode("gamepad2left_stick_y"), 0.4f),
                            new ScaleNode(new JoystickNode("gamepad2right_stick_x"), 0.4f),
                            new ScaleNode(new JoystickNode("gamepad2left_stick_x"), 0.7f)
                        )
                )
            );
        input.setOverlapResolutionMethod(InputOverlapResolutionMethod.MOST_COMPLEX_ARE_THE_FAVOURITE_CHILD);
        input.registerInput("precisionDriving", new IfNode(
                new ToggleNode(new ButtonNode("b")),
                new StaticValueNode(0.1f),
                new StaticValueNode(0.6f)
        ));
        input.registerInput("dashing", new IfNode(
                new ToggleNode(new ButtonNode("x")),
                new StaticValueNode(1f),
                new StaticValueNode(0.6f)
        ));
        input.registerInput("CarouselBlue", new ButtonNode("y"));
        input.registerInput("CarouselRed", new ButtonNode("a"));
        input.registerInput("ClawPos1", new ButtonNode ("gamepad2y"));
        input.registerInput("ClawPos2", new ButtonNode ("gamepad2b"));
        input.registerInput("ClawPos3", new ButtonNode ("gamepad2a"));
        input.registerInput("ClawPosHome", new ButtonNode("gamepad2x"));
        input.registerInput("ClawPosNeutral", new ButtonNode("gamepad2ps"));
        input.registerInput("ClawUp", new ButtonNode("gamepad2dpadup"));
        input.registerInput("ClawDown", new ButtonNode("gamepad2dpaddown"));
        input.registerInput("ClawOpen", new ButtonNode("gamepad2leftbumper"));
        input.registerInput("Intake",
                new AnyNode(
                        new ButtonNode("righttrigger"),
                        new ButtonNode("gamepad2righttrigger")
                ));
        input.registerInput("Anti-Intake",
                new AnyNode(
                        new ButtonNode("lefttrigger"),
                        new ButtonNode("gamepad2lefttrigger")
                ));
        input.registerInput("EmergencyStop",
                new AnyNode(
                                new ButtonNode("dpadleft"),
                                new ButtonNode("gamepad2dpadleft"),
                                new ButtonNode("dpadright"),
                                new ButtonNode("gamepad2dpadright")
                ));

        PriorityAsyncOpmodeComponent.start(() -> {
            if(looping) driver.driveOmni(input.getFloatArrayOfInput("drivingControls"));
        });


    }
    private boolean looping = false;
    private boolean shouldActuallyDoThings = true;
    public void loop() {
        looping = true;
        try {
            if(shouldActuallyDoThings) real_loop_Bad_Practice_Fix_Me_Later();
        }
        catch (Throwable t) {
            FeatureManager.logger.log(t.toString());
            StackTraceElement[] e = t.getStackTrace();
            for(int i = 0; i < 3 && i < e.length;i++) {
                FeatureManager.logger.log(e[i].toString());
            }
            shouldActuallyDoThings = false;
            telemetry.update();
        }
    }
    public void real_loop_Bad_Practice_Fix_Me_Later() {
        input.update();

//        driver.setScale(Math.min(input.getFloat("precisionDriving"), input.getFloat("dashing")));

        if (input.getBool("Intake")){
            clawCheck = clawPosition.getClawOpenish();
            clawPos = clawPosition.getClawPosition();
            if (clawCheck != 0.0 && clawPos == 0 && clawPosition.liftMovementFinished()) {
                hands.setMotorPower("noodle", 0.9);
            }
            else {
                hands.setMotorPower("noodle", 0.0);
            }
            hands.setMotorPower("intake", 1);
            hands.setServoPosition("rampLeft", 0.50);
            hands.setServoPosition("rampRight", 0.0);
        }
        else if (input.getBool("Anti-Intake")){
            hands.setMotorPower("noodle", -0.50);
            hands.setMotorPower("intake", -0.50);
            hands.setServoPosition("rampLeft", 0.50);
            hands.setServoPosition("rampRight", 0.0);
        }
        else {
            hands.setMotorPower("noodle", 0.0);
            hands.setMotorPower("intake", 0.0);
            hands.setServoPosition("rampLeft", 0.0);
            hands.setServoPosition("rampRight", 0.35);
        }
        if (input.getBool("EmergencyStop")){
            clawPosition.emergencyStop();
        }
        if (input.getBool("CarouselBlue") && input.getBool("CarouselRed") == false){
            hands.setMotorPower("Carousel", 0.6);
        }
        else if (input.getBool("CarouselRed") && input.getBool("CarouselBlue") == false) {
            hands.setMotorPower("Carousel", -0.6);
        }
        else {
            hands.setMotorPower("Carousel", 0.0);
        }

        if (hands.hasEncodedMovement("ClawMotor") == false) {
            if (input.getBool("ClawUp") == true && input.getBool("ClawDown") == false) {
                hands.setMotorPower("ClawMotor", -0.20);
            }
            else if (input.getBool("ClawDown") == true && input.getBool("ClawUp") == false) {
                hands.setMotorPower("ClawMotor", 0.20);
            }
            else {
                hands.setMotorPower("ClawMotor", 0);
            }
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
        if (input.getBool("ClawOpen") == true){
            clawPosition.setClawOpen(true);
        }
        else {
            clawPosition.setClawOpen(false);
        }
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
        if (input.getBool("ClawPosNeutral") == true) {
            clawPosition.positionNeutral();
        }

        //FeatureManager.logger.log(BuildHistory.buildName);
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