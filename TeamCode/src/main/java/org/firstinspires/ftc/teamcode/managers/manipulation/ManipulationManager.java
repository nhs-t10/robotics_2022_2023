package org.firstinspires.ftc.teamcode.managers.manipulation;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.auxilary.BasicMapEntry;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

import java.util.Arrays;
import java.util.Map;

public class ManipulationManager extends FeatureManager {
    public static final int ENCODER_TICK_VALUE_TOLERANCE = 25;
    public static final float DEFAULT_ENCODED_MOVEMENT_POWER = 0.3f;

    public CRServo[] crservos;
    public DcMotor[] motors;
    public Servo[] servos;

    public String[] crservoNames;
    public String[] motorNames;
    public String[] servoNames;

    public PIDlessEncoderMovementThread movementThread;

    /**
     * Make a ManipulationManager by only specifying the names. The constructor uses the given HardwareMap to resolve each name by itself.
     * <h3>Usage Example:</h3>
     * <pre><code>
     *     new ManipulationManager(hardwareMap,
     *          ManipulationManager.crservo("CRServoNameOne", "CRServoNameTwo"),
     *          ManipulationManager.servo("ServoNameOne", "ServoNameTwo"),
     *          ManipulationManager.motor("MotorNameOne", "MotorNameTwo")
     *     );
     * </code></pre>
     *
     * <p>The constructor works the same way as {@link #ManipulationManager(HardwareMap, String[], String[], String[]) the other constructor}, but allows you to specify args
     * in any order with the {@link #crservo(String...) crservo}, {@link #servo(String...) servo}, and {@link #motor(String...) motor} script-helpers.</p>
     * <p>Consider using <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/language/static-import.html">static imports</a> on the helper methods for better readability.</p>
     *
     * @see #crservo(String...) 
     * @see #servo(String...) 
     * @see #motor(String...)
     * 
     * @param _hardwareMap a hardware map that contains <u>all</u> of the listed hardware devices.
     * @param args A series of at least 3 map entries.
     */
    public ManipulationManager(HardwareMap _hardwareMap, Map.Entry<String, String[]>... args) {
        String[] _crservos = new String[0], _servos = new String[0], _motors = new String[0];
        for(Map.Entry<String, String[]> a : args) {
            if(a.getKey().equals("crservo")) _crservos = a.getValue();
            else if(a.getKey().equals("servo")) _servos = a.getValue();
            else if(a.getKey().equals("motor")) _motors = a.getValue();
        }

        this.crservos = new CRServo[_crservos.length];
        for(int i = 0; i < _crservos.length; i++) {crservos[i]  = _hardwareMap.tryGet(CRServo.class, _crservos[i]); }
        this.crservoNames = _crservos;
        this.servos = new Servo[_servos.length];
        for(int i = 0; i < _servos.length; i++) {servos[i]  = _hardwareMap.tryGet(Servo.class, _servos[i]); }
        this.servoNames = _servos;
        this.motors = new DcMotor[_motors.length];
        for(int i = 0; i < _motors.length; i++) {motors[i]  = _hardwareMap.tryGet(DcMotor.class, _motors[i]); }
        this.motorNames = _motors;

        resetEncoders();
        runWithoutEncoders();

        this.movementThread = new PIDlessEncoderMovementThread(motors, motorNames);
        this.movementThread.start();
    }
    public static BasicMapEntry<String, String[]> crservo(String... names) {
        return new BasicMapEntry<String, String[]>("crservo", names);
    }
    public static BasicMapEntry<String, String[]> motor(String... names) {
        return new BasicMapEntry<String, String[]>("motor", names);
    }
    public static BasicMapEntry<String, String[]> servo(String... names) {
        return new BasicMapEntry<String, String[]>("servo", names);
    }

    /**
     * Make a ManipulationManager by only specifying the names. The constructor uses the given HardwareMap to resolve each name by itself.
     * <br> This is a less verbose alternative to {@link #ManipulationManager(CRServo[], String[], Servo[], String[], DcMotor[], String[]) the six-argument constructor}.
     *
     * @see #ManipulationManager(HardwareMap, Map.Entry[])
     * 
     * @param _hardwareMap a hardware map that contains <u>all</u> of the listed hardware devices.
     * @param _crservos an array of each CR Servo's name
     * @param _servos an array of each Servo's name
     * @param _motors an array of each DC Motor's name
     */
    public ManipulationManager(HardwareMap _hardwareMap, String[] _crservos, String[] _servos, String[] _motors) {
        this.crservos = new CRServo[_crservos.length];
        for(int i = 0; i < _crservos.length; i++) {crservos[i]  = _hardwareMap.tryGet(CRServo.class, _crservos[i]); }
        this.crservoNames = _crservos;
        this.servos = new Servo[_servos.length];
        for(int i = 0; i < _servos.length; i++) {servos[i]  = _hardwareMap.tryGet(Servo.class, _servos[i]); }
        this.servoNames = _servos;
        this.motors = new DcMotor[_motors.length];
        for(int i = 0; i < _motors.length; i++) {motors[i]  = _hardwareMap.tryGet(DcMotor.class, _motors[i]); }
        this.motorNames = _motors;

        resetEncoders();
        runWithoutEncoders();

        this.movementThread = new PIDlessEncoderMovementThread(motors, motorNames);
        this.movementThread.start();
    }

