package org.firstinspires.ftc.teamcode.unitTests.input;

import org.firstinspires.ftc.teamcode.managers.input.nodes.MinusNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.MultiInputNode;
import org.firstinspires.ftc.teamcode.managers.input.nodes.PlusNode;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyInputNode;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MinusNodeTest {
    @Test
    public void test() {
        DummyInputNode scaler = new DummyInputNode(0);
        DummyInputNode value = new DummyInputNode(10);

        MultiInputNode array1 = new MultiInputNode(new DummyInputNode(3), new DummyInputNode(2));
        MultiInputNode array2 = new MultiInputNode(new DummyInputNode(4), new DummyInputNode(2), new DummyInputNode(-3));

        MinusNode minusNode = new MinusNode(scaler, value);

        assertEquals(-10, minusNode.getResult().getFloat(), 0);

        scaler.set(20);
        minusNode.update();

        assertEquals(10, minusNode.getResult().getFloat(), 0);


        minusNode = new MinusNode(array1, array2);
        minusNode.update();

        assertArrayEquals(new float[] {-1,0,3}, minusNode.getResult().getFloatArray(), 0);
    }
}
