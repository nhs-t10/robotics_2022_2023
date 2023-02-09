package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.crservo;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.motor;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.servo;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
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
import org.firstinspires.ftc.teamcode.managers.input.nodes.GradualStickNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.JoystickNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiplyNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.PlusNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ToggleNode;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;
import org.firstinspires.ftc.teamcode.managers.roadrunner.RoadRunnerManager;
import org.firstinspires.ftc.teamcode.managers.roadrunner.SequenceInitException;
import org.firstinspires.ftc.teamcode.managers.sensor.SensorManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;

@TeleOp
public class MonkeyModeDualRR extends OpMode {
    public MovementManager driver;
    public ManipulationManager hands;
    public InputManager input;
    public SensorManager sensor;
    public SampleMecanumDrive drive;
    public TrajectoryBuilder trajBuild;
    public bigArmManager monkeyArm;
    public SensorManager sensing;
    private float microDriveSpeed = 0.4f;
    private boolean handStatus = false;
    private boolean intakeToggle = false;
    private boolean rrStatus = false;
    private float[] driving;
    boolean rrToggle = false;
    private float[] controlFloats;
    public boolean movingToLow = false;
    public boolean movingToMid = false;
    public boolean movingToHigh = false;
    public boolean movingToFloor = false;

    public boolean nyooming = false;
    public double distance;
    int towerPos = 0;
    SampleMecanumDrive driveRR;
    int currentColor = 0;
    int currentColor1 = 0;
    float rainbowSenseRed = 0;
    float rainbowSenseBlue = 0;
    public Pose2d lastError;
    private boolean looping = false;
    private boolean shouldActuallyDoThings = true;
    private RoadRunnerManager rr;
    boolean deb = false;


