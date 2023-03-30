package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.Misc;
import org.firstinspires.ftc.teamcode.managers.roadrunner.RoadRunnerManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequenceBuilder;

import java.util.ArrayList;

/**
 * Op mode for computing RoadRunner paths from Trajectory Sequences...
 *
 */
@Config
@Autonomous(group = "drive")
public class TrajectoryExtrapolator extends LinearOpMode {
    public static double MAX_POWER = 1;
    public static double len = 17.0;
    public static double wid = 15.5;
    public static double halfwid = wid/2.0;
    public static double offsetWid = halfwid+0.5;
    public static double halflen = len/2.0;
    public static double offsetLen = halflen+0.5;
    public static Pose2d finalDest = new Pose2d(30,30); // in
    public static double[][] rangesX = {{-72+halfwid, -48-offsetWid}, {-48+offsetWid, -24-offsetWid}, {-24+offsetWid, 0-offsetWid},{0+offsetWid, 24-offsetWid}, {24+offsetWid, 48-offsetWid}, {48+offsetWid, 72-halfwid}};
    public static double[][] rangesY = {{-72+offsetLen, -48-offsetLen}, {-48+offsetLen, -24-offsetLen}, {-24+offsetLen, 0-offsetLen},{0+offsetLen, 24-offsetLen}, {24+offsetLen, 48-offsetLen}, {48+offsetLen, 72-halflen}};
    @Override
    public void runOpMode() throws InterruptedException {


        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        RoadRunnerManager drive  = new RoadRunnerManager(hardwareMap, new Pose2d(0, 0), (TelemetryManager) telemetry, this, true);
        TrajectoryBuilder nTrajBuild = drive.getDrive().trajectoryBuilder(new Pose2d());
        NanoClock clock = NanoClock.system();

        telemetry.addLine("Press play to begin the feedforward tuning routine");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        telemetry.clearAll();
        telemetry.addLine("Would you like to fit kStatic?");
        telemetry.addLine("Press (Y/Δ) for yes, (B/O) for no");
        telemetry.update();


        while (!isStopRequested()) {
            if (gamepad1.y) {

                while (!isStopRequested() && gamepad1.y) {
                    idle();
                }
                break;
            } else if (gamepad1.b) {
                while (!isStopRequested() && gamepad1.b) {
                    idle();
                }
                break;
            }
            idle();
        }

        telemetry.clearAll();
        telemetry.addLine(Misc.formatInvariant(
                "Place your robot on the field at starting position"));
        telemetry.addLine("Press (Y/Δ) to begin");
        telemetry.update();

        while (!isStopRequested() && !gamepad1.y) {
            idle();
        }
        while (!isStopRequested() && gamepad1.y) {
            idle();
        }
        TrajectorySequenceBuilder trajBuild = drive.getDrive().trajectorySequenceBuilder(new Pose2d());
        ArrayList<Trajectory> Pos = new ArrayList<>();
        double prevX = 0;
        double prevY = 0;
        double prevRot = 0;
        int numStops = (int) (finalDest.getX() / 24) + 1;
        if(numStops < (int) (finalDest.getY() / 24) + 1){
            numStops = (int) (finalDest.getY() / 24) + 1;
        }
        int directionX = (int) (Math.abs(finalDest.getX()) / finalDest.getX());
        int directionY = (int) (Math.abs(finalDest.getY()) / finalDest.getY());
        for(int i = 1; i <= numStops; i++){
            Trajectory test = null;
            Trajectory finalTraj = null;
            double x = 0;
            double y = 0;
            double rot = 0;
            double minDur = 1000;
            if(directionX > 0){
                if (i==numStops-1){
                    break;
                } else if(i==numStops){
                    finalTraj = nTrajBuild.splineToConstantHeading(finalDest.vec(), Math.toRadians(-90)).build();
                    break;
                }
                for(double j = rangesX[getRange(prevX+1)][0]; j<=rangesX[getRange(prevX+1)][1]; j+=0.1){
                    if(directionY < 0){
                        if (i==numStops-1){
                            break;
                        }
                        for(double k = rangesY[getRange(prevY+1)][0]; k<=rangesY[getRange(prevY+1)][1]; k+=0.1){
                            Pose2d testPos = new Pose2d(j, k);
                            test = nTrajBuild.splineToConstantHeading(testPos.vec(), 0).build();
                            if(test.duration() < minDur){
                                finalTraj = test;
                                minDur = finalTraj.duration();
                            }
                        }
                    }else{
                        if (i==numStops-1){
                            break;
                        }
                        for(double k = rangesY[getRange(prevY-1)][0]; k<=rangesY[getRange(prevY-1)][1]; k-=0.1){
                            Pose2d testPos = new Pose2d(j, k);
                            test = nTrajBuild.splineToConstantHeading(testPos.vec(), 0).build();
                            if(test.duration() < minDur){
                                finalTraj = test;
                                minDur = finalTraj.duration();
                            }
                        }
                    }
                }

            }else{
                if (i==numStops-1){
                    break;
                } else if(i==numStops){
                    test = nTrajBuild.splineToConstantHeading(finalDest.vec(), Math.toRadians(-90)).build();
                    break;
                }
                for(double j = rangesX[getRange(prevX-1)][0]; j<=rangesX[getRange(prevX-1)][1]; j-=0.1){
                    if(directionY < 0){
                        if (i==numStops-1){
                            break;
                        }
                        for(double k = rangesY[getRange(prevY+1)][0]; k<=rangesY[getRange(prevY+1)][1]; k+=0.1){
                            Pose2d testPos = new Pose2d(j, k);
                            test = nTrajBuild.splineToConstantHeading(testPos.vec(), 0).build();
                            if(test.duration() < minDur){
                                finalTraj = test;
                                minDur = finalTraj.duration();
                            }
                        }
                    }else{
                        if (i==numStops-1){
                            break;
                        }
                        for(double k = rangesY[getRange(prevY-1)][0]; k<=rangesY[getRange(prevY-1)][1]; k-=0.1){
                            Pose2d testPos = new Pose2d(j, k);
                            test = nTrajBuild.splineToConstantHeading(testPos.vec(), 0).build();
                            if(test.duration() < minDur){
                                finalTraj = test;
                                minDur = finalTraj.duration();
                            }
                        }
                    }
                }
            }
            prevX = x;
            prevY = y;
            prevRot = rot;
            Pos.add(finalTraj);
        }

        while (!isStopRequested()) {
            idle();
        }
    }
    public int getRange(double prev){
        if(prev>=-72 && prev<=-48){
            return 0;
        }else if (prev>=-48 && prev<=-24){
            return 1;
        }else if (prev>=-24 && prev<=0){
            return 2;
        }else if (prev>=0 && prev <= 24){
            return 3;
        }else if (prev>=24 && prev <= 48){
            return 4;
        }else if (prev>=48 && prev <= 72){
            return 5;
        }
        telemetry.addLine("Not within range");
        return 6;
    }
}