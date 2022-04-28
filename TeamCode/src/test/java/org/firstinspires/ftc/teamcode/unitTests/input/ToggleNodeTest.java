package org.firstinspires.ftc.teamcode.unitTests.input;

import org.firstinspires.ftc.teamcode.managers.input.nodes.ToggleNode;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyInputNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ToggleNodeTest {
    @Test
    public void test() {
        DummyInputNode toggler = new DummyInputNode(0);

        ToggleNode toggleNode = new ToggleNode(toggler);

        toggleNode.update();

        assertEquals(0, toggleNode.getResult().getFloat(), 0);

        toggler.set(1);
        toggleNode.update();

        assertEquals(1, toggleNode.getResult().getFloat(), 0);

        toggleNode.update();

        assertEquals(1, toggleNode.getResult().getFloat(), 0);

        toggler.set(0);
        toggleNode.update();

        assertEquals(1, toggleNode.getResult().getFloat(), 0);

        toggler.set(1);
        toggleNode.update();

        assertEquals(0, toggleNode.getResult().getFloat(), 0);

        toggleNode.update();

        assertEquals(0, toggleNode.getResult().getFloat(), 0);
    }
}
