package org.firstinspires.ftc.teamcode.unitTests.opmodetesting;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.auxilary.RobotTime;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.unitTests.TestTypeManager;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyGamepad;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyHardwareMap;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyOutputStream;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyPrintStream;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyTelemetry;
import org.junit.Test;

import java.io.PrintStream;
import java.time.Clock;

import static org.firstinspires.ftc.robotcore.internal.system.Assert.assertTrue;

public class OpmodeTester {
    private static final Object TEST_LOCK = new Object();

    static RunWhileInLoopFunction DEFAULT_INLOOP_FUNCTION = new DoNothingWhileInLoopFunction();
    static final long DEFAULT_REALTIME_MS = 100;
    static final long DEFAULT_SIMULATED_MS = DEFAULT_REALTIME_MS;

    public static synchronized boolean runTestOn(OpMode opmode) {
        return runTestOn(opmode, DEFAULT_SIMULATED_MS);
    }
    public static synchronized boolean runTestOn(OpMode opmode, long simulatedMsFor) {
        return runTestOn(opmode, simulatedMsFor, DEFAULT_INLOOP_FUNCTION);
    }
    public static synchronized boolean runTestOn(OpMode opmode, long simulatedMsFor, RunWhileInLoopFunction function) {
        return runTestOn(opmode, simulatedMsFor, function, DEFAULT_REALTIME_MS);
    }
    public static synchronized boolean runTestOn(OpMode opmode, long simulatedMsFor, RunWhileInLoopFunction function, long realMsFor) {
        synchronized (TEST_LOCK) {
            PrintStream originalStdout = FeatureManager.logger.fallbackBackend;
            DummyPrintStream fakeStdout = new DummyPrintStream(new DummyOutputStream());
            FeatureManager.logger.setBackend(fakeStdout);

            opmode.hardwareMap = new DummyHardwareMap();
            opmode.gamepad1 = new DummyGamepad();
            opmode.gamepad2 = new DummyGamepad();
            opmode.telemetry = new DummyTelemetry();

            //TODO: make time dilation work

            OpmodeLoopRunnerThread runner = new OpmodeLoopRunnerThread(opmode);
            runner.start();

            runner.blockUntilInit();

            long startRealTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startRealTime < realMsFor) {
                function.looper(RobotTime.currentTimeMillis());
            }
            runner.testOver();

            //wait until the runner and its watchdog are dead before cleaning up
            boolean succeeded = runner.watchdog.blockUntilDone();

            FeatureManager.logger.setBackend(originalStdout);
            if(!TestTypeManager.isRunningOnServer()) fakeStdout.printBufferTo(originalStdout);

            return succeeded;
        }
    }

}
