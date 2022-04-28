package org.firstinspires.ftc.teamcode.unitTests.http;

import org.firstinspires.ftc.teamcode.managers.telemetry.server.QueryStringParams;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueryStringParserTest {
    @Test
    public void test() {
        String q = "?a=b&c=d&%20=%20&c=3";
        QueryStringParams qsp = QueryStringParams.from(q);

        assertTrue(qsp.has("a"));
        assertTrue(qsp.has("c"));
        assertTrue(qsp.has(" "));

        assertEquals("b", qsp.get("a"));
        assertEquals("d", qsp.get("c"));
        assertEquals(" ", qsp.get(" "));
        assertEquals(3, qsp.getAsInt("c", 1));

    }
}
