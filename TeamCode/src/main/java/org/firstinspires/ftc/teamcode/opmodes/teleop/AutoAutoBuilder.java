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
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiplyNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.PlusNode;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;
import org.firstinspires.ftc.teamcode.managers.sensor.SensorManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;

import java.util.ArrayList;
import java.util.Arrays;

@TeleOp
public class AutoAutoBuilder extends OpMode {
    public MovementManager driver;
    public ManipulationManager hands;
    public InputManager input;
    public SensorManager sensor;
    public float distance;
    public int startPosition;
    public int endPosition;
    public boolean tracking;
    public boolean appending;
    public int moveNum;
    //public ArrayList<float[][]> movements;
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
        /*
        input.registerInput("distanceTracker",
                new ButtonNode("leftbumper")
        );
         */
        input.registerInput("D-Pad Drive",
                new MultiInputNode(
                        new PlusNode(
                                new MultiplyNode(new ButtonNode("dpadup"), 0.5f),
                                new MultiplyNode(new ButtonNode("dpaddown"), -0.5f)
                        ),
                        new PlusNode(
                                new MultiplyNode(new ButtonNode("dpadright"), 0.5f),
                                new MultiplyNode(new ButtonNode("dpadleft"), -0.5f)
                        )
                )
        );
        input.registerInput("retraceSteps",
                new ButtonNode("rightbumper")
        );
        input.setOverlapResolutionMethod(InputOverlapResolutionMethod.MOST_COMPLEX_ARE_THE_FAVOURITE_CHILD);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        tracking = false;
        appending=false;
        moveNum=0;
        //movements=new ArrayList<float[][]>();
    }
    public void loop() {
        try {
            input.update();
            float[] h = input.getFloatArrayOfInput("D-Pad Drive");
            driver.driveOmni(h[0],h[1],0);
            if (h[0]!=0 || h[1]!=0){
                appending=true;
                if (!tracking){
                    startPosition = driver.frontLeft.getCurrentPosition();
                    tracking = true;
                }
                endPosition = driver.frontLeft.getCurrentPosition();
                distance = PaulMath.encoderDistanceCm(endPosition - startPosition);
            } else if (appending) {
                //float[][] movement = {{h[0],h[1],0},{distance}};
                //movements.add(movement);
                moveNum+=1;
                telemetry.addLine(moveNum+": driveOmni("+h[0]+", "+h[1]+", 0), after "+distance+"cm next;");
                appending=false;
            }



            /*
            telemetry.addData("FL Power", driver.frontLeft.getPower());
            telemetry.addData("FR Power", driver.frontRight.getPower());
            telemetry.addData("BR Power", driver.backLeft.getPower());
            telemetry.addData("BL Power", driver.backRight.getPower());
            telemetry.addData("Distance Traveled", distance);
             */
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