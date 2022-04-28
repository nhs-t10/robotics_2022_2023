package org.firstinspires.ftc.teamcode.unitTests.http;

import org.firstinspires.ftc.teamcode.managers.telemetry.server.BodyParser;
import org.firstinspires.ftc.teamcode.managers.telemetry.server.Headers;
import org.firstinspires.ftc.teamcode.managers.telemetry.server.HttpStatusLine;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class BodyParsingTest {
    String HTTP_BODY = "{}";
    String HTTP_REQUEST = "POST / HTTP/1.1\r\n" +
            "Host: www.example.com\r\n" +
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0\r\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n" +
            "Accept-Language: en-US,en;q=0.5\r\n" +
            "Accept-Encoding: gzip, deflate, br\r\n" +
            "Alt-Used: classroom.google.com\r\n" +
            "Connection: keep-alive\r\n" +
            "Cookie: NID=223=Wl-3ItwOQ8sRbizbzViiK9U9xrY3_dYduKsgGbnN4u97RA6P9PG61QCmYNs3JzhYh5vni7SJtoPctkeYtWoaWl6SEQOHzcCypya2o6nrRa7zT9Zjwg2D1zvOLamcQBEmPCXhEryAgIG_lZGEJvu_Y-Ojto_r7oZAYtMzxf_VnQm6QEOV_tbYjhcJQF_wnFSCIjbPSYWnZHtMEJbBlkBX7vkqTx4l_GvnysoPEGFWy4bz8fRXM0J5V9lr2eZZ1K66yEXto-aQoScH406uZgmGnffViPvvjddCkWLAvEZamctL6PjKgRfGiBUrErS125XviXV8XVs77f19uoTQEp0GKuv5Y_18tjyfcbMWfJYiIxo90TSJs4VaEgW62A; ANID=AHWqTUlSmD33ALNYO8IF4otNhkSuElXuueCFvLHPAGYVRjUPeTFrUklPiexkM8CL; SID=BgikMBMlZBnF78SPJrtXTvtvyhEzsVlY0ezMrX2hKlUuuUaYvVe-s7IwNWU-G5xUijYwQQ.; __Secure-3PSID=BgikMBMlZBnF78SPJrtXTvtvyhEzsVlY0ezMrX2hKlUuuUaYjUmKLEyk8ZbVOf_y_q9jvA.; HSID=AKnkPVxvjTLLt_SL3; SSID=AmjSk2-aSkQSemzSd; APISID=PtBxtbVm75YSfmgL/ACYLNoo0q7Ywv4NjR; SAPISID=rvr2zlpo8HnZhGbR/ArgZ_2y5h0lHugvDB; __Secure-3PAPISID=rvr2zlpo8HnZhGbR/ArgZ_2y5h0lHugvDB; SIDCC=AJi4QfHdfDpJaOhjazB_eeiEnmkGdZ_LHoKppKv7ZuamZuJCgxSbhSKj80EA8RN_t6hte-bzobE; __Secure-3PSIDCC=AJi4QfFNNuFRCRNqVdbm7SeFeZc4amWQiJtJZ0FBdJMzg_to6Iko4Cg_FdFi9LsQ67aX1AiXLss0; 1P_JAR=2021-09-12-02; SEARCH_SAMESITE=CgQIg5MB; CONSENT=YES+US.en+202005; __Secure-1PSID=BgikMBMlZBnF78SPJrtXTvtvyhEzsVlY0ezMrX2hKlUuuUaY8I5cU8s1Qp9JT6mcUwdyeg.; __Secure-1PAPISID=rvr2zlpo8HnZhGbR/ArgZ_2y5h0lHugvDB; OTZ=6152398_72_76_104100_72_446760; S=billing-ui-v3=051uZ6WjcsEOImFKZMXjVNZHPs40apU8:billing-ui-v3-efe=051uZ6WjcsEOImFKZMXjVNZHPs40apU8\r\n" +
            "Upgrade-Insecure-Requests: 1\r\n" +
            "Sec-Fetch-Dest: document\r\n" +
            "Sec-Fetch-Mode: navigate\r\n" +
            "Sec-Fetch-Site: cross-site\r\n" +
            "Content-Length: " + HTTP_BODY.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
            "\r\n" +
            HTTP_BODY;

    @Test
    public void test() throws IOException {
        BufferedReader reqReader = new BufferedReader(new StringReader(HTTP_REQUEST));

        HttpStatusLine headerLine = HttpStatusLine.from(reqReader);

        assertEquals("/", headerLine.path);
        assertEquals("POST", headerLine.verb);

        Headers headers = Headers.from(reqReader);

        assertEquals("www.example.com", headers.get("Host"));
        assertEquals("1", headers.get("Upgrade-Insecure-Requests"));

        //Doesn't work
        //TODO: fix BodyParser
//        String body = BodyParser.from(reqReader, headers);
//
//        assertEquals(HTTP_BODY, body);dummy
//
//        reqReader.close();
    }

}
