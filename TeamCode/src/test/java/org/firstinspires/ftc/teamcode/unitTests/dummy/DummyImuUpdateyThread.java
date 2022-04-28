package org.firstinspires.ftc.teamcode.unitTests.dummy;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.MagneticFlux;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;
import org.firstinspires.ftc.robotcore.external.navigation.Temperature;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.auxilary.PaulMath;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

public class DummyImuUpdateyThread extends Thread {
    private final WeakReference<DummyImu> imuRef;

    public float orientation = 0;

    public DummyImuUpdateyThread(DummyImu dummyImu) {
        this.imuRef = new WeakReference<DummyImu>(dummyImu);
    }

    public void run() {
        while(true) {
            try {
                //get the imu from the weak reference. If the result is `null`, it's been garbaged and this thread should stop.
                DummyImu imu = imuRef.get();
                if(imu == null) {
                    break;
                }

                DummyDcMotor fl = imu.flAnalysisMotor,
                        fr = imu.frAnalysisMotor,
                        bl = imu.blAnalysisMotor,
                        br = imu.brAnalysisMotor;
                if(fl != null && fr != null && bl != null && br != null) {
                    float[] omni = PaulMath.omniCalcInverse((float)fl.power, (float)fr.power, (float)br.power, (float)bl.power, true);
                    float r = omni[2];
                    orientation += r;
                }
                sleep(10);
            } catch(InterruptedException ignored) {}
        }
    }
}
