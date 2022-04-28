package org.firstinspires.ftc.teamcode.unitTests.paulmath;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JSONifyTest {
    @Test
    public void test() {
        String unencoded = "This is some text containing special formatting characters\n\n\r\r\r\n\\\\\\\\t\\r\\n";
        String encoded = "\"This is some text containing special formatting characters\\n\\n\\r\\r\\r\\n\\\\\\\\\\\\\\\\t\\\\r\\\\n\"";

        //test encoding strings
        assertEquals(encoded, PaulMath.JSONify(unencoded));

        //test decoding strings
        assertEquals(unencoded, PaulMath.deJSONify(encoded));

        //test chars
        assertEquals("\"A\"", PaulMath.JSONify('A'));

        //test booleans
        assertEquals("true", PaulMath.JSONify(true));

        //test numeric primitives
        assertEquals("4", PaulMath.JSONify(4));
        assertEquals("4.0", PaulMath.JSONify(4.0));
        assertEquals("4", PaulMath.JSONify(4L));
        assertEquals("4.0", PaulMath.JSONify(4f));
        assertEquals("4", PaulMath.JSONify((byte)4));
        assertEquals("4", PaulMath.JSONify((short)4));
    }
}
