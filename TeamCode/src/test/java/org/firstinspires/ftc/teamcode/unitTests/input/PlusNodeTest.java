package org.firstinspires.ftc.teamcode.unitTests.input;

import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.PlusNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.ScaleNode;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyInputNode;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class PlusNodeTest {
    @Test
    public void test() {
        DummyInputNode scaler = new DummyInputNode(0);
        DummyInputNode value = new DummyInputNode(10);

        MultiInputNode array1 = new MultiInputNode(new DummyInputNode(3), new DummyInputNode(2));
        MultiInputNode array2 = new MultiInputNode(new DummyInputNode(3), new DummyInputNode(2), new DummyInputNode(-3));

        PlusNode plusNode = new PlusNode(scaler, value);

        assertEquals(10, plusNode.getResult().getFloat(), 0);

        scaler.set(20);
        plusNode.update();

        assertEquals(30, plusNode.getResult().getFloat(), 0);


        plusNode = new PlusNode(array1, array2);
        plusNode.update();

        assertArrayEquals(new float[] {6,4,-3}, plusNode.getResult().getFloatArray(), 0);
    }
}
