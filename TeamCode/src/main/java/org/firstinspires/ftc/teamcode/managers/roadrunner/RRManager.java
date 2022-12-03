package org.firstinspires.ftc.teamcode.managers.roadrunner;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.localization.Localizer;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.*;

public class RRManager extends FeatureManager {
    private SampleMecanumDrive driveRR;
    private TrajectoryBuilder trajBuildRR;
    private TelemetryManager telemetry;
    private Pose2d[] nonono = {new Pose2d(-120, 48), new Pose2d(-72, 48), new Pose2d(-24, 48), new Pose2d(-24, 0), new Pose2d(-120, 0), new Pose2d(-72, 0), new Pose2d(-24, -48), new Pose2d(-120, -48), new Pose2d(-72, -48)};
    /**
     * Initializes the Road Runner Manager
     * @param hardwareMap The hardwareMap for Roadrunner to access for the drive motors
     * @param start The start position for the robot
     * @param telemetryManager The telemetry manager to use for telemetry logging
     */
    public RRManager(@NotNull HardwareMap hardwareMap, @NotNull Pose2d start, @NotNull TelemetryManager telemetryManager){
        driveRR = new SampleMecanumDrive(hardwareMap); //Necessary Component for RoadRunner!
        trajBuildRR = driveRR.trajectoryBuilder(start);
        this.telemetry = telemetryManager;
        calibrateDriveToZero();
        telemetry.addLine("Go to 192.168.43.1:8080/dash for the FTC Dashboard! Unless this is the competition, for which, in that case, never mind...");
    }

    /**
     * Moves the robot to the given id's position and rotates it to the id's given rotation
     * @param id The id for the specified movement: 1 = Center, 2 = Top Corner, 3 = Bottom Corner
     */
    public void moveToPosWithID(int id){
        if(id==1){driveRR.followTrajectory(trajBuildRR.splineToSplineHeading(new Pose2d(-24, 12), Math.toRadians(90)).build());}
        else if(id==2){driveRR.followTrajectory(trajBuildRR.splineToSplineHeading(new Pose2d(0, 72), Math.toRadians(driveRR.getExternalHeading())).build());}
        else if(id==3){driveRR.followTrajectory(trajBuildRR.splineToSplineHeading(new Pose2d(0, -72), Math.toRadians(driveRR.getExternalHeading())).build());}
        driveRR.update();
    }

    /**
     * Marks the displacement from (0, 0) on the FTC Dashboard's field display
     */
    public void markDisp(){
        trajBuildRR.addDisplacementMarker(driveRR.getLocalizer().getPoseEstimate().vec().distTo(new Vector2d(0, 0)), () -> {});
    }

    /**
     * Returns the drive object element from the class FOR TESTING ONLY
     * @return The drive object of the class
     */
    public SampleMecanumDrive getDrive(){
        return driveRR;
    }
    /**
     * Returns the pose object element from the class FOR TESTING ONLY
     * @return The pose object of the class
     */
    public Pose2d getPose(){
        return driveRR.getPoseEstimate();
    }
    /**
     * Returns the trajectory builder object element from the class FOR TESTING ONLY
     * @return The trajectory builder object of the class
     */
    public TrajectoryBuilder getTrajBuildRR(){
        return trajBuildRR;
    }

    /**
     * Calibrates the robot back to its "Home" Position
     */
    public void calibrateDriveToZero(){
        driveRR.setPoseEstimate(new Pose2d(0, 0));

        telemetry.addLine("RoadRunner Drive Recalibrated");
    }

    /**
     * Reinitializes the Trajectory Builder back to (0, 0)
     */

    @TestOnly public void calibrateTrajectoryBuilderToZero(){
        trajBuildRR = driveRR.trajectoryBuilder(new Pose2d(0, 0));
        telemetry.addLine("RoadRunner Drive Recalibrated");
    }

    /**
     * Allows for the developer to specify a specific pose and type of movement for teh robot to follow
     * @param pose The pose to go to
     * @param type The type of movement the robot is to perform
     * @param rotation The end rotation, if needed, for the movement
     */
    public void customMoveWithPose(@NotNull Pose2d pose, @NotNull String type, @Nullable double rotation){
        for(Pose2d poses: nonono){
            if(pose.equals(poses)){
                return;
            }else{
                telemetry.log().add("Path Accepted");
            }
        }
        if(type.equals("strafe")){
            driveRR.followTrajectory(trajBuildRR.strafeTo(pose.vec()).build());
        }
        else if(type.equals("line")){
            driveRR.followTrajectory(trajBuildRR.lineTo(pose.vec()).build());
        }
        else if(type.equals("spline")){
            driveRR.followTrajectory(trajBuildRR.splineTo(pose.vec(), Math.toRadians(rotation)).build());
        }
        else if(type.equals("splinespline")){
            driveRR.followTrajectory(trajBuildRR.splineToSplineHeading(pose, Math.toRadians(rotation)).build());
        }
        else if(type.equals("splineline")){
            driveRR.followTrajectory(trajBuildRR.splineToLinearHeading(pose, Math.toRadians(rotation)).build());
            telemetry.addLine("WARNING! Using this movement will likely result in a PathContinuityError!");
        }
        getLocalizer().update();
    }

    /**
     * Creates a simple Trajectory Sequence for the robot to follow made ot of arrays
     * @param poseArr The array of positions to go to
     * @param typeArr The array of the types of movement the correspond to the positions
     * @param rotationArr The array of the different rotations that correspond with the positions and types of movement
     * @throws Exception
     */
    public void customMoveSequenceWithPose(@NotNull Pose2d[] poseArr, @NotNull String[] typeArr, @NotNull double[] rotationArr) throws SequenceInitException, Exception {
        if(poseArr.length != typeArr.length || typeArr.length != rotationArr.length || poseArr.length != rotationArr.length){
            throw new SequenceInitException("Array Lengths for sequence do not match! "+poseArr.length+" does not equal "+typeArr.length+" or does not equal "+rotationArr.length);
        }
        for(int i = 0; i<poseArr.length; i++) {
            Pose2d pose = poseArr[i];
            String type = typeArr[i];
            double rotation = rotationArr[i];
            for(Pose2d poses: nonono){
                if(pose.equals(poses)){
                    return;
                }else{
                    telemetry.log().add("Path Accepted");
                }
            }
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
                telemetry.addLine("WARNING! Using this movement will likely result in a PathContinuityError!");
            }
            getLocalizer().update();
            driveRR.waitForIdle();
        }
    }

    public boolean notBusy(){
        return driveRR.notBusy();
    }
    public void updateLocalizer(){
        driveRR.getLocalizer().update();
    }
    public Localizer getLocalizer(){
        return driveRR.getLocalizer();

    }
    @Override
    public String toString(){
        return "Road Runner with position of "+getPose().toString();
    }


}
