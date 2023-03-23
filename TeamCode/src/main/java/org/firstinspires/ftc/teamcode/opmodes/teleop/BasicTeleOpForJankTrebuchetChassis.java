package org.firstinspires.ftc.teamcode.opmodes.teleop;

import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.crservo;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.motor;
import static org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager.servo;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

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

@TeleOp
public class BasicTeleOpForJankTrebuchetChassis extends OpMode {
    public MovementManager driver;
    public ManipulationManager hands;
    public InputManager input;
    @Override
    public void init() {
        // Phone is labelled as Not Ready For Use
        FeatureManager.setIsOpModeRunning(true);
        FeatureManager.reconfigureForTeleop();
        //TODO: Mention is redundant statement

        //Necessary Component for RoadRunner!
        /*
        DcMotor fl = hardwareMap.get(DcMotor.class, "fl");
        DcMotor fr = hardwareMap.get(DcMotor.class, "fr");
        DcMotor br = hardwareMap.get(DcMotor.class, "br");
        DcMotor bl = hardwareMap.get(DcMotor.class, "bl");
        driver = new MovementManager(fl, fr, br, bl);
         */
        hands = new ManipulationManager(
                hardwareMap,
                crservo         (),
                servo           (),
                motor           ("flipper")
        );
        input = new InputManager(gamepad1, gamepad2);
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
        input.registerInput("grabberToggle",
                new ButtonNode("b")
        );

        input.setOverlapResolutionMethod(InputOverlapResolutionMethod.MOST_COMPLEX_ARE_THE_FAVOURITE_CHILD);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        TelemetryPacket packet = new TelemetryPacket();
        packet.put("x", 3.7);
        packet.put("status", "alive");
        FtcDashboard dashboard = FtcDashboard.getInstance();

        telemetry.addData("FL Power", driver.frontLeft.getPower());
        telemetry.addData("FR Power", driver.frontRight.getPower());
        telemetry.addData("BR Power", driver.backLeft.getPower());
        telemetry.addData("BL Power", driver.backRight.getPower());
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

        }
        telemetry.update();
    }
    public void stop() {
        FeatureManager.setIsOpModeRunning(false);
    }
}