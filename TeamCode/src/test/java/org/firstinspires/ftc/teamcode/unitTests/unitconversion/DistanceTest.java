package org.firstinspires.ftc.teamcode.unitTests.unitconversion;

import org.firstinspires.ftc.teamcode.auxilary.units.DistanceUnit;
import org.firstinspires.ftc.teamcode.auxilary.units.TimeUnit;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DistanceTest {
    @Test
    public void test() {
        assertEquals(2.54, DistanceUnit.convertBetween(DistanceUnit.CM, DistanceUnit.IN, 1), 0);
    }
}
