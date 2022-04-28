package org.firstinspires.ftc.teamcode.unitTests.telemetry;

import org.firstinspires.ftc.teamcode.managers.telemetry.pojotracker.OhNoJavaFieldMonitorAndExposer;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyOpmode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PojoMonitorTest {
    @Test
    public void test() {
        TestPojoOpmode testPojoOpmode = new TestPojoOpmode();
        OhNoJavaFieldMonitorAndExposer monitor = new OhNoJavaFieldMonitorAndExposer(testPojoOpmode, true);

        assertEquals(
                "{\"testString\":\"foobar\",\"testInt\":3,\"testFloat\":2.4,\"nullField\":null,\"arrayTest[0]\":\"foo\",\"arrayTest[1]\":3,\"objectField.foobar\":\"feeeeees!\",\"getMethodTestString()\":\"boofar\"}",
                monitor.getJSON()
        );
        testPojoOpmode.testString = "barfoo";

        assertEquals("barfoo", testPojoOpmode.testString);

        assertEquals(
                "{\"testString\":\"barfoo\",\"testInt\":3,\"testFloat\":2.4,\"nullField\":null,\"arrayTest[0]\":\"foo\",\"arrayTest[1]\":3,\"objectField.foobar\":\"feeeeees!\",\"getMethodTestString()\":\"boofar\"}",
                monitor.getJSON()
        );
    }

    public static class TestPojoOpmode extends DummyOpmode {
        public String testString = "foobar";
        public int testInt = 3;
        public float testFloat = 2.4f;

        private String thisOneShouldntBePrinted;
        public String nullField;

        public Object[] arrayTest = new Object[] {"foo", 3};

        public TestPojoPropertyClass objectField = new TestPojoPropertyClass();

        public String getMethodTestString() {
            return "boofar";
        }
    }

    public static class TestPojoPropertyClass {
        public String foobar = "feeeeees!";
    }
}
