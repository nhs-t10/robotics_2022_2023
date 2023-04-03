package org.firstinspires.ftc.teamcode.opmodes.auto.calibration;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.Misc;
import org.firstinspires.ftc.teamcode.managers.roadrunner.RoadRunnerManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequenceBuilder;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Op mode for computing RoadRunner paths from Trajectory Sequences...
 *
 */
@Config
@Autonomous(group = "calibration")
public class TrajectoryExtrapolator extends LinearOpMode {
    public static double len = 17.0;
    public static double wid = 15.5;
    public static double halfWid = wid/2.0;
    public static double offsetWid = halfWid +0.5;
    public static double halfLen = len/2.0;
    public static double offsetLen = halfLen +0.5;
    public static Pose2d finalDest = new Pose2d(67,120); // in
    private static final double[][] rangesX = {{-72+ halfWid, -48-offsetWid}, {-48+offsetWid, -24-offsetWid}, {-24+offsetWid, 0-offsetWid},{0+offsetWid, 24-offsetWid}, {24+offsetWid, 48-offsetWid}, {48+offsetWid, 72- halfWid}};
    private static final double[][] rangesY = {{-72+offsetLen, -48-offsetLen}, {-48+offsetLen, -24-offsetLen}, {-24+offsetLen, 0-offsetLen},{0+offsetLen, 24-offsetLen}, {24+offsetLen, 48-offsetLen}, {48+offsetLen, 72- halfLen}};
    public static TrajectorySequence createdTrajSeq;
    @Override
    public void runOpMode() throws InterruptedException {


        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        RoadRunnerManager drive  = new RoadRunnerManager(hardwareMap, new Pose2d(0, 0), (TelemetryManager) telemetry, this, true);
        TrajectoryBuilder nTrajBuild = drive.getDrive().trajectoryBuilder(new Pose2d());


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
        double prevTan = 0;
        boolean turn = false;
        int numStops = (int) (finalDest.getX() / 24) + 1;
        if(numStops < (int) (finalDest.getY() / 24) + 1){
            numStops = (int) (finalDest.getY() / 24) + 1;
        }
        int numStopsX = (int) (finalDest.getX() / 24) + 1;
        int numStopsY = (int) (finalDest.getY() / 24) + 1;
        int directionX = (int) (Math.abs(finalDest.getX()) / finalDest.getX());
        int directionY = (int) (Math.abs(finalDest.getY()) / finalDest.getY());
        if (directionX > 0) {
            drive.getDrive().setPoseEstimate(new Pose2d(drive.getDrive().getPoseEstimate().vec(), -90));
        } else {
            drive.getDrive().setPoseEstimate(new Pose2d(drive.getDrive().getPoseEstimate().vec(), 90));
        }
        for(int i = 1; i <= numStops; i++){
            Trajectory test = null;
            Trajectory finalTraj = null;
            double x = prevX;
            double y = prevY;
            double tan = prevTan;
            double minDur = 1000;
            int xMove = 1;
            int yMove = 1;
            if(directionX > 0){
                if (i==numStopsX-1){
                    tan = -90;
                    xMove = 0;
                    break;
                } else if(i==numStops){
                    finalTraj = nTrajBuild.splineToConstantHeading(finalDest.vec(), Math.toRadians(-90)).build();
                    break;
                } else {
                    tan = -90;
                }
                for(double j = rangesX[getRange(prevX)+xMove][0]; j<=rangesX[getRange(prevX)+xMove][1]; j+=0.1){
                    if(directionY < 0){
                        if (i==numStopsY-1){
                            yMove = 0;
                            break;
                        } else {
                            tan = 180;
                        }
                        for(double k = rangesY[getRange(prevY)+yMove][0]; k<=rangesY[getRange(prevY)+yMove][1]; k+=0.1){
                            Pose2d testPos = new Pose2d(j, k);
                            test = nTrajBuild.splineToConstantHeading(testPos.vec(), tan).build();
                            if(test.duration() < minDur){
                                finalTraj = test;
                                minDur = finalTraj.duration();
                            }
                        }
                    }else{
                        if (i==numStopsY-1){
                            yMove=0;
                            break;
                        }else{
                            tan = 0;
                        }
                        for(double k = rangesY[getRange(prevY)-yMove][0]; k<=rangesY[getRange(prevY)-yMove][1]; k-=0.1){
                            Pose2d testPos = new Pose2d(j, k);
                            test = nTrajBuild.splineToConstantHeading(testPos.vec(), tan).build();
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
                    finalTraj = nTrajBuild.splineToConstantHeading(finalDest.vec(), Math.toRadians(90)).build();
                    break;
                } else {
                    tan = 90;
                }
                for(double j = rangesX[getRange(prevX)-xMove][0]; j<=rangesX[getRange(prevX)-xMove][1]; j-=0.1){
                    if(directionY < 0){
                        if (i==numStops-1){
                            break;
                        }
                        for(double k = rangesY[getRange(prevY)+yMove][0]; k<=rangesY[getRange(prevY)+yMove][1]; k+=0.1){
                            Pose2d testPos = new Pose2d(j, k);
                            test = nTrajBuild.splineToConstantHeading(testPos.vec(), tan).build();
                            if(test.duration() < minDur){
                                finalTraj = test;
                                minDur = finalTraj.duration();
                            }
                        }
                    }else{
                        if (i==numStops-1){
                            break;
                        }
                        for(double k = rangesY[getRange(prevY)-yMove][0]; k<=rangesY[getRange(prevY)-yMove][1]; k-=0.1){
                            Pose2d testPos = new Pose2d(j, k);
                            test = nTrajBuild.splineToConstantHeading(testPos.vec(), tan).build();
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
            prevTan = tan;
            Pos.add(finalTraj);
        }
        int number = 1;
        double dur = 0.0;
        PrintWriter pw = null;
        for(Trajectory t : Pos){
            telemetry.addData(String.format("#"+"%.5 Trajectory", number), t.end());
            try {
                pw = new PrintWriter("lastTrajectoryOverview.txt", "UTF-8");
                pw.println(String.format("#"+"%.5 Trajectory", number)+": "+ t.end());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            trajBuild.addTrajectory(t);
            number++;
            dur += t.duration();
        }
        assert pw != null;
        pw.println("ALL HEADING INTERPOLATORS: CONSTANT");
        pw.close();
        telemetry.addData("Total Duration: ", dur);
        createdTrajSeq = trajBuild.build();
        drive.getDrive().followTrajectorySequence(createdTrajSeq);
        stop();
    }
    private int getRange(double prev){
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