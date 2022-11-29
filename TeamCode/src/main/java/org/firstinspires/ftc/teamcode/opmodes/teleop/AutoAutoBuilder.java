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

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.auxilary.integratedasync.PriorityAsyncOpmodeComponent;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.managers.bigArm.bigArmManager;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputOverlapResolutionMethod;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ButtonNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.JoystickNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;
import org.firstinspires.ftc.teamcode.managers.sensor.SensorManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;

@TeleOp
public class AutoAutoBuilder extends OpMode {
    public MovementManager driver;
    public ManipulationManager hands;
    public InputManager input;
    public SensorManager sensor;
    public double distance;
    public int startPosition;
    public int endPosition;
    public boolean tracking;
    @Override
    public void init() {
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
        input = new InputManager(gamepad1, gamepad2);
        input.registerInput("drivingControls",
                    new MultiInputNode(
                        new JoystickNode("left_stick_y"),
                        new JoystickNode("left_stick_x"),
                        new JoystickNode("right_stick_x")
                    )
            );
        input.registerInput("distanceTracker",
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
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
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
            input.update();
            //Turns inputs into ints (false is 0 and true is 1) and then multiplies them by 0.5
            driver.driveOmni(0.5f * (Boolean.compare(input.getBool("D-Up"),false) - Boolean.compare(input.getBool("D-Down"),false)),
                    0.5f * (Boolean.compare(input.getBool("D-Right"),false) - Boolean.compare(input.getBool("D-Left"),false)),
                    0);

            if (input.getBool("distanceTracker")) {
                if (!tracking){
                    startPosition = driver.frontLeft.getCurrentPosition();
                    tracking = true;
                }
                endPosition = driver.frontLeft.getCurrentPosition();
                distance = PaulMath.encoderDistanceCm(endPosition - startPosition);
                telemetry.addLine("Distance Traveled: " + distance);
            }
            else {
                tracking = false;
            }

            telemetry.addData("FL Power", driver.frontLeft.getPower());
            telemetry.addData("FR Power", driver.frontRight.getPower());
            telemetry.addData("BR Power", driver.backLeft.getPower());
            telemetry.addData("BL Power", driver.backRight.getPower());
            telemetry.addData("Distance Traveled", distance);
            telemetry.update();
        }
        catch (Throwable t) {
            FeatureManager.logger.log(t.toString());
            StackTraceElement[] e = t.getStackTrace();
            for (int i = 0; i < 3 && i < e.length; i++) {
                FeatureManager.logger.log(e[i].toString());
            }
            telemetry.update();
        }
    }
    public void stop() {
        FeatureManager.setIsOpModeRunning(false);
    }
}