    @Override
    public void init() {
        // Phone is labelled as T-10 Melman
        FeatureManager.setIsOpModeRunning(true);
        FeatureManager.reconfigureForTeleop();
        TelemetryManager telemetryManager = new TelemetryManager(telemetry, this, TelemetryManager.BITMASKS.NONE);
        telemetry = telemetryManager;
        FeatureManager.logger.setBackend(telemetry.log());
        rr = new RoadRunnerManager(hardwareMap, new Pose2d(0, 0, Math.toRadians(0)), telemetryManager, this);
        driveRR = rr.getDrive();
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
        //hands.resetEncoders("monkeyShoulder");
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
                                new MultiplyNode(new GradualStickNode(new JoystickNode("left_stick_y"), 0.25f, 0.5f), -1f),
                                new MultiplyNode(new GradualStickNode(new JoystickNode("left_stick_x"), 0.25f, 0.5f), -1f),
                                new MultiplyNode(new GradualStickNode(new JoystickNode("right_stick_x"), 0.25f, 0.5f), -1f)
                        ),
                        new MultiInputNode(
                                new MultiplyNode(new JoystickNode("gamepad2left_stick_y"), -0.4f),
                                new MultiplyNode(new JoystickNode("gamepad2left_stick_x"), -0.4f),
                                new MultiplyNode(new JoystickNode("gamepad2right_stick_x"), -0.4f)
                        )
                )
        );
        input.registerInput("inversionToggle",
                new ToggleNode(new ButtonNode("gamepad2leftbumper"))
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
               new ButtonNode("righttrigger")
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
        input.registerInput("y1",
                new ButtonNode("y")
                );
        input.registerInput("x1",
                new ButtonNode("x")
        );
        input.registerInput("b1",
                new ButtonNode("b")
        );
        input.registerInput("a1",
                new ButtonNode("a")
        );
        input.registerInput("rrTog", new ToggleNode(new ButtonNode("dpadup")));
        input.registerInput("rrToggle", new ButtonNode("dpadup"));
        input.setOverlapResolutionMethod(InputOverlapResolutionMethod.MOST_COMPLEX_ARE_THE_FAVOURITE_CHILD);
        rr.calibrateDriveToAutoPosition();
        PriorityAsyncOpmodeComponent.start(() -> {
            input.update();
            controlFloats = input.getFloatArrayOfInput("drivingControls");
            if (rr.notBusy()) {
                rr.doOmniDisplace(gamepad1, gamepad2, controlFloats);
            }
        });
        PriorityAsyncOpmodeComponent.start(() -> {

            if(input.getBool("rrTog") && rr.notBusy()) {
                if (input.getBool("a1") && rr.notBusy()) {
                    //rr.moveToPosWithID(2);
                    try {
                        rr.customMoveSequenceWithPoseTrajSequence(new Pose2d[]{new Pose2d(0, 15), new Pose2d(0, 15), new Pose2d(15, 15)}, new String[]{"strafe", "turn", "strafe"}, new double[]{0, 90, 0});
                    } catch (SequenceInitException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (input.getBool("b1")) {
                    //rr.moveToPosWithID(1);
                    telemetry.log().add("RR PATH STARTED");
                    rr.customMoveWithPose(new Pose2d(0, 25), "strafe", 20);
                }
                if (input.getBool("x1") && rr.notBusy()) {
                    rr.calibrateDriveToZero();
                }
                if (input.getBool("y1") && rr.notBusy()) {
                    telemetry.log().add("RR PATH STARTED");
                    rr.customMoveWithPose(new Pose2d(0, 20), "strafe", 20);
                }
            }
            rr.updateLocalizer();


            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        });

    }
    public void loop() {
        try {
            input.update();
            towerPos = (int)hands.getMotorPosition("monkeyShoulder");

//            if(rr.notBusy()){
            //Meant to be if this && !input.getBool("armLengthNone");
            //driver.driveOmni(input.getFloatArrayOfInput("drivingControls"));
//            }

            if (input.getBool("inversionToggle")){
                microDriveSpeed = 0.4f;
            }
            else {
                microDriveSpeed = -0.4f;
            }
            if (input.getBool("handToggle") && !handStatus) {
                monkeyArm.openHand();
            }
            else {
                monkeyArm.closeHand();
            }
            if (input.getBool("extendArm")) {
                monkeyArm.extendArm(input.getFloat("extendArm"));
                movingToHigh=false;
                movingToMid=false;
                movingToLow=false;
                movingToFloor=false;
                monkeyArm.resetDoOnce();
            } else if (input.getBool("retractArm")) {
                monkeyArm.retractArm(input.getFloat("retractArm"));
                movingToHigh=false;
                movingToMid=false;
                movingToLow=false;
                movingToFloor=false;
                monkeyArm.resetDoOnce();
                intakeToggle = false;
            } else if (!movingToFloor && !movingToLow && !movingToMid && !movingToHigh){
                monkeyArm.stopArm();
            }
            /*if (input.getBool("colorNYOOM") && !nyooming) {
                nyooming = true;
                rr.setBusy();
                while (currentColor == 0 && currentColor1 == 0) {
                    driver.driveOmni(0.5f, 0, 0);
                }
                rr.notBusy();
                driver.driveOmni(0,0,0);
                nyooming = false;
            }*/
//            currentColor = sensing.getColor("rainbowSense");
//            currentColor1 = sensing.getColor("rainbowSense1");

            /*if ((input.getBool("armLengthNone") || movingToFloor) && !movingToLow && !movingToMid && !movingToHigh) {
                movingToFloor = monkeyArm.setPositionFloorLocation();
            } Look into this more later */
            if (input.getBool("armLengthNone")) {
                movingToFloor=true;
                movingToLow=false;
                movingToMid=false;
                movingToHigh=false;
                monkeyArm.resetDoOnce();
            }
            if (input.getBool("armLengthSmall")) {
                movingToFloor=false;
                movingToLow=true;
                movingToMid=false;
                movingToHigh=false;
                monkeyArm.resetDoOnce();
            }
            if (input.getBool("armLengthMedium")) {
                movingToFloor=false;
                movingToLow=false;
                movingToMid=true;
                movingToHigh=false;
                monkeyArm.resetDoOnce();
            }
            if (input.getBool("armLengthTall")) {
                movingToFloor=false;
                movingToLow=false;
                movingToMid=false;
                movingToHigh=true;
                monkeyArm.resetDoOnce();
            }
            if (movingToFloor) {
                movingToFloor = monkeyArm.setPositionFloorLocation();
            } else if (movingToLow) {
                movingToLow = monkeyArm.setPositionLowLocation();
            } else if (movingToMid) {
                movingToMid = monkeyArm.setPositionMiddleLocation();
            } else if (movingToHigh) {
                movingToHigh = monkeyArm.setPositionHighLocation();
            }

            telemetry.addData("FL Power", driver.frontLeft.getPower());
            telemetry.addData("FR Power", driver.frontRight.getPower());
            telemetry.addData("BR Power", driver.backLeft.getPower());
            telemetry.addData("BL Power", driver.backRight.getPower());
//          telemetry.addData("Roadrunner Not Busy: ", rr.notBusy());
//          telemetry.addData("Heading", rr.getDrive().getExternalHeading());
            telemetry.addData("Servo Open",""+intakeToggle);
            telemetry.addData("Tower Power", hands.getMotorPower("monkeyShoulder"));
            telemetry.addData("Tower Position: ", towerPos);
            telemetry.addData("FL Position: ", driver.frontLeft.getCurrentPosition());
            telemetry.addData("Last Error: ", lastError);
            telemetry.addData("CurrentColor", currentColor);
            telemetry.addData("CurrentColor1", currentColor1);
//            telemetry.addData("Pos: ", rr.getDrive().getPoseEstimate());
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