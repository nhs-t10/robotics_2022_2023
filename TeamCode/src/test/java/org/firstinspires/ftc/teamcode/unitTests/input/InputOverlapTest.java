package org.firstinspires.ftc.teamcode.unitTests.input;

import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputOverlapResolutionMethod;
import org.firstinspires.ftc.teamcode.managers.input.nodes.AllNode;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyGamepad;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyInputNode;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InputOverlapTest {
    @Test
    public void test() {
        DummyInputNode inA = new DummyInputNode(0, "a");
        DummyInputNode inB = new DummyInputNode(0, "b");
        DummyInputNode inA2 = new DummyInputNode(0, "a");

        InputManager manager = new InputManager(new DummyGamepad(), new DummyGamepad());

        AllNode both = new AllNode(inB, inA2);
        manager.registerInput("lessComplex", inA);
        manager.registerInput("moreComplex", both);

        manager.setOverlapResolutionMethod(InputOverlapResolutionMethod.MOST_COMPLEX_ARE_THE_FAVOURITE_CHILD);

        manager.update();

        assertFalse(manager.getBool("lessComplex"));
        assertFalse(manager.getBool("moreComplex"));

        inA.set(1f);
        inA2.set(1f);
        manager.update();

        assertTrue(manager.getBool("lessComplex"));
        assertFalse(manager.getBool("moreComplex"));

        inB.set(1f);
        manager.update();

        assertFalse(manager.getBool("lessComplex"));
        assertTrue(manager.getBool("moreComplex"));
    }
}
