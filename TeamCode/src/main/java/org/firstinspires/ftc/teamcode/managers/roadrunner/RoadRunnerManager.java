package org.firstinspires.ftc.teamcode.managers.roadrunner;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.localization.Localizer;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;
import org.firstinspires.ftc.teamcode.util.AssetsTrajectoryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.*;
import org.junit.Test;

import java.util.Arrays;

/**
 * Manager for Pathing and Dead Reckoning... Makes Road runner much easier to use with a set of complex methods for making precise paths. created by ACHYUT SHASTRI
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
    public RoadRunnerManager(@NotNull HardwareMap hardwareMap, @NotNull Pose2d start, @NotNull TelemetryManager telemetryManager, @NotNull OpMode opMode) {
        driveRR = new SampleMecanumDrive(hardwareMap); //Necessary Component for RoadRunner!
        trajBuildRR = driveRR.trajectoryBuilder(start);
        this.opMode = opMode;
        this.telemetry = telemetryManager;
        this.t = AssetsTrajectoryManager.load("dropoffleft", telemetry);
        this.t2 = AssetsTrajectoryManager.load("dropoffleftblue", telemetry);
        this.t3 = AssetsTrajectoryManager.load("dropoffright", telemetry);
        this.t4 = AssetsTrajectoryManager.load("dropoffrightblue", telemetry);

        calibrateDriveToZero();
        calibrateDriveToAutoPosition();
        telemetry.log().add("Go to 192.168.43.1:8080/dash for the FTC Dashboard! Unless this is the competition, for which, in that case, never mind, don't use FTC Dashboard...");

    }



    /**
     * Moves the robot to the given id's position and rotates it to the id's given rotation
     *
     * @param id The id for the specified movement: 1 = Center, 2 = Top Corner, 3 = Bottom Corner
     */
    public void moveToPosWithID(int id) {

        if (id == 1) {
            telemetry.log().add("Trajectory: ", t);
            driveRR.followTrajectory(t);
        } else if (id == 2) {
            telemetry.log().add("Trajectory: ", t2);
            driveRR.followTrajectory(t2);
        } else if (id == 3) {
            telemetry.log().add("Trajectory: ", t3);
            driveRR.followTrajectory(t3);
        } else if (id == 4) {
            telemetry.log().add("Trajectory: ", t4);
            driveRR.followTrajectory(t4);
        }
        return;


    }

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
        opMode.stop();
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

        telemetry.log().add("RoadRunner Drive Calibrated to 0,0");
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
        if (type.equals("strafe")) {
            driveRR.followTrajectory(trajBuildRR.strafeTo(pose.vec()).build());
            telemetry.log().add("RR PATH FINISHED");
        } else if (type.equals("line")) {
            driveRR.followTrajectory(trajBuildRR.lineTo(pose.vec()).build());
        } else if (type.equals("spline")) {
            driveRR.followTrajectory(trajBuildRR.splineTo(pose.vec(), Math.toRadians(rotation)).build());
        } else if (type.equals("splinespline")) {
            driveRR.followTrajectory(trajBuildRR.splineToSplineHeading(pose, Math.toRadians(rotation)).build());
        } else if (type.equals("splineline")) {
            driveRR.followTrajectory(trajBuildRR.splineToLinearHeading(pose, Math.toRadians(rotation)).build());
            telemetry.log().add("WARNING! Using this movement will likely result in a PathContinuityError!");
        } else if (type.equals("turn")) {
            driveRR.turn(rotation);
        } else if (type.equals("forward")) {
            driveRR.followTrajectory(trajBuildRR.forward(rotation).build());
            telemetry.log().add("Dist: " + rotation);
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
        if (poseArr.length != typeArr.length || typeArr.length != rotationArr.length || poseArr.length != rotationArr.length) {
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
            if (type.equals("strafe")) {
                driveRR.followTrajectory(trajBuildRR.strafeTo(pose.vec()).build());
            } else if (type.equals("line")) {
                driveRR.followTrajectory(trajBuildRR.lineTo(pose.vec()).build());
            } else if (type.equals("spline")) {
                driveRR.followTrajectory(trajBuildRR.splineTo(pose.vec(), Math.toRadians(rotation)).build());
            } else if (type.equals("splinespline")) {
                driveRR.followTrajectory(trajBuildRR.splineToSplineHeading(pose, Math.toRadians(rotation)).build());
            } else if (type.equals("splineline")) {
                driveRR.followTrajectory(trajBuildRR.splineToLinearHeading(pose, Math.toRadians(rotation)).build());
                telemetry.log().add("WARNING! Using this movement will likely result in a PathContinuityError!");
            } else if (type.equals("turn")) {
                driveRR.turn(rotation);
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

        if (poseArr.length != typeArr.length || typeArr.length != rotationArr.length || poseArr.length != rotationArr.length) {
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
            if (type.equals("strafe")) {
                tsb = tsb.strafeTo(pose.vec());
            } else if (type.equals("line")) {
                tsb = tsb.lineTo(pose.vec());
            } else if (type.equals("spline")) {
                tsb = tsb.splineTo(pose.vec(), Math.toRadians(rotation));
            } else if (type.equals("splinespline")) {
                tsb = tsb.splineToSplineHeading(pose, Math.toRadians(rotation));
            } else if (type.equals("splineline")) {
                tsb = tsb.splineToLinearHeading(pose, Math.toRadians(rotation));
                telemetry.log().add("WARNING! Using this movement will likely result in a PathContinuityError!");
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

    /**
     * Returns the overview of Roadrunner's instance variables and reference ids
     *
     * @return String of overview
     */
    @Override
    public String toString() {
        return "RoadRunnerManager{" +
                "driveRR=" + driveRR +
                ", trajBuildRR=" + trajBuildRR +
                ", tsb=" + tsb +
                ", telemetry=" + telemetry +
                ", nonono=" + Arrays.toString(nonono) +
                "} For OpMode " + opMode.toString();
    }

    /**
     * Get the gamepad inputs and use them to sense where the robot is displacing to...
     *
     * @param gamepad1 Gamepad 1 (Main driver)
     * @param gamepad2 Gamepad 2 (Micro driver)
     * @return
     */
    public void doOmniDisplace(Gamepad gamepad1, Gamepad gamepad2, @NonNull float[] driving) {


        input = new Vector2d(
                driving[1],
                driving[0]
        );

        drivePower = new Pose2d(
                input.getX(),
                input.getY(),
                -driving[2]
        );
        vel = drivePower;
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
        driveRR.fr.setDirection(DcMotorSimple.Direction.FORWARD);
        driveRR.br.setDirection(DcMotorSimple.Direction.FORWARD);
        driveRR.fl.setDirection(DcMotorSimple.Direction.FORWARD);
        driveRR.bl.setDirection(DcMotorSimple.Direction.FORWARD);
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
}