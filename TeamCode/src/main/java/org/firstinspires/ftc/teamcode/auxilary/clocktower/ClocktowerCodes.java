package org.firstinspires.ftc.teamcode.auxilary.clocktower;

import org.firstinspires.ftc.teamcode.auxilary.problems.ProblemTypeCodes;

import java.lang.reflect.Field;

public enum ClocktowerCodes {
    MAIN_OPMODE_LOOP,
    MOTOR_ENCODER_THREAD,
    IMU_INTEGRATION_THREAD,
    SENSOR_COMBINATION_THREAD,
    INPUT_NODE_UPDATER_THREAD,
    TELEMETRY_STREAM_SENDER_THREAD,
    CV_UPDATE_LOOP,
    MOVEMENT_MOTOR_ACCELERATION_FINDER,
    ASYNC_OPMODE_COMPONENT;

    public static String[] getCodes() {
        ClocktowerCodes[] v = values();
        String[] n = new String[v.length];

        //for each enum, copy its name to the array of Strings
        for(int i = 0 ; i < n.length; i++) n[i] = v[i].name();

        return n;
    }

    public static int size() {
        return values().length;
    }
}
