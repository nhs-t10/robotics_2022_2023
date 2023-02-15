package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.imu.ImuManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputOverlapResolutionMethod;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ButtonNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiplyNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.PlusNode;
import org.firstinspires.ftc.teamcode.managers.macro.MacroManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;
import org.firstinspires.ftc.teamcode.managers.sensor.SensorManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;

@TeleOp
public class RotationTesting extends OpMode {
    public MovementManager driver;
    public ManipulationManager hands;
    public InputManager input;
    public ImuManager imu;
    public MacroManager macro;
    public boolean turningRight;
    public boolean turningLeft;
    public boolean spinMoving;
    public boolean aPressed;
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
        imu = new ImuManager(hardwareMap.get(com.qualcomm.hardware.bosch.BNO055IMU.class, "imu"), driver);
        macro=new MacroManager(driver,imu);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //Turns inputs into ints (false is 0 and true is 1) and then multiplies them by 0.5
//        input.registerInput("D-Pad Drive",
//                new MultiInputNode(
//                        new PlusNode(
//                                new MultiplyNode(new ButtonNode("dpadup"), 0.5f),
//                                new MultiplyNode(new ButtonNode("dpaddown"), -0.5f)
//                        ),
//                        new PlusNode(
//                                new MultiplyNode(new ButtonNode("dpadright"), 0.5f),
//                                new MultiplyNode(new ButtonNode("dpadleft"), -0.5f)
//                        )/*,
//                        new PlusNode(
//                                new MultiplyNode(new ButtonNode("b"), 0.5f),
//                                new MultiplyNode(new ButtonNode("x"), -0.5f)
//                        )*/
//                )
//        );
        input.registerInput("rotateLeft",
                new ButtonNode("leftTrigger"));
        input.registerInput("rotateRight",
                new ButtonNode("rightTrigger"));
        input.registerInput("turn90Left",
                new ButtonNode("x"));
        input.registerInput("turn90Right",
                new ButtonNode("y"));
        input.registerInput("spinMove",
                new ButtonNode("a"));
        input.setOverlapResolutionMethod(InputOverlapResolutionMethod.MOST_COMPLEX_ARE_THE_FAVOURITE_CHILD);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }
    public void loop() {
        try {
            input.update();
            if (input.getBool("rotateLeft")){
                driver.driveOmni(0,0,0.5f);
                turningLeft=false;
                turningRight=false;
            }else if (input.getBool("rotateRight")){
                driver.driveOmni(0,0,-0.5f);
                turningLeft=false;
                turningRight=false;
            } else if (!turningRight && !turningLeft && !(macro.movingAndRotating)){
                driver.stopDrive();
            }
            if (input.getBool("turn90Left")){
                turningLeft=true;
                turningRight=false;
                imu.resetDoOnce();
            }
            if (input.getBool("turn90Right")){
                turningRight=true;
                turningLeft=false;
                imu.resetDoOnce();
            }
            if(input.getBool("spinMove") && !aPressed && !(macro.movingAndRotating)){
                turningRight=false;
                turningLeft=false;
                imu.resetDoOnce();
                macro.moveAndRotate(0.5f,0,0.5f);
                aPressed=true;
            } else if (!input.getBool("spinMove") && aPressed){
                aPressed=false;
            }


            if (turningLeft){
                turningLeft=imu.rotate(90f,0.5f);
            } else if (turningRight){
                turningRight=imu.rotate(-90f,0.5f);
            }


            telemetry.addData("FL Power", driver.frontLeft.getPower());
            telemetry.addData("FR Power", driver.frontRight.getPower());
            telemetry.addData("BR Power", driver.backLeft.getPower());
            telemetry.addData("BL Power", driver.backRight.getPower());
            telemetry.addData("Rotation", imu.getAngle());
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