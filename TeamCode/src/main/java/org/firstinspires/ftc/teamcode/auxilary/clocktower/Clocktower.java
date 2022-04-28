package org.firstinspires.ftc.teamcode.auxilary.clocktower;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;

/**
 * A centralized place to report loop times. Reporting a loop in Clocktower means that it automatically gets Telemetry'd.
 */
public class Clocktower {
    public static long[] starts = new long[ClocktowerCodes.size()];
    public static long[] durations = new long[starts.length];

    public static void time(int code) {
        if(code < 0) throw new IllegalArgumentException("A Clocktower loop code MUST be 0 or greater. Try using the ClocktowerStandardTimingCodes class.");
        if(code >= starts.length) throw new IllegalArgumentException("A Clocktower loop code MUST be within the range of the `starts` array. Try using the ClocktowerStandardTimingCodes class; adding a `public static final int` field will automatically re-size the array.");

        long now = System.nanoTime();
        if(starts[code] != 0) durations[code] = now - starts[code];
        starts[code] = now;
    }

    public static long getDuration(int code) {
        return starts[code];
    }

    private static String[] namesCache;
    public static String getTimeJSON() {
        if(namesCache == null) namesCache = ClocktowerCodes.getCodes();

        return "{" +
                "\"names\":" + PaulMath.JSONify(namesCache) + "," +
                "\"stati\":" + PaulMath.JSONify(durations) +
                "}";

    }
    //Return a valid JSON object, with the starting `{` and ending `}` missing. Each key is a "scoped" form of the code.
    //Example: `"clocktower.MAIN_OPMODE_LOOP": 249, "clocktower.MOTOR_ENCODER_THREAD": 1549`
    public static String getTimeJSONFlatFragment() {
        if(namesCache == null) namesCache = ClocktowerCodes.getCodes();

        StringBuilder s = new StringBuilder();
        for(int i = 0; i < namesCache.length; i++) {
            s.append("\"clocktower.").append(namesCache[i]).append("ns\":").append(durations[i]);
            //only add a "," if we're NOT at the last line.
            if(i + 1 < namesCache.length) s.append(",");
        }
        return s.toString();
    }

    public static void time(ClocktowerCodes code) {
        time(code.ordinal());
    }
}
