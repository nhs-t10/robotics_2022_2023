package org.firstinspires.ftc.teamcode.roadrunner.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.acmerobotics.roadrunner.trajectory.config.TrajectoryConfig;
import com.acmerobotics.roadrunner.trajectory.config.TrajectoryConfigManager;
import com.acmerobotics.roadrunner.trajectory.config.TrajectoryGroupConfig;
import com.acmerobotics.roadrunner.util.Angle;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequenceBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Set of utilities for loading trajectories from assets (the plugin save location).
 */
public class AssetsTrajectoryManager {

    /**
     * Loads the group config.
     */
    public static @Nullable
    TrajectoryGroupConfig loadGroupConfig() {
        try {
            InputStream inputStream = AppUtil.getDefContext().getAssets().open(
                    "trajectory/"+TrajectoryConfigManager.GROUP_FILENAME);
            System.out.println(TrajectoryConfigManager.GROUP_FILENAME);
            return TrajectoryConfigManager.loadGroupConfig(inputStream);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Loads a trajectory config with the given name.
     */
    public static @Nullable TrajectoryConfig loadConfig(String name) {
        try {
            InputStream inputStream = AppUtil.getDefContext().getAssets().open(
                    "trajectory/" + name + ".yaml");
            return TrajectoryConfigManager.loadConfig(inputStream);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Loads a trajectory builder with the given name.
     */
    public static @Nullable TrajectoryBuilder loadBuilder(String name) {
        TrajectoryGroupConfig groupConfig = loadGroupConfig();
        TrajectoryConfig config = loadConfig(name);

        if (groupConfig == null || config == null) {
            return null;
        }
        return config.toTrajectoryBuilder(groupConfig);
    }
    public static @Nullable TrajectorySequenceBuilder loadBuilderSequence(String name) {
        TrajectoryGroupConfig groupConfig = loadGroupConfig();
        TrajectoryConfig config = loadConfig(name);

        if (groupConfig == null || config == null) {
            return null;
        }

        return toSeq(config, groupConfig);
    }

    /**
     * Loads a trajectory with the given name.
     */
    public static @Nullable Trajectory load(String name) {
        TrajectoryBuilder builder = loadBuilder(name);
        if (builder == null) {
            return null;
        }
        return builder.build();
    }
    //Untested
    public static @Nullable TrajectorySequence loadSequence(String name) {
        TrajectorySequenceBuilder builder = loadBuilderSequence(name);
        if (builder == null) {
            return null;
        }
        return builder.build();
    }
    @NonNull
    public static TrajectorySequenceBuilder toSeq(TrajectoryConfig config, TrajectoryGroupConfig groupConfig){
        Pose2d startPose = config.getStartPose();
        double startTangent = config.getStartTangent();


        TrajectorySequenceBuilder builder = new TrajectorySequenceBuilder(
                config.getStartPose(),
                config.getStartTangent(),
                groupConfig.getVelConstraint(),
                groupConfig.getAccelConstraint(),
                groupConfig.getMaxAngVel(),
                groupConfig.getMaxAngAccel()
        );
        List<TrajectoryConfig.Waypoint> waypoints = config.getWaypoints();
        for (int i = 0; i<waypoints.size(); i++) {
            TrajectoryConfig.Waypoint w = waypoints.get(i);
            Vector2d position = w.getPosition();
             double heading = w.getHeading();
             double tangent = w.getTangent();
            TrajectoryConfig.HeadingInterpolationType interpolationType = w.getInterpolationType();

            double prevTangent;
            if (i == 0) {
                prevTangent = startTangent;
            } else {
                prevTangent = waypoints.get(i-1).getTangent();
            }
            Vector2d prevPosition;
            if (i == 0) {
                prevPosition = startPose.vec();
            } else {
                prevPosition = waypoints.get(i-1).getPosition();
            }
            double interWaypointAngle = (position.minus(prevPosition).angle());
            boolean line = ((Angle.normDelta(prevTangent - tangent)-0.0)<1e-6) &&
                    ((Angle.normDelta(tangent - interWaypointAngle)-0.0)<1e-6);

            switch (interpolationType) {
                case TANGENT:
                    if (line) {
                        builder.lineTo(position);
                    } else {
                        builder.splineTo(position, tangent);
                    }
                case CONSTANT:
                    if (line) {
                        builder.lineToConstantHeading(position);
                    } else {
                        builder.splineToConstantHeading(position, tangent);
                    }
                case LINEAR:
                    if (line) {
                        builder.lineToLinearHeading(new Pose2d(position, heading));
                    } else {
                        builder.splineToLinearHeading(new Pose2d(position, heading), tangent);
                    }
                case SPLINE:
                    if (line) {
                        builder.lineToSplineHeading(new Pose2d(position, heading));
                    } else {
                        builder.splineToSplineHeading(new Pose2d(position, heading), tangent);
                    }
            }
        }

        return builder;
    }
}