    /**
     * An older form of the ManipulationManager constructor that doesn't need a HardwareMap, but uses wayyyyy too many arguments.
     * Use only when there is no HardWareMap.
     *
     * Consider using {@link #ManipulationManager(HardwareMap, String[], String[], String[])} instead.
     */
    public ManipulationManager(CRServo[] _crservos, String[] _crservoNames, Servo[] _servos, String[] _servoNames, DcMotor[] _motors, String[] _motorNames) {
        if(_crservoNames.length != _crservos.length) throw new IllegalArgumentException("CRServo Names must be the same length as CRServos");
        if(_servoNames.length != _servos.length) throw new IllegalArgumentException("Servo Names must be the same length as Servos");
        if(_motorNames.length != _motors.length) throw new IllegalArgumentException("Motor Names must be the same length as Motors");

        this.crservos = _crservos;
        this.crservoNames = _crservoNames;
        this.servos = _servos;
        this.servoNames = _servoNames;
        this.motors = _motors;
        this.motorNames = _motorNames;

        resetEncoders();
        runWithoutEncoders();

        this.movementThread = new PIDlessEncoderMovementThread(motors, motorNames);
        this.movementThread.start();
    }

    /**
     * Move the given servo to the given position.
     * @param name the name of the servo; must be one of the names given in the {@link #ManipulationManager(HardwareMap, String[], String[], String[]) constructor}
     * @param position the position to go to, between {@code 0} and {@code 1}, inclusive.
     */
    public void setServoPosition(String name, double position) {
        int index = (Arrays.asList(servoNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Servo " + name + " does not exist or is not registered in ManipulationManager");
        servos[index].setPosition(position);
    }

    /**
     * Set the given CR Servo's power.
     * @param name the name of the CRServo; must be one of the names given in the {@link #ManipulationManager(HardwareMap, String[], String[], String[]) constructor}
     * @param power the power to give it, between {@code 0} and {@code 1}, inclusive.
     */
    public void setServoPower(String name, double power) {
        int index = (Arrays.asList(crservoNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("CRServo " + name + " does not exist or is not registered in ManipulationManager");
        crservos[index].setPower(power);
    }

    /**
     * Get a Servo object by name.
     * @param name the name of the servo; must be one of the names given in the {@link #ManipulationManager(HardwareMap, String[], String[], String[]) constructor}
     * @return The Servo object, exactly as it came out of the {@link HardwareMap}.
     */
    public Servo getServo(String name) {
        int index = (Arrays.asList(servoNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Servo " + name + " does not exist or is not registered in ManipulationManager");

        return servos[index];
    }


    public double getMotorPower(String name) {
        int index = (Arrays.asList(motorNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Motor " + name + " does not exist or is not registered in ManipulationManager");
        return motors[index].getPower();
    }

    public double getMotorPower(int i) {
        return motors[i].getPower();
    }

    public double getMotorPosition(String name) {
        int index = (Arrays.asList(motorNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Motor " + name + " does not exist or is not registered in ManipulationManager");
        return motors[index].getCurrentPosition();
    }

    public double getServoPower(String name) {
        int index = (Arrays.asList(crservoNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("CRServo " + name + " does not exist or is not registered in ManipulationManager");
        return crservos[index].getPower();
    }
    public double getServoPower(int i) {
        return crservos[i].getPower();
    }

    public double getServoPosition(String name) {
        int index = (Arrays.asList(servoNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Servo " + name + " does not exist or is not registered in ManipulationManager");
        return servos[index].getPosition();
    }

    public void setMotorModes(DcMotor.RunMode mode) {
        for(DcMotor motor : motors) motor.setMode(mode);
    }

    public void setMotorMode(String name, DcMotor.RunMode mode) {
        int index = (Arrays.asList(motorNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Motor " + name + " does not exist or is not registered in ManipulationManager");
        motors[index].setMode(mode);
    }

    public void resetEncoders() {
        for(DcMotor motor : motors) {
            if(motor != null) motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }

    private void runWithoutEncoders() {
        for(DcMotor motor : motors) {
            if(motor != null) motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    public void runUsingEncoders() {
        for(DcMotor motor : motors) motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void resetEncoders(String name) {
        int index = (Arrays.asList(motorNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Motor " + name + " does not exist or is not registered in ManipulationManager");
        motors[index].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void runUsingEncoders(String name) {
        int index = (Arrays.asList(motorNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Motor " + name + " does not exist or is not registered in ManipulationManager");
        motors[index].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setMotorPower(String name, double power) {
        int index = (Arrays.asList(motorNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Motor " + name + " does not exist or is not registered in ManipulationManager");
        setMotorPower(index,power);
    }

    public void setMotorPower(int i, double power) {
        motors[i].setPower(power);
    }

    public void setServoPower(int i, double power) {
        crservos[i].setPower(power);
    }

    public void setServoPosition(int i, double power) {
        servos[i].setPosition(power);
    }

    public void resetEncoders(int i) {
        motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void closeMotors() {
        for(DcMotor m : motors) m.close();
    }

    public int getMotorTargetPosition(String name) {
        int index = (Arrays.asList(motorNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Motor " + name + " does not exist or is not registered in ManipulationManager");
        return movementThread.getTarget(index);
    }
    public int getMotorTargetPosition(int i) {
        return motors[i].getTargetPosition();
    }

    public void encodeMoveToPosition(String name, int position, double power) {
        int index = (Arrays.asList(motorNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Motor " + name + " does not exist or is not registered in ManipulationManager");

        encodeMoveToPosition(index,position, power);
    }

    public void encodeMoveToPosition(String name, int position) {
        encodeMoveToPosition(name,position, DEFAULT_ENCODED_MOVEMENT_POWER);
    }

    public void encodeMoveToPosition(int index, int position) {
        encodeMoveToPosition(index, position, DEFAULT_ENCODED_MOVEMENT_POWER);
    }

    //monitors whether the lift motor has been given RUN_TO_POSITION
    public void encodeMoveToPosition(int index, int position, double power) {
        movementThread.move(index, position, power);
    }

    /**
     * Check whether the given motor is moving asynchronously. Usually, this means that it was moved with {@link #encodeMoveToPosition(String, int) encodeMoveToPosition} and hasn't finished yet.
     * <p>
     * Unless you're a robot, please try to use {@link #hasEncodedMovement(String) the String version} of this method instead.
     * </p>
     * @param index the index of the motor in the internal motors array.
     * @return {@code true} if the motor is moving; {@code false} otherwise.
     */
    public boolean hasEncodedMovement(int index) {
        return movementThread.isMoving(index);
    }

    /**
     * Check whether the given motor is moving asynchronously. Usually, this means that it was moved with {@link #encodeMoveToPosition(String, int) encodeMoveToPosition} and hasn't finished yet.
     *
     * @param name the name of the motor; must be one of the motor names given in the {@link #ManipulationManager(HardwareMap, String[], String[], String[]) constructor}
     * @return {@code true} if the motor is moving; {@code false} otherwise.
     */
    public boolean hasEncodedMovement(String name) {
        int index = (Arrays.asList(motorNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Motor " + name + " does not exist or is not registered in ManipulationManager");

        return hasEncodedMovement(index);
    }

    /**
     * <p>
     * If a motor is moving asynchronously, stop it.
     * If it isn't moving, this has no effect.
     * </p>
     * <p>
     * Unless you're a robot, please try to use {@link #cancelEncodedMovement(String) the String version} of this method instead.
     * </p>
     *
     * @see #cancelEncodedMovement(String)
     *
     * @param index the index of the motor in the internal motors array.
     */
    public void cancelEncodedMovement(int index) {
        movementThread.cancelMovement(index);
    }

    /**
     * If a motor is moving asynchronously, stop it.
     * If it isn't moving, this has no effect.
     *
     * @param name the name of the motor; must be one of the names given in the {@link #ManipulationManager(HardwareMap, String[], String[], String[]) constructor}
     */
    public void cancelEncodedMovement(String name) {
        int index = (Arrays.asList(motorNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Motor " + name + " does not exist or is not registered in ManipulationManager");

        cancelEncodedMovement(index);
    }


    public void setMotorTargetPosition(String name, int position) {
        int index = (Arrays.asList(motorNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Motor " + name + " does not exist or is not registered in ManipulationManager");

        motors[index].setTargetPosition(position);
    }

    public void manualMoveEncodedMotor(int index, int increment) {
        movementThread.incrementTarget(index, increment);
    }

    public void manualMoveEncodedMotor(String name, int increment) {
        int index = (Arrays.asList(motorNames)).indexOf(name);
        if(index == -1) throw new IllegalArgumentException("Motor " + name + " does not exist or is not registered in ManipulationManager");

        manualMoveEncodedMotor(index, increment);
    }
}
