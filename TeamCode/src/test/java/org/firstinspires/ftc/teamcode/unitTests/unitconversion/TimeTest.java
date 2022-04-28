package org.firstinspires.ftc.teamcode.unitTests.unitconversion;

import org.firstinspires.ftc.teamcode.auxilary.units.TimeUnit;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.PlusNode;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyInputNode;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TimeTest {
    @Test
    public void test() {
        assertEquals(3600000, TimeUnit.convertBetween(TimeUnit.HR, TimeUnit.MS, 1), 0);
    }
}
