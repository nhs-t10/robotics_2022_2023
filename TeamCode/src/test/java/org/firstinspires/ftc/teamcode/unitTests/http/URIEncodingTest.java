package org.firstinspires.ftc.teamcode.unitTests.http;

import org.firstinspires.ftc.teamcode.managers.telemetry.server.QueryStringParams;
import org.firstinspires.ftc.teamcode.managers.telemetry.server.URIEncoding;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class URIEncodingTest {
    @Test
    public void test() {
        String[] testCasesEnc = new String[] {"The%20Bible", "", "%C3%A5", "%E5%86%86ace"};
        String[] testCasesDec = new String[] {"The Bible", "", "\u00e5", "円ace"};

        for(int i = 0; i < testCasesEnc.length; i++) {
            String encoded = URIEncoding.encode(testCasesDec[i]);
            assertEquals(testCasesEnc[i], encoded);

            String decoded = URIEncoding.decode(testCasesEnc[i]);
            assertEquals(testCasesDec[i], decoded);
        }

        assertEquals("ace", URIEncoding.decode("%uxxxxace"));
    }
}
