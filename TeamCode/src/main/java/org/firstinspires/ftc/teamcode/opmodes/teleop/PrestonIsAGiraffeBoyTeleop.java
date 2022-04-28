package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.crservo;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.motor;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.servo;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.auxilary.buildhistory.BuildHistory;
import org.firstinspires.ftc.teamcode.auxilary.integratedasync.PriorityAsyncOpmodeComponent;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputOverlapResolutionMethod;
import org.firstinspires.ftc.teamcode.managers.input.nodes.AllNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.AnyNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ButtonNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.IfNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.InputVariableNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.JoystickNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiplyNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.PlusNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ScaleNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.StaticValueNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ToggleNode;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;
import org.firstinspires.ftc.teamcode.managers.nate.GiraffeManager;
import org.firstinspires.ftc.teamcode.managers.nate.NateManager;
import org.firstinspires.ftc.teamcode.managers.sensor.SensorManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;

import java.util.Arrays;

@TeleOp
public class PrestonIsAGiraffeBoyTeleop extends OpMode {
    public MovementManager driver;
    public ManipulationManager hands;
    public InputManager input;
    public SensorManager sensor;
    public GiraffeManager giraffeNeck;
    public NateManager clawPosition;

    private boolean precision = false;
    private boolean dashing = false;
    private double clawCheck;

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
                crservo         (),
                servo           ("nateClaw"),
                motor           ("Carousel", "ClawMotor", "NeckMotor")
        );

        input = new InputManager(gamepad1, gamepad2);
        input.registerInput("drivingControls",
                new MultiplyNode(new InputVariableNode("negativeIfIsShort"),
                        new PlusNode(
                                new MultiInputNode(
                                        new ScaleNode(new JoystickNode("left_stick_y"), 1),
                                        new ScaleNode(new JoystickNode("right_stick_x"), 0.9f),
                                        new ScaleNode(new JoystickNode("left_stick_x"), 1)
                    ),
                                new MultiInputNode(
                                new ScaleNode(new JoystickNode("gamepad2left_stick_y"), 0.4f),
                                new ScaleNode(new JoystickNode("gamepad2right_stick_x"), 0.6f),
                                new ScaleNode(new JoystickNode("gamepad2left_stick_x"), 0.7f)
                            )
                        )
                )
            );
        input.setInputVariable("negativeIfIsShort", 1);

        input.setOverlapResolutionMethod(InputOverlapResolutionMethod.MOST_COMPLEX_ARE_THE_FAVOURITE_CHILD);
        input.registerInput("Carousel",
                new PlusNode(
                        new MultiplyNode(new ButtonNode("cross"), 0.35f),
                        new MultiplyNode(new ButtonNode("circle"), -0.35f)
                )
        );

        input.registerInput("NeckPosAlliance", new ButtonNode("gamepad 2 right trigger"));
        input.registerInput("NeckPosShared", new ButtonNode("gamepad 2 left trigger"));

        input.registerInput("ClawPos0", //also moves the neck to neckNeutral, despite the absolute failure of naming
                new AnyNode(
                        new ButtonNode("gamepad 2 PS"),
                        new ButtonNode("gamepad 1 PS")
                )
        );
        input.registerInput("ClawPosIntake",new ButtonNode("gamepad 2 square"));
        input.registerInput("ClawPos1OrShared", new ButtonNode("gamepad 2 triangle"));
        input.registerInput("ClawPos2",new ButtonNode("gamepad 2 circle"));
        input.registerInput("ClawPos3", new ButtonNode ("gamepad 2 cross"));

        input.registerInput("ClawManualMove",
                new PlusNode(
                        new MultiplyNode(new ButtonNode("gamepad2dpaddown"), -10f),
                        new MultiplyNode(new ButtonNode("gamepad2dpadup"), 10f)
                )
        );
        input.registerInput("NeckManualMove",
                new PlusNode(
                        new MultiplyNode(new ButtonNode("dpadup"), -10f),
                        new MultiplyNode(new ButtonNode("dpaddown"), 10f)
                )
        );
        input.registerInput("ClawOpen",
                new AnyNode(
                        new ButtonNode("leftbumper"),
                        new ButtonNode("gamepad2leftbumper")
                ));
        input.registerInput("EmergencyStop",
                new AllNode(
                                new ButtonNode("dpadleft"),
                                new ButtonNode("dpadright")
                ));

        PriorityAsyncOpmodeComponent.start(() -> {
            driver.driveOmni(input.getFloatArrayOfInput("drivingControls"));
        });

        clawPosition = new NateManager(hands);
        giraffeNeck = new GiraffeManager(clawPosition);
    }
    private boolean shouldActuallyDoThings = true;
    public void loop() {
        try {
            if(shouldActuallyDoThings) real_loop_Bad_Practice_Fix_Me_Later();
        }
        catch (Throwable t) {
            FeatureManager.setIsOpModeRunning(false);
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
        hands.setMotorPower("Carousel", input.getFloat("Carousel"));
        
        hands.manualMoveEncodedMotor("NeckMotor",  (int) input.getFloat("NeckManualMove"));
        hands.manualMoveEncodedMotor("ClawMotor", (int) input.getFloat("ClawManualMove"));

        if(input.getBool("NeckPosAlliance")) {
            giraffeNeck.neckTall();
            input.setInputVariable("negativeIfIsShort", 1);
        }
        if(input.getBool("NeckPosShared")) {
            giraffeNeck.neckShort();
            input.setInputVariable("negativeIfIsShort", -1);
        }

        clawPosition.setClawOpen(giraffeNeck.neckMovementFinished() && input.getBool("ClawOpen"));

        if (input.getBool("ClawPosIntake")) {
            clawPosition.positionHome();
            clawPosition.ifStableBobDownThenUp();
        }

        if (input.getBool("ClawPos1OrShared")) {
            if (giraffeNeck.getNeckPosition() == GiraffeManager.POSITION_TALL) clawPosition.positionOne();
            else clawPosition.positionShared();
        }

        if (input.getBool("ClawPos2")) clawPosition.positionTwo();
        if (input.getBool("ClawPos3")) clawPosition.positionThree();
        if (input.getBool("ClawPos0")) {clawPosition.positionNeutral(); giraffeNeck.neckNeutral(); input.setInputVariable("negativeIfIsShort", 1);}
        if (input.getBool("EmergencyStop")) clawPosition.emergencyStop();

        //FeatureManager.logger.log(BuildHistory.buildName);
        telemetry.addData("FL Power", driver.frontLeft.getPower());
        telemetry.addData("FR Power", driver.frontRight.getPower());
        telemetry.addData("BR Power", driver.backLeft.getPower());
        telemetry.addData("BL Power", driver.backRight.getPower());
        telemetry.addData("Pos Y (encoders)", driver.getCentimeters());
        telemetry.addData("WhichBoy", FeatureManager.getRobotName());
        telemetry.addData("Carousel", hands.getMotorPower("Carousel"));
        telemetry.addData("driver control", Arrays.toString(input.getFloatArrayOfInput("drivingControls")));
        telemetry.addData("ClawTowerTicks", hands.getMotorPosition("ClawMotor"));
        telemetry.addData("ClawTowerTarTicks", hands.getMotorTargetPosition("ClawMotor"));
        telemetry.addData("ClawTowerPower", hands.getMotorPower("ClawMotor"));
        telemetry.addData("ClawServoPos", clawPosition.getClawOpenish());
        telemetry.addData("NeckTowerTicks", hands.getMotorPosition("NeckMotor"));
        telemetry.addData("NeckTowerTarTicks", hands.getMotorTargetPosition("NeckMotor"));
        telemetry.addData("NeckTowerPower", hands.getMotorPower("NeckMotor"));
        telemetry.addData("Build Name", BuildHistory.buildName);
        telemetry.update();

    }

    public void stop() {
        FeatureManager.setIsOpModeRunning(false);
    }
}