package org.firstinspires.ftc.teamcode.unitTests.dummy;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.auxilary.RobotTime;

import java.lang.ref.WeakReference;

public class DummyMotorMovementThread extends Thread {
    //use a WeakReference so this thread won't keep its motor from being garbage collected
    private final WeakReference<DummyDcMotor> motorRef;
    public volatile boolean running;

    private double lastVelocityRotPerms;

    public final double MAX_ROTS_PER_MS = 0.1;
    public final double DRAG_COEF = 0.1;
    public final double ACCELERATION_ROT_PER_POWER_PER_MS = 1;

    private double ticksPerRot;

    public DummyMotorMovementThread(DummyDcMotor m) {
        this.running = true;
        this.motorRef = new WeakReference<>(m);
        this.ticksPerRot = m.ticksPerRot;
    }
    @Override
    public void run() {
        long lastIterationTime = RobotTime.currentTimeMillis();
        while(running) {
            long thisIterationTime = RobotTime.currentTimeMillis();
            double deltaTime = (thisIterationTime - lastIterationTime);

            if(deltaTime < 1) continue;

            //get the motor from the weak reference. If the result is `null`, it's been garbaged and this thread should stop.
            DummyDcMotor motor = motorRef.get();
            if(motor == null) {
                running = false;
                break;
            }

            if (motor.runMode == DcMotor.RunMode.RUN_TO_POSITION) {
                moveMotorTowardsTarget(motor, deltaTime);
            } else {
                moveMotorAccordingToPower(motor, deltaTime);
            }
            lastIterationTime = thisIterationTime;
            Thread.yield();
        }
    }

    private void moveMotorAccordingToPower(DummyDcMotor motor, double elapsedTimeMs) {
        double acceleration = motor.power * ACCELERATION_ROT_PER_POWER_PER_MS;
        if(motor.direction.equals(DcMotorSimple.Direction.REVERSE)) acceleration *= -1;

        double velocity = (lastVelocityRotPerms * DRAG_COEF + acceleration) * elapsedTimeMs;
        velocity = PaulMath.clamp(velocity, -MAX_ROTS_PER_MS, MAX_ROTS_PER_MS);

        motor.currentPosition += (velocity * elapsedTimeMs) * ticksPerRot;

        lastVelocityRotPerms = velocity;
    }

    private void moveMotorTowardsTarget(DummyDcMotor motor, double elapsedTimeMs) {
        double currentPosition = motor.currentPosition, target = motor.target;

        double delta = target - currentPosition;
        motor.currentPosition += Math.min(delta, ticksPerRot) * (elapsedTimeMs / 100.0);
    }
}
