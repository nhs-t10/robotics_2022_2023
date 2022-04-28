package org.firstinspires.ftc.teamcode.managers.movement;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.RobotConfiguration;

public class MovementManager extends FeatureManager {


    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;

    private float scale = 0.6f;

    /**
     * Create a MovementManager with a hardware map.
     * @param hardwareMap hardware map that includes fl, fr, br, and bl
     */
    public MovementManager(HardwareMap hardwareMap) {
        this(hardwareMap.get(DcMotor.class, "fl"),
                hardwareMap.get(DcMotor.class, "fr"),
                hardwareMap.get(DcMotor.class, "br"),
                hardwareMap.get(DcMotor.class, "bl"));
    }

    /**
     * Create a MovementManager with four motors.
     * @param fl Front Left motor
     * @param fr Front Right motor
     * @param br Back Right motor
     * @param bl Back Left motor
     */
    public MovementManager(DcMotor fl, DcMotor fr, DcMotor br, DcMotor bl) {
        this.frontLeft = fl;
        this.frontRight = fr;
        this.backRight = br;
        this.backLeft = bl;

        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    /**
     * Just drive the motors, without <u>any</u> calculations.
     *
     * <h2 class="AutoautoDontUseProd">Never use this unless you are testing motors. This is for <u>testing only</u>. Don't use this in final OpModes.<br>
     *      If you need to individually control each motor, try {@linkplain #driveBlue(float, float, float, float) driveBlue} instead so that it can account for robot differences.</h2>
     *
     *
     * @param fl The raw power for the front left motor. From {@code -1} to {@code 1}, inclusive.
     * @param fr The raw power for the front right motor. From {@code -1} to {@code 1}, inclusive.
     * @param br The raw power for the back right motor. From {@code -1} to {@code 1}, inclusive.
     * @param bl The raw power for the back left motor. From {@code -1} to {@code 1}, inclusive.
     */
    public void driveRaw(float fl, float fr, float br, float bl) {
        frontLeft.setPower(fl);
        frontRight.setPower(fr);
        backRight.setPower(br);
        backLeft.setPower(bl);

    }

    /**
     * <p>
     * Drive each motor individually. This method accounts for per-robot differences (e.g. motors being backwards), which are
     * defined in {@link FeatureManager#defaultConfiguration FeatureManager}.</p>
     *<p>
     * Each power is considered from the perspective of the <u>robot</u>, not the motor. For example, {@code 1} means "towards the front of the robot", not just "motor forwards".
     *</p>
     * @param fl The power for the front left motor. From {@code -1} to {@code 1}, inclusive.
     * @param fr The power for the front right motor. From {@code -1} to {@code 1}, inclusive.
     * @param br The power for the back right motor. From {@code -1} to {@code 1}, inclusive.
     * @param bl The power for the back left motor. From {@code -1} to {@code 1}, inclusive.
     */
    public void driveBlue(float fl, float fr, float br, float bl) {
        RobotConfiguration configuration = FeatureManager.getRobotConfiguration();
        driveRaw(fl * configuration.motorCoefficients.fl,
                fr * configuration.motorCoefficients.fr,
                br * configuration.motorCoefficients.br,
                bl * configuration.motorCoefficients.bl);
    }

    /**
     * Stop all drive motors directly. <br>
     * Equivalent to {@code driveOmni(0,0,0)}.
     * <br><br>
     * If you are changing this method's signature:
     * <ul>
     *     <li>Change {@linkplain org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.State#init() its usage in Autoauto}, if necessary</li>
     * </ul>
     */
    public void stopDrive() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);
    }

    /**
     * <p>Drive with vertical, horizontal, and rotational powers.
     * By combining different powers, you can get any two-dimensional transformation.
     * Any combination of directions may be used, but <u>straight lines are the most stable</u> for auto.</p>
     * <h2>Usage Examples</h2>
     * <ul>
     * <li>To drive <u>forwards</u>, use {@code driveOmni(1, 0, 0)}</li>
     * <li>To drive <u>backwards</u>, use {@code driveOmni(-1, 0, 0)}</li>
     * <li>To drive <u>right</u>, use {@code driveOmni(0, 1, 0)}</li>
     * <li>To drive <u>left</u>, use {@code driveOmni(0, -1, 0)}</li>
     * <li>To turn <u>right</u>, use {@code driveOmni(0, 0, 1)}</li>
     * <li>To turn <u>left</u>, use {@code driveOmni(0, 0, -1)}</li>
     * </ul>
     *
     * @param v Vertical power, with -1 being backwards movement and 1 being forwards movement. Must be between {@code -1} and {@code 1} inclusive.
     * @param h Horizontal power, with -1 being leftwards movement and 1 being rightwards movement. Must be between {@code -1} and {@code 1} inclusive.
     * @param r Rotation power, with -1 being "turn left" and 1 being "turn right". Must be between {@code -1} and {@code 1} inclusive.
     */
    public void driveOmni(float v, float h, float r) {
        driveOmni(new float[] {v, h, r});
    }

    /**
     * <p>Drive with vertical, horizontal, and rotational powers.
     * By combining different powers, you can get any two-dimensional transformation.
     * Any combination of directions may be used, but <u>straight lines are the most stable</u> for auto.</p>
     * When {@code driveOmni} is being used in auto, {@linkplain #driveOmni(float, float, float) the seperate-argument version} is recommended.
     *
     * @see #driveOmni(float, float, float)
     * @param powers An array of powers (vertical, horizontal, rotational-- in that order). Each must be between {@code -1} and {@code 1} inclusive.
     */
    public void driveOmni(float[] powers) {
        float[] sum = PaulMath.omniCalc(powers[0]*scale, powers[1]*scale, powers[2] * scale);
        driveBlue(sum[0], sum[1], sum[2], sum[3]);
    }

    public double getFLMotorPower(){
        return this.frontLeft.getPower();
    }
    public double getBLMotorPower(){
        return this.backLeft.getPower();
    }
    public double getFRMotorPower(){
        return this.frontRight.getPower();
    }
    public double getBRMotorPower(){
        return this.backRight.getPower();
    }


    /**
     * Get the internal array of DCMotors.
     * @return All 4 DcMotors managed by this manager, in the following order: frontLeft, frontRight, backRight, backLeft
     */
    public DcMotor[] getMotor(){
        DcMotor[] motors = {frontLeft, frontRight, backRight, backLeft};

        return motors;
    }


    /**
     * Reset all 4 motors' encoders. The method accomplishes this by changing every RunMode to {@link DcMotor.RunMode#STOP_AND_RESET_ENCODER}.
     */
    public void resetEncoders() {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Change every motor's RunMode to {@link DcMotor.RunMode#RUN_TO_POSITION}.
     * <u>IMPORTANT: Make sure to set target positions with {@link #setTargetPositions(int, int, int, int) setTargetPositions()} before calling this! If you don't, there WILL be an error.</u>
     */
    public void runToPosition() {
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    /**
     * Turn on all 4 motors' encoders. The method accomplishes this by changing every RunMode to {@link DcMotor.RunMode#RUN_USING_ENCODER}.
     * Over 4 years (2017-2021), we've never been able to figure out what {@code RUN_USING_ENCODER} actually does. If you figure it out, please write it here!
     */
    public void runUsingEncoders() {
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Turn off all 4 motors' encoders. The method accomplishes this by changing every RunMode to {@link DcMotor.RunMode#RUN_WITHOUT_ENCODER}.
     * This is the default RunMode.
     */
    public void runWithOutEncoders() {
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


    /**
     * Set all 4 motors' target positions at once. This method is for use with the {@link DcMotor.RunMode#RUN_TO_POSITION} RunMode.
     *
     * <p>
     *     Please note that this method uses raw ticks for the units. Ticks vary based on the motor, the configuration, and how the robot is started, so it's <u>very</u> important to test this.
     * </p>
     *
     * @see #runToPosition()
     * @param fl The target position, in ticks, for the front-left motor.
     * @param fr The target position, in ticks, for the front-right motor.
     * @param br The target position, in ticks, for the back-right motor.
     * @param bl The target position, in ticks, for the back-left motor.
     */
    public void setTargetPositions(int fl, int fr, int br, int bl) {
        frontLeft.setTargetPosition(fl);
        frontRight.setTargetPosition(fr);
        backRight.setTargetPosition(br);
        backLeft.setTargetPosition(bl);
    }

    /**
     * Get the internal scale factor that {@link #driveOmni(float[]) driveOmni} uses. By default, this is {@code 0.6}.
     * <u>IMPORTANT NOTE: No other methods use the scale.</u>
     * @see #upScale(float)
     * @see #downScale(float)
     * @return the internal scale factor; usually from 0-1, but there's no rule for that.
     */
    public float getScale(){
        return scale;
    }
    /**
     * Increase the internal scale factor that {@link #driveOmni(float[]) driveOmni} uses. By default, it is {@code 0.6}.
     * <u>IMPORTANT NOTE: No other methods use the scale.</u>
     * @see #getScale()
     * @see #downScale(float)
     *
     * @param ScaleFactor An amount to <u>increase</u> the scale factor by. <u>For historical reasons, this may be negative.</u>
     */
    public void upScale(float ScaleFactor){scale+=ScaleFactor;}
    /**
     * Decrease the internal scale factor that {@link #driveOmni(float[]) driveOmni} uses. By default, it is {@code 0.6}.
     * <u>IMPORTANT NOTE: No other methods use the scale.</u>
     * @see #getScale()
     * @see #upScale(float)
     *
     * @param ScaleFactor An amount to <u>decrease</u> the scale factor by. <u>For historical reasons, this may be negative.</u>
     */
    public void downScale(float ScaleFactor){scale-=ScaleFactor;}

    public void setScale(float ScaleValue){scale = ScaleValue;}
    /**
     * Get the positions of each motor, as reported by encoders.
     * @return An array of positions, in the following order: frontRight, frontLeft, backRight, backLeft.
     */
    public float[] getMotorPositions() {
        return new float[] {
          frontRight.getCurrentPosition(),
          frontLeft.getCurrentPosition(),
          backRight.getCurrentPosition(),
          backLeft.getCurrentPosition()
        };
    }

    /**
     * Get a general measurement of the robot's total movement.
     * Note that this method uses raw ticks as its unit, so {@linkplain PaulMath#encoderDistanceCm(double) some additional processing} should probably be done to get a useful measurement.
     *
     * @return a general measurement of total movement
     */
    public int getTicks() {
        return getGeneralMeasurementMotor().getCurrentPosition();
    }

    public DcMotor getGeneralMeasurementMotor() {
        return backLeft;
    }

    /**
     * Get the robot's horizontal movement since it started. This is more precise than {@link #getTicks()}.
     * Still, though, its unit is raw ticks, so {@linkplain PaulMath#encoderDistanceCm(double) some additional processing} should be done to get a useful measurement.
     *
     * <h3>WARNING: This method depends on hardware doing something right. If they didn't hook up an odometry pod, don't use this.</h3>
     *
     * @return the robot's horizontal movement, in ticks.
     */
    public int getHorizontalTicks() { return frontRight.getCurrentPosition(); }

    /**
     * Get the robot's vertical movement since it started. This is more precise than {@link #getTicks()}.
     * Still, though, its unit is raw ticks, so {@linkplain PaulMath#encoderDistanceCm(double) some additional processing} should be done to get a useful measurement.
     *
     * <h3>WARNING: This method depends on hardware doing something right. If they didn't hook up an odometry pod, don't use this.</h3>
     *
     * @return the robot's vertical movement, in ticks.
     */
    public int getVerticalTicks() { return  backLeft.getCurrentPosition(); }

    /**
     * Get a general measurement of the robot's total movement, in meters, since it started. This will be correct for local straight-line movement, but may be off when the robot's moves are complex.
     *
     * @return a measurement of total movement, in meters
     */
    public float getMeters(){
        return getCentimeters() / 100f;
    }
    /**
     * Get a general measurement of the robot's total movement, in centimeters, since it started. This will be correct for local straight-line movement, but may be off when the robot's moves are complex.
     *
     * @return a measurement of total movement, in centimeters
     */
    public float getCentimeters(){
        return PaulMath.encoderDistanceCm(getTicks());
    }

    /**
     * Get the robot's horizontal movement since it started. This is more precise than {@link #getMeters()}, but should only be used when the robot is moving directly left or right.
     *
     * <h3>WARNING: This method depends on hardware doing something right. If they didn't hook up an odometry pod, don't use this.</h3>
     *
     *
     * @return A measurement of horizontal movement, in meters.
     */
    public float getHorizontalMeters(){
        return (PaulMath.encoderDistanceCm(getHorizontalTicks()) / 100f);
    }
    /**
     * Get the robot's vertical movement since it started. This is more precise than {@link #getMeters()}, but should only be used when the robot is moving directly forwards or backwards.
     *
     * <h3>WARNING: This method depends on hardware doing something right. If they didn't hook up an odometry pod, don't use this.</h3>
     *
     * @return A measurement of vertical movement, in meters.
     */
    public float getVerticalMeters(){
        return (PaulMath.encoderDistanceCm(getVerticalTicks()) / 100f);
    }

}