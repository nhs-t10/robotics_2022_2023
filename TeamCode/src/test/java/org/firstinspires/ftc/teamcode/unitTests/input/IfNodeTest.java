package org.firstinspires.ftc.teamcode.unitTests.input;

import org.firstinspires.ftc.teamcode.managers.input.nodes.IfNode;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyInputNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IfNodeTest {
    @Test
    public void test() {
        DummyInputNode switcher = new DummyInputNode(0);
        DummyInputNode on = new DummyInputNode(3);
        DummyInputNode off = new DummyInputNode(2);

        IfNode ifNode = new IfNode(switcher, on, off);

        assertEquals(2, ifNode.getResult().getFloat(), 0);

        switcher.set(20);
        ifNode.update();

        assertEquals(3, ifNode.getResult().getFloat(), 0);
    }
}
