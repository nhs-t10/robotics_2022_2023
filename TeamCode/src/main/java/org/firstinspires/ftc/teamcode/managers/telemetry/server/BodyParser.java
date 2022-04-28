package org.firstinspires.ftc.teamcode.managers.telemetry.server;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

import java.io.BufferedReader;
import java.io.IOException;

public class BodyParser {

    public static String from(BufferedReader reader, Headers headers, boolean isClientRequest) throws IOException {
        boolean hasContentLength = headers.hasHeader("content-length");
        int contentLength = headers.getAsInt("content-length");

        if(hasContentLength) return readBodyInPlace(reader, contentLength);
        //if it's a client request and doesn't specify the content-length, it has no body.
        else if(isClientRequest) return "";
        //body time!
        StringBuilder reqBodyBuilder = new StringBuilder();
        while(true) {
            int nextChar = reader.read();
            if(nextChar < 0) break;
            reqBodyBuilder.append((char)nextChar);
        }

        return reqBodyBuilder.toString();
    }

    private static String readBodyInPlace(BufferedReader reader, int contentLength) throws IOException {
        char[] charArr = new char[contentLength];
        int readStart = 0;
        while(true) {
            int len = reader.read(charArr, readStart, contentLength - readStart);
            if(len < 0) break;
            readStart += len;
        }
        return new String(charArr);
    }
}
