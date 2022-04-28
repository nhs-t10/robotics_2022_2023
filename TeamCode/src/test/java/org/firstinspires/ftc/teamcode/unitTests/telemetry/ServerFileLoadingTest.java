package org.firstinspires.ftc.teamcode.unitTests.telemetry;

import org.firstinspires.ftc.teamcode.managers.telemetry.server.ServerFiles;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ServerFileLoadingTest {
    @Test
    public void test() throws IOException {
        assertNull(ServerFiles.getAssetString("file-that-doesnt-exist-askldjdavnk"));
        assertEquals("Test Asset", ServerFiles.getAssetString("test.txt"));
    }
}
