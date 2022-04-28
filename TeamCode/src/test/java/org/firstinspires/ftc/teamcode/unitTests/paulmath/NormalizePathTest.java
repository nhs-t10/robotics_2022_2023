package org.firstinspires.ftc.teamcode.unitTests.paulmath;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NormalizePathTest {
    @Test
    public void test() {
        assertEquals("foo", PaulMath.normalizeRelativePath("foo"));
        assertEquals("foo/bar", PaulMath.normalizeRelativePath("foo/bar"));
        assertEquals("foo/bar", PaulMath.normalizeRelativePath("foo/./bar"));
        assertEquals("foo", PaulMath.normalizeRelativePath("./foo"));
        assertEquals("bar", PaulMath.normalizeRelativePath("foo/../bar"));
    }
}
