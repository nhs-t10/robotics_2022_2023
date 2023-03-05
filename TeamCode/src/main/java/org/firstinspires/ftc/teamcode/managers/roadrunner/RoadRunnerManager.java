package org.firstinspires.ftc.teamcode.managers.roadrunner;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.drive.DriveSignal;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.localization.Localizer;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;
import org.firstinspires.ftc.teamcode.util.AssetsTrajectoryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.*;
import org.junit.Test;

import java.util.Arrays;

import kotlin.OptionalExpectation;

/**
 * Manager for Pathing, Dead Reckoning, and Macros... Makes Road runner much easier to use with a set of complex methods for making precise paths. created by ACHYUT SHASTRI
 */
@Config
public class RoadRunnerManager extends FeatureManager {
    public static Pose2d currentPose = new Pose2d(0, 0, Math.toRadians(0));
    private SampleMecanumDrive driveRR;
    private Localizer l;
    private TrajectoryBuilder trajBuildRR;
    private TrajectorySequenceBuilder tsb;
    private TelemetryManager telemetry;
    private OpMode opMode;
    private Vector2d estimatedPosition;
    private float[] sum;
    private Pose2d vel;
    private Vector2d input;
    private double denom;
    private Pose2d drivePower;
    private Trajectory t;
    private Trajectory t2;
    private Trajectory t3;
    private Trajectory t4;
    private Trajectory t5;
    private Trajectory t6;
    private Trajectory t7;
    private Trajectory t8;
    private Trajectory t9;
    private Trajectory t10;
    private Trajectory t11;
    private Trajectory t12;
    private Trajectory t13;
    private Trajectory t14;
    private Trajectory t15;
    private Trajectory t16;
    private Trajectory t17;
    private DriveConstants d;

    Thread load = new Thread(() -> {
        this.t = AssetsTrajectoryManager.load("dropoffleft", telemetry);
        this.t2 = AssetsTrajectoryManager.load("dropoffleftblue", telemetry);
        this.t3 = AssetsTrajectoryManager.load("dropoffright", telemetry);
        this.t4 = AssetsTrajectoryManager.load("dropoffrightblue", telemetry);
        this.t13 = AssetsTrajectoryManager.load("JunctionToParkingGreenLeft", telemetry);
        this.t15 = AssetsTrajectoryManager.load("JunctionToParkingPinkRight", telemetry);
        telemetry.log().add("DONE1");

    });
    Thread load2 = new Thread(() -> {
        this.t5 = AssetsTrajectoryManager.load("toLeftPole", telemetry);
        this.t6 = AssetsTrajectoryManager.load("MoveToHighLeft", telemetry);
        this.t7 = AssetsTrajectoryManager.load("BackToStackLeft", telemetry);
        this.t8 = AssetsTrajectoryManager.load("JunctionToParkingBlueLeft", telemetry);
        this.t16 = AssetsTrajectoryManager.load("JunctionToParkingGreenRight", telemetry);
        telemetry.log().add("DONE2");
    });
    Thread load3 = new Thread(() -> {
        this.t9 = AssetsTrajectoryManager.load("toRightPole", telemetry);
        this.t10 = AssetsTrajectoryManager.load("MoveToHighRight", telemetry);
        this.t11 = AssetsTrajectoryManager.load("BackToStackRight", telemetry);
        this.t12 = AssetsTrajectoryManager.load("JunctionToParkingBlueRight", telemetry);
        this.t14 = AssetsTrajectoryManager.load("JunctionToParkingPinkLeft", telemetry);
        telemetry.log().add("DONE3");
    });
    Thread load4 = new Thread(() -> {
        this.t17 = AssetsTrajectoryManager.load("ToMiddlePoleLeft", telemetry);
        telemetry.log().add("DONE4");
    });
    private double firstWheelLastRotation, secondWheelLastRotation, lastHeading;
    private static final Pose2d[] nonono = {new Pose2d(-120, 48), new Pose2d(-72, 48), new Pose2d(-24, 48), new Pose2d(-24, 0), new Pose2d(-120, 0), new Pose2d(-72, 0), new Pose2d(-24, -48), new Pose2d(-120, -48), new Pose2d(-72, -48)};

