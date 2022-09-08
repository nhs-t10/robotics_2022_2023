package org.firstinspires.ftc.teamcode.unitTests.autoauto;

import static org.firstinspires.ftc.robotcore.internal.system.Assert.assertTrue;

import org.firstinspires.ftc.teamcode.opmodes.auto.Seawall;
import org.firstinspires.ftc.teamcode.opmodes.teleop.LearnDual;
import org.firstinspires.ftc.teamcode.unitTests.opmodetesting.OpmodeTester;
import org.junit.Test;

public class SeawallTest {
    @Test
    public void test() {
        assertTrue(OpmodeTester.runTestOn(new Seawall(), 10000));
    }
}
