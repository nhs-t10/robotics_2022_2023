package org.firstinspires.ftc.teamcode.managers.imu;

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
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

public class ImuManager extends FeatureManager {
    public BNO055IMU imu;

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


}
