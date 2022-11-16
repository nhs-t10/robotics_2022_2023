package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.crservo;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.motor;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.servo;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
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
import org.firstinspires.ftc.teamcode.managers.input.nodes.ButtonNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.IfNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.JoystickNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiplyNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.StaticValueNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ToggleNode;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;
import org.firstinspires.ftc.teamcode.managers.sensor.SensorManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;

@TeleOp
public class MonkeyMode extends OpMode {
    public MovementManager driver;
    public ManipulationManager hands;
    public InputManager input;
    public SensorManager sensor;
    public SampleMecanumDrive drive;
    public TrajectoryBuilder trajBuild;
    public bigArmManager monkeyArm;
    private boolean armStatus = false;
    public int distance;
    public int startPosition;
    public int endPosition;
    public boolean tracking;
    public Pose2d lastError;
    private boolean looping = false;
    private boolean shouldActuallyDoThings = true;
    @Override
    public void init() {
        // Phone is labelled as T-10 Melman
        FeatureManager.setIsOpModeRunning(true);
        FeatureManager.reconfigureForTeleop();
        TelemetryManager telemetryManager = new TelemetryManager(telemetry, this, TelemetryManager.BITMASKS.NONE);
        telemetry = telemetryManager;
        FeatureManager.logger.setBackend(telemetry.log());
        drive = new SampleMecanumDrive(hardwareMap); //Necessary Component for RoadRunner!
        trajBuild = drive.trajectoryBuilder(new Pose2d());
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
        monkeyArm = new bigArmManager(hands);
        input = new InputManager(gamepad1, gamepad2);
        input.registerInput("drivingControls",
                    new MultiInputNode(
                        new JoystickNode("left_stick_y"),
                        new JoystickNode("left_stick_x"),
                        new JoystickNode("right_stick_x")
                    )
            );
        input.registerInput("handToggle",
                new ButtonNode("a")
        );
        input.registerInput("extendArm",
                new ButtonNode("righttrigger")
        );
        input.registerInput("retractArm",
                new ButtonNode("lefttrigger")
        );
        input.registerInput("armLengthSmall",
                new ButtonNode("rightbumper")
        );
        input.registerInput("armLengthMedium",
                new ButtonNode("x")
        );
        input.registerInput("armLengthTall",
                new ButtonNode("y")
        );
        input.registerInput("armLengthNone",
                new ButtonNode("b")
        );
        input.registerInput("distanceTrackToggle",
                new ButtonNode("leftbumper")
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
            if(input.getBool("D-Up") && drive.notBusy()){
                drive.followTrajectory(trajBuild.splineToLinearHeading(new Pose2d(36, 36), Math.toRadians(90)).build());
            }
            if(input.getBool("D-Down") && drive.notBusy()){
                drive.followTrajectory(trajBuild.strafeTo(new Vector2d(14, 28)).build());
            }
            if(input.getBool("D-Right") && drive.notBusy()){
                trajBuild.addDisplacementMarker(drive.getLocalizer().getPoseEstimate().vec().distTo(new Vector2d(0, 0)), () -> {});
            }
            if(input.getBool("D-Left")){
                drive.waitForIdle();
                lastError = drive.getLastError();
            }
        });
        drive.getLocalizer().update();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetry.addData("FL Power", driver.frontLeft.getPower());
        telemetry.addData("FR Power", driver.frontRight.getPower());
        telemetry.addData("BR Power", driver.backLeft.getPower());
        telemetry.addData("BL Power", driver.backRight.getPower());
        telemetry.addData("Distance Traveled", distance);
        telemetry.addData("Roadrunner Busy: ", drive.isBusy());
        telemetry.addData("Heading", drive.getLocalizer().getPoseEstimate());
        telemetry.addData("Last Error: ", lastError);
        //dashboard.sendTelemetryPacket(packet);
        /*
        TelemetryPacket packet = new TelemetryPacket();
        packet.put("x", 3.7);
        packet.put("status", "alive");
        FtcDashboard dashboard = FtcDashboard.getInstance();
        double coordx = 0.0;
        double coordy = 0.0;
        //Coordinates are measured in a unit that appears to match inches, robot is 17in x 17in
        double[] pointsX = {coordx, coordx + 17, coordx + 17, coordx, coordx};
        double[] pointsY = {coordy, coordy, coordy + 17, coordy + 17, coordy};
        packet.fieldOverlay()
                .setStroke("blue")
                .setStrokeWidth(1)
                .strokePolyline(pointsX, pointsY);
                */
    }
    public void loop() {
        try {
            if (shouldActuallyDoThings) {
                input.update();
                if(drive.notBusy()){
                    driver.driveOmni(input.getFloatArrayOfInput("drivingControls"));
                }
                if (input.getBool("handToggle")) {
                    monkeyArm.toggleArm();
                }
                if (input.getBool("extendArm")) {
                    monkeyArm.extendArm();
                } else if (input.getBool("retractArm")) {
                    monkeyArm.retractArm();
                } else {
                    monkeyArm.stopArm();
                }
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
                if (input.getBool("distanceTrackToggle")) {
                    if (tracking) {
                        endPosition = driver.frontLeft.getCurrentPosition();
                        distance = endPosition - startPosition;
                        tracking = false;
                    } else {
                        startPosition = driver.frontLeft.getCurrentPosition();
                        tracking = true;
                    }
                }
            }
            telemetry.update();
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
    public void stop() {
        FeatureManager.setIsOpModeRunning(false);
    }
}