    /**
     * Initializes the Road Runner Manager
     *
     * @param hardwareMap      The hardwareMap for Roadrunner to access for the drive motors
     * @param start            The start position for the robot
     * @param telemetryManager The telemetry manager to use for telemetry logging
     *                         {@link #telemetry}
     */
    public RoadRunnerManager(@NotNull HardwareMap hardwareMap, @NotNull Pose2d start, @NotNull TelemetryManager telemetryManager, @NotNull OpMode opMode, @NotNull boolean isTeleop) {
        //DriveConstants.updateBattery(hardwareMap);
        driveRR = new SampleMecanumDrive(hardwareMap); //Necessary Component for RoadRunner! DO NOT DELETE!
        trajBuildRR = driveRR.trajectoryBuilder(start);
        this.opMode = opMode;
        this.telemetry = telemetryManager;
        if(!isTeleop) {
            loadYAMLs();
        }
        calibrateDriveToZero();
        calibrateDriveToAutoPosition();
        telemetry.log().add("Go to 192.168.43.1:8080/dash for the FTC Dashboard! Unless this is the competition, for which, in that case, never mind, don't use FTC Dashboard...");
    }
    /**
     * Adds all YAMLs... YAMLs are stored in assets/trajectory in the main folder
     * Uses {@link AssetsTrajectoryManager}
     */
    public void loadYAMLs(){
        load.start();
        load2.start();
        load3.start();
    }
    /**
     * Moves the robot to the given id's position and rotates it to the id's given rotation
     *
     * @param id The id for the specified movement
     */
    public void moveToPosWithID(int id) {
        switch (id) {
            case 1:
                telemetry.log().add("Trajectory: ", t);
                driveRR.followTrajectory(t);
                break;
            case 2:
                telemetry.log().add("Trajectory: ", t2);
                driveRR.followTrajectory(t2);
                break;
            case 3:
                telemetry.log().add("Trajectory: ", t3);
                driveRR.followTrajectory(t3);
                break;
            case 4:
                telemetry.log().add("Trajectory: ", t4);
                driveRR.followTrajectory(t4);
                break;
            case 5:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).forward(36).build());
                break;
            case 6:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).forward(18).build());
                break;
            case 7:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).forward(0).build());
                break;
            case 8:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).forward(36).build());
                break;
            case 9:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).forward(18).build());
                break;
            case 10:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).forward(0).build());
                break;
            case 11:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).strafeRight(14).build());
                break;
            case 12:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).back(40).build());
                break;
            case 13:
                telemetry.log().add("Trajectory: ", t5);
                driveRR.followTrajectory(t5);
                break;
            case 14:
                telemetry.log().add("Trajectory: ", t6);
                driveRR.followTrajectory(t6);
                break;
            case 15:
                telemetry.log().add("Trajectory: ", t7);
                driveRR.followTrajectory(t7);
                break;
            case 16:
                telemetry.log().add("Trajectory: ", t8);
                driveRR.followTrajectory(t8);
                break;
            case 17:
                telemetry.log().add("Trajectory: ", t9);
                driveRR.followTrajectory(t9);
                break;
            case 18:
                telemetry.log().add("Trajectory: ", t10);
                driveRR.followTrajectory(t10);
                break;
            case 19:
                telemetry.log().add("Trajectory: ", t11);
                driveRR.followTrajectory(t11);
                break;
            case 20:
                telemetry.log().add("Trajectory: ", t12);
                driveRR.followTrajectory(t12);
                break;
            case 21:
                driveRR.turn(1.57079632679);
                break;
            case 22:
                driveRR.turn(-1.57079632679);
                break;
            case 23:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).forward(17).build());
                break;
            case 24:
                driveRR.followTrajectory(t13);
                break;
            case 25:
                driveRR.followTrajectory(t14);
                break;
            case 26:
                driveRR.followTrajectory(t15);
                break;
            case 27:
                driveRR.followTrajectory(t16);
                break;
            case 28:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).strafeLeft(14).build());
                break;
            case 29:
                driveRR.followTrajectory(t17);
                break;
            case 30:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).forward(6).build());
                break;
            default:
                return;
        }
        return;


    }
    public void moveToPosWithIDs(int... ids) {
        for (int id : ids) {
            switch (id) {
                case 1:
                    telemetry.log().add("Trajectory: ", t);
                    driveRR.followTrajectory(t);
                    break;
                case 2:
                    telemetry.log().add("Trajectory: ", t2);
                    driveRR.followTrajectory(t2);
                    break;
                case 3:
                    telemetry.log().add("Trajectory: ", t3);
                    driveRR.followTrajectory(t3);
                    break;
                case 4:
                    telemetry.log().add("Trajectory: ", t4);
                    driveRR.followTrajectory(t4);
                    break;
                case 5:
                    driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).strafeLeft(50).build());
                    break;
                case 6:
                    driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).strafeLeft(31.5).build());
                    break;
                case 7:
                    driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).strafeLeft(12).build());
                    break;
                case 8:
                    driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).strafeRight(50).build());
                    break;
                case 9:
                    driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).strafeRight(50).build());
                    break;
                case 10:
                    driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).strafeRight(55).build());
                    break;
                case 11:
                    driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).forward(26).build());
                    break;
                case 12:
                    driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).back(40).build());
                    break;
                case 13:
                    telemetry.log().add("Trajectory: ", t5);
                    driveRR.followTrajectory(t5);
                    break;
                case 14:
                    telemetry.log().add("Trajectory: ", t6);
                    driveRR.followTrajectory(t6);
                    break;
                case 15:
                    telemetry.log().add("Trajectory: ", t7);
                    driveRR.followTrajectory(t7);
                    break;
                case 16:
                    telemetry.log().add("Trajectory: ", t8);
                    driveRR.followTrajectory(t8);
                    break;
                case 17:
                    telemetry.log().add("Trajectory: ", t9);
                    driveRR.followTrajectory(t9);
                    break;
                case 18:
                    telemetry.log().add("Trajectory: ", t10);
                    driveRR.followTrajectory(t10);
                    break;
                case 19:
                    telemetry.log().add("Trajectory: ", t11);
                    driveRR.followTrajectory(t11);
                    break;
                case 20:
                    telemetry.log().add("Trajectory: ", t12);
                    driveRR.followTrajectory(t12);
                    break;
                case 21:
                    driveRR.turn(90);
                    break;
                case 22:
                    driveRR.turn(-90);
                    break;
                default:
                    return;
            }
        }
        return;
    }

    public void activateMacro(int id){
        switch (id) {
            case 1:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).lineToLinearHeading(new Pose2d(-20, 0, Math.toRadians(180))).build());
                break;
            case 2:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).strafeRight(90).build());
                break;
            case 3:
                driveRR.followTrajectory(driveRR.trajectoryBuilder(new Pose2d()).strafeLeft(90).build());
                break;
        }
    }

    @Deprecated
    public void setBusy() {
        driveRR.isBusy();
    }

    /**
     * Marks the displacement from (0, 0) on the FTC Dashboard's field display
     */
    public void markDisp() {
        trajBuildRR.addDisplacementMarker(driveRR.getLocalizer().getPoseEstimate().vec().distTo(new Vector2d(0, 0)), () -> {
        });
    }

    /**
     * Allows for the opMode to stop on a critical error if chosen
     */
    public void errorInterrupt() {
        if(driveRR.isBusy()){
            driveRR.breakFollowing();
        }
        opMode.stop();
    }
    public void stopDrive() {
        if(driveRR.isBusy()){
            driveRR.breakFollowing();
        }
    }
    /**
     * Method that sets the current Roadrunner position to what AutoAuto reports
     */
    public void setAutoAutoPosition(double x, double y, double rot) {
        RoadRunnerManager.currentPose = new Pose2d(x, y, Math.toRadians(rot));
    }

    /**
     * Returns the drive object element from the class FOR TESTING ONLY
     *
     * @return The drive object of the class
     */
    public SampleMecanumDrive getDrive() {
        return driveRR;
    }

    /**
     * Returns the pose object element from the class FOR TESTING ONLY
     *
     * @return The pose object of the class
     */
    public Pose2d getPose() {
        return driveRR.getPoseEstimate();
    }

    /**
     * Returns the trajectory builder object element from the class FOR TESTING ONLY
     *
     * @return The trajectory builder object of the class
     */
    public TrajectoryBuilder getTrajBuildRR() {
        return trajBuildRR;
    }

    /**
     * Calibrates the robot back to its "Home" Position
     */
    public void calibrateDriveToZero() {
        driveRR.setPoseEstimate(new Pose2d(0, 0, Math.toRadians(0)));

        telemetry.log().add("RoadRunner Drive Calibrated to 0,0");
    }

    public void calibrateDrive(int x, int y, double rotation) {
        driveRR.setPoseEstimate(new Pose2d(x, y, Math.toRadians(rotation)));

        telemetry.log().add("RoadRunner Drive Calibrated to " + x + ", " + y + " and " + rotation + " degrees.");
    }

    public void calibrateDriveToAutoPosition() {
        driveRR.setPoseEstimate(currentPose);
        telemetry.log().add("RoadRunner Drive Calibrated to Auto Position");
    }

    public double[] getCurrPose() {
        return new double[]{driveRR.getPoseEstimate().getX(), driveRR.getPoseEstimate().getY(), Math.toDegrees(driveRR.getPoseEstimate().getHeading())};
    }

    /**
     * Reinitializes the Trajectory Builder back to (0, 0)
     */

    @TestOnly
    public void calibrateTrajectoryBuilderToZero() {
        trajBuildRR = driveRR.trajectoryBuilder(new Pose2d(0, 0));
        telemetry.log().add("RoadRunner Drive Recalibrated");
    }

    /**
     * Allows for the developer to specify a specific pose and type of movement for teh robot to follow
     *
     * @param pose     The pose to go to
     * @param type     The type of movement the robot is to perform
     * @param rotation The end rotation, if needed, for the movement
     */
    public void customMoveWithPose(Pose2d pose, String type, double rotation) {

        for (Pose2d poses : nonono) {
            if (pose.equals(poses)) {
                return;
            } else {

            }
        }
        telemetry.log().add("Path Accepted");
        switch (type) {
            case "strafe":
                driveRR.followTrajectory(trajBuildRR.strafeTo(pose.vec()).build());
                telemetry.log().add("RR PATH FINISHED");
                break;
            case "line":
                driveRR.followTrajectory(trajBuildRR.lineTo(pose.vec()).build());
                break;
            case "spline":
                driveRR.followTrajectory(trajBuildRR.splineTo(pose.vec(), Math.toRadians(rotation)).build());
                break;
            case "splinespline":
                driveRR.followTrajectory(trajBuildRR.splineToSplineHeading(pose, Math.toRadians(rotation)).build());
                break;
            case "splineline":
                driveRR.followTrajectory(trajBuildRR.splineToLinearHeading(pose, Math.toRadians(rotation)).build());
                telemetry.log().add("WARNING! Using this movement will likely result in a PathContinuityError!");
                break;
            case "turn":
                driveRR.turn(rotation);
                break;
            case "forward":
                driveRR.followTrajectory(trajBuildRR.forward(rotation).build());
                telemetry.log().add("Dist: " + rotation);
                break;
        }
        telemetry.log().add("Out of if");
        //updateLocalizer();

    }

    /**
     * Creates a simple Trajectory Sequence for the robot to follow made ot of arrays. PS: If trying t turn in one phase, put an empty Pose2d in that index of poseArr.
     *
     * @param poseArr     The array of positions to go to
     * @param typeArr     The array of the types of movement the correspond to the positions
     * @param rotationArr The array of the different rotations that correspond with the positions and types of movement
     * @throws SequenceInitException The exception that tells you a sequence calculation has filed on init
     * @throws Exception             The regular exception that SequenceInit Exception uses
     */
    public void customMoveSequenceWithPose(@NotNull Pose2d[] poseArr, @NotNull String[] typeArr, @NotNull double[] rotationArr) throws SequenceInitException, Exception {
        if (typeArr.length != rotationArr.length || poseArr.length != rotationArr.length) {
            throw new SequenceInitException("Array Lengths for sequence do not match! " + poseArr.length + " does not equal " + typeArr.length + " or does not equal " + rotationArr.length, this);
        }
        for (int i = 0; i < poseArr.length; i++) {
            updateLocalizer();

            Pose2d pose = poseArr[i];
            String type = typeArr[i];
            double rotation = rotationArr[i];
            for (Pose2d poses : nonono) {
                if (pose.equals(poses)) {
                    return;
                } else {

                }
            }
            telemetry.log().add("Path Accepted");
            switch (type) {
                case "strafe":
                    driveRR.followTrajectory(trajBuildRR.strafeTo(pose.vec()).build());
                    break;
                case "line":
                    driveRR.followTrajectory(trajBuildRR.lineTo(pose.vec()).build());
                    break;
                case "spline":
                    driveRR.followTrajectory(trajBuildRR.splineTo(pose.vec(), Math.toRadians(rotation)).build());
                    break;
                case "splinespline":
                    driveRR.followTrajectory(trajBuildRR.splineToSplineHeading(pose, Math.toRadians(rotation)).build());
                    break;
                case "splineline":
                    driveRR.followTrajectory(trajBuildRR.splineToLinearHeading(pose, Math.toRadians(rotation)).build());
                    telemetry.log().add("WARNING! Using this movement will likely result in a PathContinuityError!");
                    break;
                case "turn":
                    driveRR.turn(rotation);
                    break;
            }

            driveRR.waitForIdle();
            updateLocalizer();

        }
    }

    /**
     * Creates a Roadrunner Trajectory Sequence using a different method that the robot will follow seamlessly: In development
     *
     * @param poseArr     Poses to follow
     * @param typeArr     Types of movement to follow
     * @param rotationArr Rotations to follow
     * @throws SequenceInitException The exception that tells you a sequence calculation has filed on init
     * @throws Exception             The regular exception that SequenceInit Exception uses
     */
    @Test
    public void customMoveSequenceWithPoseTrajSequence(@NotNull Pose2d[] poseArr, @NotNull String[] typeArr, @NotNull double[] rotationArr) throws SequenceInitException, Exception {

        if (typeArr.length != rotationArr.length || poseArr.length != rotationArr.length) {
            throw new SequenceInitException("Array Lengths for sequence do not match! " + poseArr.length + " does not equal " + typeArr.length + " or does not equal " + rotationArr.length, this);
        }
        tsb = driveRR.trajectorySequenceBuilder(driveRR.getPoseEstimate());
        for (int i = 0; i < poseArr.length; i++) {
            updateLocalizer();
            Pose2d pose = poseArr[i];
            String type = typeArr[i];
            double rotation = rotationArr[i];
            for (Pose2d poses : nonono) {
                if (pose.equals(poses)) {
                    return;
                } else {

                }
            }
            telemetry.log().add("Path Accepted");
            switch (type) {
                case "strafe":
                    tsb = tsb.strafeTo(pose.vec());
                    break;
                case "line":
                    tsb = tsb.lineTo(pose.vec());
                    break;
                case "spline":
                    tsb = tsb.splineTo(pose.vec(), Math.toRadians(rotation));
                    break;
                case "splinespline":
                    tsb = tsb.splineToSplineHeading(pose, Math.toRadians(rotation));
                    break;
                case "splineline":
                    tsb = tsb.splineToLinearHeading(pose, Math.toRadians(rotation));
                    telemetry.log().add("WARNING! Using this movement will likely result in a PathContinuityError!");
                    break;
                default:
                    return;
            }
            driveRR.followTrajectorySequence(tsb.build());
            driveRR.waitForIdle();
            updateLocalizer();

        }
    }

    /**
     * Gets whether roadrunner is currently busy
     *
     * @return Boolean result
     */
    public boolean notBusy() {
        return driveRR.notBusy();
    }

    /**
     * Updates the Localizer, allowing it to refresh its position
     */
    public void updateLocalizer() {
        driveRR.getLocalizer().update();
    }

    /**
     * Gets the localizer of the drive object
     *
     * @return Localizer of RoadRunner
     */
    public Localizer getLocalizer() {
        return driveRR.getLocalizer();

    }

    /**
     * Determines whether a pose is viable for te robot to go to
     *
     * @param pose2d The pose to test
     * @return Boolean on whether the pose is viable
     */
    @Deprecated
    public boolean isPoseViable(Pose2d pose2d) {
        for (Pose2d poses : nonono) {
            if (pose2d.equals(poses)) {
                return false;
            } else {

            }
        }
        telemetry.log().add("Path Accepted");
        return true;
    }

    public boolean arePosesViable(Pose2d... pose2ds) {
        boolean pass = true;
        for (Pose2d pose2d : pose2ds) {
            for (Pose2d poses : nonono) {
                if (pose2d.equals(poses)) {
                    telemetry.log().add("Path Not Accepted! Pose:" +pose2d.getX()+", "+pose2d.getY()+", "+Math.toDegrees(pose2d.getHeading())+" degrees");
                    pass = false;
                } else {

                }
            }
        }
        if(pass){
            telemetry.log().add("Paths Accepted");
            return true;
        }
        return false;

    }

    /**
     * Get the gamepad inputs and use them to sense where the robot is displacing to...
     *
     * @param gamepad1 Gamepad 1 (Main driver)
     * @param gamepad2 Gamepad 2 (Micro driver)
     */
    public void doOmniDisplace(Gamepad gamepad1, Gamepad gamepad2, @NonNull float[] driving) {
        drivePower = new Pose2d(
                driving[1],
                driving[0],
                -driving[2]
        );

        if (Math.abs(drivePower.getX()) + Math.abs(drivePower.getY())
                + Math.abs(drivePower.getHeading()) > 1) {
            // re-normalize the powers according to the weights
            denom = 1 * Math.abs(drivePower.getX())
                    + 1 * Math.abs(drivePower.getY())
                    + 1 * Math.abs(drivePower.getHeading());

            vel = new Pose2d(
                    1 * drivePower.getX(),
                    1 * drivePower.getY(),
                    1 * drivePower.getHeading()
            ).div(denom);
        }

        sum = PaulMath.omniCalc((float) vel.getX(), (float) vel.getY(), (float) vel.getHeading());
        driveRR.setMotorPowers(sum[0], -sum[1], -sum[2], sum[3]);
        /*driveRR.setWeightedDrivePower(
                new Pose2d(
                        input.getX(),
                        input.getY(),
                        -driving[2]
                )
        );*/
        driveRR.update();

    }

    public void reverseMotorsOmni() {
        for (DcMotorEx dcMotorEx : Arrays.asList(driveRR.fr, driveRR.br, driveRR.fl, driveRR.bl)) {
            dcMotorEx.setDirection(DcMotorSimple.Direction.FORWARD);
        }
    }

    public boolean areMotorsIdle(){
        double[] analyzer = new double[]{driveRR.fr.getPower(), driveRR.fl.getPower(), driveRR.br.getPower(), driveRR.bl.getPower()};
        for(double d: analyzer){
            if(!(Math.abs(d)<=0.05)){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the overview of Roadrunner's instance variables and reference ids
     *
     * @return String of overview
     */
    @NonNull
    @Override
    public String toString() {
        return "RoadRunnerManager{" +
                "driveRR=" + driveRR +
                ", l=" + l +
                ", trajBuildRR=" + trajBuildRR +
                ", tsb=" + tsb +
                ", telemetry=" + telemetry +
                ", opMode=" + opMode +
                ", estimatedPosition=" + estimatedPosition +
                ", sum=" + Arrays.toString(sum) +
                ", vel=" + vel +
                ", input=" + input +
                ", denom=" + denom +
                ", drivePower=" + drivePower +
                ", t=" + t +
                ", t2=" + t2 +
                ", t3=" + t3 +
                ", t4=" + t4 +
                ", t5=" + t5 +
                ", t6=" + t6 +
                ", t7=" + t7 +
                ", t8=" + t8 +
                ", t9=" + t9 +
                ", t10=" + t10 +
                ", t11=" + t11 +
                ", t12=" + t12 +
                ", t13=" + t13 +
                ", t14=" + t14 +
                ", d=" + d +
                ", load=" + load +
                ", load2=" + load2 +
                ", load3=" + load3 +
                ", firstWheelLastRotation=" + firstWheelLastRotation +
                ", secondWheelLastRotation=" + secondWheelLastRotation +
                ", lastHeading=" + lastHeading +
                '}';
    }
}