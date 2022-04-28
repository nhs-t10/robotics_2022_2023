package org.firstinspires.ftc.teamcode.unitTests.input;

import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NodeResultTest {
    @Test
    public void test() {
        InputManagerNodeResult n = new InputManagerNodeResult();
        assertEquals(0, n.getFloat(), 0);

        n.setFloat(3);
        assertEquals(3, n.getFloat(), 0);

        n.setFloatArray(new float[] { 25 });
        assertEquals(25, n.getFloat(), 0);

        n.setFloatArray(new float[] { 10 });
        assertEquals(10, n.getFloat(), 0);

        n.setBool(false);
        assertEquals(0, n.getFloat(), 0);

        n.setBool(true);
        assertEquals(1, n.getFloat(), 0);
    }
}
