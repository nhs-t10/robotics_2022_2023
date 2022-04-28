package org.firstinspires.ftc.teamcode.auxilary;

/**
 * A class that lets us play around with the time. This is <i>very</i> fast, and lets us get precision over System.currentTimeMillis, where it's possible.
 */
public class RobotTime {

    /**
     * The same as System.currentTimeMillis, but with greater precision if the device allows it
     * @see System#currentTimeMillis()
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * The same as System.nanoTime, but with greater precision if the device allows it
     * @see System#nanoTime()
     */
    public static long nanoTime() {
        return System.nanoTime();
    }
}
