package org.firstinspires.ftc.teamcode.unitTests.manipulationmanager;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.manipulation.ManipulationManager;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyHardwareMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EncodedMovementTest {
    @Test
    public void test() {
        final String fooMotor = "foo";

        FeatureManager.setIsOpModeRunning(true);
        ManipulationManager m = new ManipulationManager(new DummyHardwareMap(), new String[0], new String[0], new String[] {fooMotor});

        m.encodeMoveToPosition(fooMotor, 4096);

        long startTime = System.currentTimeMillis();

        long lastPrintTime = 0;
        //wait until it's finished moving...
        while(m.hasEncodedMovement(fooMotor)) {
            long now = System.currentTimeMillis();
            if(now - lastPrintTime > 0) {
                FeatureManager.logger.log(now + "\t" + m.getMotorPosition(fooMotor) + "\t" + m.getMotorPower(fooMotor));
                lastPrintTime = now;
            }

            //...or the time has ran out
            if(now - startTime > 10_000) {
                fail("Timeout");
            }

            Thread.yield();
        }

        assertEquals(4096, m.getMotorPosition(fooMotor), 1000);
    }
}
