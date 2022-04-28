package org.firstinspires.ftc.teamcode.unitTests.dummy;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

import java.lang.ref.WeakReference;

public class DummyDcMotor implements DcMotor {
    protected double power = 0.0;
    protected Direction direction = Direction.FORWARD;
    protected RunMode runMode = RunMode.RUN_WITHOUT_ENCODER;
    protected int target;
    protected boolean hasTargetSet;
    protected double currentPosition;

    protected double ticksPerRot;

    private static int dummyIDcounter;

    public DummyDcMotor(String deviceName, DummyImu imu) {
        if(deviceName.equals("fl")) imu.setFl(this);
        if(deviceName.equals("fr")) imu.setFr(this);
        if(deviceName.equals("bl")) imu.setBl(this);
        if(deviceName.equals("br")) imu.setBr(this);

        this.ticksPerRot = FeatureManager.getRobotConfiguration().encoderTicksPerRotation;
        (new DummyMotorMovementThread(this)).start();
    }

    @Override
    public MotorConfigurationType getMotorType() {
        return null;
    }

    @Override
    public void setMotorType(MotorConfigurationType motorType) {
        
    }

    @Override
    public DcMotorController getController() {
        return null;
    }

    @Override
    public int getPortNumber() {
        return 0;
    }

    @Override
    public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {

    }

    @Override
    public ZeroPowerBehavior getZeroPowerBehavior() {
        return null;
    }

    @Override
    public void setPowerFloat() {

    }

    @Override
    public boolean getPowerFloat() {
        return false;
    }

    @Override
    public void setTargetPosition(int position) {
        this.target = position;
        hasTargetSet = true;
    }

    @Override
    public int getTargetPosition() {
        return target;
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public int getCurrentPosition() {
        return (int) Math.round(this.currentPosition);
    }

    @Override
    public void setMode(RunMode mode) {
        if(mode == RunMode.RUN_TO_POSITION) assert hasTargetSet == true;
        this.runMode = mode;
    }

    @Override
    public RunMode getMode() {
        return runMode;
    }

    @Override
    public void setDirection(Direction d) {
        this.direction = d;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setPower(double p) {
        this.power = Math.min(1, Math.max(-1, p));
    }

    @Override
    public double getPower() {
        return power;
    }

    @Override
    public Manufacturer getManufacturer() {
        return null;
    }

    @Override
    public String getDeviceName() {
        return null;
    }

    @Override
    public String getConnectionInfo() {
        return "dummy motor " + (dummyIDcounter++);
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {

    }

    @Override
    public void close() {

    }
}
