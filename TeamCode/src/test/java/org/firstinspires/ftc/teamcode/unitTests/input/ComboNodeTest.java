package org.firstinspires.ftc.teamcode.unitTests.input;

import org.firstinspires.ftc.teamcode.managers.input.nodes.ComboNode;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyInputNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ComboNodeTest {
    @Test
    public void test() {
        DummyInputNode a = new DummyInputNode();
        DummyInputNode b = new DummyInputNode();
        DummyInputNode c = new DummyInputNode();
        ComboNode combo = new ComboNode(a, b, c);

        assertEquals(0, combo.getResult().getFloat(), 0);

        a.set(1);
        combo.update();
        b.set(1);
        combo.update();
        c.set(1);
        combo.update();

        //make sure that an AND works
        assertEquals(1, combo.getResult().getFloat(), 0);

        a.set(0);combo.update();
        b.set(0);combo.update();
        c.set(0);combo.update();

        //make sure that it'll turn off again
        assertEquals(0, combo.getResult().getFloat(), 0);

        a.set(1);combo.update();
        b.set(1);combo.update();
        c.set(1);combo.update();
        combo.update();
        a.set(0);combo.update();

        //make sure that it'll turn off again with only 1
        assertEquals(0, combo.getResult().getFloat(), 0);

        //ensure that it WON'T work when the incorrect order is used
        a.set(0);combo.update();
        b.set(0);combo.update();
        c.set(0);combo.update();

        c.set(1);combo.update();
        a.set(1);combo.update();
        b.set(1);combo.update();

        assertEquals(0, combo.getResult().getFloat(), 0);

    }

    private void blockFor(int ms) {
        long ty = System.currentTimeMillis();
        while(System.currentTimeMillis() < ty + ms);
    }
}
