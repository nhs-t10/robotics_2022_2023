package org.firstinspires.ftc.teamcode.roadrunner.drive;

import android.util.Log;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;


/*
 * Constants shared between multiple org.firstinspires.ftc.teamcode.roadrunner.drive types.
 *
 * TODO: Tune or adjust the following constants to fit your robot. Note that the non-final
 * fields may also be edited through the dashboard (connect to the robot's WiFi network and
 * navigate to https://192.168.49.1:8080/dash). Make sure to save the values here after you
 * adjust them in the dashboard; **config variable changes don't persist between app restarts**.
 *
 * These are not the only parameters; some are located in the localizer classes, org.firstinspires.ftc.teamcode.roadrunner.drive base classes,
 * and op modes themselves.
 */
@Config
public class DriveConstants {

    private static VoltageSensor voltage;
    /*
     * These are motor constants that should be listed online for your motors.
     */


    public static double TICKS_PER_REV = 537.7;
    public static double MAX_RPM = 312;

    /*
     * Set RUN_USING_ENCODER to true to enable built-in hub velocity control using org.firstinspires.ftc.teamcode.roadrunner.drive encoders.
     * Set this flag to false if org.firstinspires.ftc.teamcode.roadrunner.drive encoders are not present and an alternative localization
     * method is in use (e.g., tracking wheels).
     *
     * If using the built-in motor velocity PID, update MOTOR_VELO_PID with the tuned coefficients
     * from DriveVelocityPIDTuner.
     */
    public static final boolean RUN_USING_ENCODER = false;
    public static PIDFCoefficients MOTOR_VELO_PID = new PIDFCoefficients(0, 0, 0,
            18);

    /*
     * These are physical constants that can be determined from your robot (including the track
     * width; it will be tune empirically later although a rough estimate is important). Users are
     * free to chose whichever linear distance unit they would like so long as it is consistently
     * used. The default values were selected with inches in mind. Road runner uses radians for
     * angular distances although most angular parameters are wrapped in Math.toRadians() for
     * convenience. Make sure to exclude any gear ratio included in MOTOR_CONFIG from GEAR_RATIO.
     */
    public static double WHEEL_RADIUS = 1.1; // in
    public static double GEAR_RATIO = 1.53; // output (wheel) speed / input (motor) speed
    public static double TRACK_WIDTH = 12.1; // in

    /*
     * These are the feedforward parameters used to model the org.firstinspires.ftc.teamcode.roadrunner.drive motor behavior. If you are using
     * the built-in velocity PID, *these values are fine as is*. However, if you do not have org.firstinspires.ftc.teamcode.roadrunner.drive
     * motor encoders or have elected not to use them for velocity control, these values should be
     * empirically tuned.
     */
    public static double kV = 0.016;
    public static boolean newBatteryCalcEnabled = true;
    public static double kA = 0.00031;
    public static double kStatic = 0;

    /*public static double kV = 0.018;
    public static double kA = 0.00022;
    public static double kStatic = 0; old values */

    /*
     * These values are used to generate the trajectories for you robot. To ensure proper operation,
     * the constraints should never exceed ~80% of the robot's actual capabilities. While Road
     * Runner is designed to enable faster autonomous motion, it is a good idea for testing to start
     * small and gradually increase them later after everything is working. All distance units are
     * inches.
     */
    public static double MAX_VEL = 45;
    public static double MAX_ACCEL = 40;
    public static double MAX_ANG_VEL = 3.4;
    public static double MAX_ANG_ACCEL = 3.4;
    //Calculations for kV at the beginning of the match
    public static void updateBattery(HardwareMap hardwareMap){
        if(newBatteryCalcEnabled){
            voltage = hardwareMap.voltageSensor.iterator().next();
            Log.d(null, "DriveConstants - Voltage Reported: "+voltage.getVoltage());
            kV = (-1.14*Math.pow(10.0, -3.0)*voltage.getVoltage())+0.0335;
            newBatteryCalcEnabled = false;
        }
    }
    public static double encoderTicksToInches(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    public static double rpmToVelocity(double rpm) {
        return rpm * GEAR_RATIO * 2 * Math.PI * WHEEL_RADIUS / 60.0;
    }

    public static double getMotorVelocityF(double ticksPerSecond) {
        // see https://docs.google.com/document/d/1tyWrXDfMidwYyP_5H4mZyVgaEswhOC35gvdmP-V-5hA/edit#heading=h.61g9ixenznbx

        return 32767 / ticksPerSecond;
    }
}
