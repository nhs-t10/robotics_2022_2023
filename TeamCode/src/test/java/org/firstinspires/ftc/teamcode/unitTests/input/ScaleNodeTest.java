package org.firstinspires.ftc.teamcode.unitTests.input;

import org.firstinspires.ftc.teamcode.managers.input.nodes.ScaleNode;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyInputNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScaleNodeTest {
    @Test
    public void test() {
        DummyInputNode scaler = new DummyInputNode(0);
        DummyInputNode value = new DummyInputNode(10);

        ScaleNode scaleNode = new ScaleNode(scaler, value);

        assertEquals(0, scaleNode.getResult().getFloat(), 0);

        scaler.set(20);
        scaleNode.update();

        assertEquals(200, scaleNode.getResult().getFloat(), 0);
    }
}
