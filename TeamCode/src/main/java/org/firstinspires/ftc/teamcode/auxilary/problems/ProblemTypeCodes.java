package org.firstinspires.ftc.teamcode.auxilary.problems;

import java.lang.reflect.Field;

public class ProblemTypeCodes {
    public final static long CLOCKTOWER_DELAY_TOO_MUCH       = 0;
    public final static long INPUT_MANAGER_UNABLE_TO_RESOLVE = 1;
    public final static long SENSOR_INTEGRATION_UNUSABLE     = 2;
    public final static long LIFT_NOT_INIT_IN_CORRECT_PLACE  = 3;
    public final static long CONCURRENCY_PROBLEM_OF_COURSE   = 4;
    public final static long QUANTUM_BOOLEANS                = 5;


    //statically get the codes with reflection
    public static String[] getCodes() {
        Field[] fields = ProblemTypeCodes.class.getDeclaredFields();
        String[] names = new String[fields.length];

        for(int i = 0; i < fields.length; i++) {
            names[i] = fields[i].getName();
        }

        return names;
    }
}
