package org.firstinspires.ftc.teamcode.unitTests.dummy;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.MagneticFlux;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;
import org.firstinspires.ftc.robotcore.external.navigation.Temperature;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.auxilary.RobotTime;

public class DummyImu implements com.qualcomm.hardware.bosch.BNO055IMU {
    private final DummyImuUpdateyThread updateThread;
    protected DummyDcMotor frAnalysisMotor, flAnalysisMotor, brAnalysisMotor, blAnalysisMotor;

    public DummyImu() {
        this.updateThread = new DummyImuUpdateyThread(this);
        updateThread.start();
    }
    @Override
    public boolean initialize(@NonNull Parameters parameters) {
        return false;
    }

    @NonNull
    @Override
    public Parameters getParameters() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public Orientation getAngularOrientation() {
        return new Orientation(AxesReference.INTRINSIC,
                AxesOrder.XYZ,
                org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES,
                updateThread.orientation,
                updateThread.orientation,
                updateThread.orientation, RobotTime.nanoTime());

    }

    @Override
    public Orientation getAngularOrientation(AxesReference reference, AxesOrder order, org.firstinspires.ftc.robotcore.external.navigation.AngleUnit angleUnit) {
        return new Orientation(reference, order, angleUnit, updateThread.orientation, updateThread.orientation, updateThread.orientation, RobotTime.nanoTime());
    }

    @Override
    public Acceleration getOverallAcceleration() {
        return new Acceleration();
    }

    @Override
    public AngularVelocity getAngularVelocity() {
        return new AngularVelocity();
    }

    @Override
    public Acceleration getLinearAcceleration() {
        return new Acceleration();
    }

    @Override
    public Acceleration getGravity() {
        return new Acceleration();
    }

    @Override
    public Temperature getTemperature() {
        return new Temperature();
    }

    @Override
    public MagneticFlux getMagneticFieldStrength() {
        return new MagneticFlux();
    }

    @Override
    public Quaternion getQuaternionOrientation() {
        return new Quaternion();
    }

    @Override
    public Position getPosition() {
        return new Position();
    }

    @Override
    public Velocity getVelocity() {
        return new Velocity();
    }

    @Override
    public Acceleration getAcceleration() {
        return new Acceleration();
    }

    @Override
    public void startAccelerationIntegration(Position initialPosition, Velocity initialVelocity, int msPollInterval) {

    }

    @Override
    public void stopAccelerationIntegration() {

    }

    @Override
    public SystemStatus getSystemStatus() {
        return SystemStatus.RUNNING_FUSION;
    }

    @Override
    public SystemError getSystemError() {
        return SystemError.NO_ERROR;
    }

    @Override
    public CalibrationStatus getCalibrationStatus() {
        return new CalibrationStatus(0b1111_1111);
    }

    @Override
    public boolean isSystemCalibrated() {
        return true;
    }

    @Override
    public boolean isGyroCalibrated() {
        return true;
    }

    @Override
    public boolean isAccelerometerCalibrated() {
        return true;
    }

    @Override
    public boolean isMagnetometerCalibrated() {
        return true;
    }

    @Override
    public CalibrationData readCalibrationData() {
        return new CalibrationData();
    }

    @Override
    public void writeCalibrationData(CalibrationData data) {

    }

    @Override
    public byte read8(Register register) {
        return 0;
    }

    @Override
    public byte[] read(Register register, int cb) {
        return new byte[0];
    }

    @Override
    public void write8(Register register, int bVal) {

    }

    @Override
    public void write(Register register, byte[] data) {

    }

    public void setFl(DummyDcMotor m) { this.flAnalysisMotor = m; }
    public void setFr(DummyDcMotor m) { this.frAnalysisMotor = m; }
    public void setBl(DummyDcMotor m) { this.blAnalysisMotor = m; }
    public void setBr(DummyDcMotor m) { this.brAnalysisMotor = m; }
}
