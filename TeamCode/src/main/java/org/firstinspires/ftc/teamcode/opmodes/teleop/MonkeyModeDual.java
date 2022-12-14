package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.crservo;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.motor;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.servo;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.auxilary.integratedasync.PriorityAsyncOpmodeComponent;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.managers.bigArm.bigArmManager;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputOverlapResolutionMethod;
import org.firstinspires.ftc.teamcode.managers.input.nodes.AnyNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ButtonNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.JoystickNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiplyNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.PlusNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ToggleNode;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;
import org.firstinspires.ftc.teamcode.managers.roadrunner.RRManager;
import org.firstinspires.ftc.teamcode.managers.sensor.ColorSensor;
import org.firstinspires.ftc.teamcode.managers.sensor.SensorManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.opmodes.auto.S;

import java.util.Arrays;

@TeleOp
public class MonkeyModeDual extends OpMode {
    public MovementManager driver;
    public ManipulationManager hands;
    public InputManager input;
    public SensorManager sensor;
    public SampleMecanumDrive drive;
    public TrajectoryBuilder trajBuild;
    public bigArmManager monkeyArm;
    public SensorManager sensing;
    private boolean armStatus = false;
    private boolean intakeToggle = false;
    public double distance;
    int towerPos = 0;
    int currentColor = 0;
    int currentColor1 = 0;
    public Pose2d lastError;
    private boolean looping = false;
    private boolean shouldActuallyDoThings = true;
    private RRManager rr;
    @Override
    public void init() {
        // Phone is labelled as T-10 Melman
        FeatureManager.setIsOpModeRunning(true);
        FeatureManager.reconfigureForTeleop();
        TelemetryManager telemetryManager = new TelemetryManager(telemetry, this, TelemetryManager.BITMASKS.NONE);
        telemetry = telemetryManager;
        FeatureManager.logger.setBackend(telemetry.log());
        rr = new RRManager(hardwareMap, new Pose2d(0, 0), telemetryManager);
        DcMotor fl = hardwareMap.get(DcMotor.class, "fl");
        DcMotor fr = hardwareMap.get(DcMotor.class, "fr");
        DcMotor br = hardwareMap.get(DcMotor.class, "br");
        DcMotor bl = hardwareMap.get(DcMotor.class, "bl");
        driver = new MovementManager(fl, fr, br, bl);
        hands = new ManipulationManager(
                hardwareMap,
                crservo         (),
                servo           ("monkeyHand"),
                motor           ("monkeyShoulder")
        );
        sensing = new SensorManager(
                hardwareMap,
                SensorManager.colorSensor("rainbowSense", "rainbowSense1"),
                SensorManager.touchSensor(),
                SensorManager.distanceSensor()
        );
        monkeyArm = new bigArmManager(hands);
        input = new InputManager(gamepad1, gamepad2);
        input.registerInput("drivingControls",
                    new PlusNode(
                            new MultiInputNode(
                                    new MultiplyNode(new JoystickNode("left_stick_y"), -1f),
                                    new MultiplyNode(new JoystickNode("left_stick_x"), -1f),
                                    new JoystickNode("right_stick_x")
                            ),
                            new MultiInputNode(
                                    new MultiplyNode(new JoystickNode("gamepad2left_stick_y"), -0.25f),
                                    new MultiplyNode(new JoystickNode("gamepad2left_stick_x"), -0.25f),
                                    new MultiplyNode(new JoystickNode("gamepad2right_stick_x"), 0.25f)
                            )
                    )
        );
        input.registerInput("handToggle",
                new AnyNode(
                        new ButtonNode("rightbumper"),
                        new ButtonNode("gamepad2rightbumper")
                )
        );
        input.registerInput("extendArm",
                new ButtonNode("gamepad2righttrigger")
        );
        input.registerInput("retractArm",
                new ButtonNode("gamepad2lefttrigger")
        );
        input.registerInput("colorNYOOM",
                new ToggleNode(new ButtonNode("righttrigger"))
        );
        input.registerInput("armLengthSmall",
                new ButtonNode("gamepad2a")
        );
        input.registerInput("armLengthMedium",
                new ButtonNode("gamepad2x")
        );
        input.registerInput("armLengthTall",
                new ButtonNode("gamepad2y")
        );
        input.registerInput("armLengthNone",
                new ButtonNode("gamepad2b")
        );
        input.registerInput("D-Up",
                new ButtonNode("dpadup")
                );
        input.registerInput("D-Left",
                new ButtonNode("dpadleft")
        );
        input.registerInput("D-Right",
                new ButtonNode("dpadright")
        );
        input.registerInput("D-Down",
                new ButtonNode("dpaddown")
        );
        input.setOverlapResolutionMethod(InputOverlapResolutionMethod.MOST_COMPLEX_ARE_THE_FAVOURITE_CHILD);
        PriorityAsyncOpmodeComponent.start(() -> {

            if(input.getBool("D-Up") && rr.notBusy()){
                rr.moveToPosWithID(2);
            }
            if(input.getBool("D-Down") && rr.notBusy()){
                rr.moveToPosWithID(3);
            }
            if(input.getBool("D-Right") && rr.notBusy()){
                rr.calibrateDriveToZero();
            }
            if(input.getBool("D-Left")){
                rr.moveToPosWithID(1);
            }

        });
        rr.updateLocalizer();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }
    public void loop() {
        try {
            input.update();
            if(rr.notBusy()){
                //Meant to be if this && !input.getBool("armLengthNone");
                driver.driveOmni(input.getFloatArrayOfInput("drivingControls"));
            }
            if (input.getBool("handToggle") && !armStatus) {
                intakeToggle=!intakeToggle;
                armStatus = true;
            } else if (!input.getBool("handToggle") && armStatus){
                    armStatus = false;
            }
            if (intakeToggle){
                monkeyArm.openArm();
            } else {
                monkeyArm.closeArm();
            }
            if (input.getBool("extendArm")) {
                monkeyArm.extendArm();
            } else if (input.getBool("retractArm")) {
                monkeyArm.retractArm();
            } else {
                monkeyArm.stopArm();
            }
            if (input.getBool("colorNYOOM")) {
                rr.setBusy();
                driver.driveOmni(1,0,0);
                if (currentColor > 100 && currentColor < 200){
                    rr.notBusy();
                    driver.driveOmni(0,0,0);
                }
            }
            currentColor = sensing.getColorInteger("rainbowSense");
            currentColor1 = sensing.getColorInteger("rainbowSense1");
            if (input.getBool("armLengthNone")) {
                monkeyArm.setPositionFloorLocation();
            }
            if (input.getBool("armLengthSmall")) {
                monkeyArm.setPositionLowLocation();
            }
            if (input.getBool("armLengthMedium")) {
                monkeyArm.setPositionMiddleLocation();
            }
            if (input.getBool("armLengthTall")) {
                monkeyArm.setPositionHighLocation();
            }
            towerPos += (-(int)hands.getMotorPosition("monkeyShoulder"));
            telemetry.addData("FL Power", driver.frontLeft.getPower());
            telemetry.addData("FR Power", driver.frontRight.getPower());
            telemetry.addData("BR Power", driver.backLeft.getPower());
            telemetry.addData("BL Power", driver.backRight.getPower());
            telemetry.addData("Roadrunner Not Busy: ", rr.notBusy());
            telemetry.addData("Heading", rr.getDrive().getExternalHeading());
            telemetry.addData("Servo Open",""+intakeToggle);
            telemetry.addData("Tower Power", hands.getMotorPower("monkeyShoulder"));
            telemetry.addData("Tower Position: ", towerPos);
            telemetry.addData("FL Position: ", driver.frontLeft.getCurrentPosition());
            telemetry.addData("Last Error: ", lastError);
            telemetry.addData("Current Color: ", currentColor);
            telemetry.addData("Current Color: ", currentColor1);
            telemetry.update();
        }
        catch (Throwable t) {
            FeatureManager.logger.log(t.toString());
            StackTraceElement[] e = t.getStackTrace();
            for (int i = 0; i < 3 && i < e.length; i++) {
                FeatureManager.logger.log(e[i].toString());
            }
            shouldActuallyDoThings = false;
            telemetry.update();
        }
    }
    public void stop() {
        FeatureManager.setIsOpModeRunning(false);
    }
}