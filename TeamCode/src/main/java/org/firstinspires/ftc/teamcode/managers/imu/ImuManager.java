package org.firstinspires.ftc.teamcode.managers.imu;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.auxilary.integratedasync.PriorityAsyncOpmodeComponent;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;
import org.firstinspires.ftc.teamcode.managers.movement.MovementManager;

public class ImuManager extends FeatureManager {
    public BNO055IMU imu;
    private boolean doOnce = false;
    ManipulationManager hands;
    MovementManager driver;

    public ImuManager(BNO055IMU imu) {
        this.imu = imu;
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode = BNO055IMU.SensorMode.NDOF;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.useExternalCrystal = true;
        parameters.loggingEnabled  = true;
        parameters.loggingTag = "Imu";

        imu.initialize(parameters);
        imu.startAccelerationIntegration(null, null,0);
    }

    /**
     * Get the orientation
     * @return Current orientation of the robot, in degrees
     */
    public Orientation getOrientation() {
        return imu.getAngularOrientation(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
    }

    public float getFirstAngleOrientation() {
        return getOrientation().firstAngle;
    }
    public float getSecondAngleOrientation() {
        return getOrientation().secondAngle;
    }
    public float getThirdAngleOrientation() {
        return getOrientation().thirdAngle;
    }

    public Position getPosition() {
        return imu.getPosition();
    }
    //we use Z for Y because the rev hub is rotated
    public double getPositionX() {
        return imu.getPosition().x;
    }
    public double getPositionY() {
        return imu.getPosition().z;
    }
    public double getVelocityY() {
        return imu.getVelocity().zVeloc;
    }
    public double getVelocityX() {
        return imu.getVelocity().xVeloc;
    }
    public double getAccelerationX() {
        return imu.getAcceleration().xAccel;
    }
    public double getAccelerationY() {
        return imu.getAcceleration().zAccel;
    }
    private Thread thread;
    public Acceleration getLinearAcceleration() {
        return imu.getLinearAcceleration();
    }

    public float[] rotateDriveControlToHeadless(float[] drive) {
        float y = drive[0];
        float x = drive[1];

        float[] polar = PaulMath.cartesianToPolar(x, y);

        polar[1] -= getOrientation().thirdAngle;

        float[] cartesian = PaulMath.polarToCartesian(polar[0], polar[1]);

        return new float[] {
                cartesian[0],
                cartesian[1],
                drive[2]
        };
    }
//power is a magnitude, so unsigned. Sign the angle.
    public void rotate(double angle, float power) {
        if (angle < 0) {
            power = power * -1;
        }
        double endingPosition = getOrientation().thirdAngle + angle;
        driver.driveOmni(0, 0, power);
        thread = new Thread(() -> {
            double currentPosition = getOrientation().thirdAngle;
            while (!(Math.abs(currentPosition - endingPosition) < 5)) {
                driver.driveOmni(0, 0, 0);
                threadStop();
                return;
            }
        });
    }
    public void threadStop(){
        thread.interrupt();
    }

}
