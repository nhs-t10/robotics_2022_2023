package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.crservo;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.motor;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.servo;

import android.widget.Button;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import com.acmerobotics.roadrunner.*;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.MarkerCallback;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.auxilary.integratedasync.PriorityAsyncOpmodeComponent;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.managers.apple.AppleManager;
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
import org.firstinspires.ftc.teamcode.managers.nate.NateManager;
import org.firstinspires.ftc.teamcode.managers.sensor.SensorManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.util.DashboardUtil;

import java.util.Arrays;

@TeleOp
public class BasicDriving extends OpMode {
    public MovementManager driver;
    public ManipulationManager hands;
    public InputManager input;
    //public NateManager clawPosition;
    public SensorManager sensor;
    public SampleMecanumDrive drive;
    public TrajectoryBuilder trajBuild;
    public bigArmManager monkeyArm;
    @Override
    public void init() {
        // Phone is labelled as Not Ready For Use
        FeatureManager.setIsOpModeRunning(true);
        FeatureManager.reconfigureForTeleop();
        //TODO: Mention is redundant statement
        TelemetryManager telemetryManager = new TelemetryManager(telemetry, this, TelemetryManager.BITMASKS.NONE);
        telemetry = telemetryManager;
        FeatureManager.logger.setBackend(telemetry.log());

        //Necessary Component for RoadRunner!
        drive = new SampleMecanumDrive(hardwareMap);
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
        input = new InputManager(gamepad1, gamepad2);
        monkeyArm = new bigArmManager(hands);
        input.registerInput("drivingControls",
                new MultiplyNode(
                    new IfNode(new ToggleNode(new ButtonNode("a")), new StaticValueNode(1.1f), new StaticValueNode(1f)),
                    new MultiInputNode(
                        new JoystickNode("left_stick_y"),
                        new JoystickNode("left_stick_x"),
                        new JoystickNode("right_stick_x")
                    )
                )
            );
        input.registerInput("toggleHand",
                new ButtonNode("b")
        );
        input.registerInput("extendArm",
                new ButtonNode("right trigger")
        );
        input.registerInput("retractArm",
                new ButtonNode("left trigger")
        );

        input.setOverlapResolutionMethod(InputOverlapResolutionMethod.MOST_COMPLEX_ARE_THE_FAVOURITE_CHILD);
        PriorityAsyncOpmodeComponent.start(() -> {
            if(looping && !drive.isBusy()) driver.driveOmni(input.getFloatArrayOfInput("drivingControls"));
            //TODO: Figure out way to use nodes to rewrite if statement conditions...
            if(looping && input.gamepad.dpad_up && !drive.isBusy()){
                drive.followTrajectory(trajBuild.splineToLinearHeading(new Pose2d(36, 36), Math.toRadians(90)).build());
            }
            if(looping && input.gamepad.dpad_down && !drive.isBusy()){
                drive.followTrajectory(trajBuild.strafeTo(new Vector2d(14, 28)).build());
            }
            if(looping && input.gamepad.dpad_left && !drive.isBusy()){
                trajBuild.addDisplacementMarker(drive.getLocalizer().getPoseEstimate().vec().distTo(new Vector2d(0, 0)), () -> {});
            }
            if(looping && input.gamepad2.dpad_left){
                drive.waitForIdle();
                telemetry.addData("Last Error: ", drive.getLastError());
            }
        });
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        TelemetryPacket packet = new TelemetryPacket();
        packet.put("x", 3.7);
        packet.put("status", "alive");
        FtcDashboard dashboard = FtcDashboard.getInstance();

        drive.getLocalizer().update();

        telemetry.addData("FL Power", driver.frontLeft.getPower());
        telemetry.addData("FR Power", driver.frontRight.getPower());
        telemetry.addData("BR Power", driver.backLeft.getPower());
        telemetry.addData("BL Power", driver.backRight.getPower());
        telemetry.addData("Roadrunner Busy: ", drive.isBusy());
        telemetry.addData("Heading", drive.getLocalizer().getPoseEstimate());
        //dashboard.sendTelemetryPacket(packet);
        double coordx = 0.0;
        double coordy = 0.0;
        //Coordinates are measured in a unit that appears to match inches, robot is 17in x 17in
        double[] pointsX = {coordx, coordx + 17, coordx + 17, coordx, coordx};
        double[] pointsY = {coordy, coordy, coordy + 17, coordy + 17, coordy};
        packet.fieldOverlay()
                .setStroke("blue")
                .setStrokeWidth(1)
                .strokePolyline(pointsX, pointsY);
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
    //TODO: We gonna fix this?
    public void real_loop_Bad_Practice_Fix_Me_Later() {
        input.update();
        if (input.getBool("grabberToggle")){
            //B button
            monkeyArm.toggleArm();
        }
        if (input.getBool("extendArm")){
            //right shoulder
            monkeyArm.extendArm();
        }
        if (input.getBool("retractArm")){
            //left shoulder
            monkeyArm.retractArm();
        }



        telemetry.update();
    }
    public void stop() {
        FeatureManager.setIsOpModeRunning(false);
    }
}