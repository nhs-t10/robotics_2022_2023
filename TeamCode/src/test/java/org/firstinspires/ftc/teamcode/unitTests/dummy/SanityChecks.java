package org.firstinspires.ftc.teamcode.unitTests.dummy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SanityChecks {

    @Test
    public void dummyInputNodeSanityCheck() {
        DummyInputNode n = new DummyInputNode(3);
        assertEquals(3, n.getResult().getFloat(), 0);
        n.set(2);
        assertEquals(2, n.getResult().getFloat(), 0);
    }
}
