package org.firstinspires.ftc.teamcode.unitTests.parserTools;

import org.firstinspires.ftc.teamcode.auxilary.dsls.ParserTools;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class GroupAwareSplitTest {
    @Test
    public void test() {
        assertArrayEquals(new String[] {"a"}, ParserTools.groupAwareSplit("a", ','));
        assertArrayEquals(new String[] {"a", "b"}, ParserTools.groupAwareSplit("a,b", ','));
        assertArrayEquals(new String[] {"(,a)", "b"}, ParserTools.groupAwareSplit("(,a),b", ','));
        assertArrayEquals(new String[] {"{,a}", "b"}, ParserTools.groupAwareSplit("{,a},b", ','));
        assertArrayEquals(new String[] {"[,a]", "b"}, ParserTools.groupAwareSplit("[,a],b", ','));
        assertArrayEquals(new String[] {"a", "(,b)"}, ParserTools.groupAwareSplit("a,(,b)", ','));
        assertArrayEquals(new String[] {"a", "{,b}"}, ParserTools.groupAwareSplit("a,{,b}", ','));
        assertArrayEquals(new String[] {"a", "[,b]"}, ParserTools.groupAwareSplit("a,[,b]", ','));
    }
}
