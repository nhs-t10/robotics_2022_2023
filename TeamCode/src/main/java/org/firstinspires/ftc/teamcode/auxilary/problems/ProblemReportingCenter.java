package org.firstinspires.ftc.teamcode.auxilary.problems;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;

/**
 * This class is, more or less, a bit array implementation. Each bit gets put in a specific place in some `long`s so that it can be sure to maintain memory effectiveness.
 * ProblemReporting NEEDS to be fast so that it can keep up with {@link org.firstinspires.ftc.teamcode.auxilary.clocktower.Clocktower Clocktower}, so it uses a lot of bitwise math.
 */
public class ProblemReportingCenter {
    //Increase the length of this array so that there's at least 1 `long` for every 32 `ProblemTypeCode`s.
    private static long[] currentStatus = new long[1];

    public static void reportProblem(int code) {

        //`code >>> 5` is the same as `code / 32`, but a lot faster because it's bitwise and doesn't have to perform all the checks (e.g. divide by 0, divide by a string) that division does.
        int bucketId = code >>> 5;

        //take the last 5 bits as the bit ID. This is the same as `code % 32`, but a lot faster.
        long bitIdInBucket = code & 0b11111;

        //use bitwise OR to make sure the selected bit is flipped
        currentStatus[bucketId] |= 1 << bitIdInBucket;
    }
    public static void clearProblem(int code) {
        //`code >>> 5` is the same as `code / 32`, but a lot faster because it's bitwise and doesn't have to perform all the checks (e.g. divide by 0, divide by a string) that division does.
        int bucketId = code >>> 5;

        //take the last 5 bits as the bit ID. This is the same as `code % 32`, but a lot faster.
        long bitIdInBucket = code & 0b11111;

        //use bitwise AND to zero the selected bit by inverting the value. This is like what we did in `reportProblem`, but since it's inverted, it'll zero the bit instead of turning it on.
        currentStatus[bucketId] &= ~(1 << bitIdInBucket);
    }

    private static String[] namesCache;
    public static String getProblemsJSON() {
        if(namesCache == null) namesCache = ProblemTypeCodes.getCodes();

        return "{" +
                "\"names\":" + PaulMath.JSONify(namesCache) + "," +
                "\"stati\":" + PaulMath.JSONify(currentStatus) +
                "}";

    }
}
