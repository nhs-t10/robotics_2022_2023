package org.firstinspires.ftc.teamcode.unitTests.paulmath;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RepeatStringTest {
    @Test
    public void test() {
        assertEquals("hhh", PaulMath.repeat("h", 3));
    }
}
