package org.firstinspires.ftc.teamcode.unitTests.telemetry;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.server.BodyParser;
import org.firstinspires.ftc.teamcode.managers.telemetry.server.Headers;
import org.firstinspires.ftc.teamcode.managers.telemetry.server.Server;
import org.firstinspires.ftc.teamcode.managers.telemetry.server.ServerFiles;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyOpmode;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyTelemetry;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HttpServerTest {
    @Test
    public void test() throws IOException {
        FeatureManager.setIsOpModeRunning(true);

        TelemetryManager telManNoFeat = new TelemetryManager(new DummyTelemetry(), new DummyOpmode(), TelemetryManager.BITMASKS.NONE);

        Server server = new Server(telManNoFeat);

        server.blockUntilLoaded();

        assertTrue(connectAddress("localhost", server.port));

        String httpReply = getResponse("localhost", server.port,
                "GET / HTTP/1.1\r\n" +
                        "Host: localhost\r\n\r\n");

        BufferedReader httpBodyReader = new BufferedReader(new StringReader(httpReply));

        //discard status line
        assertEquals("HTTP/1.1 200 OK", httpBodyReader.readLine());

        Headers headers = Headers.from(httpBodyReader);
        assertEquals("text/html; charset=utf-8", headers.get("Content-Type"));

        String httpBody = BodyParser.from(httpBodyReader, headers, false);

        assertEquals(ServerFiles.getAssetString("index.html"), httpBody);

        FeatureManager.setIsOpModeRunning(false);
    }

    private boolean connectAddress(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 30_000);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }
    private String getResponse(String host, int port, String sendBody) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 30_000);

            PrintWriter sendWriter = new PrintWriter(socket.getOutputStream());
            sendWriter.write(sendBody);
            sendWriter.flush();

            try(InputStream response = socket.getInputStream()) {
                StringBuilder responseMessage = new StringBuilder();
                while(true) {
                    int nextByte = response.read();
                    if(nextByte < 0) break;
                    else responseMessage.append((char) nextByte);
                }
                sendWriter.close();
                socket.close();
                return responseMessage.toString();
            }

        } catch (IOException e) {
            return ""; // Either timeout or unreachable or failed DNS lookup.
        }
    }
}
